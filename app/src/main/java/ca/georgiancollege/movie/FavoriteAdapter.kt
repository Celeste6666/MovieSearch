package ca.georgiancollege.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ca.georgiancollege.movie.databinding.MovieFavoriteItemLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoriteAdapter(
    private val movies: MutableList<Movie>,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(val binding: MovieFavoriteItemLayoutBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = MovieFavoriteItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val movie = movies[position]
        holder.binding.textViewTitle.text = movie.title
        holder.binding.textViewDirector.text = "Director: ${movie.director}"
        holder.binding.textViewRating.text = "Rating: ${movie.rating}"
        holder.binding.textViewYear.text = "Year: ${movie.year}"

        holder.binding.root.setOnClickListener {
            onItemClick(movie)
        }
        holder.binding.buttonDelete.setOnClickListener {
            val firestore = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser

            if(user != null){
                firestore.collection("favorites")
                    .document(user.uid)
                    .collection("movies")
                    .document(movie.imdbID)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(holder.binding.root.context, "Deleted successfully", Toast.LENGTH_SHORT).show()
                        // remove item
                        movies.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, movies.size - position)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(holder.binding.root.context, "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    override fun getItemCount() = movies.size
}
