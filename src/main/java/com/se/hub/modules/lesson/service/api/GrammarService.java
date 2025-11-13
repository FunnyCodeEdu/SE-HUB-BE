package com.se.hub.modules.lesson.service.api;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.lesson.dto.request.CreateGrammarRequest;
import com.se.hub.modules.lesson.dto.request.UpdateGrammarRequest;
import com.se.hub.modules.lesson.dto.response.GrammarResponse;

public interface GrammarService extends BaseContentService{
    /**
     * create new grammar
     * @author catsocute
     */
    GrammarResponse createGrammar(CreateGrammarRequest request);

    /**
     * get grammar by id
     * @author catsocute
     */
    GrammarResponse getById(String grammarId);

    /**
     * get grammars by level
     * @author catsocute
     */
    PagingResponse<GrammarResponse> getGrammarsByLevel(String level, PagingRequest request);

    /**
     * get grammars
     * @author catsocute
     */
    PagingResponse<GrammarResponse> getGrammars(PagingRequest request);

    /**
     * update grammar by id
     * @author catsocute
     */
    GrammarResponse updateGrammarById(String grammarId, UpdateGrammarRequest request);

    /**
     * delete grammar by id
     * @author catsocute
     */
    void deleteGrammarById(String grammarId);
}
