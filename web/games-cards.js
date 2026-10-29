// 카드 게임 2종: Up/Down / 호치민
import { C, S, h, btn, top, stepper, toggle, setupPanel, vibe, sleep, cardEl, newDeck, isRed, shuffle } from './core.js';

// ===================== Up/Down =====================
/** 판정: 'ok' | 'fail' | 'same'. 기준 카드가 A·K면 색깔(red/black)로 */
export function judgeUpDown(base, next, guess) {
  if (base.r === 1 || base.r === 13) return (guess === 'red') === isRed(next) ? 'ok' : 'fail';
  if (next.r === base.r) return 'same';
  return (guess === 'up' ? next.r > base.r : next.r < base.r) ? 'ok' : 'fail';
}

export function upDown(root, back) {
  const s = S();
  let n = 4, stack = false, alive = true;
  const setup = () => {
    root.replaceChildren(top(s.udTitle, back), h('div', { class: 'body' }, setupPanel(s.udDesc, play,
      stepper(s.players, n, 2, 8, (v) => { n = v; }),
      toggle(s.udStackOpt, stack, (v) => { stack = v; }))));
  };

  const play = () => {
    let deck = newDeck(), turn = 0, pot = 1, busy = false, waiting = false;
    let base = deck.pop();
    const draw = () => {
      if (!deck.length) deck = shuffle(newDeck().filter((c) => !(c.r === base.r && c.s === base.s)));
      return deck.pop();
    };
    const baseEl = cardEl(base, 128);
    const nextEl = cardEl(null, 128);
    const turnEl = h('div', { class: 'turn' });
    const info = h('div', { class: 'info' });
    const resEl = h('div', { class: 'bigres', style: 'min-height:84px' });
    const ctl = h('div', { class: 'row', style: 'display:flex;gap:10px;width:100%' });
    const ud = h('div', { class: 'ud' }, turnEl, info, h('div', { class: 'cards' }, baseEl, nextEl), resEl, ctl);
    root.replaceChildren(top(s.udTitle, setup, btn(s.shuffle, () => { if (!busy) { deck = newDeck(); base = deck.pop(); pot = 1; baseEl.set(base); waiting = false; render(); } }, 'tbtn accent')), h('div', { class: 'body' }, ud));

    const render = () => {
      const color = C.players[turn % C.players.length];
      turnEl.replaceChildren(h('span', { style: `color:${color}` }, s.playerN(turn + 1)), h('small', {}, s.udGuess));
      info.replaceChildren(...[h('span', { class: 'pill' }, s.udLeft(deck.length)), stack ? h('span', { class: 'pill hot' }, s.udPot(pot)) : null].filter(Boolean));
      if (waiting) { ctl.replaceChildren(btn(s.udNext, next)); return; }
      resEl.replaceChildren();
      const ak = base.r === 1 || base.r === 13;
      ctl.replaceChildren(...(ak
        ? [btn(`♥ ${s.udRed}`, () => guess('red'), 'btn dn'), btn(`♠ ${s.udBlack}`, () => guess('black'), 'btn blk')]
        : [btn(`▲ ${s.udUp}`, () => guess('up'), 'btn up'), btn(`▼ ${s.udDown}`, () => guess('down'), 'btn dn')]));
    };

    let last = null;
    const guess = async (g) => {
      if (busy || waiting) return;
      busy = true;
      ctl.querySelectorAll('button').forEach((b) => { b.disabled = true; });
      const c = draw();
      nextEl.set(c); await sleep(520); if (!alive) return;
      const r = judgeUpDown(base, c, g);
      last = { r, c };
      if (r === 'ok') {
        vibe.tap();
        resEl.replaceChildren(...[h('div', { class: 'pops', style: 'color:#69F0AE' }, `⭕ ${s.udSuccess}`), stack ? h('small', {}, s.udPot(pot + 1)) : null].filter(Boolean));
        if (stack) pot++;
      } else if (r === 'same') {
        vibe.strong();
        resEl.replaceChildren(h('div', { class: 'pops', style: `color:${C.win}` }, `🔥 ${s.udSame}`), h('small', {}, s.playerN(turn + 1)));
      } else {
        vibe.strong();
        resEl.replaceChildren(h('div', { class: 'pops', style: `color:${C.win}` }, `❌ ${s.udFail}`), h('small', {}, `${s.playerN(turn + 1)} · ${s.udDrink(stack ? pot : 1)}`));
      }
      waiting = true; busy = false; render();
    };

    const next = async () => {
      if (busy) return;
      busy = true;
      const { r, c } = last;
      if (r === 'ok') { base = c; }
      else { pot = 1; base = draw(); }
      baseEl.set(base); nextEl.set(null);
      turn = (turn + 1) % n;
      waiting = false; render();
      await sleep(300); busy = false;
    };
    render();
  };

  setup();
  return () => { alive = false; };
}

