package  workable.movieRama.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import workable.movieRama.domain.Movie;
import workable.movieRama.repository.MovieRepository;

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public Mono<Movie> addMovie(Movie movieInfo) {
        return movieRepository.save(movieInfo);
    }

    public Flux<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Mono<Movie> getMovieByIdAndUserId(String movieId, String userId) {

        return movieRepository.findByIdAndUserId(movieId, userId);
    }

    public Flux<Movie> getMoviesByUserId(String userId) {
        return movieRepository.findByUserId(userId);
    }

    public Mono<Movie> getMovieInfoById(String id) {
        return movieRepository.findById(id);
    }

    public Mono<Movie> updateMovieInfo(Movie updatedMovie, String id) {

        return movieRepository.findById(id)
                .flatMap(movieInfo -> {
                    movieInfo.setTitle(updatedMovie.getTitle());
                    movieInfo.setDescription(updatedMovie.getDescription());
                    movieInfo.setUserId(updatedMovie.getUserId());
                    movieInfo.setLikes(updatedMovie.getLikes());
                    return movieRepository.save(movieInfo);
                });
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return movieRepository.deleteById(id);
    }
}