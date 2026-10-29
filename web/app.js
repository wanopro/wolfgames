// 울프 게임즈 웹 버전 — 화면 이동, 메인, 언어 설정
import { S, h, btn, card, top, lang, LANGS, vibe, VERSION, dualize, one, SEP, main1 } from './core.js';
import { rollDice, shutBox, fiveDice, d789, diceDuel } from './games-dice.js';
import { reaction, colorGame, stopwatch, bombGame } from './games-quick.js';
import { upDown, hoChiMinh } from './games-cards.js';
import { fingerPick } from './games-finger.js';
import { wolfTeeth, ladder, roulette } from './games-pick.js';
import { race, pirate, arrow } from './games-fun.js';
import { four } from './games-four.js';
import { calculator, exchange } from './calc.js';
import { CHANGELOG } from './changelog.js';
import { tugWar, slapDuel, beerPour, coinToss, kingsCup } from './games-v15.js';
import { qrScreen, qrEdit } from './qr.js';
import { plinko, ticTacToe, gomoku } from './games-v16.js';
import { hotelScreen, hotelEdit } from './hotel.js';

const HOMEPAGE = 'https://wolf.community';
const FEEDBACK = 'https://wolf.community/traveltalk/6817682';
// 이모지가 폰마다 다르게(또는 네모로) 나오는 것 대신 직접 그린 아이콘
const icon = (name) => h('img', { src: `icons/${name}.png`, alt: '', style: 'width:44px;height:44px;display:block' });
const app = document.getElementById('app');
new MutationObserver((ms) => { if (!lang.second) return; for (const m of ms) { if (m.type === 'characterData') dualize(m.target.parentNode || app); else m.addedNodes.forEach((n) => (n.nodeType === 3 ? n.parentNode && dualize(n.parentNode) : n.nodeType === 1 && dualize(n))); } }).observe(app, { childList: true, subtree: true, characterData: true });
let cleanup = null;

const SCREENS = {
  home: homeScreen,
  dice: diceMenu,
  roll: rollDice,
  shut: shutBox,
  five: fiveDice,
  d789,
  duel: diceDuel,
  quick: quickMenu,
  react: reaction,
  color: colorGame,
  stop: stopwatch,
  bomb: bombGame,
  cards: cardMenu,
  fingers: fingerMenu,
  finger: fingerPick,
  wolves: wolfMenu,
  luck: luckMenu,
  battle: battleMenu,
  updown: upDown,
  hcm: hoChiMinh,
  teeth: wolfTeeth,
  ladder,
  roulette,
  race,
  pirate,
  arrow,
  four,
  calc: (root, b) => calculator(root, b, go),
  fx: exchange,
  lang: langScreen,
  log: logScreen,
  tug: tugWar,
  slap: slapDuel,
  beer: beerPour,
  coin: coinToss,
  kings: kingsCup,
  qr: (root, b) => qrScreen(root, b, go),
  qredit: (root, b) => qrEdit(root, b, go),
  plinko,
  ttt: ticTacToe,
  gomoku,
  hotel: (root, b) => hotelScreen(root, b, go),
  hoteledit: (root, b) => hotelEdit(root, b, go),
};
const PARENT = { roll: 'dice', shut: 'dice', five: 'dice', d789: 'dice', duel: 'dice', react: 'quick', color: 'quick', stop: 'quick', bomb: 'luck', updown: 'cards', hcm: 'cards', finger: 'fingers', teeth: 'wolves', race: 'wolves', pirate: 'wolves', arrow: 'wolves', ladder: 'luck', roulette: 'luck', four: 'battle', tug: 'battle', slap: 'battle', ttt: 'battle', gomoku: 'battle', plinko: 'luck', beer: 'quick', coin: 'luck', kings: 'cards' };

// ---------- 화면 이동 (휴대폰 뒤로가기 버튼과 연동) ----------
function show(name) {
  if (cleanup) { try { cleanup(); } catch (_) { /* 무시 */ } cleanup = null; }
  const root = h('div', { class: 'screen' });
  app.replaceChildren(root);
  document.title = one(S().appName);
  const fn = SCREENS[name] || homeScreen;
  cleanup = fn(root, back) || null;
}
function go(name) { history.pushState({ s: name }, ''); show(name); }
function back() {
  if (history.state && history.state.s && history.state.s !== 'home') history.back();
  else show('home');
}
window.addEventListener('popstate', (e) => show((e.state && e.state.s) || 'home'));

