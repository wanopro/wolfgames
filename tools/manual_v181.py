"""v1.8.1 설명서 반영: 구성 제목 v1.8 → v1.8.1, 수정 내용에 1.8.1 + 1.8 항목 (6개 언어). 한 번만 실행 → manual-i18n.js 다시 쓰기"""
import sys, pprint, json
sys.path.insert(0, 'tools')
import manual_text as m
from changelog import CHANGELOG
LANGS = ['ko', 'en', 'ja', 'zh', 'th', 'vi']
assert CHANGELOG[0]['ver'] == '1.8.1' and CHANGELOG[1]['ver'] == '1.8'
for L in LANGS:
    P = m.PAGE[L]
    for k in ['navNew', 'mainP', 'hNew', 'newIntro']: P[k] = P[k].replace('v1.8.1', 'v1.8').replace('v1.8', 'v1.8.1')
    P['changes'] = [tx[L] for _, tx in CHANGELOG[0]['items']] + [tx[L] for _, tx in CHANGELOG[1]['items']]
head = open('tools/manual_text.py', encoding='utf-8').read().split('\nPAGE = {')[0]
pp = lambda o: pprint.pformat(o, width=200, sort_dicts=False)
out = head + '\nPAGE = ' + pp(m.PAGE) + '\n\n# 단계 그림 속 폰 화면 문구\nSHOT = ' + pp(m.SHOT) + '\n\n# 게임 방법: (그림 이름, 제목으로 쓸 앱 문구 키, 설명 3줄)\nHOW_KEYS = ' + repr(m.HOW_KEYS) + '\nHOW_EMOJI = ' + repr(m.HOW_EMOJI) + '\n\nHOW = ' + pp(m.HOW) + '\n'
open('tools/manual_text.py', 'w', encoding='utf-8').write(out)
print('ok', m.PAGE['ko']['hNew'], len(m.PAGE['ko']['changes']))
