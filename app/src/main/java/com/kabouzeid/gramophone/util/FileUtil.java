package com.kabouzeid.gramophone.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.webkit.MimeTypeMap;

import com.kabouzeid.gramophone.loader.SongLoader;
import com.kabouzeid.gramophone.loader.SortedCursor;
import com.kabouzeid.gramophone.model.Song;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public final class FileUtil {
    private FileUtil() {
    }

    @NonNull
    public static ArrayList<Song> matchFilesWithMediaStore(@NonNull Context context, @Nullable List<File> files) {
        return SongLoader.getSongs(makeSongCursor(context, files));
    }

    @Nullable
    public static SortedCursor makeSongCursor(@NonNull final Context context, @Nullable final List<File> files) {
        String selection = null;
        String[] paths = null;

        if (files != null) {
            paths = toPathArray(files);

            if (files.size() > 0 && files.size() < 999) { // 999 is the max amount Androids SQL implementation can handle.
                selection = MediaStore.Audio.AudioColumns.DATA + " IN (" + makePlaceholders(files.size()) + ")";
            }
        }

        Cursor songCursor = SongLoader.makeSongCursor(context, selection, selection == null ? null : paths);

        return songCursor == null ? null : new SortedCursor(songCursor, paths, MediaStore.Audio.AudioColumns.DATA);
    }

    private static String makePlaceholders(int len) {
        StringBuilder sb = new StringBuilder(len * 2 - 1);
        sb.append("?");
        for (int i = 1; i < len; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }

    @Nullable
    private static String[] toPathArray(@Nullable List<File> files) {
        if (files != null) {
            String[] paths = new String[files.size()];
            for (int i = 0; i < files.size(); i++) {
                try {
                    paths[i] = files.get(i).getCanonicalPath(); // canonical path is important here because we want to compare the path with the media store entry later
                } catch (IOException e) {
                    e.printStackTrace();
                    paths[i] = files.get(i).getPath();
                }
            }
            return paths;
        }
        return null;
    }

    @NonNull
    public static List<File> listFiles(@NonNull File directory, @Nullable FileFilter fileFilter) {
        List<File> fileList = new LinkedList<>();
        File[] found = directory.listFiles(fileFilter);
        if (found != null) {
            Collections.addAll(fileList, found);
        }
        return fileList;
    }

    @NonNull
    public static List<File> listFilesDeep(@NonNull File directory, @Nullable FileFilter fileFilter) {
        List<File> files = new LinkedList<>();
        internalListFilesDeep(files, directory, fileFilter);
        return files;
    }

    @NonNull
    public static List<File> listFilesDeep(@NonNull Collection<File> files, @Nullable FileFilter fileFilter) {
        List<File> resFiles = new LinkedList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                internalListFilesDeep(resFiles, file, fileFilter);
            } else if (fileFilter == null || fileFilter.accept(file)) {
                resFiles.add(file);
            }
        }
        return resFiles;
    }

    private static void internalListFilesDeep(@NonNull Collection<File> files, @NonNull File directory, @Nullable FileFilter fileFilter) {
        File[] found = directory.listFiles(fileFilter);

        if (found != null) {
            for (File file : found) {
                if (file.isDirectory()) {
                    internalListFilesDeep(files, file, fileFilter);
                } else {
                    files.add(file);
                }
            }
        }
    }

    public static boolean fileIsMimeType(File file, String mimeType, MimeTypeMap mimeTypeMap) {
        if (mimeType == null || mimeType.equals("*/*")) {
            return true;
        } else {
            // get the file mime type
            String filename = file.toURI().toString();
            int dotPos = filename.lastIndexOf('.');
            if (dotPos == -1) {
                return false;
            }
            String fileExtension = filename.substring(dotPos + 1);
            String fileType = mimeTypeMap.getMimeTypeFromExtension(fileExtension);
            if (fileType == null) {
                return false;
            }
            // check the 'type/subtype' pattern
            if (fileType.equals(mimeType)) {
                return true;
            }
            // check the 'type/*' pattern
            int mimeTypeDelimiter = mimeType.lastIndexOf('/');
            if (mimeTypeDelimiter == -1) {
                return false;
            }
            String mimeTypeMainType = mimeType.substring(0, mimeTypeDelimiter);
            String mimeTypeSubtype = mimeType.substring(mimeTypeDelimiter + 1);
            if (!mimeTypeSubtype.equals("*")) {
                return false;
            }
            int fileTypeDelimiter = fileType.lastIndexOf('/');
            if (fileTypeDelimiter == -1) {
                return false;
            }
            String fileTypeMainType = fileType.substring(0, fileTypeDelimiter);
            if (fileTypeMainType.equals(mimeTypeMainType)) {
                return true;
            }
        }
        return false;
    }
}
