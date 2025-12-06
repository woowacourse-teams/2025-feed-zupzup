import { GoogleSpreadsheet } from 'google-spreadsheet';
import { JWT } from 'google-auth-library';
import process from 'node:process';
import config from '../lighthouse.config.cjs';

const { LHCI_GOOGLE_SPREAD_SHEET_ID, getLhciSheetIdFromPageName } = config;

const scores = JSON.parse(process.env.LHCI_SCORES || '{}');
const monitoringTime = process.env.LHCI_MONITORING_TIME;
const prNumber = process.env.PR_NUMBER;
const repoOwner = process.env.REPO_OWNER;
const repoName = process.env.REPO_NAME;

async function updateGoogleSheet() {
  try {
    const serviceAccountAuth = new JWT({
      email: process.env.LHCI_GOOGLE_CLIENT_EMAIL,
      key: (process.env.LHCI_GOOGLE_PRIVATE_KEY || '').replace(/\\n/g, '\n'),
      scopes: [
        'https://www.googleapis.com/auth/spreadsheets',
        'https://www.googleapis.com/auth/drive.file',
      ],
    });

    const doc = new GoogleSpreadsheet(
      LHCI_GOOGLE_SPREAD_SHEET_ID,
      serviceAccountAuth
    );
    await doc.loadInfo();

    const { desktop = {}, mobile = {} } = scores;

    for (const pageName of Object.keys(desktop)) {
      const sheetId = getLhciSheetIdFromPageName(pageName);
      const sheet = doc.sheetsById[sheetId];
      if (!sheet) {
        continue;
      }
      await sheet.loadHeaderRow();

      const desktopScores = desktop[pageName] || {};
      const mobileScores = mobile[pageName] || {};

      const prUrl = `https://github.com/${repoOwner}/${repoName}/pull/${prNumber}`;
      const prHyperlink = `=HYPERLINK("${prUrl}", "#${prNumber}")`;

      const rowData = {
        'PR url': prHyperlink,
        'Monitoring Time': monitoringTime,
      };
      for (const k of Object.keys(desktopScores)) {
        rowData[`${k} [D]`] = desktopScores[k];
        rowData[`${k} [M]`] = mobileScores[k];
      }

      const rows = await sheet.getRows();
      const existing = rows.find(
        (r) => r['PR url'] && String(r['PR url']).includes(`#${prNumber}`)
      );

      if (existing) {
        Object.assign(existing, rowData);
        await existing.save();
      } else {
        await sheet.addRow(rowData);
      }
    }
  } catch {
    process.exit(1);
  }
}

updateGoogleSheet();
