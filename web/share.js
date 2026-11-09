// 📤 앱 공유하기 — 친구 폰(안드로이드 / 아이폰·PC) 고르고 → 앱으로 공유 · 링크만 복사 · QR (앱 ShareScreen.kt 와 같음)
/* global qrcode */
import { S, h, top, btn } from './core.js';

export const SHARE_APK = 'https://github.com/wanopro/wolfgames/releases/latest/download/WolfGames.apk';
export const SHARE_WEB = 'https://wanopro.github.io/wolfgames/';

function qrBox(text) {
  const box = h('div', { style: 'background:#fff;border-radius:18px;padding:14px;width:min(62vw,240px);aspect-ratio:1;margin:4px auto 0' });
  if (typeof qrcode === 'undefined') return box;
  qrcode.stringToBytes = qrcode.stringToBytesFuncs['UTF-8'];
  const q = qrcode(0, 'M'); q.addData(text); q.make();
  box.innerHTML = q.createSvgTag({ cellSize: 8, margin: 0, scalable: true });
  const svg = box.querySelector('svg'); svg.style.width = '100%'; svg.style.height = '100%'; svg.style.display = 'block';
  return box;
}

function toast(root, msg) {
  const el = h('div', { style: 'position:fixed;left:50%;bottom:90px;transform:translateX(-50%);background:rgba(20,20,28,.95);color:#fff;padding:12px 20px;border-radius:22px;font-weight:800;z-index:50;box-shadow:0 4px 16px rgba(0,0,0,.4)' }, msg);
  root.append(el); setTimeout(() => el.remove(), 1600);
}

async function copyText(t) {
  try { await navigator.clipboard.writeText(t); return true; } catch (_) { /* 아래 방법 */ }
  try { const a = h('textarea', { style: 'position:fixed;opacity:0' }); a.value = t; document.body.append(a); a.select(); const ok = document.execCommand('copy'); a.remove(); return ok; } catch (_) { return false; }
}

export function shareScreen(root, back) {
  const s = S();
  let os = 'android';
  try { if (localStorage.getItem('wg-share-os') === 'ios') os = 'ios'; } catch (_) { /* 무시 */ }
  const body = h('div', { class: 'scroll' });
  root.append(top(s.shTitle, back), body);
  const draw = () => {
    const link = os === 'android' ? SHARE_APK : SHARE_WEB;
    const pick = (v, label) => { const b = btn(label, () => { os = v; try { localStorage.setItem('wg-share-os', v); } catch (_) { /* 무시 */ } draw(); }, os === v ? 'btn' : 'btn alt'); b.style.flex = '1'; b.style.margin = '0'; return b; };
    body.replaceChildren(
      h('div', { class: 'desc', style: 'font-weight:800;color:var(--text)' }, s.shWhich),
      h('div', { style: 'display:flex;gap:8px' }, pick('android', s.shAndroid), pick('ios', s.shIphone)),
      h('div', { class: 'desc' }, os === 'android' ? s.shAndroidTip : s.shIphoneTip),
      qrBox(link),
      h('div', { class: 'desc', style: 'text-align:center;margin-top:6px' }, s.shQr),
      h('div', { style: 'text-align:center;font-size:12px;color:var(--sub);word-break:break-all;margin:-4px 0 10px' }, link),
      btn(s.shSend, async () => {
        const text = `${s.shMsg}\n${link}`;
        if (navigator.share) { try { await navigator.share({ title: s.shMsg, text: s.shMsg, url: link }); return; } catch (e) { if (e && e.name === 'AbortError') return; } }
        if (await copyText(text)) toast(root, s.shCopied);
      }),
      btn(s.shCopy, async () => { if (await copyText(link)) toast(root, s.shCopied); }, 'btn alt'));
  };
  draw();
}
