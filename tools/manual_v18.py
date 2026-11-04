"""v1.8 설명서 반영: 새 방법 5개(탑 쌓기 · 따라 하기 · 늑대컬링 · 폭탄 선 자르기 · 사이즈 변환표) + 여행 정산 설명 보강 + v1.8 구성/수정 내용 (6개 언어). 한 번만 실행"""
import sys, pprint, json, re
sys.path.insert(0, 'tools')
import manual_text as m
from changelog import CHANGELOG

LANGS = ['ko', 'en', 'ja', 'zh', 'th', 'vi']
src = open('web/i18n.js', encoding='utf-8').read()
def strings(lang):
    st = src.index(f'  {lang}: {{'); en = src.index('\n  },', st)
    out = {}
    for mm in re.finditer(r'^    (\w+): ("(?:[^"\\]|\\.)*"),?$', src[st:en], re.M): out[mm.group(1)] = json.loads(mm.group(2))
    return out

NEW = {
 'how-tower': ('tsTitle', '🏗️', {
  'ko': ['⚡ 순발력게임 → <b>🏗️ 탑 쌓기</b>. 좌우로 움직이는 블록을 <b>터치</b>하면 떨어져요.', '아래 블록 밖으로 삐져나간 부분은 잘려 나가서 점점 좁아져요. 딱 맞으면 <b>✨ PERFECT</b>. 한 번 누르면 폰을 다음 사람에게 👉', '층이 올라갈수록 빨라져요. 블록이 아래 블록을 완전히 벗어나면 와르르 — 무너뜨린 사람이 🍺 원샷!'],
  'en': ['⚡ Quick Games → <b>🏗️ Tower Stack</b>. <b>Tap</b> to drop the block sliding left and right.', 'Whatever hangs over the block below is cut off, so it keeps getting narrower. A perfect fit gets <b>✨ PERFECT</b>. One tap, then pass the phone 👉', 'It speeds up as it grows. Miss the block below completely and it all comes crashing down — whoever topples it drinks 🍺!'],
  'ja': ['⚡ 反射神経ゲーム → <b>🏗️ タワー積み</b>。左右に動くブロックを<b>タッチ</b>すると落ちます。', '下のブロックからはみ出た部分は切れて、だんだん細くなります。ぴったりなら<b>✨ PERFECT</b>。1回押したら次の人へ 👉', '高くなるほど速くなります。下のブロックから完全に外れるとガラガラ — 崩した人が🍺一気！'],
  'zh': ['⚡ 反应游戏 → <b>🏗️ 叠高塔</b>。<b>点击</b>屏幕，左右移动的方块就会落下。', '超出下面方块的部分会被切掉，塔会越来越窄。完全对准就是 <b>✨ PERFECT</b>。点一次就把手机传给下一个人 👉', '越高越快。方块完全没落在下面的方块上，塔就哗啦倒了 — 弄倒的人 🍺 干杯！'],
  'th': ['⚡ เกมความไว → <b>🏗️ ต่อตึก</b> <b>แตะ</b>เพื่อปล่อยบล็อกที่เลื่อนไปมา', 'ส่วนที่ล้นออกนอกบล็อกข้างล่างจะถูกตัด ตึกจะแคบลงเรื่อยๆ ถ้าตรงเป๊ะได้ <b>✨ PERFECT</b> แตะครั้งเดียวแล้วส่งให้คนถัดไป 👉', 'ยิ่งสูงยิ่งเร็ว ถ้าบล็อกพลาดบล็อกข้างล่างทั้งหมด ตึกจะพังโครม — ใครทำพัง 🍺 หมดแก้ว!'],
  'vi': ['⚡ Trò phản xạ → <b>🏗️ Xếp tháp</b>. <b>Chạm</b> để thả khối đang trượt qua lại.', 'Phần thò ra ngoài khối bên dưới bị cắt, nên tháp hẹp dần. Khớp hoàn hảo được <b>✨ PERFECT</b>. Chạm một lần rồi chuyền điện thoại 👉', 'Càng cao càng nhanh. Trượt hẳn khỏi khối bên dưới là tháp đổ ầm — ai làm đổ thì 🍺 uống!']}),
 'how-simon': ('smTitle', '🔴', {
  'ko': ['⚡ 순발력게임 → <b>🔴 따라 하기</b>. 액세서리 늑대 4마리(😎 선글라스 · 🎀 리본 · 🧢 모자 · 👑 왕관)가 있어요.', '늑대들이 소리와 함께 <b>순서대로</b> 폴짝 뛰며 반짝여요 (👀). 끝나면 똑같은 순서로 눌러요 (👆).', '맞히면 순서가 하나 늘고 폰을 다음 사람에게 👉 틀리면 정답 늑대가 깜빡이고 🍺 원샷! 칸마다 ● ▲ ■ ★ 모양이 있어서 색이 헷갈려도 돼요.'],
  'en': ['⚡ Quick Games → <b>🔴 Copy Me</b>. There are 4 wolves with accessories (😎 sunglasses · 🎀 bow · 🧢 cap · 👑 crown).', 'The wolves hop and light up <b>in order</b> with a sound (👀). When they stop, press them in the same order (👆).', 'Get it right and the sequence grows by one — pass the phone 👉 Miss and the right wolf blinks — 🍺 drink! Each square also has a ● ▲ ■ ★ shape, so colors aren\'t a problem.'],
  'ja': ['⚡ 反射神経ゲーム → <b>🔴 まねっこ</b>。アクセサリーをつけたオオカミ4匹（😎 サングラス・🎀 リボン・🧢 帽子・👑 王冠）がいます。', 'オオカミが音と一緒に<b>順番に</b>ぴょんと光ります（👀）。終わったら同じ順番で押します（👆）。', '正解なら1つ増えて次の人へ 👉 間違えると正解のオオカミが点滅して🍺一気！マスごとに ● ▲ ■ ★ の形もあるので色が苦手でもOK。'],
  'zh': ['⚡ 反应游戏 → <b>🔴 跟着按</b>。有4只戴配饰的狼（😎 墨镜 · 🎀 蝴蝶结 · 🧢 帽子 · 👑 皇冠）。', '狼会随着声音<b>按顺序</b>跳起来亮一下（👀）。亮完后按同样的顺序按（👆）。', '答对顺序就多一个，把手机传下去 👉 按错时正确的狼会闪 — 🍺 干杯！每格还有 ● ▲ ■ ★ 形状，分不清颜色也没关系。'],
  'th': ['⚡ เกมความไว → <b>🔴 ทำตาม</b> มีหมาป่า 4 ตัวใส่เครื่องประดับ (😎 แว่นกันแดด · 🎀 โบว์ · 🧢 หมวก · 👑 มงกุฎ)', 'หมาป่าจะกระโดดกะพริบ<b>ตามลำดับ</b>พร้อมเสียง (👀) จบแล้วกดตามลำดับเดิม (👆)', 'ถูกแล้วลำดับยาวขึ้นหนึ่ง ส่งมือถือต่อ 👉 ถ้าผิด หมาป่าตัวที่ถูกจะกะพริบ — 🍺 ดื่ม! แต่ละช่องมีรูป ● ▲ ■ ★ ด้วย แยกสีไม่ออกก็เล่นได้'],
  'vi': ['⚡ Trò phản xạ → <b>🔴 Bắt chước</b>. Có 4 chú sói đeo phụ kiện (😎 kính râm · 🎀 nơ · 🧢 mũ · 👑 vương miện).', 'Các chú sói nhảy lên và sáng <b>theo thứ tự</b> kèm âm thanh (👀). Xong thì bấm lại đúng thứ tự đó (👆).', 'Đúng thì chuỗi dài thêm một, chuyền điện thoại 👉 Sai thì chú sói đúng nhấp nháy — 🍺 uống! Mỗi ô còn có hình ● ▲ ■ ★ nên khó phân biệt màu cũng chơi được.']}),
 'how-curling': ('cuTitle', '🍺', {
  'ko': ['⚡ 순발력게임 → <b>🍺 늑대컬링</b>. 인원(2~6명)을 고르고 시작해요. 사람마다 두건 색과 숫자가 다른 늑대 맥주잔이 있어요.', '출발선의 잔에 손가락을 대고 <b>뒤로(아래로) 당겼다가 놓으면</b> 미끄러져 나가요. 많이 당길수록 멀리 가요 (힘 게이지 없이 감으로!).', '테이블 끝에 가까울수록 좋아요 (🥇). 끝을 넘으면 💦 떨어져요. 다 던지면 🏆 순위 — 떨어진 사람은 모두, 아무도 안 떨어졌으면 제일 먼 사람이 🍺 원샷!'],
  'en': ['⚡ Quick Games → <b>🍺 Wolf Curling</b>. Pick the players (2–6) and start. Each person gets a wolf beer mug with its own bandana color and number.', 'Put your finger on the mug at the start line, <b>pull it back (down) and let go</b> — it slides forward. The more you pull, the farther it goes (no power meter — go by feel!).', 'The closer to the edge, the better (🥇). Go past it and it 💦 falls. After everyone throws, 🏆 ranking — everyone who fell drinks, or if nobody fell, the farthest one 🍺!'],
  'ja': ['⚡ 反射神経ゲーム → <b>🍺 オオカミカーリング</b>。人数（2〜6人）を選んでスタート。1人ずつバンダナの色と番号が違うオオカミジョッキがあります。', 'スタートラインのジョッキに指を当てて<b>後ろ（下）に引いて放す</b>と滑っていきます。たくさん引くほど遠くへ（パワーゲージなし、感覚で！）。', 'テーブルの端に近いほど良い（🥇）。越えると💦落ちます。全員投げたら🏆順位 — 落ちた人は全員、誰も落ちなければ一番遠い人が🍺一気！'],
  'zh': ['⚡ 反应游戏 → <b>🍺 狼冰壶</b>。选好人数（2~6人）开始。每人有一个头巾颜色和号码不同的狼啤酒杯。', '手指按住起点线上的杯子，<b>往后（往下）拉再松手</b>，它就会滑出去。拉得越多滑得越远（没有力度条，全凭手感！）。', '离桌边越近越好（🥇）。超过桌边就 💦 掉下去。大家都推完后 🏆 排名 — 掉下去的人都要喝，没人掉的话离得最远的人 🍺 干杯！'],
  'th': ['⚡ เกมความไว → <b>🍺 เคิร์ลลิงหมาป่า</b> เลือกจำนวนคน (2–6) แล้วเริ่ม แต่ละคนมีแก้วเบียร์หมาป่าที่สีผ้าโพกและหมายเลขต่างกัน', 'วางนิ้วบนแก้วที่เส้นเริ่ม <b>ดึงลง (ไปข้างหลัง) แล้วปล่อย</b> แก้วจะไถลไป ยิ่งดึงมากยิ่งไปไกล (ไม่มีเกจแรง ใช้ความรู้สึก!)', 'ยิ่งใกล้ขอบโต๊ะยิ่งดี (🥇) ถ้าเลยขอบจะ 💦 ตก ทุกคนเล่นครบแล้วดู 🏆 อันดับ — ใครตกต้องดื่มทุกคน ถ้าไม่มีใครตก คนที่ไกลสุด 🍺 หมดแก้ว!'],
  'vi': ['⚡ Trò phản xạ → <b>🍺 Bi đá sói</b>. Chọn số người (2–6) rồi bắt đầu. Mỗi người có một cốc bia sói với màu khăn và số riêng.', 'Đặt ngón tay lên cốc ở vạch xuất phát, <b>kéo về sau (xuống dưới) rồi thả</b> — cốc trượt đi. Kéo càng nhiều càng đi xa (không có thanh lực, dựa vào cảm giác!).', 'Càng sát mép bàn càng tốt (🥇). Vượt mép là 💦 rơi. Ném hết thì xem 🏆 xếp hạng — ai rơi đều uống, nếu không ai rơi thì người xa nhất 🍺 cạn ly!']}),
 'how-wire': ('wcTitle', '✂️', {
  'ko': ['🍀 운빨게임 → <b>✂️ 폭탄 선 자르기</b>. 선 개수(4 · 5 · 6 · 8)를 고르고 시작해요. 그중 하나만 진짜 선이에요.', '돌아가면서 선을 하나씩 <b>터치해서 잘라요</b> ✂️ 자를 때마다 폭탄 타이머가 빨라져요. 안전하면 "휴~ 살았다!" → 다음 사람 👉', '진짜 선을 자르면 💥 펑! 헤롱헤롱 늑대와 함께 🍺 원샷. 다시 하기를 누르면 진짜 선이 새로 숨어요.'],
  'en': ['🍀 Luck Games → <b>✂️ Cut the Wire</b>. Pick the number of wires (4 · 5 · 6 · 8) and start. Only one of them is real.', 'Take turns <b>tapping a wire to cut it</b> ✂️ The bomb timer speeds up with every cut. Safe? "Phew~ safe!" → next person 👉', 'Cut the real one and 💥 BOOM — a dizzy wolf and 🍺 bottoms up. Play again hides a new real wire.'],
  'ja': ['🍀 運ゲーム → <b>✂️ 爆弾の線切り</b>。線の数（4・5・6・8）を選んでスタート。本物は1本だけ。', '順番に線を1本<b>タッチして切ります</b> ✂️ 切るたびに爆弾のタイマーが速くなります。セーフなら「ふぅ〜セーフ！」→ 次の人 👉', '本物を切ったら💥ドカン！フラフラのオオカミと一緒に🍺一気。もう一度で本物の線が新しく隠れます。'],
  'zh': ['🍀 运气游戏 → <b>✂️ 剪炸弹线</b>。选好线的数量（4 · 5 · 6 · 8）开始。只有一根是真的。', '轮流<b>点一根线剪断</b> ✂️ 每剪一根，炸弹计时就更快。安全的话“呼~安全！”→ 下一个人 👉', '剪到真线就 💥 砰！和晕乎乎的小狼一起 🍺 干杯。再来一局会重新藏一根真线。'],
  'th': ['🍀 เกมดวง → <b>✂️ ตัดสายระเบิด</b> เลือกจำนวนสาย (4 · 5 · 6 · 8) แล้วเริ่ม มีสายจริงแค่เส้นเดียว', 'ผลัดกัน<b>แตะสายเพื่อตัด</b>ทีละเส้น ✂️ ตัดแต่ละครั้งตัวจับเวลาจะเร็วขึ้น ถ้ารอด "เฮ้อ~ รอด!" → คนถัดไป 👉', 'ตัดโดนสายจริง 💥 ตูม! หมาป่ามึนตึ้บพร้อม 🍺 หมดแก้ว กดเล่นอีกครั้งจะซ่อนสายจริงใหม่'],
  'vi': ['🍀 Trò may rủi → <b>✂️ Cắt dây bom</b>. Chọn số dây (4 · 5 · 6 · 8) rồi bắt đầu. Chỉ một dây là thật.', 'Lần lượt <b>chạm một dây để cắt</b> ✂️ Mỗi lần cắt, đồng hồ bom chạy nhanh hơn. An toàn thì "Phù~ thoát!" → người kế tiếp 👉', 'Cắt trúng dây thật là 💥 BÙM! Sói choáng váng và 🍺 cạn ly. Chơi lại sẽ giấu một dây thật mới.']}),
 'how-sizes': ('szTitle', '📏', {
  'ko': ['🧮 계산기 → <b>📏 사이즈</b> 버튼 (계산기에는 <b>🧾 정산</b> 버튼도 있어요).', '위에서 👟 신발(남·여) · 👕 상의(남·여) · 👖 바지 허리 · 💍 반지 중 하나를 고르면 🇰🇷🇺🇸🇬🇧🇪🇺🇯🇵🇨🇳 사이즈가 한 줄에 나란히 나와요.', '내 사이즈 줄을 누르면 ⭐ 저장돼서 다음에도 바로 보여요. 대략적인 기준이라 브랜드마다 조금 달라요.'],
  'en': ['🧮 Calculator → <b>📏 Sizes</b> button (the calculator also has a <b>🧾 Split bill</b> button).', 'Pick 👟 shoes (men/women) · 👕 tops (men/women) · 👖 waist · 💍 rings at the top, and 🇰🇷🇺🇸🇬🇧🇪🇺🇯🇵🇨🇳 sizes line up in one row.', 'Tap your row to ⭐ save it for next time. It\'s a rough guide — brands vary a little.'],
  'ja': ['🧮 電卓 → <b>📏 サイズ</b>ボタン（電卓には<b>🧾 精算</b>ボタンもあります）。', '上で 👟 靴（男・女）・👕 トップス（男・女）・👖 ウエスト・💍 指輪 を選ぶと、🇰🇷🇺🇸🇬🇧🇪🇺🇯🇵🇨🇳 のサイズが1行に並びます。', '自分のサイズの行をタッチすると⭐保存され、次もすぐ表示。目安なのでブランドによって少し違います。'],
  'zh': ['🧮 计算器 → <b>📏 尺码</b>按钮（计算器里也有 <b>🧾 结算</b> 按钮）。', '在上面选 👟 鞋（男女）· 👕 上衣（男女）· 👖 裤腰 · 💍 戒指，🇰🇷🇺🇸🇬🇧🇪🇺🇯🇵🇨🇳 的尺码会排在同一行。', '点自己的那一行会 ⭐ 保存，下次直接显示。仅供参考，各品牌略有不同。'],
  'th': ['🧮 เครื่องคิดเลข → ปุ่ม <b>📏 ไซซ์</b> (ในเครื่องคิดเลขมีปุ่ม <b>🧾 แบ่งจ่าย</b> ด้วย)', 'เลือก 👟 รองเท้า (ชาย/หญิง) · 👕 เสื้อ (ชาย/หญิง) · 👖 เอว · 💍 แหวน ด้านบน ไซซ์ 🇰🇷🇺🇸🇬🇧🇪🇺🇯🇵🇨🇳 จะเรียงอยู่แถวเดียวกัน', 'แตะแถวของเราเพื่อ ⭐ บันทึกไว้ครั้งหน้า เป็นค่าประมาณ แต่ละแบรนด์ต่างกันเล็กน้อย'],
  'vi': ['🧮 Máy tính → nút <b>📏 Cỡ</b> (máy tính còn có nút <b>🧾 Chia tiền</b>).', 'Chọn 👟 giày (nam/nữ) · 👕 áo (nam/nữ) · 👖 vòng eo · 💍 nhẫn ở trên, cỡ 🇰🇷🇺🇸🇬🇧🇪🇺🇯🇵🇨🇳 sẽ xếp cùng một hàng.', 'Chạm vào hàng của bạn để ⭐ lưu cho lần sau. Chỉ là ước lượng, mỗi hãng hơi khác.']}),
}
AFTER = {'how-tower': 'how-beer', 'how-simon': 'how-tower', 'how-curling': 'how-simon', 'how-wire': 'how-balloon', 'how-sizes': 'how-settle'}
SETTLE_ADD = {
 'ko': ' <b>💰 중간 정산</b>: 1,000원 단위 등으로 반올림해서 받고 남은 차이는 다음으로. <b>📤 내보내기</b>: 엑셀(.csv) · 메모장(.txt)으로 저장하거나 카톡으로 공유.',
 'en': ' <b>💰 Settle now</b>: collect rounded amounts (e.g. to 1,000); the leftover carries over. <b>📤 Export</b>: save as Excel (.csv) / text (.txt) or share to a messenger.',
 'ja': ' <b>💰 途中精算</b>：1,000単位などに丸めて受け取り、残りの差額は次へ。<b>📤 書き出し</b>：Excel（.csv）・メモ帳（.txt）で保存、またはLINEなどで共有。',
 'zh': ' <b>💰 中途结算</b>：按1,000等单位取整先收，剩下的差额留到下次。<b>📤 导出</b>：存成 Excel（.csv）或文本（.txt），或分享到聊天软件。',
 'th': ' <b>💰 เคลียร์ระหว่างทาง</b>: ปัดเป็นหลัก 1,000 ฯลฯ แล้วเก็บก่อน ส่วนต่างยกไปรอบหน้า <b>📤 ส่งออก</b>: บันทึกเป็น Excel (.csv) / ข้อความ (.txt) หรือแชร์ไปแชท',
 'vi': ' <b>💰 Chốt giữa chừng</b>: làm tròn (vd theo 1,000) rồi thu, phần chênh lệch chuyển sang lần sau. <b>📤 Xuất ra</b>: lưu Excel (.csv) / văn bản (.txt) hoặc chia sẻ qua chat.',
}

