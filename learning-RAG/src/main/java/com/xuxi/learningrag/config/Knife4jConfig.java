package com.xuxi.learningrag.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置类 (基于OpenAPI 3.0)
 * 
 * @author xuxi
 * @version 1.0-SNAPSHOT
 */
@Configuration
public class Knife4jConfig {

    /**
     * 配置OpenAPI文档信息
     * 
     * @return OpenAPI对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring AI Alibaba Demo API")
                        .version("1.0-SNAPSHOT")
                        .description("Spring AI Alibaba 企业级应用接口文档 - Knife4j增强版")
                        .contact(new Contact()
                                .name("xuxi")
                                .email("xuxi@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
