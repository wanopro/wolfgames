// v2.0 게임 — 🎰 늑대 슬롯머신 (🍀 운빨) · 🧱 늑대 젠가 · ⚖️ 늑대 균형 잡기 (⚡ 순발력) · 🃏 늑대 짝 맞추기 (⚔️ 대결)
// 앱 V20Games.kt 와 같은 방식 — 글자 없이 그림만 봐도 알게
import { S, h, top, setupPanel, vibe, stepper } from './core.js';
import { coin } from './art.js';
import { stage, tone } from './games-v18.js';
import { yWolf, YB_COLS } from './games-v19.js';

const TAU = Math.PI * 2;
const ease = (u) => { u = Math.min(1, Math.max(0, u)); return u < 0.5 ? 2 * u * u : 1 - Math.pow(-2 * u + 2, 2) / 2; };
const easeOut = (u) => { u = Math.min(1, Math.max(0, u)); return 1 - Math.pow(1 - u, 3); };
const font = (c, size, w = 900) => { c.font = `${w} ${size}px -apple-system, "Noto Sans KR", "Noto Sans Thai", sans-serif`; c.textAlign = 'center'; c.textBaseline = 'middle'; };
const txt = (c, s, x, y, size, color, w = 900) => { font(c, size, w); c.fillStyle = color; c.fillText(s, x, y); };
const fitTxt = (c, s, x, y, size, color, maxW) => { font(c, size); const w = c.measureText(s).width; txt(c, s, x, y, w > maxW ? size * maxW / w : size, color); };
const pref = (k, d, lo, hi) => { try { const v = +localStorage.getItem(k); if (v >= lo && v <= hi) return v; } catch (_) { /* 무시 */ } return d; };
const save = (k, v) => { try { localStorage.setItem(k, String(v)); } catch (_) { /* 무시 */ } };

/** 사람 점 줄 (지금 차례 밝게) — y 위치에 */
function turnDots(c, W, n, cur, y, extra) {
  const r = Math.min(14, W / (n * 3.2));
  for (let i = 0; i < n; i++) {
    const x = W / 2 + (i - (n - 1) / 2) * r * 2.8;
    c.globalAlpha = i === cur ? 1 : 0.35;
    c.beginPath(); c.arc(x, y, r * (i === cur ? 1.15 : 1), 0, TAU); c.fillStyle = YB_COLS[i]; c.fill();
    txt(c, String(i + 1), x, y + 1, r * 0.95, '#000'); c.globalAlpha = 1;
    if (extra) extra(i, x, y + r + 12);
  }
}
/** 결과 카드 (가운데 크게) */
function resultCard(c, W, H, v, title, line, col = '#FF5A6E', cy = H / 2) {
  const k = (0.6 + 0.4 * Math.min(1, v / 0.25)) * Math.min(1, W / 380), sh = v < 0.4 ? Math.sin(v * 60) * 7 * (1 - v / 0.4) : 0;
  c.save(); c.translate(W / 2 + sh, cy); c.scale(k, k);
  c.beginPath(); c.roundRect(-165, -82, 330, 164, 26); c.fillStyle = 'rgba(12,12,18,.94)'; c.fill(); c.strokeStyle = col; c.lineWidth = 4; c.stroke();
  fitTxt(c, title, 0, -34, 40, col, 300); fitTxt(c, line, 0, 30, 34, '#FFD740', 300);
  c.restore();
}

