package com.yanyu.init.manager;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.UploadResult;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import com.qcloud.cos.transfer.Upload;
import com.yanyu.init.config.TencentCosClientConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TencentCosManager {

    @Resource
    private TencentCosClientConfig tencentCosClientConfig;

    @Resource
    private COSClient cosClient;

    // 创建 TransferManager 实例
    TransferManager createTransferManager() {
        // 创建一个 COSClient 实例
        COSClient cosClient = tencentCosClientConfig.cosClient();
        // 自定义线程池大小，建议在客户端与 COS 网络充足（例如使用腾讯云的 CVM，同地域上传 COS）的情况下，设置成16或32即可，可较充分的利用网络资源
        // 对于使用公网传输且网络带宽质量不高的情况，建议减小该值，避免因网速过慢，造成请求超时
        ExecutorService threadPool = Executors.newFixedThreadPool(16);
        // 传入一个 threadPool, 若不传入线程池，默认 TransferManager 中会生成一个单线程的线程池
        TransferManager transferManager = new TransferManager(cosClient, threadPool);
        // 分块上传阈值和分块大小分别为 5MB 和 1MB
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(5 * 1024 * 1024);
        transferManagerConfiguration.setMinimumUploadPartSize(1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);
        return transferManager;
    }

    void shutdownTransferManager(TransferManager transferManager) {
        // 指定参数为 true, 则同时会关闭 transferManager 内部的 COSClient 实例
        // 指定参数为 false, 则不会关闭 transferManager 内部的 COSClient 实例
        transferManager.shutdownNow(true);
    }

    /**
     * 上传对象
     *
     * @param key  对象键
     * @param file 文件
     * @return UploadResult上传结果(成功返回 UploadResult, 失败抛出异常)
     */
    public UploadResult putObjRequest(String key, File file) {
        //创建 TransferManager
        TransferManager transferManager = createTransferManager();
        PutObjectRequest putObjectRequest = new PutObjectRequest(tencentCosClientConfig.getBucket(), key, file);
        return uploadOperation(transferManager, putObjectRequest);
    }

    /**
     * @param key           对象键
     * @param localFilePath 本地文件路径
     * @return UploadResult上传结果(成功返回 UploadResult, 失败抛出异常)
     */
    public UploadResult putObjectLocalFile(String key, String localFilePath) {
        TransferManager transferManager = createTransferManager();
        PutObjectRequest putObjectRequest = new PutObjectRequest(tencentCosClientConfig.getBucket(), key,
                new File(localFilePath));
        return uploadOperation(transferManager, putObjectRequest);
    }

    /**
     * 抽象公共上传操作
     *
     * @param transferManager  TransferManager 实例
     * @param putObjectRequest PutObjectRequest
     * @return UploadResult上传结果
     */
    private UploadResult uploadOperation(TransferManager transferManager, PutObjectRequest putObjectRequest) {
        // 设置存储类型
        putObjectRequest.setStorageClass(tencentCosClientConfig.getStandard());
        // 执行上传
        Upload upload = transferManager.upload(putObjectRequest);
        // 上传结果
        UploadResult uploadResult = null;
        upload.getState();
        try {
            uploadResult = upload.waitForUploadResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        shutdownTransferManager(transferManager);
        return uploadResult;
    }
}
