package bs.tool.commongui.utils.interest;

import bs.tool.commongui.utils.NumberUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static bs.tool.commongui.utils.interest.LoanInterestBean.*;

public class LoanInterestUtils {


    // [房贷“等额本息”和“等额本金”怎么选?银行员工:牢记这5点_房产资讯_房天下](https://cd.news.fang.com/open/36952602.html)
    // [等额本息计算公式_等额本息提前还款法_ 等额本息贷款计算 - 融360](https://www.rong360.com/baike/1.html)
    // [等额本金计算公式_等额本金还款法 - 融360](https://www.rong360.com/baike/2.html)

    // 好用的在线房贷、提前还款、利率等等计算器：http://www.tiqianhuandai.cc/

    // 贷款计算：[房贷计算器2021 在线贷款、房贷明细计算器 - 房贷精灵](http://www.baiozhuntuixing.com/)
    // 提前还款计算：[提前还款计算器2021 - 提前还贷计算器2021 - 房贷计算器2021](http://www.tiqianhuandai.cc/)
    // 提前还款是否划算：[提前还款是否划算计算器 贷款、房贷什么时提前还款最划算 - 房贷计算器](http://www.baiozhuntuixing.com/huasuan.aspx)


    // 等额本息：每月还款数额一致，一部分利息，一部分本金，本金所占比例逐月递增，利息所占比例逐月减少。
    //         还款总额=还款月数*贷款额*月利率*(1+月利率)^贷款月数/[(1+月利率)^还款月数 - 1]      // https://www.rong360.com/baike/1.html
    // 等额本金：每月还款本金一致，一部分利息，一部分本金，利息随着本金逐渐减少，每月还款金额逐渐减少。
    //         还款总额=(还款月数+1)*贷款额*月利率/2+贷款额      // https://www.rong360.com/baike/2.html

    // 结论：1. 因为每期利息都等于剩余本金*当期利率，所以一定是等额本息每期利息大于等于等额本金，且只有首期相等；
    //      2. 等额本息前期还款利息较多，不利于提前还款，等额本金方式利息比较稳定，任何时候提前还款都有意义；无论哪种方式，前期已还款利息占比总利息都很高；越早提前还款越省利息；
    //      3. 提前还款叠加缩短还款期限更能节省利息，并且节省的非常多；
    //      4. 当存贷款利率相同时，等额本息与等额本金的最终利率、存款收益可以填平一致；否则，等额本金利息更低；

    // 问题：1. 2种方式不同年限分别哪种方式提前还款相对合算？
    //      2. 2种方式还利息分别达到1/3, 1/2, 2/3, 3/4的时间以及剩余本金比例；
    //      3. 为何分期变长，利率反而有所微降？


