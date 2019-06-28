package com.wongnai.interview.movie.search;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wongnai.interview.movie.Movie;
import com.wongnai.interview.movie.MovieSearchService;
import com.wongnai.interview.movie.external.MovieDataService;

@Component("simpleMovieSearchService")
public class SimpleMovieSearchService implements MovieSearchService {
	@Autowired
	private MovieDataService movieDataService;

	@Override
	public List<Movie> search(String queryText) {
		//TODO: Step 2 => Implement this method by using data from MovieDataService
		// All test in SimpleMovieSearchServiceIntegrationTest must pass.
		// Please do not change @Component annotation on this class
		return movieDataService.fetchAll().stream()
				.map(movieData -> {
					Movie movie = new Movie(movieData.getTitle());
					movie.setActors(movieData.getCast());
					return movie;
				}).filter(movie -> {
					List<String> words = Arrays.asList(movie.getName().split(" "));
					return words.stream().anyMatch(word ->
							word.toLowerCase().equals(queryText.toLowerCase()));
				}).collect(Collectors.toList());
	}
}
