// 주사위 게임 4종: 주사위 굴리기 / 숫자 뒤집기 잭팟 / 5개 주사위 / 789
import { C, S, h, btn, top, stepper, toggle, setupPanel, resultOverlay, liveCanvas, vibe, drawDie, dieSvg, sleep, rnd, animate } from './core.js';

// ===================== 1-1 주사위 굴리기 =====================
export function rollDice(root, back) {
  const s = S();
  const HOLD = 500, TAP = 300, DTAP = 400;
  const active = new Map();
  const placed = [];
  let lastTap = 0, lastPos = { x: 0, y: 0 };
  const reset = () => { placed.length = 0; };

  const body = h('div', { class: 'body' });
  const hint = h('div', { class: 'h-sub', style: 'position:absolute;inset:0;display:flex;align-items:center;justify-content:center;font-size:18px;line-height:28px;padding:24px;white-space:pre-line;pointer-events:none' }, s.rollHint);
  root.append(top(s.rollTitle, back, btn(s.reset, reset, 'tbtn accent')), body);
  body.append(hint);

  const pick = () => {
    const used = [...active.values()].map((a) => a.color).concat(placed.map((p) => p.color));
    return C.players.find((c) => !used.includes(c)) || C.players[rnd(0, C.players.length - 1)];
  };
  const pos = (e) => { const r = cv.canvas.getBoundingClientRect(); return { x: e.clientX - r.left, y: e.clientY - r.top }; };

  const cv = liveCanvas(body, (ctx) => {
    const now = performance.now();
    hint.style.display = active.size || placed.length ? 'none' : 'flex';
    for (const d of placed) {
      ctx.beginPath(); ctx.arc(d.x, d.y, 47.5, 0, 7); ctx.fillStyle = d.color + '38'; ctx.fill();
      drawDie(ctx, d.x, d.y, 64, d.value, d.color);
    }
    for (const a of active.values()) {
      const p = Math.min(1, (now - a.start) / HOLD);
      if (!a.ready && p >= 1) { a.ready = true; vibe.strong(); }
      ctx.beginPath(); ctx.arc(a.x, a.y, 50, 0, 7); ctx.fillStyle = a.color; ctx.fill();
      ctx.beginPath(); ctx.arc(a.x, a.y, 59.6, -Math.PI / 2, -Math.PI / 2 + Math.PI * 2 * p);
      ctx.lineWidth = 6; ctx.lineCap = 'round'; ctx.strokeStyle = a.ready ? '#fff' : a.color; ctx.stroke();
      if (a.ready) { ctx.beginPath(); ctx.arc(a.x, a.y, 27.5, 0, 7); ctx.fillStyle = 'rgba(255,255,255,.35)'; ctx.fill(); }
    }
  });
  const el = cv.canvas;
  el.addEventListener('pointerdown', (e) => {
    e.preventDefault();
    try { el.setPointerCapture(e.pointerId); } catch (_) { /* 무시 */ }
    const p = pos(e);
    active.set(e.pointerId, { ...p, start: performance.now(), color: pick(), value: rnd(1, 6), ready: false });
    vibe.tap();
  });
  el.addEventListener('pointermove', (e) => { const a = active.get(e.pointerId); if (a) Object.assign(a, pos(e)); });
  const up = (e) => {
    const a = active.get(e.pointerId);
    if (!a) return;
    active.delete(e.pointerId);
    const now = performance.now(), held = now - a.start;
    if (held >= HOLD) placed.push({ x: a.x, y: a.y, value: a.value, color: a.color });
    else if (held < TAP) {
      if (now - lastTap < DTAP && Math.hypot(a.x - lastPos.x, a.y - lastPos.y) < 60) { reset(); vibe.strong(); lastTap = 0; }
      else { lastTap = now; lastPos = { x: a.x, y: a.y }; }
    }
  };
  el.addEventListener('pointerup', up);
  el.addEventListener('pointercancel', (e) => active.delete(e.pointerId));
  return () => cv.stop();
}

