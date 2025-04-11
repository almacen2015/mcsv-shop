package backend.mcsvauth.filter;

import backend.mcsvauth.util.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

//cuando se hace una peticion al servidor, este filtro se encarga de validar el token(siempre)
public class JwtTokenValidator extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    public JwtTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (esUriSwagger(request, response, filterChain)) return;

        if (jwtToken != null) {
            jwtToken = jwtToken.substring(7);

            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);

            String username = jwtUtils.extractUsername(decodedJWT);
            String stringRoles = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();

            //se obtienen los roles del usuario logueado y se convierten en una lista de GrantedAuthority
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(stringRoles);

            //se obtiene el contexto de seguridad
            SecurityContext context = SecurityContextHolder.getContext();

            //se crea un objeto de tipo Authentication y se le asigna el usuario logueado
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);

            //se asigna el objeto Authentication al objeto SecurityContext

            //se asigna el objeto SecurityContext al contexto de seguridad
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }

        filterChain.doFilter(request, response);
    }

    private boolean esUriSwagger(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String[] swaggerWhiteList = {
                "/swagger-ui",
                "/v3/api-docs"
        };

        String uri = request.getRequestURI();

        for (String path : swaggerWhiteList) {
            if (uri.contains(path)) {
                filterChain.doFilter(request, response);
                return true;
            }
        }
        return false;
    }
}
