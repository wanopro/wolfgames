"""설치 안내: 옛날 폰(갤럭시 노트8 등)에서 Play 프로텍트 때문에 설치가 안 될 때 — 앱 검사 일시중지 방법 (6개 언어). 한 번만 실행"""
import sys, pprint
sys.path.insert(0, 'tools')
import manual_text as m
TIP = {
 'ko': ('<b>갤럭시 노트8처럼 오래된 폰</b>에서 "무시하고 설치"가 없거나 계속 막히면: <b>Play 스토어</b> → 오른쪽 위 <b>프로필</b> → <span class="k">Play 프로텍트</span> → 오른쪽 위 <b>⚙️ 톱니</b> → <span class="k">Play 프로텍트로 앱 검사</span> → <span class="k">일시중지</span> 후 다시 설치하세요. 설치가 끝나면 다시 켜도 돼요.',
        ['옛날 폰(갤럭시 노트8 등)에서 설치가 계속 막혀요', '<b>Play 프로텍트 앱 검사를 잠깐 꺼야 해요.</b><br>1. <b>Google Play 스토어</b> 열기<br>2. 오른쪽 위 <b>프로필</b> 누르기<br>3. <b>Play 프로텍트</b><br>4. 오른쪽 위 <b>⚙️ 톱니</b><br>5. <b>Play 프로텍트로 앱 검사</b><br>6. <b>일시중지</b> → APK를 다시 열어 설치<br>설치가 끝나면 같은 곳에서 다시 켜도 앱은 그대로 쓸 수 있어요.']),
 'en': ('On <b>older phones like the Galaxy Note8</b>, if there\'s no "Install anyway" or it keeps getting blocked: <b>Play Store</b> → your <b>profile</b> (top right) → <span class="k">Play Protect</span> → <b>⚙️ gear</b> (top right) → <span class="k">Scan apps with Play Protect</span> → <span class="k">Pause</span>, then install again. You can turn it back on afterwards.',
        ["It keeps getting blocked on an older phone (Galaxy Note8, etc.)", '<b>Pause the Play Protect app scan for a moment.</b><br>1. Open the <b>Google Play Store</b><br>2. Tap your <b>profile</b> (top right)<br>3. <b>Play Protect</b><br>4. <b>⚙️ gear</b> (top right)<br>5. <b>Scan apps with Play Protect</b><br>6. <b>Pause</b> → open the APK again and install<br>After installing you can turn it back on — the app keeps working.']),
 'ja': ('<b>Galaxy Note8 など古いスマホ</b>で「このままインストール」が出ない・何度もブロックされる場合：<b>Play ストア</b> → 右上の<b>プロフィール</b> → <span class="k">Play プロテクト</span> → 右上の<b>⚙️ 歯車</b> → <span class="k">Play プロテクトでアプリをスキャン</span> → <span class="k">一時停止</span> してからもう一度インストール。インストール後は元に戻してOK。',
        ['古いスマホ（Galaxy Note8 など）でインストールがブロックされる', '<b>Play プロテクトのアプリスキャンを一時的に止めます。</b><br>1. <b>Google Play ストア</b>を開く<br>2. 右上の<b>プロフィール</b><br>3. <b>Play プロテクト</b><br>4. 右上の<b>⚙️ 歯車</b><br>5. <b>Play プロテクトでアプリをスキャン</b><br>6. <b>一時停止</b> → APK をもう一度開いてインストール<br>インストール後に元に戻してもアプリはそのまま使えます。']),
 'zh': ('在 <b>Galaxy Note8 等旧手机</b>上，如果没有“仍然安装”或一直被拦截：<b>Play 商店</b> → 右上角<b>头像</b> → <span class="k">Play 保护机制</span> → 右上角<b>⚙️ 齿轮</b> → <span class="k">使用 Play 保护机制扫描应用</span> → <span class="k">暂停</span>，然后重新安装。装好后可以再打开。',
        ['旧手机（Galaxy Note8 等）一直安装不上', '<b>需要暂时关闭 Play 保护机制的应用扫描。</b><br>1. 打开 <b>Google Play 商店</b><br>2. 点右上角<b>头像</b><br>3. <b>Play 保护机制</b><br>4. 右上角<b>⚙️ 齿轮</b><br>5. <b>使用 Play 保护机制扫描应用</b><br>6. <b>暂停</b> → 重新打开 APK 安装<br>装好后在同一处再打开也没关系，应用照常能用。']),
 'th': ('ใน<b>มือถือรุ่นเก่าอย่าง Galaxy Note8</b> ถ้าไม่มี "ติดตั้งต่อไป" หรือโดนบล็อกตลอด: <b>Play Store</b> → <b>โปรไฟล์</b> มุมขวาบน → <span class="k">Play Protect</span> → <b>⚙️ ฟันเฟือง</b> มุมขวาบน → <span class="k">สแกนแอปด้วย Play Protect</span> → <span class="k">หยุดชั่วคราว</span> แล้วติดตั้งใหม่ ติดตั้งเสร็จแล้วเปิดกลับได้',
        ['มือถือรุ่นเก่า (Galaxy Note8 ฯลฯ) ติดตั้งไม่ได้สักที', '<b>ต้องปิดการสแกนแอปของ Play Protect ชั่วคราว</b><br>1. เปิด <b>Google Play Store</b><br>2. แตะ<b>โปรไฟล์</b>มุมขวาบน<br>3. <b>Play Protect</b><br>4. <b>⚙️ ฟันเฟือง</b>มุมขวาบน<br>5. <b>สแกนแอปด้วย Play Protect</b><br>6. <b>หยุดชั่วคราว</b> → เปิด APK อีกครั้งแล้วติดตั้ง<br>ติดตั้งเสร็จแล้วเปิดกลับได้ แอปยังใช้ได้ตามปกติ']),
 'vi': ('Trên <b>máy cũ như Galaxy Note8</b>, nếu không có "Vẫn cài đặt" hoặc cứ bị chặn: <b>Play Store</b> → <b>ảnh hồ sơ</b> góc trên bên phải → <span class="k">Play Protect</span> → <b>⚙️ bánh răng</b> góc trên bên phải → <span class="k">Quét ứng dụng bằng Play Protect</span> → <span class="k">Tạm dừng</span>, rồi cài lại. Cài xong có thể bật lại.',
        ['Máy cũ (Galaxy Note8…) cứ bị chặn không cài được', '<b>Cần tạm tắt tính năng quét ứng dụng của Play Protect.</b><br>1. Mở <b>Google Play Store</b><br>2. Chạm <b>ảnh hồ sơ</b> góc trên bên phải<br>3. <b>Play Protect</b><br>4. <b>⚙️ bánh răng</b> góc trên bên phải<br>5. <b>Quét ứng dụng bằng Play Protect</b><br>6. <b>Tạm dừng</b> → mở lại APK và cài<br>Cài xong bật lại cũng được, ứng dụng vẫn dùng bình thường.']),
}
for L, (tip, faq) in TIP.items():
    P = m.PAGE[L]
    st = list(P['a'][4]); bullets = list(st[2])
    if not any('Note8' in b for b in bullets): bullets.append(tip)
    st[2] = bullets; P['a'][4] = st
    f = list(P['faq'])
    if not any('Note8' in q[0] for q in f): f.insert(1, faq)
    P['faq'] = f
head = open('tools/manual_text.py', encoding='utf-8').read().split('\nPAGE = {')[0]
pp = lambda o: pprint.pformat(o, width=200, sort_dicts=False)
out = head + '\nPAGE = ' + pp(m.PAGE) + '\n\n# 단계 그림 속 폰 화면 문구\nSHOT = ' + pp(m.SHOT) + '\n\n# 게임 방법: (그림 이름, 제목으로 쓸 앱 문구 키, 설명 3줄)\nHOW_KEYS = ' + repr(m.HOW_KEYS) + '\nHOW_EMOJI = ' + repr(m.HOW_EMOJI) + '\n\nHOW = ' + pp(m.HOW) + '\n'
open('tools/manual_text.py', 'w', encoding='utf-8').write(out)
print('ok')
