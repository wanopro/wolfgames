package com.wolfgames.app.i18n

import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

/** 지원 언어 */
enum class Lang(val code: String, val flag: String, val label: String) {
    KO("ko", "🇰🇷", "한국어"),
    EN("en", "🇺🇸", "English"),
    JA("ja", "🇯🇵", "日本語"),
    ZH("zh", "🇨🇳", "简体中文"),
    TH("th", "🇹🇭", "ไทย"),
    VI("vi", "🇻🇳", "Tiếng Việt");

    companion object {
        const val SYSTEM = "system"

        fun fromSystem(): Lang {
            val code = Locale.getDefault().language
            return entries.firstOrNull { it.code == code } ?: EN
        }

        /** 저장된 설정값("system" 또는 언어 코드) → 실제 언어 */
        fun resolve(pref: String): Lang =
            if (pref == SYSTEM) fromSystem() else entries.firstOrNull { it.code == pref } ?: fromSystem()
    }
}

/** 화면에 나오는 모든 문구 */
class Strings {
    // 공통
    lateinit var appName: String
    lateinit var appSubtitle: String
    lateinit var back: String
    lateinit var start: String
    lateinit var players: String
    lateinit var prizes: String
    lateinit var playAgain: String
    lateinit var exit: String
    lateinit var again: String
    lateinit var rolling: String
    lateinit var win: String
    lateinit var pass: String
    lateinit var language: String
    lateinit var followSystem: String
    lateinit var goHomepage: String
    lateinit var ok: String
    lateinit var cancel: String
    // 메뉴
    lateinit var menuDice: String
    lateinit var menuDiceSub: String
    lateinit var menuCroc: String
    lateinit var menuCrocSub: String
    lateinit var menuLadder: String
    lateinit var menuLadderSub: String
    lateinit var menuRoulette: String
    lateinit var menuRouletteSub: String
    lateinit var rollTitle: String
    lateinit var rollSub: String
    lateinit var shutTitle: String
    lateinit var shutSub: String
    lateinit var fiveTitle: String
    lateinit var fiveSub: String
    // 주사위 굴리기
    lateinit var reset: String
    lateinit var rollHint: String
    // 숫자 뒤집기
    lateinit var shutDesc: String
    lateinit var solo: String
    lateinit var turnOf: (Int) -> String
    lateinit var numbersLeft: (Int) -> String
    lateinit var rollPrompt: String
    lateinit var pickPrompt: (String) -> String
    lateinit var noMove: String
    lateinit var throwDice: String
    lateinit var nextPlayer: String
    lateinit var pickSecond: (Int) -> String
    lateinit var endTurn: String
    lateinit var jackpot: String
    lateinit var playerWins: (Int) -> String
    lateinit var flippedLast: String
    lateinit var changePlayers: String
    // 5개 주사위
    lateinit var fiveDesc: String
    lateinit var playerN: (Int) -> String
    lateinit var tapYourArea: (Int) -> String
    lateinit var allRolled: String
    lateinit var winnersWin: (String) -> String
    lateinit var winner: String
    lateinit var diceLeft: (Int) -> String
    lateinit var tapToRoll: String
    /** (아웃 개수, 넘겨받을 플레이어 번호, 넘길 개수) */
    lateinit var outPass: (Int, Int, Int) -> String
    lateinit var waitingOthers: String
    // 악어이빨
    lateinit var bitten: String
    lateinit var pressTeeth: String
    lateinit var bittenAt: (Int) -> String
    /** (남은 이빨, 남은 당첨) */
    lateinit var teethLeft: (Int, Int) -> String
    lateinit var teethDesc: String
    lateinit var continueN: (Int) -> String
    lateinit var allFound: String
    lateinit var changePrizes: String
    // 늑대달리기
    lateinit var menuRace: String
    lateinit var menuRaceSub: String
    lateinit var raceDesc: String
    lateinit var raceSeconds: String
    lateinit var raceTapStart: String
    lateinit var raceStart: String
    lateinit var raceGo: String
    lateinit var raceRunning: String
    /** (1등 번호, 꼴찌 번호) */
    lateinit var raceResult: (Int, Int) -> String
    lateinit var raceAgain: String
    lateinit var changeRace: String
    // 해적 룰렛
    lateinit var menuPirate: String
    lateinit var menuPirateSub: String
    lateinit var pirateHint: String
    lateinit var slotsLeft: (Int) -> String
    lateinit var popped: String
    lateinit var poppedAt: (Int) -> String
    // 화살표 돌리기
    lateinit var menuArrow: String
    lateinit var menuArrowSub: String
    lateinit var arrowSeconds: String
    lateinit var arrowTap: String
    lateinit var arrowSpinning: String
    lateinit var arrowStopped: String
    // 4목
    lateinit var menuFour: String
    lateinit var menuFourSub: String
    lateinit var fourRed: String
    lateinit var fourYellow: String
    lateinit var fourTurn: (String) -> String
    lateinit var fourWin: (String) -> String
    lateinit var fourDraw: String
    lateinit var fourHint: String
    lateinit var fourRestart: String
    lateinit var installGuide: String
    // 계산기 · N빵 · 환전
    lateinit var calcTitle: String
    lateinit var splitBtn: String
    lateinit var fxBtn: String
    lateinit var splitTitle: String
    lateinit var splitTotal: String
    lateinit var splitPeople: String
    lateinit var splitRound: String
    lateinit var splitNone: String
    lateinit var splitPer: String
    /** (모두 걷은 금액, 남는 금액) */
    lateinit var splitCollect: (String, String) -> String
    lateinit var splitNeedTotal: String
    lateinit var fxTitle: String
    lateinit var fxAmount: String
    lateinit var fxUpdated: (String) -> String
    lateinit var fxLoading: String
    lateinit var fxOffline: String
    lateinit var fxError: String
    lateinit var fxRefresh: String
    lateinit var fxPick: String
    lateinit var fxNote: String
    // v1.2: 5개 주사위 패자 정하기
    lateinit var fiveSafe: String
    lateinit var fiveLoser: (String) -> String
    lateinit var fiveLoserSub: String
    // 카드 메뉴
    lateinit var menuCards: String
    lateinit var menuCardsSub: String
    // 789
    lateinit var d789Title: String
    lateinit var d789Sub: String
    lateinit var d789Desc: String
    lateinit var d789RevOpt: String
    lateinit var d789Top: String
    lateinit var d789Half: String
    lateinit var d789Shot: String
    lateinit var d789Rev: String
    lateinit var d789Pass: String
    lateinit var d789Tap: String
    lateinit var d789Sum: (Int) -> String
    // Up/Down
    lateinit var udTitle: String
    lateinit var udSub: String
    lateinit var udDesc: String
    lateinit var udStackOpt: String
    lateinit var udUp: String
    lateinit var udDown: String
    lateinit var udRed: String
    lateinit var udBlack: String
    lateinit var udGuess: String
    lateinit var udSuccess: String
    lateinit var udFail: String
    lateinit var udSame: String
    lateinit var udPot: (Int) -> String
    lateinit var udDrink: (Int) -> String
    lateinit var udNext: String
    lateinit var udLeft: (Int) -> String
    // 호치민
    lateinit var hcmTitle: String
    lateinit var hcmSub: String
    lateinit var hcmDesc: String
    lateinit var hcmPick: String
    lateinit var hcmPenalty: (String) -> String
    lateinit var hcmNone: String
    /** (합, 뒷자리) */
    lateinit var hcmSum: (Int, Int) -> String
    lateinit var newGame: String
    // 업데이트 내역
    lateinit var logTitle: String
    lateinit var logNew: (String) -> String
    lateinit var logAll: String
    lateinit var logLatest: String
    lateinit var logOk: String
    // v1.3: 카테고리 · 손가락선택
    lateinit var menuFinger: String
    lateinit var menuFingerSub: String
    lateinit var menuWolf: String
    lateinit var menuWolfSub: String
    lateinit var menuLuck: String
    lateinit var menuLuckSub: String
    lateinit var menuBattle: String
    lateinit var menuBattleSub: String
    lateinit var fingerTitle: String
    lateinit var fingerSub: String
    lateinit var fingerDesc: String
    lateinit var fingerLosers: String
    lateinit var fingerHint: String
    lateinit var fingerNeed: (Int) -> String
    lateinit var fingerResult: (Int) -> String
    lateinit var fingerPass: String
    lateinit var fingerMerong: String
    lateinit var fingerAgain: String
    // v1.4: 순발력게임 · 폭탄돌리기 · 주사위 대결 · 늑대달리기 개편
    lateinit var menuQuick: String
    lateinit var menuQuickSub: String
    lateinit var rankFirst: String
    lateinit var rankLast: String
    lateinit var hiTag: String
    lateinit var loTag: String
    lateinit var caught: String
    lateinit var tapHubAgain: String
    lateinit var secs: (String) -> String
    lateinit var biteWord: String
    lateinit var reactTitle: String
    lateinit var reactSub: String
    lateinit var reactDesc: String
    lateinit var reactWait: String
    lateinit var reactWaitSub: String
    lateinit var reactGo: String
    lateinit var reactFoul: String
    lateinit var reactFoulSub: String
    lateinit var colorTitle: String
    lateinit var colorSub: String
    lateinit var colorDesc: String
    lateinit var colorNames: String
    lateinit var colorWhen: (String) -> String
    lateinit var colorTap: String
    lateinit var colorThis: String
    lateinit var colorWrong: String
    lateinit var colorMiss: String
    lateinit var stopTitle: String
    lateinit var stopSub: String
    lateinit var stopDesc: String
    lateinit var stopSeconds: String
    lateinit var stopCount: (Int) -> String
    lateinit var stopTapNow: String
    lateinit var stopDone: String
    lateinit var bombTitle: String
    lateinit var bombSub: String
    lateinit var bombDesc: String
    lateinit var bombDir: String
    lateinit var dirCw: String
    lateinit var dirCcw: String
    lateinit var dirRandom: String
    lateinit var bombPass: String
    lateinit var bombBoom: String
    lateinit var duelTitle: String
    lateinit var duelSub: String
    lateinit var duelDesc: String
    lateinit var duelDice: String
    lateinit var duelMode: String
    lateinit var duelSum: String
    lateinit var duelMul: String
    lateinit var duelSumN: (Int) -> String
    lateinit var duelMulN: (Int) -> String
    lateinit var animalNames: String
    lateinit var racePick: (Int) -> String
    lateinit var raceLane: (Int, String) -> String
    lateinit var raceResultNames: (String, String) -> String
    lateinit var raceNeed: (Int) -> String
    lateinit var pirateTease: String
    lateinit var shuffle: String
    // 사다리
    lateinit var ladderDesc: String
    lateinit var ladderHint: String
    lateinit var numberN: (Int) -> String
    lateinit var hiddenPrizes: (Int) -> String
    lateinit var noWinnerYet: String
    lateinit var winnersList: (String) -> String
    lateinit var showAll: String
    lateinit var newLadder: String
    lateinit var changeLadder: String
    // 돌림판
    lateinit var rouletteDesc: String
    lateinit var slices: String
    lateinit var sliceWin: (Int) -> String
    lateinit var slicePass: (Int) -> String
    lateinit var drumroll: String
    lateinit var tapWheel: String
    lateinit var wheelInfo: (Int, Int) -> String
    lateinit var spinHub: String
    lateinit var spin: String
    lateinit var shuffleWins: String
    lateinit var changeWheel: String
}

