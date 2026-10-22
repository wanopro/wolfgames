// 동물 얼굴 10종 (늑대 그림과 같은 그림체). (x,y) 중심, r 반지름, st: 0 보통 / 1 겁먹음 / 2 물림
export const ANIMALS = [['rabbit', '토끼'], ['pig', '돼지'], ['chicken', '닭'], ['sheep', '양'], ['deer', '사슴'], ['mouse', '쥐'], ['duck', '오리'], ['cow', '소'], ['hamster', '햄스터'], ['chick', '병아리'], ['dog', '강아지'], ['cat', '고양이']];
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
