package engine.repositories

import engine.models.Quiz
import engine.models.SolvedQuiz
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface QuizRepository : PagingAndSortingRepository <Quiz, Int> {
    fun deleteById(id: Int?)
    override fun findAll(pageable: Pageable): Page<Quiz>
    fun findById(id: Int?): Optional<Quiz?>?
    fun save(quiz: Quiz?): Quiz?
    @Query("SELECT MAX(e.id) FROM Quiz e")
    fun findMaxId(): Int?
}