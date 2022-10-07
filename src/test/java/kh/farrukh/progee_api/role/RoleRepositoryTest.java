package kh.farrukh.progee_api.role;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
    }

    @Test
    void existsByTitle_returnsTrue_whenRoleExistsByTitle() {
        // given
        String title = "user";
        Role role = new Role(title, false, Collections.emptyList());
        roleRepository.save(role);

        // when
        boolean actual = roleRepository.existsByTitle(title);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void existsByTitle_returnsFalse_whenRoleDoesNotExistByTitle() {
        // given
        String title = "user";
        Role role = new Role("admin", false, Collections.emptyList());
        roleRepository.save(role);

        // when
        boolean actual = roleRepository.existsByTitle(title);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void existsByTitle_returnsFalse_whenTitleIsEmpty() {
        // given
        String title = "";
        Role role = new Role("admin", false, Collections.emptyList());
        roleRepository.save(role);

        // when
        boolean actual = roleRepository.existsByTitle(title);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void existsByTitle_returnsFalse_whenTitleIsNull() {
        // given
        String title = null;
        Role role = new Role("admin", false, Collections.emptyList());
        roleRepository.save(role);

        // when
        boolean actual = roleRepository.existsByTitle(title);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void findFirstByIsDefaultIsTrue_returnsEmptyData_whenDoesNotContainAnyRole() {
        // when
        Optional<Role> actual = roleRepository.findFirstByIsDefaultIsTrue();

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void findFirstByIsDefaultIsTrue_returnsValidData_whenContainsOnlySingleDefaultRole() {
        // given
        Role role = new Role("test", true, Collections.emptyList());
        roleRepository.save(role);

        // when
        Optional<Role> actual = roleRepository.findFirstByIsDefaultIsTrue();

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getTitle()).isEqualTo(role.getTitle());
    }

    @Test
    void findFirstByIsDefaultIsTrue_returnsEmptyData_whenContainsOnlyNonDefaultRole() {
        // given
        Role role = new Role("test", false, Collections.emptyList());
        roleRepository.save(role);

        // when
        Optional<Role> actual = roleRepository.findFirstByIsDefaultIsTrue();

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void findFirstByIsDefaultIsTrue_returnsValidData_whenContainsNonDefaultAndSingleDefaultRole() {
        // given
        List<Role> roles = roleRepository.saveAll(List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", false, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList())
        ));

        // when
        Optional<Role> actual = roleRepository.findFirstByIsDefaultIsTrue();

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getTitle()).isEqualTo(roles.get(2).getTitle());
    }

    @Test
    void findFirstByIsDefaultIsTrue_returnsValidData_whenContainsNonDefaultAndMultipleDefaultRole() {
        // given
        List<Role> roles = roleRepository.saveAll(List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", false, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList()),
                new Role("test4", true, Collections.emptyList())
        ));

        // when
        Optional<Role> actual = roleRepository.findFirstByIsDefaultIsTrue();

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getTitle()).isEqualTo(roles.get(2).getTitle());
    }

    @Test
    void findFirstByIsDefaultIsTrueAndIdNot_returnsEmptyData_whenContainsOnlySingleDefaultRoleAndIdIsSame() {
        // given
        Role role = roleRepository.save(new Role("test", true, Collections.emptyList()));

        // when
        Optional<Role> actual = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(role.getId());

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void findFirstByIsDefaultIsTrueAndIdNot_returnsValidData_whenContainsOnlySingleDefaultRoleAndIdIsDifferent() {
        // given
        Role role = roleRepository.save(new Role("test", true, Collections.emptyList()));

        // when
        Optional<Role> actual = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(role.getId() + 1);

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getTitle()).isEqualTo(role.getTitle());
    }

    @Test
    void findFirstByIsDefaultIsTrueAndIdNot_returnsEmptyData_whenContainsOnlyNonDefaultRoleAndIdIsSame() {
        // given
        Role role = roleRepository.save(new Role("test", false, Collections.emptyList()));

        // when
        Optional<Role> actual = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(role.getId());

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void findFirstByIsDefaultIsTrueAndIdNot_returnsEmptyData_whenContainsOnlyNonDefaultRoleAndIdIsDifferent() {
        // given
        Role role = roleRepository.save(new Role("test", false, Collections.emptyList()));

        // when
        Optional<Role> actual = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(role.getId() + 1);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void findFirstByIsDefaultIsTrueAndIdNot_returnsValidData_whenContainsNonDefaultAndSingleDefaultRoleAndIdIsSame() {
        // given
        List<Role> roles = roleRepository.saveAll(List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", false, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList())
        ));

        // when
        Optional<Role> actual = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(roles.get(2).getId());

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void findFirstByIsDefaultIsTrueAndIdNot_returnsValidData_whenContainsNonDefaultAndSingleDefaultRoleAndIdIsDifferent() {
        // given
        List<Role> roles = roleRepository.saveAll(List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", false, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList())
        ));

        // when
        Optional<Role> actual = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(roles.get(2).getId() + 1);

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getTitle()).isEqualTo(roles.get(2).getTitle());
    }

    @Test
    void findFirstByIsDefaultIsTrueAndIdNot_returnsEmptyData_whenContainsNonDefaultAndMultipleDefaultRoleAndIdIsSame() {
        // given
        List<Role> roles = roleRepository.saveAll(List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", false, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList()),
                new Role("test4", true, Collections.emptyList())
        ));

        // when
        Optional<Role> actual = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(roles.get(2).getId());

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getTitle()).isEqualTo(roles.get(3).getTitle());
    }

    @Test
    void findFirstByIsDefaultIsTrueAndIdNot_returnsValidData_whenContainsNonDefaultAndMultipleDefaultRoleAndIdIsDifferent() {
        // given
        List<Role> roles = roleRepository.saveAll(List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", false, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList()),
                new Role("test4", true, Collections.emptyList())
        ));

        // when
        Optional<Role> actual = roleRepository.findFirstByIsDefaultIsTrueAndIdNot(roles.get(2).getId() + 1);

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getTitle()).isEqualTo(roles.get(2).getTitle());
    }

    @Test
    void countByIsDefaultIsTrue_returnZero_whenNoRoleExists() {
        // when
        long actual = roleRepository.countByIsDefaultIsTrue();

        // then
        assertThat(actual).isEqualTo(0);
    }

    @Test
    void countByIsDefaultIsTrue_returnsOne_whenOneDefaultRoleExists() {
        // given
        roleRepository.save(new Role("test", true, Collections.emptyList()));

        // when
        long actual = roleRepository.countByIsDefaultIsTrue();

        // then
        assertThat(actual).isEqualTo(1);
    }

    @Test
    void countByIsDefaultIsTrue_returnZero_whenOneNonDefaultRoleExists() {
        // given
        roleRepository.save(new Role("test", false, Collections.emptyList()));

        // when
        long actual = roleRepository.countByIsDefaultIsTrue();

        // then
        assertThat(actual).isEqualTo(0);
    }

    @Test
    void countByIsDefaultIsTrue_returnsOne_whenOneDefaultAndOneNonDefaultRoleExists() {
        // given
        roleRepository.saveAll(List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", true, Collections.emptyList())
        ));

        // when
        long actual = roleRepository.countByIsDefaultIsTrue();

        // then
        assertThat(actual).isEqualTo(1);
    }

    @Test
    void countByIsDefaultIsTrue_returnsOne_whenOneDefaultAndMultipleNonDefaultRoleExists() {
        // given
        roleRepository.saveAll(List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", false, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList())
        ));

        // when
        long actual = roleRepository.countByIsDefaultIsTrue();

        // then
        assertThat(actual).isEqualTo(1);
    }

    @Test
    void countByIsDefaultIsTrue_returnsValidCount_whenMultipleDefaultAndOneNonDefaultRoleExists() {
        // given
        roleRepository.saveAll(List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", true, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList())
        ));

        // when
        long actual = roleRepository.countByIsDefaultIsTrue();

        // then
        assertThat(actual).isEqualTo(2);
    }

    @Test
    void countByIsDefaultIsTrue_returnsValidCount_whenMultipleDefaultAndMultipleNonDefaultRoleExists() {
        // given
        roleRepository.saveAll(List.of(
                new Role("test1", false, Collections.emptyList()),
                new Role("test2", false, Collections.emptyList()),
                new Role("test3", true, Collections.emptyList()),
                new Role("test4", true, Collections.emptyList())
        ));

        // when
        long actual = roleRepository.countByIsDefaultIsTrue();

        // then
        assertThat(actual).isEqualTo(2);
    }
}