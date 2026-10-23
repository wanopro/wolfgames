// 늑대이빨 / 사다리게임 / 돌림판
import { C, S, h, btn, top, stepper, setupPanel, liveCanvas, vibe, animate, shuffle, rnd } from './core.js';
import { ladderWolf } from './art.js';

// ===================== 2. 늑대이빨 =====================
export function wolfTeeth(root, back) {
  const s = S();
  let prizes = 1;
  root.append(top(s.menuCroc, back));
  const body = h('div', { class: 'body' });
  root.append(body);

  const setup = () => body.replaceChildren(setupPanel(s.teethDesc, play, stepper(s.prizes, prizes, 1, 10, (v) => { prizes = v; })));

  const play = () => {
    const TEETH = 20;
    let traps, pressed, bitten, found;
    const wrap = h('div', { class: 'body pad center' });
    body.replaceChildren(wrap);
    const title = h('div', { class: 'h-title' });
    const sub = h('div', { class: 'h-sub' });
    const wolf = h('div', { class: 'wolf' });
    const actions = h('div', { style: 'width:100%' });
    wrap.append(title, sub, h('div', { class: 'grow' }), wolf, h('div', { class: 'grow' }), actions, btn(s.changePrizes, setup, 'tbtn'));

    // 귀 + 얼굴 + 입 (앱과 같은 비율)
    const W = 361, IW = 329;
    const ears = () => {
      let g = '';
      for (const cx of [W * 0.24, W * 0.76]) {
        const half = W * 0.15, sk = cx < W / 2 ? -half * 0.15 : half * 0.15, ih = half * 0.55, sk2 = cx < W / 2 ? -ih * 0.15 : ih * 0.15;
        g += `<polygon points="${cx - half},80 ${cx + sk},0 ${cx + half},80" fill="#6E7A8A"/><polygon points="${cx - ih},80 ${cx + sk2},24 ${cx + ih},80" fill="#F6AFC0"/>`;
      }
      return `<svg viewBox="0 0 ${W} 80" width="100%" style="position:absolute;top:0;left:0">${g}</svg>`;
    };
    const upper = (closed) => {
      const w = IW, hh = 120, ey = hh * 0.38, er = hh * 0.13;
      let g = `<polygon points="${w * .42},0 ${w * .5},${hh * .32} ${w * .58},0" fill="#F4ECE1" fill-opacity=".7"/>`;
      g += `<ellipse cx="${w / 2}" cy="${hh * 0.83}" rx="${w * 0.23}" ry="${hh * 0.35}" fill="#F4ECE1"/>`;
      for (const ex of [w * 0.3, w * 0.7]) {
        if (closed) {
          const d = er * 0.9, sg = ex < w / 2 ? 1 : -1;
          g += `<polyline points="${ex - d * sg},${ey - d} ${ex + d * sg},${ey} ${ex - d * sg},${ey + d}" fill="none" stroke="#2B2B33" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>`;
        } else {
          g += `<circle cx="${ex}" cy="${ey}" r="${er}" fill="#2B2B33"/><circle cx="${ex - er * .3}" cy="${ey - er * .35}" r="${er * .38}" fill="#fff"/><circle cx="${ex + er * .35}" cy="${ey + er * .3}" r="${er * .16}" fill="#fff"/>`;
        }
        const off = ex < w / 2 ? -er * 0.6 : er * 0.6;
        g += `<ellipse cx="${ex + off}" cy="${ey + er * 2}" rx="${er * 1.4}" ry="${er * 0.7}" fill="#FF8FA3" fill-opacity=".4"/>`;
      }
      const nw = w * 0.13, nh = hh * 0.14;
      g += `<ellipse cx="${w / 2}" cy="${hh * .62 + nh / 2}" rx="${nw / 2}" ry="${nh / 2}" fill="#2B2B33"/><ellipse cx="${w / 2 - nw * .1}" cy="${hh * .665}" rx="${nw * .15}" ry="${nh * .15}" fill="#fff" fill-opacity=".5"/>`;
      g += `<line x1="${w / 2}" y1="${hh * .62 + nh}" x2="${w / 2}" y2="${hh}" stroke="#2B2B33" stroke-width="2.5" stroke-linecap="round"/>`;
      return `<svg viewBox="0 0 ${w} ${hh}" width="100%">${g}</svg>`;
    };
    const tooth = (i, down) => {
      const tw = (IW - 12) / 10, th = 46, x = (i % 10) * tw, ins = tw * 0.06;
      const pr = pressed[i], tip = pr ? 0.5 : 1;
      const col = pr && traps.has(i) ? C.win : pr ? '#5E5E66' : '#FFFDF5';
      const d = down
        ? `M${x + ins},0 L${x + tw - ins},0 Q${x + tw * .62},${th * tip * .75} ${x + tw / 2},${th * tip} Q${x + tw * .38},${th * tip * .75} ${x + ins},0Z`
        : `M${x + ins},${th} L${x + tw - ins},${th} Q${x + tw * .62},${th - th * tip * .75} ${x + tw / 2},${th - th * tip} Q${x + tw * .38},${th - th * tip * .75} ${x + ins},${th}Z`;
      return `<rect class="tooth" data-i="${i}" x="${x}" y="0" width="${tw}" height="${th}" fill="transparent"/><path class="tooth" data-i="${i}" d="${d}" fill="${col}"/>`;
    };
    const row = (from, down) => {
      let g = '';
      for (let i = from; i < from + 10; i++) g += tooth(i, down);
      return `<svg viewBox="0 0 ${IW - 12} 46" style="width:calc(100% - 12px);margin:0 6px">${g}</svg>`;
    };
    const drawWolf = () => {
      wolf.classList.toggle('bit', bitten);
      wolf.innerHTML = ears() + `<div class="face">${upper(bitten)}<div class="mouth">${row(0, true)}<div class="gap"><div class="tongue"></div></div>${row(10, false)}</div>` +
        `<div style="height:40px;position:relative;overflow:hidden"><div style="position:absolute;left:20%;width:60%;top:-16px;height:44px;border-radius:50%;background:#F4ECE1"></div></div></div>`;
    };
    // 입이 닫히는 애니메이션을 위해 바뀐 부분만 갱신
    const refresh = () => {
      const count = pressed.filter(Boolean).length;
      const all = found >= prizes;
      title.textContent = bitten && all ? s.allFound : bitten ? s.bitten : s.pressTeeth;
      title.classList.toggle('win', bitten);
      sub.textContent = bitten ? s.bittenAt(count) : s.teethLeft(TEETH - count, prizes - found);
      wolf.classList.toggle('bit', bitten);
      wolf.querySelector('.face > svg').outerHTML = upper(bitten);
      const rows = wolf.querySelectorAll('.mouth > svg');
      rows[0].outerHTML = row(0, true);
      rows[1].outerHTML = row(10, false);
      actions.replaceChildren(
        bitten && !all ? btn(s.continueN(prizes - found), () => { bitten = false; refresh(); })
          : bitten ? btn(s.playAgain, newRound)
            : btn(s.shuffle, newRound, 'btn alt'));
    };
    wolf.addEventListener('click', (e) => {
      const t = e.target.closest('.tooth');
      if (!t || bitten) return;
      const i = +t.dataset.i;
      if (pressed[i]) return;
      pressed[i] = true;
      if (traps.has(i)) { found++; bitten = true; vibe.strong(); } else vibe.tap();
      refresh();
    });
    const newRound = () => {
      traps = new Set(shuffle([...Array(TEETH).keys()]).slice(0, prizes));
      pressed = Array(TEETH).fill(false); bitten = false; found = 0;
      drawWolf(); refresh();
    };
    newRound();
  };

  setup();
  return () => {};
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
