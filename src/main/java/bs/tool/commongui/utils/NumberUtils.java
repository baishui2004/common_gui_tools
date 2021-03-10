package bs.tool.commongui.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NumberUtils {

    /**
     * 转化为百分比展示，百分比后精确到2位小数.
     *
     * @param obj
     * @return
     */
    public static String convertPercentageStr(Object obj) {
        return convertPrecision(obj, 100) + "%";
    }

    /**
     * 精确到3位精度.
     *
     * @param obj
     * @return
     */
    public static String convertPrecision(Object obj) {
        return convertPrecision(obj, 1);
    }

    /**
     * 精确到3位精度.
     *
     * @param obj
     * @param multi
     * @return
     */
    public static String convertPrecision(Object obj, int multi) {
        if (obj instanceof Double) {
            return String.format("%.3f", ((Double) obj) * multi);
        } else if (obj instanceof BigDecimal) {
            DecimalFormat format = new DecimalFormat("0.000");
            BigDecimal decimal = (BigDecimal) obj;
            if (decimal != null) {
                return format.format(decimal.multiply(new BigDecimal(multi)));
            } else {
                return "0.000";
            }
        } else {
            throw new RuntimeException("Convert precision string error, not support object type.");
        }
    }

}
