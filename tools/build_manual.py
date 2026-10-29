import re
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
from manual_text import HOW, HOW_EMOJI, HOW_KEYS, PAGE, SHOT  # noqa: E402

WEB = 'web'
WEB_VERSION = re.search(r"VERSION = '([^']+)'", open(f'{WEB}/core.js', encoding='utf-8').read()).group(1)
SERVER = 'http://localhost:8765/'
APPNAME = {'ko': '울프 게임즈', 'en': 'Wolf Games', 'ja': 'ウルフゲームズ', 'zh': '沃夫游戏', 'th': 'Wolf Games', 'vi': 'Wolf Games'}

# ---------- 1) 문구 파일 ----------
with open(f'{WEB}/manual-i18n.js', 'w', encoding='utf-8') as f:
    f.write('// 자동 생성: tools/build_manual.py (문구 원본은 tools/manual_text.py)\n')
    out = {lg: {**PAGE[lg], **HOW[lg], 'howKeys': HOW_KEYS, 'howEmoji': HOW_EMOJI} for lg in PAGE}
    f.write('export const MANUAL = ' + json.dumps(out, ensure_ascii=False, indent=1) + ';\n')
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
               f'<div style="font-size:22px;font-weight:800">{name} v1.2</div>'
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
        pg.evaluate(f"localStorage.setItem('wg-lang','{lang}'); localStorage.setItem('wg-seen','{WEB_VERSION}')")
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


# ---------- 3) 게임 방법용 실제 게임 화면 (언어별) ----------
import math  # noqa: E402
import random  # noqa: E402

FAKE_RATES = {"result": "success", "time_last_update_unix": 1792540801, "time_next_update_unix": 1792627201,
              "rates": {"USD": 1, "KRW": 1385.5, "JPY": 149.2, "CNY": 7.12, "THB": 32.9, "VND": 25360, "EUR": 0.92, "GBP": 0.77, "HKD": 7.78,
                        "TWD": 31.9, "SGD": 1.29, "PHP": 56.8, "MYR": 4.21, "IDR": 15620, "AUD": 1.49, "CAD": 1.37, "CHF": 0.86, "NZD": 1.64,
                        "INR": 84.1, "MNT": 3390, "LAK": 21900, "KHR": 4060, "RUB": 96.5, "AED": 3.67}}


