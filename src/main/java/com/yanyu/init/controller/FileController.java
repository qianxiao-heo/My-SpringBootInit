package com.yanyu.init.controller;

import cn.hutool.core.io.FileUtil;
import com.yanyu.init.common.BaseResponse;
import com.yanyu.init.common.ErrorCode;
import com.yanyu.init.common.ResultUtils;
import com.yanyu.init.constant.FileConstant;
import com.yanyu.init.exception.BusinessException;
import com.yanyu.init.manager.TencentCosManager;
import com.yanyu.init.model.dto.file.UploadFileRequest;
import com.yanyu.init.model.enums.FileUploadBizEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private TencentCosManager tencentCosManager;

    @PostMapping("upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile, UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //校验文件是否合法
        validFile(multipartFile,fileUploadBizEnum);
        // 创建文件目录:业务+日期划分
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM");
        String uuid = RandomStringUtils.randomAlphabetic(8);
        String filename=uuid+"-"+multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), simpleDateFormat.format(date).replace("-", ""), filename);
        File file=null;
        try {
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            tencentCosManager.putObjRequest(filepath,file);
            // 返回可访问地址
            return ResultUtils.success(FileConstant.TENCENT_COS_HOST + filepath);
        } catch (IOException e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        }finally {
            if (file != null){
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete){
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 文件校验
     *
     * @param multipartFile 接收上传文件
     * @param fileUploadBizEnum
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 2 * 1024 * 1024L;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 2M");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
    }
}
