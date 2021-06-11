package main;
import java.util.ArrayList;
import java.util.List;
//import java.io.IOException;
import java.util.Scanner;
import java.io.*;
import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.Rule;



public class Main {
	//private final static String bucketName = "zhengchenkai";
	private static String bucketName;
	private static String filePath;
	private static List<String> localDocuName = new ArrayList(); 
    private static Integer count = 0;
    //static Options One = new Options();
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		while(true) {
			Options One = new Options();
			//��ӡ�˵�
			System.out.println("1. Download all the files from bucket");
			System.out.println("2. Synchronize the local the file to the bucket");
			System.out.println("3. Exit");
			System.out.println("Show your choice:");
			int choice = sc.nextInt();
			
			if(choice==3) {
				System.out.println("Goodbye~");
				System.exit(0);
			}else if(choice==1 || choice == 2){
				//����Ͱ�����ļ�Ŀ¼�� (succ)
				System.out.println("Enter the bucket's name:");
				bucketName = sc.next();
				System.out.println("Enter the catalog's path:");
				filePath = sc.next();
				localDocuName.clear();
				if(choice==1) {
					One.downloadAllFile( bucketName,filePath);
				}else {
					//��ȡĿ¼�������ļ����� ac
					getFileName(filePath,filePath,One);
					//ɾ��bucket���ڱ����Ѿ�ɾ�����ļ�
					One.deleteBucketFile(bucketName, localDocuName);
				}
			}else {
				System.out.println("Invalid input! Enter it again!");
			}
						
		}		
	}
	
	//��ȡ����Ŀ¼�������ļ�������ͬ�ƶ˵��ļ����бȽϺ�ͬ����ֻ���ϴ����޸ģ�
	public static void getFileName(String catalog,String filePath,Options One) {
		File f = new File(catalog);
		File fa[] = f.listFiles();
		for(int i=0;i<fa.length;++i) {
			File fs = fa[i];
			if (fs.isDirectory() ) {
				if(fs.toString().contains("download")) continue;
				getFileName(fs.toString(),filePath+fs.toString(),One);
			}else {
				One.putFlie(fs.toString(), bucketName);
				localDocuName.add(fs.toString());
			}
		}
	}

}
