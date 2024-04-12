package engine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan
class WebQuizEngine

fun main(args: Array<String>) {
    runApplication<WebQuizEngine>(*args)
}
