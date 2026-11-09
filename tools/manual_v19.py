"""v1.9 설명서 반영 (cwd = /home/claude/wolfgames)"""
import sys, pprint, re
sys.path.insert(0, 'tools')
import manual_text as m
sys.path.insert(0, '/home/claude/wolfgames_beta/tools')
from changelog import CHANGELOG
LANGS = ['ko', 'en', 'ja', 'zh', 'th', 'vi']
rel = CHANGELOG[0]; assert rel['ver'] == '1.9-beta'

# ---- app strings (disclaimer, kcNames)
src = open('app/src/main/java/com/wolfgames/app/i18n/Strings.kt', encoding='utf-8').read()
BLK = {'ko': 'KoStrings', 'en': 'EnStrings', 'ja': 'JaStrings', 'zh': 'ZhStrings', 'th': 'ThStrings', 'vi': 'ViStrings'}
def appstr(L, key):
    st = src.index(f'val {BLK[L]} = Strings().apply {{')
    mm = re.compile(r'^    ' + key + r' = ("(?:[^"\\]|\\.)*")$', re.M).search(src, st)
    return mm.group(1)[1:-1].replace('\\"', '"')
KC_ICONS = ["🌊", "👉", "🙋", "🚽", "💃", "🎉", "🙌", "🤝", "🔤", "📚", "📜", "❓", "👑"]
KC_RANK = ['A', '2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K']

# ---- PAGE
LEAD = {'ko': '미니 게임 모음', 'en': 'Mini game collection', 'ja': 'ミニゲーム集', 'zh': '迷你游戏合集', 'th': 'รวมมินิเกม', 'vi': 'Tuyển tập mini game'}
NEW19 = {
 'ko': '🆕 🐺 늑대 야바위 · 📤 공유 · 🏁 늑대달리기 순위 크게 · 🍺 늑대컬링 → 🐺 늑대게임 · 🎯 "벌칙" · "당첨"으로 통일',
 'en': '🆕 🐺 Wolf Shell Game · 📤 Share · 🏁 Big Wolf Race rankings · 🍺 Wolf Curling → 🐺 Wolf Games · 🎯 "penalty" / "you\'re it" everywhere',
 'ja': '🆕 🐺 オオカミ当て · 📤 共有 · 🏁 ウルフレースの順位を大きく · 🍺 オオカミカーリング → 🐺 オオカミゲーム · 🎯「罰ゲーム」「当たり」に統一',
 'zh': '🆕 🐺 狼猜猜猜 · 📤 分享 · 🏁 群狼赛跑名次放大 · 🍺 狼冰壶 → 🐺 狼游戏 · 🎯 统一为"惩罚"·"中奖"',
 'th': '🆕 🐺 หมาป่าซ่อนเหรียญ · 📤 แชร์ · 🏁 อันดับวิ่งแข่งหมาป่าตัวใหญ่ · 🍺 เคิร์ลลิงหมาป่า → 🐺 เกมหมาป่า · 🎯 ใช้คำว่า "ทำโทษ" · "โดน"',
 'vi': '🆕 🐺 Đoán sói · 📤 Chia sẻ · 🏁 Thứ hạng Sói chạy đua hiện to · 🍺 Bi đá sói → 🐺 Trò chơi sói · 🎯 Thống nhất "bị phạt" · "trúng"',
}
for L in LANGS:
    P = m.PAGE[L]
    P['lead'] = LEAD[L] + ' · ' + P['lead'].split(' · ', 1)[1]
    for k in ['navNew', 'mainP', 'hNew', 'newIntro']: P[k] = P[k].replace('v1.8.1', 'v1.9')
    P['changes'] = [tx[L] for _, tx in rel['items']]
    nf = list(P['newFeat'])
    if not nf[1].startswith('🆕 🐺'):
        nf[1:2] = [NEW19[L], '✨ v1.8: ' + nf[1][len('🆕 '):]]
    nf = [x.replace('🍶 789', '🎯 789') for x in nf]
    i8 = next(i for i, x in enumerate(nf) if x.startswith('🧮'))
    nf[i8] = (nf[i8].replace('🍻 N빵(술값 나누기)', '💸 N빵').replace('🍻', '💸')
              .replace('หารบิล', 'หารเท่า').replace('Chia tiền', 'Chia đều'))
    P['newFeat'] = nf
    P['disclaimer'] = appstr(L, 'disclaimer')

