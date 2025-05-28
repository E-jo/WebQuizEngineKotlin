package engine.services

import engine.models.User
import engine.models.UserDetailsAdapter
import engine.repositories.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository
) : UserDetailsService {

    @Bean
    fun encoder(): BCryptPasswordEncoder =
        BCryptPasswordEncoder()

    fun findAll(): List<User?>? {
        return userRepository.findAll()
    }

    fun findByUsername(username: String?): Optional<User?>? {
        println("findByUsername for $username")
        return userRepository.findByUserName(username)
    }


    fun save(user: User?): User? {
        return userRepository.save(user)
    }

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUserName(username)
            ?: throw UsernameNotFoundException("Not found")

        return UserDetailsAdapter(user.get())
    }

    fun registerUser(
        registerRequest: RegisterRequest
    ): ResponseEntity<*> {
        val checkExisting: Optional<User?>? = findByUsername(registerRequest.email)

        checkExisting?.let {
            if (checkExisting.isPresent) {
                if (checkExisting.get().userName == registerRequest.email) {
                    return ResponseEntity(
                        "That email is already registered",
                        HttpStatus.BAD_REQUEST
                    )
                }
            }
        }

        if (!validateRequest(registerRequest)) {
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        }
        save(User(
            null,
            "ROLE_USER",
            registerRequest.email,
            encoder().encode(registerRequest.password))
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