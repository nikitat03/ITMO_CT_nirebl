package ru.itmo.wp.model.service;

import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.repository.UserRepository;
import ru.itmo.wp.model.repository.impl.UserRepositoryImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class UserService {
    private static final String PASSWORD_SALT = "1174f9d7bc21e00e9a5fd0a783a44c9a9f73413c";

    private final UserRepository userRepository = new UserRepositoryImpl();

    public void validateUser(User user) throws ValidationException
    {
        User dbUser = userRepository.find(user.getId());
        if(dbUser == null)
            throw new ValidationException("User is incorrect");

        if(!user.getLogin().equals(dbUser.getLogin()))
            throw new ValidationException("User is incorrect");

        if(!user.getEmail().equals(dbUser.getEmail()))
            throw new ValidationException("User is incorrect");
    }

    public void validateRegistration(User user, String password, String passwordConfirmation, String email) throws ValidationException {
        if (Strings.isNullOrEmpty(user.getLogin())) {
            throw new ValidationException("Login is required");
        }
        if (Strings.isNullOrEmpty(user.getEmail())) {
            throw new ValidationException("Email is required");
        }
        if (!user.getLogin().matches("[a-z]+")) {
            throw new ValidationException("Login can contain only lowercase Latin letters");
        }
        if (user.getLogin().length() > 8) {
            throw new ValidationException("Login can't be longer than 8 letters");
        }
        if (userRepository.findByLogin(user.getLogin()) != null) {
            throw new ValidationException("Login is already in use");
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new ValidationException("Email is already in use");
        }
        if (Strings.isNullOrEmpty(password)) {
            throw new ValidationException("Password is required");
        }
        if (password.length() < 4) {
            throw new ValidationException("Password can't be shorter than 4 characters");
        }
        if (password.length() > 64) {
            throw new ValidationException("Password can't be longer than 64 characters");
        }

        if (!password.equals(passwordConfirmation)) {
            throw new ValidationException("Passwords don't equals");
        }

        if (email.indexOf('@') == -1) {
            throw new ValidationException("Email is wrong");
        }
    }

    public void register(User user, String password, String email) {
        userRepository.save(user, getPasswordSha(password), email);
    }

    private String getPasswordSha(String password) {
        return Hashing.sha256().hashBytes((PASSWORD_SALT + password).getBytes(StandardCharsets.UTF_8)).toString();
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void validateEnter(String loginOrEmail, String password) throws ValidationException {
        if(Strings.isNullOrEmpty(loginOrEmail)
                || Strings.isNullOrEmpty(password))
            throw new ValidationException("LoginOrEmail or password can not be empty");

        User user = userRepository.findByLoginAndPasswordSha(loginOrEmail, getPasswordSha(password));
        if (user == null) {
            user = userRepository.findByEmailAndPasswordSha(loginOrEmail, getPasswordSha(password));
            if (user == null)
                throw new ValidationException("Invalid loginOrEmail or password");
        }
    }

    public User findByLoginOrEmailAndPassword(String loginOrEmail, String password) throws ValidationException{
        User user = userRepository.findByLoginAndPasswordSha(loginOrEmail, getPasswordSha(password));
        if (user == null) {
            user = userRepository.findByEmailAndPasswordSha(loginOrEmail, getPasswordSha(password));
            if (user == null)
                throw new ValidationException("Invalid loginOrEmail or password");
        }

        return user;
    }

    public long findCount() {
        return userRepository.findCount();
    }

}
