// 사다리 타는 늑대 머리: 앞모습, 입을 앙앙 벌렸다 닫았다. dir: 이동 방향(-1 왼쪽, 0 아래, 1 오른쪽) → 살짝 기울임
export function ladderWolf(ctx, x, y, r, t, color, dir = 0, bite = null) {
  const FUR = '#9AA5B4', DARK = '#6E7A8A', CREAM = '#F4ECE1', INK = '#2B2B33';
  const chomp = bite ?? (0.5 + 0.5 * Math.sin(t * 22)); // 0 닫힘 ~ 1 벌림
  ctx.save(); ctx.translate(x, y); ctx.rotate(dir * 0.25 + Math.sin(t * 11) * 0.05);
  // 플레이어 색 목도리
  ctx.beginPath(); ctx.ellipse(0, r * 0.82, r * 0.78, r * 0.28, 0, 0, 7); ctx.fillStyle = color; ctx.fill();
  ctx.beginPath(); ctx.moveTo(r * 0.45, r * 0.9); ctx.lineTo(r * 0.75, r * 1.35); ctx.lineTo(r * 0.3, r * 1.05); ctx.fill();
  // 귀
  for (const s of [-1, 1]) { ctx.beginPath(); ctx.moveTo(s * r * 0.25, -r * 0.7); ctx.lineTo(s * r * 0.82, -r * 1.3); ctx.lineTo(s * r * 0.95, -r * 0.35); ctx.closePath(); ctx.fillStyle = DARK; ctx.fill();
    ctx.beginPath(); ctx.moveTo(s * r * 0.43, -r * 0.66); ctx.lineTo(s * r * 0.78, -r * 1.05); ctx.lineTo(s * r * 0.83, -r * 0.5); ctx.closePath(); ctx.fillStyle = '#F6AFC0'; ctx.fill(); }
  // 얼굴
  ctx.beginPath(); ctx.arc(0, 0, r, 0, 7); ctx.fillStyle = FUR; ctx.fill(); ctx.lineWidth = Math.max(1, r * 0.08); ctx.strokeStyle = 'rgba(0,0,0,.3)'; ctx.stroke();
  ctx.beginPath(); ctx.moveTo(-r * 0.16, -r * 0.98); ctx.lineTo(0, -r * 0.5); ctx.lineTo(r * 0.16, -r * 0.98); ctx.closePath(); ctx.fillStyle = CREAM; ctx.fill();
  // 눈 (가는 방향을 봄)
  const look = dir * r * 0.06;
  for (const s of [-1, 1]) { ctx.beginPath(); ctx.arc(s * r * 0.4 + look, -r * 0.18, r * 0.15, 0, 7); ctx.fillStyle = INK; ctx.fill(); ctx.beginPath(); ctx.arc(s * r * 0.4 + look - r * 0.04, -r * 0.24, r * 0.055, 0, 7); ctx.fillStyle = '#fff'; ctx.fill(); }
  // 입: 위아래로 앙앙
  const o = r * 0.42 * chomp;
  ctx.beginPath(); ctx.ellipse(0, r * 0.42, r * 0.52, r * 0.06 + o * 0.55, 0, 0, 7); ctx.fillStyle = '#7A1E2E'; ctx.fill();
  if (chomp > 0.25) { ctx.beginPath(); ctx.ellipse(0, r * 0.42 + o * 0.35, r * 0.25, r * 0.08 + o * 0.15, 0, 0, Math.PI); ctx.fillStyle = '#E86A86'; ctx.fill(); }
  ctx.fillStyle = '#fff';
  for (const k of [-1, 1]) { // 송곳니 위·아래
    ctx.beginPath(); ctx.moveTo(k * r * 0.3 - r * 0.07, r * 0.42 - r * 0.04 - o * 0.5); ctx.lineTo(k * r * 0.3 + r * 0.07, r * 0.42 - r * 0.04 - o * 0.5); ctx.lineTo(k * r * 0.3, r * 0.42 + r * 0.1 - o * 0.5); ctx.fill();
    ctx.beginPath(); ctx.moveTo(k * r * 0.22 - r * 0.06, r * 0.42 + r * 0.04 + o * 0.5); ctx.lineTo(k * r * 0.22 + r * 0.06, r * 0.42 + r * 0.04 + o * 0.5); ctx.lineTo(k * r * 0.22, r * 0.42 - r * 0.08 + o * 0.5); ctx.fill();
  }
  // 주둥이 위 코
  ctx.beginPath(); ctx.ellipse(0, r * 0.15 - o * 0.25, r * 0.15, r * 0.1, 0, 0, 7); ctx.fillStyle = INK; ctx.fill();
  ctx.restore();
}
