package ca.georgiancollege.movie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ca.georgiancollege.movie.databinding.ActivityFavoriteListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoriteListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteListBinding
    private lateinit var adapter: FavoriteAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val movieList = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadFavoriteMovies()

        binding.movieDirect.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun setupRecyclerView() {
        adapter = FavoriteAdapter(movieList) { selectedMovie ->
            val intent = Intent(this, MovieDetailsActivity::class.java).apply {
                putExtra("MOVIE_TITLE", selectedMovie.title)
                putExtra("MOVIE_DIRECTOR", selectedMovie.director)
                putExtra("MOVIE_RATING", selectedMovie.rating)
                putExtra("MOVIE_YEAR", selectedMovie.year)
                putExtra("MOVIE_DESCRIPTION", selectedMovie.description)
                putExtra("MOVIE_POSTER_URL", selectedMovie.posterUrl)
            }
            startActivity(intent)
        }
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewFavorites.adapter = adapter

        binding.buttonLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadFavoriteMovies() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

       // clear previous data
        movieList.clear()

        firestore.collection("favorites")
            .document(user.uid)
            .collection("movies")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot.documents) {
                    try {
                        val movie = doc.toObject(Movie::class.java)
                        if (movie != null) {
                            movieList.add(
                                Movie(
                                    imdbID = movie.imdbID ?: "",
                                    title = movie.title ?: "",
                                    director = movie.director ?: "",
                                    rating = movie.rating ?: "",
                                    year = movie.year ?: "",
                                    posterUrl = movie.posterUrl ?: "",
                                    description = movie.description ?: ""
                                )
                            )
                        }
                    }  catch (e: Exception) {
                        Log.e("FirestoreError", "Error converting document", e)
                    }

                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("FavoriteListActivity", "Error loading favorites", e)
                Toast.makeText(this, "Failed to load favorites", Toast.LENGTH_SHORT).show()
            }
    }
}
