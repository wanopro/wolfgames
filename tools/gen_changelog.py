"""tools/changelog.py → 앱(Changelog.kt) · 웹(web/changelog.js) 버전별 수정 내역 생성"""
import json, os, sys
sys.path.insert(0, os.path.dirname(__file__))
from changelog import CHANGELOG

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LANGS = ['ko', 'en', 'ja', 'zh', 'th', 'vi']
for r in CHANGELOG:
    for emo, tx in r['items']:
        miss = [l for l in LANGS if not tx.get(l)]
        assert not miss, (r['ver'], emo, miss)

def kq(s):  # Kotlin 문자열
    return json.dumps(s, ensure_ascii=False).replace('$', '\\$')

kt = ['package com.wolfgames.app.i18n', '',
      '// 자동 생성 파일: tools/changelog.py 를 고친 뒤  python3 tools/gen_changelog.py', '',
      'data class ChangeItem(val emoji: String, val text: Map<String, String>)',
      'data class Release(val version: String, val date: String, val items: List<ChangeItem>)', '',
      'val CHANGELOG = listOf(']
for r in CHANGELOG:
    kt.append(f'    Release({kq(r["ver"])}, {kq(r["date"])}, listOf(')
    for emo, tx in r['items']:
        kt.append(f'        ChangeItem({kq(emo)}, mapOf(' + ', '.join(f'{kq(l)} to {kq(tx[l])}' for l in LANGS) + ')),')
    kt.append('    )),')
kt.append(')')
open(os.path.join(ROOT, 'app/src/main/java/com/wolfgames/app/i18n/Changelog.kt'), 'w', encoding='utf-8').write('\n'.join(kt) + '\n')

data = [dict(ver=r['ver'], date=r['date'], items=[[emo, tx] for emo, tx in r['items']]) for r in CHANGELOG]
open(os.path.join(ROOT, 'web/changelog.js'), 'w', encoding='utf-8').write(
    '// 자동 생성 파일: tools/changelog.py 를 고친 뒤  python3 tools/gen_changelog.py\n'
    'export const CHANGELOG = ' + json.dumps(data, ensure_ascii=False, indent=1) + ';\n')
print('changelog', [r['ver'] for r in CHANGELOG])
