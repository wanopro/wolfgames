// 손가락게임: 손가락선택
import { C, S, h, btn, top, stepper, setupPanel, liveCanvas, vibe, shuffle } from './core.js';

/** 메롱하는 늑대 얼굴. (x,y) 중심, r 반지름, t = 나타난 뒤 지난 초, ring = 플레이어 색, word = 말풍선 글자 */
function tauntWolf(ctx, x, y, r, t, ring, word) {
  const FUR = '#9AA5B4', DARK = '#6E7A8A', CREAM = '#F4ECE1', INK = '#2B2B33', OL = 'rgba(0,0,0,.35)';
  // 튀어나오기 (살짝 튕김)
  const u = Math.min(1, t / 0.35), pop = u < 1 ? 1 - Math.pow(1 - u, 3) * Math.cos(u * 9) : 1;
  const tilt = Math.sin(t * 7) * 0.2 * u;           // 고개 까딱까딱
  const wag = Math.sin(t * 16) * 0.32;               // 혀 날름날름
  const tl = 0.85 + Math.sin(t * 8) * 0.15;          // 혀 길이
  ctx.save(); ctx.translate(x, y); ctx.scale(pop, pop);
  // 플레이어 색 테두리 + 반짝 링
  if (ring) {
    ctx.beginPath(); ctx.arc(0, 0, r * 1.18, 0, 7); ctx.fillStyle = ring + '40'; ctx.fill();
    ctx.lineWidth = r * 0.1; ctx.strokeStyle = ring; ctx.stroke();
  }
  ctx.rotate(tilt);
  const lw = Math.max(1.5, r * 0.04);
  // 귀 (번갈아 쫑긋)
  for (const sx of [-1, 1]) {
    const e = Math.sin(t * 7 + (sx > 0 ? 0 : Math.PI)) * 0.08;
    ctx.save(); ctx.translate(sx * r * 0.55, -r * 0.6); ctx.rotate(sx * (0.25 + e));
    ctx.beginPath(); ctx.moveTo(-r * 0.3, r * 0.15); ctx.lineTo(0, -r * 0.62); ctx.lineTo(r * 0.3, r * 0.15); ctx.closePath();
    ctx.fillStyle = DARK; ctx.fill(); ctx.lineWidth = lw; ctx.strokeStyle = OL; ctx.stroke();
    ctx.beginPath(); ctx.moveTo(-r * 0.15, r * 0.08); ctx.lineTo(0, -r * 0.4); ctx.lineTo(r * 0.15, r * 0.08); ctx.closePath(); ctx.fillStyle = '#F6AFC0'; ctx.fill();
    ctx.restore();
  }
  // 얼굴 + 볼털
  ctx.beginPath();
  ctx.moveTo(-r * 0.95, -r * 0.1);
  ctx.quadraticCurveTo(-r * 0.95, -r * 0.95, 0, -r * 0.95);
  ctx.quadraticCurveTo(r * 0.95, -r * 0.95, r * 0.95, -r * 0.1);
  ctx.lineTo(r * 1.08, r * 0.25); ctx.lineTo(r * 0.82, r * 0.3); ctx.lineTo(r * 0.92, r * 0.55); ctx.lineTo(r * 0.55, r * 0.6);
  ctx.quadraticCurveTo(0, r * 0.98, -r * 0.55, r * 0.6);
  ctx.lineTo(-r * 0.92, r * 0.55); ctx.lineTo(-r * 0.82, r * 0.3); ctx.lineTo(-r * 1.08, r * 0.25); ctx.closePath();
  ctx.fillStyle = FUR; ctx.fill(); ctx.lineWidth = lw; ctx.strokeStyle = OL; ctx.stroke();
  // 이마 무늬
  ctx.beginPath(); ctx.moveTo(-r * 0.16, -r * 0.93); ctx.lineTo(0, -r * 0.45); ctx.lineTo(r * 0.16, -r * 0.93); ctx.closePath(); ctx.fillStyle = CREAM; ctx.globalAlpha = 0.8; ctx.fill(); ctx.globalAlpha = 1;
  // 주둥이
  ctx.beginPath(); ctx.ellipse(0, r * 0.38, r * 0.48, r * 0.36, 0, 0, 7); ctx.fillStyle = CREAM; ctx.fill();
  // 눈: 왼쪽은 찡긋(>), 오른쪽은 동그랗게 + 장난스러운 눈썹
  const ey = -r * 0.12;
  ctx.lineCap = 'round'; ctx.lineJoin = 'round';
  ctx.beginPath(); ctx.moveTo(-r * 0.55, ey - r * 0.12); ctx.lineTo(-r * 0.32, ey); ctx.lineTo(-r * 0.55, ey + r * 0.12);
  ctx.strokeStyle = INK; ctx.lineWidth = r * 0.09; ctx.stroke();
  ctx.beginPath(); ctx.arc(r * 0.42, ey, r * 0.15, 0, 7); ctx.fillStyle = INK; ctx.fill();
  ctx.beginPath(); ctx.arc(r * 0.37, ey - r * 0.05, r * 0.055, 0, 7); ctx.fillStyle = '#fff'; ctx.fill();
  ctx.beginPath(); ctx.moveTo(r * 0.25, ey - r * 0.3); ctx.quadraticCurveTo(r * 0.42, ey - r * 0.42, r * 0.6, ey - r * 0.28); ctx.lineWidth = r * 0.06; ctx.stroke();
  // 볼 홍조
  for (const sx of [-1, 1]) { ctx.beginPath(); ctx.ellipse(sx * r * 0.62, r * 0.2, r * 0.17, r * 0.09, 0, 0, 7); ctx.fillStyle = 'rgba(255,120,150,.5)'; ctx.fill(); }
  // 코
  ctx.beginPath(); ctx.ellipse(0, r * 0.2, r * 0.14, r * 0.095, 0, 0, 7); ctx.fillStyle = INK; ctx.fill();
  ctx.beginPath(); ctx.arc(-r * 0.04, r * 0.17, r * 0.03, 0, 7); ctx.fillStyle = 'rgba(255,255,255,.6)'; ctx.fill();
  // 입 (헤~) + 혀 메롱
  ctx.beginPath(); ctx.moveTo(-r * 0.24, r * 0.42); ctx.quadraticCurveTo(0, r * 0.6, r * 0.24, r * 0.42); ctx.strokeStyle = INK; ctx.lineWidth = r * 0.05; ctx.stroke();
  ctx.save(); ctx.translate(0, r * 0.5); ctx.rotate(wag);
  const L = r * 0.55 * tl, W = r * 0.2;
  ctx.beginPath(); ctx.moveTo(-W, 0); ctx.lineTo(-W, L * 0.6); ctx.quadraticCurveTo(-W, L + W * 0.4, 0, L + W * 0.4); ctx.quadraticCurveTo(W, L + W * 0.4, W, L * 0.6); ctx.lineTo(W, 0); ctx.closePath();
  ctx.fillStyle = '#FF6F91'; ctx.fill(); ctx.lineWidth = lw; ctx.strokeStyle = '#C2405F'; ctx.stroke();
  ctx.beginPath(); ctx.moveTo(0, L * 0.15); ctx.lineTo(0, L * 0.7); ctx.strokeStyle = '#D9506F'; ctx.lineWidth = r * 0.035; ctx.stroke();
  ctx.restore();
  ctx.restore();
  // "메롱~" 말풍선 (통통)
  if (t > 0.25) {
    const b = Math.min(1, (t - 0.25) / 0.25), bob = Math.sin(t * 7) * r * 0.06;
    ctx.save(); ctx.translate(x + r * 0.95, y - r * 1.25 + bob); ctx.scale(b, b); ctx.rotate(-0.12);
    ctx.font = `900 ${Math.round(r * 0.42)}px sans-serif`; ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
    const w = ctx.measureText(word).width + r * 0.4, hh = r * 0.6;
    ctx.beginPath(); ctx.roundRect(-w / 2, -hh / 2, w, hh, hh / 2); ctx.fillStyle = '#fff'; ctx.fill();
    ctx.beginPath(); ctx.moveTo(-w * 0.25, hh / 2 - 2); ctx.lineTo(-w * 0.4, hh / 2 + r * 0.22); ctx.lineTo(-w * 0.05, hh / 2 - 2); ctx.fill();
    ctx.fillStyle = '#E53950'; ctx.fillText(word, 0, 2);
    ctx.restore();
  }
}

