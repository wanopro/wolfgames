// 공통: 요소 생성, 진동, 화면 부품, 캔버스, 시간 기반 애니메이션
import { STRINGS } from './i18n.js';

export const VERSION = '1.6';

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
  /** 두 번째 언어 (없으면 null) */
  get second() { const v = store('wg-lang2'); return v && STRINGS[v] && v !== this.current ? v : null; },
  setSecond(v) { try { if (v) localStorage.setItem('wg-lang2', v); else localStorage.removeItem('wg-lang2'); } catch (_) { /* 무시 */ } },
};

// ---------- 두 언어 같이 보기 ----------
// 문구를 "메인 SEP 보조" 로 묶어서 넘기고, 화면에 그릴 때 둘로 나눔 (같은 글씨 크기, 보조만 살짝 흐리게)
export const SEP = '\u2063';
const LISTS = { kcNames: '|', kcDescs: '|', plinkoSlots: '|', colorNames: ',', animalNames: ',' };
const lead = (p, r) => { let i = 0; while (i < p.length && i < r.length && p[i] === r[i] && !/[\p{L}\p{N}]/u.test(p[i])) i++; return i; };
export const join2 = (p, r) => { if (p === r || r == null) return p; const i = lead(p, r); return p + SEP + r.slice(i).trimStart(); };
const part = (v, k) => (typeof v === 'string' && v.includes(SEP) ? v.split(SEP)[k] : v);
let dualCache = null;
export const S = () => {
  const a = STRINGS[lang.current], sec = lang.second;
  if (!sec) return a;
  if (dualCache && dualCache.k === lang.current + sec) return dualCache.v;
  const b = STRINGS[sec], v = {};
  for (const k of Object.keys(a)) {
    const x = a[k], y = b[k];
    if (typeof x === 'function') v[k] = (...q) => join2(x(...q.map((u) => part(u, 0))), y(...q.map((u) => part(u, 1))));
    else if (LISTS[k]) { const xs = x.split(LISTS[k]), ys = y.split(LISTS[k]); v[k] = xs.map((u, i) => join2(u, ys[i])).join(LISTS[k]); }
    else v[k] = typeof x === 'string' ? join2(x, y) : x;
  }
  dualCache = { k: lang.current + sec, v }; return v;
};
/** 두 언어 문구 여러 개를 이어 붙이기 (언어별로 따로 이어서 다시 묶음) */
export const cat = (...xs) => { xs = xs.map(String); if (!xs.some((x) => x.includes(SEP))) return xs.join(''); const pick = (k) => xs.map((x) => { const q = x.split(SEP); return q.length > 1 ? q[k] : q[0]; }).join(''); return pick(0) + SEP + pick(1); };
/** 한 줄만 필요한 곳 (예: 입력칸 안내) */
export const one = (str) => String(str).split(SEP).join(' / ');
// 캔버스: 두 언어 문구는 두 줄로 (같은 크기)
{
  const P = CanvasRenderingContext2D.prototype, F = P.fillText, ST = P.strokeText, M = P.measureText;
  // 두 줄이 원래 한 줄 자리 정도에 들어가도록 같이 줄임 (너무 작아지지 않게 11px 까지만)
  const two = (ctx, str, x, y, fn) => {
    const [m, sub] = str.split(SEP), f0 = ctx.font, px = parseFloat((/(\d+(?:\.\d+)?)px/.exec(f0) || [0, 16])[1]);
    const lp = Math.max(px * 0.62, Math.min(px, 11)), dy = lp * 0.56;
    ctx.font = f0.replace(/(\d+(?:\.\d+)?)px/, `${lp}px`);
    const bl = ctx.textBaseline; ctx.textBaseline = 'middle';
    const yc = bl === 'middle' ? y : bl === 'alphabetic' || bl === 'bottom' ? y - px * 0.35 : y + px * 0.5;
    fn.call(ctx, m, x, yc - dy); const al = ctx.globalAlpha; ctx.globalAlpha = al * 0.85; fn.call(ctx, sub, x, yc + dy); ctx.globalAlpha = al;
    ctx.textBaseline = bl; ctx.font = f0;
  };
  P.fillText = function (str, x, y, ...r) { if (typeof str === 'string' && str.includes(SEP)) return two(this, str, x, y, F); return F.call(this, str, x, y, ...r); };
  P.strokeText = function (str, x, y, ...r) { if (typeof str === 'string' && str.includes(SEP)) return two(this, str, x, y, ST); return ST.call(this, str, x, y, ...r); };
  P.measureText = function (str) {
    if (typeof str === 'string' && str.includes(SEP)) {
      const f0 = this.font, px = parseFloat((/(\d+(?:\.\d+)?)px/.exec(f0) || [0, 16])[1]), lp = Math.max(px * 0.62, Math.min(px, 11));
      this.font = f0.replace(/(\d+(?:\.\d+)?)px/, `${lp}px`); const ws = str.split(SEP).map((q) => M.call(this, q)); this.font = f0;
      return ws[0].width >= ws[1].width ? ws[0] : ws[1];
    }
    return M.call(this, str);
  };
}
// 글자(DOM): SEP 가 든 글자는 "메인 + 보조 한 줄" 로 바꿈 (textContent 로 넣은 것도 자동으로)
export function dualize(root) {
  const w = document.createTreeWalker(root, NodeFilter.SHOW_TEXT), list = [];
  for (let n = w.nextNode(); n; n = w.nextNode()) if (n.nodeValue.includes(SEP)) list.push(n);
  for (const n of list) {
    const [m, sub] = n.nodeValue.split(SEP);
    const span = document.createElement('span'); span.className = 'dual'; span.textContent = sub;
    n.nodeValue = m; n.after(span);
  }
}

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
/** 메뉴 · 화면 제목은 메인 언어만 (두 언어로 쓰면 너무 커짐) */
export const main1 = (str) => String(str).split(SEP)[0];
export function top(title, onBack, ...actions) {
  return h('div', { class: 'top' },
    btn('←', onBack, 'back'),
    h('div', { class: 'title' }, main1(title)),
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
  return btn([h('span', { class: 'emo' }, emo), h('div', {}, h('div', { class: 't' }, main1(title)), h('div', { class: 's' }, main1(sub))), h('span', { class: 'chev' }, '›')], onClick, 'card');
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