// ---------- 메인 ----------
function homeScreen(root) {
  const s = S();
  const cur = LANGS.find((l) => l.code === lang.current);
  const ask = () => confirmLink(root);
  const m = (x) => String(x).split(SEP)[0]; // 맨 위 머리 부분은 메인 언어만
  const logo = h('img', { src: 'icons/logo.png', alt: m(s.appName) });
  logo.addEventListener('click', () => { vibe.tap(); ask(); });
  const scroll = h('div', { class: 'scroll' },
    h('div', { class: 'hero' },
      logo,
      h('div', {},
        h('h1', { style: m(s.appName).length > 6 ? 'font-size:27px;line-height:31px' : '' }, m(s.appName)),
        h('p', {}, m(s.appSubtitle), ' ', h('a', { href: FEEDBACK, target: '_blank', rel: 'noopener', class: 'fb', onclick: () => vibe.tap() }, m(s.feedback))),
        h('div', { style: 'display:flex;gap:6px;flex-wrap:wrap' },
          btn([h('span', { class: 'flag' }, cur.flag), `🌐 ${m(s.language)}`], () => go('lang'), 'chip'),
          btn(`🧮 ${m(s.calcTitle)}`, () => go('calc'), 'chip'),
          btn('▦ QR', () => go('qr'), 'chip'),
          btn(m(s.hotelBtn), () => go('hotel'), 'chip')))),
    h('div', { style: 'height:4px' }),
    card('🎲', s.menuDice, s.menuDiceSub, () => go('dice')),
    card('🃏', s.menuCards, s.menuCardsSub, () => go('cards')),
    card('👆', s.menuFinger, s.menuFingerSub, () => go('fingers')),
    card('⚡', s.menuQuick, s.menuQuickSub, () => go('quick')),
    card('🐺', s.menuWolf, s.menuWolfSub, () => go('wolves')),
    card('🍀', s.menuLuck, s.menuLuckSub, () => go('luck')),
    card('⚔️', s.menuBattle, s.menuBattleSub, () => go('battle')),
    h('div', { class: 'footer' }, 'Powered by ', btn('WOLF', ask, ''), '(feat. segna)'),
    h('div', { class: 'footer', style: 'padding-top:0;font-size:12px' }, h('a', { href: `manual.html?lang=${lang.current}`, style: 'color:var(--sub)' }, `📖 ${m(s.installGuide)}`), h('span', { style: 'opacity:.6' }, '  ·  '), btn(`📜 v${VERSION} ${m(s.logTitle)}`, () => go('log'), 'linkbtn')));
  // 누적 방문 수 (GoatCounter에서 '방문자 수 표시 허용'을 켜야 나옴, 못 가져오면 안 보임)
  const visits = h('div', { class: 'footer', style: 'padding-top:0;font-size:12px;opacity:.55;display:none' });
  scroll.append(visits);
  // 사이트 전체(TOTAL) → 안 되면 이 페이지 주소로 다시 시도
  const GC = 'https://wolfgames.goatcounter.com/counter/';
  const tryCount = (paths) => {
    if (!paths.length) return;
    fetch(GC + encodeURIComponent(paths[0]) + '.json', { cache: 'no-store' })
      .then((r) => (r.ok ? r.json() : Promise.reject(r.status)))
      .then((j) => { if (j && Number(String(j.count).replace(/\D/g, '')) > 0) { visits.textContent = `👀 ${j.count}`; visits.style.display = ''; } else tryCount(paths.slice(1)); })
      .catch((e) => { console.warn('[방문 수]', paths[0], e); tryCount(paths.slice(1)); });
  };
  /* 베타: 방문 수 안 셈 */
  root.append(scroll);
  whatsNew(root);
}

