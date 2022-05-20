package workable.movieRama.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import workable.movieRama.domain.Movie;
import workable.movieRama.service.MovieService;
import workable.movieRama.service.UserService;
import workable.movieRama.service.VoteService;
import workable.movieRama.utils.Response;
import workable.movieRama.utils.ResponseCode;

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
    private final VoteService voteService;

    @GetMapping("/movies")
    public Flux<Movie> getAllMovies() {
        return movieService.getAllMovies().flatMap(movie ->
              voteService.getVotesByMovieId(movie.getId(), true)
                      .map(cnt -> {
                          movie.setLikes(cnt);
                          return movie;
                        })
        ).flatMap(movie ->
                voteService.getVotesByMovieId(movie.getId(), false)
                        .map(cnt -> {
                            movie.setDislikes(cnt);
                            return movie;
                        })
        );

//            movie.setLikes(voteService.getVotesByMovieId(movie.getId(), true));
//            movie.setDislikes(voteService.getVotesByMovieId(movie.getId(), true));

    }

    @GetMapping("/movies/{userId}/")
    public Flux<Movie> getAllMovies(@PathVariable String userId){
        return movieService.getMoviesByUserId(userId).flatMap(movie ->
                voteService.getVotesByMovieId(movie.getId(), true)
                        .map(cnt -> {
                            movie.setLikes(cnt);
                            return movie;
                        })
        ).flatMap(movie ->
                voteService.getVotesByMovieId(movie.getId(), false)
                        .map(cnt -> {
                            movie.setDislikes(cnt);
                            return movie;
                        })
        );
    }

    @GetMapping("/movie/{id}")
    public Mono<Movie> getMovieById(@PathVariable String id){
        return movieService.getMovieInfoById(id);
    }

    @PostMapping(value = "/movie", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ObjectNode> addMovie(
            @Valid @RequestBody Movie movie,
            @RequestHeader(value = "X-workable-user-id", required = true) String userId) {

        if (isNull(userId)) {
            return Mono.just(Response.REQUEST_FAILED.toResult(ResponseCode.MISSING_HEADERS));
        }

        return userService.findByUserId(userId).flatMap(usr-> {
            movie.setUserId(userId);
            movie.setPublicationDate(LocalDateTime.now());
            return  movieService.addMovie(movie);
        }).flatMap(mv->{
            return Mono.just(Response.OK.toResult(ResponseCode.REQUEST_SUCCESS));
        })
        .switchIfEmpty(Mono.just(Response.REQUEST_FAILED.toResult(ResponseCode.INVALID_USER)));
    }


    @PutMapping("/movie/{id}/")
    public Mono<Movie> updateMovie(@RequestBody Movie updatedMovieInfo, @PathVariable String id){
        return movieService.updateMovieInfo(updatedMovieInfo, id);
    }
}
