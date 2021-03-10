package bs.tool.commongui;

import bs.tool.commongui.utils.MacUtils;
import bs.tool.commongui.utils.SimpleMouseListener;
import bs.util.io.PropertiesUtils;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.*;

/**
 * GUI主界面.
 */
public class GuiMain extends JFrame {

    private static final long serialVersionUID = 1L;

    /**
     * 软件名称.
     */
    private final String softName = "Common Gui Tools";

    /**
     * GUI配置属性Map.
     */
    private static LinkedHashMap<String, String> cgt_propsMap;

    /**
     * GUI配置属性Map(工具).
     */
    private static LinkedHashMap<String, String> tools_propsMap;

    /**
     * 常用插件.
     */
    private static String commonUsePlugins;

    /**
     * 宽度, 满足宽高比例1024:768.
     */
    private static int gui_width;

    /**
     * 高度.
     */
    private static int gui_height;

    /**
     * 入口程序.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    cgt_propsMap = PropertiesUtils.getPropertiesMap(GuiUtils
                            .getActualPath("conf/common_gui_tools.properties"));
                    tools_propsMap = PropertiesUtils.getPropertiesMap(GuiUtils
                            .getActualPath("conf/tools.properties"));
                    commonUsePlugins = cgt_propsMap.get("CommonUseTools");
                    gui_width = Integer.parseInt(cgt_propsMap.get("GUIWidth"));
                    gui_height = gui_width * 768 / 1024;

                    // 设置皮肤外观
                    String guiSkinStr = cgt_propsMap.get("GUISkin").trim();
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
                    GuiUtils.fontStyles_cn = new String(cgt_propsMap.get("fontStyles_cn").getBytes("ISO-8859-1"), "UTF-8")
                            .split(",");
                    GuiUtils.fontStyle_cn = GuiUtils.getAvailableFont(GuiUtils.fontStyles_cn);
                    // 英文字体
                    GuiUtils.fontStyles = (new String(cgt_propsMap.get("fontStyles").getBytes("ISO-8859-1"), "UTF-8") + "," + GuiUtils.fontStyle_cn)
                            .split(",");
                    GuiUtils.fontStyle = GuiUtils.getAvailableFont(GuiUtils.fontStyles);
                    // 支持Unicode的字体
                    GuiUtils.fontStyles_un = (new String(cgt_propsMap.get("fontStyles_un").getBytes("ISO-8859-1"), "UTF-8")
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
                    setMacCommandCopyPaste();
                    return true;
                } catch (Exception e) {
                    GuiUtils.log(e);
                    return false;
                }
            }

            // How to use Command-c/Command-v shortcut in Mac to copy/paste text?
            // https://stackoverflow.com/questions/7252749/how-to-use-command-c-command-v-shortcut-in-mac-to-copy-paste-text
            private void setMacCommandCopyPaste() {
                if (GuiUtils.IS_MAC) {
                    try {
                        InputMap im = (InputMap) UIManager.get("TextField.focusInputMap");
                        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);
                        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
                        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
                        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);

                        InputMap tim = (InputMap) UIManager.get("TextArea.focusInputMap");
                        tim.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);
                        tim.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
                        tim.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
                        tim.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
                    } catch (Exception e) {
                        GuiUtils.log("", e);
                    }
                }
            }
        });
    }

    private final Toolkit kit = Toolkit.getDefaultToolkit();
    /**
     * Swing面板.
     */
    private final JPanel contextPanel;

    /**
     * 警告、错误消息输出表单.
     */
    public static JTextArea msgTextArea = new JTextArea();

    /**
     * Tab Panel.
     */
    private final JTabbedPane tabbedPane = new JTabbedPane();

    /**
     * 插件属性，key：插件名称，value：{插件名称, 插件类名全称, 插件图标}.
     */
    private final Map<String, List<String>> pluginsProperties = new LinkedHashMap<String, List<String>>();
    /**
     * 插件title面板，key：插件名称，value：title面板.
     */
    private final Map<String, JPanel> titlesPanel = new HashMap<String, JPanel>();
    /**
     * 插件面板，key：插件名称，value：JPanel面板.
     */
    private final Map<String, JPanel> pluginsPanel = new HashMap<String, JPanel>();
    /**
     * 记录插件在当前TabPanel的位置，key：插件名称，value：Index索引位置.
     */
    private final Map<String, Integer> pluginsTabIndex = new HashMap<String, Integer>();
    /**
     * 记录当前TabPanel位置的插件，key：Index索引位置，value：插件名称.
     */
    private final Map<Integer, String> tabPluginsId = new HashMap<Integer, String>();

