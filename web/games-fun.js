// 동물달리기 / 해적 룰렛 / 화살표 돌리기
import { C, S, h, btn, top, stepper, setupPanel, liveCanvas, vibe, animate, sleep, rnd } from './core.js';

const INK = '#2B2B33';
const R = (d) => (d * Math.PI) / 180;

// ---------- 옆모습 늑대 (달리기용) ----------
export const RACE_COLORS = ['#E53935', '#FF8F00', '#FDD835', '#43A047', '#1E88E5', '#212121', '#8E24AA', '#9E9E9E', '#FF6FB0'];
function sideWolf(ctx, cx, cy, s, fur, ph) {
  const BEL = '#FFF6E8', OL = 'rgba(0,0,0,.4)', sw = Math.max(1.5, s * 0.035), swing = Math.sin(ph) * 28;
  ctx.save(); ctx.translate(cx, cy);
  const oval = (x, y, rx, ry, fill, stroke) => { ctx.beginPath(); ctx.ellipse(x, y, rx, ry, 0, 0, 7); ctx.fillStyle = fill; ctx.fill(); if (stroke) { ctx.lineWidth = sw; ctx.strokeStyle = OL; ctx.stroke(); } };
  // 꼬리
  ctx.save(); ctx.translate(-s * 0.42, -s * 0.05); ctx.rotate(R(-25 + Math.sin(ph * 0.5) * 15)); ctx.translate(s * 0.42, s * 0.05);
  oval(-s * 0.6, -s * 0.09, s * 0.25, s * 0.11, fur, true); oval(-s * 0.78, -s * 0.095, s * 0.08, s * 0.065, BEL);
  ctx.restore();
  // 다리
  const leg = (x, a, dark) => {
    ctx.save(); ctx.translate(x, s * 0.1); ctx.rotate(R(a));
    ctx.globalAlpha = dark ? 0.75 : 1; ctx.strokeStyle = fur; ctx.lineWidth = s * 0.13; ctx.lineCap = 'round';
    ctx.beginPath(); ctx.moveTo(0, 0); ctx.lineTo(0, s * 0.32); ctx.stroke(); ctx.globalAlpha = 1;
    ctx.beginPath(); ctx.arc(0, s * 0.33, s * 0.065, 0, 7); ctx.fillStyle = BEL; ctx.fill(); ctx.restore();
  };
  leg(-s * 0.28, -swing, 1); leg(s * 0.22, swing, 1); leg(-s * 0.2, swing, 0); leg(s * 0.3, -swing, 0);
  // 몸
  oval(0, 0, s * 0.5, s * 0.24, fur, true); oval(0, s * 0.12, s * 0.3, s * 0.1, BEL);
  // 머리, 귀
  const hx = s * 0.52, hy = -s * 0.3, hr = s * 0.27;
  for (const ex of [hx - hr * 0.55, hx + hr * 0.25]) {
    ctx.beginPath(); ctx.moveTo(ex - hr * 0.32, hy - hr * 0.55); ctx.lineTo(ex + hr * 0.02, hy - hr * 1.45); ctx.lineTo(ex + hr * 0.4, hy - hr * 0.55); ctx.closePath();
    ctx.fillStyle = fur; ctx.fill(); ctx.lineWidth = sw; ctx.strokeStyle = OL; ctx.stroke();
    ctx.beginPath(); ctx.moveTo(ex - hr * 0.14, hy - hr * 0.65); ctx.lineTo(ex + hr * 0.03, hy - hr * 1.15); ctx.lineTo(ex + hr * 0.22, hy - hr * 0.65); ctx.closePath();
    ctx.fillStyle = '#F6AFC0'; ctx.fill();
  }
  ctx.beginPath(); ctx.arc(hx, hy, hr, 0, 7); ctx.fillStyle = fur; ctx.fill(); ctx.lineWidth = sw; ctx.strokeStyle = OL; ctx.stroke();
  const mx = hx + hr * 0.25, my = hy - hr * 0.05, mw = hr * 1.15, mh = hr * 0.75;
  oval(mx + mw / 2, my + mh / 2, mw / 2, mh / 2, BEL, true);
  ctx.beginPath(); ctx.arc(mx + mw * 0.92, my + mh * 0.3, hr * 0.17, 0, 7); ctx.fillStyle = INK; ctx.fill();
  const ex = hx + hr * 0.15, ey = hy - hr * 0.25;
  ctx.beginPath(); ctx.arc(ex, ey, hr * 0.27, 0, 7); ctx.fillStyle = '#fff'; ctx.fill();
  ctx.beginPath(); ctx.arc(ex + hr * 0.05, ey, hr * 0.19, 0, 7); ctx.fillStyle = INK; ctx.fill();
  ctx.beginPath(); ctx.arc(ex, ey - hr * 0.07, hr * 0.07, 0, 7); ctx.fillStyle = '#fff'; ctx.fill();
  ctx.beginPath(); ctx.arc(hx + hr * 0.05, hy + hr * 0.35, hr * 0.17, 0, 7); ctx.fillStyle = 'rgba(255,143,163,.53)'; ctx.fill();
  ctx.restore();
}

