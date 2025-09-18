import { motion } from 'motion/react';
import { BarChart3, Users, TrendingUp } from 'lucide-react';
import {
  container,
  background,
  mainChartContainer,
  chartIconWrapper,
  chartIcon,
  animatedBarsContainer,
  animatedBar,
  statsDisplay,
  statsIcon,
  statsText,
  floatingDataPoint,
} from './AnalyticsManagementScene.styles';

export default function AnalyticsManagementScene() {
  return (
    <div css={container}>
      {/* Background */}
      <motion.div
        css={background}
        animate={{
          scale: [1, 1.01, 1],
        }}
        transition={{
          duration: 4,
          repeat: Infinity,
          ease: 'easeInOut',
        }}
      />

      {/* Main Chart Container */}
      <motion.div
        css={mainChartContainer}
        animate={{
          scale: [1, 1.03, 1],
        }}
        transition={{
          duration: 3,
          repeat: Infinity,
          ease: 'easeInOut',
        }}
      >
        {/* Chart Icon */}
        <motion.div
          css={chartIconWrapper}
          animate={{
            scale: [1, 1.1, 1],
          }}
          transition={{
            duration: 2.5,
            repeat: Infinity,
            ease: 'easeInOut',
          }}
        >
          <BarChart3 css={chartIcon} />
        </motion.div>

        {/* Animated Bars */}
        <div css={animatedBarsContainer}>
          {[0, 1, 2, 3, 4].map((i) => (
            <motion.div
              key={i}
              css={animatedBar}
              animate={{
                height: [
                  `${12 + i * 4}px`,
                  `${20 + i * 6}px`,
                  `${12 + i * 4}px`,
                ],
              }}
              transition={{
                duration: 2,
                repeat: Infinity,
                ease: 'easeInOut',
                delay: i * 0.2,
              }}
            />
          ))}
        </div>

        {/* Stats Display */}
        <div css={statsDisplay}>
          <Users css={statsIcon} />
          <motion.span
            css={statsText}
            animate={{
              opacity: [0.7, 1, 0.7],
            }}
            transition={{
              duration: 2,
              repeat: Infinity,
              ease: 'easeInOut',
            }}
          >
            1,234
          </motion.span>
          <TrendingUp css={statsIcon} />
          <motion.span
            css={statsText}
            animate={{
              scale: [1, 1.1, 1],
            }}
            transition={{
              duration: 2,
              repeat: Infinity,
              ease: 'easeInOut',
              delay: 1,
            }}
          >
            +15%
          </motion.span>
        </div>
      </motion.div>

      {/* Floating Data Points - Simplified */}
      {[0, 1, 2].map((i) => (
        <motion.div
          key={i}
          css={floatingDataPoint}
          style={{
            backgroundColor: `#7356ff${['40', '60', '80'][i]}`,
            left: `${30 + i * 25}%`,
            bottom: `${15 + i * 5}%`,
          }}
          animate={{
            scale: [1, 1.5, 1],
            opacity: [0.5, 1, 0.5],
          }}
          transition={{
            duration: 2,
            repeat: Infinity,
            ease: 'easeInOut',
            delay: i * 0.4,
          }}
        />
      ))}
    </div>
  );
}