// ---------- 업데이트 내역 ----------
const fmtDate = (d) => d.replace(/-/g, '.');
function releaseCard(r, latest) {
  const s = S();
  return h('div', { class: 'rel' },
    h('div', { class: 'rh' }, h('b', {}, `v${r.ver}`), h('span', {}, fmtDate(r.date)), latest ? h('span', { class: 'new' }, main1(s.logLatest)) : null),
    h('ul', {}, r.items.map(([emo, tx]) => h('li', {}, h('span', { class: 'e' }, emo), h('span', {}, tx[lang.current] || tx.en)))));
}
function logScreen(root) {
  const s = S();
  root.append(top(`📜 ${s.logTitle}`, back), h('div', { class: 'scroll' }, CHANGELOG.map((r, i) => releaseCard(r, i === 0))));
}
/** 새 버전을 처음 열었을 때 한 번만 바뀐 점 보여주기 */
function whatsNew(root) {
  let seen = null;
  try { seen = localStorage.getItem('wg-seen'); } catch (_) { return; }
  if (seen === VERSION) return;
  try { localStorage.setItem('wg-seen', VERSION); } catch (_) { /* 무시 */ }
  const s = S(), r = CHANGELOG.find((x) => x.ver === VERSION) || CHANGELOG[0];
  const ov = h('div', { class: 'overlay' });
  ov.addEventListener('click', (e) => { if (e.target === ov) ov.remove(); });
  ov.append(h('div', { class: 'dialog wn' },
    h('div', { class: 'dt' }, `🐺 ${s.logNew(r.ver)}`),
    h('ul', {}, r.items.map(([emo, tx]) => h('li', {}, h('span', { class: 'e' }, emo), h('span', {}, tx[lang.current] || tx.en)))),
    btn(s.logOk, () => ov.remove()),
    btn(s.logAll, () => { ov.remove(); go('log'); }, 'btn alt')));
  root.append(ov);
}

/** "홈페이지로 이동하겠습니까?" 확인 창 */
function confirmLink(root) {
  const s = S();
  const ov = h('div', { class: 'overlay' });
  ov.addEventListener('click', (e) => { if (e.target === ov) ov.remove(); });
  ov.append(h('div', { class: 'confirm' },
    h('div', { class: 'ct' }, '🐺 WOLF'),
    h('div', { class: 'cb' }, `${s.goHomepage}\n${HOMEPAGE}`),
    h('div', { class: 'ca' },
      btn(s.cancel, () => ov.remove(), 'tbtn'),
      btn(s.ok, () => { ov.remove(); window.open(HOMEPAGE, '_blank', 'noopener'); }, 'tbtn accent'))));
  root.append(ov);
}

function diceMenu(root) {
  const s = S();
  root.append(top(s.menuDice, back), h('div', { class: 'scroll' },
    card('👆', s.rollTitle, s.rollSub, () => go('roll')),
    card('🔢', s.shutTitle, s.shutSub, () => go('shut')),
    card('🎲', s.fiveTitle, s.fiveSub, () => go('five')),
    card('🍶', s.d789Title, s.d789Sub, () => go('d789')),
    card('⚔️', s.duelTitle, s.duelSub, () => go('duel'))));
}

function fingerMenu(root) {
  const s = S();
  root.append(top(`👆 ${s.menuFinger}`, back), h('div', { class: 'scroll' },
    card('☝️', s.fingerTitle, s.fingerSub, () => go('finger'))));
}
function wolfMenu(root) {
  const s = S();
  root.append(top(`🐺 ${s.menuWolf}`, back), h('div', { class: 'scroll' },
    card('🐺', s.menuCroc, s.menuCrocSub, () => go('teeth')),
    card('🏁', s.menuRace, s.menuRaceSub, () => go('race')),
    card('🗡️', s.menuPirate, s.menuPirateSub, () => go('pirate')),
    card('🔄', s.menuArrow, s.menuArrowSub, () => go('arrow'))));
}
function quickMenu(root) {
  const s = S();
  root.append(top(`⚡ ${s.menuQuick}`, back), h('div', { class: 'scroll' },
    card('🟢', s.reactTitle, s.reactSub, () => go('react')),
    card('🎨', s.colorTitle, s.colorSub, () => go('color')),
    card('⏱️', s.stopTitle, s.stopSub, () => go('stop')),
    card('🍺', s.beerTitle, s.beerSub, () => go('beer'))));
}
function luckMenu(root) {
  const s = S();
  root.append(top(`🍀 ${s.menuLuck}`, back), h('div', { class: 'scroll' },
    card(icon('ladder'), s.menuLadder, s.menuLadderSub, () => go('ladder')),
    card('🎡', s.menuRoulette, s.menuRouletteSub, () => go('roulette')),
    card('💣', s.bombTitle, s.bombSub, () => go('bomb')),
    card(icon('coin'), s.coinTitle, s.coinSub, () => go('coin')),
    card('🔻', s.plinkoTitle, s.plinkoSub, () => go('plinko'))));
}
function battleMenu(root) {
  const s = S();
  root.append(top(`⚔️ ${s.menuBattle}`, back), h('div', { class: 'scroll' },
    card('🟢', s.tttTitle, s.tttSub, () => go('ttt')),
    card('🔴', s.menuFour, s.menuFourSub, () => go('four')),
    card('⚫', s.gomokuTitle, s.gomokuSub, () => go('gomoku')),
    card('🎀', s.tugTitle, s.tugSub, () => go('tug')),
    card('🐾', s.slapTitle, s.slapSub, () => go('slap'))));
}