// ===================== 1-2 숫자 뒤집기 잭팟 =====================
export function shutBox(root, back) {
  const s = S();
  let players = 2;
  root.append(top(s.shutTitle, back));
  const body = h('div', { class: 'body' });
  root.append(body);
  let alive = true;

  const setup = () => {
    body.replaceChildren(setupPanel(s.shutDesc, play, stepper(s.players, players, 1, 8, (v) => { players = v; })));
  };

  const play = () => {
    const flipped = Array(12).fill(false);
    let turn = 0, dice = [1, 1], phase = 'roll', second = 0;
    const wrap = h('div', { class: 'body pad center', style: 'gap:12px' });
    body.replaceChildren(wrap);

    const options = () => new Set([dice[0], dice[1], dice[0] + dice[1]].filter((n) => n <= 12 && !flipped[n - 1]));
    const nextTurn = () => { turn = (turn + 1) % players; phase = 'roll'; render(); };
    const reset = () => { flipped.fill(false); turn = 0; dice = [1, 1]; phase = 'roll'; render(); };

    const flip = (n) => {
      flipped[n - 1] = true;
      vibe.tap();
      if (flipped.every(Boolean)) { phase = 'win'; vibe.strong(); render(); return; }
      if (phase === 'second') { nextTurn(); return; }
      const [a, b] = dice;
      const isDie = n !== a + b && (n === a || n === b);
      const other = n === a ? b : a;
      if (isDie && a !== b && !flipped[other - 1]) { second = other; phase = 'second'; render(); } else nextTurn();
    };

    const roll = async () => {
      phase = 'rolling'; render();
      for (let i = 0; i < 10 && alive; i++) { dice = [rnd(1, 6), rnd(1, 6)]; render(); await sleep(60); }
      if (!alive) return;
      dice = [rnd(1, 6), rnd(1, 6)];
      phase = options().size ? 'pick' : 'nomove';
      render();
    };

    const render = () => {
      const avail = phase === 'pick' ? options() : phase === 'second' ? new Set([second]) : new Set();
      const color = C.players[turn % C.players.length];
      const tiles = h('div', { class: 'tiles' });
      for (let n = 1; n <= 12; n++) {
        const t = h('button', { class: 'tile' + (flipped[n - 1] ? ' off' : avail.has(n) ? ' on' : '') }, n);
        if (avail.has(n)) t.onclick = () => flip(n);
        tiles.append(t);
      }
      const msg = { roll: s.rollPrompt, rolling: s.rolling, pick: s.pickPrompt([...avail].sort((x, y) => x - y).join(', ')), second: s.pickSecond(second), nomove: s.noMove, win: '' }[phase];
      let action = h('div', { style: 'height:56px;flex:none' });
      if (phase === 'roll') action = btn(s.throwDice, roll);
      else if (phase === 'nomove') action = btn(s.nextPlayer, nextTurn);
      else if (phase === 'second') action = btn(s.endTurn, nextTurn, 'btn alt');
      wrap.replaceChildren(
        h('div', { style: `color:${color};font-size:28px;font-weight:700` }, players === 1 ? s.solo : s.turnOf(turn + 1)),
        h('div', { class: 'h-sub', style: 'font-size:14px;margin-top:-6px' }, s.numbersLeft(flipped.filter((f) => !f).length)),
        tiles,
        h('div', { class: 'grow' }),
        h('div', { class: 'dicebar' }, dieSvg(dice[0], 76), dieSvg(dice[1], 76), `= ${dice[0] + dice[1]}`),
        h('div', { class: 'msg' + (phase === 'nomove' ? ' win' : '') }, msg),
        action);
      if (phase === 'win') resultOverlay(wrap, players === 1 ? s.jackpot : s.playerWins(turn + 1), s.flippedLast, s.playAgain, reset, s.changePlayers, setup);
    };
    render();
  };

  setup();
  return () => { alive = false; };
}

