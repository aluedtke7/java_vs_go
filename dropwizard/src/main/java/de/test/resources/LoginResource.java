package de.test.resources;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.codahale.metrics.annotation.Timed;

import de.test.api.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {
    private final AtomicLong counter;
    private Algorithm algorithm = Algorithm.HMAC256("secret");

    public LoginResource() {
        this.counter = new AtomicLong();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postLogin(User user) {
        String token = "";
        try {
//            Map<String,Object> map = new HashMap<>();
//            map.put("Content-Type", "application/json");
            token = JWT.create()
//                .withHeader(map)
                .withIssuer("auth0")
                .withClaim("user", user.getUsername())
                .withClaim("sub", 1)
                .withClaim("admin", true)
                .withExpiresAt(Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant()))
                .withKeyId("0001")
                .sign(algorithm);
        } catch (JWTCreationException exception) {
            //Invalid Signing configuration / Couldn't convert Claims.
        }
        return token;
    }
}
