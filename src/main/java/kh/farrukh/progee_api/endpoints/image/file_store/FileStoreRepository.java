package kh.farrukh.progee_api.endpoints.image.file_store;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;

@Repository
public class FileStoreRepository {

    String RESOURCES_DIR = Objects.requireNonNull(FileStoreRepository.class.getResource("/")).getPath();

    public String save(byte[] content) throws Exception {
        Path newFile = Paths.get(RESOURCES_DIR + new Date().getTime());
        Files.createDirectories(newFile.getParent());

        Files.write(newFile, content);

        return newFile.toAbsolutePath().toString();
    }

    public FileSystemResource findInFileSystem(String location) {
        try {
            return new FileSystemResource(Paths.get(location));
        } catch (Exception e) {
            // TODO: 6/8/22 custom exception with exception handler
            throw new RuntimeException(e.getMessage());
        }
    }
}
