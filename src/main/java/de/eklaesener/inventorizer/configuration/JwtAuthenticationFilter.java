package de.eklaesener.inventorizer.configuration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    /* default */ static final String ROLE = "role";

    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";
    private static final String TYPE = "type";
    private static final long TOKEN_VALIDITY_IN_MILLISECONDS = 864_000_000;


    private final AuthenticationManager authenticationManager;

    private final SecurityProperties securityProperties;

    public JwtAuthenticationFilter(final AuthenticationManager authenticationManager,
                                   final SecurityProperties securityProperties) {
        super();
        this.authenticationManager = authenticationManager;
        this.securityProperties = securityProperties;
        setFilterProcessesUrl(this.securityProperties.getAuthLoginURL());
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) {
        final String username = request.getParameter(USER_NAME);
        final String password = request.getParameter(PASSWORD);
        final UsernamePasswordAuthenticationToken authenticationToken
            = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }


    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
                                            final FilterChain chain, final Authentication authResult) {
        final UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        final List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        final byte[] signingKey = securityProperties.getJwtSecret().getBytes();
        final String token = Jwts.builder()
            .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
            .setHeaderParam(TYPE, securityProperties.getTokenType())
            .setSubject(userDetails.getUsername())
            .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY_IN_MILLISECONDS)) // Ten days from now
            .claim(ROLE, roles)
            .compact();
        response.addHeader(securityProperties.getTokenHeader(), securityProperties.getTokenPrefix() + token);
    }
}
