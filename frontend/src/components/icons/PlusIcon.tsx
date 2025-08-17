interface PlusIconProps {
  color?: string;
  width?: string;
  height?: string;
}
export default function PlusIcon({
  color = 'black',
  width = '15',
  height = '14',
}: PlusIconProps) {
  return (
    <svg
      width={width}
      height={height}
      viewBox='0 0 15 14'
      fill='none'
      xmlns='http://www.w3.org/2000/svg'
    >
      <path
        d='M3.4165 7H11.5832'
        stroke={color}
        strokeWidth='1.16667'
        strokeLinecap='round'
        strokeLinejoin='round'
      />
      <path
        d='M7.5 2.9165V11.0832'
        stroke={color}
        strokeWidth='1.16667'
        strokeLinecap='round'
        strokeLinejoin='round'
      />
    </svg>
  );
}
