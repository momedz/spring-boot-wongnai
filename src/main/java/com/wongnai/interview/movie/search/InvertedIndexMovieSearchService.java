package com.wongnai.interview.movie.search;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.wongnai.interview.movie.Movie;
import com.wongnai.interview.movie.MovieRepository;
import com.wongnai.interview.movie.MovieSearchService;

@Component("invertedIndexMovieSearchService")
@DependsOn("movieDatabaseInitializer")
public class InvertedIndexMovieSearchService implements MovieSearchService {
	@Autowired
	private MovieRepository movieRepository;

	private Map<String, Set<Long>> InvertedIndexMovie = new HashMap<>();

	@Override
	public List<Movie> search(String queryText) {
		//TODO: Step 4 => Please implement in-memory inverted index to search movie by keyword.
		// You must find a way to build inverted index before you do an actual search.
		// Inverted index would looks like this:
		// -------------------------------
		// |  Term      | Movie Ids      |
		// -------------------------------
		// |  Star      |  5, 8, 1       |
		// |  War       |  5, 2          |
		// |  Trek      |  1, 8          |
		// -------------------------------
		// When you search with keyword "Star", you will know immediately, by looking at Term column, and see that
		// there are 3 movie ids contains this word -- 1,5,8. Then, you can use these ids to find full movie object from repository.
		// Another case is when you find with keyword "Star War", there are 2 terms, Star and War, then you lookup
		// from inverted index for Star and for War so that you get movie ids 1,5,8 for Star and 2,5 for War. The result that
		// you have to return can be union or intersection of those 2 sets of ids.
		// By the way, in this assignment, you must use intersection so that it left for just movie id 5.
		if(InvertedIndexMovie.isEmpty()) {
			Iterable<Movie> iter_movies = movieRepository.findAll();
			for(Movie movie: iter_movies) {
				Long id = movie.getId();
				String name = movie.getName().toLowerCase();
				String[] words = name.split(" ");
				for(String word: words) {
					Set<Long> set = new HashSet<>(Collections.singletonList(id));
					set.add(id);
					if (InvertedIndexMovie.containsKey(word)) {
						set.addAll(InvertedIndexMovie.get(word));
						InvertedIndexMovie.replace(word, set);
					} else {
						InvertedIndexMovie.put(word, set);
					}
				}
			}
		}
		List<Set<Long>> lists_indexs = new ArrayList<>();

		for(String word: queryText.toLowerCase().split(" "))
			if(InvertedIndexMovie.containsKey(word.trim()))
				lists_indexs.add(InvertedIndexMovie.get(word.trim()));
			else
				lists_indexs.add(new HashSet<>());

		List<Movie> movies = new ArrayList<>();
		if (lists_indexs.size() > 0) {
			Set<Long> indexs = new HashSet<>(lists_indexs.get(0));
			lists_indexs.forEach(indexs::retainAll);
			movieRepository.findAllById(indexs).forEach(movies::add);
		}
		return movies;
	}
}
