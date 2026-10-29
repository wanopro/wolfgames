// v1.6 새 게임: 플링코(🍀 운빨) / 삼목 · 오목(⚔️ 대결)
import { C, S, h, top, setupPanel, liveCanvas, vibe, shuffle, SEP, cat } from './core.js';
import { coin } from './art.js';

const GOLD = '#FFD740', RED = '#FF3D5A';
const ease = (u) => 1 - Math.pow(1 - Math.min(1, Math.max(0, u)), 3);
const font = (c, size, w = 900) => { c.font = `${w} ${size}px -apple-system, "Noto Sans KR", "Noto Sans Thai", sans-serif`; c.textAlign = 'center'; c.textBaseline = 'middle'; };
const txt = (c, s, x, y, size, color, w = 900) => { font(c, size, w); c.fillStyle = color; c.fillText(s, x, y); };
/** 두 언어를 원래 크기 그대로 두 줄로 (자리가 넉넉한 곳용) */
const txtBig = (c, s, x, y, size, color) => {
  if (!String(s).includes(SEP)) return txt(c, s, x, y, size, color);
  const [m, sub] = String(s).split(SEP); txt(c, m, x, y - size * 0.58, size, color);
  const al = c.globalAlpha; c.globalAlpha = al * 0.85; txt(c, sub, x, y + size * 0.58, size, color); c.globalAlpha = al;
};
const wide = (c, s, size) => { font(c, size); return Math.max(...String(s).split(SEP).map((q) => c.measureText(q).width)); };

/** 캔버스 화면 하나 + 터치 좌표 */
function stage(root, title, onBack, game) {
  const body = h('div', { class: 'body', style: 'touch-action:none' });
  root.replaceChildren(top(title, onBack), body);
  const t0 = performance.now(), now = () => (performance.now() - t0) / 1000;
  const cv = liveCanvas(body, (ctx, w, hh) => { ctx.fillStyle = C.bg; ctx.fillRect(0, 0, w, hh); game.draw(ctx, w, hh, now()); });
  cv.canvas.addEventListener('pointerdown', (e) => {
    e.preventDefault();
    const r = cv.canvas.getBoundingClientRect(); const { w, h: hh } = cv.size();
    game.down(e.clientX - r.left, e.clientY - r.top, now(), w, hh);
  });
  cv.canvas.addEventListener('contextmenu', (e) => e.preventDefault());
  return cv;
}

/** 좁은 칸 안에 글자 맞추기 (단어 단위 줄바꿈 + 크기 줄이기). 두 언어면 아래에 흐리게 */
function fitLabel(c, str, cx, cy, maxW, size, color) {
  const parts = String(str).split(SEP);
  const wrap = (p, sz) => {
    font(c, sz, 800);
    const words = p.split(/\s+/).filter(Boolean), out = [];
    let cur = '';
    for (const wd of words) { const tr = cur ? cur + ' ' + wd : wd; if (c.measureText(tr).width <= maxW || !cur) cur = tr; else { out.push(cur); cur = wd; } }
    if (cur) out.push(cur);
    return out;
  };
  let sz = size, rows;
  for (;;) { rows = parts.map((p) => wrap(p, sz)); const wide = rows.flat().some((ln) => { font(c, sz, 800); return c.measureText(ln).width > maxW; }); if ((!wide && rows.flat().length <= 4) || sz <= 8) break; sz -= 0.5; }
  const all = rows.flatMap((r, k) => r.map((ln) => [ln, k])), lh = sz * 1.15;
  all.forEach(([ln, k], j) => { c.globalAlpha = k ? 0.75 : 1; txt(c, ln, cx, cy + (j - (all.length - 1) / 2) * lh, sz, color, 800); });
  c.globalAlpha = 1;
}

/** 위·아래 양쪽에서 읽히는 안내 글자 (위쪽은 뒤집어서) */
function bothSides(c, W, H, str, size, color, pad = 26) {
  txt(c, str, W / 2, H - pad - (str.includes(SEP) ? size * 0.25 : 0), size, color);
  c.save(); c.translate(W / 2, pad + (str.includes(SEP) ? size * 0.25 : 0)); c.rotate(Math.PI); txt(c, str, 0, 0, size, color); c.restore();
}

