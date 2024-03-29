package org.warestore.configuration.jwt;

import io.jsonwebtoken.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Log
@Component
public class JwtProvider {

    @Autowired
    Environment environment;

    public String getTokenFromRequest(HttpServletRequest request){
        String bearer = request.getHeader("Authorization");
        if (bearer!=null){
            bearer = bearer.replaceAll(" ","");
            if (bearer.startsWith("Bearer")){
                return bearer.substring(6);
            }
        }
        return null;
    }

    public String generateToken(String username){
        Date date = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512,environment.getProperty("jwt.secret"))
                .compact();
    }

    public boolean validateJWT(String token){
        try {
            Jwts.parser().setSigningKey(environment.getProperty("jwt.secret")).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            log.warning("Invalid token.");
            return false;
        }
    }

    public String getUsernameFromToken(String token){
        Claims claims = Jwts.parser().setSigningKey(environment.getProperty("jwt.secret")).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}
