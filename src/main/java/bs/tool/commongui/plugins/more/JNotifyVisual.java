package bs.tool.commongui.plugins.more;

import bs.org.suite.tool.JNotifyUtils;
import bs.tool.commongui.GuiJPanel;
import bs.tool.commongui.GuiUtils;
import net.contentobjects.jnotify.JNotifyException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件(夹)变化监控，适合Windows、Linux、Mac系统.
 */
public class JNotifyVisual extends GuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 监控文件夹路径表单.
     */
    private JTextField monitorPathTextField = new JTextField();
    /**
     * 监控文件夹路径选择.
     */
    private JFileChooser monitorPathChooser = new JFileChooser();

    /**
     * 监控按钮.
     */
    private JButton monitorButton;
    /**
     * 停止监控按钮.
     */
    private JButton stopMonitorButton;

    /**
     * 是否保存监控日志.
     */
    private boolean saveLog = false;
    /**
     * 监控日志文件路径表单.
     */
    private JTextField monitorLogFilePathTextField = new JTextField();
    /**
     * 监控日志文件选择.
     */
    private JFileChooser monitorLogFileChooser = new JFileChooser(".");

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
     * 记录日志文本域.
     */
    private JTextArea logTextArea = createJTextArea(GuiUtils.font14_un);

    /**
     * 高级条件面板.
     */
    private JPanel advanceConditionPanel;

    /**
     * 监控.
     */
    private JNotifyUtils notify;
    /**
     * 监控ID.
     */
    private int watchId;

    public JNotifyVisual() {

        // 主面板：边界布局，分North、Center两部分，North用于放置条件控件，Center是放置高级(条件)及监控日志输出
        setLayout(new BorderLayout());

        // 输入/操作
        JPanel inputPanel = new JPanel(new GridLayout(2, 1));
        add(inputPanel, BorderLayout.NORTH);

        // 监控文件夹选择/填写
        JPanel fileChooPanel = new JPanel(new BorderLayout());
        addJLabel(fileChooPanel, "  监控路径: ", GuiUtils.font14_cn, BorderLayout.WEST);
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.add(new JPanel(), BorderLayout.NORTH);
        addJTextField(pathPanel, monitorPathTextField, GuiUtils.font14_un, BorderLayout.CENTER);
        pathPanel.add(new JPanel(), BorderLayout.SOUTH);
        fileChooPanel.add(pathPanel, BorderLayout.CENTER);
        JPanel buttonFlowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJButton(buttonFlowPanel, "浏览", "", GuiUtils.font12_cn,
                buttonBrowseListener(monitorPathChooser, monitorPathTextField));
        // 监控按钮
        monitorButton = createJButton("监控", "", GuiUtils.font14b_cn);
        monitorButton.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent event) {
                String path = monitorPathTextField.getText().trim();
                if (!new File(path).exists()) {
                    showMessage("监控路径不存在！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                } else if (!new File(path).isDirectory()) {
                    showMessage("只能监控文件夹！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Map<String, Object> paramsMap = new HashMap<String, Object>();
                paramsMap.put("fileNameContainsText", fileNameContainsTextField.getText().trim());
                paramsMap.put("fileNameNotContainsText", fileNameNotContainsTextField.getText().trim());
                paramsMap.put("fileNameSupportRegex", fileNameSupportRegex);
                paramsMap.put("folderPathContainsText", folderPathContainsTextField.getText().trim());
                paramsMap.put("folderPathNotContainsText", folderPathNotContainsTextField.getText().trim());
                paramsMap.put("folderPathSupportRegex", folderPathSupportRegex);
                if (saveLog) {
                    String monitorLogFilePath = monitorLogFilePathTextField.getText().trim();
                    if (monitorLogFilePath.length() == 0) {
                        showMessage("必须选择监控日志路径！", "警告", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    notify = new JNotifyUtils(logTextArea, monitorLogFilePath);
                } else {
                    notify = new JNotifyUtils(logTextArea);
                }
                try {
                    watchId = notify.startMonitor(path, paramsMap);
                    monitorButton.setEnabled(false);
                    stopMonitorButton.setEnabled(true);
                } catch (JNotifyException e) {
                    showExceptionMessage(e);
                } catch (IOException e) {
                    showExceptionMessage(e);
                }
            }

            public void mousePressed(MouseEvent e) {
                logTextArea.setText("");
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });
        buttonFlowPanel.add(monitorButton);
        // 监控路径选择控件
        monitorPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 仅可选择文件夹
        // 停止监控按钮
        stopMonitorButton = createJButton("停止监控", "", GuiUtils.font14b_cn);
        stopMonitorButton.setEnabled(false);
        stopMonitorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    notify.stopMonitor(watchId);
                    monitorButton.setEnabled(true);
                    stopMonitorButton.setEnabled(false);
                } catch (JNotifyException e) {
                    showExceptionMessage(e);
                }
            }
        });
        buttonFlowPanel.add(stopMonitorButton);
        fileChooPanel.add(buttonFlowPanel, BorderLayout.EAST);
        inputPanel.add(fileChooPanel);

        // 监控日志路径填写
        JPanel logChooPanel = new JPanel(new BorderLayout());
        JPanel logBorderPanel = new JPanel(new BorderLayout());
        logBorderPanel.add(new JPanel(), BorderLayout.NORTH);
        JPanel logFlowPanel = new JPanel(new FlowLayout());
        addJLabel(logFlowPanel, "", GuiUtils.font14_cn);
        // 是否记录监控日志，默认否
        addJCheckBox(logFlowPanel, "保存监控日志", false, GuiUtils.font14_cn, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (((JCheckBox) event.getSource()).isSelected()) {
                    saveLog = true;
                    monitorLogFilePathTextField.setEnabled(true);
                } else {
                    saveLog = false;
                    monitorLogFilePathTextField.setEnabled(false);
                }
            }
        });
        addJLabel(logFlowPanel, " 路径: ", GuiUtils.font14_cn);
        logBorderPanel.add(logFlowPanel, BorderLayout.CENTER);
        logChooPanel.add(logBorderPanel, BorderLayout.WEST);

        JPanel logPathPanel = new JPanel(new BorderLayout());
        logPathPanel.add(new JPanel(), BorderLayout.NORTH);
        monitorLogFilePathTextField.setEnabled(false);
        monitorLogFilePathTextField.setEditable(false);
        monitorLogFilePathTextField.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                fileChooserBrowse(monitorLogFileChooser, monitorLogFilePathTextField);
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });
        addJTextField(logPathPanel, monitorLogFilePathTextField, GuiUtils.font14_un, BorderLayout.CENTER);
        // 监控路径选择控件
        monitorLogFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // 仅可选择文件
        logPathPanel.add(new JPanel(), BorderLayout.SOUTH);
        logChooPanel.add(logPathPanel, BorderLayout.CENTER);

        JPanel logButtonPanel = new JPanel(new BorderLayout());
        logButtonPanel.add(new JPanel(), BorderLayout.NORTH);
        // 展开/收缩高级(条件)按钮
        addJButton(logButtonPanel, "高级", "", GuiUtils.font12_cn, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                advanceConditionPanel.setVisible(!advanceConditionPanel.isVisible());
                getContextPanel().revalidate();
            }
        });
        logButtonPanel.add(new JPanel(), BorderLayout.SOUTH);
        addJLabel(logButtonPanel, "                          ", GuiUtils.font12_cn, BorderLayout.WEST);
        addJLabel(logButtonPanel, " ", GuiUtils.font12_cn, BorderLayout.EAST);
        logChooPanel.add(logButtonPanel, BorderLayout.EAST);
        inputPanel.add(logChooPanel);

        // 高级(条件)及监控日志输出面板，使用边界布局，North为高级(条件)，Center为监控日志输出
        JPanel advanceAndLogPanel = new JPanel(new BorderLayout());
        // 高级(条件
        advanceConditionPanel = new JPanel(new GridLayout(2, 1));
        advanceConditionPanel.setVisible(false);
        advanceAndLogPanel.add(advanceConditionPanel, BorderLayout.NORTH);

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

        // 监控日志输出
        JPanel monitorLogPanel = new JPanel(new BorderLayout());
        monitorLogPanel.add(new JScrollPane(logTextArea));
        advanceAndLogPanel.add(monitorLogPanel, BorderLayout.CENTER);
        add(advanceAndLogPanel, BorderLayout.CENTER);
    }

}
