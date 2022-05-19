package  workable.movieRama.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import workable.movieRama.domain.Movie;
import workable.movieRama.domain.User;
import workable.movieRama.model.LoginRequest;
import workable.movieRama.repository.UserRepository;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<User> addUser(User user) {
        user.setUserId(generateToken());
        user.setPassword(generatePassword(user.getPassword()));
        return userRepository.save(user);
    }

    public Mono<User> findByCredentials(LoginRequest loginRequest) {
        return userRepository.findByEmailAndPassword(loginRequest.getEmail(), loginRequest.getPassword());
    }

    public Mono<User> findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }


    public String generatePassword(String password) {
        return sha256(sha1(password));
    }

    private String sha1(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(password.getBytes(), 0, password.length());

            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            log.error("sha1(%s): Failed to generate the digest (%s)", password, e.toString());
            return "failed-sha1";
        }
    }

    private  String generateToken() {
        return UUID.randomUUID().toString();
    }

    private String sha256(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes(), 0, password.length());

            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            log.error("sha256(%s): Failed to generate the digest (%s)", password, e.toString());
            return "failed-sha256";
        }
    }

    public Mono<Boolean> useExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}