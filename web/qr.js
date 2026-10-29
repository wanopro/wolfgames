// 📇 연락처 QR: 내 SNS 링크를 풀화면 QR로 (정보는 이 기기에만 저장, QR은 기기 안에서 만듦)
import { S, h, btn, top, vibe, one } from './core.js';

/* global qrcode */
const KEY = 'wg-qr';
// [id, 이름, 색, 아이콘 글자, 입력 종류(phone|id|link), 링크 만들기]
export const SNS = [
  ['instagram', 'Instagram', 'linear-gradient(45deg,#F58529,#DD2A7B,#8134AF)', '◎', 'id', (v) => `https://instagram.com/${v.replace(/^@/, '')}`],
  ['whatsapp', 'WhatsApp', '#25D366', '✆', 'phone', (v) => `https://wa.me/${v.replace(/\D/g, '')}`],
  ['tiktok', 'TikTok', '#000', '♪', 'id', (v) => `https://www.tiktok.com/@${v.replace(/^@/, '')}`],
  ['line', 'LINE', '#06C755', 'L', 'id', (v) => `https://line.me/ti/p/~${v.replace(/^@/, '')}`],
  ['kakao', 'KakaoTalk', '#FEE500', 'K', 'link', (v) => v],
  ['telegram', 'Telegram', '#229ED9', '✈', 'id', (v) => `https://t.me/${v.replace(/^@/, '')}`],
  ['facebook', 'Facebook', '#1877F2', 'f', 'id', (v) => `https://facebook.com/${v.replace(/^@/, '')}`],
  ['zalo', 'Zalo', '#0068FF', 'Z', 'phone', (v) => `https://zalo.me/${v.replace(/\D/g, '')}`],
];
export const snsLink = (row, v) => { v = v.trim(); return /^https?:\/\//i.test(v) ? v : row[5](v); };
export function loadQr() { try { return JSON.parse(localStorage.getItem(KEY) || '{}'); } catch (_) { return {}; } }
function saveQr(d) { try { localStorage.setItem(KEY, JSON.stringify(d)); } catch (_) { /* 무시 */ } }
const active = (d) => SNS.filter((r) => d[r[0]] && d[r[0]].v && d[r[0]].on !== false);

function qrSvg(textStr) {
  if (typeof qrcode === 'undefined') return h('div', {}, '');
  qrcode.stringToBytes = qrcode.stringToBytesFuncs['UTF-8'];
  const q = qrcode(0, 'M'); q.addData(textStr); q.make();
  const box = h('div', { html: q.createSvgTag({ cellSize: 8, margin: 0, scalable: true }) });
  const svg = box.querySelector('svg'); svg.style.width = '100%'; svg.style.height = '100%'; svg.style.display = 'block';
  return box.firstChild;
}

/** 풀화면 QR (흰 배경). 아무것도 없으면 입력 화면으로 */
export function qrScreen(root, back, go) {
  const s = S(), d = loadQr(), list = active(d);
  if (!list.length) { qrEdit(root, back, go, true); return; }
  let k = 0;
  const wrap = h('div', { class: 'qrfull' });
  const draw = () => {
    const r = list[k], v = d[r[0]].v;
    wrap.replaceChildren(...[
      h('div', { class: 'qrtabs' }, list.map((x, i) => btn(x[1], () => { k = i; vibe.tap(); draw(); }, 'qrtab' + (i === k ? ' on' : ''), {}))),
      h('div', { class: 'qrbox', style: `border-color:${r[2].startsWith('linear') ? '#DD2A7B' : r[2] === '#000' ? '#FE2C55' : r[2]}` }, qrSvg(snsLink(r, v))),
      h('div', { class: 'qrwho' }, r[4] === 'link' ? r[1] : v),
      h('div', { class: 'qrhint' }, s.qrScan),
      list.length > 1 ? h('div', { class: 'qrdots' }, list.map((_, i) => h('i', { class: i === k ? 'on' : '' }))) : null,
      list.length > 1 ? h('div', { class: 'qrhint', style: 'margin-top:10px' }, s.qrSwipe) : null,
      h('div', { class: 'qrbar' }, btn(s.qrEdit, () => go('qredit'), 'qrbtn2'), btn(s.qrClose, back, 'qrbtn'))].filter(Boolean));
    const tab = wrap.querySelectorAll('.qrtab')[k]; if (tab) tab.style.background = r[2];
  };
  let sx = null;
  wrap.addEventListener('pointerdown', (e) => { sx = e.clientX; });
  wrap.addEventListener('pointerup', (e) => { if (sx === null) return; const dx = e.clientX - sx; sx = null; if (Math.abs(dx) > 50 && list.length > 1) { k = (k + (dx < 0 ? 1 : -1) + list.length) % list.length; vibe.tap(); draw(); } });
  root.append(wrap); draw();
}

/** 내 SNS 입력 */
export function qrEdit(root, back, go, first = false) {
  const s = S(), d = loadQr();
  const ph = { phone: s.qrPhone, id: s.qrId, link: s.qrLink };
  const rows = SNS.map((r) => {
    const cur = d[r[0]] || { v: '', on: true };
    const inp = h('input', { value: cur.v, placeholder: `${r[1]} · ${one(ph[r[4]])}`, inputmode: r[4] === 'phone' ? 'tel' : 'text', autocapitalize: 'off', autocomplete: 'off', spellcheck: 'false' });
    const tg = h('button', { class: 'qtog' + (cur.on !== false && cur.v ? ' on' : '') });
    tg.onclick = () => { vibe.tap(); tg.classList.toggle('on'); };
    inp.addEventListener('input', () => { if (inp.value.trim()) tg.classList.add('on'); });
    return { r, inp, tg, el: h('div', { class: 'qrow' }, h('div', { class: 'qic', style: `background:${r[2]};color:${r[0] === 'kakao' ? '#3C1E1E' : '#fff'}` }, r[3]), inp, tg) };
  });
  const save = () => {
    const out = {};
    for (const { r, inp, tg } of rows) if (inp.value.trim()) out[r[0]] = { v: inp.value.trim(), on: tg.classList.contains('on') };
    saveQr(out); vibe.tap();
    if (first && Object.keys(out).length) { root.replaceChildren(); qrScreen(root, back, go); } else back();
  };
  root.append(top(`📇 ${s.qrTitle}`, back), h('div', { class: 'scroll', style: 'gap:8px' },
    first ? h('div', { class: 'desc' }, s.qrEmpty) : null,
    ...rows.map((x) => x.el),
    h('div', { class: 'note', style: 'color:var(--sub);font-size:13px;line-height:1.5;padding:6px 4px' }, s.qrPrivacy),
    btn(s.qrSave, save)));
}