val KoStrings = Strings().apply {
    appName = "울프 게임즈"
    appSubtitle = "술자리 게임 모음"
    back = "뒤로"
    start = "게임 시작"
    players = "참가 인원"
    prizes = "당첨 개수"
    playAgain = "다시 하기"
    exit = "나가기"
    again = "다시"
    rolling = "굴리는 중..."
    win = "당첨"
    pass = "통과"
    language = "언어"
    followSystem = "휴대폰 설정 따르기"
    goHomepage = "홈페이지로 이동하겠습니까?"
    ok = "이동"
    cancel = "취소"
    menuDice = "주사위"
    menuDiceSub = "주사위 굴리기 · 숫자 뒤집기 · 5개 주사위 · 789 · 주사위 대결"
    menuCroc = "늑대이빨"
    menuCrocSub = "숨어 있는 당첨 이빨을 피하세요"
    menuLadder = "사다리게임"
    menuLadderSub = "인원과 당첨 개수를 정해서"
    menuRoulette = "돌림판"
    menuRouletteSub = "칸 수와 당첨 개수를 정해서"
    rollTitle = "주사위 굴리기"
    rollSub = "여러 명이 동시에 손가락으로"
    shutTitle = "숫자 뒤집기 잭팟"
    shutSub = "마지막 숫자를 뒤집으면 승리"
    fiveTitle = "5개 주사위"
    fiveSub = "1은 아웃, 6은 옆 사람에게 · 마지막 1명이 패배"
    reset = "초기화"
    rollHint = "화면에 손가락을 올리고\n0.5초 기다렸다가 떼세요\n\n여러 명이 동시에 가능해요\n더블탭하면 초기화"
    shutDesc = "주사위 2개를 던져 두 숫자의 합 또는 두 숫자를 뒤집어요.\n" +
        "• 합을 고르면 그 숫자 하나만 뒤집고 차례 끝\n" +
        "• 한 숫자를 고르면 나머지 숫자도 이어서 뒤집을 수 있어요 (예: 1·4 → 5, 또는 1 다음 4)\n" +
        "뒤집을 숫자가 없으면 다음 사람 차례.\n" +
        "1~12를 모두 뒤집어서 마지막 숫자를 뒤집은 사람이 승리!"
    solo = "혼자 하기"
    turnOf = { "플레이어 $it 차례" }
    numbersLeft = { "남은 숫자 ${it}개" }
    rollPrompt = "주사위를 던지세요"
    pickPrompt = { "숫자 하나를 누르세요: $it" }
    noMove = "뒤집을 숫자가 없어요!"
    throwDice = "🎲 주사위 던지기"
    nextPlayer = "다음 사람 ▶"
    pickSecond = { "이어서 ${it}도 뒤집을 수 있어요" }
    endTurn = "여기서 마치기 ▶"
    jackpot = "🎉 잭팟!"
    playerWins = { "🎉 플레이어 $it 승리!" }
    flippedLast = "마지막 숫자를 뒤집었어요"
    changePlayers = "인원 변경"
    fiveDesc = "각자 주사위 5개로 시작해요. 자기 영역을 탭하면 자기 주사위가 굴러가요.\n모든 사람이 다 굴리면 결과가 한꺼번에 반영돼요.\n• 1이 나온 주사위는 아웃\n• 6이 나온 주사위는 시계방향 옆 사람에게\n주사위를 다 없앤 사람은 탈출! 마지막까지 주사위가 남은 한 명이 패배예요.\n\n폰을 테이블 가운데에 두면 각자 자기 방향으로 보여요."
    playerN = { "플레이어 $it" }
    tapYourArea = { "각자 자기 영역을 탭! (남은 ${it}명)" }
    allRolled = "모두 굴렸어요! 1은 아웃 · 6은 시계방향으로"
    winnersWin = { "🏆 $it 승리!" }
    winner = "🏆 승리!"
    diceLeft = { "남은 주사위 ${it}개" }
    tapToRoll = "👆 탭해서 굴리기"
    outPass = { ones, next, sixes -> "아웃 ${ones}개 · P${next}에게 ${sixes}개" }
    waitingOthers = "다른 사람 기다리는 중"
    bitten = "앙! 🐺 당첨!"
    pressTeeth = "이빨을 하나씩 눌러보세요"
    bittenAt = { "${it}번째 이빨에서 물렸어요" }
    teethLeft = { left, k -> "남은 이빨 ${left}개 · 남은 당첨 ${k}개" }
    teethDesc = "늑대 입 속 이빨 20개 중에 당첨 이빨이 숨어 있어요.\n돌아가며 하나씩 누르고, 당첨을 누르면 늑대가 앙! 물어요."
    continueN = { "계속하기 (남은 당첨 ${it}개)" }
    allFound = "당첨 이빨을 모두 찾았어요!"
    changePrizes = "당첨 개수 변경"
    menuRace = "늑대달리기"
    menuRaceSub = "엎치락뒤치락 늑대들의 달리기 시합"
    raceDesc = "인원수를 정하고 각자 동물을 고르세요.\n동물들이 결승선에서 덜덜 떨며 기다리고, 늑대가 먼저 무는 동물이 1등!\n결과가 나올 시간도 정할 수 있어요."
    raceSeconds = "결과 시간(초)"
    raceTapStart = "준비되면 출발!"
    raceStart = "🏁 출발"
    raceGo = "출발!"
    raceRunning = "달리는 중..."
    raceResult = { a, b -> "1등: ${a}번 · 꼴찌: ${b}번" }
    raceAgain = "다시 달리기"
    changeRace = "인원 / 시간 변경"
    menuPirate = "늑대 룰렛"
    menuPirateSub = "칼을 꽂다 늑대가 튀어나오면 당첨"
    pirateHint = "칼 자리를 눌러 칼을 꽂으세요"
    slotsLeft = { "남은 칼 자리 ${it}개" }
    popped = "펑! 🐺 튀어나왔다!"
    poppedAt = { "${it}번째 칼에서 튀어나왔어요" }
    menuArrow = "늑대 돌리기"
    menuArrowSub = "늑대가 멈춰서 문 사람이 당첨"
    arrowSeconds = "돌아가는 시간(초)"
    arrowTap = "늑대를 탭해서 돌리세요"
    arrowSpinning = "빙글빙글..."
    arrowStopped = "앙! 🐺 물었다!"
    menuFour = "4목"
    menuFourSub = "가로·세로·대각선으로 4개를 먼저 이으면 승리"
    fourRed = "빨강"
    fourYellow = "노랑"
    fourTurn = { "$it 차례" }
    fourWin = { "🎉 $it 승리!" }
    fourDraw = "무승부!"
    fourHint = "칸을 눌러 알을 떨어뜨리세요"
    fourRestart = "처음부터"
    installGuide = "설치 안내"
    calcTitle = "계산기"
    splitBtn = "🍻 N빵"
    fxBtn = "💱 환전"
    splitTitle = "술값 N빵"
    splitTotal = "총 금액"
    splitPeople = "인원"
    splitRound = "올림 단위"
    splitNone = "안 함"
    splitPer = "1인당"
    splitCollect = { a, b -> "모두 걷으면 $a · $b 남음" }
    splitNeedTotal = "계산기에 총 금액을 먼저 입력하세요"
    fxTitle = "환율 계산"
    fxAmount = "금액"
    fxUpdated = { "기준: $it" }
    fxLoading = "환율 불러오는 중..."
    fxOffline = "인터넷 연결이 없어 마지막으로 받은 환율을 보여줘요"
    fxError = "환율을 불러오지 못했어요. 인터넷 연결을 확인해 주세요"
    fxRefresh = "새로고침"
    fxPick = "통화 선택"
    fxNote = "환율은 하루 한 번 갱신돼요 (참고용)"
    fiveSafe = "✓ 탈출"
    fiveLoser = { "💀 $it 패배!" }
    fiveLoserSub = "마지막까지 주사위가 남았어요"
    menuCards = "카드"
    menuCardsSub = "Up/Down · 호치민"
    d789Title = "789"
    d789Sub = "7 첨잔 · 8 반샷 · 9 원샷"
    d789Desc = "주사위 2개를 굴려 두 눈의 합 뒷자리로 정해요.\n• 7 → 첨잔\n• 8 → 반만 마시기\n• 9 → 한번에 다 마시기\n• 6 → 순서 반대로 (옵션)\n• 나머지 → 다음 사람"
    d789RevOpt = "6이 나오면 순서 반대로"
    d789Top = "첨잔"
    d789Half = "반만 마시기"
    d789Shot = "한번에 다 마시기"
    d789Rev = "순서 반대로"
    d789Pass = "통과! 다음 사람"
    d789Tap = "탭해서 주사위 굴리기"
    d789Sum = { "합 $it" }
    udTitle = "Up/Down"
    udSub = "다음 카드가 높을까 낮을까"
    udDesc = "바닥의 카드보다 다음 카드가 높을지(UP) 낮을지(DOWN) 맞혀요.\n• 기준 카드가 A나 K면 레드/블랙을 맞혀요\n• 성공하면 다음 사람 차례, 실패하면 벌주 마시고 리셋\n• 같은 숫자가 나오면 원샷!\n카드 순서: A(가장 낮음) < 2 … 10 < J < Q < K(가장 높음)"
    udStackOpt = "성공할 때마다 벌주 1잔씩 누적"
    udUp = "UP"
    udDown = "DOWN"
    udRed = "레드"
    udBlack = "블랙"
    udGuess = "다음 카드는?"
    udSuccess = "성공!"
    udFail = "실패!"
    udSame = "같은 숫자! 원샷"
    udPot = { "누적 ${it}잔" }
    udDrink = { "벌주 ${it}잔" }
    udNext = "다음"
    udLeft = { "남은 카드 ${it}장" }
    hcmTitle = "호치민"
    hcmSub = "합의 뒷자리가 내 숫자면 벌칙"
    hcmDesc = "각자 카드 1장을 받아 자기 숫자로 정해요. (A=1, J·Q·K=10)\n바닥에 2장을 펼쳐두고, 차례마다 바꿀 카드 1장을 골라 새 카드를 올려요.\n두 카드 합의 뒷자리가 내 숫자와 같으면 벌주! (10·J·Q·K는 뒷자리 0)"
    hcmPick = "바꿀 카드를 고르세요"
    hcmPenalty = { "$it 원샷!" }
    hcmNone = "벌칙 없음"
    hcmSum = { a, b -> "합 $a → 뒷자리 $b" }
    newGame = "새 게임"
    logTitle = "업데이트 내역"
    logNew = { "v${it} 새로워진 점" }
    logAll = "전체 내역 보기"
    logLatest = "최신"
    logOk = "확인"
    menuFinger = "손가락게임"
    menuFingerSub = "손가락선택"
    menuWolf = "늑대게임"
    menuWolfSub = "늑대이빨 · 늑대달리기 · 늑대 룰렛 · 늑대 돌리기"
    menuLuck = "운빨게임"
    menuLuckSub = "사다리게임 · 돌림판 · 폭탄돌리기"
    menuBattle = "대결게임"
    menuBattleSub = "4목"
    fingerTitle = "손가락선택"
    fingerSub = "손가락 올리고 떼면 늑대가 메롱!"
    fingerDesc = "걸릴 인원을 정하고 다 같이 화면에 손가락을 하나씩 올려요.\n모두 손을 떼고 0.5초가 지나면, 걸린 사람 자리에 늑대가 나타나 메롱~ 하고 약올려요."
    fingerLosers = "걸릴 인원"
    fingerHint = "다 같이 손가락을 올렸다가\n한꺼번에 떼세요 👆"
    fingerNeed = { "${it}명 이상 올려주세요" }
    fingerResult = { "🐺 ${it}명 걸렸다!" }
    fingerPass = "통과"
    fingerMerong = "메롱~"
    fingerAgain = "화면을 탭하면 다시 해요"
    menuQuick = "순발력게임"
    menuQuickSub = "반응속도 대결 · 색 반응 대결 · 스탑워치"
    rankFirst = "👑 1등"
    rankLast = "💀 꼴찌"
    hiTag = "👑 최고"
    loTag = "💀 최저"
    caught = "💀 걸림!"
    tapHubAgain = "가운데를 누르면 다시"
    secs = { "${it}초" }
    biteWord = "앙!"
    reactTitle = "반응속도 대결"
    reactSub = "초록색이 되는 순간 터치!"
    reactDesc = "폰을 가운데 두면 화면이 인원수만큼 똑같이 나뉘어요.\n화면이 초록색으로 바뀌는 순간 자기 영역을 터치!\n가장 빠른 사람 1등, 가장 느린 사람 꼴찌. 초록색 되기 전에 누르면 부정출발이에요."
    reactWait = "기다리세요..."
    reactWaitSub = "초록색이 되면 터치"
    reactGo = "지금 터치!"
    reactFoul = "🚫 부정출발"
    reactFoulSub = "너무 빨라요"
    colorTitle = "색 반응 대결"
    colorSub = "지정색이 나오면 터치!"
    colorDesc = "가운데에 이번 판 지정색이 나와요.\n화면 색이 계속 바뀌다가 지정색이 나오면 자기 영역을 터치!\n못 누르거나 다른 색에서 누르면 걸려요. 모두 누르면 빠른 순서로 등수가 나와요."
    colorNames = "빨강,주황,노랑,초록,파랑,보라"
    colorWhen = { "${it} 나오면" }
    colorTap = "터치!"
    colorThis = "이 색!"
    colorWrong = "땡! 다른 색"
    colorMiss = "못 눌렀어요"
    stopTitle = "스탑워치"
    stopSub = "속으로 초를 세고 터치!"
    stopDesc = "목표 시간을 정하면 가운데에 나와요.\n시작하면 시간은 안 보여요. 속으로 세다가 목표 시간이 됐다 싶을 때 자기 영역을 터치!\n목표에 가장 가까운 사람이 1등이에요."
    stopSeconds = "목표 시간(초)"
    stopCount = { "속으로 ${it}초 세고" }
    stopTapNow = "터치!"
    stopDone = "✋ 완료! 기다려요"
    bombTitle = "폭탄돌리기"
    bombSub = "폭탄이 터지면 당첨!"
    bombDesc = "폰을 가운데 두면 화면이 인원수만큼 나뉘어요.\n내 자리에 폭탄이 오면 터치해서 정한 방향으로 넘기세요.\n언제 터질지 몰라요! 터질 때 폭탄을 들고 있던 사람이 당첨."
    bombDir = "방향"
    dirCw = "시계방향"
    dirCcw = "반시계방향"
    dirRandom = "랜덤"
    bombPass = "터치해서 넘기기!"
    bombBoom = "펑!!"
    duelTitle = "주사위 대결"
    duelSub = "합이나 곱으로 최고·최저 가리기"
    duelDesc = "폰을 가운데 두면 화면이 인원수만큼 나뉘어요.\n각자 자기 영역을 탭하면 주사위가 굴러가고, 더한 값이나 곱한 값이 나와요.\n모두 굴리면 가장 높은 사람과 가장 낮은 사람이 표시돼요."
    duelDice = "주사위 개수"
    duelMode = "계산 방법"
    duelSum = "더하기"
    duelMul = "곱하기"
    duelSumN = { "합 ${it}" }
    duelMulN = { "곱 ${it}" }
    animalNames = "토끼,돼지,닭,양,사슴,쥐,오리,소,햄스터,병아리,강아지,고양이"
    racePick = { "동물 고르기 (${it}마리 · 누른 순서대로 레인 번호)" }
    raceLane = { n, a -> "${n}번 ${a}" }
    raceResultNames = { a, b -> "1등: ${a} · 꼴찌: ${b}" }
    raceNeed = { "동물을 ${it}마리 골라주세요" }
    pirateTease = "통과! 늑대가 약올려요 😜"
    shuffle = "새로 섞기"
    ladderDesc = "참가 인원과 당첨 개수를 정하세요.\n위쪽 번호를 누르면 사다리를 타고 내려가요."
    ladderHint = "번호를 눌러 사다리를 타세요"
    numberN = { "${it}번" }
    hiddenPrizes = { "당첨 ${it}개가 숨어 있어요" }
    noWinnerYet = "아직 당첨자 없음"
    winnersList = { "당첨: $it" }
    showAll = "전체 결과"
    newLadder = "새 사다리"
    changeLadder = "인원 / 당첨 개수 변경"
    rouletteDesc = "돌림판 칸 수와 당첨 칸 개수를 정하세요.\n당첨 칸 위치는 무작위로 섞여요."
    slices = "총 칸 수"
    sliceWin = { "🎉 ${it}번 칸 — 당첨!" }
    slicePass = { "${it}번 칸 — 통과" }
    drumroll = "두구두구두구..."
    tapWheel = "돌림판을 탭하세요"
    wheelInfo = { m, k -> "${m}칸 중 당첨 ${k}칸" }
    spinHub = "돌려!"
    spin = "돌리기"
    shuffleWins = "당첨 칸 섞기"
    changeWheel = "칸 수 / 당첨 개수 변경"
}

