"""v1.7 설명서 반영: 새 방법 4개(숫자 폭탄 · 풍선 불기 · 회화 카드 · 여행 정산) + 손가락선택 자리 옮김 + v1.7 구성/수정 내용 (6개 언어). 한 번만 실행"""
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
 'how-nbomb': ('nbTitle', '🔢', {
  'ko': ['🍀 운빨게임 → <b>🔢 숫자 폭탄</b>. 최대 숫자(30 · 50 · 100 · 200)를 고르고 시작하면 1부터 그 숫자 사이에 폭탄 숫자가 하나 숨어요.', '폰을 돌리면서 숫자를 하나씩 눌러요. 폭탄보다 작으면 <b>⬆️ UP</b>, 크면 <b>⬇️ DOWN</b> — 범위가 점점 좁아지고 폭탄 심지도 짧아져요.', '폭탄 숫자를 누르면 <b>💥 펑!</b> 그을린 늑대가 헤롱헤롱 😵‍💫 그 사람이 <b>💀 원샷</b>. "다시 하기"로 새 판.'],
  'en': ['🍀 Luck Games → <b>🔢 Number Bomb</b>. Pick the max (30 · 50 · 100 · 200); a bomb number hides between 1 and that number.', 'Pass the phone and enter one number each. Lower than the bomb → <b>⬆️ UP</b>, higher → <b>⬇️ DOWN</b> — the range shrinks and so does the fuse.', 'Hit the bomb number and it goes <b>💥 BOOM!</b> — a singed wolf gets dizzy 😵‍💫 and you <b>💀 drink</b>. "Play again" for a new round.'],
  'ja': ['🍀 運ゲーム → <b>🔢 数字爆弾</b>。最大の数字（30・50・100・200）を選んで始めると、1からその数字の間に爆弾の数字が1つ隠れます。', 'スマホを回して数字を1つずつ入力。爆弾より小さければ<b>⬆️ UP</b>、大きければ<b>⬇️ DOWN</b> — 範囲が狭まり、導火線も短くなります。', '爆弾の数字を押すと<b>💥ドカン！</b>焦げたオオカミがフラフラ😵‍💫 その人が<b>💀一気</b>。「もう一回」で新しいゲーム。'],
  'zh': ['🍀 运气游戏 → <b>🔢 数字炸弹</b>。选好最大数字（30 · 50 · 100 · 200）开始，1到这个数字之间藏着一个炸弹数字。', '手机轮流传，每人输入一个数字。比炸弹小 → <b>⬆️ UP</b>，比炸弹大 → <b>⬇️ DOWN</b> — 范围越来越小，导火线也越来越短。', '按到炸弹数字就 <b>💥 砰！</b> 烤焦的小狼晕乎乎 😵‍💫 那个人 <b>💀 干杯</b>。点“再来一局”重新开始。'],
  'th': ['🍀 เกมดวง → <b>🔢 ระเบิดตัวเลข</b> เลือกเลขสูงสุด (30 · 50 · 100 · 200) แล้วเริ่ม จะมีเลขระเบิดซ่อนอยู่ระหว่าง 1 ถึงเลขนั้น', 'ส่งมือถือวนกันแล้วใส่เลขทีละคน น้อยกว่าระเบิด → <b>⬆️ UP</b> มากกว่า → <b>⬇️ DOWN</b> ช่วงจะแคบลงและสายชนวนก็สั้นลง', 'กดโดนเลขระเบิด <b>💥 ตูม!</b> หมาป่าไหม้เกรียมมึนตึ้บ 😵‍💫 คนนั้น <b>💀 หมดแก้ว</b> กด "เล่นอีกครั้ง" เพื่อเริ่มใหม่'],
  'vi': ['🍀 Trò may rủi → <b>🔢 Bom số</b>. Chọn số lớn nhất (30 · 50 · 100 · 200); một số bom ẩn giữa 1 và số đó.', 'Chuyền điện thoại, mỗi người nhập một số. Nhỏ hơn bom → <b>⬆️ UP</b>, lớn hơn → <b>⬇️ DOWN</b> — khoảng số hẹp dần, ngòi nổ cũng ngắn dần.', 'Chạm đúng số bom là <b>💥 BÙM!</b> sói cháy xém choáng váng 😵‍💫 người đó <b>💀 cạn ly</b>. "Chơi lại" để bắt đầu ván mới.']}),
 'how-balloon': ('blTitle', '🎈', {
  'ko': ['🍀 운빨게임 → <b>🎈 풍선 불기</b>. 인원 정할 필요 없이 바로 시작해요.', '폰을 돌리면서 한 사람이 <b>1~3번</b> 화면을 터치하고 넘겨요. 누를 때마다 늑대 얼굴 풍선이 커져요.', '언제 터질지는 아무도 몰라요 (판마다 달라요). 풍선을 터뜨린 사람이 <b>💥 💀 원샷</b>. 화면을 누르면 새 풍선.'],
  'en': ['🍀 Luck Games → <b>🎈 Balloon Pump</b>. No player count — just start.', 'Pass the phone; each person taps <b>1–3 times</b> and passes it on. Every tap pumps up the wolf-face balloon.', 'Nobody knows when it pops (different every round). Whoever pops it — <b>💥 💀 drink</b>. Tap the screen for a new balloon.'],
  'ja': ['🍀 運ゲーム → <b>🎈 風船ふくらまし</b>。人数を決めずにすぐスタート。', 'スマホを回して1人<b>1〜3回</b>タッチしたら次の人へ。押すたびにオオカミ顔の風船がふくらみます。', 'いつ割れるかは誰にもわかりません（毎回ちがう）。割った人が<b>💥💀一気</b>。画面タップで新しい風船。'],
  'zh': ['🍀 运气游戏 → <b>🎈 吹气球</b>。不用选人数，直接开始。', '手机轮流传，每人点<b>1~3下</b>再传下去。每点一下，狼脸气球就大一点。', '谁也不知道什么时候爆（每局不一样）。把气球弄爆的人 <b>💥 💀 干杯</b>。点屏幕换新气球。'],
  'th': ['🍀 เกมดวง → <b>🎈 เป่าลูกโป่ง</b> ไม่ต้องเลือกจำนวนคน เริ่มได้เลย', 'ส่งมือถือวนกัน คนละ <b>1–3 ครั้ง</b> แล้วส่งต่อ แตะทีไรลูกโป่งหน้าหมาป่าก็ใหญ่ขึ้น', 'ไม่มีใครรู้ว่าจะแตกตอนไหน (แต่ละรอบไม่เหมือนกัน) ใครทำแตก <b>💥 💀 หมดแก้ว</b> แตะจอเพื่อเริ่มลูกใหม่'],
  'vi': ['🍀 Trò may rủi → <b>🎈 Thổi bóng bay</b>. Không cần chọn số người, bắt đầu luôn.', 'Chuyền điện thoại, mỗi người chạm <b>1–3 lần</b> rồi chuyền đi. Mỗi lần chạm, bóng mặt sói to thêm.', 'Không ai biết khi nào nổ (mỗi ván một khác). Ai làm nổ — <b>💥 💀 cạn ly</b>. Chạm màn hình để có bóng mới.']}),
 'how-phrase': ('phTitle', '🗣️', {
  'ko': ['메인 화면 <b>🗣️ 회화</b> 버튼. 먼저 <b>보여줄 언어</b>(상대방 나라 말)를 고르고, 아래 묶음 — 👋 인사 · 🚕 교통 · 🍜 식당 · 🏨 숙소 · 🍻 술집 · 🛍️ 쇼핑·도움 · 🤬 장난 욕 — 중 하나를 골라요.', '문장을 누르면 <b>흰 화면에 큰 글씨</b>로 상대방 말이 나와요 (아래엔 내 말 작게). ‹ › 로 같은 묶음 안에서 넘겨요.', '<b>🔊 들려주기</b>를 누르면 그 나라 말로 읽어줘요. 문장 95개, 인터넷 없이 돼요 (음성은 휴대폰에 그 언어 음성이 있어야 해요).'],
  'en': ['Tap <b>🗣️ Phrases</b> on the main screen. First pick the <b>language to show</b> (theirs), then a group — 👋 Hello · 🚕 Getting around · 🍜 Restaurant · 🏨 Hotel · 🍻 Bar · 🛍️ Shopping & help · 🤬 Swears.', 'Tap a phrase to show it <b>big on white</b> in their language (yours in small below). Use ‹ › to move within the group.', 'Tap <b>🔊 Play sound</b> to hear it read aloud. 95 phrases, all offline (sound needs that language\'s voice on the phone).'],
  'ja': ['メイン画面の<b>🗣️ 会話</b>ボタン。まず<b>見せる言語</b>（相手の国の言葉）を選び、下のグループ — 👋 あいさつ・🚕 交通・🍜 食堂・🏨 宿・🍻 飲み屋・🛍️ 買い物・助け・🤬 悪口 — から1つ選びます。', '文をタップすると<b>白い画面に大きな文字</b>で相手の言葉が出ます（下に自分の言葉を小さく）。‹ › で同じグループ内を移動。', '<b>🔊 読み上げる</b>でその国の言葉で読み上げ。全95文、ネットなしでOK（音声はスマホにその言語の音声が必要）。'],
  'zh': ['点主界面<b>🗣️ 会话</b>按钮。先选<b>显示语言</b>（对方的语言），再选一个分组 — 👋 问候 · 🚕 交通 · 🍜 餐厅 · 🏨 住宿 · 🍻 酒吧 · 🛍️ 购物·求助 · 🤬 脏话。', '点句子就会用对方的语言<b>白底大字</b>显示（下面小字是自己的语言）。用 ‹ › 在同一分组里切换。', '点<b>🔊 朗读</b>就用那种语言读出来。共95句，没网也能用（朗读需要手机里有该语言的语音）。'],
  'th': ['แตะปุ่ม <b>🗣️ ประโยค</b> ที่หน้าหลัก เลือก<b>ภาษาที่จะให้ดู</b> (ภาษาของอีกฝ่าย) ก่อน แล้วเลือกหมวด — 👋 ทักทาย · 🚕 เดินทาง · 🍜 ร้านอาหาร · 🏨 ที่พัก · 🍻 บาร์ · 🛍️ ช้อปปิ้ง·ขอความช่วยเหลือ · 🤬 คำด่า', 'แตะประโยคเพื่อแสดง<b>ตัวใหญ่บนพื้นขาว</b>เป็นภาษาของอีกฝ่าย (ข้างล่างเป็นภาษาเราตัวเล็ก) ใช้ ‹ › เลื่อนในหมวดเดียวกัน', 'แตะ <b>🔊 เปิดเสียง</b> เพื่อฟังการอ่านออกเสียง มีทั้งหมด 95 ประโยค ไม่ต้องใช้เน็ต (เสียงต้องมีเสียงภาษานั้นในมือถือ)'],
  'vi': ['Chạm nút <b>🗣️ Câu nói</b> ở màn hình chính. Chọn <b>ngôn ngữ hiển thị</b> (tiếng của người kia) trước, rồi chọn nhóm — 👋 Chào hỏi · 🚕 Đi lại · 🍜 Nhà hàng · 🏨 Khách sạn · 🍻 Quán nhậu · 🛍️ Mua sắm·Giúp đỡ · 🤬 Chửi vui.', 'Chạm một câu để hiện <b>chữ to trên nền trắng</b> bằng tiếng của họ (bên dưới là tiếng của mình, chữ nhỏ). Dùng ‹ › để chuyển trong cùng nhóm.', 'Chạm <b>🔊 Phát âm</b> để nghe đọc to. 95 câu, không cần mạng (phát âm cần có giọng đọc của ngôn ngữ đó trên điện thoại).']}),
 'how-settle': ('stTitle', '🧾', {
  'ko': ['메인 화면 <b>🧾 정산</b> 버튼. 같이 간 사람 이름을 넣어요 (2~12명, ➕ 사람 · ✕ 빼기).', '돈을 쓸 때마다 <b>금액 · 메모</b>를 넣고, <b>낸 사람</b>과 <b>같이 쓴 사람</b>을 고른 뒤 <b>➕ 쓴 돈 추가</b>.', '<b>정산 결과</b>에 "A → B 얼마"처럼 누가 누구한테 얼마 보내면 되는지 나와요 (사람별 쓴 몫도). 기록은 이 휴대폰에만 저장돼요.'],
  'en': ['Tap <b>🧾 Split bill</b> on the main screen. Enter the travelers\' names (2–12, ➕ Person · ✕ to remove).', 'For each expense, enter the <b>amount · note</b>, pick <b>Paid by</b> and <b>Split between</b>, then <b>➕ Add expense</b>.', '<b>Who pays whom</b> shows lines like "A → B amount" (plus everyone\'s share). Everything stays only on this phone.'],
  'ja': ['メイン画面の<b>🧾 精算</b>ボタン。一緒に行く人の名前を入れます（2〜12人、➕ 人・✕ で削除）。', 'お金を使うたびに<b>金額・メモ</b>を入れ、<b>払った人</b>と<b>一緒に使った人</b>を選んで<b>➕ 支出を追加</b>。', '<b>精算結果</b>に「A → B いくら」のように誰が誰にいくら送ればいいか表示（一人ずつの使った分も）。記録はこのスマホだけに保存。'],
  'zh': ['点主界面<b>🧾 结算</b>按钮。输入同行人的名字（2~12人，➕ 添加 · ✕ 删除）。', '每次花钱都输入<b>金额 · 备注</b>，选好<b>付款人</b>和<b>一起分摊的人</b>，再点<b>➕ 添加支出</b>。', '<b>结算结果</b>会显示“A → B 多少钱”，告诉你谁该转给谁多少（还有每人应摊的钱）。记录只保存在这部手机里。'],
  'th': ['แตะปุ่ม <b>🧾 แบ่งจ่าย</b> ที่หน้าหลัก ใส่ชื่อคนที่ไปด้วยกัน (2–12 คน ➕ คน · ✕ เพื่อลบ)', 'ทุกครั้งที่จ่ายเงิน ใส่<b>จำนวนเงิน · โน้ต</b> เลือก<b>คนจ่าย</b>และ<b>หารกับใคร</b> แล้วแตะ <b>➕ เพิ่มค่าใช้จ่าย</b>', '<b>ผลการแบ่งจ่าย</b>จะบอกเป็น "A → B เท่าไหร่" ว่าใครต้องโอนให้ใคร (พร้อมส่วนที่แต่ละคนใช้) ข้อมูลเก็บในมือถือเครื่องนี้เท่านั้น'],
  'vi': ['Chạm nút <b>🧾 Chia tiền</b> ở màn hình chính. Nhập tên những người đi cùng (2–12 người, ➕ Người · ✕ để bỏ).', 'Mỗi lần chi tiền, nhập <b>số tiền · ghi chú</b>, chọn <b>Người trả</b> và <b>Chia cho</b>, rồi chạm <b>➕ Thêm khoản chi</b>.', '<b>Kết quả</b> hiện dạng "A → B bao nhiêu" để biết ai chuyển cho ai bao nhiêu (kèm phần chi của từng người). Mọi thứ chỉ lưu trên điện thoại này.']}),
}
AFTER = {'how-nbomb': 'how-plinko', 'how-balloon': 'how-nbomb', 'how-phrase': 'how-hotel', 'how-settle': 'how-phrase'}

