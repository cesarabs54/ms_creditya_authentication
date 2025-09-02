package co.com.bancolombia.usecase.api.security;

import co.com.bancolombia.model.dtos.RefreshAccessTokenRequest;
import co.com.bancolombia.model.dtos.TokenRefreshResponse;
import co.com.bancolombia.model.gateways.JwtGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RefreshAccessTokenUseCase {

    private final RefreshTokenUseCase refreshTokenUseCase;
    private final JwtGateway jwtGateway;

    public Mono<TokenRefreshResponse> execute(RefreshAccessTokenRequest request) {
        String refreshToken = request.refreshToken();

        return refreshTokenUseCase.findByToken(refreshToken)
                .flatMap(refreshTokenUseCase::verifyExpiration)
                .flatMap(validToken -> jwtGateway.generateAccessToken(validToken.getUser())
                        .map(jwt -> new TokenRefreshResponse(jwt, refreshToken)));
    }
}
