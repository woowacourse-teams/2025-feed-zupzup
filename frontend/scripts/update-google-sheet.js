const fs = require('fs');
const path = require('path');
const { GoogleSpreadsheet } = require('google-spreadsheet');
const {
  LHCI_GOOGLE_SPREAD_SHEET_ID,
  getLhciSheetIdFromPageName,
} = require('../lighthouse.config.cjs');

// GitHub Actions에서 전달받은 값
const scores = JSON.parse(process.env.LHCI_SCORES || '{}');
const monitoringTime = process.env.LHCI_MONITORING_TIME;
const prNumber = process.env.PR_NUMBER;
const repoOwner = process.env.REPO_OWNER;
const repoName = process.env.REPO_NAME;

async function updateGoogleSheet() {
  try {
    const creds = {
      client_email: process.env.LHCI_GOOGLE_CLIENT_EMAIL,
      private_key: process.env.LHCI_GOOGLE_PRIVATE_KEY.replace(/\\n/g, '\n'),
    };

    const doc = new GoogleSpreadsheet(LHCI_GOOGLE_SPREAD_SHEET_ID);
    await doc.useServiceAccountAuth(creds);
    await doc.loadInfo();

    const { desktop = {}, mobile = {} } = scores;

    for (const pageName of Object.keys(desktop)) {
      const sheetId = getLhciSheetIdFromPageName(pageName);
      const sheet = doc.sheetsById[sheetId];
      if (!sheet) {
        console.warn(`⚠️  Sheet not found for pageName: ${pageName}`);
        continue;
      }
      await sheet.loadHeaderRow();

      const desktopScores = desktop[pageName] || {};
      const mobileScores = mobile[pageName] || {};

      const prUrl = `https://github.com/${repoOwner}/${repoName}/pull/${prNumber}`;
      const prHyperlink = `=HYPERLINK("${prUrl}", "#${prNumber}")`;

      const newRow = {
        'PR url': prHyperlink,
        'Monitoring Time': monitoringTime,
      };

      // 점수 데이터 채우기
      Object.keys(desktopScores).forEach((key) => {
        newRow[`${key} [D]`] = desktopScores[key];
        newRow[`${key} [M]`] = mobileScores[key];
      });

      // 기존 행 찾기
      const rows = await sheet.getRows();
      const existing = rows.find(
        (row) => row['PR url'] && row['PR url'].includes(`#${prNumber}`)
      );

      if (existing) {
        Object.assign(existing, newRow);
        await existing.save();
        console.log(`🔄 Updated row for pageName=${pageName}, PR=#${prNumber}`);
      } else {
        await sheet.addRow(newRow);
        console.log(`➕ Added row for pageName=${pageName}, PR=#${prNumber}`);
      }
    }

    console.log('✅ Google Sheet update finished');
  } catch (err) {
    console.error('❌ Failed to update Google Sheet:', err);
    process.exit(1);
  }
}

updateGoogleSheet();
