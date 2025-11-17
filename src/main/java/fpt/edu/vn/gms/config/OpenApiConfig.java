package fpt.edu.vn.gms.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(info = @Info(title = "GMS Backend API", version = "1.0.0"), security = {
    @SecurityRequirement(name = "JWT") })
@SecurityScheme(name = "JWT", description = "Enter JWT authentication (access) token", bearerFormat = "Bearer", in = SecuritySchemeIn.HEADER, type = SecuritySchemeType.HTTP, scheme = "bearer")
@Configuration
public class OpenApiConfig {

}
