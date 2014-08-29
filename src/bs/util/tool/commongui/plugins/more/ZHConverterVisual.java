package bs.util.tool.commongui.plugins.more;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.spreada.utils.chinese.ZHConverter;

import bs.util.tool.commongui.GuiJPanel;
import bs.util.tool.commongui.GuiUtils;

/**
 * 中文简繁体转换.
 */
public class ZHConverterVisual extends GuiJPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * 简体文本域.
	 */
	private JTextArea simplifiedTextArea = createJTextArea(GuiUtils.font14_un);
	/**
	 * 繁体文本域.
	 */
	private JTextArea traditionalTextArea = createJTextArea(GuiUtils.font14_un);

	public ZHConverterVisual() {
		// 边界布局
		setLayout(new BorderLayout());
		// Center，文字及转换结果输入输出域，使用2行1列的Grid布局，使其平均显示
		JPanel textAreaPanel = new JPanel(new GridLayout(2, 1));
		add(textAreaPanel, BorderLayout.CENTER);

		JPanel scriptPanel = new JPanel(new BorderLayout());
		addJLabel(scriptPanel, " 简体文本: ", GuiUtils.font14b_cn, BorderLayout.WEST);
		scriptPanel.add(new JScrollPane(simplifiedTextArea), BorderLayout.CENTER);
		textAreaPanel.add(scriptPanel);

		JPanel resultPanel = new JPanel(new BorderLayout());
		addJLabel(resultPanel, " 繁体文本: ", GuiUtils.font14b_cn, BorderLayout.WEST);
		resultPanel.add(new JScrollPane(traditionalTextArea), BorderLayout.CENTER);
		textAreaPanel.add(resultPanel);

		// East，操作区域，使用BorderLayout布局
		JPanel actionPanel = new JPanel(new BorderLayout());
		add(actionPanel, BorderLayout.EAST);

		// 填充
		actionPanel.add(new JPanel(), BorderLayout.CENTER);

		// 放置按钮
		JPanel buttonPanel = new JPanel(new GridLayout(7, 1));
		actionPanel.add(buttonPanel, BorderLayout.SOUTH);
		// 简->繁
		addJButton(buttonPanel, " 简 -> 繁 ", "", GuiUtils.font14b_cn, new MouseListener() {
			public void mouseReleased(MouseEvent event) {
				String input = simplifiedTextArea.getText();
				traditionalTextArea.append(ZHConverter.convert(input, ZHConverter.TRADITIONAL));
			}

			public void mousePressed(MouseEvent e) {
				traditionalTextArea.setText("");
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
			}
		});
		buttonPanel.add(new JPanel()); // 仅做填充
		// 繁->简
		addJButton(buttonPanel, " 繁 -> 简 ", "", GuiUtils.font14b_cn, new MouseListener() {
			public void mouseReleased(MouseEvent event) {
				String input = traditionalTextArea.getText();
				simplifiedTextArea.append(ZHConverter.convert(input, ZHConverter.SIMPLIFIED));
			}

			public void mousePressed(MouseEvent e) {
				simplifiedTextArea.setText("");
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
				simplifiedTextArea.setText("");
				traditionalTextArea.setText("");
			}
		});
	}

}
