package kh.farrukh.progee_api.role;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.app_user.AppUserRepository;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.role.payloads.RoleRequestDTO;
import kh.farrukh.progee_api.role.payloads.RoleResponseDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;

import static kh.farrukh.progee_api.role.RoleController.ENDPOINT_ROLE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class RoleControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @AfterEach
    void tearDown() {
        appUserRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test@mail.com")
    void canGetRoles() throws Exception {
        // given
        List<Role> roles = List.of(
                new Role("ADMIN", false, Collections.singletonList(Permission.CAN_VIEW_ROLE)),
                new Role("USER", false, Collections.emptyList())
        );
        roles = roleRepository.saveAll(roles);
        appUserRepository.save(new AppUser("test@mail.com", roles.get(0)));

        // when
        MvcResult result = mvc.perform(get(ENDPOINT_ROLE)
                        .param("page_size", String.valueOf(roles.size())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        PagingResponse<Role> response = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                }
        );
        assertThat(response.getTotalItems()).isEqualTo(roles.size());
    }

    @Test
    @WithMockUser(username = "test@mail.com")
    void canGetRoleById() throws Exception {
        // given
        Role role = roleRepository.save(
                new Role("ADMIN", false, Collections.singletonList(Permission.CAN_VIEW_ROLE))
        );
        appUserRepository.save(new AppUser("test@mail.com", role));

        // when
        MvcResult result = mvc.perform(get(ENDPOINT_ROLE + "/" + role.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        RoleResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(), RoleResponseDTO.class
        );
        assertThat(response.getId()).isEqualTo(role.getId());
    }

    @Test
    @WithMockUser(username = "test@mail.com")
    void canAddRole() throws Exception {
        // given
        Role existingRole = roleRepository.save(
                new Role("ADMIN", false, Collections.singletonList(Permission.CAN_CREATE_ROLE))
        );
        appUserRepository.save(new AppUser("test@mail.com", existingRole));
        RoleRequestDTO roleRequestDTO = new RoleRequestDTO("USER", false, Collections.emptyList());

        // when
        MvcResult result = mvc.perform(
                        post(ENDPOINT_ROLE)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(roleRequestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // then
        RoleResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(), RoleResponseDTO.class
        );

        assertThat(response.getTitle()).isEqualTo(roleRequestDTO.getTitle());
    }

    @Test
    @WithMockUser(username = "test@mail.com")
    void canUpdateRole() throws Exception {
        // given
        Role existingRole = roleRepository.save(
                new Role("ADMIN", false, Collections.singletonList(Permission.CAN_UPDATE_ROLE))
        );
        appUserRepository.save(new AppUser("test@mail.com", existingRole));
        RoleRequestDTO roleRequestDTO = new RoleRequestDTO(
                "SUPER_ADMIN", false, Collections.singletonList(Permission.CAN_VIEW_ROLE)
        );

        // when
        MvcResult result = mvc.perform(
                        put(ENDPOINT_ROLE + "/" + existingRole.getId())
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(roleRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then
        RoleResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(), RoleResponseDTO.class
        );

        assertThat(response.getId()).isEqualTo(existingRole.getId());
        assertThat(response.getTitle()).isEqualTo(roleRequestDTO.getTitle());
    }

    @Test
    @WithMockUser(username = "test@mail.com")
    void canDeleteRole() throws Exception {
        // given
        Role existingRole = roleRepository.save(
                new Role("ADMIN", false, Collections.singletonList(Permission.CAN_DELETE_ROLE))
        );
        appUserRepository.save(new AppUser("test@mail.com", existingRole));

        // when
        // then
        mvc.perform(delete(ENDPOINT_ROLE + "/" + existingRole.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}