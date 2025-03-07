package com.example.androidcicd;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.androidcicd.movie.Movie;
import com.example.androidcicd.movie.MovieProvider;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class MovieProviderTest {
    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock
    private CollectionReference mockMovieCollection;

    @Mock
    private DocumentReference mockDocRef;

    private MovieProvider movieProvider;

    @Before
    public void setUp() {
        // Start up mocks
        MockitoAnnotations.openMocks(this);
        // Define the behaviour we want during our tests. This part is what avoids the calls to firestore.
        when(mockFirestore.collection("movies")).thenReturn(mockMovieCollection);
        when(mockMovieCollection.document()).thenReturn(mockDocRef);
        when(mockMovieCollection.document(anyString())).thenReturn(mockDocRef);
        // Setup the movie provider
        MovieProvider.setInstanceForTesting(mockFirestore);
        movieProvider = MovieProvider.getInstance(mockFirestore);
    }

    @Test
    public void testAddMovieSetsId() {
        // Movie to add
        Movie movie = new Movie("Oppenheimer", "Thriller/Historical Drama", 2023);

        // Define the ID we want to set for the movie
        when(mockDocRef.getId()).thenReturn("123");


        // Add movie and check that we update our movie with the generated id
        movieProvider.addMovie(movie);
        assertEquals("Movie was not updated with correct id.", "123", movie.getId());

    /*
        Verify that we called the set method. Normally, this would call the database, but due to what we did in our setup method, this will not actually interact with the database, but the mock does track that the method was called.
    */
        verify(mockDocRef).set(movie);
    }
    @Test
    public void testDeleteMovie() {
        // Create movie and set our id
        Movie movie = new Movie("Oppenheimer", "Thriller/Historical Drama", 2023);
        movie.setId("123");

        // Call the delete movie and verify the firebase delete method was called.
        movieProvider.deleteMovie(movie);
        verify(mockDocRef).delete();
    }
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateMovieShouldThrowErrorForDifferentIds() {
        Movie movie = new Movie("Oppenheimer", "Thriller/Historical Drama", 2023);
        // Set our ID to 1
        movie.setId("1");

        // Make sure the doc ref has a different ID
        when(mockDocRef.getId()).thenReturn("123");

        // Call update movie, which should throw an error
        movieProvider.updateMovie(movie, "Another Title", "Another Genre", 2026);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateMovieShouldThrowErrorForEmptyName() {
        Movie movie = new Movie("Oppenheimer", "Thriller/Historical Drama", 2023);
        movie.setId("123");
        when(mockDocRef.getId()).thenReturn("123");

        // Call update movie, which should throw an error due to having an empty name
        movieProvider.updateMovie(movie, "", "Another Genre", 2026);
    }

    @Test
    public void testValidTitle() {
            Movie movie = new Movie("Oppenheimer", "Thriller/Historical Drama", 2023);
            movie.setId("123");

            // Define the ID we want to set for the movie
            when(mockDocRef.getId()).thenReturn("123");
            //assertTrue(movieProvider.checkMovieName(movie.getTitle()));
            movieProvider.addMovie(movie);


            Movie movie2 = new Movie("Oppenheimer", "Thriller/Historical Drama", 2023);
            movie2.setId("321");
            movieProvider.addMovie(movie2);
            //assertFalse(movieProvider.checkMovieName(movie2.getTitle()));

            when(mockDocRef.getId()).thenReturn("321");

            assertEquals("Movie was not updated with correct id.", "123", movie2.getId());


    }


    /*
@Test
public void testValidTitle() {
    Movie movie = new Movie("Oppenheimer", "Thriller/Historical Drama", 2023);
    movie.setId("123");

    // Define the ID we want to set for the movie
    when(mockDocRef.getId()).thenReturn("123");
    movieProvider.addMovie(movie);

    Movie movie2 = new Movie("Oppenheimer", "Thriller/Historical Drama", 2023);
    movie2.setId("321");

    // Mock the behavior of Firestore to simulate a movie already in the database
    // Mock the collection and query behavior
    Query mockQuery = mock(Query.class);
    when(mockMovieCollection.whereEqualTo("title", "Oppenheimer")).thenReturn(mockQuery);

    // Mock the behavior of the query's get() method to return a task with a result
    Task<QuerySnapshot> mockTask = mock(Task.class);
    QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
    when(mockTask.isSuccessful()).thenReturn(true); // Simulate a successful task
    when(mockTask.getResult()).thenReturn(mockQuerySnapshot); // Simulate query result

    when(mockQuery.get()).thenReturn(mockTask); // Mock get() on the query object

    // Simulate that the query result contains documents (i.e., the title already exists)
    when(mockQuerySnapshot.size()).thenReturn(1); // Simulate that there's one result

    // The checkMovieName method should return false since the title "Oppenheimer" is already taken
    assertFalse(movieProvider.checkMovieName(movie2.getTitle()));
}
*/

}
