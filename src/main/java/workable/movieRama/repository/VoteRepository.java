package workable.movieRama.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import workable.movieRama.domain.Votes;

public class VoteRepository extends ReactiveMongoRepository<Votes,String> {
}
