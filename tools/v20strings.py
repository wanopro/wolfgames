# v2.0 새 문구 (ko, en, ja, zh, th, vi) — 🎰 늑대 슬롯머신 · 🧱 늑대 젠가 · 🃏 늑대 짝 맞추기 · ⚖️ 늑대 균형 잡기
# 사용: python3 tools/v20strings.py → python3 tools/gen_web_i18n.py → python3 tools/gen_dual.py
S = {
 'turnN': ('fn', '(Int) -> String', ['{ "👆 ${it}번 차례" }', '{ "👆 Player $it\'s turn" }', '{ "👆 ${it}番の番" }', '{ "👆 轮到${it}号" }', '{ "👆 ตาคนที่ $it" }', '{ "👆 Lượt người số $it" }']),
 # 🎰 늑대 슬롯머신 (🍀 운빨)
 'slTitle': ['늑대 슬롯머신', 'Wolf Slots', 'オオカミスロット', '狼老虎机', 'สล็อตหมาป่า', 'Máy quay sói'],
 'slSub': ['레버를 당겨라! 💀 나오면 벌칙', 'Pull the lever! 💀 = penalty', 'レバーを引け！💀が出たら罰ゲーム', '拉下拉杆！出现💀就受罚', 'ดึงคันโยก! ออก 💀 โดนทำโทษ', 'Kéo cần! Ra 💀 là bị phạt'],
 'slDesc': ['한 사람씩 레버를 당겨요 (화면 아무 데나 눌러도 돼요). 그림 3개가 하나씩 멈춰요!\n💀 해골이 하나라도 나오면 → 벌칙\n🐺 늑대 3개 → 🙌 다 같이 벌칙\n같은 그림 3개 → 👉 벌칙 받을 사람 지목\n나머지 → ✅ 통과, 다음 사람에게 👉',
            'Take turns pulling the lever (or tap anywhere). The 3 reels stop one by one!\nAny 💀 skull → penalty\n🐺🐺🐺 three wolves → 🙌 everyone gets a penalty\nThree of a kind → 👉 pick who gets the penalty\nAnything else → ✅ safe, pass it on 👉',
            '1人ずつレバーを引きます（画面のどこを押してもOK）。絵が3つ順番に止まります！\n💀が1つでも出たら → 罰ゲーム\n🐺が3つ → 🙌 みんなで罰ゲーム\n同じ絵が3つ → 👉 罰ゲームの人を指名\nそれ以外 → ✅ セーフ、次の人へ 👉',
            '每人轮流拉下拉杆（点屏幕任意位置也行）。3个图案会依次停下！\n出现任何一个💀 → 受罚\n3个🐺 → 🙌 大家一起受罚\n3个相同图案 → 👉 指定一个人受罚\n其他 → ✅ 过关，交给下一个人 👉',
            'ผลัดกันดึงคันโยก (แตะตรงไหนก็ได้) รูป 3 ช่องจะหยุดทีละช่อง!\nมี 💀 แม้แต่อันเดียว → โดนทำโทษ\n🐺 3 ตัว → 🙌 โดนทำโทษทุกคน\nรูปเหมือนกัน 3 อัน → 👉 ชี้คนที่จะโดนทำโทษ\nนอกนั้น → ✅ รอด ส่งต่อคนถัดไป 👉',
            'Lần lượt kéo cần (chạm chỗ nào cũng được). 3 ô hình sẽ dừng từng ô một!\nCó 💀 dù chỉ một → bị phạt\n🐺 3 con → 🙌 cả nhóm bị phạt\n3 hình giống nhau → 👉 chỉ định người bị phạt\nCòn lại → ✅ thoát, chuyển cho người kế 👉'],
 'slPass': ['✅ 통과', '✅ Safe', '✅ セーフ', '✅ 过关', '✅ รอด', '✅ Thoát'],
 'slAll': ['🙌 다 같이 벌칙!', '🙌 Everyone — penalty!', '🙌 みんなで罰ゲーム！', '🙌 大家一起受罚！', '🙌 ทุกคนโดนทำโทษ!', '🙌 Cả nhóm bị phạt!'],
 'slPick': ['👉 지목!', '👉 Pick someone!', '👉 指名！', '👉 指定一个人！', '👉 ชี้คน!', '👉 Chỉ định!'],
 # 🧱 늑대 젠가 (⚡ 순발력)
 'jgTitle': ['늑대 젠가', 'Wolf Jenga', 'オオカミジェンガ', '狼叠叠乐', 'เจงก้าหมาป่า', 'Jenga sói'],
 'jgSub': ['블록을 빼다 무너뜨리면 벌칙', 'Topple the tower = penalty', '崩したら罰ゲーム', '把塔弄倒就受罚', 'ทำหอถล่ม โดนทำโทษ', 'Làm đổ tháp là bị phạt'],
 'jgDesc': ["블록을 꾹 누른 채 바깥쪽으로 쭉 끌어서 빼요. 빨리 빼면 탑이 흔들려요 (위 게이지 〰️)\n빼는 동안 손가락이 위아래로 흔들리면 안 돼요 — 점이 🔴 되면 게이지가 차요\n걸리는 곳에서 덜컹하면 아주 천천히! 게이지가 꽉 차면 와르르 → 💀 벌칙\n끝까지 빼면 맨 위로 올라가고 다음 사람 👉 (중간에 손 떼면 다시 들어가요 · 맨 위 줄은 못 빼요)", "Press and hold a block, then drag it outward. Pull too fast and the tower wobbles (gauge on top 〰️)\nKeep your finger level while pulling — if it drifts up or down the dot turns 🔴 and the gauge fills\nWhen it jolts on a snag, go really slow! Full gauge = it all comes down → 💀 penalty\nPull it all the way and it goes on top, next player 👉 (let go early and it slides back · not from the top row)", "ブロックを押したまま外側へ引き抜きます。速く抜くとタワーが揺れます（上のゲージ 〰️）\n抜いている間、指を上下にぶらさないで — 点が 🔴 になるとゲージが溜まります\n引っかかってガタッときたらゆっくり！ゲージが満タンで崩壊 → 💀 罰ゲーム\n最後まで抜けばてっぺんへ、次の人 👉（途中で離すと戻ります・一番上の段は抜けません）", "按住积木往外拖出来。拖得太快塔就会晃（上方的计量条 〰️）\n拖的时候手指不能上下晃 — 圆点变 🔴 计量条就会涨\n卡住一顿时要非常慢！计量条满了就倒 → 💀 受罚\n完全抽出就放到最上面，换下一个人 👉（中途松手会滑回去 · 最上层不能抽）", "กดบล็อกค้างไว้แล้วลากออกด้านข้าง ลากเร็วหอจะโยก (เกจด้านบน 〰️)\nระหว่างดึงห้ามนิ้วขยับขึ้นลง — ถ้าจุดเป็น 🔴 เกจจะเพิ่ม\nพอสะดุดตรงจุดที่ติดให้ค่อยๆ ดึง! เกจเต็มหอถล่ม → 💀 โดนทำโทษ\nดึงออกสุดบล็อกไปวางบนสุด คนถัดไป 👉 (ปล่อยกลางทางบล็อกจะกลับเข้าไป · แถวบนสุดดึงไม่ได้)", "Giữ một khối rồi kéo ra ngoài. Kéo nhanh quá tháp sẽ lắc (thanh đo phía trên 〰️)\nKhi kéo đừng để ngón tay lệch lên xuống — chấm thành 🔴 là thanh đo tăng\nKhi bị kẹt giật thì kéo thật chậm! Thanh đầy là đổ → 💀 bị phạt\nRút hết thì khối lên đỉnh, đến người kế 👉 (buông giữa chừng khối sẽ trượt lại · không rút hàng trên cùng)"],
 # 🃏 늑대 짝 맞추기 (⚔️ 대결)
 'mmTitle': ['늑대 짝 맞추기', 'Wolf Memory', 'オオカミ神経衰弱', '狼狼配对', 'จับคู่หมาป่า', 'Lật hình sói'],
 'mmSub': ['같은 늑대 2장을 찾아라!', 'Find the matching wolves!', '同じオオカミを2枚見つけろ！', '找出两张一样的狼！', 'หาหมาป่าที่เหมือนกัน 2 ใบ!', 'Tìm 2 con sói giống nhau!'],
 'mmDesc': ['카드를 2장씩 뒤집어서 같은 늑대를 찾아요.\n맞히면 1점 + 한 번 더! 틀리면 다음 사람 👉\n카드가 다 없어지면 끝! 점수가 제일 낮은 사람이 💀 벌칙',
            'Flip 2 cards at a time to find the same wolf.\nMatch = 1 point + go again! Miss = next player 👉\nWhen all cards are gone, the lowest score gets a 💀 penalty',
            'カードを2枚ずつめくって同じオオカミを探します。\n当たれば1点＋もう1回！外れたら次の人 👉\nカードがなくなったら終わり！点数が一番低い人が 💀 罰ゲーム',
            '每次翻两张牌，找出一样的狼。\n配对成功得1分＋再翻一次！没配上就换下一个人 👉\n牌全部翻完就结束！分数最低的人 💀 受罚',
            'เปิดการ์ดทีละ 2 ใบ หาหมาป่าที่เหมือนกัน\nตรงกัน = 1 แต้ม + เล่นต่อ! ไม่ตรง = คนถัดไป 👉\nการ์ดหมดก็จบ! คนที่แต้มน้อยสุด 💀 โดนทำโทษ',
            'Lật 2 lá mỗi lần để tìm 2 con sói giống nhau.\nĐúng = 1 điểm + lật tiếp! Sai = người kế 👉\nHết bài là xong! Ai ít điểm nhất sẽ 💀 bị phạt'],
 'mmPairs': ['카드 쌍 수', 'Pairs', 'ペアの数', '配对数', 'จำนวนคู่', 'Số cặp'],
 # ⚖️ 늑대 균형 잡기 (⚡ 순발력)
 'bbTitle': ['늑대 균형 잡기', 'Wolf Balance', 'オオカミバランス', '狼平衡', 'ทรงตัวหมาป่า', 'Giữ thăng bằng sói'],
 'bbSub': ['폰을 기울여 늑대를 접시 위에!', 'Tilt to keep the wolf on the plate!', '傾けてオオカミをお皿の上に！', '倾斜手机，让狼留在盘子上！', 'เอียงมือถือให้หมาป่าอยู่บนจาน!', 'Nghiêng điện thoại giữ sói trên đĩa!'],
 'bbDesc': ["폰을 평평하게 들고 살살 기울여서 늑대가 접시에서 떨어지지 않게 해요.\n6초 버티면 \"👉 넘겨!\" — 3초 동안 늑대가 멈추니까 그때 폰을 다음 사람에게\n한 턴 넘길 때마다 🔥 Lv 업: 접시는 더 작고 미끄럽게, 바람은 더 세고 자주, 계속 미는 바람(파란 화살표)까지 🌬️\n늑대를 떨어뜨린 사람이 💀 벌칙! (PC는 마우스를 움직여서 기울여요)", "Hold the phone flat and tilt gently to keep the wolf on the plate.\nLast 6 seconds and \"👉 Pass!\" shows — the wolf freezes for 3 seconds while you hand the phone on\nEvery pass is 🔥 Lv up: smaller, slipperier plate, stronger and more frequent gusts, plus a steady push wind (blue arrows) 🌬️\nWhoever drops the wolf gets a 💀 penalty! (On PC, move the mouse to tilt)", "スマホを平らに持ってそっと傾け、オオカミをお皿から落とさないように。\n6秒耐えたら「👉 次へ！」— 3秒間オオカミが止まるので、その間に次の人へ\n渡すたびに 🔥 Lvアップ：お皿はより小さく滑りやすく、突風は強く頻繁に、押し続ける風（青い矢印）も 🌬️\nオオカミを落とした人が 💀 罰ゲーム！（PCはマウスで傾けます）", "把手机放平，轻轻倾斜，别让狼从盘子上掉下去。\n坚持6秒出现\"👉 传！\"— 狼会停住3秒，趁这时把手机交给下一个人\n每传一次 🔥 Lv 升级：盘子更小更滑，阵风更强更频繁，还有一直在推的风（蓝色箭头）🌬️\n让狼掉下去的人 💀 受罚！（电脑用鼠标倾斜）", "ถือมือถือให้ราบแล้วเอียงเบาๆ อย่าให้หมาป่าตกจาน\nอยู่ได้ 6 วินาทีจะขึ้น \"👉 ส่งต่อ!\" — หมาป่าหยุด 3 วินาที ส่งมือถือให้คนถัดไปตอนนั้น\nส่งต่อแต่ละครั้ง 🔥 Lv เพิ่ม: จานเล็กลงและลื่นขึ้น ลมกระโชกแรงและบ่อยขึ้น มีลมดันตลอด (ลูกศรสีฟ้า) 🌬️\nใครทำหมาป่าตก 💀 โดนทำโทษ! (บน PC ขยับเมาส์เพื่อเอียง)", "Cầm điện thoại thẳng và nghiêng nhẹ để sói không rơi khỏi đĩa.\nGiữ được 6 giây sẽ hiện \"👉 Chuyền!\" — sói đứng yên 3 giây, lúc đó đưa máy cho người kế\nMỗi lần chuyền 🔥 lên Lv: đĩa nhỏ và trơn hơn, gió giật mạnh và dày hơn, thêm gió đẩy liên tục (mũi tên xanh) 🌬️\nAi làm rơi sói sẽ 💀 bị phạt! (Trên PC, di chuột để nghiêng)"],
 'bbPass': ['👉 넘겨!', '👉 Pass!', '👉 次へ！', '👉 传！', '👉 ส่งต่อ!', '👉 Chuyền!'],
 'bbHold': ('fn', '(Int) -> String', ['{ "✋ ${it}번, 버텨!" }', '{ "✋ Player $it, hold it!" }', '{ "✋ ${it}番、がんばれ！" }', '{ "✋ ${it}号，稳住！" }', '{ "✋ คนที่ $it อดทน!" }', '{ "✋ Người số $it, giữ vững!" }']),
 'bbNoSensor': ['📱 기울기 센서를 쓸 수 없어서 손가락으로 기울여요', '📱 No tilt sensor — drag with your finger', '📱 傾きセンサーが使えないので指で傾けます', '📱 无法使用倾斜感应，用手指拖动', '📱 ใช้เซ็นเซอร์เอียงไม่ได้ ใช้นิ้วลากแทน', '📱 Không có cảm biến nghiêng — kéo bằng ngón tay'],
}
# 메뉴 설명 줄에 이름 붙이기: (위치, 6개 언어 이름)
MENU = {
 'menuLuckSub': ('beforeLast', ['늑대 슬롯머신', 'Wolf Slots', 'オオカミスロット', '狼老虎机', 'สล็อตหมาป่า', 'Máy quay sói']),
 'menuQuickSub': ('end', ['늑대 젠가', 'Wolf Jenga', 'オオカミジェンガ', '狼叠叠乐', 'เจงก้าหมาป่า', 'Jenga sói']),
 'menuBattleSub': ('end', ['늑대 짝 맞추기', 'Wolf Memory', 'オオカミ神経衰弱', '狼狼配对', 'จับคู่หมาป่า', 'Lật hình sói']),
}
MENU2 = {'menuQuickSub': ('end', ['늑대 균형 잡기', 'Wolf Balance', 'オオカミバランス', '狼平衡', 'ทรงตัวหมาป่า', 'Giữ thăng bằng sói'])}
if __name__ == '__main__':
    import json, re
    P = 'app/src/main/java/com/wolfgames/app/i18n/Strings.kt'
    src = open(P, encoding='utf-8').read()
    LANGS = ['Ko', 'En', 'Ja', 'Zh', 'Th', 'Vi']
    kq = lambda v: json.dumps(v, ensure_ascii=False).replace('$', '\\$')
    for k, v in S.items():
        assert len(v[2] if isinstance(v, tuple) else v) == 6, k
    decl = ''.join(f'    lateinit var {k}: {v[1] if isinstance(v, tuple) else "String"}\n' for k, v in S.items() if f'lateinit var {k}:' not in src)
    i = src.index('\n}\n', src.index('class Strings {')); src = src[:i + 1] + decl + src[i + 1:]
    for li, L in enumerate(LANGS):
        head = f'val {L}Strings = Strings().apply {{'
        st = src.index(head); en = src.index('\n}\n', st); body = src[st:en]; add = ''
        for k, v in S.items():
            if re.search(rf'^    {k} = ', body, re.M): continue
            add += f'    {k} = {v[2][li] if isinstance(v, tuple) else kq(v[li])}\n'
        for k, (where, names) in MENU.items():
            m = re.search(rf'^    {k} = "(.*)"$', body, re.M); assert m, k
            items = m.group(1).split(' · ')
            if names[li] not in items:
                if where == 'beforeLast': items.insert(len(items) - 1, names[li])
                else: items.append(names[li])
            body = body[:m.start()] + f'    {k} = "' + ' · '.join(items) + '"' + body[m.end():]
        src = src[:st] + body + ('\n' + add.rstrip('\n') if add else '') + src[en:]
    open(P, 'w', encoding='utf-8').write(src); print('ok', len(S))
    if MENU2:
        MENU = MENU2; MENU2 = None; S = {}
        src = open(P, encoding='utf-8').read()
        for li, L in enumerate(LANGS):
            head = f'val {L}Strings = Strings().apply {{'
            st = src.index(head); en = src.index('\n}\n', st); body = src[st:en]
            for k, (where, names) in MENU.items():
                m = re.search(rf'^    {k} = "(.*)"$', body, re.M); items = m.group(1).split(' · ')
                if names[li] not in items: items.append(names[li])
                body = body[:m.start()] + f'    {k} = "' + ' · '.join(items) + '"' + body[m.end():]
            src = src[:st] + body + src[en:]
        open(P, 'w', encoding='utf-8').write(src)
