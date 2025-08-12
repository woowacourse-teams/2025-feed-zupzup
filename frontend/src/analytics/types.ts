export interface BaseEvent {
  name: string;
  parameters: {
    event_category: string;
    event_label?: string;
    value?: number;
    [key: string]: string | number | boolean | undefined;
  };
}

// export type CategoryType = '시설' | '행정' | '커리큘럼' | '기타';

export type CategoryType = '신고' | '질문' | '건의' | '기타';
