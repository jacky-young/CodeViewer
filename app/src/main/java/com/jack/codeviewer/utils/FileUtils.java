package com.jack.codeviewer.utils;

import com.jack.codeviewer.AppConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 2/23/16.
 */
public class FileUtils {

    public static boolean unzipFile(final DownloadState state) {
        if (state.getGitName() != "" && state.getSavedFileName() != ""
                && state.getSavedFilePath() != "") {
            File zipFile = new File(state.getSavedFilePath()+state.getSavedFileName());
            File outFile = new File(state.getExtractFilePath());
            if (zipFile.exists()) {
                if (!outFile.exists()) {
                    outFile.mkdirs();
                }
                state.setOldGitName(ZipUtils.extract(zipFile, outFile));
                return true;
            }
        }
        return false;
    }

    public static boolean renameRoot(final DownloadState state) {
        String oldName = state.getOldGitName();
        String newName = state.getGitName();
        if (oldName.equals("")) {
            return false;
        }
        File root = new File(state.getExtractFilePath());
        String[] files = root.list();
        File f = null;
        for (String file : files) {
            if (file.equals(newName)) {
                File original = new File(state.getExtractFilePath() + newName);
                deleteDir(original);
            }
        }
        for (String file : files) {
            if (file.equals(oldName)) {
                f = new File(root, file);
                f.renameTo(new File(state.getExtractFilePath() + newName));
                return true;
            }
        }
        return false;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static boolean isFileEqual(String pathA, String pathB) {
        if (pathA == null || pathA.equals("")) {
            return false;
        }
        if (pathB == null || pathB.equals("")) {
            return false;
        }

        File fileA = new File(pathA);
        File fileB = new File(pathB);
        return fileA.equals(fileB);
    }

    public static boolean isDirectory(String file) {
        if (file != null) {
            File f = new File(file);
            if (f.exists() && f.isDirectory()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFile(String file) {
        if (file != null) {
            File f = new File(file);
            if (f.exists() && f.isFile()) {
                return true;
            }
        }
        return false;
    }

    public static GitPage getLowerGitPage(GitPage page) {
        String parent = page.getParent();
        if (parent.equals("")) {
            return null;
        }

        File parentFile = new File(parent);
        if (!parentFile.exists() || isFile(parent)) {
            return null;
        }

        page.initElementList();
        String[] children = parentFile.list();
        for (String child : children) {
            String wholePath = parent + File.separator + child;
            if (isDirectory(wholePath)) {
                String newChild = child + File.separator + shortFolderPath(wholePath);
                page.addElementList(newChild);
            } else {
                page.addElementList(child);
            }
        }
        return page;
    }

    public static GitPage getUpperGitPage(GitPage page) {
        String parent = page.getParent();
        if (parent.equals("")) {
            return null;
        }

        File parentFile = new File(parent);
        if (!parentFile.exists() || isFile(parent)) {
            return null;
        }

        if (page.getLevelCount() == 0) {
            page.setParent(page.getHomePath());
        } else {
            page.setParent(computeUpperShortPath(parent));
        }

        return getLowerGitPage(page);
    }

    private static String computeUpperShortPath(String path) {
        String firstUpper = getUpperLevel(path);
        File upperFile = new File(firstUpper);
        if (upperFile.exists()) {
            String[] children = upperFile.list();
            if (children.length == 1 && isDirectory(firstUpper + File.separator + children[0])) {
                return computeUpperShortPath(firstUpper);
            }
            return firstUpper;
        }
        return "";
    }

    private static String getUpperLevel(String path) {
        String result = "";
        List<String> all = getAllPaths(path);
        for (int i = 0; i < all.size() - 1; i++) {
            result += File.separator + all.get(i);
        }
        return result;
    }

    private static List<String> getAllPaths(String path) {
        List<String> all = new ArrayList<String>();
        String[] strArray = path.trim().split("///|//|/");
        for (int i = 0; i < strArray.length; i++) {
            if (strArray[i].equals("")) {
                continue;
            }
            all.add(strArray[i]);
        }
        return all;
    }

    private static String getCleanPath(String path) {
        String result = "";
        List<String> all = getAllPaths(path);
        for (int i = 0; i < all.size(); i++) {
            result += File.separator + all.get(i);
        }
        return result;
    }

    private static String shortFolderPath(String path) {
        File f = new File(path);
        if (f.exists()) {
            String[] children = f.list();
            if (children.length == 1 && isDirectory(path + File.separator + children[0])) {
                return children[0] + File.separator
                        +shortFolderPath(path + File.separator + children[0]);
            }
        }
        return "";
    }

    public static List<DownloadState> initDownloadStateList() {
        List<DownloadState> list = new ArrayList<DownloadState>();
        File extractRoot = new File(AppConfig.DEFAULT_EXTRACT_FILE_PATH);
        String[] children = extractRoot.list();
        if (children.length > 0) {
            for (String child : children) {
                DownloadState state = new DownloadState();
                state.setGitName(child);
                list.add(state);
            }
        }
        return list;
    }

    public static String getShortTitle(GitPage page) {
        if (page.getLevelCount() == 0) {
            return page.getGitName();
        }
        String parent = page.getParent();
        int index = parent.indexOf(page.getGitName()) + page.getGitName().length();
        return getCleanPath(parent.substring(index));
    }
}
