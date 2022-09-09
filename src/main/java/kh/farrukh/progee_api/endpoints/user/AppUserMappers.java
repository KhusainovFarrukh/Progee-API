package kh.farrukh.progee_api.endpoints.user;

import kh.farrukh.progee_api.endpoints.user.payloads.AppUserResponseDTO;
import org.springframework.beans.BeanUtils;

public class AppUserMappers {

    public static AppUserResponseDTO toAppUserResponseDTO(AppUser appUser) {
        if (appUser == null) return null;
        AppUserResponseDTO appUserResponseDTO = new AppUserResponseDTO();
        BeanUtils.copyProperties(appUser, appUserResponseDTO);
        return appUserResponseDTO;
    }
}
