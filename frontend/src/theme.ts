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
};

export const typography = {
  bmKkubulim: {
    fontFamily: 'BM kkubulim, sans-serif',
    fontSize: 150,
    lineHeight: 'auto',
    letterSpacing: 0,
  },
  bmHannaPro: {
    fontFamily: 'BM HANNA Pro OTF, sans-serif',
    fontSize: 90,
    lineHeight: 'auto',
    letterSpacing: 0,
  },

  inter: {
    h1: {
      fontFamily: 'Inter, sans-serif',
      fontWeight: 600,
      fontSize: 60,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    h2: {
      fontFamily: 'Inter, sans-serif',
      fontWeight: 600,
      fontSize: 30,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    h3: {
      fontFamily: 'Inter, sans-serif',
      fontWeight: 400,
      fontSize: 30,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    h4: {
      fontFamily: 'Inter, sans-serif',
      fontWeight: 600,
      fontSize: 20,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    h5: {
      fontFamily: 'Inter, sans-serif',
      fontWeight: 400,
      fontSize: 20,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    h6: {
      fontFamily: 'Inter, sans-serif',
      fontWeight: 400,
      fontSize: 16,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    bodyLarge: {
      fontFamily: 'Inter, sans-serif',
      fontWeight: 700,
      fontSize: 16,
      lineHeight: 25,
      letterSpacing: 0,
    },
    bodyMedium: {
      fontFamily: 'Inter, sans-serif',
      fontWeight: 600,
      fontSize: 15,
      lineHeight: 'auto',
      letterSpacing: 0,
    },
    bodyRegular: {
      fontFamily: 'Inter, sans-serif',
      fontWeight: 400,
      fontSize: 14,
      lineHeight: 21,
      letterSpacing: 0,
    },
    bodyBold: {
      fontFamily: 'Inter, sans-serif',
      fontWeight: 700,
      fontSize: 14,
      lineHeight: 21,
      letterSpacing: 0,
    },
    small: {
      fontFamily: 'Inter, sans-serif',
      fontWeight: 400,
      fontSize: 12,
      lineHeight: 18,
      letterSpacing: 0.6,
    },
    caption: {
      fontFamily: 'Inter, sans-serif',
      fontWeight: 400,
      fontSize: 10,
      lineHeight: 14,
      letterSpacing: 0,
    },
  },
};

export const theme = {
  colors,
  typography,
};

export type Theme = typeof theme;
