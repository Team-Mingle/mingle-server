package community.mingle.app.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import community.mingle.app.config.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import community.mingle.app.config.BaseException;
import static community.mingle.app.config.BaseResponseStatus.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static community.mingle.app.config.BaseResponseStatus.DATABASE_ERROR;
import static community.mingle.app.config.BaseResponseStatus.UPLOAD_FAIL_IMAGE;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷 이름

    public List<String> uploadFile(List<MultipartFile> multipartFile, String dirName) throws BaseException {
        List<String> fileNameList = new ArrayList<>();

        // multipartFile로 넘어온 파일들 fileNameList에 추가
        for (MultipartFile file : multipartFile) {
            String fileName = dirName + "/" + createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                fileNameList.add(amazonS3.getUrl(bucket, fileName).toString());
            } catch(Exception e) {
                e.printStackTrace();
                throw new BaseException(UPLOAD_FAIL_IMAGE);
            }
        }

        return fileNameList;
    }

    public void deleteFile(String fileName) { amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    private String createFileName(String fileName) throws BaseException{ // 먼저 파일 업로드 시, 파일명을 난수화
        try{
            return UUID.randomUUID().toString().concat(getFileExtension(fileName));
        }catch(Exception e) {
            throw new BaseException(INVALID_IMAGE);
        }

    }

    private String getFileExtension(String fileName) throws BaseException{ // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며, 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단하였습니다.
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (Exception e) {
            throw new BaseException(INVALID_IMAGE_FORMAT);
        }
    }
}
