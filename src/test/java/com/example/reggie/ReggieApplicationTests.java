package com.example.reggie;

import com.example.reggie.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Function;

@SpringBootTest
class ReggieApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void test(){
        System.out.println();
        Function<Employee, String> getUsername = Employee::getUsername;
        System.out.println(getUsername);
    }
}
