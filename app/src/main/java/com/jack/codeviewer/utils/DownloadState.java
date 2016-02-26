package com.jack.codeviewer.utils;

import com.jack.codeviewer.AppConfig;

import java.io.Serializable;

/**
 * Created by jack on 2/22/16.
 */
public class DownloadState implements Serializable{
    public static final String DOWNLOAD_ING = "downloading...";
    public static final String DOWNLOAD_FINISHED = "download finished";
    public static final String DOWNLOAD_WAIT = "download wait";
    public static final String DOWNLOAD_ERROR = "download error";
    public static final String EXTRACT_FAILED = "extract failed";
    public static final String EXTRACT_SUCCESSS = "extract successful";

    private String downloadUrl;

    private String gitName;

    private String oldGitName;

    private String savedFileName;

    private String savedFilePath;

    private String extractFilePath;

    private int totalSize;

    private String downloadState;

    public DownloadState() {
        downloadUrl = "";
        gitName = "";
        oldGitName = "";
        savedFileName = "";
        savedFilePath = AppConfig.DEFAULT_SAVE_FILE_PATH;
        extractFilePath = AppConfig.DEFAULT_EXTRACT_FILE_PATH;
        totalSize = 0;
        downloadState = DOWNLOAD_WAIT;
    }

    @Override
    public String toString() {
        return "Download Url: " + downloadUrl + "\n"
             + "Git Name: " + gitName + "\n"
             + "Saved File Name: " + savedFileName + "\n"
             + "Saved File Path: " + savedFilePath + "\n"
             + "Extract File Path: " + extractFilePath + "\n"
             + "Total Size: " + totalSize + "B\n"
             + "Download State: " + downloadState;
    }

    public void setDownloadUrl(String url) {
        downloadUrl = url;
    }

    public void setGitName(String name) {
        gitName = name;
    }

    public void setSavedFileName(String name) {
        savedFileName = name;
    }

    public void setTotalSize(int size) {
        totalSize = size;
    }

    public void setState(String state) {
        downloadState = state;
    }

    public void setOldGitName(String name) {
        oldGitName = name;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getGitName() {
        return gitName;
    }

    public String getSavedFileName() {
        return savedFileName;
    }

    public String getSavedFilePath() {
        return savedFilePath;
    }

    public String getExtractFilePath() {
        return  extractFilePath;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public String getState() {
        return  downloadState;
    }

    public String getOldGitName() {
        return oldGitName;
    }
}
