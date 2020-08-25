package de.eklaesener.inventorizer.configuration;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    private final SecurityProperties securityProperties;

    public JwtAuthorizationFilter(
        final AuthenticationManager authenticationManager, final SecurityProperties securityProperties) {
        super(authenticationManager);
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(
        final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
        throws IOException, ServletException {
        final UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);
        if (authenticationToken != null) {
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(final HttpServletRequest request) {
        final String token = request.getHeader(securityProperties.tokenHeader());
        final UsernamePasswordAuthenticationToken parsedToken;
        if (token != null && !token.isEmpty() && token.startsWith(securityProperties.tokenPrefix())) {
            parsedToken = parseToken(token);
        } else {
            parsedToken = null;
        }
        return parsedToken;
    }

    private UsernamePasswordAuthenticationToken parseToken(final String token) {
        UsernamePasswordAuthenticationToken result = null;
        try {
            final byte[] signingKey = securityProperties.jwtSecret().getBytes();
            final Jws<Claims> parsedToken = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token.replace(securityProperties.tokenPrefix(), "").strip());
            final String username = parsedToken.getBody().getSubject();
            final List<SimpleGrantedAuthority> authorities = ((List<?>) parsedToken.getBody()
                .get(JwtAuthenticationFilter.ROLE)).stream()
                .map(authority -> new SimpleGrantedAuthority((String) authority))
                .collect(Collectors.toList());
            if (username != null && !username.isBlank()) {
                result = new UsernamePasswordAuthenticationToken(username, null, authorities);
            }
        } catch (final ExpiredJwtException e) {
            LOGGER.warn("Request to parse expired JWT: {} failed : {}", token, e.getMessage());
        } catch (final UnsupportedJwtException e) {
            LOGGER.warn("Request to parse unsupported JWT : {} failed : {}", token, e.getMessage());
        } catch (final MalformedJwtException e) {
            LOGGER.warn("Request to parse invalid JWT : {} failed : {}", token, e.getMessage());
        } catch (final SignatureException e) {
            LOGGER.warn("Request to parse JWT with invalid signature : {} failed : {}", token, e.getMessage());
        } catch (final IllegalArgumentException e) {
            LOGGER.warn("Request to parse empty or null JWT : {} failed : {}", token, e.getMessage());
        }
        return result;
    }
}
