package ca.georgiancollege.movie

import MovieAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ca.georgiancollege.movie.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var movieAdapter: MovieAdapter

    private val auth = FirebaseAuth.getInstance()
    private val movies = mutableListOf<Movie>()

    private val apiKey = "aeb47a88"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()


        val user = auth.currentUser
        binding.movieFavoriteDirect.visibility = if (user != null) View.VISIBLE else View.GONE
        binding.buttonLogout.visibility = if (user != null) View.VISIBLE else View.GONE
        binding.buttonLogin.visibility = if (user != null) View.GONE else View.VISIBLE

        movieAdapter = MovieAdapter(movies) { movie ->
            Toast.makeText(this, "you click：${movie.title}", Toast.LENGTH_SHORT).show()
            // direct to MovieDetailsActivity
            val intent = Intent(this, MovieDetailsActivity::class.java).apply {
                putExtra("imdbID", movie.imdbID)
                putExtra("title", movie.title)
                putExtra("director", movie.director)
                putExtra("rating", movie.rating)
                putExtra("year", movie.year)
                putExtra("description", movie.description)
                putExtra("posterUrl", movie.posterUrl)
            }
            startActivity(intent)
        }
        binding.recyclerViewMovies.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMovies.adapter = movieAdapter

        binding.buttonSearch.setOnClickListener {

            val query = binding.editTextSearch.text.toString()
            if (query.isNotBlank()) {
                searchResults(query)
            } else {
                Toast.makeText(this, "Enter the keyword...", Toast.LENGTH_SHORT).show()
            }
        }

        binding.movieFavoriteDirect.setOnClickListener {
            startActivity(Intent(this, FavoriteListActivity::class.java)) // redirect to Login page
        }

        binding.buttonLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java)) // redirect to Login page
        }

        binding.buttonLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun searchResults(query: String) {
        val searchUrl = "https://www.omdbapi.com/?apikey=$apiKey&s=${query.trim()}"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(searchUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(response)

                    if (json.getString("Response") == "True") {
                        val searchArray = json.getJSONArray("Search")
                        val movieList = mutableListOf<Movie>()

                        for (i in 0 until searchArray.length()) {
                            val item = searchArray.getJSONObject(i)
                            val imdbID = item.getString("imdbID")

                            val detailsUrl = "https://www.omdbapi.com/?apikey=$apiKey&i=$imdbID"
                            val detailsConnection = URL(detailsUrl).openConnection() as HttpURLConnection
                            detailsConnection.requestMethod = "GET"
                            detailsConnection.connect()

                            val detailsResponse = detailsConnection.inputStream.bufferedReader().use { it.readText() }
                            val detailsJson = JSONObject(detailsResponse)
                            val movie = Movie(
                                imdbID = imdbID,
                                title = detailsJson.optString("Title", "N/A"),
                                director = detailsJson.optString("Director", "N/A"),
                                rating = detailsJson.optString("imdbRating", "N/A"),
                                year = detailsJson.optString("Year", "N/A"),
                                posterUrl = detailsJson.optString("Poster", ""),
                                description = detailsJson.optString("Plot", "No description")
                            )

                            movieList.add(movie)
                        }

                        runOnUiThread {
                            movies.clear()
                            movies.addAll(movieList)
                            movieAdapter.notifyDataSetChanged()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "no result", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            } catch (e: Exception) {
                Log.e("searchResults", "error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}