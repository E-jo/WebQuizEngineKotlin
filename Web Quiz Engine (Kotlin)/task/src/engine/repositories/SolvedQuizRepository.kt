package engine.repositories

import engine.models.SolvedQuiz
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SolvedQuizRepository : PagingAndSortingRepository<SolvedQuiz, Int> {
    fun deleteById(id: Int?)
    fun findAllByUserName(userName: String, pageable: Pageable?): Page<SolvedQuiz?>?
    fun findById(id: Int?): Optional<SolvedQuiz?>?
    fun save(quiz: SolvedQuiz?): SolvedQuiz?
}