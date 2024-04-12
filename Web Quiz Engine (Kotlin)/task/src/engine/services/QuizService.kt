package engine.services

import engine.models.Quiz
import engine.models.SolvedQuiz
import engine.repositories.QuizRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties
import org.springframework.data.domain.*
import org.springframework.stereotype.Service
import java.util.*

@Service
class QuizService {
    @Autowired
    lateinit var quizRepository: QuizRepository

    fun deleteById(id: Int?) {
        quizRepository.deleteById(id)
    }

    fun findAll(pageable: Pageable?): Page<Quiz> {
        val pageToUse = pageable ?:
            PageRequest.of(0, 10, Sort.by("id").ascending())
        println("findAll() with pageable: $pageToUse")
        val page: Page<Quiz> = quizRepository.findAll(pageToUse)
        println("findAll() results: ${page.totalElements}")
        val content: MutableList<Quiz?> = page.content
        return PageImpl(
            content,
            page.pageable,
            page.totalElements
        )
    }

    fun findAll(): Page<Quiz>  {
        return quizRepository.findAll(PageRequest.of(0, 10, Sort.by("id").ascending()))
    }

    fun findById(id: Int?): Optional<Quiz?>? {
        return quizRepository.findById(id)
    }

    fun save(quiz: Quiz?): Quiz? {
        return quizRepository.save(quiz)
    }

    fun findMaxId(): Int? {
        return quizRepository.findMaxId()
    }

}