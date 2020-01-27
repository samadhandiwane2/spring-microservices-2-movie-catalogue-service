package javabrains.moviecatalogueservice.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import javabrains.moviecatalogueservice.model.Rating;
import javabrains.moviecatalogueservice.model.UserRating;

@Service
public class UserRatingInfo {
	
	@Autowired
	RestTemplate restTemplate;
	
	/* Bulkhead Pattern */
	/*
	@HystrixCommand(fallbackMethod = "getFallbackUserRatings", 
			threadPoolKey = "userRatingPool", 
			threadPoolProperties = {
					@HystrixProperty(name="coreSize", value="20"),
					@HystrixProperty(name="maxQueueSize", value="10")
			})
	
	 */
	
	/* Circuit Breaker Pattern */
	@HystrixCommand(fallbackMethod = "getFallbackUserRatings", 
			commandProperties = {
					@HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="2000"),
					@HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="5"),
					@HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="50"),
					@HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="5000")
			})
	public UserRating getUserRatings(@PathVariable("userId") String userId) {
		return restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId, UserRating.class);
	}
		
	public UserRating getFallbackUserRatings(@PathVariable("userId") String userId) {
		UserRating userRatings = new UserRating();
		userRatings.setUserRatings(Arrays.asList(new Rating("0", 0)));
		return userRatings;
	}

}
