package bs.tool.commongui.plugins.more;

import bs.tool.commongui.GuiJPanel;
import bs.tool.commongui.GuiUtils;
import bs.tool.commongui.utils.FileUtils;
import bs.tool.commongui.utils.SearchFileNameParams;
import bs.util.io.PropertiesUtils;
import org.apache.commons.io.LineIterator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * 文本文件切分.
 */
public class TextFileSplit extends GuiJPanel {

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
     * 切分结果文本域.
     */
    private JTextArea resultTextArea = createJTextArea(GuiUtils.font14_un);

    public TextFileSplit() {

        // 主面板：边界布局，分North、Center两部分，North用于放置输入及条件控件，Center是放置切分结果输出
        setLayout(new BorderLayout());

        // 输入及条件Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 1));

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
        splitButton.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent event) {
                String path = splitPathTextField.getText().trim();
                File file = new File(path);
                if (!file.exists()) {
                    showMessage("切分文本/文件夹不存在！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                splitResultDirectory = file.getAbsolutePath() + " - split result";
                Map<String, Object> paramsMap = new HashMap<String, Object>();
                paramsMap.put("fileNameContainsText", fileNameContainsTextField.getText().trim());
                paramsMap.put("fileNameNotContainsText", fileNameNotContainsTextField.getText().trim());
                paramsMap.put("fileNameSupportRegex", fileNameSupportRegex);
                splitButton.setEnabled(false);

                resultTextArea.setText("");
                try {
                    int splitLength = Integer.parseInt(splitValueField.getText().trim());
                    if (!file.isDirectory()) {
                        appendSplitResult(resultTextArea, file, splitLength);
                    } else {
                        List<File> files = new ArrayList<File>();
                        FileUtils.loopDirectory(file, files, new SearchFileNameParams(paramsMap));
                        for (File fFile : files) {
                            appendSplitResult(resultTextArea, fFile, splitLength);
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

            public void mousePressed(MouseEvent e) {
                resultTextArea.setText("");
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });
        buttonFlowPanel.add(splitButton);
        // 切分路径选择控件
        splitPathChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // 可选择文件/文件夹
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
            public void actionPerformed(ActionEvent event) {
                fileNameSupportRegex = ((JCheckBox) event.getSource()).isSelected();
            }
        });
        inputPanel.add(advancePanel);

        add(inputPanel, BorderLayout.NORTH);

        // 切分结果输出
        JPanel splitLogPanel = new JPanel(new BorderLayout());
        splitLogPanel.add(new JScrollPane(resultTextArea));
        add(splitLogPanel, BorderLayout.CENTER);
    }

    /**
     * append切分文本编码结果.
     */
    private void appendSplitResult(JTextArea resultTextArea, File file, int splitLength) throws IOException {
        resultTextArea.append(file.getAbsolutePath() + "\n    Result: " + splitFileResult(file, splitLength)
                + "\n");
    }

    /**
     * 切分文本编码.
     */
    private String splitFileResult(File file, int splitLength) throws IOException {
        StringBuilder rsb = new StringBuilder();
        String charset = JUniversalChardet.detectFileCharset(file, 4096);
        LineIterator lineIterator = org.apache.commons.io.FileUtils.lineIterator(file, charset);
        File splitResultDir = new File(splitResultDirectory);
        if (splitResultDir.exists()) {
            org.apache.commons.io.FileUtils.deleteDirectory(splitResultDir);
        }
        splitResultDir.mkdir();
        String fileName = file.getName();
        int lastIndex = fileName.lastIndexOf(".");
        lastIndex = (lastIndex == -1 ? fileName.length() : lastIndex);
        String fileSimpleName = fileName.substring(0, lastIndex);
        String fileType = fileName.substring(lastIndex + 1, fileName.length());
        String rs;
        int fileNum = 1;
        while ((rs = loopSplitFile(lineIterator, splitLength, fileSimpleName + (fileNum++) + (fileType.length() != 0 ? "." : "") + fileType)) != null) {
            if (rsb.length() != 0) {
                rsb.append("            ");
            }
            rsb.append(rs).append("\n");
        }
        lineIterator.close();
        return rsb.toString();
    }

    private String loopSplitFile(LineIterator lineIterator, int splitLength, String splitFileName) throws IOException {
        File splitFile = new File(splitResultDirectory + "/" + splitFileName);
        int lineCnt = 0;
        List<String> lines;
        while ((lines = iteratorLine(lineIterator, 100, splitLength, lineCnt)).size() != 0) {
            lineCnt = lines.size();
            org.apache.commons.io.FileUtils.writeLines(splitFile, GuiUtils.CHARSET_UTF_8, lines);
            if (currentSplitType.equals(GuiUtils.SPLIT_TYPE_SIZE) && splitFile.getTotalSpace() >= splitLength * 1024 * 1024) {
                break;
            }
        }
        if (splitFile.getTotalSpace() != 0) {
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
