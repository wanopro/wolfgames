// 늑대달리기 / 늑대 룰렛 / 늑대 돌리기
import { C, S, h, btn, top, stepper, setupPanel, liveCanvas, vibe, animate, sleep, rnd, shuffle } from './core.js';
import { cuteWolf, animalFace, ANIMALS, pirateHead2 } from './art.js';

const INK = '#2B2B33';
const R = (d) => (d * Math.PI) / 180;

// ---------- 옆모습 늑대 (달리기용) ----------

// ===================== 5. 늑대달리기 =====================
export function race(root, back) {
  const s = S();
  const names = s.animalNames.split(',');
  let n = 4, secs = 8, cv = null, alive = true, picks = [0, 1, 3, 10];
  root.append(top(s.menuRace, back));
  const body = h('div', { class: 'body' });
  root.append(body);

  const face = (kind, px) => {
    const c = h('canvas', { width: px * 2, height: px * 2, style: `width:${px}px;height:${px}px` });
    const x = c.getContext('2d'); x.scale(2, 2); animalFace(x, px / 2, px / 2 + px * 0.08, px * 0.33, kind, 0);
    return c;
  };
  const fixPicks = () => {
    picks = picks.slice(0, n);
    for (let k = 0; picks.length < n && k < ANIMALS.length; k++) if (!picks.includes(k)) picks.push(k);
  };
  const setup = () => {
    if (cv) { cv.stop(); cv = null; }
    fixPicks();
    const grid = h('div', { class: 'agrid' }), lanes = h('div', { class: 'alanes' }), title = h('div', { class: 'h-sub', style: 'font-weight:700;color:var(--text);text-align:left;width:100%' });
    const startB = btn(s.start, () => { if (picks.length === n) play(); });
    const draw = () => {
      title.textContent = s.racePick(n);
      grid.replaceChildren(...ANIMALS.map((k, i) => {
        const j = picks.indexOf(i);
        const b = h('button', { class: 'a' + (j >= 0 ? ' on' : '') }, face(k, 44), h('small', {}, names[i]), j >= 0 ? h('span', { class: 'n' }, j + 1) : null);
        b.addEventListener('click', () => { vibe.tap(); if (j >= 0) picks.splice(j, 1); else if (picks.length < n) picks.push(i); draw(); });
        return b;
      }));
      lanes.replaceChildren(...picks.map((a, i) => h('div', {}, face(ANIMALS[a], 32), h('small', {}, s.raceLane(i + 1, names[a])))));
      startB.disabled = picks.length !== n;
      startB.textContent = picks.length === n ? s.start : s.raceNeed(n);
    };
    const nStep = stepper(s.players, n, 2, 9, (v) => { n = v; fixPicks(); draw(); });
    body.replaceChildren(h('div', { class: 'scroll' }, h('div', { class: 'desc' }, s.raceDesc), nStep, stepper(s.raceSeconds, secs, 3, 30, (v) => { secs = v; }), title, grid, lanes, startB));
    draw();
  };

  // 정한 시간에 1등이 들어오도록 진행 곡선을 만듦 (앱과 같은 방식)
  // 구간마다 속도를 다르게(질주·주춤) 해서 순위가 자주 뒤바뀜. 끝까지 접전(꼴찌도 1등보다 최대 8%만 늦음)
  const makeRunners = () => {
    const order = [...Array(n).keys()].sort(() => Math.random() - 0.5);
    const times = Array(n);
    times[order[0]] = secs;
    const gaps = Array.from({ length: n - 1 }, () => Math.random() * 0.6 + 0.4).sort((a, b) => a - b);
    const total = gaps.reduce((a, b) => a + b, 0);
    let acc = 0;
    for (let r = 1; r < n; r++) { acc += gaps[r - 1]; times[order[r]] = secs * (1 + 0.08 * acc / total); }
    const K = 20, L = K - 1, slots = shuffle([...Array(L).keys()].map((j) => j + 1));
    const CFG = [[0, 2.8, 0.6], [4, 2.4, 0.5], [8, 2.0, 0.4], [12, 1.8, 0.4], [2, 0.1, 0.1], [6, 0.12, 0.1], [10, 0.15, 0.1], [14, 0.2, 0.1]];
    return times.map((T, i) => {
      const v = Array.from({ length: K + 1 }, () => 0.5 + Math.random() * 0.5);
      v[0] = 0.6 + Math.random() * 0.6;
      CFG.forEach(([o, a, b]) => { v[slots[(i * CFG.length + o) % L]] = a + Math.random() * b; });
      const M = 160, tab = [0];
      let sum = 0;
      for (let j = 1; j <= M; j++) {
        const u = ((j - 0.5) / M) * K, k = Math.floor(u), e = (1 - Math.cos(Math.PI * (u - k))) / 2;
        sum += v[k] * (1 - e) + v[Math.min(K, k + 1)] * e; tab.push(sum);
      }
      for (let j = 0; j <= M; j++) tab[j] /= sum;
      return {
        T,
        pos(t) {
          const u = Math.min(1, Math.max(0, t / T)) * M, j = Math.min(M - 1, Math.floor(u));
          return tab[j] + (tab[j + 1] - tab[j]) * (u - j);
        },
      };
    });
  };

  const play = () => {
    let runners = makeRunners(), phase = 'ready', count = 3, t = 0, finished = [];
    const BITE = 0.7;
    const wrap = h('div', { class: 'body', style: 'padding:12px;align-items:center' });
    body.replaceChildren(wrap);
    const title = h('div', { class: 'h-title', style: 'height:54px;font-size:21px;display:flex;align-items:center;text-align:center' });
    const holder = h('div', { class: 'cv-wrap grow', style: 'border-radius:16px;overflow:hidden' });
    const action = h('div', { style: 'width:100%;margin-top:10px' });
    const chB = btn(s.changeRace, setup, 'tbtn');
    wrap.append(title, holder, action, chB);
    const nm = (i) => names[picks[i]];

    const render = () => {
      title.style.fontSize = phase === 'count' ? '40px' : '21px';
      title.classList.toggle('acc', phase === 'done');
      title.textContent = phase === 'ready' ? s.raceTapStart
        : phase === 'count' ? (count > 0 ? String(count) : s.raceGo)
          : phase === 'run' ? s.raceRunning + (finished.length ? '' : `  ${Math.max(0, secs - Math.floor(t))}`)
            : s.raceResultNames(nm(finished[0]), nm(finished[finished.length - 1]));
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
      const t0 = performance.now(), end = Math.max(...runners.map((r) => r.T)) + BITE;
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
      const lh = hh / n, finish = w - 66, ws = Math.min(lh * 0.42, 70), ar = Math.min(lh * 0.14, ws * 0.34), ax = w - 34;
      ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
      for (let i = 0; i < n; i++) {
        ctx.fillStyle = i % 2 === 0 ? '#F1E7D0' : '#E4D6B8'; ctx.fillRect(0, i * lh, w, lh);
        ctx.fillStyle = 'rgba(0,0,0,.07)'; ctx.fillRect(0, (i + 1) * lh - 6, w, 6);
        ctx.beginPath(); ctx.arc(18, i * lh + lh / 2, Math.min(lh * 0.28, 12), 0, 7); ctx.fillStyle = '#5B616B'; ctx.fill();
        ctx.font = '900 12px sans-serif'; ctx.fillStyle = '#fff'; ctx.fillText(String(i + 1), 18, i * lh + lh / 2 + 1);
      }
      ctx.fillStyle = '#fff'; ctx.fillRect(34, 0, 3, hh);
      for (let y = 0, r = 0; y < hh; y += 8, r++) for (let c = 0; c < 2; c++) { ctx.fillStyle = (r + c) % 2 ? '#fff' : INK; ctx.fillRect(finish + c * 8, y, 8, Math.min(8, hh - y)); }
      const running = phase === 'run' || phase === 'done';
      for (let i = 0; i < n; i++) {
        const gy = (i + 0.84) * lh, ay = gy - ws * 0.5, rt = runners[i].T, bt = running ? t - rt : -1;
        // 동물: 결승선 너머에서 덜덜, 늑대가 가까우면 겁먹고 땀, 물리면 X눈
        ctx.save(); ctx.translate(ax, ay);
        if (bt < 0) {
          const near = running && t > rt - 1.4; ctx.rotate(Math.sin((running ? t : performance.now() / 1000) * (near ? 26 : 12) + i) * (near ? 0.12 : 0.05));
          animalFace(ctx, 0, 0, ar, ANIMALS[picks[i]], near ? 1 : 0);
          if (near) { ctx.font = `${Math.round(ar * 0.7)}px sans-serif`; ctx.fillText('💦', ar * 0.9, -ar * 0.9); }
        } else {
          const hit = bt > 0.22; if (hit) { ctx.translate(Math.sin(bt * 40) * 2 * Math.max(0, 1 - bt), 0); ctx.rotate(0.3); ctx.scale(1.12, 0.84); }
          animalFace(ctx, 0, 0, ar, ANIMALS[picks[i]], hit ? 2 : 1);
          if (bt > 0.3) { ctx.rotate(-0.3); ctx.font = `${Math.round(ar * 0.65)}px sans-serif`; ctx.fillText('💫', 0, -ar * 1.25); }
        }
        ctx.restore();
        // 늑대
        const s0 = ws, p = running ? runners[i].pos(t) : 0;
        const startX = 40 + s0 * 0.9, endX = finish - 4 - s0 * 0.84;
        let wx = startX + p * (endX - startX), open = 0, run = phase === 'run' && bt < 0 ? 1 : 0, ph = t * 16 + i * 1.7;
        if (run && t > rt - 1.4) { const k = Math.min(1, (t - (rt - 1.4)) / 0.3); open = k * (0.55 + 0.25 * Math.abs(Math.sin(t * 9 + i))); }
        if (bt >= 0) {
          const u = Math.min(1, bt / BITE), reach = (ax - ar * 0.6) - (endX + 0.84 * s0), e = u < 0.4 ? 1 - Math.pow(1 - u / 0.4, 2) : 1;
          wx = endX + reach * e; run = u < 0.4 ? 0.6 : 0; ph = 0.8 + i; open = u < 0.3 ? u / 0.3 : u < 0.45 ? 1 - (u - 0.3) / 0.15 : 0;
        }
        if (run > 0.5 && t > 0) { // 질주할 때 흙먼지·속도선
          const sp = (runners[i].pos(t + 0.06) - p) / 0.06 * rt;
          if (sp > 1.25) {
            const k = Math.min(1, (sp - 1.25) / 1.2);
            for (let d = 0; d < 3; d++) { const q = (t * 3 + d / 3 + i * 0.3) % 1; ctx.globalAlpha = (1 - q) * 0.55 * k; ctx.beginPath(); ctx.arc(wx - s0 * (0.5 + q * 0.9), gy - s0 * 0.06 - q * s0 * 0.12, s0 * (0.07 + q * 0.12), 0, 7); ctx.fillStyle = '#B8A27E'; ctx.fill(); }
            ctx.globalAlpha = 0.5 * k; ctx.strokeStyle = '#fff'; ctx.lineWidth = 2;
            for (let d = 0; d < 3; d++) { const yy = gy - s0 * (0.45 + d * 0.18); ctx.beginPath(); ctx.moveTo(wx - s0 * (0.95 + d * 0.1), yy); ctx.lineTo(wx - s0 * (1.35 + d * 0.15), yy); ctx.stroke(); }
            ctx.globalAlpha = 1;
          }
        }
        cuteWolf(ctx, wx, gy, s0, ph, { run, open, happy: bt > BITE * 0.6 });
        if (bt >= 0.25 && bt < 0.9) { ctx.font = '900 20px sans-serif'; ctx.fillStyle = '#E53950'; ctx.fillText(s.biteWord, ax - 6, ay - ar * 1.6 - (bt - 0.25) * 20); ctx.font = '18px sans-serif'; ctx.fillText('💥', ax - ar * 0.9, ay + ar * 0.4); }
        const rank = finished.indexOf(i);
        if (rank >= 0 && bt >= BITE * 0.6) {
          ctx.beginPath(); ctx.arc(ax + 2, ay + ar * 1.45, Math.min(lh * 0.2, 12), 0, 7); ctx.fillStyle = rank === 0 ? '#FFC107' : rank === n - 1 ? C.win : '#555566'; ctx.fill();
          ctx.font = '900 12px sans-serif'; ctx.fillStyle = rank === 0 ? INK : '#fff'; ctx.fillText(String(rank + 1), ax + 2, ay + ar * 1.45 + 1);
        }
      }
    });
    setAction(); render();
  };

  setup();
  return () => { alive = false; if (cv) cv.stop(); };
}

