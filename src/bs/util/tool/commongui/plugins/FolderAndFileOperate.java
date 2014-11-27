package bs.util.tool.commongui.plugins;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import bs.util.io.PropertiesUtils;
import bs.util.tool.commongui.GuiJPanel;
import bs.util.tool.commongui.GuiUtils;
import bs.util.tool.commongui.utils.CopyFileFromCommonsIo;
import bs.util.tool.commongui.utils.FileUtils;

/**
 * 文件(夹)操作.
 */
public class FolderAndFileOperate extends GuiJPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * 路径表单.
	 */
	private JTextField pathTextField = new JTextField();
	/**
	 * 目录选择.
	 */
	private JFileChooser search_fileChooser = new JFileChooser();

	/**
	 * 查找类型-文件(夹)查找.
	 */
	private final String type_search = "文件(夹)查找";
	/**
	 * 查找类型-重复文件查找.
	 */
	private final String type_repeatSearch = "重复文件查找";
	/**
	 * 查找类型-同名文件查找.
	 */
	private final String type_sameNameSearch = "同名文件查找";
	/**
	 * 查找类型-空文件(夹)查找.
	 */
	private final String type_blankSearch = "空文件(夹)查找";
	/**
	 * 查找类型.
	 */
	private final String[] types = new String[] { type_search, type_repeatSearch, type_sameNameSearch, type_blankSearch };
	/**
	 * 当前查找类型.
	 */
	private String curType = types[0];

	/**
	 * 操作类型-默认查找.
	 */
	private final String action_onlySearch = "默认查找";
	/**
	 * 操作类型-复制文件.
	 */
	private final String action_copyFile = "复制文件";
	/**
	 * 操作类型-剪切文件.
	 */
	private final String action_cutFile = "剪切文件";
	/**
	 * 操作类型-删除文件.
	 */
	private final String action_deleteFile = "删除文件";
	/**
	 * 操作类型-删除空文件夹.
	 */
	private final String action_deleteBlankFolder = "删除空文件夹";
	/**
	 * 操作类型.
	 */
	private final String[] actions = new String[] { action_onlySearch, action_copyFile, action_cutFile,
			action_deleteFile, action_deleteBlankFolder };
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
	private JFileChooser action_fileChooser = new JFileChooser();
	/**
	 * 操作(复制、剪切文件存放位置)目录浏览按钮.
	 */
	private JButton action_chooseButton = createJButton("浏览", "", GuiUtils.font12_cn);

	/**
	 * 常见文件类型.
	 */
	private Map<String, String> fileTypesMap = new HashMap<String, String>();
	/**
	 * 常见文件类型名称.
	 */
	private String[] fileTypeNames;
	/**
	 * 当前类型.
	 */
	private String curFileType = "";

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
			Collections.sort(propsSortAndNames); // 排序

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
				buttonBrowseListener(search_fileChooser, pathTextField));

		// Search按钮
		addJButton(buttonFlowPanel, "查找", "", GuiUtils.font14b_cn, new MouseListener() {
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
				Map<String, Object> paramsMap = new HashMap<String, Object>();
				if (curType.equals(type_repeatSearch)) {
					paramsMap.put("type_repeatSearch", true);
					FileUtils.sameSizeFilesMap = new HashMap<String, List<File>>();
					FileUtils.repeatFilesMap = new HashMap<String, List<File>>();
					FileUtils.repeatFilesProp = new LinkedHashSet<String>();
				} else if (curType.equals(type_sameNameSearch)) {
					paramsMap.put("type_sameNameSearch", true);
					FileUtils.sameNameFilesMap = new HashMap<String, List<File>>();
					FileUtils.sameNameFilesProp = new LinkedHashSet<String>();
				} else if (curType.equals(type_blankSearch)) {
					paramsMap.put("type_blankSearch", true);
				}
				Long curTime = new Date().getTime();
				paramsMap.put("searchFileType", curFileType);
				paramsMap.put("containsFile", containsFile);
				paramsMap.put("containsFolder", containsFolder);
				paramsMap.put("containsHidden", containsHidden);
				paramsMap.put("containsNotHidden", containsNotHidden);
				paramsMap.put("fileSizeFrom", GuiUtils.getCountFileSizeUnit(fileSizeFromTextField.getText().trim(),
						fileSizeUnitFromBox.getSelectedItem().toString()));
				paramsMap.put("fileSizeTo", GuiUtils.getCountFileSizeUnit(fileSizeToTextField.getText().trim(),
						fileSizeUnitToBox.getSelectedItem().toString()));
				paramsMap.put("modifyTimeFrom",
						getLongFormatTime(modifyTimeFromTextField.getText().trim(), format_yyyyMMddHHmmss));
				paramsMap.put("modifyTimeTo",
						getLongFormatTime(modifyTimeToTextField.getText().trim(), format_yyyyMMddHHmmss));
				paramsMap.put("filePathContainsText", filePathContainsTextField.getText().trim());
				paramsMap.put("filePathNotContainsText", filePathNotContainsTextField.getText().trim());
				paramsMap.put("filePathSupportRegex", filePathSupportRegex);
				paramsMap.put("fileNameContainsText", fileNameContainsTextField.getText().trim());
				paramsMap.put("fileNameNotContainsText", fileNameNotContainsTextField.getText().trim());
				paramsMap.put("fileNameSupportRegex", fileNameSupportRegex);
				paramsMap.put("folderPathContainsText", folderPathContainsTextField.getText().trim());
				paramsMap.put("folderPathNotContainsText", folderPathNotContainsTextField.getText().trim());
				paramsMap.put("folderPathSupportRegex", folderPathSupportRegex);
				// 最终Search到的File，当查找类型为'重复文件查找'，files最后长度为0，结果保存在FileUtils.repeatFilesProp及FileUtils.repeatFilesMap中
				List<File> files = FileUtils.getAllSubFiles(path, paramsMap);
				int cnt_action = 0;
				if (curType.equals(type_repeatSearch)) {
					List<File> repeatFiles = null;
					long cnt = 0;
					int groupCnt = FileUtils.repeatFilesProp.size();
					int size_cnt = 0;
					int f = 0;
					resultTextArea
							.append("查找方法：取出有相同大小的所有文件，比较文件大小以及前2048Byte的内容的MD5值是否一样，\n                 如果两者相同，则认为重复，否则认为不重复，有较小的误差率。\n\n\n");
					for (String prop : FileUtils.repeatFilesProp) {
						resultTextArea.append("第" + (++f) + "组：\n");
						repeatFiles = FileUtils.repeatFilesMap.get(prop);
						cnt += repeatFiles.size();
						Integer[] cntArr = printPropAndAction(curAction, repeatFiles);
						cnt_action += cntArr[0];
						size_cnt += cntArr[5];
					}
					resultTextArea.append("\n\nCount repeat group: " + groupCnt + ", files: " + cnt + ", Size: "
							+ size_cnt + "M");
					FileUtils.repeatFilesMap.clear();
					FileUtils.repeatFilesMap = null;
					FileUtils.repeatFilesProp.clear();
					FileUtils.repeatFilesProp = null;
				} else if (curType.equals(type_sameNameSearch)) {
					List<File> sameNameFiles = null;
					long cnt = 0;
					int groupCnt = FileUtils.sameNameFilesProp.size();
					int size_cnt = 0;
					int f = 0;
					resultTextArea.append("查找方法：比较文件名，不比较后缀名，查找相同文件名称的文件。\n\n\n");
					for (String prop : FileUtils.sameNameFilesProp) {
						resultTextArea.append("第" + (++f) + "组：\n");
						sameNameFiles = FileUtils.sameNameFilesMap.get(prop);
						cnt += sameNameFiles.size();
						Integer[] cntArr = printPropAndAction(curAction, sameNameFiles);
						cnt_action += cntArr[0];
						size_cnt += cntArr[5];
					}
					resultTextArea.append("\n\nCount same name group: " + groupCnt + ", files: " + cnt + ", Size: "
							+ size_cnt + "M");
					FileUtils.sameNameFilesMap.clear();
					FileUtils.sameNameFilesMap = null;
					FileUtils.sameNameFilesProp.clear();
					FileUtils.sameNameFilesProp = null;
				} else {
					Integer[] cntArr = printPropAndAction(curAction, files);
					cnt_action += cntArr[0];
					resultTextArea.append("\n\nCount: " + files.size() + ", Size: " + cntArr[5] + "M, folders: "
							+ cntArr[1] + ", files: " + cntArr[2] + ", hidden folders: " + cntArr[3]
							+ ", hidden files: " + cntArr[4]);
				}
				files.clear();
				files = null;
				if (curAction.equals(action_copyFile)) {
					resultTextArea.append(", Count Copy files: " + cnt_action);
				} else if (curAction.equals(action_cutFile)) {
					resultTextArea.append(", Count Cut files: " + cnt_action);
				} else if (curAction.equals(action_deleteFile)) {
					resultTextArea.append(", Count Delete files: " + cnt_action);
				} else if (curAction.equals(action_deleteBlankFolder)) {
					resultTextArea.append(", Count Delete blank folders: " + cnt_action);
				}
				resultTextArea.append(". Cost time:" + (new Date().getTime() - curTime) / 1000.0 + "s.");
				System.gc();
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
		// 路径选择控件
		search_fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 仅可选择文件夹
		fileChooPanel.add(buttonFlowPanel, BorderLayout.EAST);
		inputPanel.add(fileChooPanel);

		// 条件
		JPanel conditionPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		// 查找类型
		addJLabel(conditionPanel, " 查找类型: ", GuiUtils.font14_cn);
		addJComboBox(conditionPanel, types, GuiUtils.font13_cn, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				curType = ((JComboBox) event.getSource()).getSelectedItem().toString();
				if (curType.equals(type_blankSearch)) {
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
			}
		});

		addJLabel(conditionPanel, "   文件类型: ", GuiUtils.font14_cn);

		// 常见文件类型下拉框
		addJComboBox(conditionPanel, fileTypeNames, GuiUtils.font13_cn, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				curFileType = fileTypesMap.get(((JComboBox) event.getSource()).getSelectedItem().toString());
			}
		});

		addJLabel(conditionPanel, " 包括:", GuiUtils.font14_cn);
		// 是否包括文件JCheckBox
		addJCheckBox(conditionPanel, "文件", true, GuiUtils.font14_cn, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JCheckBox checkBox = (JCheckBox) event.getSource();
				containsFile = checkBox.isSelected();
			}
		});
		// 是否包括文件夹JCheckBox
		addJCheckBox(conditionPanel, "文件夹", true, GuiUtils.font14_cn, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JCheckBox checkBox = (JCheckBox) event.getSource();
				containsFolder = checkBox.isSelected();
			}
		});
		// 是否包括隐藏文件(夹)JCheckBox
		addJCheckBox(conditionPanel, "隐藏文件(夹)", true, GuiUtils.font14_cn, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JCheckBox checkBox = (JCheckBox) event.getSource();
				containsHidden = checkBox.isSelected();
			}
		});
		// 是否包括隐藏文件(夹)JCheckBox
		addJCheckBox(conditionPanel, "非隐藏文件(夹)", true, GuiUtils.font14_cn, new ActionListener() {
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
			public void actionPerformed(ActionEvent event) {
				curAction = ((JComboBox) event.getSource()).getSelectedItem().toString();
				if (!curAction.equals(action_onlySearch)) {
					// 将焦点转移到下一个组件，就好像此 Component 曾是焦点所有者
					((JComboBox) event.getSource()).transferFocus();
					showMessage("非查找操作务必确认不会对原文件(夹)造成意外损害！", "警告", JOptionPane.WARNING_MESSAGE);
				}
				if (curAction.equals(action_copyFile) || curAction.equals(action_cutFile)) {
					actionTextField.setEnabled(true);
					action_chooseButton.setEnabled(true);
				} else {
					actionTextField.setText("");
					actionTextField.setEnabled(false);
					action_chooseButton.setEnabled(false);
				}
			}
		});
		addJTextField(viewPropAndActionPanel, actionTextField, GuiUtils.font14_un);
		actionTextField.setEnabled(false);
		// 路径选择控件
		action_fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 仅可选择文件夹
		action_chooseButton.addActionListener(buttonBrowseListener(action_fileChooser, actionTextField));
		action_chooseButton.setEnabled(false);
		viewPropAndActionPanel.add(action_chooseButton);

		addJLabel(viewPropAndActionPanel, " 显示:", GuiUtils.font14_cn);
		// 是否显示文件(夹)完整路径
		addJCheckBox(viewPropAndActionPanel, "完整路径", true, GuiUtils.font14_cn, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JCheckBox checkBox = (JCheckBox) event.getSource();
				viewFullPathProp = checkBox.isSelected();
			}
		});
		// 是否显示文件大小JCheckBox
		addJCheckBox(viewPropAndActionPanel, "大小", true, GuiUtils.font14_cn, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JCheckBox checkBox = (JCheckBox) event.getSource();
				viewSizeCkProp = checkBox.isSelected();
			}
		});
		JComboBox viewSizeCkPropUnitBox = createFileSizeUnitBox(GuiUtils.font12_cn);
		viewSizeCkPropUnitBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				viewSizeCkPropUnit = ((JComboBox) event.getSource()).getSelectedItem().toString();
			}
		});
		viewPropAndActionPanel.add(viewSizeCkPropUnitBox);
		// 是否显示修改时间
		addJCheckBox(viewPropAndActionPanel, "修改时间", false, GuiUtils.font14_cn, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JCheckBox checkBox = (JCheckBox) event.getSource();
				viewModifyTimeProp = checkBox.isSelected();
			}
		});
		// 是否显示文件大小JCheckBox
		addJCheckBox(viewPropAndActionPanel, "隐藏属性", false, GuiUtils.font14_cn, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JCheckBox checkBox = (JCheckBox) event.getSource();
				viewHiddenProp = checkBox.isSelected();
			}
		});
		// 展开/收缩高级(条件)按钮
		addJButton(viewPropAndActionPanel, "高级", "", GuiUtils.font12_cn, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				advanceConditionPanel.setVisible(!advanceConditionPanel.isVisible());
				getContextPanel().revalidate();
			}
		});
		inputPanel.add(viewPropAndActionPanel);

		// 高级(条件)及输出面板，使用边界布局，North为高级(条件)，Center为输出
		JPanel advanceAndResultPanel = new JPanel(new BorderLayout());
		// 高级(条件
		advanceConditionPanel = new JPanel(new GridLayout(4, 1));
		advanceConditionPanel.setVisible(false);
		advanceAndResultPanel.add(advanceConditionPanel, BorderLayout.NORTH);
		// 大小条件、时间条件
		JPanel fileSizeTimePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		addJLabel(fileSizeTimePanel, " 文件大小: ", GuiUtils.font14_cn);
		fileSizeTimePanel.add(fileSizeFromTextField);
		fileSizeTimePanel.add(fileSizeUnitFromBox);
		addJLabel(fileSizeTimePanel, "-", GuiUtils.font14_cn);
		fileSizeTimePanel.add(fileSizeToTextField);
		fileSizeTimePanel.add(fileSizeUnitToBox);
		addJLabel(fileSizeTimePanel, "   修改时间: ", GuiUtils.font14_cn);
		fileSizeTimePanel.add(modifyTimeFromTextField);
		addJLabel(fileSizeTimePanel, "--", GuiUtils.font14_cn);
		fileSizeTimePanel.add(modifyTimeToTextField);
		advanceConditionPanel.add(fileSizeTimePanel);

		// 文件(夹)路径包含(不包含)字符
		JPanel filePathContainsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		addJLabel(filePathContainsPanel, " 文件/夹路径包含:", GuiUtils.font14_cn);
		filePathContainsTextField = new JTextField(24);
		addJTextField(filePathContainsPanel, filePathContainsTextField, GuiUtils.font14_un);
		addJLabel(filePathContainsPanel, "  文件/夹路径不包含:", GuiUtils.font14_cn);
		filePathNotContainsTextField = new JTextField(24);
		addJTextField(filePathContainsPanel, filePathNotContainsTextField, GuiUtils.font14_un);
		// 是否支持正则
		addJCheckBox(filePathContainsPanel, "支持正则", false, GuiUtils.font14_cn, new ActionListener() {
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
		addJLabel(fileNameContainsPanel, "  文件名不包含字符: ", GuiUtils.font14_cn);
		fileNameNotContainsTextField = new JTextField(24);
		addJTextField(fileNameContainsPanel, fileNameNotContainsTextField, GuiUtils.font14_un);
		// 是否支持正则
		addJCheckBox(fileNameContainsPanel, "支持正则", false, GuiUtils.font14_cn, new ActionListener() {
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
		addJLabel(folderPathContainsPanel, "  文件夹路径不包含: ", GuiUtils.font14_cn);
		folderPathNotContainsTextField = new JTextField(24);
		addJTextField(folderPathContainsPanel, folderPathNotContainsTextField, GuiUtils.font14_un);
		// 是否支持正则
		addJCheckBox(folderPathContainsPanel, "支持正则", false, GuiUtils.font14_cn, new ActionListener() {
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
		int cnt_action = 0;
		int cnt_folders = 0;
		int cnt_files = 0;
		int cnt_folders_hidden = 0;
		int cnt_files_hidden = 0;
		long cnt_length = 0;
		for (File file : files) {
			if (file.isDirectory()) {
				cnt_folders++;
				if (file.isHidden()) {
					cnt_folders_hidden++;
				}
			} else {
				cnt_files++;
				cnt_length += file.length();
				if (file.isHidden()) {
					cnt_files_hidden++;
				}
			}
			if (viewFullPathProp) {
				resultTextArea.append(file.getAbsolutePath());
			} else {
				resultTextArea.append(file.getName());
			}
			if (viewSizeCkProp && !file.isDirectory()) {
				resultTextArea.append("   Size: ");
				if (viewSizeCkPropUnit.equals(GuiUtils.FileSize_M)) {
					resultTextArea.append(format_double_3.format(file.length() / 1024.0 / 1024.0) + "M");
				} else if (viewSizeCkPropUnit.equals(GuiUtils.FileSize_KB)) {
					resultTextArea.append(format_double_3.format(file.length() / 1024.0) + "KB");
				} else if (viewSizeCkPropUnit.equals(GuiUtils.FileSize_Byte)) {
					resultTextArea.append(file.length() + "Byte");
				} else if (viewSizeCkPropUnit.equals(GuiUtils.FileSize_G)) {
					resultTextArea.append(format_double_6.format(file.length() / 1024.0 / 1024.0 / 1024.0) + "G");
				}
			}
			if (viewModifyTimeProp) {
				resultTextArea.append("   ModifyTime: " + format_yyyyMMddHHmmss.format(new Date(file.lastModified())));
			}
			if (viewHiddenProp) {
				resultTextArea.append("   Hidden: " + (file.isHidden() ? "Y" : "N"));
			}
			resultTextArea.append("\n");
			if (!curAction.equals(action_onlySearch)) {
				if (!file.isDirectory()) {
					if (curAction.equals(action_copyFile)) {
						// 复制文件
						copyFile(file);
						cnt_action++;
					} else if (curAction.equals(action_cutFile)) {
						// 剪切文件
						copyFile(file); // 先复制文件
						file.delete(); // 然后删除文件
						cnt_action++;
					} else if (curAction.equals(action_deleteFile)) {
						file.delete(); // 删除文件
						cnt_action++;
					}
				} else if (curAction.equals(action_deleteBlankFolder) && file.listFiles() != null
						&& file.listFiles().length == 0) {
					file.delete(); // 删除空文件夹
					cnt_action++;
				}
			}
		}
		return new Integer[] { cnt_action, cnt_folders, cnt_files, cnt_folders_hidden, cnt_files_hidden,
				(int) (cnt_length / 1024 / 1024) };
	}

	/**
	 * 复制文件.
	 */
	private void copyFile(File srcFile) {
		String actionPath = actionTextField.getText().trim();
		try {
			CopyFileFromCommonsIo.copyFile(srcFile, new File(actionPath + "/" + srcFile.getName()));
		} catch (IOException e) {
			showExceptionMessage(e);
		}
	}
}
