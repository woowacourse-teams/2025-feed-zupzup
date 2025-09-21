import { MessageSquare, Star } from 'lucide-react';
import { motion } from 'motion/react';
import {
  container,
  backgroundLayer,
  mainCard,
  iconWrapper,
  mainIcon,
  uiElementsContainer,
  uiElement,
  starRatingContainer,
  starIcon,
} from './IntuitiveInterfaceScene.styles';

export default function IntuitiveInterfaceScene() {
  return (
    <div css={container}>
      {[0, 1, 2].map((i) => (
        <motion.div
          key={i}
          css={backgroundLayer}
          style={{
            backgroundColor: `#7356ff${['08', '12', '20'][2 - i]}`,
            zIndex: i,
          }}
          animate={{
            scale: [1, 1 + i * 0.02, 1],
            rotate: [0, i * 1.5, 0],
            x: [0, i * 6, 0],
            y: [0, i * 3, 0],
          }}
          transition={{
            duration: 3.5,
            repeat: Infinity,
            ease: 'easeInOut',
            delay: i * 0.4,
          }}
        />
      ))}

      <motion.div
        css={mainCard}
        style={{
          zIndex: 3,
        }}
        animate={{
          scale: [1, 1.05, 1],
        }}
        transition={{
          duration: 2.5,
          repeat: Infinity,
          ease: 'easeInOut',
        }}
      >
        <div css={iconWrapper}>
          <motion.div
            animate={{
              scale: [1, 1.1, 1],
            }}
            transition={{
              duration: 2,
              repeat: Infinity,
              ease: 'easeInOut',
            }}
          >
            <MessageSquare css={mainIcon} />
          </motion.div>
        </div>

        {/* Simple UI Elements */}
        <div css={uiElementsContainer}>
          {[0, 1, 2].map((i) => (
            <motion.div
              key={i}
              css={uiElement}
              animate={{
                width: ['60%', '90%', '60%'],
                opacity: [0.3, 0.8, 0.3],
              }}
              transition={{
                duration: 2.5,
                repeat: Infinity,
                ease: 'easeInOut',
                delay: i * 0.3,
              }}
            />
          ))}
        </div>

        {/* Star Rating */}
        <div css={starRatingContainer}>
          {[0, 1, 2, 3, 4].map((i) => (
            <motion.div
              key={i}
              animate={{
                scale: [1, 1.3, 1],
                opacity: [0.5, 1, 0.5],
              }}
              transition={{
                duration: 1,
                repeat: Infinity,
                ease: 'easeInOut',
                delay: i * 0.1 + 1,
              }}
            >
              <Star css={starIcon} />
            </motion.div>
          ))}
        </div>
      </motion.div>
    </div>
  );
}
