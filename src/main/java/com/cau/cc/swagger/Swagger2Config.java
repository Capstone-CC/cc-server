package com.cau.cc.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    private ServletContext servletContext;

    @Autowired
    public Swagger2Config(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .host("localhost:8080").pathProvider(new RelativePathProvider(servletContext){
                    @Override
                    public String getApplicationBasePath() {
                        return "/api";
                    }
                })
                .apiInfo(apiInfo())
                .select()
                //패키지 경로 설정
                .apis(RequestHandlerSelectors.basePackage("com.cau.cc"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("CauConnect")
                .version("1.1")
                .description("Default root Path : https://cauconnect.com/api")
//                .license("라이센스 작성")
//                .licenseUrl("라이센스 URL 작성")
                .build();
    }





}