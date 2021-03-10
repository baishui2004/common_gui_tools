package bs.tool.commongui.utils;


import jxl.format.*;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;

public class ExcelUtils {

    public static WritableCellFormat getHeaderCellFormat() throws WriteException {
        return getCellFormat(Alignment.CENTRE, Colour.GRAY_25);
    }

    public static WritableCellFormat getBodyCellFormat() throws WriteException {
        return getCellFormat(Alignment.LEFT, Colour.WHITE);
    }

    private static WritableCellFormat getCellFormat(Alignment alignment, Colour colour) throws WriteException {
        WritableFont arial10font = new WritableFont(WritableFont.ARIAL, 10);
        WritableCellFormat arial10format = new WritableCellFormat(arial10font);
        arial10format.setAlignment(alignment);
        arial10format.setVerticalAlignment(VerticalAlignment.CENTRE);
        arial10format.setBackground(colour);
        arial10format.setWrap(true);
        arial10format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
        return arial10format;
    }

}
