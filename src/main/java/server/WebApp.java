package server;

import com.sun.net.httpserver.*;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jdbc.Database;
import jdbc.Programare;
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