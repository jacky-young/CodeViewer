package com.jack.codeviewer;

import android.os.Environment;

import java.io.File;

/**
 * Created by jack on 2/22/16.
 */
public class AppConfig {

    public static final String ZIP_FILE_POSTFIX = "/archive/master.zip";

    public static final String SAVED_FILE_POSTFIX = ".zip";

    public static final String BUNDLE_KEY_DOWNLOAD_URL = "download_url";

    public static final String BUNDLE_KEY_CURRENT_SIZE = "download_size";

    public static final String DEFAULT_SAVE_FILE_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "CodeViewer"
            + File.separator
            + "download" + File.separator;

    public static final String DEFAULT_EXTRACT_FILE_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "CodeViewer"
            + File.separator
            + "extract" + File.separator;

    public static final int CONNECT_TIME_OUT_MS = 10000;
    public static final int READ_TIME_OUT_MS = 20000;

    public static final String TEST_GITHUB_URL = "https://github.com/nostra13/Android-Universal-Image-Loader";

}
