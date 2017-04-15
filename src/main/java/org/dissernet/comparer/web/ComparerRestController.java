package org.dissernet.comparer.web;

import org.apache.log4j.Logger;
import org.dissernet.comparer.MonoLauncher;
import org.dissernet.comparer.web.entity.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.io.*;

/**
 * Created by APuzikov on 19.02.2017.
 */

@RestController
@RequestMapping("/rest")
@MultipartConfig(fileSizeThreshold = 20971520)
public class ComparerRestController {

    Logger logger = Logger.getLogger(ComparerRestController.class);

    @Autowired
    private MonoLauncher launcher;

    @Value("${files.directory}")
    private String tempFileDirectory;

    @RequestMapping(method = RequestMethod.POST, value = "/evaluate")
    public ResponseEntity<Response> takeFile(@RequestParam("uploadedFiles") MultipartFile[] mpFiles) {
        Response response = null;
        logger.info(mpFiles.length + " files were taken.");
        for (MultipartFile mpFile : mpFiles) {
            if (mpFile.isEmpty()) {
                logger.info("files empty.");
                response = new Response("File is empty", true);
                return new ResponseEntity<Response>(response, HttpStatus.NO_CONTENT);
            }
            String originalFilename = mpFile.getOriginalFilename();
            if (!isAcceptableFormat(originalFilename)) {
                logger.info(originalFilename + "- file has wrong format.");
                response = new Response("File format incorrect. Only txt, pdf, doc is acceptable.", true);
                return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
            }
        }
        try {
            File[] files = copyToTempFile(mpFiles);
            String result = launcher.processFile(files);
            response = new Response(result, false);
        } catch (Exception e) {
            response = new Response("Can't process files. Internal error.", true);
            return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<Response>(response, HttpStatus.ACCEPTED);
    }

    private File[] copyToTempFile(MultipartFile[] mpFiles) throws IOException {
        logger.info("Coping to temp files " + mpFiles);
        File[] files = new File[mpFiles.length];
        int index = 0;
        for (MultipartFile mpFile : mpFiles) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mpFile.getInputStream()))) {
                File tempFile = File.createTempFile("temploaded", "tmp");
                BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    bw.write(line);
                }
                bw.flush();
                bw.close();
                tempFile.deleteOnExit();
                files[index++] = tempFile;
            }
        }
        logger.info("Files copied. " + files);
        return files;
    }


    private boolean isAcceptableFormat(String originalFilename) {
        logger.debug("Check format of file " + originalFilename);
        return originalFilename.substring(originalFilename.lastIndexOf(".")).equals(".txt")
                ||
                originalFilename.substring(originalFilename.lastIndexOf(".")).equals(".pdf")
                ||
                originalFilename.substring(originalFilename.lastIndexOf(".")).equals(".doc");
    }
}