keys = list(m.HOW_KEYS); emo = list(m.HOW_EMOJI); steps = {L: list(m.HOW[L]['steps']) for L in LANGS}
for shot, after in AFTER.items():
    if any(k[0] == shot for k in keys): continue
    i = [k[0] for k in keys].index(after) + 1
    key, e, txt = NEW[shot]
    keys.insert(i, (shot, key)); emo.insert(i, e)
    for L in LANGS: steps[L].insert(i, txt[L])
si = [k[0] for k in keys].index('how-settle')
for L in LANGS:
    st = list(steps[L][si])
    if '💰' not in st[2]: st[2] = st[2] + SETTLE_ADD[L]
    steps[L][si] = st
for L in LANGS: m.HOW[L]['steps'] = steps[L]

# v1.8 구성 · 수정 내용
rel = CHANGELOG[0]; assert rel['ver'] == '1.8'
for L in LANGS:
    P = m.PAGE[L]; s = strings(L)
    for k in ['navNew', 'mainP', 'hNew', 'newIntro']: P[k] = P[k].replace('v1.7', 'v1.8')
    P['changes'] = [tx[L] for _, tx in rel['items']]
    feat = list(P['newFeat'])
    old17 = feat[1]; assert old17.startswith('🆕'), old17
    feat[1] = '🆕 ' + ' · '.join([f"🏗️ {s['tsTitle']}", f"🔴 {s['smTitle']}", f"🍺 {s['cuTitle']}", f"✂️ {s['wcTitle']}", f"📏 {s['szTitle']}", f"💰 {s['stMid'].split(' ', 1)[-1]}", f"📤 {s['stExport'].split(' ', 1)[-1]}", f"🔄 {s['upCheck'].split(' ', 1)[-1]}"])
    feat.insert(2, '✨ v1.7: ' + old17[2:].strip())
    P['newFeat'] = feat

head = open('tools/manual_text.py', encoding='utf-8').read().split('\nPAGE = {')[0]
pp = lambda o: pprint.pformat(o, width=200, sort_dicts=False)
out = head + '\nPAGE = ' + pp(m.PAGE) + '\n\n# 단계 그림 속 폰 화면 문구\nSHOT = ' + pp(m.SHOT) + '\n\n# 게임 방법: (그림 이름, 제목으로 쓸 앱 문구 키, 설명 3줄)\nHOW_KEYS = ' + repr(keys) + '\nHOW_EMOJI = ' + repr(emo) + '\n\nHOW = ' + pp(m.HOW) + '\n'
open('tools/manual_text.py', 'w', encoding='utf-8').write(out)
print('ok', [k[0] for k in keys])
