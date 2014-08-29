package bs.util.tool.commongui.plugins;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import bs.util.io.PropertiesUtils;
import bs.util.tool.commongui.GuiJPanel;
import bs.util.tool.commongui.GuiUtils;

/**
 * 正则表达式验证.
 */
public class RegexTester extends GuiJPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * 正则表达式文本域.
	 */
	private JTextArea regexTextArea = createJTextArea(GuiUtils.font14_un);
	/**
	 * 验证字符文本域.
	 */
	private JTextArea sourceTextArea = createJTextArea(GuiUtils.font14_un);
	/**
	 * 匹配结果文本域.
	 */
	private JTextArea matchTextArea = createJTextArea(GuiUtils.font14_un);

	/**
	 * 帮助.
	 */
	private String helpText;

	/**
	 * 是否忽略大小写，默认否.
	 */
	private boolean ignoreCase = false;
	/**
	 * 是否显示详细匹配，默认否.
	 */
	private boolean viewDetail = false;
	/**
	 * 替换匹配字符文本域.
	 */
	private JTextField replaceTextField = new JTextField("");

	/**
	 * 常用正则表达式-名称.
	 */
	private String[] expNames = null;

	/**
	 * 常用正则表达式-表达式.
	 */
	private Map<String, String> expsMap = new HashMap<String, String>();

	{
		// 正则帮助
		String helpPropsFile = "conf/RegexTester/help.txt";
		try {
			InputStream helpInputStream = new FileInputStream(GuiUtils.getActualPath(helpPropsFile));
			int count = 0;
			while (count == 0) {
				count = helpInputStream.available();
			}
			byte[] bytes = new byte[count];
			helpInputStream.read(bytes);
			helpText = new String(bytes, "UTF-8");
		} catch (IOException e) {
			logLoadPropertiesException(helpPropsFile, e);
		}

		// 常用正则表达式
		String expressionPropsFile = "conf/RegexTester/expression.properties";
		try {
			Map<String, String> expressionsMap = PropertiesUtils.getPropertiesMap(GuiUtils
					.getActualPath(expressionPropsFile));
			List<String> expsSortAndNames = new ArrayList<String>();
			for (String key : expressionsMap.keySet()) {
				expsSortAndNames.add(key);
			}
			Collections.sort(expsSortAndNames); // 排序

			int expSize = expsSortAndNames.size() + 1;
			expNames = new String[expSize];
			expNames[0] = "常用正则表达式";

			// 常用正则表达式，按序号正排序
			for (int i = 1; i < expSize; i++) {
				String key = expsSortAndNames.get(i - 1);
				String[] keySplit = key.split("_");
				expNames[i] = keySplit.length > 1 ? key.substring(keySplit[0].length() + 1) : keySplit[0];
				expsMap.put(expNames[i], expressionsMap.get(key));
			}
		} catch (IOException e) {
			logLoadPropertiesException(expressionPropsFile, e);
		}
	}

	public RegexTester() {

		// 主面板：边界布局，只有Center部分
		setLayout(new BorderLayout());
		// Center，加密解密输入输出域，使用3行1列的Grid布局，使其平均显示
		JPanel textAreaPanel = new JPanel(new GridLayout(3, 1));
		add(textAreaPanel, BorderLayout.CENTER);

		JPanel regexPanel = new JPanel(new BorderLayout());
		addJLabel(regexPanel, " 匹配正则: ", GuiUtils.font14b_cn, BorderLayout.WEST);
		regexPanel.add(new JScrollPane(regexTextArea), BorderLayout.CENTER);
		textAreaPanel.add(regexPanel);

		JPanel sourcePanel = new JPanel(new BorderLayout());
		addJLabel(sourcePanel, " 匹配文本: ", GuiUtils.font14b_cn, BorderLayout.WEST);
		sourcePanel.add(new JScrollPane(sourceTextArea), BorderLayout.CENTER);
		textAreaPanel.add(sourcePanel);

		JPanel matchPanel = new JPanel(new BorderLayout());
		addJLabel(matchPanel, " 匹配结果: ", GuiUtils.font14b_cn, BorderLayout.WEST);
		matchPanel.add(new JScrollPane(matchTextArea), BorderLayout.CENTER);
		textAreaPanel.add(matchPanel);

		// East，操作区域，使用BorderLayout布局
		JPanel actionPanel = new JPanel(new BorderLayout());
		add(actionPanel, BorderLayout.EAST);
		// 放置多选框等
		JPanel actionGridPanel = new JPanel(new GridLayout(10, 1));
		actionPanel.add(actionGridPanel, BorderLayout.NORTH);

		// 放置帮助按钮
		JPanel helpButtonPanel = new JPanel(new GridLayout(1, 2));
		helpButtonPanel.add(new Panel());// 仅作填充
		addJButton(helpButtonPanel, "帮助", "", GuiUtils.font13_cn, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				showTextAreaMessage(helpText, "帮助", JOptionPane.INFORMATION_MESSAGE, null, null);
			}
		});
		actionGridPanel.add(helpButtonPanel);

		if (expNames != null && expNames.length != 0) {
			// 仅作填充
			actionGridPanel.add(new JLabel(""));

			// 常用正则表达式下拉框
			JPanel expressionsBoxPanel = new JPanel(new BorderLayout());
			addJLabel(expressionsBoxPanel, "  ", GuiUtils.font14_cn, BorderLayout.WEST);
			addJComboBox(expressionsBoxPanel, expNames, GuiUtils.font13_cn, new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					String exp = expsMap.get(((JComboBox) event.getSource()).getSelectedItem().toString());
					if (exp != null) {
						regexTextArea.setText(exp);
					}
				}
			}, BorderLayout.CENTER);
			actionGridPanel.add(expressionsBoxPanel);
		}

		// 仅作填充
		actionGridPanel.add(new JLabel(""));
		
		// 是否忽略大小写
		JPanel ignoreCasePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		addJCheckBox(ignoreCasePanel, "忽略大小写", false, GuiUtils.font14_cn, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JCheckBox checkBox = (JCheckBox) event.getSource();
				ignoreCase = checkBox.isSelected();
			}
		});
		actionGridPanel.add(ignoreCasePanel);
		// 是否显示详细匹配
		JPanel viewDetailPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		addJCheckBox(viewDetailPanel, "显示详细匹配", false, GuiUtils.font14_cn, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JCheckBox checkBox = (JCheckBox) event.getSource();
				viewDetail = checkBox.isSelected();
			}
		});
		actionGridPanel.add(viewDetailPanel);

		// 仅作填充
		actionGridPanel.add(new JLabel(""));

		// 替换匹配
		JPanel replaceLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		addJLabel(replaceLabelPanel, " 替换匹配: ", GuiUtils.font14_cn);
		actionGridPanel.add(replaceLabelPanel);
		JPanel replaceTextPanel = new JPanel(new BorderLayout());
		addJLabel(replaceTextPanel, "  ", GuiUtils.font14_cn, BorderLayout.WEST);
		addJTextField(replaceTextPanel, replaceTextField, GuiUtils.font14_un, BorderLayout.CENTER);
		actionGridPanel.add(replaceTextPanel);

		// 仅作填充
		actionPanel.add(new Panel(), BorderLayout.CENTER);

		// 放置匹配按钮
		JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
		actionPanel.add(buttonPanel, BorderLayout.SOUTH);
		// 加密按钮
		addJButton(buttonPanel, "匹配", "", GuiUtils.font14b_cn, new MouseListener() {
			public void mouseReleased(MouseEvent event) {
				String regexText = regexTextArea.getText().trim();
				String sourceText = sourceTextArea.getText().trim();
				String replaceText = replaceTextField.getText();
				Pattern p = null;
				if (ignoreCase) {
					p = Pattern.compile(regexText, Pattern.CASE_INSENSITIVE); // 不区分大小写
				} else {
					p = Pattern.compile(regexText);
				}
				// 用Pattern类的matcher()方法生成一个Matcher对象
				Matcher m = p.matcher(sourceText);
				StringBuffer sb = new StringBuffer();
				StringBuffer rsb = new StringBuffer(); // 替换匹配
				// 使用find()方法查找第一个匹配的对象
				boolean result = m.find();
				// 使用循环找出模式匹配的内容替换之,再将内容加到sb里
				int cnt = 0; // 匹配总数
				int start = 0;
				int end = 0;
				while (result) {
					m.appendReplacement(rsb, replaceText); // 替换匹配
					cnt++;
					sb.append("\n");
					start = m.start();
					end = m.end();
					String matchText = sourceText.substring(start, end);
					if (viewDetail) {
						sb.append("Match[").append(cnt).append("]: ");
					}
					sb.append(matchText);
					if (viewDetail) {
						sb.append(" [start: ").append(start).append(", end: ").append(end).append("]");
					}
					result = m.find();
				}
				sb.append("\n\n匹配总数: " + cnt);
				if (replaceText.length() != 0) {
					m.appendTail(rsb);
					sb.append("\n\n替换匹配: ").append(rsb);
				}
				matchTextArea.setText(sb.length() > 0 ? sb.substring(1) : "");
			}

			public void mousePressed(MouseEvent e) {
				matchTextArea.setText("");
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
			}
		});
	}

}
