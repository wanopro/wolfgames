"""v1.5 설명서 반영: 새 게임 방법 7개 + v1.5 구성/수정 내용 (6개 언어). 한 번만 실행"""
import sys, pprint
sys.path.insert(0, 'tools')
import manual_text as m
from changelog import CHANGELOG
import json, re

LANGS = ['ko', 'en', 'ja', 'zh', 'th', 'vi']
# 앱 문구 (Strings.kt → web/i18n.js 에서 읽음)
src = open('web/i18n.js', encoding='utf-8').read()
def strings(lang):
    st = src.index(f'  {lang}: {{'); en = src.index('\n  },', st)
    out = {}
    for mm in re.finditer(r'^    (\w+): ("(?:[^"\\]|\\.)*"),?$', src[st:en], re.M): out[mm.group(1)] = json.loads(mm.group(2))
    return out

def kings_rules(lang):
    s = strings(lang); names = s['kcNames'].split('|'); ranks = ['A', '2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K']
    ic = ['🌊', '👉', '🙋', '🚽', '💃', '🍻', '🙌', '🤝', '🔤', '📚', '📜', '❓', '👑']
    return ' · '.join(f'{r} {i} {n}' for r, i, n in zip(ranks, ic, names))

NEW = {  # 그림 이름: (앱 문구 키, 이모지, {언어: 3줄})
 'how-kings': ('kcTitle', '👑', {
  'ko': ['카드 더미를 터치하면 한 장씩 뒤집혀 나와요. 카드 한가운데 큰 그림이 할 일이에요.', '{R}. 아래 <b>📋 규칙 보기</b>에서 언제든 확인!', 'K가 나올 때마다 오른쪽 위 잔이 차올라요. <b>4번째 K를 뽑은 사람이 킹스컵 원샷!</b> <b>🔀 새로 섞기</b>로 새 판.'],
  'en': ['Tap the deck to flip one card at a time. The big picture in the middle tells you what to do.', '{R}. Check them any time with <b>📋 Rules</b> at the bottom.', "Each King fills the cup at the top right. <b>Whoever draws the 4th King drinks the King's Cup!</b> <b>🔀 Reshuffle</b> starts over."],
  'ja': ['山札をタップすると1枚ずつめくれて出てきます。カード真ん中の大きな絵がやることです。', '{R}。下の<b>📋 ルール</b>でいつでも確認！', 'Kが出るたびに右上の杯が満ちていきます。<b>4枚目のKを引いた人がキングスカップを一気！</b> <b>🔀 シャッフル</b>で新しいゲーム。'],
  'zh': ['点牌堆一张一张翻开。牌中间的大图就是要做的事。', '{R}。随时可以点下面的<b>📋 规则</b>查看！', '每出一张K右上角的杯子就满一点。<b>抽到第4张K的人把国王杯干了！</b> <b>🔀 重新洗牌</b>开新局。'],
  'th': ['แตะกองไพ่เพื่อเปิดทีละใบ รูปใหญ่ตรงกลางไพ่คือสิ่งที่ต้องทำ', '{R} ดูได้ตลอดที่ <b>📋 กติกา</b> ด้านล่าง!', 'ทุกครั้งที่ได้ K แก้วมุมขวาบนจะเต็มขึ้น <b>ใครจั่ว K ใบที่ 4 หมดแก้วคิงส์คัพ!</b> <b>🔀 สับไพ่ใหม่</b> เพื่อเริ่มใหม่'],
  'vi': ['Chạm vào chồng bài để lật từng lá. Hình lớn ở giữa lá bài là việc cần làm.', '{R}. Xem lại bất cứ lúc nào ở <b>📋 Luật</b> bên dưới!', "Mỗi lá K làm ly ở góc phải đầy thêm. <b>Ai rút lá K thứ 4 phải uống cạn King's Cup!</b> <b>🔀 Xáo lại</b> để chơi ván mới."]}),
 'how-beer': ('beerTitle', '🍺', {
  'ko': ['인원(2~6명)을 정하고 폰을 가운데 둬요. 화면이 인원수만큼 나뉘고 각자 맥주잔이 하나씩 생겨요.', '맥주가 점점 빨리 차올라요. 빨간 점선 <b>"여기까지!"</b>에 맞춰서 내 영역을 터치하면 멈춰요.', '선에 제일 가깝게 멈춘 사람 <b>👑 1등</b>, 선을 넘기면 <b>💦 넘쳤다 → 💀 꼴찌</b>. 가운데 원을 누르면 다시.'],
  'en': ['Choose 2–6 players and put the phone in the middle. The screen splits and everyone gets a beer glass.', 'The beer fills faster and faster. Tap your area to stop it right at the red dashed line <b>"Stop here!"</b>.', 'Closest to the line is <b>👑 1st</b>; go past it and it <b>💦 overflows → 💀 last</b>. Tap the center to play again.'],
  'ja': ['人数（2〜6人）を決めてスマホを真ん中に置きます。画面が人数分に分かれ、それぞれジョッキが1つずつ。', 'ビールがどんどん速く注がれます。赤い点線<b>「ここまで！」</b>に合わせて自分のエリアをタップして止めます。', '線に一番近い人が<b>👑 1位</b>、線を超えると<b>💦 あふれた → 💀 ビリ</b>。真ん中の円でもう一度。'],
  'zh': ['选人数（2~6人），把手机放中间。屏幕按人数分开，每人一个啤酒杯。', '啤酒越倒越快。对准红色虚线<b>「到这里！」</b>点自己的区域停下。', '最接近线的人<b>👑 第一</b>，超过线就<b>💦 溢出 → 💀 垫底</b>。点中间的圆再来一局。'],
  'th': ['เลือกผู้เล่น 2–6 คน วางมือถือไว้ตรงกลาง หน้าจอแบ่งตามจำนวนคน ทุกคนมีแก้วเบียร์ของตัวเอง', 'เบียร์จะเติมเร็วขึ้นเรื่อยๆ แตะช่องของตัวเองให้หยุดตรงเส้นประสีแดง <b>"ถึงตรงนี้!"</b>', 'ใกล้เส้นที่สุด <b>👑 ที่ 1</b> เลยเส้น <b>💦 ล้น → 💀 ที่โหล่</b> แตะวงกลมตรงกลางเพื่อเล่นใหม่'],
  'vi': ['Chọn 2–6 người và đặt điện thoại ở giữa. Màn hình chia theo số người, mỗi người một ly bia.', 'Bia rót ngày càng nhanh. Chạm phần của mình để dừng đúng vạch đỏ <b>"Đến đây!"</b>.', 'Gần vạch nhất là <b>👑 nhất</b>, vượt vạch là <b>💦 tràn → 💀 bét</b>. Chạm vòng tròn giữa để chơi lại.']}),
 'how-coin': ('coinTitle', 'icons/coin.png', {
  'ko': ['인원(2~8명)을 정하고 폰을 가운데 둬요. 각자 자리에서 <b>앞(🐺 늑대 얼굴)</b>이나 <b>뒤(🍑 늑대 엉덩이)</b>를 눌러요.', '모두 고르면 3·2·1 뒤 동전이 가운데서 튀어 올라 빙글빙글 돌다가 떨어져요.', '맞춘 사람 <b>⭕</b>, 틀린 사람 <b>❌ 원샷</b>. 가끔(15%) 동전이 <b>세로로 서면 전원 틀림!</b> 가운데 원을 누르면 다시.'],
  'en': ['Choose 2–8 players and put the phone in the middle. In your spot, tap <b>Heads (🐺 wolf face)</b> or <b>Tails (🍑 wolf butt)</b>.', 'When everyone has picked, after 3·2·1 the coin flips up from the center, spins and lands.', 'Right = <b>⭕</b>, wrong = <b>❌ drink</b>. Sometimes (15%) the coin <b>lands on its edge — everyone is wrong!</b> Tap the center to play again.'],
  'ja': ['人数（2〜8人）を決めてスマホを真ん中に置きます。自分の場所で<b>表（🐺 オオカミの顔）</b>か<b>裏（🍑 おしり）</b>をタップ。', 'みんな選んだら3・2・1のあと、コインが真ん中から跳ね上がってくるくる回って落ちます。', '当たりは<b>⭕</b>、はずれは<b>❌ 一気</b>。ときどき（15%）コインが<b>立つと全員はずれ！</b> 真ん中の円でもう一度。'],
  'zh': ['选人数（2~8人），把手机放中间。在自己的位置点<b>正面（🐺 狼脸）</b>或<b>反面（🍑 狼屁股）</b>。', '都选好后 3·2·1，硬币从中间弹起，旋转后落下。', '猜对<b>⭕</b>，猜错<b>❌ 干杯</b>。有时（15%）硬币<b>立起来，全员猜错！</b> 点中间的圆再来一局。'],
  'th': ['เลือกผู้เล่น 2–8 คน วางมือถือไว้ตรงกลาง ในช่องตัวเองแตะ <b>หัว (🐺 หน้าหมาป่า)</b> หรือ <b>ก้อย (🍑 ก้นหมาป่า)</b>', 'พอเลือกครบ 3·2·1 เหรียญจะเด้งขึ้นจากตรงกลาง หมุนติ้วแล้วตกลง', 'ทายถูก <b>⭕</b> ทายผิด <b>❌ หมดแก้ว</b> บางครั้ง (15%) เหรียญ<b>ตั้งขึ้น ผิดทุกคน!</b> แตะวงกลมตรงกลางเพื่อเล่นใหม่'],
  'vi': ['Chọn 2–8 người và đặt điện thoại ở giữa. Ở chỗ của mình, chạm <b>Sấp (🐺 mặt sói)</b> hoặc <b>Ngửa (🍑 mông sói)</b>.', 'Khi tất cả đã chọn, sau 3·2·1 đồng xu bật lên từ giữa, xoay tít rồi rơi xuống.', 'Đúng <b>⭕</b>, sai <b>❌ cạn ly</b>. Thỉnh thoảng (15%) đồng xu <b>đứng thẳng — tất cả đều sai!</b> Chạm vòng tròn giữa để chơi lại.']}),
 'how-tug': ('tugTitle', '🎀', {
  'ko': ['2명이 폰을 사이에 두고 위·아래로 마주 앉아요.', '<b>GO!</b> 하면 내 쪽 화면을 미친 듯이 연타! 누를수록 가운데 깃발(🎀)이 상대 쪽으로 밀려가요.', '깃발을 <b>상대편 끝 점선</b>까지 밀면 승리 👑, 진 쪽엔 늑대가 콱! 가운데 🔄를 누르면 다시.'],
  'en': ['Two players sit facing each other with the phone between them.', 'On <b>GO!</b>, mash your half of the screen! Every tap pushes the flag (🎀) toward the other side.', 'Push the flag to the <b>dashed line on their side</b> to win 👑 — the wolf bites the loser! Tap 🔄 in the center to play again.'],
  'ja': ['2人でスマホをはさんで上下に向かい合って座ります。', '<b>GO!</b>で自分側の画面をひたすら連打！押すほど真ん中の旗(🎀)が相手側へ。', '旗を<b>相手側の点線</b>まで押したら勝ち👑、負けた側にはオオカミがガブッ！真ん中の🔄でもう一度。'],
  'zh': ['两个人隔着手机上下面对面坐。', '喊<b>GO!</b>后狂点自己这一半！点得越多，中间的旗(🎀)越往对方推。', '把旗推到<b>对方那边的虚线</b>就赢👑，输的一方被小狼咬一口！点中间🔄再来。'],
  'th': ['สองคนนั่งหันหน้าเข้าหากัน วางมือถือไว้ตรงกลาง', 'พอ <b>GO!</b> แตะรัวๆ ฝั่งตัวเอง! ยิ่งแตะ ธง (🎀) ยิ่งถูกดันไปฝั่งตรงข้าม', 'ดันธงถึง<b>เส้นประฝั่งตรงข้าม</b>ชนะ 👑 ฝั่งแพ้โดนหมาป่างับ! แตะ 🔄 ตรงกลางเพื่อเล่นใหม่'],
  'vi': ['Hai người ngồi đối diện, điện thoại ở giữa.', 'Khi <b>GO!</b> hãy chạm thật nhanh vào nửa của mình! Càng chạm, lá cờ (🎀) càng bị đẩy sang đối thủ.', 'Đẩy cờ tới <b>vạch đứt bên đối thủ</b> là thắng 👑 — sói ngoạm người thua! Chạm 🔄 ở giữa để chơi lại.']}),
 'how-slap': ('slapTitle', '🐾', {
  'ko': ['2명이 위·아래로 마주 앉아요. 가운데에 늑대 앞발 두 개가 겹쳐 있어요.', '<b>🛡️ 수비</b>는 내 쪽 화면을 꾹 누르고 있다가 공격이 오면 떼서 피하기, <b>🐾 공격</b>은 아무 때나 내 쪽을 터치하면 찰싹!', '맞으면 수비 원샷, 피하면 공수 교대. 공격이 안 왔는데 떼면 <b>움찔 반칙</b>으로 원샷! 💥 숫자는 맞힌 횟수예요.'],
  'en': ['Two players sit facing each other. Two wolf paws overlap in the middle.', 'The <b>🛡️ defender</b> keeps a finger pressed on their half and lifts it to dodge; the <b>🐾 attacker</b> taps their half any time to slap!', 'Hit = defender drinks, dodge = swap roles. Lift without an attack = <b>flinch</b>, drink! 💥 counts the hits.'],
  'ja': ['2人で上下に向かい合って座ります。真ん中にオオカミの前足が2つ重なっています。', '<b>🛡️ 守り</b>は自分側を指で押さえておき攻撃が来たら離してよける、<b>🐾 攻め</b>はいつでも自分側をタップでバチン！', '当たったら守りが一気、よけたら攻守交代。攻撃が来てないのに離したら<b>ビクッ反則</b>で一気！💥は当てた回数。'],
  'zh': ['两个人上下面对面坐。中间有两只狼爪叠在一起。', '<b>🛡️ 防守</b>一直按住自己这一半，攻击来了就松开躲；<b>🐾 进攻</b>随时点自己这一半就拍下去！', '拍中防守干杯，躲开就攻守互换。没攻击就松手算<b>抖了犯规</b>，干杯！💥是拍中次数。'],
  'th': ['สองคนนั่งหันหน้าเข้าหากัน ตรงกลางมีอุ้งเท้าหมาป่าสองข้างซ้อนกัน', '<b>🛡️ ฝ่ายรับ</b> กดฝั่งตัวเองค้างไว้ พอโดนโจมตีให้ยกหลบ <b>🐾 ฝ่ายรุก</b> แตะฝั่งตัวเองเมื่อไหร่ก็ได้เพื่อตบ!', 'โดนตบฝ่ายรับหมดแก้ว หลบได้สลับฝั่ง ยกนิ้วทั้งที่ยังไม่โดนโจมตี = <b>สะดุ้ง</b> หมดแก้ว! 💥 คือจำนวนครั้งที่ตบโดน'],
  'vi': ['Hai người ngồi đối diện. Ở giữa có hai chân sói chồng lên nhau.', '<b>🛡️ Phòng thủ</b> giữ ngón tay trên nửa của mình, bị đánh thì nhấc lên để né; <b>🐾 Tấn công</b> chạm nửa của mình bất cứ lúc nào để đập!', 'Trúng = phòng thủ cạn ly, né được = đổi vai. Nhấc tay khi chưa bị đánh = <b>giật mình</b>, cạn ly! 💥 là số lần đập trúng.']}),
 'how-qr': ('qrTitle', '▦', {
  'ko': ['메인 화면 로고 옆 <b>▦ QR</b>을 눌러요. 처음엔 인스타·왓츠앱·틱톡·라인·카톡·텔레그램·페이스북·잘로 중 쓰는 걸 입력하고 저장해요.', '다음부터는 누르면 바로 <b>흰 배경 풀화면 QR</b>이 나와요. 여러 개면 위 버튼을 누르거나 옆으로 넘겨서 바꿔요.', '상대가 폰 카메라로 찍으면 내 프로필·채팅이 바로 열려요. 입력한 정보는 <b>내 폰에만 저장</b>되고, 앱에서는 화면이 최대 밝기가 돼요.'],
  'en': ['Tap <b>▦ QR</b> next to the logo on the main screen. The first time, enter the ones you use (Instagram, WhatsApp, TikTok, LINE, KakaoTalk, Telegram, Facebook, Zalo) and save.', 'After that, it opens a <b>full-screen QR on white</b> right away. With several, tap the buttons on top or swipe sideways.', 'Others scan it with their phone camera to open your profile or chat. It is <b>saved only on your phone</b>, and the app turns the screen to full brightness.'],
  'ja': ['メイン画面のロゴ横の<b>▦ QR</b>をタップ。初回はインスタ・WhatsApp・TikTok・LINE・カカオ・Telegram・Facebook・Zaloの中から使うものを入力して保存。', '次からはタップするとすぐ<b>白背景の全画面QR</b>が出ます。複数あるときは上のボタンか横スワイプで切り替え。', '相手がスマホのカメラで読み取ると、自分のプロフィールやチャットがすぐ開きます。情報は<b>自分のスマホだけに保存</b>、アプリでは画面が最大の明るさに。'],
  'zh': ['点主界面标志旁的<b>▦ QR</b>。第一次先输入常用的（Instagram、WhatsApp、TikTok、LINE、KakaoTalk、Telegram、Facebook、Zalo）并保存。', '之后点一下就直接出现<b>白底全屏二维码</b>。有多个时点上面的按钮或左右滑动切换。', '对方用手机相机一扫就能打开你的主页或聊天。信息<b>只保存在你的手机上</b>，App 里屏幕会调到最亮。'],
  'th': ['แตะ <b>▦ QR</b> ข้างโลโก้ที่หน้าหลัก ครั้งแรกให้ใส่ที่ใช้ (IG, WhatsApp, TikTok, LINE, KakaoTalk, Telegram, Facebook, Zalo) แล้วบันทึก', 'ครั้งต่อไปแตะแล้วจะขึ้น <b>QR เต็มจอพื้นขาว</b> ทันที ถ้ามีหลายอันให้แตะปุ่มด้านบนหรือปัดซ้ายขวา', 'อีกฝ่ายสแกนด้วยกล้องมือถือก็เปิดโปรไฟล์หรือแชทของเราได้เลย ข้อมูล<b>เก็บไว้ในมือถือเราเท่านั้น</b> ในแอปหน้าจอจะสว่างสุด'],
  'vi': ['Chạm <b>▦ QR</b> cạnh logo ở màn hình chính. Lần đầu, nhập những mạng bạn dùng (Instagram, WhatsApp, TikTok, LINE, KakaoTalk, Telegram, Facebook, Zalo) rồi lưu.', 'Từ lần sau, chạm là hiện ngay <b>QR toàn màn hình nền trắng</b>. Có nhiều thì chạm nút phía trên hoặc vuốt ngang.', 'Người khác quét bằng camera điện thoại là mở ngay trang cá nhân hoặc chat của bạn. Thông tin <b>chỉ lưu trên điện thoại của bạn</b>, ứng dụng sẽ tăng độ sáng tối đa.']}),
 'how-tip': ('tipBtn', '', {
  'ko': ['계산기에서 금액을 입력하고 <b>💁 팁</b>을 눌러요 (🍻 N빵과 💱 환전 사이).', '<b>5 · 10 · 15 · 20 · 25 · 30%</b> 중 하나를 고르면 팁 금액과 <b>팁 포함 합계</b>가 나와요. 고른 %는 기억해 둬요.', '<b>🍻 팁 포함 N빵</b>을 누르면 팁까지 합친 금액으로 바로 나눌 수 있어요.'],
  'en': ['Enter the amount in the calculator and tap <b>💁 Tip</b> (between 🍻 Split and 💱 Exchange).', 'Pick <b>5 · 10 · 15 · 20 · 25 · 30%</b> to see the tip and the <b>total with tip</b>. Your last pick is remembered.', 'Tap <b>🍻 Split with tip</b> to split the total including the tip right away.'],
  'ja': ['電卓で金額を入れて<b>💁 チップ</b>をタップ（🍻 割り勘と💱 両替の間）。', '<b>5・10・15・20・25・30%</b>から選ぶとチップ額と<b>チップ込み合計</b>が出ます。選んだ%は覚えておきます。', '<b>🍻 チップ込みで割り勘</b>でチップを足した金額をそのまま割り勘できます。'],
  'zh': ['在计算器输入金额，点<b>💁 小费</b>（🍻 AA 和 💱 汇率之间）。', '选<b>5 · 10 · 15 · 20 · 25 · 30%</b>就显示小费和<b>含小费合计</b>。会记住上次选的%。', '点<b>🍻 含小费AA</b>，直接按加上小费的金额平分。'],
  'th': ['ใส่จำนวนเงินในเครื่องคิดเลขแล้วแตะ <b>💁 ทิป</b> (ระหว่าง 🍻 หารบิล กับ 💱 แลกเงิน)', 'เลือก <b>5 · 10 · 15 · 20 · 25 · 30%</b> จะเห็นทิปและ<b>ยอดรวมทิป</b> จำ % ที่เลือกล่าสุดไว้', 'แตะ <b>🍻 หารบิลรวมทิป</b> เพื่อหารยอดที่รวมทิปแล้วได้เลย'],
  'vi': ['Nhập số tiền trong máy tính rồi chạm <b>💁 Tip</b> (giữa 🍻 Chia tiền và 💱 Đổi tiền).', 'Chọn <b>5 · 10 · 15 · 20 · 25 · 30%</b> để xem tiền tip và <b>tổng kèm tip</b>. Lựa chọn gần nhất được ghi nhớ.', 'Chạm <b>🍻 Chia kèm tip</b> để chia luôn tổng tiền đã cộng tip.']}),
}
# 어디 뒤에 넣을지 (메인 화면 카테고리 순서)
AFTER = {'how-kings': 'how-hcm', 'how-beer': 'how-stop', 'how-coin': 'how-bomb', 'how-tug': 'how-four', 'how-slap': 'how-tug', 'how-tip': 'how-split', 'how-qr': 'how-fx'}

