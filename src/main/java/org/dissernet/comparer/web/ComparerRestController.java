package org.dissernet.comparer.web;

import org.apache.log4j.Logger;
import org.dissernet.comparer.MonoLauncher;
import org.dissernet.comparer.web.entity.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.io.*;

/**
 * Created by APuzikov on 19.02.2017.
 */

@RestController
@RequestMapping("/rest")
@MultipartConfig(fileSizeThreshold = 20971520)
public class ComparerRestController extends FilesChecker{

    Logger logger = Logger.getLogger(ComparerRestController.class);

    @Autowired
    private MonoLauncher launcher;

    @Value("${files.directory}")
    private String tempFileDirectory;

    @Value("${files.result.format:xml}")
    private String resultFormat;

    @RequestMapping(method = RequestMethod.POST, value = "/evaluate")
    public ResponseEntity<Response> takeFile(@RequestParam("uploadedFiles") MultipartFile[] mpFiles) {
        ResponseEntity<Response> responseEntity = null;
        Response response = null;
        logger.info(mpFiles.length + " files were taken.");
        for (MultipartFile mpFile : mpFiles) {
            if (mpFile.isEmpty()) {
                logger.info("files empty.");
                response = new Response("File is empty", true);
                responseEntity = new ResponseEntity<Response>(response, HttpStatus.NO_CONTENT);
            }
            String originalFilename = mpFile.getOriginalFilename();
            if (!isAcceptableFormat(originalFilename)) {
                logger.info(originalFilename + "- file has wrong format.");
                response = new Response("File format incorrect. Only txt, pdf, doc is acceptable.", true);
                responseEntity = new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
            }
        }
        File[] files = null;
        try {
            files = copyToTempFile(mpFiles);
            String result = launcher.processFile(files, resultFormat);
            response = new Response(result, false);
            responseEntity = new ResponseEntity<Response>(response, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            response = new Response("Can't process files. Internal error.", true);
            responseEntity = new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
        }finally {
            clearFiles(files);
        }
        return responseEntity;
    }

    private File[] copyToTempFile(MultipartFile[] mpFiles) throws IOException {
        logger.info("Coping to temp files " + mpFiles);
        File[] files = new File[mpFiles.length];
        int index = 0;
        for (MultipartFile mpFile : mpFiles) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mpFile.getInputStream()))) {
                String originalFilename = mpFile.getOriginalFilename();
                File tempFile = File.createTempFile("temploaded", originalFilename.substring(originalFilename.lastIndexOf('.')));
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

}
