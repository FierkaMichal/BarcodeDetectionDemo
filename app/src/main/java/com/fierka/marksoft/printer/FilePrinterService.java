package com.fierka.marksoft.printer;

import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintManager;

public class FilePrinterService {

    private static final String PRINT_JOB_NAME = "EXAMPLE_FILE_PRINT_JOB";

    public void print(Context context) {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);

        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
                .build();

        printManager.print(PRINT_JOB_NAME, new PdfPrinterAdapter(context), printAttributes);
    }
}
