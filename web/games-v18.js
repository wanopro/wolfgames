// v1.8 새 게임 (글 없이 하는 게임): 🏗️ 탑 쌓기 · 🔴 따라 하기 · 🍺 늑대컬링 (⚡ 순발력) · ✂️ 폭탄 선 자르기 (🍀 운빨)
import { S, h, top, setupPanel, liveCanvas, vibe, stepper } from './core.js';
import { choice } from './games-quick.js';
import { wolfFace } from './art.js';
import { dazedWolf } from './dazed.js';

const ease = (u) => 1 - Math.pow(1 - Math.min(1, Math.max(0, u)), 3);
const font = (c, size, w = 900) => { c.font = `${w} ${size}px -apple-system, "Noto Sans KR", "Noto Sans Thai", sans-serif`; c.textAlign = 'center'; c.textBaseline = 'middle'; };
const txt = (c, s, x, y, size, color, w = 900) => { font(c, size, w); c.fillStyle = color; c.fillText(s, x, y); };
const pill = (c, W, H, label) => { c.fillStyle = 'rgba(0,0,0,.55)'; c.beginPath(); c.roundRect(W / 2 - 110, H - 86, 220, 44, 22); c.fill(); txt(c, label, W / 2, H - 64, 17, '#fff'); };

// 🔊 짧은 소리 (WebAudio, 실패하면 조용히)
let ac = null;
function audio() { try { ac = ac || new (window.AudioContext || window.webkitAudioContext)(); if (ac.state === 'suspended') ac.resume(); } catch (_) { ac = null; } return ac; }
function tone(hz, dur, type = 'sine', vol = 0.22) {
  const a = audio(); if (!a) return;
  try {
    const o = a.createOscillator(), g = a.createGain(), t = a.currentTime;
    o.type = type; o.frequency.value = hz;
    g.gain.setValueAtTime(0.0001, t); g.gain.exponentialRampToValueAtTime(vol, t + 0.015); g.gain.exponentialRampToValueAtTime(0.0001, t + dur);
    o.connect(g); g.connect(a.destination); o.start(t); o.stop(t + dur + 0.05);
  } catch (_) { /* 무시 */ }
}

function stage(root, title, setup, draw, onDown) {
  const body = h('div', { class: 'body', style: 'touch-action:none' });
  root.replaceChildren(top(title, setup), body);
  const t0 = performance.now(), now = () => (performance.now() - t0) / 1000;
  const cv = liveCanvas(body, (c, W, H) => draw(c, W, H, now()));
  cv.canvas.addEventListener('pointerdown', (e) => {
    e.preventDefault(); const r = cv.canvas.getBoundingClientRect();
    onDown(e.clientX - r.left, e.clientY - r.top, now(), r.width, r.height);
  });
  return cv;
}

