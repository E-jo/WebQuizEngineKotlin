package engine.controllers

import engine.services.RegisterRequest
import engine.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
class UserController(
    private val userService: UserService
) {

    @PostMapping("/api/register")
    fun registerUser(@RequestBody registerRequest: RegisterRequest): ResponseEntity<*> {
        return userService.registerUser(registerRequest)
    }

}

