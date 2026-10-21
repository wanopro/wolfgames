// 주사위 게임 3종: 주사위 굴리기 / 숫자 뒤집기 잭팟 / 5개 주사위
import { C, S, h, btn, top, stepper, setupPanel, resultOverlay, liveCanvas, vibe, drawDie, dieSvg, sleep, rnd } from './core.js';

// ===================== 1-1 주사위 굴리기 =====================
export function rollDice(root, back) {
  const s = S();
  const HOLD = 1000, TAP = 300, DTAP = 400;
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
    let phase = 'playing', winners = [];
    const fd = h('div', { class: 'fd' });
    root.replaceChildren(fd);

    const applyResults = async () => {
      if (phase !== 'playing') return;
      phase = 'applying'; render();
      await sleep(1800);
      if (!alive) return;
      const ones = faces.map((f) => f.filter((v) => v === 1).length);
      const sixes = faces.map((f) => f.filter((v) => v === 6).length);
      for (let i = 0; i < n; i++) counts[i] = counts[i] - ones[i] - sixes[i] + sixes[(i - 1 + n) % n];
      faces = faces.map(() => []); slots = slots.map(() => 'wait');
      winners = [...Array(n).keys()].filter((i) => counts[i] === 0);
      if (winners.length) { phase = 'win'; vibe.strong(); } else phase = 'playing';
      render();
    };

    const roll = async (i) => {
      if (phase !== 'playing' || slots[i] !== 'wait') return;
      slots[i] = 'rolling'; vibe.tap();
      for (let k = 0; k < 12 && alive; k++) { faces[i] = Array.from({ length: counts[i] }, () => rnd(1, 6)); render(); await sleep(60); }
      if (!alive) return;
      faces[i] = Array.from({ length: counts[i] }, () => rnd(1, 6));
      slots[i] = 'done'; vibe.tap(); render();
      if (slots.every((x) => x === 'done')) applyResults();
    };

    const restart = () => { counts.fill(5); faces = faces.map(() => []); slots = slots.map(() => 'wait'); winners = []; phase = 'playing'; render(); };

    const area = (i, rotated) => {
      const color = C.players[i], slot = slots[i], win = winners.includes(i);
      const canTap = phase === 'playing' && slot === 'wait';
      const bg = win ? '59' : canTap ? '38' : '14';
      const vals = slot !== 'wait' ? faces[i] : Array(counts[i]).fill(0);
      const ds = vals.length <= 6 ? 44 : vals.length <= 10 ? 34 : 26;
      const marked = slot === 'done';
      const dice = h('div', { class: 'dice' }, vals.map((v) => dieSvg(v, ds, marked && v === 6 ? C.accent : marked && v === 1 ? C.win : null, marked && v === 1 ? 0.25 : 1)));
      let st = '';
      if (!(win || phase === 'win')) {
        if (slot === 'wait') st = s.tapToRoll;
        else if (slot === 'rolling') st = s.rolling;
        else {
          const ones = faces[i].filter((v) => v === 1).length, sixes = faces[i].filter((v) => v === 6).length;
          st = s.outPass(ones, ((i + 1) % n) + 1, sixes) + (phase === 'playing' ? '\n' + s.waitingOthers : '');
        }
      }
      const a = h('div', { class: 'area' + (rotated ? ' rot' : ''), style: `background:${color}${bg};border-color:${color}${canTap ? 'ff' : '80'};border-width:${canTap ? 3 : 2}px` },
        h('div', { class: 'pn', style: `color:${color}` }, s.playerN(i + 1)),
        h('div', { class: 'pc' + (win ? ' acc' : ''), style: win ? 'font-size:22px;font-weight:900' : '' }, win ? s.winner : s.diceLeft(counts[i])),
        dice,
        h('div', { class: 'st' + (slot === 'wait' ? ' big' : ''), style: slot === 'wait' ? `color:${color}` : '' }, st));
      if (canTap) a.addEventListener('click', () => roll(i));
      return a;
    };

    const bar = () => {
      const text = phase === 'playing' ? s.tapYourArea(slots.filter((x) => x !== 'done').length)
        : phase === 'applying' ? s.allRolled : s.winnersWin(winners.map((i) => 'P' + (i + 1)).join(', '));
      return h('div', { class: 'bar' },
        btn(s.exit, setup, 'tbtn'),
        h('div', { class: 'mid' }, text),
        phase === 'win' ? btn(s.again, restart, 'tbtn accent') : h('div', { style: 'width:64px' }));
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
