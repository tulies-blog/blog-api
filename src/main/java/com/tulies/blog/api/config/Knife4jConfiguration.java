package com.tulies.blog.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfiguration {

    @Bean(value = "appApi")
    public Docket appApi() {
        Docket docket=new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("门户服务API接口")
                        .description("提供博客前端门户需要的API接口")
//                        .termsOfServiceUrl("http://www.xx.com/")
                        .contact(new Contact("王嘉炀","wangjiayang.cn","346461062@qq.com"))
                        .version("1.0")
                        .build())
                //分组名称
                .groupName("门户服务API")
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.tulies.blog.api.module.app"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    @Bean(value = "msApi")
    public Docket msApi() {

        //设置knife文档的全局参数请求头,不用在这设置了，直接在文档中设置全局参数就可以了
//        List<Parameter> headerList = new ArrayList<>();
//        ParameterBuilder languageTicketPar = new ParameterBuilder();
//        languageTicketPar.name("ssotoken").description("SSO TOKEN")
////                .scalarExample("zh-MO")
//                .modelRef(new ModelRef("string")).parameterType("header")
//                .required(false).build();
//        headerList.add(languageTicketPar.build());

        Docket docket=new Docket(DocumentationType.SWAGGER_2)
//                .globalOperationParameters(headerList)
                .apiInfo(new ApiInfoBuilder()
                        .title("平台服务API接口")
                        .description("提供博客后台系统需要的API接口")
//                        .termsOfServiceUrl("http://www.xx.com/")
                        .contact(new Contact("王嘉炀","wangjiayang.cn","346461062@qq.com"))
                        .version("1.0")
                        .build())
                //分组名称
                .groupName("平台服务API")
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.tulies.blog.api.module.ms"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }
}
