// styles/theme.ts
const colors = {
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
    200: '#00A63E',
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

const typography = {
  pretendard: {
    h1: {
      fontFamily: 'Pretendard, sans-serif',
      fontWeight: 600,
      fontSize: 30,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    h2: {
      fontFamily: 'Pretendard, sans-serif',
      fontWeight: 400,
      fontSize: 26,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    bodyRegular: {
      fontFamily: 'Pretendard, sans-serif',
      fontWeight: 400,
      fontSize: 22,
      letterSpacing: 0,
    },
    bodyBold: {
      fontFamily: 'Pretendard, sans-serif',
      fontWeight: 700,
      fontSize: 22,
      letterSpacing: 0,
    },
    small: {
      fontFamily: 'Pretendard, sans-serif',
      fontWeight: 400,
      fontSize: 19,
      letterSpacing: 0.6,
    },
    smallBold: {
      fontFamily: 'Pretendard, sans-serif',
      fontWeight: 600,
      fontSize: 19,
      letterSpacing: 0.6,
    },
    caption: {
      fontFamily: 'Pretendard, sans-serif',
      fontWeight: 400,
      fontSize: 16,
      letterSpacing: 0,
    },
    captionSmall: {
      fontFamily: 'Pretendard, sans-serif',
      fontWeight: 400,
      fontSize: 14,
      letterSpacing: 0,
    },
  },
  BMHANNAAir: {
    bodyBold: {
      fontFamily: 'BMHANNAAir, sans-serif',
      fontWeight: 700,
      fontSize: 22,
      letterSpacing: 0,
    },
    caption: {
      fontFamily: 'BMHANNAAir, sans-serif',
      fontWeight: 400,
      fontSize: 16,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
  },
  BMHANNAPro: {
    h2: {
      fontFamily: 'BMHANNAPro, sans-serif',
      fontSize: 26,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    bodyBold: {
      fontFamily: 'BMHANNAPro, sans-serif',
      fontWeight: 700,
      fontSize: 22,
      letterSpacing: 0,
    },
    small: {
      fontFamily: 'BMHANNAPro, sans-serif',
      fontWeight: 400,
      fontSize: 19,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    caption: {
      fontFamily: 'BMHANNAPro, sans-serif',
      fontWeight: 400,
      fontSize: 16,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
  },
};

export const theme = {
  colors,
  typography,
};

export type Theme = typeof theme;
