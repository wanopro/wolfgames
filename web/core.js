// 공통: 요소 생성, 진동, 화면 부품, 캔버스, 시간 기반 애니메이션
import { STRINGS } from './i18n.js';

export const VERSION = '1.5.1';

export const C = {
  bg: '#12121A', surface: '#1E1E2A', surface2: '#2C2C3E', accent: '#FFB300',
  text: '#F2F2F7', sub: '#9A9AB0', win: '#FF3D5A',
  players: ['#FF5252', '#448AFF', '#69F0AE', '#FFD740', '#E040FB', '#18FFFF', '#FF6E40', '#B2FF59', '#FF80AB', '#8C9EFF'],
};

export const LANGS = [
  { code: 'ko', flag: '🇰🇷', label: '한국어' },
  { code: 'en', flag: '🇺🇸', label: 'English' },
  { code: 'ja', flag: '🇯🇵', label: '日本語' },
  { code: 'zh', flag: '🇨🇳', label: '简体中文' },
  { code: 'th', flag: '🇹🇭', label: 'ไทย' },
  { code: 'vi', flag: '🇻🇳', label: 'Tiếng Việt' },
];

// ---------- 언어 ----------
function store(k, v) {
  try { if (v === undefined) return localStorage.getItem(k); localStorage.setItem(k, v); } catch (_) { return null; }
}
export const lang = {
  pref: store('wg-lang') || 'system',
  get current() {
    if (this.pref !== 'system' && STRINGS[this.pref]) return this.pref;
    const sys = (navigator.language || 'en').slice(0, 2).toLowerCase();
    return STRINGS[sys] ? sys : 'en';
  },
  set(p) { this.pref = p; store('wg-lang', p); document.documentElement.lang = this.current; },
};
export const S = () => STRINGS[lang.current];

// ---------- 진동 (아이폰 사파리는 지원 안 함 → 조용히 무시) ----------
const canVibe = typeof navigator !== 'undefined' && 'vibrate' in navigator;
export const vibe = {
  tap: () => canVibe && navigator.vibrate(30),
  tick: () => canVibe && navigator.vibrate(60),
  strong: () => canVibe && navigator.vibrate(220),
};

// ---------- 요소 생성 ----------
export function h(tag, props = {}, ...kids) {
  const el = document.createElement(tag);
  for (const [k, v] of Object.entries(props || {})) {
    if (v == null || v === false) continue;
    if (k === 'class') el.className = v;
    else if (k === 'style') el.style.cssText = v;
    else if (k === 'html') el.innerHTML = v;
    else if (k.startsWith('on')) el.addEventListener(k.slice(2), v);
    else if (v === true) el.setAttribute(k, '');
    else el.setAttribute(k, v);
  }
  for (const c of kids.flat()) if (c != null && c !== false) el.append(c.nodeType ? c : document.createTextNode(String(c)));
  return el;
}

/** 누르면 진동이 오는 버튼 */
export function btn(text, onClick, cls = 'btn', opts = {}) {
  const b = h('button', { class: cls, disabled: opts.disabled }, text);
  b.addEventListener('click', () => { if (b.disabled) return; vibe.tap(); onClick(); });
  return b;
}

/** 상단 뒤로가기 + 제목 */
export function top(title, onBack, ...actions) {
  return h('div', { class: 'top' },
    btn('←', onBack, 'back'),
    h('div', { class: 'title' }, title),
    ...actions);
}

export function stepper(label, value, min, max, onChange) {
  const val = h('span', { class: 'val' }, value);
  const minus = h('button', {}, '−');
  const plus = h('button', {}, '+');
  const sync = () => { val.textContent = value; minus.disabled = value <= min; plus.disabled = value >= max; };
  minus.onclick = () => { if (value > min) { vibe.tap(); value--; sync(); onChange(value); } };
  plus.onclick = () => { if (value < max) { vibe.tap(); value++; sync(); onChange(value); } };
  sync();
  const el = h('div', { class: 'stepper' }, h('span', { class: 'lbl' }, label), minus, val, plus);
  el.setMax = (m) => { max = m; if (value > max) { value = max; onChange(value); } sync(); };
  return el;
}

