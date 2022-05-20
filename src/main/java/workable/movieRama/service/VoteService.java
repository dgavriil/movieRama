package workable.movieRama.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import workable.movieRama.domain.Movie;
import workable.movieRama.domain.User;
import workable.movieRama.domain.Vote;
import workable.movieRama.repository.UserRepository;
import workable.movieRama.repository.VoteRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;

    public Mono<Vote> getVoteByMovieIdAndUserId(String movieId, String userId) {
        return voteRepository.findByMovieIdAndUserId(movieId, userId);
    }

    public Mono<Long> getVotesByMovieId(String id, Boolean positive) {
        return voteRepository.countByMovieIdAndPositive(id, positive);
    }

    public Mono<Vote> addOrUpdateVote(Vote vote) {
        vote.setPublicationDate(LocalDateTime.now());
        return voteRepository.save(vote);
    }

    public Mono<Void> deleteVote(Vote vote) {
        return voteRepository.deleteById(vote.getId());
    }
}
