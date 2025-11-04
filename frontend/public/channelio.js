/* eslint-env browser */
/* global ChannelIO */
(function () {
  var w = window;
  if (w.ChannelIO) {
    return w.console.error('ChannelIO script included twice.');
  }
  var ch = function () {
    ch.c(arguments);
  };
  ch.q = [];
  ch.c = function (args) {
    ch.q.push(args);
  };
  w.ChannelIO = ch;
  function l() {
    if (w.ChannelIOInitialized) {
      return;
    }
    w.ChannelIOInitialized = true;
    var s = document.createElement('script');
    s.type = 'text/javascript';
    s.async = true;
    s.src = 'https://cdn.channel.io/plugin/ch-plugin-web.js';
    var x = document.getElementsByTagName('script')[0];
    if (x.parentNode) {
      x.parentNode.insertBefore(s, x);
    }
  }
  if (document.readyState === 'complete') {
    l();
  } else {
    w.addEventListener('DOMContentLoaded', l);
    w.addEventListener('load', l);
  }
})();

ChannelIO('boot', {
  pluginKey: 'e63d6484-428b-4745-9e39-54a8cb382f91',
  hideChannelButtonOnBoot: true,
});
