package org.warestore.configuration.jwt;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.warestore.configuration.CustomUserDetails;
import org.warestore.service.CustomUserDetailsService;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log
@Component
public class JwtFilter extends GenericFilterBean {

    @Autowired
    private Environment environment;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private CustomUserDetailsService userDetailsService;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String appVerificationCode = getAppVerificationCodeFromRequest((HttpServletRequest)servletRequest);
        if (validateApplication(appVerificationCode)) {
            String token = jwtProvider.getTokenFromRequest((HttpServletRequest) servletRequest);
            if (token != null) {
                if (jwtProvider.validateJWT(token)) {
                    log.info("The filter passes the request.");
                    String username = jwtProvider.getUsernameFromToken(token);
                    CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(servletRequest, servletResponse);
        }
        else
            ((HttpServletResponse) servletResponse).sendError(401);
    }

    private String getAppVerificationCodeFromRequest(HttpServletRequest request){
        return request.getHeader("App-Verification");
    }

    private boolean validateApplication(String key){
        if (key==null)return false;
        else return passwordEncoder.matches(environment.getProperty("app.secret"),key);
    }
}
