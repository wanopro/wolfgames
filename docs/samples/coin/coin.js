import { sectors, region, lines, text, badge } from './split.js';
export const PC = ['#FF5252', '#448AFF', '#69F0AE', '#FFD740', '#E040FB', '#18FFFF', '#FF6E40', '#B2FF59'];
const GOLD = '#FFC94A', GOLD_D = '#E0A021', GOLD_DD = '#A86E0C', FUR = '#9AA3AF', FUR_D = '#7C8593', CREAM = '#F1EEE8', INK = '#2B2B33', PINK = '#F6AFC0';
const ease = (u) => 1 - Math.pow(1 - Math.min(1, Math.max(0, u)), 3);

// ----- 동전 앞면: 귀여운 늑대 얼굴 -----
function wolfFace(c, r) {
  for (const s of [-1, 1]) { // 귀
    c.beginPath(); c.moveTo(s * r * 0.2, -r * 0.42); c.lineTo(s * r * 0.62, -r * 0.92); c.lineTo(s * r * 0.72, -r * 0.22); c.closePath(); c.fillStyle = FUR_D; c.fill();
    c.beginPath(); c.moveTo(s * r * 0.34, -r * 0.42); c.lineTo(s * r * 0.58, -r * 0.74); c.lineTo(s * r * 0.62, -r * 0.32); c.closePath(); c.fillStyle = PINK; c.fill();
  }
  c.beginPath(); c.ellipse(0, r * 0.02, r * 0.66, r * 0.58, 0, 0, 7); c.fillStyle = FUR; c.fill(); // 머리
  for (const s of [-1, 1]) { c.beginPath(); c.moveTo(s * r * 0.62, r * 0.05); c.lineTo(s * r * 0.8, r * 0.18); c.lineTo(s * r * 0.6, r * 0.28); c.closePath(); c.fillStyle = FUR; c.fill(); } // 볼털
  c.beginPath(); c.ellipse(0, r * 0.24, r * 0.34, r * 0.26, 0, 0, 7); c.fillStyle = CREAM; c.fill(); // 주둥이
  for (const s of [-1, 1]) { c.beginPath(); c.ellipse(s * r * 0.26, -r * 0.08, r * 0.11, r * 0.13, 0, 0, 7); c.fillStyle = INK; c.fill();
    c.beginPath(); c.arc(s * r * 0.23, -r * 0.13, r * 0.045, 0, 7); c.fillStyle = '#fff'; c.fill();
    c.beginPath(); c.ellipse(s * r * 0.44, r * 0.16, r * 0.1, r * 0.06, 0, 0, 7); c.fillStyle = 'rgba(255,130,150,.55)'; c.fill(); }
  c.beginPath(); c.ellipse(0, r * 0.13, r * 0.09, r * 0.065, 0, 0, 7); c.fillStyle = INK; c.fill(); // 코
  c.beginPath(); c.moveTo(-r * 0.12, r * 0.28); c.quadraticCurveTo(-r * 0.06, r * 0.36, 0, r * 0.28); c.quadraticCurveTo(r * 0.06, r * 0.36, r * 0.12, r * 0.28); c.strokeStyle = INK; c.lineWidth = r * 0.04; c.lineCap = 'round'; c.stroke();
  c.beginPath(); c.ellipse(0, r * 0.36, r * 0.05, r * 0.06, 0, 0, Math.PI); c.fillStyle = '#F48FB1'; c.fill(); // 혀
}
// ----- 동전 뒷면: 늑대 엉덩이 -----
function wolfButt(c, r) {
  // 꼬리 (위로 말린 복슬)
  c.save(); c.beginPath(); c.moveTo(-r * 0.08, -r * 0.18); c.bezierCurveTo(-r * 0.3, -r * 0.7, r * 0.3, -r * 0.95, r * 0.34, -r * 0.52); c.bezierCurveTo(r * 0.36, -r * 0.3, r * 0.14, -r * 0.24, r * 0.1, -r * 0.16); c.closePath(); c.fillStyle = FUR_D; c.fill();
  c.beginPath(); c.ellipse(r * 0.24, -r * 0.62, r * 0.13, r * 0.11, 0.5, 0, 7); c.fillStyle = CREAM; c.fill(); c.restore();
  // 뒷다리 + 발바닥
  for (const s of [-1, 1]) { c.beginPath(); c.ellipse(s * r * 0.3, r * 0.5, r * 0.16, r * 0.12, 0, 0, 7); c.fillStyle = FUR_D; c.fill();
    c.beginPath(); c.ellipse(s * r * 0.3, r * 0.52, r * 0.08, r * 0.06, 0, 0, 7); c.fillStyle = PINK; c.fill(); }
  // 엉덩이 두 쪽
  for (const s of [-1, 1]) { c.beginPath(); c.arc(s * r * 0.24, r * 0.12, r * 0.34, 0, 7); c.fillStyle = FUR; c.fill(); }
  c.beginPath(); c.moveTo(0, -r * 0.1); c.quadraticCurveTo(r * 0.03, r * 0.15, 0, r * 0.4); c.strokeStyle = 'rgba(60,66,78,.6)'; c.lineWidth = r * 0.04; c.lineCap = 'round'; c.stroke();
  for (const s of [-1, 1]) { c.beginPath(); c.ellipse(s * r * 0.3, r * 0.02, r * 0.1, r * 0.06, s * 0.4, 0, 7); c.fillStyle = 'rgba(255,255,255,.35)'; c.fill(); // 반짝
    c.beginPath(); c.ellipse(s * r * 0.36, r * 0.24, r * 0.09, r * 0.05, 0, 0, 7); c.fillStyle = 'rgba(255,130,150,.45)'; c.fill(); } // 볼터치(?)
}
// 동전 (face: 1 앞, -1 뒤). sy: 세로 눌림(뒤집히는 중), 크기 r
export function coin(c, x, y, r, face, sy = 1) {
  c.save(); c.translate(x, y); c.scale(1, Math.max(0.04, sy));
  c.beginPath(); c.arc(0, r * 0.07 / Math.max(0.2, sy), r, 0, 7); c.fillStyle = GOLD_DD; c.fill(); // 두께
  const g = c.createRadialGradient(-r * 0.3, -r * 0.35, r * 0.1, 0, 0, r); g.addColorStop(0, '#FFE9A6'); g.addColorStop(0.6, GOLD); g.addColorStop(1, GOLD_D);
  c.beginPath(); c.arc(0, 0, r, 0, 7); c.fillStyle = g; c.fill(); c.lineWidth = r * 0.06; c.strokeStyle = GOLD_DD; c.stroke();
  c.beginPath(); c.arc(0, 0, r * 0.84, 0, 7); c.lineWidth = r * 0.035; c.strokeStyle = 'rgba(168,110,12,.55)'; c.stroke();
  for (let k = 0; k < 24; k++) { const a = k / 24 * Math.PI * 2; c.beginPath(); c.arc(Math.cos(a) * r * 0.92, Math.sin(a) * r * 0.92, r * 0.025, 0, 7); c.fillStyle = 'rgba(168,110,12,.5)'; c.fill(); }
  c.save(); c.beginPath(); c.arc(0, 0, r * 0.8, 0, 7); c.clip(); c.translate(0, r * 0.02); if (face > 0) wolfFace(c, r * 0.95); else wolfButt(c, r * 0.95); c.restore();
  c.beginPath(); c.ellipse(-r * 0.45, -r * 0.5, r * 0.22, r * 0.09, -0.7, 0, 7); c.fillStyle = 'rgba(255,255,255,.35)'; c.fill();
  c.restore();
}
// 옆에서 본 동전 (세로로 선 모습)
function coinEdge(c, x, y, r, wob) {
  c.save(); c.translate(x, y); c.rotate(wob);
  const w = r * 0.26, h = r * 2;
  c.beginPath(); c.roundRect(-w / 2, -h, w, h, w * 0.45); c.fillStyle = GOLD_D; c.fill(); c.lineWidth = 3; c.strokeStyle = GOLD_DD; c.stroke();
  for (let k = 1; k < 14; k++) { c.beginPath(); c.moveTo(-w / 2 + 2, -h + k * h / 14); c.lineTo(w / 2 - 2, -h + k * h / 14); c.strokeStyle = 'rgba(168,110,12,.6)'; c.lineWidth = 1.5; c.stroke(); }
  c.beginPath(); c.roundRect(-w / 2 + 3, -h + 6, w * 0.3, h - 12, 3); c.fillStyle = 'rgba(255,240,190,.6)'; c.fill();
  c.restore();
  c.beginPath(); c.ellipse(x, y + 2, r * 0.5, r * 0.1, 0, 0, 7); c.fillStyle = 'rgba(0,0,0,.35)'; c.fill();
}

