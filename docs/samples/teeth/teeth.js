// 늑대이빨 새 디자인 시안 (만화 그림체). 보내준 그림 배치: 벌린 입(이빨 20개) ↔ 닫으면 화난 늑대 얼굴 + 다시 버튼
const FUR = '#A3ABB7', FUR_D = '#6F7887', FUR_L = '#CCD2DA', CREAM = '#EFE9E1', INK = '#25252D', OL = '#4A515D';
const ease = (u) => 1 - Math.pow(1 - Math.min(1, Math.max(0, u)), 3);
const HEAD = 44; // 위 제목줄
// 이빨 자리 (보내준 그림 비율): [x, y, 위턱?]
export function layout(W, H) {
  const hh = H - HEAD, X = (f) => f * W, Y = (f) => HEAD + f * hh;
  const t = [];
  for (const [fx, fy] of [[0.19, 0.225], [0.39, 0.21], [0.61, 0.21], [0.81, 0.225]]) t.push([X(fx), Y(fy), true]);
  for (const fy of [0.305, 0.385, 0.465]) t.push([X(0.11), Y(fy), true]);
  for (const fy of [0.305, 0.385, 0.465]) t.push([X(0.89), Y(fy), true]);
  for (const [fy, fx] of [[0.585, 0.135], [0.665, 0.12], [0.745, 0.115]]) t.push([X(fx), Y(fy), false]);
  for (const [fy, fx] of [[0.585, 0.865], [0.665, 0.88], [0.745, 0.885]]) t.push([X(fx), Y(fy), false]);
  for (const [fx, fy] of [[0.19, 0.825], [0.39, 0.84], [0.61, 0.84], [0.81, 0.825]]) t.push([X(fx), Y(fy), false]);
  return { X, Y, t, mid: Y(0.525) };
}
// 달걀 모양 이빨. up=true(위턱) 이면 끝이 아래로
function tooth(c, x, y, up, s, pressed) {
  c.save(); c.translate(x, y); if (!up) c.rotate(Math.PI);
  const w = 27 * s, h = 30 * s;
  c.beginPath(); c.ellipse(0, -h * 0.62, w * 1.08, h * 0.32, 0, 0, 7); c.fillStyle = 'rgba(60,0,10,.5)'; c.fill(); // 잇몸 그림자
  if (pressed) { c.translate(0, -h * 0.4); c.scale(0.88, 0.55); }
  c.beginPath(); c.moveTo(-w, -h * 0.55); c.bezierCurveTo(-w, h * 0.25, -w * 0.5, h, 0, h); c.bezierCurveTo(w * 0.5, h, w, h * 0.25, w, -h * 0.55);
  c.bezierCurveTo(w, -h * 0.85, -w, -h * 0.85, -w, -h * 0.55); c.closePath();
  const g = c.createRadialGradient(-w * 0.25, -h * 0.05, 3, 0, 0, h * 1.15);
  g.addColorStop(0, pressed ? '#d6d0c8' : '#FFFFFF'); g.addColorStop(0.55, pressed ? '#b3aca4' : '#F1EDE7'); g.addColorStop(1, pressed ? '#8f8880' : '#BDB5AC');
  c.fillStyle = g; c.fill(); c.lineWidth = 2.5; c.strokeStyle = 'rgba(80,55,45,.45)'; c.stroke();
  if (!pressed) { c.beginPath(); c.ellipse(-w * 0.32, h * 0.05, w * 0.16, h * 0.32, 0.15, 0, 7); c.fillStyle = 'rgba(255,255,255,.85)'; c.fill(); }
  c.restore();
}
function furFrame(c, x, y, w, h, r) {
  const g = c.createLinearGradient(x, 0, x + w, 0); g.addColorStop(0, FUR_D); g.addColorStop(0.18, FUR); g.addColorStop(0.5, FUR_L); g.addColorStop(0.82, FUR); g.addColorStop(1, FUR_D);
  c.beginPath(); c.roundRect(x, y, w, h, r); c.fillStyle = g; c.fill(); c.lineWidth = 5; c.strokeStyle = OL; c.stroke();
}
function ears(c, W, y, s = 1, droop = 0, cx = W / 2) {
  for (const sd of [-1, 1]) {
    const x = cx + sd * W * 0.3;
    c.save(); c.translate(x, y); c.scale(sd, 1); c.rotate(droop); // 왼쪽 귀는 오른쪽 귀를 좌우로 뒤집어서 그림
    c.beginPath(); c.moveTo(-58 * s, 20); c.quadraticCurveTo(-4 * s, -92 * s, 22 * s, -96 * s); c.quadraticCurveTo(46 * s, -50 * s, 58 * s, 20); c.closePath();
    c.fillStyle = FUR_D; c.fill(); c.lineWidth = 5; c.strokeStyle = OL; c.stroke();
    c.beginPath(); c.moveTo(-30 * s, 12); c.quadraticCurveTo(0, -60 * s, 18 * s, -62 * s); c.quadraticCurveTo(32 * s, -34 * s, 30 * s, 12); c.closePath(); c.fillStyle = '#F6AFC0'; c.fill();
    c.restore();
  }
}
// ---------- 벌린 입 ----------
function openMouth(c, W, H, lay, s) {
  const { X, Y } = lay;
  ears(c, W, Y(0.122), 0.86);
  furFrame(c, X(0.005), Y(0.085), X(0.99), Y(0.915) - Y(0.085), X(0.2));
  // 코 (위쪽 털 위)
  c.save(); c.translate(W / 2, Y(0.12));
  c.beginPath(); c.moveTo(-46, -12); c.bezierCurveTo(-46, -30, 46, -30, 46, -12); c.bezierCurveTo(46, 8, 12, 22, 0, 22); c.bezierCurveTo(-12, 22, -46, 8, -46, -12); c.closePath();
  c.fillStyle = INK; c.fill(); for (const sd of [-1, 1]) { c.beginPath(); c.ellipse(sd * 18, -2, 10, 5, sd * 0.5, 0, 7); c.fillStyle = '#000'; c.fill(); }
  c.beginPath(); c.ellipse(-16, -18, 16, 5, -0.15, 0, 7); c.fillStyle = 'rgba(255,255,255,.4)'; c.fill(); c.restore();
  // 입 안
  const mx = X(0.025), my = Y(0.165), mw = X(0.95), mh = Y(0.885) - Y(0.165);
  const g = c.createLinearGradient(0, my, 0, my + mh); g.addColorStop(0, '#B8323A'); g.addColorStop(0.12, '#6E1219'); g.addColorStop(0.5, '#1E0306'); g.addColorStop(0.8, '#5A0E14'); g.addColorStop(1, '#D9473F');
  c.beginPath(); c.roundRect(mx, my, mw, mh, X(0.13)); c.fillStyle = g; c.fill(); c.lineWidth = 4; c.strokeStyle = '#3A060B'; c.stroke();
  // 안쪽 그늘 + 위·아래턱 경계
  const ig = c.createRadialGradient(W / 2, lay.mid, 20, W / 2, lay.mid, W * 0.6); ig.addColorStop(0, 'rgba(0,0,0,.55)'); ig.addColorStop(1, 'rgba(0,0,0,0)');
  c.beginPath(); c.roundRect(X(0.2), Y(0.27), X(0.6), Y(0.6), X(0.08)); c.fillStyle = ig; c.fill();
  c.beginPath(); c.moveTo(mx + 6, lay.mid); c.lineTo(mx + mw - 6, lay.mid); c.strokeStyle = 'rgba(0,0,0,.35)'; c.lineWidth = 2; c.stroke();
  // 혀
  const tg = c.createLinearGradient(0, Y(0.6), 0, Y(0.8)); tg.addColorStop(0, '#5E1622'); tg.addColorStop(1, '#C94A5C');
  c.beginPath(); c.roundRect(X(0.25), Y(0.6), X(0.5), Y(0.2), X(0.12)); c.fillStyle = tg; c.fill();
  lay.t.forEach(([x, y, up], i) => tooth(c, x, y, up, s, lay.pressed.has(i)));
}
// ---------- 닫은 입: 지금 게임의 늑대 얼굴 그대로, 입만 꽉 닫음 ----------
function closedFace(c, W, H, s, bite) {
  const k = (W * 0.92) / 722, cy = HEAD + (H - HEAD) * 0.42;
  c.save(); c.translate(W / 2 - 361 * k, cy - 252 * k); c.scale(k, k);
  // 귀
  for (const [b0, ap, b1, i0, ia, i1] of [[[60, 80], [158, -87], [300, 60], [118, 40], [158, -36], [212, 30]], [[662, 80], [564, -87], [422, 60], [604, 40], [564, -36], [510, 30]]]) {
    c.beginPath(); c.moveTo(...b0); c.lineTo(...ap); c.lineTo(...b1); c.closePath(); c.fillStyle = '#6E7A8A'; c.fill();
    c.beginPath(); c.moveTo(...i0); c.lineTo(...ia); c.lineTo(...i1); c.closePath(); c.fillStyle = '#F6AFC0'; c.fill();
  }
  // 얼굴
  c.beginPath(); c.roundRect(0, 0, 722, 503, [230, 230, 160, 160]); c.fillStyle = '#9AA5B4'; c.fill();
  c.save(); c.clip();
  c.beginPath(); c.moveTo(310, 0); c.lineTo(361, 78); c.lineTo(412, 0); c.closePath(); c.fillStyle = '#D9D5CE'; c.fill();
  c.beginPath(); c.ellipse(361, 186, 150, 72, 0, 0, 7); c.fillStyle = '#F2EAE0'; c.fill();
  c.beginPath(); c.ellipse(361, 448, 200, 44, 0, 0, 7); c.fillStyle = '#F2EAE0'; c.fill();
  c.restore();
  // 눈 > <
  c.lineWidth = 7; c.lineCap = 'round'; c.lineJoin = 'round'; c.strokeStyle = '#2B2B33';
  c.beginPath(); c.moveTo(200, 60); c.lineTo(260, 90); c.lineTo(200, 120); c.stroke();
  c.beginPath(); c.moveTo(522, 60); c.lineTo(462, 90); c.lineTo(522, 120); c.stroke();
  // 볼터치
  for (const x of [211, 511]) { c.beginPath(); c.ellipse(x, 153, 44, 22, 0, 0, 7); c.fillStyle = 'rgba(255,143,163,.4)'; c.fill(); }
  // 코
  c.beginPath(); c.ellipse(361, 165, 43, 18, 0, 0, 7); c.fillStyle = '#2B2B33'; c.fill();
  c.beginPath(); c.ellipse(352, 160, 12, 5, 0, 0, 7); c.fillStyle = 'rgba(255,255,255,.5)'; c.fill();
  c.beginPath(); c.moveTo(361, 183); c.lineTo(361, 268 + bite * 6); c.lineWidth = 5; c.stroke();
  // 꽉 닫은 입 (가운데가 살짝 내려간 선) + 삐죽 나온 이빨
  const my = 268 + bite * 6;
  c.beginPath(); c.moveTo(80, my - 18); c.quadraticCurveTo(220, my + 4, 361, my); c.quadraticCurveTo(502, my + 4, 642, my - 18); c.lineWidth = 7; c.stroke();
  for (const [x, w, h] of [[170, 34, 30], [278, 40, 40], [444, 40, 40], [552, 34, 30]]) {
    const yy = my - 18 * Math.pow((x - 361) / 281, 2) + 2;
    c.beginPath(); c.moveTo(x - w / 2, yy); c.lineTo(x, yy + h); c.lineTo(x + w / 2, yy); c.closePath(); c.fillStyle = '#FFFDF5'; c.fill();
  }
  c.restore();
  // 다시 버튼
  const by = HEAD + (H - HEAD) * 0.74;
  c.beginPath(); c.arc(W / 2, by, 46, 0, 7); const bg = c.createRadialGradient(W / 2 - 14, by - 16, 6, W / 2, by, 50); bg.addColorStop(0, '#FF6B6B'); bg.addColorStop(1, '#D32F2F'); c.fillStyle = bg; c.fill();
  c.beginPath(); c.arc(W / 2, by + 3, 22, -2.4, 2.6); c.strokeStyle = '#F5E6E6'; c.lineWidth = 9; c.lineCap = 'round'; c.stroke();
  c.beginPath(); c.moveTo(W / 2 - 30, by - 22); c.lineTo(W / 2 - 13, by - 22); c.lineTo(W / 2 - 22, by - 6); c.closePath(); c.fillStyle = '#F5E6E6'; c.fill();
}
export class Teeth {
  constructor(win, presses) { this.win = win; this.presses = presses; }
  draw(c, W, H, t) {
    const lay = layout(W, H), s = Math.min(W / 393, (H - HEAD) / 716);
    lay.pressed = new Set(this.presses.filter(([tt]) => tt <= t).map(([, i]) => i));
    const wp = this.presses.find(([, i]) => i === this.win), bt = wp ? t - wp[0] : -1;
    c.fillStyle = '#0B0B10'; c.fillRect(0, 0, W, H);
    const vg = c.createRadialGradient(W / 2, H * 0.45, 30, W / 2, H * 0.45, H * 0.6); vg.addColorStop(0, '#1C2326'); vg.addColorStop(1, '#0B0B10'); c.fillStyle = vg; c.fillRect(0, 0, W, H);
    if (bt < 0.1) openMouth(c, W, H, lay, s);
    else if (bt < 0.22) { // 콱 닫히는 중: 입이 세로로 확 줄어듦
      const u = (bt - 0.1) / 0.12; c.save(); c.translate(0, lay.mid); c.scale(1, 1 - 0.8 * u); c.translate(0, -lay.mid); openMouth(c, W, H, lay, s); c.restore();
    } else {
      const sh = bt < 0.7 ? Math.sin(bt * 70) * 5 * (1 - (bt - 0.22) / 0.5) : 0;
      c.save(); c.translate(sh, 0); closedFace(c, W, H, s, bt < 0.6 ? 1 : 0); c.restore();
      if (bt < 0.4) { c.fillStyle = `rgba(255,40,60,${0.4 * (1 - (bt - 0.22) / 0.18)})`; c.fillRect(0, 0, W, H); }
      const u = Math.min(1, (bt - 0.22) / 0.2); c.save(); c.translate(W / 2, HEAD + (H - HEAD) * 0.12); c.scale(0.6 + 0.5 * u, 0.6 + 0.5 * u);
      c.font = '900 56px sans-serif'; c.textAlign = 'center'; c.textBaseline = 'middle'; c.lineWidth = 10; c.strokeStyle = '#fff'; c.strokeText('앙!', 0, 0); c.fillStyle = '#FF3D5A'; c.fillText('앙!', 0, 0); c.restore();
    }
    c.fillStyle = '#12121A'; c.fillRect(0, 0, W, HEAD); c.font = '700 18px sans-serif'; c.fillStyle = '#fff'; c.textAlign = 'left'; c.textBaseline = 'middle'; c.fillText('←  늑대이빨', 14, 23);
    c.textAlign = 'right'; c.fillStyle = '#FFB300'; c.fillText(`남은 이빨 ${20 - lay.pressed.size}`, W - 14, 23);
    if (bt < 0.1) for (const [tt, i] of this.presses) { const u = (t - tt) / 0.4; if (u < 0 || u > 1) continue; const [x, y] = lay.t[i]; c.beginPath(); c.arc(x, y, 26 + 26 * u, 0, 7); c.strokeStyle = `rgba(255,255,255,${0.8 * (1 - u)})`; c.lineWidth = 4; c.stroke(); }
  }
}
