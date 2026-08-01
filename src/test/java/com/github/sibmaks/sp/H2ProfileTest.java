package com.github.sibmaks.sp;

import com.github.sibmaks.sp.domain.User;
import com.github.sibmaks.sp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("h2")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class H2ProfileTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldApplyFlywayMigrations() {
        var rolesCount = jdbcTemplate.queryForObject(
                "select count(*) from scrum_poker.role",
                Integer.class
        );

        assertThat(rolesCount).isEqualTo(4);
    }

    @Test
    void shouldQueryAndPersistUser() {
        assertThat(userRepository.existsByLogin("missing-user")).isFalse();

        var user = User.builder()
                .login("h2-test@example.com")
                .password("password")
                .firstName("H2")
                .lastName("Test")
                .build();
        userRepository.saveAndFlush(user);

        assertThat(user.getId()).isPositive();
        assertThat(userRepository.existsByLogin(user.getLogin())).isTrue();
    }
}