/** 위·아래 사람 각자 자기 쪽 글자 (msgs[0] 아래 사람, msgs[1] 위 사람 · 위쪽은 뒤집어서) */
function eachSide(c, W, H, msgs, size, cols, pad = 26) {
  msgs.forEach((m, i) => {
    let sz = size; while (wide(c, m, sz) > W - 72 && sz > 10) sz -= 0.5;
    const off = m.includes(SEP) ? sz * 0.6 : 0;
    c.save(); if (i) { c.translate(W, H); c.rotate(Math.PI); }
    txtBig(c, m, W / 2, H - pad - off, sz, cols[i]); c.restore();
  });
}

// ===================== 🔻 플링코 =====================
export const PLINKO_ICONS = ['🍺', '🥃', '🔄', '👉', '👈', '🍻', '✅'];
const PLINKO_COLS = ['#FF7043', '#29B6F6', '#78909C', '#AB47BC', '#EC407A', '#FFB300', '#43A047'];
const ROWS = 11, CR = 12, PR = 5;
export function board(W, H) {
  const n = PLINKO_ICONS.length, sw = (W - 24) / n, slotBot = H - 14, slotH = Math.min(118, Math.max(92, H * 0.17)), slotTop = slotBot - slotH;
  const top = Math.min(150, Math.max(96, H * 0.17)), gapY = (slotTop - 26 - top) / (ROWS - 1), pegs = [];
  for (let r = 0; r < ROWS; r++) { const odd = r % 2; if (odd) for (let i = 0; i <= n; i++) pegs.push([12 + sw * i, top + r * gapY]); else for (let i = 0; i < n; i++) pegs.push([12 + sw * (i + 0.5), top + r * gapY]); }
  return { pegs, sw, slotTop, slotBot, n, top, dropY: top - Math.min(48, gapY * 1.4) };
}
export function simulate(W, H, x0, seed) {
  const b = board(W, H); let s = seed; const rnd = () => (s = (s * 16807) % 2147483647) / 2147483647;
  let x = Math.max(12 + CR, Math.min(W - 12 - CR, x0)), y = b.dropY, vx = (rnd() - 0.5) * 30, vy = 0; const path = [], dt = 1 / 240, g0 = { y };
  for (let k = 0; k < 240 * 14; k++) {
    vy += 1500 * dt; x += vx * dt; y += vy * dt;
    for (const [px, py] of b.pegs) { const dx = x - px, dy = y - py, d = Math.hypot(dx, dy); if (d < CR + PR && d > 0) { let nx = dx / d, ny = dy / d; const out = px < W / 2 ? -1 : 1; if (Math.abs(px - (out < 0 ? 12 : W - 12)) < b.sw * 0.6 && dx * out > 0 && Math.abs(px - (out < 0 ? 12 : W - 12)) > 1) { nx = -out * 0.6; ny = -0.8; vx = -out * Math.max(80, Math.abs(vx)); } /* 벽 옆 좁은 틈: 안쪽으로 굴러가게 */ x = px + nx * (CR + PR); y = py + ny * (CR + PR); const vn = vx * nx + vy * ny; if (vn < 0) { vx -= 1.55 * vn * nx; vy -= 1.55 * vn * ny; vx += (rnd() - 0.5) * 40; } } }
    if (x < 12 + CR) { x = 12 + CR; vx = Math.abs(vx) * 0.5; } if (x > W - 12 - CR) { x = W - 12 - CR; vx = -Math.abs(vx) * 0.5; }
    if (y > b.slotTop) { const i = Math.max(0, Math.min(b.n - 1, Math.floor((x - 12) / b.sw))), l = 12 + i * b.sw, r = l + b.sw; if (x - CR < l + 2) { x = l + 2 + CR; vx = Math.abs(vx) * 0.4; } if (x + CR > r - 2) { x = r - 2 - CR; vx = -Math.abs(vx) * 0.4; } }
    if (y > b.slotBot - CR) { y = b.slotBot - CR; vy = -Math.abs(vy) * 0.25; vx *= 0.6; if (Math.abs(vy) < 80) { vy = 0; vx = 0; } }
    if (k % 60 === 0) { if (k && Math.abs(y - (g0.y)) < 3 && y < b.slotBot - CR - 1) { vx += (x < W / 2 ? 1 : -1) * 160; vy = -60; } g0.y = y; }
    if (k % 4 === 0) path.push([x, y]);
    if (y >= b.slotBot - CR - 0.5 && vy === 0) { path.push([x, y]); break; }
  }
  return { path, slot: Math.max(0, Math.min(b.n - 1, Math.floor((x - 12) / b.sw))) };
}
export function plinko(root, back) {
  const s = S();
  let cv = null;
  const setup = () => { if (cv) { cv.stop(); cv = null; } root.replaceChildren(top(s.plinkoTitle, back), h('div', { class: 'body' }, setupPanel(s.plinkoDesc, play))); };
  const play = () => {
    const labels = s.plinkoSlots.split('|');
    // phase: ready → drop → result → shuffle → ready
    const g = { phase: 'ready', order: shuffle([...Array(7).keys()]), prev: null, at: 0, drop: null, buzz: false };
    const startShuffle = (t) => { g.prev = g.order; g.order = shuffle(g.order); if (g.order.every((v, i) => v === g.prev[i])) g.order.reverse(); g.phase = 'shuffle'; g.at = t; g.drop = null; vibe.tick(); };
    cv = stage(root, s.plinkoTitle, setup, {
      down(x, y, t, W, H) {
        const b = board(W, H);
        if (g.phase === 'result' && t - g.at > 0.5) { startShuffle(t); return; }
        if (g.phase !== 'ready' || y > b.top - 6) return;
        g.drop = { x, sim: simulate(W, H, x, 1 + Math.floor(Math.random() * 2147483000)), at: t }; g.phase = 'drop'; g.buzz = false; vibe.tap();
      },
      draw(c, W, H, t) {
        const b = board(W, H);
        const bg = c.createLinearGradient(0, 0, 0, H); bg.addColorStop(0, '#1E2433'); bg.addColorStop(1, '#141824');
        c.beginPath(); c.roundRect(6, 6, W - 12, H - 12, 18); c.fillStyle = bg; c.fill(); c.lineWidth = 3; c.strokeStyle = '#2E3650'; c.stroke();
        let u = 0, landed = false;
        if (g.drop) { u = Math.floor((t - g.drop.at) * 60); landed = u >= g.drop.sim.path.length; }
        window.__plinkoPhase = g.phase; // 설명서 그림용
        if (g.phase === 'drop' && landed) { g.phase = 'result'; g.at = t; if (!g.buzz) { g.buzz = true; vibe.strong(); } }
        if (g.phase === 'result' && t - g.at > 2.6) startShuffle(t);
        if (g.phase === 'shuffle' && t - g.at > 0.7) { g.phase = 'ready'; g.prev = null; }
        // 안내
        const msg = g.phase === 'shuffle' ? s.plinkoShuffling : g.phase === 'ready' ? s.plinkoTap : '';
        if (msg) fitLabel(c, msg, W / 2, Math.max(30, b.dropY - 34), W - 40, 15, GOLD);
        if (g.phase === 'ready') {
          c.save(); c.setLineDash([6, 6]); c.strokeStyle = 'rgba(255,255,255,.28)'; c.lineWidth = 2; c.beginPath(); c.moveTo(18, b.dropY); c.lineTo(W - 18, b.dropY); c.stroke(); c.restore();
          const bob = Math.sin(t * 4) * 4; coin(c, W / 2, b.dropY + bob, CR, 1, 1); c.globalAlpha = 1;
        }
        // 핀
        for (const [px, py] of b.pegs) { c.beginPath(); c.arc(px, py, PR, 0, 7); c.fillStyle = '#CFD8DC'; c.fill(); c.beginPath(); c.arc(px - 1.5, py - 1.5, 1.8, 0, 7); c.fillStyle = '#fff'; c.fill(); }
        // 칸 (섞일 때는 빙글 뒤집힘)
        const hit = g.phase === 'result' || (g.phase === 'drop' && landed) ? g.drop.sim.slot : -1;
        const sh = g.phase === 'shuffle' ? Math.min(1, (t - g.at) / 0.6) : 1;
        for (let i = 0; i < b.n; i++) {
          const si = g.phase === 'shuffle' && sh < 0.5 && g.prev ? g.prev[i] : g.order[i], col = PLINKO_COLS[si];
          const x = 12 + i * b.sw, glow = i === hit && Math.floor(t * 8) % 2 === 0, sq = g.phase === 'shuffle' ? Math.abs(Math.cos(sh * Math.PI)) : 1;
          c.save(); c.translate(x + b.sw / 2, 0); c.scale(Math.max(0.05, sq), 1); c.translate(-(x + b.sw / 2), 0);
          c.beginPath(); c.roundRect(x + 2, b.slotTop + 10, b.sw - 4, b.slotBot - b.slotTop - 10, 8); c.fillStyle = glow ? col : col + '44'; c.fill();
          if (i === hit) { c.lineWidth = 3; c.strokeStyle = col; c.stroke(); }
          txt(c, PLINKO_ICONS[si], x + b.sw / 2, b.slotTop + 34, Math.min(24, b.sw * 0.5), '#fff');
          fitLabel(c, labels[si], x + b.sw / 2, (b.slotTop + 52 + b.slotBot) / 2, b.sw - 8, 12.5, '#fff');
          c.restore();
          if (i) { c.beginPath(); c.moveTo(x, b.slotTop); c.lineTo(x, b.slotBot); c.strokeStyle = '#CFD8DC'; c.lineWidth = 4; c.lineCap = 'round'; c.stroke(); }
        }
        // 코인
        if (g.drop && g.phase !== 'shuffle') {
          const p = g.drop.sim.path[Math.min(u, g.drop.sim.path.length - 1)];
          coin(c, p[0], p[1], CR, 1, 1);
          if (u < 10) { c.beginPath(); c.arc(g.drop.x, b.dropY, 14 + u * 3, 0, 7); c.strokeStyle = `rgba(255,255,255,${Math.max(0, 0.7 - u * 0.07)})`; c.lineWidth = 3; c.stroke(); }
        }
        if (g.phase === 'result') {
          const si = g.order[hit], col = PLINKO_COLS[si], pop = ease((t - g.at) / 0.25), dual = labels[si].includes(SEP);
          const bw = Math.min(W - 40, 300), bh = dual ? 128 : 96;
          c.save(); c.translate(W / 2, H * 0.4); c.scale(0.6 + 0.4 * pop, 0.6 + 0.4 * pop);
          c.beginPath(); c.roundRect(-bw / 2, -bh / 2, bw, bh, 22); c.fillStyle = 'rgba(0,0,0,.8)'; c.fill(); c.lineWidth = 4; c.strokeStyle = col; c.stroke();
          txt(c, PLINKO_ICONS[si], -bw / 2 + 38, 0, 34, '#fff');
          fitLabel(c, labels[si], 22, 0, bw - 96, 32, col);
          c.restore();
        }
      },
    });
  };
  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== 🟢🔴 삼목 =====================
const TTT_LINES = [[0, 1, 2], [3, 4, 5], [6, 7, 8], [0, 3, 6], [1, 4, 7], [2, 5, 8], [0, 4, 8], [2, 4, 6]];
const TTT_COL = ['#4CD964', '#FF5A5A'];
export const TTT_SEC = 2;
export function ticTacToe(root, back) {
  const s = S();
  let cv = null, starter = 0;
  const setup = () => { if (cv) { cv.stop(); cv = null; } root.replaceChildren(top(s.tttTitle, back), h('div', { class: 'body' }, setupPanel(s.tttDesc, play))); };
  const play = () => {
    const g = {};
    const names = [s.green, s.red];
    const round = (t) => { Object.assign(g, { cells: Array(9).fill(-1), placed: Array(9).fill(0), turn: starter, q: [[], []], turnAt: t + 1.2, win: -2, line: null, endAt: 0, timeUpAt: -9, tick: 9 }); starter = 1 - starter; };
    round(0);
    const geo = (W, H) => { const sz = Math.min(W - 40, H - 190, 420), x0 = (W - sz) / 2, y0 = (H - sz) / 2; return { sz, x0, y0, cell: sz / 3 }; };
    const finish = (t) => {
      for (const L of TTT_LINES) { const v = g.cells[L[0]]; if (v >= 0 && L.every((k) => g.cells[k] === v)) { g.win = v; g.line = L; g.endAt = t; vibe.strong(); return; } }
      if (g.cells.every((v) => v >= 0)) { g.win = -1; g.endAt = t; vibe.strong(); }
    };
    cv = stage(root, s.tttTitle, setup, {
      down(x, y, t, W, H) {
        if (g.win > -2) { if (t - g.endAt > 0.8) { round(t); vibe.tap(); } return; }
        if (t < g.turnAt) return;
        const { x0, y0, cell } = geo(W, H), cx = Math.floor((x - x0) / cell), cy = Math.floor((y - y0) / cell);
        if (cx < 0 || cy < 0 || cx > 2 || cy > 2) return;
        const k = cy * 3 + cx; if (g.cells[k] >= 0) return;
        // 한 사람당 3개까지: 4번째를 놓으면 내 가장 오래된 코인이 사라짐
        if (g.q[g.turn].length >= 3) g.cells[g.q[g.turn].shift()] = -1;
        g.q[g.turn].push(k); g.cells[k] = g.turn; g.placed[k] = t; vibe.tap(); finish(t);
        if (g.win === -2) { g.turn = 1 - g.turn; g.turnAt = t; g.tick = 9; }
      },
      draw(c, W, H, t) {
        const { sz, x0, y0, cell } = geo(W, H);
        // 시간 초과 → 차례 넘김
        if (g.win === -2 && t >= g.turnAt && t - g.turnAt > TTT_SEC) { g.timeUpAt = t; g.turn = 1 - g.turn; g.turnAt = t; g.tick = 9; vibe.strong(); }
        const left = g.win === -2 ? Math.max(0, TTT_SEC - Math.max(0, t - g.turnAt)) : 0;
        if (g.win === -2 && t >= g.turnAt) { const k = Math.ceil(left); if (k !== g.tick) { g.tick = k; if (k < TTT_SEC) vibe.tick(); } }
        const col = TTT_COL[g.turn];
        // 바탕 (차례 색으로 은은하게)
        if (g.win === -2) { c.fillStyle = col + '1A'; c.fillRect(0, 0, W, H); }
        // 판
        c.beginPath(); c.roundRect(x0 - 10, y0 - 10, sz + 20, sz + 20, 22); c.fillStyle = '#1E2433'; c.fill(); c.lineWidth = 3; c.strokeStyle = g.win === -2 ? col : '#2E3650'; c.stroke();
        c.strokeStyle = '#3A4462'; c.lineWidth = 6; c.lineCap = 'round';
        for (let i = 1; i < 3; i++) { c.beginPath(); c.moveTo(x0 + i * cell, y0 + 12); c.lineTo(x0 + i * cell, y0 + sz - 12); c.stroke(); c.beginPath(); c.moveTo(x0 + 12, y0 + i * cell); c.lineTo(x0 + sz - 12, y0 + i * cell); c.stroke(); }
        for (let k = 0; k < 9; k++) {
          const v = g.cells[k]; if (v < 0) continue;
          const cx = x0 + (k % 3 + 0.5) * cell, cy = y0 + (Math.floor(k / 3) + 0.5) * cell, pop = ease((t - g.placed[k]) / 0.18);
          // 다음에 사라질 코인 (지금 차례인 사람이 3개 다 놓았을 때) 은 깜빡임
          const goes = g.win === -2 && v === g.turn && g.q[v].length >= 3 && g.q[v][0] === k;
          const dim = g.line && !g.line.includes(k) ? 0.35 : goes ? 0.45 + 0.35 * Math.sin(t * 10) : 1;
          c.globalAlpha = dim; coin(c, cx, cy, cell * 0.36 * (0.5 + 0.5 * pop), 1, 1, v ? 'red' : 'green'); c.globalAlpha = 1;
        }
        if (g.line) {
          const p = (k) => [x0 + (k % 3 + 0.5) * cell, y0 + (Math.floor(k / 3) + 0.5) * cell], a = p(g.line[0]), b = p(g.line[2]), u = ease((t - g.endAt) / 0.3);
          c.beginPath(); c.moveTo(a[0], a[1]); c.lineTo(a[0] + (b[0] - a[0]) * u, a[1] + (b[1] - a[1]) * u); c.strokeStyle = '#fff'; c.lineWidth = 8; c.lineCap = 'round'; c.stroke();
        }
        // 시간 막대 (위·아래)
        if (g.win === -2) {
          const bw = Math.min(W - 60, 360), u = t < g.turnAt ? 1 : left / TTT_SEC;
          for (const yy of [y0 + sz + 28, y0 - 28]) {
            c.beginPath(); c.roundRect((W - bw) / 2, yy - 6, bw, 12, 6); c.fillStyle = 'rgba(255,255,255,.12)'; c.fill();
            c.beginPath(); c.roundRect((W - bw) / 2 + (yy < H / 2 ? bw * (1 - u) : 0), yy - 6, Math.max(6, bw * u), 12, 6); c.fillStyle = u < 0.35 ? RED : col; c.fill();
          }
          const msg = t - g.timeUpAt < 1 ? s.timeUp : t < g.turnAt ? s.colorTurn(names[g.turn]) : cat(s.colorTurn(names[g.turn]), `  ${left.toFixed(1)}`);
          // 아래 사람 = 초록, 위 사람 = 빨강. 각자 쪽에 "나: 초록" + 지금 상황
          const tu = t - g.timeUpAt < 1;
          eachSide(c, W, H, [0, 1].map((p) => cat(s.youAre(names[p]), '  ·  ', msg)), 19, [0, 1].map((p) => (tu ? RED : p === g.turn ? TTT_COL[p] : '#8A8FA0')));
          for (const p of [0, 1]) { c.save(); if (p) { c.translate(W, H); c.rotate(Math.PI); } coin(c, 30, H - 26 - (msg.includes(SEP) ? 11 : 0), 13, 1, 1, p ? 'red' : 'green'); c.restore(); }
        } else {
          const msg = g.win === -1 ? s.drawWord : cat(s.winsOf(names[g.win]), '  ·  ', names[1 - g.win], ' ', s.shotWord);
          eachSide(c, W, H, [0, 1].map((p) => cat(s.youAre(names[p]), '  ·  ', msg)), 19, [0, 1].map(() => (g.win === -1 ? GOLD : TTT_COL[g.win])));
          if (t - g.endAt > 0.8) { c.fillStyle = 'rgba(0,0,0,.55)'; c.beginPath(); c.roundRect(W / 2 - 110, H / 2 - 22, 220, 44, 22); c.fill(); fitLabel(c, s.playAgain, W / 2, H / 2, 200, 17, '#fff'); }
        }
      },
    });
  };
  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== ⚫⚪ 오목 =====================
export const GN = 15;
const DIRS = [[1, 0], [0, 1], [1, 1], [1, -1]];
/** 놓은 자리(x,y)로 같은 색이 몇 개 이어지는지 (가장 긴 줄) */
export function maxRun(b, x, y, v) {
  let best = 0;
  for (const [dx, dy] of DIRS) {
    let n = 1;
    for (const sg of [1, -1]) for (let k = 1; k < GN; k++) { const X = x + dx * k * sg, Y = y + dy * k * sg; if (X < 0 || Y < 0 || X >= GN || Y >= GN || b[Y * GN + X] !== v) break; n++; }
    best = Math.max(best, n);
  }
  return best;
}
/** 흑 33 금지: (x,y)에 흑을 놓으면 열린 3이 두 줄 이상 생기는지. b 는 아직 안 놓은 판 (0 빈칸, 1 흑, 2 백) */
export function isDoubleThree(b, x, y) {
  const bb = b.slice(); bb[y * GN + x] = 1;
  if (maxRun(bb, x, y, 1) >= 5) return false;
  const at = (X, Y) => (X < 0 || Y < 0 || X >= GN || Y >= GN ? 3 : bb[Y * GN + X]);
  let threes = 0;
  for (const [dx, dy] of DIRS) {
    let open3 = false;
    // 이 방향에서 빈칸 하나에 흑을 더 놓아 "열린 4 (.BBBB.)" 가 되면 → 지금은 열린 3
    for (let q = -4; q <= 4 && !open3; q++) {
      if (!q || at(x + dx * q, y + dy * q) !== 0) continue;
      for (let a = -5; a <= 0 && !open3; a++) {
        // 창: a..a+5 (6칸), 양 끝 빈칸, 가운데 4칸 흑. 0(놓은 곳)과 q 둘 다 가운데에
        const lo = a + 1, hi = a + 4;
        if (0 < lo || 0 > hi || q < lo || q > hi) continue;
        let ok = at(x + dx * a, y + dy * a) === 0 && at(x + dx * (a + 5), y + dy * (a + 5)) === 0;
        for (let k = lo; k <= hi && ok; k++) { const v = k === q ? 1 : at(x + dx * k, y + dy * k); if (v !== 1) ok = false; }
        // 6목이 되는 열린 4는 제외 (양옆 바깥이 흑이면)
        if (ok && (at(x + dx * (a - 1), y + dy * (a - 1)) === 1 || at(x + dx * (a + 6), y + dy * (a + 6)) === 1)) ok = false;
        if (ok) open3 = true;
      }
    }
    if (open3) threes++;
  }
  return threes >= 2;
}
export function gomoku(root, back) {
  const s = S();
  let cv = null;
  const setup = () => { if (cv) { cv.stop(); cv = null; } root.replaceChildren(top(s.gomokuTitle, back), h('div', { class: 'body' }, setupPanel(s.gomokuDesc, play))); };
  const play = () => {
    const g = {};
    const names = [s.black, s.white];
    const round = () => Object.assign(g, { b: Array(GN * GN).fill(0), turn: 1, last: -1, lastAt: 0, win: 0, line: null, endAt: 0, ban: null, banAt: -9, moves: 0 });
    round();
    const geo = (W, H) => { const sz = Math.min(W - 16, H - 130, 640), x0 = (W - sz) / 2, y0 = (H - sz) / 2, pad = sz / (GN + 0.2) * 0.6, step = (sz - pad * 2) / (GN - 1); return { sz, x0, y0, pad, step }; };
    const winLine = (x, y, v) => {
      for (const [dx, dy] of DIRS) {
        const cells = [[x, y]];
        for (const sg of [1, -1]) for (let k = 1; k < GN; k++) { const X = x + dx * k * sg, Y = y + dy * k * sg; if (X < 0 || Y < 0 || X >= GN || Y >= GN || g.b[Y * GN + X] !== v) break; cells.push([X, Y]); }
        if (cells.length >= 5) { cells.sort((p, q) => (p[0] - q[0]) || (p[1] - q[1])); return [cells[0], cells[cells.length - 1]]; }
      }
      return null;
    };
    cv = stage(root, s.gomokuTitle, setup, {
      down(px, py, t, W, H) {
        if (g.win) { if (t - g.endAt > 0.8) { round(); vibe.tap(); } return; }
        const { x0, y0, pad, step } = geo(W, H), x = Math.round((px - x0 - pad) / step), y = Math.round((py - y0 - pad) / step);
        if (x < 0 || y < 0 || x >= GN || y >= GN || g.b[y * GN + x]) return;
        if (Math.hypot(px - (x0 + pad + x * step), py - (y0 + pad + y * step)) > step * 0.7) return;
        if (g.turn === 1 && isDoubleThree(g.b, x, y)) { g.ban = [x, y]; g.banAt = t; vibe.strong(); return; }
        g.b[y * GN + x] = g.turn; g.last = y * GN + x; g.lastAt = t; g.moves++; vibe.tap();
        const L = winLine(x, y, g.turn);
        if (L) { g.win = g.turn; g.line = L; g.endAt = t; vibe.strong(); return; }
        if (g.moves >= GN * GN) { g.win = -1; g.endAt = t; return; }
        g.turn = 3 - g.turn;
      },
      draw(c, W, H, t) {
        const { sz, x0, y0, pad, step } = geo(W, H);
        // 나무 판
        const wood = c.createLinearGradient(x0, y0, x0 + sz, y0 + sz); wood.addColorStop(0, '#E8C27A'); wood.addColorStop(1, '#C99A4E');
        c.beginPath(); c.roundRect(x0, y0, sz, sz, 10); c.fillStyle = wood; c.fill();
        c.strokeStyle = 'rgba(60,35,10,.8)'; c.lineWidth = 1.2;
        for (let i = 0; i < GN; i++) {
          const a = x0 + pad + i * step, b2 = y0 + pad + i * step;
          c.beginPath(); c.moveTo(a, y0 + pad); c.lineTo(a, y0 + pad + (GN - 1) * step); c.stroke();
          c.beginPath(); c.moveTo(x0 + pad, b2); c.lineTo(x0 + pad + (GN - 1) * step, b2); c.stroke();
        }
        for (const [i, j] of [[3, 3], [11, 3], [7, 7], [3, 11], [11, 11]]) { c.beginPath(); c.arc(x0 + pad + i * step, y0 + pad + j * step, Math.max(2, step * 0.12), 0, 7); c.fillStyle = 'rgba(60,35,10,.9)'; c.fill(); }
        // 돌
        const r = step * 0.46;
        for (let k = 0; k < GN * GN; k++) {
          const v = g.b[k]; if (!v) continue;
          const cx = x0 + pad + (k % GN) * step, cy = y0 + pad + Math.floor(k / GN) * step;
          c.beginPath(); c.arc(cx + r * 0.12, cy + r * 0.16, r, 0, 7); c.fillStyle = 'rgba(0,0,0,.28)'; c.fill();
          const gr = c.createRadialGradient(cx - r * 0.35, cy - r * 0.35, r * 0.1, cx, cy, r);
          if (v === 1) { gr.addColorStop(0, '#666'); gr.addColorStop(1, '#0B0B0B'); } else { gr.addColorStop(0, '#fff'); gr.addColorStop(1, '#CFCFCF'); }
          c.beginPath(); c.arc(cx, cy, r, 0, 7); c.fillStyle = gr; c.fill();
          if (k === g.last) { c.beginPath(); c.arc(cx, cy, r * 0.28, 0, 7); c.fillStyle = RED; c.fill(); }
        }
        if (g.ban && t - g.banAt < 1.2) {
          const cx = x0 + pad + g.ban[0] * step, cy = y0 + pad + g.ban[1] * step, a = 1 - (t - g.banAt) / 1.2;
          c.globalAlpha = a; c.strokeStyle = RED; c.lineWidth = 3; c.beginPath(); c.arc(cx, cy, r, 0, 7); c.moveTo(cx - r * 0.7, cy - r * 0.7); c.lineTo(cx + r * 0.7, cy + r * 0.7); c.stroke(); c.globalAlpha = 1;
        }
        if (g.line) {
          const P = (q) => [x0 + pad + q[0] * step, y0 + pad + q[1] * step], a = P(g.line[0]), b2 = P(g.line[1]), u = ease((t - g.endAt) / 0.3);
          c.beginPath(); c.moveTo(a[0], a[1]); c.lineTo(a[0] + (b2[0] - a[0]) * u, a[1] + (b2[1] - a[1]) * u); c.strokeStyle = RED; c.lineWidth = 5; c.lineCap = 'round'; c.stroke();
        }
        // 안내 (위·아래)
        let msg, col;
        if (g.win === -1) { msg = s.drawWord; col = GOLD; }
        else if (g.win) { msg = cat(s.winsOf(names[g.win - 1]), '  ·  ', names[2 - g.win], ' ', s.shotWord); col = GOLD; }
        else if (t - g.banAt < 1.2) { msg = s.banned33; col = RED; }
        else { msg = s.colorTurn(names[g.turn - 1]); col = g.turn === 1 ? '#fff' : '#ddd'; }
        // 아래 사람 = 흑, 위 사람 = 백. 각자 쪽에 "나: 흑" + 지금 상황 (내 차례면 밝게)
        const pd = Math.max(22, y0 / 2);
        eachSide(c, W, H, [0, 1].map((p) => cat(s.youAre(names[p]), '  ·  ', msg)), 19, [0, 1].map((p) => (g.win || t - g.banAt < 1.2 ? col : p === g.turn - 1 ? '#fff' : '#8A8FA0')), pd);
        for (const p of [0, 1]) {
          c.save(); if (p) { c.translate(W, H); c.rotate(Math.PI); }
          const sy = H - pd - (msg.includes(SEP) ? 11 : 0);
          c.beginPath(); c.arc(28, sy, 11, 0, 7); c.fillStyle = p ? '#eee' : '#111'; c.fill(); c.lineWidth = 2; c.strokeStyle = !g.win && p === g.turn - 1 ? GOLD : '#888'; c.stroke(); c.restore();
        }
        if (!g.win) { /* 차례 표시는 위 글자로 */ } else if (t - g.endAt > 0.8) { c.fillStyle = 'rgba(0,0,0,.6)'; c.beginPath(); c.roundRect(W / 2 - 110, H / 2 - 22, 220, 44, 22); c.fill(); fitLabel(c, s.playAgain, W / 2, H / 2, 200, 17, '#fff'); }
      },
    });
  };
  setup();
  return () => { if (cv) cv.stop(); };
}
