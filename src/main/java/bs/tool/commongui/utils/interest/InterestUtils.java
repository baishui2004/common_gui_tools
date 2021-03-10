package bs.tool.commongui.utils.interest;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class InterestUtils {

    /**
     * 精度.
     */
    public final static int SCALE = 20;

    /**
     * 每月存款变化计算复利.
     *
     * @param principal 初始本金
     * @param amount    初始月存金额
     * @param monthDiff 每月差额
     * @param rate      月利率
     * @param months    存款月数
     */
    public static BigDecimal compoundInterest(BigDecimal principal, BigDecimal amount, BigDecimal monthDiff, BigDecimal rate, int months) {
        BigDecimal all = new BigDecimal(0);
        for (int m = 0; m < months; m++) {
            // 每月本金金额逐月递减
            all = all.add(fixedPrincipalCompoundInterest(amount.subtract(monthDiff.multiply(new BigDecimal(m))), rate, months - m));
        }
        return all.add(fixedPrincipalCompoundInterest(principal, rate, months));
    }

    /**
     * 固定本金计算复利.
     *
     * @param principal 本金
     * @param rate      月利率
     * @param months    存款月数
     */
    public static BigDecimal fixedPrincipalCompoundInterest(BigDecimal principal, BigDecimal rate, int months) {
        for (int s = 0; s < months; s++) {
            principal = principal.multiply(rate.add(new BigDecimal(1)));
        }
        return principal;
    }

    /**
     * 分期真实年化利率计算.
     * 二分最大次数100次进行计算，总本金差值精度精确到0.001.
     *
     * @param declaredPeriodRate 名义每期费率
     * @param periods            期数
     * @return
     */
    public static BigDecimal calculatePeriodActualRate(String declaredPeriodRate, int periods) {
        // 假定本金10000
        BigDecimal principal = new BigDecimal(10000);
        // 平均每期本金
        BigDecimal avgPrincipal = principal.divide(new BigDecimal(periods), InterestUtils.SCALE, RoundingMode.HALF_UP);
        // 利息总额
        BigDecimal interest = principal.multiply(new BigDecimal(declaredPeriodRate)).multiply(new BigDecimal(periods));
        // 每期还款额
        BigDecimal periodAmount = (interest.add(principal)).divide(new BigDecimal(periods), InterestUtils.SCALE, RoundingMode.HALF_UP);
        BigDecimal assumeRate = new BigDecimal(0);
        BigDecimal head = new BigDecimal(0);
        BigDecimal tail = avgPrincipal;
        // 使用二分推测首期本金
        BigDecimal firstPeriodPrincipal;
        // 二分最大次数100次进行计算，总本金差值精度精确到0.001
        for (int i = 0; i < 100; i++) {
            firstPeriodPrincipal = (head.add(tail)).divide(new BigDecimal(2));
            // 以首期还款额 = 总本金 * 实际月利率 + 首期本金 计算月利率
            assumeRate = (periodAmount.subtract(firstPeriodPrincipal)).divide(principal, InterestUtils.SCALE, RoundingMode.HALF_UP);
            BigDecimal assumePrincipal = new BigDecimal(0);
            BigDecimal assumePeriodPrincipal = firstPeriodPrincipal;
            for (int p = 0; p < periods; p++) {
                assumePrincipal = assumePrincipal.add(assumePeriodPrincipal);
                assumePeriodPrincipal = periodAmount.subtract(principal.subtract(assumePrincipal).multiply(assumeRate));
            }
            double s = principal.subtract(assumePrincipal).doubleValue();
            if (s >= -0.001 && s <= 0.001) {
                // System.out.println("calculateActualRate success times: " + i + ", head: " + head + ", tail: " + tail);
                break;
            } else if (s > 0.001) {
                head = firstPeriodPrincipal;
            } else if (s < -0.001) {
                tail = firstPeriodPrincipal;
            }

        }
        return assumeRate;
    }

}
