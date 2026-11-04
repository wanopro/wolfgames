// 🔴 따라 하기: 액세서리 늑대 4마리 — 0 😎 선글라스(초록) · 1 🎀 리본(빨강) · 2 🧢 모자(파랑) · 3 👑 왕관(노랑)
const CFUR = '#9AA3AF', CFUR_D = '#7C8593', CCREAM = '#F1EEE8', CINK = '#2B2B33', CPINK = '#F6AFC0';
// 늑대 얼굴 (원점 = 얼굴 가운데, r = 크기). open: 입 벌림 0~1 (아우~)
export function wolfHead(c, r, open = 0) {
  for (const s of [-1, 1]) {
    c.beginPath(); c.moveTo(s * r * 0.2, -r * 0.42); c.lineTo(s * r * 0.62, -r * 0.92); c.lineTo(s * r * 0.72, -r * 0.22); c.closePath(); c.fillStyle = CFUR_D; c.fill();
    c.beginPath(); c.moveTo(s * r * 0.34, -r * 0.42); c.lineTo(s * r * 0.58, -r * 0.74); c.lineTo(s * r * 0.62, -r * 0.32); c.closePath(); c.fillStyle = CPINK; c.fill();
  }
  c.beginPath(); c.ellipse(0, r * 0.02, r * 0.66, r * 0.58, 0, 0, 7); c.fillStyle = CFUR; c.fill();
  for (const s of [-1, 1]) { c.beginPath(); c.moveTo(s * r * 0.62, r * 0.05); c.lineTo(s * r * 0.8, r * 0.18); c.lineTo(s * r * 0.6, r * 0.28); c.closePath(); c.fillStyle = CFUR; c.fill(); }
  c.beginPath(); c.ellipse(0, r * 0.24, r * 0.34, r * 0.26, 0, 0, 7); c.fillStyle = CCREAM; c.fill();
  for (const s of [-1, 1]) {
    c.beginPath(); c.ellipse(s * r * 0.26, -r * 0.08, r * 0.11, r * 0.13 * (1 - open * 0.5), 0, 0, 7); c.fillStyle = CINK; c.fill();
    c.beginPath(); c.arc(s * r * 0.23, -r * 0.13, r * 0.045, 0, 7); c.fillStyle = '#fff'; c.fill();
    c.beginPath(); c.ellipse(s * r * 0.44, r * 0.16, r * 0.1, r * 0.06, 0, 0, 7); c.fillStyle = 'rgba(255,130,150,.55)'; c.fill();
  }
  c.beginPath(); c.ellipse(0, r * 0.13, r * 0.09, r * 0.065, 0, 0, 7); c.fillStyle = CINK; c.fill();
  if (open > 0.05) { // 아우~ 입
    c.beginPath(); c.ellipse(0, r * 0.33, r * 0.11, r * 0.13 * open + r * 0.03, 0, 0, 7); c.fillStyle = '#5A1E2B'; c.fill();
    c.beginPath(); c.ellipse(0, r * 0.38 + r * 0.06 * open, r * 0.06, r * 0.04, 0, 0, 7); c.fillStyle = '#F48FB1'; c.fill();
  } else {
    c.beginPath(); c.moveTo(-r * 0.12, r * 0.28); c.quadraticCurveTo(-r * 0.06, r * 0.36, 0, r * 0.28); c.quadraticCurveTo(r * 0.06, r * 0.36, r * 0.12, r * 0.28); c.strokeStyle = CINK; c.lineWidth = r * 0.04; c.lineCap = 'round'; c.stroke();
    c.beginPath(); c.ellipse(0, r * 0.36, r * 0.05, r * 0.06, 0, 0, Math.PI); c.fillStyle = '#F48FB1'; c.fill();
  }
}
// 액세서리 (얼굴 위에)
export function accessory(c, k, r, t = 0) {
  if (k === 0) { // 😎 선글라스
    c.fillStyle = '#111';
    for (const s of [-1, 1]) { c.beginPath(); c.roundRect(s * r * 0.26 - r * 0.2, -r * 0.2, r * 0.4, r * 0.26, [r * 0.06, r * 0.06, r * 0.14, r * 0.14]); c.fill(); }
    c.fillRect(-r * 0.08, -r * 0.16, r * 0.16, r * 0.05);
    c.beginPath(); c.moveTo(-r * 0.46, -r * 0.14); c.lineTo(-r * 0.64, -r * 0.2); c.moveTo(r * 0.46, -r * 0.14); c.lineTo(r * 0.64, -r * 0.2); c.strokeStyle = '#111'; c.lineWidth = r * 0.05; c.stroke();
    for (const s of [-1, 1]) { c.beginPath(); c.moveTo(s * r * 0.26 - r * 0.12, -r * 0.14); c.lineTo(s * r * 0.26 - r * 0.02, -r * 0.14); c.strokeStyle = 'rgba(255,255,255,.7)'; c.lineWidth = r * 0.04; c.stroke(); }
  } else if (k === 1) { // 🎀 리본 (머리 왼쪽 위)
    c.save(); c.translate(-r * 0.38, -r * 0.62); c.rotate(-0.35);
    c.fillStyle = '#FF80AB';
    for (const s of [-1, 1]) { c.beginPath(); c.moveTo(0, 0); c.quadraticCurveTo(s * r * 0.32, -r * 0.28, s * r * 0.36, 0); c.quadraticCurveTo(s * r * 0.32, r * 0.26, 0, 0); c.fill(); }
    for (const s of [-1, 1]) { c.beginPath(); c.moveTo(0, 0); c.lineTo(s * r * 0.14, r * 0.3); c.lineTo(s * r * 0.04, r * 0.26); c.closePath(); c.fill(); }
    c.beginPath(); c.arc(0, 0, r * 0.08, 0, 7); c.fillStyle = '#D81B60'; c.fill();
    c.beginPath(); c.ellipse(-r * 0.18, -r * 0.08, r * 0.06, r * 0.03, -0.5, 0, 7); c.fillStyle = 'rgba(255,255,255,.45)'; c.fill();
    c.restore();
  } else if (k === 2) { // 🧢 야구 모자 (챙은 오른쪽)
    c.beginPath(); c.ellipse(0, -r * 0.36, r * 0.6, r * 0.42, 0, Math.PI, 0); c.closePath(); c.fillStyle = '#0D3B8C'; c.fill();
    c.beginPath(); c.ellipse(r * 0.42, -r * 0.36, r * 0.42, r * 0.1, -0.05, 0, 7); c.fillStyle = '#082A66'; c.fill();
    c.beginPath(); c.arc(0, -r * 0.78, r * 0.06, 0, 7); c.fillStyle = '#fff'; c.fill();
    c.beginPath(); c.moveTo(0, -r * 0.76); c.lineTo(0, -r * 0.38); c.strokeStyle = 'rgba(255,255,255,.25)'; c.lineWidth = r * 0.03; c.stroke();
    c.beginPath(); c.ellipse(-r * 0.26, -r * 0.58, r * 0.12, r * 0.06, -0.6, 0, 7); c.fillStyle = 'rgba(255,255,255,.3)'; c.fill();
  } else { // 👑 왕관
    c.beginPath(); c.moveTo(-r * 0.4, -r * 0.48); c.lineTo(-r * 0.46, -r * 0.92); c.lineTo(-r * 0.22, -r * 0.7); c.lineTo(0, -r * 1.02); c.lineTo(r * 0.22, -r * 0.7); c.lineTo(r * 0.46, -r * 0.92); c.lineTo(r * 0.4, -r * 0.48); c.closePath();
    c.fillStyle = '#FFE082'; c.fill(); c.strokeStyle = '#A86E0C'; c.lineWidth = r * 0.03; c.stroke();
    c.fillStyle = '#C8901A'; c.fillRect(-r * 0.4, -r * 0.56, r * 0.8, r * 0.08);
    [[-0.46, -0.92, '#E53950'], [0, -1.02, '#4FC3F7'], [0.46, -0.92, '#69F0AE']].forEach(([x, y, col]) => { c.beginPath(); c.arc(x * r, y * r, r * 0.06, 0, 7); c.fillStyle = col; c.fill(); });
    const tw = 0.5 + 0.5 * Math.sin(t * 6); c.globalAlpha *= tw; c.fillStyle = '#fff'; c.beginPath(); c.arc(r * 0.18, -r * 0.84, r * 0.035, 0, 7); c.fill(); c.globalAlpha /= Math.max(0.01, tw);
  }
}
