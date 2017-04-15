package org.dissernet.comparer;

import org.apache.log4j.Logger;
import org.dissernet.comparer.web.ComparerRestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Created by APuzikov on 18.02.2017.
 */

@Service
@Scope(value = "prototype")
public class MonoLauncher {

    Logger logger = Logger.getLogger(ComparerRestController.class);

    @Value(value = "${comparer.directory}")
    String comparerDir;

    public String processFile(File[] files, String resultFormat) throws IOException, InterruptedException {
        logger.info("Processing files. " + files);
        File taskFile = prepareTaskFile(files);
        String absolutePath = taskFile.getAbsolutePath();
        Runtime runtime = Runtime.getRuntime();
        Process exec = runtime.exec(comparerDir + " -input " + absolutePath + " -format " + resultFormat);
        if (exec.waitFor() != 0) {
            throw new IOException("Comparer failed with exception");
        }
        StringBuffer lineBuffer = new StringBuffer();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                lineBuffer.append(line);
            }
        }
        String processResult = lineBuffer.toString();
        logger.info("Tasks processed. " + processResult);
        return processResult;
    }

    private File prepareTaskFile(File[] files) throws IOException {
        File tempFile = File.createTempFile("task", "mp");
        logger.info("Preparing task file. " + tempFile);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            boolean fistOne = true;
            for (File file : files) {
                if (!fistOne) {
                    writer.newLine();
                }
                fistOne = false;
                logger.info(file.getAbsolutePath());
                writer.write(file.getAbsolutePath());
            }
        }
        logger.info("Task file prepared.");
        return tempFile;
    }

}
