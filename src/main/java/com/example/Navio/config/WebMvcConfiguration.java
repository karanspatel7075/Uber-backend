package com.example.Navio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/rider_img/**")
                .addResourceLocations("file:C:/Users/KARAN PATEL/Desktop/E-commerce/Uber/");
    }

}

//any URL that starts with /rider_img/ will be handled here.
//Example: http://localhost:8080/rider_img/12345_photo.png

//addResourceLocations("file:C:/Users/KARAN PATEL/Desktop/E-commerce/Uber/")
// → Spring will look in this folder on your computer to serve the files.

//When your frontend asks for an image like /rider_img/1760173857587_Screenshot.png, Spring will fetch it from
//C:/Users/KARAN PATEL/Desktop/E-commerce/Uber/1760173857587_Screenshot.png
//and return it in the HTTP response.

//✅ In simple words: It lets your app serve images from a local folder via a URL.