// ===================== 손가락선택 =====================
export function fingerPick(root, back) {
  const s = S();
  let losers = 1, cv = null;
  const WAIT = 500;

  const setup = () => {
    if (cv) { cv.stop(); cv = null; }
    root.replaceChildren(top(s.fingerTitle, back), h('div', { class: 'body' },
      setupPanel(s.fingerDesc, play, stepper(s.fingerLosers, losers, 1, 9, (v) => { losers = v; }))));
  };

  const play = () => {
    const spots = []; // { id, x, y, color, down, t0 }
    let phase = 'touch', timer = 0, resultAt = 0, msg = '', msgUntil = 0;
    const body = h('div', { class: 'body', style: 'touch-action:none' });
    const hint = h('div', { class: 'h-sub', style: 'position:absolute;inset:0;display:flex;align-items:center;justify-content:center;text-align:center;font-size:18px;line-height:28px;padding:24px;white-space:pre-line;pointer-events:none' });
    const head = h('div', { style: 'position:absolute;left:0;right:0;top:10px;text-align:center;font-size:20px;font-weight:800;pointer-events:none;color:var(--accent)' });
    root.replaceChildren(top(s.fingerTitle, setup, btn(`${s.fingerLosers} ${losers}`, setup, 'tbtn accent')), body);
    body.append(hint, head);

    const reset = () => { spots.length = 0; phase = 'touch'; clearTimeout(timer); };
    const pick = () => {
      const used = spots.map((p) => p.color);
      return C.players.find((c) => !used.includes(c)) || C.players[spots.length % C.players.length];
    };
    const decide = () => {
      if (phase !== 'touch' || spots.some((p) => p.down) || !spots.length) return;
      if (spots.length <= losers) { msg = s.fingerNeed(losers + 1); msgUntil = performance.now() + 1800; vibe.tap(); spots.length = 0; return; }
      shuffle(spots.map((_, i) => i)).slice(0, losers).forEach((i) => { spots[i].lose = true; });
      phase = 'result'; resultAt = performance.now(); vibe.strong();
    };
    const pos = (e) => { const r = cv.canvas.getBoundingClientRect(); return { x: e.clientX - r.left, y: e.clientY - r.top }; };

    cv = liveCanvas(body, (ctx, w, hh) => {
      const now = performance.now();
      hint.style.display = spots.length || phase !== 'touch' ? 'none' : 'flex';
      hint.textContent = now < msgUntil ? msg : s.fingerHint;
      if (now < msgUntil) hint.style.display = 'flex';
      head.textContent = phase === 'result' ? s.fingerResult(losers) : '';
      const R = Math.min(52, w * 0.13);
      for (const p of spots) {
        if (phase === 'result') {
          const t = (now - resultAt) / 1000;
          if (p.lose) continue;
          const a = Math.max(0, 1 - t / 0.3);
          ctx.globalAlpha = 0.25 + 0.75 * a;
          ctx.beginPath(); ctx.arc(p.x, p.y, R * (0.7 + 0.3 * a), 0, 7); ctx.fillStyle = p.color; ctx.fill();
          ctx.globalAlpha = 1;
          ctx.fillStyle = '#fff'; ctx.font = '800 15px sans-serif'; ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
          ctx.fillText(s.fingerPass, p.x, p.y + 1);
          continue;
        }
        const pulse = p.down ? 1 + Math.sin((now - p.t0) / 120) * 0.05 : 1;
        ctx.beginPath(); ctx.arc(p.x, p.y, R * pulse, 0, 7); ctx.fillStyle = p.color; ctx.fill();
        ctx.beginPath(); ctx.arc(p.x, p.y, R * 1.18, 0, 7); ctx.lineWidth = 6; ctx.strokeStyle = p.down ? p.color + '80' : '#fff'; ctx.stroke();
      }
      if (phase === 'result') {
        const t = (now - resultAt) / 1000;
        for (const p of spots) if (p.lose) tauntWolf(ctx, p.x, p.y, R, t, p.color, s.fingerMerong);
        ctx.fillStyle = C.sub; ctx.font = '600 14px sans-serif'; ctx.textAlign = 'center';
        if (t > 1) ctx.fillText(s.fingerAgain, w / 2, hh - 18);
      }
    });
    const el = cv.canvas;
    el.addEventListener('pointerdown', (e) => {
      e.preventDefault();
      if (phase === 'result') { if (performance.now() - resultAt > 600) { reset(); vibe.tap(); } return; }
      try { el.setPointerCapture(e.pointerId); } catch (_) { /* 무시 */ }
      clearTimeout(timer); msgUntil = 0;
      spots.push({ id: e.pointerId, ...pos(e), color: pick(), down: true, t0: performance.now() });
      vibe.tap();
    });
    el.addEventListener('pointermove', (e) => { const p = spots.find((x) => x.id === e.pointerId && x.down); if (p) Object.assign(p, pos(e)); });
    const up = (e) => {
      const p = spots.find((x) => x.id === e.pointerId && x.down);
      if (!p) return;
      p.down = false; p.id = null;
      if (!spots.some((x) => x.down)) { clearTimeout(timer); timer = setTimeout(decide, WAIT); }
    };
    el.addEventListener('pointerup', up);
    el.addEventListener('pointercancel', up);
  };

  setup();
  return () => { if (cv) cv.stop(); };
}
