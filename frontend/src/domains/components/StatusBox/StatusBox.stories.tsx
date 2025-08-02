import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { css } from '@emotion/react';
import StatusBox from './StatusBox';

const meta: Meta<typeof StatusBox> = {
  title: 'Common/StatusBox',
  component: StatusBox,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    width: {
      control: { type: 'text' },
      description: '컨테이너의 너비',
    },
    height: {
      control: { type: 'number', min: 100, max: 400, step: 50 },
      description: '컨테이너의 높이',
    },
    textIcon: {
      control: 'text',
      description: '표시할 이모지/아이콘',
    },
    title: {
      control: 'text',
      description: '메인 제목',
    },
    description: {
      control: 'text',
      description: '설명 텍스트',
    },
  },
  decorators: [
    (Story) => (
      <div
        style={{
          width: '600px',
          height: '50vh',
          backgroundColor: '#f5f5f5',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '20px',
        }}
      >
        <Story />
      </div>
    ),
  ],
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    width: '100%',
    height: 200,
    textIcon: '📋',
    title: '기본 상태',
    description: 'StatusBox의 기본 형태입니다.',
  },
};

export const EmptyState: Story = {
  args: {
    width: '100%',
    height: 220,
    textIcon: '📝',
    title: '아직 내용이 없어요',
    description: '첫 번째 항목을 추가해보세요!',
  },
};

export const ErrorState: Story = {
  args: {
    width: '100%',
    height: 200,
    textIcon: '❌',
    title: '오류가 발생했어요',
    description: '다시 시도해주세요.',
  },
};

export const SuccessState: Story = {
  args: {
    width: '100%',
    height: 200,
    textIcon: '✅',
    title: '성공적으로 완료되었어요',
    description: '모든 작업이 정상적으로 처리되었습니다.',
  },
};

export const LoadingError: Story = {
  args: {
    width: '100%',
    height: 200,
    textIcon: '😅',
    title: '데이터를 불러올 수 없어요',
    description: '네트워크 연결을 확인해주세요.',
  },
};

export const NoResults: Story = {
  args: {
    width: '100%',
    height: 180,
    textIcon: '🔍',
    title: '검색 결과가 없어요',
    description: '다른 키워드로 검색해보세요.',
  },
};

export const WithCustomCSS: Story = {
  args: {
    width: '100%',
    height: 220,
    textIcon: '🎨',
    title: '커스텀 스타일',
    description: '특별한 디자인이 적용된 StatusBox입니다.',
    customCSS: css`
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: 2px solid #fff;
      border-radius: 20px;
      box-shadow: 0 8px 32px rgb(0 0 0 / 10%);

      /* 텍스트 색상도 변경 */
      div:nth-of-type(2),
      div:nth-of-type(3) {
        color: white !important;
      }
    `,
  },
};
