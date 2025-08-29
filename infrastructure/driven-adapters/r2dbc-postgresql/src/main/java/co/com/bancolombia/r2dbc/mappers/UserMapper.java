package co.com.bancolombia.r2dbc.mappers;


import co.com.bancolombia.model.entities.User;
import co.com.bancolombia.r2dbc.entities.UserData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "documentIdentification", target = "documentIdentification")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(source = "direction", target = "direction")
    @Mapping(source = "telephoneNumber", target = "telephoneNumber")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "baseSalary", target = "baseSalary")
    User toModel(UserData data);
    UserData toData(User model);
}