// ===================== 🏗️ 탑 쌓기 =====================
export const TS_COLS = ['#FF6B81', '#FFB74D', '#FFD54F', '#81C784', '#4DD0E1', '#64B5F6', '#9575CD', '#F06292'];
export function tower(root, back) {
  const s = S();
  let cv = null;
  const setup = () => { if (cv) { cv.stop(); cv = null; } root.replaceChildren(top(s.tsTitle, back), h('div', { class: 'body' }, setupPanel(s.tsDesc, play))); };
  const play = () => {
    const g = { blocks: null };
    const reset = (W, H) => {
      const bw = W * 0.62;
      Object.assign(g, { W, H, bh: Math.max(22, Math.min(36, H * 0.055)), blocks: [{ x: (W - bw) / 2, w: bw }], cur: { x: -bw * 0.5, w: bw, dir: 1 }, speed: W * 0.5, chips: [], fallT: 0, perfT: -9, passT: -9, landT: -9, cam: 0, last: null, tip: 1 });
    };
    const rowY = (i) => g.H - 70 - (i + 1) * g.bh + g.cam;
    const block = (c, x, y, w, bh, col) => {
      c.beginPath(); c.roundRect(x, y, w, bh - 2, 4); c.fillStyle = col; c.fill();
      c.fillStyle = 'rgba(255,255,255,.28)'; c.fillRect(x + 3, y + 2, Math.max(0, w - 6), bh * 0.2);
    };
    const colOf = (i) => (i === 0 ? '#546E7A' : TS_COLS[(i - 1) % TS_COLS.length]);
    cv = stage(root, s.tsTitle, setup, (c, W, H, t) => {
      if (!g.blocks || g.W !== W || g.H !== H) reset(W, H);
      const dt = g.last == null ? 0 : Math.min(0.05, t - g.last); g.last = t;
      const n = g.blocks.length, bh = g.bh;
      g.cam += (Math.max(0, (n + 1) * bh - H * 0.5) - g.cam) * Math.min(1, dt * 6);
      // 땅
      c.fillStyle = '#263238'; c.fillRect(0, H - 70 + g.cam, W, 400);
      if (!g.fallT) {
        const cu = g.cur; cu.x += cu.dir * g.speed * dt;
        if (cu.x < -cu.w * 0.5) { cu.x = -cu.w * 0.5; cu.dir = 1; }
        if (cu.x > W - cu.w * 0.5) { cu.x = W - cu.w * 0.5; cu.dir = -1; }
        g.blocks.forEach((b, i) => block(c, b.x, rowY(i), b.w, bh, colOf(i)));
        // 맨 위 늑대
        const tb = g.blocks[n - 1], hop = t - g.landT < 0.3 ? Math.sin((t - g.landT) / 0.3 * Math.PI) * bh * 0.5 : 0, r = bh * 0.8;
        c.save(); c.translate(tb.x + tb.w / 2, rowY(n - 1) - r * 0.62 - hop); wolfFace(c, r); c.restore();
        block(c, cu.x, rowY(n) - bh * 1.6, cu.w, bh, colOf(n));
      } else {
        const u = t - g.fallT;
        g.blocks.forEach((b, i) => {
          const k = i / Math.max(1, n - 1), d = Math.max(0, u - (n - 1 - i) * 0.04);
          c.save(); c.translate(b.x + b.w / 2 + g.tip * d * d * W * 0.7 * k, rowY(i) + bh / 2 + d * d * 900 * k * k); c.rotate(g.tip * d * 2.2 * k);
          block(c, -b.w / 2, -bh / 2, b.w, bh, colOf(i)); c.restore();
        });
        const tb = g.blocks[n - 1], r = bh * 0.8;
        c.save(); c.translate(tb.x + tb.w / 2 + g.tip * u * u * W * 0.8, rowY(n - 1) - r * 0.62 + u * u * 1100); c.rotate(g.tip * u * 5); wolfFace(c, r); c.restore();
        const cu = g.cur; block(c, cu.x, rowY(n) - bh * 1.6 + u * u * 1400, cu.w, bh, colOf(n));
      }
      // 잘려 떨어지는 조각
      g.chips = g.chips.filter((p) => t - p.t < 1.5);
      for (const p of g.chips) { const u = t - p.t; c.save(); c.globalAlpha = Math.max(0, 1 - u / 1.5); c.translate(p.x + p.w / 2, rowY(p.row) + bh / 2 + u * u * 900); c.rotate(p.dir * u * 3); block(c, -p.w / 2, -bh / 2, p.w, bh, p.col); c.restore(); }
      txt(c, s.tsFloor(n - 1), W / 2, 38, 30, '#fff');
      if (t - g.perfT < 0.8) { c.globalAlpha = 1 - (t - g.perfT) / 0.8; txt(c, '✨ PERFECT!', W / 2, 76 - (t - g.perfT) * 30, 22, '#FFD740'); c.globalAlpha = 1; }
      if (!g.fallT) {
        if (t - g.passT < 0.9) { const u = (t - g.passT) / 0.9; c.globalAlpha = 1 - u; txt(c, '👉', W / 2 + u * 60, H * 0.62, 54, '#fff'); c.globalAlpha = 1; }
        txt(c, s.tsTap, W / 2, H - 22, 14, '#9A9AB0', 700);
      } else {
        const u = t - g.fallT;
        if (u > 0.6) { c.fillStyle = 'rgba(0,0,0,.45)'; c.fillRect(0, H * 0.3, W, 130); txt(c, s.tsFall, W / 2, H * 0.3 + 42, 30, '#FFD740'); txt(c, s.shotWord, W / 2, H * 0.3 + 92, 26, '#FF5A6E'); }
        if (u > 1.4) pill(c, W, H, s.playAgain);
      }
    }, (x, y, t) => {
      if (!g.blocks) return;
      if (g.fallT) { if (t - g.fallT > 1.4) { reset(g.W, g.H); vibe.tap(); } return; }
      const n = g.blocks.length, tb = g.blocks[n - 1], cu = g.cur;
      const L = Math.max(cu.x, tb.x), R = Math.min(cu.x + cu.w, tb.x + tb.w);
      if (R - L <= 0) { g.fallT = t; g.tip = cu.x < tb.x ? -1 : 1; vibe.strong(); tone(110, 0.6, 'sawtooth', 0.18); return; }
      let nb;
      if (Math.abs(cu.x - tb.x) < Math.max(4, g.W * 0.012)) { nb = { x: tb.x, w: cu.w }; g.perfT = t; vibe.tick(); tone(880, 0.18); }
      else {
        const left = cu.x < tb.x;
        g.chips.push({ x: left ? cu.x : R, w: left ? tb.x - cu.x : cu.x + cu.w - R, row: n, t, col: colOf(n), dir: left ? -1 : 1 });
        nb = { x: L, w: R - L }; vibe.tap(); tone(440, 0.1, 'triangle');
      }
      g.blocks.push(nb); g.landT = t; g.passT = t;
      const m = g.blocks.length;
      g.cur = { x: m % 2 ? -nb.w * 0.5 : g.W - nb.w * 0.5, w: nb.w, dir: m % 2 ? 1 : -1 };
      g.speed = Math.min(g.W * 1.7, g.speed * 1.06);
    });
  };
  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== 🔴 따라 하기 (사이먼) =====================
export const SM_COL = [['#2ECC71', '#145A32'], ['#E74C3C', '#641E16'], ['#3498DB', '#154360'], ['#F1C40F', '#7D6608']];
export const SM_HZ = [330, 392, 494, 659];
// 색약도 알아보게 모양: ● ▲ ■ ★
export function smShape(c, k, x, y, r) {
  c.beginPath();
  if (k === 0) c.arc(x, y, r, 0, Math.PI * 2);
  else if (k === 1) { c.moveTo(x, y - r); c.lineTo(x + r * 0.95, y + r * 0.75); c.lineTo(x - r * 0.95, y + r * 0.75); c.closePath(); }
  else if (k === 2) c.rect(x - r * 0.82, y - r * 0.82, r * 1.64, r * 1.64);
  else { for (let i = 0; i < 10; i++) { const a = -Math.PI / 2 + i * Math.PI / 5, rr = i % 2 ? r * 0.45 : r * 1.05; i ? c.lineTo(x + Math.cos(a) * rr, y + Math.sin(a) * rr) : c.moveTo(x + Math.cos(a) * rr, y + Math.sin(a) * rr); } c.closePath(); }
  c.fill();
}
// 🔴 따라 하기: 액세서리 늑대 4마리 — 0 😎 선글라스(초록) · 1 🎀 리본(빨강) · 2 🧢 모자(파랑) · 3 👑 왕관(노랑)
const SW_FUR = '#9AA3AF', SW_FUR_D = '#7C8593', SW_CREAM = '#F1EEE8', SW_INK = '#2B2B33', SW_PINK = '#F6AFC0';
// 늑대 얼굴 (원점 = 얼굴 가운데, r = 크기). open: 입 벌림 0~1 (아우~)
export function smWolfHead(c, r, open = 0) {
  for (const s of [-1, 1]) {
    c.beginPath(); c.moveTo(s * r * 0.2, -r * 0.42); c.lineTo(s * r * 0.62, -r * 0.92); c.lineTo(s * r * 0.72, -r * 0.22); c.closePath(); c.fillStyle = SW_FUR_D; c.fill();
    c.beginPath(); c.moveTo(s * r * 0.34, -r * 0.42); c.lineTo(s * r * 0.58, -r * 0.74); c.lineTo(s * r * 0.62, -r * 0.32); c.closePath(); c.fillStyle = SW_PINK; c.fill();
  }
  c.beginPath(); c.ellipse(0, r * 0.02, r * 0.66, r * 0.58, 0, 0, 7); c.fillStyle = SW_FUR; c.fill();
  for (const s of [-1, 1]) { c.beginPath(); c.moveTo(s * r * 0.62, r * 0.05); c.lineTo(s * r * 0.8, r * 0.18); c.lineTo(s * r * 0.6, r * 0.28); c.closePath(); c.fillStyle = SW_FUR; c.fill(); }
  c.beginPath(); c.ellipse(0, r * 0.24, r * 0.34, r * 0.26, 0, 0, 7); c.fillStyle = SW_CREAM; c.fill();
  for (const s of [-1, 1]) {
    c.beginPath(); c.ellipse(s * r * 0.26, -r * 0.08, r * 0.11, r * 0.13 * (1 - open * 0.5), 0, 0, 7); c.fillStyle = SW_INK; c.fill();
    c.beginPath(); c.arc(s * r * 0.23, -r * 0.13, r * 0.045, 0, 7); c.fillStyle = '#fff'; c.fill();
    c.beginPath(); c.ellipse(s * r * 0.44, r * 0.16, r * 0.1, r * 0.06, 0, 0, 7); c.fillStyle = 'rgba(255,130,150,.55)'; c.fill();
  }
  c.beginPath(); c.ellipse(0, r * 0.13, r * 0.09, r * 0.065, 0, 0, 7); c.fillStyle = SW_INK; c.fill();
  if (open > 0.05) { // 아우~ 입
    c.beginPath(); c.ellipse(0, r * 0.33, r * 0.11, r * 0.13 * open + r * 0.03, 0, 0, 7); c.fillStyle = '#5A1E2B'; c.fill();
    c.beginPath(); c.ellipse(0, r * 0.38 + r * 0.06 * open, r * 0.06, r * 0.04, 0, 0, 7); c.fillStyle = '#F48FB1'; c.fill();
  } else {
    c.beginPath(); c.moveTo(-r * 0.12, r * 0.28); c.quadraticCurveTo(-r * 0.06, r * 0.36, 0, r * 0.28); c.quadraticCurveTo(r * 0.06, r * 0.36, r * 0.12, r * 0.28); c.strokeStyle = SW_INK; c.lineWidth = r * 0.04; c.lineCap = 'round'; c.stroke();
    c.beginPath(); c.ellipse(0, r * 0.36, r * 0.05, r * 0.06, 0, 0, Math.PI); c.fillStyle = '#F48FB1'; c.fill();
  }
}
// 액세서리 (얼굴 위에)
export function smAccessory(c, k, r, t = 0) {
  if (k === 0) { // 😎 선글라스
    c.fillStyle = '#111';
    for (const s of [-1, 1]) { c.beginPath(); c.roundRect(s * r * 0.26 - r * 0.2, -r * 0.2, r * 0.4, r * 0.26, [r * 0.06, r * 0.06, r * 0.14, r * 0.14]); c.fill(); }
    c.fillRect(-r * 0.08, -r * 0.16, r * 0.16, r * 0.05);
    c.beginPath(); c.moveTo(-r * 0.46, -r * 0.14); c.lineTo(-r * 0.64, -r * 0.2); c.moveTo(r * 0.46, -r * 0.14); c.lineTo(r * 0.64, -r * 0.2); c.strokeStyle = '#111'; c.lineWidth = r * 0.05; c.stroke();
    for (const s of [-1, 1]) { c.beginPath(); c.moveTo(s * r * 0.26 - r * 0.12, -r * 0.14); c.lineTo(s * r * 0.26 - r * 0.02, -r * 0.14); c.strokeStyle = 'rgba(255,255,255,.7)'; c.lineWidth = r * 0.04; c.stroke(); }
  } else if (k === 1) { // 🎀 리본 (머리 왼쪽 위)
    c.save(); c.translate(-r * 0.38, -r * 0.62); c.rotate(-0.35);
    c.fillStyle = '#FF80AB';
    for (const s of [-1, 1]) { c.beginPath(); c.moveTo(0, 0); c.quadraticCurveTo(s * r * 0.32, -r * 0.28, s * r * 0.36, 0); c.quadraticCurveTo(s * r * 0.32, r * 0.26, 0, 0); c.fill(); }
    for (const s of [-1, 1]) { c.beginPath(); c.moveTo(0, 0); c.lineTo(s * r * 0.14, r * 0.3); c.lineTo(s * r * 0.04, r * 0.26); c.closePath(); c.fill(); }
    c.beginPath(); c.arc(0, 0, r * 0.08, 0, 7); c.fillStyle = '#D81B60'; c.fill();
    c.beginPath(); c.ellipse(-r * 0.18, -r * 0.08, r * 0.06, r * 0.03, -0.5, 0, 7); c.fillStyle = 'rgba(255,255,255,.45)'; c.fill();
    c.restore();
  } else if (k === 2) { // 🧢 야구 모자 (챙은 오른쪽)
    c.beginPath(); c.ellipse(0, -r * 0.36, r * 0.6, r * 0.42, 0, Math.PI, 0); c.closePath(); c.fillStyle = '#0D3B8C'; c.fill();
    c.beginPath(); c.ellipse(r * 0.42, -r * 0.36, r * 0.42, r * 0.1, -0.05, 0, 7); c.fillStyle = '#082A66'; c.fill();
    c.beginPath(); c.arc(0, -r * 0.78, r * 0.06, 0, 7); c.fillStyle = '#fff'; c.fill();
    c.beginPath(); c.moveTo(0, -r * 0.76); c.lineTo(0, -r * 0.38); c.strokeStyle = 'rgba(255,255,255,.25)'; c.lineWidth = r * 0.03; c.stroke();
    c.beginPath(); c.ellipse(-r * 0.26, -r * 0.58, r * 0.12, r * 0.06, -0.6, 0, 7); c.fillStyle = 'rgba(255,255,255,.3)'; c.fill();
  } else { // 👑 왕관
    c.beginPath(); c.moveTo(-r * 0.4, -r * 0.48); c.lineTo(-r * 0.46, -r * 0.92); c.lineTo(-r * 0.22, -r * 0.7); c.lineTo(0, -r * 1.02); c.lineTo(r * 0.22, -r * 0.7); c.lineTo(r * 0.46, -r * 0.92); c.lineTo(r * 0.4, -r * 0.48); c.closePath();
    c.fillStyle = '#FFE082'; c.fill(); c.strokeStyle = '#A86E0C'; c.lineWidth = r * 0.03; c.stroke();
    c.fillStyle = '#C8901A'; c.fillRect(-r * 0.4, -r * 0.56, r * 0.8, r * 0.08);
    [[-0.46, -0.92, '#E53950'], [0, -1.02, '#4FC3F7'], [0.46, -0.92, '#69F0AE']].forEach(([x, y, col]) => { c.beginPath(); c.arc(x * r, y * r, r * 0.06, 0, 7); c.fillStyle = col; c.fill(); });
    const tw = 0.5 + 0.5 * Math.sin(t * 6); c.globalAlpha *= tw; c.fillStyle = '#fff'; c.beginPath(); c.arc(r * 0.18, -r * 0.84, r * 0.035, 0, 7); c.fill(); c.globalAlpha /= Math.max(0.01, tw);
  }
}

export function simon(root, back) {
  const s = S();
  let cv = null;
  const setup = () => { if (cv) { cv.stop(); cv = null; } root.replaceChildren(top(s.smTitle, back), h('div', { class: 'body' }, setupPanel(s.smDesc, play))); };
  const play = () => {
    audio();
    const g = {};
    const rnd = () => Math.floor(Math.random() * 4);
    const fresh = (t) => Object.assign(g, { seq: [rnd()], phase: 'show', ps: t + 0.8, inp: 0, tap: -1, tapT: -9, shown: -1, pt: t });
    fresh(0);
    const step = () => Math.max(0.3, 0.62 - g.seq.length * 0.025);
    const layout = (W, H) => { const S2 = Math.min(W - 32, H - 210), x0 = (W - S2) / 2, y0 = Math.max(100, (H - S2) / 2 + 10), gap = 12, p = (S2 - gap) / 2; return { x0, y0, p, gap }; };
    cv = stage(root, s.smTitle, setup, (c, W, H, t) => {
      const L = layout(W, H);
      let lit = -1, lu = 0;
      if (g.phase === 'show') {
        const st = step(), k = Math.floor((t - g.ps) / st);
        if (t >= g.ps) {
          if (k >= g.seq.length) { g.phase = 'input'; g.inp = 0; g.pt = t; }
          else if (t - g.ps - k * st < st * 0.68) { lit = g.seq[k]; lu = (t - g.ps - k * st) / (st * 0.68); if (g.shown !== k) { g.shown = k; tone(SM_HZ[lit], st * 0.68); } }
        }
      } else if (g.phase === 'ok') {
        if (t - g.pt > 1.1) { g.seq.push(rnd()); g.phase = 'show'; g.ps = t + 0.3; g.shown = -1; }
      }
      if (g.phase !== 'show' && t - g.tapT < 0.3) { lit = g.tap; lu = (t - g.tapT) / 0.3; }
      const wrongK = g.phase === 'wrong' ? g.seq[g.inp] : -1;
      for (let k = 0; k < 4; k++) {
        const x = L.x0 + (k % 2) * (L.p + L.gap), y = L.y0 + Math.floor(k / 2) * (L.p + L.gap);
        const on = k === lit || (k === wrongK && Math.sin(t * 14) > 0);
        c.save(); c.translate(x + L.p / 2, y + L.p / 2); const sc = on ? 1.04 : 1; c.scale(sc, sc);
        c.beginPath(); c.roundRect(-L.p / 2, -L.p / 2, L.p, L.p, L.p * 0.16); c.fillStyle = on ? SM_COL[k][0] : SM_COL[k][1]; c.fill();
        if (on) { c.beginPath(); c.roundRect(-L.p / 2, -L.p / 2, L.p, L.p, L.p * 0.16); c.fillStyle = 'rgba(255,255,255,.22)'; c.fill(); }
        c.fillStyle = on ? 'rgba(255,255,255,.95)' : 'rgba(255,255,255,.35)'; smShape(c, k, -L.p / 2 + L.p * 0.11, -L.p / 2 + L.p * 0.11, L.p * 0.055);
        // 액세서리 늑대: 켜지면 폴짝 + 아우~
        const bump = on && k === lit ? Math.sin(Math.min(1, lu) * Math.PI) : (on ? 0.6 : 0);
        c.save(); c.globalAlpha = on ? 1 : 0.55; c.translate(0, L.p * 0.06 - bump * L.p * 0.08);
        smWolfHead(c, L.p * 0.36, bump); smAccessory(c, k, L.p * 0.36, t); c.restore();
        if (on) { txt(c, '♪', L.p * 0.32, -L.p * 0.3 - lu * 14, 22, 'rgba(255,255,255,.9)'); for (let q = 0; q < 5; q++) { const a = q * 1.26 + t * 2; c.beginPath(); c.arc(Math.cos(a) * L.p * 0.42, Math.sin(a) * L.p * 0.42, 3, 0, 7); c.fillStyle = 'rgba(255,255,255,.7)'; c.fill(); } }
        c.restore();
      }
      txt(c, s.smLevel(g.seq.length), W / 2, 38, 30, '#fff');
      const msg = g.phase === 'show' ? s.smWatch : g.phase === 'input' ? s.smGo : g.phase === 'ok' ? s.smOk : s.smWrong;
      const my = L.y0 + L.p * 2 + L.gap + 34;
      if (g.phase !== 'wrong') txt(c, msg, W / 2, Math.min(H - 110, my), 22, g.phase === 'ok' ? '#FFD740' : '#fff');
      if (g.phase === 'input') { // 진행 점
        const n = g.seq.length, dw = Math.min(14, (W - 40) / n);
        for (let i = 0; i < n; i++) { c.beginPath(); c.arc(W / 2 + (i - (n - 1) / 2) * dw, 74, Math.min(4, dw * 0.35), 0, Math.PI * 2); c.fillStyle = i < g.inp ? '#FFD740' : 'rgba(255,255,255,.25)'; c.fill(); }
      }
      if (g.phase === 'wrong') { // ❌ 틀렸다! 가운데 크게
        const u = t - g.pt, k = 0.6 + 0.4 * ease(u / 0.25), sh = u < 0.4 ? Math.sin(u * 60) * 8 * (1 - u / 0.4) : 0;
        const cx = W / 2 + sh, cy = L.y0 + L.p + L.gap / 2, bw = Math.min(W - 40, 300), bh = 210;
        c.save(); c.translate(cx, cy); c.scale(k, k);
        c.beginPath(); c.roundRect(-bw / 2, -bh / 2, bw, bh, 26); c.fillStyle = 'rgba(12,12,18,.9)'; c.fill(); c.strokeStyle = '#FF5A6E'; c.lineWidth = 4; c.stroke();
        txt(c, '❌', 0, -bh / 2 + 52, 60, '#fff'); txt(c, s.smWrong.replace(/❌\s*/g, ''), 0, 18, 34, '#FF5A6E'); txt(c, s.shotWord, 0, 66, 30, '#FFD740');
        c.restore();
        if (u > 1.2) pill(c, W, H, s.playAgain);
      }
    }, (x, y, t, W, H) => {
      if (g.phase === 'wrong') { if (t - g.pt > 1.2) { fresh(t); vibe.tap(); } return; }
      if (g.phase !== 'input') return;
      const L = layout(W, H);
      const cx = Math.floor((x - L.x0) / (L.p + L.gap)), cy = Math.floor((y - L.y0) / (L.p + L.gap));
      if (cx < 0 || cx > 1 || cy < 0 || cy > 1) return;
      const k = cy * 2 + cx;
      g.tap = k; g.tapT = t;
      if (k !== g.seq[g.inp]) { g.phase = 'wrong'; g.pt = t; vibe.strong(); tone(140, 0.7, 'square', 0.15); return; }
      tone(SM_HZ[k], 0.22); vibe.tap(); g.inp++;
      if (g.inp >= g.seq.length) { g.phase = 'ok'; g.pt = t; }
    });
  };
  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== ✂️ 폭탄 선 자르기 =====================
export const WC_N = [4, 5, 6, 8];
export const WC_COLS = ['#E53935', '#1E88E5', '#FDD835', '#43A047', '#8E24AA', '#FB8C00', '#ECEFF1', '#8D6E63'];
export function wireCut(root, back) {
  const s = S();
  let cv = null, n = 6;
  try { const v = +localStorage.getItem('wg-wc-n'); if (WC_N.includes(v)) n = v; } catch (_) { /* 무시 */ }
  const setup = () => {
    if (cv) { cv.stop(); cv = null; }
    root.replaceChildren(top(s.wcTitle, back), h('div', { class: 'body' }, setupPanel(s.wcDesc, play,
      choice(s.wcWires, WC_N.map((v) => [v, String(v)]), n, (v) => { n = v; try { localStorage.setItem('wg-wc-n', String(v)); } catch (_) { /* 무시 */ } }))));
  };
  const play = () => {
    const g = {};
    const order = () => { const a = [...Array(WC_COLS.length).keys()]; for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]]; } return a.slice(0, n); };
    const round = () => Object.assign(g, { cols: order(), cut: Array(n).fill(0), bomb: Math.floor(Math.random() * n), boomT: 0, safeT: -9, cuts: 0, clock: 0, last: null, snip: null });
    round();
    const geo = (W, H) => { const bx = W * 0.1, bw = W * 0.8, by = Math.max(24, H * 0.07), bhh = Math.min(140, H * 0.2); const y1 = by + bhh, y2 = H * 0.8, span = bw * 0.92, left = bx + bw * 0.04; return { bx, bw, by, bhh, y1, y2, span, left, gapX: span / n }; };
    const wireX = (G, i) => G.left + (i + 0.5) * G.gapX;
    const wirePath = (c, G, i) => { const x = wireX(G, i), amp = G.gapX * 0.32 * (i % 2 ? 1 : -1), d = G.y2 - G.y1; c.beginPath(); c.moveTo(x, G.y1); c.bezierCurveTo(x + amp, G.y1 + d * 0.33, x - amp, G.y1 + d * 0.66, x, G.y2); };
    cv = stage(root, s.wcTitle, setup, (c, W, H, t) => {
      const G = geo(W, H), dt = g.last == null ? 0 : Math.min(0.05, t - g.last); g.last = t;
      if (!g.boomT) g.clock += dt * (1 + g.cuts * 0.8);
      const shake = g.boomT && t - g.boomT < 0.5 ? (1 - (t - g.boomT) / 0.5) * 10 : 0;
      c.save(); if (shake) c.translate((Math.random() - 0.5) * shake, (Math.random() - 0.5) * shake);
      // 아래 단자
      c.beginPath(); c.roundRect(G.bx, G.y2 - 6, G.bw, 26, 8); c.fillStyle = '#37474F'; c.fill();
      // 선
      const midY = (G.y1 + G.y2) / 2;
      for (let i = 0; i < n; i++) {
        const col = WC_COLS[g.cols[i]];
        const drawWire = () => { wirePath(c, G, i); c.strokeStyle = 'rgba(0,0,0,.35)'; c.lineWidth = 12; c.lineCap = 'round'; c.stroke(); wirePath(c, G, i); c.strokeStyle = col; c.lineWidth = 8; c.stroke(); };
        if (!g.cut[i]) { drawWire(); continue; }
        const u = t - g.cut[i], sag = ease(u / 0.4) * 14;
        c.save(); c.beginPath(); c.rect(0, 0, W, midY - 9); c.clip(); drawWire(); c.restore();
        c.save(); c.beginPath(); c.rect(0, midY + 9, W, H); c.clip(); c.translate(0, sag); drawWire(); c.restore();
        const x = wireX(G, i);
        c.fillStyle = '#FFB300'; c.beginPath(); c.arc(x, midY - 9, 4, 0, Math.PI * 2); c.fill(); c.beginPath(); c.arc(x, midY + 9 + sag, 4, 0, Math.PI * 2); c.fill();
        if (u < 0.35 && i !== g.bomb) for (let k = 0; k < 6; k++) { const a = k + t * 30; c.beginPath(); c.moveTo(x, midY); c.lineTo(x + Math.cos(a) * 16, midY + Math.sin(a) * 16); c.strokeStyle = '#FFF59D'; c.lineWidth = 2; c.stroke(); }
      }
      // 나사
      for (let i = 0; i < n; i++) { c.beginPath(); c.arc(wireX(G, i), G.y2 + 7, 5, 0, Math.PI * 2); c.fillStyle = '#90A4AE'; c.fill(); }
      // 폭탄 상자 (다이너마이트 + 타이머)
      for (let k = 0; k < 3; k++) { c.beginPath(); c.roundRect(G.bx + G.bw * (0.08 + k * 0.3), G.by - 10, G.bw * 0.24, G.bhh + 12, 10); c.fillStyle = '#C62828'; c.fill(); c.fillStyle = 'rgba(255,255,255,.18)'; c.fillRect(G.bx + G.bw * (0.1 + k * 0.3), G.by - 4, G.bw * 0.04, G.bhh); }
      c.beginPath(); c.roundRect(G.bx, G.by + G.bhh * 0.22, G.bw, G.bhh * 0.78, 12); c.fillStyle = '#263238'; c.fill();
      const tw = G.bw * 0.5, th = G.bhh * 0.42, tx = W / 2 - tw / 2, ty = G.by + G.bhh * 0.38;
      c.beginPath(); c.roundRect(tx, ty, tw, th, 8); c.fillStyle = '#0B0B0B'; c.fill();
      const sec = g.boomT ? 0 : 59 - (Math.floor(g.clock) % 60), blink = g.boomT || Math.sin(t * (6 + g.cuts * 4)) > -0.3;
      if (blink) { font(c, th * 0.72, 900); c.fillStyle = '#FF1744'; c.fillText(`00:${String(sec).padStart(2, '0')}`, W / 2, ty + th / 2 + 1); }
      c.restore();
      // ✂️ 자른 곳
      if (g.snip && t - g.snip[2] < 0.6) { const u = (t - g.snip[2]) / 0.6; c.globalAlpha = 1 - u; txt(c, '✂️', g.snip[0], g.snip[1] - u * 20, 40, '#fff'); c.globalAlpha = 1; }
      const left = g.cut.filter((v) => !v).length;
      if (!g.boomT) {
        if (t - g.safeT < 1.4) { const u = (t - g.safeT) / 1.4; c.globalAlpha = Math.min(1, (1 - u) * 3); txt(c, s.wcSafe, W / 2, G.y2 + 52, 19, '#69F0AE'); c.globalAlpha = 1; }
        else txt(c, s.wcLeft(left), W / 2, G.y2 + 52, 17, '#fff');
        txt(c, s.wcTap, W / 2, H - 22, 14, '#9A9AB0', 700);
        return;
      }
      const u = t - g.boomT;
      if (u < 0.5) { c.fillStyle = `rgba(255,240,200,${0.85 * (1 - u / 0.5)})`; c.fillRect(0, 0, W, H); }
      txt(c, '💥', W / 2, G.by + G.bhh / 2, Math.min(W, H) * 0.45 * (0.5 + 0.5 * ease(u / 0.25)), '#fff');
      if (u > 0.55) {
        const p = ease((u - 0.55) / 0.35), r = Math.min(W * 0.27, H * 0.2);
        c.fillStyle = `rgba(0,0,0,${0.6 * p})`; c.fillRect(0, 0, W, H);
        c.save(); c.globalAlpha = p; c.translate(W / 2, H * 0.45); c.scale(0.7 + 0.3 * p, 0.7 + 0.3 * p); dazedWolf(c, 0, 0, r, t); c.restore();
        c.globalAlpha = p; txt(c, s.wcBoom, W / 2, H * 0.45 + r * 1.35, 26, '#FFD740'); txt(c, s.shotWord, W / 2, H * 0.45 + r * 1.35 + 44, 26, '#FF5A6E'); c.globalAlpha = 1;
      }
      if (u > 1.3) pill(c, W, H, s.playAgain);
    }, (x, y, t, W, H) => {
      if (g.boomT) { if (t - g.boomT > 1.3) { round(); vibe.tap(); } return; }
      const G = geo(W, H);
      if (y < G.y1 - 10 || y > G.y2 + 30) return;
      let best = -1, bd = G.gapX * 0.5;
      for (let i = 0; i < n; i++) { if (g.cut[i]) continue; const d = Math.abs(x - wireX(G, i)); if (d < bd) { bd = d; best = i; } }
      if (best < 0) return;
      g.cut[best] = t; g.snip = [wireX(G, best), (G.y1 + G.y2) / 2, t];
      if (best === g.bomb) { g.boomT = t; vibe.strong(); tone(70, 0.9, 'sawtooth', 0.25); }
      else { g.cuts++; g.safeT = t; vibe.tap(); tone(1200, 0.06, 'square', 0.12); }
    });
  };
  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== 🍺 늑대컬링 =====================
