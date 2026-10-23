const INK='#2B2B33';
export function oldWolf(ctx, r, open, bite) {
  const FUR = '#9AA5B4', DARK = '#7D8899', BEL = '#FFF6E8', OL = 'rgba(0,0,0,.4)', INK = '#2B2B33', PINK = '#F6AFC0';
  const s = r, sw = Math.max(1.5, s * 0.02);
  ctx.save();
  ctx.rotate(-Math.PI / 2); // u(→)=앞(코), v(↓)=배 쪽
  ctx.lineJoin = 'round'; ctx.lineCap = 'round';
  const oval = (x, y, rx, ry, fill, stroke, rot = 0) => { ctx.beginPath(); ctx.ellipse(x * s, y * s, rx * s, ry * s, rot, 0, 7); ctx.fillStyle = fill; ctx.fill(); if (stroke) { ctx.lineWidth = sw; ctx.strokeStyle = OL; ctx.stroke(); } };
  const tri = (pts, fill, stroke) => { ctx.beginPath(); pts.forEach(([u, v], i) => (i ? ctx.lineTo(u * s, v * s) : ctx.moveTo(u * s, v * s))); ctx.closePath(); ctx.fillStyle = fill; ctx.fill(); if (stroke) { ctx.lineWidth = sw; ctx.strokeStyle = OL; ctx.stroke(); } };

  // 꼬리 (뒤로 쭉, 끝은 흰색)
  oval(-0.68, -0.04, 0.22, 0.09, FUR, true, -0.15); oval(-0.85, -0.01, 0.07, 0.055, BEL, false, -0.15);
  // 뒷다리·앞다리 (엎드려 앞으로 뻗은 자세)
  for (const [u, v, len, dark] of [[-0.34, 0.12, 0.2, 1], [0.12, 0.12, 0.24, 1], [-0.26, 0.15, 0.2, 0], [0.2, 0.15, 0.26, 0]]) {
    ctx.globalAlpha = dark ? 0.75 : 1;
    ctx.strokeStyle = FUR; ctx.lineWidth = s * 0.1; ctx.beginPath(); ctx.moveTo(u * s, v * s); ctx.lineTo((u + len) * s, (v + 0.08) * s); ctx.stroke();
    ctx.globalAlpha = 1; oval(u + len + 0.02, v + 0.085, 0.06, 0.045, BEL, true);
  }
  // 몸통
  oval(-0.15, 0.0, 0.42, 0.19, FUR, true); oval(-0.12, 0.1, 0.28, 0.07, BEL);
  // 등 무늬
  oval(-0.2, -0.1, 0.25, 0.06, DARK);
  // 목 갈기
  tri([[0.05, -0.17], [0.1, -0.24], [0.15, -0.16], [0.2, -0.22], [0.2, 0.12], [0.08, 0.16], [0.12, 0.08], [0.02, 0.1]], FUR, true);

  // 머리
  const hx = 0.36, hy = -0.07, hr = 0.27;
  for (const ex of [hx - hr * 0.6, hx + hr * 0.15]) { // 귀 2개
    tri([[ex - hr * 0.35, hy - hr * 0.55], [ex - hr * 0.05, hy - hr * 1.5], [ex + hr * 0.38, hy - hr * 0.6]], FUR, true);
    tri([[ex - hr * 0.17, hy - hr * 0.68], [ex - hr * 0.04, hy - hr * 1.18], [ex + hr * 0.2, hy - hr * 0.7]], PINK);
  }
  ctx.beginPath(); ctx.arc(hx * s, hy * s, hr * s, 0, 7); ctx.fillStyle = FUR; ctx.fill(); ctx.lineWidth = sw; ctx.strokeStyle = OL; ctx.stroke();
  // 볼털 (뾰족)
  tri([[hx - hr * 0.5, hy + hr * 0.75], [hx - hr * 0.2, hy + hr * 1.25], [hx + hr * 0.05, hy + hr * 0.85], [hx + hr * 0.3, hy + hr * 1.15], [hx + hr * 0.45, hy + hr * 0.7]], FUR, true);

  // 주둥이: 위턱·아래턱 (경첩 = 주둥이 뒤쪽)
  const jx = hx + hr * 0.5, jy = hy + hr * 0.36, a = open * 0.75;
  const hinge = (ang, fn) => { ctx.save(); ctx.translate(jx * s, jy * s); ctx.rotate(ang); ctx.translate(-jx * s, -jy * s); fn(); ctx.restore(); };
  if (open > 0.04) { // 입 안
    ctx.beginPath(); ctx.moveTo(jx * s, jy * s);
    ctx.lineTo((jx + Math.cos(-a * 0.45) * 0.27) * s, (jy + Math.sin(-a * 0.45) * 0.27) * s);
    ctx.lineTo((jx + Math.cos(a * 0.55) * 0.24) * s, (jy + Math.sin(a * 0.55) * 0.24) * s); ctx.closePath();
    ctx.fillStyle = '#8E2440'; ctx.fill();
  }
  hinge(a * 0.55, () => { // 아래턱
    oval(jx + 0.13, jy + 0.04, 0.15, 0.05, BEL, true);
    if (open > 0.04) { oval(jx + 0.09, jy + 0.005, 0.09, 0.022, '#E86A86'); for (const u of [0.1, 0.16]) tri([[jx + u, jy - 0.0], [jx + u + 0.012, jy - 0.035], [jx + u + 0.024, jy - 0.0]], '#fff'); tri([[jx + 0.22, jy - 0.0], [jx + 0.238, jy - 0.07], [jx + 0.256, jy - 0.0]], '#fff', true); }
  });
  hinge(-a * 0.45, () => { // 위턱 (코)
    ctx.beginPath(); ctx.moveTo((jx - 0.03) * s, (jy - 0.1) * s);
    ctx.quadraticCurveTo((jx + 0.15) * s, (jy - 0.15) * s, (jx + 0.29) * s, (jy - 0.08) * s);
    ctx.quadraticCurveTo((jx + 0.34) * s, (jy - 0.02) * s, (jx + 0.28) * s, (jy + 0.005) * s);
    ctx.lineTo((jx - 0.02) * s, (jy + 0.005) * s); ctx.closePath();
    ctx.fillStyle = BEL; ctx.fill(); ctx.lineWidth = sw; ctx.strokeStyle = OL; ctx.stroke();
    tri([[jx + 0.19, jy + 0.0], [jx + 0.208, jy + 0.075], [jx + 0.226, jy + 0.0]], '#fff', true); // 송곳니
    if (open > 0.04) for (const u of [0.08, 0.13]) tri([[jx + u, jy], [jx + u + 0.012, jy + 0.035], [jx + u + 0.024, jy]], '#fff');
    oval(jx + 0.3, jy - 0.065, 0.055, 0.042, INK, false, 0.3); // 코
    oval(jx + 0.288, jy - 0.08, 0.014, 0.01, 'rgba(255,255,255,.6)');
    ctx.beginPath(); ctx.moveTo((jx + 0.02) * s, (jy - 0.13) * s); ctx.quadraticCurveTo((jx + 0.1) * s, (jy - 0.15) * s, (jx + 0.2) * s, (jy - 0.115) * s); ctx.lineWidth = s * 0.03; ctx.strokeStyle = FUR; ctx.stroke(); // 콧등 회색
  });
  // 눈
  const ex = hx + hr * 0.25, ey = hy - hr * 0.15;
  if (bite) {
    ctx.beginPath(); ctx.moveTo((ex - 0.045) * s, (ey - 0.045) * s); ctx.lineTo((ex + 0.035) * s, ey * s); ctx.lineTo((ex - 0.045) * s, (ey + 0.045) * s);
    ctx.strokeStyle = INK; ctx.lineWidth = s * 0.028; ctx.stroke();
  } else {
    ctx.beginPath(); ctx.arc(ex * s, ey * s, hr * 0.27 * s, 0, 7); ctx.fillStyle = '#fff'; ctx.fill();
    ctx.beginPath(); ctx.arc((ex + hr * 0.06) * s, ey * s, hr * 0.19 * s, 0, 7); ctx.fillStyle = INK; ctx.fill();
    ctx.beginPath(); ctx.arc((ex + hr * 0.01) * s, (ey - hr * 0.07) * s, hr * 0.07 * s, 0, 7); ctx.fillStyle = '#fff'; ctx.fill();
  }
  oval(hx + hr * 0.0, hy + hr * 0.4, hr * 0.2, hr * 0.12, 'rgba(255,143,163,.55)'); // 볼
  ctx.restore();
}