// ===================== 🎰 늑대 슬롯머신 =====================
// 그림 5종: 0 늑대 · 1 코인 · 2 별 · 3 하트 · 4 해골(💀). 해골이 하나라도 → 벌칙 / 늑대 3개 → 다 같이 / 같은 그림 3개 → 지목 / 나머지 → 통과
export const SL_W = [3, 3, 3, 3, 1.5];
export function slPick(rnd = Math.random) { let r = rnd() * SL_W.reduce((a, b) => a + b, 0); for (let k = 0; k < SL_W.length; k++) { r -= SL_W[k]; if (r < 0) return k; } return 0; }
export function slOutcome(rnd = Math.random) { const a = slPick(rnd), b = slPick(rnd); let c3 = slPick(rnd); if (a === b && a !== 4 && rnd() < 0.4) c3 = a; return [a, b, c3]; }
export function slJudge(r) { if (r.includes(4)) return 'bad'; if (r[0] === r[1] && r[1] === r[2]) return r[0] === 0 ? 'all' : 'pick'; return 'pass'; }
function star(c, x, y, r, col) { c.beginPath(); for (let i = 0; i < 10; i++) { const a = i * Math.PI / 5 - Math.PI / 2, rr = i % 2 ? r * 0.45 : r; c.lineTo(x + Math.cos(a) * rr, y + Math.sin(a) * rr); } c.closePath(); c.fillStyle = col; c.fill(); c.lineWidth = r * 0.12; c.strokeStyle = '#7A5A00'; c.stroke(); }
function heart(c, x, y, s, col) { c.beginPath(); c.moveTo(x, y + s * 0.35); c.bezierCurveTo(x - s * 1.1, y - s * 0.35, x - s * 0.45, y - s * 1.05, x, y - s * 0.45); c.bezierCurveTo(x + s * 0.45, y - s * 1.05, x + s * 1.1, y - s * 0.35, x, y + s * 0.35); c.fillStyle = col; c.fill(); }
export function skull(c, x, y, r) {
  c.beginPath(); c.arc(x, y - r * 0.1, r * 0.78, Math.PI * 0.85, Math.PI * 2.15); c.lineTo(x + r * 0.5, y + r * 0.55); c.lineTo(x - r * 0.5, y + r * 0.55); c.closePath(); c.fillStyle = '#F3F3F3'; c.fill(); c.lineWidth = r * 0.08; c.strokeStyle = '#555'; c.stroke();
  for (const s of [-1, 1]) { c.beginPath(); c.ellipse(x + s * r * 0.3, y - r * 0.1, r * 0.2, r * 0.24, 0, 0, TAU); c.fillStyle = '#2B2B33'; c.fill(); }
  c.beginPath(); c.moveTo(x, y + r * 0.1); c.lineTo(x - r * 0.1, y + r * 0.28); c.lineTo(x + r * 0.1, y + r * 0.28); c.closePath(); c.fillStyle = '#2B2B33'; c.fill();
  for (const k of [-0.25, 0, 0.25]) { c.beginPath(); c.moveTo(x + k * r, y + r * 0.42); c.lineTo(x + k * r, y + r * 0.6); c.lineWidth = r * 0.07; c.strokeStyle = '#555'; c.stroke(); }
}
export function slSymbol(c, k, x, y, s, t = 0) {
  if (k === 0) yWolf(c, x, y + s * 0.1, s * 0.62, 0.3, 1, t);
  else if (k === 1) coin(c, x, y, s * 0.7, 1);
  else if (k === 2) star(c, x, y, s * 0.8, '#FFD54F');
  else if (k === 3) heart(c, x, y + s * 0.25, s * 0.9, '#FF4D6D');
  else skull(c, x, y, s * 0.85);
}
export function slotMachine(root, back) {
  const s = S(); let cv = null, P = pref('wg-sl-n', 4, 1, 8);
  const setup = () => { if (cv) { cv.stop(); cv = null; } root.replaceChildren(top(s.slTitle, back), h('div', { class: 'body' }, setupPanel(s.slDesc, play, stepper(s.players, P, 1, 8, (v) => { P = v; save('wg-sl-n', v); })))); };
  const play = () => {
    const g = { cur: 0, ts: 1e9, res: [0, 1, 2], stops: [0, 0, 0], judged: null, jt: 0, ticks: 0, landed: 0 };
    const spin = (t) => { g.ts = t; g.res = slOutcome(); const slow = g.res[0] === g.res[1] ? 1.1 : 0; g.stops = [1.0, 1.5, 2.0 + slow]; g.judged = null; g.landed = 0; g.ticks = 0; tone(220, 0.2, 'square', 0.12); vibe.tap(); };
    const rnd = (i, r) => { const x = Math.sin(i * 12.9898 + r * 78.233) * 43758.5453; return x - Math.floor(x); };
    cv = stage(root, s.slTitle, setup, (c, W, H, t) => {
      const st = t - g.ts, spinning = st >= 0 && st < g.stops[2] + 0.3;
      turnDots(c, W, P, g.cur, 30);
      const mw = Math.min(W * 0.9, 400, H * 0.62), mh = mw * 0.98, mx = W / 2 - mw / 2 - mw * 0.04, my = Math.max(64, H * 0.46 - mh / 2);
      // 위 간판: 늑대 얼굴 + 깜빡이 불
      c.beginPath(); c.roundRect(mx + mw * 0.12, my - mw * 0.08, mw * 0.76, mw * 0.2, mw * 0.06); c.fillStyle = '#B71C1C'; c.fill();
      for (let i = 0; i < 9; i++) { const on = spinning ? ((Math.floor(t * 10) + i) % 2 === 0) : ((Math.floor(t * 2) + i) % 3 === 0); c.beginPath(); c.arc(mx + mw * 0.17 + i * mw * 0.083, my - mw * 0.08 + mw * 0.035, mw * 0.018, 0, TAU); c.fillStyle = on ? '#FFE082' : '#6D1B1B'; c.fill(); }
      yWolf(c, W / 2 - mw * 0.04, my - mw * 0.02, mw * 0.11, spinning ? 0.6 : 0.2, 1, t);
      // 몸통
      c.beginPath(); c.roundRect(mx, my + mw * 0.1, mw, mh * 0.8, mw * 0.08); c.fillStyle = '#E53935'; c.fill(); c.lineWidth = 6; c.strokeStyle = '#7F1D1D'; c.stroke();
      c.beginPath(); c.roundRect(mx + mw * 0.04, my + mw * 0.14, mw * 0.92, mh * 0.72, mw * 0.06); c.fillStyle = '#C62828'; c.fill();
      // 창 3칸
      const ww = mw * 0.84, wx = mx + mw * 0.08, wy = my + mw * 0.22, wh = mh * 0.44, cw = ww / 3;
      c.beginPath(); c.roundRect(wx - 6, wy - 6, ww + 12, wh + 12, 14); c.fillStyle = '#FFD54F'; c.fill();
      for (let r = 0; r < 3; r++) {
        const x0 = wx + r * cw, cx = x0 + cw / 2, cy = wy + wh / 2, sz = Math.min(cw, wh) * 0.36;
        c.save(); c.beginPath(); c.roundRect(x0 + 3, wy, cw - 6, wh, 10); c.fillStyle = '#FFFDF5'; c.fill(); c.clip();
        if (st >= 0 && st < g.stops[r]) { // 돌아가는 중
          const v = st < 0.25 ? st / 0.25 : 1, pos = st * 14 * v, i0 = Math.floor(pos), f = pos - i0;
          for (let d = -1; d <= 1; d++) { const k = Math.floor(rnd(i0 - d, r) * 5); c.globalAlpha = 0.9; slSymbol(c, k, cx, cy + (d + f) * wh * 0.55, sz, t); }
          c.globalAlpha = 1; c.fillStyle = 'rgba(255,255,255,.25)'; c.fillRect(x0, wy, cw, wh);
          if (r === 2 && g.res[0] === g.res[1] && st > g.stops[1]) { c.fillStyle = `rgba(255,213,79,${0.25 + 0.2 * Math.sin(t * 30)})`; c.fillRect(x0, wy, cw, wh); }
        } else { // 멈춤 (살짝 튕김)
          const k = st >= 0 ? g.res[r] : [0, 2, 1][r], u = st - g.stops[r], bo = st >= 0 && u < 0.35 ? Math.sin(u / 0.35 * Math.PI) * (1 - u / 0.35) * wh * 0.12 : 0;
          slSymbol(c, k, cx, cy + bo, sz * (g.judged && g.judged !== 'pass' && (k === 4 || g.judged !== 'bad') ? 1 + 0.08 * Math.sin(t * 10) : 1), t);
        }
        c.restore();
        // 멈출 때 소리
        if (st >= g.stops[r] && g.landed <= r && st < g.stops[2] + 1) { g.landed = r + 1; tone(140 + r * 40, 0.12, 'square', 0.14); vibe.tap(); }
      }
      // 가운데 선
      c.strokeStyle = 'rgba(229,57,53,.5)'; c.lineWidth = 3; c.beginPath(); c.moveTo(wx, wy + wh / 2); c.lineTo(wx + ww, wy + wh / 2); c.stroke();
      // 아래 동전 받이
      c.beginPath(); c.roundRect(mx + mw * 0.2, my + mw * 0.1 + mh * 0.62, mw * 0.6, mh * 0.12, 12); c.fillStyle = '#7F1D1D'; c.fill();
      // 레버
      const lx = mx + mw + mw * 0.06, ly0 = my + mw * 0.3, pull = st >= 0 && st < 0.45 ? Math.sin(st / 0.45 * Math.PI) : 0, kh = mh * 0.42 * (1 - pull * 1.6);
      c.beginPath(); c.roundRect(lx - 9, ly0 + mh * 0.12, 18, mh * 0.34, 8); c.fillStyle = '#9E9E9E'; c.fill();
      c.lineWidth = 9; c.strokeStyle = '#BDBDBD'; c.lineCap = 'round'; c.beginPath(); c.moveTo(lx, ly0 + mh * 0.29); c.lineTo(lx + 6, ly0 + mh * 0.29 - kh); c.stroke();
      c.beginPath(); c.arc(lx + 6, ly0 + mh * 0.29 - kh, mw * 0.06, 0, TAU); c.fillStyle = '#FF1744'; c.fill(); c.beginPath(); c.arc(lx + 3, ly0 + mh * 0.29 - kh - 4, mw * 0.02, 0, TAU); c.fillStyle = 'rgba(255,255,255,.7)'; c.fill();
      // 돌아가는 소리
      if (spinning && st < g.stops[2]) { const n = Math.floor(st * 14); if (n > g.ticks) { g.ticks = n; tone(900, 0.02, 'square', 0.04); } }
      // 판정
      if (st >= g.stops[2] + 0.3 && !g.judged && st >= 0) { g.judged = slJudge(g.res); g.jt = t; if (g.judged === 'pass') tone(1320, 0.15, 'triangle', 0.18); else { vibe.strong(); tone(90, 0.6, 'sawtooth', 0.2); } }
      const fs = Math.min(20, W / 19);
      if (g.judged) {
        const v = t - g.jt;
        if (g.judged === 'pass') txt(c, s.slPass, W / 2, H - 60, fs * 1.3, '#69F0AE');
        else resultCard(c, W, H, v, g.judged === 'bad' ? s.shotWord : g.judged === 'all' ? s.slAll : s.slPick, g.judged === 'pick' ? '👉' : s.ybShotN(g.judged === 'all' ? '🙌' : String(g.cur + 1)), g.judged === 'pick' ? '#4FC3F7' : '#FF5A6E', H - 120);
        if (v > 0.8) txt(c, s.turnN(((g.cur + 1) % P) + 1) + ' 👉', W / 2, H - 22, 15, YB_COLS[(g.cur + 1) % P], 700);
      } else if (!spinning) txt(c, s.turnN(g.cur + 1) + ' — 🎰', W / 2, H - 60, fs, YB_COLS[g.cur]);
    }, (x, y, t) => {
      const st = t - g.ts;
      if (st >= 0 && !g.judged) return; // 돌아가는 중
      if (g.judged) { if (t - g.jt < 0.8) return; g.cur = (g.cur + 1) % P; }
      spin(t);
    });
  };
  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== 🧱 늑대 젠가 =====================
// 12층 × 2칸 (2026-11-27 사용자: 가운데 줄 빼기는 어색 → 2줄). 블록을 꾹 누르고 바깥으로(왼쪽 블록은 왼쪽, 오른쪽은 오른쪽) 끌어서 빼기.
// 빨리 끌수록 흔들림 게이지가 참 (위험한 블록일수록 더 빨리). 블록마다 걸리는 곳(삐걱)이 1~2군데 — 거기선 아주 천천히.
// 게이지가 꽉 차면 와르르. 끝까지 빼면 맨 위로 → 다음 사람. 중간에 손 떼면 블록이 다시 들어감 (같은 사람이 다시)
export const JG_LAYERS = 12, JG_W = 2, JG_VSAFE = 1.35, JG_VSNAG = 0.35;
/** 위험도: danger(게이지 차는 배율), lethal(층이 비면 반쯤 빼는 순간 무너짐) */
export function jgDanger(layers, li, k, pulls) {
  const L = layers[li].slice(); L[k] = 0; const n = L.reduce((a, b) => a + b, 0);
  const lethal = n === 0;
  return { lethal, danger: 1.3 + pulls * 0.06 + (li < 3 ? 0.25 : 0) };
}
export function jgCanPull(layers, li) { const topFull = layers[layers.length - 1].every((b) => b); const maxLi = layers.length - (topFull ? 2 : 3); return li <= maxLi; }
export function jenga(root, back) {
  const s = S(); let cv = null, P = pref('wg-jg-n', 4, 1, 8);
  const setup = () => { if (cv) { cv.stop(); cv = null; } root.replaceChildren(top(s.jgTitle, back), h('div', { class: 'body' }, setupPanel(s.jgDesc, play, stepper(s.players, P, 1, 8, (v) => { P = v; save('wg-jg-n', v); })))); };
  const play = () => {
    const g = {};
    const reset = () => Object.assign(g, { layers: Array.from({ length: JG_LAYERS }, () => [1, 1]), cur: 0, pulls: 0, stress: 0, grab: null, back: null, fly: null, fallT: 0, debris: [], now: 0, lastT: 0, creakT: -9 });
    reset();
    if (location.search.includes('debug')) window._jg = g; // 테스트용
    const geo = (W, H) => {
      const n = g.layers.length + 1, bh = Math.min(30, (H * 0.66) / n), bw = Math.min(W * 0.3, 130, bh * 4.4), baseY = H - 64, cx = W / 2;
      return { bh, bw, baseY, cx, rect: (li, k) => ({ x: cx + (k - 1) * bw, y: baseY - (li + 1) * bh, w: bw, h: bh }) };
    };
    const blockDraw = (c, x, y, w, hh, li, k, alpha = 1, hot = 0) => {
      c.globalAlpha = alpha;
      const base = li % 2 ? ['#E7B77A', '#C98B4A'] : ['#F2C98E', '#D69B57'], gr = c.createLinearGradient(0, y, 0, y + hh); gr.addColorStop(0, base[0]); gr.addColorStop(1, base[1]);
      c.beginPath(); c.roundRect(x + 1.5, y + 1.5, w - 3, hh - 3, 4); c.fillStyle = gr; c.fill(); c.lineWidth = hot ? 3 : 1.5; c.strokeStyle = hot ? '#FFD740' : '#8D5A2B'; c.stroke();
      c.strokeStyle = 'rgba(141,90,43,.35)'; c.lineWidth = 1;
      if (li % 2) { c.beginPath(); c.arc(x + w / 2, y + hh / 2, hh * 0.28, 0, TAU); c.stroke(); c.beginPath(); c.arc(x + w / 2, y + hh / 2, hh * 0.14, 0, TAU); c.stroke(); }
      else for (const f of [0.35, 0.62]) { c.beginPath(); c.moveTo(x + 4, y + hh * f); c.quadraticCurveTo(x + w / 2, y + hh * (f + 0.12), x + w - 4, y + hh * f); c.stroke(); }
      c.globalAlpha = 1;
    };
    const collapse = (t, G, li) => {
      g.fallT = t; g.fallLi = li; g.loser = g.cur; vibe.strong(); tone(70, 0.9, 'sawtooth', 0.24);
      const topY = G.baseY - g.layers.length * G.bh;
      for (let l = li; l < g.layers.length; l++) for (let k = 0; k < 2; k++) if (g.layers[l][k]) { const r = G.rect(l, k), off = g.grab && g.grab.li === l && g.grab.k === k ? g.grab.dir * g.grab.p * G.bw : 0; g.debris.push({ x: r.x + off, y: r.y, vx: (Math.random() - 0.5) * 260 + (l - li) * (Math.random() < 0.5 ? -12 : 12), vy: -Math.random() * 160, vr: (Math.random() - 0.5) * 8, li: l, k }); }
      g.wolf = { x: G.cx, y: topY - G.bh * 1.25, vx: (Math.random() - 0.5) * 200, vy: -260 };
      g.layers = g.layers.slice(0, li); g.grab = null; g.back = null;
    };
    cv = stage(root, s.jgTitle, setup, (c, W, H, t) => {
      const G = geo(W, H), dt = Math.min(0.05, Math.max(0.001, t - g.lastT)); g.lastT = t; g.now = t;
      turnDots(c, W, P, g.cur, 30);
      // 흔들림 게이지 계산
      const gr = g.grab; let snag = false;
      if (gr && !g.fallT) {
        const v = Math.max(0, (gr.p - gr.pPrev) / dt); gr.pPrev = gr.p;
        snag = gr.snags.some((sn) => Math.abs(gr.p - sn) < 0.06);
        const vs = snag ? JG_VSNAG : JG_VSAFE, over = Math.max(0, v - vs);
        g.stress += over * 0.55 * gr.danger * dt * 1.6;
        if (snag && over > 0 && !gr.hit[gr.snags.findIndex((sn) => Math.abs(gr.p - sn) < 0.06)]) { gr.hit[gr.snags.findIndex((sn) => Math.abs(gr.p - sn) < 0.06)] = 1; g.stress += 0.12 * gr.danger; g.creakT = t; tone(180, 0.15, 'sawtooth', 0.12); vibe.tap(); }
        if (snag && t - g.creakT > 0.25 && v > 0.05) { g.creakT = t; tone(240 + Math.random() * 60, 0.06, 'square', 0.06); }
        // 위아래: 빼는 중에 손가락이 위아래로 움직이면(처음 잡은 높이에서 벗어나면) 게이지 참
        const tol = G.bh * 0.45, err = Math.abs(gr.fy - gr.y0); gr.off = err > tol;
        if (gr.off) { g.stress += Math.min(2.5, err / tol - 1 + 0.4) * 0.5 * gr.danger * dt; if (t - g.creakT > 0.3) { g.creakT = t; tone(150 + Math.random() * 40, 0.06, 'square', 0.07); } }
        else if (v < 0.2) g.stress = Math.max(0, g.stress - 0.18 * dt);
        if (gr.lethal && gr.p > 0.55) g.stress = 1;
        if (g.stress >= 1) collapse(t, G, gr.li);
        else if (gr.p >= 1) { // 다 뺐다 → 맨 위로
          const after = g.layers.map((l) => l.slice()); after[gr.li][gr.k] = 0;
          let toLi = after.length - 1; if (after[toLi].every((b) => b)) { after.push([0, 0]); toLi++; }
          const toK = after[toLi].indexOf(0);
          g.fly = { li: gr.li, k: gr.k, dir: gr.dir, toLi, toK, t, after }; g.grab = null; g.pulls++; tone(660, 0.08, 'triangle', 0.14); vibe.tap();
        }
      } else if (!g.fallT) g.stress = Math.max(0, g.stress - 0.3 * dt);
      // 바닥
      c.fillStyle = '#3E3E52'; c.beginPath(); c.roundRect(G.cx - G.bw * 1.5, G.baseY, G.bw * 3, 14, 7); c.fill();
      // 탑 (게이지만큼 흔들)
      const st = g.stress, sway = g.fallT ? 0 : Math.pow(st, 1.5) * 0.07 * Math.sin(t * 14) + (snag && g.grab ? Math.sin(t * 60) * 0.004 : 0);
      const bob = g.fallT ? 0 : Math.pow(st, 1.5) * 4 * Math.sin(t * 23) + (g.grab && g.grab.off ? Math.sin(t * 50) * 1.5 : 0);
      c.save(); c.translate(G.cx, G.baseY + bob); c.rotate(sway); c.translate(-G.cx, -G.baseY);
      for (let li = 0; li < g.layers.length; li++) for (let k = 0; k < 2; k++) {
        if (!g.layers[li][k]) continue;
        const r = G.rect(li, k);
        if (g.grab && g.grab.li === li && g.grab.k === k) { blockDraw(c, r.x + g.grab.dir * g.grab.p * G.bw * 1.05, r.y + Math.max(-G.bh * 0.3, Math.min(G.bh * 0.3, (g.grab.fy - g.grab.y0) * 0.3)) + (snag ? Math.sin(t * 70) * 1.2 : 0), r.w, r.h, li, k, 1, 1); continue; }
        if (g.fly && g.fly.li === li && g.fly.k === k) continue;
        if (g.back && g.back.li === li && g.back.k === k) { const u = Math.min(1, (t - g.back.t) / 0.25), pp = g.back.p0 * (1 - easeOut(u)); blockDraw(c, r.x + g.back.dir * pp * G.bw * 1.05, r.y, r.w, r.h, li, k); if (u >= 1) g.back = null; continue; }
        blockDraw(c, r.x, r.y, r.w, r.h, li, k);
      }
      const topY = G.baseY - g.layers.length * G.bh;
      if (!g.fallT) yWolf(c, G.cx, topY - G.bh * 1.25, G.bh * 1.1, st > 0.5 ? 0.6 : 0, st > 0.5 ? -1 : 1, t);
      c.restore();
      // 손가락 높이 점 (처음 잡은 높이 가로선에서 벗어나면 🔴)
      if (g.grab && !g.fallT) {
        const gr2 = g.grab, r = G.rect(gr2.li, gr2.k), cy0 = r.y + r.h / 2 + bob, fx = r.x + r.w / 2 + gr2.dir * gr2.p * G.bw * 1.05, fyy = cy0 + (gr2.fy - gr2.y0);
        c.lineWidth = 2; c.strokeStyle = 'rgba(255,255,255,.35)'; for (const d of [-1, 1]) { c.beginPath(); c.moveTo(fx - 16, cy0 + d * G.bh * 0.45); c.lineTo(fx + 16, cy0 + d * G.bh * 0.45); c.stroke(); }
        c.beginPath(); c.arc(fx, fyy, 7, 0, TAU); c.fillStyle = gr2.off ? '#FF5A6E' : '#69F0AE'; c.fill(); c.lineWidth = 2; c.strokeStyle = '#fff'; c.stroke();
      }
      // 삐걱 표시 (지그재그)
      if (g.grab && snag) { const r = G.rect(g.grab.li, g.grab.k), x = r.x + r.w / 2 + g.grab.dir * (g.grab.p * G.bw * 1.05 + r.w * 0.6); c.strokeStyle = '#FFD740'; c.lineWidth = 3; for (const d of [-1, 1]) { c.beginPath(); for (let i = 0; i < 4; i++) c.lineTo(x + g.grab.dir * i * 6, r.y + r.h / 2 + d * (8 + (i % 2) * 6)); c.stroke(); } }
      // 위로 올라가는 블록
      if (g.fly) { const f = g.fly, u = ease((t - f.t) / 0.4), r = G.rect(f.li, f.k), d = G.rect(f.toLi, f.toK), sx = r.x + f.dir * G.bw * 1.05; blockDraw(c, sx + (d.x - sx) * u, r.y + (d.y - r.y) * u - Math.sin(u * Math.PI) * 60, r.w, r.h, f.toLi, f.toK); if (t - f.t >= 0.4) { f.after[f.toLi][f.toK] = 1; g.layers = f.after; g.fly = null; g.cur = (g.cur + 1) % P; g.stress *= 0.5; } }
      // 흔들림 게이지 (위)
      if (!g.fallT) {
        const bw = Math.min(220, W * 0.55), x0 = W / 2 - bw / 2, y0 = 54; c.beginPath(); c.roundRect(x0, y0, bw, 12, 6); c.fillStyle = '#2A2A3A'; c.fill();
        const col = st < 0.5 ? '#69F0AE' : st < 0.8 ? '#FFD740' : '#FF5A6E'; c.beginPath(); c.roundRect(x0, y0, Math.max(12, bw * Math.min(1, st)), 12, 6); c.fillStyle = col; c.fill();
        txt(c, st > 0.8 && Math.sin(t * 16) > 0 ? '⚠️' : '〰️', x0 - 16, y0 + 6, 16, '#fff');
      }
      if (g.fallT) {
        const u = t - g.fallT, gy = 900;
        for (const d of g.debris) { const x = d.x + d.vx * u, y = Math.min(G.baseY - G.bh, d.y + d.vy * u + 0.5 * gy * u * u); c.save(); c.translate(x + G.bw / 2, y + G.bh / 2); c.rotate(d.vr * Math.min(u, 0.9)); blockDraw(c, -G.bw / 2, -G.bh / 2, G.bw, G.bh, d.li, d.k); c.restore(); }
        const w = g.wolf; c.save(); c.translate(w.x + w.vx * u, Math.min(G.baseY - G.bh * 1.2, w.y + w.vy * u + 0.5 * gy * u * u)); c.rotate(u * 6); yWolf(c, 0, 0, G.bh * 1.1, 1, -1, t); c.restore();
        if (u > 0.9) resultCard(c, W, H, u - 0.9, '💥 ' + s.shotWord, s.ybShotN(String(g.loser + 1)), '#FF5A6E', H * 0.36);
        if (u > 2) txt(c, s.playAgain, W / 2, H - 22, 15, '#9A9AB0', 700);
      } else if (!g.grab && !g.fly) txt(c, s.turnN(g.cur + 1) + ' 👆 ⇄', W / 2, H - 24, Math.min(18, W / 20), YB_COLS[g.cur]);
    }, (x, y, t, W, H) => {
      if (g.fallT) { if (t - g.fallT > 2) { reset(); vibe.tap(); } return; }
      if (g.fly || g.grab) return;
      const G = geo(W, H);
      for (let li = g.layers.length - 1; li >= 0; li--) for (let k = 0; k < 2; k++) {
        if (!g.layers[li][k]) continue; const r = G.rect(li, k);
        if (x >= r.x && x <= r.x + r.w && y >= r.y - 3 && y <= r.y + r.h + 3) {
          if (!jgCanPull(g.layers, li)) { tone(200, 0.08, 'square', 0.1); vibe.tap(); return; }
          if (g.back && g.back.li === li && g.back.k === k) g.back = null;
          const d = jgDanger(g.layers, li, k, g.pulls), nS = Math.random() < 0.5 ? 1 : 2, snags = [];
          for (let i = 0; i < nS; i++) snags.push(0.18 + Math.random() * 0.62);
          g.grab = { li, k, dir: k === 0 ? -1 : 1, x0: x, y0: y, fy: y, p: 0, pPrev: 0, danger: d.danger, lethal: d.lethal, snags, hit: [0, 0], bw: G.bw, off: false };
          vibe.tap(); tone(520, 0.05, 'triangle', 0.1);
          return;
        }
      }
    });
    // 끌기 · 놓기
    const pos = (e) => { const r = cv.canvas.getBoundingClientRect(); return e.clientX - r.left; }, posY = (e) => { const r = cv.canvas.getBoundingClientRect(); return e.clientY - r.top; };
    cv.canvas.addEventListener('pointermove', (e) => {
      const gr = g.grab; if (!gr || g.fallT) return;
      const dx = pos(e) - gr.x0; gr.fy = posY(e);
      if (gr.dir === 0) { if (Math.abs(dx) > 6) gr.dir = Math.sign(dx); else return; }
      gr.p = Math.max(0, Math.min(1, (dx * gr.dir) / (gr.bw * 1.05)));
    });
    const up = () => { const gr = g.grab; if (!gr || g.fallT) return; if (gr.dir) g.back = { li: gr.li, k: gr.k, dir: gr.dir, p0: gr.p, t: g.now }; g.grab = null; };
    cv.canvas.addEventListener('pointerup', up); cv.canvas.addEventListener('pointercancel', up); cv.canvas.addEventListener('pointerleave', up);
  };
  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== 🃏 늑대 짝 맞추기 =====================
// 카드 앞면: 같은 늑대 + 머리 위 그림 (같은 그림끼리 짝), 배경색도 짝마다 다름
export const MM_ICONS = ['👑', '🎩', '🎀', '🕶️', '⭐', '🌸', '🍀', '❤️', '🎧', '🧢'];
export const MM_BG = ['#FFE082', '#B39DDB', '#F8BBD0', '#90CAF9', '#FFF59D', '#F48FB1', '#A5D6A7', '#EF9A9A', '#80DEEA', '#FFCC80'];
export function mmDeal(pairs, rnd = Math.random) { const a = []; for (let i = 0; i < pairs; i++) a.push(i, i); for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(rnd() * (i + 1)); [a[i], a[j]] = [a[j], a[i]]; } return a; }
export function memory(root, back) {
  const s = S(); let cv = null, P = pref('wg-mm-n', 3, 2, 6), PR = pref('wg-mm-p', 8, 4, 10);
  const setup = () => { if (cv) { cv.stop(); cv = null; } root.replaceChildren(top(s.mmTitle, back), h('div', { class: 'body' }, setupPanel(s.mmDesc, play,
    stepper(s.players, P, 2, 6, (v) => { P = v; save('wg-mm-n', v); }), stepper(s.mmPairs, PR, 4, 10, (v) => { PR = v; save('wg-mm-p', v); })))); };
  const play = () => {
    const g = {};
    const reset = () => Object.assign(g, { deck: mmDeal(PR), up: [], flipT: {}, gone: {}, cur: 0, score: Array(P).fill(0), busyT: 0, endT: 0, lastMatch: -9 });
    reset();
    const layout = (W, H) => { const n = g.deck.length, cols = n <= 8 ? 4 : n <= 12 ? 4 : n <= 16 ? 4 : 5, rows = Math.ceil(n / cols), top0 = 86, aw = W - 24, ah = H - top0 - 70, cw = Math.min(aw / cols, (ah / rows) * 0.78), chh = cw / 0.78;
      return { cols, rows, cw, chh, x0: W / 2 - (cols * cw) / 2, y0: top0 + (ah - rows * chh) / 2, pos: (i) => { const r = Math.floor(i / cols), inRow = r === rows - 1 ? n - r * cols : cols, cc = i - r * cols; return { x: W / 2 + (cc - (inRow - 1) / 2) * cw - cw / 2, y: top0 + (ah - rows * chh) / 2 + r * chh }; } }; };
    const face = (c, k, x, y, w, hh, t) => {
      c.beginPath(); c.roundRect(x, y, w, hh, 10); c.fillStyle = '#fff'; c.fill(); c.lineWidth = 2.5; c.strokeStyle = '#2B2B33'; c.stroke();
      c.beginPath(); c.arc(x + w / 2, y + hh * 0.56, w * 0.36, 0, TAU); c.fillStyle = MM_BG[k]; c.fill();
      yWolf(c, x + w / 2, y + hh * 0.6, w * 0.24, 0.2, 1, t);
      font(c, w * 0.3, 400); c.fillText(MM_ICONS[k], x + w / 2, y + hh * 0.22);
    };
    const backSide = (c, x, y, w, hh) => {
      c.beginPath(); c.roundRect(x, y, w, hh, 10); const gr = c.createLinearGradient(x, y, x + w, y + hh); gr.addColorStop(0, '#3949AB'); gr.addColorStop(1, '#1A237E'); c.fillStyle = gr; c.fill(); c.lineWidth = 2.5; c.strokeStyle = '#0D1240'; c.stroke();
      c.beginPath(); c.roundRect(x + 5, y + 5, w - 10, hh - 10, 7); c.strokeStyle = 'rgba(255,255,255,.35)'; c.lineWidth = 1.5; c.stroke();
      const px = x + w / 2, py = y + hh / 2, r = w * 0.14; c.fillStyle = 'rgba(255,255,255,.85)';
      c.beginPath(); c.ellipse(px, py + r * 0.5, r * 1.05, r * 0.85, 0, 0, TAU); c.fill();
      for (const [dx, dy] of [[-1.05, -0.55], [-0.38, -1.15], [0.38, -1.15], [1.05, -0.55]]) { c.beginPath(); c.ellipse(px + dx * r, py + dy * r, r * 0.38, r * 0.46, 0, 0, TAU); c.fill(); }
    };
    cv = stage(root, s.mmTitle, setup, (c, W, H, t) => {
      turnDots(c, W, P, g.endT ? -1 : g.cur, 30, (i, x, y) => txt(c, '⭐' + g.score[i], x, y + 4, 13, '#FFD740', 800));
      const L = layout(W, H);
      for (let i = 0; i < g.deck.length; i++) {
        const p = L.pos(i), gone = g.gone[i];
        if (gone && t - gone.t > 0.9) continue;
        const ft = g.flipT[i], isUp = g.up.includes(i) || (gone && true);
        let sx = 1, showFace = isUp;
        if (ft !== undefined && t - ft < 0.25 && !gone) { const u = (t - ft) / 0.25; sx = Math.abs(Math.cos(u * Math.PI)); showFace = u < 0.5 ? !isUp : isUp; }
        c.save(); c.translate(p.x + L.cw / 2, p.y + L.chh / 2);
        if (gone) { const u = Math.max(0, (t - gone.t - 0.4) / 0.5); c.globalAlpha = 1 - u; const k = 1 + 0.12 * Math.sin(Math.min(1, (t - gone.t) / 0.4) * Math.PI); c.scale(k * (1 - u * 0.4), k * (1 - u * 0.4)); }
        c.scale(sx, 1);
        const w = L.cw - 8, hh = L.chh - 8;
        if (showFace) face(c, g.deck[i], -w / 2, -hh / 2, w, hh, t); else backSide(c, -w / 2, -hh / 2, w, hh);
        if (gone && t - gone.t < 0.4) { c.beginPath(); c.roundRect(-w / 2, -hh / 2, w, hh, 10); c.lineWidth = 5; c.strokeStyle = YB_COLS[gone.p]; c.stroke(); }
        c.restore(); c.globalAlpha = 1;
      }
      // 판정 (2장 뒤집고 0.9초 뒤)
      if (g.up.length === 2 && g.busyT && t - g.busyT > 0.9) {
        const [a, b] = g.up;
        if (g.deck[a] === g.deck[b]) { g.gone[a] = { t, p: g.cur }; g.gone[b] = { t, p: g.cur }; g.score[g.cur]++; g.lastMatch = t; tone(880, 0.08, 'triangle', 0.16); setTimeout(() => tone(1320, 0.12, 'triangle', 0.16), 90); vibe.tap(); }
        else { g.flipT[a] = t; g.flipT[b] = t; g.cur = (g.cur + 1) % P; tone(240, 0.12, 'square', 0.1); }
        g.up = []; g.busyT = 0;
        if (Object.keys(g.gone).length === g.deck.length) { g.endT = t + 0.9; }
      }
      if (g.endT && t > g.endT) {
        const min = Math.min(...g.score), losers = g.score.map((v, i) => (v === min ? i + 1 : 0)).filter(Boolean);
        if (!g.endSnd) { g.endSnd = 1; vibe.strong(); tone(90, 0.6, 'sawtooth', 0.2); }
        resultCard(c, W, H, t - g.endT, s.shotWord, s.ybShotN(losers.join(' · ')));
        if (t - g.endT > 1.2) txt(c, s.playAgain, W / 2, H - 22, 15, '#9A9AB0', 700);
      } else if (!g.endT) {
        const again = t - g.lastMatch < 1.2;
        txt(c, again ? '⭐ +1 👍' : s.turnN(g.cur + 1), W / 2, H - 40, Math.min(20, W / 19), again ? '#FFD740' : YB_COLS[g.cur]);
      }
    }, (x, y, t, W, H) => {
      if (g.endT) { if (t - g.endT > 1.2) { reset(); vibe.tap(); } return; }
      if (g.up.length >= 2) return;
      const L = layout(W, H);
      for (let i = 0; i < g.deck.length; i++) {
        if (g.gone[i] || g.up.includes(i)) continue;
        const p = L.pos(i);
        if (x >= p.x && x <= p.x + L.cw && y >= p.y && y <= p.y + L.chh) {
          g.up.push(i); g.flipT[i] = t; vibe.tap(); tone(600, 0.04, 'triangle', 0.1);
          if (g.up.length === 2) g.busyT = t;
          return;
        }
      }
    });
  };
  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== ⚖️ 늑대 균형 잡기 =====================
// 기울기 센서(없으면 손가락/마우스)로 접시를 기울여 늑대를 위에. 한 사람 6초 → "넘겨!" 3초 (늑대 멈춤) → 다음 사람. 갈수록 접시 작아지고 바람 세짐
export const BB_HOLD = 6, BB_PASS = 3;
export function balance(root, back) {
  const s = S(); let cv = null, P = pref('wg-bb-n', 4, 1, 8), off = null;
  const setup = () => { if (cv) { cv.stop(); cv = null; } if (off) { off(); off = null; } root.replaceChildren(top(s.bbTitle, back), h('div', { class: 'body' }, setupPanel(s.bbDesc, start, stepper(s.players, P, 1, 8, (v) => { P = v; save('wg-bb-n', v); })))); };
  const start = async () => { // iOS: 버튼 누를 때 센서 허락 받기
    try { if (typeof DeviceOrientationEvent !== 'undefined' && typeof DeviceOrientationEvent.requestPermission === 'function') await DeviceOrientationEvent.requestPermission(); } catch (_) { /* 무시 */ }
    play();
  };
  const play = () => {
    const g = { tilt: { x: 0, y: 0 }, sensor: false, finger: { x: 0, y: 0 }, useFinger: false };
    const reset = (t) => Object.assign(g, { x: 0, y: 0, vx: 0, vy: 0, t0: t, play: 0, cur: 0, phaseT: t, passing: false, dropT: 0, gust: null, nextGust: 3.5, lastT: t, lv: 0, drift: null });
    reset(0); g.t0 = null;
    if (location.search.includes('debug')) window._bb = g; // 테스트용 (주소에 ?debug 붙였을 때만)
    const onOri = (e) => { if (e.gamma == null) return; g.sensor = true; g.tilt.x = Math.max(-1, Math.min(1, e.gamma / 28)); g.tilt.y = Math.max(-1, Math.min(1, e.beta / 28)); };
    window.addEventListener('deviceorientation', onOri);
    off = () => window.removeEventListener('deviceorientation', onOri);
    cv = stage(root, s.bbTitle, setup, (c, W, H, t) => {
      if (g.t0 === null) reset(t);
      const dt = Math.min(0.05, t - g.lastT); g.lastT = t;
      if (!g.passing && !g.dropT) g.play += dt;
      const el = g.play, R0 = Math.min(W, H - 160) * 0.42, lv = g.lv, pr = R0 * Math.max(0.32, 1 - el * 0.02 - lv * 0.035), cx = W / 2, cy = H * 0.52;
      if (!g.sensor && el > 1.2) g.useFinger = true;
      const tx = g.useFinger ? g.finger.x : g.tilt.x, ty = g.useFinger ? g.finger.y : g.tilt.y;
      // 물리
      if (!g.dropT && !g.passing) {
        let ax = tx * 460 * (1 + lv * 0.1), ay = ty * 460 * (1 + lv * 0.1);
        if (g.drift) { g.drift.a += g.drift.w * dt; ax += Math.cos(g.drift.a) * g.drift.f; ay += Math.sin(g.drift.a) * g.drift.f; } // 한 턴 지나면: 계속 미는 바람
        if (el > g.nextGust && !g.gust) { const a = Math.random() * TAU; g.gust = { a, t: el, f: 320 + el * 10 + lv * 70 }; }
        if (g.gust) { const u = (el - g.gust.t) / 0.9; if (u > 1) { g.gust = null; g.nextGust = el + Math.max(lv ? 1.1 : 1.8, 4.5 - el * 0.06 - lv * 0.4) + Math.random() * (lv ? 1 : 1.5); } else { ax += Math.cos(g.gust.a) * g.gust.f * Math.sin(u * Math.PI); ay += Math.sin(g.gust.a) * g.gust.f * Math.sin(u * Math.PI); } }
        const damp = 0.986 + Math.min(0.007, lv * 0.0015); g.vx = (g.vx + ax * dt) * damp; g.vy = (g.vy + ay * dt) * damp; g.x += g.vx * dt; g.y += g.vy * dt;
        if (Math.hypot(g.x, g.y) > pr) { g.dropT = t; g.dropA = Math.atan2(g.y, g.x); vibe.strong(); tone(120, 0.5, 'sawtooth', 0.2); setTimeout(() => tone(70, 0.6, 'sawtooth', 0.2), 200); }
        // 차례
        const ph = t - g.phaseT;
        if (ph > BB_HOLD) { g.passing = true; g.phaseT = t; g.vx = 0; g.vy = 0; g.gust = null; vibe.tap(); tone(880, 0.1, 'triangle', 0.18); }
      } else if (!g.dropT && g.passing) { // 넘기는 중: 늑대 멈춤
        const ph = t - g.phaseT;
        if (ph > BB_PASS) { g.passing = false; g.phaseT = t; g.cur = (g.cur + 1) % P; g.vx = 0; g.vy = 0; g.lv = Math.min(10, g.lv + 1); g.drift = { a: Math.random() * TAU, w: (Math.random() < 0.5 ? -1 : 1) * (0.5 + g.lv * 0.08), f: 70 + g.lv * 22 }; g.nextGust = el + (g.lv > 1 ? 0.8 : 1.5); tone(660, 0.08, 'triangle', 0.14); vibe.tap(); }
        else if (Math.floor(ph) !== g.lastCount) { g.lastCount = Math.floor(ph); tone(520, 0.06, 'triangle', 0.1); }
      }
      turnDots(c, W, P, g.cur, 30);
      if (lv > 0) txt(c, '🔥 Lv.' + (lv + 1), W / 2, 58, 15, lv > 4 ? '#FF5A6E' : '#FFB74D', 800);
      // 접시 (기울기 따라 그림자·하이라이트)
      c.save(); c.translate(cx, cy);
      c.beginPath(); c.ellipse(tx * 12, ty * 12 + 14, pr * 1.04, pr * 1.04, 0, 0, TAU); c.fillStyle = 'rgba(0,0,0,.35)'; c.fill();
      const gr = c.createRadialGradient(-tx * pr * 0.5, -ty * pr * 0.5, pr * 0.1, 0, 0, pr * 1.05); gr.addColorStop(0, '#FFFFFF'); gr.addColorStop(1, '#C9D3E0');
      c.beginPath(); c.arc(0, 0, pr, 0, TAU); c.fillStyle = gr; c.fill(); c.lineWidth = 8; c.strokeStyle = '#90A4AE'; c.stroke();
      c.beginPath(); c.arc(0, 0, pr * 0.78, 0, TAU); c.lineWidth = 2; c.strokeStyle = 'rgba(144,164,174,.6)'; c.stroke();
      // 기울기 화살표
      const tl = Math.hypot(tx, ty); if (tl > 0.08) { c.save(); c.rotate(Math.atan2(ty, tx)); c.globalAlpha = Math.min(0.5, tl); c.fillStyle = '#FF7043'; c.beginPath(); c.moveTo(pr * 0.55, 0); c.lineTo(pr * 0.38, -14); c.lineTo(pr * 0.38, 14); c.closePath(); c.fill(); c.restore(); }
      // 바람
      if (g.gust) { const u = (el - g.gust.t) / 0.9; c.save(); c.rotate(g.gust.a); c.globalAlpha = Math.max(0, Math.min(1, Math.sin(u * Math.PI))); for (let i = -1; i <= 1; i++) { c.beginPath(); const yy = i * 26, xx = -pr * 1.1 + u * pr * 0.6; c.moveTo(xx, yy); c.quadraticCurveTo(xx + 30, yy - 12, xx + 60, yy); c.lineWidth = 5; c.strokeStyle = '#4FC3F7'; c.lineCap = 'round'; c.stroke(); } c.restore(); }
      // 계속 미는 바람 (한 턴 지난 뒤)
      if (g.drift && !g.dropT) { c.save(); c.rotate(g.drift.a); c.globalAlpha = g.passing ? 0.25 : 0.6; c.fillStyle = '#4FC3F7'; for (const k of [0, 1]) { const xx = -pr - 22 + k * 14; c.beginPath(); c.moveTo(xx + 10, 0); c.lineTo(xx, -9); c.lineTo(xx, 9); c.closePath(); c.fill(); } c.restore(); }
      // 늑대
      const wr = Math.min(36, pr * 0.3);
      if (!g.dropT) yWolf(c, g.x, g.y, wr, Math.min(1, Math.hypot(g.x, g.y) / pr * 1.3), Math.hypot(g.x, g.y) > pr * 0.7 ? -1 : 0, t);
      else { const u = Math.min(1, (t - g.dropT) / 0.8); c.save(); c.translate(Math.cos(g.dropA) * (pr + u * 40), Math.sin(g.dropA) * (pr + u * 40)); c.rotate(u * 5); c.scale(1 - u * 0.6, 1 - u * 0.6); c.globalAlpha = 1 - u * 0.5; yWolf(c, 0, 0, wr, 1, -1, t); c.restore(); }
      c.restore();
      // 안내
      const fs = Math.min(20, W / 19), ph = t - g.phaseT;
      if (g.dropT) {
        const v = t - g.dropT;
        if (v > 0.6) resultCard(c, W, H, v - 0.6, s.shotWord, s.ybShotN(String(g.cur + 1)), '#FF5A6E', H * 0.5);
        if (v > 1.8) txt(c, s.playAgain, W / 2, H - 22, 15, '#9A9AB0', 700);
      } else if (g.passing) { const k = 1 + 0.15 * Math.abs(Math.sin(t * 8)), left = Math.max(1, Math.ceil(BB_PASS - ph)); txt(c, s.bbPass + ' ' + left, W / 2, H - 70, fs * 1.6 * k, '#FFD740'); txt(c, '→ ' + (((g.cur + 1) % P) + 1), W / 2, H - 36, fs, YB_COLS[(g.cur + 1) % P]); txt(c, '⏸️', W / 2, H * 0.52 - pr - 24, 26, '#fff'); }
      else {
        txt(c, s.bbHold(g.cur + 1), W / 2, H - 70, fs, YB_COLS[g.cur]);
        // 남은 시간 막대
        const bw = Math.min(260, W * 0.6), u = Math.min(1, ph / BB_HOLD); c.beginPath(); c.roundRect(W / 2 - bw / 2, H - 46, bw, 10, 5); c.fillStyle = '#2A2A3A'; c.fill(); c.beginPath(); c.roundRect(W / 2 - bw / 2, H - 46, bw * (1 - u), 10, 5); c.fillStyle = YB_COLS[g.cur]; c.fill();
        if (g.useFinger) txt(c, s.bbNoSensor, W / 2, H - 20, 11, '#9A9AB0', 600);
      }
    }, (x, y, t) => { if (g.dropT && t - g.dropT > 1.8) { reset(t); vibe.tap(); } });
    // 손가락 / 마우스로 기울이기 (센서 없을 때)
    const mv = (e) => { const r = cv.canvas.getBoundingClientRect(); g.finger.x = Math.max(-1, Math.min(1, (e.clientX - r.left - r.width / 2) / (r.width * 0.4))); g.finger.y = Math.max(-1, Math.min(1, (e.clientY - r.top - r.height * 0.52) / (r.width * 0.4))); };
    cv.canvas.addEventListener('pointermove', mv);
  };
  setup();
  return () => { if (cv) cv.stop(); if (off) off(); };
}
