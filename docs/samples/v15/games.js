import { sectors, region, lines, text, badge } from './split.js';
import { chompWolf } from './art.js';
export const PC = ['#FF5252', '#448AFF', '#69F0AE', '#FFD740'];
const GOLD = '#FFC107', RED = '#FF3D5A';
const ease = (u) => 1 - Math.pow(1 - Math.min(1, Math.max(0, u)), 3);
function hubC(ctx, W, H, r, fill, draw) { ctx.save(); ctx.beginPath(); ctx.arc(W / 2, H / 2, r, 0, 7); ctx.fillStyle = fill; ctx.fill(); ctx.lineWidth = 5; ctx.strokeStyle = 'rgba(0,0,0,.6)'; ctx.stroke(); draw && draw(); ctx.restore(); }
// 위(P2)는 180도 돌려서 그림
function half(ctx, W, H, top, fn) { ctx.save(); if (top) { ctx.translate(W, H / 2); ctx.rotate(Math.PI); } else ctx.translate(0, H / 2); fn(); ctx.restore(); }

// ================= 탭 줄다리기 =================
export class Tug {
  constructor(taps) { this.taps = taps; this.pos = 0; this.disp = 0; this.cnt = [0, 0]; this.k = 0; this.win = -1; this.last = 0; this.ripples = []; }
  step(t) {
    while (this.k < this.taps.length && this.taps[this.k][0] <= t && this.win < 0) {
      const [tt, p, x, y] = this.taps[this.k++]; this.cnt[p]++; this.pos += p === 0 ? 0.045 : -0.045; this.ripples.push([tt, p, x, y]);
    }
    const dt = t - this.last; this.last = t;
    this.disp += (this.pos - this.disp) * Math.min(1, dt * 12);
    if (this.win < 0 && Math.abs(this.pos) >= 1) { this.win = this.pos > 0 ? 0 : 1; this.winAt = t; this.pos = Math.sign(this.pos); }
    if (this.win >= 0) this.disp = this.pos;
  }
  draw(ctx, W, H, t) {
    this.step(t);
    const midY = H / 2 - this.disp * (H / 2 - 110); // 깃발 위치: P1이 이기면 위(상대 쪽)로 끌려감
    const shake = this.win < 0 ? Math.sin(t * 60) * 2 : 0;
    // 배경: 내 영역은 내 색, 깃발이 넘어간 만큼 색이 밀려 들어감
    ctx.fillStyle = PC[1] + '44'; ctx.fillRect(0, 0, W, midY); ctx.fillStyle = PC[0] + '44'; ctx.fillRect(0, midY, W, H - midY);
    // 승리선
    for (const [y, c] of [[110, PC[0]], [H - 110, PC[1]]]) { ctx.setLineDash([10, 8]); ctx.strokeStyle = c; ctx.lineWidth = 3; ctx.beginPath(); ctx.moveTo(14, y); ctx.lineTo(W - 14, y); ctx.stroke(); ctx.setLineDash([]); }
    // 밧줄
    ctx.save(); ctx.translate(W / 2 + shake, 0);
    ctx.lineWidth = 16; ctx.strokeStyle = '#C8A06A'; ctx.beginPath(); ctx.moveTo(0, 40); ctx.lineTo(0, H - 40); ctx.stroke();
    ctx.strokeStyle = '#8D6A3F'; ctx.lineWidth = 3; for (let y = 40 + (this.disp * 60 % 22 + 22) % 22; y < H - 40; y += 22) { ctx.beginPath(); ctx.moveTo(-8, y); ctx.lineTo(8, y + 9); ctx.stroke(); }
    // 깃발(가운데 매듭)
    ctx.beginPath(); ctx.arc(0, midY, 24, 0, 7); ctx.fillStyle = '#FF7043'; ctx.fill(); ctx.lineWidth = 4; ctx.strokeStyle = '#fff'; ctx.stroke();
    text(ctx, '🎀', 0, midY + 1, 26, '#fff');
    ctx.restore();
    // 터치 물결
    for (const [tt, p, x, y] of this.ripples) { const u = (t - tt) / 0.35; if (u < 0 || u > 1) continue; ctx.beginPath(); ctx.arc(x, y, 14 + u * 40, 0, 7); ctx.strokeStyle = `rgba(255,255,255,${0.7 * (1 - u)})`; ctx.lineWidth = 4; ctx.stroke(); }
    // 각자 글자
    for (const p of [0, 1]) half(ctx, W, H, p === 1, () => {
      const won = this.win === p, lost = this.win >= 0 && !won;
      text(ctx, `P${p + 1}`, 50, H / 2 - 40, 22, PC[p], 900);
      text(ctx, `${this.cnt[p]}`, W - 62, H / 2 - 50, 30, '#fff', 900);
      text(ctx, '탭', W - 62, H / 2 - 24, 13, '#bbb', 700);
      if (this.win < 0) { const pulse = 1 + 0.08 * Math.sin(t * 25); text(ctx, '연타!! 👊', W / 2 - 90, H / 2 - 150, 26 * pulse, '#fff', 900); }
      else { badge(ctx, won ? '👑 승리!' : '💀 원샷!', W / 2 - 90, H / 2 - 150, 1.2, won ? GOLD : RED, won ? '#000' : '#fff'); }
      if (lost) chompWolf(ctx, W / 2 + 80, H / 2 - 160, 34, Math.abs(Math.sin((t - this.winAt) * 10)), Math.min(1, (t - this.winAt) * 4));
    });
    if (this.win < 0 && t < 1) { ctx.fillStyle = 'rgba(0,0,0,.55)'; ctx.fillRect(0, 0, W, H); text(ctx, t < 0.5 ? '준비…' : 'GO!', W / 2, H / 2, 54, '#FFD740', 900); }
  }
}

