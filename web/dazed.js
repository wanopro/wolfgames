// 숫자 폭탄: 폭탄이 터진 뒤 그을린 늑대가 헤롱헤롱 (시안)
const FUR = '#9AA3AF', DARK = '#6E7784', LIGHT = '#F1EEE8', INK = '#2B2B33', PINK = '#F6AFC0';
export function dazedWolf(c, x, y, r, t) {
  const wob = Math.sin(t * 3.2) * 0.2, bob = Math.sin(t * 6.4) * r * 0.03;
  c.save(); c.translate(x, y + bob); c.rotate(wob);
  // 연기 (머리 위로 모락모락)
  for (let k = 0; k < 4; k++) {
    const u = ((t * 0.5 + k / 4) % 1), sx = Math.sin(u * 6 + k) * r * 0.18 + (k - 1.5) * r * 0.22;
    c.globalAlpha = 0.5 * (1 - u); c.beginPath(); c.arc(sx, -r * 1.05 - u * r * 0.9, r * (0.12 + u * 0.2), 0, 7); c.fillStyle = '#8A8F99'; c.fill();
  }
  c.globalAlpha = 1;
  // 그을린 뻗친 머리털 (펑 터진 아프로)
  for (let k = 0; k < 9; k++) {
    const a = -Math.PI * 0.95 + k * Math.PI * 0.9 / 8, l = r * (0.95 + 0.12 * Math.sin(k * 2.3 + t * 8));
    c.beginPath(); c.moveTo(Math.cos(a - 0.12) * r * 0.6, Math.sin(a - 0.12) * r * 0.6 - r * 0.05); c.lineTo(Math.cos(a) * l, Math.sin(a) * l - r * 0.05); c.lineTo(Math.cos(a + 0.12) * r * 0.6, Math.sin(a + 0.12) * r * 0.6 - r * 0.05); c.closePath();
    c.fillStyle = k % 2 ? '#4A4F58' : '#5E646E'; c.fill();
  }
  // 귀 (한쪽은 축 처짐, 끝이 까맣게 탐)
  for (const s of [-1, 1]) {
    c.save(); c.translate(s * r * 0.45, -r * 0.5); c.rotate(s * (s < 0 ? 0.25 : 1.1 + Math.sin(t * 6) * 0.08));
    c.beginPath(); c.moveTo(-r * 0.2, r * 0.1); c.lineTo(0, -r * 0.5); c.lineTo(r * 0.2, r * 0.1); c.closePath(); c.fillStyle = DARK; c.fill();
    c.beginPath(); c.moveTo(-r * 0.1, r * 0.04); c.lineTo(0, -r * 0.3); c.lineTo(r * 0.1, r * 0.04); c.closePath(); c.fillStyle = PINK; c.fill();
    c.beginPath(); c.moveTo(-r * 0.07, -r * 0.3); c.lineTo(0, -r * 0.5); c.lineTo(r * 0.07, -r * 0.3); c.closePath(); c.fillStyle = '#26282D'; c.fill();
    c.restore();
  }
  // 머리
  c.beginPath(); c.ellipse(0, 0, r * 0.78, r * 0.7, 0, 0, 7); c.fillStyle = FUR; c.fill();
  for (const s of [-1, 1]) { c.beginPath(); c.moveTo(s * r * 0.7, r * 0.05); c.lineTo(s * r * 0.95, r * 0.25); c.lineTo(s * r * 0.68, r * 0.35); c.closePath(); c.fillStyle = FUR; c.fill(); }
  // 그을음
  for (const [sx, sy, rx, ry] of [[-0.35, -0.3, 0.22, 0.13], [0.3, 0.35, 0.18, 0.1], [0.4, -0.15, 0.12, 0.08], [-0.15, 0.45, 0.14, 0.07]]) { c.beginPath(); c.ellipse(sx * r, sy * r, rx * r, ry * r, 0.4, 0, 7); c.fillStyle = 'rgba(30,30,35,.55)'; c.fill(); }
  // 주둥이
  c.beginPath(); c.ellipse(0, r * 0.3, r * 0.4, r * 0.3, 0, 0, 7); c.fillStyle = LIGHT; c.fill();
  c.beginPath(); c.ellipse(r * 0.12, r * 0.38, r * 0.1, r * 0.05, 0, 0, 7); c.fillStyle = 'rgba(30,30,35,.35)'; c.fill();
  // 뱅글뱅글 눈
  for (const s of [-1, 1]) {
    c.save(); c.translate(s * r * 0.3, -r * 0.08);
    c.beginPath(); c.arc(0, 0, r * 0.17, 0, 7); c.fillStyle = '#fff'; c.fill();
    c.rotate(t * 7 * s); c.beginPath();
    for (let k = 0; k <= 40; k++) { const a = k / 40 * Math.PI * 5, rr = r * 0.15 * k / 40; c.lineTo(Math.cos(a) * rr, Math.sin(a) * rr); }
    c.strokeStyle = INK; c.lineWidth = r * 0.035; c.lineCap = 'round'; c.stroke(); c.restore();
  }
  // 코 (살짝 그을림)
  c.beginPath(); c.ellipse(0, r * 0.15, r * 0.11, r * 0.08, 0, 0, 7); c.fillStyle = INK; c.fill();
  c.beginPath(); c.ellipse(-r * 0.03, r * 0.12, r * 0.03, r * 0.02, 0, 0, 7); c.fillStyle = 'rgba(255,255,255,.6)'; c.fill();
  // 축 늘어진 혀 (한쪽으로 삐죽)
  c.beginPath(); c.moveTo(-r * 0.18, r * 0.42); c.quadraticCurveTo(0, r * 0.5, r * 0.18, r * 0.42); c.strokeStyle = INK; c.lineWidth = r * 0.04; c.stroke();
  const tl = Math.sin(t * 5) * r * 0.03;
  c.beginPath(); c.ellipse(r * 0.08, r * 0.58 + tl, r * 0.1, r * 0.15, 0.3, 0, 7); c.fillStyle = '#F48FB1'; c.fill();
  c.beginPath(); c.moveTo(r * 0.06, r * 0.5 + tl); c.lineTo(r * 0.1, r * 0.66 + tl); c.strokeStyle = '#D2668B'; c.lineWidth = r * 0.02; c.stroke();
  c.restore();
  // 머리 둘레를 도는 별 · 새 (기울기 따라 같이)
  c.save(); c.translate(x, y + bob - r * 0.95); c.rotate(wob * 0.6);
  for (let k = 0; k < 5; k++) {
    const a = t * 3 + k * Math.PI * 2 / 5, ox = Math.cos(a) * r * 0.95, oy = Math.sin(a) * r * 0.22, front = Math.sin(a) > 0;
    c.globalAlpha = front ? 1 : 0.55;
    if (k % 2 === 0) star(c, ox, oy, r * (front ? 0.13 : 0.1), t * 4 + k);
    else bird(c, ox, oy, r * (front ? 0.12 : 0.09), t * 12 + k);
  }
  c.globalAlpha = 1; c.restore();
}
function star(c, x, y, R, rot) {
  c.save(); c.translate(x, y); c.rotate(rot); c.beginPath();
  for (let k = 0; k < 10; k++) { const a = k * Math.PI / 5 - Math.PI / 2, rr = k % 2 ? R * 0.45 : R; c.lineTo(Math.cos(a) * rr, Math.sin(a) * rr); }
  c.closePath(); c.fillStyle = '#FFD740'; c.fill(); c.lineWidth = R * 0.15; c.strokeStyle = '#E0A000'; c.stroke(); c.restore();
}
function bird(c, x, y, R, ph) {
  c.save(); c.translate(x, y);
  c.beginPath(); c.ellipse(0, 0, R, R * 0.8, 0, 0, 7); c.fillStyle = '#81D4FA'; c.fill();
  const w = Math.sin(ph) * R * 0.6; c.beginPath(); c.moveTo(-R * 0.2, 0); c.quadraticCurveTo(-R * 0.6, -R * 0.4 - w, -R * 1.1, -R * 0.2 - w); c.quadraticCurveTo(-R * 0.6, R * 0.1, -R * 0.2, 0); c.fillStyle = '#4FC3F7'; c.fill();
  c.beginPath(); c.arc(R * 0.35, -R * 0.15, R * 0.14, 0, 7); c.fillStyle = INK; c.fill();
  c.beginPath(); c.moveTo(R * 0.85, 0); c.lineTo(R * 1.25, R * 0.1); c.lineTo(R * 0.85, R * 0.25); c.closePath(); c.fillStyle = '#FFB300'; c.fill();
  c.restore();
}
