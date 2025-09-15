import { motion } from 'motion/react';
import { Bell } from 'lucide-react';
import {
  container,
  background,
  mainBellCenter,
  bellIcon,
  notificationBadge,
  badgeText,
} from './RealTimeNotificationScene.styles';

export default function RealTimeNotificationScene() {
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

      {/* Main Bell Center */}
      <motion.div
        css={mainBellCenter}
        animate={{
          scale: [1, 1.1, 1],
          rotate: [0, -5, 5, 0],
        }}
        transition={{
          duration: 2,
          repeat: Infinity,
          ease: 'easeInOut',
        }}
      >
        <motion.div
          animate={{
            rotate: [-10, 10, -10],
          }}
          transition={{
            duration: 1.5,
            repeat: Infinity,
            ease: 'easeInOut',
          }}
        >
          <Bell css={bellIcon} />
        </motion.div>
      </motion.div>

      {/* Notification Badge */}
      <motion.div
        css={notificationBadge}
        animate={{
          scale: [1, 1.3, 1],
        }}
        transition={{
          duration: 1,
          repeat: Infinity,
          ease: 'easeInOut',
        }}
      >
        <span css={badgeText}>3</span>
      </motion.div>

      {/* Pulse Rings - Simplified */}
      {/* {[0, 1].map((i) => (
        <motion.div
          key={i}
          css={pulseRing}
          style={{
            borderColor: `#7356ff${['30', '20'][i]}`,
            width: `${100 + i * 30}px`,
            height: `${100 + i * 30}px`,
          }}
          animate={{
            scale: [1, 1.4, 1],
            opacity: [0.8, 0, 0.8],
          }}
          transition={{
            duration: 2.5,
            repeat: Infinity,
            ease: 'easeOut',
            delay: i * 0.5,
          }}
        />
      ))} */}
    </div>
  );
}
