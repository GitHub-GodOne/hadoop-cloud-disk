package com.example.class_design;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
public class ClassDesignApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassDesignApplication.class, args);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxRequestSize(DataSize.parse("-1"));
        factory.setMaxFileSize(DataSize.parse("-1"));
        return factory.createMultipartConfig();
    }
}