val EnStrings = Strings().apply {
    appName = "Wolf Games"
    appSubtitle = "Drinking game collection"
    back = "Back"
    start = "Start"
    players = "Players"
    prizes = "Prizes"
    playAgain = "Play again"
    exit = "Exit"
    again = "Again"
    rolling = "Rolling..."
    win = "WIN"
    pass = "Pass"
    language = "Language"
    followSystem = "Use phone setting"
    goHomepage = "Go to the homepage?"
    ok = "Open"
    cancel = "Cancel"
    menuDice = "Dice"
    menuDiceSub = "Roll Dice · Number Flip · Five Dice · 789 · Dice Duel"
    menuCroc = "Wolf Teeth"
    menuCrocSub = "Avoid the hidden biting teeth"
    menuLadder = "Ladder Game"
    menuLadderSub = "Set players and prizes"
    menuRoulette = "Roulette"
    menuRouletteSub = "Set slices and prizes"
    rollTitle = "Roll Dice"
    rollSub = "Everyone at once with fingers"
    shutTitle = "Number Flip Jackpot"
    shutSub = "Flip the last number to win"
    fiveTitle = "Five Dice"
    fiveSub = "1 is out, 6 goes on · last one standing loses"
    reset = "Reset"
    rollHint = "Put your finger on the screen,\nwait half a second, then lift it\n\nSeveral people can play at once\nDouble-tap to reset"
    shutDesc = "Roll 2 dice and flip the sum or the two numbers.\n" +
        "• Pick the sum: flip just that number and your turn ends\n" +
        "• Pick one number: you can then flip the other one too (e.g. 1·4 → 5, or 1 then 4)\n" +
        "If nothing can be flipped, it's the next player's turn.\n" +
        "Whoever flips the last of 1–12 wins!"
    solo = "Solo play"
    turnOf = { "Player $it's turn" }
    numbersLeft = { "$it numbers left" }
    rollPrompt = "Roll the dice"
    pickPrompt = { "Tap a number: $it" }
    noMove = "Nothing to flip!"
    throwDice = "🎲 Roll dice"
    nextPlayer = "Next player ▶"
    pickSecond = { "You can also flip $it" }
    endTurn = "End turn ▶"
    jackpot = "🎉 Jackpot!"
    playerWins = { "🎉 Player $it wins!" }
    flippedLast = "Flipped the last number"
    changePlayers = "Change players"
    fiveDesc = "Everyone starts with 5 dice. Tap your own area to roll your dice.\nOnce everyone has rolled, the results apply all at once.\n• Dice showing 1 are out\n• Dice showing 6 go to the next player clockwise\nNo dice left = you’re safe! The last player still holding dice loses.\n\nPut the phone in the middle of the table so each area faces its player."
    playerN = { "Player $it" }
    tapYourArea = { "Tap your own area! ($it left)" }
    allRolled = "All rolled! 1 is out · 6 goes clockwise"
    winnersWin = { "🏆 $it win!" }
    winner = "🏆 Winner!"
    diceLeft = { "$it dice left" }
    tapToRoll = "👆 Tap to roll"
    outPass = { ones, next, sixes -> "Out $ones · $sixes to P$next" }
    waitingOthers = "Waiting for others"
    bitten = "Chomp! 🐺 Bitten!"
    pressTeeth = "Press the teeth one by one"
    bittenAt = { "Bitten on tooth #$it" }
    teethLeft = { left, k -> "$left teeth left · $k bite(s) left" }
    teethDesc = "Biting teeth are hidden among the wolf's 20 teeth.\nTake turns pressing one at a time — hit one and the wolf chomps!"
    continueN = { "Continue ($it bite(s) left)" }
    allFound = "All biting teeth found!"
    changePrizes = "Change prize count"
    menuRace = "Wolf Race"
    menuRaceSub = "A neck-and-neck wolf race"
    raceDesc = "Choose the number of players and each pick an animal.\nThe animals wait trembling at the finish line — the animal the wolf bites first is 1st!\nYou can also set how long the race lasts."
    raceSeconds = "Race time (sec)"
    raceTapStart = "Ready? Hit start!"
    raceStart = "🏁 Start"
    raceGo = "Go!"
    raceRunning = "Running..."
    raceResult = { a, b -> "1st: #$a · Last: #$b" }
    raceAgain = "Race again"
    changeRace = "Change players / time"
    menuPirate = "Wolf Roulette"
    menuPirateSub = "Insert swords — pop the wolf and you lose"
    pirateHint = "Tap a slot to insert a sword"
    slotsLeft = { "$it slots left" }
    popped = "Pop! 🐺 The wolf jumped out!"
    poppedAt = { "Popped on sword #$it" }
    menuArrow = "Wolf Spin"
    menuArrowSub = "Whoever the wolf bites when it stops"
    arrowSeconds = "Spin time (sec)"
    arrowTap = "Tap the wolf to spin"
    arrowSpinning = "Spinning..."
    arrowStopped = "Chomp! 🐺 Got you!"
    menuFour = "Connect 4"
    menuFourSub = "Line up 4 in a row first to win"
    fourRed = "Red"
    fourYellow = "Yellow"
    fourTurn = { "$it's turn" }
    fourWin = { "🎉 $it wins!" }
    fourDraw = "Draw!"
    fourHint = "Tap a column to drop a disc"
    fourRestart = "Restart"
    installGuide = "Install guide"
    calcTitle = "Calculator"
    splitBtn = "🍻 Split"
    fxBtn = "💱 Exchange"
    splitTitle = "Split the bill"
    splitTotal = "Total"
    splitPeople = "People"
    splitRound = "Round up to"
    splitNone = "None"
    splitPer = "Per person"
    splitCollect = { a, b -> "Collected $a · $b extra" }
    splitNeedTotal = "Enter the total on the calculator first"
    fxTitle = "Currency converter"
    fxAmount = "Amount"
    fxUpdated = { "As of $it" }
    fxLoading = "Loading rates..."
    fxOffline = "Offline — showing the last saved rates"
    fxError = "Couldn't load rates. Check your internet connection"
    fxRefresh = "Refresh"
    fxPick = "Choose currency"
    fxNote = "Rates update once a day (for reference)"
    fiveSafe = "✓ Safe"
    fiveLoser = { "💀 $it loses!" }
    fiveLoserSub = "Still holding dice at the end"
    menuCards = "Cards"
    menuCardsSub = "Up/Down · Ho Chi Minh"
    d789Title = "789"
    d789Sub = "7 top up · 8 half · 9 shot"
    d789Desc = "Roll 2 dice and use the last digit of the sum.\n• 7 → top up the glass\n• 8 → drink half\n• 9 → drink it all\n• 6 → reverse the order (option)\n• Anything else → next player"
    d789RevOpt = "Reverse the order on 6"
    d789Top = "Top up"
    d789Half = "Drink half"
    d789Shot = "Drink it all"
    d789Rev = "Reverse order"
    d789Pass = "Pass! Next player"
    d789Tap = "Tap to roll the dice"
    d789Sum = { "Sum $it" }
    udTitle = "Up/Down"
    udSub = "Higher or lower?"
    udDesc = "Guess whether the next card is higher (UP) or lower (DOWN) than the card on the table.\n• If the base card is A or K, guess red or black\n• Correct: next player’s turn. Wrong: drink and reset\n• Same number: shot!\nCard order: A (lowest) < 2 … 10 < J < Q < K (highest)"
    udStackOpt = "Add 1 drink to the pot for each success"
    udUp = "UP"
    udDown = "DOWN"
    udRed = "Red"
    udBlack = "Black"
    udGuess = "Next card?"
    udSuccess = "Correct!"
    udFail = "Wrong!"
    udSame = "Same number! Shot"
    udPot = { "Pot: $it" }
    udDrink = { "Drink $it" }
    udNext = "Next"
    udLeft = { "$it cards left" }
    hcmTitle = "Ho Chi Minh"
    hcmSub = "Drink if the last digit matches your card"
    hcmDesc = "Everyone gets 1 card as their number (A=1, J·Q·K=10).\nTwo cards lie face up. On your turn pick one and a new card goes on top.\nIf the last digit of the two cards’ sum matches your number, you drink! (10·J·Q·K match 0)"
    hcmPick = "Pick a card to cover"
    hcmPenalty = { "$it: shot!" }
    hcmNone = "No penalty"
    hcmSum = { a, b -> "Sum $a → last digit $b" }
    newGame = "New game"
    logTitle = "Update history"
    logNew = { "What’s new in v${it}" }
    logAll = "See all updates"
    logLatest = "Latest"
    logOk = "OK"
    menuFinger = "Finger Games"
    menuFingerSub = "Finger Pick"
    menuWolf = "Wolf Games"
    menuWolfSub = "Wolf Teeth · Wolf Race · Wolf Roulette · Wolf Spin"
    menuLuck = "Luck Games"
    menuLuckSub = "Ladder Game · Roulette · Hot Bomb"
    menuBattle = "Versus Games"
    menuBattleSub = "Connect 4"
    fingerTitle = "Finger Pick"
    fingerSub = "Put fingers down, lift — the wolf teases!"
    fingerDesc = "Choose how many get picked, then everyone puts one finger on the screen.\nWhen all fingers are lifted, after 0.5 sec a wolf pops up on the picked spots and sticks its tongue out."
    fingerLosers = "Picked"
    fingerHint = "Everyone put a finger down,\nthen lift together 👆"
    fingerNeed = { "Need at least ${it} fingers" }
    fingerResult = { "🐺 ${it} picked!" }
    fingerPass = "Safe"
    fingerMerong = "Nyah~"
    fingerAgain = "Tap the screen to play again"
    menuQuick = "Quick Games"
    menuQuickSub = "Reaction Duel · Color Reaction · Stopwatch"
    rankFirst = "👑 1st"
    rankLast = "💀 Last"
    hiTag = "👑 Highest"
    loTag = "💀 Lowest"
    caught = "💀 Caught!"
    tapHubAgain = "Tap the center to play again"
    secs = { "${it}s" }
    biteWord = "Chomp!"
    reactTitle = "Reaction Duel"
    reactSub = "Tap the moment it turns green!"
    reactDesc = "Put the phone in the middle and the screen splits evenly for everyone.\nTap your area the moment the screen turns green!\nFastest is 1st, slowest is last. Tapping before green is a false start."
    reactWait = "Wait..."
    reactWaitSub = "Tap when it turns green"
    reactGo = "Tap now!"
    reactFoul = "🚫 False start"
    reactFoulSub = "Too early"
    colorTitle = "Color Reaction"
    colorSub = "Tap when the target color shows!"
    colorDesc = "The target color for this round is shown in the center.\nThe screen keeps changing color — tap your area when the target color appears!\nMiss it or tap on the wrong color and you are caught. If everyone taps, fastest wins."
    colorNames = "Red,Orange,Yellow,Green,Blue,Purple"
    colorWhen = { "When ${it} shows" }
    colorTap = "Tap!"
    colorThis = "This!"
    colorWrong = "Wrong color!"
    colorMiss = "Missed"
    stopTitle = "Stopwatch"
    stopSub = "Count in your head and tap!"
    stopDesc = "Pick a target time; it is shown in the center.\nThe timer is hidden once it starts. Count in your head and tap your area when you think the time is up!\nClosest to the target is 1st."
    stopSeconds = "Target time (sec)"
    stopCount = { "Count ${it} sec" }
    stopTapNow = "then tap!"
    stopDone = "✋ Done! Waiting"
    bombTitle = "Hot Bomb"
    bombSub = "Holding the bomb when it blows? You lose!"
    bombDesc = "Put the phone in the middle and the screen splits for everyone.\nWhen the bomb comes to you, tap to pass it on in the chosen direction.\nNobody knows when it blows! Whoever holds it then is picked."
    bombDir = "Direction"
    dirCw = "Clockwise"
    dirCcw = "Counter-clockwise"
    dirRandom = "Random"
    bombPass = "Tap to pass!"
    bombBoom = "BOOM!!"
    duelTitle = "Dice Duel"
    duelSub = "Highest and lowest by sum or product"
    duelDesc = "Put the phone in the middle and the screen splits for everyone.\nTap your area to roll; you get the sum or the product of your dice.\nWhen everyone has rolled, the highest and lowest are shown."
    duelDice = "Dice"
    duelMode = "Calculation"
    duelSum = "Sum"
    duelMul = "Product"
    duelSumN = { "Sum ${it}" }
    duelMulN = { "Product ${it}" }
    animalNames = "Rabbit,Pig,Chicken,Sheep,Deer,Mouse,Duck,Cow,Hamster,Chick,Puppy,Kitty"
    racePick = { "Pick animals (${it} · lane number in the order you tap)" }
    raceLane = { n, a -> "${n}. ${a}" }
    raceResultNames = { a, b -> "1st: ${a} · Last: ${b}" }
    raceNeed = { "Pick ${it} animals" }
    pirateTease = "Safe! The wolf is teasing you 😜"
    shuffle = "Shuffle"
    ladderDesc = "Set the number of players and prizes.\nTap a number at the top to ride down the ladder."
    ladderHint = "Tap a number to ride the ladder"
    numberN = { "#$it" }
    hiddenPrizes = { "$it prize(s) hidden" }
    noWinnerYet = "No winner yet"
    winnersList = { "Winners: $it" }
    showAll = "Show all"
    newLadder = "New ladder"
    changeLadder = "Change players / prizes"
    rouletteDesc = "Set the number of slices and prize slices.\nPrize slices are placed randomly."
    slices = "Slices"
    sliceWin = { "🎉 Slice $it — WIN!" }
    slicePass = { "Slice $it — Pass" }
    drumroll = "Drumroll..."
    tapWheel = "Tap the wheel"
    wheelInfo = { m, k -> "$k prize(s) out of $m slices" }
    spinHub = "SPIN"
    spin = "Spin"
    shuffleWins = "Shuffle prizes"
    changeWheel = "Change slices / prizes"
}

