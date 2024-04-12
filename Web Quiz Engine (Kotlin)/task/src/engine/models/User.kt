package engine.models

import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "quiz_user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    var authority: String,
    @NotEmpty
    val userName: String,
    @NotEmpty
    val password: String
)