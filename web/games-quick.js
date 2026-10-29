// 순발력게임 3종(반응속도 대결 · 색 반응 대결 · 스탑워치) + 폭탄돌리기
// 모두 원형 화면 분할: 화면 가운데를 중심으로 인원수만큼 같은 넓이로 둘러앉은 모양
import { C, S, h, btn, top, stepper, setupPanel, liveCanvas, vibe, rnd, SEP } from './core.js';
import { sectors, region, lines, text, badge, inPoly } from './split.js';
import { bombArt, chompWolf } from './art.js';
// 두 언어일 때 위아래로 붙은 글자 사이를 조금 더 벌림
const DU = () => (String(S().start).includes(SEP) ? 9 : 0);

const GOLD = '#FFC107', RED = '#FF3D5A';
const PC = C.players;

/** 분할 화면 공용 틀. game = { draw(ctx,W,H,t,secs), tap(i,t), hub(t) }. 반환: { stop } */
export function splitStage(root, title, onBack, game) {
  const s = S();
  const body = h('div', { class: 'body', style: 'touch-action:none' });
  root.replaceChildren(top(title, onBack), body);
  const t0 = performance.now(), now = () => (performance.now() - t0) / 1000;
  let secs = [], size = { w: 0, h: 0 };
  const cv = liveCanvas(body, (ctx, w, hh) => {
    if (w !== size.w || hh !== size.h) { size = { w, h: hh }; secs = sectors(w, hh, game.n); }
    ctx.fillStyle = C.bg; ctx.fillRect(0, 0, w, hh);
    game.draw(ctx, w, hh, now(), secs, s);
  });
  cv.canvas.addEventListener('pointerdown', (e) => {
    e.preventDefault();
    const r = cv.canvas.getBoundingClientRect(), p = [e.clientX - r.left, e.clientY - r.top];
    if (Math.hypot(p[0] - r.width / 2, p[1] - r.height / 2) < 48) { game.hub && game.hub(now()); return; }
    const i = secs.findIndex((sc) => inPoly(p, sc.poly));
    if (i >= 0) game.tap(i, now(), p, secs[i]);
  });
  return cv;
}
export function hub(ctx, W, H, r, fill, draw) {
  ctx.save(); ctx.beginPath(); ctx.arc(W / 2, H / 2, r, 0, 7); ctx.fillStyle = fill; ctx.fill();
  ctx.lineWidth = 5; ctx.strokeStyle = 'rgba(0,0,0,.6)'; ctx.stroke(); if (draw) draw(); ctx.restore();
}
const pname = (c, i, sc) => text(c, `P${i + 1}`, 0, -78 * sc, 20 * sc, PC[i]);
const rankStyle = (s, rank, last) => (rank === 0 ? { glow: GOLD, tag: s.rankFirst, bg: GOLD, fg: '#000' } : rank === last ? { glow: RED, tag: s.rankLast, bg: RED, fg: '#fff' } : null);
const againHint = (ctx, W, H, s, show) => { if (show) text(ctx, s.tapHubAgain, W / 2, H / 2 + 62, 13, '#fff', 700); };
const fmt = (v, d) => v.toFixed(d);