    public static void main(String[] args) {
        System.out.println(Math.pow(1 + 0.0075, 12) - 1);

        // 分期名义利率真实利率计算
        String declaredMonthRate = "0.01";
        int periods = 3;
        BigDecimal actualRate = InterestUtils.calculatePeriodActualRate(declaredMonthRate, periods);
        System.out.println(periods + "期名义利率" + declaredMonthRate + "的实际利率："
                + NumberUtils.convertPercentageStr(actualRate)
                + "，年化利率：" + NumberUtils.convertPercentageStr(actualRate.multiply(new BigDecimal(12))));
        int p = 1;
        while (p <= 360) {
            System.out.println(p + "期真实年化利率：" + NumberUtils.convertPercentageStr(
                    InterestUtils.calculatePeriodActualRate(declaredMonthRate, p).multiply(new BigDecimal(12))));
            p++;
        }

        // 假设10000的本金
        int principal = 10000;
        double interest = principal * Double.parseDouble(declaredMonthRate) * periods;
        System.out.println("principal=" + principal + ", avgPrincipal=" + NumberUtils.convertPrecision(principal * 1.0 / periods)
                + ", interest=" + interest + ", periodAmount=" + NumberUtils.convertPrecision((principal + interest) / periods));


        int totalAmount = 250 * 10000;
        String rate = "5.05";
        int year = 30;

        System.out.println("------ 基本情况 ------");
        LoanInterestBean bean = new LoanInterestBean(totalAmount, rate, year, LendType.Interest);
        LoanInterestBean iRsBean = calculateByLendType(bean);
        System.out.println("等额本息 " + iRsBean);
        bean.setLendType(LendType.Principal);
        LoanInterestBean pRsBean = calculateByLendType(bean);
        System.out.println("等额本金 " + pRsBean);


        System.out.println("\n------ 利率对比 ------");
        String cRate = "5.10";
        LoanInterestBean rBean = new LoanInterestBean(totalAmount, cRate, year, LendType.Interest);
        LoanInterestBean iRsBean2 = calculateByLendType(rBean);
        rBean.setLendType(LendType.Principal);
        LoanInterestBean pRsBean2 = calculateByLendType(rBean);
        System.out.println("利率从" + rate + "变到" + cRate
                + "\n    等额本息月还款额变化：" + decimalFormat(iRsBean.getMonthRepayments().get(0).getAmount()) + " -> " + decimalFormat(iRsBean2.getMonthRepayments().get(0).getAmount())
                + "，总利息变化：" + decimalFormat(iRsBean.getInterestTotalAmount()) + " -> " + decimalFormat(iRsBean2.getInterestTotalAmount())
                + "，少还利息：" + decimalFormat(iRsBean.getInterestTotalAmount().subtract(iRsBean2.getInterestTotalAmount()))
                + "\n    等额本金月还款额变化：" + decimalFormat(pRsBean.getMonthRepayments().get(0).getAmount()) + " -> " + decimalFormat(pRsBean2.getMonthRepayments().get(0).getAmount())
                + "，总利息变化：" + decimalFormat(pRsBean.getInterestTotalAmount()) + " -> " + decimalFormat(pRsBean2.getInterestTotalAmount())
                + "，少还利息：" + decimalFormat(pRsBean.getInterestTotalAmount().subtract(pRsBean2.getInterestTotalAmount())));


        System.out.println("\n------ 两者对比 ------");
        // 存款复利收益
        String compoundRate = "4.00";

        // 等额本息相比等额本金
        ComparePrincipalInterest compare = comparePrincipalInterest(new ComparePrincipalInterest(pRsBean, iRsBean, compoundRate));
        int firstThan = compare.getFirstLessThanMonth();
        System.out.println("等额本息比等额本金多还利息：" + LoanInterestUtils.decimalFormat(compare.getSavedInterest())
                + "，月还款额前者首次大于后者的期数：" + firstThan
                + "，截止该期后者比前者累计多还：" + LoanInterestUtils.decimalFormat(compare.getMoreLentBefore())
                + "，自该期后者比前者累计少还：" + LoanInterestUtils.decimalFormat(compare.getLessLentBefore()));

        System.out.println("以年化" + compoundRate + "%计算，等额本息比等额本金存款复利少：" + LoanInterestUtils.decimalFormat(compare.getPrincipalFirstLessThanMonthCompoundInterest()
                .subtract(compare.getFirstLessThanMonthCompoundTwiceInterest()))
                + "，月还款额前者首次大于后者时的前者累积少还部分钱的复利总额：" + LoanInterestUtils.decimalFormat(compare.getFirstLessThanMonthCompoundInterest())
                + "，随后还款日期里该部分钱的复利总额：" + LoanInterestUtils.decimalFormat(compare.getFirstLessThanMonthCompoundTwiceInterest())
                + "，自该期后者比前者累计少还部分钱的复利总额：" + LoanInterestUtils.decimalFormat(compare.getPrincipalFirstLessThanMonthCompoundInterest()));


        System.out.println("\n------ 提前还款 ------");
        // 提前5年、还款50W、还款后缩短还款期限5年
        int tqYear = 5;
        int tqAmount = 50 * 10000;
        int yearReduce = 5;

        // 等额本息提前还款
        LendAheadOfSchedule ahead = new LendAheadOfSchedule(iRsBean, tqYear, tqAmount, LendType.Interest, rate, yearReduce);
        ahead = calculateLendAhead(ahead);
        System.out.println("等额本息" + tqYear + "年已还本金：" + LoanInterestUtils.decimalFormat(ahead.getLentPrincipal())
                + "，已还利息：" + LoanInterestUtils.decimalFormat(ahead.getLentInterest())
                + "，提前" + tqYear + "年还款" + tqAmount + "并且缩短还款" + yearReduce + "年节省利息：" + LoanInterestUtils.decimalFormat(ahead.getSavedInterest())
                + "，实际总利息：" + LoanInterestUtils.decimalFormat(ahead.getLentInterest().add(ahead.getNewBean().getInterestTotalAmount()))
                + "，提前还款后情况 " + ahead.getNewBean());

        // 等额本金提前还款
        ahead = new LendAheadOfSchedule(pRsBean, tqYear, tqAmount, LendType.Principal, rate, yearReduce);
        ahead = calculateLendAhead(ahead);
        System.out.println("等额本金" + tqYear + "年已还本金：" + LoanInterestUtils.decimalFormat(ahead.getLentPrincipal())
                + "，已还利息：" + LoanInterestUtils.decimalFormat(ahead.getLentInterest())
                + "，提前" + tqYear + "年还款" + tqAmount + "并且缩短还款" + yearReduce + "年节省利息：" + LoanInterestUtils.decimalFormat(ahead.getSavedInterest())
                + "，实际总利息：" + LoanInterestUtils.decimalFormat(ahead.getLentInterest().add(ahead.getNewBean().getInterestTotalAmount()))
                + "，提前还款后情况 " + ahead.getNewBean());
    }

