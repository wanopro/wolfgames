import { barrel, sword } from './orig.js';
const INK = '#2B2B33';
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

/** 통 안에서 내다보는 늑대 장면. st = {swords:[slot..], trap, mode, modeT, launch, shake} */
export function drawScene(ctx, w, hh, st) {
  const H = w / 0.9, oy = hh - H, sx = Math.sin(st.shake * 40) * st.shake * 6;
  const l = w * 0.14 + sx, rr = w * 0.86 + sx, rimY = oy + H * 0.36, rx = (rr - l - w * 0.08) / 2, ry = H * 0.03, cxr = (l + rr) / 2;
  // 1) 통
  ctx.save(); ctx.translate(0, oy); barrel(ctx, w, H, sx); ctx.restore();
  // 2) 늑대 — 통 입구 앞쪽 테두리보다 위만 보이게(통 안에 들어가 있는 느낌)
  const hr = w * 0.17, hx = w * 0.5;
  let hy, mode = st.mode;
  if (mode === 'sur') hy = rimY - hr * 0.3 - st.launch * (rimY - hr * 0.3 - hr * 1.45);
  else if (mode === 'tease') { const u = Math.min(1, st.modeT / 0.25), back = st.modeT > 1.05 ? Math.min(1, (st.modeT - 1.05) / 0.2) : 0; const up = (u < 1 ? 1 - Math.pow(1 - u, 3) * Math.cos(u * 8) : 1) * (1 - back); hy = rimY - hr * 0.3 - up * hr * 0.75 + Math.sin(st.modeT * 9) * hr * 0.04 * (1 - back); }
  else hy = rimY - hr * 0.3 + Math.sin(st.time * 2.2) * hr * 0.05; // 살짝 들썩
  ctx.save();
  ctx.beginPath(); ctx.moveTo(0, 0); ctx.lineTo(w, 0); ctx.lineTo(w, rimY); ctx.lineTo(cxr + rx, rimY);
  ctx.ellipse(cxr, rimY, rx, ry, 0, 0, Math.PI, false); ctx.lineTo(0, rimY); ctx.closePath(); ctx.clip();
  ctx.translate(hx, hy); ctx.rotate(mode === 'sur' ? st.launch * Math.PI * 4 : 0); ctx.translate(-hx, -hy);
  pirateHead2(ctx, hx, hy, hr, mode, st.modeT);
  ctx.restore();
  // 3) 통 입구 앞쪽 테두리 + 앞발 (통을 붙잡고 있음)
  ctx.beginPath(); ctx.ellipse(cxr, rimY, (rr - l + 8) / 2, H * 0.045, 0, 0, Math.PI, false); ctx.ellipse(cxr, rimY, rx, ry, 0, Math.PI, 0, true); ctx.closePath(); ctx.fillStyle = '#6E4220'; ctx.fill();
  if (mode !== 'sur' || st.launch < 0.15) {
    const lift = mode === 'tease' ? Math.min(1, st.modeT / 0.2) * (st.modeT > 1.05 ? Math.max(0, 1 - (st.modeT - 1.05) / 0.2) : 1) : 0;
    for (const s of [-1, 1]) {
      const px = hx + s * hr * 0.75, py = rimY + H * 0.012 - lift * hr * 0.1;
      ctx.beginPath(); ctx.ellipse(px, py, hr * 0.28, hr * 0.17, 0, 0, 7); ctx.fillStyle = '#9AA5B4'; ctx.fill(); ctx.lineWidth = 1.5; ctx.strokeStyle = 'rgba(0,0,0,.3)'; ctx.stroke();
      for (const k of [-1, 0, 1]) { ctx.beginPath(); ctx.moveTo(px + k * hr * 0.09, py - hr * 0.02); ctx.lineTo(px + k * hr * 0.09, py + hr * 0.12); ctx.strokeStyle = 'rgba(0,0,0,.3)'; ctx.lineWidth = 1.5; ctx.stroke(); }
    }
  }
  // 4) 칼 구멍과 칼
  for (let i = 0; i < 20; i++) {
    const r = Math.floor(i / 5), c = i % 5, b = 1 + 0.06 * Math.sin(Math.PI * (r + 0.5) / 4);
    const x = w * 0.5 + (c - 2) * w * 0.135 * b, y = oy + H * (0.47 + r * 0.125);
    ctx.beginPath(); ctx.ellipse(x + sx, y, w * 0.04, w * 0.025, 0, 0, 7); ctx.fillStyle = '#2A170A'; ctx.fill();
    const idx = st.swords.indexOf(i);
    if (idx >= 0) sword(ctx, x + sx, y, w * 0.11, ['#FF5252', '#448AFF', '#69F0AE', '#FFD740'][idx % 4], mode === 'sur' && i === st.trap);
  }
  // 5) 메롱~ 말풍선
  if (mode === 'tease' && st.modeT > 0.15 && st.modeT < 1.15) {
    const b = Math.min(1, (st.modeT - 0.15) / 0.2), bob = Math.sin(st.modeT * 8) * 3;
    ctx.save(); ctx.translate(hx + hr * 1.35, hy - hr * 1.15 + bob); ctx.scale(b, b); ctx.rotate(-0.1);
    ctx.font = `900 ${Math.round(hr * 0.42)}px sans-serif`; ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
    const tw = ctx.measureText('메롱~').width + hr * 0.4, th = hr * 0.62;
    ctx.beginPath(); ctx.roundRect(-tw / 2, -th / 2, tw, th, th / 2); ctx.fillStyle = '#fff'; ctx.fill();
    ctx.beginPath(); ctx.moveTo(-tw * 0.3, th / 2 - 2); ctx.lineTo(-tw * 0.45, th / 2 + hr * 0.2); ctx.lineTo(-tw * 0.1, th / 2 - 2); ctx.fill();
    ctx.fillStyle = '#E53950'; ctx.fillText('메롱~', 0, 2); ctx.restore();
  }
}
