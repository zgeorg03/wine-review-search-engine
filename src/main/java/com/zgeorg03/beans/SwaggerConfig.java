package com.zgeorg03.beans;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableAutoConfiguration
public class SwaggerConfig {

    @Bean
    public Docket api(){

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.zgeorg03.api"))
                .paths(PathSelectors.any())
                .build().apiInfo(apiInfo());
    }
    private ApiInfo apiInfo(){
        return   new ApiInfoBuilder()
                .title("Wine Review Search Engine")
                .description("RESTfull API for the search engine of wine reviews")
                .contact(
                        new Contact("Zacharias Georgiou",
                                "http://cs.ucy.ac.cy/~zgeorg03",
                                "zgeorg03@cs.ucy.ac.cy")
                ).build();
    }

}