    /**
     * 根据计息方式计算.
     *
     * @param bean
     * @return
     */
    public static LoanInterestBean calculateByLendType(LoanInterestBean bean) {
        if (bean.getLendType() == LendType.Interest) {
            return equalMonthlyAmount(bean);
        } else if (bean.getLendType() == LendType.Principal) {
            return equalMonthlyPrincipal(bean);
        }
        return null;
    }

    /**
     * 提前还款计算.
     *
     * @param ahead
     * @return
     */
    public static LendAheadOfSchedule calculateLendAhead(LendAheadOfSchedule ahead) {
        ahead = new LendAheadOfSchedule(ahead);
        // 原还款情况
        LoanInterestBean bean = ahead.getOriginBean();
        // 提前期数的还款情况
        MonthlyLendAmount lentAmount = bean.getMonthRepayments().get(ahead.getYear() * 12 - 1);
        // 已还本金
        BigDecimal lentPrincipal = lentAmount.getPrincipalTotal();
        // 已还利息
        BigDecimal lentInterest = lentAmount.getInterestTotal();
        ahead.setLentPrincipal(lentPrincipal);
        ahead.setLentInterest(lentInterest);

        // 提前还款后还款情况
        LoanInterestBean newBean = calculateByLendType(new LoanInterestBean(bean.getLoanTotalAmount().subtract(lentPrincipal).toBigInteger().intValue() - ahead.getAmount(),
                ahead.getRate(), bean.getLendYear() - ahead.getYear() - ahead.getYearReduce(), ahead.getLendType()));
        ahead.setNewBean(newBean);
        // 节省利息
        ahead.setSavedInterest(bean.getInterestTotalAmount().subtract(lentInterest).subtract(newBean.getInterestTotalAmount()));
        return ahead;
    }

