package org.jcg.springboot.aws.s3.controller;

import org.jcg.springboot.aws.s3.serv.AWSS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/files")
public class AwsController {

    @Autowired
    private AWSS3Service awsS3Service;

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadFile(@RequestPart(value = "file") MultipartFile multipartFile) {
        return new ResponseEntity<>(awsS3Service.uploadFile(multipartFile), HttpStatus.OK);
    }

    @GetMapping(value = "/getAll")
    public ResponseEntity<List<String>> galleryList() {
        return new ResponseEntity<>(awsS3Service.listOfBucketImageUrls(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete")
    public void galleryList(@RequestParam String fileName) {
        awsS3Service.deleteFile(fileName);
    }
}
