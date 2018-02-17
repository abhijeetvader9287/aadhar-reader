package com.rajdeol.aadhaarreader.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * The type Storage.
 */
public class Storage {
    private static final String STORAGE_FILE_NAME = "data_storage.txt";
    private final Context mContext;

    /**
     * Instantiates a new Storage.
     *
     * @param activity the activity
     */
    public Storage(Context activity) {
        mContext = activity;
    }

    /**
     * Write to file.
     *
     * @param data the data
     */
    public void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(mContext.openFileOutput(STORAGE_FILE_NAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /**
     * Read from file string.
     *
     * @return the string
     */
    public String readFromFile() {
        // ensure file is created
        checkFilePresent();

        String ret = "";

        try {
            InputStream inputStream = mContext.openFileInput(STORAGE_FILE_NAME);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("Storage activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Storage activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void checkFilePresent() {
        String path = mContext.getFilesDir().getAbsolutePath() + "/" + Storage.STORAGE_FILE_NAME;
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
