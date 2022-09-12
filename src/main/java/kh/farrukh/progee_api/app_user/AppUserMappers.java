package kh.farrukh.progee_api.app_user;

import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.image.ImageMappers;
import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.role.RoleMappers;
import kh.farrukh.progee_api.role.RoleRepository;
import kh.farrukh.progee_api.app_user.payloads.AppUserRequestDTO;
import kh.farrukh.progee_api.app_user.payloads.AppUserResponseDTO;
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

    public static AppUser toAppUser(
            AppUserRequestDTO appUserRequestDTO,
            RoleRepository roleRepository,
            ImageRepository imageRepository
    ) {
        if (appUserRequestDTO == null) return null;
        AppUser appUser = new AppUser();
        BeanUtils.copyProperties(appUserRequestDTO, appUser);
        appUser.setImage(imageRepository.findById(appUserRequestDTO.getImageId())
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", appUserRequestDTO.getImageId())));
        appUser.setRole(roleRepository.findById(appUserRequestDTO.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", appUserRequestDTO.getRoleId())));
        return appUser;
    }
}
