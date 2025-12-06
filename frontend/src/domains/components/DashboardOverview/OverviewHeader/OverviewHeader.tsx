import { useAppTheme } from '@/hooks/useAppTheme';
import useCheerButton from '@/domains/hooks/useCheerButton';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import {
  headerContainer,
  headerText,
  headerCheerButton,
  cheerButtonLayout,
  titleText,
  panelCaption,
} from './OverviewHeader.styles';
import useOrganizationName from '@/domains/hooks/useOrganizationName';
import CheerButton from '../../CheerButton/CheerButton';
import RefreshButton from '@/domains/components/RefreshButton/RefreshButton';

interface OverviewHeaderProps {
  feedbackDiff: number;
  handleRefresh: () => void;
}

export default function OverviewHeader({
  feedbackDiff,
  handleRefresh,
}: OverviewHeaderProps) {
  const theme = useAppTheme();
  const { organizationId } = useOrganizationId();
  const { groupName, totalCheeringCount } = useOrganizationName({
    organizationId,
  });

  const { handleCheerButton, animate, isDisabled } = useCheerButton({
    organizationId,
  });

  const hasNewFeedback = feedbackDiff > 0;

  return (
    <div css={headerContainer}>
      <div css={headerText}>
        <p css={titleText(theme)}>{groupName}</p>
        <p css={panelCaption(theme)}>지금까지의 피드백</p>
      </div>
      <div css={headerCheerButton}>
        <div css={cheerButtonLayout}>
          {hasNewFeedback && (
            <RefreshButton
              handleRefresh={handleRefresh}
              feedbackDiff={feedbackDiff}
            />
          )}
          <CheerButton
            disabled={isDisabled}
            totalCheeringCount={totalCheeringCount}
            onClick={handleCheerButton}
            animate={animate}
          />
        </div>
      </div>
    </div>
  );
}
