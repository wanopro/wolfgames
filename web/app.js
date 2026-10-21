// 울프 게임즈 웹 버전 — 화면 이동, 메인, 언어 설정
import { S, h, btn, card, top, lang, LANGS, vibe, VERSION } from './core.js';
import { rollDice, shutBox, fiveDice } from './games-dice.js';
import { wolfTeeth, ladder, roulette } from './games-pick.js';
import { race, pirate, arrow } from './games-fun.js';
import { four } from './games-four.js';

const HOMEPAGE = 'https://wolf.community';
const app = document.getElementById('app');
let cleanup = null;

const SCREENS = {
  home: homeScreen,
  dice: diceMenu,
  roll: rollDice,
  shut: shutBox,
  five: fiveDice,
  teeth: wolfTeeth,
  ladder,
  roulette,
  race,
  pirate,
  arrow,
  four,
  lang: langScreen,
};
const PARENT = { roll: 'dice', shut: 'dice', five: 'dice' };

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
        h('h1', {}, s.appName),
        h('p', {}, s.appSubtitle),
        btn([h('span', { class: 'flag' }, cur.flag), `🌐 ${s.language}`], () => go('lang'), 'chip'))),
    h('div', { style: 'height:4px' }),
    card('🎲', s.menuDice, s.menuDiceSub, () => go('dice')),
    card('🐺', s.menuCroc, s.menuCrocSub, () => go('teeth')),
    card('🪜', s.menuLadder, s.menuLadderSub, () => go('ladder')),
    card('🎡', s.menuRoulette, s.menuRouletteSub, () => go('roulette')),
    card('🏁', s.menuRace, s.menuRaceSub, () => go('race')),
    card('🏴‍☠️', s.menuPirate, s.menuPirateSub, () => go('pirate')),
    card('➡️', s.menuArrow, s.menuArrowSub, () => go('arrow')),
    card('🔴', s.menuFour, s.menuFourSub, () => go('four')),
    h('div', { class: 'footer' }, 'Powered by ', btn('WOLF', ask, ''), '(feat. segna)'),
    h('div', { class: 'footer', style: 'padding-top:0;font-size:12px' }, h('a', { href: 'manual.html', style: 'color:var(--sub)' }, '📖 설치 안내'), h('span', { style: 'opacity:.6' }, `  ·  v${VERSION}`)));
  root.append(scroll);
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
    card('🎲', s.fiveTitle, s.fiveSub, () => go('five'))));
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
