export interface BaseEvent {
  name: string;
  parameters: {
    event_category: string;
    event_label?: string;
    value?: number;
    [key: string]: string | number | boolean | undefined;
  };
}

export type CategoryType = '시설' | '학사행정' | '커리큘럼' | '기타';
