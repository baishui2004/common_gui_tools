package bs.util.tool.commongui.plugins;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import bs.util.io.PropertiesUtils;
import bs.util.tool.commongui.GuiJPanel;
import bs.util.tool.commongui.GuiUtils;
import bs.util.tool.commongui.utils.FileUtils;

/**
 * Java类查找.
 */
public class ClassFinder extends GuiJPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * 查找类表单.
	 */
	private JTextField searchClassTextField = new JTextField();

	/**
	 * 查找文件/文件夹路径表单.
	 */
	private JTextField searchPathTextField = new JTextField();
	/**
	 * 查找文件/文件夹路径选择.
	 */
	private JFileChooser searchPath_Chooser = new JFileChooser();

	/**
	 * 查找按钮.
	 */
	private JButton searchButton;

	/**
	 * 查找结果文本域.
	 */
	private JTextArea resultTextArea = createJTextArea(GuiUtils.font14_un);

	/**
	 * 查找的直接文件类型.
	 */
	private String searchFileTypes = "";
	/**
	 * 查找的压缩文件类型.
	 */
	private String compressFileTypes = "";

	{
		String confPropsFile = "conf/ClassFinder/conf.properties";
		try {
			Properties confProperties = PropertiesUtils.getProperties(GuiUtils.getActualPath(confPropsFile));
			searchFileTypes = confProperties.getProperty("SearchFileTypes").trim();
			compressFileTypes = confProperties.getProperty("CompressFileTypes").trim();
		} catch (IOException e) {
			logLoadPropertiesException(confPropsFile, e);
		}
	}

	public ClassFinder() {

		// 主面板：边界布局，分North、Center两部分，North用于放置输入及条件控件，Center是放置查找结果输出
		setLayout(new BorderLayout());

		// 输入及条件Panel
		JPanel inputPanel = new JPanel(new GridLayout(2, 1));

		// Grid第一行条件表单
		JPanel firstLineGridPanel = new JPanel(new BorderLayout());
		inputPanel.add(firstLineGridPanel);
		addJLabel(firstLineGridPanel, "  查找类名: ", GuiUtils.font14_cn, BorderLayout.WEST);
		// 查找类表单
		JPanel classInputPanel = new JPanel(new BorderLayout());
		classInputPanel.add(new JPanel(), BorderLayout.NORTH);
		addJTextField(classInputPanel, searchClassTextField, GuiUtils.font14_un, BorderLayout.CENTER);
		classInputPanel.add(new JPanel(), BorderLayout.SOUTH);
		firstLineGridPanel.add(classInputPanel, BorderLayout.CENTER);

		addJLabel(firstLineGridPanel, " 支持正则且忽略大小写 ", GuiUtils.font14_cn, BorderLayout.EAST);

		// Grid第一行条件表单
		JPanel secondLineGridPanel = new JPanel(new BorderLayout());
		inputPanel.add(secondLineGridPanel);
		addJLabel(secondLineGridPanel, "  文件(夹): ", GuiUtils.font14_cn, BorderLayout.WEST);
		// 查找文件/文件夹表单
		JPanel fileChooPanel = new JPanel(new BorderLayout());
		fileChooPanel.add(new JPanel(), BorderLayout.NORTH);
		addJTextField(fileChooPanel, searchPathTextField, GuiUtils.font14_un, BorderLayout.CENTER);
		fileChooPanel.add(new JPanel(), BorderLayout.SOUTH);
		secondLineGridPanel.add(fileChooPanel, BorderLayout.CENTER);

		JPanel secondFlowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		addJButton(secondFlowPanel, "浏览", "", GuiUtils.font12_cn,
				buttonBrowseListener(searchPath_Chooser, searchPathTextField));
		addJLabel(secondFlowPanel, " ", GuiUtils.font14_cn);
		// 查找按钮
		searchButton = createJButton("查找", "", GuiUtils.font14b_cn);
		searchButton.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent event) {
				String className = searchClassTextField.getText().trim().toLowerCase();
				if (className.length() == 0) {
					showMessage("请输入类名！", "警告", JOptionPane.WARNING_MESSAGE);
					return;
				}
				String actualSearchFileTypes = searchFileTypes;
				String searchFileType = getSearchFileType(className, searchFileTypes);
				if (searchFileType != null) {
					actualSearchFileTypes = searchFileType;
					className = className.substring(0, className.length() - ("." + searchFileType).length());
				}

				String path = searchPathTextField.getText().trim();
				File file = new File(path);
				if (!file.exists()) {
					showMessage("查找文件(夹)不存在！", "警告", JOptionPane.WARNING_MESSAGE);
					return;
				}
				searchButton.setEnabled(false);

				resultTextArea.setText("");
				try {
					List<File> files = new ArrayList<File>();

					if (!file.isDirectory()) {
						files.add(file);
					} else {
						// keys @see Class: bs.util.tool.commongui.utils.SearchFileAndFolderNamePathParams
						Map<String, Object> paramsMap = new HashMap<String, Object>();
						paramsMap.put("containsFolder", false);
						paramsMap.put("filePathContainsText",
								"(" + className + "\\.(" + actualSearchFileTypes.replace(",", "|") + ")$)" + "|"
										+ "(\\.(" + compressFileTypes.replace(",", "|") + ")$)");
						paramsMap.put("filePathSupportRegex", true);
						files = FileUtils.getAllSubFiles(path, paramsMap);
					}

					Pattern cnPattern = Pattern.compile(className, Pattern.CASE_INSENSITIVE);
					int cnt = 0;
					for (File fFile : files) {
						String fileName = fFile.getName();
						String compressFileType = getCompressFileTypes(fileName);
						if (compressFileType == null) {
							cnt++;
							resultTextArea.append(fFile.getAbsolutePath() + "\n");
						} else {
							cnt += findZipEntries(fFile, actualSearchFileTypes, cnPattern);
						}
					}
					resultTextArea.append("\nFind files: " + cnt + ".");
				} catch (Exception e) {
					showExceptionMessage(e);
				}
				searchButton.setEnabled(true);
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
		secondFlowPanel.add(searchButton);
		// 查找路径选择控件
		searchPath_Chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // 可选择文件/文件夹
		searchPath_Chooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return compressFileTypes + ",文件夹";
			}

			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				}
				String fileType = FileUtils.getFileType(file.getName());
				if (("," + compressFileTypes + ",").contains("," + fileType + ",")) {
					return true;
				}
				return false;
			}
		});
		addJLabel(secondFlowPanel, "  ", GuiUtils.font14_cn);
		secondLineGridPanel.add(secondFlowPanel, BorderLayout.EAST);
		inputPanel.add(secondLineGridPanel);

		add(inputPanel, BorderLayout.NORTH);

		// 查找结果输出
		JPanel searchLogPanel = new JPanel(new BorderLayout());
		searchLogPanel.add(new JScrollPane(resultTextArea));
		add(searchLogPanel, BorderLayout.CENTER);
	}

	/**
	 * 获取文件类型，如果不在既定的“查找的直接文件类型”中则返回null.
	 */
	private String getSearchFileType(String className, String fileTypes) {
		if (className.contains(".") && !className.endsWith(".")) {
			String type = className.substring(className.lastIndexOf(".") + 1);
			if (("." + fileTypes).contains(type)) {
				return type;
			}
		}
		return null;
	}

	/**
	 * 获取文件类型，如果不在既定的"查找的压缩文件类型"中则返回null.
	 */
	private String getCompressFileTypes(String fileName) {
		if (fileName.contains(".") && !fileName.endsWith(".")) {
			String type = fileName.substring(fileName.lastIndexOf(".") + 1);
			if (("." + compressFileTypes).contains(type)) {
				return type;
			}
		}
		return null;
	}

	/**
	 * 遍历zip文件中的文件.
	 */
	private int findZipEntries(File file, String fileTypes, Pattern cnPattern) {
		ZipFile zipFile = null;
		int cnt = 0;
		try {
			zipFile = new ZipFile(file);
			@SuppressWarnings("unchecked")
			Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) entries.nextElement();
				String entryName = zipEntry.getName();
				if (getSearchFileType(entryName, fileTypes) != null && cnPattern.matcher(entryName).find()) {
					cnt++;
					resultTextArea.append(file.getAbsolutePath() + " -> " + entryName + "\n");
				}
			}
		} catch (IOException e) {
			GuiUtils.log(e);
		} finally {
			try {
				if (zipFile != null) {
					zipFile.close();
				}
			} catch (IOException e) {
				GuiUtils.log(e);
			}
		}
		return cnt;
	}
}
