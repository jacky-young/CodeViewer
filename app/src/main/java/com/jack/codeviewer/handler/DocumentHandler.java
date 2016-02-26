package com.jack.codeviewer.handler;

/**
 * Created by jack on 2/26/16.
 */
public interface DocumentHandler {
    public String getFileExtension();
    public String getFileMimeType();
    public String getFilePrettifyClass();
    public String getFileFormattedString(String fileString);
    public String getFileScriptFiles();

}
