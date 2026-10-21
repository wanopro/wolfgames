// 🧮 계산기 · 🍻 N빵 · 💱 환전
import { C, S, h, btn, top, stepper, vibe, lang } from './core.js';

// ---------- 숫자 표시 ----------
const fmt = (n, maxFrac = 8) => {
  if (!isFinite(n)) return '—';
  const r = Math.round(n * 1e8) / 1e8;
  return r.toLocaleString('en-US', { maximumFractionDigits: maxFrac });
};
const fmtInput = (str) => {
  if (str === '' || str === '-') return str;
  const [i, d] = str.split('.');
  const neg = i.startsWith('-');
  const digits = neg ? i.slice(1) : i;
  const withComma = digits.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  return (neg ? '-' : '') + withComma + (d !== undefined ? '.' + d : '');
};
const OPS = { '+': (a, b) => a + b, '−': (a, b) => a - b, '×': (a, b) => a * b, '÷': (a, b) => a / b };

/** 곱셈·나눗셈 먼저 계산 */
function evaluate(tokens) {
  const t = [...tokens];
  if (t.length && OPS[t[t.length - 1]]) t.pop();
  if (!t.length) return 0;
  const vals = [], ops = [];
  for (const x of t) { if (OPS[x]) ops.push(x); else vals.push(parseFloat(x)); }
  if (vals.some((v) => isNaN(v))) return NaN;
  const v2 = [vals[0]], o2 = [];
  ops.forEach((op, i) => {
    if (op === '×' || op === '÷') v2[v2.length - 1] = OPS[op](v2[v2.length - 1], vals[i + 1]);
    else { o2.push(op); v2.push(vals[i + 1]); }
  });
  return o2.reduce((acc, op, i) => OPS[op](acc, v2[i + 1]), v2[0]);
}

// 앱 안에서 화면을 오가도 계산 내용 유지
const state = { tokens: [], fresh: false, lastExpr: '' };
export const calcValue = () => { const v = evaluate(state.tokens); return isFinite(v) ? v : 0; };

export function calculator(root, back, go) {
  const s = S();
  const exprEl = h('div', { style: 'min-height:24px;color:var(--sub);font-size:18px;text-align:right;word-break:break-all' });
  const mainEl = h('div', { style: 'font-size:52px;font-weight:300;text-align:right;line-height:1.1;word-break:break-all' });
  const disp = h('div', { style: 'flex:1;display:flex;flex-direction:column;justify-content:flex-end;gap:6px;padding:8px 6px 14px;min-height:120px' }, exprEl, mainEl);

  const render = () => {
    const t = state.tokens;
    const show = (arr) => arr.map((x) => (OPS[x] ? ` ${x} ` : fmtInput(x))).join('');
    exprEl.textContent = state.fresh ? state.lastExpr : show(t);
    const last = t[t.length - 1];
    const v = evaluate(t);
    mainEl.textContent = !t.length ? '0' : state.fresh || t.length > 1 ? fmt(v) : fmtInput(last);
    mainEl.style.fontSize = mainEl.textContent.length > 12 ? '34px' : '52px';
  };
  const press = (k) => {
    const t = state.tokens;
    const last = t[t.length - 1];
    const isNum = last !== undefined && !OPS[last];
    if (/^\d+$/.test(k) || k === '.') {
      if (state.fresh) { t.length = 0; state.fresh = false; }
      if (t.length && !OPS[t[t.length - 1]]) {
        let cur = t[t.length - 1];
        if (k === '.' && cur.includes('.')) return;
        if (cur.replace(/[-.]/g, '').length >= 15) return;
        cur = cur === '0' && k !== '.' ? (k === '00' ? '0' : k) : cur + k;
        t[t.length - 1] = cur;
      } else t.push(k === '.' ? '0.' : k === '00' ? '0' : k);
    } else if (OPS[k]) {
      state.fresh = false;
      if (!t.length) t.push('0');
      if (OPS[t[t.length - 1]]) t[t.length - 1] = k; else t.push(k);
    } else if (k === '=') {
      if (!t.length) return;
      const v = evaluate(t);
      state.lastExpr = t.map((x) => (OPS[x] ? ` ${x} ` : fmtInput(x))).join('') + ' =';
      state.tokens = [isFinite(v) ? String(Math.round(v * 1e10) / 1e10) : '0'];
      state.fresh = true;
    } else if (k === 'C') { t.length = 0; state.fresh = false; }
    else if (k === '⌫') {
      if (state.fresh) { t.length = 0; state.fresh = false; }
      else if (isNum) { const c = last.slice(0, -1); if (c === '' || c === '-') t.pop(); else t[t.length - 1] = c; }
      else t.pop();
    } else if (k === '%') {
      if (isNum) t[t.length - 1] = String(parseFloat(last) / 100);
    }
    vibe.tap();
    render();
  };

  const keys = ['C', '⌫', '%', '÷', '7', '8', '9', '×', '4', '5', '6', '−', '1', '2', '3', '+', '00', '0', '.', '='];
  const pad = h('div', { style: 'display:grid;grid-template-columns:repeat(4,1fr);gap:10px' });
  for (const k of keys) {
    const isOp = OPS[k] || k === '=';
    const fn = k === 'C' || k === '⌫' || k === '%';
    const b = h('button', {
      style: `height:min(68px,9.5vh);border-radius:18px;font-size:26px;font-weight:${isOp ? 800 : 500};` +
        `background:${k === '=' ? C.accent : isOp ? C.surface2 : fn ? '#3A3A50' : C.surface};color:${k === '=' ? '#000' : isOp ? C.accent : C.text}`,
    }, k);
    b.addEventListener('click', () => press(k));
    pad.append(b);
  }
  const actions = h('div', { class: 'row', style: 'margin-bottom:10px' },
    btn(s.splitBtn, () => openSplit(root, calcValue()), 'btn alt accent-text'),
    btn(s.fxBtn, () => go('fx'), 'btn alt accent-text'));
  root.append(top(s.calcTitle, back), h('div', { class: 'body pad' }, disp, actions, pad));
  render();
}

