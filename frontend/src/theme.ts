// styles/theme.ts
export const colors = {
  white: {
    100: '#ffffff',
    200: '#ffffff',
    300: '#f9fafb',
    400: '#f8f8f8',
  },

  black: {
    100: '#000000',
  },

  yellow: {
    100: '#fef3c6',
    200: '#fbd784',
  },

  brown: {
    100: '#973c00',
    200: '#624b47',
    300: '#4f4f4f',
    400: '#443536',
  },

  gray: {
    100: '#eeeeee',
    200: '#d9d9d9',
    300: '#c5c5c5',
    400: '#adadad',
    500: '#a0a0a0',
    600: '#7c7c7c',
  },

  green: {
    100: '#91c681',
    200: '#006045',
  },

  darkGray: {
    100: '#4a5565',
    200: '#364153',
    300: '#1e2939',
    400: '#101828',
  },

  red: {
    100: '#FB2C36',
  },

  purple: {
    100: '#7356FF',
  },
};

const fontStyle = 'Pretendard-Regular';
const headerFontStyle = 'Pretendard';

export const typography = {
  bmKkubulim: {
    fontFamily: 'BM kkubulim, sans-serif',
    fontSize: 150,
    lineHeight: 'auto',
    letterSpacing: 0,
  },
  bmHannaPro: {
    h1: {
      fontFamily: 'BM HANNA Pro OTF, sans-serif',
      fontSize: 90,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    bodyLarge: {
      fontFamily: 'BM HANNA Pro OTF, sans-serif',
      fontSize: 26,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    bodyRegular: {
      fontFamily: 'BM HANNA Pro OTF, sans-serif',
      fontSize: 22,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
  },
  inter: {
    h1: {
      fontFamily: `${headerFontStyle}, sans-serif`,
      fontWeight: 600,
      fontSize: 96,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    h2: {
      fontFamily: `${headerFontStyle}, sans-serif`,
      fontWeight: 600,
      fontSize: 48,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    h3: {
      fontFamily: `${headerFontStyle}, sans-serif`,
      fontWeight: 400,
      fontSize: 48,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    h4: {
      fontFamily: `${headerFontStyle}, sans-serif`,
      fontWeight: 600,
      fontSize: 32,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    h5: {
      fontFamily: `${headerFontStyle}, sans-serif`,
      fontWeight: 400,
      fontSize: 32,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    h6: {
      fontFamily: `${headerFontStyle}, sans-serif`,
      fontWeight: 400,
      fontSize: 26,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    bodyLarge: {
      fontFamily: `${headerFontStyle}, sans-serif`,
      fontWeight: 700,
      fontSize: 26,
      letterSpacing: 0,
    },
    bodyMedium: {
      fontFamily: `${headerFontStyle}, sans-serif`,
      fontWeight: 600,
      fontSize: 24,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    bodyRegular: {
      fontFamily: `${headerFontStyle}, sans-serif`,
      fontWeight: 400,
      fontSize: 22,
      letterSpacing: 0,
    },
    bodyBold: {
      fontFamily: `${headerFontStyle}, sans-serif`,
      fontWeight: 700,
      fontSize: 22,
      letterSpacing: 0,
    },
    small: {
      fontFamily: `${fontStyle}, sans-serif`,
      fontWeight: 400,
      fontSize: 19,
      letterSpacing: 0.6,
    },
    caption: {
      fontFamily: `${fontStyle}, sans-serif`,
      fontWeight: 400,
      fontSize: 16,
      letterSpacing: 0,
    },
  },
  BMHANNAAir: {
    caption: {
      fontFamily: 'BMHANNAAir, sans-serif',
      fontWeight: 400,
      fontSize: 16,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
  },
  BMHANNAPro: {
    small: {
      fontFamily: 'BMHANNAPro, sans-serif',
      fontWeight: 400,
      fontSize: 19,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    bodyBold: {
      fontFamily: 'BMHANNAPro, sans-serif',
      fontWeight: 700,
      fontSize: 22,
      letterSpacing: 0,
    },
  },
};

export const theme = {
  colors,
  typography,
};

export type Theme = typeof theme;
