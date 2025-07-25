package ca.georgiancollege.movie

data class Movie(
    val imdbID: String,
    val title: String,
    val director: String,
    val rating: String,
    val year: String,
    val posterUrl: String = "",
    val description: String = ""
)