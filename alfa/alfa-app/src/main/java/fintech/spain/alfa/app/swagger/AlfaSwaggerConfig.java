package fintech.spain.alfa.app.swagger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fintech.spain.alfa.web.config.security.WebApiUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class AlfaSwaggerConfig {

    @Value("${swagger.enabled:false}")
    private Boolean enable;

    @Bean
    public Docket swagger() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("web-api")
            .select()
            .apis(RequestHandlerSelectors.basePackage("fintech.spain.alfa.web.controllers.web"))
            .paths(PathSelectors.ant("/api/**"))
            .build()
            .ignoredParameterTypes(AuthenticationPrincipal.class, WebApiUser.class)
            .securitySchemes(ImmutableList.of(apiKey()))
            .securityContexts(ImmutableList.of(securityContext()))
            .apiInfo(apiInfo())
            .useDefaultResponseMessages(false)
            .enable(enable);
    }

    private ApiKey apiKey() {
        return new ApiKey(HttpHeaders.AUTHORIZATION, "jwt_token", ApiKeyVehicle.HEADER.getValue());
    }

    private SecurityContext securityContext() {
        return new SecurityContext(Lists.newArrayList(securityReference()), PathSelectors.ant("/api/web/**"));
    }

    private SecurityReference securityReference() {
        return new SecurityReference(HttpHeaders.AUTHORIZATION, new AuthorizationScope[]{new AuthorizationScope("global", "accessEverything")});
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Alfa Web API")
            .description("All decimal values are accepted and returned with 2 decimal place precision, e.g., 150.21." +
                "\nAll date fields are sent in ISO 8601 format YYYY-MM-DD, e.g., 2016-11-30.")
            .version("1.0")
            .build();
    }
}