    /**
     * 等额本金与等额本息对比.
     *
     * @param compare
     * @return
     */
    public static ComparePrincipalInterest comparePrincipalInterest(ComparePrincipalInterest compare) {
        compare = new ComparePrincipalInterest(compare);
        LoanInterestBean principalBean = compare.getPrincipalBean();
        LoanInterestBean interestBean = compare.getInterestBean();
        compare.setSavedInterest(interestBean.getInterestTotalAmount().subtract(principalBean.getInterestTotalAmount()));

        // 等额本金每月还款额度首次大于等额本息的每月还款额度的期数
        List<MonthlyLendAmount> iMonthRepayments = interestBean.getMonthRepayments();
        BigDecimal monthRepayment = iMonthRepayments.get(0).getAmount();
        List<MonthlyLendAmount> pMonthRepayments = principalBean.getMonthRepayments();
        int ml = principalBean.getLendMonth();
        // 首次还款数额接近的期数
        int firstThan = -1;
        for (int mi = 0; mi < ml; mi++) {
            BigDecimal pAmount = pMonthRepayments.get(mi).getAmount();
            if (pAmount.compareTo(monthRepayment) < 0) {
                firstThan = mi + 1;
                break;
            }
        }
        compare.setFirstLessThanMonth(firstThan);
        compare.setMoreLentBefore((pMonthRepayments.get(firstThan - 1).getInterestTotal().add(pMonthRepayments.get(firstThan - 1).getPrincipalTotal()))
                .subtract(iMonthRepayments.get(firstThan - 1).getInterestTotal().add(iMonthRepayments.get(firstThan - 1).getPrincipalTotal())));
        compare.setLessLentBefore(compare.getSavedInterest().add(compare.getMoreLentBefore()));

        // 计算存款复利收益
        BigDecimal newMonthRate = new BigDecimal(compare.getCompoundRate()).divide(new BigDecimal(1200), InterestUtils.SCALE, RoundingMode.HALF_UP);
        BigDecimal firstCompound = InterestUtils.compoundInterest(new BigDecimal(0), pMonthRepayments.get(0).getAmount().subtract(monthRepayment),
                principalBean.getMonthDecrease(), newMonthRate, firstThan);
        compare.setFirstLessThanMonthCompoundInterest(firstCompound);

        BigDecimal firstTwiceCompound = InterestUtils.fixedPrincipalCompoundInterest(firstCompound, newMonthRate, ml - firstThan);
        compare.setFirstLessThanMonthCompoundTwiceInterest(firstTwiceCompound);

        BigDecimal secondCompound = InterestUtils.compoundInterest(new BigDecimal(0), new BigDecimal(0),
                new BigDecimal(0).subtract(principalBean.getMonthDecrease()), newMonthRate, ml - firstThan);
        compare.setPrincipalFirstLessThanMonthCompoundInterest(secondCompound);

        return compare;
    }

    /**
     * 等额本金方式计算每期还款情况.
     *
     * @param bean
     * @return
     */
    private static List<MonthlyLendAmount> calculatePrincipalMonthLend(LoanInterestBean bean) {
        List<MonthlyLendAmount> monthlyLendAmounts = new ArrayList<MonthlyLendAmount>();

        // 已还本金
        BigDecimal lentPrincipal = new BigDecimal(0);
        // 已还利息
        BigDecimal lentInterest = new BigDecimal(0);
        int months = bean.getLendMonth();
        // 每月还款本金
        BigDecimal monthlyPrincipal = bean.getLoanTotalAmount().divide(new BigDecimal(months), InterestUtils.SCALE, RoundingMode.HALF_UP);

        BigDecimal loanTotalAmount = bean.getLoanTotalAmount();
        for (int m = 0; m < months; m++) {
            // 当月利息
            BigDecimal curMonthInterest = (loanTotalAmount.subtract(lentPrincipal)).multiply(bean.getLendMonthRate());
            lentPrincipal = lentPrincipal.add(monthlyPrincipal);
            lentInterest = lentInterest.add(curMonthInterest);

            monthlyLendAmounts.add(new MonthlyLendAmount(m + 1, monthlyPrincipal, curMonthInterest, lentPrincipal, lentInterest, bean));
        }
        return monthlyLendAmounts;
    }

