"""v2.0 설명서 반영 (cwd = /home/claude/wolfgames). 한 번만 실행 → manual_text.py 다시 쓰기
- 구성 제목 v1.9.1 → v2.0, 수정 내용 = 2.0 항목, 🆕 줄 추가
- 게임 방법: 🎰 슬롯머신 · 🧱 젠가 · ⚖️ 균형 잡기 · 🃏 짝 맞추기 추가
- 1.9.1 때 어긋난 설명 바로잡기 (늑대 야바위 설명·그림 이모지가 손가락선택 자리에 들어가 한 칸씩 밀려 있었음)
"""
import sys, pprint
sys.path.insert(0, 'tools')
import manual_text as m
from changelog import CHANGELOG
LANGS = ['ko', 'en', 'ja', 'zh', 'th', 'vi']
rel = CHANGELOG[0]; assert rel['ver'] == '2.0'

NEW20 = {
 'ko': '🆕 🎰 늑대 슬롯머신 · 🧱 늑대 젠가 · ⚖️ 늑대 균형 잡기 · 🃏 늑대 짝 맞추기',
 'en': '🆕 🎰 Wolf Slots · 🧱 Wolf Jenga · ⚖️ Wolf Balance · 🃏 Wolf Memory',
 'ja': '🆕 🎰 オオカミスロット · 🧱 オオカミジェンガ · ⚖️ オオカミバランス · 🃏 オオカミ神経衰弱',
 'zh': '🆕 🎰 狼老虎机 · 🧱 狼叠叠乐 · ⚖️ 狼平衡 · 🃏 狼狼配对',
 'th': '🆕 🎰 สล็อตหมาป่า · 🧱 เจงก้าหมาป่า · ⚖️ ทรงตัวหมาป่า · 🃏 จับคู่หมาป่า',
 'vi': '🆕 🎰 Máy quay sói · 🧱 Jenga sói · ⚖️ Giữ thăng bằng sói · 🃏 Lật hình sói',
}
for L in LANGS:
    P = m.PAGE[L]
    for k in ['navNew', 'mainP', 'hNew', 'newIntro']: P[k] = P[k].replace('v1.9.1', 'v2.0')
    P['changes'] = [tx[L] for _, tx in rel['items']]
    nf = list(P['newFeat'])
    if not nf[1].startswith(NEW20[L]):
        nf[1:2] = [NEW20[L], '✨ v1.9: ' + nf[1][len('🆕 '):]]
    P['newFeat'] = nf

keys = list(m.HOW_KEYS); emo = list(m.HOW_EMOJI); steps = {L: [list(s) for s in m.HOW[L]['steps']] for L in LANGS}
def ix(img): return [k[0] for k in keys].index(img)

# ---- 어긋남 바로잡기: 야바위 설명·이모지(지금 how-finger 자리)를 how-yabawi 자리로
fi, yi = ix('how-finger'), ix('how-yabawi')
if steps['ko'][fi][0].startswith('🐺 늑대게임 → <b>🐺 늑대 야바위'):
    assert yi == fi + 5
    for L in LANGS:
        st = steps[L].pop(fi); steps[L].insert(yi, st)
    e = emo.pop(fi); emo.insert(yi, e)
assert steps['ko'][ix('how-finger')][0].startswith('🍀 운빨게임 → <b>☝️ 손가락선택')
assert steps['ko'][ix('how-yabawi')][0].startswith('🐺 늑대게임 → <b>🐺 늑대 야바위')
assert steps['ko'][ix('how-curling')][0].startswith('🐺 늑대게임 → <b>🍺 늑대컬링')

