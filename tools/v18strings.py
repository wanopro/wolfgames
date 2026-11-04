# v1.8 새 문구 (ko, en, ja, zh, th, vi) — 글 없이 하는 게임: 탑 쌓기 · 따라 하기 · 폭탄 선 자르기
# 사용: python3 tools/v18strings.py → python3 tools/gen_web_i18n.py → python3 tools/gen_dual.py
S = {
 # 🏗️ 탑 쌓기 (⚡ 순발력)
 'tsTitle': ['탑 쌓기', 'Tower Stack', 'タワー積み', '叠高塔', 'ต่อตึก', 'Xếp tháp'],
 'tsSub': ['딱 맞춰 쌓아라! 무너뜨리면 원샷', 'Stack it right! Topple it, drink it', 'ぴったり積め！崩したら一気', '对准叠好！弄倒就干杯', 'ต่อให้ตรง! ทำพังต้องดื่ม', 'Xếp cho khéo! Làm đổ là uống'],
 'tsDesc': ['좌우로 움직이는 블록을 터치해서 떨어뜨려요. 아래 블록 밖으로 삐져나간 부분은 잘려 나가요.\n한 번 누르면 폰을 다음 사람에게 넘겨요 👉\n탑을 무너뜨린 사람이 🍺 마셔요!',
            'Tap to drop the sliding block. Whatever hangs over the block below gets cut off.\nAfter one tap, pass the phone to the next person 👉\nWhoever topples the tower — 🍺 drink!',
            '左右に動くブロックをタッチして落とします。下のブロックからはみ出た部分は切れて落ちます。\n1回押したら次の人にスマホを渡して 👉\nタワーを崩した人が🍺飲む！',
            '点击屏幕让左右移动的方块落下。超出下面方块的部分会被切掉。\n点一次就把手机传给下一个人 👉\n把塔弄倒的人 🍺 喝！',
            'แตะเพื่อปล่อยบล็อกที่เลื่อนไปมา ส่วนที่ล้นออกนอกบล็อกข้างล่างจะถูกตัดทิ้ง\nแตะครั้งเดียวแล้วส่งมือถือให้คนถัดไป 👉\nใครทำตึกพัง — 🍺 ดื่ม!',
            'Chạm để thả khối đang trượt. Phần thò ra ngoài khối bên dưới sẽ bị cắt.\nChạm một lần rồi chuyền điện thoại cho người kế tiếp 👉\nAi làm đổ tháp — 🍺 uống!'],
 'tsFloor': ('fn', '(Int) -> String', ['{ "${it}층" }', '{ "Floor $it" }', '{ "${it}階" }', '{ "第${it}层" }', '{ "ชั้น $it" }', '{ "Tầng $it" }']),
 'tsTap': ['터치해서 떨어뜨리기 → 다음 사람 👉', 'Tap to drop → next person 👉', 'タッチで落とす → 次の人 👉', '点击放下 → 下一个人 👉', 'แตะเพื่อปล่อย → คนถัดไป 👉', 'Chạm để thả → người kế tiếp 👉'],
 'tsFall': ['💥 와르르! 무너졌다!', '💥 CRASH! It fell!', '💥 ガラガラ！崩れた！', '💥 哗啦！塔倒了！', '💥 โครม! พังแล้ว!', '💥 Ầm! Đổ rồi!'],
 # 🔴 따라 하기 (⚡ 순발력)
 'smTitle': ['따라 하기', 'Copy Me', 'まねっこ', '跟着按', 'ทำตาม', 'Bắt chước'],
 'smSub': ['반짝인 순서 그대로! 틀리면 마시기', 'Repeat the flashes! Miss and drink', '光った順にそのまま！間違えたら飲む', '照着亮的顺序按！错了就喝', 'กดตามลำดับที่กะพริบ! ผิดต้องดื่ม', 'Bấm đúng thứ tự nháy! Sai là uống'],
 'smDesc': ['색 버튼이 소리와 함께 순서대로 반짝여요. 잘 보고 똑같은 순서로 눌러요.\n성공하면 순서가 하나 늘어나고 폰을 다음 사람에게 넘겨요 👉\n틀린 사람이 🍺 마셔요! (소리 켜면 더 쉬워요 🔊)',
            'The color buttons flash in order with a sound. Watch, then press them in the same order.\nGet it right and the sequence grows by one — pass the phone on 👉\nWhoever misses — 🍺 drink! (Turn sound on to make it easier 🔊)',
            '色ボタンが音と一緒に順番に光ります。よく見て同じ順番で押そう。\n成功すると1つ増えて、次の人にスマホを渡して 👉\n間違えた人が🍺飲む！（音をオンにすると簡単 🔊）',
            '彩色按钮会随着声音按顺序亮起。看好后按同样的顺序按。\n成功后顺序多一个，把手机传给下一个人 👉\n按错的人 🍺 喝！（开声音更容易 🔊）',
            'ปุ่มสีจะกะพริบตามลำดับพร้อมเสียง ดูให้ดีแล้วกดตามลำดับเดิม\nถ้าถูก ลำดับจะเพิ่มขึ้นหนึ่ง แล้วส่งมือถือให้คนถัดไป 👉\nใครกดผิด — 🍺 ดื่ม! (เปิดเสียงจะง่ายขึ้น 🔊)',
            'Các nút màu nháy theo thứ tự kèm âm thanh. Nhìn kỹ rồi bấm đúng thứ tự đó.\nĐúng thì chuỗi dài thêm một, chuyền điện thoại cho người kế tiếp 👉\nAi bấm sai — 🍺 uống! (Bật âm thanh sẽ dễ hơn 🔊)'],
 'smLevel': ('fn', '(Int) -> String', ['{ "${it}단계" }', '{ "Level $it" }', '{ "レベル $it" }', '{ "第${it}关" }', '{ "ด่าน $it" }', '{ "Cấp $it" }']),
 'smWatch': ['👀 잘 봐요', '👀 Watch', '👀 よく見て', '👀 看好', '👀 ดูให้ดี', '👀 Xem kỹ'],
 'smGo': ['👆 따라 눌러요!', '👆 Your turn!', '👆 まねして押して！', '👆 跟着按！', '👆 กดตามเลย!', '👆 Bấm theo!'],
 'smOk': ['⭕ 성공! 다음 사람 👉', '⭕ Nice! Next person 👉', '⭕ 成功！次の人 👉', '⭕ 成功！下一个人 👉', '⭕ ถูก! คนถัดไป 👉', '⭕ Đúng! Người kế tiếp 👉'],
 'smWrong': ['❌ 틀렸다!', '❌ Wrong!', '❌ 間違い！', '❌ 按错了！', '❌ ผิด!', '❌ Sai rồi!'],
 # ✂️ 폭탄 선 자르기 (🍀 운빨)
 'wcTitle': ['폭탄 선 자르기', 'Cut the Wire', '爆弾の線切り', '剪炸弹线', 'ตัดสายระเบิด', 'Cắt dây bom'],
 'wcSub': ['진짜 선을 자르면 펑! 운에 맡겨', 'Cut the wrong one — BOOM! Trust your luck', '本物の線を切ったらドカン！運まかせ', '剪到真线就爆炸！全靠运气', 'ตัดโดนสายจริง ตูม! วัดดวง', 'Cắt trúng dây thật là BÙM! Phó mặc số phận'],
 'wcDesc': ['폭탄에 색깔 선이 여러 개 있어요. 그중 하나만 진짜예요.\n돌아가면서 선을 하나씩 터치해서 잘라요 ✂️ 자르고 나면 다음 사람에게 👉\n진짜 선을 자른 사람이 💥 원샷!',
            'The bomb has several colored wires. Only one of them is real.\nTake turns tapping a wire to cut it ✂️ then pass to the next person 👉\nWhoever cuts the real one — 💥 bottoms up!',
            '爆弾に色の線がいくつかあります。本物は1本だけ。\n順番に線を1本タッチして切ります ✂️ 切ったら次の人へ 👉\n本物の線を切った人が💥一気！',
            '炸弹上有好几根彩色的线，只有一根是真的。\n轮流点一根线把它剪断 ✂️ 剪完传给下一个人 👉\n剪到真线的人 💥 干杯！',
            'ระเบิดมีสายหลายสี แต่มีสายจริงแค่เส้นเดียว\nผลัดกันแตะสายเพื่อตัดทีละเส้น ✂️ ตัดแล้วส่งให้คนถัดไป 👉\nใครตัดโดนสายจริง — 💥 หมดแก้ว!',
            'Quả bom có nhiều dây màu. Chỉ một dây là thật.\nLần lượt chạm một dây để cắt ✂️ rồi chuyền cho người kế tiếp 👉\nAi cắt trúng dây thật — 💥 cạn ly!'],
 'wcWires': ['선 개수', 'Wires', '線の数', '线的数量', 'จำนวนสาย', 'Số dây'],
 'wcTap': ['✂️ 선을 하나 터치해서 잘라요', '✂️ Tap a wire to cut it', '✂️ 線を1本タッチして切る', '✂️ 点一根线剪断', '✂️ แตะสายเพื่อตัด', '✂️ Chạm một dây để cắt'],
 'wcSafe': ['😮‍💨 휴~ 살았다! 다음 사람 👉', '😮‍💨 Phew~ safe! Next person 👉', '😮‍💨 ふぅ〜セーフ！次の人 👉', '😮‍💨 呼~安全！下一个人 👉', '😮‍💨 เฮ้อ~ รอด! คนถัดไป 👉', '😮‍💨 Phù~ thoát! Người kế tiếp 👉'],
 'wcBoom': ['💥 펑! 진짜 선이었다!', '💥 BOOM! That was the one!', '💥 ドカン！本物だった！', '💥 砰！就是这根！', '💥 ตูม! สายจริง!', '💥 BÙM! Đúng dây thật!'],
 'wcLeft': ('fn', '(Int) -> String', ['{ "남은 선 $it" }', '{ "$it wires left" }', '{ "残り $it 本" }', '{ "还剩 $it 根" }', '{ "เหลือ $it สาย" }', '{ "Còn $it dây" }']),
 # 🍺 늑대컬링 (⚡ 순발력)
 'cuTitle': ['늑대컬링', 'Wolf Curling', 'オオカミカーリング', '狼冰壶', 'เคิร์ลลิงหมาป่า', 'Bi đá sói'],
 'cuSub': ['늑대 잔을 당겼다 놓아 끝에 딱!', 'Pull back the wolf mug, let go, stop at the edge!', 'オオカミジョッキを引いて放して端ギリギリに！', '把狼杯往后拉再松手，停在桌边！', 'ดึงแก้วหมาป่าแล้วปล่อย ให้หยุดชิดขอบ!', 'Kéo cốc sói rồi thả, dừng sát mép!'],
 'cuDesc': ['테이블 아래쪽의 늑대 맥주잔을 손가락으로 뒤로(아래로) 당겼다가 놓으면 앞으로 미끄러져요. 많이 당길수록 멀리 가요.\n테이블 끝에 가까울수록 좋아요. 끝을 넘으면 💦 떨어져요.\n돌아가며 한 번씩! 떨어진 사람은 모두 🍺 원샷, 아무도 안 떨어졌으면 제일 먼 사람이 마셔요.',
            'Pull the wolf mug at the bottom of the table back (down) with your finger and let go — it slides forward. The more you pull, the farther it goes.\nThe closer to the edge, the better. Go past it and it 💦 falls off.\nOne turn each! Everyone who falls off drinks 🍺, and if nobody does, the farthest one drinks.',
            'テーブル下のオオカミジョッキを指で後ろ（下）に引いて放すと前に滑ります。たくさん引くほど遠くへ。\nテーブルの端に近いほど良い。端を越えると💦落ちます。\n1人1回ずつ！落ちた人は全員🍺一気、誰も落ちなければ一番遠い人が飲む。',
            '用手指把桌子下方的狼啤酒杯往后（往下）拉，松手它就会往前滑。拉得越多滑得越远。\n越靠近桌边越好。超过桌边就会 💦 掉下去。\n每人轮流一次！掉下去的人全部 🍺 干杯，没人掉的话离桌边最远的人喝。',
            'ใช้นิ้วดึงแก้วเบียร์หมาป่าด้านล่างโต๊ะไปข้างหลัง (ลง) แล้วปล่อย มันจะไถลไปข้างหน้า ยิ่งดึงมากยิ่งไปไกล\nยิ่งใกล้ขอบโต๊ะยิ่งดี ถ้าเลยขอบจะ 💦 ตกลงไป\nผลัดกันคนละครั้ง! ใครตกต้อง 🍺 หมดแก้วทุกคน ถ้าไม่มีใครตก คนที่ไกลที่สุดดื่ม',
            'Dùng ngón tay kéo cốc bia sói ở cuối bàn về sau (xuống dưới) rồi thả — nó trượt về phía trước. Kéo càng nhiều càng đi xa.\nCàng sát mép bàn càng tốt. Vượt quá mép là 💦 rơi.\nMỗi người một lượt! Ai rơi đều 🍺 cạn ly, nếu không ai rơi thì người xa nhất uống.'],
 'cuHint': ['👇 뒤로 당겼다가 놓기', '👇 Pull back, then let go', '👇 後ろに引いて放す', '👇 往后拉再松手', '👇 ดึงลงแล้วปล่อย', '👇 Kéo xuống rồi thả'],
 'cuFall': ['💦 떨어졌다!', '💦 It fell off!', '💦 落ちた！', '💦 掉下去了！', '💦 ตกแล้ว!', '💦 Rơi rồi!'],
 # 🧾 여행 정산: 내보내기 · 중간 정산
 'stExport': ['📤 내보내기', '📤 Export', '📤 書き出し', '📤 导出', '📤 ส่งออก', '📤 Xuất ra'],
 'stExportDesc': ['정산 기록을 파일로 저장하거나 동행들한테 바로 보내요.', 'Save the record as a file or send it straight to your travel buddies.', '精算の記録をファイルに保存したり、仲間にそのまま送れます。', '把结算记录存成文件，或直接发给同行的人。', 'บันทึกเป็นไฟล์ หรือส่งให้เพื่อนร่วมทริปได้เลย', 'Lưu lại thành tệp hoặc gửi ngay cho bạn đồng hành.'],
 'stCsv': ['📊 엑셀 파일 (.csv)', '📊 Excel file (.csv)', '📊 Excelファイル (.csv)', '📊 Excel 文件 (.csv)', '📊 ไฟล์ Excel (.csv)', '📊 Tệp Excel (.csv)'],
 'stTxt': ['📝 메모장 파일 (.txt)', '📝 Text file (.txt)', '📝 メモ帳ファイル (.txt)', '📝 文本文件 (.txt)', '📝 ไฟล์ข้อความ (.txt)', '📝 Tệp văn bản (.txt)'],
 'stShare': ['💬 공유하기 (카톡 등)', '💬 Share (messengers etc.)', '💬 共有（LINEなど）', '💬 分享（微信等）', '💬 แชร์ (LINE ฯลฯ)', '💬 Chia sẻ (Zalo…)'],
 'stSaved': ['✅ 저장했어요', '✅ Saved', '✅ 保存しました', '✅ 已保存', '✅ บันทึกแล้ว', '✅ Đã lưu'],
 'stMid': ['💰 중간 정산', '💰 Settle now', '💰 途中精算', '💰 中途结算', '💰 เคลียร์ระหว่างทาง', '💰 Chốt giữa chừng'],
 'stMidDesc': ['딱 떨어지게 반올림해서 받아요. 남은 차이는 다음 정산으로 넘어가요.', 'Collect rounded amounts now — the leftover difference carries over to the next settle-up.', 'きりのいい金額に丸めて受け取ります。残った差額は次の精算に回ります。', '四舍五入成整数先收，剩下的差额留到下次结算。', 'ปัดเป็นตัวเลขกลมๆ แล้วเก็บก่อน ส่วนต่างที่เหลือยกไปรอบหน้า', 'Thu số tiền đã làm tròn, phần chênh lệch còn lại chuyển sang lần sau.'],
 'stUnit': ['반올림 단위', 'Round to', '丸める単位', '取整单位', 'ปัดเศษทีละ', 'Làm tròn theo'],
 'stMidDone': ['✅ 받았어요 (정산 완료)', '✅ Paid (done)', '✅ 受け取った（精算完了）', '✅ 已收款（结算完成）', '✅ ได้รับแล้ว (เสร็จ)', '✅ Đã nhận (xong)'],
 'stMidTag': ('fn', '(Int) -> String', ['{ "💰 중간 정산 #$it" }', '{ "💰 Settle-up #$it" }', '{ "💰 途中精算 #$it" }', '{ "💰 中途结算 #$it" }', '{ "💰 เคลียร์รอบที่ $it" }', '{ "💰 Chốt lần $it" }']),
 'stResetSub': ['지우면 되돌릴 수 없어요. 동행들한테 보낼 거면 먼저 저장해 두세요.', "This can't be undone. Save it first if you want to send it to your buddies.", '消すと元に戻せません。仲間に送るなら先に保存しておきましょう。', '清空后无法恢复。要发给同行的人的话，先保存一下吧。', 'ลบแล้วกู้คืนไม่ได้ ถ้าจะส่งให้เพื่อน บันทึกไว้ก่อนนะ', 'Xóa rồi không khôi phục được. Muốn gửi cho bạn thì lưu trước nhé.'],
 'stResetSave': ['📤 먼저 저장하기', '📤 Save first', '📤 先に保存', '📤 先保存', '📤 บันทึกก่อน', '📤 Lưu trước'],
 'stResetGo': ['🗑️ 그냥 지우기', '🗑️ Just clear', '🗑️ そのまま消す', '🗑️ 直接清空', '🗑️ ลบเลย', '🗑️ Cứ xóa'],
 'stCols': ['번호|날짜|메모|금액|낸 사람|같이 쓴 사람|보내는 사람|받는 사람|이름|낸 돈|쓴 몫|합계', 'No.|Date|Note|Amount|Paid by|Split between|From|To|Name|Paid|Share|Total', '番号|日付|メモ|金額|払った人|一緒に使った人|送る人|受け取る人|名前|払った額|使った分|合計', '序号|日期|备注|金额|付款人|分摊的人|转出|收款|姓名|已付|应摊|合计', 'ลำดับ|วันที่|โน้ต|จำนวนเงิน|คนจ่าย|หารกับ|ผู้โอน|ผู้รับ|ชื่อ|จ่ายไป|ส่วนที่ใช้|รวม', 'STT|Ngày|Ghi chú|Số tiền|Người trả|Chia cho|Người gửi|Người nhận|Tên|Đã trả|Phần chi|Tổng'],
 # 📏 사이즈 변환표 (계산기 안)
 'szBtn': ['📏 사이즈', '📏 Sizes', '📏 サイズ', '📏 尺码', '📏 ไซซ์', '📏 Cỡ'],
 'szTitle': ['사이즈 변환표', 'Size Chart', 'サイズ換算表', '尺码对照表', 'ตารางเทียบไซซ์', 'Bảng quy đổi cỡ'],
 'szDesc': ['나라마다 옷·신발 사이즈가 달라요. 내 사이즈 줄을 누르면 ⭐ 저장돼서 다음에도 바로 보여요. (대략적인 기준이라 브랜드마다 조금 달라요)', 'Clothing and shoe sizes differ by country. Tap your row to ⭐ save it for next time. (Rough guide — brands vary a little.)', '国によって服・靴のサイズが違います。自分のサイズの行をタッチすると⭐保存されます。（目安なのでブランドで少し違います）', '各国的衣服鞋子尺码不同。点自己的那一行会 ⭐ 保存，下次直接显示。（仅供参考，各品牌略有不同）', 'ไซซ์เสื้อผ้าและรองเท้าแต่ละประเทศไม่เหมือนกัน แตะแถวของเราเพื่อ ⭐ บันทึกไว้ (เป็นค่าประมาณ แต่ละแบรนด์ต่างกันเล็กน้อย)', 'Cỡ quần áo, giày mỗi nước một khác. Chạm vào hàng của bạn để ⭐ lưu lại. (Chỉ là ước lượng, mỗi hãng hơi khác.)'],
 'szCats': ['👟 신발 남|👠 신발 여|👕 상의 남|👚 상의 여|👖 바지 허리|💍 반지', "👟 Men's shoes|👠 Women's shoes|👕 Men's tops|👚 Women's tops|👖 Waist|💍 Rings", '👟 靴 男|👠 靴 女|👕 トップス 男|👚 トップス 女|👖 ウエスト|💍 指輪', '👟 男鞋|👠 女鞋|👕 男上衣|👚 女上衣|👖 裤腰|💍 戒指', '👟 รองเท้าชาย|👠 รองเท้าหญิง|👕 เสื้อชาย|👚 เสื้อหญิง|👖 เอว|💍 แหวน', '👟 Giày nam|👠 Giày nữ|👕 Áo nam|👚 Áo nữ|👖 Vòng eo|💍 Nhẫn'],
 # 🔄 앱 안 업데이트
 'upTitle': ['🎉 새 버전이 나왔어요', '🎉 A new version is out', '🎉 新しいバージョンが出ました', '🎉 有新版本了', '🎉 มีเวอร์ชันใหม่แล้ว', '🎉 Đã có phiên bản mới'],
 'upSub': ('fn', '(String, String) -> String', ['{ a, b -> "지금 $a → 새 버전 $b\\n받고 나서 설치 버튼만 누르면 돼요. 저장한 설정은 그대로 남아요." }', '{ a, b -> "Now $a → new $b\\nAfter it downloads, just tap Install. Your saved settings stay." }', '{ a, b -> "今 $a → 新しい $b\\nダウンロード後、インストールを押すだけ。保存した設定はそのまま。" }', '{ a, b -> "当前 $a → 新版 $b\\n下载后点安装就行，保存的设置不会丢。" }', '{ a, b -> "ตอนนี้ $a → ใหม่ $b\\nดาวน์โหลดเสร็จแล้วกดติดตั้งได้เลย การตั้งค่าที่บันทึกไว้ยังอยู่" }', '{ a, b -> "Hiện tại $a → mới $b\\nTải xong chỉ cần bấm Cài đặt. Cài đặt đã lưu vẫn giữ nguyên." }']),
 'upGo': ['⬇️ 업데이트', '⬇️ Update', '⬇️ アップデート', '⬇️ 更新', '⬇️ อัปเดต', '⬇️ Cập nhật'],
 'upLater': ['나중에', 'Later', 'あとで', '以后再说', 'ไว้ทีหลัง', 'Để sau'],
 'upDownloading': ['⬇️ 새 버전 받는 중…', '⬇️ Downloading the new version…', '⬇️ 新しいバージョンをダウンロード中…', '⬇️ 正在下载新版本…', '⬇️ กำลังดาวน์โหลดเวอร์ชันใหม่…', '⬇️ Đang tải phiên bản mới…'],
 'upAllow': ['처음 한 번만: 다음 화면에서 "이 출처 허용"을 켜고 돌아와 주세요.', 'First time only: turn on "Allow from this source" on the next screen, then come back.', '初回だけ：次の画面で「この提供元を許可」をオンにして戻ってください。', '仅第一次：在下一个页面打开"允许来自此来源"，然后返回。', 'ครั้งแรกเท่านั้น: เปิด "อนุญาตจากแหล่งนี้" ในหน้าถัดไปแล้วกลับมา', 'Chỉ lần đầu: bật "Cho phép từ nguồn này" ở màn hình tiếp theo rồi quay lại.'],
 'upFail': ['받기 실패 — 인터넷을 확인하고 다시 해 주세요.', 'Download failed — check your internet and try again.', 'ダウンロード失敗 — ネットを確認してもう一度。', '下载失败 — 请检查网络后再试。', 'ดาวน์โหลดไม่สำเร็จ — ตรวจอินเทอร์เน็ตแล้วลองใหม่', 'Tải thất bại — kiểm tra mạng rồi thử lại.'],
 'upCheck': ['🔄 업데이트 확인', '🔄 Check for updates', '🔄 アップデート確認', '🔄 检查更新', '🔄 ตรวจหาอัปเดต', '🔄 Kiểm tra cập nhật'],
 'upLatest': ['✅ 최신 버전이에요', "✅ You're up to date", '✅ 最新バージョンです', '✅ 已是最新版本', '✅ เป็นเวอร์ชันล่าสุดแล้ว', '✅ Đã là bản mới nhất'],
}
MENU = {
 'menuQuickSub': ['반응속도 대결 · 색 반응 대결 · 스탑워치 · 맥주 거품 채우기 · 탑 쌓기 · 따라 하기 · 늑대컬링', 'Reaction Duel · Color Reaction · Stopwatch · Beer Pour · Tower Stack · Copy Me · Wolf Curling', '反応速度対決 · 色反応対決 · ストップウォッチ · ビール注ぎ · タワー積み · まねっこ · オオカミカーリング', '反应速度对决 · 颜色反应对决 · 秒表 · 倒啤酒 · 叠高塔 · 跟着按 · 狼冰壶', 'ดวลความไว · ดวลสี · นาฬิกาจับเวลา · รินเบียร์ · ต่อตึก · ทำตาม · เคิร์ลลิงหมาป่า', 'Đấu phản xạ · Đấu màu · Bấm giờ · Rót bia · Xếp tháp · Bắt chước · Bi đá sói'],
 'menuLuckSub': ['사다리게임 · 돌림판 · 폭탄돌리기 · 동전 던지기 · 플링코 · 숫자 폭탄 · 풍선 불기 · 폭탄 선 자르기 · 손가락선택', 'Ladder Game · Roulette · Hot Bomb · Coin Toss · Plinko · Number Bomb · Balloon Pump · Cut the Wire · Finger Pick', 'あみだくじ · ルーレット · 爆弾回し · コイントス · プリンコ · 数字爆弾 · 風船ふくらまし · 爆弾の線切り · 指えらび', '爬梯子 · 转盘 · 传炸弹 · 抛硬币 · 弹珠台 · 数字炸弹 · 吹气球 · 剪炸弹线 · 手指选人', 'เกมบันได · วงล้อ · ส่งระเบิด · โยนเหรียญ · พลิงโก · ระเบิดตัวเลข · เป่าลูกโป่ง · ตัดสายระเบิด · จิ้มนิ้วเลือกคน', 'Leo thang · Vòng quay · Chuyền bom · Tung đồng xu · Plinko · Bom số · Thổi bóng bay · Cắt dây bom · Chọn ngón tay'],
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
