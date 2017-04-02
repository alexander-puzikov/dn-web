package org.dissernet.comparer.web;

import org.dissernet.comparer.MonoLauncher;
import org.dissernet.comparer.web.entity.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.io.File;
import java.io.IOException;

/**
 * Created by APuzikov on 19.02.2017.
 */

@RestController
@RequestMapping("/rest")
@MultipartConfig(fileSizeThreshold = 20971520)
public class ComparerRestController {

    @Autowired
    private MonoLauncher launcher;

    @Value("${files.directory}")
    private String tempFileDirectory;

    @RequestMapping(method = RequestMethod.POST, value = "/evaluate")
    public ResponseEntity<Response> takeFile(@RequestParam("uploadedFile") MultipartFile mpFile) {
        Response response = null;
        if (mpFile.isEmpty()) {
            response = new Response("File is empty", true);
            return new ResponseEntity<Response>(response, HttpStatus.NO_CONTENT);
        }
        String originalFilename = mpFile.getOriginalFilename();
        if (!isAcceptableFormat(originalFilename)) {
            response = new Response("File format incorrect. Only txt, pdf, doc is acceptable.", true);
            return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            File file = File.createTempFile("temploaded", "tmp");
            launcher.processFile(file);
        } catch (IOException e) {
            response = new Response("Can't process files. Internal error.", true);
            return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<Response>(response, HttpStatus.ACCEPTED);
    }


    private boolean isAcceptableFormat(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf(".")).equals(".txt")
                ||
                originalFilename.substring(originalFilename.lastIndexOf(".")).equals(".pdf")
                ||
                originalFilename.substring(originalFilename.lastIndexOf(".")).equals(".doc");
    }
}
