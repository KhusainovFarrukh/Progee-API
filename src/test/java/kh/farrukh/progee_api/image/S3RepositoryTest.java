package kh.farrukh.progee_api.image;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3RepositoryTest {

    @Mock
    private AmazonS3Client s3Client;

    @InjectMocks
    private S3Repository s3Repository;

    private static final String BUCKET_NAME = "test-bucket";

    @Test
    void savePublicReadObject_callsSaveObject() throws MalformedURLException {
        // given
        ReflectionTestUtils.setField(s3Repository, "bucketName", BUCKET_NAME);
        when(s3Client.getUrl(any(), any())).thenReturn(new URL("https", "test.com", 80, "test"));

        // when
        s3Repository.savePublicReadObject(InputStream.nullInputStream(), "images/test.png");

        // then
        ArgumentCaptor<PutObjectRequest> objectRequestArgumentCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(objectRequestArgumentCaptor.capture());

        PutObjectRequest actual = objectRequestArgumentCaptor.getValue();
        assertThat(actual.getCannedAcl()).isEqualTo(CannedAccessControlList.PublicRead);
        assertThat(actual.getKey()).isEqualTo("images/test.png");
        assertThat(actual.getBucketName()).isEqualTo(BUCKET_NAME);
    }

    @Test
    void saveObject_callsPutObject() throws MalformedURLException {
        // given
        ReflectionTestUtils.setField(s3Repository, "bucketName", BUCKET_NAME);
        when(s3Client.getUrl(any(), any())).thenReturn(new URL("https", "test.com", 80, "test"));

        // when
        s3Repository.saveObject(InputStream.nullInputStream(), "images/test.png", CannedAccessControlList.Private);

        // then
        ArgumentCaptor<PutObjectRequest> objectRequestArgumentCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(objectRequestArgumentCaptor.capture());

        PutObjectRequest actual = objectRequestArgumentCaptor.getValue();
        assertThat(actual.getCannedAcl()).isEqualTo(CannedAccessControlList.Private);
        assertThat(actual.getKey()).isEqualTo("images/test.png");
        assertThat(actual.getBucketName()).isEqualTo(BUCKET_NAME);
    }

    @Test
    void deleteObject_callsDeleteObject() {
        // when
        ReflectionTestUtils.setField(s3Repository, "bucketName", BUCKET_NAME);
        s3Repository.deleteObject("images/test.png");

        // then
        verify(s3Client).deleteObject(BUCKET_NAME, "images/test.png");
    }
}