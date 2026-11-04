// 🧾 여행 정산: 누가 얼마 냈는지 → 누가 누구한테 얼마 보내면 되는지 (이 기기에만 저장)
import { S, h, btn, top, vibe, resultOverlay, one } from './core.js';

const today = () => { const d = new Date(); return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`; };
export const ST_UNITS = [100, 500, 1000];

const KEY = 'wg-settle';
const blank = () => ({ people: ['P1', 'P2', 'P3'], ex: [] });
export function loadSettle() {
  try { const v = JSON.parse(localStorage.getItem(KEY) || 'null'); if (v && Array.isArray(v.people) && Array.isArray(v.ex) && v.people.length >= 2) return v; } catch (_) { /* 무시 */ }
  return blank();
}
function save(d) { try { localStorage.setItem(KEY, JSON.stringify(d)); } catch (_) { /* 무시 */ } }
export const fmt = (n) => (Math.round(n * 100) / 100).toLocaleString(undefined, { maximumFractionDigits: 2 });

/** 보낼 돈 계산: [{from, to, amt}] (가장 적은 횟수에 가깝게, 큰 빚부터 맞춤) */
export function settle(d) {
  const n = d.people.length, bal = Array(n).fill(0);
  for (const e of d.ex) {
    const fr = e.for.filter((i) => i < n); if (!fr.length || !(e.amt > 0)) continue;
    bal[e.by] += e.amt; for (const i of fr) bal[i] -= e.amt / fr.length;
  }
  const deb = [], cre = [];
  bal.forEach((b, i) => { if (b < -0.005) deb.push([i, -b]); else if (b > 0.005) cre.push([i, b]); });
  deb.sort((a, b) => b[1] - a[1]); cre.sort((a, b) => b[1] - a[1]);
  const out = []; let i = 0, j = 0;
  while (i < deb.length && j < cre.length) {
    const m = Math.min(deb[i][1], cre[j][1]);
    if (m > 0.005) out.push({ from: deb[i][0], to: cre[j][0], amt: m });
    deb[i][1] -= m; cre[j][1] -= m;
    if (deb[i][1] < 0.005) i++; if (cre[j][1] < 0.005) j++;
  }
  return { out, bal };
}

// ---------- 📤 내보내기: 메모장 글 · 엑셀(csv) ----------
function shares(d) { const a = Array(d.people.length).fill(0), paid = Array(d.people.length).fill(0); for (const e of d.ex) { if (e.mid) continue; paid[e.by] += e.amt; for (const i of e.for) a[i] += e.amt / e.for.length; } return { share: a, paid }; }
export function settleText(d, s) {
  const C = one(s.stCols).split('|'), { out } = settle(d), { share } = shares(d);
  const total = d.ex.filter((e) => !e.mid).reduce((a, e) => a + e.amt, 0);
  const who = (e) => (e.for.length === d.people.length ? '👥 ' + d.people.join(', ') : e.for.map((i) => d.people[i]).join(', '));
  const L = [`🧾 ${one(s.stTitle)} — ${today()}`, `👥 ${one(s.stPeople)}: ${d.people.join(', ')}`, '', `[${one(s.stList)}]`];
  d.ex.forEach((e, k) => L.push(e.mid ? `${k + 1}. ${e.at || ''} ${e.memo}: ${d.people[e.by]} → ${d.people[e.for[0]]} ${fmt(e.amt)}`.replace('  ', ' ')
    : `${k + 1}. ${e.at ? e.at + ' ' : ''}${e.memo || '-'}  ${fmt(e.amt)}  (${C[4]} ${d.people[e.by]} → ${who(e)})`));
  L.push('', `[${one(s.stTotal(fmt(total)))}]`, `${C[10]}: ${d.people.map((p, i) => `${p} ${fmt(share[i])}`).join(' · ')}`, '', `[${one(s.stResult)}]`);
  if (out.length) out.forEach((o) => L.push(`${d.people[o.from]} → ${d.people[o.to]}  ${fmt(o.amt)}`)); else L.push(one(s.stEven));
  L.push('', '— 🐺 Wolf Games');
  return L.join('\n');
}
export function settleCsv(d, s) {
  const C = one(s.stCols).split('|'), { out } = settle(d), { share, paid } = shares(d);
  const q = (v) => { const t = String(v); return /[",\n]/.test(t) ? `"${t.replace(/"/g, '""')}"` : t; };
  const num = (v) => String(Math.round(v * 100) / 100);
  const R = [[C[0], C[1], C[2], C[3], C[4], C[5]]];
  d.ex.forEach((e, k) => R.push([k + 1, e.at || '', e.memo || '', num(e.amt), d.people[e.by], e.for.map((i) => d.people[i]).join(' / ')]));
  R.push([], [C[8], C[9], C[10]]);
  d.people.forEach((p, i) => R.push([p, num(paid[i]), num(share[i])]));
  R.push([C[11], num(paid.reduce((a, b) => a + b, 0)), num(share.reduce((a, b) => a + b, 0))]);
  R.push([], [C[6], C[7], C[3]]);
  out.forEach((o) => R.push([d.people[o.from], d.people[o.to], num(o.amt)]));
  return '﻿' + R.map((r) => r.map(q).join(',')).join('\r\n');
}
function download(name, text, type) {
  const a = h('a', { href: URL.createObjectURL(new Blob([text], { type })), download: name });
  document.body.append(a); a.click(); setTimeout(() => { URL.revokeObjectURL(a.href); a.remove(); }, 1000);
}
export function exportSheet(root, d, s) {
  const note = h('div', { class: 'ds', style: 'color:#69F0AE;min-height:18px' });
  const name = `trip-split-${today()}`;
  const ov = h('div', { class: 'overlay' }, h('div', { class: 'dialog' },
    h('div', { class: 'dt' }, s.stExport), h('div', { class: 'ds' }, s.stExportDesc),
    btn(s.stCsv, () => { vibe.tap(); download(`${name}.csv`, settleCsv(d, s), 'text/csv;charset=utf-8'); note.textContent = one(s.stSaved); }),
    btn(s.stTxt, () => { vibe.tap(); download(`${name}.txt`, settleText(d, s), 'text/plain;charset=utf-8'); note.textContent = one(s.stSaved); }, 'btn alt'),
    navigator.share ? btn(s.stShare, () => { vibe.tap(); navigator.share({ title: one(s.stTitle), text: settleText(d, s) }).catch(() => {}); }, 'btn alt') : null,
    note, btn(s.cancel, () => ov.remove(), 'btn alt')));
  root.append(ov);
  return ov;
}
// ---------- 💰 중간 정산: 반올림해서 받고 남은 건 다음으로 ----------
export function roundTransfers(out, unit) { return out.map((o) => ({ ...o, amt: Math.round(o.amt / unit) * unit })).filter((o) => o.amt > 0); }
function midSheet(root, d, s, done) {
  let unit = 1000; try { const v = +localStorage.getItem('wg-st-unit'); if (ST_UNITS.includes(v)) unit = v; } catch (_) { /* 무시 */ }
  const list = h('div', { style: 'display:flex;flex-direction:column;gap:6px;width:100%' }), chips = h('div', { class: 'strow', style: 'justify-content:center' });
  let rows = [];
  const draw = () => {
    rows = roundTransfers(settle(d).out, unit);
    chips.replaceChildren(...ST_UNITS.map((u) => btn(fmt(u), () => { unit = u; try { localStorage.setItem('wg-st-unit', String(u)); } catch (_) { /* 무시 */ } vibe.tap(); draw(); }, 'chip' + (u === unit ? ' on' : ''))));
    list.replaceChildren(...(rows.length ? rows.map((o) => h('div', { class: 'stpay' }, h('b', {}, d.people[o.from]), ' → ', h('b', {}, d.people[o.to]), h('span', {}, fmt(o.amt)))) : [h('div', { class: 'stpay' }, s.stEven)]));
  };
  draw();
  const ov = h('div', { class: 'overlay' }, h('div', { class: 'dialog' },
    h('div', { class: 'dt' }, s.stMid), h('div', { class: 'ds' }, s.stMidDesc),
    h('div', { class: 'stl' }, s.stUnit), chips, list,
    btn(s.stMidDone, () => {
      if (!rows.length) { ov.remove(); return; }
      const k = d.ex.reduce((m, e) => Math.max(m, e.mid || 0), 0) + 1, at = today();
      for (const o of rows) d.ex.push({ amt: o.amt, by: o.from, for: [o.to], memo: one(s.stMidTag(k)), mid: k, at });
      vibe.strong(); ov.remove(); done();
    }),
    btn(s.cancel, () => ov.remove(), 'btn alt')));
  root.append(ov);
}

