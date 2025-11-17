package com.se.hub.modules.search.dto.request;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.modules.search.enums.SearchTarget;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchRequest {
    String keyword;
    PagingRequest pagingRequest;
    Set<SearchTarget> targets;
}

