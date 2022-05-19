package workable.movieRama.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import workable.movieRama.domain.Movie;
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
public class MovieController {

    private final UserService userService;
    private final MovieService movieService;

    @GetMapping("/movies")
    public Flux<Movie> getAllMovies(){
        return movieService.getAllMovies().log();
    }

    @GetMapping("/movies/{userId}/")
    public Flux<Movie> getAllMovies(@PathVariable String userId){
        return movieService.getMoviesByUserId(userId);
    }

    @GetMapping("/movie/{id}")
    public Mono<Movie> getMovieById(@PathVariable String id){
        return movieService.getMovieInfoById(id);
    }

    @PostMapping(value = "/movie", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Movie>> addMovie(
            @Valid @RequestBody Movie movieInfo,
            @RequestHeader(value = "X-workable-user-id", required = true) String userId) {

        if (isNull(userId)) {
            throw new RuntimeException("No headers no honey");
        }

        return userService.findByUserId(userId).flatMap(usr-> {
            movieInfo.setUserId(userId);
            movieInfo.setPublicationDate(LocalDateTime.now());
            return movieService.addMovie(movieInfo);
        }).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @PutMapping("/movie/{id}")
    public Mono<Movie> updateMovie(@RequestBody Movie updatedMovieInfo, @PathVariable String id){
        return movieService.updateMovieInfo(updatedMovieInfo, id);

    }

    @DeleteMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String id){
        return movieService.deleteMovieInfo(id);
    }
}
