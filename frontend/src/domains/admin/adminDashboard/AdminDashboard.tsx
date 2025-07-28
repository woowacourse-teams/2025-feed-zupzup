import {
  cheerButtonLayout,
  customCSSexample,
  dashboardLayout,
  panelCaption,
  panelLayout,
  titleText,
} from '@/domains/admin/adminDashboard/adminDashboard.style';
import FeedbackBoxList from '@/domains/components/FeedbackBoxList/FeedbackBoxList';
import CheerButton from '@/domains/user/userDashboard/components/CheerButton/CheerButton';
import DashboardPanel from '@/domains/user/userDashboard/components/DashboardPanel/DashboardPanel';
import UserFeedbackBox from '@/domains/user/userDashboard/components/UserFeedbackBox/UserFeedbackBox';

import { DASH_PANELS } from '@/domains/user/userDashboard/mocks/dashPanels.mock';
import { FEEDBACK_MOCK } from '@/domains/user/userDashboard/mocks/userFeedback.mock';
import { useAppTheme } from '@/hooks/useAppTheme';
import { FeedbackStatusType } from '@/types/feedbackStatus.types';
import { getLocalStorage } from '@/utils/localStorage';

const GROUP_NAME = '우아한테크코스';

export default function AdminDashboard() {
  const likedFeedbackIds = getLocalStorage<number[]>('feedbackIds') || [];
  const theme = useAppTheme();

  // const {
  //   items: feedbacks,
  //   fetchMore,
  //   hasNext,
  //   loading,
  // } = useInfinityScroll<
  //   UserFeedback,
  //   'feedbacks',
  //   FeedbackResponse<UserFeedback>
  // >({
  //   url: '/places/1/feedbacks',
  //   key: 'feedbacks',
  // });

  // useGetFeedback({ fetchMore, hasNext, loading });

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
              type={feedback.status as FeedbackStatusType}
              content={feedback.content}
              createdAt={feedback.createdAt}
              isLiked={getFeedbackIsLike(likedFeedbackIds, feedback.feedbackId)}
              isSecret={feedback.isSecret}
              feedbackId={feedback.feedbackId}
              likeCount={feedback.likeCount}
              customCSS={customCSSexample}
            />
          ))}
          {/* {loading && <div>로딩중...</div>} */}
        </FeedbackBoxList>
      </div>
    </div>
  );
}

function getFeedbackIsLike(likedFeedbackIds: number[], feedbackId: number) {
  const isLiked = likedFeedbackIds?.includes(feedbackId);

  return !!isLiked;
}
