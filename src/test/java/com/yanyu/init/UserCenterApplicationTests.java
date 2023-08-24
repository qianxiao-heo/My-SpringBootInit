package com.yanyu.init;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SpringBootTest
class UserCenterApplicationTests {


    @Test
    void contextLoads() {
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM");
        System.out.println(simpleDateFormat.format(date).replace("-",""));
    }

}
