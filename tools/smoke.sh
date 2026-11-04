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
  adb shell "run-as $PKG sh -c 'mkdir -p shared_prefs; echo \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" standalone=\\\"yes\\\" ?><map><string name=\\\"lang\\\">ko</string>$L2<string name=\\\"seen_version\\\">1.8</string><string name=\\\"hotels_json\\\">[{&quot;name&quot;:&quot;Hotel Nikko Saigon&quot;,&quot;addr&quot;:&quot;235 Nguyễn Văn Cừ, Phường 4, Quận 5, Hồ Chí Minh&quot;,&quot;phone&quot;:&quot;+84 28 3925 7777&quot;,&quot;note&quot;:&quot;편의점 옆&quot;},{&quot;name&quot;:&quot;두번째 숙소&quot;,&quot;addr&quot;:&quot;Bangkok Sukhumvit 11&quot;,&quot;phone&quot;:&quot;&quot;,&quot;note&quot;:&quot;&quot;}]</string></map>\" > shared_prefs/wolfgames.xml'"
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
# v1.8 새 화면 점검 (한국어 → 한국어+태국어): 탑 쌓기 · 따라 하기 · 폭탄 선 자르기
v18() { local P=$1
  open QuickMenu; shot ${P}_quick
  open LuckMenu; adb shell input swipe $(px 500) $(py 800) $(px 500) $(py 200) 300; sleep 1; shot ${P}_luck
  open Tower; shot ${P}_ts0
  open Tower true; sleep 1; for i in 1 2 3 4 5 6; do tap 500 500; sleep 0.45; done; shot ${P}_ts1
  for i in $(seq 1 25); do tap 500 500; sleep 0.25; done; sleep 2; shot ${P}_ts2
  open Simon true; sleep 1.1; shot ${P}_sm1; sleep 1; tap 300 400; tap 700 400; sleep 0.4; shot ${P}_sm2
  # 늑대컬링 (4명): 잔을 아래로 당겼다 놓기 — 약하게 · 세게(떨어짐)
  open Curling; shot ${P}_cu0
  open Curling true; sleep 1; shot ${P}_cu1
  adb shell input swipe $(px 222) $(py 812) $(px 222) $(py 850) 600; sleep 3.5; shot ${P}_cu2
  adb shell input swipe $(px 407) $(py 812) $(px 407) $(py 900) 600; sleep 1.2; shot ${P}_cu3; sleep 2.5
  adb shell input swipe $(px 592) $(py 812) $(px 592) $(py 860) 600; sleep 3.5
  adb shell input swipe $(px 777) $(py 812) $(px 777) $(py 840) 600; sleep 4; shot ${P}_cu4
  # 계산기 → 사이즈 · 정산 (중간 정산 · 내보내기)
  open Calc; shot ${P}_calc
  open Sizes; tap 500 420; sleep 0.5; shot ${P}_sz
  open Settle; sleep 1
  tap 500 362; adb shell input text 35400; adb shell input keyevent 4; sleep 0.6; tapText "쓴 돈 추가"; sleep 0.6
  tap 500 362; adb shell input text 12000; adb shell input keyevent 4; sleep 0.6; tap 260 548; sleep 0.3; tapText "쓴 돈 추가"; sleep 0.8; shot ${P}_st0
  tapText "중간 정산"; sleep 1; shot ${P}_mid; tapText "받았어요"; sleep 1; tapText "내보내기"; sleep 1; shot ${P}_exp; tapText "취소"; sleep 0.5
  adb shell input swipe $(px 500) $(py 800) $(px 500) $(py 200) 300; sleep 0.5; shot ${P}_st
  open WireCut; shot ${P}_wc0
  open WireCut true; sleep 1; tap 193 550; sleep 0.5; shot ${P}_wc1
  for x in 316 439 561 684 807; do tap $x 550; sleep 0.4; done; sleep 1.6; shot ${P}_wc2
}
prefs ""; v18 k
prefs th; v18 d
adb logcat -d -b crash > out/crash.txt
echo done >> out/log.txt
