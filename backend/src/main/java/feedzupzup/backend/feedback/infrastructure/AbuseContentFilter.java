package feedzupzup.backend.feedback.infrastructure;

import com.vane.badwordfiltering.BadWordFiltering;
import feedzupzup.backend.feedback.domain.service.moderation.ContentFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AbuseContentFilter implements ContentFilter {

    private static final String SUBSTITUTION_WORD = "OO";
    private static final String ASTERISK_PLACEHOLDER = "\u0000";

    @Override
    public String filter(final String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        String processedText = text.replace("*", ASTERISK_PLACEHOLDER);
        final BadWordFiltering filter = new BadWordFiltering();

        if (!filter.check(processedText)) {
            return processedText.replace(ASTERISK_PLACEHOLDER, "*");
        }

        String result = filter.change(processedText);
        while (result.contains("*")) {
            result = result.replaceFirst("\\*+", SUBSTITUTION_WORD);
        }
        String filteredResult = result.replace(ASTERISK_PLACEHOLDER, "*");

        log.info("욕설 필터링 수행 - Before: [{}], After: [{}]", text, filteredResult);

        return filteredResult;
    }
}
