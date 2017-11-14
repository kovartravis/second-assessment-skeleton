package com.example.assess2.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;

@Configuration
public class SpringFoxConfiguration {
	
	/**
	 * Allows SpringFox to generate the swagger documentation for all endpoints in the project
	 * @return Docket object used by SpringFox
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.forCodeGeneration(true)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build();
	}
	
	/**
	 * By default, the SpringFox generated "swagger-ui" will not allow testing of HEAD or OPTIONS http methods. This will enable them.
	 * @return UiConfiguration object used by SpringFox to configure the swagger-ui
	 */
	@Bean
	public UiConfiguration uiConfiguration() {
		return new UiConfiguration(null, new String[] { "get", "post", "put", "delete", "patch", "options", "head" });
	}

}
