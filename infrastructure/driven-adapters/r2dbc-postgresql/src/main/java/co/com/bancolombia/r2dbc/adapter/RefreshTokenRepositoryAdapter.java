package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.entities.RefreshToken;
import co.com.bancolombia.model.entities.User;
import co.com.bancolombia.model.gateways.RefreshTokenRepository;
import co.com.bancolombia.r2dbc.mappers.RefreshTokenMapper;
import co.com.bancolombia.r2dbc.mappers.UserMapper;
import co.com.bancolombia.r2dbc.repositories.RefreshTokenDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final RefreshTokenDataRepository repository;
    private final RefreshTokenMapper mapper;
    private final UserMapper userMapper;

    @Override
    public Mono<RefreshToken> findByToken(String token) {
        return repository.findByToken(token).map(mapper::toModel);
    }

    @Override
    public Mono<RefreshToken> save(RefreshToken refreshToken) {
        return repository.save(mapper.toData(refreshToken)).map(mapper::toModel);
    }

    @Override
    public Mono<Void> delete(RefreshToken token) {
        return repository.delete(mapper.toData(token));

    }

    @Override
    public Mono<Integer> deleteByUser(User user) {
        return repository.deleteByUserId(userMapper.toData(user).getUserId());
    }
}

