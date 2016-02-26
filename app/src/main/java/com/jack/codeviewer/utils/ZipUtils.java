package com.jack.codeviewer.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by jack on 2/23/16.
 */
public class ZipUtils {
    private static final int BUFFER_SIZE = 4096;

    private static void extractFile(ZipInputStream in, File outdir, String name) {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(new File(outdir, name)));
            int count = -1;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private static void mkdirs(File outdir, String path) {
        File d = new File(outdir, path);
        if (!d.exists()) {
            d.mkdirs();
        }
    }

    private static String dirpart(String name) {
        int s = name.lastIndexOf(File.separator);
        return s == -1 ? null : name.substring(0, s);
    }

    public static String extract(File zipfile, File outdir) {
        String root = "";
        try {
            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipfile));
            ZipEntry entry;
            String name, dir;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                root = name.substring(0, name.indexOf("/"));
                if (entry.isDirectory()) {
                    mkdirs(outdir, name);
                    continue;
                }
                dir = dirpart(name);
                if (dir != null) {
                    mkdirs(outdir, dir);
                }
                extractFile(zin, outdir, name);
            }
            zin.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return root;
    }
}

