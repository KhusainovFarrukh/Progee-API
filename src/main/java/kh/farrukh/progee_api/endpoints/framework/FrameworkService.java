package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.endpoints.framework.payloads.FrameworkRequestDTO;
import kh.farrukh.progee_api.endpoints.framework.payloads.FrameworkResponseDTO;
import kh.farrukh.progee_api.global.dto.ResourceStateDTO;
import kh.farrukh.progee_api.global.entity.ResourceState;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;

/**
 * A base interface for service of Framework endpoints
 * <p>
 * Methods implemented in FrameworkServiceImpl
 */
public interface FrameworkService {

    PagingResponse<FrameworkResponseDTO> getFrameworks(
            Long languageId,
            ResourceState state,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    );

    FrameworkResponseDTO getFrameworkById(long id);

    FrameworkResponseDTO addFramework(FrameworkRequestDTO frameworkRequestDto);

    FrameworkResponseDTO updateFramework(long id, FrameworkRequestDTO frameworkRequestDto);

    void deleteFramework(long id);

    FrameworkResponseDTO setFrameworkState(long id, ResourceStateDTO resourceStateDto);
}