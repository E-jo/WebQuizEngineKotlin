package engine.services

import engine.models.SolvedQuiz
import engine.models.SolvedQuizDTO
import engine.models.UserDetailsAdapter
import engine.repositories.SolvedQuizRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
            pageSize!!,
            Sort.by("completedAt").descending()
        )

        return solvedQuizRepository.findAll(paging)
    }

    fun findAllByUserName(userName: String, pageNumber: Int, pageSize: Int): Page<SolvedQuiz> {
        val pageable: Pageable = PageRequest.of(pageNumber, pageSize)
        val page: Page<SolvedQuiz?>? = solvedQuizRepository.findAllByUserName(userName, pageable)
        val content: List<SolvedQuiz> = page?.content?.mapNotNull { it } ?: emptyList()
        return PageImpl(content, pageable, page?.totalElements ?: 0)
    }

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

    fun getSolvedQuizzes(
        page: Int,
        pageSize: Int,
        sortBy: String?,
        user: UserDetailsAdapter
    ): ResponseEntity<*> {
        val pageable: Pageable = PageRequest.of(
            page,
            pageSize,
            Sort.by(sortBy).descending()
        )
        val solvedQuizzesPageResponse = convertToSolvedQuizPageResponse(
            findAllByUserName(user.username, pageable)
        )

        return ResponseEntity(
            solvedQuizzesPageResponse,
            HttpStatus.OK
        )
    }

    fun convertToSolvedQuizPageResponse(page: Page<SolvedQuiz>): SolvedQuizPageResponse {
        val solvedQuizDTOs = page.content.map { solvedQuiz ->
            SolvedQuizDTO(
                id = solvedQuiz.quizId,
                completedAt = solvedQuiz.completedAt
            )
        }
        return SolvedQuizPageResponse(
            totalPages = page.totalPages,
            totalElements = page.totalElements,
            last = page.isLast,
            first = page.isFirst,
            sort = page.sort,
            number = page.number,
            numberOfElements = page.numberOfElements,
            size = page.size,
            empty = page.isEmpty,
            pageable = page.pageable,
            content = solvedQuizDTOs
        )
    }
}

data class SolvedQuizPageResponse(
    val totalPages: Int,
    val totalElements: Long,
    val last: Boolean,
    val first: Boolean,
    val sort: Any,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val empty: Boolean,
    val pageable: Any,
    val content: List<SolvedQuizDTO>
)

