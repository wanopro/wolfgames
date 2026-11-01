// 🗣️ 회화 카드: 자주 쓰는 말을 현지어 큰 글씨로 (인터넷 없이, 문구는 i18n 에서)
import { S, h, btn, top, vibe, lang, LANGS, SEP } from './core.js';
import { STRINGS } from './i18n.js';

export const PH_ICONS = ["🚻", "👉", "🧾", "💰", "🌶️", "🌿", "🧊", "💧", "🍺", "🥡", "💳", "🏷️", "📄", "📸", "📶", "💊", "🆘", "🥜", "🍻", "🌙", "😍", "🥴", "🎁", "🤝", "🤬", "😱", "😤", "🤪", "🤣", "👋", "🙏", "🙇", "🐢", "📍", "✈️", "🛑", "⏱️", "🛎️", "🧳", "🧺", "❄️", "🙆", "👌", "🤔", "🗣️", "✍️", "😊", "👍", "👋", "🙇", "😓", "🙅", "✋", "👀", "🙉", "📅", "🛑", "🚉", "🚌", "🎫", "🧮", "🐢", "⚡", "➡️", "⬅️", "⬆️", "😋", "📖", "⭐", "🚪", "🕐", "🍳", "🔑", "🚿", "🧹", "⏰", "🥃", "🍢", "🎤", "💸", "🤢", "🏠", "👕", "📏", "💵", "🏧", "🔌", "🚓", "🏥", "🙄", "😒", "🤡", "🐔", "💨"];
// 카테고리별 문장 번호 (phCats 순서: 인사 · 교통 · 식당 · 숙소 · 술집 · 쇼핑·도움 · 장난 욕)
export const PH_CATS = [[29, 30, 31, 32, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56], [33, 34, 35, 36, 57, 58, 59, 60, 61, 62, 63, 64, 65], [1, 2, 4, 5, 6, 7, 9, 10, 12, 17, 66, 67, 68], [37, 38, 39, 40, 14, 69, 70, 71, 72, 73, 74, 75], [8, 18, 19, 20, 21, 22, 23, 76, 77, 78, 79, 80, 81], [3, 11, 13, 0, 15, 16, 82, 83, 84, 85, 86, 87, 88], [24, 25, 26, 27, 28, 89, 90, 91, 92, 93]];
const CAT_KEY = 'wg-ph-cat';
const KEY = 'wg-ph-to';
// 🔊 읽어주기: 휴대폰(브라우저)에 들어 있는 음성 사용 — 그 언어 음성이 깔려 있으면 인터넷 없이도 됨
const BCP = { ko: 'ko-KR', en: 'en-US', ja: 'ja-JP', zh: 'zh-CN', th: 'th-TH', vi: 'vi-VN' };
const synth = typeof window !== 'undefined' ? window.speechSynthesis : null;
export function voiceFor(code) {
  if (!synth) return null;
  const vs = synth.getVoices(), want = BCP[code].toLowerCase(), pre = code;
  return vs.find((v) => v.lang.toLowerCase().replace('_', '-') === want) || vs.find((v) => v.lang.toLowerCase().startsWith(pre)) || null;
}
export function speak(text, code) {
  if (!synth) return false;
  const v = voiceFor(code);
  synth.cancel();
  const u = new SpeechSynthesisUtterance(text); u.lang = BCP[code]; u.rate = 0.9; if (v) u.voice = v;
  synth.speak(u);
  return !!v || synth.getVoices().length === 0; // 목록을 아직 못 받았으면 일단 시도
}
const main = (x) => String(x).split(SEP)[0];
function defaultTo() {
  try { const v = localStorage.getItem(KEY); if (v && STRINGS[v]) return v; } catch (_) { /* 무시 */ }
  if (lang.second) return lang.second;
  return lang.current === 'en' ? 'ko' : 'en';
}

export function phraseScreen(root, back) {
  const s = S();
  let to = defaultTo();
  const mine = STRINGS[lang.current].phrases.split('|');
  const list = h('div', { style: 'display:flex;flex-direction:column;gap:8px' });
  const chips = h('div', { class: 'phlangs' });
  const cats = h('div', { class: 'phcats' });
  let cat = 0; try { const v = +localStorage.getItem(CAT_KEY); if (v >= 0 && v < PH_CATS.length) cat = v; } catch (_) { /* 무시 */ }
  const catNames = s.phCats.split('|');
  const draw = () => {
    const theirs = STRINGS[to].phrases.split('|');
    chips.replaceChildren(...LANGS.map((L) => btn([h('span', { class: 'flag' }, L.flag), L.label], () => { to = L.code; try { localStorage.setItem(KEY, to); } catch (_) { /* 무시 */ } vibe.tap(); draw(); }, 'chip' + (L.code === to ? ' on' : ''))));
    cats.replaceChildren(...catNames.map((c, k) => btn(c, () => { cat = k; try { localStorage.setItem(CAT_KEY, String(k)); } catch (_) { /* 무시 */ } vibe.tap(); draw(); }, 'chip' + (k === cat ? ' on' : ''))));
    list.replaceChildren(...PH_CATS[cat].map((i) => [mine[i], i]).flatMap(([m, i]) => [btn([h('span', { class: 'phic' }, PH_ICONS[i]), h('div', { class: 'phtx' }, h('b', {}, m), to !== lang.current ? h('span', {}, theirs[i]) : null)], () => { vibe.tap(); show(PH_CATS[cat].indexOf(i)); }, 'phrow')]));
  };
  const show = (k) => {
    const theirs = STRINGS[to].phrases.split('|'), ids = PH_CATS[cat], n = ids.length; let i = ids[k];
    const wrap = h('div', { class: 'qrfull phfull' }), note = h('div', { class: 'phnote' });
    const paint = () => {
      wrap.replaceChildren(...[
        h('div', { class: 'phbig-ic' }, PH_ICONS[i]),
        h('div', { class: 'phbig' }, theirs[i]),
        to !== lang.current ? h('div', { class: 'phsmall' }, mine[i]) : null,
        synth ? btn(s.phSpeak, () => { vibe.tap(); if (!speak(theirs[i], to)) note.textContent = s.phNoVoice; }, 'phspeak') : null,
        note,
        h('div', { class: 'qrbar' },
          btn('‹', () => { k = (k + n - 1) % n; i = ids[k]; note.textContent = ''; vibe.tap(); paint(); }, 'qrbtn2'),
          btn(main(s.qrClose), () => { if (synth) synth.cancel(); wrap.remove(); }, 'qrbtn'),
          btn('›', () => { k = (k + 1) % n; i = ids[k]; note.textContent = ''; vibe.tap(); paint(); }, 'qrbtn2'))].filter(Boolean));
    };
    paint(); root.append(wrap);
  };
  draw();
  root.append(top(`🗣️ ${s.phTitle}`, back), h('div', { class: 'scroll', style: 'gap:10px' },
    h('div', { class: 'desc' }, s.phDesc),
    h('div', { class: 'lbl', style: 'color:var(--sub);font-size:14px' }, s.phTo), chips, cats, list));
}
