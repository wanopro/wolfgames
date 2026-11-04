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
    // v1.5
    lateinit var tugTitle: String
    lateinit var tugSub: String
    lateinit var tugDesc: String
    lateinit var tugMash: String
    lateinit var tugTaps: String
    lateinit var winWord: String
    lateinit var shotWord: String
    lateinit var readyWord: String
    lateinit var slapTitle: String
    lateinit var slapSub: String
    lateinit var slapDesc: String
    lateinit var slapAtt: String
    lateinit var slapDef: String
    lateinit var slapAttHint: String
    lateinit var slapAttWait: String
    lateinit var slapDefHint: String
    lateinit var slapDefHold: String
    lateinit var slapHit: String
    lateinit var slapOuch: String
    lateinit var slapMiss: String
    lateinit var slapDodge: String
    lateinit var slapFlinch: String
    lateinit var slapFlinchAtt: String
    lateinit var slapSmack: String
    lateinit var kcTitle: String
    lateinit var kcSub: String
    lateinit var kcDesc: String
    lateinit var kcNames: String
    lateinit var kcDescs: String
    lateinit var kcTap: String
    lateinit var kcLeft: (Int) -> String
    lateinit var kcLastKing: String
    lateinit var kcLastKingSub: String
    lateinit var kcRules: String
    lateinit var kcShuffle: String
    lateinit var kcEmpty: String
    lateinit var beerTitle: String
    lateinit var beerSub: String
    lateinit var beerDesc: String
    lateinit var beerLine: String
    lateinit var beerTap: String
    lateinit var beerOver: String
    lateinit var coinTitle: String
    lateinit var coinSub: String
    lateinit var coinDesc: String
    lateinit var coinHead: String
    lateinit var coinTail: String
    lateinit var coinPick: String
    lateinit var coinRight: String
    lateinit var coinWrong: String
    lateinit var coinResHead: String
    lateinit var coinResTail: String
    lateinit var coinEdge: String
    lateinit var qrTitle: String
    lateinit var qrEdit: String
    lateinit var qrPrivacy: String
    lateinit var qrEmpty: String
    lateinit var qrScan: String
    lateinit var qrSwipe: String
    lateinit var qrClose: String
    lateinit var qrSave: String
    lateinit var qrPhone: String
    lateinit var qrId: String
    lateinit var qrLink: String
    lateinit var tipBtn: String
    lateinit var tipTitle: String
    lateinit var tipAmount: String
    lateinit var tipLabel: (Int) -> String
    lateinit var tipTotal: String
    lateinit var tipSplit: String
    lateinit var feedback: String
    lateinit var langPickTwo: String
    lateinit var plinkoTitle: String
    lateinit var plinkoSub: String
    lateinit var plinkoDesc: String
    lateinit var plinkoTap: String
    lateinit var plinkoShuffling: String
    lateinit var plinkoSlots: String
    lateinit var tttTitle: String
    lateinit var tttSub: String
    lateinit var tttDesc: String
    lateinit var green: String
    lateinit var red: String
    lateinit var winsOf: (String) -> String
    lateinit var drawWord: String
    lateinit var timeUp: String
    lateinit var gomokuTitle: String
    lateinit var gomokuSub: String
    lateinit var gomokuDesc: String
    lateinit var black: String
    lateinit var white: String
    lateinit var banned33: String
    lateinit var hotelBtn: String
    lateinit var hotelTitle: String
    lateinit var hotelName: String
    lateinit var hotelAddr: String
    lateinit var hotelPhone: String
    lateinit var hotelNote: String
    lateinit var hotelAdd: String
    lateinit var hotelDelete: String
    lateinit var hotelEmpty: String
    lateinit var hotelTakeMe: String
    lateinit var hotelMap: String
    lateinit var hotelCall: String
    lateinit var hotelEditBtn: String
    lateinit var colorTurn: (String) -> String
    lateinit var youAre: (String) -> String
    lateinit var phraseBtn: String
    lateinit var settleBtn: String
    lateinit var nbTitle: String
    lateinit var nbSub: String
    lateinit var nbDesc: String
    lateinit var nbMax: String
    lateinit var nbEnter: String
    lateinit var nbUp: String
    lateinit var nbDown: String
    lateinit var nbBoom: String
    lateinit var nbOut: String
    lateinit var nbPass: String
    lateinit var nbOk: String
    lateinit var blTitle: String
    lateinit var blSub: String
    lateinit var blDesc: String
    lateinit var blTap: String
    lateinit var blPumps: (Int) -> String
    lateinit var blPop: String
    lateinit var phTitle: String
    lateinit var phDesc: String
    lateinit var phTo: String
    lateinit var phrases: String
    lateinit var stTitle: String
    lateinit var stDesc: String
    lateinit var stPeople: String
    lateinit var stAddPerson: String
    lateinit var stAmount: String
    lateinit var stMemo: String
    lateinit var stPaidBy: String
    lateinit var stFor: String
    lateinit var stAdd: String
    lateinit var stList: String
    lateinit var stEmpty: String
    lateinit var stResult: String
    lateinit var stEven: String
    lateinit var stTotal: (String) -> String
    lateinit var stSpent: (String) -> String
    lateinit var stReset: String
    lateinit var stResetAsk: String
    lateinit var nbDazed: String
    lateinit var phSpeak: String
    lateinit var phNoVoice: String
    lateinit var phFun: String
    lateinit var phSwear: String
    lateinit var phCats: String
    lateinit var tsTitle: String
    lateinit var tsSub: String
    lateinit var tsDesc: String
    lateinit var tsFloor: (Int) -> String
    lateinit var tsTap: String
    lateinit var tsFall: String
    lateinit var smTitle: String
    lateinit var smSub: String
    lateinit var smDesc: String
    lateinit var smLevel: (Int) -> String
    lateinit var smWatch: String
    lateinit var smGo: String
    lateinit var smOk: String
    lateinit var smWrong: String
    lateinit var wcTitle: String
    lateinit var wcSub: String
    lateinit var wcDesc: String
    lateinit var wcWires: String
    lateinit var wcTap: String
    lateinit var wcSafe: String
    lateinit var wcBoom: String
    lateinit var wcLeft: (Int) -> String
    lateinit var cuTitle: String
    lateinit var cuSub: String
    lateinit var cuDesc: String
    lateinit var cuHint: String
    lateinit var cuFall: String
    lateinit var stExport: String
    lateinit var stExportDesc: String
    lateinit var stCsv: String
    lateinit var stTxt: String
    lateinit var stShare: String
    lateinit var stSaved: String
    lateinit var stMid: String
    lateinit var stMidDesc: String
    lateinit var stUnit: String
    lateinit var stMidDone: String
    lateinit var stMidTag: (Int) -> String
    lateinit var stResetSub: String
    lateinit var stResetSave: String
    lateinit var stResetGo: String
    lateinit var stCols: String
    lateinit var szBtn: String
    lateinit var szTitle: String
    lateinit var szDesc: String
    lateinit var szCats: String
    lateinit var upTitle: String
    lateinit var upSub: (String, String) -> String
    lateinit var upGo: String
    lateinit var upLater: String
    lateinit var upDownloading: String
    lateinit var upAllow: String
    lateinit var upFail: String
    lateinit var upCheck: String
    lateinit var upLatest: String
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
    menuCardsSub = "Up/Down · 호치민 · 킹스컵"
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
    menuLuckSub = "사다리게임 · 돌림판 · 폭탄돌리기 · 동전 던지기 · 플링코 · 숫자 폭탄 · 풍선 불기 · 폭탄 선 자르기 · 손가락선택"
    menuBattle = "대결게임"
    menuBattleSub = "삼목 · 4목 · 오목 · 연타 땅따먹기 · 슬랩 대결"
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
    menuQuickSub = "반응속도 대결 · 색 반응 대결 · 스탑워치 · 맥주 거품 채우기 · 탑 쌓기 · 따라 하기 · 늑대컬링"
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
    tugTitle = "연타 땅따먹기"
    tugSub = "연타해서 내 땅을 넓혀라!"
    tugDesc = "2명이 휴대폰을 사이에 두고 위·아래로 마주 앉아요.\nGO! 하면 내 쪽 화면을 미친 듯이 연타!\n가운데 깃발(🎀)을 상대편 끝 선까지 밀어내면 승리, 진 사람은 원샷!"
    tugMash = "연타!! 👊"
    tugTaps = "탭"
    winWord = "👑 승리!"
    shotWord = "💀 원샷!"
    readyWord = "준비…"
    slapTitle = "슬랩 대결"
    slapSub = "늑대 앞발로 손등 때리기!"
    slapDesc = "2명이 위·아래로 마주 앉아요.\n🛡️ 수비: 내 쪽 화면을 손가락으로 꾹 누르고 있다가, 공격이 오면 떼서 피하기!\n🐾 공격: 아무 때나 내 쪽을 터치하면 찰싹!\n맞으면 수비가 원샷, 피하면 공수 교대. 공격이 안 왔는데 떼면 움찔 반칙으로 원샷!"
    slapAtt = "🐾 공격"
    slapDef = "🛡️ 수비"
    slapAttHint = "아무 때나 터치하면 찰싹!"
    slapAttWait = "수비가 누를 때까지 기다려…"
    slapDefHint = "꾹 누르고 있다가 피하기!"
    slapDefHold = "여기를 꾹 눌러!"
    slapHit = "짝!! 명중 🎯"
    slapOuch = "아야!! 😵 원샷"
    slapMiss = "헛손질… 공수 교대 🔄"
    slapDodge = "피했다! 🙌"
    slapFlinch = "움찔 반칙! 🚫 원샷"
    slapFlinchAtt = "상대가 움찔! 🤭"
    slapSmack = "짝!"
    kcTitle = "글로벌 킹스컵"
    kcSub = "그림만 봐도 아는 킹스컵"
    kcDesc = "카드 더미를 터치해서 한 장씩 뽑아요. 카드마다 그림으로 할 일이 나와요.\n👑 K가 나올 때마다 가운데 킹스컵 잔에 술을 조금씩 따르고, 4번째 K를 뽑은 사람이 킹스컵 원샷!"
    kcNames = "폭포|지목|나|화장실 프리패스|댄스 릴레이|다 같이 짠|하늘|짝꿍|끝말잇기|카테고리|규칙 만들기|질문 마스터|킹스컵"
    kcDescs = "다 같이 마시기 시작 → 앞사람이 멈춰야 멈춤|한 명 골라서 마시게 하기|내가 마시기|화장실 1번 다녀와도 OK|내가 춤 → 다음 사람이 따라 하고 하나 추가|모두 한 잔!|손 제일 늦게 든 사람 마시기|짝꿍 정하기 · 내가 마실 때 같이 마심|막히는 사람 마시기|주제 정하고 돌아가며 말하기, 막히면 마시기|새 규칙 하나 (어기면 마시기)|내 질문에 대답하면 마시기|가운데 잔에 조금 따르기"
    kcTap = "카드 더미를 터치!"
    kcLeft = { "${it}장 남음" }
    kcLastKing = "마지막 킹! 킹스컵 원샷!!"
    kcLastKingSub = "가운데 잔을 다 마시기 🍺"
    kcRules = "📋 규칙 보기"
    kcShuffle = "🔀 새로 섞기"
    kcEmpty = "카드를 다 썼어요!"
    beerTitle = "맥주 거품 채우기"
    beerSub = "넘치기 직전에 멈춰!"
    beerDesc = "각자 내 맥주잔을 지켜봐요. 맥주가 빨라졌다 느려졌다 하면서 차올라요!\n빨간 점선(여기까지!)에 맞춰서 터치해 멈추기.\n선에 제일 가깝게 멈춘 사람 1등, 넘치면 바로 꼴찌!"
    beerLine = "여기까지!"
    beerTap = "터치해서 멈춰!"
    beerOver = "넘쳤다! 💦"
    coinTitle = "동전 던지기"
    coinSub = "늑대 얼굴? 늑대 엉덩이?"
    coinDesc = "각자 내 자리에서 앞(🐺 늑대 얼굴)이나 뒤(🍑 늑대 엉덩이)를 골라요.\n모두 고르면 3초 뒤 동전이 빙글빙글~\n틀린 사람은 원샷! 가끔(15%) 동전이 세로로 서면 전원 틀림!"
    coinHead = "앞"
    coinTail = "뒤"
    coinPick = "골라!"
    coinRight = "⭕ 맞춤!"
    coinWrong = "❌ 틀림!"
    coinResHead = "🐺 앞 (얼굴)!"
    coinResTail = "🍑 뒤 (엉덩이)!"
    coinEdge = "😱 세로로 섰다! 전원 틀림"
    qrTitle = "내 연락처 QR"
    qrEdit = "✏️ 내 SNS 입력"
    qrPrivacy = "🔒 입력한 정보는 이 휴대폰에만 저장돼요. 어디에도 안 보내고, 인터넷 없어도 QR이 만들어져요."
    qrEmpty = "아직 입력한 SNS가 없어요"
    qrScan = "카메라로 찍으면 바로 연결 📷"
    qrSwipe = "← 옆으로 넘기면 다른 SNS →"
    qrClose = "✕ 닫기"
    qrSave = "저장"
    qrPhone = "전화번호 (국가번호 포함, 예: +82 10…)"
    qrId = "아이디"
    qrLink = "링크"
    tipBtn = "💁 팁"
    tipTitle = "💁 팁 계산"
    tipAmount = "금액"
    tipLabel = { "팁 ${it}%" }
    tipTotal = "팁 포함 합계"
    tipSplit = "🍻 팁 포함 N빵"
    feedback = "💬 피드백"
    langPickTwo = "최대 2개까지 고를 수 있어요. ① 메인 언어 · ② 보조 언어 (그 아래에 같이 나와요). 누른 걸 다시 누르면 빠져요."
    plinkoTitle = "플링코"
    plinkoSub = "늑대 코인을 떨어뜨려 봐!"
    plinkoDesc = "위쪽 아무 데나 터치하면 그 자리에서 늑대 코인이 떨어져요.\n핀 사이를 톡톡 튕기다 아래 칸에 들어가면 그 칸이 걸려요!\n칸 순서는 판마다 섞여요."
    plinkoTap = "위쪽 아무 데나 터치해서 떨어뜨리기"
    plinkoShuffling = "🔀 칸 섞는 중…"
    plinkoSlots = "1잔|반 잔|다시|오른쪽 사람|왼쪽 사람|다 같이|통과"
    tttTitle = "삼목"
    tttSub = "3×3 늑대 코인 · 3개까지만 · 한 수 2초!"
    tttDesc = "초록과 빨강이 번갈아 늑대 코인을 놓아요.\n가로·세로·대각선 3개를 먼저 이으면 승리!\n코인은 한 사람당 3개까지 — 4번째를 놓으면 내 가장 오래된 코인이 사라져요 (사라질 코인은 깜빡여요).\n한 수에 2초, 넘기면 차례가 넘어가요. 진 사람은 원샷!"
    green = "초록"
    red = "빨강"
    winsOf = { "${it} 승리!" }
    drawWord = "무승부!"
    timeUp = "⏰ 시간 초과! 차례 넘김"
    gomokuTitle = "오목"
    gomokuSub = "15×15 바둑판 · 33 금지"
    gomokuDesc = "흑과 백이 번갈아 돌을 놓아요 (흑이 먼저).\n가로·세로·대각선 5개를 먼저 이으면 승리!\n흑은 33 (열린 3이 두 줄 동시에 생기는 자리)에 둘 수 없어요. 진 사람은 원샷!"
    black = "흑"
    white = "백"
    banned33 = "🚫 33 금지!"
    hotelBtn = "🏨 숙소"
    hotelTitle = "숙소 주소 카드"
    hotelName = "숙소 이름"
    hotelAddr = "주소 (현지어로 복사해서 붙여넣기)"
    hotelPhone = "전화번호 (선택)"
    hotelNote = "메모 (선택, 예: 편의점 옆 골목)"
    hotelAdd = "➕ 숙소 추가"
    hotelDelete = "🗑️ 삭제"
    hotelEmpty = "아직 저장한 숙소가 없어요"
    hotelTakeMe = "여기로 가 주세요"
    hotelMap = "📍 지도 QR — 찍으면 바로 길 안내"
    hotelCall = "📞 전화하기"
    hotelEditBtn = "✏️ 숙소 수정"
    colorTurn = { "${it} 차례" }
    youAre = { "나: ${it}" }
    phraseBtn = "🗣️ 회화"
    settleBtn = "🧾 정산"
    nbTitle = "숫자 폭탄"
    nbSub = "숨은 폭탄 숫자를 피해라! UP · DOWN"
    nbDesc = "1부터 최대 숫자 사이에 폭탄 숫자가 하나 숨어 있어요.\n돌아가면서 숫자를 하나씩 눌러요. 폭탄보다 작으면 ⬆️ UP, 크면 ⬇️ DOWN — 범위가 점점 좁아져요.\n폭탄 숫자를 누른 사람이 💥 펑! 원샷!"
    nbMax = "최대 숫자"
    nbEnter = "숫자를 누르고 확인!"
    nbUp = "⬆️ UP! 더 큰 숫자"
    nbDown = "⬇️ DOWN! 더 작은 숫자"
    nbBoom = "💥 펑! 폭탄 숫자"
    nbOut = "범위 안의 숫자를 눌러요"
    nbPass = "다음 사람에게 넘겨요 👉"
    nbOk = "확인"
    blTitle = "풍선 불기"
    blSub = "언제 터질지 몰라! 펑!"
    blDesc = "폰을 돌리면서 풍선에 바람을 넣어요.\n한 사람이 1~3번 화면을 터치하고 다음 사람에게 넘겨요.\n풍선은 언제 터질지 몰라요. 터뜨린 사람이 💥 원샷!"
    blTap = "터치해서 바람 넣기 (1~3번)"
    blPumps = { "${it}번 불었어요" }
    blPop = "💥 펑! 터졌다!"
    phTitle = "회화 카드"
    phDesc = "보여줄 나라 말을 고르고, 문장을 누르면 크게 보여줘요. 인터넷 없어도 돼요."
    phTo = "보여줄 언어"
    phrases = "화장실 어디예요?|이거 주세요|계산해 주세요|얼마예요?|맵지 않게 해 주세요|고수는 빼 주세요|얼음은 빼 주세요|물 좀 주세요|맥주 하나 더 주세요|포장해 주세요|카드 돼요?|조금 깎아 주세요|영수증 주세요|사진 좀 찍어 주실래요?|와이파이 비밀번호가 뭐예요?|약국 어디예요?|도와주세요!|땅콩 알레르기가 있어요|건배! 원샷!|오늘 밤은 안 끝나!|와, 진짜 잘생기셨다/예쁘시다!|나 안 취했어! (취함)|사장님, 이건 서비스죠?|우리 친구 해요!|젠장!|헐, 대박!|아 씨…|이 바보야!|미쳤다 ㅋㅋ 개웃겨|안녕하세요|감사합니다|실례합니다|천천히 말해 주세요|이 주소로 가 주세요|공항으로 가 주세요|여기서 세워 주세요|얼마나 걸려요?|체크인할게요|짐 좀 맡아 주세요|수건 좀 더 주세요|에어컨이 안 돼요|네|아니요|괜찮아요|잘 모르겠어요|영어 할 줄 아세요?|여기에 적어 주세요|만나서 반가워요|좋아요!|안녕히 계세요, 또 봐요!|정말 죄송해요|제 잘못이에요|됐어요, 고마워요|필요 없어요|그냥 구경만 할게요|관심 없어요|다음에 할게요|그만하세요!|역은 어디예요?|이거 여기 가요?|표 한 장 주세요|미터기 켜 주세요|천천히 가 주세요|조금 서둘러 주세요|오른쪽이요|왼쪽이요|쭉 가 주세요|맛있어요!|메뉴판 주세요|뭐가 제일 맛있어요?|체크아웃할게요|체크아웃은 몇 시예요?|아침은 몇 시예요?|방 열쇠를 잃어버렸어요|뜨거운 물이 안 나와요|방 청소해 주세요|모닝콜 부탁해요|제일 센 술 주세요|안주는 뭐 있어요?|노래 한 곡 해요!|오늘은 내가 쏠게!|토할 것 같아…|이제 집에 갈래|입어 봐도 돼요?|더 큰 거 있어요?|현금만 돼요?|ATM 어디 있어요?|충전 좀 해도 돼요?|경찰을 불러 주세요|병원에 데려다 주세요|뭐래~|아 짜증나!|너 진짜 웃긴다|마셔, 이 겁쟁이야!|꺼져! (장난이야)"
    stTitle = "여행 정산"
    stDesc = "누가 얼마 냈는지만 넣으면, 마지막에 누가 누구한테 얼마 보내면 되는지 알려줘요. 이 휴대폰에만 저장돼요."
    stPeople = "같이 간 사람"
    stAddPerson = "➕ 사람"
    stAmount = "금액"
    stMemo = "메모 (예: 저녁, 택시)"
    stPaidBy = "낸 사람"
    stFor = "같이 쓴 사람"
    stAdd = "➕ 쓴 돈 추가"
    stList = "쓴 돈"
    stEmpty = "아직 쓴 돈이 없어요"
    stResult = "정산 결과"
    stEven = "보낼 돈 없음! 딱 맞아요 👍"
    stTotal = { "총 $it" }
    stSpent = { "쓴 몫 $it" }
    stReset = "🗑️ 전부 지우기"
    stResetAsk = "정말 전부 지울까요?"

    nbDazed = "헤롱헤롱~ 😵‍💫"
    phSpeak = "🔊 들려주기"
    phNoVoice = "이 휴대폰에 이 언어 음성이 없어요. 휴대폰 설정 → 음성(텍스트 음성 변환)에서 받을 수 있어요."
    phFun = "😆 재미있는 말"
    phSwear = "🤬 장난 욕 (친한 사이에만!)"
    phCats = "👋 인사|🚕 교통|🍜 식당|🏨 숙소|🍻 술집|🛍️ 쇼핑·도움|🤬 장난 욕"


    tsTitle = "탑 쌓기"
    tsSub = "딱 맞춰 쌓아라! 무너뜨리면 원샷"
    tsDesc = "좌우로 움직이는 블록을 터치해서 떨어뜨려요. 아래 블록 밖으로 삐져나간 부분은 잘려 나가요.\n한 번 누르면 폰을 다음 사람에게 넘겨요 👉\n탑을 무너뜨린 사람이 🍺 마셔요!"
    tsFloor = { "${it}층" }
    tsTap = "터치해서 떨어뜨리기 → 다음 사람 👉"
    tsFall = "💥 와르르! 무너졌다!"
    smTitle = "따라 하기"
    smSub = "반짝인 순서 그대로! 틀리면 마시기"
    smDesc = "색 버튼이 소리와 함께 순서대로 반짝여요. 잘 보고 똑같은 순서로 눌러요.\n성공하면 순서가 하나 늘어나고 폰을 다음 사람에게 넘겨요 👉\n틀린 사람이 🍺 마셔요! (소리 켜면 더 쉬워요 🔊)"
    smLevel = { "${it}단계" }
    smWatch = "👀 잘 봐요"
    smGo = "👆 따라 눌러요!"
    smOk = "⭕ 성공! 다음 사람 👉"
    smWrong = "❌ 틀렸다!"
    wcTitle = "폭탄 선 자르기"
    wcSub = "진짜 선을 자르면 펑! 운에 맡겨"
    wcDesc = "폭탄에 색깔 선이 여러 개 있어요. 그중 하나만 진짜예요.\n돌아가면서 선을 하나씩 터치해서 잘라요 ✂️ 자르고 나면 다음 사람에게 👉\n진짜 선을 자른 사람이 💥 원샷!"
    wcWires = "선 개수"
    wcTap = "✂️ 선을 하나 터치해서 잘라요"
    wcSafe = "😮‍💨 휴~ 살았다! 다음 사람 👉"
    wcBoom = "💥 펑! 진짜 선이었다!"
    wcLeft = { "남은 선 $it" }
    cuTitle = "늑대컬링"
    cuSub = "늑대 잔을 당겼다 놓아 끝에 딱!"
    cuDesc = "테이블 아래쪽의 늑대 맥주잔을 손가락으로 뒤로(아래로) 당겼다가 놓으면 앞으로 미끄러져요. 많이 당길수록 멀리 가요.\n테이블 끝에 가까울수록 좋아요. 끝을 넘으면 💦 떨어져요.\n돌아가며 한 번씩! 떨어진 사람은 모두 🍺 원샷, 아무도 안 떨어졌으면 제일 먼 사람이 마셔요."
    cuHint = "👇 뒤로 당겼다가 놓기"
    cuFall = "💦 떨어졌다!"
    stExport = "📤 내보내기"
    stExportDesc = "정산 기록을 파일로 저장하거나 동행들한테 바로 보내요."
    stCsv = "📊 엑셀 파일 (.csv)"
    stTxt = "📝 메모장 파일 (.txt)"
    stShare = "💬 공유하기 (카톡 등)"
    stSaved = "✅ 저장했어요"
    stMid = "💰 중간 정산"
    stMidDesc = "딱 떨어지게 반올림해서 받아요. 남은 차이는 다음 정산으로 넘어가요."
    stUnit = "반올림 단위"
    stMidDone = "✅ 받았어요 (정산 완료)"
    stMidTag = { "💰 중간 정산 #$it" }
    stResetSub = "지우면 되돌릴 수 없어요. 동행들한테 보낼 거면 먼저 저장해 두세요."
    stResetSave = "📤 먼저 저장하기"
    stResetGo = "🗑️ 그냥 지우기"
    stCols = "번호|날짜|메모|금액|낸 사람|같이 쓴 사람|보내는 사람|받는 사람|이름|낸 돈|쓴 몫|합계"
    szBtn = "📏 사이즈"
    szTitle = "사이즈 변환표"
    szDesc = "나라마다 옷·신발 사이즈가 달라요. 내 사이즈 줄을 누르면 ⭐ 저장돼서 다음에도 바로 보여요. (대략적인 기준이라 브랜드마다 조금 달라요)"
    szCats = "👟 신발 남|👠 신발 여|👕 상의 남|👚 상의 여|👖 바지 허리|💍 반지"
    upTitle = "🎉 새 버전이 나왔어요"
    upSub = { a, b -> "지금 $a → 새 버전 $b\n받고 나서 설치 버튼만 누르면 돼요. 저장한 설정은 그대로 남아요." }
    upGo = "⬇️ 업데이트"
    upLater = "나중에"
    upDownloading = "⬇️ 새 버전 받는 중…"
    upAllow = "처음 한 번만: 다음 화면에서 \"이 출처 허용\"을 켜고 돌아와 주세요."
    upFail = "받기 실패 — 인터넷을 확인하고 다시 해 주세요."
    upCheck = "🔄 업데이트 확인"
    upLatest = "✅ 최신 버전이에요"
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
    menuCardsSub = "Up/Down · Ho Chi Minh · King's Cup"
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
    menuLuckSub = "Ladder Game · Roulette · Hot Bomb · Coin Toss · Plinko · Number Bomb · Balloon Pump · Cut the Wire · Finger Pick"
    menuBattle = "Versus Games"
    menuBattleSub = "Tic-Tac-Toe · Connect 4 · Gomoku · Tap Turf War · Slap Duel"
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
    menuQuickSub = "Reaction Duel · Color Reaction · Stopwatch · Beer Pour · Tower Stack · Copy Me · Wolf Curling"
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
    tugTitle = "Tap Turf War"
    tugSub = "Mash to grab more turf!"
    tugDesc = "Two players sit facing each other, phone in the middle.\nOn GO!, mash your half of the screen like crazy!\nPush the flag (🎀) past the line on their side to win. Loser drinks!"
    tugMash = "MASH!! 👊"
    tugTaps = "taps"
    winWord = "👑 WIN!"
    shotWord = "💀 Drink!"
    readyWord = "Ready…"
    slapTitle = "Slap Duel"
    slapSub = "Red Hands with wolf paws!"
    slapDesc = "Two players sit facing each other.\n🛡️ Defender: keep a finger pressed on your half, and lift it to dodge when the attack comes!\n🐾 Attacker: tap your half any time to slap!\nHit = defender drinks. Dodge = swap roles. Lift without an attack = flinch, drink!"
    slapAtt = "🐾 Attack"
    slapDef = "🛡️ Defend"
    slapAttHint = "Tap any time to slap!"
    slapAttWait = "Wait for the defender to hold…"
    slapDefHint = "Hold, then lift to dodge!"
    slapDefHold = "Press and hold here!"
    slapHit = "SMACK!! Hit 🎯"
    slapOuch = "Ouch!! 😵 Drink"
    slapMiss = "Missed… swap roles 🔄"
    slapDodge = "Dodged! 🙌"
    slapFlinch = "Flinch! 🚫 Drink"
    slapFlinchAtt = "They flinched! 🤭"
    slapSmack = "SMACK!"
    kcTitle = "Global King's Cup"
    kcSub = "King's Cup with picture rules"
    kcDesc = "Tap the deck to draw a card. Each card shows what to do with a picture.\n👑 Every King: pour a little into the King's Cup. Whoever draws the 4th King drinks the whole cup!"
    kcNames = "Waterfall|You|Me|Toilet pass|Dance relay|Cheers all|Heaven|Mate|Word chain|Categories|Make a rule|Question master|King's Cup"
    kcDescs = "Everyone drinks → you can stop only after the person before you stops|Pick someone to drink|You drink|Go to the toilet once, no penalty|Do a move → next person copies it and adds one|Everyone drinks!|Last to raise a hand drinks|Pick a mate who drinks whenever you do|Whoever gets stuck drinks|Pick a topic, take turns naming things, stuck = drink|Make a new rule (break it = drink)|Anyone who answers your question drinks|Pour a little into the middle cup"
    kcTap = "Tap the deck!"
    kcLeft = { "$it left" }
    kcLastKing = "Last King! Drink the King's Cup!!"
    kcLastKingSub = "Drink the whole middle cup 🍺"
    kcRules = "📋 Rules"
    kcShuffle = "🔀 Reshuffle"
    kcEmpty = "Out of cards!"
    beerTitle = "Beer Pour"
    beerSub = "Stop right before it overflows!"
    beerDesc = "Watch your own glass. The beer speeds up and slows down as it fills!\nTap to stop it at the red dashed line.\nClosest to the line wins. Overflow = last place!"
    beerLine = "Stop here!"
    beerTap = "Tap to stop!"
    beerOver = "Overflow! 💦"
    coinTitle = "Coin Toss"
    coinSub = "Wolf face or wolf butt?"
    coinDesc = "Everyone picks heads (🐺 wolf face) or tails (🍑 wolf butt) in their own spot.\nWhen all have picked, the coin spins after 3 seconds!\nWrong = drink! Sometimes (15%) the coin lands on its edge — everyone is wrong!"
    coinHead = "Heads"
    coinTail = "Tails"
    coinPick = "Pick!"
    coinRight = "⭕ Right!"
    coinWrong = "❌ Wrong!"
    coinResHead = "🐺 Heads (face)!"
    coinResTail = "🍑 Tails (butt)!"
    coinEdge = "😱 It landed on its edge! Everyone loses"
    qrTitle = "My Contact QR"
    qrEdit = "✏️ Edit my SNS"
    qrPrivacy = "🔒 What you enter is saved only on this phone. Nothing is sent anywhere, and QR codes work offline."
    qrEmpty = "No SNS added yet"
    qrScan = "Scan with a camera to connect 📷"
    qrSwipe = "← Swipe for other SNS →"
    qrClose = "✕ Close"
    qrSave = "Save"
    qrPhone = "Phone with country code (e.g. +1 555…)"
    qrId = "ID / username"
    qrLink = "Link"
    tipBtn = "💁 Tip"
    tipTitle = "💁 Tip Calculator"
    tipAmount = "Amount"
    tipLabel = { "Tip $it%" }
    tipTotal = "Total with tip"
    tipSplit = "🍻 Split with tip"
    feedback = "💬 Feedback"
    langPickTwo = "Pick up to 2. ① main language · ② second language (shown underneath). Tap again to remove."
    plinkoTitle = "Plinko"
    plinkoSub = "Drop the wolf coin!"
    plinkoDesc = "Tap anywhere at the top to drop a wolf coin from there.\nIt bounces between the pins — whatever slot it lands in is yours!\nThe slots are shuffled every round."
    plinkoTap = "Tap anywhere at the top to drop"
    plinkoShuffling = "🔀 Shuffling slots…"
    plinkoSlots = "1 drink|Half|Again|Right person|Left person|Everyone|Pass"
    tttTitle = "Tic-Tac-Toe"
    tttSub = "3×3 wolf coins · only 3 each · 2 s a move!"
    tttDesc = "Green and red take turns placing wolf coins.\nThree in a row (across, down or diagonal) wins!\nEach player can have only 3 coins — placing a 4th removes your oldest one (it blinks before it goes).\n2 seconds per move — run out and your turn passes. Loser drinks!"
    green = "Green"
    red = "Red"
    winsOf = { "$it wins!" }
    drawWord = "Draw!"
    timeUp = "⏰ Time's up! Turn passes"
    gomokuTitle = "Gomoku"
    gomokuSub = "15×15 board · no double-three"
    gomokuDesc = "Black and white take turns placing stones (black first).\nFive in a row (across, down or diagonal) wins!\nBlack may not play a double-three (two open threes at once). Loser drinks!"
    black = "Black"
    white = "White"
    banned33 = "🚫 Double-three not allowed!"
    hotelBtn = "🏨 Hotel"
    hotelTitle = "Hotel Address Card"
    hotelName = "Hotel name"
    hotelAddr = "Address (paste it in the local language)"
    hotelPhone = "Phone (optional)"
    hotelNote = "Note (optional, e.g. alley next to the convenience store)"
    hotelAdd = "➕ Add hotel"
    hotelDelete = "🗑️ Delete"
    hotelEmpty = "No hotel saved yet"
    hotelTakeMe = "Please take me here"
    hotelMap = "📍 Map QR — scan for directions"
    hotelCall = "📞 Call"
    hotelEditBtn = "✏️ Edit hotels"
    colorTurn = { "$it's turn" }
    youAre = { "You: $it" }
    phraseBtn = "🗣️ Phrases"
    settleBtn = "🧾 Split bill"
    nbTitle = "Number Bomb"
    nbSub = "Dodge the hidden bomb number! UP · DOWN"
    nbDesc = "A bomb number is hidden between 1 and the max.\nTake turns entering a number. Lower than the bomb → ⬆️ UP, higher → ⬇️ DOWN — the range keeps shrinking.\nWhoever hits the bomb number goes 💥 BOOM — drink!"
    nbMax = "Max number"
    nbEnter = "Enter a number and tap OK!"
    nbUp = "⬆️ UP! Higher"
    nbDown = "⬇️ DOWN! Lower"
    nbBoom = "💥 BOOM! The bomb number"
    nbOut = "Pick a number inside the range"
    nbPass = "Pass it to the next person 👉"
    nbOk = "OK"
    blTitle = "Balloon Pump"
    blSub = "Who knows when it pops! BANG!"
    blDesc = "Pass the phone around and pump up the balloon.\nEach person taps 1–3 times, then passes it on.\nNobody knows when it pops. Whoever pops it — 💥 drink!"
    blTap = "Tap to pump (1–3 times)"
    blPumps = { "$it pumps" }
    blPop = "💥 BANG! It popped!"
    phTitle = "Phrase Cards"
    phDesc = "Pick the language to show, then tap a phrase to show it big. Works offline."
    phTo = "Show in"
    phrases = "Where is the restroom?|This one, please|Check, please|How much is it?|Not spicy, please|No cilantro, please|No ice, please|Some water, please|One more beer, please|To go, please|Can I pay by card?|Can you give me a discount?|Receipt, please|Could you take a photo of us?|What is the Wi-Fi password?|Where is the pharmacy?|Help!|I am allergic to peanuts|Cheers! Bottoms up!|The night is still young!|Wow, you're so good-looking!|I'm not drunk! (I'm drunk)|This one's on the house, right?|Let's be friends!|Damn it!|Holy crap!|Oh shit…|You idiot!|LOL, that's insane!|Hello|Thank you|Excuse me|Please speak slowly|Please take me to this address|To the airport, please|Please stop here|How long does it take?|I'd like to check in|Can you keep my luggage?|More towels, please|The air conditioner isn't working|Yes|No|It's okay|I don't understand|Do you speak English?|Please write it down here|Nice to meet you|Sounds good!|Goodbye, see you!|I'm really sorry|It's my fault|No, thank you|I don't need it|I'm just looking|I'm not interested|Maybe next time|Please stop!|Where is the station?|Does this go here?|One ticket, please|Please turn on the meter|Please drive slowly|Please hurry a little|Turn right, please|Turn left, please|Go straight, please|Delicious!|Menu, please|What do you recommend?|I'd like to check out|What time is check-out?|What time is breakfast?|I lost my room key|There's no hot water|Please clean my room|Wake-up call, please|Your strongest drink, please|What snacks do you have?|Let's sing a song!|Tonight's on me!|I think I'm gonna throw up…|I want to go home now|Can I try this on?|Do you have a bigger size?|Cash only?|Where is an ATM?|Can I charge my phone?|Please call the police|Please take me to a hospital|Whatever~|Ugh, so annoying!|You're such a clown|Drink, you chicken!|Get lost! (just kidding)"
    stTitle = "Trip Split"
    stDesc = "Just enter who paid what — it tells you who should send how much to whom. Saved only on this phone."
    stPeople = "Travelers"
    stAddPerson = "➕ Person"
    stAmount = "Amount"
    stMemo = "Note (e.g. dinner, taxi)"
    stPaidBy = "Paid by"
    stFor = "Split between"
    stAdd = "➕ Add expense"
    stList = "Expenses"
    stEmpty = "No expenses yet"
    stResult = "Who pays whom"
    stEven = "All square — nothing to send 👍"
    stTotal = { "Total $it" }
    stSpent = { "Share $it" }
    stReset = "🗑️ Clear all"
    stResetAsk = "Really clear everything?"

    nbDazed = "Dizzy~ 😵‍💫"
    phSpeak = "🔊 Play sound"
    phNoVoice = "This phone has no voice for this language. You can download one in phone Settings → Text-to-speech."
    phFun = "😆 Fun phrases"
    phSwear = "🤬 Playful swears (close friends only!)"
    phCats = "👋 Hello|🚕 Getting around|🍜 Restaurant|🏨 Hotel|🍻 Bar|🛍️ Shopping & help|🤬 Swears"


    tsTitle = "Tower Stack"
    tsSub = "Stack it right! Topple it, drink it"
    tsDesc = "Tap to drop the sliding block. Whatever hangs over the block below gets cut off.\nAfter one tap, pass the phone to the next person 👉\nWhoever topples the tower — 🍺 drink!"
    tsFloor = { "Floor $it" }
    tsTap = "Tap to drop → next person 👉"
    tsFall = "💥 CRASH! It fell!"
    smTitle = "Copy Me"
    smSub = "Repeat the flashes! Miss and drink"
    smDesc = "The color buttons flash in order with a sound. Watch, then press them in the same order.\nGet it right and the sequence grows by one — pass the phone on 👉\nWhoever misses — 🍺 drink! (Turn sound on to make it easier 🔊)"
    smLevel = { "Level $it" }
    smWatch = "👀 Watch"
    smGo = "👆 Your turn!"
    smOk = "⭕ Nice! Next person 👉"
    smWrong = "❌ Wrong!"
    wcTitle = "Cut the Wire"
    wcSub = "Cut the wrong one — BOOM! Trust your luck"
    wcDesc = "The bomb has several colored wires. Only one of them is real.\nTake turns tapping a wire to cut it ✂️ then pass to the next person 👉\nWhoever cuts the real one — 💥 bottoms up!"
    wcWires = "Wires"
    wcTap = "✂️ Tap a wire to cut it"
    wcSafe = "😮‍💨 Phew~ safe! Next person 👉"
    wcBoom = "💥 BOOM! That was the one!"
    wcLeft = { "$it wires left" }
    cuTitle = "Wolf Curling"
    cuSub = "Pull back the wolf mug, let go, stop at the edge!"
    cuDesc = "Pull the wolf mug at the bottom of the table back (down) with your finger and let go — it slides forward. The more you pull, the farther it goes.\nThe closer to the edge, the better. Go past it and it 💦 falls off.\nOne turn each! Everyone who falls off drinks 🍺, and if nobody does, the farthest one drinks."
    cuHint = "👇 Pull back, then let go"
    cuFall = "💦 It fell off!"
    stExport = "📤 Export"
    stExportDesc = "Save the record as a file or send it straight to your travel buddies."
    stCsv = "📊 Excel file (.csv)"
    stTxt = "📝 Text file (.txt)"
    stShare = "💬 Share (messengers etc.)"
    stSaved = "✅ Saved"
    stMid = "💰 Settle now"
    stMidDesc = "Collect rounded amounts now — the leftover difference carries over to the next settle-up."
    stUnit = "Round to"
    stMidDone = "✅ Paid (done)"
    stMidTag = { "💰 Settle-up #$it" }
    stResetSub = "This can't be undone. Save it first if you want to send it to your buddies."
    stResetSave = "📤 Save first"
    stResetGo = "🗑️ Just clear"
    stCols = "No.|Date|Note|Amount|Paid by|Split between|From|To|Name|Paid|Share|Total"
    szBtn = "📏 Sizes"
    szTitle = "Size Chart"
    szDesc = "Clothing and shoe sizes differ by country. Tap your row to ⭐ save it for next time. (Rough guide — brands vary a little.)"
    szCats = "👟 Men's shoes|👠 Women's shoes|👕 Men's tops|👚 Women's tops|👖 Waist|💍 Rings"
    upTitle = "🎉 A new version is out"
    upSub = { a, b -> "Now $a → new $b\nAfter it downloads, just tap Install. Your saved settings stay." }
    upGo = "⬇️ Update"
    upLater = "Later"
    upDownloading = "⬇️ Downloading the new version…"
    upAllow = "First time only: turn on \"Allow from this source\" on the next screen, then come back."
    upFail = "Download failed — check your internet and try again."
    upCheck = "🔄 Check for updates"
    upLatest = "✅ You're up to date"
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
    menuCardsSub = "Up/Down · ホーチミン · キングスカップ"
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
    menuLuckSub = "あみだくじ · ルーレット · 爆弾回し · コイントス · プリンコ · 数字爆弾 · 風船ふくらまし · 爆弾の線切り · 指えらび"
    menuBattle = "対決ゲーム"
    menuBattleSub = "三目並べ · 4目並べ · 五目並べ · 連打陣取り · スラップ対決"
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
    menuQuickSub = "反応速度対決 · 色反応対決 · ストップウォッチ · ビール注ぎ · タワー積み · まねっこ · オオカミカーリング"
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
    tugTitle = "連打陣取り"
    tugSub = "連打で陣地を広げろ！"
    tugDesc = "2人でスマホをはさんで上下に向かい合って座ります。\nGO!で自分側の画面をひたすら連打！\n真ん中の旗(🎀)を相手側の線まで押し込んだら勝ち、負けた人は一気！"
    tugMash = "連打!! 👊"
    tugTaps = "タップ"
    winWord = "👑 勝ち!"
    shotWord = "💀 一気!"
    readyWord = "準備…"
    slapTitle = "スラップ対決"
    slapSub = "狼の前足で手の甲たたき！"
    slapDesc = "2人で上下に向かい合って座ります。\n🛡️ 守り：自分側の画面を指で押さえておき、攻撃が来たら離してよける！\n🐾 攻め：いつでも自分側をタップするとバチン！\n当たったら守りが一気、よけたら攻守交代。攻撃が来てないのに離したらビクッ反則で一気！"
    slapAtt = "🐾 攻め"
    slapDef = "🛡️ 守り"
    slapAttHint = "いつでもタップでバチン！"
    slapAttWait = "守りが押さえるまで待って…"
    slapDefHint = "押さえておいて、離してよける！"
    slapDefHold = "ここを押さえて！"
    slapHit = "バチン!! 命中 🎯"
    slapOuch = "いたっ!! 😵 一気"
    slapMiss = "空振り… 攻守交代 🔄"
    slapDodge = "よけた! 🙌"
    slapFlinch = "ビクッ反則! 🚫 一気"
    slapFlinchAtt = "相手がビクッ! 🤭"
    slapSmack = "バチン!"
    kcTitle = "グローバル・キングスカップ"
    kcSub = "絵で分かるキングスカップ"
    kcDesc = "カードの山をタップして1枚ずつ引きます。カードごとにやることが絵で出ます。\n👑 Kが出るたびに真ん中のキングスカップに少しずつ注ぎ、4枚目のKを引いた人がキングスカップを一気！"
    kcNames = "滝|指名|自分|トイレ免除券|ダンスリレー|みんなで乾杯|天|相棒|しりとり|カテゴリー|ルール作り|質問マスター|キングスカップ"
    kcDescs = "みんなで飲み始め → 前の人が止まるまで止まれない|1人選んで飲ませる|自分が飲む|トイレに1回行ってもOK|自分が踊る → 次の人がまねして1つ追加|みんなで1杯！|最後に手を挙げた人が飲む|相棒を決める · 自分が飲むとき一緒に飲む|詰まった人が飲む|お題を決めて順番に言う、詰まったら飲む|新しいルールを1つ（破ったら飲む）|自分の質問に答えた人が飲む|真ん中の杯に少し注ぐ"
    kcTap = "山札をタップ！"
    kcLeft = { "残り${it}枚" }
    kcLastKing = "最後のK！キングスカップ一気!!"
    kcLastKingSub = "真ん中の杯を飲み干す 🍺"
    kcRules = "📋 ルール"
    kcShuffle = "🔀 シャッフル"
    kcEmpty = "カードがなくなりました！"
    beerTitle = "ビール注ぎ"
    beerSub = "あふれる直前で止めて！"
    beerDesc = "それぞれ自分のジョッキを見ていてね。ビールが速くなったり遅くなったりしながら注がれる！\n赤い点線（ここまで！）に合わせてタップで止める。\n線に一番近い人が1位、あふれたら即ビリ！"
    beerLine = "ここまで！"
    beerTap = "タップで止めて！"
    beerOver = "あふれた! 💦"
    coinTitle = "コイントス"
    coinSub = "オオカミの顔？おしり？"
    coinDesc = "それぞれ自分の場所で表（🐺 オオカミの顔）か裏（🍑 オオカミのおしり）を選びます。\nみんな選んだら3秒後にコインがくるくる〜\nはずれた人は一気！ときどき（15%）コインが立つと全員はずれ！"
    coinHead = "表"
    coinTail = "裏"
    coinPick = "選んで！"
    coinRight = "⭕ 当たり!"
    coinWrong = "❌ はずれ!"
    coinResHead = "🐺 表（顔）!"
    coinResTail = "🍑 裏（おしり）!"
    coinEdge = "😱 コインが立った！全員はずれ"
    qrTitle = "連絡先QR"
    qrEdit = "✏️ SNSを入力"
    qrPrivacy = "🔒 入力した情報はこのスマホにだけ保存されます。どこにも送らず、ネットなしでもQRが作れます。"
    qrEmpty = "まだSNSが入力されていません"
    qrScan = "カメラで読み取るとすぐつながる 📷"
    qrSwipe = "← 横にスワイプで他のSNS →"
    qrClose = "✕ 閉じる"
    qrSave = "保存"
    qrPhone = "電話番号（国番号付き、例: +81 90…）"
    qrId = "ID"
    qrLink = "リンク"
    tipBtn = "💁 チップ"
    tipTitle = "💁 チップ計算"
    tipAmount = "金額"
    tipLabel = { "チップ $it%" }
    tipTotal = "チップ込み合計"
    tipSplit = "🍻 チップ込みで割り勘"
    feedback = "💬 フィードバック"
    langPickTwo = "最大2つまで選べます。① メイン言語 · ② サブ言語（下に一緒に表示）。もう一度押すと外れます。"
    plinkoTitle = "プリンコ"
    plinkoSub = "オオカミコインを落とそう！"
    plinkoDesc = "上のどこかをタップすると、そこからオオカミコインが落ちます。\nピンの間を跳ねて、入ったマスが当たり！\nマスの順番は毎回シャッフルされます。"
    plinkoTap = "上のどこかをタップして落とす"
    plinkoShuffling = "🔀 シャッフル中…"
    plinkoSlots = "1杯|半分|もう一回|右の人|左の人|みんなで|セーフ"
    tttTitle = "三目並べ"
    tttSub = "3×3 オオカミコイン · 3個まで · 1手2秒！"
    tttDesc = "緑と赤が交互にオオカミコインを置きます。\n縦・横・斜めに3つ先にそろえたら勝ち！\nコインは1人3個まで — 4個目を置くと自分の一番古いコインが消えます（消えるコインは点滅）。\n1手2秒、過ぎると相手の番に。負けたら一気！"
    green = "緑"
    red = "赤"
    winsOf = { "${it}の勝ち！" }
    drawWord = "引き分け！"
    timeUp = "⏰ 時間切れ！相手の番"
    gomokuTitle = "五目並べ"
    gomokuSub = "15×15 · 三三禁止"
    gomokuDesc = "黒と白が交互に石を置きます（黒が先）。\n縦・横・斜めに5つ先にそろえたら勝ち！\n黒は三三（開いた三が同時に2つ）に置けません。負けたら一気！"
    black = "黒"
    white = "白"
    banned33 = "🚫 三三禁止！"
    hotelBtn = "🏨 宿"
    hotelTitle = "宿の住所カード"
    hotelName = "宿の名前"
    hotelAddr = "住所（現地の言葉でコピペ）"
    hotelPhone = "電話番号（任意）"
    hotelNote = "メモ（任意、例：コンビニ横の路地）"
    hotelAdd = "➕ 宿を追加"
    hotelDelete = "🗑️ 削除"
    hotelEmpty = "まだ宿が登録されていません"
    hotelTakeMe = "ここへ行ってください"
    hotelMap = "📍 地図QR — 読み取るとすぐ道案内"
    hotelCall = "📞 電話する"
    hotelEditBtn = "✏️ 宿を編集"
    colorTurn = { "${it}の番" }
    youAre = { "あなた: ${it}" }
    phraseBtn = "🗣️ 会話"
    settleBtn = "🧾 精算"
    nbTitle = "数字爆弾"
    nbSub = "隠れた爆弾の数字をよけろ！UP・DOWN"
    nbDesc = "1から最大の数字の間に爆弾の数字が1つ隠れています。\n順番に数字を1つずつ入力。爆弾より小さければ⬆️ UP、大きければ⬇️ DOWN — 範囲がどんどん狭くなります。\n爆弾の数字を押した人は💥ドカン！一気！"
    nbMax = "最大の数字"
    nbEnter = "数字を入れてOK！"
    nbUp = "⬆️ UP！もっと大きい"
    nbDown = "⬇️ DOWN！もっと小さい"
    nbBoom = "💥 ドカン！爆弾の数字"
    nbOut = "範囲内の数字を入れてね"
    nbPass = "次の人に渡して 👉"
    nbOk = "OK"
    blTitle = "風船ふくらまし"
    blSub = "いつ割れるかわからない！パン！"
    blDesc = "スマホを回しながら風船に空気を入れます。\n1人1〜3回タッチして次の人に渡します。\n風船はいつ割れるかわかりません。割った人は💥一気！"
    blTap = "タッチで空気を入れる（1〜3回）"
    blPumps = { "${it}回ふくらませた" }
    blPop = "💥 パン！割れた！"
    phTitle = "会話カード"
    phDesc = "見せる言語を選んで、文をタップすると大きく表示します。ネットなしでOK。"
    phTo = "見せる言語"
    phrases = "トイレはどこですか？|これをください|お会計お願いします|いくらですか？|辛くしないでください|パクチー抜きでお願いします|氷なしでお願いします|お水をください|ビールもう一杯ください|持ち帰りでお願いします|カードは使えますか？|少し安くしてください|領収書をください|写真を撮ってもらえますか？|Wi-Fiのパスワードは何ですか？|薬局はどこですか？|助けてください！|ピーナッツアレルギーがあります|乾杯！一気！|夜はまだこれから！|わあ、めっちゃかっこいい／かわいい！|酔ってないよ！（酔ってる）|これ、サービスですよね？|友だちになろう！|くそっ！|マジかよ！|ちくしょう…|このバカ！|ヤバい、ウケる！|こんにちは|ありがとうございます|すみません|ゆっくり話してください|この住所までお願いします|空港までお願いします|ここで止めてください|どのくらいかかりますか？|チェックインお願いします|荷物を預かってもらえますか？|タオルをもう少しください|エアコンが動きません|はい|いいえ|大丈夫です|よくわかりません|英語は話せますか？|ここに書いてください|はじめまして|いいですね！|さようなら、またね！|本当にすみません|私のせいです|結構です、ありがとう|いりません|見ているだけです|興味ありません|また今度にします|やめてください！|駅はどこですか？|これはここに行きますか？|切符を1枚ください|メーターを使ってください|ゆっくり走ってください|少し急いでください|右に曲がってください|左に曲がってください|まっすぐ行ってください|おいしいです！|メニューをください|おすすめは何ですか？|チェックアウトします|チェックアウトは何時ですか？|朝食は何時ですか？|部屋の鍵をなくしました|お湯が出ません|部屋を掃除してください|モーニングコールをお願いします|一番強いお酒をください|おつまみは何がありますか？|一曲歌おう！|今日は僕のおごり！|吐きそう…|もう帰りたい|試着してもいいですか？|もっと大きいサイズはありますか？|現金だけですか？|ATMはどこですか？|充電してもいいですか？|警察を呼んでください|病院に連れて行ってください|何言ってんの〜|あー、ムカつく！|お前ほんとウケる|飲めよ、この弱虫！|消えろ！（冗談だよ）"
    stTitle = "旅行の精算"
    stDesc = "誰がいくら払ったかを入れるだけで、最後に誰が誰にいくら送ればいいか教えてくれます。このスマホだけに保存。"
    stPeople = "一緒に行く人"
    stAddPerson = "➕ 人"
    stAmount = "金額"
    stMemo = "メモ（例：夕食、タクシー）"
    stPaidBy = "払った人"
    stFor = "一緒に使った人"
    stAdd = "➕ 支出を追加"
    stList = "支出"
    stEmpty = "まだ支出がありません"
    stResult = "精算結果"
    stEven = "送金なし！ぴったり 👍"
    stTotal = { "合計 $it" }
    stSpent = { "使った分 $it" }
    stReset = "🗑️ 全部消す"
    stResetAsk = "本当に全部消しますか？"

    nbDazed = "フラフラ〜 😵‍💫"
    phSpeak = "🔊 読み上げる"
    phNoVoice = "このスマホにはこの言語の音声がありません。スマホの設定 → テキスト読み上げで追加できます。"
    phFun = "😆 おもしろフレーズ"
    phSwear = "🤬 ふざけた悪口（仲良しだけ！）"
    phCats = "👋 あいさつ|🚕 交通|🍜 食堂|🏨 宿|🍻 飲み屋|🛍️ 買い物・助け|🤬 悪口"


    tsTitle = "タワー積み"
    tsSub = "ぴったり積め！崩したら一気"
    tsDesc = "左右に動くブロックをタッチして落とします。下のブロックからはみ出た部分は切れて落ちます。\n1回押したら次の人にスマホを渡して 👉\nタワーを崩した人が🍺飲む！"
    tsFloor = { "${it}階" }
    tsTap = "タッチで落とす → 次の人 👉"
    tsFall = "💥 ガラガラ！崩れた！"
    smTitle = "まねっこ"
    smSub = "光った順にそのまま！間違えたら飲む"
    smDesc = "色ボタンが音と一緒に順番に光ります。よく見て同じ順番で押そう。\n成功すると1つ増えて、次の人にスマホを渡して 👉\n間違えた人が🍺飲む！（音をオンにすると簡単 🔊）"
    smLevel = { "レベル $it" }
    smWatch = "👀 よく見て"
    smGo = "👆 まねして押して！"
    smOk = "⭕ 成功！次の人 👉"
    smWrong = "❌ 間違い！"
    wcTitle = "爆弾の線切り"
    wcSub = "本物の線を切ったらドカン！運まかせ"
    wcDesc = "爆弾に色の線がいくつかあります。本物は1本だけ。\n順番に線を1本タッチして切ります ✂️ 切ったら次の人へ 👉\n本物の線を切った人が💥一気！"
    wcWires = "線の数"
    wcTap = "✂️ 線を1本タッチして切る"
    wcSafe = "😮‍💨 ふぅ〜セーフ！次の人 👉"
    wcBoom = "💥 ドカン！本物だった！"
    wcLeft = { "残り $it 本" }
    cuTitle = "オオカミカーリング"
    cuSub = "オオカミジョッキを引いて放して端ギリギリに！"
    cuDesc = "テーブル下のオオカミジョッキを指で後ろ（下）に引いて放すと前に滑ります。たくさん引くほど遠くへ。\nテーブルの端に近いほど良い。端を越えると💦落ちます。\n1人1回ずつ！落ちた人は全員🍺一気、誰も落ちなければ一番遠い人が飲む。"
    cuHint = "👇 後ろに引いて放す"
    cuFall = "💦 落ちた！"
    stExport = "📤 書き出し"
    stExportDesc = "精算の記録をファイルに保存したり、仲間にそのまま送れます。"
    stCsv = "📊 Excelファイル (.csv)"
    stTxt = "📝 メモ帳ファイル (.txt)"
    stShare = "💬 共有（LINEなど）"
    stSaved = "✅ 保存しました"
    stMid = "💰 途中精算"
    stMidDesc = "きりのいい金額に丸めて受け取ります。残った差額は次の精算に回ります。"
    stUnit = "丸める単位"
    stMidDone = "✅ 受け取った（精算完了）"
    stMidTag = { "💰 途中精算 #$it" }
    stResetSub = "消すと元に戻せません。仲間に送るなら先に保存しておきましょう。"
    stResetSave = "📤 先に保存"
    stResetGo = "🗑️ そのまま消す"
    stCols = "番号|日付|メモ|金額|払った人|一緒に使った人|送る人|受け取る人|名前|払った額|使った分|合計"
    szBtn = "📏 サイズ"
    szTitle = "サイズ換算表"
    szDesc = "国によって服・靴のサイズが違います。自分のサイズの行をタッチすると⭐保存されます。（目安なのでブランドで少し違います）"
    szCats = "👟 靴 男|👠 靴 女|👕 トップス 男|👚 トップス 女|👖 ウエスト|💍 指輪"
    upTitle = "🎉 新しいバージョンが出ました"
    upSub = { a, b -> "今 $a → 新しい $b\nダウンロード後、インストールを押すだけ。保存した設定はそのまま。" }
    upGo = "⬇️ アップデート"
    upLater = "あとで"
    upDownloading = "⬇️ 新しいバージョンをダウンロード中…"
    upAllow = "初回だけ：次の画面で「この提供元を許可」をオンにして戻ってください。"
    upFail = "ダウンロード失敗 — ネットを確認してもう一度。"
    upCheck = "🔄 アップデート確認"
    upLatest = "✅ 最新バージョンです"
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
    menuCardsSub = "Up/Down · 胡志明 · 国王杯"
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
    menuLuckSub = "爬梯子 · 转盘 · 传炸弹 · 抛硬币 · 弹珠台 · 数字炸弹 · 吹气球 · 剪炸弹线 · 手指选人"
    menuBattle = "对战游戏"
    menuBattleSub = "井字棋 · 四子棋 · 五子棋 · 狂点抢地盘 · 拍手背对决"
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
    menuQuickSub = "反应速度对决 · 颜色反应对决 · 秒表 · 倒啤酒 · 叠高塔 · 跟着按 · 狼冰壶"
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
    tugTitle = "狂点抢地盘"
    tugSub = "狂点扩大地盘！"
    tugDesc = "两个人隔着手机上下面对面坐。\n喊 GO! 后疯狂点自己这一半屏幕！\n把中间的旗(🎀)推过对方那条线就赢，输的人干杯！"
    tugMash = "狂点!! 👊"
    tugTaps = "次"
    winWord = "👑 胜利!"
    shotWord = "💀 干杯!"
    readyWord = "准备…"
    slapTitle = "拍手背对决"
    slapSub = "用狼爪拍手背！"
    slapDesc = "两个人上下面对面坐。\n🛡️ 防守：手指一直按住自己这一半，攻击来了就松开躲开！\n🐾 进攻：随时点自己这一半就拍下去！\n拍中防守干杯，躲开就攻守互换。没攻击就松手算抖了犯规，干杯！"
    slapAtt = "🐾 进攻"
    slapDef = "🛡️ 防守"
    slapAttHint = "随时点一下就拍！"
    slapAttWait = "等防守按住…"
    slapDefHint = "按住，松开就能躲！"
    slapDefHold = "按住这里！"
    slapHit = "啪!! 拍中 🎯"
    slapOuch = "好痛!! 😵 干杯"
    slapMiss = "扑空… 攻守互换 🔄"
    slapDodge = "躲开了! 🙌"
    slapFlinch = "抖了犯规! 🚫 干杯"
    slapFlinchAtt = "对方抖了! 🤭"
    slapSmack = "啪!"
    kcTitle = "全球国王杯"
    kcSub = "看图就懂的国王杯"
    kcDesc = "点牌堆一张一张抽。每张牌都会用图告诉你要做什么。\n👑 每抽到 K 就往中间的国王杯里倒一点，抽到第 4 张 K 的人把国王杯干了！"
    kcNames = "瀑布|点名|自己|厕所通行证|舞蹈接力|一起干杯|举手|搭档|成语接龙|类别|定规则|提问大师|国王杯"
    kcDescs = "大家一起开始喝 → 前面的人停了你才能停|选一个人喝|自己喝|可以去一次厕所|我跳一个动作 → 下一个人模仿再加一个|大家一起喝一杯！|最后举手的人喝|选一个搭档 · 我喝时他也喝|卡住的人喝|定一个主题轮流说，卡住就喝|定一条新规则（违反就喝）|回答我问题的人喝|往中间的杯子里倒一点"
    kcTap = "点牌堆！"
    kcLeft = { "剩 $it 张" }
    kcLastKing = "最后一张K！国王杯干了!!"
    kcLastKingSub = "把中间的杯子喝光 🍺"
    kcRules = "📋 规则"
    kcShuffle = "🔀 重新洗牌"
    kcEmpty = "牌抽完了！"
    beerTitle = "倒啤酒"
    beerSub = "快溢出来前停下！"
    beerDesc = "盯好自己的啤酒杯，啤酒忽快忽慢地往上涨！\n对准红色虚线（到这里！）点一下停下。\n最接近线的人第一，溢出来直接垫底！"
    beerLine = "到这里！"
    beerTap = "点一下停下！"
    beerOver = "溢出来了! 💦"
    coinTitle = "抛硬币"
    coinSub = "狼脸？还是狼屁股？"
    coinDesc = "每个人在自己的位置选正面（🐺 狼脸）或反面（🍑 狼屁股）。\n都选好后 3 秒硬币开始旋转~\n猜错的人干杯！有时（15%）硬币会立起来，全员猜错！"
    coinHead = "正"
    coinTail = "反"
    coinPick = "选一个！"
    coinRight = "⭕ 猜对!"
    coinWrong = "❌ 猜错!"
    coinResHead = "🐺 正面（脸）!"
    coinResTail = "🍑 反面（屁股）!"
    coinEdge = "😱 硬币立起来了！全员猜错"
    qrTitle = "我的联系方式二维码"
    qrEdit = "✏️ 编辑我的社交账号"
    qrPrivacy = "🔒 输入的信息只保存在这部手机上，不会发送到任何地方，没网也能生成二维码。"
    qrEmpty = "还没有添加社交账号"
    qrScan = "用相机一扫就能加 📷"
    qrSwipe = "← 左右滑动切换 →"
    qrClose = "✕ 关闭"
    qrSave = "保存"
    qrPhone = "手机号（含国家代码，如 +86 138…）"
    qrId = "账号"
    qrLink = "链接"
    tipBtn = "💁 小费"
    tipTitle = "💁 小费计算"
    tipAmount = "金额"
    tipLabel = { "小费 $it%" }
    tipTotal = "含小费合计"
    tipSplit = "🍻 含小费AA"
    feedback = "💬 反馈"
    langPickTwo = "最多可选2种。① 主语言 · ② 第二语言（显示在下面）。再点一次取消。"
    plinkoTitle = "弹珠台"
    plinkoSub = "投下狼币！"
    plinkoDesc = "点上方任意位置，狼币就从那里落下。\n在钉子间弹来弹去，落进哪个格子就是哪个！\n每一局格子顺序都会打乱。"
    plinkoTap = "点上方任意位置投下"
    plinkoShuffling = "🔀 正在洗牌…"
    plinkoSlots = "1杯|半杯|再来|右边的人|左边的人|大家一起|过"
    tttTitle = "井字棋"
    tttSub = "3×3 狼币 · 每人3个 · 每步2秒！"
    tttDesc = "绿和红轮流放狼币。\n横、竖、斜先连成3个就赢！\n每人最多3个狼币 — 放第4个时，自己最早的那个会消失（要消失的会闪烁）。\n每步2秒，超时就轮到对方。输的人干杯！"
    green = "绿"
    red = "红"
    winsOf = { "${it}胜！" }
    drawWord = "平局！"
    timeUp = "⏰ 超时！轮到对方"
    gomokuTitle = "五子棋"
    gomokuSub = "15×15 棋盘 · 禁双三"
    gomokuDesc = "黑白轮流落子（黑先）。\n横、竖、斜先连成5个就赢！\n黑棋不能下双三（同时形成两个活三）。输的人干杯！"
    black = "黑"
    white = "白"
    banned33 = "🚫 禁止双三！"
    hotelBtn = "🏨 住处"
    hotelTitle = "住处地址卡"
    hotelName = "住处名称"
    hotelAddr = "地址（复制当地语言粘贴）"
    hotelPhone = "电话（可选）"
    hotelNote = "备注（可选，如：便利店旁边的小巷）"
    hotelAdd = "➕ 添加住处"
    hotelDelete = "🗑️ 删除"
    hotelEmpty = "还没有保存住处"
    hotelTakeMe = "请带我去这里"
    hotelMap = "📍 地图二维码 — 扫码即可导航"
    hotelCall = "📞 打电话"
    hotelEditBtn = "✏️ 编辑住处"
    colorTurn = { "轮到${it}" }
    youAre = { "你: ${it}" }
    phraseBtn = "🗣️ 会话"
    settleBtn = "🧾 结算"
    nbTitle = "数字炸弹"
    nbSub = "躲开隐藏的炸弹数字！UP · DOWN"
    nbDesc = "1到最大数字之间藏着一个炸弹数字。\n轮流输入一个数字。比炸弹小 → ⬆️ UP，比炸弹大 → ⬇️ DOWN — 范围越来越小。\n按到炸弹数字的人 💥 砰！干杯！"
    nbMax = "最大数字"
    nbEnter = "输入数字点确定！"
    nbUp = "⬆️ UP！再大一点"
    nbDown = "⬇️ DOWN！再小一点"
    nbBoom = "💥 砰！炸弹数字"
    nbOut = "请输入范围内的数字"
    nbPass = "传给下一个人 👉"
    nbOk = "确定"
    blTitle = "吹气球"
    blSub = "不知道什么时候爆！砰！"
    blDesc = "手机轮流传，给气球打气。\n每人点1~3下，然后传给下一个人。\n谁也不知道气球什么时候爆。弄爆的人 💥 干杯！"
    blTap = "点击打气（1~3下）"
    blPumps = { "已经吹了${it}下" }
    blPop = "💥 砰！爆了！"
    phTitle = "会话卡片"
    phDesc = "选好要给对方看的语言，点句子就会放大显示。没网也能用。"
    phTo = "显示语言"
    phrases = "洗手间在哪里？|我要这个|买单|多少钱？|不要辣|不要香菜|不要加冰|请给我一杯水|再来一瓶啤酒|打包|可以刷卡吗？|便宜一点吧|请给我收据|可以帮我们拍张照吗？|Wi-Fi密码是多少？|药店在哪里？|救命！|我对花生过敏|干杯！一口闷！|今晚不醉不归！|哇，你长得真好看！|我没醉！（其实醉了）|老板，这个是送的吧？|我们做朋友吧！|该死！|我去！|靠……|你个笨蛋！|笑死我了！|你好|谢谢|不好意思|请说慢一点|请去这个地址|请去机场|请在这里停车|要多长时间？|我要办理入住|可以寄存行李吗？|请再给我几条毛巾|空调坏了|是的|不是|没关系|我听不懂|你会说英语吗？|请写在这里|很高兴认识你|好啊！|再见，回头见！|真的很抱歉|是我的错|不用了，谢谢|我不需要|我只是看看|我不感兴趣|下次吧|请别这样！|车站在哪里？|这个去这里吗？|请给我一张票|请打表|请开慢一点|请快一点|请右转|请左转|请直走|好吃！|请给我菜单|有什么推荐的？|我要退房|几点退房？|早餐几点？|我的房卡丢了|没有热水|请打扫一下房间|请提供叫醒服务|来最烈的酒|有什么下酒菜？|来唱一首歌吧！|今天我请客！|我想吐……|我想回家了|可以试穿吗？|有大一点的吗？|只收现金吗？|哪里有ATM？|可以充一下电吗？|请叫警察|请带我去医院|说什么呢~|烦死了！|你真是个活宝|喝啊，胆小鬼！|滚！（开玩笑的）"
    stTitle = "旅行结算"
    stDesc = "只要输入谁付了多少钱，最后会告诉你谁该转给谁多少。只保存在这部手机里。"
    stPeople = "同行的人"
    stAddPerson = "➕ 添加"
    stAmount = "金额"
    stMemo = "备注（如：晚饭、打车）"
    stPaidBy = "付款人"
    stFor = "一起分摊的人"
    stAdd = "➕ 添加支出"
    stList = "支出"
    stEmpty = "还没有支出"
    stResult = "结算结果"
    stEven = "刚好扯平，不用转账 👍"
    stTotal = { "合计 $it" }
    stSpent = { "应摊 $it" }
    stReset = "🗑️ 全部清空"
    stResetAsk = "确定全部清空吗？"

    nbDazed = "晕乎乎~ 😵‍💫"
    phSpeak = "🔊 朗读"
    phNoVoice = "这部手机没有这种语言的语音。可以在手机设置 → 文字转语音里下载。"
    phFun = "😆 搞笑用语"
    phSwear = "🤬 开玩笑的脏话（只对好朋友！）"
    phCats = "👋 问候|🚕 交通|🍜 餐厅|🏨 住宿|🍻 酒吧|🛍️ 购物·求助|🤬 脏话"


    tsTitle = "叠高塔"
    tsSub = "对准叠好！弄倒就干杯"
    tsDesc = "点击屏幕让左右移动的方块落下。超出下面方块的部分会被切掉。\n点一次就把手机传给下一个人 👉\n把塔弄倒的人 🍺 喝！"
    tsFloor = { "第${it}层" }
    tsTap = "点击放下 → 下一个人 👉"
    tsFall = "💥 哗啦！塔倒了！"
    smTitle = "跟着按"
    smSub = "照着亮的顺序按！错了就喝"
    smDesc = "彩色按钮会随着声音按顺序亮起。看好后按同样的顺序按。\n成功后顺序多一个，把手机传给下一个人 👉\n按错的人 🍺 喝！（开声音更容易 🔊）"
    smLevel = { "第${it}关" }
    smWatch = "👀 看好"
    smGo = "👆 跟着按！"
    smOk = "⭕ 成功！下一个人 👉"
    smWrong = "❌ 按错了！"
    wcTitle = "剪炸弹线"
    wcSub = "剪到真线就爆炸！全靠运气"
    wcDesc = "炸弹上有好几根彩色的线，只有一根是真的。\n轮流点一根线把它剪断 ✂️ 剪完传给下一个人 👉\n剪到真线的人 💥 干杯！"
    wcWires = "线的数量"
    wcTap = "✂️ 点一根线剪断"
    wcSafe = "😮‍💨 呼~安全！下一个人 👉"
    wcBoom = "💥 砰！就是这根！"
    wcLeft = { "还剩 $it 根" }
    cuTitle = "狼冰壶"
    cuSub = "把狼杯往后拉再松手，停在桌边！"
    cuDesc = "用手指把桌子下方的狼啤酒杯往后（往下）拉，松手它就会往前滑。拉得越多滑得越远。\n越靠近桌边越好。超过桌边就会 💦 掉下去。\n每人轮流一次！掉下去的人全部 🍺 干杯，没人掉的话离桌边最远的人喝。"
    cuHint = "👇 往后拉再松手"
    cuFall = "💦 掉下去了！"
    stExport = "📤 导出"
    stExportDesc = "把结算记录存成文件，或直接发给同行的人。"
    stCsv = "📊 Excel 文件 (.csv)"
    stTxt = "📝 文本文件 (.txt)"
    stShare = "💬 分享（微信等）"
    stSaved = "✅ 已保存"
    stMid = "💰 中途结算"
    stMidDesc = "四舍五入成整数先收，剩下的差额留到下次结算。"
    stUnit = "取整单位"
    stMidDone = "✅ 已收款（结算完成）"
    stMidTag = { "💰 中途结算 #$it" }
    stResetSub = "清空后无法恢复。要发给同行的人的话，先保存一下吧。"
    stResetSave = "📤 先保存"
    stResetGo = "🗑️ 直接清空"
    stCols = "序号|日期|备注|金额|付款人|分摊的人|转出|收款|姓名|已付|应摊|合计"
    szBtn = "📏 尺码"
    szTitle = "尺码对照表"
    szDesc = "各国的衣服鞋子尺码不同。点自己的那一行会 ⭐ 保存，下次直接显示。（仅供参考，各品牌略有不同）"
    szCats = "👟 男鞋|👠 女鞋|👕 男上衣|👚 女上衣|👖 裤腰|💍 戒指"
    upTitle = "🎉 有新版本了"
    upSub = { a, b -> "当前 $a → 新版 $b\n下载后点安装就行，保存的设置不会丢。" }
    upGo = "⬇️ 更新"
    upLater = "以后再说"
    upDownloading = "⬇️ 正在下载新版本…"
    upAllow = "仅第一次：在下一个页面打开\"允许来自此来源\"，然后返回。"
    upFail = "下载失败 — 请检查网络后再试。"
    upCheck = "🔄 检查更新"
    upLatest = "✅ 已是最新版本"
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
    menuCardsSub = "Up/Down · โฮจิมินห์ · คิงส์คัพ"
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
    menuLuckSub = "เกมบันได · วงล้อ · ส่งระเบิด · โยนเหรียญ · พลิงโก · ระเบิดตัวเลข · เป่าลูกโป่ง · ตัดสายระเบิด · จิ้มนิ้วเลือกคน"
    menuBattle = "เกมดวล"
    menuBattleSub = "โอเอ็กซ์ · เรียง 4 · โกะโมะกุ · แตะรัวยึดพื้นที่ · ดวลตบมือ"
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
    menuQuickSub = "ดวลความไว · ดวลสี · นาฬิกาจับเวลา · รินเบียร์ · ต่อตึก · ทำตาม · เคิร์ลลิงหมาป่า"
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
    tugTitle = "แตะรัวยึดพื้นที่"
    tugSub = "แตะรัวขยายพื้นที่ของเรา!"
    tugDesc = "สองคนนั่งหันหน้าเข้าหากัน วางมือถือไว้ตรงกลาง\nพอ GO! ให้แตะรัวๆ ฝั่งของตัวเอง!\nดันธง (🎀) ไปถึงเส้นฝั่งตรงข้ามได้ก่อนชนะ คนแพ้หมดแก้ว!"
    tugMash = "แตะรัว!! 👊"
    tugTaps = "ครั้ง"
    winWord = "👑 ชนะ!"
    shotWord = "💀 หมดแก้ว!"
    readyWord = "เตรียม…"
    slapTitle = "ดวลตบมือ"
    slapSub = "ตบหลังมือด้วยอุ้งเท้าหมาป่า!"
    slapDesc = "สองคนนั่งหันหน้าเข้าหากัน\n🛡️ ฝ่ายรับ: ใช้นิ้วกดฝั่งตัวเองค้างไว้ พอโดนโจมตีให้ยกนิ้วหลบ!\n🐾 ฝ่ายรุก: แตะฝั่งตัวเองเมื่อไหร่ก็ได้เพื่อตบ!\nโดนตบ = ฝ่ายรับหมดแก้ว หลบได้ = สลับฝั่ง ยกนิ้วทั้งที่ยังไม่โดนโจมตี = สะดุ้ง หมดแก้ว!"
    slapAtt = "🐾 ฝ่ายรุก"
    slapDef = "🛡️ ฝ่ายรับ"
    slapAttHint = "แตะเมื่อไหร่ก็ตบ!"
    slapAttWait = "รอฝ่ายรับกดค้าง…"
    slapDefHint = "กดค้างไว้ แล้วยกหลบ!"
    slapDefHold = "กดค้างตรงนี้!"
    slapHit = "เพี๊ยะ!! โดน 🎯"
    slapOuch = "โอ๊ย!! 😵 หมดแก้ว"
    slapMiss = "พลาด… สลับฝั่ง 🔄"
    slapDodge = "หลบได้! 🙌"
    slapFlinch = "สะดุ้ง! 🚫 หมดแก้ว"
    slapFlinchAtt = "อีกฝ่ายสะดุ้ง! 🤭"
    slapSmack = "เพี๊ยะ!"
    kcTitle = "คิงส์คัพสากล"
    kcSub = "คิงส์คัพแบบดูรูปก็เข้าใจ"
    kcDesc = "แตะกองไพ่เพื่อจั่วทีละใบ แต่ละใบจะมีรูปบอกว่าต้องทำอะไร\n👑 ทุกครั้งที่ได้ K ให้รินเหล้าใส่แก้วคิงส์คัพตรงกลางนิดหน่อย ใครจั่ว K ใบที่ 4 ต้องหมดแก้วคิงส์คัพ!"
    kcNames = "น้ำตก|ชี้คน|ตัวเอง|บัตรผ่านห้องน้ำ|เต้นต่อกัน|ชนแก้วทุกคน|ยกมือ|คู่หู|ต่อคำ|หมวดหมู่|ตั้งกฎ|เจ้าแห่งคำถาม|คิงส์คัพ"
    kcDescs = "ทุกคนเริ่มดื่ม → หยุดได้เมื่อคนก่อนหน้าหยุด|เลือกคนดื่ม 1 คน|ตัวเองดื่ม|ไปห้องน้ำได้ 1 ครั้ง|เราเต้น → คนถัดไปเต้นตามแล้วเพิ่ม 1 ท่า|ทุกคนดื่ม 1 แก้ว!|คนยกมือช้าที่สุดดื่ม|เลือกคู่หู · เราดื่มเมื่อไหร่ คู่หูดื่มด้วย|ใครต่อไม่ได้ดื่ม|ตั้งหมวด ผลัดกันพูด ใครติดดื่ม|ตั้งกฎใหม่ 1 ข้อ (ฝ่าฝืนดื่ม)|ใครตอบคำถามเราต้องดื่ม|รินลงแก้วกลางนิดหน่อย"
    kcTap = "แตะกองไพ่!"
    kcLeft = { "เหลือ $it ใบ" }
    kcLastKing = "K ใบสุดท้าย! หมดแก้วคิงส์คัพ!!"
    kcLastKingSub = "ดื่มแก้วกลางให้หมด 🍺"
    kcRules = "📋 กติกา"
    kcShuffle = "🔀 สับไพ่ใหม่"
    kcEmpty = "ไพ่หมดแล้ว!"
    beerTitle = "รินเบียร์"
    beerSub = "หยุดก่อนล้นแก้ว!"
    beerDesc = "ดูแก้วเบียร์ของตัวเองไว้ เบียร์จะเติมเร็วบ้างช้าบ้าง!\nแตะให้หยุดตรงเส้นประสีแดง\nใครหยุดใกล้เส้นที่สุดชนะ ล้นแก้ว = ที่โหล่ทันที!"
    beerLine = "ถึงตรงนี้!"
    beerTap = "แตะเพื่อหยุด!"
    beerOver = "ล้นแล้ว! 💦"
    coinTitle = "โยนเหรียญ"
    coinSub = "หน้าหมาป่า หรือ ก้นหมาป่า?"
    coinDesc = "ทุกคนเลือกหัว (🐺 หน้าหมาป่า) หรือ ก้อย (🍑 ก้นหมาป่า) ในช่องของตัวเอง\nพอเลือกครบ อีก 3 วินาทีเหรียญจะหมุนติ้วๆ~\nทายผิดหมดแก้ว! บางครั้ง (15%) เหรียญจะตั้งขึ้น ทุกคนผิดหมด!"
    coinHead = "หัว"
    coinTail = "ก้อย"
    coinPick = "เลือก!"
    coinRight = "⭕ ถูก!"
    coinWrong = "❌ ผิด!"
    coinResHead = "🐺 หัว (หน้า)!"
    coinResTail = "🍑 ก้อย (ก้น)!"
    coinEdge = "😱 เหรียญตั้งขึ้น! ผิดทุกคน"
    qrTitle = "QR ติดต่อของฉัน"
    qrEdit = "✏️ ใส่โซเชียลของฉัน"
    qrPrivacy = "🔒 ข้อมูลที่ใส่จะเก็บไว้ในมือถือเครื่องนี้เท่านั้น ไม่ส่งไปไหน และสร้าง QR ได้แม้ไม่มีเน็ต"
    qrEmpty = "ยังไม่ได้ใส่โซเชียล"
    qrScan = "สแกนด้วยกล้องเพื่อเชื่อมต่อ 📷"
    qrSwipe = "← ปัดเพื่อดูโซเชียลอื่น →"
    qrClose = "✕ ปิด"
    qrSave = "บันทึก"
    qrPhone = "เบอร์โทร (มีรหัสประเทศ เช่น +66 8…)"
    qrId = "ไอดี"
    qrLink = "ลิงก์"
    tipBtn = "💁 ทิป"
    tipTitle = "💁 คำนวณทิป"
    tipAmount = "จำนวนเงิน"
    tipLabel = { "ทิป $it%" }
    tipTotal = "รวมทิป"
    tipSplit = "🍻 หารบิลรวมทิป"
    feedback = "💬 ความคิดเห็น"
    langPickTwo = "เลือกได้สูงสุด 2 ภาษา ① ภาษาหลัก · ② ภาษารอง (แสดงด้านล่าง) แตะอีกครั้งเพื่อเอาออก"
    plinkoTitle = "พลิงโก"
    plinkoSub = "ปล่อยเหรียญหมาป่า!"
    plinkoDesc = "แตะตรงไหนก็ได้ด้านบน เหรียญหมาป่าจะตกลงจากตรงนั้น\nเด้งไปมาระหว่างหมุด ตกช่องไหนก็ได้อันนั้น!\nลำดับช่องจะสลับทุกรอบ"
    plinkoTap = "แตะด้านบนตรงไหนก็ได้เพื่อปล่อย"
    plinkoShuffling = "🔀 กำลังสลับช่อง…"
    plinkoSlots = "1 แก้ว|ครึ่งแก้ว|อีกรอบ|คนขวา|คนซ้าย|ทุกคน|รอด"
    tttTitle = "โอเอ็กซ์"
    tttSub = "3×3 เหรียญหมาป่า · คนละ 3 อัน · ตาละ 2 วิ!"
    tttDesc = "เขียวกับแดงผลัดกันวางเหรียญหมาป่า\nเรียง 3 อันก่อน (แนวนอน ตั้ง ทแยง) ชนะ!\nแต่ละคนมีได้แค่ 3 เหรียญ — วางอันที่ 4 เหรียญที่เก่าที่สุดของเราจะหายไป (อันที่จะหายจะกะพริบ)\nตาละ 2 วินาที หมดเวลาเสียตา คนแพ้หมดแก้ว!"
    green = "เขียว"
    red = "แดง"
    winsOf = { "${it}ชนะ!" }
    drawWord = "เสมอ!"
    timeUp = "⏰ หมดเวลา! เสียตา"
    gomokuTitle = "โกะโมะกุ"
    gomokuSub = "กระดาน 15×15 · ห้ามสามสองแถว"
    gomokuDesc = "ดำกับขาวผลัดกันวางหมาก (ดำเริ่มก่อน)\nเรียง 5 เม็ดก่อน (แนวนอน ตั้ง ทแยง) ชนะ!\nดำห้ามวางจุดที่เกิดสามเปิดสองแถวพร้อมกัน คนแพ้หมดแก้ว!"
    black = "ดำ"
    white = "ขาว"
    banned33 = "🚫 ห้ามสามสองแถว!"
    hotelBtn = "🏨 ที่พัก"
    hotelTitle = "การ์ดที่อยู่ที่พัก"
    hotelName = "ชื่อที่พัก"
    hotelAddr = "ที่อยู่ (คัดลอกภาษาท้องถิ่นมาวาง)"
    hotelPhone = "เบอร์โทร (ไม่บังคับ)"
    hotelNote = "โน้ต (ไม่บังคับ เช่น ซอยข้างร้านสะดวกซื้อ)"
    hotelAdd = "➕ เพิ่มที่พัก"
    hotelDelete = "🗑️ ลบ"
    hotelEmpty = "ยังไม่ได้บันทึกที่พัก"
    hotelTakeMe = "ช่วยไปส่งที่นี่หน่อย"
    hotelMap = "📍 QR แผนที่ — สแกนเพื่อนำทาง"
    hotelCall = "📞 โทร"
    hotelEditBtn = "✏️ แก้ไขที่พัก"
    colorTurn = { "ตา${it}" }
    youAre = { "คุณ: ${it}" }
    phraseBtn = "🗣️ ประโยค"
    settleBtn = "🧾 แบ่งจ่าย"
    nbTitle = "ระเบิดตัวเลข"
    nbSub = "หลบเลขระเบิดที่ซ่อนอยู่! UP · DOWN"
    nbDesc = "มีเลขระเบิดซ่อนอยู่ 1 ตัว ระหว่าง 1 ถึงเลขสูงสุด\nผลัดกันใส่ตัวเลขทีละตัว น้อยกว่าระเบิด → ⬆️ UP มากกว่า → ⬇️ DOWN ช่วงจะแคบลงเรื่อยๆ\nใครกดโดนเลขระเบิด 💥 ตูม! หมดแก้ว!"
    nbMax = "เลขสูงสุด"
    nbEnter = "ใส่ตัวเลขแล้วกดตกลง!"
    nbUp = "⬆️ UP! มากกว่านี้"
    nbDown = "⬇️ DOWN! น้อยกว่านี้"
    nbBoom = "💥 ตูม! เลขระเบิด"
    nbOut = "ใส่เลขที่อยู่ในช่วง"
    nbPass = "ส่งให้คนถัดไป 👉"
    nbOk = "ตกลง"
    blTitle = "เป่าลูกโป่ง"
    blSub = "ไม่รู้จะแตกตอนไหน! ปัง!"
    blDesc = "ส่งมือถือวนกันแล้วเป่าลมเข้าลูกโป่ง\nแต่ละคนแตะ 1–3 ครั้งแล้วส่งต่อ\nไม่มีใครรู้ว่าจะแตกตอนไหน ใครทำแตก 💥 หมดแก้ว!"
    blTap = "แตะเพื่อเป่าลม (1–3 ครั้ง)"
    blPumps = { "เป่าไปแล้ว $it ครั้ง" }
    blPop = "💥 ปัง! แตกแล้ว!"
    phTitle = "การ์ดประโยค"
    phDesc = "เลือกภาษาที่จะให้ดู แล้วแตะประโยคเพื่อแสดงตัวใหญ่ ไม่มีเน็ตก็ใช้ได้"
    phTo = "ภาษาที่จะให้ดู"
    phrases = "ห้องน้ำอยู่ที่ไหน|เอาอันนี้|เช็คบิลด้วย|ราคาเท่าไหร่|ไม่เอาเผ็ด|ไม่ใส่ผักชี|ไม่ใส่น้ำแข็ง|ขอน้ำเปล่าหน่อย|ขอเบียร์อีกขวด|ใส่ถุงกลับบ้าน|จ่ายบัตรได้ไหม|ลดหน่อยได้ไหม|ขอใบเสร็จด้วย|ช่วยถ่ายรูปให้หน่อยได้ไหม|รหัสไวไฟคืออะไร|ร้านขายยาอยู่ที่ไหน|ช่วยด้วย!|แพ้ถั่วลิสง|ชนแก้ว! หมดแก้ว!|คืนนี้ยังอีกยาว!|ว้าว หน้าตาดีมากเลย!|ยังไม่เมา! (เมาแล้ว)|อันนี้แถมใช่ไหม?|มาเป็นเพื่อนกันนะ!|ให้ตายสิ!|เชี่ย! สุดยอด!|ชิบหายแล้ว…|ไอ้บ้า!|ขำฉิบหาย!|สวัสดี|ขอบคุณ|ขอโทษนะ|พูดช้าๆ หน่อยได้ไหม|ไปที่อยู่นี้|ไปสนามบิน|จอดตรงนี้|ใช้เวลานานเท่าไหร่|ขอเช็คอิน|ฝากกระเป๋าได้ไหม|ขอผ้าเช็ดตัวเพิ่ม|แอร์เสีย|ใช่|ไม่ใช่|ไม่เป็นไร|ไม่เข้าใจ|พูดภาษาอังกฤษได้ไหม|ช่วยเขียนตรงนี้หน่อย|ยินดีที่ได้รู้จัก|ได้เลย!|ลาก่อน แล้วเจอกันนะ|ขอโทษจริงๆ|เป็นความผิดของฉันเอง|ไม่เป็นไร ขอบคุณ|ไม่ต้องการ|แค่ดูเฉยๆ|ไม่สนใจ|ไว้คราวหน้านะ|หยุดได้แล้ว!|สถานีอยู่ที่ไหน|อันนี้ไปที่นี่ไหม|ขอตั๋วหนึ่งใบ|เปิดมิเตอร์ด้วย|ขับช้าๆ หน่อย|รีบหน่อยนะ|เลี้ยวขวา|เลี้ยวซ้าย|ตรงไปเลย|อร่อยมาก!|ขอเมนูหน่อย|แนะนำเมนูไหนดี|ขอเช็คเอาท์|เช็คเอาท์กี่โมง|อาหารเช้ากี่โมง|ทำกุญแจห้องหาย|ไม่มีน้ำร้อน|ช่วยทำความสะอาดห้องด้วย|ขอโทรปลุกตอนเช้าด้วย|ขอเหล้าที่แรงที่สุด|มีกับแกล้มอะไรบ้าง|มาร้องเพลงกัน!|วันนี้ฉันเลี้ยงเอง!|จะอ้วกแล้ว…|อยากกลับบ้านแล้ว|ลองใส่ได้ไหม|มีไซส์ใหญ่กว่านี้ไหม|รับแต่เงินสดเหรอ|ตู้ ATM อยู่ที่ไหน|ขอชาร์จแบตหน่อยได้ไหม|ช่วยเรียกตำรวจหน่อย|ช่วยพาไปโรงพยาบาลหน่อย|พูดอะไรเนี่ย~|น่ารำคาญชะมัด!|แกนี่ตลกจริงๆ|ดื่มสิ ไอ้ขี้ขลาด!|ไปไกลๆ เลย! (ล้อเล่น)"
    stTitle = "แบ่งจ่ายทริป"
    stDesc = "แค่ใส่ว่าใครจ่ายไปเท่าไหร่ ตอนท้ายจะบอกว่าใครต้องโอนให้ใครเท่าไหร่ เก็บไว้ในมือถือเครื่องนี้เท่านั้น"
    stPeople = "คนที่ไปด้วยกัน"
    stAddPerson = "➕ คน"
    stAmount = "จำนวนเงิน"
    stMemo = "โน้ต (เช่น มื้อเย็น แท็กซี่)"
    stPaidBy = "คนจ่าย"
    stFor = "หารกับใคร"
    stAdd = "➕ เพิ่มค่าใช้จ่าย"
    stList = "ค่าใช้จ่าย"
    stEmpty = "ยังไม่มีค่าใช้จ่าย"
    stResult = "ผลการแบ่งจ่าย"
    stEven = "พอดีเป๊ะ ไม่ต้องโอน 👍"
    stTotal = { "รวม $it" }
    stSpent = { "ส่วนที่ใช้ $it" }
    stReset = "🗑️ ล้างทั้งหมด"
    stResetAsk = "ล้างทั้งหมดจริงไหม?"

    nbDazed = "มึนตึ้บ~ 😵‍💫"
    phSpeak = "🔊 เปิดเสียง"
    phNoVoice = "มือถือเครื่องนี้ไม่มีเสียงภาษานี้ ดาวน์โหลดได้ที่ ตั้งค่า → การอ่านออกเสียงข้อความ"
    phFun = "😆 ประโยคฮาๆ"
    phSwear = "🤬 คำด่าขำๆ (เฉพาะเพื่อนสนิท!)"
    phCats = "👋 ทักทาย|🚕 เดินทาง|🍜 ร้านอาหาร|🏨 ที่พัก|🍻 บาร์|🛍️ ช้อปปิ้ง·ขอความช่วยเหลือ|🤬 คำด่า"


    tsTitle = "ต่อตึก"
    tsSub = "ต่อให้ตรง! ทำพังต้องดื่ม"
    tsDesc = "แตะเพื่อปล่อยบล็อกที่เลื่อนไปมา ส่วนที่ล้นออกนอกบล็อกข้างล่างจะถูกตัดทิ้ง\nแตะครั้งเดียวแล้วส่งมือถือให้คนถัดไป 👉\nใครทำตึกพัง — 🍺 ดื่ม!"
    tsFloor = { "ชั้น $it" }
    tsTap = "แตะเพื่อปล่อย → คนถัดไป 👉"
    tsFall = "💥 โครม! พังแล้ว!"
    smTitle = "ทำตาม"
    smSub = "กดตามลำดับที่กะพริบ! ผิดต้องดื่ม"
    smDesc = "ปุ่มสีจะกะพริบตามลำดับพร้อมเสียง ดูให้ดีแล้วกดตามลำดับเดิม\nถ้าถูก ลำดับจะเพิ่มขึ้นหนึ่ง แล้วส่งมือถือให้คนถัดไป 👉\nใครกดผิด — 🍺 ดื่ม! (เปิดเสียงจะง่ายขึ้น 🔊)"
    smLevel = { "ด่าน $it" }
    smWatch = "👀 ดูให้ดี"
    smGo = "👆 กดตามเลย!"
    smOk = "⭕ ถูก! คนถัดไป 👉"
    smWrong = "❌ ผิด!"
    wcTitle = "ตัดสายระเบิด"
    wcSub = "ตัดโดนสายจริง ตูม! วัดดวง"
    wcDesc = "ระเบิดมีสายหลายสี แต่มีสายจริงแค่เส้นเดียว\nผลัดกันแตะสายเพื่อตัดทีละเส้น ✂️ ตัดแล้วส่งให้คนถัดไป 👉\nใครตัดโดนสายจริง — 💥 หมดแก้ว!"
    wcWires = "จำนวนสาย"
    wcTap = "✂️ แตะสายเพื่อตัด"
    wcSafe = "😮‍💨 เฮ้อ~ รอด! คนถัดไป 👉"
    wcBoom = "💥 ตูม! สายจริง!"
    wcLeft = { "เหลือ $it สาย" }
    cuTitle = "เคิร์ลลิงหมาป่า"
    cuSub = "ดึงแก้วหมาป่าแล้วปล่อย ให้หยุดชิดขอบ!"
    cuDesc = "ใช้นิ้วดึงแก้วเบียร์หมาป่าด้านล่างโต๊ะไปข้างหลัง (ลง) แล้วปล่อย มันจะไถลไปข้างหน้า ยิ่งดึงมากยิ่งไปไกล\nยิ่งใกล้ขอบโต๊ะยิ่งดี ถ้าเลยขอบจะ 💦 ตกลงไป\nผลัดกันคนละครั้ง! ใครตกต้อง 🍺 หมดแก้วทุกคน ถ้าไม่มีใครตก คนที่ไกลที่สุดดื่ม"
    cuHint = "👇 ดึงลงแล้วปล่อย"
    cuFall = "💦 ตกแล้ว!"
    stExport = "📤 ส่งออก"
    stExportDesc = "บันทึกเป็นไฟล์ หรือส่งให้เพื่อนร่วมทริปได้เลย"
    stCsv = "📊 ไฟล์ Excel (.csv)"
    stTxt = "📝 ไฟล์ข้อความ (.txt)"
    stShare = "💬 แชร์ (LINE ฯลฯ)"
    stSaved = "✅ บันทึกแล้ว"
    stMid = "💰 เคลียร์ระหว่างทาง"
    stMidDesc = "ปัดเป็นตัวเลขกลมๆ แล้วเก็บก่อน ส่วนต่างที่เหลือยกไปรอบหน้า"
    stUnit = "ปัดเศษทีละ"
    stMidDone = "✅ ได้รับแล้ว (เสร็จ)"
    stMidTag = { "💰 เคลียร์รอบที่ $it" }
    stResetSub = "ลบแล้วกู้คืนไม่ได้ ถ้าจะส่งให้เพื่อน บันทึกไว้ก่อนนะ"
    stResetSave = "📤 บันทึกก่อน"
    stResetGo = "🗑️ ลบเลย"
    stCols = "ลำดับ|วันที่|โน้ต|จำนวนเงิน|คนจ่าย|หารกับ|ผู้โอน|ผู้รับ|ชื่อ|จ่ายไป|ส่วนที่ใช้|รวม"
    szBtn = "📏 ไซซ์"
    szTitle = "ตารางเทียบไซซ์"
    szDesc = "ไซซ์เสื้อผ้าและรองเท้าแต่ละประเทศไม่เหมือนกัน แตะแถวของเราเพื่อ ⭐ บันทึกไว้ (เป็นค่าประมาณ แต่ละแบรนด์ต่างกันเล็กน้อย)"
    szCats = "👟 รองเท้าชาย|👠 รองเท้าหญิง|👕 เสื้อชาย|👚 เสื้อหญิง|👖 เอว|💍 แหวน"
    upTitle = "🎉 มีเวอร์ชันใหม่แล้ว"
    upSub = { a, b -> "ตอนนี้ $a → ใหม่ $b\nดาวน์โหลดเสร็จแล้วกดติดตั้งได้เลย การตั้งค่าที่บันทึกไว้ยังอยู่" }
    upGo = "⬇️ อัปเดต"
    upLater = "ไว้ทีหลัง"
    upDownloading = "⬇️ กำลังดาวน์โหลดเวอร์ชันใหม่…"
    upAllow = "ครั้งแรกเท่านั้น: เปิด \"อนุญาตจากแหล่งนี้\" ในหน้าถัดไปแล้วกลับมา"
    upFail = "ดาวน์โหลดไม่สำเร็จ — ตรวจอินเทอร์เน็ตแล้วลองใหม่"
    upCheck = "🔄 ตรวจหาอัปเดต"
    upLatest = "✅ เป็นเวอร์ชันล่าสุดแล้ว"
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
    menuCardsSub = "Up/Down · Hồ Chí Minh · King's Cup"
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
    menuLuckSub = "Leo thang · Vòng quay · Chuyền bom · Tung đồng xu · Plinko · Bom số · Thổi bóng bay · Cắt dây bom · Chọn ngón tay"
    menuBattle = "Trò đối kháng"
    menuBattleSub = "Cờ ba · Nối 4 · Cờ năm · Chạm nhanh giành đất · Đấu đập tay"
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
    menuQuickSub = "Đấu phản xạ · Đấu màu · Bấm giờ · Rót bia · Xếp tháp · Bắt chước · Bi đá sói"
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
    tugTitle = "Chạm nhanh giành đất"
    tugSub = "Chạm nhanh để mở rộng đất!"
    tugDesc = "Hai người ngồi đối diện, điện thoại ở giữa.\nKhi GO! hãy chạm thật nhanh vào nửa màn hình của mình!\nĐẩy lá cờ (🎀) qua vạch bên đối thủ là thắng, người thua cạn ly!"
    tugMash = "Chạm nhanh!! 👊"
    tugTaps = "lần"
    winWord = "👑 Thắng!"
    shotWord = "💀 Cạn ly!"
    readyWord = "Chuẩn bị…"
    slapTitle = "Đấu đập tay"
    slapSub = "Đập mu bàn tay bằng chân sói!"
    slapDesc = "Hai người ngồi đối diện.\n🛡️ Phòng thủ: giữ ngón tay trên nửa màn hình của mình, khi bị đánh thì nhấc lên để né!\n🐾 Tấn công: chạm nửa của mình bất cứ lúc nào để đập!\nTrúng = người phòng thủ cạn ly. Né được = đổi vai. Nhấc tay khi chưa bị đánh = giật mình, cạn ly!"
    slapAtt = "🐾 Tấn công"
    slapDef = "🛡️ Phòng thủ"
    slapAttHint = "Chạm bất cứ lúc nào để đập!"
    slapAttWait = "Chờ người phòng thủ giữ tay…"
    slapDefHint = "Giữ tay, nhấc lên để né!"
    slapDefHold = "Giữ ngón tay ở đây!"
    slapHit = "Bốp!! Trúng 🎯"
    slapOuch = "Á!! 😵 Cạn ly"
    slapMiss = "Hụt… đổi vai 🔄"
    slapDodge = "Né được! 🙌"
    slapFlinch = "Giật mình! 🚫 Cạn ly"
    slapFlinchAtt = "Đối thủ giật mình! 🤭"
    slapSmack = "Bốp!"
    kcTitle = "King's Cup toàn cầu"
    kcSub = "King's Cup bằng hình, ai cũng hiểu"
    kcDesc = "Chạm vào chồng bài để rút từng lá. Mỗi lá hiện việc cần làm bằng hình.\n👑 Mỗi lần ra K thì rót một ít vào ly King's Cup ở giữa, ai rút lá K thứ 4 phải uống cạn ly đó!"
    kcNames = "Thác nước|Chỉ định|Tôi|Vé đi vệ sinh|Nhảy tiếp sức|Cùng cụng ly|Giơ tay|Bạn đồng hành|Nối từ|Chủ đề|Đặt luật|Vua câu hỏi|King's Cup"
    kcDescs = "Mọi người cùng uống → chỉ được dừng khi người trước dừng|Chọn một người uống|Bạn uống|Được đi vệ sinh 1 lần|Bạn nhảy → người sau bắt chước và thêm 1 động tác|Mọi người cùng uống!|Ai giơ tay cuối cùng thì uống|Chọn bạn đồng hành, bạn uống thì họ cũng uống|Ai bí thì uống|Chọn chủ đề, lần lượt kể tên, bí thì uống|Đặt một luật mới (vi phạm thì uống)|Ai trả lời câu hỏi của bạn thì uống|Rót một ít vào ly ở giữa"
    kcTap = "Chạm vào chồng bài!"
    kcLeft = { "Còn $it lá" }
    kcLastKing = "Lá K cuối! Cạn ly King's Cup!!"
    kcLastKingSub = "Uống hết ly ở giữa 🍺"
    kcRules = "📋 Luật"
    kcShuffle = "🔀 Xáo lại"
    kcEmpty = "Hết bài rồi!"
    beerTitle = "Rót bia"
    beerSub = "Dừng ngay trước khi tràn!"
    beerDesc = "Mỗi người nhìn ly bia của mình. Bia rót lúc nhanh lúc chậm!\nChạm để dừng đúng vạch đứt màu đỏ.\nGần vạch nhất thắng, tràn ly = bét ngay!"
    beerLine = "Đến đây!"
    beerTap = "Chạm để dừng!"
    beerOver = "Tràn rồi! 💦"
    coinTitle = "Tung đồng xu"
    coinSub = "Mặt sói hay mông sói?"
    coinDesc = "Mỗi người chọn mặt sấp (🐺 mặt sói) hoặc ngửa (🍑 mông sói) ở chỗ của mình.\nKhi tất cả đã chọn, 3 giây sau đồng xu sẽ xoay tít~\nĐoán sai thì cạn ly! Thỉnh thoảng (15%) đồng xu đứng thẳng — tất cả đều sai!"
    coinHead = "Sấp"
    coinTail = "Ngửa"
    coinPick = "Chọn đi!"
    coinRight = "⭕ Đúng!"
    coinWrong = "❌ Sai!"
    coinResHead = "🐺 Sấp (mặt)!"
    coinResTail = "🍑 Ngửa (mông)!"
    coinEdge = "😱 Đồng xu đứng thẳng! Tất cả đều sai"
    qrTitle = "QR liên hệ của tôi"
    qrEdit = "✏️ Nhập mạng xã hội"
    qrPrivacy = "🔒 Thông tin bạn nhập chỉ lưu trên điện thoại này. Không gửi đi đâu cả, không có mạng vẫn tạo được QR."
    qrEmpty = "Chưa nhập mạng xã hội nào"
    qrScan = "Quét bằng camera để kết nối 📷"
    qrSwipe = "← Vuốt để xem mạng khác →"
    qrClose = "✕ Đóng"
    qrSave = "Lưu"
    qrPhone = "Số điện thoại (kèm mã quốc gia, vd +84 9…)"
    qrId = "ID"
    qrLink = "Liên kết"
    tipBtn = "💁 Tip"
    tipTitle = "💁 Tính tiền tip"
    tipAmount = "Số tiền"
    tipLabel = { "Tip $it%" }
    tipTotal = "Tổng kèm tip"
    tipSplit = "🍻 Chia kèm tip"
    feedback = "💬 Góp ý"
    langPickTwo = "Chọn tối đa 2. ① ngôn ngữ chính · ② ngôn ngữ phụ (hiện bên dưới). Chạm lại để bỏ."
    plinkoTitle = "Plinko"
    plinkoSub = "Thả đồng xu sói!"
    plinkoDesc = "Chạm vào bất kỳ đâu ở phía trên để thả đồng xu sói.\nNó nảy giữa các chốt — rơi vào ô nào thì trúng ô đó!\nThứ tự các ô được xáo mỗi ván."
    plinkoTap = "Chạm phía trên để thả"
    plinkoShuffling = "🔀 Đang xáo ô…"
    plinkoSlots = "1 ly|Nửa ly|Lại|Người bên phải|Người bên trái|Cả bàn|Qua"
    tttTitle = "Cờ ba"
    tttSub = "3×3 xu sói · mỗi người 3 xu · 2 giây một lượt!"
    tttDesc = "Xanh và đỏ lần lượt đặt đồng xu sói.\nNối 3 hàng (ngang, dọc, chéo) trước là thắng!\nMỗi người chỉ có 3 xu — đặt xu thứ 4 thì xu cũ nhất của bạn biến mất (xu sắp mất sẽ nhấp nháy).\nMỗi lượt 2 giây, hết giờ thì mất lượt. Người thua cạn ly!"
    green = "Xanh"
    red = "Đỏ"
    winsOf = { "$it thắng!" }
    drawWord = "Hòa!"
    timeUp = "⏰ Hết giờ! Mất lượt"
    gomokuTitle = "Cờ năm"
    gomokuSub = "Bàn 15×15 · cấm 3-3"
    gomokuDesc = "Đen và trắng lần lượt đặt quân (đen đi trước).\nNối 5 quân (ngang, dọc, chéo) trước là thắng!\nĐen không được đi nước 3-3 (hai hàng ba mở cùng lúc). Người thua cạn ly!"
    black = "Đen"
    white = "Trắng"
    banned33 = "🚫 Cấm 3-3!"
    hotelBtn = "🏨 Chỗ ở"
    hotelTitle = "Thẻ địa chỉ chỗ ở"
    hotelName = "Tên chỗ ở"
    hotelAddr = "Địa chỉ (dán bằng tiếng địa phương)"
    hotelPhone = "Số điện thoại (tùy chọn)"
    hotelNote = "Ghi chú (tùy chọn, vd: hẻm cạnh cửa hàng tiện lợi)"
    hotelAdd = "➕ Thêm chỗ ở"
    hotelDelete = "🗑️ Xóa"
    hotelEmpty = "Chưa lưu chỗ ở nào"
    hotelTakeMe = "Làm ơn chở tôi đến đây"
    hotelMap = "📍 QR bản đồ — quét để chỉ đường"
    hotelCall = "📞 Gọi"
    hotelEditBtn = "✏️ Sửa chỗ ở"
    colorTurn = { "Lượt $it" }
    youAre = { "Bạn: $it" }
    phraseBtn = "🗣️ Câu nói"
    settleBtn = "🧾 Chia tiền"
    nbTitle = "Bom số"
    nbSub = "Né con số bom ẩn! UP · DOWN"
    nbDesc = "Có một con số bom ẩn giữa 1 và số lớn nhất.\nLần lượt nhập một số. Nhỏ hơn bom → ⬆️ UP, lớn hơn → ⬇️ DOWN — khoảng số hẹp dần.\nAi chạm đúng số bom thì 💥 BÙM! Cạn ly!"
    nbMax = "Số lớn nhất"
    nbEnter = "Nhập số rồi bấm OK!"
    nbUp = "⬆️ UP! Lớn hơn"
    nbDown = "⬇️ DOWN! Nhỏ hơn"
    nbBoom = "💥 BÙM! Số bom"
    nbOut = "Chọn số trong khoảng"
    nbPass = "Chuyền cho người kế tiếp 👉"
    nbOk = "OK"
    blTitle = "Thổi bóng bay"
    blSub = "Không biết khi nào nổ! Bụp!"
    blDesc = "Chuyền điện thoại và bơm bóng bay.\nMỗi người chạm 1–3 lần rồi chuyền đi.\nKhông ai biết khi nào bóng nổ. Ai làm nổ — 💥 cạn ly!"
    blTap = "Chạm để bơm (1–3 lần)"
    blPumps = { "Đã bơm $it lần" }
    blPop = "💥 BỤP! Nổ rồi!"
    phTitle = "Thẻ câu nói"
    phDesc = "Chọn ngôn ngữ muốn đưa xem, chạm câu để hiện chữ to. Không cần mạng."
    phTo = "Hiển thị bằng"
    phrases = "Nhà vệ sinh ở đâu ạ?|Cho tôi cái này|Tính tiền ạ|Bao nhiêu tiền?|Đừng cho cay ạ|Không cho rau mùi ạ|Không đá ạ|Cho tôi xin nước|Cho thêm một bia nữa|Cho mang về ạ|Trả bằng thẻ được không?|Bớt chút được không?|Cho tôi hóa đơn|Chụp giúp tôi tấm ảnh được không?|Mật khẩu Wi-Fi là gì?|Nhà thuốc ở đâu ạ?|Cứu tôi với!|Tôi bị dị ứng đậu phộng|Dô! 100% nào!|Đêm nay còn dài!|Wow, bạn đẹp quá!|Tôi chưa say! (say rồi)|Cái này là tặng phải không ạ?|Làm bạn nhé!|Chết tiệt!|Vãi thật!|Đù…|Đồ ngốc!|Buồn cười vãi!|Xin chào|Cảm ơn|Xin lỗi|Làm ơn nói chậm thôi|Cho tôi đến địa chỉ này|Cho tôi ra sân bay|Dừng ở đây ạ|Mất bao lâu ạ?|Tôi muốn nhận phòng|Gửi hành lý được không?|Cho thêm khăn tắm ạ|Máy lạnh bị hỏng|Vâng|Không|Không sao đâu|Tôi không hiểu|Bạn nói tiếng Anh được không?|Làm ơn viết ra đây giúp tôi|Rất vui được gặp bạn|Được thôi!|Tạm biệt, hẹn gặp lại!|Tôi thật sự xin lỗi|Là lỗi của tôi|Không cần đâu, cảm ơn|Tôi không cần|Tôi chỉ xem thôi|Tôi không quan tâm|Để lần sau nhé|Làm ơn dừng lại!|Nhà ga ở đâu ạ?|Xe này có đi đến đây không?|Cho tôi một vé|Bật đồng hồ tính tiền giúp tôi|Làm ơn chạy chậm thôi|Làm ơn đi nhanh một chút|Rẽ phải ạ|Rẽ trái ạ|Đi thẳng ạ|Ngon quá!|Cho tôi xem thực đơn|Món nào ngon nhất ạ?|Tôi muốn trả phòng|Mấy giờ trả phòng ạ?|Bữa sáng lúc mấy giờ ạ?|Tôi làm mất chìa khóa phòng|Không có nước nóng|Làm ơn dọn phòng giúp tôi|Cho tôi đặt cuộc gọi báo thức|Cho tôi loại rượu mạnh nhất|Có đồ nhắm gì không?|Hát một bài đi!|Hôm nay tôi bao!|Tôi muốn ói quá…|Giờ tôi muốn về nhà|Tôi mặc thử được không?|Có cỡ lớn hơn không?|Chỉ nhận tiền mặt thôi à?|Cây ATM ở đâu ạ?|Tôi sạc điện thoại được không?|Làm ơn gọi cảnh sát|Làm ơn đưa tôi đến bệnh viện|Nói gì vậy trời~|Bực mình quá!|Mày hài thật đó|Uống đi, đồ nhát gan!|Biến đi! (đùa thôi)"
    stTitle = "Chia tiền chuyến đi"
    stDesc = "Chỉ cần nhập ai đã trả bao nhiêu — cuối cùng sẽ biết ai cần chuyển cho ai bao nhiêu. Chỉ lưu trên điện thoại này."
    stPeople = "Người đi cùng"
    stAddPerson = "➕ Người"
    stAmount = "Số tiền"
    stMemo = "Ghi chú (vd: ăn tối, taxi)"
    stPaidBy = "Người trả"
    stFor = "Chia cho"
    stAdd = "➕ Thêm khoản chi"
    stList = "Khoản chi"
    stEmpty = "Chưa có khoản chi nào"
    stResult = "Kết quả"
    stEven = "Huề rồi, không cần chuyển 👍"
    stTotal = { "Tổng $it" }
    stSpent = { "Phần chi $it" }
    stReset = "🗑️ Xóa hết"
    stResetAsk = "Xóa hết thật chứ?"

    nbDazed = "Choáng váng~ 😵‍💫"
    phSpeak = "🔊 Phát âm"
    phNoVoice = "Điện thoại này chưa có giọng đọc cho ngôn ngữ này. Có thể tải trong Cài đặt → Chuyển văn bản thành giọng nói."
    phFun = "😆 Câu vui"
    phSwear = "🤬 Chửi vui (chỉ với bạn thân!)"
    phCats = "👋 Chào hỏi|🚕 Đi lại|🍜 Nhà hàng|🏨 Khách sạn|🍻 Quán nhậu|🛍️ Mua sắm·Giúp đỡ|🤬 Chửi vui"


    tsTitle = "Xếp tháp"
    tsSub = "Xếp cho khéo! Làm đổ là uống"
    tsDesc = "Chạm để thả khối đang trượt. Phần thò ra ngoài khối bên dưới sẽ bị cắt.\nChạm một lần rồi chuyền điện thoại cho người kế tiếp 👉\nAi làm đổ tháp — 🍺 uống!"
    tsFloor = { "Tầng $it" }
    tsTap = "Chạm để thả → người kế tiếp 👉"
    tsFall = "💥 Ầm! Đổ rồi!"
    smTitle = "Bắt chước"
    smSub = "Bấm đúng thứ tự nháy! Sai là uống"
    smDesc = "Các nút màu nháy theo thứ tự kèm âm thanh. Nhìn kỹ rồi bấm đúng thứ tự đó.\nĐúng thì chuỗi dài thêm một, chuyền điện thoại cho người kế tiếp 👉\nAi bấm sai — 🍺 uống! (Bật âm thanh sẽ dễ hơn 🔊)"
    smLevel = { "Cấp $it" }
    smWatch = "👀 Xem kỹ"
    smGo = "👆 Bấm theo!"
    smOk = "⭕ Đúng! Người kế tiếp 👉"
    smWrong = "❌ Sai rồi!"
    wcTitle = "Cắt dây bom"
    wcSub = "Cắt trúng dây thật là BÙM! Phó mặc số phận"
    wcDesc = "Quả bom có nhiều dây màu. Chỉ một dây là thật.\nLần lượt chạm một dây để cắt ✂️ rồi chuyền cho người kế tiếp 👉\nAi cắt trúng dây thật — 💥 cạn ly!"
    wcWires = "Số dây"
    wcTap = "✂️ Chạm một dây để cắt"
    wcSafe = "😮‍💨 Phù~ thoát! Người kế tiếp 👉"
    wcBoom = "💥 BÙM! Đúng dây thật!"
    wcLeft = { "Còn $it dây" }
    cuTitle = "Bi đá sói"
    cuSub = "Kéo cốc sói rồi thả, dừng sát mép!"
    cuDesc = "Dùng ngón tay kéo cốc bia sói ở cuối bàn về sau (xuống dưới) rồi thả — nó trượt về phía trước. Kéo càng nhiều càng đi xa.\nCàng sát mép bàn càng tốt. Vượt quá mép là 💦 rơi.\nMỗi người một lượt! Ai rơi đều 🍺 cạn ly, nếu không ai rơi thì người xa nhất uống."
    cuHint = "👇 Kéo xuống rồi thả"
    cuFall = "💦 Rơi rồi!"
    stExport = "📤 Xuất ra"
    stExportDesc = "Lưu lại thành tệp hoặc gửi ngay cho bạn đồng hành."
    stCsv = "📊 Tệp Excel (.csv)"
    stTxt = "📝 Tệp văn bản (.txt)"
    stShare = "💬 Chia sẻ (Zalo…)"
    stSaved = "✅ Đã lưu"
    stMid = "💰 Chốt giữa chừng"
    stMidDesc = "Thu số tiền đã làm tròn, phần chênh lệch còn lại chuyển sang lần sau."
    stUnit = "Làm tròn theo"
    stMidDone = "✅ Đã nhận (xong)"
    stMidTag = { "💰 Chốt lần $it" }
    stResetSub = "Xóa rồi không khôi phục được. Muốn gửi cho bạn thì lưu trước nhé."
    stResetSave = "📤 Lưu trước"
    stResetGo = "🗑️ Cứ xóa"
    stCols = "STT|Ngày|Ghi chú|Số tiền|Người trả|Chia cho|Người gửi|Người nhận|Tên|Đã trả|Phần chi|Tổng"
    szBtn = "📏 Cỡ"
    szTitle = "Bảng quy đổi cỡ"
    szDesc = "Cỡ quần áo, giày mỗi nước một khác. Chạm vào hàng của bạn để ⭐ lưu lại. (Chỉ là ước lượng, mỗi hãng hơi khác.)"
    szCats = "👟 Giày nam|👠 Giày nữ|👕 Áo nam|👚 Áo nữ|👖 Vòng eo|💍 Nhẫn"
    upTitle = "🎉 Đã có phiên bản mới"
    upSub = { a, b -> "Hiện tại $a → mới $b\nTải xong chỉ cần bấm Cài đặt. Cài đặt đã lưu vẫn giữ nguyên." }
    upGo = "⬇️ Cập nhật"
    upLater = "Để sau"
    upDownloading = "⬇️ Đang tải phiên bản mới…"
    upAllow = "Chỉ lần đầu: bật \"Cho phép từ nguồn này\" ở màn hình tiếp theo rồi quay lại."
    upFail = "Tải thất bại — kiểm tra mạng rồi thử lại."
    upCheck = "🔄 Kiểm tra cập nhật"
    upLatest = "✅ Đã là bản mới nhất"
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
