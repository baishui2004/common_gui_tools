package bs.tool.commongui.plugins;

import bs.tool.commongui.AbstractGuiJPanel;
import bs.tool.commongui.GuiUtils;
import bs.tool.commongui.utils.FileUtils;
import bs.tool.commongui.utils.SearchFileNameParams;
import bs.tool.commongui.utils.SimpleMouseListener;
import org.apache.commons.codec.digest.DigestUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 计算文件数字签名.
 */
public class FileDigitalSignature extends AbstractGuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 文本/文件夹路径表单.
     */
    private JTextField digitalPathTextField = new JTextField();
    /**
     * 文本/文件夹路径选择.
     */
    private JFileChooser digitalPathChooser = new JFileChooser();

    /**
     * 是否计算MD5.
     */
    private boolean ifDigitalMd5 = true;

    /**
     * 是否计算SHA1.
     */
    private boolean ifDigitalSha1 = false;

    /**
     * 计算按钮.
     */
    private JButton digitalButton;

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
     * 计算结果文本域.
     */
    private JTextArea resultTextArea = createJTextArea(GuiUtils.font14_un);

    public FileDigitalSignature() {

        // 主面板：边界布局，分North、Center两部分，North用于放置输入及条件控件，Center是放置计算结果输出
        setLayout(new BorderLayout());

        // 输入及条件Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 1));

        // 文本/文件夹表单、计算按钮Panel
        JPanel fileChooAndDetectPanel = new JPanel(new BorderLayout());
        // 文本/文件夹表单
        addJLabel(fileChooAndDetectPanel, "  文件/文件夹: ", GuiUtils.font14_cn, BorderLayout.WEST);
        JPanel fileChooPanel = new JPanel(new BorderLayout());
        fileChooPanel.add(new JPanel(), BorderLayout.NORTH);
        addJTextField(fileChooPanel, digitalPathTextField, GuiUtils.font14_un, BorderLayout.CENTER);
        fileChooPanel.add(new JPanel(), BorderLayout.SOUTH);
        fileChooAndDetectPanel.add(fileChooPanel, BorderLayout.CENTER);

        JPanel buttonFlowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJButton(buttonFlowPanel, "浏览", "", GuiUtils.font12_cn,
                buttonBrowseListener(digitalPathChooser, digitalPathTextField));
        // 计算按钮
        digitalButton = createJButton("计算", "", GuiUtils.font14b_cn);
        digitalButton.addMouseListener(new SimpleMouseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                String path = digitalPathTextField.getText().trim();
                File file = new File(path);
                if (!file.exists()) {
                    showMessage("文件/文件夹不存在！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Map<String, Object> paramsMap = new HashMap<String, Object>();
                paramsMap.put("fileNameContainsText", fileNameContainsTextField.getText().trim());
                paramsMap.put("fileNameNotContainsText", fileNameNotContainsTextField.getText().trim());
                paramsMap.put("fileNameSupportRegex", fileNameSupportRegex);
                digitalButton.setEnabled(false);

                resultTextArea.setText("");
                try {
                    if (!file.isDirectory()) {
                        appendDigitalResult(resultTextArea, file, ifDigitalMd5, ifDigitalSha1);
                    } else {
                        List<File> files = new ArrayList<File>();
                        FileUtils.loopDirectory(file, files, new SearchFileNameParams(paramsMap));
                        for (File fFile : files) {
                            appendDigitalResult(resultTextArea, fFile, ifDigitalMd5, ifDigitalSha1);
                        }
                        resultTextArea.append("\nDigital files: " + files.size());
                    }
                } catch (NumberFormatException e) {
                    showMessage("取样大小必须是正整数！", "警告", JOptionPane.WARNING_MESSAGE);
                } catch (IOException e) {
                    showExceptionMessage(e);
                }
                digitalButton.setEnabled(true);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                resultTextArea.setText("");
            }
        });
        buttonFlowPanel.add(digitalButton);
        // 路径选择控件，可选择文件/文件夹
        digitalPathChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooAndDetectPanel.add(buttonFlowPanel, BorderLayout.EAST);
        inputPanel.add(fileChooAndDetectPanel);

        // 高级条件Panel
        JPanel advancePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJLabel(advancePanel, " ", GuiUtils.font12_cn);
        // 是否计算MD5
        addJCheckBox(advancePanel, "MD5 ", true, GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                ifDigitalMd5 = ((JCheckBox) event.getSource()).isSelected();
            }
        });
        // 是否计算SHA1
        addJCheckBox(advancePanel, "SHA1 ", false, GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                ifDigitalSha1 = ((JCheckBox) event.getSource()).isSelected();
            }
        });
        // 文件名包含(不包含)字符
        addJLabel(advancePanel, "  文件名包含字符: ", GuiUtils.font14_cn);
        fileNameContainsTextField = new JTextField(16);
        addJTextField(advancePanel, fileNameContainsTextField, GuiUtils.font14_un);
        addJLabel(advancePanel, "  文件名不包含字符: ", GuiUtils.font14_cn);
        fileNameNotContainsTextField = new JTextField(16);
        addJTextField(advancePanel, fileNameNotContainsTextField, GuiUtils.font14_un);
        // 是否支持正则
        addJCheckBox(advancePanel, "支持正则", false, GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                fileNameSupportRegex = ((JCheckBox) event.getSource()).isSelected();
            }
        });
        inputPanel.add(advancePanel);

        add(inputPanel, BorderLayout.NORTH);

        // 计算结果输出
        JPanel detectLogPanel = new JPanel(new BorderLayout());
        detectLogPanel.add(new JScrollPane(resultTextArea));
        add(detectLogPanel, BorderLayout.CENTER);
    }

    /**
     * append计算文件数字签名结果.
     */
    private void appendDigitalResult(JTextArea resultTextArea, File file, boolean ifDigitalMd5, boolean ifDigitalSha1)
            throws IOException {
        resultTextArea.append(file.getAbsolutePath());
        if (ifDigitalMd5) {
            resultTextArea.append("         MD5: " + digitalFileMd5(file));
        }
        if (ifDigitalSha1) {
            resultTextArea.append("         SHA1: " + digitalFileSha1(file));
        }
        resultTextArea.append("\n");
    }

    /**
     * 计算文件数字签名-MD5.
     */
    public static String digitalFileMd5(File file) throws IOException {
        return DigestUtils.md5Hex(new FileInputStream(file));
    }

    /**
     * 计算文件数字签名-SHA1.
     */
    public static String digitalFileSha1(File file) throws IOException {
        return DigestUtils.shaHex(new FileInputStream(file));
    }

}