export function newWolf(ctx, r, open, bite) {
  const FUR = '#9AA5B4', DARK = '#7D8899', BEL = '#FFF6E8', OL = 'rgba(0,0,0,.4)', INK = '#2B2B33', PINK = '#F6AFC0';
  const s = r, sw = Math.max(1.5, s * 0.02);
  ctx.save();
  ctx.rotate(-Math.PI / 2); // u(→)=앞(코), v(↓)=배 쪽
  ctx.lineJoin = 'round'; ctx.lineCap = 'round';
  const oval = (x, y, rx, ry, fill, stroke, rot = 0) => { ctx.beginPath(); ctx.ellipse(x * s, y * s, rx * s, ry * s, rot, 0, 7); ctx.fillStyle = fill; ctx.fill(); if (stroke) { ctx.lineWidth = sw; ctx.strokeStyle = OL; ctx.stroke(); } };
  const tri = (pts, fill, stroke) => { ctx.beginPath(); pts.forEach(([u, v], i) => (i ? ctx.lineTo(u * s, v * s) : ctx.moveTo(u * s, v * s))); ctx.closePath(); ctx.fillStyle = fill; ctx.fill(); if (stroke) { ctx.lineWidth = sw; ctx.strokeStyle = OL; ctx.stroke(); } };

  // 꼬리 (등 쪽으로 동그랗게 말린 복슬 꼬리, 끝은 흰색) — 주둥이랑 헷갈리지 않게
  const tail = new Path2D();
  tail.moveTo(-0.5 * s, 0.0 * s);
  tail.bezierCurveTo(-0.86 * s, 0.04 * s, -0.9 * s, -0.34 * s, -0.64 * s, -0.33 * s);
  tail.quadraticCurveTo(-0.5 * s, -0.31 * s, -0.55 * s, -0.19 * s);
  ctx.lineCap = 'round'; ctx.lineJoin = 'round';
  ctx.lineWidth = s * 0.15 + sw * 2; ctx.strokeStyle = OL; ctx.stroke(tail);
  ctx.lineWidth = s * 0.15; ctx.strokeStyle = FUR; ctx.stroke(tail);
  ctx.beginPath(); ctx.arc(-0.55 * s, -0.19 * s, s * 0.075, 0, 7); ctx.fillStyle = BEL; ctx.fill();
  // 뒷다리·앞다리 (엎드려 앞으로 뻗은 자세)
  for (const [u, v, len, dark] of [[-0.34, 0.12, 0.2, 1], [0.12, 0.12, 0.24, 1], [-0.26, 0.15, 0.2, 0], [0.2, 0.15, 0.26, 0]]) {
    ctx.globalAlpha = dark ? 0.75 : 1;
    ctx.strokeStyle = FUR; ctx.lineWidth = s * 0.1; ctx.beginPath(); ctx.moveTo(u * s, v * s); ctx.lineTo((u + len) * s, (v + 0.08) * s); ctx.stroke();
    ctx.globalAlpha = 1; oval(u + len + 0.02, v + 0.085, 0.06, 0.045, BEL, true);
  }
  // 몸통
  oval(-0.15, 0.0, 0.42, 0.19, FUR, true); oval(-0.12, 0.1, 0.28, 0.07, BEL);
  // 등 무늬
  oval(-0.2, -0.1, 0.25, 0.06, DARK);
  // 목 갈기
  tri([[0.05, -0.17], [0.1, -0.24], [0.15, -0.16], [0.2, -0.22], [0.2, 0.12], [0.08, 0.16], [0.12, 0.08], [0.02, 0.1]], FUR, true);

  // 머리
  const hx = 0.36, hy = -0.07, hr = 0.27;
  for (const ex of [hx - hr * 0.6, hx + hr * 0.15]) { // 귀 2개
    tri([[ex - hr * 0.35, hy - hr * 0.55], [ex - hr * 0.05, hy - hr * 1.5], [ex + hr * 0.38, hy - hr * 0.6]], FUR, true);
    tri([[ex - hr * 0.17, hy - hr * 0.68], [ex - hr * 0.04, hy - hr * 1.18], [ex + hr * 0.2, hy - hr * 0.7]], PINK);
  }
  ctx.beginPath(); ctx.arc(hx * s, hy * s, hr * s, 0, 7); ctx.fillStyle = FUR; ctx.fill(); ctx.lineWidth = sw; ctx.strokeStyle = OL; ctx.stroke();
  // 볼털 (뾰족)
  tri([[hx - hr * 0.5, hy + hr * 0.75], [hx - hr * 0.2, hy + hr * 1.25], [hx + hr * 0.05, hy + hr * 0.85], [hx + hr * 0.3, hy + hr * 1.15], [hx + hr * 0.45, hy + hr * 0.7]], FUR, true);

  // 주둥이: 위턱·아래턱 (경첩 = 주둥이 뒤쪽)
  const jx = hx + hr * 0.5, jy = hy + hr * 0.36, a = open * 0.75;
  const hinge = (ang, fn) => { ctx.save(); ctx.translate(jx * s, jy * s); ctx.rotate(ang); ctx.translate(-jx * s, -jy * s); fn(); ctx.restore(); };
  if (open > 0.04) { // 입 안
    ctx.beginPath(); ctx.moveTo(jx * s, jy * s);
    ctx.lineTo((jx + Math.cos(-a * 0.45) * 0.27) * s, (jy + Math.sin(-a * 0.45) * 0.27) * s);
    ctx.lineTo((jx + Math.cos(a * 0.55) * 0.24) * s, (jy + Math.sin(a * 0.55) * 0.24) * s); ctx.closePath();
    ctx.fillStyle = '#8E2440'; ctx.fill();
  }
  hinge(a * 0.55, () => { // 아래턱
    oval(jx + 0.13, jy + 0.04, 0.15, 0.05, BEL, true);
    if (open > 0.04) { oval(jx + 0.09, jy + 0.005, 0.09, 0.022, '#E86A86'); for (const u of [0.1, 0.16]) tri([[jx + u, jy - 0.0], [jx + u + 0.012, jy - 0.035], [jx + u + 0.024, jy - 0.0]], '#fff'); tri([[jx + 0.22, jy - 0.0], [jx + 0.238, jy - 0.07], [jx + 0.256, jy - 0.0]], '#fff', true); }
  });
  hinge(-a * 0.45, () => { // 위턱 (코)
    ctx.beginPath(); ctx.moveTo((jx - 0.03) * s, (jy - 0.1) * s);
    ctx.quadraticCurveTo((jx + 0.15) * s, (jy - 0.15) * s, (jx + 0.29) * s, (jy - 0.08) * s);
    ctx.quadraticCurveTo((jx + 0.34) * s, (jy - 0.02) * s, (jx + 0.28) * s, (jy + 0.005) * s);
    ctx.lineTo((jx - 0.02) * s, (jy + 0.005) * s); ctx.closePath();
    ctx.fillStyle = BEL; ctx.fill(); ctx.lineWidth = sw; ctx.strokeStyle = OL; ctx.stroke();
    tri([[jx + 0.19, jy + 0.0], [jx + 0.208, jy + 0.075], [jx + 0.226, jy + 0.0]], '#fff', true); // 송곳니
    if (open > 0.04) for (const u of [0.08, 0.13]) tri([[jx + u, jy], [jx + u + 0.012, jy + 0.035], [jx + u + 0.024, jy]], '#fff');
    oval(jx + 0.3, jy - 0.065, 0.055, 0.042, INK, false, 0.3); // 코
    oval(jx + 0.288, jy - 0.08, 0.014, 0.01, 'rgba(255,255,255,.6)');
    ctx.beginPath(); ctx.moveTo((jx + 0.02) * s, (jy - 0.13) * s); ctx.quadraticCurveTo((jx + 0.1) * s, (jy - 0.15) * s, (jx + 0.2) * s, (jy - 0.115) * s); ctx.lineWidth = s * 0.03; ctx.strokeStyle = FUR; ctx.stroke(); // 콧등 회색
  });
  // 눈
  const ex = hx + hr * 0.25, ey = hy - hr * 0.15;
  if (bite) {
    ctx.beginPath(); ctx.moveTo((ex - 0.045) * s, (ey - 0.045) * s); ctx.lineTo((ex + 0.035) * s, ey * s); ctx.lineTo((ex - 0.045) * s, (ey + 0.045) * s);
    ctx.strokeStyle = INK; ctx.lineWidth = s * 0.028; ctx.stroke();
  } else {
    ctx.beginPath(); ctx.arc(ex * s, ey * s, hr * 0.27 * s, 0, 7); ctx.fillStyle = '#fff'; ctx.fill();
    ctx.beginPath(); ctx.arc((ex + hr * 0.06) * s, ey * s, hr * 0.19 * s, 0, 7); ctx.fillStyle = INK; ctx.fill();
    ctx.beginPath(); ctx.arc((ex + hr * 0.01) * s, (ey - hr * 0.07) * s, hr * 0.07 * s, 0, 7); ctx.fillStyle = '#fff'; ctx.fill();
  }
  oval(hx + hr * 0.0, hy + hr * 0.4, hr * 0.2, hr * 0.12, 'rgba(255,143,163,.55)'); // 볼
  ctx.restore();
}
