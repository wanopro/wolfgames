"""탭 줄다리기 → 연타 땅따먹기 (6개 언어). 인자: 저장소 경로들"""
import sys, re
NAME = [('탭 줄다리기', '연타 땅따먹기'), ('Tap Tug of War', 'Tap Turf War'), ('タップ綱引き', '連打陣取り'), ('点击拔河', '狂点抢地盘'), ('ชักเย่อแตะรัว', 'แตะรัวยึดพื้นที่'), ('Kéo co chạm', 'Chạm nhanh giành đất')]
SUB = [('연타해서 깃발을 상대 쪽으로!', '연타해서 내 땅을 넓혀라!'), ('Mash to push the flag to their side!', 'Mash to grab more turf!'), ('連打で旗を相手側へ！', '連打で陣地を広げろ！'), ('狂点把旗推向对方！', '狂点扩大地盘！'), ('แตะรัวๆ ดันธงไปฝั่งตรงข้าม!', 'แตะรัวขยายพื้นที่ของเรา!'), ('Chạm thật nhanh đẩy cờ sang đối thủ!', 'Chạm nhanh để mở rộng đất!')]
for repo in sys.argv[1:]:
    for f in ['app/src/main/java/com/wolfgames/app/i18n/Strings.kt', 'tools/v15strings.py']:
        p = f'{repo}/{f}'; s = open(p, encoding='utf-8').read()
        for a, b in NAME + SUB: s = s.replace(a, b)
        open(p, 'w', encoding='utf-8').write(s)
    print('ok', repo)
