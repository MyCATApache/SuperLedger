package io.mycat.fabric.phdc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("io.mycat.fabric.phdc")
public class PHDCApplication {
    public static void main(String[] args) {
        SpringApplication.run(PHDCApplication.class, args);
    }
}