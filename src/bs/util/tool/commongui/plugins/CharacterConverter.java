package bs.util.tool.commongui.plugins;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bs.util.common.ExCodec;
import bs.util.tool.commongui.GuiJPanel;
import bs.util.tool.commongui.GuiUtils;
import bs.util.tool.commongui.utils.RadixUtils;

/**
 * 编码转换.
 */
public class CharacterConverter extends GuiJPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Encode TextField.
	 */
	private JTextField encodeTextField = new JTextField("");
	/**
	 * Encode JButton.
	 */
	private JButton encodeButton = createJButton("Encode", Integer.toString(0), GuiUtils.font16);

	/**
	 * 2进制.
	 */
	private String codeType_2Radix = "二进制";
	/**
	 * 8进制.
	 */
	private String codeType_8Radix = "八进制";
	/**
	 * 10进制.
	 */
	private String codeType_10Radix = "十进制";
	/**
	 * 16进制.
	 */
	private String codeType_16Radix = "十六进制";

	/**
	 * 乱码解码.
	 */
	private String codeType_Decode = "乱码解码";

	/**
	 * 编解码类别.
	 */
	private String[] codeTypes = new String[] { codeType_2Radix, codeType_8Radix, codeType_10Radix, codeType_16Radix,
			codeType_Decode };

	/**
	 * 当前编码类别.
	 */
	private String curCodeType = codeType_16Radix;

	/**
	 * 进制前缀符.
	 */
	private String[] prefixs = new String[] { "空格", "空", "-", "%", "\\u" };

	/**
	 * 进制前缀符下拉框.
	 */
	private JComboBox prefixsBox = createJComboBox(prefixs, GuiUtils.font14_cn, new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			curPrefix = getCorrectSeparator(((JComboBox) event.getSource()).getSelectedItem().toString());
		}
	});

	/**
	 * 当前进制前缀符.
	 */
	private String curPrefix = getCorrectSeparator(prefixs[0]);

	/**
	 * 获取正确的前缀符.
	 */
	private String getCorrectSeparator(String separ) {
		if (prefixs[0].equals(separ)) {
			return " ";
		} else if (prefixs[1].equals(separ)) {
			return "";
		} else {
			return separ;
		}
	}

	/**
	 * 进制编码大小写.
	 */
	private String[] lowUpCase = new String[] { "小写", "大写" };

	/**
	 * 进制编码大小写下拉框.
	 */
	private JComboBox lowUpCaseBox = createJComboBox(lowUpCase, GuiUtils.font14_cn, new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			curLowUpCase = ((JComboBox) event.getSource()).getSelectedItem().toString();
		}
	});

	/**
	 * 当前进制编码大小写.
	 */
	private String curLowUpCase = lowUpCase[0];

	/**
	 * 字符集.
	 */
	private String[] charsets = new String[] { GuiUtils.CHARSET_UTF_16BE, GuiUtils.CHARSET_UTF_16LE,
			GuiUtils.CHARSET_UTF_8, GuiUtils.CHARSET_UTF_16, GuiUtils.CHARSET_GB2312, GuiUtils.CHARSET_GBK,
			GuiUtils.CHARSET_GB18030, GuiUtils.CHARSET_Big5, GuiUtils.CHARSET_ISO_8859_1, "" };

	/**
	 * 手动输入的字符编码框.
	 */
	private JTextField customCharsetField = new JTextField("UTF-8");

	/**
	 * TextField数组，与字符集对应.
	 */
	private JTextField[] fields = new JTextField[charsets.length];
	{
		for (int i = 0; i < fields.length; i++) {
			fields[i] = new JTextField();
		}
	}

	/**
	 * Converter Encode、Decode行数.
	 */
	private int rows = fields.length + 2;

	public CharacterConverter() {

		// 主面板：边界布局，分North、Center两部分，North用于放置控件，Center是一个空面板
		setLayout(new BorderLayout());
		// Center空面板
		add(new JPanel(), BorderLayout.CENTER);
		// North面板
		JPanel topPanel = new JPanel(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);
		// 左侧：放置Label
		JPanel labelsPanel = new JPanel(new GridLayout(rows, 1));
		topPanel.add(labelsPanel, BorderLayout.WEST);
		// 中间：放置输入输出框
		JPanel fieldsPanel = new JPanel(new GridLayout(rows, 1));
		topPanel.add(fieldsPanel, BorderLayout.CENTER);
		// 右侧：放置按钮
		JPanel buttonsPanel = new JPanel(new GridLayout(rows, 1));
		topPanel.add(buttonsPanel, BorderLayout.EAST);

		// 左侧：放置Label
		addJLabel(labelsPanel, " Encode String: ", GuiUtils.font16);
		addJLabel(labelsPanel, " ", GuiUtils.font16); // 仅作填充
		for (int i = 0; i < fields.length - 1; i++) {
			addJLabel(labelsPanel, " " + charsets[i] + ": ", GuiUtils.font16);
		}
		// 手动输入的字符编码框
		addJTextField(labelsPanel, customCharsetField, GuiUtils.font14_un);

		// 中间：放置输入输出框
		addJTextField(fieldsPanel, encodeTextField, GuiUtils.font14_un);
		JPanel comboxPanel = new JPanel(new BorderLayout());
		fieldsPanel.add(comboxPanel);
		for (int i = 0; i < fields.length; i++) {
			addJTextField(fieldsPanel, fields[i], GuiUtils.font14_un);
		}
		// 下拉框面板
		JPanel flowComboxPanel = new JPanel(new FlowLayout());
		comboxPanel.add(flowComboxPanel, BorderLayout.WEST);
		comboxPanel.add(new JPanel(), BorderLayout.CENTER);
		addJLabel(flowComboxPanel, "编码类别:", GuiUtils.font14_cn);
		// 编码类别下拉框，默认选择16进制
		addJComboBox(flowComboxPanel, codeTypes, 3, GuiUtils.font14_cn, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				curCodeType = ((JComboBox) event.getSource()).getSelectedItem().toString();
				if (codeType_Decode.equals(curCodeType)) {
					encodeTextField.setEnabled(false);
					encodeButton.setEnabled(false);
					prefixsBox.setEnabled(false);
					lowUpCaseBox.setEnabled(false);
				} else {
					encodeTextField.setEnabled(true);
					encodeButton.setEnabled(true);
					prefixsBox.setEnabled(true);
					lowUpCaseBox.setEnabled(true);
				}
			}
		});
		addJLabel(flowComboxPanel, " 进制前缀符:", GuiUtils.font14_cn);
		// 进制前缀符下拉框
		prefixsBox.setEditable(true);
		flowComboxPanel.add(prefixsBox);
		addJLabel(flowComboxPanel, " 进制大小写:", GuiUtils.font14_cn);
		// 进制编码大小写下拉框
		flowComboxPanel.add(lowUpCaseBox);
		addJLabel(flowComboxPanel, "   ", GuiUtils.font14_cn);
		addJButton(flowComboxPanel, "清除输入输出", "clear", GuiUtils.font14_cn, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearTextFields();
			}
		});

		// 右侧：放置按钮
		encodeButton.addActionListener(getButtonActionListener(0));
		buttonsPanel.add(encodeButton);
		addJLabel(buttonsPanel, " ", GuiUtils.font16); // 仅作填充
		for (int i = 0; i < fields.length; i++) {
			addJButton(buttonsPanel, "Decode", Integer.toString(i + 1), GuiUtils.font16, getButtonActionListener(i + 1));
		}
	}

	/**
	 * 清除输入输出.
	 */
	private void clearTextFields() {
		encodeTextField.setText("");
		for (int i = 0; i < fields.length; i++) {
			fields[i].setText("");
		}
	}

	/**
	 * 按钮点击事件.
	 */
	private ActionListener getButtonActionListener(int current) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// 手动输入的字符编码
				charsets[charsets.length - 1] = customCharsetField.getText().trim();
				int sort = Integer.parseInt(((JButton) event.getSource()).getName());
				if (sort == 0) {
					String input = encodeTextField.getText();
					clearTextFields();
					encodeTextField.setText(input);
					if (input.length() == 0) {
						return;
					}
					for (int i = 0; i < fields.length; i++) {
						try {
							if (codeType_16Radix.equals(curCodeType)) {
								fields[i].setText(encode16RadixAddPrefix(input, charsets[i], curPrefix));
							} else if (codeType_10Radix.equals(curCodeType)) {
								fields[i].setText(encode10RadixAddPrefix(input, charsets[i], curPrefix));
							} else if (codeType_8Radix.equals(curCodeType)) {
								fields[i].setText(encode8RadixAddPrefix(input, charsets[i], curPrefix));
							} else if (codeType_2Radix.equals(curCodeType)) {
								fields[i].setText(encode2RadixAddPrefix(input, charsets[i], curPrefix));
							}
						} catch (Exception e) {
							showExceptionMessage(e);
							return;
						}
					}
				} else {
					String input = fields[sort - 1].getText();
					clearTextFields();
					fields[sort - 1].setText(input);
					input = input.replace(curPrefix, ""); // 去除前缀符
					if (input.length() == 0) {
						return;
					}
					try {
						String decodeString = "";
						if (codeType_16Radix.equals(curCodeType)) {
							decodeString = ExCodec.decodeHex(input, charsets[sort - 1]);
						} else if (codeType_10Radix.equals(curCodeType)) {
							decodeString = ExCodec.decodeHex(RadixUtils.convertRadixString10To16(input),
									charsets[sort - 1]);
						} else if (codeType_8Radix.equals(curCodeType)) {
							decodeString = ExCodec.decodeHex(RadixUtils.convertRadixString8To16(input),
									charsets[sort - 1]);
						} else if (codeType_2Radix.equals(curCodeType)) {
							decodeString = ExCodec.decodeHex(RadixUtils.convertRadixString2To16(input),
									charsets[sort - 1]);
						}
						encodeTextField.setText(decodeString);
						if (codeType_Decode.equals(curCodeType)) {
							encodeTextField.setText("\"" + codeType_Decode + "\"与Encode String无关！");
						}
						for (int i = 0; i < fields.length; i++) {
							if (i != sort - 1) {
								if (codeType_16Radix.equals(curCodeType)) {
									fields[i].setText(encode16RadixAddPrefix(decodeString, charsets[i], curPrefix));
								} else if (codeType_10Radix.equals(curCodeType)) {
									fields[i].setText(encode10RadixAddPrefix(decodeString, charsets[i], curPrefix));
								} else if (codeType_8Radix.equals(curCodeType)) {
									fields[i].setText(encode8RadixAddPrefix(decodeString, charsets[i], curPrefix));
								} else if (codeType_2Radix.equals(curCodeType)) {
									fields[i].setText(encode2RadixAddPrefix(decodeString, charsets[i], curPrefix));
								} else if (codeType_Decode.equals(curCodeType)) {
									fields[i].setText(GuiUtils.encode(input, charsets[sort - 1], charsets[i]));
								}
							}
						}
					} catch (Exception e) {
						showExceptionMessage(e);
						return;
					}

				}
				// 大小写
				if (codeType_16Radix.equals(curCodeType) || codeType_2Radix.equals(curCodeType)) {
					if (lowUpCase[0].equals(curLowUpCase)) {
						for (int fi = 0; fi < fields.length; fi++) {
							fields[fi].setText(fields[fi].getText().toLowerCase());
						}
					} else if (lowUpCase[1].equals(curLowUpCase)) {
						for (int fi = 0; fi < fields.length; fi++) {
							fields[fi].setText(fields[fi].getText().toUpperCase());
						}
					}
				}
			}
		};
	}

	/**
	 * 字符编码进制前缀字符填充 - 16进制.
	 */
	private String encode16RadixAddPrefix(String input, String charset, String prefix)
			throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			// ExCodec.encodeHex 转16进制
			sb.append(prefix).append(ExCodec.encodeHex(input.substring(i, i + 1), charset));
		}
		return sb.toString();
	}

	/**
	 * 字符编码进制前缀字符填充 - 10进制.
	 */
	private String encode10RadixAddPrefix(String input, String charset, String prefix)
			throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			sb.append(prefix).append(
					RadixUtils.convertRadixString16To10(ExCodec.encodeHex(input.substring(i, i + 1), charset)));
		}
		return sb.toString();
	}

	/**
	 * 字符编码进制前缀字符填充 - 8进制.
	 */
	private String encode8RadixAddPrefix(String input, String charset, String prefix)
			throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			sb.append(prefix).append(
					RadixUtils.convertRadixString16To8(ExCodec.encodeHex(input.substring(i, i + 1), charset)));
		}
		return sb.toString();
	}

	/**
	 * 字符编码进制前缀字符填充 - 2进制.
	 */
	private String encode2RadixAddPrefix(String input, String charset, String prefix)
			throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			sb.append(prefix).append(
					RadixUtils.convertRadixString16To2(ExCodec.encodeHex(input.substring(i, i + 1), charset)));
		}
		return sb.toString();
	}

}
