package workable.movieRama.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import workable.movieRama.domain.Movie;
import workable.movieRama.domain.User;
import workable.movieRama.domain.Vote;
import workable.movieRama.model.LoginRequest;
import workable.movieRama.service.MovieService;
import workable.movieRama.service.UserService;
import workable.movieRama.service.VoteService;
import workable.movieRama.utils.Response;
import workable.movieRama.utils.ResponseCode;
import workable.movieRama.utils.Result;

import javax.validation.Valid;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;


@Slf4j
@Validated
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UserController {

    private final String MISSING_HEADER = "Missing headers";

    private final UserService userService;
    private final MovieService movieService;
    private final VoteService voteService;


    @PostMapping(value = "/user/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public  Mono<ObjectNode> loginUser(WebSession session, @Valid @RequestBody LoginRequest loginRequest) {
            loginRequest.setPassword(userService.generatePassword(loginRequest.getPassword()));

            return userService.findByCredentials(loginRequest)
                    .map(user -> {
                        try {
                            return Response.OK.toResult(ResponseCode.REQUEST_SUCCESS, user.toJson());
                        } catch (Exception e) {
                            log.error("LoginUser threw exception: {}",e.getMessage());
                            return null;
                        }
                    })
                    .switchIfEmpty(Mono.just(Response.REQUEST_FAILED.toResult(ResponseCode.AUTH_FAILED)));

    }

    @PostMapping(value = "/user/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ObjectNode> createUSer(@Valid @RequestBody User user) {

        user.setRegistratioDate(LocalDateTime.now());
        return userService.findByEmail(user.getEmail())
                .map(usr -> Response.REQUEST_FAILED.toResult(ResponseCode.REGISTER_USER_EXISTS))
                .switchIfEmpty(createUserAndGetResponse(user));
    }

    @PutMapping("/movie/like/{movieId}/{isPositive}/")
    public Mono<ObjectNode> voteMovie(
            @RequestHeader(value = "X-workable-user-id") String userId,
            @PathVariable String movieId,
            @PathVariable boolean isPositive){
        if (isNull(userId)) {
            return Mono.just(Response.REQUEST_FAILED.toResult(ResponseCode.MISSING_HEADERS));
        }

        return userService.findByUserId(userId)
                .flatMap(usr-> movieService.getMovieByIdAndUserId(movieId, userId))
                .flatMap(movie -> {
                    return Mono.just(Response.REQUEST_FAILED.toResult(ResponseCode.USER_CANNOT_VOTE_SELF_CREATED_MOVIE));
                })
                .switchIfEmpty(insertVoteOrUpdate(movieId, userId, isPositive));

    }

    @DeleteMapping("/movie/like/{movieId}/")
    public Mono<ObjectNode> deleteVote(
            @RequestHeader(value = "X-workable-user-id") String userId,
            @PathVariable String movieId){

        if (isNull(userId)) {
            return Mono.just(Response.REQUEST_FAILED.toResult(ResponseCode.MISSING_HEADERS));
        }

        return userService.findByUserId(userId)
                .flatMap(usr-> voteService.getVoteByMovieIdAndUserId(movieId, userId))
                .flatMap(vote -> {
                    voteService.deleteVote(vote);
                    return Mono.just(Response.OK.toResult(ResponseCode.VOTE_WAS_DELETED));
                })
                .switchIfEmpty(Mono.just(Response.REQUEST_FAILED.toResult(ResponseCode.REQUEST_ERROR)));
    }

    private Mono<ObjectNode> createUserAndGetResponse(User user) {
        return userService.addUser(user).flatMap(usr->Mono.just(Response.OK.toResult(ResponseCode.REQUEST_SUCCESS)))
                .switchIfEmpty(Mono.just(Response.REQUEST_FAILED.toResult(ResponseCode.REGISTER_USER_FAILED)));
    }

    private Mono<ObjectNode> insertVoteOrUpdate(String movieId, String userId, boolean isPositive) {
        return voteService.getVoteByMovieIdAndUserId(movieId, userId)
                .flatMap(vote->{
                    vote.setPositive(isPositive);
                    return voteService.addOrUpdateVote(vote)
                            .flatMap(vt->Mono.just(Response.OK.toResult(ResponseCode.VOTE_UPDATED)));
                })
                .switchIfEmpty(createVote(Vote.builder()
                        .movieId(movieId)
                        .userId(userId)
                        .positive(isPositive)
                        .build())
                );
    }

    private Mono<ObjectNode> createVote(Vote vote) {
        return voteService.addOrUpdateVote(vote)
                .flatMap(vt->Mono.just(Response.OK.toResult(ResponseCode.VOTE_CREATED)));
    }

   /* @ExceptionHandler({ NullPointerException.class })
    public ObjectNode handleException() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resp = mapper.createObjectNode();
       return Response.OK.toResult(ResponseCode.AUTH_FAILED, resp);
    }*/
}