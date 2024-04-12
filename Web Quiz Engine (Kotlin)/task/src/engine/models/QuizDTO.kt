package engine.models

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class QuizDTO(
    @Id
    val id: Int,
    val title: String,
    val text: String,
    val options: Array<String>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QuizDTO

        if (id != other.id) return false
        if (title != other.title) return false
        if (text != other.text) return false
        if (!options.contentEquals(other.options)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + options.contentHashCode()
        return result
    }
}