package backend.gatewayservice.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static java.security.KeyRep.Type.SECRET;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (request.getURI().getPath().contains("auth")) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Token inválido");
        }

        String token = authHeader.substring(7);
        try {
            Algorithm algorithm = Algorithm.HMAC256("2dd57dfe27e671d7fad1d9517900518607dfb1d0a4718105b2e69986349d0a83");
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String roles = decodedJWT.getClaim("authorities").toString();

            ServerHttpRequest newRequest = exchange.getRequest()
                    .mutate()
                    .header("Authorization", "Bearer " + token)
                    .header("roles", roles)
                    .header("user", decodedJWT.getSubject())
                    .build();

            return chain.filter(exchange.mutate().request(newRequest).build());

        } catch (Exception e) {
            return unauthorized(exchange, "Token inválido: " + e.getMessage());
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String mensaje) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        byte[] bytes = mensaje.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
