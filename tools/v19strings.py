# v1.9 새 문구 (ybDesc 는 2026-11-07 꽝 늑대 여러 마리로 바뀜 — 기존 줄을 지우고 다시 넣음) (ko, en, ja, zh, th, vi) — 🐺 늑대 야바위 · 📤 앱 공유하기
# 사용: python3 tools/v19strings.py → python3 tools/gen_web_i18n.py → python3 tools/gen_dual.py
S = {
 # 🐺 늑대 야바위 (🍀 운빨)
 'ybTitle': ['늑대 야바위', 'Wolf Shell Game', 'オオカミ当て', '狼猜猜猜', 'หมาป่าซ่อนเหรียญ', 'Đoán sói'],
 'ybSub': ['코인 못 먹은 늑대를 고르면 원샷!', 'Pick the wolf with no coin — drink!', 'コインを食べてないオオカミを選んだら一気！', '选到没吃硬币的狼就干杯！', 'เลือกหมาป่าที่ไม่ได้กินเหรียญ ดื่ม!', 'Chọn trúng con sói không có xu là uống!'],
 'ybDesc': ['늑대들이 입을 벌리고 코인을 하나씩 먹어요. 정한 수만큼은 못 먹어요 (꽝 늑대).\n늑대들이 이리저리 섞이면 잘 따라가 봐요 👀 (점점 빨라져요)\n한 사람씩 늑대를 하나 골라요 👆 코인이 나오면 통과, 빈 입이면 🍺 원샷! 꽝 늑대를 다 찾으면 끝.',
            'The wolves open wide and eat coins — except the empty ones (you choose how many).\nThen they shuffle around. Try to keep track 👀 (it gets faster)\nOne at a time, pick a wolf 👆 Coin = safe. Empty mouth = 🍺 bottoms up! Ends when every empty wolf is found.',
            'オオカミたちが口を開けてコインを食べます。決めた数だけ食べられません（ハズレオオカミ）。\nオオカミがぐるぐる入れ替わるのをよく見て 👀（だんだん速く）\n1人ずつオオカミを1匹選ぶ 👆 コインが出たらセーフ、空っぽなら🍺一気！ハズレを全部見つけたら終わり。',
            '狼们张大嘴吃硬币，按设定的数量有几只吃不到（空嘴狼）。\n然后狼会来回交换位置，好好盯着 👀（越来越快）\n每人轮流选一只狼 👆 有硬币就过关，空嘴的 🍺 干杯！空嘴狼全部找到就结束。',
            'หมาป่าอ้าปากกินเหรียญ มีบางตัวที่อดตามจำนวนที่ตั้งไว้ (หมาป่าปากว่าง)\nแล้วหมาป่าจะสลับที่ไปมา ตามดูให้ดี 👀 (เร็วขึ้นเรื่อยๆ)\nผลัดกันเลือกหมาป่าคนละตัว 👆 มีเหรียญ = รอด ปากว่าง = 🍺 หมดแก้ว! เจอปากว่างครบก็จบ',
            'Những con sói há miệng ăn xu — trừ số con miệng trống bạn đã chọn.\nRồi chúng đổi chỗ qua lại, nhìn theo kỹ nhé 👀 (càng lúc càng nhanh)\nLần lượt mỗi người chọn một con 👆 Có xu = thoát, miệng trống = 🍺 cạn ly! Tìm hết sói miệng trống là xong.'],
 'ybLosers': ['꽝 늑대 수 (원샷할 사람 수)', 'Empty wolves (how many drink)', 'ハズレオオカミの数（飲む人数）', '空嘴狼数量（喝酒人数）', 'จำนวนหมาป่าปากว่าง (คนที่ต้องดื่ม)', 'Số sói miệng trống (số người uống)'],
 'ybEatingN': ('fn', '(Int) -> String', ['{ "🪙 냠냠… ${it}마리는 굶어요" }', '{ "🪙 Nom nom… $it go hungry" }', '{ "🪙 もぐもぐ…${it}匹はおあずけ" }', '{ "🪙 吧唧吧唧…有${it}只饿着" }', '{ "🪙 งั่มๆ… อด $it ตัว" }', '{ "🪙 Măm măm… $it con bị nhịn" }']),
 'ybShotN': ('fn', '(String) -> String', ['{ "${it}번 원샷! 🍺" }', '{ "No. $it — drink! 🍺" }', '{ "${it}番 一気！🍺" }', '{ "${it}号 干杯！🍺" }', '{ "คนที่ $it หมดแก้ว! 🍺" }', '{ "Người số $it cạn ly! 🍺" }']),
 'ybStart': ['🪙 코인 먹이기', '🪙 Feed the coins', '🪙 コインを食べさせる', '🪙 喂硬币', '🪙 ป้อนเหรียญ', '🪙 Cho ăn xu'],
 'ybEating': ['🪙 냠냠… 한 마리는 굶어요', '🪙 Nom nom… one goes hungry', '🪙 もぐもぐ…1匹はおあずけ', '🪙 吧唧吧唧…有一只饿着', '🪙 งั่มๆ… มีตัวหนึ่งอด', '🪙 Măm măm… một con bị nhịn'],
 'ybShuffle': ['🔀 섞는 중… 잘 봐요 👀', '🔀 Shuffling… watch closely 👀', '🔀 シャッフル中…よく見て 👀', '🔀 洗牌中…看仔细 👀', '🔀 กำลังสลับ… ดูให้ดี 👀', '🔀 Đang trộn… nhìn kỹ 👀'],
 'ybPick': ('fn', '(Int) -> String', ['{ "👆 ${it}번, 늑대를 골라요!" }', '{ "👆 Player $it, pick a wolf!" }', '{ "👆 ${it}番、オオカミを選んで！" }', '{ "👆 ${it}号，选一只狼！" }', '{ "👆 คนที่ $it เลือกหมาป่า!" }', '{ "👆 Người số $it, chọn một con sói!" }']),
 'ybPass': ('fn', '(Int) -> String', ['{ "🪙 코인! ${it}번 통과 ✅ 다음 사람 👉" }', '{ "🪙 Coin! Player $it is safe ✅ Next 👉" }', '{ "🪙 コイン！${it}番セーフ ✅ 次の人 👉" }', '{ "🪙 硬币！${it}号过关 ✅ 下一个 👉" }', '{ "🪙 เหรียญ! คนที่ $it รอด ✅ คนถัดไป 👉" }', '{ "🪙 Có xu! Người số $it thoát ✅ Người kế 👉" }']),
 'ybHit': ['💀 당첨!', '💀 Gotcha!', '💀 当たり！', '💀 中奖了！', '💀 โดนแล้ว!', '💀 Trúng rồi!'],
 'ybShot': ('fn', '(Int) -> String', ['{ "${it}번 원샷! 🍺" }', '{ "Player $it drinks! 🍺" }', '{ "${it}番 一気！🍺" }', '{ "${it}号 干杯！🍺" }', '{ "คนที่ $it หมดแก้ว! 🍺" }', '{ "Người số $it cạn ly! 🍺" }']),
 # 📤 앱 공유하기
 'shBtn': ['📤 공유', '📤 Share', '📤 共有', '📤 分享', '📤 แชร์', '📤 Chia sẻ'],
 'shTitle': ['📤 울프 게임즈 공유하기', '📤 Share Wolf Games', '📤 ウルフゲームズを共有', '📤 分享沃夫游戏', '📤 แชร์ Wolf Games', '📤 Chia sẻ Wolf Games'],
 'shWhich': ['친구 폰이 뭐예요?', "What phone does your friend have?", '友だちのスマホは？', '朋友用的是什么手机？', 'เพื่อนใช้มือถืออะไร?', 'Bạn của bạn dùng điện thoại gì?'],
 'shAndroid': ['🤖 안드로이드', '🤖 Android', '🤖 Android', '🤖 安卓', '🤖 Android', '🤖 Android'],
 'shIphone': ['🍎 아이폰 · PC', '🍎 iPhone · PC', '🍎 iPhone · PC', '🍎 iPhone · 电脑', '🍎 iPhone · PC', '🍎 iPhone · PC'],
 'shAndroidTip': ['링크를 누르면 앱(APK)이 바로 받아져요. 받은 파일을 눌러 설치하면 끝!', 'Tapping the link downloads the app (APK). Open the file to install — done!', 'リンクを押すとアプリ（APK）がダウンロードされます。ファイルを開いてインストールすれば完了！', '点链接就会下载应用（APK），打开文件安装就好了！', 'กดลิงก์แล้วแอป (APK) จะดาวน์โหลดทันที เปิดไฟล์เพื่อติดตั้งก็เสร็จ!', 'Bấm link là tải ứng dụng (APK). Mở tệp để cài là xong!'],
 'shIphoneTip': ['사파리로 열고 → 공유 → "홈 화면에 추가" 하면 앱처럼 쓸 수 있어요.', 'Open in Safari → Share → "Add to Home Screen" to use it like an app.', 'Safariで開いて → 共有 → 「ホーム画面に追加」でアプリのように使えます。', '用 Safari 打开 → 分享 → "添加到主屏幕"，就能像应用一样用。', 'เปิดใน Safari → แชร์ → "เพิ่มไปยังหน้าจอโฮม" ก็ใช้ได้เหมือนแอป', 'Mở bằng Safari → Chia sẻ → "Thêm vào MH chính" là dùng như ứng dụng.'],
 'shQr': ['옆에 있으면 이 QR을 바로 찍어요 📷', "If they're next to you, they can scan this QR 📷", '隣にいるならこのQRをそのまま撮って 📷', '在旁边的话直接扫这个二维码 📷', 'ถ้าอยู่ข้างๆ สแกน QR นี้ได้เลย 📷', 'Nếu ở cạnh nhau, quét mã QR này luôn 📷'],
 'shSend': ['📲 앱으로 공유', '📲 Share via app', '📲 アプリで共有', '📲 用应用分享', '📲 แชร์ผ่านแอป', '📲 Chia sẻ qua ứng dụng'],
 'shCopy': ['🔗 링크만 복사', '🔗 Copy link only', '🔗 リンクだけコピー', '🔗 只复制链接', '🔗 คัดลอกลิงก์', '🔗 Chỉ sao chép link'],
 'shCopied': ['✅ 링크를 복사했어요', '✅ Link copied', '✅ リンクをコピーしました', '✅ 链接已复制', '✅ คัดลอกลิงก์แล้ว', '✅ Đã sao chép link'],
 'shMsg': ['🐺 울프 게임즈 — 술자리 게임 모음 (6개 언어)', '🐺 Wolf Games — drinking games in one app (6 languages)', '🐺 ウルフゲームズ — 飲み会ゲーム集（6か国語）', '🐺 沃夫游戏 — 酒局游戏合集（6种语言）', '🐺 Wolf Games — รวมเกมวงเหล้า (6 ภาษา)', '🐺 Wolf Games — tổng hợp trò chơi nhậu (6 ngôn ngữ)'],
}
MENU = {
 'menuLuckSub': ['사다리게임 · 돌림판 · 폭탄돌리기 · 동전 던지기 · 플링코 · 숫자 폭탄 · 풍선 불기 · 폭탄 선 자르기 · 늑대 야바위 · 손가락선택', 'Ladder Game · Roulette · Hot Bomb · Coin Toss · Plinko · Number Bomb · Balloon Pump · Cut the Wire · Wolf Shell Game · Finger Pick', 'あみだくじ · ルーレット · 爆弾回し · コイントス · プリンコ · 数字爆弾 · 風船ふくらまし · 爆弾の線切り · オオカミ当て · 指えらび', '爬梯子 · 转盘 · 传炸弹 · 抛硬币 · 弹珠台 · 数字炸弹 · 吹气球 · 剪炸弹线 · 狼猜猜猜 · 手指选人', 'เกมบันได · วงล้อ · ส่งระเบิด · โยนเหรียญ · พลิงโก · ระเบิดตัวเลข · เป่าลูกโป่ง · ตัดสายระเบิด · หมาป่าซ่อนเหรียญ · จิ้มนิ้วเลือกคน', 'Leo thang · Vòng quay · Chuyền bom · Tung đồng xu · Plinko · Bom số · Thổi bóng bay · Cắt dây bom · Đoán sói · Chọn ngón tay'],
}
if __name__ == '__main__':
    import json, re
    P = 'app/src/main/java/com/wolfgames/app/i18n/Strings.kt'
    src = open(P, encoding='utf-8').read()
    LANGS = ['Ko', 'En', 'Ja', 'Zh', 'Th', 'Vi']
    kq = lambda v: json.dumps(v, ensure_ascii=False).replace('$', '\\$')
    for k, v in list(S.items()) + list(MENU.items()):
        assert len(v[2] if isinstance(v, tuple) else v) == 6, k
    decl = ''.join(f'    lateinit var {k}: {v[1] if isinstance(v, tuple) else "String"}\n' for k, v in S.items() if f'lateinit var {k}:' not in src)
    i = src.index('\n}\n', src.index('class Strings {')); src = src[:i + 1] + decl + src[i + 1:]
    for li, L in enumerate(LANGS):
        head = f'val {L}Strings = Strings().apply {{'
        st = src.index(head); en = src.index('\n}\n', st); body = src[st:en]; add = ''
        for k, v in S.items():
            if re.search(rf'^    {k} = ', body, re.M): continue
            add += f'    {k} = {v[2][li] if isinstance(v, tuple) else kq(v[li])}\n'
        for k, v in MENU.items():
            body, n = re.subn(rf'^    {k} = .*$', lambda m: f'    {k} = {kq(v[li])}', body, flags=re.M); assert n == 1, k
        src = src[:st] + body + ('\n' + add.rstrip('\n') if add else '') + src[en:]
    open(P, 'w', encoding='utf-8').write(src); print('ok', len(S))
