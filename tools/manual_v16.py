"""v1.6 설명서 반영: 새 게임·기능 방법 5개 + v1.6 구성/수정 내용 (6개 언어). 한 번만 실행"""
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

NEW = {  # 그림 이름: (앱 문구 키, 이모지, {언어: 3줄})
 'how-plinko': ('plinkoTitle', '🔻', {
  'ko': ['🍀 운빨게임 → <b>🔻 플링코</b>. 인원 정할 필요 없이 바로 시작해요.', '위쪽 아무 데나 터치하면 그 자리에서 늑대 코인이 떨어져요. 핀 사이를 톡톡 튕기다가 아래 칸에 쏙!', '들어간 칸이 걸려요: <b>1잔 · 반 잔 · 다시 · 오른쪽 사람 · 왼쪽 사람 · 다 같이 · 통과</b>. 칸 순서는 판마다 <b>🔀 섞여요</b>.'],
  'en': ['🍀 Luck Games → <b>🔻 Plinko</b>. No player count — just start.', 'Tap anywhere at the top and a wolf coin drops from there, bouncing off the pins into a slot below.', 'Whatever slot it lands in is yours: <b>1 drink · Half · Again · Right person · Left person · Everyone · Pass</b>. The slots <b>🔀 shuffle</b> every round.'],
  'ja': ['🍀 運ゲーム → <b>🔻 プリンコ</b>。人数を決めずにすぐスタート。', '上のどこかをタッチするとそこからオオカミコインが落ちて、ピンの間を跳ねながら下のマスにスポッ！', '入ったマスが当たり：<b>1杯・半分・もう一回・右の人・左の人・みんな・セーフ</b>。マスの順番は毎回<b>🔀 シャッフル</b>。'],
  'zh': ['🍀 运气游戏 → <b>🔻 弹珠台</b>。不用选人数，直接开始。', '点上方任意位置，狼币就从那里落下，在钉子间弹来弹去掉进下面的格子！', '落进哪个格子就是哪个：<b>1杯 · 半杯 · 再来 · 右边的人 · 左边的人 · 大家一起 · 过</b>。格子顺序每局<b>🔀 打乱</b>。'],
  'th': ['🍀 เกมดวง → <b>🔻 พลิงโก</b> ไม่ต้องเลือกจำนวนคน เริ่มได้เลย', 'แตะตรงไหนก็ได้ด้านบน เหรียญหมาป่าจะตกจากตรงนั้น เด้งผ่านหมุดลงไปในช่องด้านล่าง!', 'ตกช่องไหนได้ช่องนั้น: <b>1 แก้ว · ครึ่งแก้ว · อีกรอบ · คนขวา · คนซ้าย · ทุกคน · รอด</b> ลำดับช่อง<b>🔀 สลับ</b>ทุกรอบ'],
  'vi': ['🍀 Trò may rủi → <b>🔻 Plinko</b>. Không cần chọn số người, bắt đầu luôn.', 'Chạm bất kỳ đâu phía trên, đồng xu sói rơi từ đó, nảy qua các chốt rồi lọt vào ô bên dưới!', 'Rơi vào ô nào thì trúng ô đó: <b>1 ly · Nửa ly · Lại · Người bên phải · Người bên trái · Cả bàn · Qua</b>. Thứ tự ô <b>🔀 đổi</b> mỗi ván.']}),
 'how-ttt': ('tttTitle', '🟢', {
  'ko': ['⚔️ 대결게임 → <b>🟢 삼목</b>. 2명이 폰을 사이에 두고 위·아래로 마주 앉아요 (아래 = 초록, 위 = 빨강). 각자 쪽에 <b>"나: 초록"</b>처럼 내 색이 보여요.', '번갈아 늑대 코인을 놓아요. <b>한 수에 2초</b> — 넘기면 차례가 넘어가요. 가로·세로·대각선 3개를 먼저 이으면 승리!', '코인은 <b>한 사람 3개까지</b>. 4번째를 놓으면 내 가장 오래된 코인이 사라져요 (사라질 코인은 깜빡여요). 진 사람 원샷, 화면을 누르면 다시.'],
  'en': ['⚔️ Versus Games → <b>🟢 Tic-Tac-Toe</b>. Two players sit facing each other (bottom = green, top = red). Each side shows your color, like <b>"You: Green"</b>.', 'Take turns placing wolf coins. <b>2 seconds a move</b> — run out and your turn passes. Three in a row wins!', '<b>Only 3 coins each</b>. Placing a 4th removes your oldest one (it blinks first). Loser drinks; tap the screen to play again.'],
  'ja': ['⚔️ 対戦ゲーム → <b>🟢 三目並べ</b>。2人でスマホをはさんで上下に向かい合います（下＝緑、上＝赤）。それぞれの側に<b>「あなた: 緑」</b>のように自分の色が出ます。', '交互にオオカミコインを置きます。<b>1手2秒</b>、過ぎると相手の番。縦・横・斜めに3つそろえたら勝ち！', 'コインは<b>1人3個まで</b>。4個目を置くと自分の一番古いコインが消えます（消えるコインは点滅）。負けたら一気、画面タップでもう一度。'],
  'zh': ['⚔️ 对战游戏 → <b>🟢 井字棋</b>。两个人隔着手机上下面对面坐（下 = 绿，上 = 红）。各自一边会显示<b>“你: 绿”</b>这样的颜色。', '轮流放狼币。<b>每步2秒</b>，超时就轮到对方。横、竖、斜先连成3个就赢！', '<b>每人最多3个</b>。放第4个时，自己最早的那个会消失（要消失的会闪烁）。输的人干杯，点屏幕再来。'],
  'th': ['⚔️ เกมดวล → <b>🟢 โอเอ็กซ์</b> สองคนนั่งหันหน้าเข้าหากัน (ล่าง = เขียว บน = แดง) แต่ละฝั่งแสดงสีของตัวเอง เช่น <b>"คุณ: เขียว"</b>', 'ผลัดกันวางเหรียญหมาป่า <b>ตาละ 2 วิ</b> หมดเวลาเสียตา เรียง 3 อันก่อนชนะ!', '<b>คนละ 3 เหรียญ</b> วางอันที่ 4 เหรียญเก่าสุดของเราจะหายไป (อันที่จะหายจะกะพริบ) คนแพ้หมดแก้ว แตะจอเพื่อเล่นใหม่'],
  'vi': ['⚔️ Trò đối kháng → <b>🟢 Cờ ba</b>. Hai người ngồi đối diện (dưới = xanh, trên = đỏ). Mỗi bên hiện màu của mình, như <b>"Bạn: Xanh"</b>.', 'Lần lượt đặt đồng xu sói. <b>2 giây một lượt</b> — hết giờ thì mất lượt. Nối 3 hàng trước là thắng!', '<b>Mỗi người chỉ 3 xu</b>. Đặt xu thứ 4 thì xu cũ nhất biến mất (xu sắp mất sẽ nhấp nháy). Người thua cạn ly, chạm màn hình để chơi lại.']}),
 'how-gomoku': ('gomokuTitle', '⚫', {
  'ko': ['⚔️ 대결게임 → <b>⚫ 오목</b>. 위·아래로 마주 앉아요 (아래 = 흑, 위 = 백). 각자 쪽에 <b>"나: 흑"</b>처럼 내 돌 색이 보이고, 내 차례면 밝게 표시돼요.', '흑이 먼저, 번갈아 15×15 바둑판에 돌을 놓아요. 시간 제한은 없어요. 빨간 점이 방금 둔 돌이에요.', '가로·세로·대각선 5개를 먼저 이으면 승리! 흑은 <b>🚫 33 금지</b> (열린 3이 두 줄 동시에 생기는 자리). 진 사람 원샷.'],
  'en': ['⚔️ Versus Games → <b>⚫ Gomoku</b>. Sit facing each other (bottom = black, top = white). Each side shows your stone color, like <b>"You: Black"</b>, and lights up on your turn.', 'Black goes first; take turns placing stones on the 15×15 board. No time limit. The red dot marks the last move.', 'Five in a row wins! Black may not make a <b>🚫 double-three</b> (two open threes at once). Loser drinks.'],
  'ja': ['⚔️ 対戦ゲーム → <b>⚫ 五目並べ</b>。上下に向かい合って座ります（下＝黒、上＝白）。それぞれの側に<b>「あなた: 黒」</b>のように石の色が出て、自分の番は明るく表示。', '黒が先手、交互に15×15の盤に石を置きます。時間制限なし。赤い点が今置いた石です。', '縦・横・斜めに5つ先にそろえたら勝ち！黒は<b>🚫 三三禁止</b>（開いた三が同時に2本できる場所）。負けたら一気。'],
  'zh': ['⚔️ 对战游戏 → <b>⚫ 五子棋</b>。上下面对面坐（下 = 黑，上 = 白）。各自一边显示<b>“你: 黑”</b>这样的棋子颜色，轮到自己时会变亮。', '黑先，轮流在15×15棋盘上落子。没有时间限制。红点是刚下的那颗。', '横、竖、斜先连成5个就赢！黑棋<b>🚫 禁三三</b>（同时形成两个活三的位置）。输的人干杯。'],
  'th': ['⚔️ เกมดวล → <b>⚫ โกะโมะกุ</b> นั่งหันหน้าเข้าหากัน (ล่าง = ดำ บน = ขาว) แต่ละฝั่งแสดงสีหมากของตัวเอง เช่น <b>"คุณ: ดำ"</b> ถึงตาเราจะสว่างขึ้น', 'ดำเดินก่อน ผลัดกันวางหมากบนกระดาน 15×15 ไม่จำกัดเวลา จุดแดงคือหมากที่เพิ่งวาง', 'เรียง 5 ก่อนชนะ! ดำ<b>🚫 ห้ามสามสาม</b> (จุดที่เกิดสามเปิดสองแถวพร้อมกัน) คนแพ้หมดแก้ว'],
  'vi': ['⚔️ Trò đối kháng → <b>⚫ Cờ năm</b>. Ngồi đối diện (dưới = đen, trên = trắng). Mỗi bên hiện màu quân của mình, như <b>"Bạn: Đen"</b>, sáng lên khi tới lượt.', 'Đen đi trước, lần lượt đặt quân trên bàn 15×15. Không giới hạn thời gian. Chấm đỏ là nước vừa đi.', 'Nối 5 quân trước là thắng! Đen <b>🚫 cấm 3-3</b> (hai hàng ba mở cùng lúc). Người thua cạn ly.']}),
 'how-hotel': ('hotelTitle', '🏨', {
  'ko': ['메인 화면 <b>🏨 숙소</b> 버튼 (QR 옆). 처음엔 숙소 이름, <b>현지어 주소</b>(예약 앱에서 복사해서 붙여넣기), 전화번호·메모(선택)를 넣고 저장. 여러 개 넣을 수 있어요.', '누르면 바로 <b>흰 화면에 큰 글씨</b>로 "여기로 가 주세요" + 숙소 이름 + 주소가 나와요. 택시 기사님께 보여주세요.', '<b>📍 지도 QR</b>을 찍으면 구글 지도로 바로 길 안내, <b>📞 전화하기</b>로 숙소에 바로 전화. 정보는 내 폰에만 저장되고 인터넷 없어도 돼요.'],
  'en': ['Tap <b>🏨 Hotel</b> on the main screen (next to QR). The first time, enter the name, the <b>address in the local language</b> (copy it from your booking app), and phone/note (optional). You can add several.', 'After that it opens right away: <b>big text on white</b> — "Please take me here" + hotel name + address. Show it to the taxi driver.', 'Scan the <b>📍 map QR</b> for Google Maps directions, or tap <b>📞 Call</b> to phone the hotel. Info stays only on your phone and works offline.'],
  'ja': ['メイン画面の<b>🏨 宿</b>ボタン（QRの隣）。初回は宿の名前、<b>現地語の住所</b>（予約アプリからコピーして貼り付け）、電話番号・メモ（任意）を入れて保存。複数入れられます。', 'タップするとすぐ<b>白い画面に大きな文字</b>で「ここに行ってください」＋宿名＋住所。タクシーの運転手さんに見せましょう。', '<b>📍 地図QR</b>を読み取るとGoogleマップで道案内、<b>📞 電話する</b>で宿にすぐ電話。情報は自分のスマホだけに保存、ネットなしでOK。'],
  'zh': ['点主界面<b>🏨 住处</b>按钮（二维码旁）。第一次填住处名称、<b>当地语言地址</b>（从订房App复制粘贴）、电话和备注（可选）后保存。可以存多个。', '之后点一下就是<b>白底大字</b>：“请带我去这里” + 住处名称 + 地址。给出租车司机看就行。', '扫<b>📍 地图二维码</b>直接用谷歌地图导航，点<b>📞 打电话</b>直接拨给住处。信息只存在自己手机里，没网也能用。'],
  'th': ['แตะปุ่ม <b>🏨 ที่พัก</b> ที่หน้าหลัก (ข้าง QR) ครั้งแรกใส่ชื่อที่พัก <b>ที่อยู่ภาษาท้องถิ่น</b> (คัดลอกจากแอปจอง) และเบอร์โทร·โน้ต (ไม่บังคับ) แล้วบันทึก ใส่ได้หลายที่', 'แตะแล้วจะขึ้น<b>ตัวหนังสือใหญ่บนพื้นขาว</b>ทันที: "ช่วยพาไปที่นี่" + ชื่อที่พัก + ที่อยู่ ยื่นให้คนขับแท็กซี่ดูได้เลย', 'สแกน <b>📍 QR แผนที่</b> เพื่อนำทางด้วย Google Maps หรือแตะ <b>📞 โทร</b> โทรหาที่พักได้ทันที ข้อมูลเก็บในมือถือเราเท่านั้น ไม่มีเน็ตก็ใช้ได้'],
  'vi': ['Chạm nút <b>🏨 Chỗ ở</b> ở màn hình chính (cạnh QR). Lần đầu nhập tên, <b>địa chỉ bằng tiếng địa phương</b> (sao chép từ app đặt phòng), số điện thoại·ghi chú (tùy chọn) rồi lưu. Có thể lưu nhiều chỗ.', 'Từ đó chạm là hiện ngay <b>chữ to trên nền trắng</b>: "Làm ơn chở tôi đến đây" + tên chỗ ở + địa chỉ. Đưa tài xế taxi xem.', 'Quét <b>📍 QR bản đồ</b> để Google Maps chỉ đường, hoặc chạm <b>📞 Gọi</b> để gọi chỗ ở. Thông tin chỉ lưu trên điện thoại của bạn, không cần mạng.']}),
 'how-dual': ('language', '🌐', {
  'ko': ['로고 옆 <b>🌐 언어</b>에서 언어를 <b>2개까지</b> 골라요. 처음 고른 게 <b>① 메인</b>, 두 번째가 <b>② 보조</b>예요. 누른 걸 다시 누르면 빠져요.', '게임 설명부터 게임 화면, 결과까지 <b>메인 아래에 보조 언어</b>가 같이 나와요 (같은 크기, 보조만 살짝 흐리게). 메뉴와 제목은 메인 언어만.', '외국인 친구랑 같이 할 때 좋아요. 다시 한 언어로 돌리려면 ② 보조 언어를 한 번 더 누르면 돼요.'],
  'en': ['In <b>🌐 Language</b> next to the logo, pick <b>up to 2</b> languages. The first is <b>① main</b>, the second <b>② second</b>. Tap one again to remove it.', 'From game rules to game screens and results, the <b>second language shows under the main one</b> (same size, slightly dimmer). Menus and titles stay in the main language.', 'Great when playing with friends from abroad. To go back to one language, tap the ② second language again.'],
  'ja': ['ロゴ横の<b>🌐 言語</b>で言語を<b>2つまで</b>選べます。最初が<b>① メイン</b>、2つ目が<b>② サブ</b>。もう一度押すと外れます。', 'ゲーム説明からゲーム画面・結果まで<b>メインの下にサブ言語</b>が出ます（同じ大きさ、サブだけ少し薄く）。メニューとタイトルはメイン言語だけ。', '外国の友だちと遊ぶときに便利。1言語に戻すには② サブ言語をもう一度押せばOK。'],
  'zh': ['在标志旁的<b>🌐 语言</b>里最多选<b>2种</b>语言。先选的是<b>① 主语言</b>，第二个是<b>② 第二语言</b>。再点一次就取消。', '从游戏说明到游戏画面和结果，<b>主语言下面会同时显示第二语言</b>（同样大小，第二语言稍淡）。菜单和标题只显示主语言。', '和外国朋友一起玩正合适。想换回一种语言，再点一次 ② 第二语言就行。'],
  'th': ['ที่ <b>🌐 ภาษา</b> ข้างโลโก้ เลือกได้ <b>สูงสุด 2 ภาษา</b> อันแรกคือ <b>① ภาษาหลัก</b> อันที่สองคือ <b>② ภาษารอง</b> แตะซ้ำเพื่อเอาออก', 'ตั้งแต่กติกา หน้าเกม จนถึงผลลัพธ์ จะมี<b>ภาษารองอยู่ใต้ภาษาหลัก</b> (ขนาดเท่ากัน ภาษารองจางลงนิดหน่อย) เมนูและชื่อหัวข้อเป็นภาษาหลักอย่างเดียว', 'เหมาะกับเล่นกับเพื่อนต่างชาติ ถ้าจะกลับเป็นภาษาเดียว แตะ ② ภาษารองอีกครั้ง'],
  'vi': ['Trong <b>🌐 Ngôn ngữ</b> cạnh logo, chọn <b>tối đa 2</b> ngôn ngữ. Cái đầu là <b>① chính</b>, cái thứ hai là <b>② phụ</b>. Chạm lại để bỏ.', 'Từ luật chơi đến màn hình trò chơi và kết quả, <b>ngôn ngữ phụ hiện dưới ngôn ngữ chính</b> (cùng cỡ, hơi mờ hơn). Menu và tiêu đề chỉ hiện ngôn ngữ chính.', 'Rất hợp khi chơi với bạn nước ngoài. Muốn về một ngôn ngữ, chạm lại ② ngôn ngữ phụ.']}),
}
DUAL = {'ko': '두 언어 같이 보기', 'en': 'Two languages at once', 'ja': '2言語同時表示', 'zh': '双语同时显示', 'th': 'แสดงสองภาษาพร้อมกัน', 'vi': 'Hiển thị hai ngôn ngữ'}
AFTER = {'how-plinko': 'how-coin', 'how-ttt': 'how-slap', 'how-gomoku': 'how-ttt', 'how-hotel': 'how-qr', 'how-dual': 'how-hotel'}