// ---------- 🍻 N빵 ----------
const UNITS = [0, 10, 100, 1000];
let splitPrefs = { n: 4, unit: 100 };
try { Object.assign(splitPrefs, JSON.parse(localStorage.getItem('wg-split') || '{}')); } catch (_) { /* 무시 */ }

function openSplit(root, total) {
  const s = S();
  const ov = h('div', { class: 'overlay', style: 'align-items:flex-end;padding:0' });
  ov.addEventListener('click', (e) => { if (e.target === ov) ov.remove(); });
  const sheet = h('div', { style: 'width:100%;background:var(--surface);border-radius:24px 24px 0 0;padding:20px 18px calc(18px + env(safe-area-inset-bottom));display:flex;flex-direction:column;gap:12px' });
  ov.append(sheet);
  const save = () => { try { localStorage.setItem('wg-split', JSON.stringify(splitPrefs)); } catch (_) { /* 무시 */ } };
  const res = h('div', { style: 'text-align:center;padding:6px 0' });
  const unitRow = h('div', { style: 'display:flex;gap:8px' });
  const draw = () => {
    unitRow.replaceChildren(...UNITS.map((u) => {
      const b = h('button', { style: `flex:1;height:42px;border-radius:12px;font-weight:700;background:${splitPrefs.unit === u ? C.accent : C.surface2};color:${splitPrefs.unit === u ? '#000' : C.text}` }, u ? fmt(u) : s.splitNone);
      b.onclick = () => { vibe.tap(); splitPrefs.unit = u; save(); draw(); };
      return b;
    }));
    if (!(total > 0)) { res.innerHTML = `<div style="color:var(--sub);font-size:16px">${s.splitNeedTotal}</div>`; return; }
    const raw = total / splitPrefs.n;
    const per = splitPrefs.unit ? Math.ceil(raw / splitPrefs.unit - 1e-9) * splitPrefs.unit : raw;
    const collected = per * splitPrefs.n, extra = collected - total;
    res.innerHTML = `<div style="color:var(--sub);font-size:15px">${s.splitPer}</div>
      <div style="font-size:44px;font-weight:900;color:var(--accent)">${fmt(per, splitPrefs.unit ? 0 : 2)}</div>
      <div style="color:var(--sub);font-size:14px">${extra > 0.004 ? s.splitCollect(fmt(collected, 2), fmt(extra, 2)) : ''}</div>`;
  };
  sheet.append(
    h('div', { style: 'font-size:22px;font-weight:900;text-align:center' }, s.splitTitle),
    h('div', { style: 'display:flex;justify-content:space-between;background:var(--bg);border-radius:14px;padding:12px 16px;font-size:18px' },
      h('span', { style: 'color:var(--sub)' }, s.splitTotal), h('b', {}, fmt(total, 2))),
    stepper(s.splitPeople, splitPrefs.n, 2, 30, (v) => { splitPrefs.n = v; save(); draw(); }),
    h('div', { style: 'color:var(--sub);font-size:14px;margin-bottom:-4px' }, s.splitRound), unitRow,
    res,
    btn('OK', () => ov.remove()));
  root.append(ov);
  draw();
}

