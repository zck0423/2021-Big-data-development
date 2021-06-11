package main;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.UploadPartRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

public class Options {
	private final static String accessKey = "F08312441E9AE64886F5";
	private final static String secretKey = "W0IzNTM5OENCMzgyMTUyMjVDQTJCQ0VGNUI5QzVE";
	private final static String serviceEndpoint = "http://10.16.0.1:81";
	private final static String signingRegion = "";
	private static List<String> bucketDocuName = new ArrayList();
	private static final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
	private static final ClientConfiguration ccfg = new ClientConfiguration().
            withUseExpectContinue(false);

	private static final EndpointConfiguration endpoint = new EndpointConfiguration(serviceEndpoint, signingRegion);

	private static final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withClientConfiguration(ccfg)
            .withEndpointConfiguration(endpoint)
            .withPathStyleAccessEnabled(true)
            .build();
	
	
	public static void downloadAllFile(String bucketName,String filePath) {
		//创建文件夹
		String fileFolderPath = filePath +"\\\\download";
		File fileFolders=new File(fileFolderPath);
		if(!fileFolders.exists()){//如果文件夹不存在
			fileFolders.mkdir();//创建文件夹
		}
		//判断下载文件是否大于20MB
		getBucketDocuName(bucketName);
		for(String keyName :  bucketDocuName) {
			String downFilePath = fileFolderPath + "\\\\" + keyName;
	        final File downloadFile = new File(downFilePath);
	        ObjectMetadata oMetaData = s3.getObjectMetadata(bucketName, keyName);
			final long contentLength = oMetaData.getContentLength();
			final GetObjectRequest downloadRequest = new GetObjectRequest(bucketName, keyName);
			if(contentLength<= 20<<20 ) {
				//小于20MB	        
		        System.out.format("Downloading %s from S3 bucket %s...\n", keyName, bucketName);
		        S3ObjectInputStream s3is = null;
				FileOutputStream fos = null;
				try {
					S3Object o = s3.getObject(bucketName, keyName);
					s3is = o.getObjectContent();
					fos = new FileOutputStream(new File(downFilePath));
					byte[] read_buf = new byte[64 * 1024];
					int read_len = 0;
					while ((read_len = s3is.read(read_buf)) > 0) {
						fos.write(read_buf, 0, read_len);
					}
				} catch (AmazonServiceException e) {
					System.err.println(e.toString());
					System.exit(1);
				} catch (IOException e) {
					System.err.println(e.getMessage());
					System.exit(1);
				} finally {
					if (s3is != null) try { s3is.close(); } catch (IOException e) { }
					if (fos != null) try { fos.close(); } catch (IOException e) { }
				}
				
				System.out.println("Done!");

			}else {
				//大于20MB，分块下载
				S3Object o = null;				
				S3ObjectInputStream s3is = null;
				FileOutputStream fos = null;
				long partSize = 20<<20;
				System.out.println("Downloading "+keyName+"from S3 bucket zck in chunks");
				try {
					long filePosition = 0;
					fos = new FileOutputStream(downFilePath);
					for (int i = 1; filePosition < contentLength; i++) {
						// Last part can be less than 5 MB. Adjust part size.
						partSize = Math.min(partSize, contentLength - filePosition);

						// Create request to download a part.
						downloadRequest.setRange(filePosition, filePosition + partSize);
						o = s3.getObject(downloadRequest);

						// download part and save to local file.
						System.out.format("Downloading part %d\n", i);

						filePosition += partSize+1;
						s3is = o.getObjectContent();
						byte[] read_buf = new byte[64 * 1024];
						int read_len = 0;
						while ((read_len = s3is.read(read_buf)) > 0) {
							fos.write(read_buf, 0, read_len);
						}
					}

					// Step 3: Complete.
					System.out.println("Completing download");
					System.out.format("save %s to %s\n", keyName, filePath);
			} catch (Exception e) {
				System.err.println(e.toString());
				
				System.exit(1);
			} finally {
				if (s3is != null) try { s3is.close(); } catch (IOException e) { }
				if (fos != null) try { fos.close(); } catch (IOException e) { }
			}
			System.out.println("Done!");
			}
		}				
	}
	
	@SuppressWarnings("deprecation")
	//filePath是绝对路径
	//检查文件是否被修改
	public static void putFlie(String filePath, String bucketName) {
        //列举桶内的文件
        ObjectListing objectListing = s3.listObjects(bucketName);
        List<S3ObjectSummary> sums = objectListing.getObjectSummaries();
        String [] temp = filePath.split("\\\\");
        String fileName = temp[temp.length-1];
        //System.out.println(temp[temp.length-1]);
        
        int flag = 0; // 显示文件是否在桶中
        for (S3ObjectSummary s : sums) {
          //本地文件存在桶中
          if(filePath.contains(s.getKey())) {
        	  flag = 1;
        	  System.out.println("the local file " + filePath + " is already in the bucket");
        	  //利用eTags检测本地文件与云端文件是否有区别
        	  //有区别
        	  String localEtag = calculateETag(filePath);
        	  String bucketEtag = s3.getObjectMetadata(bucketName ,fileName ).getETag();       	  
       	   	  if(!localEtag.equals(bucketEtag)) {
        		  System.out.println("The local file "+fileName+" is modified ");
        		  System.out.format("Updating %s to S3 bucket %s...\n", filePath, bucketName);
        		  s3.deleteObject(bucketName,fileName);
        		  //判断文件是否大于20m
        		  File f = new File(filePath);
        		  if(f.length()<= 20<<20)
        			  uploadSmallFile(bucketName,filePath);
        		  else uploadBigFile(bucketName,filePath);
        	  }
          }
        }
        
        //本地文件不在桶中
        if(flag == 0){
        	  //如果该本地文件没有在桶中，则上传
        	  System.out.println("the local file " + filePath + " is not in the bucket");
        	  System.out.format("Uploading %s to S3 bucket %s...\n", filePath, bucketName);
        	  File f = new File(filePath);
        	  if(f.length()<= 20<<20)
    			  uploadSmallFile(bucketName,filePath);
    		  else uploadBigFile(bucketName,filePath);
        }
    }
	
