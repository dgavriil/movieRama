package workable.movieRama.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import workable.movieRama.domain.Movie;

public interface MovieRepository extends ReactiveMongoRepository<Movie,String> {
    Flux<Movie> findByUserId(String userId);
}

