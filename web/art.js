// 공용 그림: 귀여운 늑대(달리기·사다리·콱 물기·해적 두건), 동물 얼굴 12종, 폭탄
const INK = '#2B2B33';

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

/** 동물 12종 (이름은 문구 animalNames 순서와 같음) */
export const ANIMALS = ['rabbit', 'pig', 'chicken', 'sheep', 'deer', 'mouse', 'duck', 'cow', 'hamster', 'chick', 'dog', 'cat'];
export function animalFace(ctx, x, y, r, kind, st = 0) {
  const OL = 'rgba(0,0,0,.32)', INK = '#2B2B33', lw = Math.max(1.2, r * 0.06);
  ctx.save(); ctx.translate(x, y); ctx.lineJoin = 'round'; ctx.lineCap = 'round';
  const c = (u, v, rr, col, stroke = true) => { ctx.beginPath(); ctx.arc(u * r, v * r, rr * r, 0, 7); ctx.fillStyle = col; ctx.fill(); if (stroke) { ctx.lineWidth = lw; ctx.strokeStyle = OL; ctx.stroke(); } };
  const e = (u, v, rx, ry, col, stroke = false, rot = 0) => { ctx.beginPath(); ctx.ellipse(u * r, v * r, rx * r, ry * r, rot, 0, 7); ctx.fillStyle = col; ctx.fill(); if (stroke) { ctx.lineWidth = lw; ctx.strokeStyle = OL; ctx.stroke(); } };
  const tri = (pts, col, stroke = true) => { ctx.beginPath(); pts.forEach(([u, v], i) => (i ? ctx.lineTo(u * r, v * r) : ctx.moveTo(u * r, v * r))); ctx.closePath(); ctx.fillStyle = col; ctx.fill(); if (stroke) { ctx.lineWidth = lw; ctx.strokeStyle = OL; ctx.stroke(); } };
  let head = '#F4F1EC', eyeY = -0.08, cheek = 'rgba(255,140,160,.55)';
  // 귀·뒤쪽 장식
  switch (kind) {
    case 'rabbit': for (const s of [-1, 1]) { e(s * 0.32, -1.05, 0.2, 0.55, head, true, s * 0.15); e(s * 0.32, -1.0, 0.1, 0.4, '#F6AFC0', false, s * 0.15); } break;
    case 'pig': head = '#F7B3C2'; for (const s of [-1, 1]) tri([[s * 0.35, -0.75], [s * 0.85, -0.95], [s * 0.75, -0.4]], '#EE97AA'); break;
    case 'chicken': for (const k of [-0.25, 0, 0.25]) c(k, -1.0 + Math.abs(k) * 0.3, 0.2, '#E53935'); break;
    case 'sheep': head = '#F3E3C8'; for (let k = 0; k < 9; k++) { const a = Math.PI * (0.95 + k * 0.14); c(Math.cos(a) * 0.82, Math.sin(a) * 0.72 - 0.15, 0.3, '#FFFFFF'); } for (const s of [-1, 1]) e(s * 0.95, -0.1, 0.25, 0.12, '#5E4B3C', true, s * 0.3); break;
    case 'deer': head = '#C98A55';
      ctx.strokeStyle = '#7A4E2D'; ctx.lineWidth = r * 0.1; for (const s of [-1, 1]) { ctx.beginPath(); ctx.moveTo(s * 0.3 * r, -0.8 * r); ctx.lineTo(s * 0.45 * r, -1.35 * r); ctx.moveTo(s * 0.38 * r, -1.05 * r); ctx.lineTo(s * 0.7 * r, -1.2 * r); ctx.moveTo(s * 0.43 * r, -1.25 * r); ctx.lineTo(s * 0.25 * r, -1.45 * r); ctx.stroke(); }
      for (const s of [-1, 1]) { e(s * 0.85, -0.45, 0.35, 0.16, head, true, s * -0.5); e(s * 0.85, -0.45, 0.2, 0.08, '#F2C6A0', false, s * -0.5); } break;
    case 'mouse': head = '#BCC1CA'; for (const s of [-1, 1]) { c(s * 0.7, -0.7, 0.45, head); c(s * 0.7, -0.7, 0.28, '#F6AFC0', false); } break;
    case 'duck': head = '#FFFFFF'; c(0, -0.95, 0.12, '#FFFFFF'); break;
    case 'cow': head = '#FFFFFF'; for (const s of [-1, 1]) { tri([[s * 0.45, -0.75], [s * 0.7, -1.15], [s * 0.6, -0.7]], '#F2E3B3'); e(s * 0.95, -0.4, 0.3, 0.14, '#FFFFFF', true, s * -0.35); e(s * 0.95, -0.4, 0.16, 0.07, '#F6AFC0', false, s * -0.35); } break;
    case 'hamster': head = '#EBA762'; for (const s of [-1, 1]) { c(s * 0.62, -0.72, 0.25, head); c(s * 0.62, -0.72, 0.13, '#F6AFC0', false); } break;
    case 'dog': head = '#E3B07A'; for (const s of [-1, 1]) e(s * 0.82, -0.2, 0.26, 0.5, '#9A6A43', true, s * -0.35); break;
    case 'cat': head = '#F3A451'; for (const s of [-1, 1]) { tri([[s * 0.25, -0.8], [s * 0.78, -1.15], [s * 0.85, -0.45]], head); tri([[s * 0.4, -0.78], [s * 0.72, -0.98], [s * 0.75, -0.6]], '#F6AFC0', false); } break;
    case 'chick': head = '#FFD54A'; ctx.strokeStyle = '#E6A800'; ctx.lineWidth = r * 0.08; for (const k of [-0.12, 0.05, 0.18]) { ctx.beginPath(); ctx.moveTo(0, -0.9 * r); ctx.quadraticCurveTo(k * r, -1.2 * r, k * 1.6 * r, -1.25 * r); ctx.stroke(); } break;
  }
  // 얼굴
  c(0, 0, 0.95, head);
  if (kind === 'cow') { e(-0.45, -0.45, 0.28, 0.22, '#2B2B33', false, 0.4); e(0.55, 0.05, 0.2, 0.16, '#2B2B33', false, -0.3); }
  if (kind === 'hamster') { e(0, 0.35, 0.62, 0.45, '#FFF6EA'); }
  if (kind === 'deer') { e(0, 0.35, 0.5, 0.4, '#F2D3B0'); for (const [u, v] of [[-0.5, -0.5], [0.45, -0.6], [0.6, -0.2]]) c(u, v, 0.07, '#F2D3B0', false); }
  if (kind === 'dog') { e(0, 0.32, 0.48, 0.36, '#FFF1DE'); e(0.42, -0.4, 0.2, 0.17, '#B98557', false, 0.3); }
  if (kind === 'cat') { for (const k of [-0.18, 0, 0.18]) { ctx.strokeStyle = '#C9712A'; ctx.lineWidth = r * 0.08; ctx.beginPath(); ctx.moveTo(k * r, -0.92 * r); ctx.lineTo(k * r * 0.8, -0.62 * r); ctx.stroke(); } e(0, 0.33, 0.42, 0.28, '#FFF1DE'); }
  if (kind === 'sheep') { for (let k = 0; k < 5; k++) c(-0.5 + k * 0.25, -0.75, 0.2, '#FFFFFF'); }
  // 눈
  for (const s of [-1, 1]) {
    const ex = s * 0.36, ey = eyeY;
    if (st === 2) { // 물림: X 눈
      ctx.strokeStyle = INK; ctx.lineWidth = r * 0.1; ctx.beginPath(); ctx.moveTo((ex - 0.12) * r, (ey - 0.12) * r); ctx.lineTo((ex + 0.12) * r, (ey + 0.12) * r); ctx.moveTo((ex + 0.12) * r, (ey - 0.12) * r); ctx.lineTo((ex - 0.12) * r, (ey + 0.12) * r); ctx.stroke();
    } else if (st === 1) { // 겁먹음: 하얀 큰 눈 + 작은 동공
      c(ex, ey, 0.2, '#fff'); c(ex + s * 0.03, ey + 0.02, 0.07, INK, false);
    } else { c(ex, ey, 0.13, INK, false); c(ex - 0.04, ey - 0.05, 0.045, '#fff', false); }
  }
  // 볼
  if (!['sheep'].includes(kind)) for (const s of [-1, 1]) e(s * 0.58, 0.22, 0.17, 0.09, cheek);
  // 코·입·부리
  switch (kind) {
    case 'pig': e(0, 0.3, 0.32, 0.22, '#EE97AA', true); e(-0.11, 0.3, 0.06, 0.09, '#B5566E'); e(0.11, 0.3, 0.06, 0.09, '#B5566E'); break;
    case 'cow': e(0, 0.45, 0.48, 0.3, '#F6B8C6', true); e(-0.17, 0.45, 0.07, 0.1, '#B5566E'); e(0.17, 0.45, 0.07, 0.1, '#B5566E'); break;
    case 'chicken': tri([[-0.16, 0.12], [0.16, 0.12], [0, 0.42]], '#FFB300'); e(0, 0.55, 0.1, 0.16, '#E53935'); break;
    case 'duck': e(0, 0.32, 0.4, 0.18, '#FF9800', true); ctx.strokeStyle = 'rgba(0,0,0,.3)'; ctx.lineWidth = lw; ctx.beginPath(); ctx.moveTo(-0.35 * r, 0.32 * r); ctx.lineTo(0.35 * r, 0.32 * r); ctx.stroke(); break;
    case 'chick': tri([[-0.14, 0.12], [0.14, 0.12], [0, 0.36]], '#FF9800'); break;
    case 'deer': e(0, 0.2, 0.14, 0.1, INK); break;
    case 'dog': e(0, 0.2, 0.16, 0.11, INK); ctx.strokeStyle = INK; ctx.lineWidth = r * 0.06; ctx.beginPath(); ctx.moveTo(0, 0.3 * r); ctx.quadraticCurveTo(-0.1 * r, 0.44 * r, -0.22 * r, 0.38 * r); ctx.moveTo(0, 0.3 * r); ctx.quadraticCurveTo(0.1 * r, 0.44 * r, 0.22 * r, 0.38 * r); ctx.stroke(); if (st === 0) e(0.08, 0.5, 0.09, 0.12, '#FF7F98'); break;
    default: { // 토끼·쥐·햄스터·양
      tri([[-0.1, 0.13], [0.1, 0.13], [0, 0.25]], kind === 'sheep' ? '#7A5A48' : '#F48FA5', false);
      ctx.strokeStyle = INK; ctx.lineWidth = r * 0.06; ctx.beginPath(); ctx.moveTo(0, 0.25 * r); ctx.quadraticCurveTo(-0.08 * r, 0.38 * r, -0.18 * r, 0.33 * r); ctx.moveTo(0, 0.25 * r); ctx.quadraticCurveTo(0.08 * r, 0.38 * r, 0.18 * r, 0.33 * r); ctx.stroke();
      if (kind !== 'sheep') { ctx.strokeStyle = 'rgba(0,0,0,.35)'; ctx.lineWidth = r * 0.03; for (const s of [-1, 1]) for (const dy of [-0.04, 0.06]) { ctx.beginPath(); ctx.moveTo(s * 0.3 * r, (0.22 + dy) * r); ctx.lineTo(s * 0.75 * r, (0.18 + dy * 1.8) * r); ctx.stroke(); } }
    }
  }
  if (st >= 1 && !['pig', 'cow', 'chicken', 'duck', 'chick'].includes(kind)) { /* 입 위에 겁먹은 O */ }
  if (st === 1) { const my = ['pig', 'cow'].includes(kind) ? 0.82 : ['chicken', 'duck'].includes(kind) ? 0.7 : 0.6; if (kind !== 'chicken' && kind !== 'duck') e(0, my, 0.1, 0.12, '#8E2440'); }
  ctx.restore();
}

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

