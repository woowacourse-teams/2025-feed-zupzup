import { VirtualItem } from '@tanstack/react-virtual';
import { ReactNode } from 'react';

interface FeedbackItemProps {
  measureElement: (node: HTMLElement | null) => void;
  virtualRow: VirtualItem;
  children: ReactNode;
}

function VirtualFeedbackItem({
  children,
  measureElement,
  virtualRow,
}: FeedbackItemProps) {
  return (
    <div
      key={virtualRow.key}
      data-index={virtualRow.index}
      ref={measureElement}
      style={{
        position: 'absolute',
        top: 0,
        left: 0,
        width: '100%',
        transform: `translateY(${virtualRow.start}px)`,
      }}
    >
      {children}
    </div>
  );
}

export default VirtualFeedbackItem;