# ---- HOW
keys = list(m.HOW_KEYS); emo = list(m.HOW_EMOJI); steps = {L: [list(s) for s in m.HOW[L]['steps']] for L in LANGS}
def ix(img): return [k[0] for k in keys].index(img)
emo[ix('how-789')] = '🎯'

# 일반 치환 (지정 항목만)
GEN = {'ko': [('원샷', '벌칙')], 'ja': [('一気', '罰ゲーム')], 'zh': [('干杯', '受罚')],
       'th': [('หมดแก้ว', 'โดนทำโทษ')], 'vi': [('uống cạn', 'bị phạt'), ('cạn ly', 'bị phạt')],
       'en': [('<b>bottoms up!</b>', '<b>penalty!</b>'), ('❌ drink', '❌ penalty'), ('you <b>💀 drink</b>', 'you get a <b>💀 penalty</b>'),
              ('💥 💀 drink', '💥 💀 penalty'), ('Loser drinks', 'Loser gets a penalty'), ('defender drinks', 'defender gets a penalty'),
              ('<b>flinch</b>, drink!', '<b>flinch</b> — penalty!')]}
for img in ['how-hcm', 'how-coin', 'how-nbomb', 'how-balloon', 'how-ttt', 'how-gomoku', 'how-slap']:
    for L in LANGS:
        s = steps[L][ix(img)][2]
        for a, b in GEN[L]: s = s.replace(a, b)
        steps[L][ix(img)][2] = s