export function settleScreen(root, back) {
  const s = S();
  const d = loadSettle();
  let by = 0, fr = d.people.map((_, i) => i), draft = { amt: '', memo: '' };
  const box = h('div', { class: 'scroll', style: 'gap:12px' });
  const render = () => {
    save(d);
    if (by >= d.people.length) by = 0;
    fr = fr.filter((i) => i < d.people.length); if (!fr.length) fr = d.people.map((_, i) => i);
    const total = d.ex.filter((e) => !e.mid).reduce((a, e) => a + e.amt, 0), { share } = shares(d);
    const { out } = settle(d);
    // 사람
    const people = h('div', { class: 'strow' }, ...d.people.map((p, i) => {
      const inp = h('input', { value: p, maxlength: 12, class: 'stname' });
      inp.addEventListener('change', () => { d.people[i] = inp.value.trim() || `P${i + 1}`; setTimeout(render, 0); }); // 입력칸이 포커스를 잃는 중에 다시 그리면 오류 → 다음 틱에
      return h('div', { class: 'stp' }, inp, d.people.length > 2 ? btn('✕', () => {
        vibe.tap(); d.people.splice(i, 1);
        d.ex = d.ex.filter((e) => e.by !== i).map((e) => ({ ...e, by: e.by > i ? e.by - 1 : e.by, for: e.for.filter((k) => k !== i).map((k) => (k > i ? k - 1 : k)) })).filter((e) => e.for.length);
        render();
      }, 'stx') : null);
    }), d.people.length < 12 ? btn(s.stAddPerson, () => { vibe.tap(); d.people.push(`P${d.people.length + 1}`); fr.push(d.people.length - 1); render(); }, 'chip') : null);
    // 쓴 돈 입력
    const amt = h('input', { inputmode: 'decimal', placeholder: one(s.stAmount), class: 'stin' });
    const memo = h('input', { placeholder: one(s.stMemo), class: 'stin', maxlength: 30 });
    // 사람을 고르면 화면을 다시 그리니까, 적던 금액·메모는 따로 기억
    amt.value = draft.amt; memo.value = draft.memo;
    amt.addEventListener('input', () => { draft.amt = amt.value; }); memo.addEventListener('input', () => { draft.memo = memo.value; });
    const pick = (label, multi) => h('div', {}, h('div', { class: 'stl' }, label), h('div', { class: 'strow' }, ...d.people.map((p, i) => btn(p, () => {
      vibe.tap();
      if (multi) fr = fr.includes(i) ? (fr.length > 1 ? fr.filter((k) => k !== i) : fr) : [...fr, i].sort((a, b) => a - b); else by = i;
      render();
    }, 'chip' + ((multi ? fr.includes(i) : by === i) ? ' on' : '')))));
    const add = btn(s.stAdd, () => {
      const v = parseFloat(String(amt.value).replace(/,/g, ''));
      if (!(v > 0)) { amt.focus(); return; }
      vibe.tap(); d.ex.push({ amt: v, by, for: [...fr], memo: memo.value.trim(), at: today() }); draft = { amt: '', memo: '' }; render();
    });
    // 목록
    const list = d.ex.length ? d.ex.map((e, k) => h('div', { class: 'stex' + (e.mid ? ' mid' : '') },
      h('div', { class: 'stex-t' }, h('b', {}, fmt(e.amt)), ' ', e.memo || ''),
      h('div', { class: 'stex-s' }, `${d.people[e.by]} → ${e.for.length === d.people.length ? '👥' : e.for.map((i) => d.people[i]).join(', ')}`),
      btn('🗑️', () => { vibe.tap(); d.ex.splice(k, 1); render(); }, 'stx'))).reverse() : [h('div', { class: 'desc' }, s.stEmpty)];
    // 결과
    const res = h('div', { class: 'stres' },
      h('div', { class: 'stl' }, `${s.stResult} · ${s.stTotal(fmt(total))}`),
      ...(d.ex.length ? (out.length ? out.map((o) => h('div', { class: 'stpay' }, h('b', {}, d.people[o.from]), ' → ', h('b', {}, d.people[o.to]), h('span', {}, fmt(o.amt)))) : [h('div', { class: 'stpay' }, s.stEven)]) : []),
      d.ex.length ? h('div', { class: 'stshare' }, d.people.map((p, i) => `${p} ${s.stSpent(fmt(share[i]))}`).join('  ·  ')) : null,
      d.ex.length ? h('div', { class: 'row', style: 'margin-top:6px' }, out.length ? btn(s.stMid, () => { vibe.tap(); midSheet(root, d, s, render); }, 'btn alt accent-text') : null, btn(s.stExport, () => { vibe.tap(); exportSheet(root, d, s); }, 'btn alt accent-text')) : null);
    box.replaceChildren(...[
      h('div', { class: 'desc' }, s.stDesc),
      h('div', { class: 'stl' }, s.stPeople), people,
      h('div', { class: 'stcard' }, amt, memo, pick(s.stPaidBy, false), pick(s.stFor, true), add),
      res,
      h('div', { class: 'stl' }, s.stList), ...list,
      d.ex.length ? btn(s.stReset, () => resultOverlay(root, s.stResetAsk, s.stResetSub, s.stResetSave, () => exportSheet(root, d, s), s.stResetGo, () => { const b = blank(); d.people = b.people; d.ex = []; render(); }), 'btn alt') : null].filter(Boolean));
  };
  root.append(top(`🧾 ${s.stTitle}`, back), box);
  render();
}
