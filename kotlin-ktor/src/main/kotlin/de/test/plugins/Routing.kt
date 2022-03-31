package de.test.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.test.model.User
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun Application.configureRouting() {
    var algorithm = Algorithm.HMAC256("secret")

    // Starting point for a Ktor app:
    routing {
        get("/") {
            call.respondText("Hello Kotlin/Ktor!")
        }
    }

    routing {
        post("/login"){
            var user = call.receive<User>()
            val token = JWT.create()
                .withIssuer("auth0")
                .withKeyId("0001")
                .withExpiresAt(Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant()))
                .withClaim("user", user.username)
                .withClaim("sub", 1L)
                .withClaim("admin", true)
                .sign(algorithm)
            call.respondText(token)
        }
    }
}
