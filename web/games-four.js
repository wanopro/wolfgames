// 9. 4목 — 7×6 판, 2인, 알이 떨어지는 애니메이션
import { C, S, h, btn, top, liveCanvas, vibe, animate } from './core.js';

const COLS = 7, ROWS = 6;
const RED = ['#E53935', '#9E1C1C'], YEL = ['#FDD835', '#C79A00'];

function findWin(b, r, c) {
  const p = b[r * COLS + c];
  if (!p) return null;
  for (const [dr, dc] of [[0, 1], [1, 0], [1, 1], [1, -1]]) {
    const line = [r * COLS + c];
    for (const sg of [1, -1]) {
      let rr = r + dr * sg, cc = c + dc * sg;
      while (rr >= 0 && rr < ROWS && cc >= 0 && cc < COLS && b[rr * COLS + cc] === p) { line.push(rr * COLS + cc); rr += dr * sg; cc += dc * sg; }
    }
    if (line.length >= 4) return line;
  }
  return null;
}
// 중력처럼 빨라지다가 바닥에서 살짝 튕김
const dropCurve = (u) => (u < 0.72 ? (u / 0.72) ** 2 : 1 - 0.14 * Math.sin(Math.PI * ((u - 0.72) / 0.28)));

function disc(ctx, x, y, r, p) {
  const [main, dark] = p === 1 ? RED : YEL;
  ctx.beginPath(); ctx.arc(x, y, r, 0, 7); ctx.fillStyle = dark; ctx.fill();
  ctx.beginPath(); ctx.arc(x - r * 0.04, y - r * 0.04, r * 0.86, 0, 7); ctx.fillStyle = main; ctx.fill();
  ctx.beginPath(); ctx.arc(x, y, r * 0.6, 0, 7); ctx.strokeStyle = dark + '59'; ctx.lineWidth = r * 0.08; ctx.stroke();
  ctx.beginPath(); ctx.ellipse(x - r * 0.22, y - r * 0.46, r * 0.27, r * 0.16, 0, 0, 7); ctx.fillStyle = 'rgba(255,255,255,.35)'; ctx.fill();
}

export function four(root, back) {
  const s = S();
  const board = Array(ROWS * COLS).fill(0);
  let turn = 1, starter = 1, winLine = null, draw = false, drop = null, prog = 0, hover = 3, pulse = 0;
  root.append(top(s.menuFour, back));
  const wrap = h('div', { class: 'body pad center', style: 'gap:10px' });
  root.append(wrap);
  const title = h('div', { class: 'h-title', style: 'font-size:28px' });
  const holder = h('div', { class: 'cv-wrap', style: `aspect-ratio:${COLS}/${ROWS + 1.15};max-height:60vh` });
  const action = h('div', { style: 'width:100%' });
  wrap.append(title, h('div', { class: 'h-sub', style: 'font-size:14px' }, s.fourHint), h('div', { class: 'grow' }), holder, h('div', { class: 'grow' }), action);

  const name = (p) => (p === 1 ? s.fourRed : s.fourYellow);
  const over = () => winLine !== null || draw;
  const render = () => {
    if (winLine) { title.textContent = s.fourWin(name(board[winLine[0]])); title.style.color = board[winLine[0]] === 1 ? RED[0] : YEL[0]; }
    else if (draw) { title.textContent = s.fourDraw; title.style.color = C.accent; }
    else { title.textContent = s.fourTurn(name(turn)); title.style.color = turn === 1 ? RED[0] : YEL[0]; }
    action.replaceChildren(over() ? btn(s.playAgain, reset) : btn(s.fourRestart, reset, 'btn alt', { disabled: !!drop }));
  };
  const reset = () => { board.fill(0); winLine = null; draw = false; starter = 3 - starter; turn = starter; render(); };

  const play = async (col) => {
    if (over() || drop) return;
    hover = col;
    let row = -1;
    for (let r = ROWS - 1; r >= 0; r--) if (!board[r * COLS + col]) { row = r; break; }
    if (row < 0) return;
    drop = { col, row, p: turn }; vibe.tap(); render();
    await animate(320 + row * 55, (u) => { prog = u; }, 'linear');
    board[row * COLS + col] = drop.p; drop = null; vibe.tap();
    const w = findWin(board, row, col);
    if (w) { winLine = w; vibe.strong(); render(); animate(1600, (u) => { pulse = u; }, 'linear'); }
    else if (board.every(Boolean)) { draw = true; vibe.strong(); render(); }
    else { turn = 3 - turn; render(); }
  };

  const geo = (w, hh) => {
    const cell = Math.min(w / COLS, hh / (ROWS + 1.15));
    return { cell, left: (w - cell * COLS) / 2, top: cell * 1.15, r: cell * 0.4 };
  };
  const cv = liveCanvas(holder, (ctx, w, hh) => {
    const { cell, left, top: tp, r } = geo(w, hh);
    const cx = (c) => left + (c + 0.5) * cell, cy = (rw) => tp + (rw + 0.5) * cell;
    if (!over() && !drop) disc(ctx, cx(hover), cell * 0.55, r, turn);
    ctx.fillStyle = '#0B0F1E'; ctx.fillRect(left, tp, cell * COLS, cell * ROWS);
    for (let rw = 0; rw < ROWS; rw++) for (let c = 0; c < COLS; c++) if (board[rw * COLS + c]) disc(ctx, cx(c), cy(rw), r, board[rw * COLS + c]);
    if (drop) disc(ctx, cx(drop.col), cell * 0.55 + (cy(drop.row) - cell * 0.55) * dropCurve(prog), r, drop.p);
    // 구멍 뚫린 파란 판
    const g = ctx.createLinearGradient(0, tp, 0, tp + cell * ROWS);
    g.addColorStop(0, '#1F5FD1'); g.addColorStop(1, '#15449A');
    ctx.beginPath();
    ctx.roundRect(left - cell * 0.12, tp - cell * 0.1, cell * COLS + cell * 0.24, cell * ROWS + cell * 0.22, cell * 0.3);
    for (let rw = 0; rw < ROWS; rw++) for (let c = 0; c < COLS; c++) { ctx.moveTo(cx(c) + r, cy(rw)); ctx.arc(cx(c), cy(rw), r, 0, Math.PI * 2); }
    ctx.fillStyle = g; ctx.fill('evenodd');
    ctx.strokeStyle = 'rgba(255,255,255,.4)'; ctx.lineWidth = cell * 0.045;
    for (let rw = 0; rw < ROWS; rw++) for (let c = 0; c < COLS; c++) { ctx.beginPath(); ctx.arc(cx(c), cy(rw), r, 0, 7); ctx.stroke(); }
    if (winLine) {
      const glow = 0.5 + 0.5 * Math.sin(pulse * Math.PI * 6);
      ctx.strokeStyle = `rgba(255,255,255,${0.5 + 0.5 * glow})`; ctx.lineWidth = cell * 0.09;
      for (const i of winLine) { ctx.beginPath(); ctx.arc(cx(i % COLS), cy(Math.floor(i / COLS)), r * 1.08, 0, 7); ctx.stroke(); }
    }
  });
  cv.canvas.addEventListener('click', (e) => {
    const rc = cv.canvas.getBoundingClientRect();
    const { cell, left } = geo(rc.width, rc.height);
    const col = Math.floor((e.clientX - rc.left - left) / cell);
    if (col >= 0 && col < COLS) play(col);
  });
  render();
  return () => cv.stop();
}