/** 폭탄 (fuse: 남은 심지 0~1, t: 초) */
export function bombArt(ctx, x, y, r, fuse, t) {
  // 몸통
  ctx.save(); ctx.translate(x, y);
  ctx.beginPath(); ctx.arc(0, 0, r, 0, 7); const g = ctx.createRadialGradient(-r * 0.35, -r * 0.35, r * 0.1, 0, 0, r); g.addColorStop(0, '#5A5F6B'); g.addColorStop(1, '#16181D'); ctx.fillStyle = g; ctx.fill();
  ctx.beginPath(); ctx.ellipse(-r * 0.35, -r * 0.4, r * 0.22, r * 0.12, -0.6, 0, 7); ctx.fillStyle = 'rgba(255,255,255,.35)'; ctx.fill();
  ctx.fillStyle = '#3A3E47'; ctx.fillRect(-r * 0.25, -r * 1.15, r * 0.5, r * 0.3);
  // 심지 (남은 길이 = fuse 0~1)
  const pts = []; for (let k = 0; k <= 20; k++) { const u = k / 20; pts.push([r * 0.05 + Math.sin(u * 5) * r * 0.25 + u * r * 0.5, -r * 1.15 - u * r * 0.9]); }
  const m = Math.max(1, Math.round(fuse * 20));
  ctx.beginPath(); pts.slice(0, m + 1).forEach(([px, py], k) => (k ? ctx.lineTo(px, py) : ctx.moveTo(px, py))); ctx.strokeStyle = '#C8A26A'; ctx.lineWidth = r * 0.12; ctx.lineCap = 'round'; ctx.stroke();
  // 불꽃
  const [fx, fy] = pts[m];
  for (let k = 0; k < 8; k++) { const a = k * Math.PI / 4 + t * 20, l = r * (0.25 + 0.2 * Math.abs(Math.sin(t * 30 + k))); ctx.beginPath(); ctx.moveTo(fx, fy); ctx.lineTo(fx + Math.cos(a) * l, fy + Math.sin(a) * l); ctx.strokeStyle = k % 2 ? '#FFD54F' : '#FF7043'; ctx.lineWidth = r * 0.07; ctx.stroke(); }
  ctx.beginPath(); ctx.arc(fx, fy, r * 0.14 * (1 + 0.3 * Math.sin(t * 40)), 0, 7); ctx.fillStyle = '#FFF59D'; ctx.fill();
  ctx.restore();
}