val JaStrings = Strings().apply {
    appName = "ウルフゲームズ"
    appSubtitle = "飲み会ゲーム集"
    back = "戻る"
    start = "ゲーム開始"
    players = "参加人数"
    prizes = "当たりの数"
    playAgain = "もう一回"
    exit = "終了"
    again = "もう一回"
    rolling = "振っています..."
    win = "当たり"
    pass = "セーフ"
    language = "言語"
    followSystem = "スマホの設定に従う"
    goHomepage = "ホームページに移動しますか？"
    ok = "移動"
    cancel = "キャンセル"
    menuDice = "サイコロ"
    menuDiceSub = "サイコロ振り · 数字めくり · 5個サイコロ · 789 · サイコロ対決"
    menuCroc = "オオカミの歯"
    menuCrocSub = "隠れた当たりの歯をよけよう"
    menuLadder = "あみだくじ"
    menuLadderSub = "人数と当たりの数を決めて"
    menuRoulette = "ルーレット"
    menuRouletteSub = "マス数と当たりの数を決めて"
    rollTitle = "サイコロ振り"
    rollSub = "みんなで同時に指で"
    shutTitle = "数字めくりジャックポット"
    shutSub = "最後の数字をめくったら勝ち"
    fiveTitle = "5個サイコロ"
    fiveSub = "1はアウト、6は隣へ · 最後の1人が負け"
    reset = "リセット"
    rollHint = "画面に指を置いて\n0.5秒待ってから離してください\n\n何人でも同時にOK\nダブルタップでリセット"
    shutDesc = "サイコロ2個を振って、合計か2つの数字をめくります。\n" +
        "• 合計を選ぶとその数字だけめくって番が終わります\n" +
        "• 1つの数字を選ぶと、続けてもう1つもめくれます（例：1・4 → 5、または1のあと4）\n" +
        "めくれる数字がなければ次の人の番。\n" +
        "1〜12を全部めくり、最後の数字をめくった人の勝ち！"
    solo = "ひとりで"
    turnOf = { "プレイヤー${it}の番" }
    numbersLeft = { "残り${it}個" }
    rollPrompt = "サイコロを振ってください"
    pickPrompt = { "数字を1つタップ: $it" }
    noMove = "めくれる数字がありません！"
    throwDice = "🎲 サイコロを振る"
    nextPlayer = "次の人 ▶"
    pickSecond = { "続けて${it}もめくれます" }
    endTurn = "ここで終わる ▶"
    jackpot = "🎉 ジャックポット！"
    playerWins = { "🎉 プレイヤー${it}の勝ち！" }
    flippedLast = "最後の数字をめくりました"
    changePlayers = "人数を変更"
    fiveDesc = "全員サイコロ5個からスタート。自分のエリアをタップすると自分のサイコロが転がります。\n全員が振り終わると結果がまとめて反映されます。\n• 1が出たサイコロはアウト\n• 6が出たサイコロは時計回りで隣の人へ\nサイコロがなくなった人は脱出！最後までサイコロが残った1人が負けです。\n\nスマホをテーブルの真ん中に置くと、それぞれの向きで見られます。"
    playerN = { "プレイヤー$it" }
    tapYourArea = { "自分のエリアをタップ！（残り${it}人）" }
    allRolled = "全員振りました！1はアウト · 6は時計回りへ"
    winnersWin = { "🏆 ${it}の勝ち！" }
    winner = "🏆 勝ち！"
    diceLeft = { "残り${it}個" }
    tapToRoll = "👆 タップして振る"
    outPass = { ones, next, sixes -> "アウト${ones}個 · P${next}へ${sixes}個" }
    waitingOthers = "他の人を待っています"
    bitten = "ガブッ！🐺 当たり！"
    pressTeeth = "歯を1本ずつ押してください"
    bittenAt = { "${it}本目でかまれました" }
    teethLeft = { left, k -> "残り${left}本 · 残りの当たり${k}本" }
    teethDesc = "オオカミの口の中の20本の歯に当たりの歯が隠れています。\n順番に1本ずつ押して、当たりを押すとオオカミがガブッ！"
    continueN = { "続ける（残りの当たり${it}本）" }
    allFound = "当たりの歯を全部見つけました！"
    changePrizes = "当たりの数を変更"
    menuRace = "ウルフレース"
    menuRaceSub = "抜きつ抜かれつオオカミのかけっこ"
    raceDesc = "人数を決めて、それぞれ動物を選んでね。\n動物たちはゴールでぶるぶる待っていて、オオカミが先にかんだ動物が1位！\n結果が出るまでの時間も決められます。"
    raceSeconds = "結果までの時間（秒）"
    raceTapStart = "準備できたらスタート！"
    raceStart = "🏁 スタート"
    raceGo = "ドン！"
    raceRunning = "走っています..."
    raceResult = { a, b -> "1位: ${a}番 · ビリ: ${b}番" }
    raceAgain = "もう一回走る"
    changeRace = "人数 / 時間を変更"
    menuPirate = "オオカミルーレット"
    menuPirateSub = "剣を刺してオオカミが飛び出したら当たり"
    pirateHint = "穴をタップして剣を刺そう"
    slotsLeft = { "残りの穴 ${it}個" }
    popped = "ポン！🐺 飛び出した！"
    poppedAt = { "${it}本目で飛び出しました" }
    menuArrow = "オオカミ回し"
    menuArrowSub = "止まったオオカミにかまれた人が当たり"
    arrowSeconds = "回る時間（秒）"
    arrowTap = "オオカミをタップして回そう"
    arrowSpinning = "くるくる..."
    arrowStopped = "ガブッ！🐺 かまれた！"
    menuFour = "4目並べ"
    menuFourSub = "縦・横・斜めに4つ先に並べたら勝ち"
    fourRed = "赤"
    fourYellow = "黄"
    fourTurn = { "${it}の番" }
    fourWin = { "🎉 ${it}の勝ち！" }
    fourDraw = "引き分け！"
    fourHint = "列をタップしてコマを落とそう"
    fourRestart = "最初から"
    installGuide = "インストール案内"
    calcTitle = "電卓"
    splitBtn = "🍻 割り勘"
    fxBtn = "💱 両替"
    splitTitle = "割り勘"
    splitTotal = "合計"
    splitPeople = "人数"
    splitRound = "切り上げ単位"
    splitNone = "なし"
    splitPer = "1人あたり"
    splitCollect = { a, b -> "集めると $a · $b 余り" }
    splitNeedTotal = "先に電卓で合計を入力してください"
    fxTitle = "為替換算"
    fxAmount = "金額"
    fxUpdated = { "$it 時点" }
    fxLoading = "為替レートを取得中..."
    fxOffline = "オフラインのため前回のレートを表示しています"
    fxError = "為替レートを取得できませんでした。接続を確認してください"
    fxRefresh = "更新"
    fxPick = "通貨を選択"
    fxNote = "レートは1日1回更新されます（参考値）"
    fiveSafe = "✓ 脱出"
    fiveLoser = { "💀 ${it}の負け！" }
    fiveLoserSub = "最後までサイコロが残りました"
    menuCards = "カード"
    menuCardsSub = "Up/Down · ホーチミン"
    d789Title = "789"
    d789Sub = "7 注ぎ足し · 8 半分 · 9 一気"
    d789Desc = "サイコロ2個を振り、合計の一の位で決めます。\n• 7 → 注ぎ足し\n• 8 → 半分飲む\n• 9 → 一気飲み\n• 6 → 順番を逆に（オプション）\n• それ以外 → 次の人"
    d789RevOpt = "6で順番を逆にする"
    d789Top = "注ぎ足し"
    d789Half = "半分飲む"
    d789Shot = "一気飲み"
    d789Rev = "順番を逆に"
    d789Pass = "セーフ！次の人"
    d789Tap = "タップしてサイコロを振る"
    d789Sum = { "合計 $it" }
    udTitle = "Up/Down"
    udSub = "次のカードは高い？低い？"
    udDesc = "場のカードより次のカードが高い(UP)か低い(DOWN)かを当てます。\n• 基準カードがAかKなら赤か黒を当てます\n• 当たれば次の人へ、外れたら罰杯を飲んでリセット\n• 同じ数字なら一気！\nカードの順番：A(最低) < 2 … 10 < J < Q < K(最高)"
    udStackOpt = "当たるたびに罰杯を1杯ずつ追加"
    udUp = "UP"
    udDown = "DOWN"
    udRed = "赤"
    udBlack = "黒"
    udGuess = "次のカードは？"
    udSuccess = "当たり！"
    udFail = "はずれ！"
    udSame = "同じ数字！一気"
    udPot = { "累積 ${it}杯" }
    udDrink = { "罰杯 ${it}杯" }
    udNext = "次へ"
    udLeft = { "残り${it}枚" }
    hcmTitle = "ホーチミン"
    hcmSub = "合計の一の位が自分の数字なら罰杯"
    hcmDesc = "各自カードを1枚もらって自分の数字にします（A=1、J·Q·K=10）。\n場に2枚を表向きに置き、番ごとに1枚を選んで新しいカードを重ねます。\n2枚の合計の一の位が自分の数字と同じなら罰杯！（10·J·Q·Kは0）"
    hcmPick = "重ねるカードを選んでください"
    hcmPenalty = { "${it} 一気！" }
    hcmNone = "罰ゲームなし"
    hcmSum = { a, b -> "合計 $a → 一の位 $b" }
    newGame = "新しいゲーム"
    logTitle = "アップデート履歴"
    logNew = { "v${it}の新しいところ" }
    logAll = "すべての履歴を見る"
    logLatest = "最新"
    logOk = "OK"
    menuFinger = "指ゲーム"
    menuFingerSub = "指えらび"
    menuWolf = "オオカミゲーム"
    menuWolfSub = "オオカミの歯 · ウルフレース · オオカミルーレット · オオカミ回し"
    menuLuck = "運ゲー"
    menuLuckSub = "あみだくじ · ルーレット · 爆弾回し"
    menuBattle = "対決ゲーム"
    menuBattleSub = "4目並べ"
    fingerTitle = "指えらび"
    fingerSub = "指を置いて離すとオオカミがあっかんべー！"
    fingerDesc = "当たる人数を決めて、みんなで画面に指を1本ずつ置きます。\n全員が指を離して0.5秒たつと、当たった人の場所にオオカミが出てきて、あっかんべーとからかいます。"
    fingerLosers = "当たる人数"
    fingerHint = "みんなで指を置いて\nいっせいに離してね 👆"
    fingerNeed = { "${it}人以上置いてください" }
    fingerResult = { "🐺 ${it}人当たり！" }
    fingerPass = "セーフ"
    fingerMerong = "べー"
    fingerAgain = "画面をタップするともう一度"
    menuQuick = "反射神経ゲーム"
    menuQuickSub = "反応速度対決 · 色反応対決 · ストップウォッチ"
    rankFirst = "👑 1位"
    rankLast = "💀 ビリ"
    hiTag = "👑 最高"
    loTag = "💀 最低"
    caught = "💀 アウト！"
    tapHubAgain = "真ん中を押すともう一度"
    secs = { "${it}秒" }
    biteWord = "ガブッ！"
    reactTitle = "反応速度対決"
    reactSub = "緑になった瞬間にタッチ！"
    reactDesc = "スマホを真ん中に置くと、画面が人数分に同じ大きさで分かれます。\n画面が緑になった瞬間に自分のエリアをタッチ！\n一番速い人が1位、一番遅い人がビリ。緑になる前に押すとフライングです。"
    reactWait = "待って..."
    reactWaitSub = "緑になったらタッチ"
    reactGo = "今だ！"
    reactFoul = "🚫 フライング"
    reactFoulSub = "早すぎ"
    colorTitle = "色反応対決"
    colorSub = "指定の色が出たらタッチ！"
    colorDesc = "真ん中に今回の指定色が出ます。\n画面の色が次々に変わり、指定色が出たら自分のエリアをタッチ！\n押せなかったり違う色で押したらアウト。全員押せたら速い順に順位が出ます。"
    colorNames = "赤,オレンジ,黄,緑,青,紫"
    colorWhen = { "${it}が出たら" }
    colorTap = "タッチ！"
    colorThis = "この色！"
    colorWrong = "ブー！違う色"
    colorMiss = "押せなかった"
    stopTitle = "ストップウォッチ"
    stopSub = "心の中で数えてタッチ！"
    stopDesc = "目標時間を決めると真ん中に表示されます。\nスタートすると時間は見えません。心の中で数えて、目標時間だと思ったら自分のエリアをタッチ！\n目標に一番近い人が1位です。"
    stopSeconds = "目標時間（秒）"
    stopCount = { "心の中で${it}秒数えて" }
    stopTapNow = "タッチ！"
    stopDone = "✋ 完了！待ってね"
    bombTitle = "爆弾回し"
    bombSub = "爆発した時に持ってたら当たり！"
    bombDesc = "スマホを真ん中に置くと、画面が人数分に分かれます。\n自分のところに爆弾が来たらタッチして、決めた方向に回します。\nいつ爆発するかわかりません！爆発した時に持っていた人が当たり。"
    bombDir = "方向"
    dirCw = "時計回り"
    dirCcw = "反時計回り"
    dirRandom = "ランダム"
    bombPass = "タッチで回す！"
    bombBoom = "ドカン！！"
    duelTitle = "サイコロ対決"
    duelSub = "合計か掛け算で最高・最低を決める"
    duelDesc = "スマホを真ん中に置くと、画面が人数分に分かれます。\n自分のエリアをタップするとサイコロが転がり、合計か掛け算の値が出ます。\n全員振ると、一番高い人と一番低い人が表示されます。"
    duelDice = "サイコロの数"
    duelMode = "計算方法"
    duelSum = "足し算"
    duelMul = "掛け算"
    duelSumN = { "合計 ${it}" }
    duelMulN = { "積 ${it}" }
    animalNames = "ウサギ,ブタ,ニワトリ,ヒツジ,シカ,ネズミ,アヒル,ウシ,ハムスター,ヒヨコ,子犬,子猫"
    racePick = { "動物えらび（${it}匹・押した順にレーン番号）" }
    raceLane = { n, a -> "${n}番 ${a}" }
    raceResultNames = { a, b -> "1位：${a}・ビリ：${b}" }
    raceNeed = { "動物を${it}匹選んでください" }
    pirateTease = "セーフ！オオカミがからかってる 😜"
    shuffle = "シャッフル"
    ladderDesc = "参加人数と当たりの数を決めてください。\n上の番号を押すとあみだを下りていきます。"
    ladderHint = "番号を押してスタート"
    numberN = { "${it}番" }
    hiddenPrizes = { "当たりが${it}個隠れています" }
    noWinnerYet = "まだ当たりなし"
    winnersList = { "当たり: $it" }
    showAll = "全部見る"
    newLadder = "新しいあみだ"
    changeLadder = "人数 / 当たりの数を変更"
    rouletteDesc = "ルーレットのマス数と当たりマスの数を決めてください。\n当たりマスの位置はランダムです。"
    slices = "マス数"
    sliceWin = { "🎉 ${it}番 — 当たり！" }
    slicePass = { "${it}番 — セーフ" }
    drumroll = "ドゥルルルル..."
    tapWheel = "ルーレットをタップ"
    wheelInfo = { m, k -> "${m}マス中 当たり${k}マス" }
    spinHub = "回す!"
    spin = "回す"
    shuffleWins = "当たりをシャッフル"
    changeWheel = "マス数 / 当たりの数を変更"
}