function cardMenu(root) {
  const s = S();
  root.append(top(`🃏 ${s.menuCards}`, back), h('div', { class: 'scroll' },
    card('↕️', s.udTitle, s.udSub, () => go('updown')),
    card('🇻🇳', s.hcmTitle, s.hcmSub, () => go('hcm')),
    card('👑', s.kcTitle, s.kcSub, () => go('kings'))));
}

function langScreen(root) {
  const s = S();
  // 최대 2개: ① 메인 · ② 보조. 누른 걸 다시 누르면 빠짐 (메인을 빼면 보조가 메인이 됨)
  const pick = (code) => {
    const main = lang.pref, sec = lang.second;
    if (code === 'system') { lang.set('system'); }
    else if (code === main) { if (sec) { lang.set(sec); lang.setSecond(null); } }
    else if (code === sec) lang.setSecond(null);
    else if (main === 'system' && !sec) lang.set(code);
    else lang.setSecond(code);
    show('lang');
  };
  const rowFor = (code, flag, label) => {
    const n = lang.pref === code ? 1 : lang.second === code ? 2 : 0;
    return btn([h('span', { class: 'f' }, flag), h('span', {}, label), n ? h('span', { class: 'ck num' }, n === 1 ? '①' : '②') : null], () => pick(code), 'lang' + (n ? ' sel' : ''));
  };
  root.append(top(`🌐 ${s.language}`, back), h('div', { class: 'scroll', style: 'gap:10px' },
    h('div', { class: 'desc' }, s.langPickTwo),
    rowFor('system', '📱', s.followSystem),
    ...LANGS.map((l) => rowFor(l.code, l.flag, l.label))));
}

// ---------- 화면 꺼짐 방지 (지원하는 브라우저만) ----------
let wakeLock = null;
async function keepAwake() {
  try { if ('wakeLock' in navigator && document.visibilityState === 'visible') wakeLock = await navigator.wakeLock.request('screen'); } catch (_) { /* 무시 */ }
}
document.addEventListener('visibilitychange', () => { if (document.visibilityState === 'visible') keepAwake(); });
document.addEventListener('pointerdown', () => { if (!wakeLock) keepAwake(); }, { once: true });

// ---------- 오프라인 지원 ----------
if ('serviceWorker' in navigator && location.protocol === 'https:') {
  // 새 버전이 배포되면 바로 받아서 한 번 새로고침 (업데이트가 늦게 보이는 문제 방지)
  const hadSW = !!navigator.serviceWorker.controller;
  let reloaded = false;
  navigator.serviceWorker.addEventListener('controllerchange', () => { if (hadSW && !reloaded) { reloaded = true; location.reload(); } });
  navigator.serviceWorker.register('sw.js', { updateViaCache: 'none' }).then((reg) => {
    reg.update().catch(() => {});
    document.addEventListener('visibilitychange', () => { if (document.visibilityState === 'visible') reg.update().catch(() => {}); });
  }).catch(() => {});
}

// 시작
document.documentElement.lang = lang.current;
const start = (history.state && history.state.s) || 'home';
if (!history.state) history.replaceState({ s: 'home' }, '');
show(start);
export { go, PARENT };