// ---------- 💱 환전 ----------
export const CURRENCIES = [
  ['KRW', '🇰🇷'], ['USD', '🇺🇸'], ['JPY', '🇯🇵'], ['CNY', '🇨🇳'], ['THB', '🇹🇭'], ['VND', '🇻🇳'], ['EUR', '🇪🇺'], ['GBP', '🇬🇧'],
  ['HKD', '🇭🇰'], ['TWD', '🇹🇼'], ['SGD', '🇸🇬'], ['PHP', '🇵🇭'], ['MYR', '🇲🇾'], ['IDR', '🇮🇩'], ['AUD', '🇦🇺'], ['CAD', '🇨🇦'],
  ['CHF', '🇨🇭'], ['NZD', '🇳🇿'], ['INR', '🇮🇳'], ['MNT', '🇲🇳'], ['LAK', '🇱🇦'], ['KHR', '🇰🇭'], ['RUB', '🇷🇺'], ['AED', '🇦🇪'],
];
const NO_DECIMALS = ['KRW', 'JPY', 'VND', 'IDR', 'MNT', 'LAK', 'KHR', 'TWD'];
const HOME_CUR = { ko: 'KRW', en: 'USD', ja: 'JPY', zh: 'CNY', th: 'THB', vi: 'VND' };
const RATE_URL = 'https://open.er-api.com/v6/latest/USD';
const CACHE = 'wg-fx';

const curName = (code) => {
  try { return new Intl.DisplayNames([lang.current], { type: 'currency' }).of(code); } catch (_) { return code; }
};
const flagOf = (code) => (CURRENCIES.find((c) => c[0] === code) || ['', '🏳️'])[1];

async function loadRates(force) {
  let cached = null;
  try { cached = JSON.parse(localStorage.getItem(CACHE) || 'null'); } catch (_) { /* 무시 */ }
  const fresh = cached && Date.now() / 1000 < (cached.next || 0) && Date.now() / 1000 - cached.fetched < 6 * 3600;
  if (cached && fresh && !force) return { data: cached, offline: false };
  try {
    const r = await fetch(RATE_URL, { cache: 'no-store' });
    const j = await r.json();
    if (j.result !== 'success') throw new Error('bad');
    const data = { rates: j.rates, time: j.time_last_update_unix, next: j.time_next_update_unix, fetched: Date.now() / 1000 };
    try { localStorage.setItem(CACHE, JSON.stringify(data)); } catch (_) { /* 무시 */ }
    return { data, offline: false };
  } catch (_) {
    if (cached) return { data: cached, offline: true };
    throw new Error('no rates');
  }
}

