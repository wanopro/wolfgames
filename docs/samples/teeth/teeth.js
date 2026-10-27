// 늑대이빨 새 디자인 시안: 화면 가득 벌린 늑대 입 (위에서 내려다본 모양), 큰 이빨 20개를 넓게
const FUR = '#9AA3AF', FUR_D = '#6E7784', FUR_L = '#C5CBD4', CREAM = '#EFE9E1', INK = '#26262E';
const ease = (u) => 1 - Math.pow(1 - Math.min(1, Math.max(0, u)), 3);
// 이빨 자리: [x, y, 방향(라디안, 끝이 향하는 쪽), 위턱?] (화면 393x760 기준 비율)
export function layout(W, H) {
  // 보내준 그림 비율 그대로: 위 4 · 위 양옆 3+3 · 아래 양옆 3+3 · 아래 4 = 20개
  const y0 = 44, hh = H - 44, Y = (f) => y0 + f * hh;
  const L = 0.03 * W, R = W - L, T = Y(0.085), B = Y(0.94), mid = Y(0.5);
  const teeth = [];
  for (const fx of [0.19, 0.395, 0.605, 0.81]) teeth.push([fx * W, Y(0.215) + (fx < 0.3 || fx > 0.7 ? Y(0.012) - y0 : 0), Math.PI / 2, true]);
  for (const s of [-1, 1]) for (const fy of [0.3, 0.375, 0.45]) teeth.push([s < 0 ? 0.115 * W : 0.885 * W, Y(fy), s < 0 ? 0 : Math.PI, true]);
  for (const s of [-1, 1]) for (const [fy, fx] of [[0.56, 0.13], [0.635, 0.12], [0.71, 0.115]]) teeth.push([s < 0 ? fx * W : (1 - fx) * W, Y(fy), s < 0 ? 0 : Math.PI, false]);
  for (const fx of [0.19, 0.395, 0.605, 0.81]) teeth.push([fx * W, Y(0.815) - (fx < 0.3 || fx > 0.7 ? Y(0.012) - y0 : 0), -Math.PI / 2, false]);
  return { L, R, T, B, mid, teeth };
}
function tooth(c, x, y, dir, s, pressed) {
  c.save(); c.translate(x, y); c.rotate(dir - Math.PI / 2); // 끝이 +y 쪽
  const w = 24 * s, h = 30 * s;
  // 잇몸 그림자
  c.beginPath(); c.ellipse(0, -h * 0.55, w * 1.05, h * 0.38, 0, 0, 7); c.fillStyle = 'rgba(60,0,10,.45)'; c.fill();
  if (pressed) { c.scale(0.86, 0.6); c.translate(0, -h * 0.35); }
  const g = c.createRadialGradient(-w * 0.3, -h * 0.2, 2, 0, 0, h * 1.1);
  g.addColorStop(0, pressed ? '#cfc8c0' : '#FFFFFF'); g.addColorStop(0.6, pressed ? '#a9a29a' : '#EDE8E2'); g.addColorStop(1, pressed ? '#8a837c' : '#B9B2AA');
  c.beginPath(); c.moveTo(-w, -h * 0.5); c.bezierCurveTo(-w, h * 0.3, -w * 0.35, h * 0.95, 0, h); c.bezierCurveTo(w * 0.35, h * 0.95, w, h * 0.3, w, -h * 0.5);
  c.bezierCurveTo(w, -h * 0.85, -w, -h * 0.85, -w, -h * 0.5); c.closePath(); c.fillStyle = g; c.fill();
  c.lineWidth = 2; c.strokeStyle = 'rgba(90,60,50,.35)'; c.stroke();
  if (!pressed) { c.beginPath(); c.ellipse(-w * 0.35, -h * 0.15, w * 0.18, h * 0.3, 0.2, 0, 7); c.fillStyle = 'rgba(255,255,255,.8)'; c.fill(); }
  c.restore();
}
function jaw(c, W, H, lay, upper, off) {
  const { L, R, T, B, mid } = lay;
  c.save(); c.translate(0, off);
  c.beginPath(); if (upper) c.rect(0, 0, W, mid + 2); else c.rect(0, mid - 2, W, H); c.clip();
  // 털 테두리 (복슬)
  const fur = c.createLinearGradient(0, T, 0, B); fur.addColorStop(0, FUR_L); fur.addColorStop(0.5, FUR); fur.addColorStop(1, FUR_D);
  c.beginPath(); c.roundRect(L, T, R - L, B - T, 0.16 * W); c.fillStyle = fur; c.fill();
  // 볼 털 뾰족뾰족
  for (const s of [-1, 1]) for (let i = 0; i < 6; i++) { const y = T + 0.18 * H + i * 0.11 * H, x = s < 0 ? L : R; c.beginPath(); c.moveTo(x, y); c.lineTo(x + s * 0.035 * W, y + 0.03 * H); c.lineTo(x, y + 0.06 * H); c.closePath(); c.fillStyle = FUR; c.fill(); }
  if (upper) {
    // 귀
    for (const s of [-1, 1]) { const x = W / 2 + s * 0.3 * W; c.beginPath(); c.moveTo(x - 0.09 * W, T + 0.02 * H); c.lineTo(x + s * 0.05 * W, T - 0.075 * H); c.lineTo(x + 0.09 * W, T + 0.02 * H); c.closePath(); c.fillStyle = FUR_D; c.fill();
      c.beginPath(); c.moveTo(x - 0.05 * W, T + 0.01 * H); c.lineTo(x + s * 0.04 * W, T - 0.05 * H); c.lineTo(x + 0.05 * W, T + 0.01 * H); c.closePath(); c.fillStyle = '#F6AFC0'; c.fill(); }
    // 코 (위에서 본 늑대 코)
    c.beginPath(); c.ellipse(W / 2, T + 0.035 * H, 0.11 * W, 0.028 * H, 0, 0, 7); c.fillStyle = INK; c.fill();
    for (const s of [-1, 1]) { c.beginPath(); c.ellipse(W / 2 + s * 0.045 * W, T + 0.04 * H, 0.025 * W, 0.009 * H, s * 0.3, 0, 7); c.fillStyle = '#000'; c.fill(); }
    c.beginPath(); c.ellipse(W / 2 - 0.04 * W, T + 0.025 * H, 0.03 * W, 0.006 * H, -0.2, 0, 7); c.fillStyle = 'rgba(255,255,255,.35)'; c.fill();
  } else {
    // 턱 아래 크림색 털
    c.beginPath(); c.ellipse(W / 2, B - 0.012 * H, 0.22 * W, 0.022 * H, 0, 0, 7); c.fillStyle = CREAM; c.fill();
  }
  // 입 안
  const mL = L + 0.045 * W, mR = R - 0.045 * W, mT = T + 0.065 * H, mB = B - 0.035 * H;
  const g = c.createRadialGradient(W / 2, mid, 10, W / 2, mid, 0.55 * H); g.addColorStop(0, '#1A0306'); g.addColorStop(0.55, '#4A0A12'); g.addColorStop(1, '#A3242F');
  c.beginPath(); c.roundRect(mL, mT, mR - mL, mB - mT, 0.12 * W); c.fillStyle = g; c.fill();
  // 잇몸 띠
  c.lineWidth = 0.05 * W; c.strokeStyle = '#C0392F'; c.beginPath(); c.roundRect(mL + 0.025 * W, mT + 0.025 * W, mR - mL - 0.05 * W, mB - mT - 0.05 * W, 0.1 * W); c.stroke();
  if (!upper) { // 혀
    const tg = c.createLinearGradient(0, mid, 0, mB); tg.addColorStop(0, '#8E2A3A'); tg.addColorStop(1, '#E26A7E');
    c.beginPath(); c.ellipse(W / 2, mid + 0.25 * H, 0.22 * W, 0.17 * H, 0, 0, 7); c.fillStyle = tg; c.fill();
    c.beginPath(); c.moveTo(W / 2, mid + 0.12 * H); c.lineTo(W / 2, mid + 0.36 * H); c.strokeStyle = 'rgba(120,20,40,.5)'; c.lineWidth = 3; c.stroke();
  } else { // 입천장 주름
    for (let i = 0; i < 4; i++) { c.beginPath(); c.ellipse(W / 2, mT + 0.12 * H + i * 0.05 * H, 0.2 * W - i * 0.02 * W, 0.012 * H, 0, 0, Math.PI); c.strokeStyle = 'rgba(160,40,50,.35)'; c.lineWidth = 3; c.stroke(); }
  }
  c.restore();
}
export class Teeth {
  constructor(win, presses) { this.win = win; this.presses = presses; }
  draw(c, W, H, t) {
    const lay = layout(W, H), s = Math.min(W / 393, H / 760) * 1.0;
    const pressed = new Set(this.presses.filter(([tt]) => tt <= t).map(([, i]) => i));
    const winP = this.presses.find(([, i]) => i === this.win), bt = winP ? t - winP[0] : -1;
    // 무는 동작: 0.15초 뒤 확 닫힘 → 0.9초 물고 있다가 → 천천히 벌림
    let close = 0; if (bt > 0.12) { const u = bt - 0.12; close = u < 0.12 ? ease(u / 0.12) : u < 1.2 ? 1 : Math.max(0.55, 1 - (u - 1.2) * 0.8); }
    const gap = 0.1 * H * close;
    const shake = bt > 0.24 && bt < 0.7 ? Math.sin(bt * 70) * 4 : 0;
    c.fillStyle = '#0B0B10'; c.fillRect(0, 0, W, H);
    c.save(); c.translate(shake, 0);
    jaw(c, W, H, lay, false, -gap);
    lay.teeth.forEach(([x, y, dir, up], i) => { if (!up) tooth(c, x, y - gap, dir, s, pressed.has(i) && i !== this.win); });
    jaw(c, W, H, lay, true, gap);
    lay.teeth.forEach(([x, y, dir, up], i) => { if (up) tooth(c, x, y + gap, dir, s, pressed.has(i) && i !== this.win); });
    if (bt > 0.2 && bt < 0.5) { c.fillStyle = `rgba(255,40,60,${0.35 * (1 - (bt - 0.2) / 0.3)})`; c.fillRect(0, 0, W, H); }
    c.restore();
    if (bt > 0.24) {
      const u = Math.min(1, (bt - 0.24) / 0.2); c.save(); c.translate(W / 2, lay.mid); c.scale(0.6 + u * 0.6, 0.6 + u * 0.6);
      c.font = '900 64px sans-serif'; c.textAlign = 'center'; c.textBaseline = 'middle'; c.lineWidth = 10; c.strokeStyle = '#fff'; c.strokeText('앙!', 0, 0); c.fillStyle = '#FF3D5A'; c.fillText('앙!', 0, 0); c.restore();
    }
    // 위 제목줄
    c.fillStyle = '#12121A'; c.fillRect(0, 0, W, 44); c.font = '700 18px sans-serif'; c.fillStyle = '#fff'; c.textAlign = 'left'; c.textBaseline = 'middle'; c.fillText('←  늑대이빨', 14, 23);
    c.textAlign = 'right'; c.fillStyle = '#FFB300'; c.fillText(`남은 당첨 1 · 남은 이빨 ${20 - pressed.size}`, W - 14, 23);
    // 터치 표시
    for (const [tt, i] of this.presses) { const u = (t - tt) / 0.4; if (u < 0 || u > 1) continue; const [x, y, , up] = lay.teeth[i]; c.beginPath(); c.arc(x, y + (up ? gap : -gap), 24 + 26 * u, 0, 7); c.strokeStyle = `rgba(255,255,255,${0.8 * (1 - u)})`; c.lineWidth = 4; c.stroke(); }
  }
}
