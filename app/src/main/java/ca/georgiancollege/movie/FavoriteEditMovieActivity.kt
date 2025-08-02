package ca.georgiancollege.movie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.georgiancollege.movie.databinding.FavoriteEditMovieBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoriteEditMovieActivity : AppCompatActivity() {

    private lateinit var binding: FavoriteEditMovieBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var isEditMode = false
    private var movieId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FavoriteEditMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = auth.currentUser
        binding.buttonLogout.visibility = if (user != null) View.VISIBLE else View.GONE

        movieId = intent.getStringExtra("imdbID") ?: ""
        isEditMode = movieId != null && movieId != ""

        Log.d("EditPage", "Received imdbID: $movieId")

        binding.buttonSave.setOnClickListener {
            saveMovieData()
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }

    private fun loadMovieData() {
        val user = auth.currentUser ?: return
        firestore.collection("favorites")
            .document(user.uid)
            .collection("movies")
            .document(movieId!!)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    Log.d("FavoriteEditMovie", "Document data: ${doc.data}")
                    binding.editTitle.setText(doc.getString("title"))
                    binding.editYear.setText(doc.getString("year"))
                    binding.editDirector.setText(doc.getString("director"))
                    binding.editRating.setText(doc.getString("rating"))
                    binding.editDescription.setText(doc.getString("description"))
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveMovieData() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val title = binding.editTitle.text.toString().trim()
        val year = binding.editYear.text.toString().trim()
        val director = binding.editDirector.text.toString().trim()
        val rating = binding.editRating.text.toString().trim()
        val description = binding.editDescription.text.toString().trim()
        val posterUrl = intent.getStringExtra("posterUrl") ?: ""

        if (title.isEmpty() || year.isEmpty() || rating.isEmpty()) {
            Toast.makeText(this, "Title, Year and Rating are required", Toast.LENGTH_SHORT).show()
            return
        }

        var movieId = movieId ?: "$title$year"

        var movieData = Movie(
            title,
            director,
            rating,
            year,
            description,
            posterUrl,
        )

        firestore.collection("favorites")
            .document(user.uid)
            .collection("movies")
            .document(movieId)
            .set(movieData)
            .addOnSuccessListener {
                Toast.makeText(this, "Movie saved", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save movie", Toast.LENGTH_SHORT).show()
            }
    }
}
