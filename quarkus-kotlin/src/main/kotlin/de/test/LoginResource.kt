package de.test

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Path("/login")
class LoginResource {
    private val algorithm: Algorithm = Algorithm.HMAC256("secret")

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    fun postLogin(user: User): String {
        var token = ""
        try {
//            Algorithm algorithm = Algorithm.HMAC256("secret");
//            Map<String,Object> map = new HashMap<>();
//            map.put("Content-Type", "application/json");
            token = JWT.create() //                .withHeader(map)
                .withIssuer("auth0")
                .withClaim("user", user.username)
                .withClaim("sub", 1)
                .withClaim("admin", true)
                .withExpiresAt(Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant()))
                .withKeyId("0001")
                .sign(algorithm)
        } catch (exception: JWTCreationException) {
            //Invalid Signing configuration / Couldn't convert Claims.
        }
        return token
    }
}