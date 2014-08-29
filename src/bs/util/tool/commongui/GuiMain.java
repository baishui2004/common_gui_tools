package bs.util.tool.commongui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import bs.util.io.PropertiesUtils;

/**
 * GUI主界面.
 */
public class GuiMain extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * 软件名称.
	 */
	private String softName = "Common GUI Tools";

	/**
	 * GUI配置属性Map.
	 */
	private static Map<String, String> propsMap;

	/**
	 * GUI配置属性Map(更多工具).
	 */
	private static Map<String, String> more_propsMap;
	
	/**
	 * GUI配置属性Map(网络工具).
	 */
	private static Map<String, String> network_propsMap;

	/**
	 * 常用插件ID.
	 */
	private static String commonUsePlugins;

	/**
	 * 宽度, 满足宽高比例1024:768.
	 */
	private static int gui_width;

	/**
	 * 高度.
	 */
	private int gui_height = gui_width * 768 / 1024;

	/**
	 * 入口程序.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					propsMap = PropertiesUtils.getPropertiesMap(GuiUtils
							.getActualPath("conf/common_gui_tools.properties"));
					more_propsMap = PropertiesUtils.getPropertiesMap(GuiUtils
							.getActualPath("conf/more_tools.properties"));
					network_propsMap = PropertiesUtils.getPropertiesMap(GuiUtils
							.getActualPath("conf/network_tools.properties"));
					propsMap.putAll(more_propsMap);
					propsMap.putAll(network_propsMap);
					commonUsePlugins = propsMap.get("CommonUseTools");
					gui_width = Integer.parseInt(propsMap.get("GUIWidth"));

					// 设置皮肤外观
					String guiSkinStr = propsMap.get("GUISkin").trim();
					if (guiSkinStr.length() > 0) {
						String[] guiSkins = guiSkinStr.split(",");
						for (String skin : guiSkins) {
							if (setLookAndFeel(skin)) {
								break;
							}
						}
					}

					// 设置显示字体
					// 中文字体
					GuiUtils.fontStyles_cn = new String(propsMap.get("fontStyles_cn").getBytes("ISO-8859-1"), "UTF-8")
							.split(",");
					GuiUtils.fontStyle_cn = GuiUtils.getAvailableFont(GuiUtils.fontStyles_cn);
					// 英文字体
					GuiUtils.fontStyles = (new String(propsMap.get("fontStyles").getBytes("ISO-8859-1"), "UTF-8") + "," + GuiUtils.fontStyle_cn)
							.split(",");
					GuiUtils.fontStyle = GuiUtils.getAvailableFont(GuiUtils.fontStyles);
					// 支持Unicode的字体
					GuiUtils.fontStyles_un = (new String(propsMap.get("fontStyles_un").getBytes("ISO-8859-1"), "UTF-8")
							+ "," + GuiUtils.fontStyle_cn).split(",");
					GuiUtils.fontStyle_un = GuiUtils.getAvailableFont(GuiUtils.fontStyles_un);
					// 初始化字体
					GuiUtils.initFont();

					GuiMain frame = new GuiMain();
					frame.setVisible(true);
				} catch (Exception e) {
					GuiUtils.log(e);
				}
			}

			/**
			 * 设置皮肤外观.
			 */
			private boolean setLookAndFeel(String lookAndFeel) {
				try {
					UIManager.setLookAndFeel(lookAndFeel);
					JFrame.setDefaultLookAndFeelDecorated(true);
					JDialog.setDefaultLookAndFeelDecorated(true);
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});
	}

	/**
	 * Swing面板.
	 */
	private JPanel contextPanel;
	
	/**
	 * 警告、错误消息输出表单.
	 */
	public static JTextArea msgTextArea = new JTextArea();

	/**
	 * Tab Panel.
	 */
	private JTabbedPane tabbedPane = new JTabbedPane();

	/**
	 * 插件属性，key：插件ID_插件名称，value：{插件ID, 插件名称, 插件类名全称, 插件图标}.
	 */
	private Map<Integer, List<String>> pluginsProperties = new LinkedHashMap<Integer, List<String>>();
	/**
	 * 插件title面板，key：插件ID，value：title面板.
	 */
	private Map<Integer, JPanel> titlesPanel = new HashMap<Integer, JPanel>();
	/**
	 * 插件面板，key：插件ID，value：JPanel面板.
	 */
	private Map<Integer, JPanel> pluginsPanel = new HashMap<Integer, JPanel>();
	/**
	 * 记录插件在当前TabPanel的位置，key：插件ID，value：Index索引位置.
	 */
	private Map<Integer, Integer> pluginsTabIndex = new HashMap<Integer, Integer>();
	/**
	 * 记录当前TabPanel位置的插件，key：Index索引位置，value：插件ID.
	 */
	private Map<Integer, Integer> tabPluginsId = new HashMap<Integer, Integer>();

	/**
	 * 设定显示位置及大小.
	 */
	private void setLocatinAndSize() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension dimension = kit.getScreenSize();
		int screen_width = (int) dimension.getWidth();
		int screen_height = (int) dimension.getHeight();
		// 如果显示屏幕分辨率没有已设定的面板大, 则按显示屏幕分辨率显示
		gui_width = screen_width < gui_width ? screen_width : gui_width;
		gui_height = screen_height < gui_height ? screen_height : gui_height;
		setLocation((screen_width - gui_width) / 2, (screen_height - gui_height) / 2); // 默认距屏幕左上角(0, 0), 此处设为居中显示屏幕
		setSize(gui_width, gui_height); // JFrame大小
	}

	public GuiMain() {
		contextPanel = (JPanel) getContentPane();
		setTitle(softName); // 标题
		// setResizable(false); // 默认不设置时是true
		setLocatinAndSize();
		// setLayout(new FlowLayout(FlowLayout.RIGHT)); // 流布局管理器(默认居中对齐)
		setLayout(new BorderLayout()); // 边界布局管理器
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Toolkit kit = Toolkit.getDefaultToolkit();
		setIconImage(GuiUtils.getImage("img/icon/cgt_Icon.png", kit));

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar); // 菜单工具条

		add(tabbedPane, BorderLayout.CENTER);
		// 警告、错误消息输出域
		JPanel msgPanel = new JPanel(new BorderLayout());
		msgPanel.setBorder(BorderFactory.createTitledBorder("Console:"));
		msgTextArea.setFont(GuiUtils.font13);
		msgTextArea.setRows(5);
		msgTextArea.setEditable(false);
		msgTextArea.setLineWrap(true); // 自动换行
		msgPanel.add(new JScrollPane(msgTextArea), BorderLayout.CENTER);
		// paste复制按钮、clear清楚按钮
		JPanel msgButtonPanel = new JPanel(new GridLayout(2, 1));
		JButton pasteButton = new JButton("paste");
		pasteButton.setFont(GuiUtils.font13_cn);
		pasteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				StringSelection selection = new StringSelection(msgTextArea.getText());
				// 获取系统剪切板，复制输出消息
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
			}
		});
		msgButtonPanel.add(pasteButton);
		JButton clearButton = new JButton("clear");
		clearButton.setFont(GuiUtils.font13_cn);
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				msgTextArea.setText("");
			}
		});
		msgButtonPanel.add(clearButton);
		msgPanel.add(msgButtonPanel, BorderLayout.EAST);
		add(msgPanel, BorderLayout.SOUTH);
		
		JMenu fileMenu = new JMenu("  File  ");
		menuBar.add(fileMenu); // File菜单
		JMenuItem exitItem = new JMenuItem("Exit"); // 二级菜单
		exitItem.setIcon(GuiUtils.getIcon("img/icon/cgt_Exit.png", kit));
		fileMenu.add(exitItem);
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});

		JMenu toolsMenu = new JMenu("  Tools  ");
		menuBar.add(toolsMenu); // 工具菜单

		JMenu more_toolsMenu = new JMenu("  More Tools  ");
		menuBar.add(more_toolsMenu); // 更多工具菜单
		
		JMenu network_toolsMenu = new JMenu("  NetWork Tools  ");
		menuBar.add(network_toolsMenu); // 网络工具菜单

		JMenu helpMenu = new JMenu("  Help  ");
		menuBar.add(helpMenu); // Help菜单
		JMenuItem fontItem = new JMenuItem("Font"); // 二级菜单
		fontItem.setIcon(GuiUtils.getIcon("img/icon/cgt_Font.png", kit));
		helpMenu.add(fontItem);
		fontItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(contextPanel, softName + " 使用的字体\n\n中文字体:  " + GuiUtils.fontStyle_cn
						+ "\n英文字体:  " + GuiUtils.fontStyle + "\nUnicode字体:  " + GuiUtils.fontStyle_un + "\n", "Font",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		helpMenu.addSeparator(); // 分隔符
		JMenuItem aboutItem = new JMenuItem("About"); // 二级菜单
		aboutItem.setIcon(GuiUtils.getIcon("img/icon/cgt_About.png", kit));
		helpMenu.add(aboutItem);
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(contextPanel, "Version: " + propsMap.get("Version")
						+ "\nAuthor: bs2004@163.com\nDevelop Date: " + propsMap.get("Develop_Date"), "About",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});

		// 关闭图标
		final Icon closeIcon = GuiUtils.getIcon("img/icon/cgt_Close.png", kit);

		// 关闭Label事件
		final MouseListener closeLabelListener = new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				// 当前点击Label的父Panel
				int closeIndex = tabbedPane.indexOfTabComponent(((JPanel) ((JLabel) e.getSource()).getParent()));
				tabbedPane.remove(closeIndex);

				// 移除插件在当前TabPanel的位置的记录
				pluginsTabIndex.remove(tabPluginsId.get(closeIndex));
				// 清空原TabPanel位置的插件记录，重新记录
				tabPluginsId.clear();
				// remove插件面板后调整索引位置
				for (Integer i : pluginsTabIndex.keySet()) {
					Integer index = pluginsTabIndex.get(i);
					if (index > closeIndex) {
						pluginsTabIndex.put(i, index - 1);
					}
					tabPluginsId.put(pluginsTabIndex.get(i), i);
				}
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
			}
		};

		tabbedPane.setFont(GuiUtils.font14b_cn);
		String toolPrefix = "Tool_";
		try {
			List<String> pluginSortAndNames = new ArrayList<String>();
			for (String key : propsMap.keySet()) {
				if (key.startsWith(toolPrefix)) {
					pluginSortAndNames.add(key.substring(toolPrefix.length()));
				}
			}
			Collections.sort(pluginSortAndNames); // 排序

			// 插件属性，按ID升序排列
			int moreTools_cnt = more_propsMap.size();
			int networkTools_cnt = network_propsMap.size();
			for (int i = 0; i < pluginSortAndNames.size(); i++) {
				String key = pluginSortAndNames.get(i);
				List<String> pluginProps = new ArrayList<String>();
				String[] sortAndName = key.split("_");
				String pluginName = sortAndName[1];
				pluginProps.add(sortAndName[0]);
				pluginProps.add(pluginName);

				String classAndIcon = propsMap.get(toolPrefix + key);
				String className = classAndIcon.split("_")[0];
				pluginProps.add(className);
				pluginProps.add(classAndIcon.indexOf("_") > 0 ? classAndIcon.substring(className.length() + 1) : "");

				Integer pluginId = Integer.parseInt(sortAndName[0]);
				try {
					Class.forName(className);
					pluginsProperties.put(pluginId, pluginProps);
					// 插件图标
					String iconPath = pluginProps.get(3).trim();
					Icon toolIcon = null;
					if (iconPath.length() > 0) {
						toolIcon = GuiUtils.getIcon(iconPath, kit);
					}
					if (("," + commonUsePlugins + ",").contains("," + pluginId + ",")) {
						// 初始只加载常用插件
						JPanel pluginPanel = (JPanel) (Class.forName(className)).newInstance();
						pluginsPanel.put(pluginId, pluginPanel);

						// 设置title栏左侧图标，中间插件名称，右侧关闭图标
						JPanel titlePanel = new JPanel();
						titlePanel.setOpaque(false);
						titlePanel.add(new JLabel(toolIcon), BorderLayout.WEST);
						titlePanel.add(new JLabel(pluginName + "  "), BorderLayout.CENTER);
						JLabel closeLabel = new JLabel(closeIcon);
						closeLabel.addMouseListener(closeLabelListener);
						titlePanel.add(closeLabel, BorderLayout.EAST);

						titlesPanel.put(pluginId, titlePanel);

						tabbedPane.addTab(null, pluginPanel);
						tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, titlePanel);

						pluginsTabIndex.put(pluginId, tabbedPane.getTabCount() - 1);
						tabPluginsId.put(tabbedPane.getTabCount() - 1, pluginId);

						// 设置图标，图标在右侧
						// tabbedPane.addTab(pluginName + "  ", (JPanel) (Class.forName(className)).newInstance());
						// tabbedPane.setIconAt(tabbedPane.getTabCount() - 1, toolIcon);
					}

					// 设置菜单
					JMenuItem toolItem = new JMenuItem(pluginName); // 二级菜单
					toolItem.setName(Integer.toString(pluginId));
					if (pluginId > 50 && pluginId < 71) {
						more_toolsMenu.add(toolItem);
						moreTools_cnt--;
						if (moreTools_cnt > 0) {
							more_toolsMenu.addSeparator(); // 分隔符
						}
					} else if (pluginId > 70 && pluginId < 99) {
						network_toolsMenu.add(toolItem);
						networkTools_cnt--;
						if (networkTools_cnt > 0) {
							network_toolsMenu.addSeparator(); // 分隔符
						}
					} else {
						toolsMenu.add(toolItem);
						if (pluginId != 99) {
							toolsMenu.addSeparator(); // 分隔符
						}
					}
					toolItem.setIcon(toolIcon);
					toolItem.addActionListener(new ActionListener() {
						// 菜单事件
						public void actionPerformed(ActionEvent event) {
							JMenuItem item = (JMenuItem) event.getSource();
							Integer pluginId = Integer.parseInt(item.getName());
							// 如果面板没有add到tablePanel上，则add
							if (pluginsTabIndex.get(pluginId) == null) {
								List<String> pluginProps = pluginsProperties.get(pluginId);
								// 懒加载未加载的插件
								JPanel pluginPanel = pluginsPanel.get(pluginId);
								if (pluginPanel == null) {
									try {
										pluginPanel = (JPanel) (Class.forName(pluginProps.get(2))).newInstance();
										pluginsPanel.put(pluginId, pluginPanel);
									} catch (Exception e) {
										GuiUtils.log(e);
									}
								}

								// 设置title栏左侧图标，中间插件名称，右侧关闭图标
								JPanel titlePanel = titlesPanel.get(pluginId);
								if (titlePanel == null) {
									titlePanel = new JPanel();
									titlePanel.setOpaque(false);
									String iconPath = pluginProps.get(3).trim();
									if (iconPath.length() > 0) {
										titlePanel.add(
												new JLabel(GuiUtils.getIcon(iconPath, Toolkit.getDefaultToolkit())),
												BorderLayout.WEST);
									}
									titlePanel.add(new JLabel(pluginProps.get(1) + "  "), BorderLayout.CENTER);
									JLabel closeLabel = new JLabel(closeIcon);
									closeLabel.addMouseListener(closeLabelListener);
									titlePanel.add(closeLabel, BorderLayout.EAST);
									titlesPanel.put(pluginId, titlePanel);
								}

								tabbedPane.addTab(null, pluginPanel);
								tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, titlePanel);

								pluginsTabIndex.put(pluginId, tabbedPane.getTabCount() - 1);
								tabPluginsId.put(tabbedPane.getTabCount() - 1, pluginId);

								// 选中当前新add的插件面板
								tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
							} else {
								// 如果面板已经add到tablePanel上，则选中
								tabbedPane.setSelectedIndex(pluginsTabIndex.get(pluginId));
							}
						}
					});
				} catch (ClassNotFoundException e) {
					GuiUtils.log("Warn: Ignore plugin \"" + sortAndName[1]
							+ "\", because can not find it's Class \"" + className + "\".");
				}
			}
		} catch (Exception e) {
			GuiUtils.log(e);
		}
		
	}
}
