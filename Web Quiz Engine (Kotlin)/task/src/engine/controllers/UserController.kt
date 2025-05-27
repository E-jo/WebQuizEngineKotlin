package engine.controllers

import engine.models.User
import engine.services.RegisterRequest
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

    @PostMapping("/api/register")
    fun registerUser(@RequestBody registerRequest: RegisterRequest): ResponseEntity<*> {
        return userService.registerUser(registerRequest)
    }

}

