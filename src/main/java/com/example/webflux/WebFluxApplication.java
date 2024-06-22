package com.example.webflux;

import com.example.webflux.entity.Movie;
import com.example.webflux.repository.MovieRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class WebFluxApplication {
    @Bean
    public ApplicationRunner saveMovies(MovieRepository repository) {
        Flux<Movie> movies = Flux.just(
                new Movie(null, "Catch me if you can."),
                new Movie(null, "Interstellar"),
                new Movie(null, "Fight Club"),
                new Movie(null, "Creed"),
                new Movie(null, "The Godfather")
        );
        return args -> repository.deleteAll()
                .thenMany(repository.saveAll(movies))
                .subscribe(System.out::println);
    }
    public static void main(String[] args) {
        SpringApplication.run(WebFluxApplication.class, args);
    }

}