keys = list(m.HOW_KEYS); emo = list(m.HOW_EMOJI); steps = {L: list(m.HOW[L]['steps']) for L in LANGS}
for shot, after in AFTER.items():
    if any(k[0] == shot for k in keys): continue
    i = [k[0] for k in keys].index(after) + 1
    key, e, txt = NEW[shot]
    keys.insert(i, (shot, key)); emo.insert(i, e)
    for L in LANGS: steps[L].insert(i, txt[L])
for L in LANGS: m.HOW[L]['steps'] = steps[L]

# v1.6 구성 · 수정 내용
rel = CHANGELOG[0]; assert rel['ver'] == '1.6'
for L in LANGS:
    P = m.PAGE[L]; s = strings(L)
    for k in ['navNew', 'mainP', 'hNew', 'newIntro']: P[k] = P[k].replace('v1.5.3', 'v1.6')
    P['changes'] = [tx[L] for _, tx in rel['items']]
    feat = [x for x in P['newFeat'] if not x.startswith('🆕')]
    feat.insert(1, '🆕 ' + ' · '.join([f"🔻 {s['plinkoTitle']}", f"🟢 {s['tttTitle']}", f"⚫ {s['gomokuTitle']}", f"🏨 {s['hotelTitle']}", f"🌐 {DUAL[L]}"]))
    feat.insert(2, '✨ v1.5: ' + ' · '.join([f"🎀 {s['tugTitle']}", f"🐾 {s['slapTitle']}", f"👑 {s['kcTitle']}", f"🍺 {s['beerTitle']}", f"🪙 {s['coinTitle']}"]))
    P['newFeat'] = feat

head = open('tools/manual_text.py', encoding='utf-8').read().split('\nPAGE = {')[0]
pp = lambda o: pprint.pformat(o, width=200, sort_dicts=False)
out = head + '\nPAGE = ' + pp(m.PAGE) + '\n\n# 단계 그림 속 폰 화면 문구\nSHOT = ' + pp(m.SHOT) + '\n\n# 게임 방법: (그림 이름, 제목으로 쓸 앱 문구 키, 설명 3줄)\nHOW_KEYS = ' + repr(keys) + '\nHOW_EMOJI = ' + repr(emo) + '\n\nHOW = ' + pp(m.HOW) + '\n'
open('tools/manual_text.py', 'w', encoding='utf-8').write(out)
print('ok', len(keys), len(emo), [len(steps[L]) for L in LANGS])
