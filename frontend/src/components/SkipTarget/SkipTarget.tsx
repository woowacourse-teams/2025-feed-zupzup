export default function SkipTarget({ targetId }: { targetId: string }) {
  return (
    <a
      className='srOnly'
      href={`#${targetId}`}
      onClick={(e) => {
        e.preventDefault();
        const el = document.getElementById(targetId);
        if (el) {
          el.setAttribute('tabindex', '-1');
          el.focus({ preventScroll: true });
          el.scrollIntoView({ block: 'start', behavior: 'smooth' });
        } else {
          setTimeout(() => {
            const target = document.getElementById(targetId);
            target?.setAttribute('tabindex', '-1');
            target?.focus({ preventScroll: true });
            target?.scrollIntoView({ block: 'start', behavior: 'smooth' });
          }, 100);
        }
      }}
    >
      본문으로 건너뛰기
    </a>
  );
}
