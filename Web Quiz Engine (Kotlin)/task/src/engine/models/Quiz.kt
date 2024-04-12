package engine.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kotlinx.serialization.Transient
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

@Entity
data class Quiz(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    @field:NotEmpty
    val title: String?,
    @field:NotEmpty
    val text: String?,
    @JsonIgnore
    val author: String?,
    @field:Size(min = 2)
    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    val options: Array<String>?,
    @ElementCollection(fetch = FetchType.EAGER)
    val answer: Array<Int>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Quiz

        if (title != other.title) return false
        if (text != other.text) return false
        if (!options.contentEquals(other.options)) return false
        if (answer != other.answer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + options.contentHashCode()
        return result
    }
}
