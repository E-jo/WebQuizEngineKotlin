package engine.controllers

import engine.models.Quiz
import engine.models.UserDetailsAdapter
import engine.services.AnswerRequest
import engine.services.QuizService
import engine.services.SolvedQuizService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
class QuizController(
    private val quizService: QuizService,
    private val solvedQuizService: SolvedQuizService
) {

    @PostMapping("/api/quizzes")
    fun createQuiz(
        @RequestBody quiz: Quiz,
        @AuthenticationPrincipal user: UserDetailsAdapter
    ): ResponseEntity<*> {
        return quizService.createQuiz(
            quiz,
            user
        )
    }

    @GetMapping("/api/quizzes/{id}")
    fun getQuiz(@PathVariable id: Int): ResponseEntity<*> {
        return quizService.getQuiz(id)
    }

    @GetMapping("/api/quizzes")
    fun getAllQuizzes(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "pageSize", defaultValue = "10") pageSize: Int,
        @AuthenticationPrincipal user: UserDetailsAdapter
    ): ResponseEntity<*> {
        return quizService.getAllQuizzes(
            page,
            pageSize,
            user
        )
    }

    @GetMapping("api/quizzes/completed")
    fun getSolvedQuizzes(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "pageSize", defaultValue = "10") pageSize: Int,
        @RequestParam(name = "sortBy", defaultValue = "completedAt") sortBy: String?,
        @AuthenticationPrincipal user: UserDetailsAdapter
    ): ResponseEntity<*> {
        return solvedQuizService.getSolvedQuizzes(
            page,
            pageSize,
            sortBy,
            user
        )
    }

    @PostMapping("/api/quizzes/{id}/solve")
    fun solveQuiz(@PathVariable id: Int,
                  @RequestBody userAnswer: AnswerRequest,
                  @AuthenticationPrincipal user: UserDetailsAdapter): ResponseEntity<*> {

        return quizService.solveQuiz(
            id,
            userAnswer,
            user
        )
    }

    @DeleteMapping("/api/quizzes/{id}")
    fun deleteQuiz(@PathVariable id: Int, @AuthenticationPrincipal user: UserDetailsAdapter): ResponseEntity<*> {
        return quizService.deleteQuiz(
            id,
            user
        )
    }

}