// ================= 슬랩 대결 (손등 때리기) =================
// events: [시간, 'slap'|'dodge'|'flinch']  공격자 = att (0=아래 P1, 1=위 P2)
// 귀여운 늑대 앞발 (위에서 본 모양). palm=true 발바닥(젤리) / false 발등(털·발톱). 발끝은 위(-y) 방향
const FUR = '#9AA3AF', FUR_D = '#7C8593', FUR_L = '#C9CFD8', CREAM = '#F1EEE8', PAD = '#F6AFC0', PAD_D = '#E88BA3', OL = '#4E5561';
const HAND = (ctx, x, y, s, band, flip, palm, red = 0) => {
  ctx.save(); ctx.translate(x, y); if (flip) ctx.rotate(Math.PI); ctx.scale(s, s);
  ctx.lineCap = 'round'; ctx.lineJoin = 'round';
  const TOES = [[-32, -50, 15], [-12, -66, 16], [12, -66, 16], [32, -50, 15]];
  // 발 모양: 동그란 발 + 발가락 4개 + 털 많은 팔목
  const body = () => {
    ctx.moveTo(-40, -40); ctx.bezierCurveTo(-58, -8, -50, 30, -34, 50);
    // 팔목 바깥쪽 복슬 털
    ctx.lineTo(-36, 64); ctx.lineTo(-30, 70); ctx.lineTo(-34, 82); ctx.lineTo(-28, 100); ctx.lineTo(28, 100); ctx.lineTo(34, 82); ctx.lineTo(30, 70); ctx.lineTo(36, 64); ctx.lineTo(34, 50);
    ctx.bezierCurveTo(50, 30, 58, -8, 40, -40); ctx.bezierCurveTo(26, -62, -26, -62, -40, -40); ctx.closePath();
  };
  const parts = [body, ...TOES.map(([tx, ty, r]) => () => { ctx.ellipse(tx, ty, r, r * 1.08, 0, 0, 7); })];
  const each = (fn) => { for (const pt of parts) { ctx.beginPath(); pt(); fn(); } };
  ctx.save(); ctx.translate(4, 8); ctx.globalAlpha = 0.25; ctx.fillStyle = '#000'; each(() => ctx.fill()); ctx.restore();
  ctx.lineWidth = 4.5; ctx.strokeStyle = OL; each(() => ctx.stroke());
  const g = ctx.createRadialGradient(-10, -20, 8, 0, 0, 105);
  if (palm) { g.addColorStop(0, '#E9ECF0'); g.addColorStop(1, '#B4BBC6'); } else { g.addColorStop(0, FUR_L); g.addColorStop(1, FUR_D); }
  ctx.fillStyle = g; each(() => ctx.fill());
  if (!palm) {
    // 발등: 발가락 사이 선 + 털결 + 하얀 발톱 살짝
    ctx.strokeStyle = 'rgba(78,85,97,.55)'; ctx.lineWidth = 2.4;
    for (const xx of [-22, 0, 22]) { const y0 = xx ? -48 : -60; ctx.beginPath(); ctx.moveTo(xx, y0); ctx.lineTo(xx * 0.95, y0 + 12); ctx.stroke(); }
    ctx.strokeStyle = 'rgba(255,255,255,.45)'; ctx.lineWidth = 2;
    for (const [fx, fy] of [[-18, -10], [4, -4], [22, -14], [-8, 22], [14, 30], [-20, 44], [6, 58], [-4, 80]]) { ctx.beginPath(); ctx.moveTo(fx - 4, fy + 5); ctx.quadraticCurveTo(fx, fy - 2, fx + 4, fy + 5); ctx.stroke(); }
    for (const [tx, ty, r] of TOES) { ctx.beginPath(); ctx.moveTo(tx - 3.5, ty - r + 2); ctx.quadraticCurveTo(tx, ty - r - 9, tx + 3.5, ty - r + 2); ctx.closePath(); ctx.fillStyle = '#FFFFFF'; ctx.fill(); ctx.lineWidth = 1.5; ctx.strokeStyle = OL; ctx.stroke(); }
  } else {
    // 발바닥 젤리: 큰 패드 + 발가락 젤리 4개
    const pad = (fn) => { ctx.beginPath(); fn(); const pg = ctx.createLinearGradient(0, -90, 0, 40); pg.addColorStop(0, PAD); pg.addColorStop(1, PAD_D); ctx.fillStyle = pg; ctx.fill(); ctx.lineWidth = 2.5; ctx.strokeStyle = '#C96F89'; ctx.stroke(); };
    pad(() => { ctx.moveTo(0, -30); ctx.bezierCurveTo(16, -40, 36, -26, 32, -4); ctx.bezierCurveTo(30, 16, 14, 22, 0, 18); ctx.bezierCurveTo(-14, 22, -30, 16, -32, -4); ctx.bezierCurveTo(-36, -26, -16, -40, 0, -30); });
    for (const [tx, ty, r] of TOES) pad(() => ctx.ellipse(tx, ty - 2, r * 0.62, r * 0.72, 0, 0, 7));
    ctx.fillStyle = 'rgba(255,255,255,.6)';
    for (const [tx, ty, r] of TOES) { ctx.beginPath(); ctx.ellipse(tx - r * 0.2, ty - r * 0.35, r * 0.17, r * 0.24, -0.4, 0, 7); ctx.fill(); }
    ctx.beginPath(); ctx.ellipse(-12, -18, 7, 5, -0.5, 0, 7); ctx.fill();
  }
  // 맞은 자국 (빨갛게 + 털 쭈뼛)
  if (red > 0) { ctx.save(); ctx.beginPath(); body(); ctx.clip(); ctx.globalAlpha = red * 0.85; const rg = ctx.createRadialGradient(0, -14, 4, 0, -14, 52); rg.addColorStop(0, '#FF3D57'); rg.addColorStop(1, 'rgba(255,61,87,0)'); ctx.fillStyle = rg; ctx.fillRect(-80, -130, 160, 200); ctx.restore(); }
  // 팔목 밴드 (플레이어 색)
  ctx.beginPath(); ctx.roundRect(-33, 84, 66, 16, 8); ctx.fillStyle = band; ctx.fill(); ctx.lineWidth = 3; ctx.strokeStyle = 'rgba(0,0,0,.35)'; ctx.stroke();
  ctx.beginPath(); ctx.roundRect(-26, 87, 22, 5, 3); ctx.fillStyle = 'rgba(255,255,255,.45)'; ctx.fill();
  // 팔 (화면 밖으로)
  ctx.beginPath(); ctx.moveTo(-28, 100); ctx.lineTo(-30, 160); ctx.lineTo(30, 160); ctx.lineTo(28, 100); ctx.closePath(); ctx.fillStyle = palm ? '#B4BBC6' : FUR_D; ctx.fill(); ctx.lineWidth = 4.5; ctx.strokeStyle = OL; ctx.stroke();
  ctx.restore();
};
export class Slap {
  constructor(script) { this.s = script; }
  draw(ctx, W, H, t) {
    // 상태 계산
    let cur = null; for (const e of this.s) { if (e.t > t) break; cur = e; }
    const st = cur || this.s[0], att = st.att, score = st.score, def = 1 - att, dtE = t - st.t;
    ctx.fillStyle = PC[1] + '33'; ctx.fillRect(0, 0, W, H / 2); ctx.fillStyle = PC[0] + '33'; ctx.fillRect(0, H / 2, W, H / 2);
    ctx.strokeStyle = 'rgba(0,0,0,.6)'; ctx.lineWidth = 4; ctx.beginPath(); ctx.moveTo(0, H / 2); ctx.lineTo(W, H / 2); ctx.stroke();
    // 손 위치: 수비 손이 가운데 위, 공격 손이 그 밑(손바닥 위로)
    const cx = W / 2, cy = H / 2, sign = def === 1 ? -1 : 1; // 수비자 쪽 방향
    let defOff = 0, attOff = 0, red = 0, flash = 0;
    if (st.kind === 'slap') { const u = dtE / 0.16; attOff = u < 1 ? ease(u) * 120 : Math.max(0, 1 - (dtE - 0.5) / 0.4) * 120; red = u >= 1 ? Math.max(0, 1 - (dtE - 0.2) / 1.6) : 0; flash = u >= 1 && dtE < 0.32 ? 1 - (dtE - 0.16) / 0.16 : 0; }
    if (st.kind === 'dodge') { const u = dtE / 0.16; attOff = u < 1 ? ease(u) * 120 : Math.max(0, 1 - (dtE - 0.4) / 0.4) * 120; defOff = Math.min(1, dtE / 0.1) * 80 * (dtE < 1.0 ? 1 : Math.max(0, 1 - (dtE - 1.0) / 0.4)); }
    if (st.kind === 'flinch') { defOff = Math.min(1, dtE / 0.08) * 50 * Math.max(0, 1 - Math.max(0, dtE - 0.6) / 0.4); }
    if (st.kind === 'feint') { attOff = Math.sin(Math.min(1, dtE / 0.35) * Math.PI) * 40; }
    const shake = st.kind === 'ready' ? Math.sin(t * 9) * 4 : 0;
    // 수비 손 (손등 위, 공격 손 위에 겹침)
    // 공격 손은 휘두를 때 화면 쪽으로 커지면서 수비 손 위를 덮침
    const swing = Math.min(1, attOff / 120);
    // 공격 손(손바닥 위)이 밑에, 수비 손(손등 위)이 그 위에 살짝 겹쳐 올려진 모양
    if (swing < 0.5) {
      HAND(ctx, cx + shake, cy - sign * (58 - attOff * 0.95), 0.95 + swing * 0.3, PC[att], att === 1, true);
      HAND(ctx, cx, cy + sign * (58 + defOff), 0.95, PC[def], def === 1, false, red);
    } else {
      HAND(ctx, cx, cy + sign * (58 + defOff), 0.95, PC[def], def === 1, false, red);
      // 내려칠 때는 손등이 보이게 뒤집혀서 덮침
      HAND(ctx, cx + shake + swing * 14, cy - sign * (58 - attOff * 0.95), 0.95 + swing * 0.22, PC[att], att === 1, false);
    }
    if (st.kind === 'slap' && dtE > 0.14 && dtE < 0.9) { const k = Math.min(1, (dtE - 0.14) / 0.12); ctx.save(); ctx.translate(cx + 70, cy + sign * 40); ctx.scale(k, k); ctx.rotate(-0.2);
      ctx.beginPath(); for (let a = 0; a < 16; a++) { const r = a % 2 ? 26 : 52; ctx.lineTo(Math.cos(a * Math.PI / 8) * r, Math.sin(a * Math.PI / 8) * r); } ctx.closePath(); ctx.fillStyle = '#FFEB3B'; ctx.fill(); ctx.strokeStyle = '#E65100'; ctx.lineWidth = 4; ctx.stroke();
      text(ctx, '짝!', 0, 2, 30, '#D84315', 900); ctx.restore(); }
    if (flash > 0) { ctx.fillStyle = `rgba(255,255,255,${flash * 0.8})`; ctx.fillRect(0, 0, W, H); }
    // 각자 영역 글자
    for (const p of [0, 1]) half(ctx, W, H, p === 1, () => {
      const isAtt = p === att, y = H / 2 - 40;
      text(ctx, `P${p + 1}`, 40, H / 2 - 24, 20, PC[p], 900);
      text(ctx, `💥 ${score[p]}`, W - 50, H / 2 - 24, 20, '#fff', 900);
      text(ctx, isAtt ? '🐾 공격' : '🛡️ 수비', W / 2, y - 70, 24, isAtt ? '#FFAB91' : '#80D8FF', 900);
      let sub = isAtt ? '아무 때나 터치하면 찰싹!' : '손가락 꾹 누르고 있다가 피하기!';
      if (st.kind === 'slap') sub = isAtt ? '짝!! 명중 🎯' : '아야!! 😵 원샷';
      if (st.kind === 'dodge') sub = isAtt ? '헛손질… 공수 교대 🔄' : '피했다! 🙌';
      if (st.kind === 'flinch') sub = isAtt ? '상대가 움찔! 🤭' : '움찔 반칙! 🚫 원샷';
      if (st.kind === 'feint') sub = isAtt ? '(속이기… 화면 밖으로 살짝)' : '참아… 참아…';
      text(ctx, sub, W / 2, y - 38, 17, '#fff', 800);
    });
  }
}

