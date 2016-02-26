package com.jack.codeviewer.handler;

import android.text.TextUtils;

/**
 * Created by jack on 2/26/16.
 */
public class JavaDocumentHandler implements DocumentHandler {
    @Override
    public String getFileExtension() {
        return ".java";
    }

    @Override
    public String getFileFormattedString(String fileString) {
        return TextUtils.htmlEncode(fileString).replace("\n", "<br>");
    }

    @Override
    public String getFileMimeType() {
        return "text/html";
    }

    @Override
    public String getFilePrettifyClass() {

        return "prettyprint";
    }

    @Override
    public String getFileScriptFiles() {
        return "";
    }
}