    /**
     * 等额本息方式计算每期还款情况.
     *
     * @param bean
     * @return
     */
    private static List<MonthlyLendAmount> calculateInterestMonthLend(LoanInterestBean bean) {
        List<MonthlyLendAmount> monthlyLendAmounts = new ArrayList<MonthlyLendAmount>();

        // 已还本金
        BigDecimal lentPrincipal = new BigDecimal(0);
        // 已还利息
        BigDecimal lentInterest = new BigDecimal(0);
        int months = bean.getLendMonth();
        // 每月还款金额
        BigDecimal monthRepayment = bean.getLoanTotalPrincipalAndInterest().divide(new BigDecimal(months), InterestUtils.SCALE, RoundingMode.HALF_UP);
        BigDecimal loanTotalAmount = bean.getLoanTotalAmount();
        for (int m = 0; m < months; m++) {
            // 当月利息
            BigDecimal curMonthInterest = (loanTotalAmount.subtract(lentPrincipal)).multiply(bean.getLendMonthRate());
            // 当月本金
            BigDecimal curMonthPrincipal = monthRepayment.subtract(curMonthInterest);
            lentPrincipal = lentPrincipal.add(curMonthPrincipal);
            lentInterest = lentInterest.add(curMonthInterest);

            monthlyLendAmounts.add(new MonthlyLendAmount(m + 1, curMonthPrincipal, curMonthInterest, lentPrincipal, lentInterest, bean));
        }
        return monthlyLendAmounts;
    }

    /**
     * 等额本金.
     *
     * @param bean
     * @return
     */
    private static LoanInterestBean equalMonthlyPrincipal(LoanInterestBean bean) {
        bean = new LoanInterestBean(bean);
        int totalLendMonth = bean.getLendMonth();
        BigDecimal monthRate = bean.getLendMonthRate();
        BigDecimal interestTotalAmount = new BigDecimal(totalLendMonth + 1).multiply(bean.getLoanTotalAmount())
                .multiply(monthRate).divide(new BigDecimal(2), InterestUtils.SCALE, RoundingMode.HALF_UP);
        bean.setInterestTotalAmount(interestTotalAmount);
        bean.setLoanTotalPrincipalAndInterest(interestTotalAmount.add(bean.getLoanTotalAmount()));
        bean.setPeriodNominallyYearRate(bean.getInterestTotalAmount().divide(bean.getLoanTotalAmount().multiply(new BigDecimal(bean.getLendYear())), InterestUtils.SCALE, RoundingMode.HALF_UP));

        // 每月还款本金
        BigDecimal monthlyPrincipal = bean.getLoanTotalAmount().divide(new BigDecimal(totalLendMonth), InterestUtils.SCALE, RoundingMode.HALF_UP);
        bean.setMonthRepayments(calculatePrincipalMonthLend(bean));
        bean.setMonthDecrease(new BigDecimal(LoanInterestUtils.decimalFormat(monthlyPrincipal.multiply(monthRate))));
        return bean;
    }

    /**
     * 等额本息.
     *
     * @param bean
     * @return
     */
    private static LoanInterestBean equalMonthlyAmount(LoanInterestBean bean) {
        bean = new LoanInterestBean(bean);
        int totalLendMonth = bean.getLendMonth();
        BigDecimal monthRate = bean.getLendMonthRate();
        BigDecimal pow = (new BigDecimal(1).add(monthRate)).pow(totalLendMonth, MathContext.DECIMAL128);
        BigDecimal divisor = pow.subtract(new BigDecimal(1));
        BigDecimal loanTotalPrincipalAndInterest = (new BigDecimal(totalLendMonth).multiply(bean.getLoanTotalAmount())
                .multiply(monthRate.multiply(pow)))
                .divide(divisor, InterestUtils.SCALE, RoundingMode.HALF_UP);
        bean.setLoanTotalPrincipalAndInterest(loanTotalPrincipalAndInterest);
        bean.setInterestTotalAmount(loanTotalPrincipalAndInterest.subtract(bean.getLoanTotalAmount()));
        bean.setPeriodNominallyYearRate(bean.getInterestTotalAmount().divide(bean.getLoanTotalAmount().multiply(new BigDecimal(bean.getLendYear())), InterestUtils.SCALE, RoundingMode.HALF_UP));
        bean.setMonthRepayments(calculateInterestMonthLend(bean));
        return bean;
    }

    public static String decimalFormat(BigDecimal decimal) {
        return decimalFormat(decimal, "0.00");
    }

    /**
     * BigDecimal format.
     *
     * @param decimal
     * @param formatter
     * @return
     */
    public static String decimalFormat(BigDecimal decimal, String formatter) {
        DecimalFormat format = new DecimalFormat(formatter);
        if (decimal != null) {
            return format.format(decimal);
        }
        return null;
    }
}
