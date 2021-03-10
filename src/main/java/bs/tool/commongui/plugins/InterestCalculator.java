package bs.tool.commongui.plugins;

import bs.tool.commongui.AbstractGuiJPanel;
import bs.tool.commongui.GuiUtils;
import bs.tool.commongui.utils.ExcelUtils;
import bs.tool.commongui.utils.NumberUtils;
import bs.tool.commongui.utils.SimpleMouseListener;
import bs.tool.commongui.utils.interest.InterestUtils;
import bs.tool.commongui.utils.interest.LendType;
import bs.tool.commongui.utils.interest.LoanInterestBean;
import bs.tool.commongui.utils.interest.LoanInterestUtils;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static bs.tool.commongui.utils.interest.LoanInterestBean.*;

/**
 * 利息计算器.
 */
public class InterestCalculator extends AbstractGuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 贷款金额表单.
     */
    private JTextField loanAmountField = new JTextField("250", 5);

    /**
     * 贷款年限表单.
     */
    private JTextField loanYearField = new JTextField("30", 5);

    /**
     * 贷款利率表单.
     */
    private JTextField loanRateField = new JTextField("5.05", 5);

    /**
     * 计息方式.
     */
    private String[] lendTypes = new String[]{"", LendType.Interest.getDesc(), LendType.Principal.getDesc()};

    /**
     * 当前计息方式.
     */
    private String curLendType = lendTypes[0];
    /**
     * 左边结果文本域.
     */
    private JTextArea leftResultArea = createJTextArea(GuiUtils.font12_cn);
    /**
     * 右边结果文本域.
     */
    private JTextArea rightResultArea = createJTextArea(GuiUtils.font12_cn);

    /**
     * 贷款对比利率表单.
     */
    private JTextField compareLoanRateField = new JTextField("5.10", 5);

    /**
     * 贷款存款/理财利率表单.
     */
    private JTextField depositRateField = new JTextField("4.00", 5);

    /**
     * 提前还款金额表单.
     */
    private JTextField aheadAmountField = new JTextField("50", 5);

    /**
     * 提前还款年限表单.
     */
    private JTextField aheadYearField = new JTextField("5", 5);

    /**
     * 提前贷款后利率表单.
     */
    private JTextField aheadRateField = new JTextField("5.10", 5);

    /**
     * 提前还款后缩短年限表单.
     */
    private JTextField aheadYearReduceField = new JTextField("5", 5);

    /**
     * 分期借款金额表单.
     */
    private JTextField periodAmountField = new JTextField("10000", 5);
    /**
     * 分期期数表单.
     */
    private JTextField periodsField = new JTextField("12", 5);
    /**
     * 分期费率表单.
     */
    private JTextField periodRateField = new JTextField("0.75", 5);

    public InterestCalculator() {

        // 主面板：边界布局，North, Center两部分
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());

        // 基础表单、计算按钮Panel
        JPanel topCenterPanel = new JPanel(new GridLayout(4, 1));
        topPanel.add(topCenterPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JPanel calculateBaseRawPanel = new JPanel(new BorderLayout());
        JPanel calculateBasePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        calculateBaseRawPanel.add(calculateBasePanel, BorderLayout.CENTER);
        addJLabel(calculateBasePanel, "[基础信息]", GuiUtils.font14b_cn);
        addJLabel(calculateBasePanel, "贷款金额:", GuiUtils.font13_cn);
        addJTextField(calculateBasePanel, loanAmountField, GuiUtils.font13_cn);
        addJLabel(calculateBasePanel, "万元  贷款年限:", GuiUtils.font13_cn);
        addJTextField(calculateBasePanel, loanYearField, GuiUtils.font13_cn);
        addJLabel(calculateBasePanel, "年  贷款利率:", GuiUtils.font13_cn);
        addJTextField(calculateBasePanel, loanRateField, GuiUtils.font13_cn);
        addJLabel(calculateBasePanel, "%", GuiUtils.font13_cn);
        /* 目前不使用，暂隐藏
        addJLabel(calculateBasePanel, "%  计息方式:", GuiUtils.font13_cn);
        // 计息方式
        addJComboBox(calculateBasePanel, lendTypes, GuiUtils.font13_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                curLendType = ((JComboBox) event.getSource()).getSelectedItem().toString();
            }
        });
        */
        // 基础计算按钮
        JPanel calculateBaseButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        calculateBaseRawPanel.add(calculateBaseButtonPanel, BorderLayout.EAST);
        JButton calculateBaseButton = createJButton("计算", "", GuiUtils.font13_cn);
        calculateBaseButtonPanel.add(calculateBaseButton);
        calculateBaseButton.addMouseListener(new SimpleMouseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                calculate(1, null);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                leftResultArea.setText("");
                rightResultArea.setText("");
            }
        });
        topCenterPanel.add(calculateBaseRawPanel);

        // 比较信息表单、计算按钮Panel
        JPanel calculateCompareRawPanel = new JPanel(new BorderLayout());
        JPanel calculateComparePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        calculateCompareRawPanel.add(calculateComparePanel, BorderLayout.CENTER);
        addJLabel(calculateComparePanel, "[比较信息]", GuiUtils.font14b_cn);
        addJLabel(calculateComparePanel, "对比利率:", GuiUtils.font13_cn);
        addJTextField(calculateComparePanel, compareLoanRateField, GuiUtils.font13_cn);
        addJLabel(calculateComparePanel, "%    存款利率:", GuiUtils.font13_cn);
        addJTextField(calculateComparePanel, depositRateField, GuiUtils.font13_cn);
        addJLabel(calculateComparePanel, "%", GuiUtils.font13_cn);
        addJLabel(calculateComparePanel, " ", GuiUtils.font13_cn);
        // 比较计算按钮
        JPanel calculateCompareButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        calculateCompareRawPanel.add(calculateCompareButtonPanel, BorderLayout.EAST);
        JButton calculateCompareButton = createJButton("比较", "", GuiUtils.font13_cn);
        calculateCompareButtonPanel.add(calculateCompareButton);
        calculateCompareButton.addMouseListener(new SimpleMouseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                calculate(2, null);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                leftResultArea.setText("");
                rightResultArea.setText("");
            }
        });
        topCenterPanel.add(calculateCompareRawPanel);

        // 提前还款信息表单、计算按钮Panel
        JPanel calculateAheadRawPanel = new JPanel(new BorderLayout());
        JPanel calculateAheadPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        calculateAheadRawPanel.add(calculateAheadPanel, BorderLayout.CENTER);
        addJLabel(calculateAheadPanel, "[提前还款]", GuiUtils.font14b_cn);
        addJLabel(calculateAheadPanel, "还款金额:", GuiUtils.font13_cn);
        addJTextField(calculateAheadPanel, aheadAmountField, GuiUtils.font13_cn);
        addJLabel(calculateAheadPanel, "万元  还款期限:", GuiUtils.font13_cn);
        addJTextField(calculateAheadPanel, aheadYearField, GuiUtils.font13_cn);
        addJLabel(calculateAheadPanel, "年  还款利率:", GuiUtils.font13_cn);
        addJTextField(calculateAheadPanel, aheadRateField, GuiUtils.font13_cn);
        addJLabel(calculateAheadPanel, "%  缩短还款年限:", GuiUtils.font13_cn);
        addJTextField(calculateAheadPanel, aheadYearReduceField, GuiUtils.font13_cn);
        addJLabel(calculateAheadPanel, "年", GuiUtils.font13_cn);
        // 提前还款计算按钮
        JPanel calculateAheadButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        calculateAheadRawPanel.add(calculateAheadButtonPanel, BorderLayout.EAST);
        JButton calculateAheadButton = createJButton("计算", "", GuiUtils.font13_cn);
        calculateAheadButtonPanel.add(calculateAheadButton);
        calculateAheadButton.addMouseListener(new SimpleMouseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                calculate(3, null);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                leftResultArea.setText("");
                rightResultArea.setText("");
            }
        });
        topCenterPanel.add(calculateAheadRawPanel);

        // 分期还款表单、计算按钮Panel
        JPanel calculatePeriodRawPanel = new JPanel(new BorderLayout());
        JPanel calculatePeriodPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        calculatePeriodRawPanel.add(calculatePeriodPanel, BorderLayout.CENTER);
        addJLabel(calculatePeriodPanel, "[分期还款]", GuiUtils.font14b_cn);
        addJLabel(calculatePeriodPanel, "借款金额:", GuiUtils.font13_cn);
        addJTextField(calculatePeriodPanel, periodAmountField, GuiUtils.font13_cn);
        addJLabel(calculatePeriodPanel, "元    分期期限:", GuiUtils.font13_cn);
        addJTextField(calculatePeriodPanel, periodsField, GuiUtils.font13_cn);
        addJLabel(calculatePeriodPanel, "期  名义费率:", GuiUtils.font13_cn);
        addJTextField(calculatePeriodPanel, periodRateField, GuiUtils.font13_cn);
        addJLabel(calculatePeriodPanel, "%", GuiUtils.font13_cn);
        // 分期真实利率计算按钮
        JPanel calculatePeriodButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        calculatePeriodRawPanel.add(calculatePeriodButtonPanel, BorderLayout.EAST);
        JButton calculatePeriodButton = createJButton("计算", "", GuiUtils.font13_cn);
        calculatePeriodButtonPanel.add(calculatePeriodButton);
        calculatePeriodButton.addMouseListener(new SimpleMouseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                calculate(4, null);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                leftResultArea.setText("");
                rightResultArea.setText("");
            }
        });
        topCenterPanel.add(calculatePeriodRawPanel);

        topPanel.add(topCenterPanel, BorderLayout.CENTER);

        // 保存计算信息为Excel
        JPanel topEastPanel = new JPanel(new BorderLayout());
        JPanel topEastNorthPanel = new JPanel(new GridLayout(1, 1));
        addJLabel(topEastNorthPanel, " ", GuiUtils.font13_cn);
        topEastPanel.add(topEastNorthPanel, BorderLayout.NORTH);
        JButton saveExcelButton = createJButton("保存为Excel", "", GuiUtils.font13_cn);
        topEastPanel.add(saveExcelButton, BorderLayout.CENTER);
        final JFileChooser excelFileChooser = createFileChooser("保存为Excel", "xls");
        saveExcelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int option = excelFileChooser.showSaveDialog(null);
                if (option == JFileChooser.APPROVE_OPTION) {
                    try {
                        File selectedFile = excelFileChooser.getSelectedFile();
                        if (selectedFile.getName().indexOf(".") < 0) {
                            excelFileChooser.setSelectedFile(new File(selectedFile.getAbsolutePath() + ".xls"));
                        }
                        File oFile = excelFileChooser.getSelectedFile();
                        FileOutputStream os = new FileOutputStream(oFile);
                        WritableWorkbook workbook = Workbook.createWorkbook(os);
                        workbook.createSheet("概况", 0);
                        workbook.createSheet("等额本息月还款明细", 1);
                        workbook.createSheet("等额本金月还款明细", 2);
                        calculate(1, workbook);
                        calculate(2, workbook);
                        calculate(3, workbook);
                        workbook.write();
                        workbook.close();
                        os.close();
                    } catch (Exception e) {
                        showExceptionMessage(e);
                    }
                }
            }
        });
        JPanel topEastSouthPanel = new JPanel(new GridLayout(3, 1));
        addJLabel(topEastSouthPanel, " ", GuiUtils.font13_cn);
        addJLabel(topEastSouthPanel, " ", GuiUtils.font13_cn);
        topEastPanel.add(topEastSouthPanel, BorderLayout.SOUTH);

        topPanel.add(topEastPanel, BorderLayout.EAST);

        JPanel resultPanel = new JPanel(new GridLayout(1, 2));
        resultPanel.add(new JScrollPane(leftResultArea));
        resultPanel.add(new JScrollPane(rightResultArea));

        add(resultPanel, BorderLayout.CENTER);
    }

    private List<List<String>> xlsHeader = new ArrayList<List<String>>() {{
        add(new ArrayList<String>() {{
            add("期数");
            add("还款额");
            add("本金");
            add("利息");
            add("本金占比");
            add("利息占比");
            add("累计已还本金");
            add("累计已还利息");
            add("累计已还本金占比");
            add("累计已还利息占比");
        }});
    }};

    private List<String> buildList(Object... str) {
        List<String> list = new ArrayList<String>();
        for (Object s : str) {
            list.add(s == null ? "" : s.toString());
        }
        return list;
    }

    private String appendList(List<List<String>> list, List<String> header, String indent) {
        StringBuilder sb = new StringBuilder();
        for (List<String> l : list) {
            sb.append("\n").append(indent);
            int ls = l.size();
            for (int i = 0; i < ls; i++) {
                String str = l.get(i);
                if (header != null) {
                    if (i != 0) {
                        sb.append(", ");
                    }
                    sb.append(header.get(i)).append("=");
                }
                sb.append(str);
            }
        }
        return sb.length() > 0 ? sb.substring(1) : sb.toString();
    }

    private void appendExcel(List<List<String>> list, WritableSheet sheet, WritableCellFormat cellFormat) throws WriteException {
        appendExcel(list, sheet, cellFormat, 210);
    }

    private void appendExcel(List<List<String>> list, WritableSheet sheet, WritableCellFormat cellFormat, int colWidth) throws WriteException {
        int raw = sheet.getRows();
        for (List<String> l : list) {
            int col = 0;
            for (String str : l) {
                Label lab = new Label(col, raw, str, cellFormat);
                sheet.setColumnView(col, colWidth);
                sheet.addCell(lab);
                col++;
            }
            raw++;
        }
    }

    /**
     * @param type 1 基础信息 2 比较信息 3 提前还款 4 分期还款
     */
    private void calculate(int type, WritableWorkbook workbook) {
        boolean saveToExcel = workbook != null;
        try {
            int loanAmount = Integer.parseInt(loanAmountField.getText()) * 10000;
            int loanYear = Integer.parseInt(loanYearField.getText());
            String rate = loanRateField.getText();

            LoanInterestBean bean = new LoanInterestBean(loanAmount, rate, loanYear, LendType.Interest);
            LoanInterestBean iRsBean = LoanInterestUtils.calculateByLendType(bean);
            bean.setLendType(LendType.Principal);
            LoanInterestBean pRsBean = LoanInterestUtils.calculateByLendType(bean);

            // 概况sheet
            WritableSheet sheet = null;
            if (saveToExcel) {
                sheet = workbook.getSheet(0);
            }
            WritableCellFormat headerCellFormat = ExcelUtils.getHeaderCellFormat();
            WritableCellFormat bodyCellFormat = ExcelUtils.getBodyCellFormat();

            List<List<String>> lineContents = new ArrayList<List<String>>();
            if (type == 1) {
                StringBuilder leftSb = new StringBuilder();

                lineContents.add(buildList("等额本息->基本情况："));
                lineContents.add(buildList("  利息总额=" + LoanInterestUtils.decimalFormat(iRsBean.getInterestTotalAmount()) +
                        "，每月还款=" + LoanInterestUtils.decimalFormat(iRsBean.getMonthRepayments().get(0).getAmount()) +
                        "，月利率=" + NumberUtils.convertPercentageStr(iRsBean.getLendMonthRate()) +
                        "，以分期方式计算的名义年化利率=" + NumberUtils.convertPercentageStr(iRsBean.getPeriodNominallyYearRate())));
                lineContents.add(buildList(""));

                if (saveToExcel) {
                    appendExcel(lineContents, sheet, bodyCellFormat);
                } else {
                    leftSb.append(appendList(lineContents, null, ""));
                }

                leftSb.append("\n等额本息->每月还款：\n");
                lineContents = new ArrayList<List<String>>();
                for (int i = 0; i < iRsBean.getLendMonth(); i++) {
                    MonthlyLendAmount monthlyLendAmount = iRsBean.getMonthRepayments().get(i);
                    lineContents.add(buildList(i + 1,
                            LoanInterestUtils.decimalFormat(monthlyLendAmount.getAmount()),
                            LoanInterestUtils.decimalFormat(monthlyLendAmount.getPrincipal()),
                            LoanInterestUtils.decimalFormat(monthlyLendAmount.getInterest()),
                            NumberUtils.convertPercentageStr(monthlyLendAmount.getPrincipalProportion()),
                            NumberUtils.convertPercentageStr(monthlyLendAmount.getInterestProportion()),
                            LoanInterestUtils.decimalFormat(monthlyLendAmount.getPrincipalTotal()),
                            LoanInterestUtils.decimalFormat(monthlyLendAmount.getInterestTotal()),
                            NumberUtils.convertPercentageStr(monthlyLendAmount.getPrincipalProportionTotal()),
                            NumberUtils.convertPercentageStr(monthlyLendAmount.getInterestProportionTotal())));
                }

                if (saveToExcel) {
                    appendExcel(xlsHeader, workbook.getSheet(1), headerCellFormat, 18);
                    appendExcel(lineContents, workbook.getSheet(1), bodyCellFormat, 18);
                } else {
                    leftSb.append(appendList(lineContents, xlsHeader.get(0), "  "));
                    leftResultArea.setText(leftSb.toString());
                }

                StringBuilder rightSb = new StringBuilder();
                lineContents = new ArrayList<List<String>>();
                lineContents.add(buildList("等额本金->基本情况："));
                lineContents.add(buildList("  利息总额=" + LoanInterestUtils.decimalFormat(pRsBean.getInterestTotalAmount()) +
                        "，首月还款=" + LoanInterestUtils.decimalFormat(pRsBean.getMonthRepayments().get(0).getAmount()) +
                        "，末月还款=" + LoanInterestUtils.decimalFormat(pRsBean.getMonthRepayments().get(pRsBean.getLendMonth() - 1).getAmount()) +
                        "，逐月递减=" + LoanInterestUtils.decimalFormat(pRsBean.getMonthDecrease()) +
                        "，月利率=" + NumberUtils.convertPercentageStr(pRsBean.getLendMonthRate()) +
                        "，以分期方式计算的名义年化利率=" + NumberUtils.convertPercentageStr(pRsBean.getPeriodNominallyYearRate())));
                lineContents.add(buildList(""));

                if (saveToExcel) {
                    appendExcel(lineContents, sheet, bodyCellFormat);
                } else {
                    rightSb.append(appendList(lineContents, null, ""));
                }
                rightSb.append("\n等额本金->每月还款：\n");
                lineContents = new ArrayList<List<String>>();
                for (int i = 0; i < pRsBean.getLendMonth(); i++) {
                    MonthlyLendAmount monthlyLendAmount = pRsBean.getMonthRepayments().get(i);
                    lineContents.add(buildList(i + 1,
                            LoanInterestUtils.decimalFormat(monthlyLendAmount.getAmount()),
                            LoanInterestUtils.decimalFormat(monthlyLendAmount.getPrincipal()),
                            LoanInterestUtils.decimalFormat(monthlyLendAmount.getInterest()),
                            NumberUtils.convertPercentageStr(monthlyLendAmount.getPrincipalProportion()),
                            NumberUtils.convertPercentageStr(monthlyLendAmount.getInterestProportion()),
                            LoanInterestUtils.decimalFormat(monthlyLendAmount.getPrincipalTotal()),
                            LoanInterestUtils.decimalFormat(monthlyLendAmount.getInterestTotal()),
                            NumberUtils.convertPercentageStr(monthlyLendAmount.getPrincipalProportionTotal()),
                            NumberUtils.convertPercentageStr(monthlyLendAmount.getInterestProportionTotal())));
                }
                if (saveToExcel) {
                    appendExcel(xlsHeader, workbook.getSheet(2), headerCellFormat, 18);
                    appendExcel(lineContents, workbook.getSheet(2), bodyCellFormat, 18);
                } else {
                    rightSb.append(appendList(lineContents, xlsHeader.get(0), "  "));
                    rightResultArea.setText(rightSb.toString());
                }
            } else if (type == 2) {
                // 贷款利率比较
                String compareRate = compareLoanRateField.getText();
                LoanInterestBean cBean = new LoanInterestBean(loanAmount, compareRate, loanYear, LendType.Interest);
                LoanInterestBean iCRsBean = LoanInterestUtils.calculateByLendType(cBean);
                cBean.setLendType(LendType.Principal);
                LoanInterestBean pCRsBean = LoanInterestUtils.calculateByLendType(cBean);

                StringBuilder leftSb = new StringBuilder();
                lineContents.add(buildList("不同利率对比情况->等额本息："));
                lineContents.add(buildList("  利率=" + rate + "%->" + compareRate + "%" +
                        "，以分期方式计算的名义年化利率=" + NumberUtils.convertPercentageStr(iRsBean.getPeriodNominallyYearRate()) + "->" + NumberUtils.convertPercentageStr(iCRsBean.getPeriodNominallyYearRate()) +
                        "，总利息=" + LoanInterestUtils.decimalFormat(iRsBean.getInterestTotalAmount()) + "->" + LoanInterestUtils.decimalFormat(iCRsBean.getInterestTotalAmount()) +
                        "，节省利息=" + LoanInterestUtils.decimalFormat(iRsBean.getInterestTotalAmount().subtract(iCRsBean.getInterestTotalAmount())) +
                        "，每月还款额=" + LoanInterestUtils.decimalFormat(iRsBean.getMonthRepayments().get(0).getAmount()) + "->" + LoanInterestUtils.decimalFormat(iCRsBean.getMonthRepayments().get(0).getAmount())));
                lineContents.add(buildList(""));
                lineContents.add(buildList("不同利率对比情况->等额本金："));
                lineContents.add(buildList("  利率=" + rate + "%->" + compareRate + "%" +
                        "，以分期方式计算的名义年化利率=" + NumberUtils.convertPercentageStr(pRsBean.getPeriodNominallyYearRate()) + "->" + NumberUtils.convertPercentageStr(pCRsBean.getPeriodNominallyYearRate()) +
                        "，总利息=" + LoanInterestUtils.decimalFormat(pRsBean.getInterestTotalAmount()) + "->" + LoanInterestUtils.decimalFormat(pCRsBean.getInterestTotalAmount()) +
                        "，节省利息=" + LoanInterestUtils.decimalFormat(pRsBean.getInterestTotalAmount().subtract(pCRsBean.getInterestTotalAmount())) +
                        "，首月还款额=" + LoanInterestUtils.decimalFormat(pRsBean.getMonthRepayments().get(0).getAmount()) + "->" + LoanInterestUtils.decimalFormat(pCRsBean.getMonthRepayments().get(0).getAmount()) +
                        "，末月还款额=" + LoanInterestUtils.decimalFormat(pRsBean.getMonthRepayments().get(pRsBean.getLendMonth() - 1).getAmount()) + "->" + LoanInterestUtils.decimalFormat(pCRsBean.getMonthRepayments().get(pCRsBean.getLendMonth() - 1).getAmount()) +
                        "，逐月递减=" + LoanInterestUtils.decimalFormat(pRsBean.getMonthDecrease()) + "->" + LoanInterestUtils.decimalFormat(pCRsBean.getMonthDecrease())));
                lineContents.add(buildList(""));
                if (saveToExcel) {
                    appendExcel(lineContents, sheet, bodyCellFormat);
                } else {
                    leftSb.append(appendList(lineContents, null, ""));
                    leftResultArea.setText(leftSb.toString());
                }

                // 存款复利对比
                String compoundRate = depositRateField.getText();
                ComparePrincipalInterest compare = LoanInterestUtils.comparePrincipalInterest(new ComparePrincipalInterest(pRsBean, iRsBean, compoundRate));
                int firstThan = compare.getFirstLessThanMonth();

                StringBuilder rightSb = new StringBuilder();
                lineContents = new ArrayList<List<String>>();
                lineContents.add(buildList("等额本息与等额本金对比："));
                lineContents.add(buildList("  首月还款额：等额本息" + LoanInterestUtils.decimalFormat(iRsBean.getMonthRepayments().get(0).getAmount()) + "->等额本金" + LoanInterestUtils.decimalFormat(pRsBean.getMonthRepayments().get(0).getAmount()) +
                        "，末月还款额：等额本息" + LoanInterestUtils.decimalFormat(iRsBean.getMonthRepayments().get(iRsBean.getLendMonth() - 1).getAmount()) + "->等额本金" + LoanInterestUtils.decimalFormat(pRsBean.getMonthRepayments().get(pRsBean.getLendMonth() - 1).getAmount())));
                lineContents.add(buildList("  等额本息比等额本金多还利息：" + LoanInterestUtils.decimalFormat(compare.getSavedInterest()) +
                        "，月还款额前者首次大于后者的期数：" + firstThan +
                        "，截止该期后者比前者累计多还：" + LoanInterestUtils.decimalFormat(compare.getMoreLentBefore()) +
                        "，自该期后者比前者累计少还：" + LoanInterestUtils.decimalFormat(compare.getLessLentBefore()) +
                        "，以分期方式计算的名义年化利率：" + NumberUtils.convertPercentageStr(iRsBean.getPeriodNominallyYearRate()) + "->" + NumberUtils.convertPercentageStr(pRsBean.getPeriodNominallyYearRate())));
                lineContents.add(buildList(""));
                lineContents.add(buildList("复利对比："));
                lineContents.add(buildList("  以年化" + compoundRate + "%计算，等额本息比等额本金存款复利少：" + LoanInterestUtils.decimalFormat(compare.getPrincipalFirstLessThanMonthCompoundInterest()
                        .subtract(compare.getFirstLessThanMonthCompoundTwiceInterest())) +
                        "，月还款额前者首次大于后者时的前者累积少还部分钱的复利总额：" + LoanInterestUtils.decimalFormat(compare.getFirstLessThanMonthCompoundInterest()) +
                        "，随后还款日期里该部分钱的复利总额：" + LoanInterestUtils.decimalFormat(compare.getFirstLessThanMonthCompoundTwiceInterest()) +
                        "，自该期后者比前者累计少还部分钱的复利总额：" + LoanInterestUtils.decimalFormat(compare.getPrincipalFirstLessThanMonthCompoundInterest())));
                lineContents.add(buildList(""));
                if (saveToExcel) {
                    appendExcel(lineContents, sheet, bodyCellFormat);
                } else {
                    rightSb.append(appendList(lineContents, null, ""));
                    rightResultArea.setText(rightSb.toString());
                }
            } else if (type == 3) {
                // 提前还款
                int aheadAmount = Integer.parseInt(aheadAmountField.getText()) * 10000;
                int aheadYear = Integer.parseInt(aheadYearField.getText());
                int yearReduce = Integer.parseInt(aheadYearReduceField.getText());
                String aheadRate = aheadRateField.getText();

                LendAheadOfSchedule ahead = new LendAheadOfSchedule(iRsBean, aheadYear, aheadAmount, LendType.Interest, aheadRate, yearReduce);
                ahead = LoanInterestUtils.calculateLendAhead(ahead);
                StringBuilder leftSb = new StringBuilder();
                lineContents.add(buildList("等额本息->提前还款："));
                lineContents.add(buildList("  " + aheadYear + "年已还本金：" + LoanInterestUtils.decimalFormat(ahead.getLentPrincipal()) +
                        "，已还利息：" + LoanInterestUtils.decimalFormat(ahead.getLentInterest()) +
                        "，提前" + aheadYear + "年还款" + aheadAmount + "并且缩短还款" + yearReduce + "年节省利息：" + LoanInterestUtils.decimalFormat(ahead.getSavedInterest()) +
                        "，实际总利息：" + LoanInterestUtils.decimalFormat(ahead.getLentInterest().add(ahead.getNewBean().getInterestTotalAmount()))));
                lineContents.add(buildList("  提前还款后：" +
                        "利息总额=" + LoanInterestUtils.decimalFormat(ahead.getNewBean().getInterestTotalAmount()) +
                        "，每月还款=" + LoanInterestUtils.decimalFormat(ahead.getNewBean().getMonthRepayments().get(0).getAmount()) +
                        "，月利率=" + NumberUtils.convertPercentageStr(ahead.getNewBean().getLendMonthRate()) +
                        "，以分期方式计算的名义年化利率=" + NumberUtils.convertPercentageStr(ahead.getNewBean().getPeriodNominallyYearRate())));
                if (saveToExcel) {
                    appendExcel(lineContents, sheet, bodyCellFormat);
                } else {
                    leftSb.append(appendList(lineContents, null, ""));
                    leftResultArea.setText(leftSb.toString());
                }

                ahead = new LendAheadOfSchedule(pRsBean, aheadYear, aheadAmount, LendType.Principal, aheadRate, yearReduce);
                ahead = LoanInterestUtils.calculateLendAhead(ahead);
                StringBuilder rightSb = new StringBuilder();
                lineContents = new ArrayList<List<String>>();
                if (saveToExcel) {
                    lineContents.add(buildList(""));
                    appendExcel(lineContents, sheet, bodyCellFormat);
                    lineContents = new ArrayList<List<String>>();
                }
                lineContents.add(buildList("等额本金->提前还款："));
                lineContents.add(buildList("  " + aheadYear + "年已还本金：" + LoanInterestUtils.decimalFormat(ahead.getLentPrincipal()) +
                        "，已还利息：" + LoanInterestUtils.decimalFormat(ahead.getLentInterest()) +
                        "，提前" + aheadYear + "年还款" + aheadAmount + "并且缩短还款" + yearReduce + "年节省利息：" + LoanInterestUtils.decimalFormat(ahead.getSavedInterest()) +
                        "，实际总利息：" + LoanInterestUtils.decimalFormat(ahead.getLentInterest().add(ahead.getNewBean().getInterestTotalAmount()))));
                lineContents.add(buildList("  提前还款后：" +
                        "利息总额=" + LoanInterestUtils.decimalFormat(ahead.getNewBean().getInterestTotalAmount()) +
                        "，首月还款=" + LoanInterestUtils.decimalFormat(ahead.getNewBean().getMonthRepayments().get(0).getAmount()) +
                        "，末月还款=" + LoanInterestUtils.decimalFormat(ahead.getNewBean().getMonthRepayments().get(ahead.getNewBean().getLendMonth() - 1).getAmount()) +
                        "，逐月递减=" + LoanInterestUtils.decimalFormat(ahead.getNewBean().getMonthDecrease()) +
                        "，月利率=" + NumberUtils.convertPercentageStr(ahead.getNewBean().getLendMonthRate()) +
                        "，以分期方式计算的名义年化利率=" + NumberUtils.convertPercentageStr(ahead.getNewBean().getPeriodNominallyYearRate())));
                if (saveToExcel) {
                    appendExcel(lineContents, sheet, bodyCellFormat);
                } else {
                    rightSb.append(appendList(lineContents, null, ""));
                    rightResultArea.setText(rightSb.toString());
                }
            } else if (type == 4) {
                StringBuilder leftSb = new StringBuilder();
                // 分期名义利率真实利率计算
                String declaredMonthRate = periodRateField.getText();
                int periods = Integer.parseInt(periodsField.getText());
                int principal = Integer.parseInt(periodAmountField.getText());
                // 利息
                double interest = principal * Double.parseDouble(declaredMonthRate) / 100 * periods;
                lineContents.add(buildList("分期还款->基本情况："));
                lineContents.add(buildList("  借款金额=" + principal +
                        "，总利息=" + interest +
                        "，每期还款=" + NumberUtils.convertPrecision((principal + interest) / periods)));
                // 展示利率
                BigDecimal actualRate = InterestUtils.calculatePeriodActualRate(Double.toString(Double.parseDouble(declaredMonthRate) / 100), periods);
                lineContents.add(buildList(""));
                lineContents.add(buildList("分期还款->真实利率："));
                lineContents.add(buildList("  月利率=" + NumberUtils.convertPercentageStr(actualRate) +
                        "，年化利率=" + NumberUtils.convertPercentageStr(actualRate.multiply(new BigDecimal(12)))));
                if (saveToExcel) {
                    appendExcel(lineContents, sheet, bodyCellFormat);
                } else {
                    leftSb.append(appendList(lineContents, null, ""));
                    leftResultArea.setText(leftSb.toString());
                }
            }
        } catch (Exception e) {
            showExceptionMessage(e);
        }
    }

}
