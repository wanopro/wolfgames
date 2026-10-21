"""설치 안내 만들기: web/manual-i18n.js + 언어별 단계 그림(web/manual/<lang>/*.jpg)
사용: 저장소 최상위에서  python3 tools/build_manual.py  (웹 서버가 localhost:8765 에서 web/ 를 제공 중이어야 함)
그림은 실제 화면을 단순화해서 다시 그린 것 (설명용)
"""
import base64
import io
import json
import os
import sys

from PIL import Image
from playwright.sync_api import sync_playwright

sys.path.insert(0, os.path.dirname(__file__))
from manual_text import PAGE, SHOT  # noqa: E402

WEB = 'web'
SERVER = 'http://localhost:8765/'
APPNAME = {'ko': '울프 게임즈', 'en': 'Wolf Games', 'ja': 'ウルフゲームズ', 'zh': '沃夫游戏', 'th': 'Wolf Games', 'vi': 'Wolf Games'}

# ---------- 1) 문구 파일 ----------
with open(f'{WEB}/manual-i18n.js', 'w', encoding='utf-8') as f:
    f.write('// 자동 생성: tools/build_manual.py (문구 원본은 tools/manual_text.py)\n')
    f.write('export const MANUAL = ' + json.dumps(PAGE, ensure_ascii=False, indent=1) + ';\n')
print('wrote manual-i18n.js')

icon_b64 = base64.b64encode(open(f'{WEB}/icons/icon-192.png', 'rb').read()).decode()
W, H = 360, 740
BASE = f"""*{{box-sizing:border-box;margin:0;padding:0}}
body{{width:{W}px;height:{H}px;overflow:hidden;font-family:'Noto Sans CJK KR','Noto Sans CJK JP','Noto Sans CJK SC','Noto Sans Thai','Noto Color Emoji',sans-serif;background:#fff;color:#1b1b1f;position:relative}}
.hl{{position:absolute;border:4px solid #FF2D55;border-radius:14px;box-shadow:0 0 0 4px rgba(255,45,85,.25);pointer-events:none}}
.badge{{position:absolute;width:34px;height:34px;border-radius:50%;background:#FF2D55;color:#fff;font-weight:900;font-size:19px;display:flex;align-items:center;justify-content:center;box-shadow:0 2px 6px rgba(0,0,0,.3)}}
.dim{{position:absolute;inset:0;background:rgba(0,0,0,.45)}}
.status{{height:28px;display:flex;justify-content:space-between;align-items:center;padding:0 18px;font-size:13px;font-weight:600}}
"""
# data-hl="번호" 가 붙은 요소 둘레에 빨간 테두리와 번호를 자동으로 그림
MARK = """<script>
document.querySelectorAll('[data-hl]').forEach(el=>{const r=el.getBoundingClientRect(),p=6;
const d=document.createElement('div');d.className='hl';Object.assign(d.style,{left:(r.left-p)+'px',top:(r.top-p)+'px',width:(r.width+p*2)+'px',height:(r.height+p*2)+'px'});document.body.append(d);
const b=document.createElement('div');b.className='badge';b.textContent=el.dataset.hl;
let bx=r.right-10,by=r.top-22;if(bx>326)bx=326;if(by<2)by=r.bottom-8;Object.assign(b.style,{left:bx+'px',top:by+'px'});document.body.append(b);});
</script>"""


def page(body, bg='#fff', color='#1b1b1f'):
    return f'<html><head><meta charset="utf-8"><style>{BASE}</style></head><body style="background:{bg};color:{color}">{body}{MARK}</body></html>'