/** 켜기/끄기 스위치 줄 */
export function toggle(label, value, onChange) {
  const sw = h('span', { class: 'sw' + (value ? ' on' : '') }, h('span', { class: 'knob' }));
  const el = h('button', { class: 'stepper toggle' }, h('span', { class: 'lbl' }, label), sw);
  el.addEventListener('click', () => { vibe.tap(); value = !value; sw.classList.toggle('on', value); onChange(value); });
  return el;
}

export function card(emo, title, sub, onClick) {
  return btn([h('span', { class: 'emo' }, emo), h('div', {}, h('div', { class: 't' }, title), h('div', { class: 's' }, sub)), h('span', { class: 'chev' }, '›')], onClick, 'card');
}

/** 시작 전 설정 화면 */
export function setupPanel(desc, onStart, ...content) {
  return h('div', { class: 'scroll' },
    h('div', { class: 'desc' }, desc),
    ...content,
    h('div', { style: 'height:8px' }),
    btn(S().start, onStart));
}

/** 결과 팝업 */
export function resultOverlay(parent, title, sub, primary, onPrimary, secondary, onSecondary) {
  const ov = h('div', { class: 'overlay' },
    h('div', { class: 'dialog' },
      h('div', { class: 'dt' }, title),
      sub ? h('div', { class: 'ds' }, sub) : null,
      btn(primary, () => { ov.remove(); onPrimary(); }),
      secondary ? btn(secondary, () => { ov.remove(); onSecondary(); }, 'btn alt') : null));
  parent.append(ov);
  return ov;
}

// ---------- 캔버스 ----------
/**
 * 부모 크기에 맞는 고해상도 캔버스. draw(ctx, w, h)를 매 프레임 호출.
 * 반환: { canvas, stop() }
 */
export function liveCanvas(parent, draw) {
  const cv = h('canvas', { style: 'position:absolute;left:0;top:0;width:100%;height:100%' });
  parent.append(cv);
  const ctx = cv.getContext('2d');
  let w = 0, hh = 0, raf = 0, alive = true;
  const fit = () => {
    const r = cv.getBoundingClientRect();
    const dpr = Math.min(window.devicePixelRatio || 1, 3);
    w = r.width; hh = r.height;
    cv.width = Math.max(1, Math.round(w * dpr));
    cv.height = Math.max(1, Math.round(hh * dpr));
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
  };
  const ro = new ResizeObserver(fit);
  ro.observe(cv);
  const loop = () => {
    if (!alive) return;
    if (!w) fit();
    ctx.clearRect(0, 0, w, hh);
    draw(ctx, w, hh);
    raf = requestAnimationFrame(loop);
  };
  raf = requestAnimationFrame(loop);
  return {
    canvas: cv,
    size: () => ({ w, h: hh }),
    stop() { alive = false; cancelAnimationFrame(raf); ro.disconnect(); },
  };
}

// ---------- 시간 기반 애니메이션 ----------
/** ms 동안 진행률 0→1 을 콜백. ease: 'out'(감속) | 'linear'. Promise 반환 */
export function animate(ms, onP, ease = 'out') {
  return new Promise((res) => {
    const t0 = performance.now();
    const step = (now) => {
      const u = Math.min(1, (now - t0) / ms);
      onP(ease === 'out' ? 1 - Math.pow(1 - u, 3) : u);
      if (u < 1) requestAnimationFrame(step); else res();
    };
    requestAnimationFrame(step);
  });
}

export const sleep = (ms) => new Promise((r) => setTimeout(r, ms));
export const rnd = (a, b) => a + Math.floor(Math.random() * (b - a + 1));
export const shuffle = (arr) => { const a = [...arr]; for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]]; } return a; };

