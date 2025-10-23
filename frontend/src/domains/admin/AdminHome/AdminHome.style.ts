import { PAGE_PADDING_PX } from '@/constants';
import { Theme } from '@/theme';
import { css } from '@emotion/react';
import backgroundImageWebp from '@/assets/images/background.webp';
import backgroundImagePng from '@/assets/images/background.png';

export const homeLayout = css`
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
  gap: 36px;
  width: calc(100% + ${PAGE_PADDING_PX * 2}px);
  padding-top: 52px;
  background-image: url(${backgroundImagePng});
  background-image: image-set(
    url(${backgroundImageWebp}) type('image/webp'),
    url(${backgroundImagePng}) type('image/png')
  );
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  margin-top: calc(-${PAGE_PADDING_PX}px - 20px);
  margin-left: ${-PAGE_PADDING_PX}px;
  margin-right: ${-PAGE_PADDING_PX}px;
`;

export const adminOrganizationListContainer = (theme: Theme) => css`
  width: 100%;
  min-height: calc(100% - ${PAGE_PADDING_PX}px);
  margin-top: 160px;
  background-color: ${theme.colors.white[100]};
  padding: ${PAGE_PADDING_PX}px;
  border-top-left-radius: 24px;
  border-top-right-radius: 24px;

  @media (max-height: 700px) {
    margin-top: 100px;
    height: calc(100vh - 380px);
  }

  @media (max-height: 600px) {
    margin-top: 80px;
    height: calc(100vh - 330px);
  }
`;

export const infoContainer = css`
  display: flex;
  flex-direction: column;
  align-items: start;
  gap: 12px;
`;

export const listTitle = (theme: Theme) => css`
  ${theme.typography.pretendard.smallBold}
`;

export const listCaption = (theme: Theme) => css`
  margin-bottom: 16px;
  font-size: 24px;
  font-weight: 600;
  color: ${theme.colors.gray[500]};
  ${theme.typography.pretendard.caption}
`;

export const addAdminOrganization = (theme: Theme) => css`
  width: 60px;
  min-height: 60px;
  background-color: ${theme.colors.purple[100]};

  &:active {
    background-color: ${theme.colors.purple[100]}aa;
  }
`;
