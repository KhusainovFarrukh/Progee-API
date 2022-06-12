package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.base.dto.ResourceStateDTO;
import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;

public interface FrameworkService {

    PagingResponse<Framework> getFrameworksByLanguage(
            long languageId,
            ResourceState state,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    );

    Framework getFrameworkById(long languageId, long id);

    Framework addFramework(long languageId, FrameworkDTO frameworkDto);

    Framework updateFramework(long languageId, long id, FrameworkDTO frameworkDto);

    void deleteFramework(long languageId, long id);

    Framework setFrameworkState(long languageId, long id, ResourceStateDTO resourceStateDto);
}