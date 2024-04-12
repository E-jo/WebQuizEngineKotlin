package engine.controllers

import engine.models.User
import engine.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@Validated
class UserController {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var encoder: BCryptPasswordEncoder

    @PostMapping("/api/register")
    fun registerUser(@RequestBody registerRequest: RegisterRequest): ResponseEntity<*> {
        val checkExisting: Optional<User?>? = userService.findByUsername(registerRequest.email)

        checkExisting?.let {
            if (checkExisting.isPresent) {
                if (checkExisting.get().userName == registerRequest.email) {
                    return ResponseEntity("That email is already registered", HttpStatus.BAD_REQUEST)
                }
            }
        }

        if (!validateRequest(registerRequest)) {
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        }
        userService.save(User(
            null,
            "ROLE_USER",
            registerRequest.email,
            encoder.encode(registerRequest.password))
        )

        return ResponseEntity("Successfully registered", HttpStatus.OK)
    }

    fun validateRequest(registerRequest: RegisterRequest): Boolean {
        return validateEmail(registerRequest.email) && registerRequest.password.length >= 5
    }

    fun validateEmail(email: String): Boolean {
        val regex = Regex("^[^@]+@[^@.]+\\.[^@.]+$")
        return regex.matches(email)
    }

}

data class RegisterRequest(
    val email: String,
    val password: String
)