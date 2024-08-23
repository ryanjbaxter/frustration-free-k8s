package org.example.frustrationfreek8s;

import java.util.List;
import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class FrustrationFreeK8sApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrustrationFreeK8sApplication.class, args);
	}

}

@RestController
class MovieController {

	private final MoviesRepository moviesRepository;
	private final RestTemplate rest;

	public MovieController(MoviesRepository moviesRepository, RestTemplate rest) {
		this.moviesRepository = moviesRepository;
		this.rest = rest;
	}

	@GetMapping("/")
	public String hello() {
		Movie movie = moviesRepository.randomMovie();
		return "Hello " + rest.getForObject("http://k8s-workshop-name-service", String.class) + "! Would you like to go see " + movie.name() + "?";
	}

}

@Configuration
class MyConfig {

	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplateBuilder().build();
	}
}

interface MoviesRepository extends ListCrudRepository<Movie, Integer> {

	default Movie randomMovie() {
		List<Movie> movies = findAll();
		return movies.get(new Random().nextInt(movies.size()));
	}
}

record Movie(Integer id, String name){}