val ZhStrings = Strings().apply {
    appName = "沃夫游戏"
    appSubtitle = "酒桌游戏合集"
    back = "返回"
    start = "开始游戏"
    players = "参加人数"
    prizes = "中奖数量"
    playAgain = "再来一局"
    exit = "退出"
    again = "再来"
    rolling = "摇骰中..."
    win = "中奖"
    pass = "安全"
    language = "语言"
    followSystem = "跟随手机设置"
    goHomepage = "要前往主页吗？"
    ok = "前往"
    cancel = "取消"
    menuDice = "骰子"
    menuDiceSub = "摇骰子 · 翻数字 · 五颗骰子 · 789 · 骰子对决"
    menuCroc = "狼牙齿"
    menuCrocSub = "避开藏起来的中奖牙齿"
    menuLadder = "爬梯子"
    menuLadderSub = "设定人数和中奖数量"
    menuRoulette = "转盘"
    menuRouletteSub = "设定格数和中奖数量"
    rollTitle = "摇骰子"
    rollSub = "大家一起用手指"
    shutTitle = "翻数字大奖"
    shutSub = "翻开最后一个数字就赢"
    fiveTitle = "五颗骰子"
    fiveSub = "1出局，6给下家 · 最后一人输"
    reset = "重置"
    rollHint = "把手指放在屏幕上\n等0.5秒后松开\n\n多人可同时进行\n双击重置"
    shutDesc = "掷2颗骰子，翻开两个点数之和，或者翻开两个点数。\n" +
        "• 选择和：只翻这一个数字，回合结束\n" +
        "• 选择一个点数：可以接着翻另一个点数（例：1·4 → 5，或先1再4）\n" +
        "没有可翻的数字就轮到下一位。\n" +
        "翻完1~12，翻开最后一个数字的人获胜！"
    solo = "单人模式"
    turnOf = { "轮到玩家$it" }
    numbersLeft = { "剩余${it}个数字" }
    rollPrompt = "请掷骰子"
    pickPrompt = { "点一个数字: $it" }
    noMove = "没有可翻的数字！"
    throwDice = "🎲 掷骰子"
    nextPlayer = "下一位 ▶"
    pickSecond = { "还可以接着翻$it" }
    endTurn = "到此结束 ▶"
    jackpot = "🎉 大奖！"
    playerWins = { "🎉 玩家${it}获胜！" }
    flippedLast = "翻开了最后一个数字"
    changePlayers = "更改人数"
    fiveDesc = "每人从5颗骰子开始。点自己的区域就会摇自己的骰子。\n所有人都摇完后，结果一起生效。\n• 点数为1的骰子出局\n• 点数为6的骰子按顺时针给下家\n骰子全部没有的人逃脱！最后还剩骰子的那个人输。\n\n把手机放在桌子中间，每个人都能从自己的方向看。"
    playerN = { "玩家$it" }
    tapYourArea = { "点自己的区域！（还剩${it}人）" }
    allRolled = "都摇完了！1出局 · 6顺时针传"
    winnersWin = { "🏆 ${it}获胜！" }
    winner = "🏆 获胜！"
    diceLeft = { "剩余${it}颗骰子" }
    tapToRoll = "👆 点击摇骰"
    outPass = { ones, next, sixes -> "出局${ones}颗 · 给P${next} ${sixes}颗" }
    waitingOthers = "等待其他人"
    bitten = "啊呜！🐺 中奖！"
    pressTeeth = "一颗一颗按牙齿"
    bittenAt = { "第${it}颗牙被咬了" }
    teethLeft = { left, k -> "剩余${left}颗牙 · 剩余中奖${k}颗" }
    teethDesc = "狼嘴里的20颗牙齿中藏着中奖牙齿。\n轮流一颗一颗按，按到中奖狼就会咬一口！"
    continueN = { "继续（剩余中奖${it}颗）" }
    allFound = "中奖牙齿全部找到了！"
    changePrizes = "更改中奖数量"
    menuRace = "群狼赛跑"
    menuRaceSub = "你追我赶的小狼赛跑"
    raceDesc = "选好人数，每人选一只动物。\n动物们在终点瑟瑟发抖地等着，小狼先咬到的动物第1名！\n也可以设定出结果的时间。"
    raceSeconds = "结果时间（秒）"
    raceTapStart = "准备好就出发！"
    raceStart = "🏁 出发"
    raceGo = "跑！"
    raceRunning = "奔跑中..."
    raceResult = { a, b -> "第一: ${a}号 · 最后: ${b}号" }
    raceAgain = "再跑一次"
    changeRace = "更改人数 / 时间"
    menuPirate = "狼轮盘"
    menuPirateSub = "插剑时狼跳出来就中奖"
    pirateHint = "点一个孔插入一把剑"
    slotsLeft = { "剩余${it}个孔" }
    popped = "砰！🐺 跳出来了！"
    poppedAt = { "第${it}把剑时跳出来了" }
    menuArrow = "转狼"
    menuArrowSub = "狼停下咬到谁谁中奖"
    arrowSeconds = "旋转时间（秒）"
    arrowTap = "点击狼开始转"
    arrowSpinning = "转转转..."
    arrowStopped = "啊呜！🐺 咬到了！"
    menuFour = "四子棋"
    menuFourSub = "横、竖、斜先连成4个就赢"
    fourRed = "红色"
    fourYellow = "黄色"
    fourTurn = { "轮到$it" }
    fourWin = { "🎉 ${it}获胜！" }
    fourDraw = "平局！"
    fourHint = "点一列放下棋子"
    fourRestart = "重新开始"
    installGuide = "安装说明"
    calcTitle = "计算器"
    splitBtn = "🍻 AA"
    fxBtn = "💱 汇率"
    splitTitle = "AA分摊"
    splitTotal = "总金额"
    splitPeople = "人数"
    splitRound = "向上取整到"
    splitNone = "不取整"
    splitPer = "每人"
    splitCollect = { a, b -> "共收 $a · 多出 $b" }
    splitNeedTotal = "请先在计算器输入总金额"
    fxTitle = "汇率换算"
    fxAmount = "金额"
    fxUpdated = { "更新于 $it" }
    fxLoading = "正在获取汇率..."
    fxOffline = "无网络，显示上次保存的汇率"
    fxError = "无法获取汇率，请检查网络连接"
    fxRefresh = "刷新"
    fxPick = "选择货币"
    fxNote = "汇率每天更新一次（仅供参考）"
    fiveSafe = "✓ 逃脱"
    fiveLoser = { "💀 ${it}输了！" }
    fiveLoserSub = "到最后还剩骰子"
    menuCards = "卡牌"
    menuCardsSub = "Up/Down · 胡志明"
    d789Title = "789"
    d789Sub = "7 添酒 · 8 半杯 · 9 干杯"
    d789Desc = "掷2颗骰子，看点数和的个位数。\n• 7 → 添酒\n• 8 → 喝一半\n• 9 → 一口干\n• 6 → 顺序反转（可选）\n• 其他 → 下一位"
    d789RevOpt = "掷出6时顺序反转"
    d789Top = "添酒"
    d789Half = "喝一半"
    d789Shot = "一口干"
    d789Rev = "顺序反转"
    d789Pass = "安全！下一位"
    d789Tap = "点击掷骰子"
    d789Sum = { "和 $it" }
    udTitle = "Up/Down"
    udSub = "下一张大还是小？"
    udDesc = "猜下一张牌比桌上的牌大(UP)还是小(DOWN)。\n• 基准牌是A或K时，猜红色还是黑色\n• 猜对轮到下一位，猜错罚酒并重置\n• 出现相同点数就干杯！\n大小顺序：A(最小) < 2 … 10 < J < Q < K(最大)"
    udStackOpt = "每猜对一次罚酒累加1杯"
    udUp = "UP"
    udDown = "DOWN"
    udRed = "红色"
    udBlack = "黑色"
    udGuess = "下一张？"
    udSuccess = "猜对了！"
    udFail = "猜错了！"
    udSame = "相同点数！干杯"
    udPot = { "累计${it}杯" }
    udDrink = { "罚酒${it}杯" }
    udNext = "下一步"
    udLeft = { "剩余${it}张" }
    hcmTitle = "胡志明"
    hcmSub = "和的个位是你的数字就罚酒"
    hcmDesc = "每人拿1张牌作为自己的数字（A=1，J·Q·K=10）。\n桌上翻开2张牌，轮到你时选1张，在上面放一张新牌。\n两张牌和的个位与你的数字相同就罚酒！（10·J·Q·K对应0）"
    hcmPick = "选择要覆盖的牌"
    hcmPenalty = { "${it}干杯！" }
    hcmNone = "没有惩罚"
    hcmSum = { a, b -> "和 $a → 个位 $b" }
    newGame = "新游戏"
    logTitle = "更新记录"
    logNew = { "v${it} 新内容" }
    logAll = "查看全部记录"
    logLatest = "最新"
    logOk = "知道了"
    menuFinger = "手指游戏"
    menuFingerSub = "手指选人"
    menuWolf = "狼游戏"
    menuWolfSub = "狼牙齿 · 群狼赛跑 · 狼轮盘 · 转狼"
    menuLuck = "拼手气"
    menuLuckSub = "爬梯子 · 转盘 · 传炸弹"
    menuBattle = "对战游戏"
    menuBattleSub = "四子棋"
    fingerTitle = "手指选人"
    fingerSub = "手指放上再松开，小狼来吐舌头！"
    fingerDesc = "先选要抽中几个人，大家各放一根手指在屏幕上。\n所有人松开手指0.5秒后，被抽中的位置会冒出小狼吐舌头气你。"
    fingerLosers = "抽中人数"
    fingerHint = "大家一起放上手指\n再一起松开 👆"
    fingerNeed = { "请至少放${it}根手指" }
    fingerResult = { "🐺 抽中${it}人！" }
    fingerPass = "安全"
    fingerMerong = "略略略~"
    fingerAgain = "点屏幕再来一次"
    menuQuick = "反应游戏"
    menuQuickSub = "反应速度对决 · 颜色反应对决 · 秒表"
    rankFirst = "👑 第1名"
    rankLast = "💀 最后"
    hiTag = "👑 最高"
    loTag = "💀 最低"
    caught = "💀 中招！"
    tapHubAgain = "点中间再来一次"
    secs = { "${it}秒" }
    biteWord = "嗷呜！"
    reactTitle = "反应速度对决"
    reactSub = "变绿的瞬间点击！"
    reactDesc = "把手机放在中间，屏幕会按人数平均分开。\n屏幕变绿的瞬间点自己的区域！\n最快第1名，最慢最后一名。变绿前点就是抢跑。"
    reactWait = "等一下..."
    reactWaitSub = "变绿了就点"
    reactGo = "就是现在！"
    reactFoul = "🚫 抢跑"
    reactFoulSub = "太早了"
    colorTitle = "颜色反应对决"
    colorSub = "出现指定颜色就点！"
    colorDesc = "中间显示本局的指定颜色。\n屏幕颜色不停变化，出现指定颜色时点自己的区域！\n没点到或在别的颜色时点就中招。大家都点到的话按快慢排名。"
    colorNames = "红,橙,黄,绿,蓝,紫"
    colorWhen = { "出现${it}时" }
    colorTap = "点！"
    colorThis = "这个色！"
    colorWrong = "错！不是这个色"
    colorMiss = "没点到"
    stopTitle = "秒表"
    stopSub = "心里数秒再点！"
    stopDesc = "选好目标时间，会显示在中间。\n开始后看不到时间。心里默数，觉得到了就点自己的区域！\n最接近目标的人第1名。"
    stopSeconds = "目标时间（秒）"
    stopCount = { "心里数${it}秒" }
    stopTapNow = "再点！"
    stopDone = "✋ 完成！请等待"
    bombTitle = "传炸弹"
    bombSub = "爆炸时拿着就中招！"
    bombDesc = "把手机放在中间，屏幕会按人数分开。\n炸弹传到你时点一下，按设定方向传出去。\n不知道什么时候爆炸！爆炸时拿着炸弹的人中招。"
    bombDir = "方向"
    dirCw = "顺时针"
    dirCcw = "逆时针"
    dirRandom = "随机"
    bombPass = "点一下传出去！"
    bombBoom = "砰！！"
    duelTitle = "骰子对决"
    duelSub = "用和或积比最高最低"
    duelDesc = "把手机放在中间，屏幕会按人数分开。\n点自己的区域掷骰子，得出点数之和或乘积。\n大家都掷完后显示最高和最低的人。"
    duelDice = "骰子数量"
    duelMode = "计算方式"
    duelSum = "加法"
    duelMul = "乘法"
    duelSumN = { "和 ${it}" }
    duelMulN = { "积 ${it}" }
    animalNames = "兔子,小猪,鸡,羊,鹿,老鼠,鸭子,牛,仓鼠,小鸡,小狗,小猫"
    racePick = { "选动物（${it}只 · 按点的顺序分跑道）" }
    raceLane = { n, a -> "${n}号 ${a}" }
    raceResultNames = { a, b -> "第1名：${a} · 最后：${b}" }
    raceNeed = { "请选${it}只动物" }
    pirateTease = "安全！小狼在气你 😜"
    shuffle = "重新洗牌"
    ladderDesc = "设定参加人数和中奖数量。\n点上方的号码就会沿着梯子往下走。"
    ladderHint = "点号码开始爬梯子"
    numberN = { "${it}号" }
    hiddenPrizes = { "藏着${it}个中奖" }
    noWinnerYet = "还没有中奖者"
    winnersList = { "中奖: $it" }
    showAll = "全部结果"
    newLadder = "新梯子"
    changeLadder = "更改人数 / 中奖数量"
    rouletteDesc = "设定转盘格数和中奖格数。\n中奖格的位置随机。"
    slices = "总格数"
    sliceWin = { "🎉 第${it}格 — 中奖！" }
    slicePass = { "第${it}格 — 安全" }
    drumroll = "咚咚咚..."
    tapWheel = "点击转盘"
    wheelInfo = { m, k -> "${m}格中有${k}格中奖" }
    spinHub = "转!"
    spin = "转动"
    shuffleWins = "重排中奖格"
    changeWheel = "更改格数 / 中奖数量"
}

