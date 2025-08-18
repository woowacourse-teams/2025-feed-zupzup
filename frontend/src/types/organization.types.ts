import { CategoryListType } from '@/constants/categoryList';

export interface GetOrganizationName {
  data: {
    organizationName: string;
    totalCheeringCount: number;
    categories: CategoryListType[];
  };
  status: number;
  message: string;
}

export interface GetOrganizationStatistics {
  data: {
    reflectionRate: string;
    confirmedCount: string;
    waitingCount: string;
    totalCount: string;
  };
  status: number;
  message: string;
}
