package org.jcg.springboot.aws.s3.serv;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AWSS3Service {

	String uploadFile(MultipartFile multipartFile);
	List<String> listOfBucketImageUrls();
	void deleteFile(String fileName);
}
