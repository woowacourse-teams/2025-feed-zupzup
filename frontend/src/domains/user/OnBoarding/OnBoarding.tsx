import BasicButton from '@/components/BasicButton/BasicButton';
import CategoryButton from '@/domains/components/CategoryButton/CategoryButton';
import useOrganizationName from '@/domains/hooks/useOrganizationName';
import {
  container,
  place,
  question,
  questionContainer,
  questionTitle,
  skipText,
  buttonContainer,
  title,
  skipIcon,
} from '@/domains/user/OnBoarding/OnBoarding.styles';
import { useAppTheme } from '@/hooks/useAppTheme';
import { Analytics, onboardingEvents } from '@/analytics';
import { useOrganizationId } from '@/domains/hooks/useOrganizationId';
import useNavigation from '@/domains/hooks/useNavigation';
import { createCategoryIconPairs } from '@/domains/utils/createCategoryList';
import { CategoryListType } from '@/constants/categoryList';

interface OnBoardingProps {
  onCategoryClick: (newCategory: CategoryListType) => void;
}

export default function OnBoarding({ onCategoryClick }: OnBoardingProps) {
  const theme = useAppTheme();
  const { goPath } = useNavigation();
  const { organizationId } = useOrganizationId();

  const { groupName, categories, isLoading } = useOrganizationName({
    organizationId,
  });

  const categoryIconPairs = createCategoryIconPairs(categories);

  const handleViewSuggestionsClick = () => {
    Analytics.track(onboardingEvents.viewSuggestionsFromOnboarding());

    goPath(`/${organizationId}/dashboard`);
  };

  return (
    <section css={container}>
      <div>
        <p css={title(theme)}>
          <span css={[place(theme), { opacity: isLoading ? 0 : 1 }]}>
            {groupName} <span css={title(theme)}>에</span>
          </span>
          <br /> 오신 것을 환영합니다
        </p>
        <div css={questionContainer(theme)}>
          <p css={questionTitle(theme)}>카테고리 선택</p>
          <p css={question(theme)}>건의하고 싶은 카테고리를 선택해주세요</p>
        </div>
        <div css={buttonContainer}>
          {categoryIconPairs.map((category) => (
            <CategoryButton
              key={category.category}
              icon={category.icon}
              text={category.category}
              onClick={() => onCategoryClick(category.category)}
            />
          ))}
        </div>
      </div>
      <BasicButton
        icon={<p css={skipIcon}>📄</p>}
        variant='secondary'
        onClick={handleViewSuggestionsClick}
      >
        <p css={skipText(theme)}>건의 목록 보러가기</p>
      </BasicButton>
    </section>
  );
}
