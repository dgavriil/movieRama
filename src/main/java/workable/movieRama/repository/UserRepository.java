package workable.movieRama.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import workable.movieRama.domain.Movie;
import workable.movieRama.domain.User;

public interface UserRepository extends ReactiveMongoRepository<User,String> {
    Mono<User> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    Mono<User> findByEmailAndPassword(String email, String password);

    Mono<User> findByUserId(String userID);
}

