package com.cau.cc.aws;

import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.response.ImageUploadApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class FileApiController {

    @Autowired
    AwsService awsService;

    /**
     * 이미지 1개 업로드하기
     * "file" 이름으로 이미지 받는다
     */
    @PostMapping("/upload")
    public Header<ImageUploadApiResponse> uploadImages(@RequestParam("file") MultipartFile file) throws Exception{
        log.debug("[ Call /obj/img-put - POST ]");

        //s3Path : 버켓의 /images 경로(시작경로)를 의미
        String s3Path = "/images";

        //파라미터로 받은 file을 "/image" 폴더 안에 저장
        awsService.uploadMultipartFile(file,s3Path);

        ImageUploadApiResponse response = new ImageUploadApiResponse();

        String url = "https://caucampuscontact.s3.amazonaws.com/images/"+file.getOriginalFilename();
        response.setUrl(url);

        return Header.OK(response);
    }
}