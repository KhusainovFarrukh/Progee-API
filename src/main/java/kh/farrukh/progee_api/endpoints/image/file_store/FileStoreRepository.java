package kh.farrukh.progee_api.endpoints.image.file_store;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;

/**
 * Custom repository for working with files in FileSystem
 */
@Repository
public class FileStoreRepository {

    // Getting the path of the resources folder.
    String RESOURCES_DIR = Objects.requireNonNull(FileStoreRepository.class.getResource("/")).getPath();

    /**
     * It creates a new file in the `resources` directory, writes the content to it, and returns the absolute path to the
     * file
     *
     * @param content The content of the file to be saved.
     * @return The absolute path of the file.
     */
    public String save(byte[] content) throws Exception {
        Path newFile = Paths.get(RESOURCES_DIR + new Date().getTime());
        Files.createDirectories(newFile.getParent());

        Files.write(newFile, content);

        return newFile.toAbsolutePath().toString();
    }

    /**
     * It tries to find a file in the file system and returns a FileSystemResource object
     *
     * @param location The location of the image.
     * @return A FileSystemResource object.
     */
    public FileSystemResource findInFileSystem(String location) {
        try {
            return new FileSystemResource(Paths.get(location));
        } catch (Exception exception) {
            throw new RuntimeException("Error on getting image from location: " + exception.getMessage());
        }
    }
}
