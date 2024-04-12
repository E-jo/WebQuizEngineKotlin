package engine

import engine.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Autowired
    lateinit var userService: UserService

    @Bean
    fun encoder(): BCryptPasswordEncoder =
        BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .authorizeHttpRequests { auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/register",
                    "/actuator/shutdown").permitAll()
                .requestMatchers(HttpMethod.GET, "/").permitAll()
                .anyRequest().authenticated()
            }
            .httpBasic()
            .and()
            .userDetailsService(userService)
            .csrf().disable()
            /*
            .sessionManagement {
                    session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

             */
            .build()
}

