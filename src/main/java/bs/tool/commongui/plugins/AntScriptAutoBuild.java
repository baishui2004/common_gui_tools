package bs.tool.commongui.plugins;

import bs.tool.commongui.AbstractGuiJPanel;
import bs.tool.commongui.GuiUtils;
import bs.tool.commongui.utils.SimpleMouseListener;
import bs.tool.eclipse.ProjectPropertiesDeal;
import bs.tool.eclipse.ProjectPropertiesDealInterface;
import bs.util.io.PropertiesUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Auto Build Ant构建脚本.
 */
public class AntScriptAutoBuild extends AbstractGuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Project路径表单.
     */
    private JTextField projectPathTextField = new JTextField();
    /**
     * Project路径选择.
     */
    private JFileChooser projectPathChooser = new JFileChooser();

    /**
     * 是否备份已存在的同名构建属性脚本及脚本.
     */
    private boolean isBak = true;

    /**
     * Ant 任务.
     */
    private String taskNames = "";

    /**
     * 运行日志输出文本域.
     */
    private JTextArea runLogTextArea = createJTextArea(GuiUtils.font14_un);

    public AntScriptAutoBuild() {

        // 主面板：边界布局，分North、Center两部分，North用于放置条件控件，Center放置运行日志输出
        setLayout(new BorderLayout());

        // 输入条件/操作
        JPanel inputPanel = new JPanel(new BorderLayout());
        // Project路径选择/填写
        JPanel fileChooPanel = new JPanel(new BorderLayout());
        addJLabel(fileChooPanel, "  Project路径: ", GuiUtils.font14_cn, BorderLayout.WEST);
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.add(new JPanel(), BorderLayout.NORTH);
        addJTextField(pathPanel, projectPathTextField, GuiUtils.font14_un, BorderLayout.CENTER);
        pathPanel.add(new JPanel(), BorderLayout.SOUTH);
        fileChooPanel.add(pathPanel, BorderLayout.CENTER);
        JPanel buttonFlowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJButton(buttonFlowPanel, "浏览", "", GuiUtils.font12_cn,
                buttonBrowseListener(projectPathChooser, projectPathTextField));
        addJCheckBox(buttonFlowPanel, "备份已存在脚本", true, GuiUtils.font12_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                isBak = ((JCheckBox) event.getSource()).isSelected();
            }
        });
        // 按钮
        addJButton(buttonFlowPanel, "Build", "", GuiUtils.font14b_cn, new SimpleMouseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                String projectPath = projectPathTextField.getText().trim();
                if (!new File(projectPath).exists()) {
                    showMessage("Project路径不存在！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    bs.tool.ant.AntScriptAutoBuild build = new bs.tool.ant.AntScriptAutoBuild();
                    build.setIsBak(isBak);
                    ProjectPropertiesDealInterface propertiesDeal = new ProjectPropertiesDeal();
                    if (!propertiesDeal.isJavaOrJavaWebEclipseProject(projectPath)) {
                        runLogTextArea.append("Error: The Path \'" + projectPath
                                + "\' not has a Eclipse Java Project, Dynamic Web Project or MyEclipse Web Project.");
                        return;
                    }
                    propertiesDeal.setRunLogTextArea(runLogTextArea);
                    propertiesDeal.deal(projectPath);
                    build.setRunLogTextArea(runLogTextArea);
                    build.autoBuild(projectPath, propertiesDeal, taskNames);
                } catch (IOException e) {
                    showExceptionMessage(e);
                } catch (URISyntaxException e) {
                    showExceptionMessage(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                runLogTextArea.setText("");
            }
        });
        // Project路径选择控件，仅可选择文件夹
        projectPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooPanel.add(buttonFlowPanel, BorderLayout.EAST);
        inputPanel.add(fileChooPanel, BorderLayout.NORTH);

        int gridRow = 5;
        int gridCol = 6;
        // Ant Task任务JPanel
        JPanel taskGridPanel = new JPanel(new GridLayout(gridRow, gridCol));
        String antTaskPropsFile = "conf/AntScriptAutoBuild/conf.properties";
        try {
            Properties taskConfProperties = PropertiesUtils.getProperties(GuiUtils.getActualPath(antTaskPropsFile));

            String baseTask = taskConfProperties.getProperty("baseTask").trim();
            String javaTask = taskConfProperties.getProperty("javaTask").trim();
            String javaWebTask = taskConfProperties.getProperty("javaWebTask").trim();
            String generalTask = taskConfProperties.getProperty("generalTask").trim();
            String specialTask = taskConfProperties.getProperty("specialTask").trim();

            addTaskCheckBox(taskGridPanel, "  Java Base", baseTask + "," + javaTask, baseTask, gridCol);
            addTaskCheckBox(taskGridPanel, "  Java Web ", javaWebTask, baseTask, gridCol);
            addTaskCheckBox(taskGridPanel, "  General  ", generalTask, baseTask, gridCol);
            addTaskCheckBox(taskGridPanel, "  Special  ", specialTask, baseTask, gridCol);
        } catch (IOException e) {
            logLoadPropertiesException(antTaskPropsFile, e);
        }
        inputPanel.add(taskGridPanel, BorderLayout.CENTER);

        add(inputPanel, BorderLayout.NORTH);

        add(new JScrollPane(runLogTextArea), BorderLayout.CENTER);
    }

    /**
     * 添加Ant Task JCheckBox选择框.
     */
    private void addTaskCheckBox(JPanel taskGridPanel, String labelName, String taskNames, String baseTask, int gridCol) {
        addJLabel(taskGridPanel, labelName + " 任务：", GuiUtils.font14_cn);
        String[] tasks = taskNames.split(",");
        int row = tasks.length / (gridCol - 1);
        row = row * (gridCol - 1) == tasks.length ? row : row + 1;
        int num = row * gridCol - 1;

        for (int i = 0; i < num; i++) {
            String title = "";
            if (i < tasks.length + i / (gridCol - 1) && !(i % (gridCol - 1) == 0 && i != 0)) {
                title = tasks[i - i / (gridCol - 1)];
            }
            if ("".equals(title)) {
                addJLabel(taskGridPanel, title, GuiUtils.font14_cn);
            } else {
                boolean isSelected = false;
                if (("," + baseTask + ",").contains("," + title + ",") || "jar".equals(title)) {
                    isSelected = true;
                }
                JCheckBox taskBox = createJCheckBox(title, isSelected, GuiUtils.font16, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        JCheckBox checkBox = (JCheckBox) event.getSource();
                        setTaskNames(checkBox.getText(), checkBox.isSelected());
                    }
                });
                if (isSelected) {
                    if ("base".equals(title)) {
                        taskBox.setEnabled(false);
                    }
                    this.taskNames += "," + title;
                }
                taskGridPanel.add(taskBox);
            }
        }

    }

    /**
     * 设置被选择的Ant任务.
     */
    private void setTaskNames(String taskName, boolean add) {
        if (("," + this.taskNames + ",").contains("," + taskName + ",")) {
            if (!add) {
                this.taskNames = ("," + this.taskNames + ",").replace("," + taskName + ",", "");
            }
        } else {
            if (add) {
                this.taskNames = this.taskNames + "," + taskName;
            }
        }
        if (this.taskNames.startsWith(",")) {
            this.taskNames = this.taskNames.substring(1);
        }
    }

}
