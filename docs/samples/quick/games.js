import { sectors, region, lines, text, badge, inPoly } from './split.js';
export const PC = ['#FF5252', '#448AFF', '#69F0AE', '#FFD740', '#E040FB', '#18FFFF', '#FF6E40', '#B2FF59'];
const GOLD = '#FFC107', RED = '#FF3D5A';
const rankStyle = (rank, last) => rank === 0 ? { glow: GOLD, tag: '👑 1등', bg: GOLD, fg: '#000' } : rank === last ? { glow: RED, tag: '💀 꼴찌', bg: RED, fg: '#fff' } : null;
function hub(ctx, W, H, r, fill, draw) { ctx.save(); ctx.beginPath(); ctx.arc(W / 2, H / 2, r, 0, 7); ctx.fillStyle = fill; ctx.fill(); ctx.lineWidth = 5; ctx.strokeStyle = 'rgba(0,0,0,.6)'; ctx.stroke(); if (draw) draw(); ctx.restore(); }
function pname(ctx, i, s) { text(ctx, `P${i + 1}`, 0, -78 * s, 20 * s, PC[i]); }

// ---------------- 반응속도 대결 ----------------
export class Reaction {
  constructor(n, goAt) { this.n = n; this.goAt = goAt; this.tap = Array(n).fill(null); }
  press(i, t) { if (this.tap[i] !== null) return; this.tap[i] = t < this.goAt ? 'foul' : t - this.goAt; }
  draw(ctx, W, H, t) {
    const secs = sectors(W, H, this.n), go = t >= this.goAt, done = this.tap.every((v) => v !== null);
    const valid = this.tap.map((v, i) => [v, i]).filter(([v]) => typeof v === 'number').sort((a, b) => a[0] - b[0]).map(([, i]) => i);
    const order = [...valid, ...this.tap.map((v, i) => (v === 'foul' ? i : -1)).filter((i) => i >= 0)];
    secs.forEach((sec, i) => {
      const v = this.tap[i]; const rk = done ? order.indexOf(i) : -1; const st = done ? rankStyle(rk, this.n - 1) : null;
      const fill = v === 'foul' ? '#4A1820' : !go ? '#3B2A2E' : v !== null ? '#1F6B43' : '#2ECC71';
      region(ctx, sec, fill, (c, s) => {
        pname(c, i, s);
        if (v === 'foul') { text(c, '🚫 부정출발', 0, -10 * s, 30 * s, '#FF8A80', 900); text(c, '너무 빨라요', 0, 30 * s, 16 * s, '#FFCDD2', 600); }
        else if (v !== null) { text(c, `${v.toFixed(3)}초`, 0, -6 * s, 44 * s, '#fff', 900); }
        else if (go) { text(c, '지금 터치!', 0, -6 * s, 40 * s, '#0B3D22', 900); }
        else { text(c, '기다리세요...', 0, -10 * s, 30 * s, '#F2C9CF', 800); text(c, '초록색이 되면 터치', 0, 28 * s, 16 * s, '#C9A3AA', 600); }
        if (st) badge(c, st.tag, 0, 52 * s, s, st.bg, st.fg);
      }, st ? st.glow : null);
    });
    lines(ctx, secs, W, H);
    hub(ctx, W, H, 34, go ? '#2ECC71' : '#3B2A2E', () => text(ctx, done ? '🏁' : go ? 'GO' : '⏳', W / 2, H / 2 + 1, 22, go ? '#0B3D22' : '#fff', 900));
  }
}

