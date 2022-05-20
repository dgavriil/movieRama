package workable.movieRama.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import workable.movieRama.domain.Vote;

public interface VoteRepository extends ReactiveMongoRepository<Vote,String> {

    Mono<Vote> findByMovieIdAndUserId(String movieId, String userId);

    Mono<Void> deleteById(String id);

    Mono<Long>  countByMovieIdAndPositive(String id, Boolean positive);
}
