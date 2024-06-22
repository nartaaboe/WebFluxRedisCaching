package com.example.webflux.service;

import com.example.webflux.entity.Movie;
import com.example.webflux.repository.MovieRepository;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ReactiveRedisTemplate<String, Movie> reactiveRedisTemplate;

    public MovieService(MovieRepository movieRepository, ReactiveRedisTemplate<String, Movie> reactiveRedisTemplate) {
        this.movieRepository = movieRepository;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<Movie> save(Movie movie) {
        return movieRepository.save(movie);
    }

    public Flux<Movie> findAll() {
        return reactiveRedisTemplate.keys("movie:*")
                // Fetching cached movies.
                .flatMap(key -> reactiveRedisTemplate.opsForValue().get(key))
                // If cache is empty, fetch the database for movies
                .switchIfEmpty(movieRepository.findAll()
                        // Persisting the fetched movies in the cache.
                        .flatMap(movie ->
                                reactiveRedisTemplate
                                        .opsForValue()
                                        .set("movie:" + movie.id(), movie)
                        )
                        // Fetching the movies from the updated cache.
                        .thenMany(reactiveRedisTemplate
                                .keys("movie:*")
                                .flatMap(key -> reactiveRedisTemplate.opsForValue().get(key))
                        )
                );
    }
}