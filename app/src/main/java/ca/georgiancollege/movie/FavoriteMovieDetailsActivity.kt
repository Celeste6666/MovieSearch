package ca.georgiancollege.movie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ca.georgiancollege.movie.databinding.FavoriteMovieDetailsBinding
import com.squareup.picasso.Picasso
import com.google.firebase.auth.FirebaseAuth
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class FavoriteMovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: FavoriteMovieDetailsBinding
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FavoriteMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = auth.currentUser
        binding.movieFavoriteDirect.visibility = if (user != null) View.VISIBLE else View.GONE
        binding.buttonLogin.visibility = if (user != null) View.GONE else View.VISIBLE
        binding.buttonLogout.visibility = if (user != null) View.VISIBLE else View.GONE

        val movie = Movie(
            title = intent.getStringExtra("title") ?: "",
            director = intent.getStringExtra("director") ?: "",
            rating = intent.getStringExtra("rating") ?: "",
            year = intent.getStringExtra("year") ?: "",
            description = intent.getStringExtra("description") ?: "",
            posterUrl = intent.getStringExtra("posterUrl") ?: "",
            imdbID = intent.getStringExtra("imdbID") ?: ""
        )


        binding.Title.text = movie.title
        binding.Director.text = "Director: ${movie.director}"
        binding.Rating.text = "Rating: ${movie.rating}"
        binding.Year.text = "Year: ${movie.year}"
        binding.Description.text = movie.description

        if (movie.posterUrl.isNotEmpty()) {
            Picasso.get()
                .load(movie.posterUrl)
                .into(binding.Poster)
        }else {
            binding.Poster.setImageDrawable(null)
        }

        binding.btnBack.setOnClickListener {
            finish()
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

        binding.buttonEdit.setOnClickListener {

            // direct to EditPage
            val intent = Intent(this, FavoriteEditMovieActivity::class.java).apply {
                putExtra("imdbID", movie.imdbID)
                putExtra("title", movie.title)
                putExtra("director", movie.director)
                putExtra("rating", movie.rating)
                putExtra("year", movie.year)
                putExtra("description", movie.description)
                putExtra("posterUrl", movie.posterUrl)
            }
            Log.d("EditButton", "Sending imdbID: ${movie.imdbID}")
            startActivity(intent)
        }

        binding.buttonDelete.setOnClickListener {
            val firestore = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser

            if(user != null){
                firestore.collection("favorites")
                    .document(user.uid)
                    .collection("movies")
                    .document(movie.imdbID)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
