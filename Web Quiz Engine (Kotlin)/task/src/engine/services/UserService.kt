package engine.services

import engine.models.User
import engine.models.UserDetailsAdapter
import engine.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class UserService : UserDetailsService {
    @Autowired
    lateinit var userRepository: UserRepository

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
        var user = findByUsername(username)
        user = userRepository.findByUserName(username)
            ?: throw UsernameNotFoundException("Not found")

        return UserDetailsAdapter(user.get())
    }
}