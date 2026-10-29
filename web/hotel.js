// 🏨 숙소 주소 카드: 택시 기사님께 보여주는 큰 글씨 화면 + 지도 QR (정보는 이 기기에만 저장)
import { S, h, btn, top, vibe, one, lang } from './core.js';
import { STRINGS } from './i18n.js';

/* global qrcode */
const KEY = 'wg-hotels';
export function loadHotels() { try { const v = JSON.parse(localStorage.getItem(KEY) || '[]'); return Array.isArray(v) ? v.filter((x) => x && (x.name || x.addr)) : []; } catch (_) { return []; } }
function saveHotels(d) { try { localStorage.setItem(KEY, JSON.stringify(d)); } catch (_) { /* 무시 */ } }
export const mapLink = (addr) => `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(addr)}`;

function qrSvg(textStr) {
  if (typeof qrcode === 'undefined') return h('div', {}, '');
  qrcode.stringToBytes = qrcode.stringToBytesFuncs['UTF-8'];
  const q = qrcode(0, 'M'); q.addData(textStr); q.make();
  const box = h('div', { html: q.createSvgTag({ cellSize: 8, margin: 0, scalable: true }) });
  const svg = box.querySelector('svg'); svg.style.width = '100%'; svg.style.height = '100%'; svg.style.display = 'block';
  return box.firstChild;
}

/** 풀화면 주소 카드 (흰 배경). 저장된 게 없으면 입력 화면으로 */
export function hotelScreen(root, back, go) {
  const s = S(), list = loadHotels();
  if (!list.length) { hotelEdit(root, back, go, true); return; }
  let k = 0;
  const wrap = h('div', { class: 'qrfull hotelfull' });
  // 기사님이 영어만 읽을 수도 있으니, 영어가 안 보이면 영어 한 줄 더
  const showEn = lang.current !== 'en' && lang.second !== 'en';
  const draw = () => {
    const x = list[k];
    wrap.replaceChildren(...[
      list.length > 1 ? h('div', { class: 'qrtabs' }, list.map((y, i) => btn(y.name || `#${i + 1}`, () => { k = i; vibe.tap(); draw(); }, 'qrtab' + (i === k ? ' on' : ''), {}))) : null,
      h('div', { class: 'htake' }, s.hotelTakeMe),
      showEn ? h('div', { class: 'htake en' }, STRINGS.en.hotelTakeMe) : null,
      x.name ? h('div', { class: 'hname' }, x.name) : null,
      x.addr ? h('div', { class: 'haddr' }, x.addr) : null,
      x.note ? h('div', { class: 'hnote' }, x.note) : null,
      x.addr ? h('div', { class: 'qrbox hqr' }, qrSvg(mapLink(x.addr))) : null,
      x.addr ? h('div', { class: 'qrhint' }, s.hotelMap) : null,
      x.phone ? h('a', { class: 'hcall', href: `tel:${x.phone.replace(/[^\d+]/g, '')}` }, `${s.hotelCall}  ${x.phone}`) : null,
      h('div', { class: 'qrbar' }, btn(s.hotelEditBtn, () => go('hoteledit'), 'qrbtn2'), btn(s.qrClose, back, 'qrbtn'))].filter(Boolean));
    const tab = wrap.querySelectorAll('.qrtab')[k]; if (tab) tab.style.background = '#E65100';
  };
  root.append(wrap); draw();
}

/** 숙소 입력 (여러 개) */
export function hotelEdit(root, back, go, first = false) {
  const s = S(), data = loadHotels();
  if (!data.length) data.push({ name: '', addr: '', phone: '', note: '' });
  const box = h('div', { style: 'display:flex;flex-direction:column;gap:12px' });
  const fields = [['name', s.hotelName, 'input', 'text'], ['addr', s.hotelAddr, 'textarea', 'text'], ['phone', s.hotelPhone, 'input', 'tel'], ['note', s.hotelNote, 'input', 'text']];
  const render = () => {
    box.replaceChildren(...data.map((x, i) => h('div', { class: 'hrow' },
      ...fields.map(([f, ph, tag, im]) => {
        const el = h(tag, { placeholder: one(ph), inputmode: im, autocomplete: 'off', spellcheck: 'false', rows: tag === 'textarea' ? 3 : undefined });
        el.value = x[f] || ''; el.addEventListener('input', () => { x[f] = el.value; });
        return el;
      }),
      data.length > 1 || x.name || x.addr ? btn(s.hotelDelete, () => { data.splice(i, 1); if (!data.length) data.push({ name: '', addr: '', phone: '', note: '' }); vibe.tap(); render(); }, 'linkbtn hdel') : null)));
  };
  render();
  const save = () => {
    const out = data.map((x) => ({ name: (x.name || '').trim(), addr: (x.addr || '').trim(), phone: (x.phone || '').trim(), note: (x.note || '').trim() })).filter((x) => x.name || x.addr);
    saveHotels(out); vibe.tap();
    if (first && out.length) { root.replaceChildren(); hotelScreen(root, back, go); } else back();
  };
  root.append(top(`🏨 ${s.hotelTitle}`, back), h('div', { class: 'scroll', style: 'gap:10px' },
    first ? h('div', { class: 'desc' }, s.hotelEmpty) : null,
    box,
    btn(s.hotelAdd, () => { data.push({ name: '', addr: '', phone: '', note: '' }); vibe.tap(); render(); }, 'btn alt'),
    h('div', { class: 'note', style: 'color:var(--sub);font-size:13px;line-height:1.5;padding:6px 4px' }, s.qrPrivacy),
    btn(s.qrSave, save)));
}
