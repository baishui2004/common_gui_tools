package bs.tool.commongui.plugins.more;

import bs.tool.commongui.AbstractGuiJPanel;
import bs.tool.commongui.GuiUtils;
import bs.tool.commongui.utils.FileUtils;
import bs.tool.commongui.utils.SearchFileNameParams;
import bs.tool.commongui.utils.SimpleMouseListener;
import bs.util.io.PropertiesUtils;
import org.apache.commons.io.LineIterator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

/**
 * 文本文件切分.
 */
public class TextFileSplit extends AbstractGuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 切分文本/文件夹路径表单.
     */
    private JTextField splitPathTextField = new JTextField();
    /**
     * 切分文本/文件夹路径选择.
     */
    private JFileChooser splitPathChooser = new JFileChooser();

    /**
     * 切分文件结果目录.
     */
    private String splitResultDirectory;

    /**
     * 切分按钮.
     */
    private JButton splitButton;

    /**
     * 按行数或大小切分表单.
     */
    private JTextField splitValueField = new JTextField("10", 5);

    /**
     * 切分类型.
     */
    private String[] splitTypes = new String[]{GuiUtils.SPLIT_TYPE_SIZE, GuiUtils.SPLIT_TYPE_LINE};

    /**
     * 当期切分类型.
     */
    private String currentSplitType = splitTypes[0];

    /**
     * 文件名包含字符表单.
     */
    private JTextField fileNameContainsTextField;
    /**
     * 文件名不包含字符表单.
     */
    private JTextField fileNameNotContainsTextField;
    /**
     * 文件名是否支持正则.
     */
    private boolean fileNameSupportRegex = true;

    /**
     * 源文件编码 - 程序自动检测.
     */
    private String sourceEncodingAuto = "程序自动检测";
    /**
     * 源文件编码.
     */
    private JTextField sourceEncodingField = new JTextField(sourceEncodingAuto, 8);
    /**
     * 切分结果文件编码.
     */
    private JTextField targetEncodingField = new JTextField(GuiUtils.CHARSET_UTF_8, 6);

    /**
     * 行分隔符.
     */
    private String[] lineSeparators = new String[]{"\\n", "\\r\\n", "\\r"};
    private Map<String, String> lineSeparatorsMap = new HashMap<String, String>() {{
        put("\\n", "\n");
        put("\\r\\n", "\r\n");
        put("\\r", "\r");
    }};
    /**
     * 当期行分隔符.
     */
    private String currentLineSeparator = lineSeparatorsMap.get(lineSeparators[0]);

    /**
     * 切分结果文本域.
     */
    private JTextArea resultTextArea = createJTextArea(GuiUtils.font14_un);

    public TextFileSplit() {

        // 主面板：边界布局，分North、Center两部分，North用于放置输入及条件控件，Center是放置切分结果输出
        setLayout(new BorderLayout());

        // 输入及条件Panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 1));

        // 切分文本/文件夹表单、切分按钮Panel
        JPanel fileChooAndSplitPanel = new JPanel(new BorderLayout());
        // 切分文本/文件夹表单
        addJLabel(fileChooAndSplitPanel, "  切分文本/文件夹: ", GuiUtils.font14_cn, BorderLayout.WEST);
        JPanel fileChooPanel = new JPanel(new BorderLayout());
        fileChooPanel.add(new JPanel(), BorderLayout.NORTH);
        addJTextField(fileChooPanel, splitPathTextField, GuiUtils.font14_un, BorderLayout.CENTER);
        fileChooPanel.add(new JPanel(), BorderLayout.SOUTH);
        fileChooAndSplitPanel.add(fileChooPanel, BorderLayout.CENTER);

        JPanel buttonFlowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJButton(buttonFlowPanel, "浏览", "", GuiUtils.font12_cn,
                buttonBrowseListener(splitPathChooser, splitPathTextField));
        // 切分按钮
        splitButton = createJButton("切分", "", GuiUtils.font14b_cn);
        splitButton.addMouseListener(new SimpleMouseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                String path = splitPathTextField.getText().trim();
                File file = new File(path);
                if (!file.exists()) {
                    showMessage("切分文本/文件夹不存在！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                splitResultDirectory = file.getAbsolutePath() + " - split result";
                File splitResultDir = new File(splitResultDirectory);
                if (splitResultDir.exists()) {
                    try {
                        org.apache.commons.io.FileUtils.deleteDirectory(splitResultDir);
                    } catch (IOException e) {
                        showMessage("删除原切分结果文件夹失败！", "警告", JOptionPane.WARNING_MESSAGE);
                    }
                }
                splitResultDir.mkdir();
                Map<String, Object> paramsMap = new HashMap<String, Object>();
                paramsMap.put("fileNameContainsText", fileNameContainsTextField.getText().trim());
                paramsMap.put("fileNameNotContainsText", fileNameNotContainsTextField.getText().trim());
                paramsMap.put("fileNameSupportRegex", fileNameSupportRegex);
                splitButton.setEnabled(false);

                resultTextArea.setText("");
                try {
                    String sourceEncoding = sourceEncodingField.getText().trim();
                    String targetEncoding = targetEncodingField.getText().trim();
                    if (sourceEncoding.length() == 0) {
                        showMessage("源文件编码不可为空！", "警告", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (targetEncoding.length() == 0) {
                        showMessage("切分结果文件编码不可为空！", "警告", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    int splitLength = Integer.parseInt(splitValueField.getText().trim());
                    if (!file.isDirectory()) {
                        appendSplitResult(resultTextArea, file, splitLength, sourceEncoding, targetEncoding);
                    } else {
                        List<File> files = new ArrayList<File>();
                        FileUtils.loopDirectory(file, files, new SearchFileNameParams(paramsMap));
                        for (File fFile : files) {
                            appendSplitResult(resultTextArea, fFile, splitLength, sourceEncoding, targetEncoding);
                        }
                        resultTextArea.append("\nCount files: " + files.size());
                    }
                } catch (NumberFormatException e) {
                    showMessage("切分大小必须是正整数！", "警告", JOptionPane.WARNING_MESSAGE);
                } catch (IOException e) {
                    showExceptionMessage(e);
                }
                splitButton.setEnabled(true);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                resultTextArea.setText("");
            }
        });
        buttonFlowPanel.add(splitButton);
        // 切分路径选择控件，可选择文件/文件夹
        splitPathChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooAndSplitPanel.add(buttonFlowPanel, BorderLayout.EAST);
        inputPanel.add(fileChooAndSplitPanel);

        // 高级条件Panel
        JPanel advancePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        // 切分大小
        addJLabel(advancePanel, " ", GuiUtils.font12_cn);
        addJLabel(advancePanel, "切分条件: ", GuiUtils.font14_cn);
        addJTextField(advancePanel, splitValueField, GuiUtils.font14_cn);
        // 切分型下拉框
        addJComboBox(advancePanel, splitTypes, GuiUtils.font13, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                currentSplitType = ((JComboBox) event.getSource()).getSelectedItem().toString();
            }
        });

        // 文件名包含(不包含)字符
        String confPropsFile = "conf/TextFileSplit/searchFiletypes.properties";
        String containsFiletypes = "";
        String notContainsFiletypes = "";
        try {
            Properties confProperties = PropertiesUtils.getProperties(GuiUtils.getActualPath(confPropsFile));
            containsFiletypes = confProperties.getProperty("ContainsFiletypes").trim();
            notContainsFiletypes = confProperties.getProperty("NotContainsFiletypes").trim();
        } catch (IOException e) {
            logLoadPropertiesException(confPropsFile, e);
        }
        addJLabel(advancePanel, " 文件名包含字符: ", GuiUtils.font14_cn);
        String fileNameContains = containsFiletypes.length() > 0 ? "\\.(" + containsFiletypes.replace(",", "|") + ")$"
                : "";
        fileNameContainsTextField = new JTextField(fileNameContains, 15);
        addJTextField(advancePanel, fileNameContainsTextField, GuiUtils.font14_un);
        addJLabel(advancePanel, "  文件名不包含字符: ", GuiUtils.font14_cn);
        String fileNameNotContains = notContainsFiletypes.length() > 0 ? "\\.("
                + notContainsFiletypes.replace(",", "|") + ")$" : "";
        fileNameNotContainsTextField = new JTextField(fileNameNotContains, 15);
        addJTextField(advancePanel, fileNameNotContainsTextField, GuiUtils.font14_un);
        // 是否支持正则
        addJCheckBox(advancePanel, "支持正则", true, GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                fileNameSupportRegex = ((JCheckBox) event.getSource()).isSelected();
            }
        });
        inputPanel.add(advancePanel);

        // 高级条件面板
        JPanel advanceConditionPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJLabel(advanceConditionPanel, " ", GuiUtils.font12_cn);
        addJLabel(advanceConditionPanel, "源文件编码:", GuiUtils.font14_cn);
        advanceConditionPanel.add(sourceEncodingField);
        addJLabel(advanceConditionPanel, " 切分结果文件编码:", GuiUtils.font14_cn);
        advanceConditionPanel.add(targetEncodingField);
        addJLabel(advanceConditionPanel, " 行分隔符:", GuiUtils.font14_cn);
        // 行分隔符下拉框
        addJComboBox(advanceConditionPanel, lineSeparators, GuiUtils.font13, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                currentLineSeparator = lineSeparatorsMap.get(((JComboBox) event.getSource()).getSelectedItem().toString());
            }
        });
        inputPanel.add(advanceConditionPanel);

        add(inputPanel, BorderLayout.NORTH);

        // 切分结果输出
        JPanel splitLogPanel = new JPanel(new BorderLayout());
        splitLogPanel.add(new JScrollPane(resultTextArea));
        add(splitLogPanel, BorderLayout.CENTER);
    }

    /**
     * append切分文本编码结果.
     */
    private void appendSplitResult(JTextArea resultTextArea, File file, int splitLength, String sourceEncoding, String targetEncoding) throws IOException {
        resultTextArea.append(file.getAbsolutePath() + "\n    Result: " + splitFileResult(file, splitLength, sourceEncoding, targetEncoding)
                + "\n");
    }

    /**
     * 切分文本编码.
     */
    private String splitFileResult(File file, int splitLength, String sourceEncoding, String targetEncoding) throws IOException {
        StringBuilder rsb = new StringBuilder();
        String charset = sourceEncoding;
        if (charset.equals(sourceEncodingAuto)) {
            charset = JUniversalChardet.detectFileCharset(file, 4096);
        }
        LineIterator lineIterator = org.apache.commons.io.FileUtils.lineIterator(file, charset);
        String fileName = file.getName();
        int lastIndex = fileName.lastIndexOf(".");
        lastIndex = (lastIndex == -1 ? fileName.length() : lastIndex);
        String fileSimpleName = fileName.substring(0, lastIndex);
        String fileType = fileName.substring(lastIndex + 1, fileName.length());
        String rs;
        int fileNum = 1;
        while ((rs = loopSplitFile(lineIterator, splitLength, fileSimpleName + "_" + (fileNum++) + (fileType.length() != 0 ? "." : "") + fileType, targetEncoding)) != null) {
            if (rsb.length() != 0) {
                rsb.append("            ");
            }
            rsb.append(rs).append("\n");
        }
        lineIterator.close();
        return rsb.toString();
    }

    private String loopSplitFile(LineIterator lineIterator, int splitLength, String splitFileName, String targetEncoding) throws IOException {
        File splitFile = new File(splitResultDirectory + "/" + splitFileName);
        int lineCnt = 0;
        List<String> lines;
        List<String> writeLines = new ArrayList<String>();
        while ((lines = iteratorLine(lineIterator, 100, splitLength, lineCnt)).size() != 0) {
            lineCnt += lines.size();
            writeLines.addAll(lines);
            if (currentSplitType.equals(GuiUtils.SPLIT_TYPE_SIZE) && splitFile.getTotalSpace() >= splitLength * 1024 * 1024) {
                break;
            }
        }
        if (writeLines.size() != 0) {
            org.apache.commons.io.FileUtils.writeLines(splitFile, targetEncoding, writeLines, currentLineSeparator);
            return splitFile.getAbsolutePath();
        } else {
            return null;
        }
    }

    private List<String> iteratorLine(LineIterator lineIterator, int line, int splitLength, int lineCnt) {
        List<String> lines = new ArrayList<String>();
        while (lineIterator.hasNext() && (line-- > 0)) {
            if (currentSplitType.equals(GuiUtils.SPLIT_TYPE_LINE) && lineCnt + lines.size() >= splitLength) {
                break;
            } else {
                lines.add(lineIterator.nextLine());
            }
        }
        return lines;
    }

}
