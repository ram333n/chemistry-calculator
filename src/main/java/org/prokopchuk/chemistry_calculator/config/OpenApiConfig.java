package org.prokopchuk.chemistry_calculator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Chemistry Calculator API")
                        .description("REST API for analysing chemical compound formulas")
                        .version("1.0.0"));
    }

    /**
     * Prepends the api.prefix to every path, because springdoc reads @RequestMapping
     * directly and is unaware of the prefix added via PathMatchConfigurer.
     */
    @Bean
    public OpenApiCustomizer apiPrefixCustomizer(@Value("${api.prefix}") String apiPrefix) {
        return openApi -> {
            Paths original = openApi.getPaths();
            if (original == null || original.isEmpty()) return;

            Paths prefixed = new Paths();
            original.forEach((path, item) -> prefixed.addPathItem(apiPrefix + path, item));
            openApi.setPaths(prefixed);
        };
    }
}
