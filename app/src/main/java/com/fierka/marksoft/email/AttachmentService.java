package com.fierka.marksoft.email;

import android.content.Context;

import java.io.*;

public class AttachmentService {

    private final Context context;

    public AttachmentService(Context context) {
        this.context = context;
    }

    public File createAttachment(String path) throws IOException {
        File targetFile = null;
        OutputStream out = null;
        InputStream in = null;

        try {
            in = context.getAssets().open(path);

            byte[] buffer = new byte[in.available()];
            in.read(buffer);

            targetFile = new File(context.getExternalFilesDir(null), path);
            out = new FileOutputStream(targetFile);
            copyFile(in, out);

            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        }
        return targetFile;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
