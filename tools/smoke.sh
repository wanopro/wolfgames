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
open() { adb shell am force-stop $PKG; adb shell am start -n $PKG/com.wolfgames.app.MainActivity --es screen "$1" --ez autostart ${2:-false} >/dev/null; sleep 3; }
tapExact() { adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; b=$(adb shell cat /sdcard/ui.xml | grep -o "text=\"$1\"[^>]*bounds=\"[^\"]*\"" | head -1 | grep -o 'bounds="[^"]*"' | grep -o '[0-9]\+'); set -- $b; [ -n "$4" ] && adb shell input tap $(( ($1+$3)/2 )) $(( ($2+$4)/2 )); }
tapText() { adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; b=$(adb shell cat /sdcard/ui.xml | grep -o "text=\"[^\"]*$1[^\"]*\"[^>]*bounds=\"[^\"]*\"" | head -1 | grep -o 'bounds="[^"]*"' | grep -o '[0-9]\+'); set -- $b; [ -n "$4" ] && adb shell input tap $(( ($1+$3)/2 )) $(( ($2+$4)/2 )); }
adb logcat -c
open Home; shot 01_home
for m in BattleMenu CardMenu QuickMenu LuckMenu; do open $m; shot 02_$m; done
# 탭 줄다리기
open Tug; shot 03_tug_setup; open Tug true; sleep 1
for i in $(seq 1 25); do tap 300 800; tap 700 850; tap 500 200; done; shot 03_tug_mid
for i in $(seq 1 30); do tap 400 800; tap 600 900; done; sleep 1; shot 03_tug_end
# 슬랩 대결: 위·아래 둘 다 꾹 누르기 → 떼기 (반칙/공격 확인)
open Slap true; shot 04_slap_start
adb shell input motionevent DOWN $(px 500) $(py 250); sleep 1; shot 04_slap_hold_top
adb shell input motionevent UP $(px 500) $(py 250); sleep 0.5; shot 04_slap_after_top
sleep 2; adb shell input motionevent DOWN $(px 500) $(py 750); sleep 1; shot 04_slap_hold_bottom
tap 500 250; sleep 0.6; shot 04_slap_attack
adb shell input motionevent UP $(px 500) $(py 750); sleep 1.5; shot 04_slap_after
# 맥주
open Beer true; sleep 3; tap 500 850; sleep 0.8; tap 500 150; sleep 1.5; shot 05_beer_end
# 동전 (4명)
open Coin true; tap 420 860; tap 120 420; tap 560 140; tap 880 560; sleep 0.5; shot 06_coin_picked; sleep 4; shot 06_coin_spin; sleep 3; shot 06_coin_end
# 킹스컵
open Kings; tap 500 450; sleep 1; shot 07_kings_card; tap 500 450; sleep 1; shot 07_kings_card2; tapText "📋"; sleep 1; shot 07_kings_rules
# 연락처 QR: 입력 없을 때 → 입력 화면, 미리 넣어 둔 값으로 QR
open Qr; shot 08_qr_empty
adb shell am force-stop $PKG
adb shell "run-as $PKG sh -c 'mkdir -p shared_prefs; echo \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" standalone=\\\"yes\\\" ?><map><string name=\\\"qr_json\\\">{&quot;instagram&quot;:{&quot;v&quot;:&quot;@wolf.segna&quot;,&quot;on&quot;:true},&quot;whatsapp&quot;:{&quot;v&quot;:&quot;+82 10-1234-5678&quot;,&quot;on&quot;:true},&quot;tiktok&quot;:{&quot;v&quot;:&quot;wolfgames&quot;,&quot;on&quot;:true}}</string><string name=\\\"seen_version\\\">1.5-beta</string></map>\" > shared_prefs/wolfgames.xml'"
open Qr; shot 08_qr_show
adb shell input swipe $(px 800) $(py 500) $(px 200) $(py 500) 200; sleep 1; shot 08_qr_swipe
# 계산기 팁
open Calc; for k in 8 5 0 0 0; do tapExact "$k"; done; tapText "Tip"; sleep 1; shot 09_tip
# 늑대이빨: 이빨 여러 개 누르기
open Croc true; for xy in "190 214" "390 201" "110 290" "890 367" "135 560" "610 805"; do set -- $xy; tap $1 $(( 60 + $2 * 92 / 100 )); sleep 0.3; done; shot 10_teeth_open
for xy in "810 214" "110 367" "890 290" "865 560" "120 637" "880 637" "115 712" "885 712" "190 790" "390 805" "810 790" "110 444" "890 444" "610 201"; do set -- $xy; tap $1 $(( 60 + $2 * 92 / 100 )); sleep 0.25; done; sleep 1.2; shot 10_teeth_bitten
adb logcat -d -b crash > out/crash.txt
echo done >> out/log.txt
