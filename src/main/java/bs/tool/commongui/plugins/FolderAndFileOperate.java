package bs.tool.commongui.plugins;

import bs.tool.commongui.AbstractGuiJPanel;
import bs.tool.commongui.GuiUtils;
import bs.tool.commongui.utils.FileUtils;
import bs.tool.commongui.utils.SimpleMouseListener;
import bs.util.io.PropertiesUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

/**
 * 文件(夹)操作.
 */
public class FolderAndFileOperate extends AbstractGuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 路径表单.
     */
    private JTextField pathTextField = new JTextField();
    /**
     * 目录选择.
     */
    private JFileChooser searchFileChooser = new JFileChooser();

    /**
     * 查找类型-文件(夹)查找.
     */
    private final String typeSearch = "文件(夹)查找";
    /**
     * 查找类型-重复文件查找.
     */
    private final String typeRepeatSearch = "重复文件查找";
    /**
     * 查找类型-同名文件查找.
     */
    private final String typeSameNameSearch = "同名文件查找";
    /**
     * 查找类型-空文件(夹)查找.
     */
    private final String typeBlankSearch = "空文件(夹)查找";
    /**
     * 查找类型.
     */
    private final String[] types = new String[]{typeSearch, typeRepeatSearch, typeSameNameSearch, typeBlankSearch};
    /**
     * 当前查找类型.
     */
    private String curType = types[0];

    /**
     * 文件类型表单.
     */
    private JTextField searchFileTypeTextField = new JTextField(39);
    /**
     * 是否相同文件类型，默认为false，用于重复及同名文件查找.
     */
    private boolean repeatSameSuffix = false;
    /**
     * 是否相同文件类型.
     */
    private JCheckBox repeatSameSuffixCheckBox = createJCheckBox("相同文件类型", false, GuiUtils.font14_cn, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
            JCheckBox checkBox = (JCheckBox) event.getSource();
            repeatSameSuffix = checkBox.isSelected();
        }
    });

    /**
     * 操作类型-查找.
     */
    private final String action_onlySearch = "查找";
    /**
     * 操作类型-复制.
     */
    private final String action_copyFile = "复制";
    /**
     * 操作类型-剪切.
     */
    private final String action_cutFile = "剪切";
    /**
     * 操作类型-重命名.
     */
    private final String action_renameFile = "重命名";
    /**
     * 操作类型-删除.
     */
    private final String action_deleteFile = "删除";
    /**
     * 操作类型-删除重复.
     */
    private final String action_deleteRepeatFile = "删除重复";
    /**
     * 操作类型-删除空文件夹.
     */
    private final String action_deleteBlankFolder = "删除空文件夹";
    /**
     * 操作类型.
     */
    private final String[] actions = new String[]{action_onlySearch, action_copyFile, action_cutFile,
            action_renameFile, action_deleteFile, action_deleteRepeatFile, action_deleteBlankFolder};
    /**
     * 当前操作类型.
     */
    private String curAction = actions[0];
    /**
     * 操作(复制、剪切文件存放位置)表单.
     */
    private JTextField actionTextField = new JTextField(15);
    /**
     * 操作(复制、剪切文件存放位置)目录选择.
     */
    private JFileChooser actionFileChooser = new JFileChooser();
    /**
     * 操作(复制、剪切文件存放位置)目录浏览按钮.
     */
    private JButton actionChooseButton = createJButton("浏览", "", GuiUtils.font12_cn);

    /**
     * 常见文件类型.
     */
    private Map<String, String> fileTypesMap = new HashMap<String, String>();
    /**
     * 常见文件类型名称.
     */
    private String[] fileTypeNames;

    /**
     * 是否包括文件，默认为true.
     */
    private boolean containsFile = true;
    /**
     * 是否包括文件夹，默认为true.
     */
    private boolean containsFolder = true;
    /**
     * 是否包括隐藏文件(夹)，默认为true.
     */
    private boolean containsHidden = true;
    /**
     * 是否包括非隐藏文件(夹)，默认为true.
     */
    private boolean containsNotHidden = true;

    /**
     * 是否显示文件(夹)完整路径.
     */
    private boolean viewFullPathProp = true;
    /**
     * 是否显示文件大小.
     */
    private boolean viewSizeCkProp = true;
    /**
     * 显示文件大小单位，默认M.
     */
    private String viewSizeCkPropUnit = "M";
    /**
     * 是否显示修改时间.
     */
    private boolean viewModifyTimeProp = false;
    /**
     * 是否显示隐藏属性.
     */
    private boolean viewHiddenProp = false;

    /**
     * 文件大小，最小表单.
     */
    private JFormattedTextField fileSizeFromTextField = createNumberTextField();
    /**
     * 文件大小，最大表单.
     */
    private JFormattedTextField fileSizeToTextField = createNumberTextField();
    /**
     * 文件大小单位，最小下拉框.
     */
    private JComboBox fileSizeUnitFromBox = createFileSizeUnitBox(GuiUtils.font13_cn);
    /**
     * 文件大小单位，最大下拉框.
     */
    private JComboBox fileSizeUnitToBox = createFileSizeUnitBox(GuiUtils.font13_cn);

    /**
     * 修改时间，开始表单.
     */
    private JFormattedTextField modifyTimeFromTextField = createDateTextField();
    /**
     * 修改时间，结束表单.
     */
    private JFormattedTextField modifyTimeToTextField = createDateTextField();
    /**
     * 遍历目录层级，默认所有层级.
     */
    private JTextField folderHierarchyField = new JTextField(5);

    /**
     * 文件(夹)路径包含字符表单.
     */
    private JTextField filePathContainsTextField;
    /**
     * 文件(夹)路径不包含字符表单.
     */
    private JTextField filePathNotContainsTextField;
    /**
     * 文件(夹)路径是否支持正则.
     */
    private boolean filePathSupportRegex = false;

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
    private boolean fileNameSupportRegex = false;

    /**
     * 文件夹路径包含字符表单.
     */
    private JTextField folderPathContainsTextField;
    /**
     * 文件夹路径不包含字符表单.
     */
    private JTextField folderPathNotContainsTextField;
    /**
     * 文件夹路径是否支持正则.
     */
    private boolean folderPathSupportRegex = false;

    /**
     * 结果文本域.
     */
    private JTextArea resultTextArea = createJTextArea(GuiUtils.font14_un);

    /**
     * 高级条件面板.
     */
    private JPanel advanceConditionPanel;

    {
        // 常见文件类型
        String fileTypePropsPath = "conf/FolderAndFileOperate/filetype.properties";
        try {
            Map<String, String> propsMap = PropertiesUtils.getPropertiesMap(GuiUtils.getActualPath(fileTypePropsPath));
            List<String> propsSortAndNames = new ArrayList<String>();
            for (String key : propsMap.keySet()) {
                propsSortAndNames.add(key);
            }
            // 排序
            Collections.sort(propsSortAndNames);

            int expSize = propsSortAndNames.size() + 1;
            fileTypeNames = new String[expSize];
            fileTypeNames[0] = "所有";
            fileTypesMap.put(fileTypeNames[0], "");

            // 常见文件类型，按序号正排序
            for (int i = 1; i < expSize; i++) {
                String key = propsSortAndNames.get(i - 1);
                String[] keySplit = key.split("_");
                fileTypeNames[i] = keySplit.length > 1 ? key.substring(keySplit[0].length() + 1) : keySplit[0];
                fileTypesMap.put(fileTypeNames[i], propsMap.get(key));
            }
        } catch (IOException e) {
            logLoadPropertiesException(fileTypePropsPath, e);
        }
    }

    public FolderAndFileOperate() {

        // 主面板：边界布局，分North、Center两部分，North用于放置条件控件，Center是放置高级(条件)及输出
        setLayout(new BorderLayout());

        // 输入/操作
        JPanel inputPanel = new JPanel(new GridLayout(3, 1));
        add(inputPanel, BorderLayout.NORTH);

        // 目录选择/填写
        JPanel fileChooPanel = new JPanel(new BorderLayout());
        addJLabel(fileChooPanel, "  目录: ", GuiUtils.font14_cn, BorderLayout.WEST);
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.add(new JPanel(), BorderLayout.NORTH);
        addJTextField(pathPanel, pathTextField, GuiUtils.font14_un, BorderLayout.CENTER);
        pathPanel.add(new JPanel(), BorderLayout.SOUTH);
        fileChooPanel.add(pathPanel, BorderLayout.CENTER);
        JPanel buttonFlowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJButton(buttonFlowPanel, "浏览", "", GuiUtils.font12_cn,
                buttonBrowseListener(searchFileChooser, pathTextField));

        // Search按钮
        addJButton(buttonFlowPanel, "查找", "", GuiUtils.font14b_cn, new SimpleMouseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                String path = pathTextField.getText().trim();
                if (!new File(path).exists()) {
                    showMessage("查找目录不存在！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (curAction.equals(action_copyFile) || curAction.equals(action_cutFile)) {
                    String actionPath = actionTextField.getText().trim();
                    if (!new File(actionPath).exists()) {
                        showMessage("复制或剪切文件存放目录不存在！", "警告", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                if (curAction.equals(action_renameFile) && !curType.equals(typeSearch)) {
                    showMessage(typeSearch + "才可以进行重命名操作！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Map<String, Object> paramsMap = new HashMap<String, Object>();
                if (curType.equals(typeRepeatSearch)) {
                    paramsMap.put("type_repeatSearch", true);
                    FileUtils.searchRepeatSizeFilesMap = new HashMap<String, List<File>>(5120);
                    FileUtils.searchRepeatSameSizeFilesMap = new HashMap<String, List<File>>(512);
                    FileUtils.repeatFilesSet = new LinkedHashSet<String>(512);
                } else if (curType.equals(typeSameNameSearch)) {
                    paramsMap.put("type_sameNameSearch", true);
                    FileUtils.searchSameNameFilesMap = new HashMap<String, List<File>>(5120);
                    FileUtils.sameNameFilesSet = new LinkedHashSet<String>(512);
                } else if (curType.equals(typeBlankSearch)) {
                    paramsMap.put("type_blankSearch", true);
                }
                Long curTime = System.currentTimeMillis();
                String curFileType = searchFileTypeTextField.getText();
                paramsMap.put("searchFileType", curFileType);
                paramsMap.put("containsFile", containsFile);
                paramsMap.put("containsFolder", containsFolder);
                paramsMap.put("containsHidden", containsHidden);
                paramsMap.put("containsNotHidden", containsNotHidden);
                paramsMap.put("repeatSameSuffix", repeatSameSuffix);
                paramsMap.put("fileSizeFrom", GuiUtils.getCountFileSizeUnit(fileSizeFromTextField.getText().trim(),
                        fileSizeUnitFromBox.getSelectedItem().toString()));
                paramsMap.put("fileSizeTo", GuiUtils.getCountFileSizeUnit(fileSizeToTextField.getText().trim(),
                        fileSizeUnitToBox.getSelectedItem().toString()));
                paramsMap.put("modifyTimeFrom",
                        getLongFormatTime(modifyTimeFromTextField.getText().trim(), new SimpleDateFormat(FORMATTER_YYYYMMDDHHMMSS)));
                paramsMap.put("modifyTimeTo",
                        getLongFormatTime(modifyTimeToTextField.getText().trim(), new SimpleDateFormat(FORMATTER_YYYYMMDDHHMMSS)));
                paramsMap.put("filePathContainsText", filePathContainsTextField.getText().trim());
                paramsMap.put("filePathNotContainsText", filePathNotContainsTextField.getText().trim());
                paramsMap.put("filePathSupportRegex", filePathSupportRegex);
                paramsMap.put("fileNameContainsText", fileNameContainsTextField.getText().trim());
                paramsMap.put("fileNameNotContainsText", fileNameNotContainsTextField.getText().trim());
                paramsMap.put("fileNameSupportRegex", fileNameSupportRegex);
                paramsMap.put("folderPathContainsText", folderPathContainsTextField.getText().trim());
                paramsMap.put("folderPathNotContainsText", folderPathNotContainsTextField.getText().trim());
                paramsMap.put("folderPathSupportRegex", folderPathSupportRegex);
                paramsMap.put("folderHierarchyText", folderHierarchyField.getText().trim());
                // 最终Search到的File，当查找类型为'重复文件查找'，files最后长度为0，结果保存在FileUtils.repeatFilesSet及FileUtils.repeatFilesMap中
                List<File> files = FileUtils.getAllSubFiles(path, paramsMap);
                int cntAction = 0;
                if (curType.equals(typeRepeatSearch)) {
                    List<File> repeatFiles = null;
                    long cnt = 0;
                    int groupCnt = FileUtils.repeatFilesSet.size();
                    int sizeCnt = 0;
                    int f = 0;
                    resultTextArea
                            .append("查找方法：取出有相同大小的所有文件，比较文件大小以及前2048Byte的内容的MD5值是否一样，\n         如果两者相同，则认为重复，否则认为不重复，有较小的误差率。\n\n\n");
                    for (String prop : FileUtils.repeatFilesSet) {
                        resultTextArea.append("第" + (++f) + "组：\n");
                        repeatFiles = FileUtils.searchRepeatSameSizeFilesMap.get(prop);
                        cnt += repeatFiles.size();
                        Integer[] cntArr = printPropAndAction(curAction, repeatFiles);
                        cntAction += cntArr[0];
                        sizeCnt += cntArr[5];
                    }
                    resultTextArea.append("\n\nCount repeat group: " + groupCnt + ", files: " + cnt + ", Size: "
                            + sizeCnt + "M");
                    FileUtils.searchRepeatSizeFilesMap.clear();
                    FileUtils.searchRepeatSizeFilesMap = null;
                    FileUtils.searchRepeatSameSizeFilesMap.clear();
                    FileUtils.searchRepeatSameSizeFilesMap = null;
                    FileUtils.repeatFilesSet.clear();
                    FileUtils.repeatFilesSet = null;
                } else if (curType.equals(typeSameNameSearch)) {
                    List<File> sameNameFiles = null;
                    long cnt = 0;
                    int groupCnt = FileUtils.sameNameFilesSet.size();
                    int sizeCnt = 0;
                    int f = 0;
                    resultTextArea.append("查找方法：比较文件名及文件类型，查找相同文件名称的文件。\n\n\n");
                    for (String prop : FileUtils.sameNameFilesSet) {
                        resultTextArea.append("第" + (++f) + "组：\n");
                        sameNameFiles = FileUtils.searchSameNameFilesMap.get(prop);
                        cnt += sameNameFiles.size();
                        Integer[] cntArr = printPropAndAction(curAction, sameNameFiles);
                        cntAction += cntArr[0];
                        sizeCnt += cntArr[5];
                    }
                    resultTextArea.append("\n\nCount same name group: " + groupCnt + ", files: " + cnt + ", Size: "
                            + sizeCnt + "M");
                    FileUtils.searchSameNameFilesMap.clear();
                    FileUtils.searchSameNameFilesMap = null;
                    FileUtils.sameNameFilesSet.clear();
                    FileUtils.sameNameFilesSet = null;
                } else {
                    Integer[] cntArr = printPropAndAction(curAction, files);
                    cntAction += cntArr[0];
                    resultTextArea.append("\n\nCount: " + files.size() + ", Size: " + cntArr[5] + "M, folders: "
                            + cntArr[1] + ", files: " + cntArr[2] + ", hidden folders: " + cntArr[3]
                            + ", hidden files: " + cntArr[4]);
                }
                files.clear();
                files = null;
                if (curAction.equals(action_copyFile)) {
                    resultTextArea.append(", Count Copy files: " + cntAction);
                } else if (curAction.equals(action_cutFile)) {
                    resultTextArea.append(", Count Cut files: " + cntAction);
                } else if (curAction.equals(action_renameFile)) {
                    resultTextArea.append(", Count Rename files: " + cntAction);
                } else if (curAction.equals(action_deleteFile)) {
                    resultTextArea.append(", Count Delete files: " + cntAction);
                } else if (curAction.equals(action_deleteRepeatFile)) {
                    resultTextArea.append(", Count Delete Repeat files: " + cntAction);
                } else if (curAction.equals(action_deleteBlankFolder)) {
                    resultTextArea.append(", Count Delete Blank folders: " + cntAction);
                }
                resultTextArea.append(". Cost time:" + (System.currentTimeMillis() - curTime) / 1000.0 + "s.");

                System.gc();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                resultTextArea.setText("");
            }
        });
        // 路径选择控件，仅可选择文件夹
        searchFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooPanel.add(buttonFlowPanel, BorderLayout.EAST);
        inputPanel.add(fileChooPanel);

        // 条件
        JPanel conditionPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        // 查找类型
        addJLabel(conditionPanel, " 查找类型: ", GuiUtils.font14_cn);
        addJComboBox(conditionPanel, types, GuiUtils.font13_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                curType = ((JComboBox) event.getSource()).getSelectedItem().toString();
                if (curType.equals(typeBlankSearch)) {
                    fileSizeFromTextField.setText("");
                    fileSizeFromTextField.setEnabled(false);
                    fileSizeUnitFromBox.setEnabled(false);
                    fileSizeToTextField.setText("");
                    fileSizeToTextField.setEnabled(false);
                    fileSizeUnitToBox.setEnabled(false);
                } else {
                    fileSizeFromTextField.setEnabled(true);
                    fileSizeUnitFromBox.setEnabled(true);
                    fileSizeToTextField.setEnabled(true);
                    fileSizeUnitToBox.setEnabled(true);
                }
                if (curType.equals(typeRepeatSearch) || curType.equals(typeSameNameSearch)) {
                    repeatSameSuffixCheckBox.setEnabled(true);
                } else {
                    repeatSameSuffixCheckBox.setEnabled(false);
                }
            }
        });

        addJLabel(conditionPanel, "   文件类型: ", GuiUtils.font14_cn);

        // 常见文件类型下拉框
        addJComboBox(conditionPanel, fileTypeNames, GuiUtils.font13_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                searchFileTypeTextField.setText(fileTypesMap.get(((JComboBox) event.getSource()).getSelectedItem().toString()));
            }
        });

        addJLabel(conditionPanel, " 包括:", GuiUtils.font14_cn);
        // 是否包括文件JCheckBox
        addJCheckBox(conditionPanel, "文件", true, GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JCheckBox checkBox = (JCheckBox) event.getSource();
                containsFile = checkBox.isSelected();
            }
        });
        // 是否包括文件夹JCheckBox
        addJCheckBox(conditionPanel, "文件夹", true, GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JCheckBox checkBox = (JCheckBox) event.getSource();
                containsFolder = checkBox.isSelected();
            }
        });
        // 是否包括隐藏文件(夹)JCheckBox
        addJCheckBox(conditionPanel, "隐藏文件(夹)", true, GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JCheckBox checkBox = (JCheckBox) event.getSource();
                containsHidden = checkBox.isSelected();
            }
        });
        // 是否包括隐藏文件(夹)JCheckBox
        addJCheckBox(conditionPanel, "非隐藏文件(夹)", true, GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JCheckBox checkBox = (JCheckBox) event.getSource();
                containsNotHidden = checkBox.isSelected();
            }
        });
        inputPanel.add(conditionPanel);

        JPanel viewPropAndActionPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJLabel(viewPropAndActionPanel, " 操作类型: ", GuiUtils.font14_cn);
        // 操作类型下拉框
        addJComboBox(viewPropAndActionPanel, actions, GuiUtils.font13_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                curAction = ((JComboBox) event.getSource()).getSelectedItem().toString();
                if (!curAction.equals(action_onlySearch)) {
                    // 将焦点转移到下一个组件，就好像此 Component 曾是焦点所有者
                    ((JComboBox) event.getSource()).transferFocus();
                    showMessage("非查找操作务必确认不会对原文件(夹)造成意外损害！", "警告", JOptionPane.WARNING_MESSAGE);
                }
                if (curAction.equals(action_copyFile) || curAction.equals(action_cutFile)) {
                    actionTextField.setEnabled(true);
                    actionChooseButton.setEnabled(true);
                } else {
                    actionTextField.setText("");
                    actionTextField.setEnabled(false);
                    actionChooseButton.setEnabled(false);
                }
            }
        });
        addJTextField(viewPropAndActionPanel, actionTextField, GuiUtils.font14_un);
        actionTextField.setEnabled(false);
        // 路径选择控件，仅可选择文件夹
        actionFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        actionChooseButton.addActionListener(buttonBrowseListener(actionFileChooser, actionTextField));
        actionChooseButton.setEnabled(false);
        viewPropAndActionPanel.add(actionChooseButton);

        addJLabel(viewPropAndActionPanel, " 显示:", GuiUtils.font14_cn);
        // 是否显示文件(夹)完整路径
        addJCheckBox(viewPropAndActionPanel, "完整路径", true, GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JCheckBox checkBox = (JCheckBox) event.getSource();
                viewFullPathProp = checkBox.isSelected();
            }
        });
        // 是否显示文件大小JCheckBox
        addJCheckBox(viewPropAndActionPanel, "大小", true, GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JCheckBox checkBox = (JCheckBox) event.getSource();
                viewSizeCkProp = checkBox.isSelected();
            }
        });
        JComboBox viewSizeCkPropUnitBox = createFileSizeUnitBox(GuiUtils.font12_cn);
        viewSizeCkPropUnitBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                viewSizeCkPropUnit = ((JComboBox) event.getSource()).getSelectedItem().toString();
            }
        });
        viewPropAndActionPanel.add(viewSizeCkPropUnitBox);
        // 是否显示修改时间
        addJCheckBox(viewPropAndActionPanel, "修改时间", false, GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JCheckBox checkBox = (JCheckBox) event.getSource();
                viewModifyTimeProp = checkBox.isSelected();
            }
        });
        // 是否显示文件大小JCheckBox
        addJCheckBox(viewPropAndActionPanel, "隐藏属性", false, GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JCheckBox checkBox = (JCheckBox) event.getSource();
                viewHiddenProp = checkBox.isSelected();
            }
        });
        // 展开/收缩高级(条件)按钮
        addJButton(viewPropAndActionPanel, "高级", "", GuiUtils.font12_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                advanceConditionPanel.setVisible(!advanceConditionPanel.isVisible());
                getContextPanel().revalidate();
            }
        });
        inputPanel.add(viewPropAndActionPanel);

        // 高级(条件)及输出面板，使用边界布局，North为高级(条件)，Center为输出
        JPanel advanceAndResultPanel = new JPanel(new BorderLayout());
        // 高级(条件
        advanceConditionPanel = new JPanel(new GridLayout(5, 1));
        advanceConditionPanel.setVisible(false);
        advanceAndResultPanel.add(advanceConditionPanel, BorderLayout.NORTH);

        // 文件类型条件
        JPanel fileTypeConditionPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJLabel(fileTypeConditionPanel, " 文件类型: ", GuiUtils.font14_cn);
        fileTypeConditionPanel.add(searchFileTypeTextField);
        // 是否相同文件类型
        fileTypeConditionPanel.add(repeatSameSuffixCheckBox);
        repeatSameSuffixCheckBox.setEnabled(false);
        advanceConditionPanel.add(fileTypeConditionPanel);

        // 大小条件、时间条件
        JPanel fileSizeTimePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJLabel(fileSizeTimePanel, " 文件大小: ", GuiUtils.font14_cn);
        fileSizeTimePanel.add(fileSizeFromTextField);
        fileSizeTimePanel.add(fileSizeUnitFromBox);
        addJLabel(fileSizeTimePanel, "-", GuiUtils.font14_cn);
        fileSizeTimePanel.add(fileSizeToTextField);
        fileSizeTimePanel.add(fileSizeUnitToBox);
        addJLabel(fileSizeTimePanel, "   修改时间:", GuiUtils.font14_cn);
        fileSizeTimePanel.add(modifyTimeFromTextField);
        addJLabel(fileSizeTimePanel, "--", GuiUtils.font14_cn);
        fileSizeTimePanel.add(modifyTimeToTextField);
        addJLabel(fileSizeTimePanel, "   遍历层级:", GuiUtils.font14_cn);
        addJTextField(fileSizeTimePanel, folderHierarchyField, GuiUtils.font14_cn);
        advanceConditionPanel.add(fileSizeTimePanel);

        // 文件(夹)路径包含(不包含)字符
        JPanel filePathContainsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJLabel(filePathContainsPanel, " 文件/夹路径包含:", GuiUtils.font14_cn);
        filePathContainsTextField = new JTextField(24);
        addJTextField(filePathContainsPanel, filePathContainsTextField, GuiUtils.font14_un);
        addJLabel(filePathContainsPanel, " 文件/夹路径不包含:", GuiUtils.font14_cn);
        filePathNotContainsTextField = new JTextField(24);
        addJTextField(filePathContainsPanel, filePathNotContainsTextField, GuiUtils.font14_un);
        // 是否支持正则
        addJCheckBox(filePathContainsPanel, "支持正则", false, GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                filePathSupportRegex = ((JCheckBox) event.getSource()).isSelected();
            }
        });
        advanceConditionPanel.add(filePathContainsPanel);

        // 文件名包含(不包含)字符
        JPanel fileNameContainsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJLabel(fileNameContainsPanel, " 文件名包含字符: ", GuiUtils.font14_cn);
        fileNameContainsTextField = new JTextField(24);
        addJTextField(fileNameContainsPanel, fileNameContainsTextField, GuiUtils.font14_un);
        addJLabel(fileNameContainsPanel, " 文件名不包含字符: ", GuiUtils.font14_cn);
        fileNameNotContainsTextField = new JTextField(24);
        addJTextField(fileNameContainsPanel, fileNameNotContainsTextField, GuiUtils.font14_un);
        // 是否支持正则
        addJCheckBox(fileNameContainsPanel, "支持正则", false, GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                fileNameSupportRegex = ((JCheckBox) event.getSource()).isSelected();
            }
        });
        advanceConditionPanel.add(fileNameContainsPanel);

        // 文件夹路径包含(不包含)字符
        JPanel folderPathContainsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJLabel(folderPathContainsPanel, " 文件夹路径包含: ", GuiUtils.font14_cn);
        folderPathContainsTextField = new JTextField(24);
        addJTextField(folderPathContainsPanel, folderPathContainsTextField, GuiUtils.font14_un);
        addJLabel(folderPathContainsPanel, " 文件夹路径不包含: ", GuiUtils.font14_cn);
        folderPathNotContainsTextField = new JTextField(24);
        addJTextField(folderPathContainsPanel, folderPathNotContainsTextField, GuiUtils.font14_un);
        // 是否支持正则
        addJCheckBox(folderPathContainsPanel, "支持正则", false, GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                folderPathSupportRegex = ((JCheckBox) event.getSource()).isSelected();
            }
        });
        advanceConditionPanel.add(folderPathContainsPanel);

        // 输出结果
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.add(new JScrollPane(resultTextArea));
        advanceAndResultPanel.add(resultPanel, BorderLayout.CENTER);
        add(advanceAndResultPanel, BorderLayout.CENTER);
    }

    /**
     * 输出文件属性及根据操作类型操作文件，返回值表示复制文件/剪切文件/删除文件/删除文件夹的数目，查找到文件夹总数，查找到文件总数，
     * 查找到隐藏文件夹总数，查找到隐藏文件总数.
     */
    private Integer[] printPropAndAction(String action, List<File> files) {
        int cntAction = 0;
        int cntFolders = 0;
        int cntFiles = 0;
        int cntFoldersHidden = 0;
        int cntFilesHidden = 0;
        long cntLength = 0;
        // 查找重复/同名文件，并删除重复文件只保留最新一个文件
        boolean deleteRepeat = (curType.equals(typeRepeatSearch) || curType.equals(typeSameNameSearch)) && action.equals(action_deleteRepeatFile);
        if (deleteRepeat) {
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return o2.lastModified() > o1.lastModified() ? 1 : -1;
                }
            });
        }
        // 文件重命名
        boolean renameFile = curType.equals(typeSearch) && curAction.equals(action_renameFile);
        Map<File, String> newNameMap = null;
        if (renameFile) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Map<String, Map<String, List<File>>> dirDateFiles = new HashMap<String, Map<String, List<File>>>();
            newNameMap = new HashMap<File, String>();
            for (File file : files) {
                if (!file.isDirectory()) {
                    String dir = file.getParentFile().getPath();
                    if (dirDateFiles.get(dir) == null) {
                        dirDateFiles.put(dir, new HashMap<String, List<File>>());
                    }
                    String date = dateFormat.format(new Date(file.lastModified()));
                    if (dirDateFiles.get(dir).get(date) == null) {
                        dirDateFiles.get(dir).put(date, new ArrayList<File>());
                    }
                    dirDateFiles.get(dir).get(date).add(file);
                }
            }
            for (Map.Entry<String, Map<String, List<File>>> entry : dirDateFiles.entrySet()) {
                for (Map.Entry<String, List<File>> fEntry : entry.getValue().entrySet()) {
                    List<File> fFiles = fEntry.getValue();
                    Collections.sort(fFiles, new Comparator<File>() {
                        @Override
                        public int compare(File o1, File o2) {
                            return o2.lastModified() < o1.lastModified() ? 1 : -1;
                        }
                    });
                    int fi = fFiles.size();
                    for (int i = 0; i < fi; i++) {
                        File file = fFiles.get(i);
                        int l = file.getName().lastIndexOf(".");
                        String fileType = l > 0 ? file.getName().substring(l) : "";
                        newNameMap.put(file, fEntry.getKey() + (fi == 1 ? "" : (" " + (i + 1))) + fileType);
                    }
                }
            }
        }
        int fi = 0;
        for (File file : files) {
            if (file.isDirectory()) {
                cntFolders++;
                if (file.isHidden()) {
                    cntFoldersHidden++;
                }
            } else {
                cntFiles++;
                cntLength += file.length();
                if (file.isHidden()) {
                    cntFilesHidden++;
                }
            }
            if (viewFullPathProp) {
                resultTextArea.append(file.getAbsolutePath());
            } else {
                resultTextArea.append(file.getName());
            }
            if (renameFile && !file.isDirectory()) {
                String newName = newNameMap.get(file);
                boolean rs = file.renameTo(new File(file.getParentFile().getPath() + "/" + newName));
                resultTextArea.append("   Rename: ");
                if (rs) {
                    cntAction++;
                    resultTextArea.append(newName);
                }
            }
            if (viewSizeCkProp && !file.isDirectory()) {
                resultTextArea.append("   Size: ");
                if (viewSizeCkPropUnit.equals(GuiUtils.FileSize_M)) {
                    resultTextArea.append(formatDouble3.format(file.length() / 1024.0 / 1024.0) + "M");
                } else if (viewSizeCkPropUnit.equals(GuiUtils.FileSize_KB)) {
                    resultTextArea.append(formatDouble3.format(file.length() / 1024.0) + "KB");
                } else if (viewSizeCkPropUnit.equals(GuiUtils.FileSize_Byte)) {
                    resultTextArea.append(file.length() + "Byte");
                } else if (viewSizeCkPropUnit.equals(GuiUtils.FileSize_G)) {
                    resultTextArea.append(formatDouble6.format(file.length() / 1024.0 / 1024.0 / 1024.0) + "G");
                }
            }
            if (viewModifyTimeProp) {
                resultTextArea.append("   ModifyTime: " + new SimpleDateFormat(FORMATTER_YYYYMMDDHHMMSS).format(new Date(file.lastModified())));
            }
            if (viewHiddenProp) {
                resultTextArea.append("   Hidden: " + (file.isHidden() ? "Y" : "N"));
            }
            // 删除重复文件只保留最新一个文件
            if (deleteRepeat && fi != 0) {
                file.delete();
                cntAction++;
                resultTextArea.append("   Deleted");
            }
            resultTextArea.append("\n");
            if (!action.equals(action_onlySearch)) {
                if (!file.isDirectory()) {
                    if (action.equals(action_copyFile)) {
                        // 复制文件
                        copyFile(file);
                        cntAction++;
                    } else if (action.equals(action_cutFile)) {
                        // 剪切文件，先复制文件
                        copyFile(file);
                        // 然后删除文件
                        file.delete();
                        cntAction++;
                    } else if (action.equals(action_deleteFile)) {
                        // 删除文件
                        file.delete();
                        cntAction++;
                    }
                } else if (action.equals(action_deleteBlankFolder) && file.listFiles() != null
                        && file.listFiles().length == 0) {
                    file.delete(); // 删除空文件夹
                    cntAction++;
                }
            }
            fi += 1;
        }
        return new Integer[]{cntAction, cntFolders, cntFiles, cntFoldersHidden, cntFilesHidden,
                (int) (cntLength / 1024 / 1024)};
    }

    /**
     * 复制文件.
     */
    private void copyFile(File srcFile) {
        String actionPath = actionTextField.getText().trim();
        try {
            org.apache.commons.io.FileUtils.copyFile(srcFile, new File(actionPath + "/" + srcFile.getName()));
        } catch (IOException e) {
            showExceptionMessage(e);
        }
    }
}