export function chompWolf(ctx, x, y, r, bite, pop = 1) {
  const FUR = '#9AA5B4', DARK = '#6E7A8A', CREAM = '#F4ECE1', INK = '#2B2B33';
  ctx.save(); ctx.translate(x, y); ctx.scale(pop, pop);
  for (const s of [-1, 1]) { ctx.beginPath(); ctx.moveTo(s * r * 0.25, -r * 0.7); ctx.lineTo(s * r * 0.85, -r * 1.35); ctx.lineTo(s * r * 0.95, -r * 0.35); ctx.closePath(); ctx.fillStyle = DARK; ctx.fill(); ctx.beginPath(); ctx.moveTo(s * r * 0.45, -r * 0.68); ctx.lineTo(s * r * 0.8, -r * 1.1); ctx.lineTo(s * r * 0.85, -r * 0.5); ctx.closePath(); ctx.fillStyle = '#F6AFC0'; ctx.fill(); }
  ctx.beginPath(); ctx.arc(0, 0, r, 0, 7); ctx.fillStyle = FUR; ctx.fill();
  // 화난 눈썹 + 눈
  for (const s of [-1, 1]) { ctx.beginPath(); ctx.arc(s * r * 0.4, -r * 0.2, r * 0.14, 0, 7); ctx.fillStyle = INK; ctx.fill(); ctx.beginPath(); ctx.arc(s * r * 0.36, -r * 0.25, r * 0.05, 0, 7); ctx.fillStyle = '#fff'; ctx.fill();
    ctx.beginPath(); ctx.moveTo(s * r * 0.6, -r * 0.48); ctx.lineTo(s * r * 0.2, -r * 0.36); ctx.strokeStyle = INK; ctx.lineWidth = r * 0.08; ctx.lineCap = 'round'; ctx.stroke(); }
  // 입 (위턱 / 아래턱)
  const o = r * 0.55 * bite;
  ctx.beginPath(); ctx.ellipse(0, r * 0.45, r * 0.62, r * 0.12 + o * 0.7, 0, 0, 7); ctx.fillStyle = '#7A1E2E'; ctx.fill();
  ctx.fillStyle = '#fff';
  for (let k = -3; k <= 3; k++) { const x0 = k * r * 0.16; ctx.beginPath(); ctx.moveTo(x0 - r * 0.07, r * 0.45 - r * 0.1 - o * 0.6); ctx.lineTo(x0 + r * 0.07, r * 0.45 - r * 0.1 - o * 0.6); ctx.lineTo(x0, r * 0.45 + r * 0.06 - o * 0.6); ctx.fill(); ctx.beginPath(); ctx.moveTo(x0 - r * 0.07, r * 0.45 + r * 0.1 + o * 0.6); ctx.lineTo(x0 + r * 0.07, r * 0.45 + r * 0.1 + o * 0.6); ctx.lineTo(x0, r * 0.45 - r * 0.06 + o * 0.6); ctx.fill(); }
  ctx.beginPath(); ctx.ellipse(0, r * 0.12 - o * 0.25, r * 0.17, r * 0.11, 0, 0, 7); ctx.fillStyle = INK; ctx.fill();
  ctx.restore();
}