// ---------------- 색 반응 대결 ----------------
export const COLORS = [['빨강', '#E53935'], ['주황', '#FB8C00'], ['노랑', '#FDD835'], ['초록', '#43A047'], ['파랑', '#1E88E5'], ['보라', '#8E24AA']];
export class ColorGame {
  constructor(n, target, seq, appearAt, windowSec = 1.5) { this.n = n; this.target = target; this.seq = seq; this.appearAt = appearAt; this.win = windowSec; this.tap = Array(n).fill(null); }
  colorAt(t) { if (t >= this.appearAt) return this.target; let c = this.seq[0][1]; for (const [at, col] of this.seq) if (t >= at) c = col; return c; }
  press(i, t) { if (this.tap[i] !== null) return; const ok = this.colorAt(t) === this.target && t <= this.appearAt + this.win; this.tap[i] = ok ? t - this.appearAt : 'wrong'; }
  draw(ctx, W, H, t) {
    const secs = sectors(W, H, this.n), cur = this.colorAt(t), over = t > this.appearAt + this.win || this.tap.every((v) => v !== null);
    const res = this.tap.map((v) => (v === null && over ? 'miss' : v));
    const caught = res.map((v, i) => (v === 'miss' || v === 'wrong' ? i : -1)).filter((i) => i >= 0);
    const order = res.map((v, i) => [v, i]).filter(([v]) => typeof v === 'number').sort((a, b) => a[0] - b[0]).map(([, i]) => i);
    secs.forEach((sec, i) => {
      const v = res[i]; let st = null;
      if (over) { if (caught.length) st = caught.includes(i) ? { glow: RED, tag: '💀 걸림!', bg: RED, fg: '#fff' } : null; else st = rankStyle(order.indexOf(i), this.n - 1); }
      const fill = over ? (caught.includes(i) ? '#4A1820' : '#1E1E2A') : COLORS[cur][1];
      region(ctx, sec, fill, (c, s) => {
        pname(c, i, s);
        if (v === 'wrong') text(c, '땡! 다른 색', 0, -6 * s, 34 * s, '#FF8A80', 900);
        else if (v === 'miss') text(c, '못 눌렀어요', 0, -6 * s, 34 * s, '#FF8A80', 900);
        else if (typeof v === 'number') { text(c, `${v.toFixed(3)}초`, 0, -6 * s, 42 * s, '#fff', 900); }
        else { c.save(); c.shadowColor = 'rgba(0,0,0,.5)'; c.shadowBlur = 6; text(c, `${COLORS[this.target][0]} 나오면`, 0, -22 * s, 26 * s, '#fff', 900); text(c, '터치!', 0, 14 * s, 34 * s, '#fff', 900); c.restore(); }
        if (st) badge(c, st.tag, 0, 52 * s, s, st.bg, st.fg);
      }, st ? st.glow : null);
    });
    lines(ctx, secs, W, H);
    // 가운데: 이번 판 지정색
    hub(ctx, W, H, 46, '#1E1E2A', () => { ctx.beginPath(); ctx.arc(W / 2, H / 2, 34, 0, 7); ctx.fillStyle = COLORS[this.target][1]; ctx.fill(); ctx.lineWidth = 4; ctx.strokeStyle = '#fff'; ctx.stroke(); text(ctx, '이 색!', W / 2, H / 2 + 1, 15, '#fff', 900); });
  }
}

// ---------------- 스탑워치 ----------------
export class Stopwatch {
  constructor(n, target, startAt) { this.n = n; this.target = target; this.startAt = startAt; this.tap = Array(n).fill(null); }
  press(i, t) { if (this.tap[i] !== null || t < this.startAt) return; this.tap[i] = t - this.startAt; }
  draw(ctx, W, H, t) {
    const secs = sectors(W, H, this.n), done = this.tap.every((v) => v !== null), started = t >= this.startAt;
    const order = this.tap.map((v, i) => [v, i]).sort((a, b) => Math.abs(a[0] - this.target) - Math.abs(b[0] - this.target)).map(([, i]) => i);
    secs.forEach((sec, i) => {
      const v = this.tap[i], st = done ? rankStyle(order.indexOf(i), this.n - 1) : null;
      region(ctx, sec, done ? '#1E1E2A' : v !== null ? '#2A3550' : '#24304A', (c, s) => {
        pname(c, i, s);
        if (done) { text(c, `${v.toFixed(2)}초`, 0, -14 * s, 44 * s, '#fff', 900); const d = v - this.target; text(c, `${d >= 0 ? '+' : ''}${d.toFixed(2)}초`, 0, 24 * s, 20 * s, Math.abs(d) < 0.3 ? '#69F0AE' : '#FFAB91', 800); }
        else if (v !== null) text(c, '✋ 완료! 기다려요', 0, -6 * s, 26 * s, '#BBDEFB', 800);
        else if (started) { text(c, `속으로 ${this.target}초 세고`, 0, -20 * s, 24 * s, '#fff', 900); text(c, '터치!', 0, 14 * s, 34 * s, '#FFD740', 900); }
        else text(c, `${Math.ceil(this.startAt - t)}`, 0, -6 * s, 56 * s, '#fff', 900);
        if (st) badge(c, st.tag, 0, 58 * s, s, st.bg, st.fg);
      }, st ? st.glow : null);
    });
    lines(ctx, secs, W, H);
    hub(ctx, W, H, 44, '#121826', () => { text(ctx, `${this.target}`, W / 2, H / 2 - 5, 34, '#FFD740', 900); text(ctx, '초', W / 2, H / 2 + 22, 13, '#FFD740', 800); });
  }
}

