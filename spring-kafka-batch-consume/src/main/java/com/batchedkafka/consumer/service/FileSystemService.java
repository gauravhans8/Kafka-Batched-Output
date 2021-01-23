package com.batchedkafka.consumer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class FileSystemService {

    private final Logger logger = LoggerFactory.getLogger(KafKaConsumerService.class);

    @Value(value = "${spring.fileSystem.parentDir}")
    private String parentDirectory;

    public synchronized void mergeAndProcessMessages(List<String> messages, String filePattern, double sizeThreshold, long timeThreshold) {
        messages.forEach(message -> logger.info("@@ " + message));
        Path pathStat = Paths.get(parentDirectory+filePattern+"stat");
        Path pathStaging = Paths.get(parentDirectory+filePattern+"staging");
        Instant instant = Instant.now();
        long timeStampMillis = instant.getEpochSecond();
        long creationTime = 0;
        if(Files.exists(pathStat)){
            creationTime = getLastFileCreationTime(pathStat);
            if(timeThreshold != 0 && creationTime >= 0 && (timeStampMillis-creationTime)>=timeThreshold){
                moveStageFile(pathStaging,filePattern);
            }
        }
        logger.info("before writing to stage file :::::::::");
        creationTime = writeStagingFile(pathStaging,messages,creationTime);
        double newSize = new File(parentDirectory+filePattern+"staging").length() / 1024d;
        logger.info("after writing to stage file ::::::::: " + newSize + "@@@@@@@" + sizeThreshold);
        if(sizeThreshold != 0 && newSize>=sizeThreshold) {
            moveStageFile(pathStaging, filePattern);
            creationTime = -1;
            logger.info("##################" + creationTime);
        }
        writeStatFile(pathStat,creationTime);
        logger.info("File Operation Done ::::::");
    }

    private long getLastFileCreationTime(Path pathStat){
        logger.info("Inside stat exists");
        long creationTime = 0;
        try (BufferedReader bufferedReader = Files.newBufferedReader(pathStat)) {
            creationTime = Long.parseLong(bufferedReader.readLine());
        } catch (IOException e) {
            logger.error("Could not open Stat File, Exiting",e);
            throw new RuntimeException(e);
        }
        logger.info("after reading stat file :: " + creationTime);
        return creationTime;
    }

    private long writeStagingFile(Path pathStaging, List<String> messages, long creationTime){
        try {
            if(Files.exists(pathStaging)) {
                Files.write(pathStaging, messages, StandardOpenOption.APPEND);
            }
            else{
                Files.write(pathStaging, messages, StandardOpenOption.CREATE);
                creationTime = Instant.now().getEpochSecond();
            }
        } catch (IOException e) {
            logger.error("Could not write to stage file, Exiting",e);
            throw new RuntimeException(e);
        }
        return creationTime;
    }

    private void writeStatFile(Path pathStat, long creationTime){
        String newStats = creationTime + "";
        try {
            Files.write(pathStat,newStats.getBytes(StandardCharsets.UTF_8),StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            logger.error("Could not move staging file to actual",e);
            throw new RuntimeException(e);
        }
    }

    private void moveStageFile(Path pathStaging, String filePattern) {
        try {
            logger.info("INSIDE MOVE METHOD ::::::::::::::::::::::");
            Files.move(pathStaging, Paths.get(parentDirectory + filePattern + UUID.randomUUID().toString()));
        } catch (IOException e) {
            logger.error("Could not move staging file to actual",e.getCause());
            throw new RuntimeException(e);
        }
    }
}