keys = list(m.HOW_KEYS); emo = list(m.HOW_EMOJI); steps = {L: list(m.HOW[L]['steps']) for L in LANGS}
for shot, after in AFTER.items():
    if any(k[0] == shot for k in keys): continue
    i = [k[0] for k in keys].index(after) + 1
    key, e, txt = NEW[shot]
    keys.insert(i, (shot, key)); emo.insert(i, e)
    for L in LANGS:
        lines = [x.replace('{R}', kings_rules(L)) for x in txt[L]]
        steps[L].insert(i, lines)
emo[[k[0] for k in keys].index('how-ladder')] = 'icons/ladder.png'
for L in LANGS: m.HOW[L]['steps'] = steps[L]

# v1.5 구성 · 수정 내용
rel = CHANGELOG[0]; assert rel['ver'] == '1.5'
for L in LANGS:
    P = m.PAGE[L]; s = strings(L)
    for k in ['navNew', 'mainP', 'hNew', 'newIntro']: P[k] = P[k].replace('v1.4', 'v1.5')
    P['changes'] = [tx[L] for _, tx in rel['items']]
    feat = [x for x in P['newFeat'] if not x.startswith('🆕') and not x.startswith('▦')]
    feat.insert(1, '🆕 ' + ' · '.join([f"🎀 {s['tugTitle']}", f"🐾 {s['slapTitle']}", f"👑 {s['kcTitle']}", f"🍺 {s['beerTitle']}", f"🪙 {s['coinTitle']}"]))
    feat.insert(len(feat) - 3, f"▦ {s['qrTitle']} · {s['tipBtn']}")
    P['newFeat'] = feat

head = open('tools/manual_text.py', encoding='utf-8').read().split('\nPAGE = {')[0]
pp = lambda o: pprint.pformat(o, width=200, sort_dicts=False)
out = head + '\nPAGE = ' + pp(m.PAGE) + '\n\n# 단계 그림 속 폰 화면 문구\nSHOT = ' + pp(m.SHOT) + '\n\n# 게임 방법: (그림 이름, 제목으로 쓸 앱 문구 키, 설명 3줄)\nHOW_KEYS = ' + repr(keys) + '\nHOW_EMOJI = ' + repr(emo) + '\n\nHOW = ' + pp(m.HOW) + '\n'
open('tools/manual_text.py', 'w', encoding='utf-8').write(out)
print('ok', len(keys), len(emo), [len(steps[L]) for L in LANGS])
