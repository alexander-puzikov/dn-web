package org.dissernet.comparer.web;

import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created by APuzikov on 15.04.2017.
 */
public class FilesChecker {
    Logger logger = Logger.getLogger(ComparerRestController.class);

    void clearFiles(File[] files) {
        if (files != null && files.length != 0) {
            for (File f : files){
                f.delete();
            }
        }
    }

    boolean isAcceptableFormat(String originalFilename) {
        logger.debug("Checking files " + originalFilename);
        return originalFilename.substring(originalFilename.lastIndexOf(".")).equals(".txt")
                ||
                originalFilename.substring(originalFilename.lastIndexOf(".")).equals(".pdf")
                ||
                originalFilename.substring(originalFilename.lastIndexOf(".")).equals(".doc");
    }

}
