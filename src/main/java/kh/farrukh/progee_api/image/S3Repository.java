package kh.farrukh.progee_api.image;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class S3Repository {

    private final AmazonS3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String savePublicReadObject(InputStream inputStream, String key) {
        return saveObject(inputStream, key, CannedAccessControlList.PublicRead);
    }

    public String saveObject(InputStream inputStream, String key, CannedAccessControlList acl) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, key, inputStream, null
        ).withCannedAcl(acl);
        s3Client.putObject(putObjectRequest);
        return s3Client.getUrl(bucketName, key).toString();
    }

    public void deleteObject(String key) {
        s3Client.deleteObject(bucketName, key);
    }
}