// ===================== 1-3 5개 주사위 =====================
export function fiveDice(root, back) {
  const s = S();
  let n = 2, alive = true;

  const setup = () => {
    root.replaceChildren(top(s.fiveTitle, back), h('div', { class: 'body' }, setupPanel(s.fiveDesc, play, stepper(s.players, n, 2, 4, (v) => { n = v; }))));
  };

  const play = () => {
    const counts = Array(n).fill(5);
    let faces = Array.from({ length: n }, () => []);
    let slots = Array(n).fill('wait');
    let phase = 'playing', losers = [];
    const safe = Array(n).fill(false); // 주사위를 다 없애 탈출한 사람
    const fd = h('div', { class: 'fd' });
    root.replaceChildren(fd);
    const active = () => [...Array(n).keys()].filter((i) => !safe[i]);
    /** 시계방향으로 다음, 아직 탈출하지 않은 사람 */
    const nextActive = (i, act) => { for (let k = 1; k <= n; k++) { const j = (i + k) % n; if (act.includes(j)) return j; } return i; };

    const applyResults = async () => {
      if (phase !== 'playing') return;
      phase = 'applying'; render();
      await sleep(1800);
      if (!alive) return;
      const act = active();
      const ones = faces.map((f) => f.filter((v) => v === 1).length);
      const sixes = faces.map((f) => f.filter((v) => v === 6).length);
      const add = Array(n).fill(0);
      for (const i of act) add[nextActive(i, act)] += sixes[i];
      for (const i of act) counts[i] = counts[i] - ones[i] - sixes[i] + add[i];
      const justSafe = act.filter((i) => counts[i] === 0);
      justSafe.forEach((i) => { safe[i] = true; });
      faces = faces.map(() => []); slots = slots.map((_, i) => (safe[i] ? 'safe' : 'wait'));
      const left = active();
      if (left.length <= 1) {
        losers = left.length === 1 ? left : justSafe; // 마지막이 동시에 0개면 그 사람들이 공동 패배
        phase = 'over'; vibe.strong();
      } else { phase = 'playing'; if (justSafe.length) vibe.tap(); }
      render();
    };

    const roll = async (i) => {
      if (phase !== 'playing' || slots[i] !== 'wait') return;
      slots[i] = 'rolling'; vibe.tap();
      for (let k = 0; k < 12 && alive; k++) { faces[i] = Array.from({ length: counts[i] }, () => rnd(1, 6)); render(); await sleep(60); }
      if (!alive) return;
      faces[i] = Array.from({ length: counts[i] }, () => rnd(1, 6));
      slots[i] = 'done'; vibe.tap(); render();
      if (active().every((x) => slots[x] === 'done')) applyResults();
    };

    const restart = () => { counts.fill(5); safe.fill(false); faces = faces.map(() => []); slots = slots.map(() => 'wait'); losers = []; phase = 'playing'; render(); };

    const area = (i, rotated) => {
      const color = C.players[i], slot = slots[i], lose = losers.includes(i), isSafe = safe[i] && !lose;
      const canTap = phase === 'playing' && slot === 'wait';
      const bg = lose ? '66' : isSafe ? '0A' : canTap ? '38' : '14';
      const vals = slot === 'safe' ? [] : slot !== 'wait' ? faces[i] : Array(counts[i]).fill(0);
      const ds = vals.length <= 6 ? 44 : vals.length <= 10 ? 34 : 26;
      const marked = slot === 'done';
      const dice = h('div', { class: 'dice' }, vals.map((v) => dieSvg(v, ds, marked && v === 6 ? C.accent : marked && v === 1 ? C.win : null, marked && v === 1 ? 0.25 : 1)));
      let st = '';
      if (lose) st = s.fiveLoserSub;
      else if (isSafe) st = '';
      else if (phase !== 'over') {
        if (slot === 'wait') st = s.tapToRoll;
        else if (slot === 'rolling') st = s.rolling;
        else {
          const act = active();
          const ones = faces[i].filter((v) => v === 1).length, sixes = faces[i].filter((v) => v === 6).length;
          st = s.outPass(ones, nextActive(i, act) + 1, sixes) + (phase === 'playing' ? '\n' + s.waitingOthers : '');
        }
      }
      const status = lose ? h('div', { class: 'pc win', style: 'font-size:24px;font-weight:900' }, '💀')
        : isSafe ? h('div', { class: 'pc', style: 'font-size:22px;font-weight:900;color:var(--text)' }, s.fiveSafe)
          : h('div', { class: 'pc' }, s.diceLeft(counts[i]));
      const a = h('div', { class: 'area' + (rotated ? ' rot' : ''), style: `background:${color}${bg};border-color:${color}${canTap ? 'ff' : isSafe ? '40' : '80'};border-width:${canTap ? 3 : 2}px;${isSafe ? 'opacity:.55' : ''}` },
        h('div', { class: 'pn', style: `color:${color}` }, s.playerN(i + 1)),
        status,
        dice,
        h('div', { class: 'st' + (slot === 'wait' ? ' big' : '') + (lose ? ' win' : ''), style: slot === 'wait' ? `color:${color}` : '' }, st));
      if (canTap) a.addEventListener('click', () => roll(i));
      return a;
    };

    const bar = () => {
      const text = phase === 'playing' ? s.tapYourArea(active().filter((x) => slots[x] !== 'done').length)
        : phase === 'applying' ? s.allRolled : s.fiveLoser(losers.map((i) => 'P' + (i + 1)).join(', '));
      return h('div', { class: 'bar' },
        btn(s.exit, setup, 'tbtn'),
        h('div', { class: 'mid' + (phase === 'over' ? ' win' : '') }, text),
        phase === 'over' ? btn(s.again, restart, 'tbtn accent') : h('div', { style: 'width:64px' }));
    };

    const render = () => {
      if (n === 2) fd.replaceChildren(h('div', { class: 'half' }, area(1, true)), bar(), h('div', { class: 'half' }, area(0, false)));
      else if (n === 3) fd.replaceChildren(h('div', { class: 'half' }, area(1, true), area(2, true)), bar(), h('div', { class: 'half' }, area(0, false)));
      else fd.replaceChildren(h('div', { class: 'half' }, area(1, true), area(2, true)), bar(), h('div', { class: 'half' }, area(0, false), area(3, false)));
    };
    render();
  };

  setup();
  return () => { alive = false; };
}

