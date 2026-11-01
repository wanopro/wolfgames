"""회화 카드 문장 반영: 처음 41개(v17strings 앞부분) + phrases_more.py → Strings.kt · v17strings · 웹/앱 아이콘·묶음
문장을 더 넣을 때: phrases_more.py 에 한 줄 추가 → python3 tools/apply_phrases.py → gen_web_i18n · gen_dual"""
import re, json, sys, importlib
sys.path.insert(0, 'tools')
from phrases_more import MORE
BASE_N = 41
p = 'tools/v17strings.py'; s = open(p).read()
m = re.search(r" 'phrases': \[\n(.*?)\],\n", s, re.S)
items = eval('[' + m.group(1) + ']')
base = ['|'.join(x.split('|')[:BASE_N]) for x in items]
new = [base[li] + '|' + '|'.join(e[2 + li] for e in MORE) for li in range(6)]
N = BASE_N + len(MORE)
for x in new: assert len(x.split('|')) == N
s = s[:m.start()] + " 'phrases': [\n" + ''.join(f"  {x!r},\n" for x in new) + " ],\n" + s[m.end():]
s = re.sub(r"== \d+ for x in S\['phrases'\]", f"== {N} for x in S['phrases']", s)
open(p, 'w').write(s)
P = 'app/src/main/java/com/wolfgames/app/i18n/Strings.kt'; src = open(P).read()
kq = lambda v: json.dumps(v, ensure_ascii=False).replace('$', '\\$')
for li, L in enumerate(['Ko', 'En', 'Ja', 'Zh', 'Th', 'Vi']):
    st = src.index(f'val {L}Strings = Strings().apply {{'); en = src.index('\n}\n', st); body = src[st:en]
    body, n = re.subn(r'^    phrases = .*$', lambda mm: f'    phrases = {kq(new[li])}', body, flags=re.M); assert n == 1
    src = src[:st] + body + src[en:]
open(P, 'w').write(src)
BASE_ICONS = ['🚻','👉','🧾','💰','🌶️','🌿','🧊','💧','🍺','🥡','💳','🏷️','📄','📸','📶','💊','🆘','🥜','🍻','🌙','😍','🥴','🎁','🤝','🤬','😱','😤','🤪','🤣','👋','🙏','🙇','🐢','📍','✈️','🛑','⏱️','🛎️','🧳','🧺','❄️']
icons = BASE_ICONS + [e[1] for e in MORE]
cats = [[29, 30, 31, 32], [33, 34, 35, 36], [1, 2, 4, 5, 6, 7, 9, 10, 12, 17], [37, 38, 39, 40, 14], [8, 18, 19, 20, 21, 22, 23], [3, 11, 13, 0, 15, 16], [24, 25, 26, 27, 28]]
for k, e in enumerate(MORE): cats[e[0]].append(BASE_N + k)
w = open('web/phrase.js').read()
w = re.sub(r"export const PH_ICONS = \[.*?\];", lambda _: 'export const PH_ICONS = ' + json.dumps(icons, ensure_ascii=False) + ';', w, count=1, flags=re.S)
w = re.sub(r"export const PH_CATS = \[.*?\];", lambda _: 'export const PH_CATS = ' + json.dumps(cats) + ';', w, count=1, flags=re.S)
open('web/phrase.js', 'w').write(w)
kp = 'app/src/main/java/com/wolfgames/app/games/PhraseScreen.kt'; k = open(kp).read()
k = re.sub(r"val PH_ICONS = listOf\(.*?\)\n/\*\*", lambda _: 'val PH_ICONS = listOf(' + ', '.join(json.dumps(x, ensure_ascii=False) for x in icons) + ')\n/**', k, count=1, flags=re.S)
k = re.sub(r"val PH_CATS = listOf\(listOf.*?\)\)\n", lambda _: 'val PH_CATS = listOf(' + ', '.join('listOf(' + ', '.join(map(str, c)) + ')' for c in cats) + ')\n', k, count=1, flags=re.S)
open(kp, 'w').write(k)
print('phrases', N, 'groups', [len(c) for c in cats])
