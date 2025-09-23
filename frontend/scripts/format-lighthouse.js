import fs from 'node:fs';
import path from 'node:path';
import process from 'node:process';
import * as core from '@actions/core';
import config from '../frontend/lighthouse.config.cjs';

const {
  getLhciPageNameFromUrl,
  LHCI_GREEN_MIN_SCORE,
  LHCI_ORANGE_MIN_SCORE,
  LHCI_RED_MIN_SCORE,
} = config;

function color(n) {
  return n >= LHCI_GREEN_MIN_SCORE
    ? '🟢'
    : n >= LHCI_ORANGE_MIN_SCORE
      ? '🟠'
      : '🔴';
}
function pct(v) {
  return Math.round(v * 100);
}
function fmtScore(n) {
  return `${color(n)}${n}`;
}
function fmtAudit(audit) {
  if (!audit || typeof audit.score !== 'number') return '—';
  const score100 = Math.round(audit.score * 100);
  return `${color(score100)}${audit.displayValue || ''}`.trim();
}

function loadJsons(dir) {
  return fs.existsSync(dir)
    ? fs
        .readdirSync(dir)
        .filter((f) => f.endsWith('.json'))
        .map((f) => path.join(dir, f))
        .map((file) => {
          const report = JSON.parse(fs.readFileSync(file, 'utf8'));
          return {
            url:
              report.finalUrl ||
              report.requestedUrl ||
              report.mainDocumentUrl ||
              '',
            report,
          };
        })
    : [];
}

export function formatLighthouse() {
  process.chdir('./frontend');

  const desktop = loadJsons('lighthouse-results');
  const mobile = loadJsons('lighthouse-results-mobile');

  const monitoringTime = new Date().toLocaleString('ko-KR', {
    timeZone: 'Asia/Seoul',
  });
  const legend = `> 🟢: ${LHCI_GREEN_MIN_SCORE}-100 / 🟠: ${LHCI_ORANGE_MIN_SCORE}-${LHCI_GREEN_MIN_SCORE - 1} / 🔴: ${LHCI_RED_MIN_SCORE}-${LHCI_ORANGE_MIN_SCORE - 1}`;

  let comments = `### Lighthouse report ✨\n${legend}\n\n`;
  const scores = { desktop: {}, mobile: {} };

  function addTable(results, deviceKey, title) {
    comments += `#### ${title}\n\n`;
    if (results.length === 0) {
      comments += `(결과 없음)\n\n`;
      return;
    }

    comments += `<table>\n<thead>\n<tr>\n`;
    comments += `<th>Page</th><th>Perf</th><th>A11y</th><th>Best</th><th>SEO</th><th>PWA</th>`;
    comments += `<th style="background-color: #f6f8fa;">FCP</th>`;
    comments += `<th style="background-color: #f6f8fa;">LCP</th>`;
    comments += `<th style="background-color: #f6f8fa;">Speed Index</th>`;
    comments += `<th style="background-color: #f6f8fa;">TBT</th>`;
    comments += `<th style="background-color: #f6f8fa;">CLS</th>`;
    comments += `</tr>\n</thead>\n<tbody>\n`;

    for (const { url, report } of results) {
      if (!url) continue;
      const pageUrl = url.replace(/^http:\/\/localhost:\d+/, '');
      const page = getLhciPageNameFromUrl(pageUrl);
      const { categories, audits } = report;

      const perf = pct(categories.performance.score);
      const a11y = pct(categories.accessibility.score);
      const best = pct(categories['best-practices'].score);
      const seo = pct(categories.seo.score);
      const pwa = categories.pwa ? pct(categories.pwa.score) : 0;

      const FCP = fmtAudit(audits['first-contentful-paint']);
      const LCP = fmtAudit(audits['largest-contentful-paint']);
      const SI = fmtAudit(audits['speed-index']);
      const TBT = fmtAudit(audits['total-blocking-time']);
      const CLS = fmtAudit(audits['cumulative-layout-shift']);

      comments += `<tr>\n`;
      comments += `<td>${page}</td>`;
      comments += `<td>${fmtScore(perf)}</td>`;
      comments += `<td>${fmtScore(a11y)}</td>`;
      comments += `<td>${fmtScore(best)}</td>`;
      comments += `<td>${fmtScore(seo)}</td>`;
      comments += `<td>${fmtScore(pwa)}</td>`;
      comments += `<td style="background-color: #f6f8fa;">${FCP}</td>`;
      comments += `<td style="background-color: #f6f8fa;">${LCP}</td>`;
      comments += `<td style="background-color: #f6f8fa;">${SI}</td>`;
      comments += `<td style="background-color: #f6f8fa;">${TBT}</td>`;
      comments += `<td style="background-color: #f6f8fa;">${CLS}</td>`;
      comments += `</tr>\n`;

      scores[deviceKey][page] = {
        Performance: fmtScore(perf),
        Accessibility: fmtScore(a11y),
        'Best Practices': fmtScore(best),
        SEO: fmtScore(seo),
        PWA: fmtScore(pwa),
        FCP,
        LCP,
        'Speed Index': SI,
        TBT,
        CLS,
      };
    }

    comments += `</tbody>\n</table>\n\n`;
  }

  addTable(desktop, 'desktop', 'Desktop');
  addTable(mobile, 'mobile', 'Mobile');

  core.setOutput('comments', comments);
  core.setOutput('monitoringTime', monitoringTime);
  core.setOutput('scores', scores);
}

// 실행 entrypoint
if (process.argv[1] && process.argv[1].endsWith('format-lighthouse.js')) {
  formatLighthouse();
}
