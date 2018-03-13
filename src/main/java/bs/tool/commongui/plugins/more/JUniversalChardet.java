package bs.tool.commongui.plugins.more;

import bs.tool.commongui.GuiJPanel;
import bs.tool.commongui.GuiUtils;
import bs.tool.commongui.utils.FileUtils;
import bs.tool.commongui.utils.SearchFileNameParams;
import bs.util.io.PropertiesUtils;
import org.mozilla.universalchardet.UniversalDetector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * 文本编码识别.
 */
public class JUniversalChardet extends GuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 探测文本/文件夹路径表单.
     */
    private JTextField detectPathTextField = new JTextField();
    /**
     * 探测文本/文件夹路径选择.
     */
    private JFileChooser detectPathChooser = new JFileChooser();

    /**
     * 探测按钮.
     */
    private JButton detectButton;

    /**
     * 取样大小表单.
     */
    private JTextField detectSizeTextField = new JTextField("4096", 7);

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
    private boolean fileNameSupportRegex = true;

    /**
     * 探测结果文本域.
     */
    private JTextArea resultTextArea = createJTextArea(GuiUtils.font14_un);

    public JUniversalChardet() {

        // 主面板：边界布局，分North、Center两部分，North用于放置输入及条件控件，Center是放置探测结果输出
        setLayout(new BorderLayout());

        // 输入及条件Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 1));

        // 探测文本/文件夹表单、探测按钮Panel
        JPanel fileChooAndDetectPanel = new JPanel(new BorderLayout());
        // 探测文本/文件夹表单
        addJLabel(fileChooAndDetectPanel, "  探测文本/文件夹: ", GuiUtils.font14_cn, BorderLayout.WEST);
        JPanel fileChooPanel = new JPanel(new BorderLayout());
        fileChooPanel.add(new JPanel(), BorderLayout.NORTH);
        addJTextField(fileChooPanel, detectPathTextField, GuiUtils.font14_un, BorderLayout.CENTER);
        fileChooPanel.add(new JPanel(), BorderLayout.SOUTH);
        fileChooAndDetectPanel.add(fileChooPanel, BorderLayout.CENTER);

        JPanel buttonFlowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJButton(buttonFlowPanel, "浏览", "", GuiUtils.font12_cn,
                buttonBrowseListener(detectPathChooser, detectPathTextField));
        // 探测按钮
        detectButton = createJButton("探测", "", GuiUtils.font14b_cn);
        detectButton.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent event) {
                String path = detectPathTextField.getText().trim();
                File file = new File(path);
                if (!file.exists()) {
                    showMessage("探测文本/文件夹不存在！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Map<String, Object> paramsMap = new HashMap<String, Object>();
                paramsMap.put("fileNameContainsText", fileNameContainsTextField.getText().trim());
                paramsMap.put("fileNameNotContainsText", fileNameNotContainsTextField.getText().trim());
                paramsMap.put("fileNameSupportRegex", fileNameSupportRegex);
                detectButton.setEnabled(false);

                resultTextArea.setText("");
                try {
                    int detectLength = Integer.parseInt(detectSizeTextField.getText().trim());
                    if (!file.isDirectory()) {
                        appendDetectResult(resultTextArea, file, detectLength);
                    } else {
                        List<File> files = new ArrayList<File>();
                        FileUtils.loopDirectory(file, files, new SearchFileNameParams(paramsMap));
                        for (File fFile : files) {
                            appendDetectResult(resultTextArea, fFile, detectLength);
                        }
                        resultTextArea.append("\nCount files: " + files.size());
                    }
                } catch (NumberFormatException e) {
                    showMessage("取样大小必须是正整数！", "警告", JOptionPane.WARNING_MESSAGE);
                } catch (IOException e) {
                    showExceptionMessage(e);
                }
                detectButton.setEnabled(true);
            }

            public void mousePressed(MouseEvent e) {
                resultTextArea.setText("");
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });
        buttonFlowPanel.add(detectButton);
        // 探测路径选择控件
        detectPathChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // 可选择文件/文件夹
        fileChooAndDetectPanel.add(buttonFlowPanel, BorderLayout.EAST);
        inputPanel.add(fileChooAndDetectPanel);

        // 高级条件Panel
        JPanel advancePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        // 取样大小
        addJLabel(advancePanel, " ", GuiUtils.font12_cn);
        addJLabel(advancePanel, "取样大小: ", GuiUtils.font14_cn);
        addJTextField(advancePanel, detectSizeTextField, GuiUtils.font14_cn);
        addJLabel(advancePanel, "Byte", GuiUtils.font14_cn);

        // 文件名包含(不包含)字符
        String confPropsFile = "conf/JUniversalChardet/searchFiletypes.properties";
        String containsFiletypes = "";
        String notContainsFiletypes = "";
        try {
            Properties confProperties = PropertiesUtils.getProperties(GuiUtils.getActualPath(confPropsFile));
            containsFiletypes = confProperties.getProperty("ContainsFiletypes").trim();
            notContainsFiletypes = confProperties.getProperty("NotContainsFiletypes").trim();
        } catch (IOException e) {
            logLoadPropertiesException(confPropsFile, e);
        }
        addJLabel(advancePanel, " 文件名包含字符: ", GuiUtils.font14_cn);
        String fileNameContains = containsFiletypes.length() > 0 ? "\\.(" + containsFiletypes.replace(",", "|") + ")$"
                : "";
        fileNameContainsTextField = new JTextField(fileNameContains, 15);
        addJTextField(advancePanel, fileNameContainsTextField, GuiUtils.font14_un);
        addJLabel(advancePanel, "  文件名不包含字符: ", GuiUtils.font14_cn);
        String fileNameNotContains = notContainsFiletypes.length() > 0 ? "\\.("
                + notContainsFiletypes.replace(",", "|") + ")$" : "";
        fileNameNotContainsTextField = new JTextField(fileNameNotContains, 15);
        addJTextField(advancePanel, fileNameNotContainsTextField, GuiUtils.font14_un);
        // 是否支持正则
        addJCheckBox(advancePanel, "支持正则", true, GuiUtils.font14_cn, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                fileNameSupportRegex = ((JCheckBox) event.getSource()).isSelected();
            }
        });
        inputPanel.add(advancePanel);

        add(inputPanel, BorderLayout.NORTH);

        // 探测结果输出
        JPanel detectLogPanel = new JPanel(new BorderLayout());
        detectLogPanel.add(new JScrollPane(resultTextArea));
        add(detectLogPanel, BorderLayout.CENTER);
    }

    /**
     * append探测文本编码结果.
     */
    private void appendDetectResult(JTextArea resultTextArea, File file, int detectLength) throws IOException {
        resultTextArea.append(file.getAbsolutePath() + "         Charset: " + detectFileCharset(file, detectLength)
                + "\n");
    }

    /**
     * 探测文本编码.
     */
    public static String detectFileCharset(File file, int detectLength) throws IOException {
        String charset = null;
        FileInputStream fis = null;
        try {
            byte[] buf = new byte[detectLength];
            fis = new FileInputStream(file);
            UniversalDetector detector = new UniversalDetector(null);
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
            charset = detector.getDetectedCharset();
            detector.reset();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return charset;
    }

}
