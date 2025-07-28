import useGetFeedback from '@/domains/admin/home/hooks/useGetFeedback';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import CheerButton from '@/domains/user/dashboard/components/CheerButton/CheerButton';
import DashboardPanel from '@/domains/user/dashboard/components/DashboardPanel/DashboardPanel';
import UserFeedbackBox from '@/domains/user/dashboard/components/UserFeedbackBox/UserFeedbackBox';
import { useAppTheme } from '@/hooks/useAppTheme';
import useInfinityScroll from '@/hooks/useInfinityScroll';
import { Theme } from '@/theme';
import { FeedbackResponse, UserFeedback } from '@/types/feedback.types';
import { getLocalStorage } from '@/utils/localStorage';
import { css } from '@emotion/react';

const GROUP_NAME = '우아한테크코스';

const DASH_PANELS = [
  { title: '반영률', content: '40%', caption: '총 2개 반영' },
  { title: '완료', content: '2', caption: '평균 2.5일' },
  { title: '미처리', content: '1', caption: '반영 전' },
  { title: '총 건의 수', content: '3', caption: '반영 완료' },
];

const FEEDBACK_MOCK = [
  {
    userName: '김코딩',
    feedbackId: 1,
    status: 'COMPLETED',
    content: '다음에는 점심 메뉴를 다양하게 해주세요.',
    createdAt: '2024-06-01T10:00:00Z',
    isSecret: false,
    likeCount: 5,
  },
  {
    userName: '웨이브하다가허리부러진 웨이드',
    feedbackId: 2,
    status: 'WAITING',
    content: '회의실 예약 시스템이 불편해요.',
    createdAt: '2024-06-02T12:30:00Z',
    isSecret: true,
    likeCount: 2,
  },
  {
    userName: '웨이브하다가허리부러진 웨이드',
    feedbackId: 3,
    status: 'COMPLETED',
    content: '에어컨 온도를 조금 높여주세요.',
    createdAt: '2024-06-03T09:15:00Z',
    isSecret: false,
    likeCount: 3,
  },
];

export default function Dashboard() {
  const likedFeedbackIds = getLocalStorage<number[]>('feedbackIds') || [];

  const {
    items: feedbacks,
    fetchMore,
    hasNext,
    loading,
  } = useInfinityScroll<
    UserFeedback,
    'feedbacks',
    FeedbackResponse<UserFeedback>
  >({
    url: '/places/1/feedbacks',
    key: 'feedbacks',
  });

  const theme = useAppTheme();
  useGetFeedback({ fetchMore, hasNext, loading });

  return (
    <div css={dashboardLayout}>
      <p css={titleText(theme)}>{GROUP_NAME}</p>
      <div>
        <p css={panelCaption(theme)}>일주일 간의 피드백</p>
        <div css={panelLayout}>
          {DASH_PANELS.map((panel, idx) => (
            <DashboardPanel
              key={idx}
              title={panel.title}
              content={panel.content}
              caption={panel.caption}
            />
          ))}
        </div>
        <div css={cheerButtonLayout}>
          <CheerButton />
        </div>

        <FeedbackBoxList>
          {FEEDBACK_MOCK.map((feedback) => (
            <UserFeedbackBox
              userName={feedback.userName}
              key={feedback.feedbackId}
              type={feedback.status}
              content={feedback.content}
              createdAt={feedback.createdAt}
              isLiked={getFeedbackIsLike(likedFeedbackIds, feedback.feedbackId)}
              isSecret={feedback.isSecret}
              feedbackId={feedback.feedbackId}
              likeCount={feedback.likeCount}
              customCSS={css`
                display: flex;
              `}
            />
          ))}
          {loading && <div>로딩중...</div>}
        </FeedbackBoxList>
      </div>
    </div>
  );
}

function getFeedbackIsLike(likedFeedbackIds: number[], feedbackId: number) {
  const isLiked = likedFeedbackIds?.includes(feedbackId);

  return !!isLiked;
}

const dashboardLayout = css`
  display: flex;
  flex-direction: column;
  gap: 24px;
`;

const panelCaption = (theme: Theme) => css`
  margin-bottom: 8px;
  color: ${theme.colors.gray[600]};

  ${theme.typography.inter.caption}
`;

const panelLayout = css`
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(2, 1fr);
`;

const titleText = (theme: Theme) => css`
  ${theme.typography.bmHannaPro.bodyRegular};

  font-weight: 900;
`;

const cheerButtonLayout = css`
  display: flex;
  justify-content: flex-end;
  width: 100%;
  margin: 32px 0;
`;
