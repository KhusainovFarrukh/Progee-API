package kh.farrukh.progee_api.role;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
    }

    @Test
    void returnsTrueIfRoleExistsByTitle() {
        // given
        String title = "user";
        Role role = new Role(title, false, Collections.emptyList());
        roleRepository.save(role);

        // when
        boolean exists = roleRepository.existsByTitle(title);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void returnsFalseIfRoleDoesNotExistByTitle() {
        // given
        String title = "user";
        Role role = new Role("admin", false, Collections.emptyList());
        roleRepository.save(role);

        // when
        boolean exists = roleRepository.existsByTitle(title);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void returnsFalseIfTitleIsEmpty() {
        // given
        String title = "";
        Role role = new Role("admin", false, Collections.emptyList());
        roleRepository.save(role);

        // when
        boolean exists = roleRepository.existsByTitle(title);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void returnsFalseIfTitleIsNull() {
        // given
        String title = null;
        Role role = new Role("admin", false, Collections.emptyList());
        roleRepository.save(role);

        // when
        boolean exists = roleRepository.existsByTitle(title);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void returnsEmptyDataIfDoesNotContainAnyRole() {
        // when
        Optional<Role> roleOptional = roleRepository.findFirstByIsDefaultIsTrue();

        // then
        assertThat(roleOptional.isEmpty()).isTrue();
    }

    @Test
    void returnsValidDataIfContainsOnlySingleDefaultRole() {
        // given
        Role role = new Role("test", true, Collections.emptyList());
        roleRepository.save(role);

        // when
        Optional<Role> roleOptional = roleRepository.findFirstByIsDefaultIsTrue();

        // then
        assertThat(roleOptional.isPresent()).isTrue();
        assertThat(roleOptional.get().getTitle()).isEqualTo(role.getTitle());
    }

    @Test
    void returnsEmptyDataIfContainsOnlyNonDefaultRole() {
        // given
        Role role = new Role("test", false, Collections.emptyList());
        roleRepository.save(role);

        // when
        Optional<Role> roleOptional = roleRepository.findFirstByIsDefaultIsTrue();

        // then
        assertThat(roleOptional.isEmpty()).isTrue();
    }

    @Test
    void returnsValidDataIfContainsNonDefaultAndSingleDefaultRole() {
        // given
        List<Role> roles = List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", false, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList())
        );
        roleRepository.saveAll(roles);

        // when
        Optional<Role> roleOptional = roleRepository.findFirstByIsDefaultIsTrue();

        // then
        assertThat(roleOptional.isPresent()).isTrue();
        assertThat(roleOptional.get().getTitle()).isEqualTo(roles.get(2).getTitle());
    }

    @Test
    void returnsValidDataIfContainsNonDefaultAndMultipleDefaultRole() {
        // given
        List<Role> roles = List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", false, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList()),
                new Role("test4", true, Collections.emptyList())
        );
        roleRepository.saveAll(roles);

        // when
        Optional<Role> roleOptional = roleRepository.findFirstByIsDefaultIsTrue();

        // then
        assertThat(roleOptional.isPresent()).isTrue();
        assertThat(roleOptional.get().getTitle()).isEqualTo(roles.get(2).getTitle());
    }

    @Test
    void returnsEmptyDataIfContainsOnlySingleDefaultRoleAndIdIsSame() {
        // given
        Role role = roleRepository.save(new Role("test", true, Collections.emptyList()));

        // when
        Optional<Role> roleOptional = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(role.getId());

        // then
        assertThat(roleOptional.isEmpty()).isTrue();
    }

    @Test
    void returnsValidDataIfContainsOnlySingleDefaultRoleAndIdIsDifferent() {
        // given
        Role role = roleRepository.save(new Role("test", true, Collections.emptyList()));

        // when
        Optional<Role> roleOptional = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(role.getId() + 1);

        // then
        assertThat(roleOptional.isPresent()).isTrue();
        assertThat(roleOptional.get().getTitle()).isEqualTo(role.getTitle());
    }

    @Test
    void returnsEmptyDataIfContainsOnlyNonDefaultRoleAndIdIsSame() {
        // given
        Role role = roleRepository.save(new Role("test", false, Collections.emptyList()));

        // when
        Optional<Role> roleOptional = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(role.getId());

        // then
        assertThat(roleOptional.isEmpty()).isTrue();
    }

    @Test
    void returnsEmptyDataIfContainsOnlyNonDefaultRoleAndIdIsDifferent() {
        // given
        Role role = roleRepository.save(new Role("test", false, Collections.emptyList()));

        // when
        Optional<Role> roleOptional = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(role.getId() + 1);

        // then
        assertThat(roleOptional.isEmpty()).isTrue();
    }

    @Test
    void returnsValidDataIfContainsNonDefaultAndSingleDefaultRoleAndIdIsSame() {
        // given
        List<Role> roles = roleRepository.saveAll(List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", false, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList())
        ));

        // when
        Optional<Role> roleOptional = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(roles.get(2).getId());

        // then
        assertThat(roleOptional.isEmpty()).isTrue();
    }

    @Test
    void returnsValidDataIfContainsNonDefaultAndSingleDefaultRoleAndIdIsDifferent() {
        // given
        List<Role> roles = roleRepository.saveAll(List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", false, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList())
        ));

        // when
        Optional<Role> roleOptional = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(roles.get(2).getId() + 1);

        // then
        assertThat(roleOptional.isPresent()).isTrue();
        assertThat(roleOptional.get().getTitle()).isEqualTo(roles.get(2).getTitle());
    }

    @Test
    void returnsEmptyDataIfContainsNonDefaultAndMultipleDefaultRoleAndIdIsSame() {
        // given
        List<Role> roles = roleRepository.saveAll(List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", false, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList()),
                new Role("test4", true, Collections.emptyList())
        ));

        // when
        Optional<Role> roleOptional = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(roles.get(2).getId());

        // then
        assertThat(roleOptional.isPresent()).isTrue();
        assertThat(roleOptional.get().getTitle()).isEqualTo(roles.get(3).getTitle());
    }

    @Test
    void returnsValidDataIfContainsNonDefaultAndMultipleDefaultRoleAndIdIsDifferent() {
        // given
        List<Role> roles = roleRepository.saveAll(List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", false, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList()),
                new Role("test4", true, Collections.emptyList())
        ));

        // when
        Optional<Role> roleOptional = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(roles.get(2).getId() + 1);

        // then
        assertThat(roleOptional.isPresent()).isTrue();
        assertThat(roleOptional.get().getTitle()).isEqualTo(roles.get(2).getTitle());
    }

    @Test
    void returnZeroIfNoRoleExists() {
        // when
        long count = roleRepository.countByIsDefaultIsTrue();

        // then
        assertThat(count).isEqualTo(0);
    }

    @Test
    void returnOneIfOneDefaultRoleExists() {
        // given
        Role role = new Role("test", true, Collections.emptyList());
        roleRepository.save(role);

        // when
        long count = roleRepository.countByIsDefaultIsTrue();

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    void returnZeroIfOneNonDefaultRoleExists() {
        // given
        Role role = new Role("test", false, Collections.emptyList());
        roleRepository.save(role);

        // when
        long count = roleRepository.countByIsDefaultIsTrue();

        // then
        assertThat(count).isEqualTo(0);
    }

    @Test
    void returnOneIfOneDefaultAndOneNonDefaultRoleExists() {
        // given
        List<Role> roles = List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", true, Collections.emptyList())
        );
        roleRepository.saveAll(roles);

        // when
        long count = roleRepository.countByIsDefaultIsTrue();

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    void returnOneIfOneDefaultAndMultipleNonDefaultRoleExists() {
        // given
        List<Role> roles = List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", false, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList())
        );
        roleRepository.saveAll(roles);

        // when
        long count = roleRepository.countByIsDefaultIsTrue();

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    void returnValidCountIfMultipleDefaultAndOneNonDefaultRoleExists() {
        // given
        List<Role> roles = List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", true, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList())
        );
        roleRepository.saveAll(roles);

        // when
        long count = roleRepository.countByIsDefaultIsTrue();

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void returnValidCountIfMultipleDefaultAndMultipleNonDefaultRoleExists() {
        // given
        List<Role> roles = List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", false, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList()),
                new Role("test4", true, Collections.emptyList())
        );
        roleRepository.saveAll(roles);

        // when
        long count = roleRepository.countByIsDefaultIsTrue();

        // then
        assertThat(count).isEqualTo(2);
    }
}