def game_shots(b, lang):
    d = f'{WEB}/manual/{lang}'
    ctx = b.new_context(viewport={'width': 393, 'height': 852}, device_scale_factor=2, is_mobile=True, has_touch=True)
    ctx.route('https://open.er-api.com/**', lambda r: r.fulfill(status=200, content_type='application/json', body=json.dumps(FAKE_RATES), headers={'Access-Control-Allow-Origin': '*'}))
    pg = ctx.new_page()
    rnd = random.Random(7)

    def home():
        pg.evaluate("history.replaceState({s:'home'},'')"); pg.goto(SERVER); pg.wait_for_timeout(350)

    def shot(n):
        Image.open(io.BytesIO(pg.screenshot())).convert('RGB').save(f'{d}/{n}.jpg', quality=80, optimize=True)

    def card(i):
        pg.locator('.card').nth(i).click(); pg.wait_for_timeout(200)

    def start():
        pg.locator('.scroll > .btn').last.click(); pg.wait_for_timeout(200)

    pg.goto(SERVER); pg.evaluate(f"localStorage.clear(); localStorage.setItem('wg-lang','{lang}'); localStorage.setItem('wg-seen','{WEB_VERSION}')"); home()
    # 주사위 굴리기: 손가락 3개 중 2개 완료
    card(0); card(0)
    for pid, (x, y) in [(1, (110, 230)), (2, (280, 330))]:
        pg.dispatch_event('canvas', 'pointerdown', {'pointerId': pid, 'clientX': x, 'clientY': y, 'bubbles': True})
    pg.wait_for_timeout(2150)
    for pid, (x, y) in [(1, (110, 230)), (2, (280, 330))]:
        pg.dispatch_event('canvas', 'pointerup', {'pointerId': pid, 'clientX': x, 'clientY': y, 'bubbles': True})
    pg.dispatch_event('canvas', 'pointerdown', {'pointerId': 3, 'clientX': 190, 'clientY': 560, 'bubbles': True}); pg.wait_for_timeout(1200)
    shot('how-roll')
    pg.dispatch_event('canvas', 'pointerup', {'pointerId': 3, 'clientX': 190, 'clientY': 560, 'bubbles': True})
    # 숫자 뒤집기
    pg.go_back(); pg.wait_for_timeout(250); card(1); start()
    for _ in range(3):
        pg.locator('.body.pad .btn').last.click(); pg.wait_for_timeout(1000)
        for _k in range(2):
            on = pg.query_selector_all('.tile.on')
            if on: on[-1].click(); pg.wait_for_timeout(120)
        nb = pg.query_selector_all('.body.pad .btn')
        if nb and pg.query_selector('.msg.win'): nb[-1].click()
    pg.locator('.body.pad .btn').last.click(); pg.wait_for_timeout(1100); shot('how-shut')
    # 5개 주사위 (3명)
    home(); card(0); card(2); pg.click('.stepper button:last-of-type'); start()
    pg.locator('.area').nth(1).click(); pg.wait_for_timeout(1000); shot('how-five')
    # 789 (4명): 두 번 굴린 뒤
    home(); card(0); card(3); start()
    for _ in range(2):
        pg.locator('.g7 > div').nth(1).click(); pg.wait_for_timeout(1500)
    shot('how-789')
    # Up/Down (누적 켬): 한 번 맞혀본 결과
    home(); card(1); card(0); pg.locator('.stepper.toggle').click(); start()
    pg.locator('.ud .row .btn').first.click(); pg.wait_for_timeout(900); shot('how-updown')
    # 호치민 (3명): 카드 한 장 바꾼 뒤
    home(); card(1); card(1); start(); pg.wait_for_timeout(300)
    pg.locator('.mid2 .pcard').nth(0).click(); pg.wait_for_timeout(900); shot('how-hcm')
    # 글로벌 킹스컵: 카드 두 장 뽑은 뒤
    home(); card(1); card(2)
    for _ in range(2):
        pg.mouse.click(196, 420); pg.wait_for_timeout(900)
    shot('how-kings')
    # 손가락선택 (걸릴 인원 2명, 5명 터치)
    home(); card(2); card(0); pg.click('.stepper button:last-of-type'); start(); pg.wait_for_timeout(300)
    pts = [(1, 90, 230), (2, 290, 200), (3, 200, 420), (4, 100, 620), (5, 300, 650)]
    for i, x, y in pts:
        pg.dispatch_event('canvas', 'pointerdown', {'pointerId': i, 'clientX': x, 'clientY': y, 'bubbles': True}); pg.wait_for_timeout(60)
    for i, x, y in pts:
        pg.dispatch_event('canvas', 'pointerup', {'pointerId': i, 'clientX': x, 'clientY': y, 'bubbles': True})
    pg.wait_for_timeout(1700); shot('how-finger')
    def area_tap(fx, fy, pid=50):
        bb = pg.query_selector('canvas').bounding_box()
        pg.dispatch_event('canvas', 'pointerdown', {'pointerId': pid, 'clientX': bb['x'] + bb['width'] * fx, 'clientY': bb['y'] + bb['height'] * fy, 'bubbles': True})
    # 주사위 대결 (3명): 모두 굴린 뒤
    home(); card(0); card(4); pg.locator('.stepper').first.locator('button').last.click(); start(); pg.wait_for_timeout(300)
    for k, (fx, fy) in enumerate([(0.5, 0.85), (0.2, 0.2), (0.8, 0.2)]):
        area_tap(fx, fy, 60 + k); pg.wait_for_timeout(120)
    pg.wait_for_timeout(1100); shot('how-duel')
    # 반응속도 대결 (3명): 초록 → 모두 터치
    home(); card(3); card(0); pg.locator('.stepper').first.locator('button').last.click(); start()
    # 화면이 초록색으로 바뀔 때까지 기다림 (P1 영역 색 확인)
    for _ in range(80):
        g = pg.evaluate("(() => { const c = document.querySelector('canvas'); const d = c.getContext('2d').getImageData(c.width / 2, c.height * 0.95, 1, 1).data; return d[1] > 150 && d[0] < 120; })()")
        if g: break
        pg.wait_for_timeout(50)
    pg.wait_for_timeout(180)
    for k, (fx, fy) in enumerate([(0.5, 0.85), (0.8, 0.2), (0.2, 0.2)]):
        area_tap(fx, fy, 70 + k); pg.wait_for_timeout(90 + k * 60)
    pg.wait_for_timeout(400); shot('how-react')
    # 색 반응 대결 (2명): 색이 바뀌는 중
    home(); card(3); card(1); start(); pg.wait_for_timeout(1800); shot('how-color')
    # 스탑워치 (2명, 7초): 결과
    home(); card(3); card(2); start(); pg.wait_for_timeout(3000 + 6880)
    area_tap(0.5, 0.85, 80); pg.wait_for_timeout(430); area_tap(0.5, 0.15, 81); pg.wait_for_timeout(500); shot('how-stop')
    # 맥주 거품 채우기 (2명): 둘 다 멈춘 뒤
    home(); card(3); card(3); pg.evaluate("Math.__r = Math.random; Math.random = () => 0.5"); start(); pg.wait_for_timeout(4300)
    area_tap(0.5, 0.85, 82); pg.wait_for_timeout(350); area_tap(0.5, 0.15, 83); pg.wait_for_timeout(900); shot('how-beer')
    pg.evaluate("Math.random = Math.__r")
    # 폭탄돌리기 (4명): 심지 타는 중
    home(); card(5); card(2); start(); pg.wait_for_timeout(1500); shot('how-bomb')
    # 동전 던지기 (4명): 결과 (앞면으로 고정)
    home(); card(5); card(3); start(); pg.wait_for_timeout(300)
    pg.evaluate("Math.__r = Math.random; Math.random = () => 0.3")
    for k, (fx, fy) in enumerate([(0.42, 0.86), (0.1, 0.42), (0.58, 0.14), (0.9, 0.58)]):
        area_tap(fx, fy, 84 + k); pg.wait_for_timeout(150)
    pg.evaluate("Math.random = Math.__r")
    pg.wait_for_timeout(3000 + 2400 + 900); shot('how-coin')
    # 늑대이빨 (당첨 2): 화면 가득 벌린 입에서 이빨 3개 누른 모습 (당첨은 0·1번으로 고정)
    home(); card(4); card(0); pg.click('.stepper button:last-of-type')
    pg.evaluate("Math.__r = Math.random; Math.random = () => 0.999"); start(); pg.evaluate("Math.random = Math.__r"); pg.wait_for_timeout(300)
    pts = pg.evaluate("document.querySelector('canvas').teethAt()"); bb = pg.query_selector('canvas').bounding_box()
    for k in [5, 9, 14]:
        pg.mouse.click(bb['x'] + pts[k][0], bb['y'] + pts[k][1]); pg.wait_for_timeout(150)
    pg.wait_for_timeout(500); shot('how-teeth')
    # 사다리 (5명)
    home(); card(5); card(0); pg.locator('.stepper').first.locator('button').last.click(); start()
    pg.locator('.lnums button').nth(0).click(); pg.wait_for_timeout(5300)
    pg.locator('.lnums button').nth(3).click(); pg.wait_for_timeout(2600); shot('how-ladder')
    # 돌림판
    home(); card(5); card(1); start(); pg.locator('.row .btn').first.click(); pg.wait_for_timeout(5400); shot('how-roulette')
    # 늑대달리기 (5명)
    home(); card(4); card(1); pg.locator('.stepper').first.locator('button').last.click(); start(); pg.locator('.btn').first.click(); pg.wait_for_timeout(2000 + 7800); shot('how-race')
    # 늑대 룰렛
    home(); card(4); card(2)
    bb = pg.query_selector('canvas').bounding_box(); w, h = bb['width'], bb['height']; H = w / 0.9; oy = h - H
    for i in rnd.sample(range(20), 20):
        r, c = i // 5, i % 5; bl = 1 + 0.06 * math.sin(math.pi * (r + .5) / 4)
        pg.mouse.click(bb['x'] + w * .5 + (c - 2) * w * .135 * bl, bb['y'] + oy + H * (.47 + r * .125)); pg.wait_for_timeout(40)
        if pg.query_selector('.h-title.win'): break
    pg.wait_for_timeout(1000); shot('how-pirate')
    # 늑대 돌리기 (도는 중)
    home(); card(4); card(3); pg.locator('.btn').last.click(); pg.wait_for_timeout(1500); shot('how-arrow')
    # 4목
    home(); card(6); card(0)
    bb = pg.query_selector('canvas').bounding_box(); w, h = bb['width'], bb['height']; cell = min(w / 7, h / 7.15); left = (w - cell * 7) / 2
    for c in [3, 3, 4, 2, 2, 4, 5]:
        pg.mouse.click(bb['x'] + left + (c + .5) * cell, bb['y'] + h * .5); pg.wait_for_timeout(750)
    shot('how-four')
    # 탭 줄다리기: 아래(P1) 승리
    home(); card(6); card(1); start(); pg.wait_for_timeout(1800)
    for k in range(34):
        area_tap(0.3 + (k % 5) * 0.1, 0.8, 100 + k); pg.wait_for_timeout(25)
        if k % 4 == 0: area_tap(0.6, 0.2, 200 + k); pg.wait_for_timeout(25)
    pg.wait_for_timeout(1100); shot('how-tug')
    # 슬랩 대결: 아래(P1) 공격, 위(P2) 수비 → 명중 순간
    home(); card(6); card(2); pg.evaluate("Math.__r = Math.random; Math.random = () => 0.1"); start(); pg.evaluate("Math.random = Math.__r"); pg.wait_for_timeout(400)
    area_tap(0.5, 0.25, 300); pg.wait_for_timeout(500); area_tap(0.5, 0.75, 301); pg.wait_for_timeout(330); shot('how-slap')
    # 계산기 → N빵 → 환전
    home(); pg.locator('.hero .chip').nth(1).click(); pg.wait_for_timeout(200)
    for k in ['1', '8', '7', '5', '0', '0', '=']:
        pg.click(f'.body.pad > div:last-child button:text-is("{k}")')
    pg.locator('.row .btn').nth(0).click(); pg.wait_for_timeout(150); pg.locator('.overlay .stepper button').last.click(); pg.wait_for_timeout(200)
    shot('how-split')
    pg.locator('.overlay .btn').last.click(); pg.locator('.row .btn').nth(1).click(); pg.wait_for_timeout(250)
    pg.locator('.overlay button:text-is("15%")').click(); pg.wait_for_timeout(200); shot('how-tip')
    pg.locator('.overlay .btn').last.click(); pg.locator('.row .btn').nth(2).click(); pg.wait_for_timeout(500)
    shot('how-fx')
    # 연락처 QR (예시 아이디)
    pg.evaluate("""localStorage.setItem('wg-qr', JSON.stringify({instagram: {v: '@wolf.games', on: true}, whatsapp: {v: '+82 10-1234-5678', on: true}, tiktok: {v: 'wolfgames', on: true}}))""")
    home(); pg.locator('.hero .chip').nth(2).click(); pg.wait_for_timeout(600); shot('how-qr')
    pg.evaluate("localStorage.removeItem('wg-qr')")
    # 숙소 주소 카드 (예시 숙소)
    pg.evaluate("""localStorage.setItem('wg-hotels', JSON.stringify([{name: 'Hotel Nikko Saigon', addr: '235 Nguyễn Văn Cừ, Phường 4, Quận 5, Hồ Chí Minh', phone: '+84 28 3925 7777', note: ''}, {name: 'Sukhumvit 11 Hostel', addr: 'Soi Sukhumvit 11, Khlong Toei Nuea, Bangkok', phone: '', note: ''}]))""")
    home(); pg.locator('.hero .chip').nth(3).click(); pg.wait_for_timeout(600); shot('how-hotel')
    pg.evaluate("localStorage.removeItem('wg-hotels')")
    # 플링코: 위쪽 터치 → 칸에 들어간 결과 (길은 고정)
    home(); card(5); card(4); start(); pg.wait_for_timeout(500)
    pg.evaluate("Math.__r = Math.random; Math.random = () => 0.37")
    bb = pg.query_selector('canvas').bounding_box(); pg.mouse.click(bb['x'] + bb['width'] * 0.42, bb['y'] + 40)
    pg.evaluate("Math.random = Math.__r")
    for _ in range(140):
        if pg.evaluate("window.__plinkoPhase") == 'result': break
        pg.wait_for_timeout(50)
    pg.wait_for_timeout(500); shot('how-plinko')
    # 삼목: 초록 0·8·6, 빨강 4·2·3 → 초록 차례 (0번 코인이 깜빡임)
    home(); card(6); card(3); start(); pg.wait_for_timeout(1400)
    bb = pg.query_selector('canvas').bounding_box(); W, H = bb['width'], bb['height']; sz = min(W - 40, H - 190, 420); x0 = bb['x'] + (W - sz) / 2; y0 = bb['y'] + (H - sz) / 2; cl = sz / 3
    for k in [0, 4, 8, 2, 6, 3]:
        pg.mouse.click(x0 + (k % 3 + .5) * cl, y0 + (k // 3 + .5) * cl); pg.wait_for_timeout(180)
    pg.wait_for_timeout(250); shot('how-ttt')
    # 오목: 몇 수 둔 뒤
    home(); card(6); card(4); start(); pg.wait_for_timeout(400)
    bb = pg.query_selector('canvas').bounding_box(); W, H = bb['width'], bb['height']; sz = min(W - 16, H - 130, 640); x0 = bb['x'] + (W - sz) / 2; y0 = bb['y'] + (H - sz) / 2; pad = sz / 15.2 * 0.6; st = (sz - pad * 2) / 14
    for gx, gy in [(7, 7), (8, 8), (8, 7), (6, 7), (7, 6), (9, 9), (6, 8), (7, 9), (9, 6)]:
        pg.mouse.click(x0 + pad + gx * st, y0 + pad + gy * st); pg.wait_for_timeout(120)
    pg.wait_for_timeout(300); shot('how-gomoku')
    # 두 언어 같이 보기: 보조 언어(영어, 영어면 한국어)로 삼목 화면
    pg.evaluate(f"localStorage.setItem('wg-lang2', '{'ko' if lang == 'en' else 'en'}')")
    home(); card(6); card(3); start(); pg.wait_for_timeout(1400)
    bb = pg.query_selector('canvas').bounding_box(); W, H = bb['width'], bb['height']; sz = min(W - 40, H - 190, 420); x0 = bb['x'] + (W - sz) / 2; y0 = bb['y'] + (H - sz) / 2; cl = sz / 3
    for k in [4, 0]:
        pg.mouse.click(x0 + (k % 3 + .5) * cl, y0 + (k // 3 + .5) * cl); pg.wait_for_timeout(180)
    pg.wait_for_timeout(250); shot('how-dual')
    pg.evaluate("localStorage.removeItem('wg-lang2')")
    ctx.close()


with sync_playwright() as p:
    b = p.chromium.launch()
    for lang in PAGE:
        game_shots(b, lang)
        print('game shots', lang)
    b.close()
