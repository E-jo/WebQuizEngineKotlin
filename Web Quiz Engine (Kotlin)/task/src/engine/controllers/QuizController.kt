package engine.controllers

import com.google.gson.Gson
import engine.models.*
import engine.services.QuizService
import engine.services.SolvedQuizService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@Validated
class QuizController {
    @Autowired
    lateinit var quizService: QuizService

    @Autowired
    lateinit var solvedQuizService: SolvedQuizService

    @GetMapping("/")
    fun checkGet(): ResponseEntity<*> {
        println("GET request to / received")
        return ResponseEntity("GET request to / received", HttpStatus.OK)
    }

    @PostMapping("/api/quizzes")
    fun createQuiz(@RequestBody quiz: Quiz, @AuthenticationPrincipal user: UserDetailsAdapter): ResponseEntity<*> {
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
        quizService.save(newQuiz)
        val currentId = quizService.findMaxId()

        val quizDTO = QuizDTO(
            currentId!!,
            quiz.title!!,
            quiz.text!!,
            quiz.options
        )
        return ResponseEntity(quizDTO, HttpStatus.OK)
    }

    @GetMapping("/api/quizzes/{id}")
    fun getQuiz(@PathVariable id: Int): ResponseEntity<*> {
        println("getQuiz($id)")
        val quizOptional = quizService.findById(id) ?:
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

    @GetMapping("/api/quizzes")
    fun getAllQuizzes(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "pageSize", defaultValue = "10") pageSize: Int,
        @AuthenticationPrincipal user: UserDetailsAdapter): ResponseEntity<*> {

        println("qetAllQuizzes()")
        println("page: $page")
        println("pageSize: $pageSize")
        var pageToUse = page
        var quizTotal: Long? = null
        try {
            quizTotal = quizService.findAll().totalElements
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
        val quizzesPage = quizService.findAll(pageable)

        println("calling convertToQuizPageResponse()")
        val quizzesPageResponse = convertToQuizPageResponse(quizzesPage)
        println("called convertToQuizPageResponse()")

        return ResponseEntity(Gson().toJson(quizzesPageResponse), HttpStatus.OK)
    }

    @GetMapping("api/quizzes/completed")
    fun getSolvedQuizzes(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "pageSize", defaultValue = "10") pageSize: Int,
        @RequestParam(name = "sortBy", defaultValue = "completedAt") sortBy: String?,
        @AuthenticationPrincipal user: UserDetailsAdapter
    ): ResponseEntity<*> {
        val pageable: Pageable = PageRequest.of(
            page,
            pageSize,
            Sort.by(sortBy).descending()
        )
        val solvedQuizzesPageResponse = convertToSolvedQuizPageResponse(
            solvedQuizService.findAllByUserName(user.username, pageable)
        )

        return ResponseEntity(
            solvedQuizzesPageResponse,
            HttpStatus.OK
        )
    }

    @PostMapping("/api/quizzes/{id}/solve")
    fun solveQuiz(@PathVariable id: Int,
                  @RequestBody userAnswer: AnswerRequest,
                  @AuthenticationPrincipal user: UserDetailsAdapter): ResponseEntity<*> {

        val quiz = quizService.findById(id)
        println(quiz.toString())
        println(userAnswer.toString())
        quiz?.let {
            if (quiz.isPresent) {
                quiz.get().let {
                    println("Correct answer: ${it.answer.contentToString()}")
                    println("User answer: ${userAnswer.answer.contentToString()}")

                    if (it.answer.contentEquals(userAnswer.answer)) {
                        solvedQuizService.save(SolvedQuiz(
                            null,
                            it.id!!,
                            LocalDateTime.now().toString(),
                            user.username)
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

    @DeleteMapping("/api/quizzes/{id}")
    fun deleteQuiz(@PathVariable id: Int, @AuthenticationPrincipal user: UserDetailsAdapter): ResponseEntity<*> {
        val quiz = quizService.findById(id)
        quiz?.let {
            if (quiz.isPresent) {
                if (quiz.get().author != user.username) {
                    return ResponseEntity<Any>(HttpStatus.FORBIDDEN)
                } else {
                    quizService.deleteById(id)
                    println("Deleted quiz ${quiz.get().id}")
                    return ResponseEntity<Any>(HttpStatus.NO_CONTENT)
                }
            }
        }
        return ResponseEntity<Any>(HttpStatus.NOT_FOUND)
    }
}

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