// 해적 두건 늑대 머리. mode: 'idle' | 'tease'(t초) | 'sur'(당첨으로 튀어나옴)
export function pirateHead2(ctx, cx, cy, r, mode, t = 0) {
  const tease = mode === 'tease', sur = mode === 'sur';
  for (const sx of [-1, 1]) {
    const e = tease ? Math.sin(t * 14 + (sx > 0 ? 0 : 3)) * 0.06 : 0;
    ctx.save(); ctx.translate(cx + sx * r * 0.6, cy - r * 0.85); ctx.rotate(sx * e); ctx.translate(-(cx + sx * r * 0.6), -(cy - r * 0.85));
    ctx.beginPath(); ctx.moveTo(cx + sx * r * 0.25, cy - r * 0.7); ctx.lineTo(cx + sx * r * 0.85, cy - r * 1.35); ctx.lineTo(cx + sx * r * 0.95, cy - r * 0.35); ctx.closePath(); ctx.fillStyle = '#6E7A8A'; ctx.fill();
    ctx.beginPath(); ctx.moveTo(cx + sx * r * 0.45, cy - r * 0.68); ctx.lineTo(cx + sx * r * 0.8, cy - r * 1.1); ctx.lineTo(cx + sx * r * 0.85, cy - r * 0.5); ctx.closePath(); ctx.fillStyle = '#F6AFC0'; ctx.fill();
    ctx.restore();
  }
  ctx.beginPath(); ctx.arc(cx, cy, r, 0, 7); ctx.fillStyle = '#9AA5B4'; ctx.fill();
  // 두건
  ctx.beginPath(); ctx.moveTo(cx - r * 0.97, cy - r * 0.25); ctx.quadraticCurveTo(cx, cy - r * 1.25, cx + r * 0.97, cy - r * 0.25); ctx.quadraticCurveTo(cx, cy - r * 0.75, cx - r * 0.97, cy - r * 0.25); ctx.fillStyle = '#E53935'; ctx.fill();
  for (const k of [-0.45, 0, 0.45]) { ctx.beginPath(); ctx.arc(cx + k * r, cy - r * 0.68 + Math.abs(k) * r * 0.25, r * 0.06, 0, 7); ctx.fillStyle = '#fff'; ctx.fill(); }
  const tail = tease ? Math.sin(t * 10) * 0.15 : 0;
  ctx.beginPath(); ctx.arc(cx + r * 0.95, cy - r * 0.3, r * 0.13, 0, 7); ctx.fillStyle = '#E53935'; ctx.fill();
  ctx.beginPath(); ctx.moveTo(cx + r * 0.95, cy - r * 0.3); ctx.lineTo(cx + r * (1.25 + tail * 0.3), cy - r * (0.05 - tail)); ctx.strokeStyle = '#E53935'; ctx.lineWidth = r * 0.12; ctx.lineCap = 'round'; ctx.stroke();
  // 주둥이
  ctx.beginPath(); ctx.ellipse(cx, cy + r * 0.44, r * 0.48, r * 0.36, 0, 0, 7); ctx.fillStyle = '#F4ECE1'; ctx.fill();
  // 눈
  const ey = cy - r * 0.08;
  for (const sx of [-1, 1]) {
    const ex = cx + sx * r * 0.42;
    if (sur || (tease && sx < 0)) { // 찡긋 / 놀람
      const d = r * 0.13; ctx.beginPath(); ctx.moveTo(ex - d * sx, ey - d); ctx.lineTo(ex + d * sx, ey); ctx.lineTo(ex - d * sx, ey + d); ctx.strokeStyle = INK; ctx.lineWidth = r * 0.08; ctx.lineCap = 'round'; ctx.lineJoin = 'round'; ctx.stroke();
    } else {
      ctx.beginPath(); ctx.arc(ex, ey, r * 0.15, 0, 7); ctx.fillStyle = INK; ctx.fill();
      ctx.beginPath(); ctx.arc(ex - r * 0.04, ey - r * 0.05, r * 0.06, 0, 7); ctx.fillStyle = '#fff'; ctx.fill();
      if (tease) { ctx.beginPath(); ctx.moveTo(ex - r * 0.16, ey - r * 0.3); ctx.quadraticCurveTo(ex, ey - r * 0.4, ex + r * 0.17, ey - r * 0.26); ctx.strokeStyle = INK; ctx.lineWidth = r * 0.06; ctx.stroke(); }
    }
    ctx.beginPath(); ctx.ellipse(ex + sx * r * 0.08, ey + r * 0.3, r * 0.2, r * 0.1, 0, 0, 7); ctx.fillStyle = tease ? 'rgba(255,120,150,.55)' : 'rgba(255,143,163,.4)'; ctx.fill();
  }
  ctx.beginPath(); ctx.ellipse(cx, cy + r * 0.245, r * 0.13, r * 0.085, 0, 0, 7); ctx.fillStyle = INK; ctx.fill();
  if (sur) { ctx.beginPath(); ctx.arc(cx, cy + r * 0.54, r * 0.12, 0, 7); ctx.fillStyle = '#8E2440'; ctx.fill(); }
  else {
    ctx.beginPath(); ctx.moveTo(cx - r * 0.2, cy + r * 0.45); ctx.quadraticCurveTo(cx, cy + r * 0.6, cx + r * 0.2, cy + r * 0.45); ctx.strokeStyle = INK; ctx.lineWidth = r * 0.05; ctx.stroke();
  }
  if (tease) { // 혀 메롱 (날름날름)
    const wag = Math.sin(t * 16) * 0.3, L = r * 0.5 * (0.85 + Math.sin(t * 8) * 0.15), W = r * 0.18;
    ctx.save(); ctx.translate(cx, cy + r * 0.52); ctx.rotate(wag);
    ctx.beginPath(); ctx.moveTo(-W, 0); ctx.lineTo(-W, L * 0.6); ctx.quadraticCurveTo(-W, L + W * 0.4, 0, L + W * 0.4); ctx.quadraticCurveTo(W, L + W * 0.4, W, L * 0.6); ctx.lineTo(W, 0); ctx.closePath();
    ctx.fillStyle = '#FF6F91'; ctx.fill(); ctx.lineWidth = r * 0.03; ctx.strokeStyle = '#C2405F'; ctx.stroke();
    ctx.beginPath(); ctx.moveTo(0, L * 0.15); ctx.lineTo(0, L * 0.7); ctx.strokeStyle = '#D9506F'; ctx.lineWidth = r * 0.03; ctx.stroke();
    ctx.restore();
  }
}

