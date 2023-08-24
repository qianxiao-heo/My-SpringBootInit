package com.yanyu.init.manager;

import com.qcloud.cos.model.UploadResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class CosManagerTest {

    @Resource
    private TencentCosManager tencentCosManager;

    @Test
    void CosUpload(){
        UploadResult uploadResult = tencentCosManager.putObjectLocalFile("test.webp", "D:\\bolg\\source\\img\\3.webp");
        System.out.println(uploadResult);
    }
}
