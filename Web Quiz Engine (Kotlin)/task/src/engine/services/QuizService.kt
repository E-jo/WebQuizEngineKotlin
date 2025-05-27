package engine.services

import com.google.gson.Gson
import engine.models.*
import engine.repositories.QuizRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class QuizService {
    @Autowired
    lateinit var quizRepository: QuizRepository

    @Autowired
    @Lazy
    lateinit var solvedQuizService: SolvedQuizService

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

    fun createQuiz(
        quiz: Quiz,
        user: UserDetailsAdapter
    ): ResponseEntity<*> {
        println("createQuiz()")
        println(quiz.toString())
        if (quiz.options == null) {
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        }
        if (quiz.options.isEmpty()) {
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        }

        println("Quiz options: ${quiz.options.contentToString()}")

        println("Quiz answers: ${quiz.answer.contentToString()}")

        val newQuiz = Quiz(
            null,
            quiz.title,
            quiz.text,
            user.username,
            quiz.options,
            quiz.answer ?: arrayOf()
        )
        save(newQuiz)
        val currentId = findMaxId()

        val quizDTO = QuizDTO(
            currentId!!,
            quiz.title!!,
            quiz.text!!,
            quiz.options
        )
        return ResponseEntity(quizDTO, HttpStatus.OK)
    }

    fun getQuiz(id: Int): ResponseEntity<*> {
        println("getQuiz($id)")
        val quizOptional = findById(id) ?:
        return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        if (quizOptional.isEmpty) {
            println("Quiz $id not found")
            return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        }
        val quiz: Quiz = quizOptional.get()
        quiz.let {
            val quizDTO = it.id?.let {
                    it1 -> QuizDTO(
                it1,
                it.title ?: "",
                it.text ?: "",
                it.options ?: arrayOf<String>()
            )
            }
            return ResponseEntity(quizDTO, HttpStatus.OK)
        }
    }

    fun getAllQuizzes(
        page: Int,
        pageSize: Int,
        user: UserDetailsAdapter
    ): ResponseEntity<*> {

        println("qetAllQuizzes()")
        println("page: $page")
        println("pageSize: $pageSize")
        var pageToUse = page
        var quizTotal: Long? = null
        try {
            quizTotal = findAll().totalElements
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
        println("Total elements: $quizTotal")
        if (quizTotal != null) {
            if (quizTotal < pageSize) {
                pageToUse = 0
            }
        }

        val pageable: Pageable = PageRequest.of(
            pageToUse,
            pageSize,
            Sort.by("id").ascending()
        )
        println("calling quizService.findAll()")
        val quizzesPage = findAll(pageable)

        println("calling convertToQuizPageResponse()")
        val quizzesPageResponse = convertToQuizPageResponse(quizzesPage)
        println("called convertToQuizPageResponse()")

        return ResponseEntity(Gson().toJson(quizzesPageResponse), HttpStatus.OK)
    }

    fun solveQuiz(id: Int,
                  userAnswer: AnswerRequest,
                  user: UserDetailsAdapter
    ): ResponseEntity<*> {

        val quiz = findById(id)
        println(quiz.toString())
        println(userAnswer.toString())
        quiz?.let {
            if (quiz.isPresent) {
                quiz.get().let {
                    println("Correct answer: ${it.answer.contentToString()}")
                    println("User answer: ${userAnswer.answer.contentToString()}")

                    if (it.answer.contentEquals(userAnswer.answer)) {
                        solvedQuizService.save(
                            SolvedQuiz(
                                null,
                                it.id!!,
                                LocalDateTime.now().toString(),
                                user.username
                            )
                        )
                        println("Saved correct answer for quiz ${it.id} for user ${user.username}")

                        return ResponseEntity(
                            Response(
                                true, "Congratulations, you're right!"
                            ), HttpStatus.OK
                        )
                    } else {
                        return ResponseEntity(
                            Response(
                                false, "Wrong answer! Please, try again."
                            ), HttpStatus.OK
                        )
                    }
                }
            }
        }

        return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
    }

    fun deleteQuiz(
        id: Int,
        user: UserDetailsAdapter
    ): ResponseEntity<*> {
        val quiz = findById(id)
        quiz?.let {
            if (quiz.isPresent) {
                if (quiz.get().author != user.username) {
                    return ResponseEntity<Any>(HttpStatus.FORBIDDEN)
                } else {
                    deleteById(id)
                    println("Deleted quiz ${quiz.get().id}")
                    return ResponseEntity<Any>(HttpStatus.NO_CONTENT)
                }
            }
        }
        return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
    }

    fun convertToQuizPageResponse(page: Page<Quiz>): QuizPageResponse {
        println("convertToQuizPageResponse(): content: ${page.content}")
        val quizDTOs = page.content.map { quiz ->
            quiz.id?.let {
                QuizDTO(
                    id = it,
                    title = quiz.title ?: "",
                    text = quiz.text ?: "",
                    options = quiz.options ?: arrayOf()
                )
            }
        }
        return QuizPageResponse(
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
            content = quizDTOs
        )
    }

}

data class QuizPageResponse(
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
    val content: List<QuizDTO?>,
)

data class AnswerRequest(var answer: Array<Int>?) {
    init {
        answer = answer ?: arrayOf()
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AnswerRequest

        return answer.contentEquals(other.answer)
    }

    override fun hashCode(): Int {
        return answer.contentHashCode()
    }
}

