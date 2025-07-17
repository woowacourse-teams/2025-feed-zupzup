export interface StatusTagProps {
  children: React.ReactNode;
  type: StatusType;
}

export type StatusType = 'complete' | 'incomplete';
