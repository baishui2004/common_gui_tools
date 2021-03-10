package bs.tool.commongui.utils.interest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class LoanInterestBean {

    /**
     * 贷款总额.
     */
    private BigDecimal loanTotalAmount;
    /**
     * 贷款利率.
     */
    private BigDecimal lendRate;
    /**
     * 贷款月利率.
     */
    private BigDecimal lendMonthRate;
    /**
     * 以分期方式计算的名义年化利率.
     */
    private BigDecimal periodNominallyYearRate;
    /**
     * 贷款年限.
     */
    private int lendYear;
    /**
     * 贷款月数.
     */
    private int lendMonth;
    /**
     * 计息方式.
     */
    private LendType lendType;

    /**
     * 本息合计.
     */
    private BigDecimal loanTotalPrincipalAndInterest;
    /**
     * 利息合计.
     */
    private BigDecimal interestTotalAmount;
    /**
     * 每月还款.
     */
    private List<MonthlyLendAmount> monthRepayments;
    /**
     * 每月还款递减金额.
     */
    private BigDecimal monthDecrease = new BigDecimal(0);

    public LoanInterestBean(int loanTotal, String rate, int year, LendType type) {
        loanTotalAmount = new BigDecimal(loanTotal);
        lendRate = new BigDecimal(rate);
        lendMonthRate = lendRate.divide(new BigDecimal(12 * 100), InterestUtils.SCALE, RoundingMode.HALF_UP);
        lendYear = year;
        lendMonth = year * 12;
        lendType = type;
    }

    public LoanInterestBean(LoanInterestBean bean) {
        loanTotalAmount = bean.getLoanTotalAmount();
        lendRate = bean.getLendRate();
        lendMonthRate = bean.getLendMonthRate();
        lendYear = bean.getLendYear();
        lendMonth = bean.getLendMonth();
        lendType = bean.getLendType();
    }

    public BigDecimal getLoanTotalAmount() {
        return loanTotalAmount;
    }

    public void setLoanTotalAmount(BigDecimal loanTotalAmount) {
        this.loanTotalAmount = loanTotalAmount;
    }

    public BigDecimal getLendRate() {
        return lendRate;
    }

    public void setLendRate(BigDecimal lendRate) {
        this.lendRate = lendRate;
    }

    public BigDecimal getLendMonthRate() {
        return lendMonthRate;
    }

    public void setLendMonthRate(BigDecimal lendMonthRate) {
        this.lendMonthRate = lendMonthRate;
    }

    public BigDecimal getPeriodNominallyYearRate() {
        return periodNominallyYearRate;
    }

    public void setPeriodNominallyYearRate(BigDecimal periodNominallyYearRate) {
        this.periodNominallyYearRate = periodNominallyYearRate;
    }

    public int getLendYear() {
        return lendYear;
    }

    public void setLendYear(int lendYear) {
        this.lendYear = lendYear;
    }

    public int getLendMonth() {
        return lendMonth;
    }

    public void setLendMonth(int lendMonth) {
        this.lendMonth = lendMonth;
    }

    public LendType getLendType() {
        return lendType;
    }

    public void setLendType(LendType lendType) {
        this.lendType = lendType;
    }

    public BigDecimal getLoanTotalPrincipalAndInterest() {
        return loanTotalPrincipalAndInterest;
    }

    public void setLoanTotalPrincipalAndInterest(BigDecimal loanTotalPrincipalAndInterest) {
        this.loanTotalPrincipalAndInterest = loanTotalPrincipalAndInterest;
    }

    public BigDecimal getInterestTotalAmount() {
        return interestTotalAmount;
    }

    public void setInterestTotalAmount(BigDecimal interestTotalAmount) {
        this.interestTotalAmount = interestTotalAmount;
    }

    public List<MonthlyLendAmount> getMonthRepayments() {
        return monthRepayments;
    }

    public void setMonthRepayments(List<MonthlyLendAmount> monthRepayments) {
        this.monthRepayments = monthRepayments;
    }

    public BigDecimal getMonthDecrease() {
        return monthDecrease;
    }

    public void setMonthDecrease(BigDecimal monthDecrease) {
        this.monthDecrease = monthDecrease;
    }

    @Override
    public String toString() {
        return "LoanInterestBean{" +
                "loanTotalAmount=" + loanTotalAmount +
                ", lendRate=" + lendRate +
                ", lendMonthRate=" + LoanInterestUtils.decimalFormat(lendMonthRate, "0.00000") +
                ", lendYear=" + lendYear +
                ", lendMonth=" + lendMonth +
                ", lendType=" + lendType +
                ", loanTotalPrincipalAndInterest=" + LoanInterestUtils.decimalFormat(loanTotalPrincipalAndInterest) +
                ", interestTotalAmount=" + LoanInterestUtils.decimalFormat(interestTotalAmount) +
                ", monthDecrease=" + monthDecrease +
                ", monthRepayments=" + monthRepayments.toString() +
                '}';
    }

    /**
     * 等额本金与等额本息对比.
     */
    public static class ComparePrincipalInterest {

        /**
         * 等额本金还款情况.
         */
        private LoanInterestBean principalBean;
        /**
         * 等额本息还款情况.
         */
        private LoanInterestBean interestBean;
        /**
         * 节省利息.
         */
        private BigDecimal savedInterest;
        /**
         * 首次等额本金还款额小于等额本息还款额的月数.
         */
        private int firstLessThanMonth;
        /**
         * 截止firstLessThanMonth等额本金相比等额本息累计多还金额.
         */
        private BigDecimal moreLentBefore;
        /**
         * 自firstLessThanMonth后等额本金相比等额本息累计少还金额.
         */
        private BigDecimal lessLentBefore;
        /**
         * 复利年华利率.
         */
        private String compoundRate = "0.00";
        /**
         * 截止firstLessThanMonth等额本金相比等额本息累计多还金额的复利总额.
         */
        private BigDecimal firstLessThanMonthCompoundInterest;
        /**
         * 截止firstLessThanMonth等额本金相比等额本息累计多还金额的复利总额再次累计余下的还款日期里的复利总额.
         */
        private BigDecimal firstLessThanMonthCompoundTwiceInterest;
        /**
         * 自firstLessThanMonth等额本金相比等额本息累计少还金额的复利总额.
         */
        private BigDecimal principalFirstLessThanMonthCompoundInterest;

        public ComparePrincipalInterest(LoanInterestBean principalBean, LoanInterestBean interestBean, String compoundRate) {
            this.principalBean = principalBean;
            this.interestBean = interestBean;
            this.compoundRate = compoundRate;
        }

        public ComparePrincipalInterest(ComparePrincipalInterest compare) {
            principalBean = compare.getPrincipalBean();
            interestBean = compare.getInterestBean();
            compoundRate = compare.getCompoundRate();
        }

        public LoanInterestBean getPrincipalBean() {
            return principalBean;
        }

        public void setPrincipalBean(LoanInterestBean principalBean) {
            this.principalBean = principalBean;
        }

        public LoanInterestBean getInterestBean() {
            return interestBean;
        }

        public void setInterestBean(LoanInterestBean interestBean) {
            this.interestBean = interestBean;
        }

        public BigDecimal getSavedInterest() {
            return savedInterest;
        }

        public void setSavedInterest(BigDecimal savedInterest) {
            this.savedInterest = savedInterest;
        }

        public int getFirstLessThanMonth() {
            return firstLessThanMonth;
        }

        public void setFirstLessThanMonth(int firstLessThanMonth) {
            this.firstLessThanMonth = firstLessThanMonth;
        }

        public BigDecimal getMoreLentBefore() {
            return moreLentBefore;
        }

        public void setMoreLentBefore(BigDecimal moreLentBefore) {
            this.moreLentBefore = moreLentBefore;
        }

        public BigDecimal getLessLentBefore() {
            return lessLentBefore;
        }

        public void setLessLentBefore(BigDecimal lessLentBefore) {
            this.lessLentBefore = lessLentBefore;
        }

        public String getCompoundRate() {
            return compoundRate;
        }

        public void setCompoundRate(String compoundRate) {
            this.compoundRate = compoundRate;
        }

        public BigDecimal getFirstLessThanMonthCompoundInterest() {
            return firstLessThanMonthCompoundInterest;
        }

        public void setFirstLessThanMonthCompoundInterest(BigDecimal firstLessThanMonthCompoundInterest) {
            this.firstLessThanMonthCompoundInterest = firstLessThanMonthCompoundInterest;
        }

        public BigDecimal getFirstLessThanMonthCompoundTwiceInterest() {
            return firstLessThanMonthCompoundTwiceInterest;
        }

        public void setFirstLessThanMonthCompoundTwiceInterest(BigDecimal firstLessThanMonthCompoundTwiceInterest) {
            this.firstLessThanMonthCompoundTwiceInterest = firstLessThanMonthCompoundTwiceInterest;
        }

        public BigDecimal getPrincipalFirstLessThanMonthCompoundInterest() {
            return principalFirstLessThanMonthCompoundInterest;
        }

        public void setPrincipalFirstLessThanMonthCompoundInterest(BigDecimal principalFirstLessThanMonthCompoundInterest) {
            this.principalFirstLessThanMonthCompoundInterest = principalFirstLessThanMonthCompoundInterest;
        }
    }

    /**
     * 提前还款.
     */
    public static class LendAheadOfSchedule {
        /**
         * 提前前的还款Bean.
         */
        private LoanInterestBean originBean;
        /**
         * 提前还款年数.
         */
        private int year;
        /**
         * 缩短年数.
         */
        private int yearReduce;
        /**
         * 提前还款额.
         */
        private int amount;
        /**
         * 计息方式.
         */
        private LendType lendType;
        /**
         * 贷款利率.
         */
        private String rate;
        /**
         * 已还本金.
         */
        private BigDecimal lentPrincipal;
        /**
         * 已还利息.
         */
        private BigDecimal lentInterest;
        /**
         * 节省利息.
         */
        private BigDecimal savedInterest;
        /**
         * 提前后的还款Bean.
         */
        private LoanInterestBean newBean;

        public LendAheadOfSchedule(LoanInterestBean originBean, int year, int amount, LendType lendType, String rate, int yearReduce) {
            this.originBean = originBean;
            this.year = year;
            this.amount = amount;
            this.lendType = lendType;
            this.rate = rate;
            this.yearReduce = yearReduce;
        }

        public LendAheadOfSchedule(LendAheadOfSchedule ahead) {
            originBean = ahead.getOriginBean();
            year = ahead.getYear();
            amount = ahead.getAmount();
            lendType = ahead.getLendType();
            rate = ahead.getRate();
            yearReduce = ahead.getYearReduce();
        }

        public LoanInterestBean getOriginBean() {
            return originBean;
        }

        public void setOriginBean(LoanInterestBean originBean) {
            this.originBean = originBean;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getYearReduce() {
            return yearReduce;
        }

        public void setYearReduce(int yearReduce) {
            this.yearReduce = yearReduce;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public LendType getLendType() {
            return lendType;
        }

        public void setLendType(LendType lendType) {
            this.lendType = lendType;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public BigDecimal getLentPrincipal() {
            return lentPrincipal;
        }

        public void setLentPrincipal(BigDecimal lentPrincipal) {
            this.lentPrincipal = lentPrincipal;
        }

        public BigDecimal getLentInterest() {
            return lentInterest;
        }

        public void setLentInterest(BigDecimal lentInterest) {
            this.lentInterest = lentInterest;
        }

        public BigDecimal getSavedInterest() {
            return savedInterest;
        }

        public void setSavedInterest(BigDecimal savedInterest) {
            this.savedInterest = savedInterest;
        }

        public LoanInterestBean getNewBean() {
            return newBean;
        }

        public void setNewBean(LoanInterestBean newBean) {
            this.newBean = newBean;
        }
    }

    public static class MonthlyLendAmount {
        /**
         * 期数.
         */
        private int month;
        /**
         * 金额.
         */
        private BigDecimal amount;
        /**
         * 本金.
         */
        private BigDecimal principal;
        /**
         * 利息.
         */
        private BigDecimal interest;
        /**
         * 本金占比.
         */
        private double principalProportion;
        /**
         * 利息占比.
         */
        private double interestProportion;
        /**
         * 已还本金.
         */
        private BigDecimal principalTotal;
        /**
         * 已还利息.
         */
        private BigDecimal interestTotal;
        /**
         * 已还本金占比总本金.
         */
        private double principalProportionTotal;
        /**
         * 已还利息占比总利息.
         */
        private double interestProportionTotal;

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getPrincipal() {
            return principal;
        }

        public void setPrincipal(BigDecimal principal) {
            this.principal = principal;
        }

        public BigDecimal getInterest() {
            return interest;
        }

        public void setInterest(BigDecimal interest) {
            this.interest = interest;
        }

        public double getPrincipalProportion() {
            return principalProportion;
        }

        public void setPrincipalProportion(double principalProportion) {
            this.principalProportion = principalProportion;
        }

        public double getInterestProportion() {
            return interestProportion;
        }

        public void setInterestProportion(double interestProportion) {
            this.interestProportion = interestProportion;
        }

        public BigDecimal getPrincipalTotal() {
            return principalTotal;
        }

        public void setPrincipalTotal(BigDecimal principalTotal) {
            this.principalTotal = principalTotal;
        }

        public BigDecimal getInterestTotal() {
            return interestTotal;
        }

        public void setInterestTotal(BigDecimal interestTotal) {
            this.interestTotal = interestTotal;
        }

        public double getPrincipalProportionTotal() {
            return principalProportionTotal;
        }

        public void setPrincipalProportionTotal(double principalProportionTotal) {
            this.principalProportionTotal = principalProportionTotal;
        }

        public double getInterestProportionTotal() {
            return interestProportionTotal;
        }

        public void setInterestProportionTotal(double interestProportionTotal) {
            this.interestProportionTotal = interestProportionTotal;
        }

        public MonthlyLendAmount() {
        }

        public MonthlyLendAmount(int month, BigDecimal principal, BigDecimal interest, BigDecimal principalTotal, BigDecimal interestTotal, LoanInterestBean bean) {
            this.month = month;
            this.amount = principal.add(interest);
            this.principal = principal;
            this.interest = interest;
            this.principalProportion = Double.parseDouble(LoanInterestUtils.decimalFormat(principal.divide(this.amount, InterestUtils.SCALE, RoundingMode.HALF_UP), "0.00000"));
            this.interestProportion = Double.parseDouble(LoanInterestUtils.decimalFormat(interest.divide(this.amount, InterestUtils.SCALE, RoundingMode.HALF_UP), "0.00000"));
            this.principalTotal = principalTotal;
            this.interestTotal = interestTotal;
            this.principalProportionTotal = Double.parseDouble(LoanInterestUtils.decimalFormat(principalTotal.divide(bean.getLoanTotalAmount(), InterestUtils.SCALE, RoundingMode.HALF_UP), "0.00000"));
            this.interestProportionTotal = Double.parseDouble(LoanInterestUtils.decimalFormat(interestTotal.divide(bean.getInterestTotalAmount(), InterestUtils.SCALE, RoundingMode.HALF_UP), "0.00000"));
        }

        public String toString2() {
            return "MonthlyLendAmount{" +
                    "month=" + month +
                    ", amount=" + LoanInterestUtils.decimalFormat(amount) +
                    ", principal=" + LoanInterestUtils.decimalFormat(principal) +
                    ", interest=" + LoanInterestUtils.decimalFormat(interest) +
                    ", principalProportion=" + principalProportion +
                    ", interestProportion=" + interestProportion +
                    ", principalTotal=" + LoanInterestUtils.decimalFormat(principalTotal) +
                    ", interestTotal=" + LoanInterestUtils.decimalFormat(interestTotal) +
                    ", principalProportionTotal=" + principalProportionTotal +
                    ", interestProportionTotal=" + interestProportionTotal +
                    '}';
        }

        @Override
        public String toString() {
            return month +
                    "\t" + LoanInterestUtils.decimalFormat(amount) +
                    "\t" + LoanInterestUtils.decimalFormat(principal) +
                    "\t" + LoanInterestUtils.decimalFormat(interest) +
                    "\t" + principalProportion +
                    "\t" + interestProportion +
                    "\t" + LoanInterestUtils.decimalFormat(principalTotal) +
                    "\t" + LoanInterestUtils.decimalFormat(interestTotal) +
                    "\t" + principalProportionTotal +
                    "\t" + interestProportionTotal;
        }
    }
}