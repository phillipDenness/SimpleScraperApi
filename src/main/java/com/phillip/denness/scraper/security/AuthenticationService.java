package com.phillip.denness.scraper.security;

import com.phillip.denness.scraper.config.AuthenticationProperties;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;

@Component
public class AuthenticationService {

    private static AuthenticationProperties authenticationProperties;
    private static final String PREFIX = "Bearer";

    @Autowired
    public AuthenticationService(AuthenticationProperties authenticationProperties) {
        AuthenticationService.authenticationProperties = authenticationProperties;
    }

    public static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null) {
            String user = Jwts.parser()
                    .setSigningKey(authenticationProperties.getSigning_key())
                    .parseClaimsJws(token.replace(PREFIX, ""))
                    .getBody()
                    .getSubject();

            if (isNotExpired(token)) {
                if (user != null) {
                    return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
                }
            }
        }
        return null;
    }

    private static boolean isNotExpired(String token) {

        Date issuedAt = Jwts.parser()
                .setSigningKey(authenticationProperties.getSigning_key())
                .parseClaimsJws(token.replace(PREFIX, ""))
                .getBody()
                .getIssuedAt();

        Date now = new Date();
        long diff = now.getTime() - issuedAt.getTime();
        long diffMinutes = diff / (60 * 1000);
        return !(diffMinutes >= authenticationProperties.getToken_expiration());
    }
}
