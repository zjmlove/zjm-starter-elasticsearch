package com.zjm.config.swagger;

import com.google.common.base.Predicates;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author White Tan
 * @description
 * @date 2020/3/12
 */
@Configuration
@EnableSwagger2
//@ComponentScan(
//		excludeFilters = {@ComponentScan.Filter(
//				type = FilterType.ASSIGNABLE_TYPE,
//				classes = {InMemorySwaggerResourcesProvider.class}
//		)}
//)
public class SwaggerConfig {

	@Value("${swagger.host:N}")
	private String swaggerHost;
	@Value("${swagger.enable:true}")
	private Boolean enabled;

	public SwaggerConfig() {
	}

	@Bean
	public Docket createRestApi() {
		//		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
//				.select()
//				.apis(RequestHandlerSelectors.basePackage("")) // 需要写基础包
//				.paths(PathSelectors.any()).build();

		List<ResponseMessage> responseMessageList = new ArrayList();
//		responseMessageList.add((new ResponseMessageBuilder()).code(200000).message("请求成功").build());
//		responseMessageList.add((new ResponseMessageBuilder()).code(400001).message("业务逻辑错误").build());
//		responseMessageList.add((new ResponseMessageBuilder()).code(400002).message("参数校验错误").build());
//		responseMessageList.add((new ResponseMessageBuilder()).code(400003).message("Hystrix异常").build());
//		responseMessageList.add((new ResponseMessageBuilder()).code(400004).message("查询无记录").build());
//		responseMessageList.add((new ResponseMessageBuilder()).code(400005).message("数据已存在").build());
//		responseMessageList.add((new ResponseMessageBuilder()).code(500000).message("服务内部错误").build());
		Docket docket = new Docket(DocumentationType.SWAGGER_2);
		if (StringUtils.isNotBlank(this.swaggerHost) && !"N".equals(this.swaggerHost)) {
			docket = docket.host(this.swaggerHost);
		}
		return docket.apiInfo(this.apiInfo())
				.select()
				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
				.paths(Predicates.not(PathSelectors.regex("/error.*")))
				.build().securitySchemes(this.securitySchemes())
				.securityContexts(this.securityContexts())
				.globalResponseMessage(RequestMethod.GET, responseMessageList)
				.globalResponseMessage(RequestMethod.POST, responseMessageList)
				.globalResponseMessage(RequestMethod.PUT, responseMessageList)
				.globalResponseMessage(RequestMethod.DELETE, responseMessageList)
				.enable(this.enabled);
	}

	private ApiInfo apiInfo() {
		return (new ApiInfoBuilder()).title("white Swagger RESTFUL APIs")
				.description("综合管理 Swagger API 服务")
				.termsOfServiceUrl("http://swagger.io/")
				.contact(new Contact("white", "127.0.0.1", ""))
				.version("1.0")
				.build();
	}

	private List<ApiKey> securitySchemes() {
//		ApiKey apiKey = new ApiKey("Authorization", "login-token", "header");
//		return Collections.singletonList(apiKey);
		return Collections.EMPTY_LIST;
	}

	private List<SecurityContext> securityContexts() {
		return Collections.singletonList(
				SecurityContext.builder()
						.securityReferences(this.defaultAuth())
						.forPaths(PathSelectors.regex("^.*$"))
						.build()
		);
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[]{authorizationScope};
		return Collections.singletonList(new SecurityReference("Authorization", authorizationScopes));
	}
}
