package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.endpoints.framework.payloads.FrameworkRequestDTO;
import kh.farrukh.progee_api.global.dto.ResourceStateDTO;
import kh.farrukh.progee_api.global.entity.ResourceState;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;

/**
 * A base interface for service of Framework endpoints
 *
 * Methods implemented in FrameworkServiceImpl
 */
public interface FrameworkService {

    PagingResponse<Framework> getFrameworks(
            Long languageId,
            ResourceState state,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    );

    Framework getFrameworkById(long id);

    Framework addFramework(FrameworkRequestDTO frameworkRequestDto);

    Framework updateFramework(long id, FrameworkRequestDTO frameworkRequestDto);

    void deleteFramework(long id);

    Framework setFrameworkState(long id, ResourceStateDTO resourceStateDto);
}