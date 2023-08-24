package com.yanyu.init.service;

import com.yanyu.init.model.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAdd(){
        User user=new User();
        user.setUserName("YanYu");
        user.setUserAccount("yan123");
        user.setUserAvatar("https://pic.code-nav.cn/user_avatar/1601072287388278786/9vqTr3HM-WechatIMG1287.jpeg");
        user.setGender("男");
        user.setUserPassword("123456");
        user.setPhone("13285263695");
        user.setEmail("13285263695@163.com");
        user.setPlanetCode("00001");

        boolean res=userService.save(user);
        System.out.println(res+"========"+user.getId());
    }

    @Test
    void userRegister() {
        String userAccount="demo";
        String userPassword="12345678";
        String checkPassword="12345678";

        String email="00008";

        long result = userService.userRegister(userAccount, userPassword, checkPassword, email);
        Assertions.assertTrue(result > 0);

    }
}