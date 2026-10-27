# v1.5 새 문구 (ko, en, ja, zh, th, vi). 함수형은 ('fn', 'Kotlin 타입', [람다 6개])
S = {
 # ---- 탭 줄다리기 ----
 'tugTitle': ['탭 줄다리기', 'Tap Tug of War', 'タップ綱引き', '点击拔河', 'ชักเย่อแตะรัว', 'Kéo co chạm'],
 'tugSub': ['연타해서 깃발을 상대 쪽으로!', 'Mash to push the flag to their side!', '連打で旗を相手側へ！', '狂点把旗推向对方！', 'แตะรัวๆ ดันธงไปฝั่งตรงข้าม!', 'Chạm thật nhanh đẩy cờ sang đối thủ!'],
 'tugDesc': ['2명이 휴대폰을 사이에 두고 위·아래로 마주 앉아요.\nGO! 하면 내 쪽 화면을 미친 듯이 연타!\n가운데 깃발(🎀)을 상대편 끝 선까지 밀어내면 승리, 진 사람은 원샷!',
             'Two players sit facing each other, phone in the middle.\nOn GO!, mash your half of the screen like crazy!\nPush the flag (🎀) past the line on their side to win. Loser drinks!',
             '2人でスマホをはさんで上下に向かい合って座ります。\nGO!で自分側の画面をひたすら連打！\n真ん中の旗(🎀)を相手側の線まで押し込んだら勝ち、負けた人は一気！',
             '两个人隔着手机上下面对面坐。\n喊 GO! 后疯狂点自己这一半屏幕！\n把中间的旗(🎀)推过对方那条线就赢，输的人干杯！',
             'สองคนนั่งหันหน้าเข้าหากัน วางมือถือไว้ตรงกลาง\nพอ GO! ให้แตะรัวๆ ฝั่งของตัวเอง!\nดันธง (🎀) ไปถึงเส้นฝั่งตรงข้ามได้ก่อนชนะ คนแพ้หมดแก้ว!',
             'Hai người ngồi đối diện, điện thoại ở giữa.\nKhi GO! hãy chạm thật nhanh vào nửa màn hình của mình!\nĐẩy lá cờ (🎀) qua vạch bên đối thủ là thắng, người thua cạn ly!'],
 'tugMash': ['연타!! 👊', 'MASH!! 👊', '連打!! 👊', '狂点!! 👊', 'แตะรัว!! 👊', 'Chạm nhanh!! 👊'],
 'tugTaps': ['탭', 'taps', 'タップ', '次', 'ครั้ง', 'lần'],
 'winWord': ['👑 승리!', '👑 WIN!', '👑 勝ち!', '👑 胜利!', '👑 ชนะ!', '👑 Thắng!'],
 'shotWord': ['💀 원샷!', '💀 Drink!', '💀 一気!', '💀 干杯!', '💀 หมดแก้ว!', '💀 Cạn ly!'],
 'readyWord': ['준비…', 'Ready…', '準備…', '准备…', 'เตรียม…', 'Chuẩn bị…'],
 # ---- 슬랩 대결 ----
 'slapTitle': ['슬랩 대결', 'Slap Duel', 'スラップ対決', '拍手背对决', 'ดวลตบมือ', 'Đấu đập tay'],
 'slapSub': ['늑대 앞발로 손등 때리기!', 'Red Hands with wolf paws!', '狼の前足で手の甲たたき！', '用狼爪拍手背！', 'ตบหลังมือด้วยอุ้งเท้าหมาป่า!', 'Đập mu bàn tay bằng chân sói!'],
 'slapDesc': ['2명이 위·아래로 마주 앉아요.\n🛡️ 수비: 내 쪽 화면을 손가락으로 꾹 누르고 있다가, 공격이 오면 떼서 피하기!\n🐾 공격: 아무 때나 내 쪽을 터치하면 찰싹!\n맞으면 수비가 원샷, 피하면 공수 교대. 공격이 안 왔는데 떼면 움찔 반칙으로 원샷!',
              'Two players sit facing each other.\n🛡️ Defender: keep a finger pressed on your half, and lift it to dodge when the attack comes!\n🐾 Attacker: tap your half any time to slap!\nHit = defender drinks. Dodge = swap roles. Lift without an attack = flinch, drink!',
              '2人で上下に向かい合って座ります。\n🛡️ 守り：自分側の画面を指で押さえておき、攻撃が来たら離してよける！\n🐾 攻め：いつでも自分側をタップするとバチン！\n当たったら守りが一気、よけたら攻守交代。攻撃が来てないのに離したらビクッ反則で一気！',
              '两个人上下面对面坐。\n🛡️ 防守：手指一直按住自己这一半，攻击来了就松开躲开！\n🐾 进攻：随时点自己这一半就拍下去！\n拍中防守干杯，躲开就攻守互换。没攻击就松手算抖了犯规，干杯！',
              'สองคนนั่งหันหน้าเข้าหากัน\n🛡️ ฝ่ายรับ: ใช้นิ้วกดฝั่งตัวเองค้างไว้ พอโดนโจมตีให้ยกนิ้วหลบ!\n🐾 ฝ่ายรุก: แตะฝั่งตัวเองเมื่อไหร่ก็ได้เพื่อตบ!\nโดนตบ = ฝ่ายรับหมดแก้ว หลบได้ = สลับฝั่ง ยกนิ้วทั้งที่ยังไม่โดนโจมตี = สะดุ้ง หมดแก้ว!',
              'Hai người ngồi đối diện.\n🛡️ Phòng thủ: giữ ngón tay trên nửa màn hình của mình, khi bị đánh thì nhấc lên để né!\n🐾 Tấn công: chạm nửa của mình bất cứ lúc nào để đập!\nTrúng = người phòng thủ cạn ly. Né được = đổi vai. Nhấc tay khi chưa bị đánh = giật mình, cạn ly!'],
 'slapAtt': ['🐾 공격', '🐾 Attack', '🐾 攻め', '🐾 进攻', '🐾 ฝ่ายรุก', '🐾 Tấn công'],
 'slapDef': ['🛡️ 수비', '🛡️ Defend', '🛡️ 守り', '🛡️ 防守', '🛡️ ฝ่ายรับ', '🛡️ Phòng thủ'],
 'slapAttHint': ['아무 때나 터치하면 찰싹!', 'Tap any time to slap!', 'いつでもタップでバチン！', '随时点一下就拍！', 'แตะเมื่อไหร่ก็ตบ!', 'Chạm bất cứ lúc nào để đập!'],
 'slapAttWait': ['수비가 누를 때까지 기다려…', 'Wait for the defender to hold…', '守りが押さえるまで待って…', '等防守按住…', 'รอฝ่ายรับกดค้าง…', 'Chờ người phòng thủ giữ tay…'],
 'slapDefHint': ['꾹 누르고 있다가 피하기!', 'Hold, then lift to dodge!', '押さえておいて、離してよける！', '按住，松开就能躲！', 'กดค้างไว้ แล้วยกหลบ!', 'Giữ tay, nhấc lên để né!'],
 'slapDefHold': ['여기를 꾹 눌러!', 'Press and hold here!', 'ここを押さえて！', '按住这里！', 'กดค้างตรงนี้!', 'Giữ ngón tay ở đây!'],
 'slapHit': ['짝!! 명중 🎯', 'SMACK!! Hit 🎯', 'バチン!! 命中 🎯', '啪!! 拍中 🎯', 'เพี๊ยะ!! โดน 🎯', 'Bốp!! Trúng 🎯'],
 'slapOuch': ['아야!! 😵 원샷', 'Ouch!! 😵 Drink', 'いたっ!! 😵 一気', '好痛!! 😵 干杯', 'โอ๊ย!! 😵 หมดแก้ว', 'Á!! 😵 Cạn ly'],
 'slapMiss': ['헛손질… 공수 교대 🔄', 'Missed… swap roles 🔄', '空振り… 攻守交代 🔄', '扑空… 攻守互换 🔄', 'พลาด… สลับฝั่ง 🔄', 'Hụt… đổi vai 🔄'],
 'slapDodge': ['피했다! 🙌', 'Dodged! 🙌', 'よけた! 🙌', '躲开了! 🙌', 'หลบได้! 🙌', 'Né được! 🙌'],
 'slapFlinch': ['움찔 반칙! 🚫 원샷', 'Flinch! 🚫 Drink', 'ビクッ反則! 🚫 一気', '抖了犯规! 🚫 干杯', 'สะดุ้ง! 🚫 หมดแก้ว', 'Giật mình! 🚫 Cạn ly'],
 'slapFlinchAtt': ['상대가 움찔! 🤭', 'They flinched! 🤭', '相手がビクッ! 🤭', '对方抖了! 🤭', 'อีกฝ่ายสะดุ้ง! 🤭', 'Đối thủ giật mình! 🤭'],
 'slapSmack': ['짝!', 'SMACK!', 'バチン!', '啪!', 'เพี๊ยะ!', 'Bốp!'],
 # ---- 글로벌 킹스컵 ----
 'kcTitle': ['글로벌 킹스컵', "Global King's Cup", 'グローバル・キングスカップ', '全球国王杯', 'คิงส์คัพสากล', "King's Cup toàn cầu"],
 'kcSub': ['그림만 봐도 아는 킹스컵', "King's Cup with picture rules", '絵で分かるキングスカップ', '看图就懂的国王杯', 'คิงส์คัพแบบดูรูปก็เข้าใจ', "King's Cup bằng hình, ai cũng hiểu"],
 'kcDesc': ['카드 더미를 터치해서 한 장씩 뽑아요. 카드마다 그림으로 할 일이 나와요.\n👑 K가 나올 때마다 가운데 킹스컵 잔에 술을 조금씩 따르고, 4번째 K를 뽑은 사람이 킹스컵 원샷!',
            'Tap the deck to draw a card. Each card shows what to do with a picture.\n👑 Every King: pour a little into the King\'s Cup. Whoever draws the 4th King drinks the whole cup!',
            'カードの山をタップして1枚ずつ引きます。カードごとにやることが絵で出ます。\n👑 Kが出るたびに真ん中のキングスカップに少しずつ注ぎ、4枚目のKを引いた人がキングスカップを一気！',
            '点牌堆一张一张抽。每张牌都会用图告诉你要做什么。\n👑 每抽到 K 就往中间的国王杯里倒一点，抽到第 4 张 K 的人把国王杯干了！',
            'แตะกองไพ่เพื่อจั่วทีละใบ แต่ละใบจะมีรูปบอกว่าต้องทำอะไร\n👑 ทุกครั้งที่ได้ K ให้รินเหล้าใส่แก้วคิงส์คัพตรงกลางนิดหน่อย ใครจั่ว K ใบที่ 4 ต้องหมดแก้วคิงส์คัพ!',
            'Chạm vào chồng bài để rút từng lá. Mỗi lá hiện việc cần làm bằng hình.\n👑 Mỗi lần ra K thì rót một ít vào ly King\'s Cup ở giữa, ai rút lá K thứ 4 phải uống cạn ly đó!'],
 'kcNames': ['폭포|지목|나|화장실 프리패스|댄스 릴레이|다 같이 짠|하늘|짝꿍|끝말잇기|카테고리|규칙 만들기|질문 마스터|킹스컵',
             'Waterfall|You|Me|Toilet pass|Dance relay|Cheers all|Heaven|Mate|Word chain|Categories|Make a rule|Question master|King\'s Cup',
             '滝|指名|自分|トイレ免除券|ダンスリレー|みんなで乾杯|天|相棒|しりとり|カテゴリー|ルール作り|質問マスター|キングスカップ',
             '瀑布|点名|自己|厕所通行证|舞蹈接力|一起干杯|举手|搭档|成语接龙|类别|定规则|提问大师|国王杯',
             'น้ำตก|ชี้คน|ตัวเอง|บัตรผ่านห้องน้ำ|เต้นต่อกัน|ชนแก้วทุกคน|ยกมือ|คู่หู|ต่อคำ|หมวดหมู่|ตั้งกฎ|เจ้าแห่งคำถาม|คิงส์คัพ',
             'Thác nước|Chỉ định|Tôi|Vé đi vệ sinh|Nhảy tiếp sức|Cùng cụng ly|Giơ tay|Bạn đồng hành|Nối từ|Chủ đề|Đặt luật|Vua câu hỏi|King\'s Cup'],
 'kcDescs': ['다 같이 마시기 시작 → 앞사람이 멈춰야 멈춤|한 명 골라서 마시게 하기|내가 마시기|화장실 1번 다녀와도 OK|내가 춤 → 다음 사람이 따라 하고 하나 추가|모두 한 잔!|손 제일 늦게 든 사람 마시기|짝꿍 정하기 · 내가 마실 때 같이 마심|막히는 사람 마시기|주제 정하고 돌아가며 말하기, 막히면 마시기|새 규칙 하나 (어기면 마시기)|내 질문에 대답하면 마시기|가운데 잔에 조금 따르기',
             'Everyone drinks → you can stop only after the person before you stops|Pick someone to drink|You drink|Go to the toilet once, no penalty|Do a move → next person copies it and adds one|Everyone drinks!|Last to raise a hand drinks|Pick a mate who drinks whenever you do|Whoever gets stuck drinks|Pick a topic, take turns naming things, stuck = drink|Make a new rule (break it = drink)|Anyone who answers your question drinks|Pour a little into the middle cup',
             'みんなで飲み始め → 前の人が止まるまで止まれない|1人選んで飲ませる|自分が飲む|トイレに1回行ってもOK|自分が踊る → 次の人がまねして1つ追加|みんなで1杯！|最後に手を挙げた人が飲む|相棒を決める · 自分が飲むとき一緒に飲む|詰まった人が飲む|お題を決めて順番に言う、詰まったら飲む|新しいルールを1つ（破ったら飲む）|自分の質問に答えた人が飲む|真ん中の杯に少し注ぐ',
             '大家一起开始喝 → 前面的人停了你才能停|选一个人喝|自己喝|可以去一次厕所|我跳一个动作 → 下一个人模仿再加一个|大家一起喝一杯！|最后举手的人喝|选一个搭档 · 我喝时他也喝|卡住的人喝|定一个主题轮流说，卡住就喝|定一条新规则（违反就喝）|回答我问题的人喝|往中间的杯子里倒一点',
             'ทุกคนเริ่มดื่ม → หยุดได้เมื่อคนก่อนหน้าหยุด|เลือกคนดื่ม 1 คน|ตัวเองดื่ม|ไปห้องน้ำได้ 1 ครั้ง|เราเต้น → คนถัดไปเต้นตามแล้วเพิ่ม 1 ท่า|ทุกคนดื่ม 1 แก้ว!|คนยกมือช้าที่สุดดื่ม|เลือกคู่หู · เราดื่มเมื่อไหร่ คู่หูดื่มด้วย|ใครต่อไม่ได้ดื่ม|ตั้งหมวด ผลัดกันพูด ใครติดดื่ม|ตั้งกฎใหม่ 1 ข้อ (ฝ่าฝืนดื่ม)|ใครตอบคำถามเราต้องดื่ม|รินลงแก้วกลางนิดหน่อย',
             'Mọi người cùng uống → chỉ được dừng khi người trước dừng|Chọn một người uống|Bạn uống|Được đi vệ sinh 1 lần|Bạn nhảy → người sau bắt chước và thêm 1 động tác|Mọi người cùng uống!|Ai giơ tay cuối cùng thì uống|Chọn bạn đồng hành, bạn uống thì họ cũng uống|Ai bí thì uống|Chọn chủ đề, lần lượt kể tên, bí thì uống|Đặt một luật mới (vi phạm thì uống)|Ai trả lời câu hỏi của bạn thì uống|Rót một ít vào ly ở giữa'],
 'kcTap': ['카드 더미를 터치!', 'Tap the deck!', '山札をタップ！', '点牌堆！', 'แตะกองไพ่!', 'Chạm vào chồng bài!'],
 'kcLeft': ('fn', '(Int) -> String', ['{ "${it}장 남음" }', '{ "$it left" }', '{ "残り${it}枚" }', '{ "剩 $it 张" }', '{ "เหลือ $it ใบ" }', '{ "Còn $it lá" }']),
 'kcLastKing': ['마지막 킹! 킹스컵 원샷!!', "Last King! Drink the King's Cup!!", '最後のK！キングスカップ一気!!', '最后一张K！国王杯干了!!', 'K ใบสุดท้าย! หมดแก้วคิงส์คัพ!!', "Lá K cuối! Cạn ly King's Cup!!"],
 'kcLastKingSub': ['가운데 잔을 다 마시기 🍺', 'Drink the whole middle cup 🍺', '真ん中の杯を飲み干す 🍺', '把中间的杯子喝光 🍺', 'ดื่มแก้วกลางให้หมด 🍺', 'Uống hết ly ở giữa 🍺'],
 'kcRules': ['📋 규칙 보기', '📋 Rules', '📋 ルール', '📋 规则', '📋 กติกา', '📋 Luật'],
 'kcShuffle': ['🔀 새로 섞기', '🔀 Reshuffle', '🔀 シャッフル', '🔀 重新洗牌', '🔀 สับไพ่ใหม่', '🔀 Xáo lại'],
 'kcEmpty': ['카드를 다 썼어요!', 'Out of cards!', 'カードがなくなりました！', '牌抽完了！', 'ไพ่หมดแล้ว!', 'Hết bài rồi!'],
 # ---- 맥주 거품 채우기 ----
 'beerTitle': ['맥주 거품 채우기', 'Beer Pour', 'ビール注ぎ', '倒啤酒', 'รินเบียร์', 'Rót bia'],
 'beerSub': ['넘치기 직전에 멈춰!', 'Stop right before it overflows!', 'あふれる直前で止めて！', '快溢出来前停下！', 'หยุดก่อนล้นแก้ว!', 'Dừng ngay trước khi tràn!'],
 'beerDesc': ['각자 내 맥주잔을 지켜봐요. 맥주가 점점 빨리 차올라요!\n빨간 점선(여기까지!)에 맞춰서 터치해 멈추기.\n선에 제일 가깝게 멈춘 사람 1등, 넘치면 바로 꼴찌!',
              'Watch your own glass. The beer fills faster and faster!\nTap to stop it at the red dashed line.\nClosest to the line wins. Overflow = last place!',
              'それぞれ自分のジョッキを見ていてね。ビールがどんどん速く注がれる！\n赤い点線（ここまで！）に合わせてタップで止める。\n線に一番近い人が1位、あふれたら即ビリ！',
              '盯好自己的啤酒杯，啤酒会越倒越快！\n对准红色虚线（到这里！）点一下停下。\n最接近线的人第一，溢出来直接垫底！',
              'ดูแก้วเบียร์ของตัวเองไว้ เบียร์จะเติมเร็วขึ้นเรื่อยๆ!\nแตะให้หยุดตรงเส้นประสีแดง\nใครหยุดใกล้เส้นที่สุดชนะ ล้นแก้ว = ที่โหล่ทันที!',
              'Mỗi người nhìn ly bia của mình. Bia rót ngày càng nhanh!\nChạm để dừng đúng vạch đứt màu đỏ.\nGần vạch nhất thắng, tràn ly = bét ngay!'],
 'beerLine': ['여기까지!', 'Stop here!', 'ここまで！', '到这里！', 'ถึงตรงนี้!', 'Đến đây!'],
 'beerTap': ['터치해서 멈춰!', 'Tap to stop!', 'タップで止めて！', '点一下停下！', 'แตะเพื่อหยุด!', 'Chạm để dừng!'],
 'beerOver': ['넘쳤다! 💦', 'Overflow! 💦', 'あふれた! 💦', '溢出来了! 💦', 'ล้นแล้ว! 💦', 'Tràn rồi! 💦'],
 # ---- 동전 던지기 ----
 'coinTitle': ['동전 던지기', 'Coin Toss', 'コイントス', '抛硬币', 'โยนเหรียญ', 'Tung đồng xu'],
 'coinSub': ['늑대 얼굴? 늑대 엉덩이?', 'Wolf face or wolf butt?', 'オオカミの顔？おしり？', '狼脸？还是狼屁股？', 'หน้าหมาป่า หรือ ก้นหมาป่า?', 'Mặt sói hay mông sói?'],
 'coinDesc': ['각자 내 자리에서 앞(🐺 늑대 얼굴)이나 뒤(🍑 늑대 엉덩이)를 골라요.\n모두 고르면 3초 뒤 동전이 빙글빙글~\n틀린 사람은 원샷! 가끔(15%) 동전이 세로로 서면 전원 틀림!',
              'Everyone picks heads (🐺 wolf face) or tails (🍑 wolf butt) in their own spot.\nWhen all have picked, the coin spins after 3 seconds!\nWrong = drink! Sometimes (15%) the coin lands on its edge — everyone is wrong!',
              'それぞれ自分の場所で表（🐺 オオカミの顔）か裏（🍑 オオカミのおしり）を選びます。\nみんな選んだら3秒後にコインがくるくる〜\nはずれた人は一気！ときどき（15%）コインが立つと全員はずれ！',
              '每个人在自己的位置选正面（🐺 狼脸）或反面（🍑 狼屁股）。\n都选好后 3 秒硬币开始旋转~\n猜错的人干杯！有时（15%）硬币会立起来，全员猜错！',
              'ทุกคนเลือกหัว (🐺 หน้าหมาป่า) หรือ ก้อย (🍑 ก้นหมาป่า) ในช่องของตัวเอง\nพอเลือกครบ อีก 3 วินาทีเหรียญจะหมุนติ้วๆ~\nทายผิดหมดแก้ว! บางครั้ง (15%) เหรียญจะตั้งขึ้น ทุกคนผิดหมด!',
              'Mỗi người chọn mặt sấp (🐺 mặt sói) hoặc ngửa (🍑 mông sói) ở chỗ của mình.\nKhi tất cả đã chọn, 3 giây sau đồng xu sẽ xoay tít~\nĐoán sai thì cạn ly! Thỉnh thoảng (15%) đồng xu đứng thẳng — tất cả đều sai!'],
 'coinHead': ['앞', 'Heads', '表', '正', 'หัว', 'Sấp'],
 'coinTail': ['뒤', 'Tails', '裏', '反', 'ก้อย', 'Ngửa'],
 'coinPick': ['골라!', 'Pick!', '選んで！', '选一个！', 'เลือก!', 'Chọn đi!'],
 'coinRight': ['⭕ 맞춤!', '⭕ Right!', '⭕ 当たり!', '⭕ 猜对!', '⭕ ถูก!', '⭕ Đúng!'],
 'coinWrong': ['❌ 틀림!', '❌ Wrong!', '❌ はずれ!', '❌ 猜错!', '❌ ผิด!', '❌ Sai!'],
 'coinResHead': ['🐺 앞 (얼굴)!', '🐺 Heads (face)!', '🐺 表（顔）!', '🐺 正面（脸）!', '🐺 หัว (หน้า)!', '🐺 Sấp (mặt)!'],
 'coinResTail': ['🍑 뒤 (엉덩이)!', '🍑 Tails (butt)!', '🍑 裏（おしり）!', '🍑 反面（屁股）!', '🍑 ก้อย (ก้น)!', '🍑 Ngửa (mông)!'],
 'coinEdge': ['😱 세로로 섰다! 전원 틀림', '😱 It landed on its edge! Everyone loses', '😱 コインが立った！全員はずれ', '😱 硬币立起来了！全员猜错', '😱 เหรียญตั้งขึ้น! ผิดทุกคน', '😱 Đồng xu đứng thẳng! Tất cả đều sai'],
 # ---- 연락처 QR ----
 'qrTitle': ['내 연락처 QR', 'My Contact QR', '連絡先QR', '我的联系方式二维码', 'QR ติดต่อของฉัน', 'QR liên hệ của tôi'],
 'qrEdit': ['✏️ 내 SNS 입력', '✏️ Edit my SNS', '✏️ SNSを入力', '✏️ 编辑我的社交账号', '✏️ ใส่โซเชียลของฉัน', '✏️ Nhập mạng xã hội'],
 'qrPrivacy': ['🔒 입력한 정보는 이 휴대폰에만 저장돼요. 어디에도 안 보내고, 인터넷 없어도 QR이 만들어져요.', '🔒 What you enter is saved only on this phone. Nothing is sent anywhere, and QR codes work offline.', '🔒 入力した情報はこのスマホにだけ保存されます。どこにも送らず、ネットなしでもQRが作れます。', '🔒 输入的信息只保存在这部手机上，不会发送到任何地方，没网也能生成二维码。', '🔒 ข้อมูลที่ใส่จะเก็บไว้ในมือถือเครื่องนี้เท่านั้น ไม่ส่งไปไหน และสร้าง QR ได้แม้ไม่มีเน็ต', '🔒 Thông tin bạn nhập chỉ lưu trên điện thoại này. Không gửi đi đâu cả, không có mạng vẫn tạo được QR.'],
 'qrEmpty': ['아직 입력한 SNS가 없어요', 'No SNS added yet', 'まだSNSが入力されていません', '还没有添加社交账号', 'ยังไม่ได้ใส่โซเชียล', 'Chưa nhập mạng xã hội nào'],
 'qrScan': ['카메라로 찍으면 바로 연결 📷', 'Scan with a camera to connect 📷', 'カメラで読み取るとすぐつながる 📷', '用相机一扫就能加 📷', 'สแกนด้วยกล้องเพื่อเชื่อมต่อ 📷', 'Quét bằng camera để kết nối 📷'],
 'qrSwipe': ['← 옆으로 넘기면 다른 SNS →', '← Swipe for other SNS →', '← 横にスワイプで他のSNS →', '← 左右滑动切换 →', '← ปัดเพื่อดูโซเชียลอื่น →', '← Vuốt để xem mạng khác →'],
 'qrClose': ['✕ 닫기', '✕ Close', '✕ 閉じる', '✕ 关闭', '✕ ปิด', '✕ Đóng'],
 'qrSave': ['저장', 'Save', '保存', '保存', 'บันทึก', 'Lưu'],
 'qrPhone': ['전화번호 (국가번호 포함, 예: +82 10…)', 'Phone with country code (e.g. +1 555…)', '電話番号（国番号付き、例: +81 90…）', '手机号（含国家代码，如 +86 138…）', 'เบอร์โทร (มีรหัสประเทศ เช่น +66 8…)', 'Số điện thoại (kèm mã quốc gia, vd +84 9…)'],
 'qrId': ['아이디', 'ID / username', 'ID', '账号', 'ไอดี', 'ID'],
 'qrLink': ['링크', 'Link', 'リンク', '链接', 'ลิงก์', 'Liên kết'],
 # ---- 팁 ----
 'tipBtn': ['💁 팁', '💁 Tip', '💁 チップ', '💁 小费', '💁 ทิป', '💁 Tip'],
 'tipTitle': ['💁 팁 계산', '💁 Tip Calculator', '💁 チップ計算', '💁 小费计算', '💁 คำนวณทิป', '💁 Tính tiền tip'],
 'tipAmount': ['금액', 'Amount', '金額', '金额', 'จำนวนเงิน', 'Số tiền'],
 'tipLabel': ('fn', '(Int) -> String', ['{ "팁 ${it}%" }', '{ "Tip $it%" }', '{ "チップ $it%" }', '{ "小费 $it%" }', '{ "ทิป $it%" }', '{ "Tip $it%" }']),
 'tipTotal': ['팁 포함 합계', 'Total with tip', 'チップ込み合計', '含小费合计', 'รวมทิป', 'Tổng kèm tip'],
 'tipSplit': ['🍻 팁 포함 N빵', '🍻 Split with tip', '🍻 チップ込みで割り勘', '🍻 含小费AA', '🍻 หารบิลรวมทิป', '🍻 Chia kèm tip'],
}
# 메뉴 부제 바꾸기
MENU = {
 'menuCardsSub': ['Up/Down · 호치민 · 킹스컵', "Up/Down · Ho Chi Minh · King's Cup", 'Up/Down · ホーチミン · キングスカップ', 'Up/Down · 胡志明 · 国王杯', 'Up/Down · โฮจิมินห์ · คิงส์คัพ', "Up/Down · Hồ Chí Minh · King's Cup"],
 'menuLuckSub': ['사다리게임 · 돌림판 · 폭탄돌리기 · 동전 던지기', 'Ladder Game · Roulette · Hot Bomb · Coin Toss', 'あみだくじ · ルーレット · 爆弾回し · コイントス', '爬梯子 · 转盘 · 传炸弹 · 抛硬币', 'เกมบันได · วงล้อ · ส่งระเบิด · โยนเหรียญ', 'Leo thang · Vòng quay · Chuyền bom · Tung đồng xu'],
 'menuBattleSub': ['4목 · 탭 줄다리기 · 슬랩 대결', 'Connect 4 · Tap Tug of War · Slap Duel', '4目並べ · タップ綱引き · スラップ対決', '四子棋 · 点击拔河 · 拍手背对决', 'เรียง 4 · ชักเย่อแตะรัว · ดวลตบมือ', 'Nối 4 · Kéo co chạm · Đấu đập tay'],
 'menuQuickSub': ['반응속도 대결 · 색 반응 대결 · 스탑워치 · 맥주 거품 채우기', 'Reaction Duel · Color Reaction · Stopwatch · Beer Pour', '反応速度対決 · 色反応対決 · ストップウォッチ · ビール注ぎ', '反应速度对决 · 颜色反应对决 · 秒表 · 倒啤酒', 'ดวลความไว · ดวลสี · นาฬิกาจับเวลา · รินเบียร์', 'Đấu phản xạ · Đấu màu · Bấm giờ · Rót bia'],
}
if __name__ == '__main__':
    import json, re
    P = 'app/src/main/java/com/wolfgames/app/i18n/Strings.kt'
    src = open(P, encoding='utf-8').read()
    LANGS = ['Ko', 'En', 'Ja', 'Zh', 'Th', 'Vi']
    kq = lambda v: json.dumps(v, ensure_ascii=False).replace('$', '\\$')
    # 선언
    decl = ''.join(f'    lateinit var {k}: {v[1] if isinstance(v, tuple) else "String"}\n' for k, v in S.items() if f'lateinit var {k}:' not in src)
    i = src.index('\n}\n', src.index('class Strings {')); src = src[:i + 1] + '    // v1.5\n' + decl + src[i + 1:]
    for li, L in enumerate(LANGS):
        head = f'val {L}Strings = Strings().apply {{'
        st = src.index(head); en = src.index('\n}\n', st)
        body = src[st:en]
        add = ''
        for k, v in S.items():
            if re.search(rf'^    {k} = ', body, re.M): continue
            add += f'    {k} = {v[2][li] if isinstance(v, tuple) else kq(v[li])}\n'
        for k, v in MENU.items():
            body = re.sub(rf'^    {k} = .*$', lambda m: f'    {k} = {kq(v[li])}', body, flags=re.M)
        src = src[:st] + body + '\n    // v1.5\n' + add.rstrip('\n') + src[en:]
    open(P, 'w', encoding='utf-8').write(src)
    print('ok', len(S))
