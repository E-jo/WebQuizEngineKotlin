package engine.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class SolvedQuiz(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val quizId: Int,
    val completedAt: String,
    val userName: String
)

data class SolvedQuizDTO(
    val id: Int,
    val completedAt: String
)