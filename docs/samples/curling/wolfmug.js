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