val ThStrings = Strings().apply {
    appName = "Wolf Games"
    appSubtitle = "รวมเกมวงเหล้า"
    back = "กลับ"
    start = "เริ่มเกม"
    players = "จำนวนผู้เล่น"
    prizes = "จำนวนรางวัล"
    playAgain = "เล่นอีกครั้ง"
    exit = "ออก"
    again = "อีกครั้ง"
    rolling = "กำลังทอย..."
    win = "โดน!"
    pass = "รอด"
    language = "ภาษา"
    followSystem = "ตามการตั้งค่าโทรศัพท์"
    goHomepage = "ไปที่หน้าเว็บไซต์ไหม?"
    ok = "ไป"
    cancel = "ยกเลิก"
    menuDice = "ลูกเต๋า"
    menuDiceSub = "ทอยเต๋า · พลิกตัวเลข · เต๋า 5 ลูก · 789 · ดวลลูกเต๋า"
    menuCroc = "ฟันหมาป่า"
    menuCrocSub = "หลบฟันที่ซ่อนรางวัลไว้"
    menuLadder = "เกมบันได"
    menuLadderSub = "กำหนดผู้เล่นและรางวัล"
    menuRoulette = "วงล้อ"
    menuRouletteSub = "กำหนดช่องและรางวัล"
    rollTitle = "ทอยลูกเต๋า"
    rollSub = "ทุกคนใช้นิ้วพร้อมกัน"
    shutTitle = "พลิกตัวเลขแจ็กพอต"
    shutSub = "พลิกเลขสุดท้ายชนะ"
    fiveTitle = "เต๋า 5 ลูก"
    fiveSub = "1 ออก, 6 ส่งต่อ · คนสุดท้ายแพ้"
    reset = "รีเซ็ต"
    rollHint = "วางนิ้วบนหน้าจอ\nรอ 0.5 วินาทีแล้วยกนิ้ว\n\nเล่นพร้อมกันหลายคนได้\nแตะสองครั้งเพื่อรีเซ็ต"
    shutDesc = "ทอยลูกเต๋า 2 ลูก แล้วพลิกผลรวม หรือพลิกแต้มทั้งสอง\n" +
        "• เลือกผลรวม: พลิกเลขนั้นตัวเดียวแล้วจบตา\n" +
        "• เลือกแต้มหนึ่งตัว: พลิกอีกตัวต่อได้ (เช่น 1·4 → 5 หรือ 1 แล้ว 4)\n" +
        "ถ้าไม่มีเลขให้พลิก ตาคนถัดไป\n" +
        "พลิก 1–12 จนหมด คนที่พลิกเลขสุดท้ายชนะ!"
    solo = "เล่นคนเดียว"
    turnOf = { "ตาของผู้เล่น $it" }
    numbersLeft = { "เหลือ $it ตัว" }
    rollPrompt = "ทอยลูกเต๋าเลย"
    pickPrompt = { "แตะตัวเลขหนึ่งตัว: $it" }
    noMove = "ไม่มีเลขให้พลิก!"
    throwDice = "🎲 ทอยลูกเต๋า"
    nextPlayer = "คนถัดไป ▶"
    pickSecond = { "พลิก $it ต่อได้" }
    endTurn = "จบตานี้ ▶"
    jackpot = "🎉 แจ็กพอต!"
    playerWins = { "🎉 ผู้เล่น $it ชนะ!" }
    flippedLast = "พลิกเลขสุดท้ายแล้ว"
    changePlayers = "เปลี่ยนจำนวนผู้เล่น"
    fiveDesc = "ทุกคนเริ่มด้วยลูกเต๋า 5 ลูก แตะพื้นที่ของตัวเองเพื่อทอยเต๋าของตัวเอง\nเมื่อทุกคนทอยครบ ผลจะถูกนับพร้อมกัน\n• เต๋าที่ออก 1 ถูกคัดออก\n• เต๋าที่ออก 6 ส่งให้คนถัดไปตามเข็มนาฬิกา\nใครเต๋าหมดก่อนรอด! คนสุดท้ายที่ยังเหลือเต๋าแพ้\n\nวางโทรศัพท์กลางโต๊ะ แต่ละคนจะเห็นในทิศของตัวเอง"
    playerN = { "ผู้เล่น $it" }
    tapYourArea = { "แตะพื้นที่ของตัวเอง! (เหลือ $it คน)" }
    allRolled = "ทอยครบแล้ว! 1 ออก · 6 ไปตามเข็มนาฬิกา"
    winnersWin = { "🏆 $it ชนะ!" }
    winner = "🏆 ชนะ!"
    diceLeft = { "เหลือเต๋า $it ลูก" }
    tapToRoll = "👆 แตะเพื่อทอย"
    outPass = { ones, next, sixes -> "ออก $ones · ให้ P$next $sixes ลูก" }
    waitingOthers = "รอคนอื่นอยู่"
    bitten = "งับ! 🐺 โดนแล้ว!"
    pressTeeth = "กดฟันทีละซี่"
    bittenAt = { "โดนงับที่ซี่ที่ $it" }
    teethLeft = { left, k -> "เหลือฟัน $left ซี่ · เหลือที่งับ $k ซี่" }
    teethDesc = "ในฟันหมาป่า 20 ซี่ มีฟันที่งับซ่อนอยู่\nผลัดกันกดทีละซี่ ถ้าโดนหมาป่าจะงับ!"
    continueN = { "เล่นต่อ (เหลือที่งับ $it ซี่)" }
    allFound = "เจอฟันที่งับครบแล้ว!"
    changePrizes = "เปลี่ยนจำนวนรางวัล"
    menuRace = "วิ่งแข่งหมาป่า"
    menuRaceSub = "หมาป่าวิ่งแซงกันไปมา"
    raceDesc = "เลือกจำนวนคน แล้วแต่ละคนเลือกสัตว์\nสัตว์จะรอตัวสั่นอยู่ที่เส้นชัย สัตว์ที่หมาป่างับก่อนได้ที่ 1!\nตั้งเวลาที่จะได้ผลได้ด้วย"
    raceSeconds = "เวลาผลออก (วินาที)"
    raceTapStart = "พร้อมแล้วกดเริ่ม!"
    raceStart = "🏁 เริ่ม"
    raceGo = "ไป!"
    raceRunning = "กำลังวิ่ง..."
    raceResult = { a, b -> "ที่ 1: #$a · สุดท้าย: #$b" }
    raceAgain = "วิ่งอีกครั้ง"
    changeRace = "เปลี่ยนผู้เล่น / เวลา"
    menuPirate = "รูเล็ตหมาป่า"
    menuPirateSub = "เสียบดาบ ใครทำหมาป่าเด้งออกมาโดน"
    pirateHint = "แตะช่องเพื่อเสียบดาบ"
    slotsLeft = { "เหลือ $it ช่อง" }
    popped = "ป๊อป! 🐺 เด้งออกมาแล้ว!"
    poppedAt = { "เด้งออกมาที่ดาบเล่มที่ $it" }
    menuArrow = "หมุนหมาป่า"
    menuArrowSub = "หมาป่าหยุดงับใครคนนั้นโดน"
    arrowSeconds = "เวลาหมุน (วินาที)"
    arrowTap = "แตะหมาป่าเพื่อหมุน"
    arrowSpinning = "หมุนๆ..."
    arrowStopped = "งับ! 🐺 โดนแล้ว!"
    menuFour = "เรียง 4"
    menuFourSub = "เรียงให้ได้ 4 ตัวก่อนชนะ"
    fourRed = "แดง"
    fourYellow = "เหลือง"
    fourTurn = { "ตาของ$it" }
    fourWin = { "🎉 $it ชนะ!" }
    fourDraw = "เสมอ!"
    fourHint = "แตะคอลัมน์เพื่อหย่อนเหรียญ"
    fourRestart = "เริ่มใหม่"
    installGuide = "วิธีติดตั้ง"
    calcTitle = "เครื่องคิดเลข"
    splitBtn = "🍻 หารบิล"
    fxBtn = "💱 แลกเงิน"
    splitTitle = "หารค่าเหล้า"
    splitTotal = "ยอดรวม"
    splitPeople = "จำนวนคน"
    splitRound = "ปัดขึ้นทีละ"
    splitNone = "ไม่ปัด"
    splitPer = "ต่อคน"
    splitCollect = { a, b -> "เก็บได้ $a · เกิน $b" }
    splitNeedTotal = "ใส่ยอดรวมในเครื่องคิดเลขก่อน"
    fxTitle = "แปลงสกุลเงิน"
    fxAmount = "จำนวนเงิน"
    fxUpdated = { "ข้อมูล ณ $it" }
    fxLoading = "กำลังโหลดอัตรา..."
    fxOffline = "ออฟไลน์ แสดงอัตราล่าสุดที่บันทึกไว้"
    fxError = "โหลดอัตราไม่ได้ ตรวจสอบการเชื่อมต่ออินเทอร์เน็ต"
    fxRefresh = "รีเฟรช"
    fxPick = "เลือกสกุลเงิน"
    fxNote = "อัตราอัปเดตวันละครั้ง (ใช้อ้างอิง)"
    fiveSafe = "✓ รอด"
    fiveLoser = { "💀 $it แพ้!" }
    fiveLoserSub = "ยังเหลือลูกเต๋าถึงสุดท้าย"
    menuCards = "ไพ่"
    menuCardsSub = "Up/Down · โฮจิมินห์"
    d789Title = "789"
    d789Sub = "7 เติม · 8 ครึ่ง · 9 หมดแก้ว"
    d789Desc = "ทอยลูกเต๋า 2 ลูก ดูหลักหน่วยของผลรวม\n• 7 → เติมแก้ว\n• 8 → ดื่มครึ่งแก้ว\n• 9 → ดื่มหมดแก้ว\n• 6 → กลับลำดับ (ตัวเลือก)\n• อื่นๆ → คนถัดไป"
    d789RevOpt = "ออก 6 ให้กลับลำดับ"
    d789Top = "เติมแก้ว"
    d789Half = "ดื่มครึ่งแก้ว"
    d789Shot = "ดื่มหมดแก้ว"
    d789Rev = "กลับลำดับ"
    d789Pass = "รอด! คนถัดไป"
    d789Tap = "แตะเพื่อทอยลูกเต๋า"
    d789Sum = { "รวม $it" }
    udTitle = "Up/Down"
    udSub = "ไพ่ใบถัดไปสูงหรือต่ำ?"
    udDesc = "ทายว่าไพ่ใบถัดไปสูงกว่า (UP) หรือต่ำกว่า (DOWN) ใบบนโต๊ะ\n• ถ้าไพ่ฐานเป็น A หรือ K ให้ทายแดงหรือดำ\n• ถูก: ตาคนถัดไป ผิด: ดื่มแล้วเริ่มใหม่\n• ได้เลขเดียวกัน: หมดแก้ว!\nลำดับไพ่: A (ต่ำสุด) < 2 … 10 < J < Q < K (สูงสุด)"
    udStackOpt = "สะสม 1 แก้วทุกครั้งที่ทายถูก"
    udUp = "UP"
    udDown = "DOWN"
    udRed = "แดง"
    udBlack = "ดำ"
    udGuess = "ไพ่ใบถัดไป?"
    udSuccess = "ถูก!"
    udFail = "ผิด!"
    udSame = "เลขเดียวกัน! หมดแก้ว"
    udPot = { "สะสม $it แก้ว" }
    udDrink = { "ดื่ม $it แก้ว" }
    udNext = "ถัดไป"
    udLeft = { "เหลือ $it ใบ" }
    hcmTitle = "โฮจิมินห์"
    hcmSub = "หลักหน่วยของผลรวมตรงกับเลขเรา ต้องดื่ม"
    hcmDesc = "ทุกคนได้ไพ่ 1 ใบเป็นเลขของตัวเอง (A=1, J·Q·K=10)\nเปิดไพ่ 2 ใบบนโต๊ะ ถึงตาเราให้เลือก 1 ใบแล้ววางไพ่ใหม่ทับ\nถ้าหลักหน่วยของผลรวมสองใบตรงกับเลขเรา ต้องดื่ม! (10·J·Q·K ตรงกับ 0)"
    hcmPick = "เลือกไพ่ที่จะวางทับ"
    hcmPenalty = { "$it หมดแก้ว!" }
    hcmNone = "ไม่มีใครโดน"
    hcmSum = { a, b -> "รวม $a → หลักหน่วย $b" }
    newGame = "เกมใหม่"
    logTitle = "ประวัติการอัปเดต"
    logNew = { "มีอะไรใหม่ใน v${it}" }
    logAll = "ดูทั้งหมด"
    logLatest = "ล่าสุด"
    logOk = "ตกลง"
    menuFinger = "เกมนิ้ว"
    menuFingerSub = "จิ้มนิ้วเลือกคน"
    menuWolf = "เกมหมาป่า"
    menuWolfSub = "ฟันหมาป่า · วิ่งแข่งหมาป่า · รูเล็ตหมาป่า · หมุนหมาป่า"
    menuLuck = "เกมดวง"
    menuLuckSub = "เกมบันได · วงล้อ · ส่งระเบิด"
    menuBattle = "เกมดวล"
    menuBattleSub = "เรียง 4"
    fingerTitle = "จิ้มนิ้วเลือกคน"
    fingerSub = "วางนิ้วแล้วยก หมาป่าแลบลิ้นใส่!"
    fingerDesc = "เลือกจำนวนคนที่จะโดน แล้วทุกคนวางนิ้วบนหน้าจอคนละนิ้ว\nเมื่อทุกคนยกนิ้วออกครบ 0.5 วินาที หมาป่าจะโผล่ตรงตำแหน่งคนที่โดนแล้วแลบลิ้นแซว"
    fingerLosers = "จำนวนคนที่โดน"
    fingerHint = "วางนิ้วพร้อมกัน\nแล้วยกออกพร้อมกัน 👆"
    fingerNeed = { "วางนิ้วอย่างน้อย ${it} นิ้ว" }
    fingerResult = { "🐺 โดน ${it} คน!" }
    fingerPass = "รอด"
    fingerMerong = "แบร่~"
    fingerAgain = "แตะหน้าจอเพื่อเล่นใหม่"
    menuQuick = "เกมไหวพริบ"
    menuQuickSub = "ดวลความไว · ดวลสี · นาฬิกาจับเวลา"
    rankFirst = "👑 ที่ 1"
    rankLast = "💀 ที่โหล่"
    hiTag = "👑 สูงสุด"
    loTag = "💀 ต่ำสุด"
    caught = "💀 โดน!"
    tapHubAgain = "แตะตรงกลางเพื่อเล่นใหม่"
    secs = { "${it} วิ" }
    biteWord = "งับ!"
    reactTitle = "ดวลความไว"
    reactSub = "แตะทันทีที่เป็นสีเขียว!"
    reactDesc = "วางโทรศัพท์ไว้ตรงกลาง หน้าจอจะแบ่งเท่า ๆ กันตามจำนวนคน\nทันทีที่หน้าจอเป็นสีเขียว ให้แตะพื้นที่ของตัวเอง!\nเร็วสุดได้ที่ 1 ช้าสุดได้ที่โหล่ แตะก่อนสีเขียวถือว่าออกตัวก่อน"
    reactWait = "รอก่อน..."
    reactWaitSub = "เป็นสีเขียวแล้วแตะ"
    reactGo = "แตะเลย!"
    reactFoul = "🚫 ออกตัวก่อน"
    reactFoulSub = "เร็วไป"
    colorTitle = "ดวลสี"
    colorSub = "เจอสีที่กำหนดแล้วแตะ!"
    colorDesc = "ตรงกลางจะแสดงสีที่กำหนดของรอบนี้\nสีหน้าจอจะเปลี่ยนไปเรื่อย ๆ พอเป็นสีที่กำหนดให้แตะพื้นที่ของตัวเอง!\nแตะไม่ทันหรือแตะตอนสีอื่นถือว่าโดน ถ้าทุกคนแตะได้ จะจัดอันดับตามความเร็ว"
    colorNames = "แดง,ส้ม,เหลือง,เขียว,น้ำเงิน,ม่วง"
    colorWhen = { "พอเป็นสี${it}" }
    colorTap = "แตะ!"
    colorThis = "สีนี้!"
    colorWrong = "ผิด! คนละสี"
    colorMiss = "แตะไม่ทัน"
    stopTitle = "นาฬิกาจับเวลา"
    stopSub = "นับในใจแล้วแตะ!"
    stopDesc = "เลือกเวลาเป้าหมาย จะแสดงอยู่ตรงกลาง\nพอเริ่มจะไม่เห็นเวลา ให้นับในใจ คิดว่าถึงเวลาแล้วก็แตะพื้นที่ของตัวเอง!\nใกล้เป้าหมายที่สุดได้ที่ 1"
    stopSeconds = "เวลาเป้าหมาย (วินาที)"
    stopCount = { "นับในใจ ${it} วินาที" }
    stopTapNow = "แล้วแตะ!"
    stopDone = "✋ เสร็จ! รอก่อน"
    bombTitle = "ส่งระเบิด"
    bombSub = "ถือตอนระเบิดตูม โดน!"
    bombDesc = "วางโทรศัพท์ไว้ตรงกลาง หน้าจอจะแบ่งตามจำนวนคน\nพอระเบิดมาถึงเรา แตะเพื่อส่งต่อตามทิศที่เลือก\nไม่รู้ว่าจะระเบิดเมื่อไหร่! ใครถืออยู่ตอนระเบิดคนนั้นโดน"
    bombDir = "ทิศทาง"
    dirCw = "ตามเข็ม"
    dirCcw = "ทวนเข็ม"
    dirRandom = "สุ่ม"
    bombPass = "แตะเพื่อส่งต่อ!"
    bombBoom = "ตูม!!"
    duelTitle = "ดวลลูกเต๋า"
    duelSub = "วัดสูงสุด·ต่ำสุดด้วยผลบวกหรือผลคูณ"
    duelDesc = "วางโทรศัพท์ไว้ตรงกลาง หน้าจอจะแบ่งตามจำนวนคน\nแตะพื้นที่ของตัวเองเพื่อทอยเต๋า จะได้ผลบวกหรือผลคูณ\nพอทุกคนทอยแล้วจะแสดงคนที่สูงสุดและต่ำสุด"
    duelDice = "จำนวนลูกเต๋า"
    duelMode = "วิธีคิด"
    duelSum = "บวก"
    duelMul = "คูณ"
    duelSumN = { "รวม ${it}" }
    duelMulN = { "คูณ ${it}" }
    animalNames = "กระต่าย,หมู,ไก่,แกะ,กวาง,หนู,เป็ด,วัว,แฮมสเตอร์,ลูกเจี๊ยบ,ลูกหมา,ลูกแมว"
    racePick = { "เลือกสัตว์ (${it} ตัว · ลำดับที่แตะ = เลขลู่)" }
    raceLane = { n, a -> "ลู่ ${n} ${a}" }
    raceResultNames = { a, b -> "ที่ 1: ${a} · ที่โหล่: ${b}" }
    raceNeed = { "เลือกสัตว์ ${it} ตัว" }
    pirateTease = "รอด! หมาป่าแซวแล้ว 😜"
    shuffle = "สุ่มใหม่"
    ladderDesc = "กำหนดจำนวนผู้เล่นและจำนวนรางวัล\nแตะหมายเลขด้านบนเพื่อไต่บันไดลงไป"
    ladderHint = "แตะหมายเลขเพื่อเริ่ม"
    numberN = { "#$it" }
    hiddenPrizes = { "มีรางวัลซ่อนอยู่ $it อัน" }
    noWinnerYet = "ยังไม่มีคนโดน"
    winnersList = { "โดน: $it" }
    showAll = "ดูทั้งหมด"
    newLadder = "บันไดใหม่"
    changeLadder = "เปลี่ยนผู้เล่น / รางวัล"
    rouletteDesc = "กำหนดจำนวนช่องและช่องรางวัล\nตำแหน่งช่องรางวัลจะสุ่ม"
    slices = "จำนวนช่อง"
    sliceWin = { "🎉 ช่อง $it — โดน!" }
    slicePass = { "ช่อง $it — รอด" }
    drumroll = "ลุ้นๆ..."
    tapWheel = "แตะที่วงล้อ"
    wheelInfo = { m, k -> "รางวัล $k จาก $m ช่อง" }
    spinHub = "หมุน!"
    spin = "หมุน"
    shuffleWins = "สุ่มช่องรางวัล"
    changeWheel = "เปลี่ยนช่อง / รางวัล"
}