// ================= 글로벌 킹스컵 =================
export const KC = {
  A: ['🌊', '폭포', '다 같이 마시기 시작 → 앞사람이 멈춰야 멈춤'],
  2: ['👉', '지목', '한 명 골라서 마시게 하기'],
  3: ['🙋', '나', '내가 마시기'],
  4: ['🚽', '화장실 프리패스', '화장실 1번 다녀와도 OK'],
  5: ['💃', '댄스 릴레이', '내가 춤 → 다음 사람이 따라 하고 하나 추가'],
  6: ['🍻', '다 같이 짠', '모두 한 잔!'],
  7: ['🙌', '하늘', '손 제일 늦게 든 사람 마시기'],
  8: ['🤝', '짝꿍', '짝꿍 정하기 · 내가 마실 때 같이 마심'],
  9: ['🔤', '끝말잇기', '막히는 사람 마시기'],
  10: ['📚', '카테고리', '주제 정하고 돌아가며 말하기'],
  J: ['📜', '규칙 만들기', '새 규칙 하나 (어기면 마시기)'],
  Q: ['❓', '질문 마스터', '내 질문에 대답하면 마시기'],
  K: ['👑', '킹스컵', '가운데 잔에 조금 따르기'],
};
export class Kings {
  constructor(deal) { this.deal = deal; }
  draw(ctx, W, H, t) {
    ctx.fillStyle = '#12121A'; ctx.fillRect(0, 0, W, H);
    text(ctx, '🃏 글로벌 킹스컵', W / 2, 34, 22, '#FFB300', 900);
    let k = -1; for (let i = 0; i < this.deal.length; i++) if (this.deal[i][0] <= t) k = i;
    const kings = this.deal.slice(0, k + 1).filter((d) => d[1] === 'K').length;
    const left = 52 - (k + 1) - 20;
    // 킹스컵 잔 (위 오른쪽)
    const gx = W - 60, gy = 100; ctx.save(); ctx.translate(gx, gy);
    ctx.beginPath(); ctx.moveTo(-26, -30); ctx.lineTo(26, -30); ctx.lineTo(18, 30); ctx.lineTo(-18, 30); ctx.closePath(); ctx.fillStyle = 'rgba(255,255,255,.12)'; ctx.fill(); ctx.strokeStyle = '#ddd'; ctx.lineWidth = 3; ctx.stroke();
    const lvl = kings / 4; ctx.save(); ctx.clip(); ctx.fillStyle = '#F9A825'; ctx.fillRect(-30, 30 - 60 * lvl, 60, 60 * lvl); ctx.restore(); ctx.restore();
    text(ctx, `👑 ${kings}/4`, gx, gy + 50, 16, '#FFD740', 900);
    text(ctx, `🂠 ${left}장 남음`, 70, 100, 16, '#aaa', 700);
    // 덱
    for (let i = 0; i < 4; i++) { ctx.beginPath(); ctx.roundRect(W / 2 - 40 + i * 2, 70 - i * 2, 80, 110, 10); ctx.fillStyle = '#B71C1C'; ctx.fill(); ctx.strokeStyle = '#fff'; ctx.lineWidth = 2; ctx.stroke(); }
    text(ctx, '🐺', W / 2 + 6, 120, 36, '#fff');
    if (k < 0) { text(ctx, '카드 더미를 터치!', W / 2, 420, 22, '#fff', 800); return; }
    const [t0, r, suit] = this.deal[k]; const u = (t - t0) / 0.45;
    const [ico, name, desc] = KC[r]; const red = suit === '♥' || suit === '♦';
    // 카드 뒤집기 (가로로 줄었다 펴짐) + 아래로 날아옴
    const fly = ease(Math.min(1, u * 1.6)); const flip = Math.min(1, u); const sx = Math.abs(Math.cos(flip * Math.PI));
    const cx = W / 2, cy = 125 + fly * 300, cw = 250, ch = 340;
    ctx.save(); ctx.translate(cx, cy); ctx.scale(Math.max(0.02, sx), 1);
    ctx.beginPath(); ctx.roundRect(-cw / 2, -ch / 2, cw, ch, 22);
    if (flip < 0.5) { ctx.fillStyle = '#B71C1C'; ctx.fill(); text(ctx, '🐺', 0, 0, 70, '#fff'); }
    else {
      ctx.fillStyle = '#FFFDF7'; ctx.fill(); ctx.lineWidth = 4; ctx.strokeStyle = r === 'K' && kings === 4 ? '#FFC107' : '#ddd'; ctx.stroke();
      const col = red ? '#D32F2F' : '#212121';
      text(ctx, r, -cw / 2 + 28, -ch / 2 + 30, 28, col, 900); text(ctx, suit, -cw / 2 + 28, -ch / 2 + 60, 24, col, 900);
      ctx.save(); ctx.rotate(Math.PI); text(ctx, r, -cw / 2 + 28, -ch / 2 + 30, 28, col, 900); text(ctx, suit, -cw / 2 + 28, -ch / 2 + 60, 24, col, 900); ctx.restore();
      const pop = 1 + 0.15 * Math.max(0, Math.sin(Math.min(1, (u - 0.5) * 2) * Math.PI));
      const last = r === 'K' && kings === 4;
      text(ctx, last ? '🍺' : ico, 0, -30, 110 * pop, '#000');
    }
    ctx.restore();
    if (flip >= 0.5) {
      const last = r === 'K' && kings === 4;
      text(ctx, last ? '마지막 킹! 킹스컵 원샷!!' : name, W / 2, 640, last ? 26 : 30, last ? '#FFD740' : '#fff', 900);
      text(ctx, last ? '가운데 잔을 다 마시기 🍺' : desc, W / 2, 678, 16, '#bbb', 700);
    }
  }
}