// ===================== 5. 동물달리기 =====================
export function race(root, back) {
  const s = S();
  let n = 4, secs = 8, cv = null, alive = true;
  root.append(top(s.menuRace, back));
  const body = h('div', { class: 'body' });
  root.append(body);

  const preview = () => {
    const row = h('div', { class: 'preview' });
    for (let i = 0; i < n; i++) {
      const c = h('canvas', { width: 68, height: 68, style: 'width:34px;height:34px' });
      const x = c.getContext('2d'); x.scale(2, 2); sideWolf(x, 14, 20, 21, RACE_COLORS[i], 0);
      row.append(h('div', {}, c, i + 1));
    }
    return row;
  };
  const setup = () => {
    if (cv) { cv.stop(); cv = null; }
    let pv = preview();
    const nStep = stepper(s.players, n, 2, 9, (v) => { n = v; const np = preview(); pv.replaceWith(np); pv = np; });
    body.replaceChildren(setupPanel(s.raceDesc, play, nStep, stepper(s.raceSeconds, secs, 3, 30, (v) => { secs = v; }), pv));
  };

  // 정한 시간에 1등이 들어오도록 진행 곡선을 만듦 (앱과 같은 방식)
  const makeRunners = () => {
    const order = [...Array(n).keys()].sort(() => Math.random() - 0.5);
    const times = Array(n);
    times[order[0]] = secs;
    const gaps = Array.from({ length: n - 1 }, () => Math.random() * 0.6 + 0.4).sort((a, b) => a - b);
    const total = gaps.reduce((a, b) => a + b, 0);
    let acc = 0;
    for (let r = 1; r < n; r++) { acc += gaps[r - 1]; times[order[r]] = secs * (1 + 0.22 * acc / total); }
    return times.map((T) => {
      const a1 = Math.random() * 0.45 + 0.15, k1 = rnd(1, 2), p1 = Math.random() * 6.28;
      const a2 = Math.random() * 0.25, k2 = rnd(2, 4), p2 = Math.random() * 6.28;
      return {
        T,
        pos(t) {
          const u = Math.min(1, Math.max(0, t / T)), w1 = 2 * Math.PI * k1, w2 = 2 * Math.PI * k2;
          const p = u + a1 * (Math.sin(w1 * u + p1) - Math.sin(p1)) / w1 + a2 * (Math.sin(w2 * u + p2) - Math.sin(p2)) / w2;
          return Math.min(1, Math.max(0, p));
        },
      };
    });
  };

  const play = () => {
    let runners = makeRunners(), phase = 'ready', count = 3, t = 0, finished = [];
    const wrap = h('div', { class: 'body', style: 'padding:12px;align-items:center' });
    body.replaceChildren(wrap);
    const title = h('div', { class: 'h-title', style: 'height:54px;font-size:22px;display:flex;align-items:center' });
    const holder = h('div', { class: 'cv-wrap grow', style: 'border-radius:16px;overflow:hidden' });
    const action = h('div', { style: 'width:100%;margin-top:10px' });
    const chB = btn(s.changeRace, setup, 'tbtn');
    wrap.append(title, holder, action, chB);

    const render = () => {
      title.style.fontSize = phase === 'count' ? '40px' : '22px';
      title.classList.toggle('acc', phase === 'done');
      title.textContent = phase === 'ready' ? s.raceTapStart
        : phase === 'count' ? (count > 0 ? String(count) : s.raceGo)
          : phase === 'run' ? s.raceRunning + (finished.length ? '' : `  ${Math.max(0, secs - Math.floor(t))}`)
            : s.raceResult(finished[0] + 1, finished[finished.length - 1] + 1);
      chB.disabled = !(phase === 'ready' || phase === 'done');
    };
    const setAction = () => {
      action.replaceChildren(phase === 'ready' ? btn(s.raceStart, go) : phase === 'done' ? btn(s.raceAgain, again) : h('div', { style: 'height:56px' }));
    };
    const again = () => { runners = makeRunners(); phase = 'ready'; t = 0; finished = []; setAction(); render(); };
    const go = async () => {
      phase = 'count'; setAction();
      for (let c = 3; c >= 1; c--) { count = c; vibe.tick(); render(); await sleep(650); if (!alive) return; }
      count = 0; vibe.strong(); phase = 'run'; render();
      const t0 = performance.now(), end = Math.max(...runners.map((r) => r.T));
      await new Promise((res) => {
        const step = (now) => {
          if (!alive) return res();
          t = (now - t0) / 1000;
          runners.forEach((r, i) => { if (!finished.includes(i) && t >= r.T) { finished.push(i); if (finished.length === 1) vibe.strong(); else vibe.tap(); } });
          render();
          if (t >= end) res(); else requestAnimationFrame(step);
        };
        requestAnimationFrame(step);
      });
      phase = 'done'; setAction(); render();
    };

    cv = liveCanvas(holder, (ctx, w, hh) => {
      const laneH = hh / n, startX = 44, finishX = w - 20, ws = Math.min(laneH * 0.62, 58);
      ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
      for (let i = 0; i < n; i++) {
        ctx.fillStyle = i % 2 === 0 ? '#F1E7D0' : '#E4D6B8'; ctx.fillRect(0, i * laneH, w, laneH);
        ctx.beginPath(); ctx.arc(22, i * laneH + laneH / 2, Math.min(laneH * 0.3, 15), 0, 7); ctx.fillStyle = RACE_COLORS[i]; ctx.fill();
        ctx.font = '900 14px sans-serif'; ctx.fillStyle = i === 2 || i === 7 ? INK : '#fff'; ctx.fillText(String(i + 1), 22, i * laneH + laneH / 2 + 1);
      }
      ctx.fillStyle = '#fff'; ctx.fillRect(startX - 1.5, 0, 3, hh);
      for (let y = 0, r = 0; y < hh; y += 8, r++) for (let c = 0; c < 2; c++) { ctx.fillStyle = (r + c) % 2 === 0 ? INK : '#fff'; ctx.fillRect(finishX + c * 8, y, 8, Math.min(8, hh - y)); }
      const running = phase === 'run' || phase === 'done';
      for (let i = 0; i < n; i++) {
        const p = running ? runners[i].pos(t) : 0, cy = (i + 0.5) * laneH;
        const cx = startX + ws * 0.55 + p * (finishX - startX - ws * 1.25);
        const moving = phase === 'run' && !finished.includes(i);
        const lp = moving ? t * 16 + i : 0, bob = moving ? Math.abs(Math.sin(lp)) * ws * 0.06 : 0;
        sideWolf(ctx, cx, cy + ws * 0.12 - bob, ws, RACE_COLORS[i], lp);
        const rank = finished.indexOf(i);
        if (rank >= 0) {
          const bx = cx - ws * 0.9, r = Math.min(laneH * 0.22, 13);
          ctx.beginPath(); ctx.arc(bx, cy, r, 0, 7); ctx.fillStyle = rank === 0 ? '#FFC107' : rank === n - 1 ? C.win : '#555566'; ctx.fill();
          ctx.font = '900 12px sans-serif'; ctx.fillStyle = rank === 0 ? INK : '#fff'; ctx.fillText(String(rank + 1), bx, cy + 1);
        }
      }
    });
    setAction(); render();
  };

  setup();
  return () => { alive = false; if (cv) cv.stop(); };
}