SET = {  # (img, line) -> {lang: text}
 ('how-789', 2): {
  'ko': '합의 뒷자리가 <b>7 작은 벌칙 · 8 중간 벌칙 · 9 큰 벌칙</b>, 6은 순서 반대로, 나머지는 통과예요.',
  'en': 'Last digit of the sum: <b>7 small penalty · 8 medium penalty · 9 big penalty</b>, 6 reverses the order, anything else passes.',
  'ja': '合計の下1桁が<b>7 小さい罰ゲーム・8 中くらいの罰ゲーム・9 大きい罰ゲーム</b>、6は順番を逆に、それ以外はパス。',
  'zh': '点数和的个位：<b>7 小惩罚 · 8 中惩罚 · 9 大惩罚</b>，6 顺序反转，其他直接过。',
  'th': 'หลักหน่วยของผลรวม: <b>7 ทำโทษเบา · 8 ปานกลาง · 9 ทำโทษหนัก</b> 6 สลับลำดับ นอกนั้นผ่าน',
  'vi': 'Chữ số cuối của tổng: <b>7 phạt nhẹ · 8 phạt vừa · 9 phạt nặng</b>, 6 đảo chiều, còn lại thì qua.'},
 ('how-updown', 0): {
  'ko': '인원(2~8명)을 정해요. <b>누적</b> 옵션을 켜면 성공할 때마다 벌칙이 1개씩 쌓여요.',
  'en': 'Choose the number of players (2–8). With the <b>stack</b> option, 1 penalty is added to the pot for every success.',
  'ja': '人数（2〜8人）を決めます。<b>累積</b>をオンにすると成功するたびに罰ゲームが1つずつ増えます。',
  'zh': '选择人数（2~8人）。打开<b>累积</b>后每成功一次惩罚累加1个。',
  'th': 'เลือกจำนวนคน (2–8 คน) ถ้าเปิด <b>สะสม</b> ทุกครั้งที่ทายถูกจะสะสมบทลงโทษเพิ่ม 1 ครั้ง',
  'vi': 'Chọn số người (2–8). Bật <b>cộng dồn</b> thì mỗi lần đoán đúng dồn thêm 1 hình phạt.'},
 ('how-updown', 2): {
  'ko': '성공하면 다음 사람 차례, 실패하면 벌칙(누적이면 쌓인 벌칙 전부)! <b>같은 숫자가 나오면 큰 벌칙</b>이에요.',
  'en': 'Right: next player. Wrong: penalty (with stack, the whole pot)! <b>Same number: big penalty.</b>',
  'ja': '成功なら次の人、失敗なら罰ゲーム（累積ならたまった分全部）！<b>同じ数字なら大きい罰ゲーム</b>です。',
  'zh': '猜对轮到下一位，猜错受罚（累积时接受全部惩罚）！<b>点数相同就是大惩罚</b>。',
  'th': 'ถูกก็ถึงตาคนต่อไป ผิดโดนทำโทษ (ถ้าสะสมโดนทั้งหมด)! <b>ได้เลขเดียวกันโดนทำโทษหนัก</b>',
  'vi': 'Đúng thì đến người kế, sai thì bị phạt (cộng dồn thì nhận hết)! <b>Ra cùng số thì phạt nặng.</b>'},
 ('how-kings', 2): {
  'ko': 'K가 나올 때마다 가운데 킹스컵 잔이 조금씩 차올라요. <b>4번째 K를 뽑은 사람이 킹스컵 큰 벌칙!</b> <b>🔀 새로 섞기</b>로 새 판.',
  'en': "Each King fills the King's Cup in the middle a little more. <b>Whoever draws the 4th King gets the King's Cup big penalty!</b> <b>🔀 Reshuffle</b> starts over.",
  'ja': 'Kが出るたびに真ん中のキングスカップが少しずつ満ちていきます。<b>4枚目のKを引いた人がキングスカップの大きい罰ゲーム！</b> <b>🔀 シャッフル</b>で新しいゲーム。',
  'zh': '每出一张K，中间的国王杯就满一点。<b>抽到第4张K的人接受国王杯大惩罚！</b> <b>🔀 重新洗牌</b>开新局。',
  'th': 'ทุกครั้งที่ได้ K แก้วคิงส์คัพตรงกลางจะเต็มขึ้นทีละนิด <b>ใครจั่ว K ใบที่ 4 โดนทำโทษหนักคิงส์คัพ!</b> <b>🔀 สับไพ่ใหม่</b> เพื่อเริ่มใหม่',
  'vi': "Mỗi lá K làm ly King's Cup ở giữa đầy thêm một chút. <b>Ai rút lá K thứ 4 nhận phạt nặng King's Cup!</b> <b>🔀 Xáo lại</b> để chơi ván mới."},
 ('how-curling', 2): {
  'ko': '테이블 끝에 가까울수록 좋아요 (🥇). 끝을 넘으면 💦 떨어져요. 다 던지면 🏆 순위 — 떨어진 사람은 모두, 아무도 안 떨어졌으면 제일 먼 사람이 💀 벌칙!',
  'en': 'The closer to the edge, the better (🥇). Go past it and it 💦 falls. After everyone throws, 🏆 ranking — everyone who fell gets a 💀 penalty, or if nobody fell, the farthest one does!',
  'ja': 'テーブルの端に近いほど良い（🥇）。越えると💦落ちます。全員投げたら🏆順位 — 落ちた人は全員、誰も落ちなければ一番遠い人が💀罰ゲーム！',
  'zh': '离桌边越近越好（🥇）。超过桌边就 💦 掉下去。大家都推完后 🏆 排名 — 掉下去的人都受罚，没人掉的话离得最远的人 💀 受罚！',
  'th': 'ยิ่งใกล้ขอบโต๊ะยิ่งดี (🥇) ถ้าเลยขอบจะ 💦 ตก ทุกคนเล่นครบแล้วดู 🏆 อันดับ — ใครตกโดนทำโทษทุกคน ถ้าไม่มีใครตก คนที่ไกลสุด 💀 โดนทำโทษ!',
  'vi': 'Càng sát mép bàn càng tốt (🥇). Vượt mép là 💦 rơi. Ném hết thì xem 🏆 xếp hạng — ai rơi đều bị phạt, nếu không ai rơi thì người xa nhất 💀 bị phạt!'},
 ('how-race', 2): {
  'ko': '<b>늑대가 먼저 무는 동물이 1등</b>. "앙!" 하고 문 순서대로 순위가 붙고, 레인 가운데에 크게 떠요 (1등 👑 · 꼴찌 💀).',
  'en': '<b>The animal the wolf bites first is 1st.</b> Ranks follow the order of the bites and show big in each lane (1st 👑 · last 💀).',
  'ja': '<b>オオカミが先にかんだ動物が1位</b>。かんだ順に順位がつき、各レーンの真ん中に大きく出ます（1位👑・最下位💀）。',
  'zh': '<b>小狼先咬到的动物第1名</b>，按咬的顺序排名，名次在每条跑道中间大大显示（第1名 👑 · 最后一名 💀）。',
  'th': '<b>สัตว์ที่หมาป่างับก่อนได้ที่ 1</b> อันดับเรียงตามลำดับที่โดนงับ และขึ้นตัวใหญ่กลางลู่ (ที่ 1 👑 · ที่โหล่ 💀)',
  'vi': '<b>Con vật bị sói ngoạm trước là hạng 1</b>, xếp hạng theo thứ tự bị ngoạm và hiện to giữa mỗi làn (hạng 1 👑 · bét 💀).'},
}
for (img, li), tx in SET.items():
    for L in LANGS: steps[L][ix(img)][li] = tx[L]

