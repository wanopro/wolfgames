"""Strings.kt 의 lateinit 선언을 읽어서 두 언어 묶음(dualStrings) 을 만드는 Dual.kt 생성기 (문구 추가 후 다시 실행)"""
import re, pathlib
root = pathlib.Path(__file__).resolve().parent.parent
src = (root / 'app/src/main/java/com/wolfgames/app/i18n/Strings.kt').read_text()
decl = re.findall(r'lateinit var (\w+): (.+)', src)
LISTS = {'kcNames': '|', 'kcDescs': '|', 'plinkoSlots': '|', 'colorNames': ',', 'animalNames': ','}
lines = []
for name, typ in decl:
    typ = typ.strip()
    if typ == 'String':
        if name in LISTS: lines.append(f'    {name} = zipList(a.{name}, b.{name}, \'{LISTS[name]}\')')
        else: lines.append(f'    {name} = join2(a.{name}, b.{name})')
        continue
    m = re.match(r'\((.*)\) -> String', typ)
    args = [x.strip() for x in m.group(1).split(',')]
    ps = [f'p{i}' for i in range(len(args))]
    A = ', '.join(f'part({p}, 0)' if t == 'String' else p for p, t in zip(ps, args))
    B = ', '.join(f'part({p}, 1)' if t == 'String' else p for p, t in zip(ps, args))
    lines.append(f'    {name} = {{ {", ".join(ps)} -> join2(a.{name}({A}), b.{name}({B})) }}')
out = '''// 자동 생성: tools/gen_dual.py — 직접 수정하지 말 것
package com.wolfgames.app.i18n

/** 두 언어 같이 보기: "메인 SEP 보조" 로 묶음. 화면에서 둘로 나눠 그림 (ui/DualText.kt, Ctx.fillText) */
const val SEP = '\\u2063'

private fun lead(p: String, r: String): Int { var i = 0; while (i < p.length && i < r.length && p[i] == r[i] && !p[i].isLetterOrDigit()) i++; return i }
fun join2(p: String, r: String): String = if (p == r) p else p + SEP + r.substring(lead(p, r)).trimStart()
fun part(v: String, k: Int): String = if (SEP in v) v.split(SEP)[k] else v
/** 한 줄만 필요한 곳 (입력칸 안내 등) */
fun one(v: String): String = v.split(SEP).joinToString(" / ")
/** 두 언어 문구 여러 개 이어 붙이기 */
fun cat(vararg xs: String): String {
    if (xs.none { SEP in it }) return xs.joinToString("")
    return xs.joinToString("") { part(it, 0) } + SEP + xs.joinToString("") { part(it, 1) }
}
private fun zipList(a: String, b: String, sep: Char): String { val x = a.split(sep); val y = b.split(sep); return x.mapIndexed { i, v -> join2(v, y.getOrElse(i) { v }) }.joinToString(sep.toString()) }

private val dualCache = HashMap<String, Strings>()
fun dualStrings(main: Lang, second: Lang?): Strings {
    if (second == null || second == main) return stringsFor(main)
    return dualCache.getOrPut(main.code + second.code) { build(stringsFor(main), stringsFor(second)) }
}

private fun build(a: Strings, b: Strings): Strings = Strings().apply {
''' + '\n'.join(lines) + '\n}\n'
(root / 'app/src/main/java/com/wolfgames/app/i18n/Dual.kt').write_text(out)
print(len(lines), 'keys')