keys = list(m.HOW_KEYS); emo = list(m.HOW_EMOJI); steps = {L: list(m.HOW[L]['steps']) for L in LANGS}
for shot, after in AFTER.items():
    if any(k[0] == shot for k in keys): continue
    i = [k[0] for k in keys].index(after) + 1
    key, e, txt = NEW[shot]
    keys.insert(i, (shot, key)); emo.insert(i, e)
    for L in LANGS: steps[L].insert(i, txt[L])
# ☝️ 손가락선택: 운빨게임 안으로 (풍선 불기 다음)
fi = [k[0] for k in keys].index('how-finger')
fk, fe = keys.pop(fi), emo.pop(fi); fs = {L: steps[L].pop(fi) for L in LANGS}
ti = [k[0] for k in keys].index('how-balloon') + 1
keys.insert(ti, fk); emo.insert(ti, fe)
for L in LANGS:
    s = strings(L); st = list(fs[L])
    if '→' not in st[0]: st[0] = f"🍀 {s['menuLuck']} → <b>☝️ {s['fingerTitle']}</b>. " + st[0]
    steps[L].insert(ti, st)
for L in LANGS: m.HOW[L]['steps'] = steps[L]

# v1.7 구성 · 수정 내용
rel = CHANGELOG[0]; assert rel['ver'] == '1.7'
for L in LANGS:
    P = m.PAGE[L]; s = strings(L)
    for k in ['navNew', 'mainP', 'hNew', 'newIntro']: P[k] = P[k].replace('v1.6', 'v1.7')
    P['changes'] = [tx[L] for _, tx in rel['items']]
    feat = list(P['newFeat'])
    old16 = feat[1]; assert old16.startswith('🆕')
    feat[1] = '🆕 ' + ' · '.join([f"🔢 {s['nbTitle']}", f"🎈 {s['blTitle']}", f"🗣️ {s['phTitle']}", f"🧾 {s['stTitle']}"])
    feat[2] = '✨ v1.6: ' + old16[2:].strip()
    # 손가락게임 카테고리 없어짐: "👆 … · " 앞부분 빼기
    feat = [re.split(r'\s*[·・、,，]\s*', x, maxsplit=1)[-1] if x.startswith('👆') else x for x in feat]
    P['newFeat'] = feat

head = open('tools/manual_text.py', encoding='utf-8').read().split('\nPAGE = {')[0]
pp = lambda o: pprint.pformat(o, width=200, sort_dicts=False)
out = head + '\nPAGE = ' + pp(m.PAGE) + '\n\n# 단계 그림 속 폰 화면 문구\nSHOT = ' + pp(m.SHOT) + '\n\n# 게임 방법: (그림 이름, 제목으로 쓸 앱 문구 키, 설명 3줄)\nHOW_KEYS = ' + repr(keys) + '\nHOW_EMOJI = ' + repr(emo) + '\n\nHOW = ' + pp(m.HOW) + '\n'
open('tools/manual_text.py', 'w', encoding='utf-8').write(out)
print('ok', [k[0] for k in keys])
