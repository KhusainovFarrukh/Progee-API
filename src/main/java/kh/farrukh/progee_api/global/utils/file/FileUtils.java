package kh.farrukh.progee_api.global.utils.file;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

    /**
     * It takes a file name, removes the extension, replaces spaces with underscores, and appends the current time in
     * milliseconds to the end of the file name
     *
     * @param image The image file that you want to upload.
     * @return A unique image name.
     */
    public static String getUniqueImageName(MultipartFile image) {
        String originalFilename = image.getOriginalFilename();
        String nameWithoutExtension = FilenameUtils.removeExtension(originalFilename);
        if (nameWithoutExtension == null) nameWithoutExtension = "image";
        nameWithoutExtension = nameWithoutExtension.replace(" ", "_");
        return nameWithoutExtension + "-" + System.currentTimeMillis() + "." + FilenameUtils.getExtension(originalFilename);
    }
}
