package bs.util.tool.commongui.plugins.more;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComboBox;
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
	 * 字符类型.
	 */
	private String[] characterTypes = new String[] { LanguageUtils.CONST_HTML, LanguageUtils.CONST_XML,
			LanguageUtils.CONST_SQL, LanguageUtils.CONST_JAVA, LanguageUtils.CONST_JAVASCRIPT, LanguageUtils.CONST_CSV };
	/**
	 * 字符类型.
	 */
	private String curCharacterType = characterTypes[0];

	public EscapeCharacterTool() {
		// 边界布局
		setLayout(new BorderLayout());
		// Center，字符及转义结果输入输出域，使用2行1列的Grid布局，使其平均显示
		JPanel textAreaPanel = new JPanel(new GridLayout(2, 1));
		add(textAreaPanel, BorderLayout.CENTER);

		JPanel scriptPanel = new JPanel(new BorderLayout());
		addJLabel(scriptPanel, " 原 字 符: ", GuiUtils.font14b_cn, BorderLayout.WEST);
		scriptPanel.add(new JScrollPane(unescapeTextArea), BorderLayout.CENTER);
		textAreaPanel.add(scriptPanel);

		JPanel resultPanel = new JPanel(new BorderLayout());
		addJLabel(resultPanel, " 转义字符: ", GuiUtils.font14b_cn, BorderLayout.WEST);
		resultPanel.add(new JScrollPane(escapeTextArea), BorderLayout.CENTER);
		textAreaPanel.add(resultPanel);

		// East，操作区域，使用BorderLayout布局
		JPanel actionPanel = new JPanel(new BorderLayout());
		add(actionPanel, BorderLayout.EAST);

		// 填充
		actionPanel.add(new JPanel(), BorderLayout.CENTER);

		// 放置按钮
		JPanel buttonPanel = new JPanel(new GridLayout(9, 1));
		actionPanel.add(buttonPanel, BorderLayout.SOUTH);
		addJComboBox(buttonPanel, characterTypes, GuiUtils.font13, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				curCharacterType = ((JComboBox) event.getSource()).getSelectedItem().toString();
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

}
