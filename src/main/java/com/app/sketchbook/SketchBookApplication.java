package com.app.sketchbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class SketchBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(SketchBookApplication.class, args);
    }

}
