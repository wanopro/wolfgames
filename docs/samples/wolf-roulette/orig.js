const INK = '#2B2B33';
const R = (d) => (d * Math.PI) / 180;
const C={win:'#FF3D5A',players:['#FF5252','#448AFF','#69F0AE','#FFD740','#E040FB','#18FFFF','#FF6E40','#B2FF59','#FF80AB','#8C9EFF']};
export function pirateHead(ctx, cx, cy, r, sur) {
  for (const sx of [-1, 1]) {
    ctx.beginPath(); ctx.moveTo(cx + sx * r * 0.25, cy - r * 0.7); ctx.lineTo(cx + sx * r * 0.85, cy - r * 1.35); ctx.lineTo(cx + sx * r * 0.95, cy - r * 0.35); ctx.closePath(); ctx.fillStyle = '#6E7A8A'; ctx.fill();
    ctx.beginPath(); ctx.moveTo(cx + sx * r * 0.45, cy - r * 0.68); ctx.lineTo(cx + sx * r * 0.8, cy - r * 1.1); ctx.lineTo(cx + sx * r * 0.85, cy - r * 0.5); ctx.closePath(); ctx.fillStyle = '#F6AFC0'; ctx.fill();
  }
  ctx.beginPath(); ctx.arc(cx, cy, r, 0, 7); ctx.fillStyle = '#9AA5B4'; ctx.fill();
  ctx.beginPath(); ctx.moveTo(cx - r * 0.97, cy - r * 0.25); ctx.quadraticCurveTo(cx, cy - r * 1.25, cx + r * 0.97, cy - r * 0.25); ctx.quadraticCurveTo(cx, cy - r * 0.75, cx - r * 0.97, cy - r * 0.25); ctx.fillStyle = '#E53935'; ctx.fill();
  for (const k of [-0.45, 0, 0.45]) { ctx.beginPath(); ctx.arc(cx + k * r, cy - r * 0.68 + Math.abs(k) * r * 0.25, r * 0.06, 0, 7); ctx.fillStyle = '#fff'; ctx.fill(); }
  ctx.beginPath(); ctx.arc(cx + r * 0.95, cy - r * 0.3, r * 0.13, 0, 7); ctx.fillStyle = '#E53935'; ctx.fill();
  ctx.beginPath(); ctx.moveTo(cx + r * 0.95, cy - r * 0.3); ctx.lineTo(cx + r * 1.25, cy - r * 0.05); ctx.strokeStyle = '#E53935'; ctx.lineWidth = r * 0.12; ctx.lineCap = 'round'; ctx.stroke();
  ctx.beginPath(); ctx.ellipse(cx, cy + r * 0.44, r * 0.48, r * 0.36, 0, 0, 7); ctx.fillStyle = '#F4ECE1'; ctx.fill();
  const ey = cy - r * 0.08;
  for (const sx of [-1, 1]) {
    const ex = cx + sx * r * 0.42;
    if (sur) {
      const d = r * 0.13; ctx.beginPath(); ctx.moveTo(ex - d * sx, ey - d); ctx.lineTo(ex + d * sx, ey); ctx.lineTo(ex - d * sx, ey + d); ctx.strokeStyle = INK; ctx.lineWidth = r * 0.07; ctx.stroke();
    } else {
      ctx.beginPath(); ctx.arc(ex, ey, r * 0.15, 0, 7); ctx.fillStyle = INK; ctx.fill();
      ctx.beginPath(); ctx.arc(ex - r * 0.04, ey - r * 0.05, r * 0.06, 0, 7); ctx.fillStyle = '#fff'; ctx.fill();
    }
    ctx.beginPath(); ctx.ellipse(ex + sx * r * 0.08, ey + r * 0.3, r * 0.2, r * 0.1, 0, 0, 7); ctx.fillStyle = 'rgba(255,143,163,.4)'; ctx.fill();
  }
  ctx.beginPath(); ctx.ellipse(cx, cy + r * 0.245, r * 0.13, r * 0.085, 0, 0, 7); ctx.fillStyle = INK; ctx.fill();
  if (sur) { ctx.beginPath(); ctx.arc(cx, cy + r * 0.54, r * 0.12, 0, 7); ctx.fillStyle = '#8E2440'; ctx.fill(); }
  else { ctx.beginPath(); ctx.moveTo(cx - r * 0.13, cy + r * 0.45); ctx.quadraticCurveTo(cx, cy + r * 0.55, cx + r * 0.13, cy + r * 0.45); ctx.strokeStyle = INK; ctx.lineWidth = r * 0.05; ctx.stroke(); }
}

export function barrel(ctx, w, hh, dx) {
  const l = w * 0.14 + dx, rr = w * 0.86 + dx, t = hh * 0.36, b = hh * 0.98, bu = w * 0.05;
  ctx.beginPath(); ctx.moveTo(l, t); ctx.lineTo(rr, t); ctx.quadraticCurveTo(rr + bu * 2, (t + b) / 2, rr, b); ctx.lineTo(l, b); ctx.quadraticCurveTo(l - bu * 2, (t + b) / 2, l, t); ctx.closePath(); ctx.fillStyle = '#9A6332'; ctx.fill();
  ctx.strokeStyle = '#6E4220'; ctx.lineWidth = 2;
  for (let k = 1; k <= 5; k++) { const x = l + (rr - l) * k / 6, bend = (x - (l + rr) / 2) / ((rr - l) / 2) * bu; ctx.beginPath(); ctx.moveTo(x, t); ctx.quadraticCurveTo(x + bend * 2, (t + b) / 2, x, b); ctx.stroke(); }
  const mid = (t + b) / 2 - hh * 0.005;
  for (const y of [t + hh * 0.02, b - hh * 0.03, mid]) {
    const e = y === mid ? bu : bu * 0.35;
    ctx.beginPath(); ctx.roundRect(l - e, y - hh * 0.018, rr - l + e * 2, hh * 0.036, hh * 0.018); ctx.fillStyle = '#5D6670'; ctx.fill();
    ctx.beginPath(); ctx.moveTo(l - e + 6, y - hh * 0.008); ctx.lineTo(rr + e - 6, y - hh * 0.008); ctx.strokeStyle = '#8A95A1'; ctx.lineWidth = 1; ctx.stroke();
  }
  ctx.beginPath(); ctx.ellipse((l + rr) / 2, t, (rr - l + 8) / 2, hh * 0.045, 0, 0, 7); ctx.fillStyle = '#6E4220'; ctx.fill();
  ctx.beginPath(); ctx.ellipse((l + rr) / 2, t, (rr - l - w * 0.08) / 2, hh * 0.03, 0, 0, 7); ctx.fillStyle = '#2A170A'; ctx.fill();
}

export function sword(ctx, x, y, L, grip, trap) {
  const gx = x - L * 0.35, gy = y - L * 0.55, ex = x - L * 0.75, ey = y - L * 1.15, gc = trap ? C.win : '#FFC107';
  const line = (a, b, c, d, col, wd) => { ctx.beginPath(); ctx.moveTo(a, b); ctx.lineTo(c, d); ctx.strokeStyle = col; ctx.lineWidth = wd; ctx.lineCap = 'round'; ctx.stroke(); };
  line(x, y, gx, gy, '#CFD8DC', L * 0.12);
  line(gx - L * 0.22, gy + L * 0.13, gx + L * 0.22, gy - L * 0.13, gc, L * 0.12);
  line(gx, gy, ex, ey, grip, L * 0.16);
  ctx.beginPath(); ctx.arc(ex, ey, L * 0.11, 0, 7); ctx.fillStyle = gc; ctx.fill();
}

