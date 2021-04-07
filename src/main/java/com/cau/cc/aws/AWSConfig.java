package com.cau.cc.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.cau.cc.auth.AWSS3AuthInfomation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {

    AWSS3AuthInfomation awss3AuthInfo = new AWSS3AuthInfomation();

    private String accessKey;

    private String secretKey;

    public AWSConfig() {
        awss3AuthInfo.parse_auth_info("auth/awss3.auth");
        accessKey = awss3AuthInfo.getAccessKey();
        secretKey = awss3AuthInfo.getSecretKey();
    }

    @Bean
    public BasicAWSCredentials awsCredentials(){
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey,secretKey);
        return awsCredentials;
    }

    @Bean
    public AmazonS3 awsS3Client(){
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(this.awsCredentials()))
                .build();
        return amazonS3;
    }
}
