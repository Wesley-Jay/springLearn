package com.example.common.amazon.s3;

import com.alibaba.cloud.commons.io.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wsj
 * @description 亚马逊S3通用操作
 * @date 2023/4/20
 */
@Component
@Slf4j
@Configuration
public class AmazonS3Util {

    public static String bucket;
    private final static Region REGION = Region.CN_NORTH_1;
    private final static  String S3_BASE_PATH = "s3://" ;
    public final static String OUTPUT_FILE_NAME = "output.tar.gz";

    @Value("${aws.s3.bucket}")
    public void setBucket(String value) {
        AmazonS3Util.bucket = value;
    }

    public static String role;
    @Value("${aws.sagemaker.iam}")
    public void setRole(String value) {
        AmazonS3Util.role = value;
    }


    public static S3Client client;
    @Autowired
    public void setClient(S3Client client){
        AmazonS3Util.client = client;
    }

    @Bean
    public S3Client getS3Client(){
        return  S3Client.builder().region(REGION).build();
    }

    public static void putObject(byte[] bytes, String filePath) {
        PutObjectRequest request = PutObjectRequest.builder().bucket(bucket).key(filePath).build();
        long time = System.currentTimeMillis();
        client.putObject(request, RequestBody.fromBytes(bytes));
        log.info("----------------上传文件成功，耗时毫秒：{}" , (System.currentTimeMillis() - time));
    }

    public static void createKey(String key){
        PutObjectRequest request = PutObjectRequest.builder().bucket(bucket).key(key).build();
        client.putObject(request,RequestBody.empty());
    }

    public static byte[] getObject(String filePath) {
        ResponseBytes<GetObjectResponse> responseInputStream;
        GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(filePath).build();
        responseInputStream = client.getObjectAsBytes(request);
        return responseInputStream.asByteArray();
    }

    public static void delObject(String filePath) {
        DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucket).key(filePath).build();
        client.deleteObject(request);
    }

    public static void batchDelObject(List<String> filePaths) {
        if (filePaths.size() == 0) {
            return;
        }
        List<ObjectIdentifier> keys = new ArrayList<>();
        ObjectIdentifier objectId;
        for (String path : filePaths) {
            objectId = ObjectIdentifier.builder().key(path).build();
            keys.add(objectId);
        }
        Delete del = Delete.builder().objects(keys).build();
        DeleteObjectsRequest request = DeleteObjectsRequest.builder().bucket(bucket).delete(del).build();
        client.deleteObjects(request);
        log.info("S3删除多个对象完成");
    }
    
    public static void copyObject(String  source, String out) {
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .sourceKey(source)
                .sourceBucket(bucket)
                .destinationBucket(bucket)
                .destinationKey(out)
                .build();
        client.copyObject(copyObjectRequest);
    }

    public static String findTrainOutPutPath(String prefix){
        // 设置bucket
        ListObjectsV2Request listObjReq =  ListObjectsV2Request.builder().bucket(bucket).prefix(prefix).build();
        ListObjectsV2Response listObjRes = client.listObjectsV2(listObjReq);
        // 这里会得到目录下每一层级的资源路径，包括子目录和文件
        List<S3Object> s3ObjectList = listObjRes.contents();
        if (s3ObjectList != null && s3ObjectList.size() > 0) {
            for (S3Object s3Object : s3ObjectList) {
                // 预留 第文件夹层数校验,只取第一层级下的
                if (s3Object.key().endsWith(OUTPUT_FILE_NAME)) {
                    return s3Object.key() ;
                }
            }
        }
        throw new RuntimeException("未找到输出文件");
    }
    
    public static void copyLocalFile(String source,String out) {
    	readLocalFile(source, out, source);
    }
    
    private static void readLocalFile(String path,String out,String reg){
        File sourefile = new File(path);
        //判断目录是否存在，以及是否是个目录
        if (sourefile.exists() || sourefile.isDirectory()){
            //找出目录下的所有文件对象
            File[] files = sourefile.listFiles();
            if(files==null || files.length==0) {
                return;
            }
            //遍历这个数组
            for (File file:files){
                //如果它是一个子目录，使用回调方法，继续调用
                if (file.isDirectory()){
                	readLocalFile(file.getPath(),out,reg);
                }else if(file.isFile()){
                	//获取文件的路径
                	String readPath=file.getPath();
                	String splitPath=readPath.substring(reg.length());
                	String newout;
                	if(!splitPath.startsWith("\\") && !out.endsWith("/")) {
                		newout=out+"/"+splitPath;
                	}else if(splitPath.startsWith("\\") && out.endsWith("/")) {
                		newout=out.substring(0,out.length()-1)+splitPath;
                	}else {
                		newout=out+splitPath;
                	}
                	newout=newout.replace("\\","/");
                	//byte[] fileByte= FileUtils.readFileBytes(readPath);
                    byte[] fileByte = new byte[2024];
                	putObject(fileByte, newout);
                }
            }
        }
    }

    public static  boolean isExist(String filePath) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucket).key(filePath).build();
        try {
            client.headObject(headObjectRequest);
            // 如果对象存在，则返回true
            return true;
        } catch (NoSuchKeyException e) {
            // 如果对象不存在，则返回false
            return false;
        }
    }

    public static  String getS3BasePath(){
        return  S3_BASE_PATH + bucket + "/";
    }

    public static void deleteS3Folder(String folderPath) {
        List<String> keys = new ArrayList<>();
        // 设置bucket
        ListObjectsV2Request listObjReq =  ListObjectsV2Request.builder().bucket(bucket).prefix(folderPath).build();
        ListObjectsV2Response listObjRes = client.listObjectsV2(listObjReq);
        // 获取该文件夹下的所有文件key
        List<S3Object> s3ObjectList = listObjRes.contents();
        if (s3ObjectList != null && s3ObjectList.size() > 0) {
            for (S3Object s3Object : s3ObjectList) {
                String key = s3Object.key();
                keys.add(key);
            }
        }
        batchDelObject(keys);
    }
}
