// v1.7 새 게임: 숫자 폭탄 · 풍선 불기 (🍀 운빨)
import { C, S, h, btn, top, setupPanel, liveCanvas, vibe, resultOverlay, SEP, cat } from './core.js';
import { choice } from './games-quick.js';
import { bombArt, wolfFace } from './art.js';
import { dazedWolf } from './dazed.js';

const ease = (u) => 1 - Math.pow(1 - Math.min(1, Math.max(0, u)), 3);
const font = (c, size, w = 900) => { c.font = `${w} ${size}px -apple-system, "Noto Sans KR", "Noto Sans Thai", sans-serif`; c.textAlign = 'center'; c.textBaseline = 'middle'; };
const txt = (c, s, x, y, size, color, w = 900) => { font(c, size, w); c.fillStyle = color; c.fillText(s, x, y); };

// ===================== 🔢 숫자 폭탄 =====================
export const NB_MAX = [30, 50, 100, 200];
export function numberBomb(root, back) {
  const s = S();
  let max = 100, cv = null;
  try { const v = +localStorage.getItem('wg-nb-max'); if (NB_MAX.includes(v)) max = v; } catch (_) { /* 무시 */ }
  const setup = () => {
    if (cv) { cv.stop(); cv = null; }
    root.replaceChildren(top(s.nbTitle, back), h('div', { class: 'body' }, setupPanel(s.nbDesc, play,
      choice(s.nbMax, NB_MAX.map((v) => [v, `1 ~ ${v}`]), max, (v) => { max = v; try { localStorage.setItem('wg-nb-max', String(v)); } catch (_) { /* 무시 */ } }))));
  };
  const play = () => {
    const g = { lo: 1, hi: max, bomb: 1 + Math.floor(Math.random() * max), typed: '', msg: s.nbEnter, msgCls: '', boomAt: 0, last: [], t0: performance.now() };
    const now = () => (performance.now() - g.t0) / 1000;
    const rangeEl = h('div', { class: 'nb-range' });
    const msgEl = h('div', { class: 'nb-msg' });
    const typedEl = h('div', { class: 'nb-typed' });
    const histEl = h('div', { class: 'nb-hist' });
    const art = h('div', { class: 'nb-art' });
    const keys = h('div', { class: 'nb-keys' });
    const render = () => {
      rangeEl.textContent = `${g.lo} ~ ${g.hi}`;
      msgEl.textContent = g.msg; msgEl.className = 'nb-msg ' + g.msgCls;
      typedEl.textContent = g.typed || '—';
      histEl.replaceChildren(...g.last.slice(-8).map(([n, d]) => h('span', { class: d }, `${n}${d === 'up' ? '⬆️' : '⬇️'}`)));
    };
    const press = (k) => {
      if (g.boomAt) return;
      vibe.tap();
      if (k === '⌫') g.typed = g.typed.slice(0, -1);
      else if (k === 'ok') {
        const n = parseInt(g.typed, 10); g.typed = '';
        if (!n || n < g.lo || n > g.hi) { g.msg = s.nbOut; g.msgCls = 'warn'; render(); return; }
        if (n === g.bomb) {
          g.boomAt = now(); g.lo = g.hi = n; vibe.strong(); render();
          setTimeout(() => { msgEl.textContent = cat(s.nbBoom, ` ${n}`); msgEl.className = 'nb-msg down'; histEl.replaceChildren(h('span', { class: 'nb-shot' }, s.shotWord)); typedEl.remove();
            keys.replaceWith(h('div', { class: 'nb-end' }, btn(s.playAgain, () => { cv && cv.stop(); play(); }), btn(s.again, setup, 'btn alt'))); }, 900);
          return;
        }
        if (n < g.bomb) { g.lo = n + 1; g.msg = cat(s.nbUp, ' · ', s.nbPass); g.msgCls = 'up'; g.last.push([n, 'up']); }
        else { g.hi = n - 1; g.msg = cat(s.nbDown, ' · ', s.nbPass); g.msgCls = 'down'; g.last.push([n, 'down']); }
        g.popAt = now();
      } else if (g.typed.length < String(max).length) g.typed = (g.typed + k).replace(/^0+/, '');
      render();
    };
    for (const k of ['1', '2', '3', '4', '5', '6', '7', '8', '9', '⌫', '0', 'ok']) keys.append(btn(k === 'ok' ? s.nbOk : k, () => press(k), 'nbk' + (k === 'ok' ? ' ok' : '')));
    if (cv) cv.stop();
    root.replaceChildren(top(s.nbTitle, setup), h('div', { class: 'body nb' }, art, rangeEl, msgEl, histEl, typedEl, keys));
    // 폭탄 그림: 범위가 좁아질수록 심지가 짧아짐, 맞히면 펑
    cv = liveCanvas(art, (c, W, H) => {
      const t = now(), fuse = (g.hi - g.lo + 1) / max, cx = W / 2, cy = H * 0.58, r = Math.min(W, H) * 0.3;
      if (!g.boomAt) {
        const sh = g.popAt && t - g.popAt < 0.3 ? Math.sin((t - g.popAt) * 60) * 4 * (1 - (t - g.popAt) / 0.3) : 0;
        bombArt(c, cx + sh, cy, r, Math.max(0.05, fuse), t);
        return;
      }
      const u = t - g.boomAt;
      // 💥 다음 연기 속에서 그을린 늑대가 헤롱헤롱
      if (u > 0.55) {
        const p = ease((u - 0.55) / 0.35), R = Math.min(W * 0.27, H * 0.3);
        c.save(); c.globalAlpha = p; c.translate(cx, cy); c.scale(0.7 + 0.3 * p, 0.7 + 0.3 * p); dazedWolf(c, 0, 0, R, t); c.restore();
        c.globalAlpha = Math.max(0, 1 - (u - 0.55) / 0.5); c.beginPath(); c.arc(cx, cy, R * 1.5, 0, 7); c.fillStyle = '#9097A3'; c.fill(); c.globalAlpha = 1;
        if (u > 1) txt(c, s.nbDazed, cx + Math.sin(t * 3) * 6, Math.min(H - 14, cy + R * 1.05 + 14), 18, '#FFD740');
        return;
      }
      for (let k = 0; k < 18; k++) {
        const a = k / 18 * Math.PI * 2, d = ease(u / 0.6) * Math.min(W, H) * (0.3 + 0.2 * (k % 3));
        c.globalAlpha = Math.max(0, 1 - u / 1.4); c.beginPath(); c.arc(cx + Math.cos(a) * d, cy + Math.sin(a) * d, 10 - (k % 3) * 2, 0, 7); c.fillStyle = ['#FFD740', '#FF7043', '#FF3D5A'][k % 3]; c.fill();
      }
      c.globalAlpha = 1;
      txt(c, '💥', cx, cy, Math.min(W, H) * 0.5 * (0.6 + 0.4 * ease(u / 0.25)), '#fff');
    });
    render();
  };
  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== 🎈 풍선 불기 =====================
const BL_COLS = [['#FF6B81', '#E0435C'], ['#4FC3F7', '#1E88E5'], ['#81C784', '#2E7D32'], ['#BA68C8', '#7B1FA2'], ['#FFB74D', '#EF6C00']];
export const BL_MIN = 4, BL_MAX = 24;
export function balloon(root, back) {
  const s = S();
  let cv = null, ci = 0;
  const setup = () => { if (cv) { cv.stop(); cv = null; } root.replaceChildren(top(s.blTitle, back), h('div', { class: 'body' }, setupPanel(s.blDesc, play))); };
  const play = () => {
    const g = {};
    const round = () => { Object.assign(g, { pumps: 0, popAt: BL_MIN + Math.floor(Math.random() * (BL_MAX - BL_MIN + 1)), lastPump: -9, popT: 0, col: BL_COLS[ci++ % BL_COLS.length], plus: [] }); };
    round();
    const body = h('div', { class: 'body', style: 'touch-action:none' });
    root.replaceChildren(top(s.blTitle, setup), body);
    const t0 = performance.now(), now = () => (performance.now() - t0) / 1000;
    cv = liveCanvas(body, (c, W, H) => {
      const t = now(), cx = W / 2, cy = H * 0.44, base = Math.min(W, H) * 0.15;
      if (!g.popT) {
        const grow = Math.min(1, g.pumps / BL_MAX), R = base * (1 + grow * 1.7);
        const k = t - g.lastPump, sq = k < 0.35 ? Math.sin(k * 30) * 0.08 * (1 - k / 0.35) : 0;
        const wob = Math.sin(t * 2.2) * 0.04 * (1 + grow * 2);
        // 줄
        c.beginPath(); c.moveTo(cx, cy + R * 1.12); c.bezierCurveTo(cx + 18, cy + R * 1.4, cx - 18, cy + R * 1.6, cx + 6, H - 70); c.strokeStyle = 'rgba(255,255,255,.6)'; c.lineWidth = 2; c.stroke();
        c.save(); c.translate(cx, cy); c.rotate(wob); c.scale(1 + sq, 1 - sq);
        // 매듭
        c.beginPath(); c.moveTo(-8, R * 1.13); c.lineTo(8, R * 1.13); c.lineTo(0, R * 1.0); c.closePath(); c.fillStyle = g.col[1]; c.fill();
        // 풍선 몸통
        const gr = c.createRadialGradient(-R * 0.35, -R * 0.45, R * 0.1, 0, 0, R * 1.15); gr.addColorStop(0, '#fff'); gr.addColorStop(0.18, g.col[0]); gr.addColorStop(1, g.col[1]);
        c.beginPath(); c.ellipse(0, 0, R * 0.92, R * 1.05, 0, 0, 7); c.fillStyle = gr; c.fill();
        c.beginPath(); c.ellipse(-R * 0.4, -R * 0.5, R * 0.14, R * 0.26, -0.5, 0, 7); c.fillStyle = 'rgba(255,255,255,.55)'; c.fill();
        // 늑대 얼굴 (풍선과 같이 커짐)
        c.save(); c.globalAlpha = 0.9; c.translate(0, R * 0.08); wolfFace(c, R * 0.55); c.restore();
        c.restore();
        // +1
        g.plus = g.plus.filter((p) => t - p[0] < 0.8);
        for (const [pt, px] of g.plus) { const u = (t - pt) / 0.8; c.globalAlpha = 1 - u; txt(c, '+1', px, cy - R * 1.1 - u * 40, 26, '#FFD740'); c.globalAlpha = 1; }
        txt(c, s.blPumps(g.pumps), W / 2, H - 52, 20, '#fff');
        txt(c, s.blTap, W / 2, H - 22, 14, '#9A9AB0', 700);
        return;
      }
      // 펑!
      const u = t - g.popT, R = base * (1 + Math.min(1, g.pumps / BL_MAX) * 1.7);
      for (let k = 0; k < 14; k++) {
        const a = k / 14 * Math.PI * 2 + k, d = ease(u / 0.5) * R * (1.4 + (k % 4) * 0.35);
        c.save(); c.translate(cx + Math.cos(a) * d, cy + Math.sin(a) * d + u * u * 120); c.rotate(a + u * 6); c.globalAlpha = Math.max(0, 1 - u / 1.6);
        c.beginPath(); c.moveTo(-10, -6); c.lineTo(12, -3); c.lineTo(6, 8); c.lineTo(-8, 5); c.closePath(); c.fillStyle = g.col[k % 2]; c.fill(); c.restore();
      }
      c.globalAlpha = 1;
      txt(c, '💥', cx, cy, R * 1.6 * (0.6 + 0.4 * ease(u / 0.2)), '#fff');
      if (u > 0.5) { txt(c, s.blPop, W / 2, cy + R * 1.25 + 10, 30, '#FFD740'); txt(c, s.shotWord, W / 2, cy + R * 1.25 + 56, 26, '#FF5A6E'); }
      if (u > 1.2) { c.fillStyle = 'rgba(0,0,0,.55)'; c.beginPath(); c.roundRect(W / 2 - 110, H - 86, 220, 44, 22); c.fill(); txt(c, s.playAgain, W / 2, H - 64, 17, '#fff'); }
    });
    cv.canvas.addEventListener('pointerdown', (e) => {
      e.preventDefault(); const t = now();
      if (g.popT) { if (t - g.popT > 1.2) { round(); vibe.tap(); } return; }
      const r = cv.canvas.getBoundingClientRect();
      g.pumps++; g.lastPump = t; g.plus.push([t, Math.max(30, Math.min(r.width - 30, e.clientX - r.left))]);
      if (g.pumps >= g.popAt) { g.popT = t; vibe.strong(); } else vibe.tap();
    });
  };
  setup();
  return () => { if (cv) cv.stop(); };
}