// ---------- 그리기: 주사위 ----------
const PIPS = {
  1: [[0, 0]], 2: [[-1, -1], [1, 1]], 3: [[-1, -1], [0, 0], [1, 1]],
  4: [[-1, -1], [1, -1], [-1, 1], [1, 1]], 5: [[-1, -1], [1, -1], [0, 0], [-1, 1], [1, 1]],
  6: [[-1, -1], [1, -1], [-1, 0], [1, 0], [-1, 1], [1, 1]],
};
export function drawDie(ctx, x, y, size, value, border = null, alpha = 1) {
  ctx.save();
  ctx.globalAlpha = alpha;
  const hs = size / 2, r = size * 0.18;
  ctx.beginPath(); ctx.roundRect(x - hs, y - hs, size, size, r);
  ctx.fillStyle = '#fff'; ctx.fill();
  if (border) { ctx.lineWidth = size * 0.08; ctx.strokeStyle = border; ctx.beginPath(); ctx.roundRect(x - hs + size * 0.04, y - hs + size * 0.04, size * 0.92, size * 0.92, r * 0.9); ctx.stroke(); }
  const o = size * 0.26;
  for (const [dx, dy] of PIPS[value] || []) {
    ctx.beginPath();
    ctx.arc(x + dx * o, y + dy * o, value === 1 ? size * 0.135 : size * 0.09, 0, Math.PI * 2);
    ctx.fillStyle = value === 1 ? '#E53935' : '#1A1A1A';
    ctx.fill();
  }
  ctx.restore();
}

/** 주사위 하나를 작은 SVG 요소로 */
export function dieSvg(value, size, border = null, alpha = 1) {
  let g = `<rect x="3" y="3" width="94" height="94" rx="17" fill="#fff"/>`;
  if (border) g += `<rect x="6.8" y="6.8" width="86.4" height="86.4" rx="15" fill="none" stroke="${border}" stroke-width="7.5"/>`;
  for (const [dx, dy] of PIPS[value] || []) {
    g += `<circle cx="${50 + dx * 24.4}" cy="${50 + dy * 24.4}" r="${value === 1 ? 12.7 : 8.5}" fill="${value === 1 ? '#E53935' : '#1A1A1A'}"/>`;
  }
  const s = h('span', { style: `display:inline-block;width:${size}px;height:${size}px;opacity:${alpha};flex:none`, html: `<svg viewBox="0 0 100 100" width="${size}" height="${size}">${g}</svg>` });
  return s;
}

// ---------- 그리기: 트럼프 카드 ----------
export const RANKS = ['', 'A', '2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K'];
export const SUITS = ['♠', '♥', '♦', '♣'];
export const isRed = (c) => c.s === 1 || c.s === 2;
/** 52장 덱 (r: 1~13, s: 0~3) 섞어서 */
export const newDeck = () => shuffle([...Array(52).keys()].map((i) => ({ r: (i % 13) + 1, s: Math.floor(i / 13) })));

/** 카드 한 장. card가 null이면 뒷면. 반환 요소의 .set(card)로 앞면을 뒤집어 보여줌 */
export function cardEl(c, w = 90) {
  const hh = Math.round(w * 1.4);
  const face = h('div', { class: 'pc-face' });
  const el = h('div', { class: 'pcard' + (c ? '' : ' down'), style: `width:${w}px;height:${hh}px;font-size:${w / 90 * 16}px` },
    h('div', { class: 'pc-in' }, face, h('div', { class: 'pc-back' }, h('span', {}, '🐺'))));
  const paint = (cc) => {
    face.style.color = isRed(cc) ? '#E53935' : '#1A1A1A';
    face.replaceChildren(
      h('div', { class: 'pc-c' }, h('b', {}, RANKS[cc.r]), h('span', {}, SUITS[cc.s])),
      h('div', { class: 'pc-m' }, SUITS[cc.s]),
      h('div', { class: 'pc-c r' }, h('b', {}, RANKS[cc.r]), h('span', {}, SUITS[cc.s])));
  };
  if (c) paint(c);
  el.set = (cc) => { if (cc) { paint(cc); el.classList.remove('down'); } else el.classList.add('down'); };
  return el;
}
