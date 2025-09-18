import { motion } from 'motion/react';
import { QrCode, Link } from 'lucide-react';
import {
  container,
  backgroundCard,
  headerSection,
  headerIcon,
  headerIconDot,
  mainContent,
  qrCard,
  urlCard,
  iconWrapper,
  scanEffect,
} from './QRFeedbackScene.styles';

export default function QRFeedbackScene() {
  return (
    <div css={container}>
      {/* Background Card */}
      <motion.div
        css={backgroundCard}
        animate={{
          scale: [1, 1.01, 1],
        }}
        transition={{
          duration: 4,
          repeat: Infinity,
          ease: 'easeInOut',
        }}
      >
        {/* Header Section */}
        <div css={headerSection}>
          <motion.div
            css={headerIcon}
            animate={{
              rotate: [0, 360],
            }}
            transition={{
              duration: 8,
              repeat: Infinity,
              ease: 'linear',
            }}
          >
            <div css={headerIconDot} />
          </motion.div>
        </div>

        {/* Main Content - Two Cards Side by Side */}
        <div css={mainContent}>
          {/* QR Code Card */}
          <motion.div
            css={qrCard}
            animate={{
              y: [0, -4, 0],
              scale: [1, 1.02, 1],
            }}
            transition={{
              duration: 3,
              repeat: Infinity,
              ease: 'easeInOut',
            }}
          >
            <motion.div
              animate={{
                scale: [1, 1.1, 1],
                rotate: [0, -2, 2, 0],
              }}
              transition={{
                duration: 2.5,
                repeat: Infinity,
                ease: 'easeInOut',
              }}
            >
              <QrCode css={iconWrapper} />
            </motion.div>

            {/* Scan Effect */}
            <motion.div
              css={scanEffect}
              animate={{
                x: [-100, 100],
                opacity: [0, 0.8, 0],
              }}
              transition={{
                duration: 3,
                repeat: Infinity,
                ease: 'easeInOut',
                delay: 1,
              }}
            />
          </motion.div>

          {/* URL Card */}
          <motion.div
            css={urlCard}
            animate={{
              y: [0, -6, 0],
              scale: [1, 1.03, 1],
            }}
            transition={{
              duration: 2.8,
              repeat: Infinity,
              ease: 'easeInOut',
              delay: 0.5,
            }}
          >
            <motion.div
              animate={{
                scale: [1, 1.15, 1],
                rotate: [0, 5, -5, 0],
              }}
              transition={{
                duration: 2.2,
                repeat: Infinity,
                ease: 'easeInOut',
                delay: 0.3,
              }}
            >
              <Link css={iconWrapper} />
            </motion.div>
          </motion.div>
        </div>
      </motion.div>
    </div>
  );
}
