package javabrains.moviecatalogueservice.resource;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import javabrains.moviecatalogueservice.model.CatalogueItem;
import javabrains.moviecatalogueservice.model.UserRating;
import javabrains.moviecatalogueservice.service.MovieInfo;
import javabrains.moviecatalogueservice.service.UserRatingInfo;

@RestController
@RequestMapping("/catalogue")
public class MovieCatalogueController {
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	WebClient.Builder webClientBuilder;
	
	@Autowired
	MovieInfo movieInfo;
	
	@Autowired
	UserRatingInfo userRatingInfo;
	
	@GetMapping("/{userId}")
	public List<CatalogueItem> getCatalogue(@PathVariable("userId") String userId){
		
		UserRating ratings = userRatingInfo.getUserRatings(userId);
		
		// call using rest template		
		return ratings.getUserRatings().stream().map(rating -> {
			return movieInfo.getCatalogueItem(rating);
		}).collect(Collectors.toList());		 
		 
		
		
		// call using WebClient builder
		/*
		 * return ratings.stream().map(rating -> { Movie movie =
		 * webClientBuilder.build() .get()
		 * .uri("http://localhost:8082/movies/"+rating.getMovieId()) .retrieve()
		 * .bodyToMono(Movie.class) .block(); return new CatalogueItem(movie.getName(),
		 * "Action", rating.getRating()); }) .collect(Collectors.toList());
		 */
	}	

}
