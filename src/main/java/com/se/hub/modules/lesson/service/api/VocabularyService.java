package com.se.hub.modules.lesson.service.api;

import com.se.hub.common.dto.request.PagingRequest;
import com.se.hub.common.dto.response.PagingResponse;
import com.se.hub.modules.lesson.dto.request.CreateVocabularyRequest;
import com.se.hub.modules.lesson.dto.request.UpdateVocabularyRequest;
import com.se.hub.modules.lesson.dto.response.VocabularyResponse;

public interface VocabularyService extends BaseContentService {
    /**
     * create new vocabulary
     * @author catsocute
     */
    VocabularyResponse createVocabulary(CreateVocabularyRequest request);

    /**
     * get vocabulary by id
     * @author catsocute
     */
    VocabularyResponse getById(String vocabularyId);

    /**
     * get vocabularies by level
     * @author catsocute
     */
    PagingResponse<VocabularyResponse> getVocabulariesByLevel(String level, PagingRequest request);

    /**
     * get vocabularies by type
     * @author catsocute
     */
    PagingResponse<VocabularyResponse> getVocabulariesByType(String type, PagingRequest request);

    /**
     * get vocabularies
     * @author catsocute
     */
    PagingResponse<VocabularyResponse> getVocabularies(PagingRequest request);

    /**
     * update vocabulary by id
     * @author catsocute
     */
    VocabularyResponse updateVocabularyById(String vocabularyId, UpdateVocabularyRequest request);

    /**
     * delete vocabulary by id
     * @author catsocute
     */
    void deleteVocabularyById(String vocabularyId);
}
