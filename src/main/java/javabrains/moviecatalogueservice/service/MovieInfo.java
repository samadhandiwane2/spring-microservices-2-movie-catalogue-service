package javabrains.moviecatalogueservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import javabrains.moviecatalogueservice.model.CatalogueItem;
import javabrains.moviecatalogueservice.model.Movie;
import javabrains.moviecatalogueservice.model.Rating;

@Service
public class MovieInfo {
	
	@Autowired
	RestTemplate restTemplate;
	
	/* Bulkhead Pattern */
	/*
	@HystrixCommand(fallbackMethod = "getFallbackCatalogueItem", 
			threadPoolKey = "movieInfoPool", 
			threadPoolProperties = {
					@HystrixProperty(name="coreSize", value="20"),
					@HystrixProperty(name="maxQueueSize", value="10")
			})
	
	 */
	
	/* Circuit Breaker Pattern */
	@HystrixCommand(fallbackMethod = "getFallbackCatalogueItem", 
			commandProperties = {
					@HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="2000"),
					@HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="5"),
					@HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="50"),
					@HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="5000")
			})
	public CatalogueItem getCatalogueItem(Rating rating) {
		Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
		return new CatalogueItem(movie.getName(), "Action", rating.getRating());
	}
	
	public CatalogueItem getFallbackCatalogueItem(Rating rating) {
		return new CatalogueItem("Movie name not found", "", 0);
	}

}
