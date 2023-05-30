package server;

import java.io.*;
import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class WebApp {

    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(WebApp.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8083"));
        app.run(args);
    }
}