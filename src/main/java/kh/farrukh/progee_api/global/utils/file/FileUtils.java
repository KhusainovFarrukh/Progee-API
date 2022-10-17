package kh.farrukh.progee_api.global.utils.file;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

    public static String getUniqueImageName(MultipartFile image) {
        String originalFilename = image.getOriginalFilename();
        String nameWithoutExtension = FilenameUtils.removeExtension(originalFilename);
        if (nameWithoutExtension == null) nameWithoutExtension = "image";
        nameWithoutExtension = nameWithoutExtension.replace(" ", "_");
        return nameWithoutExtension + "-" + System.currentTimeMillis() + "." + FilenameUtils.getExtension(originalFilename);
    }
}