    /**
     * 设定显示位置及大小.
     */
    private void setLocationAndSize() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension dimension = kit.getScreenSize();
        int screenWidth = (int) dimension.getWidth();
        int screenHeight = (int) dimension.getHeight();
        // 如果显示屏幕分辨率没有已设定的面板大, 则按显示屏幕分辨率显示
        gui_width = Math.min(screenWidth, gui_width);
        gui_height = Math.min(screenHeight, gui_height);
        // 默认距屏幕左上角(0, 0), 此处设为居中显示屏幕
        setLocation((screenWidth - gui_width) / 2, (screenHeight - gui_height) / 2);
        // JFrame大小
        setSize(gui_width, gui_height);
    }

    public GuiMain() {
        contextPanel = (JPanel) getContentPane();
        // 标题
        setTitle(softName);
        // 默认不设置时是true
        // setResizable(false);
        setLocationAndSize();
        // 流布局管理器(默认居中对齐)
        // setLayout(new FlowLayout(FlowLayout.RIGHT));
        // 边界布局管理器
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Image icon = GuiUtils.getImage("img/icon/cgt_Icon.png", kit);
        // set mac dock icon
        if (GuiUtils.IS_MAC) {
            MacUtils.showAboutMessage(kit, contextPanel, icon, cgt_propsMap);
        }
        setIconImage(icon);

        JMenuBar menuBar = new JMenuBar();
        // 菜单工具条
        setJMenuBar(menuBar);

        add(tabbedPane, BorderLayout.CENTER);
        // 警告、错误消息输出域
        JPanel msgPanel = new JPanel(new BorderLayout());
        msgPanel.setBorder(BorderFactory.createTitledBorder("Console:"));
        msgTextArea.setFont(GuiUtils.font13);
        msgTextArea.setRows(5);
        msgTextArea.setEditable(false);
        // 自动换行
        msgTextArea.setLineWrap(true);
        msgPanel.add(new JScrollPane(msgTextArea), BorderLayout.CENTER);
        // paste复制按钮、clear清楚按钮
        JPanel msgButtonPanel = new JPanel(new GridLayout(2, 1));
        JButton pasteButton = new JButton("paste");
        pasteButton.setFont(GuiUtils.font13_cn);
        pasteButton.addActionListener(new ActionListener() {
            @Override
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
            @Override
            public void actionPerformed(ActionEvent event) {
                msgTextArea.setText("");
            }
        });
        msgButtonPanel.add(clearButton);
        msgPanel.add(msgButtonPanel, BorderLayout.EAST);
        add(msgPanel, BorderLayout.SOUTH);

        JMenu fileMenu = new JMenu("  File  ");
        // File菜单
        menuBar.add(fileMenu);
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setIcon(GuiUtils.getIcon("img/icon/cgt_Exit.png", kit));
        fileMenu.add(exitItem);
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        // 关闭图标
        final Icon closeIcon = GuiUtils.getIcon("img/icon/cgt_Close.png", kit);

        // 关闭Label事件
        final MouseListener closeLabelListener = new SimpleMouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // 当前点击Label的父Panel
                int closeIndex = tabbedPane.indexOfTabComponent(((JLabel) e.getSource()).getParent());
                tabbedPane.remove(closeIndex);

                // 移除插件在当前TabPanel的位置的记录
                pluginsTabIndex.remove(tabPluginsId.get(closeIndex));
                // 清空原TabPanel位置的插件记录，重新记录
                tabPluginsId.clear();
                // remove插件面板后调整索引位置
                for (String i : pluginsTabIndex.keySet()) {
                    Integer index = pluginsTabIndex.get(i);
                    if (index > closeIndex) {
                        pluginsTabIndex.put(i, index - 1);
                    }
                    tabPluginsId.put(pluginsTabIndex.get(i), i);
                }
            }
        };

        tabbedPane.setFont(GuiUtils.font14b_cn);
        String toolPrefix = "Tool_";
        try {
            List<String> pluginSortNames = new ArrayList<String>();
            // 工具类别
            Map<String, String> toolsKindMap = new HashMap<String, String>();
            // 工具类别菜单
            Map<String, JMenu> toolsMenuMap = new HashMap<String, JMenu>();
            for (String key : tools_propsMap.keySet()) {
                if (key.startsWith(toolPrefix)) {
                    int li = key.lastIndexOf("_");
                    String kind = key.substring(toolPrefix.length(), li);
                    String pluginName = key.substring(li + 1);
                    toolsKindMap.put(pluginName, kind);
                    if (toolsMenuMap.get(kind) == null) {
                        // 工具菜单
                        JMenu toolsMenu = new JMenu("   " + kind + " Tools   ");
                        menuBar.add(toolsMenu);
                        toolsMenuMap.put(kind, toolsMenu);
                    }
                    pluginSortNames.add(pluginName);
                }
            }

            // 插件属性
            for (final String pluginName : pluginSortNames) {
                List<String> pluginProps = new ArrayList<String>();
                pluginProps.add(pluginName);

                String classAndIcon = tools_propsMap.get(toolPrefix + toolsKindMap.get(pluginName) + "_" + pluginName);
                String className = classAndIcon.split("_")[0];
                pluginProps.add(className);
                pluginProps.add(classAndIcon.indexOf("_") > 0 ? classAndIcon.substring(className.length() + 1) : "");

                try {
                    Class.forName(className);
                    pluginsProperties.put(pluginName, pluginProps);
                    // 插件图标
                    String iconPath = pluginProps.get(2).trim();
                    Icon toolIcon = null;
                    if (iconPath.length() > 0) {
                        toolIcon = GuiUtils.getIcon(iconPath, kit);
                    }
                    if (("," + commonUsePlugins + ",").contains("," + pluginName + ",")) {
                        // 初始只加载常用插件
                        JPanel pluginPanel = (JPanel) (Class.forName(className)).newInstance();
                        pluginsPanel.put(pluginName, pluginPanel);

                        // 设置title栏左侧图标，中间插件名称，右侧关闭图标
                        JPanel titlePanel = new JPanel();
                        titlePanel.setOpaque(false);
                        titlePanel.add(new JLabel(toolIcon), BorderLayout.WEST);
                        titlePanel.add(new JLabel(pluginName + "  "), BorderLayout.CENTER);
                        JLabel closeLabel = new JLabel(closeIcon);
                        closeLabel.addMouseListener(closeLabelListener);
                        titlePanel.add(closeLabel, BorderLayout.EAST);

                        titlesPanel.put(pluginName, titlePanel);

                        tabbedPane.addTab(null, pluginPanel);
                        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, titlePanel);

                        pluginsTabIndex.put(pluginName, tabbedPane.getTabCount() - 1);
                        tabPluginsId.put(tabbedPane.getTabCount() - 1, pluginName);

                        /*
                        // 设置图标，图标在右侧
                        // tabbedPane.addTab(pluginName + "  ", (JPanel) (Class.forName(className)).newInstance());
                        // tabbedPane.setIconAt(tabbedPane.getTabCount() - 1, toolIcon);
                        */
                    }

                    // 设置二级菜单
                    JMenuItem toolItem = new JMenuItem(pluginName);
                    toolItem.setName(pluginName);
                    JMenu menu = toolsMenuMap.get(toolsKindMap.get(pluginName));
                    if (menu.getItemCount() > 0) {
                        menu.addSeparator();
                    }
                    menu.add(toolItem);
                    toolItem.setIcon(toolIcon);
                    toolItem.addActionListener(new ActionListener() {
                        // 菜单事件
                        @Override
                        public void actionPerformed(ActionEvent event) {
                            JMenuItem item = (JMenuItem) event.getSource();
                            String pluginName = item.getName();
                            // 如果面板没有add到tablePanel上，则add
                            if (pluginsTabIndex.get(pluginName) == null) {
                                List<String> pluginProps = pluginsProperties.get(pluginName);
                                // 懒加载未加载的插件
                                JPanel pluginPanel = pluginsPanel.get(pluginName);
                                if (pluginPanel == null) {
                                    try {
                                        pluginPanel = (JPanel) (Class.forName(pluginProps.get(1))).newInstance();
                                        pluginsPanel.put(pluginName, pluginPanel);
                                    } catch (Exception e) {
                                        GuiUtils.log(e);
                                    }
                                }

                                // 设置title栏左侧图标，中间插件名称，右侧关闭图标
                                JPanel titlePanel = titlesPanel.get(pluginName);
                                if (titlePanel == null) {
                                    titlePanel = new JPanel();
                                    titlePanel.setOpaque(false);
                                    String iconPath = pluginProps.get(2).trim();
                                    if (iconPath.length() > 0) {
                                        titlePanel.add(
                                                new JLabel(GuiUtils.getIcon(iconPath, Toolkit.getDefaultToolkit())),
                                                BorderLayout.WEST);
                                    }
                                    titlePanel.add(new JLabel(pluginProps.get(0) + "  "), BorderLayout.CENTER);
                                    JLabel closeLabel = new JLabel(closeIcon);
                                    closeLabel.addMouseListener(closeLabelListener);
                                    titlePanel.add(closeLabel, BorderLayout.EAST);
                                    titlesPanel.put(pluginName, titlePanel);
                                }

                                tabbedPane.addTab(null, pluginPanel);
                                tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, titlePanel);

                                pluginsTabIndex.put(pluginName, tabbedPane.getTabCount() - 1);
                                tabPluginsId.put(tabbedPane.getTabCount() - 1, pluginName);

                                // 选中当前新add的插件面板
                                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
                            } else {
                                // 如果面板已经add到tablePanel上，则选中
                                tabbedPane.setSelectedIndex(pluginsTabIndex.get(pluginName));
                            }
                        }
                    });
                } catch (ClassNotFoundException e) {
                    GuiUtils.log("Warn: Ignore plugin \"" + pluginName
                            + "\", because can not find it's Class \"" + className + "\".", e);
                }
            }
        } catch (Exception e) {
            GuiUtils.log(e);
        }

        JMenu helpMenu = new JMenu("   Help   ");
        menuBar.add(helpMenu);
        JMenuItem fontItem = new JMenuItem("Font");
        final Icon fontIcon = GuiUtils.getIcon("img/icon/cgt_Font.png", kit);
        fontItem.setIcon(fontIcon);
        helpMenu.add(fontItem);
        fontItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(contextPanel, softName + " 使用的字体\n\n中文字体:  " + GuiUtils.fontStyle_cn
                                + "\n英文字体:  " + GuiUtils.fontStyle + "\nUnicode字体:  " + GuiUtils.fontStyle_un + "\n", "Font",
                        JOptionPane.INFORMATION_MESSAGE, fontIcon);
            }
        });
        helpMenu.addSeparator(); // 分隔符
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setIcon(GuiUtils.getIcon("img/icon/cgt_About.png", kit));
        helpMenu.add(aboutItem);
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiUtils.showAboutMessage(kit, contextPanel, cgt_propsMap);
            }
        });
        helpMenu.addSeparator(); // 分隔符
        JMenuItem donateItem = new JMenuItem("Donate");
        final Icon donateIcon = GuiUtils.getIcon("img/icon/cgt_Donate.png", kit);
        donateItem.setIcon(donateIcon);
        helpMenu.add(donateItem);
        donateItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String donateWeixin = "file://" + new File(GuiUtils.getActualPath("img/donate/weixin.png")).toURI().toURL().getPath();
                    String donateAlipay = "file://" + new File(GuiUtils.getActualPath("img/donate/alipay.jpg")).toURI().toURL().getPath();
                    JOptionPane.showMessageDialog(contextPanel, "<html><body><table><tr><td>微信</td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td>支付宝</td></tr><tr><td><img src=\"" + donateWeixin
                                    + "\"/></td><td></td><td><img src=\"" + donateAlipay + "\"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr></table></body></html>",
                            "Donate", JOptionPane.INFORMATION_MESSAGE, donateIcon);
                } catch (Exception se) {
                    GuiUtils.log(se);
                }
            }
        });

    }

}