	//上传大文件
	public static void uploadBigFile(String bucketName,String filePath) {
		String keyName = Paths.get(filePath).getFileName().toString();
		
		// Create a list of UploadPartResponse objects. You get one of these
        // for each part upload.
		System.out.println("the local file " + filePath + " is bigger than 20MB and need to Upload in chunks");
		ArrayList<PartETag> partETags = new ArrayList<PartETag>();
		File file = new File(filePath);
		long contentLength = file.length();
		String uploadId = null;
		long partSize = 20<<20;
		try {
			// Step 1: Initialize.
			InitiateMultipartUploadRequest initRequest = 
					new InitiateMultipartUploadRequest(bucketName, keyName);
			uploadId = s3.initiateMultipartUpload(initRequest).getUploadId();
			System.out.format("Created upload ID was %s\n", uploadId);

			// Step 2: Upload parts.
			long filePosition = 0;
			for (int i = 1; filePosition < contentLength; i++) {
				// Last part can be less than 5 MB. Adjust part size.
				partSize = Math.min(partSize, contentLength - filePosition);

				// Create request to upload a part.
				UploadPartRequest uploadRequest = new UploadPartRequest()
						.withBucketName(bucketName)
						.withKey(keyName)
						.withUploadId(uploadId)
						.withPartNumber(i)
						.withFileOffset(filePosition)
						.withFile(file)
						.withPartSize(partSize);

				// Upload part and add response to our list.
				System.out.format("Uploading part %d\n", i);
				partETags.add(s3.uploadPart(uploadRequest).getPartETag());

				filePosition += partSize;
			}

			// Step 3: Complete.
			System.out.println("Completing upload");
			CompleteMultipartUploadRequest compRequest = 
					new CompleteMultipartUploadRequest(bucketName, keyName, uploadId, partETags);

			s3.completeMultipartUpload(compRequest);
		} catch (Exception e) {
			System.err.println(e.toString());
			if (uploadId != null && !uploadId.isEmpty()) {
				// Cancel when error occurred
				System.out.println("Aborting upload");
				s3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, keyName, uploadId));
			}
			System.exit(1);
		}
		System.out.println("Done!");
		
	}
	
	//上传小于20m的文件
	public static void uploadSmallFile(String bucketName, String filePath) {
		final String keyName = Paths.get(filePath).getFileName().toString();
        final File file = new File(filePath);
        System.out.println("the local file " + filePath + " is smaller than 20MB");
        for (int i = 0; i < 2; i++) {
            try {
                s3.putObject(bucketName, keyName, file);
                break;
            } catch (AmazonServiceException e) {
                if (e.getErrorCode().equalsIgnoreCase("NoSuchBucket")) {
                    s3.createBucket(bucketName);
                    continue;
                }

                System.err.println(e.toString());
                System.exit(1);
            } catch (AmazonClientException e) {
                try {
                    // detect bucket whether exists
                    s3.getBucketAcl(bucketName);
                } catch (AmazonServiceException ase) {
                    if (ase.getErrorCode().equalsIgnoreCase("NoSuchBucket")) {
                        s3.createBucket(bucketName);
                        continue;
                    }
                } catch (Exception ignore) {
                }

                System.err.println(e.toString());
                System.exit(1);
            }
        }

        System.out.println("Done!");
	}
	
	//删除桶内指定文件
	public static void deleteBucketFile(String bucketName, List<String> localDocuName) {
		getBucketDocuName(bucketName);
		List<String> newBucketDocuName = new ArrayList(bucketDocuName);
		for(String bucketFileName : bucketDocuName) {
			for(String localFileName : localDocuName) {
				if(localFileName.contains(bucketFileName)) {
					newBucketDocuName.remove(bucketFileName);
				}
			}
		}
		for(String fileName : newBucketDocuName) {	
			//删除bucket中的文件
			System.out.println("the file " + fileName + " needs to be deleted in the bucket");
			System.out.format("Deleting %s to S3 bucket %s...\n", fileName, bucketName);
			s3.deleteObject(bucketName,fileName);
			System.out.println("Done!");		
		}
	}
	
	//获取本地文件eTags
	public static String calculateETag(String localFilePath) {
		File file = new File(localFilePath);
		String s = "";
		try {
			InputStream is = new FileInputStream(file);
			String digest = DigestUtils.md5Hex(is); // 计算MD5值
			s = digest;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	//获取桶内所有文件的名称
	public static void getBucketDocuName(String bucketName) {
        //列举桶内的文件
		bucketDocuName.clear();
        ObjectListing objectListing = s3.listObjects(bucketName);
        List<S3ObjectSummary> sums = objectListing.getObjectSummaries();
        for (S3ObjectSummary s : sums) {
        	bucketDocuName .add(s.getKey());
        }
	}
}
