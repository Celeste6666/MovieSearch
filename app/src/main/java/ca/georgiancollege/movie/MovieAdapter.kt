import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.georgiancollege.movie.Movie
import ca.georgiancollege.movie.databinding.MovieItemLayoutBinding

class MovieAdapter(
    private val movies: List<Movie>,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(val binding: MovieItemLayoutBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.binding.textViewTitle.text = movie.title
        holder.binding.textViewDirector.text = "Director: ${movie.director}"
        holder.binding.textViewRating.text = "Rating: ${movie.rating}"
        holder.binding.textViewYear.text = "Year: ${movie.year}"

        holder.binding.root.setOnClickListener {
            onItemClick(movie)
        }
    }

    override fun getItemCount() = movies.size
}
