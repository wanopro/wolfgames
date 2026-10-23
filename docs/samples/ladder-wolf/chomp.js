// 사다리 타는 귀여운 늑대 얼굴 (메롱 늑대와 같은 그림체). 입을 앙앙 벌렸다 닫았다.
// dir: 이동 방향(-1 왼쪽, 0 아래, 1 오른쪽), bite: 지정하면 그 값으로 입 벌림(0~1)
export function ladderWolf(ctx, x, y, r, t, color, dir = 0, bite = null) {
  const FUR = '#9AA5B4', DARK = '#6E7A8A', CREAM = '#F4ECE1', INK = '#2B2B33', OL = 'rgba(0,0,0,.3)';
  const ch = bite ?? Math.pow(0.5 + 0.5 * Math.sin(t * 20), 1.5);
  const lw = Math.max(1, r * 0.05);
  ctx.save(); ctx.translate(x, y); ctx.rotate(dir * 0.22 + Math.sin(t * 9) * 0.06);
  ctx.lineJoin = 'round'; ctx.lineCap = 'round';
  // 귀 (번갈아 쫑긋)
  for (const s of [-1, 1]) {
    const e = Math.sin(t * 9 + (s > 0 ? 0 : 3)) * 0.07;
    ctx.save(); ctx.translate(s * r * 0.55, -r * 0.6); ctx.rotate(s * (0.25 + e));
    ctx.beginPath(); ctx.moveTo(-r * 0.3, r * 0.15); ctx.lineTo(0, -r * 0.6); ctx.lineTo(r * 0.3, r * 0.15); ctx.closePath(); ctx.fillStyle = DARK; ctx.fill(); ctx.lineWidth = lw; ctx.strokeStyle = OL; ctx.stroke();
    ctx.beginPath(); ctx.moveTo(-r * 0.15, r * 0.08); ctx.lineTo(0, -r * 0.38); ctx.lineTo(r * 0.15, r * 0.08); ctx.closePath(); ctx.fillStyle = '#F6AFC0'; ctx.fill();
    ctx.restore();
  }
  // 얼굴 + 뾰족 볼털
  ctx.beginPath();
  ctx.moveTo(-r * 0.95, -r * 0.1); ctx.quadraticCurveTo(-r * 0.95, -r * 0.95, 0, -r * 0.95); ctx.quadraticCurveTo(r * 0.95, -r * 0.95, r * 0.95, -r * 0.1);
  ctx.lineTo(r * 1.08, r * 0.25); ctx.lineTo(r * 0.82, r * 0.3); ctx.lineTo(r * 0.92, r * 0.55); ctx.lineTo(r * 0.55, r * 0.6);
  ctx.quadraticCurveTo(0, r * 0.98, -r * 0.55, r * 0.6); ctx.lineTo(-r * 0.92, r * 0.55); ctx.lineTo(-r * 0.82, r * 0.3); ctx.lineTo(-r * 1.08, r * 0.25); ctx.closePath();
  ctx.fillStyle = FUR; ctx.fill(); ctx.lineWidth = lw; ctx.strokeStyle = OL; ctx.stroke();
  ctx.beginPath(); ctx.moveTo(-r * 0.16, -r * 0.93); ctx.lineTo(0, -r * 0.45); ctx.lineTo(r * 0.16, -r * 0.93); ctx.closePath(); ctx.fillStyle = CREAM; ctx.globalAlpha = 0.85; ctx.fill(); ctx.globalAlpha = 1;
  // 목도리 (플레이어 색)
  ctx.beginPath(); ctx.ellipse(0, r * 0.88, r * 0.72, r * 0.2, 0, 0, 7); ctx.fillStyle = color; ctx.fill(); ctx.lineWidth = lw; ctx.strokeStyle = OL; ctx.stroke();
  ctx.beginPath(); ctx.moveTo(r * 0.35, r * 0.95); ctx.lineTo(r * 0.62, r * 1.35); ctx.lineTo(r * 0.18, r * 1.05); ctx.closePath(); ctx.fillStyle = color; ctx.fill();
  // 주둥이
  ctx.beginPath(); ctx.ellipse(0, r * 0.4, r * 0.46, r * 0.34, 0, 0, 7); ctx.fillStyle = CREAM; ctx.fill();
  // 큰 반짝 눈 (가는 쪽을 봄)
  const lx = dir * r * 0.05, ey = -r * 0.14;
  for (const s of [-1, 1]) {
    ctx.beginPath(); ctx.arc(s * r * 0.4 + lx, ey, r * 0.19, 0, 7); ctx.fillStyle = INK; ctx.fill();
    ctx.beginPath(); ctx.arc(s * r * 0.4 + lx - r * 0.06, ey - r * 0.07, r * 0.075, 0, 7); ctx.fillStyle = '#fff'; ctx.fill();
    ctx.beginPath(); ctx.arc(s * r * 0.4 + lx + r * 0.06, ey + r * 0.06, r * 0.035, 0, 7); ctx.fill();
  }
  // 볼 홍조
  for (const s of [-1, 1]) { ctx.beginPath(); ctx.ellipse(s * r * 0.63, r * 0.2, r * 0.18, r * 0.1, 0, 0, 7); ctx.fillStyle = 'rgba(255,120,150,.55)'; ctx.fill(); }
  // 코
  ctx.beginPath(); ctx.ellipse(0, r * 0.2, r * 0.14, r * 0.095, 0, 0, 7); ctx.fillStyle = INK; ctx.fill();
  ctx.beginPath(); ctx.arc(-r * 0.04, r * 0.17, r * 0.03, 0, 7); ctx.fillStyle = 'rgba(255,255,255,.6)'; ctx.fill();
  // 입: 작고 동그랗게 앙! (작은 송곳니 2개)
  const mh = r * (0.04 + 0.28 * ch), mw = r * (0.22 + 0.06 * ch), my = r * 0.46;
  ctx.beginPath(); ctx.ellipse(0, my + mh * 0.5, mw, Math.max(r * 0.03, mh * 0.6), 0, 0, 7); ctx.fillStyle = '#8E2440'; ctx.fill();
  if (ch > 0.3) { ctx.beginPath(); ctx.ellipse(0, my + mh * 0.85, mw * 0.6, mh * 0.25, 0, 0, 7); ctx.fillStyle = '#FF7F98'; ctx.fill(); }
  ctx.fillStyle = '#fff';
  for (const s of [-1, 1]) { ctx.beginPath(); ctx.moveTo(s * mw * 0.75 - r * 0.05, my + mh * 0.5 - Math.max(r * 0.03, mh * 0.6) + 1); ctx.lineTo(s * mw * 0.75 + r * 0.05, my + mh * 0.5 - Math.max(r * 0.03, mh * 0.6) + 1); ctx.lineTo(s * mw * 0.75, my + mh * 0.5 - Math.max(r * 0.03, mh * 0.6) + r * 0.11); ctx.fill(); }
  ctx.restore();
}
