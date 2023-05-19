package server;

import com.sun.net.httpserver.*;

import java.io.*;
import java.nio.file.*;
import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class WebApp {

    public static void main(String[] args) throws IOException {
//        int port = 8000; // Change to the desired port number
//        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
//        server.createContext("/", new MyHandler());
//        server.setExecutor(null);
//        server.start();
//        System.out.println("Server is running on http://localhost:" + port);
        SpringApplication app = new SpringApplication(WebApp.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8083"));
        app.run(args);
    }
}