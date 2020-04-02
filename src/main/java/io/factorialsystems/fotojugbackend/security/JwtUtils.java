package io.factorialsystems.fotojugbackend.security;


import io.factorialsystems.fotojugbackend.service.auth.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtils {

    @Value("${factorialsystems.app.jwtSecret}")
    private String jwtSecret;

    @Value("${factorialsystems.app.jwtExpirationMs}")
    private Integer jwtExpirationMs;

    public String generateJwtToken(@NotNull Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl)authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT Signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT Token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT Token has expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT Token is Unsupported : {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT Claims String is empty : {}", e.getMessage());
        }

        return false;
    }


    // Experimental
    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

}
