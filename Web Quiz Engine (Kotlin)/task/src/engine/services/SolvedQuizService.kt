package engine.services

import engine.models.SolvedQuiz
import engine.models.UserDetailsAdapter
import engine.repositories.SolvedQuizRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.*
import org.springframework.stereotype.Service
import java.util.*

@Service
class SolvedQuizService {
    @Autowired
    lateinit var solvedQuizRepository: SolvedQuizRepository

    fun deleteById(id: Int?) {
        solvedQuizRepository.deleteById(id)
    }

    fun findAll(pageNo: Int?, pageSize: Int?): Page<SolvedQuiz>? {
        val paging: Pageable = PageRequest.of(pageNo!!, pageSize!!)

        val pagedResult: Page<SolvedQuiz>? = solvedQuizRepository.findAll(paging)

        return pagedResult
    }

    fun findAll(pageNo: Int?, pageSize: Int?, user: UserDetailsAdapter): Page<SolvedQuiz?>? {
        val paging: Pageable = PageRequest.of(
            pageNo!!,
            pageSize!!, Sort.by("completedAt").descending()
        )

        return solvedQuizRepository.findAll(paging)
    }
/*
    fun findAllByUserName(userName: String, pageNumber: Int, pageSize: Int): Page<SolvedQuiz> {
        val pageable: Pageable = PageRequest.of(pageNumber, pageSize)
        val page: Page<SolvedQuiz?>? = solvedQuizRepository.findAllByUserName(userName, pageable)
        val content: List<SolvedQuiz> = page?.content?.mapNotNull { it } ?: emptyList()
        return PageImpl(content, pageable, page?.totalElements ?: 0)
    }

 */

    fun findAllByUserName(userName: String, pageable: Pageable?): Page<SolvedQuiz> {
        println("solvedQuizService(): pageable: $pageable")
        val page: Page<SolvedQuiz?>? = solvedQuizRepository.findAllByUserName(userName, pageable)
        val content: List<SolvedQuiz> = page?.content?.mapNotNull { it } ?: emptyList()
        return PageImpl(content, page?.pageable ?: Pageable.unpaged(), page?.totalElements ?: 0)
    }

    fun findById(id: Int?): Optional<SolvedQuiz?>? {
        return solvedQuizRepository.findById(id)
    }

    fun save(quiz: SolvedQuiz?): SolvedQuiz? {
        return solvedQuizRepository.save(quiz)
    }
}