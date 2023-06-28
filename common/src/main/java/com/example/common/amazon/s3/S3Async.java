package com.example.common.amazon.s3;


import com.example.common.file.FileUtil;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;



/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
@Slf4j
public class S3Async {

	public static S3AsyncClient getS3AsyncClient(){
	    return   S3AsyncClient.builder()
	            .credentialsProvider(DefaultCredentialsProvider.create())
	            .region(Region.CN_NORTH_1)
	            .build();
	}
	
	public static void copySourceObject(String  source,String out) {
		if(!source.endsWith("/"))
			source=source+"/";
		if(!out.endsWith("/"))
			out=out+"/";
        // 设置bucket
        ListObjectsV2Request listObjReq =  ListObjectsV2Request.builder()
        		.bucket(AmazonS3Util.bucket)
        		.prefix(source)
        		.build();
        ListObjectsV2Response listObjRes = AmazonS3Util.client.listObjectsV2(listObjReq);
        // 这里会得到目录下每一层级的资源路径，包括子目录和文件
        List<S3Object> list = listObjRes.contents();
        copy(list,source,out);
    }
 
	private static void copy(List<S3Object> list, String source, String out) {
		if (list == null || list.size() < 1)
			throw new RuntimeException("文件夹下无文件可复制！");
		List<CompletableFuture<CopyObjectResponse>> futureList = new ArrayList<>();
        S3AsyncClient s3AsyncClient= getS3AsyncClient();
		for (S3Object s3Object : list) {
			String sourceKey = s3Object.key();
			String newKey = out + sourceKey.replace(source, "");
			log.info("复制的文件路径 :{}", newKey);
			CopyObjectRequest copyObjectRequest = CopyObjectRequest
					.builder()
					.sourceKey(sourceKey)
					.sourceBucket(AmazonS3Util.bucket)
					.destinationBucket(AmazonS3Util.bucket)
					.destinationKey(newKey)
					.build();
			CompletableFuture<CopyObjectResponse> future = s3AsyncClient.copyObject(copyObjectRequest);
			futureList.add(future);
		}
        CompletableFuture<?>[] futureArray = futureList.toArray(new CompletableFuture[0]);
    	CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureArray);
    	log.info("----------等待全部执行完成");
    	allFutures.join();
    	s3AsyncClient.close();
    	log.info("----------已经全部执行完成");
	}
	
	
	public static void copyLocalFile(String source,String out) {
		List<CompletableFuture<PutObjectResponse>> futureList = new ArrayList<>();
		S3AsyncClient s3AsyncClient=getS3AsyncClient();
    	readLocalFile(source, out, source,futureList,s3AsyncClient);
    	if(futureList.size()>0) {
	        CompletableFuture<?>[] futureArray = futureList.toArray(new CompletableFuture[0]);
	    	CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureArray);
	    	log.info("----------等待全部执行完成");
	    	allFutures.join();
	    	log.info("----------已经全部执行完成");
    	}
    	s3AsyncClient.close();
    }
    
    private static void readLocalFile(String path,String out,String reg,List<CompletableFuture<PutObjectResponse>> futureList,S3AsyncClient s3AsyncClient ){
        File sourefile = new File(path);
        //判断目录是否存在，以及是否是个目录
        if (!sourefile.exists() && !sourefile.isDirectory())
        	return;
         //找出目录下的所有文件对象
        File[] files = sourefile.listFiles();
        if(files==null || files.length==0)
        	return;
        //遍历这个数组
        for (File file:files){
            //如果它是一个子目录，使用回调方法，继续调用
            if (file.isDirectory()){
            	readLocalFile(file.getPath(),out,reg,futureList,s3AsyncClient);
            }else if(file.isFile()){
            	//获取文件的路径
            	String readPath=file.getPath();
            	String splitPath=readPath.substring(reg.length());
            	String newout=null;
            	if(!splitPath.startsWith("\\") && !out.endsWith("/")) {
            		newout=out+"/"+splitPath;
            	}else if(splitPath.startsWith("\\") && out.endsWith("/")) {
            		newout=out.substring(0,out.length()-1)+splitPath;
            	}else {
            		newout=out+splitPath;
            	}
            	newout=newout.replace("\\","/");
            	log.info("------文件上传路径:{}",newout);
				//byte[] fileByte= FileUtils.readFileBytes(readPath);
				byte[] fileByte = new byte[2024];
			 	PutObjectRequest request = PutObjectRequest.builder().bucket(AmazonS3Util.bucket).key(newout).build();
			 	CompletableFuture<PutObjectResponse> future=  s3AsyncClient.putObject(request, AsyncRequestBody.fromBytes(fileByte));
			 	futureList.add(future);
            }
        }
    }
    
    /**
     * 
     * @param prefix  s3 文件路径
     * @param localPath 本地路径
     */
    public static void getSourceObject(String prefix, String localPath) {
		ListObjectsV2Request listObjReq = ListObjectsV2Request.builder().bucket(AmazonS3Util.bucket).prefix(prefix)
				.build();
		ListObjectsV2Response listObjRes = AmazonS3Util.client.listObjectsV2(listObjReq);
		// 这里会得到目录下每一层级的资源路径，包括子目录和文件
		List<S3Object> s3ObjectList = listObjRes.contents();
		if (s3ObjectList == null || s3ObjectList.size() < 1)
			throw new RuntimeException("未找到输出文件");
		File file = new File(localPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			List<CompletableFuture<ResponseBytes<GetObjectResponse>>> futureList = new ArrayList<>();
	        S3AsyncClient s3AsyncClient= getS3AsyncClient();
			log.info("----------开始下载----->");
			for (S3Object s3Object : s3ObjectList) {
				GetObjectRequest objectRequest = GetObjectRequest.builder().bucket(AmazonS3Util.bucket)
						.key(s3Object.key()).build();
				CompletableFuture<ResponseBytes<GetObjectResponse>> futureGet = s3AsyncClient
						.getObject(objectRequest, AsyncResponseTransformer.toBytes()).whenComplete((resp, err) -> {
							try {
								if (resp != null) {
									resp.asByteArray();
									String filePath = s3Object.key().replace(prefix, "");
									log.info("------>" + filePath);
									File newFile = new File(localPath + filePath);
									if (!newFile.getParentFile().exists()) {
										newFile.getParentFile().mkdirs();
									}
									OutputStream out = null;
									InputStream in = null;
									try {
										out = new FileOutputStream(newFile);
										in = resp.asInputStream();
										//IoUtil.copy(in, out);
									} catch (FileNotFoundException e) {
										e.printStackTrace();
									} finally {
										try {
											out.close();
										} catch (IOException e) {
											e.printStackTrace();
										}
										try {
											in.close();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								} else {
									err.printStackTrace();
								}
							} finally {
							}
						});
				;
				futureList.add(futureGet);
			}
			CompletableFuture<?>[] futureArray = futureList.toArray(new CompletableFuture[0]);
			CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureArray);
			allFutures.join();
			s3AsyncClient.close();
		}finally {
		}
		log.info("----------已经全部执行完成");
	}
    
    
	public static void downZip(String prefix,String localZipPath, String fileName) {
		String localZipPathTemp=localZipPath+"temp/";
		downZip(prefix, localZipPathTemp, localZipPath, fileName);
	}
	/**
	 * 
	 * @param prefix s3的文件路径
	 * @param localTemp 需要压缩的临时文件路径
	 * @param localZipPath 本地压缩文件路径
	 * @param fileName 压缩文件名称
	 */
	public static void downZip(String prefix, String localTemp,String localZipPath, String fileName) {
		getSourceObject(prefix, localTemp);
		FileUtil.threadZipFile(localTemp, localZipPath+fileName);
		log.info("----------已经全部执行完成");
	}
}