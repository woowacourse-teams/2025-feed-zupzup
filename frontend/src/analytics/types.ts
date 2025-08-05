export interface BaseEvent {
  name: string;
  parameters: {
    event_category: string;
    event_label?: string;
    value?: number;
    [key: string]: string | number | boolean | undefined;
  };
}

export interface GtagEventParameters {
  event_category?: string;
  event_label?: string;
  value?: number;
  custom_parameter?: string | number | boolean;
}