// ===================== 7. 해적 룰렛 =====================
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
  let teaseAt = -9;
  const render = () => {
    const teasing = !popped && performance.now() / 1000 - teaseAt < 1.3;
    title.textContent = popped ? s.popped : teasing ? s.pirateTease : s.pirateHint;
    title.classList.toggle('win', popped);
    sub.textContent = popped ? s.poppedAt(swords.length) : s.slotsLeft(SLOTS - swords.length);
    action.replaceChildren(popped ? btn(s.playAgain, newRound) : btn(s.shuffle, newRound, 'btn alt'));
  };
  const newRound = () => { trap = rnd(0, SLOTS - 1); swords = []; popped = false; launch = 0; shake = 0; render(); };
  const insert = (i) => {
    if (popped || swords.includes(i)) return;
    swords.push(i);
    if (i === trap) { popped = true; vibe.strong(); animate(900, (p) => { launch = p; }); }
    else { vibe.tap(); teaseAt = performance.now() / 1000; animate(350, (p) => { shake = 1 - p; }, 'linear'); setTimeout(render, 1350); }
    render();
  };

  cv = liveCanvas(holder, (ctx, w, hh) => {
    const sx = Math.sin(shake * 40) * shake * 6;
    const { H, oy } = area(w, hh), now = performance.now() / 1000;
    const l = w * 0.14 + sx, rr = w * 0.86 + sx, rimY = oy + H * 0.36, rx = (rr - l - w * 0.08) / 2, ry = H * 0.03, cxr = (l + rr) / 2;
    // 1) 통
    ctx.save(); ctx.translate(0, oy); barrel(ctx, w, H, sx); ctx.restore();
    // 2) 늑대: 통 입구 앞쪽 테두리보다 위만 보이게 (통 안에 들어가 있음)
    const hr = w * 0.17, hx = w * 0.5, tt = now - teaseAt;
    const mode = popped ? 'sur' : tt < 1.3 ? 'tease' : 'idle';
    let hy;
    if (mode === 'sur') hy = rimY - hr * 0.3 - launch * (rimY - hr * 0.3 - hr * 1.45);
    else if (mode === 'tease') { const u = Math.min(1, tt / 0.25), bk = tt > 1.05 ? Math.min(1, (tt - 1.05) / 0.2) : 0; const up = (u < 1 ? 1 - Math.pow(1 - u, 3) * Math.cos(u * 8) : 1) * (1 - bk); hy = rimY - hr * 0.3 - up * hr * 0.75 + Math.sin(tt * 9) * hr * 0.04 * (1 - bk); }
    else hy = rimY - hr * 0.3 + Math.sin(now * 2.2) * hr * 0.05;
    ctx.save();
    ctx.beginPath(); ctx.moveTo(0, 0); ctx.lineTo(w, 0); ctx.lineTo(w, rimY); ctx.lineTo(cxr + rx, rimY);
    ctx.ellipse(cxr, rimY, rx, ry, 0, 0, Math.PI, false); ctx.lineTo(0, rimY); ctx.closePath(); ctx.clip();
    ctx.translate(hx, hy); ctx.rotate(mode === 'sur' ? R(launch * 720) : 0); ctx.translate(-hx, -hy);
    pirateHead2(ctx, hx, hy, hr, mode, tt);
    ctx.restore();
    // 3) 통 입구 앞쪽 테두리 + 통을 붙잡은 앞발
    ctx.beginPath(); ctx.ellipse(cxr, rimY, (rr - l + 8) / 2, H * 0.045, 0, 0, Math.PI, false); ctx.ellipse(cxr, rimY, rx, ry, 0, Math.PI, 0, true); ctx.closePath(); ctx.fillStyle = '#6E4220'; ctx.fill();
    if (mode !== 'sur' || launch < 0.15) {
      const lift = mode === 'tease' ? Math.min(1, tt / 0.2) * (tt > 1.05 ? Math.max(0, 1 - (tt - 1.05) / 0.2) : 1) : 0;
      for (const k of [-1, 1]) {
        const px = hx + k * hr * 0.75, py = rimY + H * 0.012 - lift * hr * 0.1;
        ctx.beginPath(); ctx.ellipse(px, py, hr * 0.28, hr * 0.17, 0, 0, 7); ctx.fillStyle = '#9AA5B4'; ctx.fill(); ctx.lineWidth = 1.5; ctx.strokeStyle = 'rgba(0,0,0,.3)'; ctx.stroke();
        for (const q of [-1, 0, 1]) { ctx.beginPath(); ctx.moveTo(px + q * hr * 0.09, py - hr * 0.02); ctx.lineTo(px + q * hr * 0.09, py + hr * 0.12); ctx.stroke(); }
      }
    }
    // 4) 칼 구멍과 칼
    for (let i = 0; i < SLOTS; i++) {
      const [x, y] = slot(i, w, hh);
      ctx.beginPath(); ctx.ellipse(x + sx, y, w * 0.04, w * 0.025, 0, 0, 7); ctx.fillStyle = '#2A170A'; ctx.fill();
      const idx = swords.indexOf(i);
      if (idx >= 0) sword(ctx, x + sx, y, w * 0.11, C.players[idx % C.players.length], popped && i === trap);
    }
    // 5) 메롱~ 말풍선
    if (mode === 'tease' && tt > 0.15 && tt < 1.15) {
      const b = Math.min(1, (tt - 0.15) / 0.2), bob = Math.sin(tt * 8) * 3;
      ctx.save(); ctx.translate(hx + hr * 1.35, hy - hr * 1.15 + bob); ctx.scale(b, b); ctx.rotate(-0.1);
      ctx.font = `900 ${Math.round(hr * 0.42)}px sans-serif`; ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
      const tw = ctx.measureText(s.fingerMerong).width + hr * 0.4, th = hr * 0.62;
      ctx.beginPath(); ctx.roundRect(-tw / 2, -th / 2, tw, th, th / 2); ctx.fillStyle = '#fff'; ctx.fill();
      ctx.beginPath(); ctx.moveTo(-tw * 0.3, th / 2 - 2); ctx.lineTo(-tw * 0.45, th / 2 + hr * 0.2); ctx.lineTo(-tw * 0.1, th / 2 - 2); ctx.fill();
      ctx.fillStyle = '#E53950'; ctx.fillText(s.fingerMerong, 0, 2); ctx.restore();
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

// ===================== 8. 늑대 돌리기 =====================
/**
 * 위에서 본 늑대 (앞 = -y 방향). open: 입 벌린 정도 0~1, bite: 꽉 문 표정
 * 원점은 판 가운데, r 은 판 반지름
 */
function spinWolf(ctx, r, open, bite) {
  const FUR = '#9AA5B4', DARK = '#7D8899', BEL = '#FFF6E8', OL = 'rgba(0,0,0,.4)', INK = '#2B2B33', PINK = '#F6AFC0';
  const s = r, sw = Math.max(1.5, s * 0.02);
  ctx.save();
  ctx.rotate(-Math.PI / 2); // u(→)=앞(코), v(↓)=배 쪽
  ctx.lineJoin = 'round'; ctx.lineCap = 'round';
  const oval = (x, y, rx, ry, fill, stroke, rot = 0) => { ctx.beginPath(); ctx.ellipse(x * s, y * s, rx * s, ry * s, rot, 0, 7); ctx.fillStyle = fill; ctx.fill(); if (stroke) { ctx.lineWidth = sw; ctx.strokeStyle = OL; ctx.stroke(); } };
  const tri = (pts, fill, stroke) => { ctx.beginPath(); pts.forEach(([u, v], i) => (i ? ctx.lineTo(u * s, v * s) : ctx.moveTo(u * s, v * s))); ctx.closePath(); ctx.fillStyle = fill; ctx.fill(); if (stroke) { ctx.lineWidth = sw; ctx.strokeStyle = OL; ctx.stroke(); } };

  // 꼬리 (등 쪽으로 동그랗게 말린 복슬 꼬리, 끝은 흰색) — 가리키는 코끝이랑 헷갈리지 않게
  const tail = new Path2D();
  tail.moveTo(-0.5 * s, 0.0 * s);
  tail.bezierCurveTo(-0.86 * s, 0.04 * s, -0.9 * s, -0.34 * s, -0.64 * s, -0.33 * s);
  tail.quadraticCurveTo(-0.5 * s, -0.31 * s, -0.55 * s, -0.19 * s);
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

export function arrow(root, back) {
  const s = S();
  let secs = 5, angle = Math.random() * 360, spinning = false, stopped = false, open = 0;
  root.append(top(s.menuArrow, back));
  const wrap = h('div', { class: 'body pad center', style: 'gap:10px' });
  root.append(wrap);
  const title = h('div', { class: 'h-title' });
  const holder = h('div', { class: 'cv-wrap', style: 'aspect-ratio:1;max-height:52vh;max-width:52vh' });
  const spinB = btn(s.spin, () => spin());
  wrap.append(title, h('div', { class: 'grow' }), holder, h('div', { class: 'grow' }), stepper(s.arrowSeconds, secs, 1, 15, (v) => { secs = v; }), spinB);

  const render = () => {
    title.textContent = spinning ? s.arrowSpinning : stopped ? s.arrowStopped : s.arrowTap;
    title.classList.toggle('win', stopped && !spinning);
    spinB.disabled = spinning;
  };
  const spin = async () => {
    if (spinning) return;
    spinning = true; stopped = false; vibe.tap(); render();
    animate(180, (p) => { open = p; });
    const from = angle, total = 360 * Math.max(1.5, secs * 0.85) + Math.random() * 360, dur = secs * 1000;
    await animate(dur, (p) => { angle = from + total * p; });
    angle %= 360;
    await animate(130, (p) => { open = 1 - p; }, 'linear'); // 꽉!
    spinning = false; stopped = true; vibe.strong(); render();
  };
  const cv = liveCanvas(holder, (ctx, w, hh) => {
    const cx = w / 2, cy = hh / 2, r = Math.min(w, hh) / 2 - 8;
    ctx.beginPath(); ctx.arc(cx, cy, r, 0, 7); ctx.fillStyle = C.surface; ctx.fill(); ctx.lineWidth = 6; ctx.strokeStyle = C.surface2; ctx.stroke();
    ctx.save(); ctx.translate(cx, cy); ctx.rotate(R(angle));
    spinWolf(ctx, r, open, stopped && !spinning);
    ctx.restore();
  });
  cv.canvas.addEventListener('click', spin);
  render();
  return () => cv.stop();
}
