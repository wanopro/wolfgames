#!/bin/bash
# 에뮬레이터 화면 점검: 새 화면들을 하나씩 열고 눌러 보고 화면을 찍음 (결과: out/)
PKG=$(grep -oP 'applicationId = "\K[^"]+' app/build.gradle.kts)
mkdir -p out
adb install -r app/build/outputs/apk/debug/app-debug.apk >/dev/null
adb shell settings put global window_animation_scale 0; adb shell settings put global transition_animation_scale 0
read W H < <(adb shell wm size | grep -o '[0-9]*x[0-9]*' | tail -1 | tr x ' ')
echo "screen $W x $H" > out/log.txt
px() { echo $(( $1 * W / 1000 )); }   # 천분율 → 픽셀
py() { echo $(( $1 * H / 1000 )); }
tap() { adb shell input tap $(px $1) $(py $2); }
shot() { adb exec-out screencap -p > out/$1.png; if ! adb shell pidof $PKG >/dev/null; then echo "CRASH at $1" >> out/log.txt; fi; }
open() { adb shell am force-stop $PKG; adb shell am start -n $PKG/com.wolfgames.app.MainActivity --es screen "$1" --ez autostart ${2:-false} >/dev/null; sleep 5; }
tapExact() { adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; b=$(adb shell cat /sdcard/ui.xml | grep -o "text=\"$1\"[^>]*bounds=\"[^\"]*\"" | head -1 | grep -o 'bounds="[^"]*"' | grep -o '[0-9]\+'); set -- $b; [ -n "$4" ] && adb shell input tap $(( ($1+$3)/2 )) $(( ($2+$4)/2 )); }
tapText() { adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; b=$(adb shell cat /sdcard/ui.xml | grep -o "text=\"[^\"]*$1[^\"]*\"[^>]*bounds=\"[^\"]*\"" | head -1 | grep -o 'bounds="[^"]*"' | grep -o '[0-9]\+'); set -- $b; [ -n "$4" ] && adb shell input tap $(( ($1+$3)/2 )) $(( ($2+$4)/2 )); }
adb logcat -c
# 미리 넣어 둘 설정 (언어 · 숙소). $1 = 보조 언어 (없으면 빈칸)
prefs() {
  adb shell am force-stop $PKG
  L2=""; [ -n "$1" ] && L2="<string name=\\\"lang2\\\">$1</string>"
  adb shell "run-as $PKG sh -c 'mkdir -p shared_prefs; echo \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" standalone=\\\"yes\\\" ?><map><string name=\\\"lang\\\">ko</string>$L2<string name=\\\"seen_version\\\">1.6</string><string name=\\\"hotels_json\\\">[{&quot;name&quot;:&quot;Hotel Nikko Saigon&quot;,&quot;addr&quot;:&quot;235 Nguyễn Văn Cừ, Phường 4, Quận 5, Hồ Chí Minh&quot;,&quot;phone&quot;:&quot;+84 28 3925 7777&quot;,&quot;note&quot;:&quot;편의점 옆&quot;},{&quot;name&quot;:&quot;두번째 숙소&quot;,&quot;addr&quot;:&quot;Bangkok Sukhumvit 11&quot;,&quot;phone&quot;:&quot;&quot;,&quot;note&quot;:&quot;&quot;}]</string></map>\" > shared_prefs/wolfgames.xml'"
}
# 오목 칸 (i,j) → 천분율 좌표 (pixel_5 기준 대략)
gx() { echo $(( 500 + ($1 - 7) * 63 )); }
gy() { echo $(( 518 + ($1 - 7) * 29 )); }
run() { # $1 = 이름 앞글자
  local P=$1
  open Home; shot ${P}01_home
  open Language; shot ${P}02_lang
  open LuckMenu; shot ${P}03_luck
  open BattleMenu; shot ${P}04_battle
  open Hotel; sleep 3; shot ${P}05_hotel
  tapText "숙소 수정"; sleep 1; shot ${P}06_hotel_edit
  # 플링코: 위쪽 터치 → 떨어짐 → 결과 → 섞기
  open Plinko; shot ${P}07_plinko_setup
  open Plinko true; sleep 1.5; tap 420 150; sleep 1; shot ${P}08_plinko_drop; sleep 4; shot ${P}09_plinko_result; sleep 3; shot ${P}10_plinko_next
  # 삼목: 초록 가운데, 빨강 모서리 … 그다음 2초 기다려 시간 초과
  open Ttt true; sleep 1.3; tap 500 518; sleep 0.3; tap 201 380; sleep 0.3; tap 799 380; sleep 0.2; shot ${P}11_ttt_mid; sleep 2.4; shot ${P}12_ttt_timeup
  # 삼목 승리: 다시 시작해서 초록 가로 한 줄
  open Ttt true; sleep 1.3; tap 201 380; tap 201 518; tap 500 380; tap 500 518; tap 799 380; sleep 1.2; shot ${P}13_ttt_win
  # 오목: 33 금지 확인 (흑 (6,7)(7,7)(8,6)(8,5) → (8,7))
  open Gomoku true
  for xy in "6 7" "1 13" "7 7" "3 13" "8 6" "5 13" "8 5" "13 1"; do read a b <<< "$xy"; tap $(gx $a) $(gy $b); sleep 0.25; done
  tap $(gx 8) $(gy 7); sleep 0.3; shot ${P}14_gomoku_ban
  # 오목 승리: 흑 5개 가로
  open Gomoku true
  for i in 2 3 4 5 6; do tap $(gx $i) $(gy 2); sleep 0.2; [ $i -lt 6 ] && { tap $(gx $i) $(gy 12); sleep 0.2; }; done
  sleep 1; shot ${P}15_gomoku_win
}
# 두 언어(한국어+태국어) 겹침 점검: 모든 화면 설정 → 시작 → 몇 번 눌러 보기
prefs th
for sc in Home DiceMenu LuckMenu RollDice ShutBox FiveDice D789 DiceDuel Reaction ColorReact Stopwatch Bomb Finger UpDown HoChiMinh Croc Ladder Roulette Race Pirate Arrow Four Tug Slap Beer Coin Kings Plinko Ttt Gomoku Calc Fx Hotel Changelog Language; do
  open $sc; shot "t_${sc}_0"
  open $sc true; sleep 1; shot "t_${sc}_1"
  tap 500 750; sleep 0.3; tap 500 250; sleep 0.3; tap 500 515; sleep 3; shot "t_${sc}_2"
done
adb logcat -d -b crash > out/crash.txt
echo done >> out/log.txt
