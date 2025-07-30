export default function ArrowLeftIcon({
  width,
  height,
  strokeWidth,
}: {
  width: number;
  height: number;
  strokeWidth: number;
}) {
  return (
    <svg
      width={width}
      height={height}
      viewBox='0 0 14 14'
      fill='none'
      xmlns='http://www.w3.org/2000/svg'
    >
      <path
        d='M6.99984 11.5832L2.9165 7.49984L6.99984 3.4165'
        stroke='#4A5565'
        strokeWidth={strokeWidth}
        strokeLinecap='round'
        strokeLinejoin='round'
      />
      <path
        d='M11.0832 7.5H2.9165'
        stroke='#4A5565'
        strokeWidth={strokeWidth}
        strokeLinecap='round'
        strokeLinejoin='round'
      />
    </svg>
  );
}
