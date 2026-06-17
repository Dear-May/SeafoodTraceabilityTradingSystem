package com.shopping_c_backend.common.sanitizer;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

/**
 * ?? HTML ??? ? ?? Controller/Service ???? PolicyFactory ????
 * ??, ????????????????
 */
@Component
public class HtmlSanitizer {

    /** ????: ????????? */
    private static final PolicyFactory DEFAULT_POLICY = new HtmlPolicyBuilder()
            .allowElements("b", "i", "em", "strong", "a")
            .allowUrlProtocols("http", "https")
            .toFactory();

    /** ????: ??????? */
    private static final PolicyFactory RELAXED_POLICY = new HtmlPolicyBuilder()
            .allowElements("b", "i", "em", "strong", "a", "p", "br", "ul", "ol", "li")
            .allowUrlProtocols("http", "https")
            .allowCommonInlineFormattingElements()
            .toFactory();

    /**
     * ????????
     */
    public String sanitize(String input) {
        if (input == null) {
            return "";
        }
        return DEFAULT_POLICY.sanitize(input);
    }

    /**
     * ????????
     */
    public String sanitizeRelaxed(String input) {
        if (input == null) {
            return "";
        }
        return RELAXED_POLICY.sanitize(input);
    }

    /**
     * ?????? (?? + ??)
     */
    public int parseIntSafe(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(sanitize(value.trim()));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * ?????? (?? + ??), ? Object ??
     */
    public int parseIntSafe(Object obj, int defaultValue) {
        return parseIntSafe(obj == null ? null : obj.toString(), defaultValue);
    }
}
