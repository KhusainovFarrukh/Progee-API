package kh.farrukh.progee_api.auth;

import kh.farrukh.progee_api.auth.payloads.RegistrationRequestDTO;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.role.RoleRepository;
import kh.farrukh.progee_api.app_user.payloads.AppUserRequestDTO;
import org.springframework.beans.BeanUtils;

/**
 * It maps a RegistrationRequestDTO to an AppUserRequestDTO
 */
public class AuthMappers {

    public static AppUserRequestDTO toAppUserRequestDTO(
            RegistrationRequestDTO registrationRequestDTO,
            RoleRepository roleRepository
    ) {
        if (registrationRequestDTO == null) return null;
        AppUserRequestDTO appUserRequestDTO = new AppUserRequestDTO();
        BeanUtils.copyProperties(registrationRequestDTO, appUserRequestDTO);
        appUserRequestDTO.setEnabled(true);
        appUserRequestDTO.setLocked(false);
        appUserRequestDTO.setRoleId(roleRepository.findFirstByIsDefaultIsTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Role", "isDefault", true))
                .getId());
        return appUserRequestDTO;
    }
}