export function exchange(root, back) {
  const s = S();
  let sel = { from: HOME_CUR[lang.current] || 'USD', to: null };
  try { Object.assign(sel, JSON.parse(localStorage.getItem('wg-fx-sel') || '{}')); } catch (_) { /* 무시 */ }
  if (!sel.to || sel.to === sel.from) sel.to = sel.from === 'USD' ? 'KRW' : 'USD';
  let rates = null, status = s.fxLoading, statusCls = '';
  let amount = calcValue() > 0 ? String(Math.round(calcValue() * 100) / 100) : '1';
  const save = () => { try { localStorage.setItem('wg-fx-sel', JSON.stringify(sel)); } catch (_) { /* 무시 */ } };

  const body = h('div', { class: 'scroll' });
  root.append(top(s.fxTitle, back, btn('↻', () => refresh(true), 'tbtn accent')), body);
  const input = h('input', { type: 'text', inputmode: 'decimal', value: amount, style: 'width:100%;background:transparent;border:0;color:var(--text);font-size:34px;font-weight:700;text-align:right;outline:none' });
  input.addEventListener('input', () => { amount = input.value.replace(/[^\d.]/g, ''); calc(); });
  const resEl = h('div', { style: 'font-size:34px;font-weight:900;color:var(--accent);text-align:right;word-break:break-all' });
  const rateEl = h('div', { style: 'color:var(--sub);font-size:14px;text-align:center' });
  const statusEl = h('div', { style: 'font-size:13px;text-align:center' });
  const fromBtn = h('button', { class: 'chip', style: 'margin:0;font-size:16px;padding:8px 12px' });
  const toBtn = h('button', { class: 'chip', style: 'margin:0;font-size:16px;padding:8px 12px' });
  fromBtn.onclick = () => pick('from');
  toBtn.onclick = () => pick('to');
  const box = (label, chip, content) => h('div', { style: 'background:var(--surface);border-radius:18px;padding:14px 16px;display:flex;flex-direction:column;gap:8px' },
    h('div', { style: 'display:flex;justify-content:space-between;align-items:center;gap:8px' }, chip, h('span', { style: 'color:var(--sub);font-size:13px;text-align:right' }, label)), content);
  const fromLbl = h('span'), toLbl = h('span');
  const swap = btn('⇅', () => { [sel.from, sel.to] = [sel.to, sel.from]; save(); draw(); }, 'tbtn accent');
  swap.style.cssText = 'align-self:center;font-size:26px;padding:4px 18px;background:var(--surface2);border-radius:14px';

  const draw = () => {
    fromBtn.textContent = `${flagOf(sel.from)} ${sel.from} ▾`;
    toBtn.textContent = `${flagOf(sel.to)} ${sel.to} ▾`;
    fromLbl.textContent = curName(sel.from); toLbl.textContent = curName(sel.to);
    calc();
  };
  const calc = () => {
    statusEl.textContent = status; statusEl.className = statusCls;
    if (!rates) { resEl.textContent = '—'; rateEl.textContent = ''; return; }
    const r = rates.rates[sel.to] / rates.rates[sel.from];
    const a = parseFloat(amount) || 0;
    resEl.textContent = fmt(a * r, NO_DECIMALS.includes(sel.to) ? 0 : 2);
    rateEl.textContent = `1 ${sel.from} = ${fmt(r, r < 1 ? 6 : 4)} ${sel.to}`;
  };
  const pick = (which) => {
    vibe.tap();
    const ov = h('div', { class: 'overlay' });
    ov.addEventListener('click', (e) => { if (e.target === ov) ov.remove(); });
    const list = h('div', { style: 'width:100%;max-width:380px;max-height:76vh;overflow-y:auto;background:var(--surface);border-radius:20px;padding:10px' },
      h('div', { style: 'font-weight:900;font-size:18px;padding:8px 10px' }, s.fxPick),
      ...CURRENCIES.filter(([c]) => !rates || rates.rates[c]).map(([c, f]) => {
        const b = h('button', { style: `display:flex;gap:12px;align-items:center;width:100%;padding:12px 10px;border-radius:12px;text-align:left;${sel[which] === c ? 'background:var(--surface2)' : ''}` },
          h('span', { style: 'font-size:24px' }, f), h('b', { style: 'width:44px' }, c), h('span', { style: 'color:var(--sub);font-size:14px' }, curName(c)));
        b.onclick = () => { vibe.tap(); const other = which === 'from' ? 'to' : 'from'; if (sel[other] === c) sel[other] = sel[which]; sel[which] = c; save(); ov.remove(); draw(); };
        return b;
      }));
    ov.append(list);
    root.append(ov);
  };
  const refresh = async (force) => {
    status = s.fxLoading; statusCls = ''; calc();
    try {
      const { data, offline } = await loadRates(force);
      rates = data;
      const when = new Date(data.time * 1000).toLocaleString(lang.current, { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
      status = offline ? s.fxOffline : s.fxUpdated(when);
      statusCls = offline ? 'acc' : '';
    } catch (_) { status = s.fxError; statusCls = 'win'; }
    draw();
  };

  body.append(
    box(fromLbl, fromBtn, input), swap, box(toLbl, toBtn, resEl), rateEl, statusEl,
    h('div', { style: 'color:var(--sub);font-size:12px;text-align:center;margin-top:6px' }, s.fxNote, h('br'),
      h('a', { href: 'https://www.exchangerate-api.com', target: '_blank', rel: 'noopener', style: 'color:var(--sub)' }, 'Rates By Exchange Rate API')));
  draw();
  refresh(false);
}