def shots(lang, home_b64):
    t = SHOT[lang]
    name = APPNAME[lang]
    icon = f'<img src="data:image/png;base64,{icon_b64}" style="width:64px;height:64px;border-radius:16px">'
    A = '<div class="status"><span>10:20</span><span>📶 🔋</span></div>'
    chrome = ('<div style="height:56px;display:flex;align-items:center;gap:10px;padding:0 12px;background:#f1f3f4">'
              '<div style="flex:1;height:40px;border-radius:20px;background:#fff;display:flex;align-items:center;padding:0 14px;font-size:14px;color:#444">🔒 github.com/wanopro/wolfgames/releases</div><span style="font-size:20px">⋮</span></div>')
    release = (A + chrome + '<div style="padding:18px 16px">'
               f'<div style="font-size:22px;font-weight:800">{name} v1.1</div>'
               f'<div style="font-size:13px;color:#666;margin:6px 0 18px">wanopro {t["released"]}</div>'
               '<div style="border:1px solid #ddd;border-radius:10px">'
               '<div style="padding:12px 14px;font-weight:700;border-bottom:1px solid #eee">▾ Assets 3</div>'
               '<div style="padding:14px;display:flex;justify-content:space-between;border-bottom:1px solid #eee;font-size:15px"><span data-hl="{N1}" style="color:#0969da;font-weight:700">📦 WolfGames.apk</span><span style="color:#666">9.9 MB</span></div>'
               '<div style="padding:14px;font-size:15px;color:#0969da;border-bottom:1px solid #eee">📄 Source code (zip)</div>'
               '<div style="padding:14px;font-size:15px;color:#0969da">📄 Source code (tar.gz)</div></div></div>')

    def dialog(title, body, buttons, ic='', hl_last=None):
        bs = ''.join(f'<span {"data-hl=" + chr(34) + str(hl_last) + chr(34) if (hl_last and i == len(buttons) - 1) else ""} style="padding:10px 14px;font-weight:700;color:#0b57d0">{b}</span>' for i, b in enumerate(buttons))
        icn = f'<div style="display:flex;justify-content:center;margin-bottom:12px">{ic}</div>' if ic else ''
        return (f'<div class="dim"></div><div style="position:absolute;left:24px;right:24px;top:200px;background:#f3f3f8;border-radius:28px;padding:24px 22px 14px">'
                f'{icn}<div style="font-size:20px;font-weight:700;text-align:center">{title}</div>'
                f'<div style="font-size:14.5px;line-height:22px;color:#444;margin-top:12px">{body}</div>'
                f'<div style="display:flex;justify-content:flex-end;gap:6px;margin-top:16px;font-size:15px">{bs}</div></div>')

    out = {}
    out['android-1-download'] = page(release.replace('{N1}', '1') +
        '<div style="position:absolute;left:12px;right:12px;bottom:24px;background:#2d2f31;color:#fff;border-radius:14px;padding:16px;font-size:14px">'
        f'{t["dlAsk"]}<div style="display:flex;justify-content:flex-end;gap:18px;margin-top:12px;font-weight:700;color:#a8c7fa"><span>{t["cancel"]}</span><span data-hl="2">{t["download"]}</span></div></div>')
    rel = release.replace(' data-hl="{N1}"', '')
    out['android-2-blocked'] = page(rel + dialog(t['blockedT'], t['blockedB'], [t['cancel'], t['settings']], '<span style="font-size:32px">⚠️</span>', 3))
    out['android-3-allow'] = page(A +
        f'<div style="height:56px;display:flex;align-items:center;gap:14px;padding:0 16px;font-size:20px"><span data-hl="5" style="padding:4px">←</span><span style="font-weight:600">{t["unknownT"]}</span></div>'
        '<div style="padding:8px 16px 20px;display:flex;gap:14px;align-items:center"><div style="width:48px;height:48px;border-radius:50%;background:conic-gradient(#ea4335 0 33%,#fbbc04 0 66%,#34a853 0);display:flex;align-items:center;justify-content:center"><div style="width:20px;height:20px;border-radius:50%;background:#4285f4;border:3px solid #fff"></div></div>'
        f'<div><div style="font-size:18px;font-weight:700">Chrome</div><div style="font-size:13px;color:#666">{t["browser"]}</div></div></div>'
        '<div data-hl="4" style="margin:0 12px;background:#f3f3f8;border-radius:20px;padding:18px;display:flex;align-items:center;justify-content:space-between;gap:12px">'
        f'<span style="font-size:17px">{t["allow"]}</span>'
        '<span style="flex:none;width:52px;height:30px;border-radius:15px;background:#0b57d0;position:relative"><span style="position:absolute;right:3px;top:3px;width:24px;height:24px;border-radius:50%;background:#fff"></span></span></div>'
        f'<div style="padding:18px 20px;font-size:13px;line-height:20px;color:#666">{t["allowWarn"]}</div>')
    out['android-4-install'] = page(rel + dialog(name, f'<div style="text-align:center">{t["installQ"]}</div>', [t['cancel'], t['install']], icon, 6))
    out['android-5-protect'] = page(rel + dialog(t['ppT'], t['ppB'] +
        f'<div style="margin-top:14px;padding:12px;border-radius:12px;background:#e8e8f0;font-weight:700;color:#0b57d0">▾ {t["more"]}<div data-hl="7" style="margin-top:10px;color:#1b1b1f">{t["anyway"]}</div></div>',
        [t['ok']], '<span style="font-size:32px">🛡️</span>'))
    out['android-6-done'] = page(rel + dialog(name, f'<div style="text-align:center">{t["installed"]}</div>', [t['done'], t['open']], icon, 8))

    I = '<div class="status" style="height:44px;font-size:15px"><span>10:20</span><span>📶 🔋</span></div>'
    share_icon = '<svg width="26" height="30" viewBox="0 0 26 30"><path d="M13 2v17M7 8l6-6 6 6" fill="none" stroke="#0a84ff" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/><path d="M8 12H4v16h18V12h-4" fill="none" stroke="#0a84ff" stroke-width="2.4" stroke-linejoin="round"/></svg>'
    bottom = ('<div style="position:absolute;left:0;right:0;bottom:0;height:118px;background:rgba(245,245,247,.97);border-top:1px solid #ddd">'
              '<div data-hl="1" style="margin:10px 14px;height:44px;border-radius:12px;background:#e6e6ea;display:flex;align-items:center;justify-content:center;font-size:15px;color:#333">AA&nbsp;&nbsp;&nbsp;🔒 wanopro.github.io&nbsp;&nbsp;&nbsp;↻</div>'
              f'<div style="display:flex;justify-content:space-around;align-items:center;font-size:24px;color:#0a84ff;padding:2px 8px"><span>‹</span><span style="opacity:.4">›</span><span data-hl="2" style="padding:2px 6px">{share_icon}</span><span>📖</span><span>⧉</span></div></div>')
    view = f'<div style="position:absolute;left:0;right:0;top:44px;bottom:118px;overflow:hidden;background:#12121A"><img src="data:image/png;base64,{home_b64}" style="width:100%;display:block"></div>'
    out['ios-1-safari'] = page(I + view + bottom, bg='#f5f5f7')
    icons = ['⧉', '👓', '📖', '☆', '⊞', '🔍']
    rows = ''.join(f'<div {"data-hl=" + chr(34) + "3" + chr(34) if k == 4 else ""} style="display:flex;justify-content:space-between;align-items:center;gap:10px;padding:14px 16px;border-bottom:1px solid #e3e3e8;font-size:16px"><span>{txt}</span><span style="font-size:20px">{icons[k]}</span></div>' for k, txt in enumerate(t['share']))
    out['ios-2-share'] = page(I + view + '<div class="dim"></div>'
        '<div style="position:absolute;left:0;right:0;bottom:0;top:250px;background:#f2f2f7;border-radius:14px 14px 0 0;padding:14px 12px">'
        f'<div style="display:flex;gap:10px;align-items:center;padding:4px 6px 14px"><img src="data:image/png;base64,{icon_b64}" style="width:44px;height:44px;border-radius:10px"><div><div style="font-weight:700;font-size:15px">{name}</div><div style="font-size:12px;color:#888">wanopro.github.io</div></div></div>'
        f'<div style="background:#fff;border-radius:12px;overflow:hidden">{rows}</div></div>', bg='#f5f5f7')
    out['ios-3-add'] = page(I +
        f'<div style="display:flex;justify-content:space-between;align-items:center;padding:10px 16px;font-size:17px;gap:8px"><span style="color:#0a84ff">{t["cancel"]}</span><b style="text-align:center">{t["addT"]}</b><span data-hl="4" style="color:#0a84ff;font-weight:700">{t["add"]}</span></div>'
        '<div style="margin:14px 16px;background:#fff;border-radius:12px;padding:14px;display:flex;gap:14px;align-items:center">'
        f'<img src="data:image/png;base64,{icon_b64}" style="width:60px;height:60px;border-radius:14px">'
        f'<div style="flex:1;min-width:0"><div style="font-size:17px;padding:8px 0;border-bottom:1px solid #e3e3e8">{name}</div><div style="font-size:13px;color:#888;padding-top:8px;word-break:break-all">https://wanopro.github.io/wolfgames/</div></div></div>'
        f'<div style="margin:0 28px;font-size:13px;color:#888;line-height:19px">{t["addDesc"]}</div>', bg='#f2f2f7')
    cols = ['#ff9f0a', '#8e8e93', '#30d158', '#8e8e93', '#ffd60a', '#0a84ff', '#1c1c1e', '#32d74b', '#ff375f', '#ff453a', '#636366']
    apps = ''.join(f'<div style="display:flex;flex-direction:column;align-items:center;gap:6px;font-size:11px;color:#fff;text-align:center"><div style="width:60px;height:60px;border-radius:15px;background:{c}"></div>{l}</div>' for l, c in zip(t['apps'], cols))
    apps += f'<div data-hl="5" style="display:flex;flex-direction:column;align-items:center;gap:6px;font-size:11px;color:#fff;text-align:center"><img src="data:image/png;base64,{icon_b64}" style="width:60px;height:60px;border-radius:15px">{name}</div>'
    out['ios-4-home'] = page('<div style="position:absolute;inset:0;background:linear-gradient(160deg,#2b3a67,#6b4c8a 60%,#c06c84)"></div>'
        + I.replace('<div class="status"', '<div class="status" style="color:#fff;position:relative"')
        + f'<div style="position:relative;display:grid;grid-template-columns:repeat(4,1fr);row-gap:22px;padding:24px 18px">{apps}</div>', color='#fff')
    return out