REP = {  # (img, line) -> {lang: [(old, new)]}
 ('how-tower', 2): {'ko': [('🍺 원샷!', '💀 벌칙!')], 'en': [('drinks 🍺!', 'gets a 💀 penalty!')], 'ja': [('🍺一気！', '💀罰ゲーム！')],
                    'zh': [('🍺 干杯！', '💀 受罚！')], 'th': [('🍺 หมดแก้ว!', '💀 โดนทำโทษ!')], 'vi': [('🍺 uống!', '💀 bị phạt!')]},
 ('how-simon', 2): {'ko': [('🍺 원샷!', '💀 벌칙!')], 'en': [('🍺 drink!', '💀 penalty!')], 'ja': [('🍺一気！', '💀罰ゲーム！')],
                    'zh': [('🍺 干杯！', '💀 受罚！')], 'th': [('🍺 ดื่ม!', '💀 โดนทำโทษ!')], 'vi': [('🍺 uống!', '💀 bị phạt!')]},
 ('how-wire', 2): {'ko': [('🍺 원샷.', '💀 벌칙!')], 'en': [('and 🍺 bottoms up.', 'and a 💀 penalty.')], 'ja': [('🍺一気。', '💀罰ゲーム。')],
                   'zh': [('🍺 干杯。', '💀 受罚。')], 'th': [('🍺 หมดแก้ว', '💀 โดนทำโทษ')], 'vi': [('🍺 cạn ly.', '💀 bị phạt.')]},
 ('how-curling', 0): {'ko': [('⚡ 순발력게임', '🐺 늑대게임')], 'en': [('⚡ Quick Games', '🐺 Wolf Games')], 'ja': [('⚡ 反射神経ゲーム', '🐺 オオカミゲーム')],
                      'zh': [('⚡ 反应游戏', '🐺 狼游戏')], 'th': [('⚡ เกมความไว', '🐺 เกมหมาป่า')], 'vi': [('⚡ Trò phản xạ', '🐺 Trò chơi sói')]},
 ('how-plinko', 2): {'ko': [('1잔 · 반 잔', '벌칙 · 반 벌칙')], 'en': [('1 drink · Half', 'Penalty · Half penalty')],
                     'ja': [('1杯・半分・もう一回・右の人・左の人・みんな・', '罰ゲーム・半分罰ゲーム・もう一回・右の人・左の人・みんなで・')],
                     'zh': [('1杯 · 半杯', '惩罚 · 半惩罚')], 'th': [('1 แก้ว · ครึ่งแก้ว', 'โดนทำโทษ · ครึ่งโทษ')], 'vi': [('1 ly · Nửa ly', 'Bị phạt · Nửa phạt')]},
 ('how-split', 0): {'ko': [('술값 총액', '총 금액')], 'ja': [('飲み代の合計', '合計金額')], 'zh': [('酒钱总额', '总金额')],
                    'th': [('ยอดรวมค่าเหล้าใน', 'ยอดรวมใน')], 'vi': [('tổng tiền nhậu', 'tổng tiền')]},
}
for li in range(3):
    for img in ['how-split', 'how-tip']:
        d = REP.setdefault((img, li), {})
        for L in LANGS:
            d.setdefault(L, []).extend([('🍻', '💸')] + ({'th': [('หารบิล', 'หารเท่า')], 'vi': [('Chia tiền', 'Chia đều'), ('Chia kèm tip', 'Chia đều kèm tip')]}.get(L, [])))