// ===================== ⚡ 반응속도 대결 =====================
export function reaction(root, back) {
  const s = S();
  let n = 2, cv = null;
  const setup = () => {
    if (cv) { cv.stop(); cv = null; }
    root.replaceChildren(top(s.reactTitle, back), h('div', { class: 'body' }, setupPanel(s.reactDesc, play, stepper(s.players, n, 2, 4, (v) => { n = v; }))));
  };
  const play = () => {
    const g = { n };
    const round = (t) => { g.goAt = t + 2 + Math.random() * 3; g.tap = Array(n).fill(null); g.doneAt = 0; g.buzz = false; };
    round(0);
    g.tap = Array(n).fill(null);
    g.tapFn = null;
    g.tap_ = (i, t) => {
      if (g.tap[i] !== null) return;
      g.tap[i] = t < g.goAt ? 'foul' : t - g.goAt; vibe.tap();
      if (g.tap.every((v) => v !== null)) { g.doneAt = t; vibe.strong(); }
    };
    const game = {
      n,
      tap: (i, t) => g.tap_(i, t),
      hub: (t) => { if (g.doneAt) { round(t); vibe.tap(); } },
      draw(ctx, W, H, t, secs) {
        const go = t >= g.goAt, done = !!g.doneAt;
        if (go && !g.buzz) { g.buzz = true; vibe.tick(); }
        const valid = g.tap.map((v, i) => [v, i]).filter(([v]) => typeof v === 'number').sort((a, b) => a[0] - b[0]).map(([, i]) => i);
        const order = [...valid, ...g.tap.map((v, i) => (v === 'foul' ? i : -1)).filter((i) => i >= 0)];
        secs.forEach((sec, i) => {
          const v = g.tap[i], st = done ? rankStyle(s, order.indexOf(i), n - 1) : null;
          const fill = v === 'foul' ? '#4A1820' : !go ? '#3B2A2E' : v !== null ? '#1F6B43' : '#2ECC71';
          region(ctx, sec, fill, (c, sc) => {
            pname(c, i, sc);
            if (v === 'foul') { text(c, s.reactFoul, 0, (-10 - DU()) * sc, 30 * sc, '#FF8A80', 900); text(c, s.reactFoulSub, 0, (30 + DU()) * sc, 16 * sc, '#FFCDD2', 600); }
            else if (v !== null) text(c, s.secs(fmt(v, 3)), 0, -6 * sc, 44 * sc, '#fff', 900);
            else if (go) text(c, s.reactGo, 0, -6 * sc, 40 * sc, '#0B3D22', 900);
            else { text(c, s.reactWait, 0, (-10 - DU()) * sc, 30 * sc, '#F2C9CF', 800); text(c, s.reactWaitSub, 0, (28 + DU()) * sc, 16 * sc, '#C9A3AA', 600); }
            if (st) badge(c, st.tag, 0, 52 * sc, sc, st.bg, st.fg);
          }, st ? st.glow : null);
        });
        lines(ctx, secs, W, H);
        hub(ctx, W, H, 34, go && !done ? '#2ECC71' : '#3B2A2E', () => text(ctx, done ? '🔄' : go ? 'GO' : '⏳', W / 2, H / 2 + 1, 22, go && !done ? '#0B3D22' : '#fff', 900));
        againHint(ctx, W, H, s, done);
      },
    };
    cv = splitStage(root, s.reactTitle, setup, game);
  };
  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== 🎨 색 반응 대결 =====================
export const COLORS = ['#E53935', '#FB8C00', '#FDD835', '#43A047', '#1E88E5', '#8E24AA'];
export function colorGame(root, back) {
  const s = S();
  const names = s.colorNames.split(',');
  let n = 2, cv = null;
  const WINDOW = 1.5;
  const setup = () => {
    if (cv) { cv.stop(); cv = null; }
    root.replaceChildren(top(s.colorTitle, back), h('div', { class: 'body' }, setupPanel(s.colorDesc, play, stepper(s.players, n, 2, 4, (v) => { n = v; }))));
  };
  const play = () => {
    const g = {};
    const round = (t0) => {
      g.target = rnd(0, COLORS.length - 1);
      g.seq = []; let t = t0 + 0.6, c = -1;
      const appear = t0 + 2.5 + Math.random() * 4;
      while (t < appear) { let k; do k = rnd(0, COLORS.length - 1); while (k === g.target || k === c); c = k; g.seq.push([t, k]); t += 0.1 + Math.random() * 0.4; }
      g.appearAt = t; g.tap = Array(n).fill(null); g.doneAt = 0; g.buzz = false; g.start = t0;
    };
    round(0);
    const colorAt = (t) => { if (t >= g.appearAt) return g.target; let c = -1; for (const [at, k] of g.seq) if (t >= at) c = k; return c; };
    const game = {
      n,
      tap: (i, t) => {
        if (g.doneAt || g.tap[i] !== null) return;
        const cur = colorAt(t);
        if (cur < 0) return; // 시작 전
        g.tap[i] = cur === g.target && t <= g.appearAt + WINDOW ? t - g.appearAt : 'wrong';
        if (g.tap[i] === 'wrong') vibe.strong(); else vibe.tap();
        if (g.tap.every((v) => v !== null)) g.doneAt = t;
      },
      hub: (t) => { if (g.doneAt) { round(t); vibe.tap(); } },
      draw(ctx, W, H, t, secs) {
        if (!g.doneAt && t > g.appearAt + WINDOW) { g.tap = g.tap.map((v) => (v === null ? 'miss' : v)); g.doneAt = t; vibe.strong(); }
        if (t >= g.appearAt && !g.buzz) { g.buzz = true; vibe.tick(); }
        const done = !!g.doneAt, cur = colorAt(t);
        const caught = g.tap.map((v, i) => (v === 'miss' || v === 'wrong' ? i : -1)).filter((i) => i >= 0);
        const order = g.tap.map((v, i) => [v, i]).filter(([v]) => typeof v === 'number').sort((a, b) => a[0] - b[0]).map(([, i]) => i);
        secs.forEach((sec, i) => {
          const v = g.tap[i]; let st = null;
          if (done) st = caught.length ? (caught.includes(i) ? { glow: RED, tag: s.caught, bg: RED, fg: '#fff' } : null) : rankStyle(s, order.indexOf(i), n - 1);
          const fill = done ? (caught.includes(i) ? '#4A1820' : '#1E1E2A') : cur < 0 ? '#1E1E2A' : COLORS[cur];
          region(ctx, sec, fill, (c, sc) => {
            pname(c, i, sc);
            if (v === 'wrong') text(c, s.colorWrong, 0, -6 * sc, 32 * sc, '#FF8A80', 900);
            else if (v === 'miss') text(c, s.colorMiss, 0, -6 * sc, 32 * sc, '#FF8A80', 900);
            else if (typeof v === 'number') text(c, s.secs(fmt(v, 3)), 0, -6 * sc, 42 * sc, '#fff', 900);
            else { c.save(); c.shadowColor = 'rgba(0,0,0,.55)'; c.shadowBlur = 6; text(c, s.colorWhen(names[g.target]), 0, (-22 - DU()) * sc, 26 * sc, '#fff', 900); text(c, s.colorTap, 0, (16 + DU()) * sc, 34 * sc, '#fff', 900); c.restore(); }
            if (st) badge(c, st.tag, 0, 52 * sc, sc, st.bg, st.fg);
          }, st ? st.glow : null);
        });
        lines(ctx, secs, W, H);
        hub(ctx, W, H, 46, '#1E1E2A', () => {
          ctx.beginPath(); ctx.arc(W / 2, H / 2, 34, 0, 7); ctx.fillStyle = COLORS[g.target]; ctx.fill(); ctx.lineWidth = 4; ctx.strokeStyle = '#fff'; ctx.stroke();
          text(ctx, done ? '🔄' : s.colorThis, W / 2, H / 2 + 1, done ? 22 : 14, '#fff', 900);
        });
        againHint(ctx, W, H, s, done);
      },
    };
    cv = splitStage(root, s.colorTitle, setup, game);
  };
  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== ⏱️ 스탑워치 =====================
export function stopwatch(root, back) {
  const s = S();
  let n = 2, target = 7, cv = null;
  const setup = () => {
    if (cv) { cv.stop(); cv = null; }
    root.replaceChildren(top(s.stopTitle, back), h('div', { class: 'body' }, setupPanel(s.stopDesc, play,
      stepper(s.players, n, 2, 4, (v) => { n = v; }), stepper(s.stopSeconds, target, 3, 30, (v) => { target = v; }))));
  };
  const play = () => {
    const g = {};
    const round = (t) => { g.startAt = t + 3; g.tap = Array(n).fill(null); g.doneAt = 0; g.last = 4; };
    round(0);
    const game = {
      n,
      tap: (i, t) => { if (g.doneAt || t < g.startAt || g.tap[i] !== null) return; g.tap[i] = t - g.startAt; vibe.tap(); if (g.tap.every((v) => v !== null)) { g.doneAt = t; vibe.strong(); } },
      hub: (t) => { if (g.doneAt) { round(t); vibe.tap(); } },
      draw(ctx, W, H, t, secs) {
        const started = t >= g.startAt, done = !!g.doneAt;
        const cd = Math.ceil(g.startAt - t); if (!started && cd !== g.last) { g.last = cd; vibe.tick(); }
        if (started && g.last > 0) { g.last = 0; vibe.strong(); }
        const order = g.tap.map((v, i) => [v, i]).sort((a, b) => Math.abs(a[0] - target) - Math.abs(b[0] - target)).map(([, i]) => i);
        secs.forEach((sec, i) => {
          const v = g.tap[i], st = done ? rankStyle(s, order.indexOf(i), n - 1) : null;
          region(ctx, sec, done ? '#1E1E2A' : v !== null ? '#2A3550' : '#24304A', (c, sc) => {
            pname(c, i, sc);
            if (done) { text(c, s.secs(fmt(v, 2)), 0, -14 * sc, 44 * sc, '#fff', 900); const d = v - target; text(c, s.secs(`${d >= 0 ? '+' : ''}${fmt(d, 2)}`), 0, 24 * sc, 20 * sc, Math.abs(d) < 0.3 ? '#69F0AE' : '#FFAB91', 800); }
            else if (v !== null) text(c, s.stopDone, 0, -6 * sc, 26 * sc, '#BBDEFB', 800);
            else if (started) { text(c, s.stopCount(target), 0, (-20 - DU()) * sc, 24 * sc, '#fff', 900); text(c, s.stopTapNow, 0, (14 + DU()) * sc, 34 * sc, '#FFD740', 900); }
            else text(c, String(cd), 0, -6 * sc, 56 * sc, '#fff', 900);
            if (st) badge(c, st.tag, 0, 58 * sc, sc, st.bg, st.fg);
          }, st ? st.glow : null);
        });
        lines(ctx, secs, W, H);
        hub(ctx, W, H, 44, '#121826', () => { if (done) text(ctx, '🔄', W / 2, H / 2 + 1, 24, '#fff', 900); else { text(ctx, String(target), W / 2, H / 2 - 5, 34, '#FFD740', 900); text(ctx, s.secs(''), W / 2, H / 2 + 22, 12, '#FFD740', 800); } });
        againHint(ctx, W, H, s, done);
      },
    };
    cv = splitStage(root, s.stopTitle, setup, game);
  };
  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== 💣 폭탄돌리기 =====================
/** 방향 고르기 (시계 · 반시계 · 랜덤) */
export function choice(label, opts, value, onChange) {
  const row = h('div', { class: 'choice' });
  const draw = () => row.replaceChildren(...opts.map(([v, txt]) => btn(txt, () => { value = v; onChange(v); draw(); }, 'cbtn' + (v === value ? ' on' : ''))));
  draw();
  return h('div', { class: 'stepper', style: 'flex-direction:column;align-items:stretch;gap:10px' }, h('span', { class: 'lbl' }, label), row);
}
export function bombGame(root, back) {
  const s = S();
  let n = 4, dir = 'cw', cv = null;
  const setup = () => {
    if (cv) { cv.stop(); cv = null; }
    root.replaceChildren(top(s.bombTitle, back), h('div', { class: 'body' }, setupPanel(s.bombDesc, play,
      stepper(s.players, n, 2, 8, (v) => { n = v; }),
      choice(s.bombDir, [['cw', `↻ ${s.dirCw}`], ['ccw', `↺ ${s.dirCcw}`], ['rand', `🔀 ${s.dirRandom}`]], dir, (v) => { dir = v; }))));
  };
  const play = () => {
    const g = {};
    const round = (t) => { g.holder = rnd(0, n - 1); g.start = t; g.explodeAt = t + 10 + Math.random() * 20; g.fly = null; g.boomed = false; };
    round(0);
    const game = {
      n,
      tap: (i, t) => {
        if (g.boomed || i !== g.holder || g.fly || t >= g.explodeAt) return;
        let to;
        if (dir === 'cw') to = (i + 1) % n; else if (dir === 'ccw') to = (i - 1 + n) % n; else { do to = rnd(0, n - 1); while (to === i); }
        g.fly = { from: i, to, at: t }; g.holder = to; vibe.tap();
      },
      hub: (t) => { if (g.boomed && t - g.explodeAt > 1.5) { round(t); vibe.tap(); } },
      draw(ctx, W, H, t, secs) {
        const boom = t >= g.explodeAt, bt = t - g.explodeAt;
        if (boom && !g.boomed) { g.boomed = true; g.fly = null; vibe.strong(); }
        if (g.fly && t - g.fly.at > 0.35) g.fly = null;
        secs.forEach((sec, i) => {
          const mine = i === g.holder && !g.fly, lose = boom && i === g.holder;
          const fill = lose ? (bt < 0.15 ? '#FFF3E0' : '#5A1A22') : mine ? '#4A3A1E' : '#1E1E2A';
          region(ctx, sec, fill, (c, sc) => {
            pname(c, i, sc);
            if (lose) { if (bt > 0.6) badge(c, s.caught, 0, 70 * sc, sc, RED, '#fff'); }
            else if (mine && !boom) text(c, s.bombPass, 0, 62 * sc, 22 * sc, '#FFD740', 900);
          }, lose && bt > 0.6 ? RED : null);
        });
        lines(ctx, secs, W, H);
        hub(ctx, W, H, 34, '#121826', () => text(ctx, boom && bt > 1.5 ? '🔄' : dir === 'cw' ? '↻' : dir === 'ccw' ? '↺' : '🔀', W / 2, H / 2 + 2, boom ? 24 : 30, '#FFD740', 900));
        const pos = (k) => secs[k].anchor;
        const fuse = Math.max(0, 1 - (t - g.start) / (g.explodeAt - g.start));
        if (!boom) {
          let x, y, sc = 1;
          if (g.fly) { const u = Math.min(1, (t - g.fly.at) / 0.35), [x0, y0] = pos(g.fly.from), [x1, y1] = pos(g.fly.to); x = x0 + (x1 - x0) * u; y = y0 + (y1 - y0) * u - Math.sin(u * Math.PI) * 60; sc = 1 + Math.sin(u * Math.PI) * 0.3; }
          else { [x, y] = pos(g.holder); x += Math.sin(t * 30) * 2 * (1.3 - fuse); }
          bombArt(ctx, x, y, 30 * sc, fuse, t);
        } else {
          const [x, y] = pos(g.holder), sec = secs[g.holder];
          if (bt < 0.6) {
            const u = bt / 0.6; ctx.save(); ctx.globalAlpha = 1 - u;
            for (let k = 0; k < 14; k++) { const a = k * Math.PI * 2 / 14; ctx.beginPath(); ctx.moveTo(x, y); ctx.lineTo(x + Math.cos(a) * 150 * u + 20, y + Math.sin(a) * 150 * u); ctx.strokeStyle = k % 2 ? '#FFB300' : '#FF5722'; ctx.lineWidth = 10; ctx.stroke(); }
            ctx.beginPath(); ctx.arc(x, y, 30 + 110 * u, 0, 7); ctx.fillStyle = 'rgba(255,200,80,.6)'; ctx.fill(); ctx.restore();
            ctx.save(); ctx.translate(x, y); ctx.rotate(sec.rot); text(ctx, s.bombBoom, 0, -60, 46 * (0.6 + u), '#FF5722', 900); ctx.restore();
          }
          if (bt > 0.35) {
            const u = Math.min(1, (bt - 0.35) / 0.25), pop = u < 1 ? 1 - Math.pow(1 - u, 3) * Math.cos(u * 8) : 1;
            const ch = bt - 0.6, bite = ch < 0 ? 1 : ch > 1.4 ? 0 : Math.abs(Math.cos(ch * Math.PI * 1.4));
            ctx.save(); ctx.translate(x, y); ctx.rotate(sec.rot); chompWolf(ctx, 0, -8, 46, bite, pop);
            if (ch > 0.1) text(ctx, s.biteWord, 60, -58, 26, RED, 900); ctx.restore();
          }
          againHint(ctx, W, H, s, bt > 1.5);
        }
      },
    };
    cv = splitStage(root, s.bombTitle, setup, game);
  };
  setup();
  return () => { if (cv) cv.stop(); };
}