// 🍺 늑대 맥주잔 (앞모습) — 원점 = 잔 바닥 가운데, s = 잔 너비 정도
// col = 목에 맨 두건 색 (사람 구분), num = 두건 매듭 숫자, slosh = 거품 출렁임(-1~1)
export function wolfMug(c, x, y, s, col = '#FF5A6E', num = '', slosh = 0) {
  c.save(); c.translate(x, y);
  // 그림자
  c.beginPath(); c.ellipse(0, s * 0.02, s * 0.55, s * 0.1, 0, 0, 7); c.fillStyle = 'rgba(0,0,0,.35)'; c.fill();
  // 손잡이 = 말린 꼬리
  c.beginPath(); c.moveTo(s * 0.38, -s * 0.82); c.bezierCurveTo(s * 0.82, -s * 0.85, s * 0.82, -s * 0.22, s * 0.38, -s * 0.28);
  c.strokeStyle = '#5E6773'; c.lineWidth = s * 0.15; c.lineCap = 'round'; c.stroke();
  c.beginPath(); c.moveTo(s * 0.38, -s * 0.82); c.bezierCurveTo(s * 0.82, -s * 0.85, s * 0.82, -s * 0.22, s * 0.38, -s * 0.28);
  c.strokeStyle = '#8E98A5'; c.lineWidth = s * 0.08; c.stroke();
  c.beginPath(); c.arc(s * 0.4, -s * 0.28, s * 0.09, 0, 7); c.fillStyle = '#F3F3F3'; c.fill();
  // 귀
  for (const k of [-1, 1]) {
    c.beginPath(); c.moveTo(k * s * 0.42, -s * 0.9); c.lineTo(k * s * 0.33, -s * 1.42); c.lineTo(k * s * 0.08, -s * 1.02); c.closePath(); c.fillStyle = '#6B7480'; c.fill();
    c.beginPath(); c.moveTo(k * s * 0.34, -s * 0.98); c.lineTo(k * s * 0.3, -s * 1.27); c.lineTo(k * s * 0.15, -s * 1.03); c.closePath(); c.fillStyle = '#FF9EB5'; c.fill();
  }
  // 몸통 (회색 도자기)
  const g = c.createLinearGradient(-s * 0.45, 0, s * 0.45, 0); g.addColorStop(0, '#7C8591'); g.addColorStop(0.35, '#B4BCC6'); g.addColorStop(1, '#6B7480');
  c.beginPath(); c.roundRect(-s * 0.44, -s * 1.0, s * 0.88, s, [s * 0.1, s * 0.1, s * 0.2, s * 0.2]); c.fillStyle = g; c.fill();
  // 볼털
  for (const k of [-1, 1]) { c.beginPath(); c.moveTo(k * s * 0.44, -s * 0.5); c.lineTo(k * s * 0.56, -s * 0.38); c.lineTo(k * s * 0.44, -s * 0.28); c.closePath(); c.fillStyle = '#9AA3AE'; c.fill(); }
  // 거품 (귀 사이로 넘침)
  const fy = -s * 1.0;
  c.fillStyle = '#FFFDF5';
  [[-0.3, 0.13], [-0.12, 0.16], [0.08, 0.15], [0.27, 0.13]].forEach(([fx, r], i) => { c.beginPath(); c.arc(fx * s + slosh * s * 0.05, fy - s * 0.03 + Math.sin(i * 2 + slosh * 3) * s * 0.02, r * s, 0, 7); c.fill(); });
  c.beginPath(); c.roundRect(-s * 0.44, fy - s * 0.02, s * 0.88, s * 0.1, s * 0.05); c.fill();
  // 흘러내린 거품 한 방울
  c.beginPath(); c.ellipse(-s * 0.22 + slosh * s * 0.04, fy + s * 0.13, s * 0.05, s * 0.09, 0, 0, 7); c.fill();
  // 얼굴
  c.beginPath(); c.ellipse(0, -s * 0.34, s * 0.25, s * 0.17, 0, 0, 7); c.fillStyle = '#E9ECEF'; c.fill(); // 주둥이
  for (const k of [-1, 1]) {
    c.beginPath(); c.ellipse(k * s * 0.17, -s * 0.62, s * 0.085, s * 0.1, 0, 0, 7); c.fillStyle = '#fff'; c.fill();
    c.beginPath(); c.arc(k * s * 0.16, -s * 0.6, s * 0.05, 0, 7); c.fillStyle = '#1B1B1F'; c.fill();
    c.beginPath(); c.arc(k * s * 0.14, -s * 0.63, s * 0.018, 0, 7); c.fillStyle = '#fff'; c.fill();
    c.beginPath(); c.moveTo(k * s * 0.08, -s * 0.76); c.lineTo(k * s * 0.26, -s * 0.8); c.strokeStyle = '#4A525C'; c.lineWidth = s * 0.03; c.stroke(); // 눈썹
  }
  c.beginPath(); c.ellipse(0, -s * 0.42, s * 0.08, s * 0.055, 0, 0, 7); c.fillStyle = '#1B1B1F'; c.fill(); // 코
  c.beginPath(); c.moveTo(-s * 0.1, -s * 0.3); c.quadraticCurveTo(-s * 0.05, -s * 0.24, 0, -s * 0.31); c.quadraticCurveTo(s * 0.05, -s * 0.24, s * 0.1, -s * 0.3); c.strokeStyle = '#1B1B1F'; c.lineWidth = s * 0.025; c.stroke();
  c.beginPath(); c.ellipse(s * 0.03, -s * 0.24, s * 0.04, s * 0.035, 0, 0, 7); c.fillStyle = '#FF7A95'; c.fill(); // 혀
  // 두건 (사람 색) + 숫자
  c.beginPath(); c.roundRect(-s * 0.44, -s * 0.17, s * 0.88, s * 0.11, s * 0.03); c.fillStyle = col; c.fill();
  c.beginPath(); c.moveTo(-s * 0.13, -s * 0.12); c.lineTo(s * 0.13, -s * 0.12); c.lineTo(0, s * 0.08); c.closePath(); c.fill();
  if (num !== '') { c.font = `900 ${s * 0.15}px -apple-system,sans-serif`; c.textAlign = 'center'; c.textBaseline = 'middle'; c.fillStyle = '#fff'; c.fillText(num, 0, -s * 0.06); }
  c.restore();
}

