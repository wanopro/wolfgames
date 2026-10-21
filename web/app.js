// 울프 게임즈 웹 버전 — 화면 이동, 메인, 언어 설정
import { S, h, btn, card, top, lang, LANGS, vibe, VERSION } from './core.js';
import { rollDice, shutBox, fiveDice, d789 } from './games-dice.js';
import { upDown, hoChiMinh } from './games-cards.js';
import { wolfTeeth, ladder, roulette } from './games-pick.js';
import { race, pirate, arrow } from './games-fun.js';
import { four } from './games-four.js';
import { calculator, exchange } from './calc.js';
import { CHANGELOG } from './changelog.js';

const HOMEPAGE = 'https://wolf.community';
const app = document.getElementById('app');
let cleanup = null;

const SCREENS = {
  home: homeScreen,
  dice: diceMenu,
  roll: rollDice,
  shut: shutBox,
  five: fiveDice,
  d789,
  cards: cardMenu,
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
};
const PARENT = { roll: 'dice', shut: 'dice', five: 'dice', d789: 'dice', updown: 'cards', hcm: 'cards' };

// ---------- 화면 이동 (휴대폰 뒤로가기 버튼과 연동) ----------
function show(name) {
  if (cleanup) { try { cleanup(); } catch (_) { /* 무시 */ } cleanup = null; }
  const root = h('div', { class: 'screen' });
  app.replaceChildren(root);
  document.title = S().appName;
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
  const logo = h('img', { src: 'icons/logo.png', alt: s.appName });
  logo.addEventListener('click', () => { vibe.tap(); ask(); });
  const scroll = h('div', { class: 'scroll' },
    h('div', { class: 'hero' },
      logo,
      h('div', {},
        h('h1', { style: s.appName.length > 6 ? 'font-size:27px;line-height:31px' : '' }, s.appName),
        h('p', {}, s.appSubtitle),
        h('div', { style: 'display:flex;gap:6px;flex-wrap:wrap' },
          btn([h('span', { class: 'flag' }, cur.flag), `🌐 ${s.language}`], () => go('lang'), 'chip'),
          btn(`🧮 ${s.calcTitle}`, () => go('calc'), 'chip')))),
    h('div', { style: 'height:4px' }),
    card('🎲', s.menuDice, s.menuDiceSub, () => go('dice')),
    card('🃏', s.menuCards, s.menuCardsSub, () => go('cards')),
    card('🐺', s.menuCroc, s.menuCrocSub, () => go('teeth')),
    card('🪜', s.menuLadder, s.menuLadderSub, () => go('ladder')),
    card('🎡', s.menuRoulette, s.menuRouletteSub, () => go('roulette')),
    card('🏁', s.menuRace, s.menuRaceSub, () => go('race')),
    card('🗡️', s.menuPirate, s.menuPirateSub, () => go('pirate')),
    card('🔄', s.menuArrow, s.menuArrowSub, () => go('arrow')),
    card('🔴', s.menuFour, s.menuFourSub, () => go('four')),
    h('div', { class: 'footer' }, 'Powered by ', btn('WOLF', ask, ''), '(feat. segna)'),
    h('div', { class: 'footer', style: 'padding-top:0;font-size:12px' }, h('a', { href: `manual.html?lang=${lang.current}`, style: 'color:var(--sub)' }, `📖 ${s.installGuide}`), h('span', { style: 'opacity:.6' }, '  ·  '), btn(`📜 v${VERSION} ${s.logTitle}`, () => go('log'), 'linkbtn')));
  root.append(scroll);
  whatsNew(root);
}

// ---------- 업데이트 내역 ----------
const fmtDate = (d) => d.replace(/-/g, '.');
function releaseCard(r, latest) {
  const s = S();
  return h('div', { class: 'rel' },
    h('div', { class: 'rh' }, h('b', {}, `v${r.ver}`), h('span', {}, fmtDate(r.date)), latest ? h('span', { class: 'new' }, s.logLatest) : null),
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
    card('🍶', s.d789Title, s.d789Sub, () => go('d789'))));
}

function cardMenu(root) {
  const s = S();
  root.append(top(`🃏 ${s.menuCards}`, back), h('div', { class: 'scroll' },
    card('↕️', s.udTitle, s.udSub, () => go('updown')),
    card('🇻🇳', s.hcmTitle, s.hcmSub, () => go('hcm'))));
}

function langScreen(root) {
  const s = S();
  const rowFor = (code, flag, label) => {
    const sel = lang.pref === code;
    return btn([h('span', { class: 'f' }, flag), h('span', {}, label), sel ? h('span', { class: 'ck' }, '✓') : null], () => {
      lang.set(code); show('lang');
    }, 'lang' + (sel ? ' sel' : ''));
  };
  root.append(top(`🌐 ${s.language}`, back), h('div', { class: 'scroll', style: 'gap:10px' },
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
  navigator.serviceWorker.register('sw.js').catch(() => {});
}

// 시작
document.documentElement.lang = lang.current;
const start = (history.state && history.state.s) || 'home';
if (!history.state) history.replaceState({ s: 'home' }, '');
show(start);
export { go, PARENT };
