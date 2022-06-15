package com.example.class_design;

import com.example.class_design.domain.User;
import com.example.class_design.service.UserServicelmpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootTest
class ClassDesignApplicationTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    UserServicelmpl userServicelmpl;



    @Test
    void contextLoads() throws SQLException {
//        System.out.println(dataSource.getClass());
//        System.out.println(dataSource.getConnection());
//        System.out.println(userServicelmpl.queryUserByName("123"));
        User user = new User();
        user.setUsername("12345678");
        user.setPassword("123");
        userServicelmpl.insert_user(user);
    }

}