// ===================== 7. 해적 룰렛 =====================
function pirateHead(ctx, cx, cy, r, sur) {
  for (const sx of [-1, 1]) {
    ctx.beginPath(); ctx.moveTo(cx + sx * r * 0.25, cy - r * 0.7); ctx.lineTo(cx + sx * r * 0.85, cy - r * 1.35); ctx.lineTo(cx + sx * r * 0.95, cy - r * 0.35); ctx.closePath(); ctx.fillStyle = '#6E7A8A'; ctx.fill();
    ctx.beginPath(); ctx.moveTo(cx + sx * r * 0.45, cy - r * 0.68); ctx.lineTo(cx + sx * r * 0.8, cy - r * 1.1); ctx.lineTo(cx + sx * r * 0.85, cy - r * 0.5); ctx.closePath(); ctx.fillStyle = '#F6AFC0'; ctx.fill();
  }
  ctx.beginPath(); ctx.arc(cx, cy, r, 0, 7); ctx.fillStyle = '#9AA5B4'; ctx.fill();
  ctx.beginPath(); ctx.moveTo(cx - r * 0.97, cy - r * 0.25); ctx.quadraticCurveTo(cx, cy - r * 1.25, cx + r * 0.97, cy - r * 0.25); ctx.quadraticCurveTo(cx, cy - r * 0.75, cx - r * 0.97, cy - r * 0.25); ctx.fillStyle = '#E53935'; ctx.fill();
  for (const k of [-0.45, 0, 0.45]) { ctx.beginPath(); ctx.arc(cx + k * r, cy - r * 0.68 + Math.abs(k) * r * 0.25, r * 0.06, 0, 7); ctx.fillStyle = '#fff'; ctx.fill(); }
  ctx.beginPath(); ctx.arc(cx + r * 0.95, cy - r * 0.3, r * 0.13, 0, 7); ctx.fillStyle = '#E53935'; ctx.fill();
  ctx.beginPath(); ctx.moveTo(cx + r * 0.95, cy - r * 0.3); ctx.lineTo(cx + r * 1.25, cy - r * 0.05); ctx.strokeStyle = '#E53935'; ctx.lineWidth = r * 0.12; ctx.lineCap = 'round'; ctx.stroke();
  ctx.beginPath(); ctx.ellipse(cx, cy + r * 0.44, r * 0.48, r * 0.36, 0, 0, 7); ctx.fillStyle = '#F4ECE1'; ctx.fill();
  const ey = cy - r * 0.08;
  for (const sx of [-1, 1]) {
    const ex = cx + sx * r * 0.42;
    if (sur) {
      const d = r * 0.13; ctx.beginPath(); ctx.moveTo(ex - d * sx, ey - d); ctx.lineTo(ex + d * sx, ey); ctx.lineTo(ex - d * sx, ey + d); ctx.strokeStyle = INK; ctx.lineWidth = r * 0.07; ctx.stroke();
    } else {
      ctx.beginPath(); ctx.arc(ex, ey, r * 0.15, 0, 7); ctx.fillStyle = INK; ctx.fill();
      ctx.beginPath(); ctx.arc(ex - r * 0.04, ey - r * 0.05, r * 0.06, 0, 7); ctx.fillStyle = '#fff'; ctx.fill();
    }
    ctx.beginPath(); ctx.ellipse(ex + sx * r * 0.08, ey + r * 0.3, r * 0.2, r * 0.1, 0, 0, 7); ctx.fillStyle = 'rgba(255,143,163,.4)'; ctx.fill();
  }
  ctx.beginPath(); ctx.ellipse(cx, cy + r * 0.245, r * 0.13, r * 0.085, 0, 0, 7); ctx.fillStyle = INK; ctx.fill();
  if (sur) { ctx.beginPath(); ctx.arc(cx, cy + r * 0.54, r * 0.12, 0, 7); ctx.fillStyle = '#8E2440'; ctx.fill(); }
  else { ctx.beginPath(); ctx.moveTo(cx - r * 0.13, cy + r * 0.45); ctx.quadraticCurveTo(cx, cy + r * 0.55, cx + r * 0.13, cy + r * 0.45); ctx.strokeStyle = INK; ctx.lineWidth = r * 0.05; ctx.stroke(); }
}
function barrel(ctx, w, hh, dx) {
  const l = w * 0.14 + dx, rr = w * 0.86 + dx, t = hh * 0.36, b = hh * 0.98, bu = w * 0.05;
  ctx.beginPath(); ctx.moveTo(l, t); ctx.lineTo(rr, t); ctx.quadraticCurveTo(rr + bu * 2, (t + b) / 2, rr, b); ctx.lineTo(l, b); ctx.quadraticCurveTo(l - bu * 2, (t + b) / 2, l, t); ctx.closePath(); ctx.fillStyle = '#9A6332'; ctx.fill();
  ctx.strokeStyle = '#6E4220'; ctx.lineWidth = 2;
  for (let k = 1; k <= 5; k++) { const x = l + (rr - l) * k / 6, bend = (x - (l + rr) / 2) / ((rr - l) / 2) * bu; ctx.beginPath(); ctx.moveTo(x, t); ctx.quadraticCurveTo(x + bend * 2, (t + b) / 2, x, b); ctx.stroke(); }
  const mid = (t + b) / 2 - hh * 0.005;
  for (const y of [t + hh * 0.02, b - hh * 0.03, mid]) {
    const e = y === mid ? bu : bu * 0.35;
    ctx.beginPath(); ctx.roundRect(l - e, y - hh * 0.018, rr - l + e * 2, hh * 0.036, hh * 0.018); ctx.fillStyle = '#5D6670'; ctx.fill();
    ctx.beginPath(); ctx.moveTo(l - e + 6, y - hh * 0.008); ctx.lineTo(rr + e - 6, y - hh * 0.008); ctx.strokeStyle = '#8A95A1'; ctx.lineWidth = 1; ctx.stroke();
  }
  ctx.beginPath(); ctx.ellipse((l + rr) / 2, t, (rr - l + 8) / 2, hh * 0.045, 0, 0, 7); ctx.fillStyle = '#6E4220'; ctx.fill();
  ctx.beginPath(); ctx.ellipse((l + rr) / 2, t, (rr - l - w * 0.08) / 2, hh * 0.03, 0, 0, 7); ctx.fillStyle = '#2A170A'; ctx.fill();
}
function sword(ctx, x, y, L, grip, trap) {
  const gx = x - L * 0.35, gy = y - L * 0.55, ex = x - L * 0.75, ey = y - L * 1.15, gc = trap ? C.win : '#FFC107';
  const line = (a, b, c, d, col, wd) => { ctx.beginPath(); ctx.moveTo(a, b); ctx.lineTo(c, d); ctx.strokeStyle = col; ctx.lineWidth = wd; ctx.lineCap = 'round'; ctx.stroke(); };
  line(x, y, gx, gy, '#CFD8DC', L * 0.12);
  line(gx - L * 0.22, gy + L * 0.13, gx + L * 0.22, gy - L * 0.13, gc, L * 0.12);
  line(gx, gy, ex, ey, grip, L * 0.16);
  ctx.beginPath(); ctx.arc(ex, ey, L * 0.11, 0, 7); ctx.fillStyle = gc; ctx.fill();
}
export function pirate(root, back) {
  const s = S();
  const SLOTS = 20;
  let trap, swords, popped, launch, shake, cv;
  root.append(top(s.menuPirate, back));
  const wrap = h('div', { class: 'body pad center' });
  root.append(wrap);
  const title = h('div', { class: 'h-title', style: 'font-size:24px' });
  const sub = h('div', { class: 'h-sub' });
  // 늑대가 위로 튀어나갈 자리를 위해 위쪽에 여유 공간을 둠
  const holder = h('div', { class: 'cv-wrap', style: 'aspect-ratio:.72;max-height:62vh;max-width:44.6vh' });
  const action = h('div', { style: 'width:100%' });
  wrap.append(title, sub, h('div', { class: 'grow' }), holder, h('div', { class: 'grow' }), action);

  // 통 그림 영역: 아래쪽 w/0.9 높이, 위쪽 나머지는 튀어나갈 공간
  const area = (w, hh) => { const H = w / 0.9; return { H, oy: hh - H }; };
  const slot = (i, w, hh) => {
    const { H, oy } = area(w, hh);
    const r = Math.floor(i / 5), c = i % 5, b = 1 + 0.06 * Math.sin(Math.PI * (r + 0.5) / 4);
    return [w * 0.5 + (c - 2) * w * 0.135 * b, oy + H * (0.47 + r * 0.125)];
  };
  const render = () => {
    title.textContent = popped ? s.popped : s.pirateHint;
    title.classList.toggle('win', popped);
    sub.textContent = popped ? s.poppedAt(swords.length) : s.slotsLeft(SLOTS - swords.length);
    action.replaceChildren(popped ? btn(s.playAgain, newRound) : btn(s.shuffle, newRound, 'btn alt'));
  };
  const newRound = () => { trap = rnd(0, SLOTS - 1); swords = []; popped = false; launch = 0; shake = 0; render(); };
  const insert = (i) => {
    if (popped || swords.includes(i)) return;
    swords.push(i);
    if (i === trap) { popped = true; vibe.strong(); animate(900, (p) => { launch = p; }); }
    else { vibe.tap(); animate(350, (p) => { shake = 1 - p; }, 'linear'); }
    render();
  };

  cv = liveCanvas(holder, (ctx, w, hh) => {
    const sx = Math.sin(shake * 40) * shake * 6;
    const { H, oy } = area(w, hh);
    const hr = w * 0.17, hx = w * 0.5, hy = oy + H * 0.27 - launch * (oy + H * 0.27 - hr * 1.45);
    ctx.save(); ctx.translate(hx, hy); ctx.rotate(R(launch * 720)); ctx.translate(-hx, -hy);
    pirateHead(ctx, hx, hy, hr, popped); ctx.restore();
    ctx.save(); ctx.translate(0, oy); barrel(ctx, w, H, sx); ctx.restore();
    for (let i = 0; i < SLOTS; i++) {
      const [x, y] = slot(i, w, hh);
      ctx.beginPath(); ctx.ellipse(x + sx, y, w * 0.04, w * 0.025, 0, 0, 7); ctx.fillStyle = '#2A170A'; ctx.fill();
      const idx = swords.indexOf(i);
      if (idx >= 0) sword(ctx, x + sx, y, w * 0.11, C.players[idx % C.players.length], popped && i === trap);
    }
  });
  cv.canvas.addEventListener('click', (e) => {
    const r = cv.canvas.getBoundingClientRect(), x = e.clientX - r.left, y = e.clientY - r.top;
    let best = -1, bd = 1e9;
    for (let i = 0; i < SLOTS; i++) { const [sx, sy] = slot(i, r.width, r.height), d = Math.hypot(sx - x, sy - y); if (d < bd) { bd = d; best = i; } }
    if (best >= 0 && bd < r.width * 0.08) insert(best);
  });
  newRound();
  return () => cv.stop();
}

