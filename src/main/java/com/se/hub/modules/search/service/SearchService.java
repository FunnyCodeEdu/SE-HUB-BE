package com.se.hub.modules.search.service;

import com.se.hub.modules.search.dto.request.SearchRequest;
import com.se.hub.modules.search.dto.response.SearchResponse;

public interface SearchService {
    SearchResponse search(SearchRequest request);
}



