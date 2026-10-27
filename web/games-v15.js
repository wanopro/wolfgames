// v1.5 새 게임: 탭 줄다리기 · 슬랩 대결(⚔️ 대결) / 맥주 거품 채우기(⚡ 순발력) / 동전 던지기(🍀 운빨) / 글로벌 킹스컵(🃏 카드)
import { C, S, h, btn, top, stepper, setupPanel, liveCanvas, vibe, newDeck, SUITS } from './core.js';
import { region, lines, text, badge } from './split.js';
import { splitStage, hub } from './games-quick.js';
import { chompWolf, wolfPaw, coin, coinEdge } from './art.js';

const GOLD = '#FFC107', RED = '#FF3D5A';
const PC = C.players;
const ease = (u) => 1 - Math.pow(1 - Math.min(1, Math.max(0, u)), 3);
const secScale = (sec) => Math.max(0.5, Math.min(1.05, sec.rin / 118));

/** 위·아래 2명 화면. game = { draw(ctx,W,H,t), down(p,t,x,y), up(p,t), hub(t) }  p: 0=아래(P1) 1=위(P2) */
function duelStage(root, title, onBack, game) {
  const s = S();
  const body = h('div', { class: 'body', style: 'touch-action:none' });
  root.replaceChildren(top(title, onBack), body);
  const t0 = performance.now(), now = () => (performance.now() - t0) / 1000;
  const cv = liveCanvas(body, (ctx, w, hh) => { ctx.fillStyle = C.bg; ctx.fillRect(0, 0, w, hh); game.draw(ctx, w, hh, now(), s); });
  const who = new Map();
  const el = cv.canvas;
  el.addEventListener('pointerdown', (e) => {
    e.preventDefault();
    const r = el.getBoundingClientRect(), x = e.clientX - r.left, y = e.clientY - r.top;
    if (game.hub && Math.hypot(x - r.width / 2, y - r.height / 2) < 40) { game.hub(now()); return; }
    const p = y > r.height / 2 ? 0 : 1; who.set(e.pointerId, p);
    try { el.setPointerCapture(e.pointerId); } catch (_) { /* 무시 */ }
    game.down(p, now(), x, y);
  });
  const end = (e) => { if (!who.has(e.pointerId)) return; const p = who.get(e.pointerId); who.delete(e.pointerId); game.up && game.up(p, now(), [...who.values()].filter((q) => q === p).length); };
  el.addEventListener('pointerup', end); el.addEventListener('pointercancel', end);
  el.addEventListener('contextmenu', (e) => e.preventDefault());
  return cv;
}
/** 위 사람(P2)은 화면을 180도 돌려서 그림. 좌표: (0,0)=가운데 선 왼쪽, y+ = 화면 끝 쪽 */
function half(ctx, W, H, topSide, fn) { ctx.save(); if (topSide) { ctx.translate(W, H / 2); ctx.rotate(Math.PI); } else ctx.translate(0, H / 2); fn(); ctx.restore(); }