with sync_playwright() as p:
    b = p.chromium.launch()
    for lang in PAGE:
        d = f'{WEB}/manual/{lang}'
        os.makedirs(d, exist_ok=True)
        # 해당 언어의 실제 웹 메인 화면
        ctx = b.new_context(viewport={'width': 360, 'height': 640}, device_scale_factor=2, is_mobile=True, has_touch=True)
        pg = ctx.new_page()
        pg.goto(SERVER)
        pg.evaluate(f"localStorage.setItem('wg-lang','{lang}')")
        pg.reload(); pg.wait_for_timeout(600)
        home = pg.screenshot()
        pg.set_viewport_size({'width': 393, 'height': 852}); pg.wait_for_timeout(300)
        Image.open(io.BytesIO(pg.screenshot())).convert('RGB').save(f'{d}/web-home.jpg', quality=84, optimize=True)
        ctx.close()
        home_b64 = base64.b64encode(home).decode()
        sp = b.new_page(viewport={'width': W, 'height': H}, device_scale_factor=2)
        for name, html in shots(lang, home_b64).items():
            sp.set_content(html); sp.wait_for_timeout(200)
            Image.open(io.BytesIO(sp.screenshot())).convert('RGB').save(f'{d}/{name}.jpg', quality=84, optimize=True)
        sp.close()
        print('images', lang)
    b.close()
