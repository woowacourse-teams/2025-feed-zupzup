import { Helmet } from 'react-helmet-async';

interface SEOProps {
  title?: string;
  description?: string;
  keywords?: string;
  ogImage?: string;
  ogUrl?: string;
}

export const SEO = ({
  title = '피드줍줍',
  description = '피드백을 쉽고 빠르게 수집하고 관리하세요',
  keywords = '피드백, 의견수집, 설문, feedback',
  ogImage = '/og/og-default-1200x630.png',
  ogUrl = window.location.href,
}: SEOProps) => {
  // "로그인" → "로그인 | 피드줍줍"
  const fullTitle = title === '피드줍줍' ? title : `${title} | 피드줍줍`;

  return (
    <Helmet>
      {/* 브라우저 탭 제목 */}
      <title>{fullTitle}</title>

      {/* 검색 엔진용 */}
      <meta name='description' content={description} />
      <meta name='keywords' content={keywords} />

      {/* 카카오톡/페이스북 공유용 */}
      <meta property='og:title' content={fullTitle} />
      <meta property='og:description' content={description} />
      <meta property='og:image' content={ogImage} />
      <meta property='og:url' content={ogUrl} />

      {/* 트위터 공유용 */}
      <meta name='twitter:title' content={fullTitle} />
      <meta name='twitter:description' content={description} />
      <meta name='twitter:image' content={ogImage} />
    </Helmet>
  );
};
