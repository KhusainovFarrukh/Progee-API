package kh.farrukh.progee_api.image;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * It's a wrapper around the AWS S3 client that allows us to save and delete objects from S3
 */
@Component
@RequiredArgsConstructor
public class S3Repository {

    private final AmazonS3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    /**
     * Save an object to S3 with given key and public read access
     *
     * @param inputStream The input stream of the file you want to upload.
     * @param key The key of the object to be saved.
     * @return The URL of the object.
     */
    public String savePublicReadObject(InputStream inputStream, String key) {
        return saveObject(inputStream, key, CannedAccessControlList.PublicRead);
    }

    /**
     * This function takes an input stream, a key, and an access control list, and saves the object to the bucket with
     * the given key and access control list
     *
     * @param inputStream The file you want to upload.
     * @param key The key is the name of the file you want to save in S3.
     * @param acl The canned access control list to apply to the object.
     * @return The URL of the object.
     */
    public String saveObject(InputStream inputStream, String key, CannedAccessControlList acl) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, key, inputStream, null
        ).withCannedAcl(acl);
        s3Client.putObject(putObjectRequest);
        return s3Client.getUrl(bucketName, key).toString();
    }

    /**
     * Delete the object with the given key from the bucket
     *
     * @param key The name of the object to delete.
     */
    public void deleteObject(String key) {
        s3Client.deleteObject(bucketName, key);
    }
}
