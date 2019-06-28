package com.wongnai.interview.movie.external;

import java.io.IOException;

public interface MovieDataService {
	MoviesResponse fetchAll() throws IOException;
}
