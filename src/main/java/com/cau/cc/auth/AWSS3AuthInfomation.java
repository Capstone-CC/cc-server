package com.cau.cc.auth;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Getter
public class AWSS3AuthInfomation {
    private String accessKey;
    private String secretKey;
    private String bucketName;

    public AWSS3AuthInfomation() {
        this.accessKey = null;
        this.secretKey = null;
        this.bucketName = null;
    }

    public boolean parse_auth_info(String auth_filepath) {
        String accessKey = null;
        String secretKey = null;
        String bucketName = null;

        /* Parse */
        try {
            File file = new File(auth_filepath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if(line.length() == 0) continue;
                if(line.charAt(0) == '#') continue;

                int line_length = line.length();
                if(line.substring(0, 16).equals("aws.s3.access-id")) accessKey = line.substring(17, line_length);
                else if(line.substring(0, 16).equals("aws.s3.access-pw")) secretKey = line.substring(17, line_length);
                else if(line.substring(0, 13).equals("aws.s3.bucket")) bucketName = line.substring(14, line_length);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        /* Verification */
        boolean flag_verified = true;
        if(accessKey == null) flag_verified = false;
        if(secretKey == null) flag_verified = false;
        if(bucketName == null) flag_verified = false;
        if(!flag_verified) {
            return false;
        }

        /* Apply parsed values */
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucketName = bucketName;

        return true;
    }
}
