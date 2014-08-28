package bs.util.tool.commongui.plugins.network;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import bs.util.tool.commongui.GuiJPanel;
import bs.util.tool.commongui.GuiUtils;
import bs.util.tool.commongui.utils.network.Const;

/**
 * 短网址转换.
 */
public class ShortURL extends GuiJPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * 长网址表单.
	 */
	private JTextField longURLTextField = new JTextField();

	/**
	* 短网址服务商.
	*/
	private String[] shortURLService = new String[] { Const.BAIDU_NAME };

	/**
	 * 当前短网址服务商.
	 */
	private String curShortURLService = shortURLService[0];

	/**
	 * 转换.
	 */
	private JButton convertButton;

	/**
	 * 短网址表单.
	 */
	private JTextField shortURLTextField = new JTextField();

	/**
	 * 还原.
	 */
	private JButton revertButton;

	/**
	 * 空白文本域.
	 */
	private JTextArea resultTextArea = createJTextArea(GuiUtils.font14_un);

	public ShortURL() {

		// 主面板：边界布局，分North、Center两部分，North用于放置输入及条件控件，Center是放置结果输出
		setLayout(new BorderLayout());

		// 输入及条件Panel
		JPanel inputPanel = new JPanel(new GridLayout(2, 1));

		// 长网址表单、转换服务提供商、转换按钮Panel
		JPanel convertPanel = new JPanel(new BorderLayout());
		addJLabel(convertPanel, "  长网址: ", GuiUtils.font14_cn, BorderLayout.WEST);
		// 长网址表单
		addJTextField(convertPanel, longURLTextField, GuiUtils.font14_un, BorderLayout.CENTER);

		JPanel convertFlowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		// 短网址服务商
		addJComboBox(convertFlowPanel, shortURLService, GuiUtils.font13_cn, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				curShortURLService = ((JComboBox) event.getSource()).getSelectedItem().toString();
			}
		});
		// 复制
		addJButton(convertFlowPanel, "复制", "", GuiUtils.font13_cn, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				StringSelection selection = new StringSelection(longURLTextField.getText());
				// 获取系统剪切板，复制短网址
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
			}
		});
		// 转换按钮
		convertButton = createJButton("转换", "", GuiUtils.font14b_cn);
		convertButton.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent event) {
				String path = longURLTextField.getText().trim();
				if (path.length() == 0) {
					showMessage("没有输入网址！", "警告", JOptionPane.WARNING_MESSAGE);
					return;
				}
				shortURLTextField.setText("");
				convertButton.setEnabled(false);
				try {
					if (curShortURLService.equals(Const.BAIDU_NAME)) {
						shortURLTextField.setText(bs.util.tool.commongui.utils.network.baidu.ShortURL.toShort(path));
					}
				} catch (Exception e) {
					showExceptionMessage(e);
				}
				convertButton.setEnabled(true);
			}

			public void mousePressed(MouseEvent e) {
				shortURLTextField.setText("");
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
			}
		});
		convertFlowPanel.add(convertButton);
		convertPanel.add(convertFlowPanel, BorderLayout.EAST);

		// 短网址表单、还原按钮Panel
		JPanel revertPanel = new JPanel(new BorderLayout());
		addJLabel(revertPanel, "  短网址: ", GuiUtils.font14_cn, BorderLayout.WEST);
		// 短网址表单
		addJTextField(revertPanel, shortURLTextField, GuiUtils.font14_un, BorderLayout.CENTER);

		JPanel revertFlowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		// 复制
		addJButton(revertFlowPanel, "复制", "", GuiUtils.font13_cn, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				StringSelection selection = new StringSelection(shortURLTextField.getText());
				// 获取系统剪切板，复制短网址
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
			}
		});

		revertButton = createJButton("还原", "", GuiUtils.font14b_cn);
		revertButton.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent event) {
				String path = shortURLTextField.getText().trim();
				if (path.length() == 0) {
					showMessage("没有输入网址！", "警告", JOptionPane.WARNING_MESSAGE);
					return;
				}
				longURLTextField.setText("");
				revertButton.setEnabled(false);
				try {
					if (curShortURLService.equals(Const.BAIDU_NAME)) {
						longURLTextField.setText(bs.util.tool.commongui.utils.network.baidu.ShortURL.toLongURL(path));
					}
				} catch (Exception e) {
					showExceptionMessage(e);
				}
				revertButton.setEnabled(true);
			}

			public void mousePressed(MouseEvent e) {
				longURLTextField.setText("");
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
			}
		});
		revertFlowPanel.add(revertButton);
		revertPanel.add(revertFlowPanel, BorderLayout.EAST);

		inputPanel.add(convertPanel);
		inputPanel.add(revertPanel);

		add(inputPanel, BorderLayout.NORTH);

		// 空面面板
		JPanel resultPanel = new JPanel(new BorderLayout());
		resultTextArea.setEditable(false);
		resultTextArea.setText("说明：" + "\n1，百度短网址API：http://www.baidu.com/search/dwz.html"
				+ "\n2，新浪短网址API：新浪短网址http://t.cn/需要授权才可使用，此处转换中不列出，详细API说明如下："
				+ "\n                                http://open.weibo.com/wiki/2/short_url/shorten"
				+ "\n                                http://open.weibo.com/wiki/2/short_url/expand");
		resultPanel.add(new JScrollPane(resultTextArea));
		add(resultPanel, BorderLayout.CENTER);
	}

}