// ===================== 8. 화살표 돌리기 =====================
export function arrow(root, back) {
  const s = S();
  let secs = 5, angle = Math.random() * 360, spinning = false, stopped = false;
  root.append(top(s.menuArrow, back));
  const wrap = h('div', { class: 'body pad center', style: 'gap:10px' });
  root.append(wrap);
  const title = h('div', { class: 'h-title' });
  const holder = h('div', { class: 'cv-wrap', style: 'aspect-ratio:1;max-height:52vh;max-width:52vh' });
  const spinB = btn(s.spin, () => spin());
  wrap.append(title, h('div', { class: 'grow' }), holder, h('div', { class: 'grow' }), stepper(s.arrowSeconds, secs, 1, 15, (v) => { secs = v; }), spinB);

  const render = () => {
    title.textContent = spinning ? s.arrowSpinning : stopped ? s.arrowStopped : s.arrowTap;
    title.classList.toggle('acc', stopped && !spinning);
    spinB.disabled = spinning;
  };
  const spin = async () => {
    if (spinning) return;
    spinning = true; stopped = false; vibe.tap(); render();
    const from = angle, total = 360 * Math.max(1.5, secs * 0.85) + Math.random() * 360, dur = secs * 1000;
    await animate(dur, (p) => { angle = from + total * p; });
    angle %= 360; spinning = false; stopped = true; vibe.strong(); render();
  };
  const cv = liveCanvas(holder, (ctx, w, hh) => {
    const cx = w / 2, cy = hh / 2, r = Math.min(w, hh) / 2 - 8;
    ctx.beginPath(); ctx.arc(cx, cy, r, 0, 7); ctx.fillStyle = C.surface; ctx.fill(); ctx.lineWidth = 6; ctx.strokeStyle = C.surface2; ctx.stroke();
    ctx.save(); ctx.translate(cx, cy); ctx.rotate(R(angle));
    const L = r * 0.72, sw = r * 0.07, hw = r * 0.2, hl = r * 0.24;
    ctx.beginPath(); ctx.moveTo(0, -L); ctx.lineTo(hw, -L + hl); ctx.lineTo(sw, -L + hl); ctx.lineTo(sw, L * 0.55); ctx.lineTo(-sw, L * 0.55); ctx.lineTo(-sw, -L + hl); ctx.lineTo(-hw, -L + hl); ctx.closePath();
    ctx.fillStyle = C.accent; ctx.fill(); ctx.lineWidth = 2; ctx.strokeStyle = '#7A5200'; ctx.stroke();
    ctx.fillStyle = C.win;
    for (let k = 0; k < 3; k++) {
      const y = L * (0.3 + k * 0.12);
      for (const d of [-1, 1]) { ctx.beginPath(); ctx.moveTo(d * sw, y); ctx.lineTo(d * (sw + r * 0.12), y + r * 0.1); ctx.lineTo(d * (sw + r * 0.12), y + r * 0.02); ctx.closePath(); ctx.fill(); }
    }
    ctx.restore();
    ctx.beginPath(); ctx.arc(cx, cy, r * 0.11, 0, 7); ctx.fillStyle = C.bg; ctx.fill();
    ctx.beginPath(); ctx.arc(cx, cy, r * 0.07, 0, 7); ctx.fillStyle = C.accent; ctx.fill();
  });
  cv.canvas.addEventListener('click', spin);
  render();
  return () => cv.stop();
}