export const CU_COLS = ['#FF5A6E', '#4FC3F7', '#FFD54F', '#69F0AE', '#BA68C8', '#FF9800'];
export function curling(root, back) {
  const s = S();
  let cv = null, n = 4;
  try { const v = +localStorage.getItem('wg-cu-n'); if (v >= 2 && v <= 6) n = v; } catch (_) { /* 무시 */ }
  const setup = () => {
    if (cv) { cv.stop(); cv = null; }
    root.replaceChildren(top(s.cuTitle, back), h('div', { class: 'body' }, setupPanel(s.cuDesc, play,
      stepper(s.players, n, 2, 6, (v) => { n = v; try { localStorage.setItem('wg-cu-n', String(v)); } catch (_) { /* 무시 */ } }))));
  };
  const play = () => {
    const g = {};
    const PMAX = 90;
    const fresh = () => Object.assign(g, { turn: 0, res: Array(n).fill(null), phase: 'aim', pull: 0, downY: 0, t0: 0, y0: 0, D: 0, dur: 1, fallY: 0, doneT: 0 });
    fresh();
    const geo = (W, H) => {
      const X0 = W * 0.13, X1 = W * 0.87, EDGE = Math.max(92, H * 0.13), START = H - 150;
      return { X0, X1, EDGE, START, pxcm: (START - EDGE) / 100, base: Math.min(74, W * 0.19) };
    };
    const laneX = (G, i) => G.X0 + (G.X1 - G.X0) * (i + 0.5) / n;
    const size = (G, y) => G.base * (n > 4 ? 0.85 : 1) * (0.7 + 0.3 * Math.min(1, Math.max(0, (y - G.EDGE) / (G.START - G.EDGE))));
    const cmOf = (G, y) => Math.max(0, Math.round((y - G.EDGE) / G.pxcm));
    const body = h('div', { class: 'body', style: 'touch-action:none' });
    root.replaceChildren(top(s.cuTitle, setup), body);
    const T0 = performance.now(), now = () => (performance.now() - T0) / 1000;
    let G = null;
    cv = liveCanvas(body, (c, W, H) => {
      const t = now(); G = geo(W, H);
      // 바닥 (테이블 밖)
      c.fillStyle = '#1E1E28'; c.fillRect(0, 40, W, G.EDGE - 40);
      for (let i = 0; i < 9; i++) { c.fillStyle = 'rgba(255,255,255,.03)'; c.fillRect(i * 52, 40, 26, G.EDGE - 40); }
      // 테이블
      c.beginPath(); c.roundRect(G.X0, G.EDGE, G.X1 - G.X0, H - G.EDGE + 20, [6, 6, 0, 0]); c.fillStyle = '#8D5A34'; c.fill();
      for (let i = 1; i < 5; i++) { c.fillStyle = 'rgba(0,0,0,.18)'; c.fillRect(G.X0 + (G.X1 - G.X0) * i / 5 - 1, G.EDGE, 2, H); }
      const zg = c.createLinearGradient(0, G.EDGE, 0, G.EDGE + G.pxcm * 18); zg.addColorStop(0, 'rgba(255,215,64,.55)'); zg.addColorStop(1, 'rgba(255,215,64,0)');
      c.fillStyle = zg; c.fillRect(G.X0, G.EDGE, G.X1 - G.X0, G.pxcm * 18);
      c.fillStyle = '#C08A5B'; c.fillRect(G.X0, G.EDGE - 3, G.X1 - G.X0, 6);
      txt(c, '🔥', G.X0 + 14, G.EDGE + 15, 14, '#fff'); txt(c, '🔥', G.X1 - 14, G.EDGE + 15, 14, '#fff');
      for (let k = 0; k <= 90; k += 10) { const y = G.EDGE + k * G.pxcm; c.fillStyle = 'rgba(255,255,255,.35)'; c.fillRect(G.X1 + 3, y, 8, 2); txt(c, String(k), G.X1 + 22, y + 1, 10, 'rgba(255,255,255,.5)', 700); }
      c.setLineDash([8, 8]); c.beginPath(); c.moveTo(G.X0, G.START); c.lineTo(G.X1, G.START); c.strokeStyle = 'rgba(255,255,255,.4)'; c.lineWidth = 2; c.stroke(); c.setLineDash([]);
      // 사람 점
      for (let i = 0; i < n; i++) { const x = W / 2 + (i - (n - 1) / 2) * 30, on = i === g.turn && g.phase !== 'done'; c.globalAlpha = on ? 1 : 0.45; c.beginPath(); c.arc(x, 20, on ? 12 : 9, 0, 7); c.fillStyle = CU_COLS[i]; c.fill(); c.globalAlpha = 1; txt(c, String(i + 1), x, 21, 11, '#000'); }
      // 멈춘 잔들
      const best = g.res.reduce((b, r, i) => (r != null && r >= 0 && (b < 0 || r < g.res[b]) ? i : b), -1);
      for (let i = 0; i < n; i++) {
        const r = g.res[i]; if (r == null || r < 0 || (i === g.turn && g.phase !== 'next' && g.phase !== 'done')) continue;
        const y = G.EDGE + r * G.pxcm; wolfMug(c, laneX(G, i), y, size(G, y), CU_COLS[i], String(i + 1), 0);
        txt(c, `${i === best ? '🥇 ' : ''}${r}cm`, laneX(G, i), y + 14, 12, '#fff', 800);
      }
      const i = g.turn, x = i < n ? laneX(G, i) : 0;
      if (g.phase === 'aim' || g.phase === 'drag') {
        const d = g.pull, my = G.START + 30 + d, k = d / PMAX;
        for (const sx of [-1, 1]) { c.beginPath(); c.arc(x + sx * 34, G.START + 4, 6, 0, 7); c.fillStyle = '#5D4037'; c.fill(); }
        c.beginPath(); c.moveTo(x - 34, G.START + 4); c.lineTo(x - 18, my - 6); c.moveTo(x + 34, G.START + 4); c.lineTo(x + 18, my - 6); c.strokeStyle = '#FF7043'; c.lineWidth = 3.5; c.lineCap = 'round'; c.stroke();
        wolfMug(c, x, my, size(G, G.START), CU_COLS[i], String(i + 1), Math.sin(t * 30) * 0.3 * k);
        if (d > 4) { c.setLineDash([6, 8]); c.beginPath(); c.moveTo(x, G.START - 60); c.lineTo(x, G.START - 150); c.strokeStyle = 'rgba(255,255,255,.55)'; c.lineWidth = 3; c.stroke(); c.setLineDash([]); c.beginPath(); c.moveTo(x - 10, G.START - 140); c.lineTo(x, G.START - 156); c.lineTo(x + 10, G.START - 140); c.stroke(); }
        if (g.phase === 'aim') { const b = Math.sin(t * 4) * 8; txt(c, '👆', x + 6, my + 28 + b, 34, '#fff'); txt(c, s.cuHint, W / 2, H - 22, 15, '#fff', 800); }
      } else if (g.phase === 'slide') {
        const u = Math.min(1, (t - g.t0) / g.dur), y = g.y0 - g.D * (1 - (1 - u) * (1 - u));
        if (g.fallY && y <= g.fallY) { g.phase = 'fall'; g.t0 = t; vibe.strong(); tone(220, 0.25, 'triangle'); tone(90, 0.5, 'sawtooth', 0.12); }
        else {
          for (let k = 1; k < 4 && u < 1; k++) { c.globalAlpha = 0.12; wolfMug(c, x, y + k * 14 * (1 - u), size(G, y), CU_COLS[i], '', 0); } c.globalAlpha = 1;
          wolfMug(c, x, y, size(G, y), CU_COLS[i], String(i + 1), Math.sin(t * 22) * (1 - u));
          if (u >= 1) { g.res[i] = cmOf(G, y); g.phase = 'next'; g.t0 = t; vibe.tap(); tone(523, 0.15); }
        }
      }
      if (g.phase === 'fall') {
        const f = t - g.t0, ye = G.EDGE - 12, sz = size(G, G.EDGE) * Math.max(0.15, 1 - f * 0.9), rot = Math.min(1.9, f * 4);
        if (f > 0.15) { const p = Math.min(1, (f - 0.15) / 0.5); c.beginPath(); c.ellipse(x - 8, G.EDGE - 34, 50 * p, 13 * p, 0, 0, 7); c.fillStyle = 'rgba(255,179,0,.75)'; c.fill(); }
        if (f < 1.1) { c.save(); c.globalAlpha = Math.max(0, 1 - f / 1.1); c.translate(x, ye + f * 25); c.rotate(-rot); wolfMug(c, 0, 0, sz, CU_COLS[i], String(i + 1), 1); c.restore(); }
        for (let k = 0; k < 7 && f < 1.2; k++) { const a = -Math.PI / 2 - 1.1 + k * 0.36, d = f * 80; c.globalAlpha = Math.max(0, 1 - f); txt(c, '💦', x + Math.cos(a) * d, G.EDGE - 26 + Math.sin(a) * d, 15, '#fff'); } c.globalAlpha = 1;
        if (f > 0.3) txt(c, s.cuFall, W / 2, G.EDGE + 60, 24, '#4FC3F7');
        if (f > 1.3) { g.res[i] = -1; g.phase = 'next'; g.t0 = t; }
      }
      if (g.phase === 'next' && t - g.t0 > 0.9) {
        g.turn++; g.pull = 0;
        if (g.turn >= n) { g.phase = 'done'; g.doneT = t; const fall = g.res.some((r) => r < 0); vibe.tick(); if (fall) tone(330, 0.4, 'triangle'); }
        else g.phase = 'aim';
      }
      if (g.phase === 'done') {
        const v = Math.min(1, (t - g.doneT) / 0.4); c.globalAlpha = v;
        const fallen = g.res.map((r, k) => (r < 0 ? k : -1)).filter((k) => k >= 0);
        const losers = fallen.length ? fallen : [g.res.reduce((b, r, k) => (r > g.res[b] ? k : b), 0)];
        const order = [...g.res.keys()].sort((a, b) => (g.res[a] < 0) - (g.res[b] < 0) || g.res[a] - g.res[b]);
        const ph = 200, py = H * 0.42;
        c.beginPath(); c.roundRect(18, py, W - 36, ph, 22); c.fillStyle = 'rgba(10,10,16,.9)'; c.fill();
        txt(c, '🏆', W / 2, py + 30, 30, '#fff');
        const cw = Math.min(70, (W - 60) / n);
        order.forEach((k, j) => { const rx = W / 2 + (j - (n - 1) / 2) * cw; c.beginPath(); c.arc(rx, py + 80, 15, 0, 7); c.fillStyle = CU_COLS[k]; c.fill(); txt(c, String(k + 1), rx, py + 81, 14, '#000'); txt(c, g.res[k] < 0 ? '💦' : `${j === 0 ? '🥇' : ''}${g.res[k]}`, rx, py + 112, 13, '#fff', 800); });
        txt(c, `${losers.map((k) => k + 1).join(' · ')} → ${s.shotWord}`, W / 2, py + 160, 22, '#FF5A6E');
        c.globalAlpha = 1;
        if (t - g.doneT > 1.2) pill(c, W, H, s.playAgain);
      }
    });
    const el = cv.canvas;
    el.addEventListener('pointerdown', (e) => {
      e.preventDefault(); const t = now();
      if (g.phase === 'done') { if (t - g.doneT > 1.2) { fresh(); vibe.tap(); } return; }
      if (g.phase !== 'aim') return;
      const r = el.getBoundingClientRect(); g.downY = e.clientY - r.top; g.phase = 'drag'; g.pull = 0;
      try { el.setPointerCapture(e.pointerId); } catch (_) { /* 무시 */ }
      audio();
    });
    el.addEventListener('pointermove', (e) => {
      if (g.phase !== 'drag') return; const r = el.getBoundingClientRect();
      const p = Math.max(0, Math.min(PMAX, e.clientY - r.top - g.downY));
      if (Math.floor(p / 20) !== Math.floor(g.pull / 20) && p > g.pull) vibe.tap();
      g.pull = p;
    });
    const up = () => {
      if (g.phase !== 'drag' || !G) return;
      if (g.pull < 8) { g.phase = 'aim'; g.pull = 0; return; }
      const k = g.pull / PMAX, Dmax = (G.START + 30 + PMAX - G.EDGE) * 1.25;
      g.y0 = G.START + 30 + g.pull; g.D = Math.pow(k, 1.5) * Dmax; g.dur = 0.6 + 1.5 * Math.sqrt(g.D / Dmax);
      g.fallY = g.y0 - g.D < G.EDGE - 4 ? G.EDGE - 4 : 0;
      g.t0 = now(); g.phase = 'slide'; vibe.tick(); tone(180, 0.12, 'triangle');
    };
    el.addEventListener('pointerup', up); el.addEventListener('pointercancel', up);
  };
  setup();
  return () => { if (cv) cv.stop(); };
}
