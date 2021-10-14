package com.github.sibmaks.sp.service;

import com.github.sibmaks.sp.domain.User;
import com.github.sibmaks.sp.exception.LoginIsBusyException;
import com.github.sibmaks.sp.exception.NotFoundException;
import com.github.sibmaks.sp.exception.UnauthorizedException;
import com.github.sibmaks.sp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.Locale;

/**
 * User operation service
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {
    private final SessionService sessionService;
    private final UserRepository userRepository;

    /**
     * Login client by login and password.
     * Return session identifier on success.
     * If user not found or password is wrong then NotFound result code will be returned
     *
     * @param login user login
     * @param password user password
     * @return session identifier
     */
    public String login(String login, String password) {
        User user = userRepository.findByLogin(login.toLowerCase(Locale.ROOT));
        if(user == null) {
            throw new NotFoundException();
        }

        if(BCrypt.checkpw(password,user.getPassword())) {
            return sessionService.createSession(user);
        } else {
            log.warn("Wrong password for user {}", login);
            throw new NotFoundException();
        }
    }

    /**
     * Create user with passed data.
     * If user with the same login is already exists then {@link LoginIsBusyException} will be thrown
     *
     * @param login user login
     * @param password user password
     * @param firstName user first name
     * @param lastName user last name
     * @return session identifier
     */
    @Transactional
    public String createUser(String login, String password, String firstName, String lastName) {
        login = login.toLowerCase(Locale.ROOT);

        if(userRepository.existsByLogin(login)) {
            throw new LoginIsBusyException();
        }

        User user = User.builder()
                .login(login)
                .password(BCrypt.hashpw(password, BCrypt.gensalt()))
                .firstName(HtmlUtils.htmlEscape(firstName))
                .lastName(HtmlUtils.htmlEscape(lastName))
                .build();

        userRepository.save(user);

        return sessionService.createSession(user);
    }

    /**
     * Get user by user identifier. If user not found then {@link UnauthorizedException} will be thrown.
     *
     * @param userId user identifier
     * @return user domain
     */
    public User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(UnauthorizedException::new);
    }

    /**
     * Update user data.
     * In case if user not found {@link UnauthorizedException} will be thrown.
     *
     * @param userId user identifier
     * @param firstName new user first name
     * @param lastName new user last name
     * @return is something changed or not
     */
    public boolean update(long userId, String firstName, String lastName) {
        User user = userRepository.findById(userId).orElseThrow(UnauthorizedException::new);
        boolean changed = false;
        if(firstName != null && !firstName.equals(user.getFirstName())) {
            user.setFirstName(HtmlUtils.htmlEscape(firstName));
            changed = true;
        }
        if(lastName != null && !lastName.equals(user.getLastName())) {
            user.setLastName(HtmlUtils.htmlEscape(lastName));
            changed = true;
        }
        if(changed) {
            userRepository.save(user);
        }
        return changed;
    }

    /**
     * Update user password.
     * In case if user not found {@link UnauthorizedException} will be thrown.
     *
     * @param userId user identifier
     * @param password new user password
     */
    public void changePassword(long userId, String password) {
        User user = userRepository.findById(userId).orElseThrow(UnauthorizedException::new);
        if(password != null && !password.isEmpty() && !BCrypt.checkpw(password, user.getPassword())) {
            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
            userRepository.save(user);
        }
    }
}