// ===================== v1.5 =====================
// 귀여운 늑대 앞발 (위에서 본 모양). palm=true 발바닥(젤리) / false 발등(털·발톱). 발끝은 위(-y) 방향
const PFUR = '#9AA3AF', PFUR_D = '#7C8593', PFUR_L = '#C9CFD8', PAD = '#F6AFC0', PAD_D = '#E88BA3', POL = '#4E5561';
export function wolfPaw(ctx, x, y, s, band, flip, palm, red = 0) {
  ctx.save(); ctx.translate(x, y); if (flip) ctx.rotate(Math.PI); ctx.scale(s, s);
  ctx.lineCap = 'round'; ctx.lineJoin = 'round';
  const TOES = [[-32, -50, 15], [-12, -66, 16], [12, -66, 16], [32, -50, 15]];
  // 발 모양: 동그란 발 + 발가락 4개 + 털 많은 팔목
  const body = () => {
    ctx.moveTo(-40, -40); ctx.bezierCurveTo(-58, -8, -50, 30, -34, 50);
    // 팔목 바깥쪽 복슬 털
    ctx.lineTo(-36, 64); ctx.lineTo(-30, 70); ctx.lineTo(-34, 82); ctx.lineTo(-28, 100); ctx.lineTo(28, 100); ctx.lineTo(34, 82); ctx.lineTo(30, 70); ctx.lineTo(36, 64); ctx.lineTo(34, 50);
    ctx.bezierCurveTo(50, 30, 58, -8, 40, -40); ctx.bezierCurveTo(26, -62, -26, -62, -40, -40); ctx.closePath();
  };
  const parts = [body, ...TOES.map(([tx, ty, r]) => () => { ctx.ellipse(tx, ty, r, r * 1.08, 0, 0, 7); })];
  const each = (fn) => { for (const pt of parts) { ctx.beginPath(); pt(); fn(); } };
  ctx.save(); ctx.translate(4, 8); ctx.globalAlpha = 0.25; ctx.fillStyle = '#000'; each(() => ctx.fill()); ctx.restore();
  ctx.lineWidth = 4.5; ctx.strokeStyle = POL; each(() => ctx.stroke());
  const g = ctx.createRadialGradient(-10, -20, 8, 0, 0, 105);
  if (palm) { g.addColorStop(0, '#E9ECF0'); g.addColorStop(1, '#B4BBC6'); } else { g.addColorStop(0, PFUR_L); g.addColorStop(1, PFUR_D); }
  ctx.fillStyle = g; each(() => ctx.fill());
  if (!palm) {
    // 발등: 발가락 사이 선 + 털결 + 하얀 발톱 살짝
    ctx.strokeStyle = 'rgba(78,85,97,.55)'; ctx.lineWidth = 2.4;
    for (const xx of [-22, 0, 22]) { const y0 = xx ? -48 : -60; ctx.beginPath(); ctx.moveTo(xx, y0); ctx.lineTo(xx * 0.95, y0 + 12); ctx.stroke(); }
    ctx.strokeStyle = 'rgba(255,255,255,.45)'; ctx.lineWidth = 2;
    for (const [fx, fy] of [[-18, -10], [4, -4], [22, -14], [-8, 22], [14, 30], [-20, 44], [6, 58], [-4, 80]]) { ctx.beginPath(); ctx.moveTo(fx - 4, fy + 5); ctx.quadraticCurveTo(fx, fy - 2, fx + 4, fy + 5); ctx.stroke(); }
    for (const [tx, ty, r] of TOES) { ctx.beginPath(); ctx.moveTo(tx - 3.5, ty - r + 2); ctx.quadraticCurveTo(tx, ty - r - 9, tx + 3.5, ty - r + 2); ctx.closePath(); ctx.fillStyle = '#FFFFFF'; ctx.fill(); ctx.lineWidth = 1.5; ctx.strokeStyle = POL; ctx.stroke(); }
  } else {
    // 발바닥 젤리: 큰 패드 + 발가락 젤리 4개
    const pad = (fn) => { ctx.beginPath(); fn(); const pg = ctx.createLinearGradient(0, -90, 0, 40); pg.addColorStop(0, PAD); pg.addColorStop(1, PAD_D); ctx.fillStyle = pg; ctx.fill(); ctx.lineWidth = 2.5; ctx.strokeStyle = '#C96F89'; ctx.stroke(); };
    pad(() => { ctx.moveTo(0, -30); ctx.bezierCurveTo(16, -40, 36, -26, 32, -4); ctx.bezierCurveTo(30, 16, 14, 22, 0, 18); ctx.bezierCurveTo(-14, 22, -30, 16, -32, -4); ctx.bezierCurveTo(-36, -26, -16, -40, 0, -30); });
    for (const [tx, ty, r] of TOES) pad(() => ctx.ellipse(tx, ty - 2, r * 0.62, r * 0.72, 0, 0, 7));
    ctx.fillStyle = 'rgba(255,255,255,.6)';
    for (const [tx, ty, r] of TOES) { ctx.beginPath(); ctx.ellipse(tx - r * 0.2, ty - r * 0.35, r * 0.17, r * 0.24, -0.4, 0, 7); ctx.fill(); }
    ctx.beginPath(); ctx.ellipse(-12, -18, 7, 5, -0.5, 0, 7); ctx.fill();
  }
  // 맞은 자국 (빨갛게 + 털 쭈뼛)
  if (red > 0) { ctx.save(); ctx.beginPath(); body(); ctx.clip(); ctx.globalAlpha = red * 0.85; const rg = ctx.createRadialGradient(0, -14, 4, 0, -14, 52); rg.addColorStop(0, '#FF3D57'); rg.addColorStop(1, 'rgba(255,61,87,0)'); ctx.fillStyle = rg; ctx.fillRect(-80, -130, 160, 200); ctx.restore(); }
  // 팔목 밴드 (플레이어 색)
  ctx.beginPath(); ctx.roundRect(-33, 84, 66, 16, 8); ctx.fillStyle = band; ctx.fill(); ctx.lineWidth = 3; ctx.strokeStyle = 'rgba(0,0,0,.35)'; ctx.stroke();
  ctx.beginPath(); ctx.roundRect(-26, 87, 22, 5, 3); ctx.fillStyle = 'rgba(255,255,255,.45)'; ctx.fill();
  // 팔 (화면 밖으로)
  ctx.beginPath(); ctx.moveTo(-28, 100); ctx.lineTo(-30, 160); ctx.lineTo(30, 160); ctx.lineTo(28, 100); ctx.closePath(); ctx.fillStyle = palm ? '#B4BBC6' : PFUR_D; ctx.fill(); ctx.lineWidth = 4.5; ctx.strokeStyle = POL; ctx.stroke();
  ctx.restore();
}