for (img, li), d in REP.items():
    for L, pairs in d.items():
        s = steps[L][ix(img)][li]
        for a, b in pairs:
            if a in s: s = s.replace(a, b)
            elif (img, li) not in [(i, n) for i in ('how-split', 'how-tip') for n in range(3)]: raise SystemExit(f'miss {img} {li} {L}: {a}')
        steps[L][ix(img)][li] = s

# 킹스컵 카드 이름 = 앱 kcNames
KNAME = {'ko': '킹스컵', 'en': "King's Cup", 'ja': 'キングスカップ', 'zh': '国王杯', 'th': 'คิงส์คัพ', 'vi': "King's Cup"}
for L in LANGS:
    s = steps[L][ix('how-kings')][1]
    tail = s[s.index('K 👑 ' + KNAME[L]) + len('K 👑 ' + KNAME[L]):]
    names = appstr(L, 'kcNames').split('|')
    steps[L][ix('how-kings')][1] = ' · '.join(f'{r} {e} {n}' for r, e, n in zip(KC_RANK, KC_ICONS, names)) + tail

# 늑대컬링 → 늑대게임 묶음 끝 (how-arrow 뒤)
if ix('how-curling') < ix('how-arrow'):
    i = ix('how-curling'); k = keys.pop(i); e = emo.pop(i); st = {L: steps[L].pop(i) for L in LANGS}
    j = ix('how-arrow') + 1
    keys.insert(j, k); emo.insert(j, e)
    for L in LANGS: steps[L].insert(j, st[L])

