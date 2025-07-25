package ca.georgiancollege.movie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ca.georgiancollege.movie.databinding.ActivityMovieDetailsBinding
import com.squareup.picasso.Picasso

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieTitle = intent.getStringExtra("title") ?: ""
        val movieDirector = intent.getStringExtra("director") ?: ""
        val movieRating = intent.getStringExtra("rating") ?: ""
        val movieYear = intent.getStringExtra("year") ?: ""
        val movieDescription = intent.getStringExtra("description") ?: ""
        val moviePosterUrl = intent.getStringExtra("posterUrl") ?: ""

        binding.Title.text = movieTitle
        binding.Director.text = "Director: $movieDirector"
        binding.Rating.text = "Rating: $movieRating"
        binding.Year.text = "Year: $movieYear"
        binding.Description.text = movieDescription

        if (moviePosterUrl.isNotEmpty()) {
            Picasso.get()
                .load(moviePosterUrl)
                .into(binding.Poster)
        }else {
            binding.Poster.setImageDrawable(null)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
