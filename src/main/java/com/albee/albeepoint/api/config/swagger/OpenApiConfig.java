package com.albee.albeepoint.api.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses; 
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@OpenAPIDefinition(
//        tags = {
//                @Tag(name = "계약 기관 관리", description = "포인트 서비스를 위한 계약 기관 관리 API")
//        }
//)
@Configuration
public class OpenApiConfig {
//    @Bean
//    public GroupedOpenApi groupedOpenApi(){
//        String[] packagesToScan = {"com.albeepoint.api"};
//        return GroupedOpenApi.builder()
//                .group("albeepoint")
////				.pathsToMatch("/**")s
////                .packagesToScan("/**")
//				.packagesToScan(packagesToScan)
//                .build();
//    }

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
                .version("v1.0.0")
                .title("알비 포인트 API")
                .description("알비 포인트 API")
                ;

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }

    public ApiResponse createApiResponse(String message, Content content){
   		return new ApiResponse().description(message).content(content);
   	}

   	@Bean
   	public GlobalOpenApiCustomizer customerGlobalHeaderOpenApiCustomizer() {
   		return openApi -> {
           	// 공통으로 사용되는 response 설정
   			openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
   				ApiResponses apiResponses = operation.getResponses();
   				apiResponses.addApiResponse("200", createApiResponse(apiResponses.get("200").getDescription(), apiResponses.get("200").getContent()));
   				apiResponses.addApiResponse("400", createApiResponse("Bad Request", null));
   				apiResponses.addApiResponse("401", createApiResponse("Access Token Error", null));
   				apiResponses.addApiResponse("500", createApiResponse("Server Error", null));
   			}));
   		};
   	}

}
