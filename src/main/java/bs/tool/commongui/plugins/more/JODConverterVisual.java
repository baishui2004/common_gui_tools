package bs.tool.commongui.plugins.more;

import bs.tool.commongui.GuiJPanel;
import bs.tool.commongui.GuiUtils;
import bs.tool.commongui.utils.FileUtils;
import bs.tool.commongui.utils.SearchFileNameParams;
import bs.util.io.PropertiesUtils;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.*;
import java.util.List;

/**
 * OpenOffice文档转换.
 */
public class JODConverterVisual extends GuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 转换文件/文件夹路径表单.
     */
    private JTextField convertPathTextField = new JTextField();
    /**
     * 转换文件/文件夹路径选择.
     */
    private JFileChooser convertPath_Chooser = new JFileChooser();

    /**
     * 转换按钮.
     */
    private JButton convertButton;

    /**
     * 转换目标文件夹路径表单.
     */
    private JTextField convertedPathTextField = new JTextField();
    /**
     * 转换目标文件夹路径选择.
     */
    private JFileChooser convertedPath_Chooser = new JFileChooser();

    /**
     * 转换文件格式.
     */
    private String fileFromType = "";
    /**
     * 转换文件格式.
     */
    private String fileToType = "";

    /**
     * 高级条件面板.
     */
    private JPanel advanceConditionPanel;

    /**
     * 设置IP表单.
     */
    private JTextField ipTextField = new JTextField("127.0.0.1", 8);
    /**
     * 设置端口表单.
     */
    private JFormattedTextField portTextField = createNumberTextField(new NumberFormatter(), 5, GuiUtils.font14_cn,
            "8100");

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
    private boolean fileNameSupportRegex;

    /**
     * 转换结果文件域.
     */
    private JTextArea resultTextArea = createJTextArea(GuiUtils.font14_un);

    /**
     * 可进行转换的文件格式.
     */
    private String fromComboboxItems = "";
    /**
     * 转换文件格式Map.
     */
    private Map<String, String> formatsFromMap = new HashMap<String, String>();
    /**
     * 目标文件格式Map.
     */
    private Map<String, String> formatsToMap = new HashMap<String, String>();

    /**
     * 目标文件名是否保留原始文件类型.
     */
    private boolean keepOriginalFileType = false;

    /**
     * 把需要转换的文件当作txt处理表单.
     */
    private JCheckBox filesDealAsTxtBox;

    {
        // 转换配置
        String converterPropsFile = "conf/JODConverterVisual/converter.properties";
        try {
            Properties converterConfProperties = PropertiesUtils.getProperties(GuiUtils
                    .getActualPath(converterPropsFile));
            fromComboboxItems = converterConfProperties.getProperty("fromComboboxItems").trim().toLowerCase();
            for (Object propObj : converterConfProperties.keySet()) {
                String propStr = propObj.toString();
                if (propStr.endsWith(".formats.from")) {
                    formatsFromMap.put(propStr, "," + converterConfProperties.getProperty(propStr).trim().toLowerCase()
                            + ",");
                } else if (propStr.endsWith(".formats.to")) {
                    formatsToMap.put(propStr, "," + converterConfProperties.getProperty(propStr).trim().toLowerCase()
                            + ",");
                }
            }
        } catch (IOException e) {
            logLoadPropertiesException(converterPropsFile, e);
        }
    }

    public JODConverterVisual() {

        // 主面板：边界布局，分North、Center两部分，North用于放置输入及条件控件，Center是放置转换结果输出
        setLayout(new BorderLayout());

        // 输入及条件Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 1));
        add(inputPanel, BorderLayout.NORTH);

        // 转换from格式->to格式
        final DefaultComboBoxModel fileToBoxModel = new DefaultComboBoxModel();
        resetFileToBoxModel(fileToBoxModel, fromComboboxItems.split(",")[0]);
        final JComboBox fileFromBox = createJComboBox(fromComboboxItems.split(","), GuiUtils.font13_cn,
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        fileFromType = ((JComboBox) event.getSource()).getSelectedItem().toString();
                        resetFileToBoxModel(fileToBoxModel, fileFromType);
                    }
                });
        fileFromType = fileFromBox.getSelectedItem().toString();
        final JComboBox fileToBox = new JComboBox(fileToBoxModel);
        fileToBox.setFont(GuiUtils.font12_cn);
        fileToBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (((JComboBox) event.getSource()).getSelectedItem() != null) {
                    fileToType = ((JComboBox) event.getSource()).getSelectedItem().toString();
                }
            }
        });
        fileToType = fileToBox.getSelectedItem().toString();

        // 转换文件/文件夹表单、转换按钮Panel
        JPanel fileChooAndDetectPanel = new JPanel(new BorderLayout());
        // 转换文件/文件夹表单
        addJLabel(fileChooAndDetectPanel, "  文件/文件夹: ", GuiUtils.font14_cn, BorderLayout.WEST);
        JPanel fileChooPanel = new JPanel(new BorderLayout());
        fileChooPanel.add(new JPanel(), BorderLayout.NORTH);
        convertPathTextField.setEditable(false);
        convertPathTextField.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent e) {
                chooseConvertFile(fileFromBox);
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });
        addJTextField(fileChooPanel, convertPathTextField, GuiUtils.font14_un, BorderLayout.CENTER);
        fileChooPanel.add(new JPanel(), BorderLayout.SOUTH);
        fileChooAndDetectPanel.add(fileChooPanel, BorderLayout.CENTER);

        JPanel buttonFlowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJButton(buttonFlowPanel, "浏览", "", GuiUtils.font12_cn, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                chooseConvertFile(fileFromBox);
            }
        });
        // 转换按钮
        convertButton = createJButton("转换", "", GuiUtils.font14b_cn);
        convertButton.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent event) {
                File convertFile = new File(convertPathTextField.getText().trim());
                Map<String, Object> paramsMap = new HashMap<String, Object>();
                paramsMap.put("fileNameContainsText", fileNameContainsTextField.getText().trim());
                paramsMap.put("fileNameNotContainsText", fileNameNotContainsTextField.getText().trim());
                paramsMap.put("fileNameSupportRegex", fileNameSupportRegex);
                convertButton.setEnabled(false);

                try {
                    List<File> files = new ArrayList<File>();
                    if (!convertFile.isDirectory()) {
                        files.add(convertFile);
                    } else {
                        FileUtils.loopDirectory(convertFile, files, new SearchFileNameParams(paramsMap));
                    }
                    boolean filesDealAsTxt = filesDealAsTxtBox.isSelected();
                    appendConvertResult(resultTextArea, files, filesDealAsTxt);
                } catch (IOException e) {
                    showExceptionMessage(e);
                }
                convertButton.setEnabled(true);
            }

            public void mousePressed(MouseEvent e) {
                File convertFile = new File(convertPathTextField.getText().trim());
                if (!convertFile.exists()) {
                    showMessage("转换文件/文件夹不存在！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String convertedPath = convertedPathTextField.getText().trim();
                if (convertedPath.length() == 0) {
                    showMessage("转换目标文件夹未选择！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                resultTextArea.setText("开始转换，请稍候......\n\n");
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });

        // 转换文件格式 -> 目标文件格式
        addJLabel(buttonFlowPanel, " 格式:", GuiUtils.font14_cn);
        buttonFlowPanel.add(fileFromBox);
        addJLabel(buttonFlowPanel, "->", GuiUtils.font14_cn);
        buttonFlowPanel.add(fileToBox);

        buttonFlowPanel.add(convertButton);

        // 转换路径选择控件
        convertPath_Chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // 可选择文件/文件夹
        fileChooAndDetectPanel.add(buttonFlowPanel, BorderLayout.EAST);
        inputPanel.add(fileChooAndDetectPanel);

        // 转换目标文件夹表单面板
        JPanel convertPanel = new JPanel(new BorderLayout());
        // 转换目标文件夹表单
        addJLabel(convertPanel, "  目标文件夹:  ", GuiUtils.font14_cn, BorderLayout.WEST);
        JPanel convertFileChooPanel = new JPanel(new BorderLayout());
        convertFileChooPanel.add(new JPanel(), BorderLayout.NORTH);
        convertedPathTextField.setEditable(false);
        convertedPathTextField.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent e) {
                chooseConvertedFolder();
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });
        addJTextField(convertFileChooPanel, convertedPathTextField, GuiUtils.font14_un, BorderLayout.CENTER);
        convertFileChooPanel.add(new JPanel(), BorderLayout.SOUTH);
        convertPanel.add(convertFileChooPanel, BorderLayout.CENTER);
        JPanel comboxButtonFlowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));

        addJButton(comboxButtonFlowPanel, "浏览", "", GuiUtils.font12_cn, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                chooseConvertedFolder();
            }
        });
        // 目标路径选择控件
        convertedPath_Chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 只可选择文件夹

        // 目标文件名是否保留原始文件类型.
        addJCheckBox(comboxButtonFlowPanel, "文件名中保留原类型", false, GuiUtils.font14_cn, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                keepOriginalFileType = ((JCheckBox) event.getSource()).isSelected();
            }
        });

        // 是否作为文本处理
        filesDealAsTxtBox = new JCheckBox("作为文本处理(全部) ");
        filesDealAsTxtBox.setFont(GuiUtils.font14_cn);
        filesDealAsTxtBox.setSelected(false);
        filesDealAsTxtBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                JCheckBox checkBox = (JCheckBox) event.getSource();
                if (checkBox.isSelected()) {
                    fileFromBox.setSelectedItem("txt");
                    fileFromBox.setEnabled(false);
                } else {
                    fileFromBox.setEnabled(true);
                    File selectFile = convertPath_Chooser.getSelectedFile();
                    if (!selectFile.isDirectory()) {
                        String fileType = FileUtils.getFileType(selectFile.getName());
                        if (("," + fromComboboxItems + ",").contains("," + fileType + ",")) {
                            fileFromBox.setSelectedItem(fileType);
                            fileFromBox.setEnabled(false);
                        }
                    }
                }
            }
        });
        filesDealAsTxtBox.setEnabled(false);
        comboxButtonFlowPanel.add(filesDealAsTxtBox);

        // 展开/收缩高级(条件)按钮
        addJButton(comboxButtonFlowPanel, "高级", "", GuiUtils.font14_cn, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                advanceConditionPanel.setVisible(!advanceConditionPanel.isVisible());
                getContextPanel().revalidate();
            }
        });
        convertPanel.add(comboxButtonFlowPanel, BorderLayout.EAST);
        inputPanel.add(convertPanel);

        // 高级(条件)及转换日志输出面板，使用边界布局，North为高级(条件)，Center为转换日志输出
        JPanel advanceAndLogPanel = new JPanel(new BorderLayout());
        // 高级(条件)
        advanceConditionPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        advanceConditionPanel.setVisible(false);
        advanceAndLogPanel.add(advanceConditionPanel, BorderLayout.NORTH);

        // 设置IP
        addJLabel(advanceConditionPanel, " IP:", GuiUtils.font14_cn);
        advanceConditionPanel.add(ipTextField);
        // 设置端口
        addJLabel(advanceConditionPanel, "端口:", GuiUtils.font14_cn);
        advanceConditionPanel.add(portTextField);

        // 文件名包含(不包含)字符
        addJLabel(advanceConditionPanel, " 文件名包含字符:", GuiUtils.font14_cn);
        fileNameContainsTextField = new JTextField(14);
        addJTextField(advanceConditionPanel, fileNameContainsTextField, GuiUtils.font14_un);
        addJLabel(advanceConditionPanel, " 文件名不包含字符:", GuiUtils.font14_cn);
        fileNameNotContainsTextField = new JTextField(14);
        addJTextField(advanceConditionPanel, fileNameNotContainsTextField, GuiUtils.font14_un);
        // 是否支持正则
        addJCheckBox(advanceConditionPanel, "支持正则", false, GuiUtils.font14_cn, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                fileNameSupportRegex = ((JCheckBox) event.getSource()).isSelected();
            }
        });

        // 转换结果输出
        JPanel convertLogPanel = new JPanel(new BorderLayout());
        convertLogPanel.add(new JScrollPane(resultTextArea));
        advanceAndLogPanel.add(convertLogPanel, BorderLayout.CENTER);

        add(advanceAndLogPanel, BorderLayout.CENTER);
    }

    /**
     * 根据文件格式获取可转换的文件格式.
     *
     * @param type 原格式
     * @return <code>String</code> 获取可转换的文件格式
     */
    private String getConvertAbleTypes(String type) {
        if (type == null || type.trim().length() == 0) {
            return "";
        }
        for (String formatFrom : formatsFromMap.keySet()) {
            if (formatsFromMap.get(formatFrom).contains("," + type + ",")) {
                return formatsToMap.get(formatFrom.subSequence(0, formatFrom.length() - 4) + "to");
            }
        }
        return "";
    }

    /**
     * 重设FileToBoxModel，实现联动.
     *
     * @param fileToBoxModel JCombobox Model
     * @param fromType       转换文件格式
     */
    private void resetFileToBoxModel(DefaultComboBoxModel fileToBoxModel, String fromType) {
        String ableTypes = getConvertAbleTypes(fromType);
        fileToBoxModel.removeAllElements();
        for (String type : ableTypes.split(",")) {
            type = type.trim();
            if (type.length() != 0 && !type.equals(fromType)) {
                fileToBoxModel.addElement(type);
            }
        }
    }

    /**
     * append转换文件结果.
     */
    private void appendConvertResult(JTextArea resultTextArea, List<File> files, boolean filesDealAsTxt)
            throws IOException {
        OpenOfficeConnection con = new SocketOpenOfficeConnection(ipTextField.getText().trim(),
                Integer.parseInt(portTextField.getText().trim().replace(",", "")));
        try {
            con.connect();
        } catch (ConnectException e) {
            resultTextArea
                    .append("转换出错，请检查OpenOffice服务是否启动或者点开高级按钮查看IP、端口设置是否正确\n"
                            + "本机OpenOffice服务启动方法：CMD下，OpenOffice_HOME/program>soffice -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\" -nofirststartwizard\n\n");
            GuiUtils.log(e);
            return;
        }
        DocumentConverter converter = new OpenOfficeDocumentConverter(con);

        int convertNum = 0;
        int convertedNum = 0;

        String convertPath = convertPathTextField.getText().trim();
        File convertFile = new File(convertPath);
        String convertedPath = convertedPathTextField.getText().trim();
        // 转换文件
        if (!convertFile.isDirectory()) {
            String convertFileName = convertFile.getName();
            String fileType = FileUtils.getFileType(convertFileName);
            convertFileName = (keepOriginalFileType ? convertFileName + "." : convertFileName.substring(0,
                    convertFileName.length() - FileUtils.getFileType(convertFileName).length())
                    + (fileType.length() == 0 ? "." : ""));
            File convertedFile = new File(convertedPath + File.separator + convertFileName + fileToType);
            convertNum++;
            String success = convertFile(convertFile, convertedFile, converter, resultTextArea, filesDealAsTxt);
            if (success.equals("Success")) {
                convertedNum++;
            }
            resultTextArea.append(convertFile.getAbsolutePath() + "         Convert: " + success + "\n");
        }
        // 转换文件夹下的文件
        else {
            for (File file : files) {
                String fileType = FileUtils.getFileType(file.getName());
                if (fileType.equals(fileFromType) || filesDealAsTxt) {
                    convertNum++;
                    String fileName = file.getName();
                    fileName = (keepOriginalFileType ? fileName + "." : fileName.substring(0, fileName.length()
                            - fileType.length())
                            + (fileType.length() == 0 ? "." : ""));
                    String success = convertFile(file,
                            new File(convertedPath + File.separator + fileName + fileToType), converter,
                            resultTextArea, filesDealAsTxt);
                    if (success.equals("Success")) {
                        convertedNum++;
                    }
                    resultTextArea.append(file.getAbsolutePath() + "         Convert: " + success + "\n");
                }
            }
        }
        con.disconnect();

        resultTextArea.append("\nConvert files: " + convertNum + ", Success: " + convertedNum + ", Fail: "
                + (convertNum - convertedNum) + ".");
    }

    /**
     * 转换文件.
     */
    private String convertFile(File fromFile, File toFile, DocumentConverter converter, JTextArea resultTextArea,
                               boolean filesDealAsTxt) {
        try {
            if (filesDealAsTxt) {
                File copyToFile = new File(toFile.getAbsolutePath() + ".convert-as-txt-tmp.txt");
                org.apache.commons.io.FileUtils.copyFile(fromFile, copyToFile);
                converter.convert(copyToFile, toFile);
                copyToFile.delete();
            } else {
                converter.convert(fromFile, toFile);
            }
            return "Success";
        } catch (Exception e) {
            GuiUtils.log(e);
            return "Fail[Exception: " + e.getMessage() + "]";
        }
    }

    /**
     * 选择转换文件/文件夹.
     */
    private void chooseConvertFile(JComboBox fileFromBox) {
        if (convertPath_Chooser.showDialog(getContextPanel(), "确定") == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File selectFile = convertPath_Chooser.getSelectedFile();
        if (selectFile != null) {
            boolean flag = true;
            if (!selectFile.exists()) {
                flag = false;
                showMessage("所选文件/文件夹不存在！", "警告", JOptionPane.WARNING_MESSAGE);
            } else if (selectFile.isDirectory()) {
                fileFromBox.setEnabled(true);
                filesDealAsTxtBox.setEnabled(true);
            } else if (!selectFile.isDirectory()) {
                String fileType = FileUtils.getFileType(selectFile.getName());
                if (("," + fromComboboxItems + ",").contains("," + fileType + ",")) {
                    fileFromBox.setSelectedItem(fileType);
                    fileFromBox.setEnabled(false);
                    filesDealAsTxtBox.setSelected(false);
                    filesDealAsTxtBox.setEnabled(true);
                } else {
                    int dealAsTxt = showConfirmMessage("不支持转换此种格式的文件，是否作为文本处理！", "确认", JOptionPane.OK_CANCEL_OPTION);
                    if (dealAsTxt == JOptionPane.OK_OPTION) {
                        fileFromBox.setEnabled(false);
                        filesDealAsTxtBox.setSelected(true);
                        filesDealAsTxtBox.setEnabled(false);
                    } else {
                        flag = false;
                    }
                }
            }
            if (flag) {
                convertPathTextField.setText(selectFile.getAbsolutePath());
            }
        }
    }

    /**
     * 选择目标文件夹.
     */
    private void chooseConvertedFolder() {
        if (convertedPath_Chooser.showDialog(getContextPanel(), "确定") == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File selectFile = convertedPath_Chooser.getSelectedFile();
        if (selectFile != null) {
            if (!selectFile.exists()) {
                showMessage("转换目标文件夹不存在将自动创建！", "提示", JOptionPane.INFORMATION_MESSAGE);
                try {
                    org.apache.commons.io.FileUtils.forceMkdir(selectFile);
                } catch (IOException e) {
                    showExceptionMessage(e, "创建转换目标文件夹失败！");
                    return;
                }
            }
            convertedPathTextField.setText(selectFile.getAbsolutePath());
        }
    }

}
