package com.github.sibmaks.sp.scheduler;

import com.github.sibmaks.sp.conf.DataSourceStub;
import com.github.sibmaks.sp.domain.Room;
import com.github.sibmaks.sp.domain.User;
import com.github.sibmaks.sp.repository.RoomRepository;
import com.github.sibmaks.sp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author drobyshev-ma
 * Created at 19-01-2022
 */
@Import(DataSourceStub.class)
@ActiveProfiles("test")
@ContextConfiguration
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoomCleanerTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomCleaner roomCleaner;

    @Test
    void testCleanUp() {
        var user = new User();
        user.setFirstName("first");
        user.setLastName("last");
        user.setLogin(UUID.randomUUID().toString());
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));

        user = userRepository.save(user);

        var expiredRoom = Room.builder()
                .name("Expired room")
                .expired(new Date(new Date().getTime() - 1))
                .created(new Date())
                .voting(false)
                .author(user)
                .build();

        expiredRoom = roomRepository.save(expiredRoom);

        roomCleaner.cleanUp();

        assertFalse(roomRepository.existsById(expiredRoom.getId()));
    }
}