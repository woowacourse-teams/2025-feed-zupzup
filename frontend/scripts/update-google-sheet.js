// ESM script (package.json: "type":"module")
import { GoogleSpreadsheet } from 'google-spreadsheet';
import process from 'node:process';
// lighthouse.config.cjs 는 CJS지만 ESM에서 default import로 OK
import config from '../lighthouse.config.cjs';

const { LHCI_GOOGLE_SPREAD_SHEET_ID, getLhciSheetIdFromPageName } = config;

const scores = JSON.parse(process.env.LHCI_SCORES || '{}');
const monitoringTime = process.env.LHCI_MONITORING_TIME;
const prNumber = process.env.PR_NUMBER;
const repoOwner = process.env.REPO_OWNER;
const repoName = process.env.REPO_NAME;

async function updateGoogleSheet() {
  try {
    const creds = {
      client_email: process.env.LHCI_GOOGLE_CLIENT_EMAIL,
      private_key: (process.env.LHCI_GOOGLE_PRIVATE_KEY || '').replace(
        /\\n/g,
        '\n'
      ),
    };

    // 최소 유효성 확인
    if (
      !creds.client_email ||
      !creds.private_key ||
      !LHCI_GOOGLE_SPREAD_SHEET_ID
    ) {
      throw new Error('Missing Google credentials or Spreadsheet ID.');
    }

    const doc = new GoogleSpreadsheet(LHCI_GOOGLE_SPREAD_SHEET_ID);
    await doc.useServiceAccountAuth(creds);
    await doc.loadInfo();

    const { desktop = {}, mobile = {} } = scores;

    // 페이지별로 기록
    for (const pageName of Object.keys(desktop)) {
      const sheetId = getLhciSheetIdFromPageName(pageName);
      const sheet = doc.sheetsById[sheetId];

      if (!sheet) {
        console.warn(
          `⚠️  Sheet not found for pageName="${pageName}", sheetId=${sheetId}`
        );
        continue;
      }
      await sheet.loadHeaderRow();

      const desktopScores = desktop[pageName] || {};
      const mobileScores = mobile[pageName] || {};

      const prUrl = `https://github.com/${repoOwner}/${repoName}/pull/${prNumber}`;
      const prHyperlink = `=HYPERLINK("${prUrl}", "#${prNumber}")`;

      const base = { 'PR url': prHyperlink, 'Monitoring Time': monitoringTime };
      // 점수 채우기
      for (const k of Object.keys(desktopScores)) {
        base[`${k} [D]`] = desktopScores[k];
        base[`${k} [M]`] = mobileScores[k];
      }

      // 기존 PR row 있으면 업데이트, 없으면 추가
      const rows = await sheet.getRows();
      const existing = rows.find(
        (r) => r['PR url'] && String(r['PR url']).includes(`#${prNumber}`)
      );

      if (existing) {
        Object.assign(existing, base);
        await existing.save();
        console.log(`🔄 Updated: ${pageName} (PR #${prNumber})`);
      } else {
        await sheet.addRow(base);
        console.log(`➕ Added: ${pageName} (PR #${prNumber})`);
      }
    }

    console.log('✅ Google Sheet update finished');
  } catch (err) {
    console.error('❌ Failed to update Google Sheet:', err);
    process.exit(1);
  }
}

updateGoogleSheet();