// ===================== 🎀 탭 줄다리기 =====================
export function tugWar(root, back) {
  const s = S();
  let cv = null;
  const setup = () => { if (cv) { cv.stop(); cv = null; } root.replaceChildren(top(s.tugTitle, back), h('div', { class: 'body' }, setupPanel(s.tugDesc, play))); };
  const play = () => {
    const g = {};
    const round = (t) => Object.assign(g, { startAt: t + 1.6, pos: 0, disp: 0, cnt: [0, 0], win: -1, winAt: 0, ripples: [], last: t, buzz: false });
    round(0);
    cv = duelStage(root, s.tugTitle, setup, {
      down(p, t, x, y) {
        if (g.win >= 0 || t < g.startAt) return;
        g.cnt[p]++; g.pos += p === 0 ? 0.045 : -0.045; g.ripples.push([t, x, y]); if (g.ripples.length > 40) g.ripples.shift();
        if (Math.abs(g.pos) >= 1) { g.win = g.pos > 0 ? 0 : 1; g.winAt = t; g.pos = Math.sign(g.pos); vibe.strong(); }
      },
      hub(t) { if (g.win >= 0 && t - g.winAt > 0.8) { round(t); vibe.tap(); } },
      draw(ctx, W, H, t) {
        const dt = t - g.last; g.last = t;
        if (t >= g.startAt && !g.buzz) { g.buzz = true; vibe.tick(); }
        g.disp = g.win >= 0 ? g.pos : g.disp + (g.pos - g.disp) * Math.min(1, dt * 12);
        const edge = Math.min(110, H * 0.15), midY = H / 2 - g.disp * (H / 2 - edge);
        const shake = g.win < 0 && t > g.startAt ? Math.sin(t * 60) * 2 : 0;
        ctx.fillStyle = PC[1] + '44'; ctx.fillRect(0, 0, W, midY); ctx.fillStyle = PC[0] + '44'; ctx.fillRect(0, midY, W, H - midY);
        for (const [y, c] of [[edge, PC[0]], [H - edge, PC[1]]]) { ctx.setLineDash([10, 8]); ctx.strokeStyle = c; ctx.lineWidth = 3; ctx.beginPath(); ctx.moveTo(14, y); ctx.lineTo(W - 14, y); ctx.stroke(); ctx.setLineDash([]); }
        ctx.save(); ctx.translate(W / 2 + shake, 0);
        ctx.lineWidth = 16; ctx.strokeStyle = '#C8A06A'; ctx.beginPath(); ctx.moveTo(0, 30); ctx.lineTo(0, H - 30); ctx.stroke();
        ctx.strokeStyle = '#8D6A3F'; ctx.lineWidth = 3; for (let y = 30 + ((g.disp * 60) % 22 + 22) % 22; y < H - 30; y += 22) { ctx.beginPath(); ctx.moveTo(-8, y); ctx.lineTo(8, y + 9); ctx.stroke(); }
        ctx.beginPath(); ctx.arc(0, midY, 24, 0, 7); ctx.fillStyle = '#FF7043'; ctx.fill(); ctx.lineWidth = 4; ctx.strokeStyle = '#fff'; ctx.stroke();
        text(ctx, '🎀', 0, midY + 1, 26, '#fff');
        ctx.restore();
        for (const [tt, x, y] of g.ripples) { const u = (t - tt) / 0.35; if (u < 0 || u > 1) continue; ctx.beginPath(); ctx.arc(x, y, 14 + u * 40, 0, 7); ctx.strokeStyle = `rgba(255,255,255,${0.7 * (1 - u)})`; ctx.lineWidth = 4; ctx.stroke(); }
        for (const p of [0, 1]) half(ctx, W, H, p === 1, () => {
          const won = g.win === p, lost = g.win >= 0 && !won, hy = H / 2;
          text(ctx, `P${p + 1}`, 44, hy - 34, 22, PC[p], 900);
          text(ctx, `${g.cnt[p]}`, W - 56, hy - 44, 30, '#fff', 900); text(ctx, s.tugTaps, W - 56, hy - 18, 13, '#bbb', 700);
          const lx = W / 2 - Math.min(95, W * 0.24), ly = Math.min(hy - 120, hy * 0.6);
          if (g.win < 0) { if (t >= g.startAt) text(ctx, s.tugMash, lx, ly, 24 * (1 + 0.08 * Math.sin(t * 25)), '#fff', 900); }
          else badge(ctx, won ? s.winWord : s.shotWord, lx, ly, 1.2, won ? GOLD : RED, won ? '#000' : '#fff');
          if (lost) chompWolf(ctx, W / 2 + Math.min(85, W * 0.22), ly - 6, 32, Math.abs(Math.sin((t - g.winAt) * 10)), Math.min(1, (t - g.winAt) * 4));
        });
        if (t < g.startAt + 0.4) { ctx.fillStyle = `rgba(0,0,0,${t < g.startAt ? 0.55 : 0.55 * (1 - (t - g.startAt) / 0.4)})`; ctx.fillRect(0, 0, W, H);
          for (const p of [0, 1]) half(ctx, W, H, p === 1, () => text(ctx, t < g.startAt - 0.5 ? s.readyWord : 'GO!', W / 2, 70, 44, '#FFD740', 900)); }
        if (g.win >= 0 && t - g.winAt > 0.8) { hub(ctx, W, H, 30, '#121826', () => text(ctx, '🔄', W / 2, H / 2 + 1, 22, '#fff', 900)); }
      },
    });
  };
  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== 🐾 슬랩 대결 =====================
const IMPACT = 0.22; // 공격 시작 → 맞는 순간(초). 이 안에 손을 떼면 피함
export function slapDuel(root, back) {
  const s = S();
  let cv = null;
  const setup = () => { if (cv) { cv.stop(); cv = null; } root.replaceChildren(top(s.slapTitle, back), h('div', { class: 'body' }, setupPanel(s.slapDesc, play))); };
  const play = () => {
    const g = { att: Math.random() < 0.5 ? 0 : 1, score: [0, 0], hold: [0, 0], phase: 'wait', at: 0, kind: '' };
    const result = (kind, t) => {
      g.phase = 'result'; g.kind = kind; g.at = t;
      if (kind === 'hit' || kind === 'flinch') { g.score[g.att]++; vibe.strong(); } else vibe.tick();
    };
    cv = duelStage(root, s.slapTitle, setup, {
      down(p, t) {
        g.hold[p]++;
        const def = 1 - g.att;
        if (p === def && g.phase === 'wait') g.phase = 'ready';
        if (p === g.att && g.phase === 'ready') { g.phase = 'swing'; g.at = t; vibe.tap(); }
      },
      up(p, t, left) {
        g.hold[p] = left;
        const def = 1 - g.att;
        if (p !== def || left > 0) return;
        if (g.phase === 'swing' && t - g.at < IMPACT) result('dodge', t);
        else if (g.phase === 'ready') result('flinch', t);
      },
      draw(ctx, W, H, t) {
        const def = 1 - g.att;
        if (g.phase === 'swing' && t - g.at >= IMPACT) result('hit', g.at + IMPACT);
        if (g.phase === 'result' && t - g.at > 1.7) {
          if (g.kind === 'dodge') g.att = 1 - g.att;
          g.phase = g.hold[1 - g.att] > 0 && g.kind === 'hit' ? 'ready' : 'wait';
        }
        const st = g.phase, dtE = t - g.at;
        ctx.fillStyle = PC[1] + '33'; ctx.fillRect(0, 0, W, H / 2); ctx.fillStyle = PC[0] + '33'; ctx.fillRect(0, H / 2, W, H / 2);
        ctx.strokeStyle = 'rgba(0,0,0,.6)'; ctx.lineWidth = 4; ctx.beginPath(); ctx.moveTo(0, H / 2); ctx.lineTo(W, H / 2); ctx.stroke();
        const cx = W / 2, cy = H / 2, sign = def === 1 ? -1 : 1, k = Math.min(1, H / 760) * 0.95;
        let defOff = 0, attOff = 0, red = 0, burst = -1;
        if (st === 'swing') attOff = ease(dtE / IMPACT) * 120;
        if (st === 'result' && g.kind === 'hit') { const u = (t - g.at); attOff = u < 0.45 ? 120 : Math.max(0, 1 - (u - 0.45) / 0.4) * 120; red = Math.max(0, 1 - u / 1.6); burst = u; }
        if (st === 'result' && g.kind === 'dodge') { attOff = dtE < 0.2 ? 120 * ease(0.6 + dtE * 2) : Math.max(0, 1 - (dtE - 0.2) / 0.4) * 120; defOff = Math.min(1, dtE / 0.1) * 80 * (dtE < 1.0 ? 1 : Math.max(0, 1 - (dtE - 1.0) / 0.4)); }
        if (st === 'result' && g.kind === 'flinch') defOff = Math.min(1, dtE / 0.08) * 50 * Math.max(0, 1 - Math.max(0, dtE - 0.6) / 0.4);
        if (st === 'wait') defOff = 30;
        const swing = Math.min(1, attOff / 120), shake = st === 'ready' ? Math.sin(t * 9) * 3 : 0;
        const attY = cy - sign * (58 - attOff * 0.95) * k, defY = cy + sign * (58 + defOff) * k;
        if (swing < 0.5) { wolfPaw(ctx, cx + shake, attY, k * (0.95 + swing * 0.3), PC[g.att], g.att === 1, true); wolfPaw(ctx, cx, defY, k * 0.95, PC[def], def === 1, false, red); }
        else { wolfPaw(ctx, cx, defY, k * 0.95, PC[def], def === 1, false, red); wolfPaw(ctx, cx + shake + swing * 14, attY, k * (0.95 + swing * 0.22), PC[g.att], g.att === 1, false); }
        if (burst >= 0 && burst < 0.8) { const q = Math.min(1, burst / 0.12); ctx.save(); ctx.translate(cx + 70 * k, cy + sign * 40 * k); ctx.scale(q, q); ctx.rotate(-0.2);
          ctx.beginPath(); for (let a = 0; a < 16; a++) { const r = a % 2 ? 26 : 52; ctx.lineTo(Math.cos(a * Math.PI / 8) * r, Math.sin(a * Math.PI / 8) * r); } ctx.closePath(); ctx.fillStyle = '#FFEB3B'; ctx.fill(); ctx.strokeStyle = '#E65100'; ctx.lineWidth = 4; ctx.stroke();
          text(ctx, s.slapSmack, 0, 2, 22, '#D84315', 900); ctx.restore(); }
        if (burst >= 0 && burst < 0.16) { ctx.fillStyle = `rgba(255,255,255,${0.7 * (1 - burst / 0.16)})`; ctx.fillRect(0, 0, W, H); }
        for (const p of [0, 1]) half(ctx, W, H, p === 1, () => {
          const isAtt = p === g.att, y = H / 2 - 40;
          text(ctx, `P${p + 1}`, 40, y + 16, 20, PC[p], 900);
          text(ctx, `💥 ${g.score[p]}`, W - 50, y + 16, 20, '#fff', 900);
          text(ctx, isAtt ? s.slapAtt : s.slapDef, W / 2, y - 70, 24, isAtt ? '#FFAB91' : '#80D8FF', 900);
          let sub = isAtt ? (st === 'wait' ? s.slapAttWait : s.slapAttHint) : (st === 'wait' ? s.slapDefHold : s.slapDefHint);
          if (st === 'result') sub = { hit: isAtt ? s.slapHit : s.slapOuch, dodge: isAtt ? s.slapMiss : s.slapDodge, flinch: isAtt ? s.slapFlinchAtt : s.slapFlinch }[g.kind];
          text(ctx, sub, W / 2, y - 38, 17, '#fff', 800);
        });
      },
    });
  };
  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== 🍺 맥주 거품 채우기 =====================
export function beerPour(root, back) {
  const s = S();
  let n = 2, cv = null;
  const setup = () => { if (cv) { cv.stop(); cv = null; } root.replaceChildren(top(s.beerTitle, back), h('div', { class: 'body' }, setupPanel(s.beerDesc, play, stepper(s.players, n, 2, 6, (v) => { n = v; })))); };
  const play = () => {
    const g = {};
    const level = (t) => { const x = Math.max(0, t - g.startAt) * g.k; return Math.min(1.12, 0.06 * x + 0.055 * x * x); };
    const round = (t) => Object.assign(g, { startAt: t + 1.4, k: 0.85 + Math.random() * 0.35, stop: Array(n).fill(null), doneAt: 0 });
    round(0);
    const overT = () => { let lo = g.startAt, hi = g.startAt + 30; for (let q = 0; q < 40; q++) { const m = (lo + hi) / 2; if (level(m) >= 1.06) hi = m; else lo = m; } return hi; };
    const game = {
      n,
      tap: (i, t) => { if (g.doneAt || t < g.startAt || g.stop[i] !== null) return; g.stop[i] = level(t); vibe.tap(); },
      hub: (t) => { if (g.doneAt && t - g.doneAt > 0.6) { round(t); vibe.tap(); } },
      draw(ctx, W, H, t, secs) {
        if (!g.doneAt && (g.stop.every((v) => v !== null) || t > overT() + 0.3)) { g.doneAt = t; g.stop = g.stop.map((v) => (v === null ? 1.06 : v)); vibe.strong(); }
        const done = !!g.doneAt;
        const order = g.stop.map((v, i) => [v === null || v > 1.0 ? 9 + (v || 0) : 1 - v, i]).sort((a, b) => a[0] - b[0]).map(([, i]) => i);
        secs.forEach((sec, i) => {
          const v = g.stop[i] ?? Math.min(level(t), 1.06), over = v > 1.0;
          const rk = done ? order.indexOf(i) : -1, first = done && rk === 0 && !over, lose = done && (over || rk === n - 1);
          const glow = first ? GOLD : lose ? RED : null;
          region(ctx, sec, PC[i] + '30', (c, sc) => {
            const gh = 140 * sc, gw = 84 * sc, topY = -gh / 2 - 26 * sc, bot = gh / 2 - 26 * sc, lineY = topY + 18 * sc;
            text(c, `P${i + 1}`, -gw - 22 * sc, topY + 8 * sc, 18 * sc, PC[i], 900);
            c.save(); c.beginPath(); c.moveTo(-gw / 2, topY); c.lineTo(gw / 2, topY); c.lineTo(gw / 2 - 8 * sc, bot); c.lineTo(-gw / 2 + 8 * sc, bot); c.closePath();
            c.fillStyle = 'rgba(255,255,255,.10)'; c.fill(); c.save(); c.clip();
            const fillTop = bot - (bot - lineY) * Math.min(v, 1.0);
            c.fillStyle = '#F9A825'; c.fillRect(-gw, fillTop, gw * 2, bot - fillTop + 2);
            for (let b = 0; b < 7; b++) { const by = bot - ((t * 60 * sc + b * 23 * sc) % (bot - fillTop + 1)); c.beginPath(); c.arc(-gw / 3 + (b * 13 % 50) * sc, by, 2.2 * sc, 0, 7); c.fillStyle = 'rgba(255,255,255,.55)'; c.fill(); }
            const fh = 12 * sc; c.fillStyle = '#FFF8E1'; for (let q = -3; q <= 3; q++) { c.beginPath(); c.arc(q * 14 * sc, fillTop - fh * 0.3 + Math.sin(t * 6 + q) * 1.5 * sc, 10 * sc, 0, 7); c.fill(); } c.fillRect(-gw, fillTop - fh * 0.3, gw * 2, fh);
            c.restore(); c.lineWidth = 4 * sc; c.strokeStyle = '#E0E0E0'; c.stroke();
            c.beginPath(); c.roundRect(gw / 2 - 4 * sc, topY + 26 * sc, 26 * sc, 70 * sc, 12 * sc); c.lineWidth = 7 * sc; c.stroke(); c.restore();
            if (v >= 1.04) { c.fillStyle = '#FFF8E1'; for (const sd of [-1, 1]) { c.beginPath(); c.ellipse(sd * (gw / 2 + 2 * sc), topY + 30 * sc, 8 * sc, 28 * sc, 0, 0, 7); c.fill(); } c.beginPath(); c.ellipse(0, topY - 8 * sc, gw * 0.6, 14 * sc, 0, 0, 7); c.fill(); }
            c.setLineDash([6 * sc, 5 * sc]); c.strokeStyle = '#FF5252'; c.lineWidth = 3 * sc; c.beginPath(); c.moveTo(-gw / 2 - 14 * sc, lineY); c.lineTo(gw / 2 + 14 * sc, lineY); c.stroke(); c.setLineDash([]);
            text(c, s.beerLine, -gw / 2 - 32 * sc, lineY, 11 * sc, '#FF8A80', 800);
            const msg = g.stop[i] !== null ? (over ? s.beerOver : `${(Math.round(v * 1000) / 10).toFixed(1)}%`) : t < g.startAt ? s.readyWord : s.beerTap;
            text(c, msg, 0, bot + 24 * sc, 20 * sc, over ? '#80D8FF' : '#fff', 900);
            if (first) badge(c, s.rankFirst, 0, bot + 56 * sc, sc * 0.9, GOLD, '#000');
            else if (lose) badge(c, s.rankLast, 0, bot + 56 * sc, sc * 0.9, RED, '#fff');
          }, glow);
        });
        lines(ctx, secs, W, H);
        hub(ctx, W, H, 34, '#1F2433', () => text(ctx, done ? '🔄' : '🍺', W / 2, H / 2 + 1, 24, '#fff', 900));
        if (done) text(ctx, s.tapHubAgain, W / 2, H / 2 + 52, 13, '#fff', 700);
      },
    };
    cv = splitStage(root, s.beerTitle, setup, game);
  };
  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== 🪙 동전 던지기 =====================
export const EDGE_CHANCE = 0.15;
export function coinToss(root, back) {
  const s = S();
  let n = 4, cv = null;
  const setup = () => { if (cv) { cv.stop(); cv = null; } root.replaceChildren(top(s.coinTitle, back), h('div', { class: 'body' }, setupPanel(s.coinDesc, play, stepper(s.players, n, 2, 8, (v) => { n = v; })))); };
  const play = () => {
    const g = {};
    const round = () => Object.assign(g, { pick: Array(n).fill(0), allAt: 0, last: 9 });
    round();
    const game = {
      n,
      tap: (i, t, p, sec) => {
        if (g.allAt) return;
        const dx = p[0] - sec.anchor[0], dy = p[1] - sec.anchor[1];
        const lx = dx * Math.cos(-sec.rot) - dy * Math.sin(-sec.rot);
        g.pick[i] = lx < 0 ? 1 : -1; vibe.tap();
        if (g.pick.every(Boolean)) {
          g.allAt = t; g.spinAt = t + 3; g.landAt = g.spinAt + 2.4;
          g.result = Math.random() < EDGE_CHANCE ? 0 : Math.random() < 0.5 ? 1 : -1;
        }
      },
      hub: (t) => { if (g.allAt && t > g.landAt + 1.2) { round(); vibe.tap(); } },
      draw(ctx, W, H, t, secs) {
        const done = g.allAt && t >= g.landAt + (g.result === 0 ? 1.4 : 0.35);
        if (g.allAt && t < g.spinAt) { const k = Math.ceil(g.spinAt - t); if (k !== g.last) { g.last = k; vibe.tick(); } }
        if (done && !g.buzzed) { g.buzzed = true; vibe.strong(); }
        if (!g.allAt) g.buzzed = false;
        secs.forEach((sec, i) => {
          const p = g.pick[i], ok = done && p === g.result, bad = done && !ok;
          region(ctx, sec, bad ? '#4A1820' : ok ? '#1F5E3A' : PC[i] + '30', (c, sc) => {
            text(c, `P${i + 1}`, 0, -64 * sc, 18 * sc, PC[i], 900);
            if (done) {
              text(c, ok ? s.coinRight : s.coinWrong, 0, -18 * sc, 34 * sc, ok ? '#69F0AE' : '#FF8A80', 900);
              coin(c, 0, 34 * sc, 22 * sc, p); if (bad) badge(c, s.shotWord, 0, 76 * sc, sc * 0.85, RED, '#fff');
            } else {
              for (const [f, dx, lab] of [[1, -48, s.coinHead], [-1, 48, s.coinTail]]) {
                const on = p === f, off = p !== 0 && !on;
                c.save(); c.globalAlpha = off ? 0.35 : 1;
                c.beginPath(); c.roundRect((dx - 40) * sc, -36 * sc, 80 * sc, 96 * sc, 16 * sc); c.fillStyle = on ? '#FFC94A' : 'rgba(255,255,255,.08)'; c.fill();
                if (on) { c.lineWidth = 4 * sc; c.strokeStyle = '#fff'; c.stroke(); }
                coin(c, dx * sc, 4 * sc, 26 * sc, f); text(c, lab, dx * sc, 46 * sc, 16 * sc, on ? '#000' : '#fff', 900); c.restore();
              }
              if (!p) text(c, s.coinPick, 0, 78 * sc, 14 * sc, '#ccc', 700);
            }
          }, done ? (ok ? '#69F0AE' : RED) : null);
        });
        lines(ctx, secs, W, H);
        const cx = W / 2, cy = H / 2, R = 46;
        if (!g.allAt || t < g.spinAt) {
          ctx.beginPath(); ctx.arc(cx, cy, R + 12, 0, 7); ctx.fillStyle = '#1F2433'; ctx.fill(); ctx.lineWidth = 5; ctx.strokeStyle = 'rgba(0,0,0,.6)'; ctx.stroke();
          coin(ctx, cx, cy, R * 0.8, 1, 1);
          if (g.allAt) { const k = Math.ceil(g.spinAt - t), u = (g.spinAt - t) % 1; ctx.fillStyle = 'rgba(0,0,0,.55)'; ctx.beginPath(); ctx.arc(cx, cy, R, 0, 7); ctx.fill(); text(ctx, `${k}`, cx, cy + 2, 46 * (1 + 0.3 * u), '#FFD740', 900); }
          else text(ctx, `${g.pick.filter(Boolean).length}/${n}`, cx, cy + R + 24, 15, '#fff', 800);
          return;
        }
        const u = Math.min(1, (t - g.spinAt) / 2.4), flips = 9;
        const endPhi = g.result === 0 ? (flips + 0.5) * Math.PI : (g.result > 0 ? flips + 1 : flips) * Math.PI;
        const phi = endPhi * ease(Math.min(1, u * 1.05)), hgt = Math.sin(Math.min(1, u * 1.15) * Math.PI), r = R * (1 + 0.9 * hgt);
        ctx.beginPath(); ctx.ellipse(cx, cy + R * 0.9, R * (1 - 0.4 * hgt), R * 0.18 * (1 - 0.4 * hgt), 0, 0, 7); ctx.fillStyle = 'rgba(0,0,0,.35)'; ctx.fill();
        if (g.result === 0 && u >= 1) {
          const dt = t - g.landAt; coinEdge(ctx, cx, cy + R * 0.9, R, Math.sin(dt * 14) * 0.25 * Math.exp(-dt * 2.2));
          if (dt > 0.9) badge(ctx, s.coinEdge, cx, cy - R * 1.6, Math.min(1, W / 380), RED, '#fff');
        } else {
          const sy = Math.cos(phi); coin(ctx, cx, cy - hgt * 20, r, sy >= 0 ? 1 : -1, Math.abs(sy));
          if (u >= 1 && t > g.landAt + 0.35) badge(ctx, g.result > 0 ? s.coinResHead : s.coinResTail, cx, cy - R * 1.55, 1, '#FFC94A', '#000');
        }
        if (done && t > g.landAt + 1.2) text(ctx, s.tapHubAgain, cx, cy + R + 34, 13, '#fff', 700);
      },
    };
    cv = splitStage(root, s.coinTitle, setup, game);
  };
  setup();
  return () => { if (cv) cv.stop(); };
}

// ===================== 👑 글로벌 킹스컵 =====================
export const KC_ICONS = ['🌊', '👉', '🙋', '🚽', '💃', '🍻', '🙌', '🤝', '🔤', '📚', '📜', '❓', '👑'];
const RANK = ['', 'A', '2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K'];
export function kingsCup(root, back) {
  const s = S();
  const names = s.kcNames.split('|'), descs = s.kcDescs.split('|');
  let deck, cur, at = 0, kings = 0, cv = null;
  const reset = () => { deck = newDeck(); cur = null; kings = 0; };
  reset();
  const body = h('div', { class: 'body', style: 'touch-action:manipulation' });
  const rulesBtn = btn(s.kcRules, () => showRules(), 'btn alt');
  const shuffleBtn = btn(s.kcShuffle, () => { vibe.tap(); reset(); }, 'btn alt');
  root.append(top(`🃏 ${s.kcTitle}`, back), body, h('div', { class: 'row', style: 'padding:0 16px calc(14px + env(safe-area-inset-bottom))' }, rulesBtn, shuffleBtn));
  const t0 = performance.now(), now = () => (performance.now() - t0) / 1000;
  cv = liveCanvas(body, (ctx, W, H) => {
    const t = now();
    ctx.fillStyle = C.bg; ctx.fillRect(0, 0, W, H);
    const deckY = Math.max(70, H * 0.12);
    const gx = W - 56, gy = deckY; ctx.save(); ctx.translate(gx, gy);
    ctx.beginPath(); ctx.moveTo(-24, -28); ctx.lineTo(24, -28); ctx.lineTo(17, 28); ctx.lineTo(-17, 28); ctx.closePath(); ctx.fillStyle = 'rgba(255,255,255,.12)'; ctx.fill(); ctx.strokeStyle = '#ddd'; ctx.lineWidth = 3; ctx.stroke();
    ctx.save(); ctx.clip(); ctx.fillStyle = '#F9A825'; ctx.fillRect(-30, 28 - 56 * kings / 4, 60, 56 * kings / 4); ctx.restore(); ctx.restore();
    text(ctx, `👑 ${kings}/4`, gx, gy + 46, 15, '#FFD740', 900);
    text(ctx, `🂠 ${s.kcLeft(deck.length)}`, 64, deckY, 15, '#aaa', 700);
    if (deck.length) { for (let i = 0; i < Math.min(4, deck.length); i++) { ctx.beginPath(); ctx.roundRect(W / 2 - 38 + i * 2, deckY - 52 - i * 2, 76, 104, 10); ctx.fillStyle = '#B71C1C'; ctx.fill(); ctx.strokeStyle = '#fff'; ctx.lineWidth = 2; ctx.stroke(); } text(ctx, '🐺', W / 2 + 6, deckY - 2, 34, '#fff'); }
    if (!cur) { text(ctx, deck.length ? s.kcTap : s.kcEmpty, W / 2, H * 0.55, 22, '#fff', 800); return; }
    const u = (t - at) / 0.45, ri = cur.r - 1, last = cur.r === 13 && kings === 4;
    const fly = ease(Math.min(1, u * 1.6)), flip = Math.min(1, u), sx = Math.abs(Math.cos(flip * Math.PI));
    const cw = Math.min(250, W * 0.64), ch = Math.min(cw * 1.36, H * 0.5), cx = W / 2, cy = deckY + fly * (H * 0.5 - deckY);
    ctx.save(); ctx.translate(cx, cy); ctx.scale(Math.max(0.02, sx), 1);
    ctx.beginPath(); ctx.roundRect(-cw / 2, -ch / 2, cw, ch, 22);
    if (flip < 0.5) { ctx.fillStyle = '#B71C1C'; ctx.fill(); text(ctx, '🐺', 0, 0, 64, '#fff'); }
    else {
      ctx.fillStyle = '#FFFDF7'; ctx.fill(); ctx.lineWidth = 4; ctx.strokeStyle = last ? '#FFC107' : '#ddd'; ctx.stroke();
      const col = cur.s === 1 || cur.s === 2 ? '#D32F2F' : '#212121', su = SUITS[cur.s];
      for (const rot of [0, Math.PI]) { ctx.save(); ctx.rotate(rot); text(ctx, RANK[cur.r], -cw / 2 + 26, -ch / 2 + 28, 26, col, 900); text(ctx, su, -cw / 2 + 26, -ch / 2 + 56, 22, col, 900); ctx.restore(); }
      const pop = 1 + 0.15 * Math.max(0, Math.sin(Math.min(1, (u - 0.5) * 2) * Math.PI));
      text(ctx, last ? '🍺' : KC_ICONS[ri], 0, -ch * 0.06, cw * 0.42 * pop, '#000');
    }
    ctx.restore();
    if (flip >= 0.5) {
      const ty = cy + ch / 2 + 32;
      text(ctx, last ? s.kcLastKing : `${KC_ICONS[ri]} ${names[ri]}`, W / 2, ty, last ? 22 : 28, last ? '#FFD740' : '#fff', 900);
      wrapText(ctx, last ? s.kcLastKingSub : descs[ri], W / 2, ty + 34, W - 40, 16, '#bbb');
      if (last) chompWolf(ctx, W / 2, ty + 90, 26, Math.abs(Math.sin(t * 8)), 1);
    }
  });
  cv.canvas.addEventListener('pointerdown', () => {
    if (!deck.length) return;
    if (cur && now() - at < 0.4) return;
    if (cur && cur.r === 13 && kings === 4) kings = 0; // 킹스컵 마시고 다시 시작
    cur = deck.pop(); at = now(); if (cur.r === 13) kings++;
    vibe.tap(); if (cur.r === 13 && kings === 4) vibe.strong();
  });
  function showRules() {
    const ov = h('div', { class: 'overlay' });
    ov.addEventListener('click', (e) => { if (e.target === ov) ov.remove(); });
    ov.append(h('div', { class: 'dialog', style: 'max-height:80vh;overflow:auto;text-align:left' },
      h('div', { class: 'dt' }, s.kcRules),
      ...KC_ICONS.map((ic, i) => h('div', { style: 'display:grid;grid-template-columns:28px 30px 1fr;gap:8px;align-items:start;padding:7px 0;border-bottom:1px solid #2C2C3E' },
        h('b', { style: 'color:var(--accent)' }, RANK[i + 1]), h('span', { style: 'font-size:22px' }, ic),
        h('div', {}, h('b', {}, names[i]), h('div', { style: 'color:var(--sub);font-size:13px' }, descs[i])))),
      btn(s.ok, () => ov.remove())));
    root.append(ov);
  }
  return () => cv.stop();
}
function wrapText(ctx, str, x, y, maxW, size, color) {
  ctx.font = `700 ${size}px -apple-system, "Noto Sans KR", sans-serif`;
  const words = str.includes(' ') ? str.split(' ') : [...str];
  const sep = str.includes(' ') ? ' ' : '';
  const out = []; let line = '';
  for (const w of words) { const tryL = line ? line + sep + w : w; if (ctx.measureText(tryL).width > maxW && line) { out.push(line); line = w; } else line = tryL; }
  if (line) out.push(line);
  out.forEach((l, i) => text(ctx, l, x, y + i * (size + 6), size, color, 700));
}
