import ActionButtons from './ActionButtons/ActionButtons';
import Content from './Content/Content';
import { container, cardContainer } from './OnBoarding.styles';

export default function OnBoarding() {
  return (
    <div css={container}>
      <div css={cardContainer}>
        <Content />
        <ActionButtons />
      </div>
    </div>
  );
}
