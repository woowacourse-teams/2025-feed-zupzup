import { css } from '@emotion/react';

export default function Header() {
  return <header css={header}>header</header>;
}

const header = css`
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 64px;
  background-color: white;
`;