// ===================== 1-4 789 =====================
/** 합의 뒷자리 → 결과 종류 */
export function judge789(sum, rev) {
  const d = sum % 10;
  if (d === 7) return 'top';
  if (d === 8) return 'half';
  if (d === 9) return 'shot';
  if (d === 6 && rev) return 'rev';
  return 'pass';
}

export function d789(root, back) {
  const s = S();
  let n = 4, rev = true;
  let alive = true;
  const setup = () => {
    root.replaceChildren(top(s.d789Title, back), h('div', { class: 'body' }, setupPanel(s.d789Desc, play,
      stepper(s.players, n, 2, 8, (v) => { n = v; }),
      toggle(s.d789RevOpt, rev, (v) => { rev = v; }))));
  };

  const play = () => {
    let turn = 0, dir = 1, busy = false;
    let vals = [rnd(1, 6), rnd(1, 6)], anim = null, shown = false, lastRoller = 0;
    const RES = {
      top: { t: s.d789Top, e: '🍶', c: C.accent },
      half: { t: s.d789Half, e: '🍺', c: '#FF9800' },
      shot: { t: s.d789Shot, e: '🔥', c: C.win },
      rev: { t: s.d789Rev, e: '🔄', c: '#448AFF' },
      pass: { t: s.d789Pass, e: '👉', c: C.sub },
    };
    const turnEl = h('div', { class: 'turn' });
    const resEl = h('div', { class: 'bigres' });
    const stage = h('div', { style: 'flex:1;position:relative;width:100%;min-height:0;cursor:pointer' });
    const hint = h('div', { class: 'h-sub', style: 'text-align:center;padding:0 0 18px;font-size:16px;color:var(--sub)' }, s.d789Tap);
    const g = h('div', { class: 'g7' }, turnEl, stage, resEl, hint);
    root.replaceChildren(top(s.d789Title, setup), h('div', { class: 'body' }, g));

    const showTurn = (roller) => {
      const arrow = dir === 1 ? '↻' : '↺';
      turnEl.replaceChildren(h('span', { style: `color:${C.players[turn % C.players.length]}` }, s.playerN(turn + 1)), ' ', h('span', { style: 'color:var(--sub)' }, arrow),
        h('small', {}, roller != null ? s.playerN(roller + 1) + ' → ' + s.playerN(turn + 1) : ''));
    };
    showTurn(null);

    liveCanvas(stage, (ctx, w, hh) => {
      const size = Math.min(110, w * 0.3), cy = hh * 0.55;
      const now = performance.now();
      let p = 1;
      if (anim) { p = Math.min(1, (now - anim.t0) / anim.ms); if (now - anim.last > 70 && p < 0.92) { anim.last = now; vals = [rnd(1, 6), rnd(1, 6)]; } }
      [-1, 1].forEach((side, i) => {
        const x = w / 2 + side * size * 0.75;
        const bounce = anim ? Math.abs(Math.sin(p * Math.PI * (3 + i * 0.5))) * (1 - p) * hh * 0.35 : 0;
        const rot = anim ? (1 - p) * (1 - p) * Math.PI * (5 + i) * side : 0;
        ctx.save(); ctx.translate(x, cy - bounce); ctx.rotate(rot);
        drawDie(ctx, 0, 0, size, vals[i], shown ? C.players[lastRoller % C.players.length] : null);
        ctx.restore();
        ctx.beginPath(); ctx.ellipse(x, cy + size * 0.62, size * 0.42 * (1 - bounce / hh), size * 0.08, 0, 0, 7); ctx.fillStyle = 'rgba(0,0,0,.35)'; ctx.fill();
      });
    });

    const roll = async () => {
      if (busy) return;
      busy = true; shown = false; vibe.tap();
      resEl.replaceChildren(); hint.style.visibility = 'hidden';
      const roller = turn; lastRoller = roller;
      anim = { t0: performance.now(), ms: 1100, last: 0, roller };
      const final = [rnd(1, 6), rnd(1, 6)];
      await sleep(1020); if (!alive) return;
      vals = final; await sleep(90); if (!alive) return;
      anim = null; shown = true;
      const sum = final[0] + final[1], kind = judge789(sum, rev), r = RES[kind];
      if (kind === 'pass') vibe.tap(); else vibe.strong();
      if (kind === 'rev') dir = -dir;
      resEl.replaceChildren(h('div', { class: 'pops', style: `color:${r.c}` }, `${r.e} ${r.t}`), h('small', {}, s.d789Sum(sum)));
      turn = (turn + dir + n) % n;
      showTurn(roller);
      hint.style.visibility = 'visible';
      busy = false;
    };
    stage.addEventListener('click', roll);
    hint.addEventListener('click', roll);
  };

  setup();
  return () => { alive = false; };
}
