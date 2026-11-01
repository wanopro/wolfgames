// 🧾 여행 정산: 누가 얼마 냈는지 → 누가 누구한테 얼마 보내면 되는지 (이 기기에만 저장)
import { S, h, btn, top, vibe, resultOverlay, one } from './core.js';

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

export function settleScreen(root, back) {
  const s = S();
  const d = loadSettle();
  let by = 0, fr = d.people.map((_, i) => i), draft = { amt: '', memo: '' };
  const box = h('div', { class: 'scroll', style: 'gap:12px' });
  const render = () => {
    save(d);
    if (by >= d.people.length) by = 0;
    fr = fr.filter((i) => i < d.people.length); if (!fr.length) fr = d.people.map((_, i) => i);
    const total = d.ex.reduce((a, e) => a + e.amt, 0), share = Array(d.people.length).fill(0);
    for (const e of d.ex) for (const i of e.for) share[i] += e.amt / e.for.length;
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
      vibe.tap(); d.ex.push({ amt: v, by, for: [...fr], memo: memo.value.trim() }); draft = { amt: '', memo: '' }; render();
    });
    // 목록
    const list = d.ex.length ? d.ex.map((e, k) => h('div', { class: 'stex' },
      h('div', { class: 'stex-t' }, h('b', {}, fmt(e.amt)), ' ', e.memo || ''),
      h('div', { class: 'stex-s' }, `${d.people[e.by]} → ${e.for.length === d.people.length ? '👥' : e.for.map((i) => d.people[i]).join(', ')}`),
      btn('🗑️', () => { vibe.tap(); d.ex.splice(k, 1); render(); }, 'stx'))).reverse() : [h('div', { class: 'desc' }, s.stEmpty)];
    // 결과
    const res = h('div', { class: 'stres' },
      h('div', { class: 'stl' }, `${s.stResult} · ${s.stTotal(fmt(total))}`),
      ...(d.ex.length ? (out.length ? out.map((o) => h('div', { class: 'stpay' }, h('b', {}, d.people[o.from]), ' → ', h('b', {}, d.people[o.to]), h('span', {}, fmt(o.amt)))) : [h('div', { class: 'stpay' }, s.stEven)]) : []),
      d.ex.length ? h('div', { class: 'stshare' }, d.people.map((p, i) => `${p} ${s.stSpent(fmt(share[i]))}`).join('  ·  ')) : null);
    box.replaceChildren(...[
      h('div', { class: 'desc' }, s.stDesc),
      h('div', { class: 'stl' }, s.stPeople), people,
      h('div', { class: 'stcard' }, amt, memo, pick(s.stPaidBy, false), pick(s.stFor, true), add),
      res,
      h('div', { class: 'stl' }, s.stList), ...list,
      d.ex.length ? btn(s.stReset, () => resultOverlay(root, s.stResetAsk, null, s.ok, () => { const b = blank(); d.people = b.people; d.ex = []; render(); }, s.cancel, () => {}), 'btn alt') : null].filter(Boolean));
  };
  root.append(top(`🧾 ${s.stTitle}`, back), box);
  render();
}
