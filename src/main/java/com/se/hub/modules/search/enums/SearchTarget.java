package com.se.hub.modules.search.enums;

import java.util.EnumSet;
import java.util.Set;

public enum SearchTarget {
    BLOG,
    EXAM,
    USER;

    public static Set<SearchTarget> defaultTargets() {
        return EnumSet.allOf(SearchTarget.class);
    }
}

