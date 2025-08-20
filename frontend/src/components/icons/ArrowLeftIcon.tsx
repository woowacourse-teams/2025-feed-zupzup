export default function ArrowLeftIcon({ color }: { color: string }) {
  return (
    <svg
      width={30}
      height={30}
      viewBox='0 0 14 14'
      fill='none'
      xmlns='http://www.w3.org/2000/svg'
    >
      <path
        d='M6.99984 11.5832L2.9165 7.49984L6.99984 3.4165'
        stroke={color}
        strokeWidth={0.5}
        strokeLinecap='round'
        strokeLinejoin='round'
      />
      <path
        d='M11.0832 7.5H2.9165'
        stroke={color}
        strokeWidth={0.5}
        strokeLinecap='round'
        strokeLinejoin='round'
      />
    </svg>
  );
}
