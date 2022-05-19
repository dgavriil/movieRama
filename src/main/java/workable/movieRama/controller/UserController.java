package workable.movieRama.controller;

import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import workable.movieRama.domain.Movie;
import workable.movieRama.domain.User;
import workable.movieRama.model.LoginRequest;
import workable.movieRama.service.MovieService;
import workable.movieRama.service.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;


@Slf4j
@Validated
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MovieService movieService;

    @GetMapping("/is/authenticated")
    public Mono<String> getSession(@RequestHeader(value = "X-workable-user-id") String userId) {
        if (isNull(userId)) {
            throw new RuntimeException("No headers no honey");
        }
        return Mono.just("asdas");
    }

    @PostMapping(value = "/user/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public  Mono<ResponseEntity<User>> loginUser(WebSession session, @Valid @RequestBody LoginRequest loginRequest) {

        loginRequest.setPassword(userService.generatePassword(loginRequest.getPassword()));

        return userService.findByCredentials(loginRequest)
//                .map(s-> {
//                    session.getAttributes().putIfAbsent("userId", s.getUserId());
//                    return s;
//                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/user/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> createUSer(@Valid @RequestBody User user) {

        user.setRegistratioDate(LocalDateTime.now());
        return userService.findByEmail(user.getEmail()).switchIfEmpty(userService.addUser(user));
    }

    @PutMapping("/movie/like/{id}")
    public Mono<Movie> likeMovie(
            @RequestHeader(value = "X-workable-user-id") String userId
            @RequestBody Movie updatedMovieInfo, @PathVariable String id){
        return movieService.updateMovieInfo(updatedMovieInfo, id);
    }
}