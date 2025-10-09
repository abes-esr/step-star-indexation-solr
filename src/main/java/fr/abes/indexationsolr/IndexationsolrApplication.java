package fr.abes.indexationsolr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class IndexationsolrApplication {

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(IndexationsolrApplication.class, args)));
    }

}
