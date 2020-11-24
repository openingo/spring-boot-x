package org.openingo.spring.extension.data.elasticsearch.kit;

import org.openingo.jdkits.validate.ValidateKit;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * KeywordKit
 *
 * @author Qicz
 */
public final class KeywordKit {

    private KeywordKit(){}

    /**
     * keywords to keyword string with ' split
     * @param keywords keywords array
     * @return wrapper keyword
     */
    public static String toKeyword(String... keywords) {
        Assert.notNull(keywords, "the converting keywords can not be null");
        return Arrays.stream(keywords).filter(r -> !ValidateKit.isEmpty(r)).map(String::toLowerCase).collect(Collectors.joining(","));
    }

    /**
     * keywords to keyword string with ' split
     * @param keywords keywords array
     * @return wrapper keyword
     */
    public static String toKeyword(List<String> keywords) {
        Assert.notNull(keywords, "the converting keywords can not be null");
        return keywords.stream().filter(r -> !ValidateKit.isEmpty(r)).collect(Collectors.joining(","));
    }
}
