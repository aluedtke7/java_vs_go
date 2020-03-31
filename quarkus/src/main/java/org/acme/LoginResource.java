package org.acme;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/login")
public class LoginResource {
    private Algorithm algorithm = Algorithm.HMAC256("secret");

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postLogin(User user) {
        String token = "";
        try {
//            Algorithm algorithm = Algorithm.HMAC256("secret");
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
        } catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
        }
        return token;
    }
}