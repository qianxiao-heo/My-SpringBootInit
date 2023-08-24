package com.yanyu.init.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云对象储存客户端
 */
@Configuration
@ConfigurationProperties(prefix = "cos.tencent")
@Data
public class TencentCosClientConfig {

    private String secretId;

    private String secretKey;

    private String region;

    private String bucket;

    private String standard;

    /**
     * 创建 COSClient
     *
     * @return COSClient实例
     */
    @Bean
    public COSClient cosClient() {
        // 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 设置bucket的区域
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        // 建议设置使用 https 协议，从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 生成cos客户端
        return new COSClient(cred, clientConfig);
    }
}