// ---------------- 폭탄돌리기 ----------------
function bomb(ctx, x, y, r, fuse, t) {
  // 몸통
  ctx.save(); ctx.translate(x, y);
  ctx.beginPath(); ctx.arc(0, 0, r, 0, 7); const g = ctx.createRadialGradient(-r * 0.35, -r * 0.35, r * 0.1, 0, 0, r); g.addColorStop(0, '#5A5F6B'); g.addColorStop(1, '#16181D'); ctx.fillStyle = g; ctx.fill();
  ctx.beginPath(); ctx.ellipse(-r * 0.35, -r * 0.4, r * 0.22, r * 0.12, -0.6, 0, 7); ctx.fillStyle = 'rgba(255,255,255,.35)'; ctx.fill();
  ctx.fillStyle = '#3A3E47'; ctx.fillRect(-r * 0.25, -r * 1.15, r * 0.5, r * 0.3);
  // 심지 (남은 길이 = fuse 0~1)
  const pts = []; for (let k = 0; k <= 20; k++) { const u = k / 20; pts.push([r * 0.05 + Math.sin(u * 5) * r * 0.25 + u * r * 0.5, -r * 1.15 - u * r * 0.9]); }
  const m = Math.max(1, Math.round(fuse * 20));
  ctx.beginPath(); pts.slice(0, m + 1).forEach(([px, py], k) => (k ? ctx.lineTo(px, py) : ctx.moveTo(px, py))); ctx.strokeStyle = '#C8A26A'; ctx.lineWidth = r * 0.12; ctx.lineCap = 'round'; ctx.stroke();
  // 불꽃
  const [fx, fy] = pts[m];
  for (let k = 0; k < 8; k++) { const a = k * Math.PI / 4 + t * 20, l = r * (0.25 + 0.2 * Math.abs(Math.sin(t * 30 + k))); ctx.beginPath(); ctx.moveTo(fx, fy); ctx.lineTo(fx + Math.cos(a) * l, fy + Math.sin(a) * l); ctx.strokeStyle = k % 2 ? '#FFD54F' : '#FF7043'; ctx.lineWidth = r * 0.07; ctx.stroke(); }
  ctx.beginPath(); ctx.arc(fx, fy, r * 0.14 * (1 + 0.3 * Math.sin(t * 40)), 0, 7); ctx.fillStyle = '#FFF59D'; ctx.fill();
  ctx.restore();
}
/** 앞모습 늑대가 콱 무는 얼굴. bite: 0 닫힘 ~ 1 쩍 */
export function chompWolf(ctx, x, y, r, bite, pop = 1) {
  const FUR = '#9AA5B4', DARK = '#6E7A8A', CREAM = '#F4ECE1', INK = '#2B2B33';
  ctx.save(); ctx.translate(x, y); ctx.scale(pop, pop);
  for (const s of [-1, 1]) { ctx.beginPath(); ctx.moveTo(s * r * 0.25, -r * 0.7); ctx.lineTo(s * r * 0.85, -r * 1.35); ctx.lineTo(s * r * 0.95, -r * 0.35); ctx.closePath(); ctx.fillStyle = DARK; ctx.fill(); ctx.beginPath(); ctx.moveTo(s * r * 0.45, -r * 0.68); ctx.lineTo(s * r * 0.8, -r * 1.1); ctx.lineTo(s * r * 0.85, -r * 0.5); ctx.closePath(); ctx.fillStyle = '#F6AFC0'; ctx.fill(); }
  ctx.beginPath(); ctx.arc(0, 0, r, 0, 7); ctx.fillStyle = FUR; ctx.fill();
  // 화난 눈썹 + 눈
  for (const s of [-1, 1]) { ctx.beginPath(); ctx.arc(s * r * 0.4, -r * 0.2, r * 0.14, 0, 7); ctx.fillStyle = INK; ctx.fill(); ctx.beginPath(); ctx.arc(s * r * 0.36, -r * 0.25, r * 0.05, 0, 7); ctx.fillStyle = '#fff'; ctx.fill();
    ctx.beginPath(); ctx.moveTo(s * r * 0.6, -r * 0.48); ctx.lineTo(s * r * 0.2, -r * 0.36); ctx.strokeStyle = INK; ctx.lineWidth = r * 0.08; ctx.lineCap = 'round'; ctx.stroke(); }
  // 입 (위턱 / 아래턱)
  const o = r * 0.55 * bite;
  ctx.beginPath(); ctx.ellipse(0, r * 0.45, r * 0.62, r * 0.12 + o * 0.7, 0, 0, 7); ctx.fillStyle = '#7A1E2E'; ctx.fill();
  ctx.fillStyle = '#fff';
  for (let k = -3; k <= 3; k++) { const x0 = k * r * 0.16; ctx.beginPath(); ctx.moveTo(x0 - r * 0.07, r * 0.45 - r * 0.1 - o * 0.6); ctx.lineTo(x0 + r * 0.07, r * 0.45 - r * 0.1 - o * 0.6); ctx.lineTo(x0, r * 0.45 + r * 0.06 - o * 0.6); ctx.fill(); ctx.beginPath(); ctx.moveTo(x0 - r * 0.07, r * 0.45 + r * 0.1 + o * 0.6); ctx.lineTo(x0 + r * 0.07, r * 0.45 + r * 0.1 + o * 0.6); ctx.lineTo(x0, r * 0.45 - r * 0.06 + o * 0.6); ctx.fill(); }
  ctx.beginPath(); ctx.ellipse(0, r * 0.12 - o * 0.25, r * 0.17, r * 0.11, 0, 0, 7); ctx.fillStyle = INK; ctx.fill();
  ctx.restore();
}
export class Bomb {
  constructor(n, dir, holder, explodeAt) { this.n = n; this.dir = dir; this.holder = holder; this.explodeAt = explodeAt; this.fly = null; this.passes = 0; }
  press(i, t) { if (i !== this.holder || this.fly || t >= this.explodeAt) return; const to = this.dir === 'cw' ? (i + 1) % this.n : this.dir === 'ccw' ? (i - 1 + this.n) % this.n : this.randTo; this.fly = { from: i, to, at: t }; this.holder = to; this.passes++; }
  draw(ctx, W, H, t) {
    const secs = sectors(W, H, this.n), boom = t >= this.explodeAt, bt = t - this.explodeAt;
    if (this.fly && t - this.fly.at > 0.35) this.fly = null;
    secs.forEach((sec, i) => {
      const mine = i === this.holder && !this.fly;
      const fill = boom && i === this.holder ? (bt < 0.15 ? '#FFF3E0' : '#5A1A22') : mine ? '#4A3A1E' : '#1E1E2A';
      region(ctx, sec, fill, (c, s) => {
        pname(c, i, s);
        if (boom && i === this.holder) { if (bt > 0.6) badge(c, '💀 당첨!', 0, 70 * s, s, RED, '#fff'); }
        else if (mine && !boom) text(c, '터치해서 넘기기!', 0, 62 * s, 22 * s, '#FFD740', 900);
      }, boom && i === this.holder && bt > 0.6 ? RED : null);
    });
    lines(ctx, secs, W, H);
    hub(ctx, W, H, 34, '#121826', () => text(ctx, this.dir === 'cw' ? '↻' : this.dir === 'ccw' ? '↺' : '🔀', W / 2, H / 2 + 2, 30, '#FFD740', 900));
    // 폭탄 위치
    const pos = (k) => secs[k].anchor;
    const fuse = Math.max(0, 1 - t / this.explodeAt);
    if (!boom) {
      let x, y, sc = 1;
      if (this.fly) { const u = Math.min(1, (t - this.fly.at) / 0.35), [x0, y0] = pos(this.fly.from), [x1, y1] = pos(this.fly.to); x = x0 + (x1 - x0) * u; y = y0 + (y1 - y0) * u - Math.sin(u * Math.PI) * 60; sc = 1 + Math.sin(u * Math.PI) * 0.3; }
      else { [x, y] = pos(this.holder); x += Math.sin(t * 30) * 2 * (1 - fuse + 0.3); }
      bomb(ctx, x, y, 30 * sc, fuse, t);
    } else {
      const [x, y] = pos(this.holder), sec = secs[this.holder];
      if (bt < 0.6) { // 펑!
        const u = bt / 0.6; ctx.save(); ctx.globalAlpha = 1 - u;
        for (let k = 0; k < 14; k++) { const a = k * Math.PI * 2 / 14; ctx.beginPath(); ctx.moveTo(x, y); ctx.lineTo(x + Math.cos(a) * 150 * u + 20, y + Math.sin(a) * 150 * u); ctx.strokeStyle = k % 2 ? '#FFB300' : '#FF5722'; ctx.lineWidth = 10; ctx.stroke(); }
        ctx.beginPath(); ctx.arc(x, y, 30 + 110 * u, 0, 7); ctx.fillStyle = 'rgba(255,200,80,.6)'; ctx.fill(); ctx.restore();
        ctx.save(); ctx.translate(x, y); ctx.rotate(sec.rot); text(ctx, '펑!!', 0, -60, 46 * (0.6 + u), '#FF5722', 900); ctx.restore();
      }
      if (bt > 0.35) { // 늑대가 튀어나와 콱!
        const u = Math.min(1, (bt - 0.35) / 0.25), pop = u < 1 ? 1 - Math.pow(1 - u, 3) * Math.cos(u * 8) : 1;
        const ch = bt - 0.6, bite = ch < 0 ? 1 : Math.abs(Math.cos(Math.min(ch, 1.4) * Math.PI * 1.4)) * (ch > 1.4 ? 0 : 1);
        ctx.save(); ctx.translate(x, y); ctx.rotate(sec.rot); chompWolf(ctx, 0, -8, 46, bite, pop);
        if (ch > 0.1) text(ctx, '앙!', 56, -58, 28, '#FF3D5A', 900); ctx.restore();
      }
    }
  }
}
