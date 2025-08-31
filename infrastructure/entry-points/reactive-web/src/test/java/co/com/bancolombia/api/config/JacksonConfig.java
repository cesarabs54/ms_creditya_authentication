package co.com.bancolombia.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Registrar el módulo para Java 8 date/time
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Serializar fechas como cadenas
        return objectMapper;
    }
}