// 게임: picks = [[시간, 플레이어, 1|-1]], result = 1|-1|0(세로로 섬)
export class CoinToss {
  constructor(n, picks, result) { this.n = n; this.picks = picks; this.result = result; this.allAt = Math.max(...picks.map((p) => p[0])); this.spinAt = this.allAt + 3; this.landAt = this.spinAt + 2.4; }
  draw(ctx, W, H, t) {
    const secs = sectors(W, H, this.n), pick = Array(this.n).fill(0);
    for (const [tt, i, f] of this.picks) if (tt <= t) pick[i] = f;
    const done = t >= this.landAt + (this.result === 0 ? 1.4 : 0.35);
    secs.forEach((sec, i) => {
      const p = pick[i], ok = done && p === this.result, bad = done && !ok;
      const fill = bad ? '#4A1820' : ok ? '#1F5E3A' : PC[i] + '30';
      region(ctx, sec, fill, (c, s) => {
        text(c, `P${i + 1}`, 0, -64 * s, 18 * s, PC[i], 900);
        if (done) {
          text(c, ok ? '⭕ 맞춤!' : '❌ 틀림!', 0, -18 * s, 34 * s, ok ? '#69F0AE' : '#FF8A80', 900);
          coin(c, 0, 34 * s, 22 * s, p); if (bad) badge(c, '💀 원샷', 0, 76 * s, s * 0.85, '#FF3D5A', '#fff');
        } else {
          // 앞/뒤 고르기 버튼 2개
          for (const [f, dx, lab] of [[1, -48, '앞'], [-1, 48, '뒤']]) {
            const on = p === f, off = p !== 0 && !on;
            c.save(); c.globalAlpha = off ? 0.35 : 1;
            c.beginPath(); c.roundRect((dx - 40) * s, -36 * s, 80 * s, 96 * s, 16 * s); c.fillStyle = on ? '#FFC94A' : 'rgba(255,255,255,.08)'; c.fill();
            if (on) { c.lineWidth = 4 * s; c.strokeStyle = '#fff'; c.stroke(); }
            coin(c, dx * s, 4 * s, 26 * s, f); text(c, lab, dx * s, 46 * s, 16 * s, on ? '#000' : '#fff', 900); c.restore();
          }
          if (!p) text(c, '골라!', 0, 78 * s, 14 * s, '#ccc', 700);
        }
      }, done ? (ok ? '#69F0AE' : '#FF3D5A') : null);
    });
    lines(ctx, secs, W, H);
    // 가운데 동전
    const cx = W / 2, cy = H / 2, R = 46;
    if (t < this.spinAt) {
      ctx.beginPath(); ctx.arc(cx, cy, R + 12, 0, 7); ctx.fillStyle = '#1F2433'; ctx.fill(); ctx.lineWidth = 5; ctx.strokeStyle = 'rgba(0,0,0,.6)'; ctx.stroke();
      if (t >= this.allAt) { const k = Math.ceil(this.spinAt - t), u = (this.spinAt - t) % 1; coin(ctx, cx, cy, R * 0.8, 1, 1); ctx.fillStyle = 'rgba(0,0,0,.55)'; ctx.beginPath(); ctx.arc(cx, cy, R, 0, 7); ctx.fill(); text(ctx, `${k}`, cx, cy + 2, 46 * (1 + 0.3 * u), '#FFD740', 900); }
      else { coin(ctx, cx, cy, R * 0.8, 1, 1); text(ctx, `${pick.filter(Boolean).length}/${this.n}`, cx, cy + R + 24, 15, '#fff', 800); }
      return;
    }
    // 돌아가는 중: 위로 튀어올랐다 내려오며(크기) 빙글빙글 뒤집힘
    const u = Math.min(1, (t - this.spinAt) / 2.4);
    const flips = 9; // 반바퀴 수
    const endPhi = this.result === 0 ? (flips + 0.5) * Math.PI : (this.result > 0 ? flips + 1 : flips) * Math.PI;
    const phi = endPhi * ease(u * 1.05 > 1 ? 1 : u * 1.05);
    const hgt = Math.sin(Math.min(1, u * 1.15) * Math.PI); const r = R * (1 + 0.9 * hgt);
    // 그림자
    ctx.beginPath(); ctx.ellipse(cx, cy + R * 0.9, R * (1 - 0.4 * hgt), R * 0.18 * (1 - 0.4 * hgt), 0, 0, 7); ctx.fillStyle = 'rgba(0,0,0,.35)'; ctx.fill();
    if (this.result === 0 && u >= 1) {
      const dt = t - this.landAt, wob = Math.sin(dt * 14) * 0.25 * Math.exp(-dt * 2.2);
      coinEdge(ctx, cx, cy + R * 0.9, R, wob);
      if (dt > 0.9) { badge(ctx, '😱 세로로 섰다! 전원 틀림', cx, cy - R * 1.6, 1, '#FF3D5A', '#fff'); }
      return;
    }
    const sy = Math.cos(phi); coin(ctx, cx, cy - hgt * 20, r, sy >= 0 ? 1 : -1, Math.abs(sy));
    if (u >= 1 && t > this.landAt + 0.35) { const lab = this.result > 0 ? '🐺 앞 (얼굴)!' : '🍑 뒤 (엉덩이)!'; badge(ctx, lab, cx, cy - R * 1.55, 1, '#FFC94A', '#000'); }
  }
}
