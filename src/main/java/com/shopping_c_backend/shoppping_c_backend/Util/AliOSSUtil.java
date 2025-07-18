package com.shopping_c_backend.shoppping_c_backend.Util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


@Component
public class AliOSSUtil {
    String endpoint = "https://oss-cn-hongkong.aliyuncs.com";
    String accessKeyId = "******************************";
    String accessKeySecret = "************************************";
    String bucketName = "**********";
    private static final Logger logger = LoggerFactory.getLogger(AliOSSUtil.class);

    /**
     * 实现上传图片到OSS
     */
    public String upload(MultipartFile multipartFile, String path, String userName) throws IOException {
        OSS ossClient = null;
        try (InputStream inputStream = multipartFile.getInputStream()) {
            String fileName = path + "/" + userName + ".jpg";
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            //上传文件到 OSS
            PutObjectResult result = ossClient.putObject(bucketName, fileName, inputStream);
            if (result != null) {
                return "https://" + bucketName + "." + endpoint + "/" + fileName;
            }
        } catch (Exception e) {
            logger.error("发生异常：", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
                return "OK";
            }
        }
        return null;
    }
}
