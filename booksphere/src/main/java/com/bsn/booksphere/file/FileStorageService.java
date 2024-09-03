package com.bsn.booksphere.file;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;
import static java.lang.System.currentTimeMillis;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {
    // es el camino donde se guardan las fotos
    @Value("${application.file.uploads.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(
            @Nonnull MultipartFile sourceFile,
            @Nonnull Integer bookId,
            @Nonnull Integer userId
    ) {
        final String fileUploadSubPath = "users" + separator + userId;
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    private String uploadFile(
            @Nonnull MultipartFile sourceFile,
            @Nonnull String fileUploadSubPath
    ) {
        // es el camino donde se guardan las fotos
        final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        // si no existe la carpeta la crea
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated) {
                log.warn("Failed to create the target folder: " + targetFolder);
                return null;
            }
        }
        // obtiene la extension del archivo y la guarda en minusculas para que no haya problemas con la extension del archivo
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        // crea el nombre del archivo con la extension del archivo
        String targetFilePath = finalUploadPath + separator + currentTimeMillis() + "." + fileExtension;
        // crea el camino del archivo
        Path targetPath = Paths.get(targetFilePath);
        // guarda el archivo
        try {
            Files.write(targetPath, sourceFile.getBytes());
            log.info("File saved to: " + targetFilePath);
            return targetFilePath;
        } catch (IOException e) {
            log.error("File was not saved", e);
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        // obtiene la extension del archivo y la devuelve en minusculas para que no haya problemas
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        // obtiene el ultimo punto del archivo para obtener la extension del archivo y la devuelve en minusculas
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        // devuelve la extension del archivo en minusculas para que no haya problemas
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}