val ViStrings = Strings().apply {
    appName = "Wolf Games"
    appSubtitle = "Bộ trò chơi nhậu"
    back = "Quay lại"
    start = "Bắt đầu"
    players = "Số người chơi"
    prizes = "Số ô trúng"
    playAgain = "Chơi lại"
    exit = "Thoát"
    again = "Lại"
    rolling = "Đang lắc..."
    win = "Trúng"
    pass = "Qua"
    language = "Ngôn ngữ"
    followSystem = "Theo cài đặt điện thoại"
    goHomepage = "Chuyển đến trang chủ?"
    ok = "Mở"
    cancel = "Hủy"
    menuDice = "Xúc xắc"
    menuDiceSub = "Lắc xúc xắc · Lật số · 5 xúc xắc · 789 · Đấu xúc xắc"
    menuCroc = "Răng sói"
    menuCrocSub = "Tránh những chiếc răng cắn đang ẩn"
    menuLadder = "Leo thang"
    menuLadderSub = "Chọn số người và số trúng"
    menuRoulette = "Vòng quay"
    menuRouletteSub = "Chọn số ô và số trúng"
    rollTitle = "Lắc xúc xắc"
    rollSub = "Mọi người cùng chạm một lúc"
    shutTitle = "Lật số Jackpot"
    shutSub = "Lật số cuối cùng là thắng"
    fiveTitle = "5 xúc xắc"
    fiveSub = "1 bị loại, 6 chuyền đi · người cuối cùng thua"
    reset = "Đặt lại"
    rollHint = "Đặt ngón tay lên màn hình,\nchờ 0,5 giây rồi nhấc lên\n\nNhiều người có thể chơi cùng lúc\nChạm hai lần để đặt lại"
    shutDesc = "Tung 2 xúc xắc, lật tổng hoặc lật hai số.\n" +
        "• Chọn tổng: chỉ lật số đó và hết lượt\n" +
        "• Chọn một số: có thể lật tiếp số còn lại (vd: 1·4 → 5, hoặc 1 rồi 4)\n" +
        "Không còn số để lật thì đến lượt người kế tiếp.\n" +
        "Lật hết 1–12, ai lật số cuối cùng là thắng!"
    solo = "Chơi một mình"
    turnOf = { "Lượt người chơi $it" }
    numbersLeft = { "Còn $it số" }
    rollPrompt = "Hãy tung xúc xắc"
    pickPrompt = { "Chạm một số: $it" }
    noMove = "Không còn số để lật!"
    throwDice = "🎲 Tung xúc xắc"
    nextPlayer = "Người kế tiếp ▶"
    pickSecond = { "Có thể lật tiếp $it" }
    endTurn = "Kết thúc lượt ▶"
    jackpot = "🎉 Jackpot!"
    playerWins = { "🎉 Người chơi $it thắng!" }
    flippedLast = "Đã lật số cuối cùng"
    changePlayers = "Đổi số người"
    fiveDesc = "Mỗi người bắt đầu với 5 xúc xắc. Chạm vào khu vực của mình để lắc xúc xắc của mình.\nKhi tất cả đã lắc xong, kết quả được tính cùng lúc.\n• Xúc xắc ra 1 bị loại\n• Xúc xắc ra 6 chuyền cho người kế tiếp theo chiều kim đồng hồ\nAi hết xúc xắc là thoát! Người cuối cùng còn xúc xắc là thua.\n\nĐặt điện thoại giữa bàn để mỗi người nhìn theo hướng của mình."
    playerN = { "Người chơi $it" }
    tapYourArea = { "Chạm vào khu vực của bạn! (còn $it người)" }
    allRolled = "Đã lắc xong! 1 bị loại · 6 chuyền đi"
    winnersWin = { "🏆 $it thắng!" }
    winner = "🏆 Thắng!"
    diceLeft = { "Còn $it xúc xắc" }
    tapToRoll = "👆 Chạm để lắc"
    outPass = { ones, next, sixes -> "Loại $ones · cho P$next $sixes" }
    waitingOthers = "Đang chờ người khác"
    bitten = "Phập! 🐺 Trúng rồi!"
    pressTeeth = "Lần lượt nhấn từng cái răng"
    bittenAt = { "Bị cắn ở răng thứ $it" }
    teethLeft = { left, k -> "Còn $left răng · còn $k răng cắn" }
    teethDesc = "Trong 20 chiếc răng của chú sói có giấu răng cắn.\nLần lượt nhấn từng cái, trúng là sói cắn phập!"
    continueN = { "Tiếp tục (còn $it răng cắn)" }
    allFound = "Đã tìm hết răng cắn!"
    changePrizes = "Đổi số ô trúng"
    menuRace = "Sói chạy đua"
    menuRaceSub = "Những chú sói thay nhau dẫn đầu"
    raceDesc = "Chọn số người rồi mỗi người chọn một con vật.\nCác con vật run rẩy chờ ở vạch đích — con bị sói ngoạm trước là hạng 1!\nCó thể chọn thời gian ra kết quả."
    raceSeconds = "Thời gian kết quả (giây)"
    raceTapStart = "Sẵn sàng thì xuất phát!"
    raceStart = "🏁 Xuất phát"
    raceGo = "Chạy!"
    raceRunning = "Đang chạy..."
    raceResult = { a, b -> "Nhất: #$a · Bét: #$b" }
    raceAgain = "Chạy lại"
    changeRace = "Đổi số người / thời gian"
    menuPirate = "Sói may rủi"
    menuPirateSub = "Cắm kiếm, ai làm sói bật ra là trúng"
    pirateHint = "Chạm vào một lỗ để cắm kiếm"
    slotsLeft = { "Còn $it lỗ" }
    popped = "Bụp! 🐺 Sói bật ra rồi!"
    poppedAt = { "Bật ra ở thanh kiếm thứ $it" }
    menuArrow = "Quay sói"
    menuArrowSub = "Sói dừng lại cắn ai người đó trúng"
    arrowSeconds = "Thời gian quay (giây)"
    arrowTap = "Chạm vào sói để quay"
    arrowSpinning = "Đang quay..."
    arrowStopped = "Phập! 🐺 Cắn rồi!"
    menuFour = "Nối 4"
    menuFourSub = "Xếp được 4 quân liên tiếp trước là thắng"
    fourRed = "Đỏ"
    fourYellow = "Vàng"
    fourTurn = { "Lượt $it" }
    fourWin = { "🎉 $it thắng!" }
    fourDraw = "Hòa!"
    fourHint = "Chạm vào một cột để thả quân"
    fourRestart = "Chơi lại"
    installGuide = "Hướng dẫn cài đặt"
    calcTitle = "Máy tính"
    splitBtn = "🍻 Chia tiền"
    fxBtn = "💱 Đổi tiền"
    splitTitle = "Chia tiền nhậu"
    splitTotal = "Tổng tiền"
    splitPeople = "Số người"
    splitRound = "Làm tròn lên"
    splitNone = "Không"
    splitPer = "Mỗi người"
    splitCollect = { a, b -> "Thu được $a · dư $b" }
    splitNeedTotal = "Nhập tổng tiền vào máy tính trước"
    fxTitle = "Đổi tiền tệ"
    fxAmount = "Số tiền"
    fxUpdated = { "Cập nhật: $it" }
    fxLoading = "Đang tải tỷ giá..."
    fxOffline = "Ngoại tuyến — hiển thị tỷ giá đã lưu"
    fxError = "Không tải được tỷ giá. Kiểm tra kết nối mạng"
    fxRefresh = "Làm mới"
    fxPick = "Chọn tiền tệ"
    fxNote = "Tỷ giá cập nhật mỗi ngày một lần (tham khảo)"
    fiveSafe = "✓ Thoát"
    fiveLoser = { "💀 $it thua!" }
    fiveLoserSub = "Còn xúc xắc đến cuối cùng"
    menuCards = "Bài"
    menuCardsSub = "Up/Down · Hồ Chí Minh"
    d789Title = "789"
    d789Sub = "7 rót thêm · 8 nửa ly · 9 cạn ly"
    d789Desc = "Tung 2 xúc xắc, xem chữ số hàng đơn vị của tổng.\n• 7 → rót thêm\n• 8 → uống nửa ly\n• 9 → uống cạn ly\n• 6 → đổi chiều (tùy chọn)\n• Còn lại → người kế tiếp"
    d789RevOpt = "Ra 6 thì đổi chiều"
    d789Top = "Rót thêm"
    d789Half = "Uống nửa ly"
    d789Shot = "Uống cạn ly"
    d789Rev = "Đổi chiều"
    d789Pass = "Qua! Người kế tiếp"
    d789Tap = "Chạm để tung xúc xắc"
    d789Sum = { "Tổng $it" }
    udTitle = "Up/Down"
    udSub = "Lá tiếp theo cao hay thấp?"
    udDesc = "Đoán lá bài tiếp theo cao hơn (UP) hay thấp hơn (DOWN) lá trên bàn.\n• Nếu lá gốc là A hoặc K thì đoán đỏ hay đen\n• Đúng: đến lượt người kế. Sai: uống phạt và chơi lại\n• Ra cùng số: cạn ly!\nThứ tự: A (thấp nhất) < 2 … 10 < J < Q < K (cao nhất)"
    udStackOpt = "Cộng dồn 1 ly mỗi lần đoán đúng"
    udUp = "UP"
    udDown = "DOWN"
    udRed = "Đỏ"
    udBlack = "Đen"
    udGuess = "Lá tiếp theo?"
    udSuccess = "Đúng rồi!"
    udFail = "Sai rồi!"
    udSame = "Cùng số! Cạn ly"
    udPot = { "Dồn $it ly" }
    udDrink = { "Uống $it ly" }
    udNext = "Tiếp"
    udLeft = { "Còn $it lá" }
    hcmTitle = "Hồ Chí Minh"
    hcmSub = "Chữ số cuối của tổng trùng số mình thì uống"
    hcmDesc = "Mỗi người nhận 1 lá làm số của mình (A=1, J·Q·K=10).\nLật 2 lá trên bàn; đến lượt thì chọn 1 lá và đặt lá mới lên trên.\nNếu chữ số cuối của tổng 2 lá trùng số của bạn thì uống! (10·J·Q·K ứng với 0)"
    hcmPick = "Chọn lá để đặt đè"
    hcmPenalty = { "$it cạn ly!" }
    hcmNone = "Không ai bị phạt"
    hcmSum = { a, b -> "Tổng $a → số cuối $b" }
    newGame = "Ván mới"
    logTitle = "Lịch sử cập nhật"
    logNew = { "Có gì mới trong v${it}" }
    logAll = "Xem tất cả"
    logLatest = "Mới nhất"
    logOk = "OK"
    menuFinger = "Trò chơi ngón tay"
    menuFingerSub = "Chọn ngón tay"
    menuWolf = "Trò chơi sói"
    menuWolfSub = "Răng sói · Sói chạy đua · Sói may rủi · Quay sói"
    menuLuck = "Trò may rủi"
    menuLuckSub = "Leo thang · Vòng quay · Chuyền bom"
    menuBattle = "Trò đối kháng"
    menuBattleSub = "Nối 4"
    fingerTitle = "Chọn ngón tay"
    fingerSub = "Đặt ngón tay rồi nhấc, sói lè lưỡi trêu!"
    fingerDesc = "Chọn số người bị chọn, rồi mỗi người đặt một ngón tay lên màn hình.\nKhi mọi người nhấc tay ra được 0,5 giây, sói sẽ hiện ra ở chỗ người bị chọn và lè lưỡi trêu."
    fingerLosers = "Số người bị chọn"
    fingerHint = "Mọi người cùng đặt ngón tay\nrồi cùng nhấc ra 👆"
    fingerNeed = { "Cần ít nhất ${it} ngón tay" }
    fingerResult = { "🐺 ${it} người bị chọn!" }
    fingerPass = "Thoát"
    fingerMerong = "Lêu lêu~"
    fingerAgain = "Chạm màn hình để chơi lại"
    menuQuick = "Trò phản xạ"
    menuQuickSub = "Đấu phản xạ · Đấu màu · Bấm giờ"
    rankFirst = "👑 Hạng 1"
    rankLast = "💀 Bét"
    hiTag = "👑 Cao nhất"
    loTag = "💀 Thấp nhất"
    caught = "💀 Dính!"
    tapHubAgain = "Chạm giữa để chơi lại"
    secs = { "${it} giây" }
    biteWord = "Ngoạm!"
    reactTitle = "Đấu phản xạ"
    reactSub = "Chạm ngay khi màn hình xanh!"
    reactDesc = "Đặt điện thoại giữa bàn, màn hình chia đều theo số người.\nNgay khi màn hình chuyển xanh, chạm vào phần của mình!\nNhanh nhất hạng 1, chậm nhất bét. Chạm trước khi xanh là phạm quy."
    reactWait = "Chờ đã..."
    reactWaitSub = "Xanh rồi thì chạm"
    reactGo = "Chạm ngay!"
    reactFoul = "🚫 Phạm quy"
    reactFoulSub = "Sớm quá"
    colorTitle = "Đấu màu"
    colorSub = "Thấy đúng màu thì chạm!"
    colorDesc = "Giữa màn hình là màu chỉ định của ván này.\nMàu màn hình đổi liên tục, khi ra màu chỉ định thì chạm vào phần của mình!\nKhông chạm kịp hoặc chạm sai màu là dính. Nếu ai cũng chạm được thì xếp hạng theo tốc độ."
    colorNames = "Đỏ,Cam,Vàng,Xanh lá,Xanh dương,Tím"
    colorWhen = { "Khi ra màu ${it}" }
    colorTap = "Chạm!"
    colorThis = "Màu này!"
    colorWrong = "Sai màu!"
    colorMiss = "Không kịp"
    stopTitle = "Bấm giờ"
    stopSub = "Đếm thầm rồi chạm!"
    stopDesc = "Chọn thời gian mục tiêu, sẽ hiện ở giữa.\nBắt đầu rồi thì không thấy giờ. Đếm thầm, thấy đủ rồi thì chạm vào phần của mình!\nGần mục tiêu nhất là hạng 1."
    stopSeconds = "Thời gian mục tiêu (giây)"
    stopCount = { "Đếm thầm ${it} giây" }
    stopTapNow = "rồi chạm!"
    stopDone = "✋ Xong! Chờ nhé"
    bombTitle = "Chuyền bom"
    bombSub = "Cầm bom lúc nổ là dính!"
    bombDesc = "Đặt điện thoại giữa bàn, màn hình chia theo số người.\nBom đến chỗ mình thì chạm để chuyền theo hướng đã chọn.\nKhông biết khi nào nổ! Ai cầm bom lúc nổ là dính."
    bombDir = "Hướng"
    dirCw = "Theo chiều kim"
    dirCcw = "Ngược chiều kim"
    dirRandom = "Ngẫu nhiên"
    bombPass = "Chạm để chuyền!"
    bombBoom = "BÙM!!"
    duelTitle = "Đấu xúc xắc"
    duelSub = "So cao thấp bằng tổng hoặc tích"
    duelDesc = "Đặt điện thoại giữa bàn, màn hình chia theo số người.\nChạm vào phần của mình để lắc, ra tổng hoặc tích các xúc xắc.\nAi cũng lắc xong sẽ hiện người cao nhất và thấp nhất."
    duelDice = "Số xúc xắc"
    duelMode = "Cách tính"
    duelSum = "Cộng"
    duelMul = "Nhân"
    duelSumN = { "Tổng ${it}" }
    duelMulN = { "Tích ${it}" }
    animalNames = "Thỏ,Heo,Gà,Cừu,Hươu,Chuột,Vịt,Bò,Hamster,Gà con,Cún,Mèo"
    racePick = { "Chọn con vật (${it} con · thứ tự chạm = số làn)" }
    raceLane = { n, a -> "Làn ${n} ${a}" }
    raceResultNames = { a, b -> "Hạng 1: ${a} · Bét: ${b}" }
    raceNeed = { "Hãy chọn ${it} con vật" }
    pirateTease = "Thoát! Sói đang trêu bạn 😜"
    shuffle = "Xáo lại"
    ladderDesc = "Chọn số người chơi và số ô trúng.\nChạm số ở trên để đi xuống thang."
    ladderHint = "Chạm số để leo thang"
    numberN = { "#$it" }
    hiddenPrizes = { "Có $it ô trúng đang ẩn" }
    noWinnerYet = "Chưa ai trúng"
    winnersList = { "Trúng: $it" }
    showAll = "Xem tất cả"
    newLadder = "Thang mới"
    changeLadder = "Đổi số người / số trúng"
    rouletteDesc = "Chọn số ô và số ô trúng của vòng quay.\nVị trí ô trúng được xáo ngẫu nhiên."
    slices = "Tổng số ô"
    sliceWin = { "🎉 Ô $it — Trúng!" }
    slicePass = { "Ô $it — Qua" }
    drumroll = "Hồi hộp quá..."
    tapWheel = "Chạm vào vòng quay"
    wheelInfo = { m, k -> "$k ô trúng trên $m ô" }
    spinHub = "Quay!"
    spin = "Quay"
    shuffleWins = "Xáo ô trúng"
    changeWheel = "Đổi số ô / số trúng"
}

fun stringsFor(lang: Lang): Strings = when (lang) {
    Lang.KO -> KoStrings
    Lang.EN -> EnStrings
    Lang.JA -> JaStrings
    Lang.ZH -> ZhStrings
    Lang.TH -> ThStrings
    Lang.VI -> ViStrings
}

/** 현재 언어의 문구 — 화면에서 LocalStrings.current 로 사용 */
val LocalStrings = staticCompositionLocalOf { KoStrings }
