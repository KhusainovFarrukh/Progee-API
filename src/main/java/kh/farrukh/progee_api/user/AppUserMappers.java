package kh.farrukh.progee_api.user;

import kh.farrukh.progee_api.image.ImageMappers;
import kh.farrukh.progee_api.role.RoleMappers;
import kh.farrukh.progee_api.user.payloads.AppUserResponseDTO;
import org.springframework.beans.BeanUtils;

public class AppUserMappers {

    public static AppUserResponseDTO toAppUserResponseDTO(AppUser appUser) {
        if (appUser == null) return null;
        AppUserResponseDTO appUserResponseDTO = new AppUserResponseDTO();
        BeanUtils.copyProperties(appUser, appUserResponseDTO);
        appUserResponseDTO.setImage(ImageMappers.toImageResponseDto(appUser.getImage()));
        appUserResponseDTO.setRole(RoleMappers.toRoleResponseDTO(appUser.getRole()));
        return appUserResponseDTO;
    }

    public static AppUser toAppUser(AppUserResponseDTO appUserResponseDTO) {
        if (appUserResponseDTO == null) return null;
        AppUser appUser = new AppUser();
        BeanUtils.copyProperties(appUserResponseDTO, appUser);
        appUser.setImage(ImageMappers.toImage(appUserResponseDTO.getImage()));
        appUser.setRole(RoleMappers.toRole(appUserResponseDTO.getRole()));
        return appUser;
    }
}