NEW = {
 'how-slot': ('slTitle', '🎰', 'how-wire', {
  'ko': ['🍀 운빨게임 → <b>🎰 늑대 슬롯머신</b>. 인원(1~8명)을 정하고 시작해요.',
         '자기 차례에 <b>레버를 당겨요</b> (화면 아무 데나 눌러도 돼요). 그림 3개(🐺 · 🪙 · ⭐ · ❤️ · 💀)가 하나씩 멈추고, 앞 2개가 같으면 3번째가 더 늦게 멈춰서 두근두근!',
         '💀가 하나라도 나오면 <b>💀 벌칙</b>, 🐺 3개면 <b>🙌 다 같이 벌칙</b>, 같은 그림 3개면 <b>👉 지목</b>, 나머지는 ✅ 통과. 그다음 사람 차례 👉'],
  'en': ['🍀 Luck Games → <b>🎰 Wolf Slots</b>. Choose players (1–8) and start.',
         'On your turn, <b>pull the lever</b> (or tap anywhere). The 3 reels (🐺 · 🪙 · ⭐ · ❤️ · 💀) stop one by one — if the first two match, the third stops later for extra suspense!',
         'Any 💀 = <b>💀 penalty</b>, 🐺🐺🐺 = <b>🙌 everyone gets a penalty</b>, three of a kind = <b>👉 pick someone</b>, anything else = ✅ safe. Then the next player 👉'],
  'ja': ['🍀 運ゲーム → <b>🎰 オオカミスロット</b>。人数（1〜8人）を決めてスタート。',
         '自分の番で<b>レバーを引きます</b>（画面のどこを押してもOK）。3つの絵（🐺・🪙・⭐・❤️・💀）が順番に止まり、最初の2つが同じなら3つ目がゆっくり止まってドキドキ！',
         '💀が1つでも出たら<b>💀罰ゲーム</b>、🐺が3つなら<b>🙌みんなで罰ゲーム</b>、同じ絵3つなら<b>👉指名</b>、それ以外は✅セーフ。次の人の番 👉'],
  'zh': ['🍀 运气游戏 → <b>🎰 狼老虎机</b>。选好人数（1~8人）开始。',
         '轮到自己时<b>拉下拉杆</b>（点屏幕任意位置也行）。3个图案（🐺 · 🪙 · ⭐ · ❤️ · 💀）依次停下，前两个一样的话第3个会停得更慢，超紧张！',
         '出现任何一个💀就 <b>💀 受罚</b>，3个🐺 <b>🙌 大家一起受罚</b>，3个相同图案 <b>👉 指定一个人</b>，其他 ✅ 过关。然后轮到下一个人 👉'],
  'th': ['🍀 เกมดวง → <b>🎰 สล็อตหมาป่า</b> เลือกจำนวนคน (1–8) แล้วเริ่ม',
         'ถึงตาตัวเองให้<b>ดึงคันโยก</b> (แตะตรงไหนก็ได้) รูป 3 ช่อง (🐺 · 🪙 · ⭐ · ❤️ · 💀) จะหยุดทีละช่อง ถ้า 2 ช่องแรกเหมือนกัน ช่องที่ 3 จะหยุดช้าลง ลุ้นสุดๆ!',
         'มี 💀 แม้แต่ช่องเดียว = <b>💀 โดนทำโทษ</b> 🐺 3 ตัว = <b>🙌 ทุกคนโดนทำโทษ</b> รูปเหมือนกัน 3 ช่อง = <b>👉 ชี้คน</b> นอกนั้น ✅ รอด แล้วถึงตาคนถัดไป 👉'],
  'vi': ['🍀 Trò may rủi → <b>🎰 Máy quay sói</b>. Chọn số người (1–8) rồi bắt đầu.',
         'Đến lượt thì <b>kéo cần</b> (chạm chỗ nào cũng được). 3 ô hình (🐺 · 🪙 · ⭐ · ❤️ · 💀) dừng lần lượt — nếu 2 ô đầu giống nhau, ô thứ 3 dừng chậm hơn, hồi hộp lắm!',
         'Có 💀 dù chỉ một = <b>💀 bị phạt</b>, 🐺 3 con = <b>🙌 cả nhóm bị phạt</b>, 3 hình giống nhau = <b>👉 chỉ định</b>, còn lại ✅ thoát. Rồi đến người kế 👉']}),
 'how-jenga': ('jgTitle', '🧱', 'how-simon', {
  'ko': ['⚡ 순발력게임 → <b>🧱 늑대 젠가</b>. 인원(1~8명)을 정해요. 한 층에 긴 블록 2개씩, 12층짜리 탑 위에 늑대가 있어요.',
         '블록을 <b>꾹 누른 채 바깥쪽으로 쭉 끌어서</b> 빼요 (왼쪽 블록은 왼쪽, 오른쪽 블록은 오른쪽). 빨리 빼거나 <b>손이 위아래로 흔들리면</b>(점이 🔴) 위 〰️ 게이지가 차요. 걸리는 곳에서 덜컹하면 아주 천천히!',
         '끝까지 빼면 맨 위로 올라가고 다음 사람 👉 (중간에 손 떼면 다시 들어가요). 게이지가 꽉 차면 와르르 — 무너뜨린 사람이 <b>💀 벌칙!</b>'],
  'en': ['⚡ Quick Games → <b>🧱 Wolf Jenga</b>. Choose players (1–8). A 12-story tower, 2 long blocks per layer, with a wolf on top.',
         '<b>Press and hold a block, then drag it outward</b> (left block to the left, right block to the right). Pull too fast or <b>let your finger drift up or down</b> (dot turns 🔴) and the 〰️ gauge fills. When it jolts on a snag, go really slow!',
         'Pull it all the way out and it goes on top — next player 👉 (let go early and it slides back). Full gauge = the tower falls — whoever toppled it gets a <b>💀 penalty!</b>'],
  'ja': ['⚡ 反射神経ゲーム → <b>🧱 オオカミジェンガ</b>。人数（1〜8人）を決めます。1段に長いブロック2本、12段のタワーの上にオオカミがいます。',
         'ブロックを<b>押したまま外側へ引き抜きます</b>（左は左へ、右は右へ）。速く抜いたり<b>指が上下にぶれる</b>と（点が🔴）上の〰️ゲージが溜まります。引っかかってガタッときたらゆっくり！',
         '最後まで抜けばてっぺんへ、次の人 👉（途中で離すと戻ります）。ゲージが満タンで崩壊 — 崩した人が<b>💀罰ゲーム！</b>'],
  'zh': ['⚡ 反应游戏 → <b>🧱 狼叠叠乐</b>。选好人数（1~8人）。每层2根长积木，12层高的塔顶上站着一只狼。',
         '<b>按住积木往外拖出来</b>（左边的往左，右边的往右）。拖太快或<b>手指上下晃</b>（圆点变🔴），上方 〰️ 计量条就会涨。卡住一顿时要非常慢！',
         '完全抽出就放到最上面，换下一个人 👉（中途松手会滑回去）。计量条满了塔就倒 — 弄倒的人 <b>💀 受罚！</b>'],
  'th': ['⚡ เกมความไว → <b>🧱 เจงก้าหมาป่า</b> เลือกจำนวนคน (1–8) หอสูง 12 ชั้น ชั้นละ 2 บล็อกยาว มีหมาป่าอยู่บนสุด',
         '<b>กดบล็อกค้างไว้แล้วลากออกด้านข้าง</b> (บล็อกซ้ายลากไปซ้าย ขวาลากไปขวา) ลากเร็วหรือ<b>นิ้วขยับขึ้นลง</b> (จุดเป็น 🔴) เกจ 〰️ ด้านบนจะเพิ่ม พอสะดุดตรงจุดที่ติดให้ค่อยๆ ดึง!',
         'ดึงออกสุดบล็อกไปวางบนสุด ถึงตาคนถัดไป 👉 (ปล่อยกลางทางบล็อกจะกลับเข้าไป) เกจเต็มหอถล่ม — คนทำหอล้ม <b>💀 โดนทำโทษ!</b>'],
  'vi': ['⚡ Trò phản xạ → <b>🧱 Jenga sói</b>. Chọn số người (1–8). Tháp 12 tầng, mỗi tầng 2 khối dài, trên đỉnh có một con sói.',
         '<b>Giữ một khối rồi kéo ra ngoài</b> (khối trái kéo sang trái, khối phải kéo sang phải). Kéo nhanh hoặc <b>ngón tay lệch lên xuống</b> (chấm thành 🔴) thì thanh 〰️ phía trên tăng. Khi bị kẹt giật thì kéo thật chậm!',
         'Rút hết thì khối lên đỉnh, đến người kế 👉 (buông giữa chừng khối sẽ trượt lại). Thanh đầy là tháp đổ — ai làm đổ <b>💀 bị phạt!</b>']}),
 'how-balance': ('bbTitle', '⚖️', 'how-jenga', {
  'ko': ['⚡ 순발력게임 → <b>⚖️ 늑대 균형 잡기</b>. 인원(1~8명)을 정해요. 폰을 평평하게 들고 시작해요.',
         '폰을 살살 기울여서 늑대가 접시에서 안 떨어지게 해요. <b>6초 버티면 "👉 넘겨!"</b> — 3초 동안 늑대가 멈추니까 그때 폰을 다음 사람에게. (PC는 마우스로 기울여요)',
         '넘길 때마다 <b>🔥 Lv 업</b>: 접시는 작고 미끄럽게, 바람은 세고 자주, 계속 미는 바람(파란 화살표)까지 🌬️ 늑대를 떨어뜨린 사람이 <b>💀 벌칙!</b>'],
  'en': ['⚡ Quick Games → <b>⚖️ Wolf Balance</b>. Choose players (1–8). Hold the phone flat and start.',
         'Tilt the phone gently so the wolf stays on the plate. <b>Last 6 seconds and "👉 Pass!" shows</b> — the wolf freezes for 3 seconds while you hand the phone on. (On PC, tilt with the mouse)',
         'Every pass is <b>🔥 Lv up</b>: smaller, slipperier plate, stronger and more frequent gusts, plus a steady push wind (blue arrows) 🌬️ Whoever drops the wolf gets a <b>💀 penalty!</b>'],
  'ja': ['⚡ 反射神経ゲーム → <b>⚖️ オオカミバランス</b>。人数（1〜8人）を決めます。スマホを平らに持ってスタート。',
         'スマホをそっと傾けてオオカミをお皿から落とさないように。<b>6秒耐えたら「👉 次へ！」</b>— 3秒間オオカミが止まるので、その間に次の人へ。（PCはマウスで傾けます）',
         '渡すたびに<b>🔥 Lvアップ</b>：お皿は小さく滑りやすく、突風は強く頻繁に、押し続ける風（青い矢印）も 🌬️ オオカミを落とした人が<b>💀罰ゲーム！</b>'],
  'zh': ['⚡ 反应游戏 → <b>⚖️ 狼平衡</b>。选好人数（1~8人）。把手机放平开始。',
         '轻轻倾斜手机，别让狼从盘子上掉下去。<b>坚持6秒出现"👉 传！"</b>— 狼会停住3秒，趁这时把手机交给下一个人。（电脑用鼠标倾斜）',
         '每传一次 <b>🔥 Lv 升级</b>：盘子更小更滑，阵风更强更频繁，还有一直在推的风（蓝色箭头）🌬️ 让狼掉下去的人 <b>💀 受罚！</b>'],
  'th': ['⚡ เกมความไว → <b>⚖️ ทรงตัวหมาป่า</b> เลือกจำนวนคน (1–8) ถือมือถือให้ราบแล้วเริ่ม',
         'เอียงมือถือเบาๆ อย่าให้หมาป่าตกจาน <b>อยู่ได้ 6 วินาทีจะขึ้น "👉 ส่งต่อ!"</b> — หมาป่าหยุด 3 วินาที ส่งมือถือให้คนถัดไปตอนนั้น (บน PC ใช้เมาส์เอียง)',
         'ส่งต่อแต่ละครั้ง <b>🔥 Lv เพิ่ม</b>: จานเล็กลงและลื่นขึ้น ลมกระโชกแรงและบ่อยขึ้น มีลมดันตลอด (ลูกศรสีฟ้า) 🌬️ ใครทำหมาป่าตก <b>💀 โดนทำโทษ!</b>'],
  'vi': ['⚡ Trò phản xạ → <b>⚖️ Giữ thăng bằng sói</b>. Chọn số người (1–8). Cầm điện thoại thẳng rồi bắt đầu.',
         'Nghiêng nhẹ điện thoại để sói không rơi khỏi đĩa. <b>Giữ được 6 giây sẽ hiện "👉 Chuyền!"</b> — sói đứng yên 3 giây, lúc đó đưa máy cho người kế. (Trên PC, nghiêng bằng chuột)',
         'Mỗi lần chuyền <b>🔥 lên Lv</b>: đĩa nhỏ và trơn hơn, gió giật mạnh và dày hơn, thêm gió đẩy liên tục (mũi tên xanh) 🌬️ Ai làm rơi sói <b>💀 bị phạt!</b>']}),
 'how-memory': ('mmTitle', '🃏', 'how-slap', {
  'ko': ['⚔️ 대결게임 → <b>🃏 늑대 짝 맞추기</b>. 인원(2~6명)과 카드 쌍 수(4~10쌍)를 정해요.',
         '자기 차례에 카드를 <b>2장 뒤집어서</b> 같은 늑대(머리 위 그림 👑🎩🎀🕶️… · 배경색)를 찾아요. 맞히면 <b>⭐ 1점 + 한 번 더!</b> 틀리면 잠깐 보여주고 다시 덮고 다음 사람 👉',
         '카드가 다 없어지면 끝 — <b>점수가 제일 낮은 사람이 💀 벌칙</b> (같으면 모두)!'],
  'en': ['⚔️ Versus Games → <b>🃏 Wolf Memory</b>. Choose players (2–6) and pairs (4–10).',
         'On your turn, <b>flip 2 cards</b> to find the same wolf (the item on its head 👑🎩🎀🕶️… · background color). Match = <b>⭐ 1 point + go again!</b> Miss = they show briefly, flip back, next player 👉',
         'When all cards are gone the game ends — <b>the lowest score gets a 💀 penalty</b> (ties all do)!'],
  'ja': ['⚔️ 対戦ゲーム → <b>🃏 オオカミ神経衰弱</b>。人数（2〜6人）とペアの数（4〜10ペア）を決めます。',
         '自分の番でカードを<b>2枚めくって</b>同じオオカミ（頭の上の絵 👑🎩🎀🕶️… ・背景色）を探します。当たれば<b>⭐1点＋もう1回！</b> 外れたら少し見せて戻し、次の人 👉',
         'カードが全部なくなったら終わり — <b>点数が一番低い人が💀罰ゲーム</b>（同点なら全員）！'],
  'zh': ['⚔️ 对战游戏 → <b>🃏 狼狼配对</b>。选好人数（2~6人）和配对数（4~10对）。',
         '轮到自己时<b>翻开2张牌</b>，找出一样的狼（头顶的图案 👑🎩🎀🕶️… · 背景色）。配对成功 <b>⭐ 得1分＋再翻一次！</b> 没配上会短暂显示后盖回去，换下一个人 👉',
         '牌全部翻完就结束 — <b>分数最低的人 💀 受罚</b>（同分都要受罚）！'],
  'th': ['⚔️ เกมดวล → <b>🃏 จับคู่หมาป่า</b> เลือกจำนวนคน (2–6) และจำนวนคู่ (4–10 คู่)',
         'ถึงตาตัวเอง<b>เปิดการ์ด 2 ใบ</b>หาหมาป่าที่เหมือนกัน (ของบนหัว 👑🎩🎀🕶️… · สีพื้นหลัง) ตรงกัน = <b>⭐ 1 แต้ม + เปิดต่อ!</b> ไม่ตรง = โชว์แป๊บเดียวแล้วคว่ำ ถึงตาคนถัดไป 👉',
         'การ์ดหมดก็จบ — <b>คนแต้มน้อยสุด 💀 โดนทำโทษ</b> (เท่ากันโดนทุกคน)!'],
  'vi': ['⚔️ Trò đối kháng → <b>🃏 Lật hình sói</b>. Chọn số người (2–6) và số cặp (4–10).',
         'Đến lượt thì <b>lật 2 lá</b> để tìm con sói giống nhau (đồ trên đầu 👑🎩🎀🕶️… · màu nền). Đúng = <b>⭐ 1 điểm + lật tiếp!</b> Sai = hiện một chút rồi úp lại, đến người kế 👉',
         'Hết bài là xong — <b>ai ít điểm nhất 💀 bị phạt</b> (bằng điểm thì đều bị phạt)!']}),
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
