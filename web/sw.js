// 오프라인 지원: 한 번 열면 인터넷이 약해도 게임이 열림
// 파일을 바꾸면 VERSION 을 올려서 새 파일을 받게 함 (배포 시 자동으로 바뀜)
const VERSION = 'wg-__BUILD__';
const FILES = [
  './', 'index.html', 'style.css', 'app.js', 'core.js', 'i18n.js',
  'games-dice.js', 'games-pick.js', 'games-fun.js', 'games-four.js', 'manual.html', 'manual-i18n.js', 'manifest.webmanifest',
  'icons/logo.png', 'icons/icon-192.png', 'icons/icon-512.png', 'icons/apple-touch-icon.png', 'icons/favicon.png',
];

self.addEventListener('install', (e) => {
  e.waitUntil(caches.open(VERSION).then((c) => c.addAll(FILES)).then(() => self.skipWaiting()));
});
self.addEventListener('activate', (e) => {
  e.waitUntil(caches.keys().then((keys) => Promise.all(keys.filter((k) => k !== VERSION).map((k) => caches.delete(k)))).then(() => self.clients.claim()));
});
// 인터넷이 되면 새 파일, 안 되면 저장해 둔 파일
self.addEventListener('fetch', (e) => {
  if (e.request.method !== 'GET') return;
  e.respondWith(
    fetch(e.request).then((res) => {
      const copy = res.clone();
      caches.open(VERSION).then((c) => c.put(e.request, copy)).catch(() => {});
      return res;
    }).catch(() => caches.match(e.request).then((r) => r || caches.match('index.html'))),
  );
});