// ===================== 호치민 =====================
/** 카드 점수: A=1, 2~9, 10·J·Q·K=10 */
export const hcmVal = (c) => Math.min(c.r, 10);
/** 내 숫자(뒷자리): 10·J·Q·K → 0 */
export const hcmDigit = (c) => hcmVal(c) % 10;

export function hoChiMinh(root, back) {
  const s = S();
  let n = 3, alive = true;
  const setup = () => {
    root.replaceChildren(top(s.hcmTitle, back), h('div', { class: 'body' }, setupPanel(s.hcmDesc, play,
      stepper(s.players, n, 2, 4, (v) => { n = v; }))));
  };

  const play = () => {
    let mine, field, deck, turn, busy, hit, msg;
    const fd = h('div', { class: 'fd' });
    root.replaceChildren(fd);
    const deal = () => {
      const d = newDeck();
      mine = Array.from({ length: n }, () => d.pop());
      field = [d.pop(), d.pop()];
      deck = d; turn = 0; busy = false; hit = []; msg = null;
      render();
    };
    const refill = () => {
      const used = [...mine, ...field];
      deck = shuffle(newDeck().filter((c) => !used.some((u) => u.r === c.r && u.s === c.s)));
    };

    const pick = async (k) => {
      if (busy) return;
      busy = true; vibe.tap();
      if (!deck.length) refill();
      field[k] = deck.pop();
      hit = []; msg = null;
      render(k);
      await sleep(480); if (!alive) return;
      const sum = hcmVal(field[0]) + hcmVal(field[1]), d = sum % 10;
      hit = [...Array(n).keys()].filter((i) => hcmDigit(mine[i]) === d);
      msg = { sum, d };
      if (hit.length) vibe.strong(); else vibe.tap();
      turn = (turn + 1) % n;
      busy = false; render();
    };

    const area = (i, rotated) => {
      const color = C.players[i], bad = hit.includes(i), my = turn === i && !busy;
      const a = h('div', { class: 'area' + (rotated ? ' rot' : ''), style: `background:${color}${bad ? '66' : my ? '30' : '14'};border-color:${color}${my || bad ? 'ff' : '70'};border-width:${my ? 3 : 2}px;justify-content:space-between` },
        h('div', { class: 'pn', style: `color:${color}` }, s.playerN(i + 1) + (my ? ' 👈' : '')),
        cardEl(mine[i], n === 4 ? 58 : 66),
        h('div', { class: 'st big' + (bad ? ' win' : ''), style: 'font-size:18px' }, bad ? '💀 🍺' : ''));
      return a;
    };

    const mid = (flip) => {
      const cs = field.map((c, k) => {
        const el = cardEl(flip === k ? null : c, 74);
        el.classList.add('pick');
        el.addEventListener('click', () => pick(k));
        if (flip === k) requestAnimationFrame(() => requestAnimationFrame(() => el.set(c)));
        return el;
      });
      let text = s.hcmPick, cls = 'msg';
      if (msg) {
        text = s.hcmSum(msg.sum, msg.d) + '\n' + (hit.length ? s.hcmPenalty(hit.map((i) => 'P' + (i + 1)).join(', ')) : s.hcmNone);
        if (hit.length) cls += ' win';
      }
      return h('div', { class: 'mid2' },
        h('div', { class: 'ctl' }, btn(s.exit, setup, 'tbtn'), h('div', { style: `color:${C.players[turn]};font-weight:800` }, s.playerN(turn + 1)), btn(s.newGame, deal, 'tbtn accent')),
        h('div', { class: 'fcards' }, cs[0], h('span', { style: 'font-size:22px;color:var(--sub)' }, '+'), cs[1]),
        h('div', { class: cls }, text));
    };

    const render = (flip = -1) => {
      if (n === 2) fd.replaceChildren(h('div', { class: 'half' }, area(1, true)), mid(flip), h('div', { class: 'half' }, area(0, false)));
      else if (n === 3) fd.replaceChildren(h('div', { class: 'half' }, area(1, true), area(2, true)), mid(flip), h('div', { class: 'half' }, area(0, false)));
      else fd.replaceChildren(h('div', { class: 'half' }, area(1, true), area(2, true)), mid(flip), h('div', { class: 'half' }, area(0, false), area(3, false)));
    };
    deal();
  };

  setup();
  return () => { alive = false; };
}
