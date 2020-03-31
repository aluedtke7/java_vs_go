package com.example.demo;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

@RestController
public class LoginController {
    private Algorithm algorithm = Algorithm.HMAC256("secret");

    @GetMapping("/login")
    public String getHello() {
        return "Hello evodion";
    }

    @PostMapping("/login")
    public ResponseEntity<String> postLogin(@RequestBody User user) {
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
        return new ResponseEntity<String>(token, HttpStatus.OK);
    }
}
