package com.xuxi.learningspringaiexample.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EnvCheck implements CommandLineRunner {
    @Override
    public void run(String... args) {
        String key = System.getenv("OPENAI_API_KEY");
        System.out.println("【系统环境变量读到】：" + key);
    }
}