const CGOLD = '#FFC94A', CGOLD_D = '#E0A021', CGOLD_DD = '#A86E0C', CFUR = '#9AA3AF', CFUR_D = '#7C8593', CCREAM = '#F1EEE8', CINK = '#2B2B33', CPINK = '#F6AFC0';
// ----- 동전 앞면: 귀여운 늑대 얼굴 -----
function wolfFace(c, r) {
  for (const s of [-1, 1]) { // 귀
    c.beginPath(); c.moveTo(s * r * 0.2, -r * 0.42); c.lineTo(s * r * 0.62, -r * 0.92); c.lineTo(s * r * 0.72, -r * 0.22); c.closePath(); c.fillStyle = CFUR_D; c.fill();
    c.beginPath(); c.moveTo(s * r * 0.34, -r * 0.42); c.lineTo(s * r * 0.58, -r * 0.74); c.lineTo(s * r * 0.62, -r * 0.32); c.closePath(); c.fillStyle = CPINK; c.fill();
  }
  c.beginPath(); c.ellipse(0, r * 0.02, r * 0.66, r * 0.58, 0, 0, 7); c.fillStyle = CFUR; c.fill(); // 머리
  for (const s of [-1, 1]) { c.beginPath(); c.moveTo(s * r * 0.62, r * 0.05); c.lineTo(s * r * 0.8, r * 0.18); c.lineTo(s * r * 0.6, r * 0.28); c.closePath(); c.fillStyle = CFUR; c.fill(); } // 볼털
  c.beginPath(); c.ellipse(0, r * 0.24, r * 0.34, r * 0.26, 0, 0, 7); c.fillStyle = CCREAM; c.fill(); // 주둥이
  for (const s of [-1, 1]) { c.beginPath(); c.ellipse(s * r * 0.26, -r * 0.08, r * 0.11, r * 0.13, 0, 0, 7); c.fillStyle = CINK; c.fill();
    c.beginPath(); c.arc(s * r * 0.23, -r * 0.13, r * 0.045, 0, 7); c.fillStyle = '#fff'; c.fill();
    c.beginPath(); c.ellipse(s * r * 0.44, r * 0.16, r * 0.1, r * 0.06, 0, 0, 7); c.fillStyle = 'rgba(255,130,150,.55)'; c.fill(); }
  c.beginPath(); c.ellipse(0, r * 0.13, r * 0.09, r * 0.065, 0, 0, 7); c.fillStyle = CINK; c.fill(); // 코
  c.beginPath(); c.moveTo(-r * 0.12, r * 0.28); c.quadraticCurveTo(-r * 0.06, r * 0.36, 0, r * 0.28); c.quadraticCurveTo(r * 0.06, r * 0.36, r * 0.12, r * 0.28); c.strokeStyle = CINK; c.lineWidth = r * 0.04; c.lineCap = 'round'; c.stroke();
  c.beginPath(); c.ellipse(0, r * 0.36, r * 0.05, r * 0.06, 0, 0, Math.PI); c.fillStyle = '#F48FB1'; c.fill(); // 혀
}
// ----- 동전 뒷면: 늑대 엉덩이 -----
function wolfButt(c, r) {
  // 꼬리 (위로 말린 복슬)
  c.save(); c.beginPath(); c.moveTo(-r * 0.08, -r * 0.18); c.bezierCurveTo(-r * 0.3, -r * 0.7, r * 0.3, -r * 0.95, r * 0.34, -r * 0.52); c.bezierCurveTo(r * 0.36, -r * 0.3, r * 0.14, -r * 0.24, r * 0.1, -r * 0.16); c.closePath(); c.fillStyle = CFUR_D; c.fill();
  c.beginPath(); c.ellipse(r * 0.24, -r * 0.62, r * 0.13, r * 0.11, 0.5, 0, 7); c.fillStyle = CCREAM; c.fill(); c.restore();
  // 뒷다리 + 발바닥
  for (const s of [-1, 1]) { c.beginPath(); c.ellipse(s * r * 0.3, r * 0.5, r * 0.16, r * 0.12, 0, 0, 7); c.fillStyle = CFUR_D; c.fill();
    c.beginPath(); c.ellipse(s * r * 0.3, r * 0.52, r * 0.08, r * 0.06, 0, 0, 7); c.fillStyle = CPINK; c.fill(); }
  // 엉덩이 두 쪽
  for (const s of [-1, 1]) { c.beginPath(); c.arc(s * r * 0.24, r * 0.12, r * 0.34, 0, 7); c.fillStyle = CFUR; c.fill(); }
  c.beginPath(); c.moveTo(0, -r * 0.1); c.quadraticCurveTo(r * 0.03, r * 0.15, 0, r * 0.4); c.strokeStyle = 'rgba(60,66,78,.6)'; c.lineWidth = r * 0.04; c.lineCap = 'round'; c.stroke();
  for (const s of [-1, 1]) { c.beginPath(); c.ellipse(s * r * 0.3, r * 0.02, r * 0.1, r * 0.06, s * 0.4, 0, 7); c.fillStyle = 'rgba(255,255,255,.35)'; c.fill(); // 반짝
    c.beginPath(); c.ellipse(s * r * 0.36, r * 0.24, r * 0.09, r * 0.05, 0, 0, 7); c.fillStyle = 'rgba(255,130,150,.45)'; c.fill(); } // 볼터치(?)
}
// 동전 (face: 1 앞, -1 뒤). sy: 세로 눌림(뒤집히는 중), 크기 r
// 코인 색 (금색 기본 · 삼목용 초록/빨강)
export const COIN_PAL = { gold: ['#FFE9A6', CGOLD, CGOLD_D, CGOLD_DD, 'rgba(168,110,12,.55)'], green: ['#C8F7C5', '#4CD964', '#23A33E', '#146B27', 'rgba(20,107,39,.55)'], red: ['#FFC9C9', '#FF5A5A', '#D32F2F', '#8E1414', 'rgba(142,20,20,.55)'] };
export function coin(c, x, y, r, face, sy = 1, pal = 'gold') {
  const [P0, P1, P2, P3, P4] = COIN_PAL[pal] || COIN_PAL.gold;
  c.save(); c.translate(x, y); c.scale(1, Math.max(0.04, sy));
  c.beginPath(); c.arc(0, r * 0.07 / Math.max(0.2, sy), r, 0, 7); c.fillStyle = P3; c.fill(); // 두께
  const g = c.createRadialGradient(-r * 0.3, -r * 0.35, r * 0.1, 0, 0, r); g.addColorStop(0, P0); g.addColorStop(0.6, P1); g.addColorStop(1, P2);
  c.beginPath(); c.arc(0, 0, r, 0, 7); c.fillStyle = g; c.fill(); c.lineWidth = r * 0.06; c.strokeStyle = P3; c.stroke();
  c.beginPath(); c.arc(0, 0, r * 0.84, 0, 7); c.lineWidth = r * 0.035; c.strokeStyle = P4; c.stroke();
  for (let k = 0; k < 24; k++) { const a = k / 24 * Math.PI * 2; c.beginPath(); c.arc(Math.cos(a) * r * 0.92, Math.sin(a) * r * 0.92, r * 0.025, 0, 7); c.fillStyle = P4; c.fill(); }
  c.save(); c.beginPath(); c.arc(0, 0, r * 0.8, 0, 7); c.clip(); c.translate(0, r * 0.02); if (face > 0) wolfFace(c, r * 0.95); else wolfButt(c, r * 0.95); c.restore();
  c.beginPath(); c.ellipse(-r * 0.45, -r * 0.5, r * 0.22, r * 0.09, -0.7, 0, 7); c.fillStyle = 'rgba(255,255,255,.35)'; c.fill();
  c.restore();
}
// 옆에서 본 동전 (세로로 선 모습)
export function coinEdge(c, x, y, r, wob) {
  c.save(); c.translate(x, y); c.rotate(wob);
  const w = r * 0.26, h = r * 2;
  c.beginPath(); c.roundRect(-w / 2, -h, w, h, w * 0.45); c.fillStyle = CGOLD_D; c.fill(); c.lineWidth = 3; c.strokeStyle = CGOLD_DD; c.stroke();
  for (let k = 1; k < 14; k++) { c.beginPath(); c.moveTo(-w / 2 + 2, -h + k * h / 14); c.lineTo(w / 2 - 2, -h + k * h / 14); c.strokeStyle = 'rgba(168,110,12,.6)'; c.lineWidth = 1.5; c.stroke(); }
  c.beginPath(); c.roundRect(-w / 2 + 3, -h + 6, w * 0.3, h - 12, 3); c.fillStyle = 'rgba(255,240,190,.6)'; c.fill();
  c.restore();
  c.beginPath(); c.ellipse(x, y + 2, r * 0.5, r * 0.1, 0, 0, 7); c.fillStyle = 'rgba(0,0,0,.35)'; c.fill();
}

