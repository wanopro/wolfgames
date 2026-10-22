// 귀여운 회색 늑대 (오른쪽을 보고 달림). (x,y)=땅, s=크기, ph=달리기 위상, opt.run 0~1, opt.open 0~1(입 벌림)
export function cuteWolf(ctx, x, y, s, ph, opt = {}) {
  const FUR = '#9AA3AF', DARK = '#6E7784', LIGHT = '#F1EEE8', INK = '#2B2B33', PINK = '#F6AFC0', OL = 'rgba(0,0,0,.35)';
  const run = opt.run ?? 1, open = opt.open ?? 0, lw = Math.max(1.2, s * 0.025);
  const bob = run * Math.abs(Math.sin(ph)) * s * 0.06;
  ctx.save(); ctx.translate(x, y - bob);
  ctx.lineCap = 'round'; ctx.lineJoin = 'round';
  const P = (u, v) => [u * s, v * s];
  const oval = (u, v, rx, ry, c, st, rot = 0) => { ctx.beginPath(); ctx.ellipse(u * s, v * s, rx * s, ry * s, rot, 0, 7); ctx.fillStyle = c; ctx.fill(); if (st) { ctx.lineWidth = lw; ctx.strokeStyle = OL; ctx.stroke(); } };
  const leg = (hu, hv, ang, c) => {
    ctx.save(); ctx.translate(hu * s, hv * s); ctx.rotate(ang);
    ctx.strokeStyle = c; ctx.lineWidth = s * 0.12; ctx.beginPath(); ctx.moveTo(0, 0); ctx.lineTo(0, s * 0.2); ctx.stroke();
    ctx.beginPath(); ctx.ellipse(s * 0.02, s * 0.22, s * 0.075, s * 0.05, 0, 0, 7); ctx.fillStyle = LIGHT; ctx.fill(); ctx.lineWidth = lw; ctx.strokeStyle = OL; ctx.stroke();
    ctx.restore();
  };
  const sw = run * 0.7;
  // 먼 쪽 다리
  leg(-0.28, -0.26, -Math.sin(ph + 0.6) * sw, DARK);
  leg(0.2, -0.26, Math.sin(ph + 0.6) * sw, DARK);
  // 꼬리 (복슬복슬, 살랑)
  const tw = Math.sin(ph * 0.5) * 0.15 * (0.5 + run);
  ctx.save(); ctx.translate(...P(-0.42, -0.42)); ctx.rotate(-0.5 + tw);
  oval(-0.2, 0, 0.24, 0.12, FUR, true); oval(-0.38, 0.0, 0.08, 0.075, LIGHT, false);
  ctx.restore();
  // 몸통
  oval(-0.05, -0.38, 0.42, 0.21, FUR, true);
  oval(-0.1, -0.5, 0.26, 0.07, DARK);
  oval(0.0, -0.28, 0.27, 0.08, LIGHT);
  // 가까운 쪽 다리
  leg(-0.32, -0.28, Math.sin(ph) * sw, FUR);
  leg(0.16, -0.28, -Math.sin(ph) * sw, FUR);

  // 머리
  const hx = 0.4, hy = -0.7, hr = 0.27;
  // 귀
  for (const [ex, c] of [[hx - 0.14, DARK], [hx + 0.04, FUR]]) {
    ctx.beginPath(); ctx.moveTo(...P(ex - 0.09, hy - 0.16)); ctx.lineTo(...P(ex - 0.01, hy - 0.44)); ctx.lineTo(...P(ex + 0.1, hy - 0.17)); ctx.closePath();
    ctx.fillStyle = c; ctx.fill(); ctx.lineWidth = lw; ctx.strokeStyle = OL; ctx.stroke();
    ctx.beginPath(); ctx.moveTo(...P(ex - 0.045, hy - 0.19)); ctx.lineTo(...P(ex - 0.01, hy - 0.35)); ctx.lineTo(...P(ex + 0.05, hy - 0.2)); ctx.closePath(); ctx.fillStyle = PINK; ctx.fill();
  }
  // 볼털 (뾰족)
  ctx.beginPath(); ctx.moveTo(...P(hx - 0.2, hy + 0.12)); ctx.lineTo(...P(hx - 0.17, hy + 0.3)); ctx.lineTo(...P(hx - 0.07, hy + 0.2)); ctx.lineTo(...P(hx - 0.02, hy + 0.32)); ctx.lineTo(...P(hx + 0.06, hy + 0.2)); ctx.closePath();
  ctx.fillStyle = FUR; ctx.fill(); ctx.lineWidth = lw; ctx.strokeStyle = OL; ctx.stroke();
  ctx.beginPath(); ctx.arc(hx * s, hy * s, hr * s, 0, 7); ctx.fillStyle = FUR; ctx.fill(); ctx.lineWidth = lw; ctx.strokeStyle = OL; ctx.stroke();
  // 이마 무늬
  ctx.beginPath(); ctx.moveTo(...P(hx - 0.02, hy - 0.26)); ctx.quadraticCurveTo(...P(hx + 0.08, hy - 0.14), ...P(hx + 0.2, hy - 0.08)); ctx.quadraticCurveTo(...P(hx + 0.06, hy - 0.1), ...P(hx - 0.02, hy - 0.26)); ctx.fillStyle = LIGHT; ctx.fill();
  // 주둥이: 아래턱 (입 벌리면 아래로)
  const jx = hx + 0.12, jy = hy + 0.08, a = open * 0.7;
  ctx.save(); ctx.translate(jx * s, jy * s); ctx.rotate(a); ctx.translate(-jx * s, -jy * s);
  oval(jx + 0.13, jy + 0.05, 0.14, 0.05, LIGHT, true);
  if (open > 0.05) {
    ctx.fillStyle = '#fff'; for (const u of [0.1, 0.18]) { ctx.beginPath(); ctx.moveTo(...P(jx + u, jy + 0.01)); ctx.lineTo(...P(jx + u + 0.02, jy - 0.035)); ctx.lineTo(...P(jx + u + 0.04, jy + 0.01)); ctx.fill(); }
    oval(jx + 0.1, jy + 0.025, 0.07, 0.018, '#E86A86');
  }
  ctx.restore();
  if (open > 0.05) { // 입 안
    ctx.beginPath(); ctx.moveTo(...P(jx - 0.02, jy)); ctx.lineTo(...P(jx + 0.27, jy - 0.03 - open * 0.04)); ctx.lineTo(...P(jx + 0.27 * Math.cos(a), jy + 0.27 * Math.sin(a) + 0.03)); ctx.closePath();
    ctx.fillStyle = '#8E2440'; ctx.fill();
  }
  // 위턱 (코)
  ctx.save(); ctx.translate(jx * s, jy * s); ctx.rotate(-a * 0.3); ctx.translate(-jx * s, -jy * s);
  ctx.beginPath(); ctx.moveTo(...P(jx - 0.06, jy - 0.13)); ctx.quadraticCurveTo(...P(jx + 0.14, jy - 0.16), ...P(jx + 0.27, jy - 0.07));
  ctx.quadraticCurveTo(...P(jx + 0.31, jy), ...P(jx + 0.24, jy + 0.02)); ctx.lineTo(...P(jx - 0.04, jy + 0.03)); ctx.closePath();
  ctx.fillStyle = LIGHT; ctx.fill(); ctx.lineWidth = lw; ctx.strokeStyle = OL; ctx.stroke();
  ctx.fillStyle = '#fff'; ctx.beginPath(); ctx.moveTo(...P(jx + 0.17, jy + 0.02)); ctx.lineTo(...P(jx + 0.19, jy + 0.08)); ctx.lineTo(...P(jx + 0.21, jy + 0.02)); ctx.fill(); // 송곳니
  oval(jx + 0.27, jy - 0.065, 0.05, 0.04, INK, false, 0.3);
  oval(jx + 0.26, jy - 0.08, 0.013, 0.009, 'rgba(255,255,255,.7)');
  ctx.restore();
  // 눈 (크고 반짝) — 물 때는 > 모양
  const ex = hx + 0.08, ey = hy - 0.06;
  if (opt.happy || (open > 0.5 && !opt.run)) {
    ctx.beginPath(); ctx.moveTo(...P(ex - 0.05, ey - 0.05)); ctx.lineTo(...P(ex + 0.03, ey)); ctx.lineTo(...P(ex - 0.05, ey + 0.05)); ctx.strokeStyle = INK; ctx.lineWidth = s * 0.03; ctx.stroke();
  } else {
    oval(ex, ey, 0.07, 0.075, '#fff'); oval(ex + 0.015, ey + 0.005, 0.05, 0.055, INK);
    oval(ex, ey - 0.02, 0.02, 0.02, '#fff');
  }
  if (open > 0.3 && !opt.happy) { ctx.beginPath(); ctx.moveTo(...P(ex - 0.08, ey - 0.12)); ctx.lineTo(...P(ex + 0.06, ey - 0.07)); ctx.strokeStyle = INK; ctx.lineWidth = s * 0.03; ctx.stroke(); } // 사나운 눈썹
  oval(hx - 0.02, hy + 0.1, 0.06, 0.035, 'rgba(255,143,163,.6)'); // 볼 홍조
  ctx.restore();
}
