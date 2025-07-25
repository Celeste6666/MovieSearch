package ca.georgiancollege.movie

import MovieAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import ca.georgiancollege.movie.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var movieAdapter: MovieAdapter
    private val movies = mutableListOf<Movie>()

    private val apiKey = "aeb47a88"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        binding.buttonSearch.setOnClickListener {

            val query = binding.editTextSearch.text.toString()
            if (query.isNotBlank()) {
                searchResults(query)
            } else {
                Toast.makeText(this, "Enter the keyword...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchResults(query: String) {
    }
}