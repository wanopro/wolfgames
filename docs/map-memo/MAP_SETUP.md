# 🍜 맛집 지도 설정 방법

울프 게임즈 메인의 **🍜 맛집 지도** 버튼 → 구글맵 위에 커뮤 맛집을 같이 등록·공유하는 화면.
웹(`web/map.html`)으로 만들고, 안드로이드 앱은 같은 화면을 앱 안에서 보여줌 (아이폰은 웹 그대로).
키를 넣기 전에는 "준비 중" 안내만 나옴.

## 1. Firebase (데이터 저장, 무료 Spark 요금제)
1. https://console.firebase.google.com → 프로젝트 추가
2. 빌드 → **Firestore Database** → 데이터베이스 만들기 (위치: asia-southeast1 싱가포르 등 주로 쓰는 곳 가까이)
3. Firestore → **규칙** 탭 → `docs/firestore.rules` 내용을 붙여넣고 게시
4. 프로젝트 설정 → 내 앱 → **웹 앱(</>)** 추가 → `firebaseConfig` 값을 `web/map-config.js` 에 붙여넣기

## 2. Google Maps API 키
1. https://console.cloud.google.com → **새 프로젝트를 따로 만들기** (예: wolfgames-maps)
   - Firebase 프로젝트에 결제 계정을 붙이면 Firebase가 자동으로 유료(Blaze) 플랜으로 바뀌어서, 구글맵 키는 다른 프로젝트에서 만드는 게 깔끔해요. **Firebase는 Spark(무료) 그대로.**
2. 이 새 프로젝트에 결제 계정 연결 (카드 등록 필요. 월 무료 사용량 안에서는 과금 없음)
3. API 및 서비스 → 라이브러리 → **Maps JavaScript API**, **Places API (New)**, **Routes API** 사용 설정 (Routes API는 🚐 썽태우 노선을 실제 도로 모양으로 그릴 때 씀 — 휴대폰마다 처음 한 번만 받고 저장)
4. 사용자 인증 정보 → API 키 만들기 → `web/map-config.js` 의 `GOOGLE_MAPS_API_KEY` 에 붙여넣기
5. **키 제한 (꼭!)**: 애플리케이션 제한 = 웹사이트 → `https://wanopro.github.io/*`
6. 과금 방지: 결제 → 예산 및 알림 (예: 1달러), Google Maps Platform → 할당량에서 하루 최대 요청 수 제한

## 3. 사용량 확인
- 구글맵: Google Maps Platform → 측정항목 / 결제 → 보고서
- Firebase: Firestore → 사용량 (무료 한도를 넘으면 과금이 아니라 그날 멈춤)

## 기능
- 가게 이름으로 전 세계 구글맵 장소 검색 → 위치 자동
- 등록할 때 **닉네임 필수** (익명 없음, 폰에 기억해 둠), 상세에 "등록: 닉네임" 표시
- 등록 항목: 닉네임 · 추천 메뉴 1~2개(필수) · 한줄평 · 카테고리(이모지), 같은 가게에 후기 계속 쌓임 (많이 나온 메뉴 위에 표시)
- 링크 2개: **구글맵 앱에서 열기** · **울프 커뮤니티 여행 맛집 게시판(travel_food)에서 가게 이름 검색** (검색 주소는 `web/map-config.js` 의 `WOLF_SEARCH`)
- 맛집별 공유 링크, 📍 내 위치, 6개 언어
- 수정·삭제는 막아 둠 → 이상한 글은 Firebase 콘솔 → Firestore 에서 직접 삭제
