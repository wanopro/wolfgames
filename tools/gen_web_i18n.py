"""앱 문구(Strings.kt)를 웹 버전(web/i18n.js)으로 변환.
사용: python3 tools/gen_web_i18n.py  (저장소 최상위에서)"""
import json
import re

SRC = 'app/src/main/java/com/wolfgames/app/i18n/Strings.kt'
OUT = 'web/i18n.js'
src = open(SRC, encoding='utf-8').read()
out = ['// 자동 생성: tools/gen_web_i18n.py 로 앱(Strings.kt) 문구를 변환 — 직접 수정하지 말 것', 'export const STRINGS = {']


def tmpl(sv):
    inner = sv[1:-1].replace('`', '\\`')
    inner = re.sub(r'(?<!\\)\$(?!\{)([a-zA-Z_]\w*)', r'${\1}', inner)
    return '`' + inner + '`'


for lang in ['Ko', 'En', 'Ja', 'Zh', 'Th', 'Vi']:
    head = f'val {lang}Strings = Strings('
    st = src.index(head) + len(head)
    body = src[st:src.index('\n)\n', st)]
    out.append(f'  {lang.lower()}: {{')
    for it in re.split(r'\n    (?=[a-zA-Z]+ = )', body):
        it = it.strip()
        if not it:
            continue
        k, v = re.match(r'([a-zA-Z]+) = (.*)', it, re.S).groups()
        v = v.rstrip().rstrip(',')
        if v.startswith('{'):
            m = re.match(r'\{\s*(?:([\w, ]+?)\s*->\s*)?(.*)\}$', v, re.S)
            params = m.group(1) or 'it'
            expr = re.sub(r'"(?:[^"\\]|\\.)*"', lambda mm: tmpl(mm.group(0)), m.group(2).strip())
            v = f'({params}) => {expr}'
        else:
            v = re.sub(r'"(?:[^"\\]|\\.)*"', lambda mm: json.dumps(json.loads(mm.group(0)), ensure_ascii=False), v)
        out.append(f'    {k}: {v},')
    out.append('  },')
out.append('};')
open(OUT, 'w', encoding='utf-8').write('\n'.join(out) + '\n')
print('wrote', OUT)
