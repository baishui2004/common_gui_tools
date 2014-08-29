package bs.util.tool.commongui.plugins.more;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import bs.util.tool.commongui.GuiJPanel;
import bs.util.tool.commongui.GuiUtils;
import bs.util.tool.commongui.utils.EscapeUtils;
import bs.util.tool.commongui.utils.LanguageUtils;

/**
 * 字符转义工具.
 */
public class EscapeCharacterTool extends GuiJPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * 字符文本域.
	 */
	private JTextArea unescapeTextArea = createJTextArea(GuiUtils.font14_un);
	/**
	 * 转义文本域.
	 */
	private JTextArea escapeTextArea = createJTextArea(GuiUtils.font14_un);
	/**
	 * 帮助文本域.
	 */
	private JTextArea helpTextArea = createJTextArea(GuiUtils.font14_un);

	/**
	 * 字符类型.
	 */
	private String[] characterTypes = new String[] { LanguageUtils.CONST_HTML, LanguageUtils.CONST_XML,
			LanguageUtils.CONST_JAVA, LanguageUtils.CONST_JavaScript, LanguageUtils.CONST_CSV };
	/**
	 * 字符类型.
	 */
	private String curCharacterType = characterTypes[0];

	/**
	 * HTML转义说明.
	 */
	private static final String ESCAPE_HELP_HTML = "HTML            See: http://www.w3.org/TR/html4/sgml/entities.html"
			+ "\n"
			+ "\n <          >            &              \"          no-break space       em space      en space          ®             ©             ™"
			+ "\n&lt;      &gt;      &amp;      &quot;            &nbsp;                &emsp;          &ensp;        &reg;      &copy;     &trade;";
	/**
	 * XML转义说明.
	 */
	private static final String ESCAPE_HELP_XML = "XML              See: http://www.xmlnews.org/docs/xml-basics.html"
			+ "\n" + "\n <          >            &               \"               '"
			+ "\n&lt;      &gt;      &amp;      &quot;      &apos;";
	/**
	 * JAVA转义说明.
	 */
	private static final String ESCAPE_HELP_JAVA = "JAVA             See: http://docs.oracle.com/javase/tutorial/java/data/characters.html"
			+ "\n"
			+ "\n回车符     换行符     制表符     单引号     双引号     反斜杠     退格符     换页符"
			+ "\n    \\r             \\n             \\t              \\'             \\\"              \\\\             \\b             \\f";
	/**
	 * JavaScript转义说明.
	 */
	private static final String ESCAPE_HELP_JavaScript = "JavaScript     See: http://www.w3schools.com/js/js_strings.asp"
			+ "\n"
			+ "\n回车符     换行符     制表符     单引号     双引号     反斜杠     退格符     换页符"
			+ "\n    \\r             \\n             \\t              \\'             \\\"              \\\\             \\b             \\f";
	/**
	 * CSV转义说明.
	 */
	private static final String ESCAPE_HELP_CSV = "";

	public EscapeCharacterTool() {
		// 边界布局
		setLayout(new BorderLayout());
		// Center，字符及转义结果输入输出域，使用3行1列的Grid布局，使其平均显示
		JPanel textAreaPanel = new JPanel(new GridLayout(3, 1));
		add(textAreaPanel, BorderLayout.CENTER);

		JPanel scriptPanel = new JPanel(new BorderLayout());
		addJLabel(scriptPanel, " 原 字 符: ", GuiUtils.font14b_cn, BorderLayout.WEST);
		scriptPanel.add(new JScrollPane(unescapeTextArea), BorderLayout.CENTER);
		textAreaPanel.add(scriptPanel);

		JPanel resultPanel = new JPanel(new BorderLayout());
		addJLabel(resultPanel, " 转义字符: ", GuiUtils.font14b_cn, BorderLayout.WEST);
		resultPanel.add(new JScrollPane(escapeTextArea), BorderLayout.CENTER);
		textAreaPanel.add(resultPanel);

		JPanel helpTextPanel = new JPanel(new BorderLayout());
		addJLabel(helpTextPanel, " 常用转义: ", GuiUtils.font14b_cn, BorderLayout.WEST);
		helpTextPanel.add(new JScrollPane(helpTextArea), BorderLayout.CENTER);
		textAreaPanel.add(helpTextPanel);

		setHelpTextArea();

		// East，操作区域，使用BorderLayout布局
		JPanel actionPanel = new JPanel(new BorderLayout());
		add(actionPanel, BorderLayout.EAST);

		// 帮助面板
		JPanel helpPanel = new JPanel(new BorderLayout());
		JPanel helpButtonPanel = new JPanel(new BorderLayout());
		helpPanel.add(helpButtonPanel, BorderLayout.EAST);
		addJButton(helpButtonPanel, "帮助", "", GuiUtils.font13_cn, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				showTextAreaMessage("常用转义字符" + "\n\n" + ESCAPE_HELP_HTML + "\n\n\n" + ESCAPE_HELP_XML + "\n\n\n"
						+ ESCAPE_HELP_JAVA + "\n\n\n" + ESCAPE_HELP_JavaScript + "\n\n\n" + ESCAPE_HELP_CSV, "帮助",
						JOptionPane.INFORMATION_MESSAGE, null, null);
			}
		});

		actionPanel.add(helpPanel, BorderLayout.NORTH);
		// 填充
		actionPanel.add(new JPanel(), BorderLayout.CENTER);

		// 放置按钮
		JPanel buttonPanel = new JPanel(new GridLayout(9, 1));
		actionPanel.add(buttonPanel, BorderLayout.SOUTH);
		addJComboBox(buttonPanel, characterTypes, GuiUtils.font13, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				curCharacterType = ((JComboBox) event.getSource()).getSelectedItem().toString();
				setHelpTextArea();
			}
		});
		buttonPanel.add(new JPanel()); // 仅做填充
		// 转换
		addJButton(buttonPanel, " 转换 ", "", GuiUtils.font14b_cn, new MouseListener() {
			public void mouseReleased(MouseEvent event) {
				String input = unescapeTextArea.getText();
				escapeTextArea.append(EscapeUtils.escape(input, curCharacterType));
			}

			public void mousePressed(MouseEvent e) {
				escapeTextArea.setText("");
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
			}
		});
		buttonPanel.add(new JPanel()); // 仅做填充
		// 还原
		addJButton(buttonPanel, " 还原 ", "", GuiUtils.font14b_cn, new MouseListener() {
			public void mouseReleased(MouseEvent event) {
				String input = escapeTextArea.getText();
				unescapeTextArea.append(EscapeUtils.unescape(input, curCharacterType));
			}

			public void mousePressed(MouseEvent e) {
				unescapeTextArea.setText("");
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
			}
		});
		buttonPanel.add(new JPanel()); // 仅做填充
		// 全部清空
		addJButton(buttonPanel, " 全部清空 ", "", GuiUtils.font14_cn, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				unescapeTextArea.setText("");
				escapeTextArea.setText("");
			}
		});
	}

	/**
	 * 设置帮助文本域显示文字.
	 */
	private void setHelpTextArea() {
		if (curCharacterType.equals(LanguageUtils.CONST_HTML)) {
			helpTextArea.setText(ESCAPE_HELP_HTML);
		} else if (curCharacterType.equals(LanguageUtils.CONST_XML)) {
			helpTextArea.setText(ESCAPE_HELP_XML);
		} else if (curCharacterType.equals(LanguageUtils.CONST_JAVA)) {
			helpTextArea.setText(ESCAPE_HELP_JAVA);
		} else if (curCharacterType.equals(LanguageUtils.CONST_JavaScript)) {
			helpTextArea.setText(ESCAPE_HELP_JavaScript);
		} else if (curCharacterType.equals(LanguageUtils.CONST_CSV)) {
			helpTextArea.setText(ESCAPE_HELP_CSV);
		}
	}

}
