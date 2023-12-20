package org.zerock.b01.config;


import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi restApi(){
        return GroupedOpenApi.builder()
                .group("API")
                .packagesToScan("org.zerock.b01.controller")
                .pathsToExclude("/board/*") //둘을 분리해서 사용하려고(댓글 vs 게시판)
                .build();
    }

    @Bean
    public GroupedOpenApi commonapi(){
        return GroupedOpenApi.builder()
                .pathsToMatch("/board/*")
                .group("COMMON API")
                .build();
    }
}
