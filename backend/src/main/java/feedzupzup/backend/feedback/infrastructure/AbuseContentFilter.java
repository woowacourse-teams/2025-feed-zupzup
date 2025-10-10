package feedzupzup.backend.feedback.infrastructure;

import com.vane.badwordfiltering.BadWordFiltering;
import feedzupzup.backend.feedback.domain.service.moderation.ContentFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AbuseContentFilter implements ContentFilter {

    private static final String[] SUBSTITUTION_WORDS = {"멍멍", "야옹", "음메"};
    private static final String ASTERISK_PLACEHOLDER = "\u0000";
    private static final BadWordFiltering FILTER = new BadWordFiltering();

    @Override
    public String filter(final String text) {
        String processedText = text.replace("*", ASTERISK_PLACEHOLDER);

        if (!FILTER.check(processedText)) {
            return processedText.replace(ASTERISK_PLACEHOLDER, "*");
        }

        String result = FILTER.change(processedText);
        int index = 0;
        while (result.contains("*")) {
            result = result.replaceFirst("\\*+", SUBSTITUTION_WORDS[index++ % SUBSTITUTION_WORDS.length]);
        }
        String filteredResult = result.replace(ASTERISK_PLACEHOLDER, "*");

        log.info("욕설 필터링 수행 - Before: [{}], After: [{}]", text, filteredResult);

        return filteredResult;
    }
}