NEW = {
 'how-yabawi': ('ybTitle', '🐺', 'how-wire', {
  'ko': ['🍀 운빨게임 → <b>🐺 늑대 야바위</b>. 인원(1~8명)과 <b>꽝 늑대 수</b>(1 ~ 인원−1마리)를 정하고 시작해요.',
         '늑대들이 입을 벌리고 🪙 코인을 하나씩 먹는데, 꽝 늑대는 굶어요. 그다음 이리저리 섞여요 (점점 빨라져요) — 잘 따라가 봐요 👀',
         '한 사람씩 늑대를 하나 골라요 👆 코인이 나오면 ✅ 통과, 빈 입이면 <b>💀 벌칙!</b> 꽝 늑대를 다 찾으면 끝.'],
  'en': ['🍀 Luck Games → <b>🐺 Wolf Shell Game</b>. Choose players (1–8) and the number of <b>empty wolves</b> (1 to players−1), then start.',
         'The wolves open wide and each eats a 🪙 coin — except the empty ones. Then they shuffle around, faster and faster — keep your eyes on them 👀',
         'Take turns picking a wolf 👆 Coin = ✅ safe, empty mouth = <b>💀 penalty!</b> The game ends when every empty wolf is found.'],
  'ja': ['🍀 運ゲーム → <b>🐺 オオカミ当て</b>。人数（1〜8人）と<b>ハズレオオカミの数</b>（1〜人数−1匹）を決めてスタート。',
         'オオカミたちが口を開けて🪙コインを1枚ずつ食べますが、ハズレはおあずけ。そのあとぐるぐる入れ替わります（だんだん速く）— よく見て 👀',
         '1人ずつオオカミを1匹選びます 👆 コインが出たら✅セーフ、空っぽなら<b>💀罰ゲーム！</b> ハズレを全部見つけたら終わり。'],
  'zh': ['🍀 运气游戏 → <b>🐺 狼猜猜猜</b>。选好人数（1~8人）和<b>空嘴狼数量</b>（1~人数−1只）开始。',
         '狼们张大嘴每只吃一枚 🪙 硬币，空嘴狼吃不到。然后它们来回交换位置，越来越快 — 好好盯着 👀',
         '每人轮流选一只狼 👆 有硬币就 ✅ 过关，空嘴的 <b>💀 受罚！</b> 空嘴狼全部找到就结束。'],
  'th': ['🍀 เกมดวง → <b>🐺 หมาป่าซ่อนเหรียญ</b> เลือกจำนวนคน (1–8) และ<b>จำนวนหมาป่าปากว่าง</b> (1 ถึง จำนวนคน−1 ตัว) แล้วเริ่ม',
         'หมาป่าอ้าปากกิน 🪙 เหรียญตัวละเหรียญ ยกเว้นตัวปากว่าง แล้วจะสลับที่ไปมาเร็วขึ้นเรื่อยๆ — ตามดูให้ดี 👀',
         'ผลัดกันเลือกหมาป่าคนละตัว 👆 มีเหรียญ = ✅ รอด ปากว่าง = <b>💀 โดนทำโทษ!</b> เจอปากว่างครบก็จบ'],
  'vi': ['🍀 Trò may rủi → <b>🐺 Đoán sói</b>. Chọn số người (1–8) và <b>số sói miệng trống</b> (1 đến số người−1) rồi bắt đầu.',
         'Các con sói há miệng, mỗi con ăn một 🪙 xu — trừ con miệng trống. Rồi chúng đổi chỗ qua lại, càng lúc càng nhanh — nhìn theo kỹ nhé 👀',
         'Lần lượt mỗi người chọn một con 👆 Có xu = ✅ thoát, miệng trống = <b>💀 bị phạt!</b> Tìm hết sói miệng trống là xong.']}),
 'how-share': ('shTitle', '', 'how-qr', {
  'ko': ['메인 화면 <b>📤 공유</b> 버튼. 먼저 <b>친구 폰이 뭐예요?</b>에서 친구 폰을 골라요.',
         '<b>🤖 안드로이드</b> → 앱(APK)을 바로 받는 링크, <b>🍎 아이폰 · PC</b> → 웹 링크 (사파리 → 공유 → "홈 화면에 추가"하면 앱처럼).',
         '<b>📲 앱으로 공유</b>로 카톡 등에 보내거나 <b>🔗 링크만 복사</b>. 친구가 옆에 있으면 화면의 QR을 바로 찍으면 돼요 📷'],
  'en': ['<b>📤 Share</b> button on the main screen. First pick your friend\'s phone under <b>What phone does your friend have?</b>',
         '<b>🤖 Android</b> → a link that downloads the app (APK) right away; <b>🍎 iPhone · PC</b> → the web link (Safari → Share → "Add to Home Screen" to use it like an app).',
         'Send it with <b>📲 Share via app</b> or use <b>🔗 Copy link only</b>. If your friend is right there, they can just scan the QR on the screen 📷'],
  'ja': ['メイン画面の<b>📤 共有</b>ボタン。まず<b>友だちのスマホは？</b>で友だちのスマホを選びます。',
         '<b>🤖 Android</b> → アプリ（APK）をすぐダウンロードできるリンク、<b>🍎 iPhone · PC</b> → Webのリンク（Safari → 共有 → 「ホーム画面に追加」でアプリのように）。',
         '<b>📲 アプリで共有</b>でLINEなどに送るか、<b>🔗 リンクだけコピー</b>。隣にいるなら画面のQRをそのまま撮ればOK 📷'],
  'zh': ['主界面的<b>📤 分享</b>按钮。先在<b>朋友用的是什么手机？</b>里选朋友的手机。',
         '<b>🤖 安卓</b> → 直接下载应用（APK）的链接；<b>🍎 iPhone · 电脑</b> → 网页链接（Safari → 分享 → "添加到主屏幕"，就能像应用一样用）。',
         '点<b>📲 用应用分享</b>发到聊天软件，或<b>🔗 只复制链接</b>。朋友在旁边的话直接扫屏幕上的二维码就行 📷'],
  'th': ['ปุ่ม <b>📤 แชร์</b> ที่หน้าหลัก เลือกมือถือของเพื่อนที่ <b>เพื่อนใช้มือถืออะไร?</b> ก่อน',
         '<b>🤖 Android</b> → ลิงก์ดาวน์โหลดแอป (APK) ทันที <b>🍎 iPhone · PC</b> → ลิงก์เว็บ (Safari → แชร์ → "เพิ่มไปยังหน้าจอโฮม" ใช้ได้เหมือนแอป)',
         'กด <b>📲 แชร์ผ่านแอป</b> เพื่อส่งทางแชท หรือ <b>🔗 คัดลอกลิงก์</b> ถ้าเพื่อนอยู่ข้างๆ สแกน QR บนจอได้เลย 📷'],
  'vi': ['Nút <b>📤 Chia sẻ</b> ở màn hình chính. Trước tiên chọn điện thoại của bạn mình ở <b>Bạn của bạn dùng điện thoại gì?</b>',
         '<b>🤖 Android</b> → link tải ứng dụng (APK) ngay; <b>🍎 iPhone · PC</b> → link web (Safari → Chia sẻ → "Thêm vào MH chính" là dùng như ứng dụng).',
         'Gửi bằng <b>📲 Chia sẻ qua ứng dụng</b> hoặc <b>🔗 Chỉ sao chép link</b>. Nếu bạn ở ngay cạnh, quét mã QR trên màn hình là xong 📷']}),
}
for img, (key, e, after, txt) in NEW.items():
    if any(k[0] == img for k in keys): continue
    i = ix(after) + 1
    keys.insert(i, (img, key)); emo.insert(i, e)
    for L in LANGS: steps[L].insert(i, txt[L])

m.HOW_KEYS = keys; m.HOW_EMOJI = emo
for L in LANGS: m.HOW[L]['steps'] = steps[L]

head = open('tools/manual_text.py', encoding='utf-8').read().split('\nPAGE = {')[0]
pp = lambda o: pprint.pformat(o, width=200, sort_dicts=False)
out = head + '\nPAGE = ' + pp(m.PAGE) + '\n\n# 단계 그림 속 폰 화면 문구\nSHOT = ' + pp(m.SHOT) + '\n\n# 게임 방법: (그림 이름, 제목으로 쓸 앱 문구 키, 설명 3줄)\nHOW_KEYS = ' + repr(m.HOW_KEYS) + '\nHOW_EMOJI = ' + repr(m.HOW_EMOJI) + '\n\nHOW = ' + pp(m.HOW) + '\n'
open('tools/manual_text.py', 'w', encoding='utf-8').write(out)
print('ok', m.PAGE['ko']['hNew'], len(keys))
