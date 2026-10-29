// 플링코 시안: 위에서 늑대 코인을 떨어뜨리면 핀 사이를 튕기며 내려가 아래 칸에 들어감
import { coin, chompWolf } from './art.js';
export const SLOTS = [['🍺', '1잔', '#FF7043'], ['🥃', '반 잔', '#29B6F6'], ['🔄', '다시', '#78909C'], ['👉', '오른쪽\n사람', '#AB47BC'], ['👈', '왼쪽\n사람', '#EC407A'], ['🍻', '다 같이', '#FFB300'], ['✅', '통과', '#43A047']];
const PC = ['#FF5252', '#448AFF', '#69F0AE', '#FFD740'];
export function board(W, H) {
  const top = 150, rows = 11, gapY = 36, n = SLOTS.length, sw = (W - 24) / n, pegs = [];
  for (let r = 0; r < rows; r++) { const odd = r % 2, cnt = odd ? n - 1 : n; for (let i = 0; i < cnt; i++) pegs.push([12 + sw * (i + (odd ? 1 : 0.5)), top + r * gapY]); }
  const slotTop = top + rows * gapY + 10, slotBot = H - 40;
  return { pegs, sw, slotTop, slotBot, n, top };
}
// 떨어뜨리기 시뮬레이션 (같은 seed 면 같은 길)
export function simulate(W, H, x0, seed) {
  const b = board(W, H); let s = seed; const rnd = () => (s = (s * 16807) % 2147483647) / 2147483647;
  let x = x0, y = 80, vx = (rnd() - 0.5) * 30, vy = 0; const R = 12, PR = 5, path = [], dt = 1 / 240;
  for (let k = 0; k < 240 * 12; k++) {
    vy += 1500 * dt; x += vx * dt; y += vy * dt;
    for (const [px, py] of b.pegs) { const dx = x - px, dy = y - py, d = Math.hypot(dx, dy); if (d < R + PR && d > 0) { const nx = dx / d, ny = dy / d; x = px + nx * (R + PR); y = py + ny * (R + PR); const vn = vx * nx + vy * ny; if (vn < 0) { vx -= 1.55 * vn * nx; vy -= 1.55 * vn * ny; vx += (rnd() - 0.5) * 40; } } }
    if (x < 12 + R) { x = 12 + R; vx = Math.abs(vx) * 0.5; } if (x > W - 12 - R) { x = W - 12 - R; vx = -Math.abs(vx) * 0.5; }
    if (y > b.slotTop) { // 칸 칸막이
      const i = Math.floor((x - 12) / b.sw), l = 12 + i * b.sw, r = l + b.sw;
      if (x - R < l + 2) { x = l + 2 + R; vx = Math.abs(vx) * 0.4; } if (x + R > r - 2) { x = r - 2 - R; vx = -Math.abs(vx) * 0.4; }
    }
    if (y > b.slotBot - R) { y = b.slotBot - R; vy = -Math.abs(vy) * 0.25; vx *= 0.6; if (Math.abs(vy) < 80) { vy = 0; vx = 0; } }
    if (k % 4 === 0) path.push([x, y]);
    if (y >= b.slotBot - R - 0.5 && vy === 0) { path.push([x, y]); break; }
  }
  return { path, slot: Math.max(0, Math.min(SLOTS.length - 1, Math.floor((x - 12) / b.sw))) };
}
const txt = (c, s, x, y, size, color, w = 900) => { c.font = `${w} ${size}px -apple-system, "Noto Sans KR", sans-serif`; c.textAlign = 'center'; c.textBaseline = 'middle'; c.fillStyle = color; c.fillText(s, x, y); };
const spinMsg = (g, t) => g.drops.some((d, i) => i > 0 && t < d.at && t > d.at - 0.6);
export class Plinko {
  constructor(drops) { this.drops = drops; } // [{at, x, seed, p}]
  draw(c, W, H, t) {
    const b = board(W, H);
    c.fillStyle = '#12121A'; c.fillRect(0, 0, W, H);
    c.fillStyle = '#12121A'; c.fillRect(0, 0, W, 44); txt(c, '←  플링코', 60, 23, 18, '#fff', 700);
    // 판
    const bg = c.createLinearGradient(0, 60, 0, H); bg.addColorStop(0, '#1E2433'); bg.addColorStop(1, '#141824');
    c.beginPath(); c.roundRect(6, 60, W - 12, H - 70, 18); c.fillStyle = bg; c.fill(); c.lineWidth = 3; c.strokeStyle = '#2E3650'; c.stroke();
    // 지금 상태 찾기
    let cur = null, done = [];
    for (const d of this.drops) { if (t >= d.at) { const u = Math.floor((t - d.at) * 60); if (u >= d.sim.path.length + 95) done.push(d); else cur = { d, u }; } }
    txt(c, spinMsg(this, t) ? '🔀 칸 섞는 중…' : '위쪽 아무 데나 터치해서 떨어뜨리기', W / 2, 88, 15, '#FFD740', 800);
    const turn = cur ? -1 : 0;
    // 떨어뜨리는 곳 표시
    if (turn >= 0 && !cur) { c.setLineDash([6, 6]); c.strokeStyle = 'rgba(255,255,255,.25)'; c.beginPath(); c.moveTo(18, 112); c.lineTo(W - 18, 112); c.stroke(); c.setLineDash([]); }
    // 핀
    for (const [px, py] of b.pegs) { c.beginPath(); c.arc(px, py, 5, 0, 7); c.fillStyle = '#CFD8DC'; c.fill(); c.beginPath(); c.arc(px - 1.5, py - 1.5, 1.8, 0, 7); c.fillStyle = '#fff'; c.fill(); }
    // 칸
    const hitSlot = cur && cur.u >= cur.d.sim.path.length ? cur.d.sim.slot : -1;
    const active = cur ? cur.d : this.drops.find((d) => t < d.at) || this.drops[this.drops.length - 1];
    const order = active.order, prev = this.drops[this.drops.indexOf(active) - 1];
    // 새 판 시작 0.6초 전부터 칸이 빙글 섞임
    const sh = !cur && prev ? Math.max(0, 1 - (active.at - t) / 0.6) : 1, spin = sh < 1 && sh > 0;
    order.forEach((si, i) => { const [ic, lab, col] = SLOTS[spin && sh < 0.5 ? prev.order[i] : si];
      const x = 12 + i * b.sw, glow = i === hitSlot && Math.floor(t * 8) % 2 === 0, sq = spin ? Math.abs(Math.cos(sh * Math.PI)) : 1;
      c.save(); c.translate(x + b.sw / 2, 0); c.scale(Math.max(0.05, sq), 1); c.translate(-(x + b.sw / 2), 0);
      c.beginPath(); c.roundRect(x + 2, b.slotTop + 20, b.sw - 4, b.slotBot - b.slotTop - 18, 8); c.fillStyle = glow ? col : col + '40'; c.fill();
      txt(c, ic, x + b.sw / 2, b.slotBot - 62, 22, '#fff'); lab.split('\n').forEach((ln, j, a) => txt(c, ln, x + b.sw / 2, b.slotBot - 34 + (j - (a.length - 1) / 2) * 15, 12.5, '#fff', 800)); c.restore();
      if (i) { c.beginPath(); c.moveTo(x, b.slotTop); c.lineTo(x, b.slotBot + 4); c.strokeStyle = '#CFD8DC'; c.lineWidth = 4; c.stroke(); }
    });
    // 이미 끝난 코인은 칸 안에
    if (cur) {
      const p = cur.d.sim.path[Math.min(cur.u, cur.d.sim.path.length - 1)];
      coin(c, p[0], p[1], 12, 1, 1);
      if (cur.u < 8) { c.beginPath(); c.arc(cur.d.x, 112, 14 + cur.u * 3, 0, 7); c.strokeStyle = `rgba(255,255,255,${0.7 - cur.u * 0.08})`; c.lineWidth = 3; c.stroke(); }
      if (hitSlot >= 0) {
        const [ic, lab, col] = SLOTS[cur.d.order[hitSlot]], k = cur.u - cur.d.sim.path.length, pop = Math.min(1, k / 8);
        c.save(); c.translate(W / 2, H * 0.42); c.scale(0.6 + 0.4 * pop, 0.6 + 0.4 * pop);
        c.beginPath(); c.roundRect(-140, -46, 280, 92, 22); c.fillStyle = 'rgba(0,0,0,.75)'; c.fill(); c.lineWidth = 4; c.strokeStyle = col; c.stroke();
        txt(c, `${ic} ${lab.replace('\n', ' ')}!`, 0, 2, 34, col); c.restore();
      }
    }
  }
}
