package org.jcg.springboot.aws.s3.serv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3;

@Service
public class AWSS3ServiceImpl implements AWSS3Service {

    @Autowired
    private AmazonS3 amazonS3;
    @Value("${aws.s3.bucket}")
    private String bucketName;
    @Value("${aws.s3.endpointUrl}")
    private String endpointUrl;

    public String uploadFile(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        uploadFileTos3bucket(originalFilename, convertMultiPartToFile(multipartFile));
        return buildS3ObjectUrl(originalFilename);
    }

    @Override
    public List<String> listOfBucketImageUrls() {
        return s3BucketObjects()
                .stream()
                .map(S3ObjectSummary::getKey)
                .map(this::buildS3ObjectUrl)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }

    private String buildS3ObjectUrl(String originalFileName) {
        return endpointUrl + "/" + bucketName + "/" + originalFileName;
    }

    private List<S3ObjectSummary> s3BucketObjects() {
        ObjectListing listing = amazonS3.listObjects(bucketName);
        List<S3ObjectSummary> s3ObjectSummaries = listing.getObjectSummaries();
        while (listing.isTruncated()) {
            listing = amazonS3.listNextBatchOfObjects(listing);
            s3ObjectSummaries.addAll(listing.getObjectSummaries());
        }
        return s3ObjectSummaries;
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    private File convertMultiPartToFile(MultipartFile file) {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convFile;
    }
}