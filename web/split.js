// 원형 화면 분할: 화면 가운데를 중심으로 n명이 같은 넓이로 둘러앉은 모양.
// P1 = 아래 가운데, 시계방향(아래 → 왼쪽 → 위 → 오른쪽)으로 P2, P3...
export function sectors(W, H, n) {
  const cx = W / 2, cy = H / 2;
  const path = [[W / 2, H], [0, H], [0, 0], [W, 0], [W, H], [W / 2, H]];
  const seg = []; let tot = 0;
  for (let i = 0; i < path.length - 1; i++) { const [x0, y0] = path[i], [x1, y1] = path[i + 1]; const a = Math.abs((x0 - cx) * (y1 - cy) - (x1 - cx) * (y0 - cy)) / 2; seg.push({ x0, y0, x1, y1, a, from: tot }); tot += a; }
  const at = (a) => { a = ((a % tot) + tot) % tot; for (const s of seg) if (a <= s.from + s.a + 1e-9) { const u = s.a ? (a - s.from) / s.a : 0; return [s.x0 + (s.x1 - s.x0) * u, s.y0 + (s.y1 - s.y0) * u]; } return path[0]; };
  const out = [], verts = [];
  for (let lap = -1; lap <= 1; lap++) for (const sg of seg) verts.push([sg.from + sg.a + lap * tot, [sg.x1, sg.y1]]);
  for (let k = 0; k < n; k++) {
    const a0 = k * tot / n - tot / (2 * n), a1 = k * tot / n + tot / (2 * n);
    const corners = verts.filter(([v]) => v > a0 + 1e-6 && v < a1 - 1e-6).sort((p, q) => p[0] - q[0]).map(([, p]) => p);
    const pts = [[cx, cy], at(a0), ...corners, at(a1)];
    const [ex, ey] = at(k * tot / n);
    // 무게중심을 기준점으로, 그 점에서 경계까지 가장 가까운 거리(쓸 수 있는 반지름)로 글자 크기 결정
    let A = 0, gx = 0, gy = 0;
    for (let q = 0; q < pts.length; q++) { const [x0, y0] = pts[q], [x1, y1] = pts[(q + 1) % pts.length], f = x0 * y1 - x1 * y0; A += f; gx += (x0 + x1) * f; gy += (y0 + y1) * f; }
    gx /= 3 * A; gy /= 3 * A;
    let rin = 1e9;
    for (let q = 0; q < pts.length; q++) { const [x0, y0] = pts[q], [x1, y1] = pts[(q + 1) % pts.length], dx = x1 - x0, dy = y1 - y0, L2 = dx * dx + dy * dy || 1; const u = Math.max(0, Math.min(1, ((gx - x0) * dx + (gy - y0) * dy) / L2)); rin = Math.min(rin, Math.hypot(gx - x0 - u * dx, gy - y0 - u * dy)); }
    const th = Math.atan2(gy - cy, gx - cx);
    out.push({ poly: pts, edge: [ex, ey], rot: th - Math.PI / 2, anchor: [gx, gy], dist: Math.hypot(ex - cx, ey - cy), rin });
  }
  return out;
}
export function inPoly([x, y], poly) { let c = false; for (let i = 0, j = poly.length - 1; i < poly.length; j = i++) { const [xi, yi] = poly[i], [xj, yj] = poly[j]; if ((yi > y) !== (yj > y) && x < (xj - xi) * (y - yi) / (yj - yi) + xi) c = !c; } return c; }
export function polyPath(ctx, poly) { ctx.beginPath(); poly.forEach(([x, y], i) => (i ? ctx.lineTo(x, y) : ctx.moveTo(x, y))); ctx.closePath(); }
/** 영역 하나 그리기: 배경 채우고, 그 사람 방향으로 돌린 좌표계에서 content(ctx, scale) 호출 */
export function region(ctx, sec, fill, content, glow) {
  ctx.save(); polyPath(ctx, sec.poly); ctx.clip();
  ctx.fillStyle = fill; ctx.fill();
  if (glow) { ctx.lineWidth = 14; ctx.strokeStyle = glow; polyPath(ctx, sec.poly); ctx.stroke(); }
  ctx.translate(...sec.anchor); ctx.rotate(sec.rot);
  content(ctx, Math.max(0.5, Math.min(1.05, sec.rin / 118)));
  ctx.restore();
}
export function lines(ctx, secs, W, H) {
  ctx.save(); ctx.strokeStyle = 'rgba(0,0,0,.55)'; ctx.lineWidth = 4;
  for (const s of secs) { ctx.beginPath(); ctx.moveTo(W / 2, H / 2); ctx.lineTo(...s.poly[1]); ctx.stroke(); }
  ctx.restore();
}
export function text(ctx, str, x, y, size, color, weight = 800) { ctx.font = `${weight} ${size}px -apple-system, "Noto Sans KR", sans-serif`; ctx.textAlign = 'center'; ctx.textBaseline = 'middle'; ctx.fillStyle = color; ctx.fillText(str, x, y); }
export function badge(ctx, str, x, y, s, bg, fg) { ctx.font = `900 ${18 * s}px sans-serif`; const w = ctx.measureText(str).width + 24 * s, h = 34 * s; ctx.beginPath(); ctx.roundRect(x - w / 2, y - h / 2, w, h, h / 2); ctx.fillStyle = bg; ctx.fill(); text(ctx, str, x, y + 1, 18 * s, fg, 900); }