// ================= 맥주 거품 채우기 =================
// 모두 같은 속도로 차오름 (점점 빨라짐). 선에 가장 가깝게 멈춘 사람 1등, 넘치면 꼴찌
export class Beer {
  constructor(n, taps, start = 1.2) { this.n = n; this.taps = taps; this.start = start; }
  level(t) { const x = Math.max(0, t - this.start); return Math.min(1.12, 0.06 * x + 0.055 * x * x); } // 1 = 선
  draw(ctx, W, H, t) {
    const secs = sectors(W, H, this.n);
    const stop = Array(this.n).fill(null); for (const [tt, i] of this.taps) if (tt <= t) stop[i] = this.level(tt);
    const overT = (() => { let lo = this.start, hi = 20; for (let k = 0; k < 40; k++) { const m = (lo + hi) / 2; if (this.level(m) >= 1.06) hi = m; else lo = m; } return hi; })();
    const done = stop.every((v) => v !== null) || t > overT + 0.3;
    const res = stop.map((v, i) => [v === null || v > 1.0 ? 9 : 1 - v, i]).sort((a, b) => a[0] - b[0]);
    secs.forEach((sec, i) => {
      const v = stop[i] ?? Math.min(this.level(t), 1.06); const over = v > 1.0;
      const rk = done ? res.findIndex((r) => r[1] === i) : -1;
      const glow = done ? (rk === 0 ? GOLD : rk === this.n - 1 ? RED : null) : null;
      region(ctx, sec, PC[i] + '30', (c, s) => {
        const gh = 140 * s, gw = 84 * s, top = -gh / 2 - 26 * s, bot = gh / 2 - 26 * s, lineY = top + 18 * s;
        text(c, `P${i + 1}`, -gw - 26 * s, top + 8 * s, 18 * s, PC[i], 900);
        // 잔
        c.save(); c.beginPath(); c.moveTo(-gw / 2, top); c.lineTo(gw / 2, top); c.lineTo(gw / 2 - 8 * s, bot); c.lineTo(-gw / 2 + 8 * s, bot); c.closePath();
        c.fillStyle = 'rgba(255,255,255,.10)'; c.fill(); c.save(); c.clip();
        const fillTop = bot - (bot - lineY) * Math.min(v, 1.0);
        c.fillStyle = '#F9A825'; c.fillRect(-gw, fillTop, gw * 2, bot - fillTop + 2);
        for (let b = 0; b < 7; b++) { const by = bot - ((t * 60 * s + b * 23 * s) % (bot - fillTop + 1)); c.beginPath(); c.arc(-gw / 3 + (b * 13 % 50) * s, by, 2.2 * s, 0, 7); c.fillStyle = 'rgba(255,255,255,.55)'; c.fill(); }
        // 거품
        const fh = 12 * s; c.fillStyle = '#FFF8E1'; for (let k = -3; k <= 3; k++) { c.beginPath(); c.arc(k * 14 * s, fillTop - fh * 0.3 + Math.sin(t * 6 + k) * 1.5 * s, 10 * s, 0, 7); c.fill(); } c.fillRect(-gw, fillTop - fh * 0.3, gw * 2, fh);
        c.restore();
        c.lineWidth = 4 * s; c.strokeStyle = '#E0E0E0'; c.stroke();
        c.beginPath(); c.roundRect(gw / 2 - 4 * s, top + 26 * s, 26 * s, 70 * s, 12 * s); c.lineWidth = 7 * s; c.stroke();
        c.restore();
        // 넘치는 거품
        if (over) { c.fillStyle = '#FFF8E1'; for (const sd of [-1, 1]) { c.beginPath(); c.ellipse(sd * (gw / 2 + 2 * s), top + 30 * s, 8 * s, 28 * s, 0, 0, 7); c.fill(); } c.beginPath(); c.ellipse(0, top - 8 * s, gw * 0.6, 14 * s, 0, 0, 7); c.fill(); }
        // 선
        c.setLineDash([6 * s, 5 * s]); c.strokeStyle = '#FF5252'; c.lineWidth = 3 * s; c.beginPath(); c.moveTo(-gw / 2 - 14 * s, lineY); c.lineTo(gw / 2 + 14 * s, lineY); c.stroke(); c.setLineDash([]);
        text(c, '여기까지!', -gw / 2 - 34 * s, lineY, 11 * s, '#FF8A80', 800);
        // 결과
        let msg = stop[i] !== null ? (over ? '넘쳤다! 💦' : `${Math.round(v * 1000) / 10}%`) : (t < this.start ? '준비…' : '터치해서 멈춰!');
        if (stop[i] === null && v >= 1.06) msg = '넘쳤다! 💦';
        text(c, msg, 0, bot + 24 * s, 20 * s, over ? '#80D8FF' : '#fff', 900);
        if (done && glow) badge(c, rk === 0 ? '👑 1등' : '💀 원샷', 0, bot + 56 * s, s * 0.9, glow, rk === 0 ? '#000' : '#fff');
      }, glow);
    });
    lines(ctx, secs, W, H);
    hubC(ctx, W, H, 34, '#1F2433', () => text(ctx, done ? '🏁' : '🍺', W / 2, H / 2 + 1, 24, '#fff', 900));
  }
}
