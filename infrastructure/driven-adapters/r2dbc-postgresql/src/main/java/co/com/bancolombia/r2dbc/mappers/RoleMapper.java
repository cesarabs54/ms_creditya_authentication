package co.com.bancolombia.r2dbc.mappers;

import co.com.bancolombia.model.entities.Role;
import co.com.bancolombia.r2dbc.entities.RoleData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toModel(RoleData data);
    RoleData toData(Role model);
}
