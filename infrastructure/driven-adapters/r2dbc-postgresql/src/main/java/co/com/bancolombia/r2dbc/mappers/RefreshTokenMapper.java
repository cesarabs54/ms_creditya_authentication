package co.com.bancolombia.r2dbc.mappers;

import co.com.bancolombia.model.entities.RefreshToken;
import co.com.bancolombia.r2dbc.entities.RefreshTokenData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface RefreshTokenMapper {
    RefreshToken toModel(RefreshTokenData data);

    RefreshTokenData toData(RefreshToken model);
}
