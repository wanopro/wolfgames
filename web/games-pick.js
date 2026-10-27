// 늑대이빨 / 사다리게임 / 돌림판
import { C, S, h, btn, top, stepper, setupPanel, liveCanvas, vibe, animate, shuffle, rnd } from './core.js';
import { ladderWolf } from './art.js';
import { TW, TH, teethLayout, openMouth, closedFace, RESTART } from './teeth-art.js';

// ===================== 2. 늑대이빨 =====================
export function wolfTeeth(root, back) {
  const s = S();
  let prizes = 1, cv = null;
  root.append(top(s.menuCroc, back));
  const body = h('div', { class: 'body' });
  root.append(body);

  const setup = () => { if (cv) { cv.stop(); cv = null; } body.replaceChildren(setupPanel(s.teethDesc, play, stepper(s.prizes, prizes, 1, 10, (v) => { prizes = v; }))); };

  // 화면 가득 벌린 늑대 입 (큰 이빨 20개) → 당첨이면 콱 닫히고 늑대 얼굴 + 다시 버튼
  const play = () => {
    const TEETH = 20;
    let traps, pressed, found, bitAt = -1, ripples = [];
    const t0 = performance.now(), now = () => (performance.now() - t0) / 1000;
    const stage = h('div', { style: 'position:relative;flex:1;min-height:0;touch-action:manipulation' });
    const bar = h('div', { class: 'row', style: 'padding:6px 12px calc(10px + env(safe-area-inset-bottom))' },
      btn(`🔀 ${s.shuffle}`, () => { vibe.tap(); newRound(); }, 'tbtn'), btn(s.changePrizes, setup, 'tbtn'));
    body.replaceChildren(h('div', { style: 'display:flex;flex-direction:column;height:100%' }, stage, bar));
    let view = { k: 1, ox: 0, oy: 0 };
    cv = liveCanvas(stage, (c, W, H) => {
      const t = now();
      const k = Math.min(W / TW, H / TH), ox = (W - TW * k) / 2, oy = (H - TH * k) / 2; view = { k, ox, oy };
      c.fillStyle = C.bg; c.fillRect(0, 0, W, H);
      c.save(); c.translate(ox, oy); c.scale(k, k);
      const lay = teethLayout(TW, TH), sc = 1;
      lay.pressed = new Set(pressed.map((v, i) => (v ? i : -1)).filter((i) => i >= 0));
      lay.red = new Set([...lay.pressed].filter((i) => traps.has(i)));
      const bt = bitAt >= 0 ? t - bitAt : -1, count = lay.pressed.size, all = found >= prizes;
      if (bt < 0 || bt < 0.1) {
        openMouth(c, TW, TH, lay, sc);
        c.font = '800 15px -apple-system, "Noto Sans KR", sans-serif'; c.textAlign = 'center'; c.textBaseline = 'middle'; c.fillStyle = '#FFD740';
        c.fillText(bt < 0 ? s.teethLeft(TEETH - count, prizes - found) : '', TW / 2, TH * 0.035);
        for (const [tt, i] of ripples) { const u = (t - tt) / 0.4; if (u < 0 || u > 1) continue; const [x, y] = lay.t[i]; c.beginPath(); c.arc(x, y, 26 + 26 * u, 0, 7); c.strokeStyle = `rgba(255,255,255,${0.8 * (1 - u)})`; c.lineWidth = 4; c.stroke(); }
      } else if (bt < 0.22) {
        const u = (bt - 0.1) / 0.12; c.save(); c.translate(0, lay.mid); c.scale(1, 1 - 0.8 * u); c.translate(0, -lay.mid); openMouth(c, TW, TH, lay, sc); c.restore();
      } else {
        const sh = bt < 0.7 ? Math.sin(bt * 70) * 5 * (1 - (bt - 0.22) / 0.5) : 0;
        c.save(); c.translate(sh, 0); closedFace(c, TW, TH, sc, bt < 0.6 ? 1 : 0); c.restore();
        if (bt < 0.4) { c.fillStyle = `rgba(255,40,60,${0.4 * (1 - (bt - 0.22) / 0.18)})`; c.fillRect(0, 0, TW, TH); }
        const u = Math.min(1, (bt - 0.22) / 0.2);
        c.save(); c.translate(TW / 2, TH * 0.08); c.scale(0.6 + 0.4 * u, 0.6 + 0.4 * u);
        c.font = '900 34px -apple-system, "Noto Sans KR", sans-serif'; c.textAlign = 'center'; c.textBaseline = 'middle'; c.lineWidth = 8; c.strokeStyle = '#fff';
        const ttl = s.bitten; c.strokeText(ttl, 0, 0); c.fillStyle = '#FF3D5A'; c.fillText(ttl, 0, 0); c.restore();
        c.font = '700 15px -apple-system, "Noto Sans KR", sans-serif'; c.fillStyle = '#bbb'; c.textAlign = 'center'; c.fillText(all && prizes > 1 ? s.allFound : s.bittenAt(count), TW / 2, TH * 0.14);
        c.font = '800 17px -apple-system, "Noto Sans KR", sans-serif'; c.fillStyle = '#fff';
        c.fillText(all ? s.playAgain : s.continueN(prizes - found), TW / 2, RESTART.y + RESTART.r + 26);
      }
      c.restore();
    });
    // 이빨 위치 (화면 좌표) — 설명서 그림 만들 때도 씀
    cv.canvas.teethAt = () => teethLayout(TW, TH).t.map(([x, y]) => [view.ox + x * view.k, view.oy + y * view.k]);
    cv.canvas.addEventListener('pointerdown', (e) => {
      const r = cv.canvas.getBoundingClientRect(), x = (e.clientX - r.left - view.ox) / view.k, y = (e.clientY - r.top - view.oy) / view.k, t = now();
      if (bitAt >= 0) {
        if (t - bitAt > 0.6 && Math.hypot(x - RESTART.x, y - RESTART.y) < RESTART.r + 14) { vibe.tap(); if (found >= prizes) newRound(); else bitAt = -1; }
        return;
      }
      const lay = teethLayout(TW, TH);
      let best = -1, bd = 46; // 손가락이 닿은 곳에서 가장 가까운 이빨 (넉넉하게)
      lay.t.forEach(([tx, ty], i) => { if (pressed[i]) return; const d = Math.hypot(x - tx, y - ty); if (d < bd) { bd = d; best = i; } });
      if (best < 0) return;
      pressed[best] = true; ripples.push([t, best]); if (ripples.length > 8) ripples.shift();
      if (traps.has(best)) { found++; bitAt = t; vibe.strong(); } else vibe.tap();
    });
    const newRound = () => {
      traps = new Set(shuffle([...Array(TEETH).keys()]).slice(0, prizes));
      pressed = Array(TEETH).fill(false); found = 0; bitAt = -1; ripples = [];
    };
    newRound();
  };

  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== 3. 사다리게임 =====================
export function ladder(root, back) {
  const s = S();
  let n = 4, k = 1;
  root.append(top(s.menuLadder, back));
  const body = h('div', { class: 'body' });
  root.append(body);
  let cv = null;

  const setup = () => {
    if (cv) { cv.stop(); cv = null; }
    const kStep = stepper(s.prizes, k, 1, n - 1, (v) => { k = v; });
    body.replaceChildren(setupPanel(s.ladderDesc, play,
      stepper(s.players, n, 2, 10, (v) => { n = v; kStep.setMax(n - 1); }), kStep));
  };

  const LEVELS = 14;
  const make = () => {
    const rungs = Array.from({ length: LEVELS }, () => Array(n - 1).fill(false));
    for (let l = 0; l < LEVELS; l++) for (let j = 0; j < n - 1; j++) if (!(j > 0 && rungs[l][j - 1]) && Math.random() < 0.4) rungs[l][j] = true;
    for (let j = 0; j < n - 1; j++) {
      if (!rungs.some((r) => r[j])) {
        const cand = [...Array(LEVELS).keys()].filter((l) => (j === 0 || !rungs[l][j - 1]) && (j === n - 2 || !rungs[l][j + 1]));
        if (cand.length) rungs[cand[rnd(0, cand.length - 1)]][j] = true;
      }
    }
    const prizes = shuffle([...Array(k).fill(true), ...Array(n - k).fill(false)]);
    const paths = [...Array(n).keys()].map((st) => {
      let c = st; const pts = [[c, 0]];
      for (let l = 0; l < LEVELS; l++) {
        const y = l + 1;
        if (c < n - 1 && rungs[l][c]) { pts.push([c, y], [c + 1, y]); c++; }
        else if (c > 0 && rungs[l][c - 1]) { pts.push([c, y], [c - 1, y]); c--; }
      }
      pts.push([c, LEVELS + 1]);
      return { pts, end: c };
    });
    return { rungs, prizes, paths };
  };

  const play = () => {
    let data = make();
    const done = new Set(), anim = new Set(), arrived = new Map();
    let prog = 0;
    const wrap = h('div', { class: 'body', style: 'padding:12px;gap:6px;align-items:center' + (n > 7 ? '' : '') });
    if (n > 7) wrap.classList.add('small');
    body.replaceChildren(wrap);
    const nums = h('div', { class: 'lnums' });
    const holder = h('div', { class: 'cv-wrap grow' });
    const res = h('div', { class: 'lres' });
    const status = h('div', { class: 'msg' });
    const allB = btn(s.showAll, () => start([...Array(n).keys()]));
    const newB = btn(s.newLadder, () => { data = make(); done.clear(); render(); }, 'btn alt');
    wrap.append(h('div', { class: 'h-sub', style: 'font-size:14px' }, s.ladderHint), nums, holder, res, status, h('div', { class: 'row' }, allB, newB), btn(s.changeLadder, setup, 'tbtn'));

    const start = async (list) => {
      const go = list.filter((x) => !done.has(x));
      if (anim.size || !go.length) return;
      go.forEach((x) => anim.add(x)); vibe.tap(); render();
      await animate(5000, (p) => { prog = p; }, 'linear');
      go.forEach((x) => { done.add(x); arrived.set(x, performance.now() / 1000); }); anim.clear();
      if (go.some((x) => data.prizes[data.paths[x].end])) vibe.strong(); else vibe.tap();
      render();
    };

    const render = () => {
      nums.replaceChildren(...[...Array(n).keys()].map((i) => {
        const on = done.has(i) || anim.has(i);
        const b = h('button', { style: `background:${C.players[i]}${on ? '' : '59'}` }, i + 1);
        b.onclick = () => start([i]);
        return b;
      }));
      res.replaceChildren(...[...Array(n).keys()].map((e) => {
        const owner = [...done].find((i) => data.paths[i].end === e);
        const rev = owner !== undefined, prize = data.prizes[e];
        return h('div', { style: `background:${!rev ? C.surface2 : prize ? C.win : C.surface};color:${rev && !prize ? C.sub : '#fff'}` },
          !rev ? '?' : prize ? s.win : s.pass,
          rev ? h('span', { class: 'who', style: `color:${C.players[owner]}` }, s.numberN(owner + 1)) : null);
      }));
      const winners = [...done].filter((i) => data.prizes[data.paths[i].end]).sort((a, b) => a - b);
      status.textContent = !done.size ? s.hiddenPrizes(k) : !winners.length ? s.noWinnerYet : s.winnersList(winners.map((i) => s.numberN(i + 1)).join(', '));
      status.classList.toggle('win', winners.length > 0);
      allB.disabled = anim.size > 0 || done.size >= n;
      newB.disabled = anim.size > 0;
    };

    const poly = (ctx, pts, frac, color, lw) => {
      let total = 0;
      for (let i = 1; i < pts.length; i++) total += Math.hypot(pts[i][0] - pts[i - 1][0], pts[i][1] - pts[i - 1][1]);
      let remain = total * frac, tip = pts[pts.length - 1], dir = 0;
      ctx.beginPath(); ctx.moveTo(pts[0][0], pts[0][1]);
      for (let i = 1; i < pts.length; i++) {
        const seg = Math.hypot(pts[i][0] - pts[i - 1][0], pts[i][1] - pts[i - 1][1]);
        dir = Math.sign(pts[i][0] - pts[i - 1][0]);
        if (remain >= seg) { ctx.lineTo(pts[i][0], pts[i][1]); remain -= seg; }
        else { const t = seg ? remain / seg : 0; tip = [pts[i - 1][0] + (pts[i][0] - pts[i - 1][0]) * t, pts[i - 1][1] + (pts[i][1] - pts[i - 1][1]) * t]; ctx.lineTo(tip[0], tip[1]); break; }
      }
      ctx.strokeStyle = color; ctx.lineWidth = lw; ctx.lineCap = 'round'; ctx.lineJoin = 'round'; ctx.stroke();
      tip.dir = dir;
      return tip;
    };

    if (cv) cv.stop();
    cv = liveCanvas(holder, (ctx, w, hh) => {
      const px = (p) => [(p[0] + 0.5) * w / n, p[1] * hh / (LEVELS + 1)];
      ctx.strokeStyle = '#55556A'; ctx.lineWidth = 4; ctx.lineCap = 'butt';
      for (let c = 0; c < n; c++) { const x = (c + 0.5) * w / n; ctx.beginPath(); ctx.moveTo(x, 0); ctx.lineTo(x, hh); ctx.stroke(); }
      for (let l = 0; l < LEVELS; l++) for (let j = 0; j < n - 1; j++) if (data.rungs[l][j]) {
        const y = (l + 1) * hh / (LEVELS + 1);
        ctx.beginPath(); ctx.moveTo((j + 0.5) * w / n, y); ctx.lineTo((j + 1.5) * w / n, y); ctx.stroke();
      }
      const now = performance.now() / 1000, wr = Math.min(19, w / n * 0.3);
      for (const i of done) poly(ctx, data.paths[i].pts.map(px), 1, C.players[i], 7);
      // 내려가는 늑대 얼굴: 입을 앙앙 벌렸다 닫았다, 목에 플레이어 색 목도리
      for (const i of anim) {
        const tip = poly(ctx, data.paths[i].pts.map(px), prog, C.players[i], 7);
        ladderWolf(ctx, tip[0], tip[1] - wr * 0.3, wr, now + i * 0.3, C.players[i], prog >= 1 ? 0 : tip.dir);
      }
      // 도착한 늑대는 결과 칸 위에서 "앙!" 한 번 하고 쉼
      for (const i of done) {
        const p = data.paths[i].pts, [x, y] = px(p[p.length - 1]), since = now - (arrived.get(i) || 0);
        ladderWolf(ctx, x, y - wr * 0.9, wr * 0.85, now + i, C.players[i], 0, since < 0.5 ? Math.abs(Math.sin(since * Math.PI * 3)) : 0);
      }
    });
    render();
  };

  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== 4. 돌림판 =====================
export function roulette(root, back) {
  const s = S();
  let m = 8, k = 1;
  root.append(top(s.menuRoulette, back));
  const body = h('div', { class: 'body' });
  root.append(body);
  let cv = null;
  const PAL = ['#3949AB', '#00897B', '#7B1FA2', '#1E88E5', '#43A047', '#5E35B1'];

  const setup = () => {
    if (cv) { cv.stop(); cv = null; }
    const kStep = stepper(s.prizes, k, 1, m - 1, (v) => { k = v; });
    body.replaceChildren(setupPanel(s.rouletteDesc, play,
      stepper(s.slices, m, 2, 20, (v) => { m = v; kStep.setMax(m - 1); }), kStep));
  };

  const play = () => {
    let wins = shuffle([...Array(k).fill(true), ...Array(m - k).fill(false)]);
    let rot = 0, spinning = false, result = null;
    const wrap = h('div', { class: 'body pad center', style: 'gap:12px' });
    body.replaceChildren(wrap);
    const title = h('div', { class: 'h-title' });
    const holder = h('div', { class: 'cv-wrap', style: 'aspect-ratio:1;max-height:56vh;max-width:56vh' });
    const spinB = btn(s.spin, () => spin());
    const mixB = btn(s.shuffleWins, () => { wins = shuffle(wins); result = null; render(); }, 'btn alt');
    const chB = btn(s.changeWheel, setup, 'tbtn');
    wrap.append(title, h('div', { class: 'h-sub', style: 'font-size:14px;margin-top:-8px' }, s.wheelInfo(m, k)), h('div', { class: 'grow' }), holder, h('div', { class: 'grow' }), h('div', { class: 'row' }, spinB, mixB), chB);

    const render = () => {
      const r = result;
      title.textContent = r != null && wins[r] ? s.sliceWin(r + 1) : r != null ? s.slicePass(r + 1) : spinning ? s.drumroll : s.tapWheel;
      title.classList.toggle('win', r != null && wins[r]);
      spinB.disabled = mixB.disabled = chB.disabled = spinning;
    };
    const spin = async () => {
      if (spinning) return;
      spinning = true; result = null; vibe.tap(); render();
      const from = rot, total = 360 * rnd(4, 5) + Math.random() * 360;
      await animate(5000, (p) => { rot = from + total * p; });
      rot %= 360;
      const sweep = 360 / m, a = (((-rot) % 360) + 360) % 360;
      result = Math.min(m - 1, Math.floor(a / sweep));
      spinning = false; vibe.strong(); render();
    };

    cv = liveCanvas(holder, (ctx, w, hh) => {
      const c = { x: w / 2, y: hh / 2 }, rad = Math.min(w, hh) / 2 - 10, sweep = 360 / m;
      const R = (d) => (d * Math.PI) / 180;
      for (let i = 0; i < m; i++) {
        const st = -90 + rot + i * sweep;
        ctx.beginPath(); ctx.moveTo(c.x, c.y); ctx.arc(c.x, c.y, rad, R(st), R(st + sweep)); ctx.closePath();
        ctx.fillStyle = wins[i] ? (i % 2 === 0 ? '#E53950' : '#FF6B7F') : PAL[i % PAL.length]; ctx.fill();
        ctx.strokeStyle = C.bg; ctx.lineWidth = 2; ctx.stroke();
      }
      if (result != null) {
        const st = -90 + rot + result * sweep;
        ctx.beginPath(); ctx.moveTo(c.x, c.y); ctx.arc(c.x, c.y, rad, R(st), R(st + sweep)); ctx.closePath();
        ctx.strokeStyle = C.accent; ctx.lineWidth = 6; ctx.stroke();
      }
      const fs = m <= 8 ? 20 : m <= 14 ? 15 : 11;
      ctx.font = `700 ${fs}px sans-serif`; ctx.textAlign = 'center'; ctx.textBaseline = 'middle'; ctx.fillStyle = '#fff';
      for (let i = 0; i < m; i++) {
        const mid = -90 + rot + i * sweep + sweep / 2;
        ctx.save(); ctx.translate(c.x, c.y); ctx.rotate(R(mid + 90));
        ctx.fillText(wins[i] ? s.win : String(i + 1), 0, -rad * 0.72); ctx.restore();
      }
      ctx.beginPath(); ctx.arc(c.x, c.y, rad * 0.2, 0, 7); ctx.fillStyle = C.bg; ctx.fill();
      ctx.beginPath(); ctx.arc(c.x, c.y, rad * 0.17, 0, 7); ctx.fillStyle = C.accent; ctx.fill();
      ctx.font = '900 16px sans-serif'; ctx.fillStyle = '#000'; ctx.fillText(s.spinHub, c.x, c.y);
      ctx.beginPath(); ctx.moveTo(c.x - 16, c.y - rad - 6); ctx.lineTo(c.x + 16, c.y - rad - 6); ctx.lineTo(c.x, c.y - rad + 24); ctx.closePath();
      ctx.fillStyle = '#fff'; ctx.fill(); ctx.strokeStyle = '#000'; ctx.lineWidth = 2; ctx.stroke();
    });
    cv.canvas.addEventListener('click', spin);
    render();
  };

  setup();
  return () => { if (cv) cv.stop(); };
}
