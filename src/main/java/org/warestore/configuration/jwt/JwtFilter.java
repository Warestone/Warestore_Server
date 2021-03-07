package org.warestore.configuration.jwt;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Log
@Component
public class JwtFilter extends GenericFilterBean {

    final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider) { this.jwtProvider = jwtProvider; }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("Filter is working.");
        String token = getTokenFromRequest((HttpServletRequest) servletRequest);
        if (token!=null){
            if (jwtProvider.validateJWT(token)){
                String username = jwtProvider.getUsernameFromToken(token);
            }
        }
    }

    private String getTokenFromRequest(HttpServletRequest request){
        String bearer = request.getHeader("Authorization");
        if (bearer != null){
            if (bearer.startsWith("Bearer ")){
                return bearer.substring(7);
            }
        }
        return null;
    }
}
