package bs.tool.commongui.plugins;

import bs.tool.commongui.GuiJPanel;
import bs.tool.commongui.GuiUtils;
import bs.tool.commongui.utils.TimeUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具.
 */
public class TimeTool extends GuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 时间格式.
     */
    private String[] timeFormatter = new String[]{TimeUtils.Formatter, TimeUtils.Formatter_Millisecond, TimeUtils.Formatter_zh, TimeUtils.Formatter_year, TimeUtils.Formatter_zh_year};

    /**
     * 当前时间格式.
     */
    private String curTimeFormatter = timeFormatter[0];

    /**
     * 时间字符表单.
     */
    private JTextField timeStrTextField = new JTextField(new SimpleDateFormat(curTimeFormatter).format(new Date()));

    /**
     * 转换.
     */
    private JButton convertButton;

    /**
     * 时间戳表单.
     */
    private JTextField timestampTextField = new JTextField(Long.toString(System.currentTimeMillis()));

    /**
     * 还原.
     */
    private JButton revertButton;

    public TimeTool() {

        // 主面板：边界布局，分North、Center两部分，North用于放置输入及条件控件，Center是放置空面面板
        setLayout(new BorderLayout());

        // 输入及条件Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 1));

        // 时间字符表单、时间格式、转换按钮Panel
        JPanel convertPanel = new JPanel(new BorderLayout());
        addJLabel(convertPanel, "  时间字符: ", GuiUtils.font14_cn, BorderLayout.WEST);
        // 时间字符表单
        addJTextField(convertPanel, timeStrTextField, GuiUtils.font14_un, BorderLayout.CENTER);

        JPanel convertFlowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        // 时间格式
        addJComboBox(convertFlowPanel, timeFormatter, GuiUtils.font13_cn, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                curTimeFormatter = ((JComboBox) event.getSource()).getSelectedItem().toString();
            }
        });
        // 当前时间
        addJButton(convertFlowPanel, "当前时间", "", GuiUtils.font13_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Date curDate = new Date();
                timeStrTextField.setText(new SimpleDateFormat(curTimeFormatter).format(curDate));
                timestampTextField.setText(Long.toString(curDate.getTime()));
            }
        });
        // 复制
        addJButton(convertFlowPanel, "复制", "", GuiUtils.font13_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                StringSelection selection = new StringSelection(timeStrTextField.getText());
                // 获取系统剪切板，复制时间字符
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            }
        });
        // 转换按钮
        convertButton = createJButton("转换", "", GuiUtils.font14b_cn);
        convertButton.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent event) {
                String timeStr = timeStrTextField.getText().trim();
                if (timeStr.length() == 0) {
                    showMessage("没有输入时间字符！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                timestampTextField.setText("");
                convertButton.setEnabled(false);
                try {
                    timestampTextField.setText(Long.toString(new SimpleDateFormat(curTimeFormatter).parse(timeStr).getTime()));
                } catch (Exception e) {
                    showExceptionMessage(e);
                }
                convertButton.setEnabled(true);
            }

            public void mousePressed(MouseEvent e) {
                timestampTextField.setText("");
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

        // 时间戳表单、还原按钮Panel
        JPanel revertPanel = new JPanel(new BorderLayout());
        addJLabel(revertPanel, "  时 间 戳: ", GuiUtils.font14_cn, BorderLayout.WEST);
        // 时间戳表单
        addJTextField(revertPanel, timestampTextField, GuiUtils.font14_un, BorderLayout.CENTER);

        JPanel revertFlowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        // 复制
        addJButton(revertFlowPanel, "复制", "", GuiUtils.font13_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                StringSelection selection = new StringSelection(timestampTextField.getText());
                // 获取系统剪切板，复制时间戳
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            }
        });

        revertButton = createJButton("还原", "", GuiUtils.font14b_cn);
        revertButton.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent event) {
                String timestamp = timestampTextField.getText().trim();
                if (timestamp.length() == 0) {
                    showMessage("没有输入时间戳！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                timeStrTextField.setText("");
                revertButton.setEnabled(false);
                try {
                    timeStrTextField.setText(new SimpleDateFormat(curTimeFormatter).format(new Date(Long.parseLong(timestamp))));
                } catch (Exception e) {
                    showExceptionMessage(e);
                }
                revertButton.setEnabled(true);
            }

            public void mousePressed(MouseEvent e) {
                timeStrTextField.setText("");
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
        // 空白文本域
        JTextArea resultTextArea = createJTextArea(GuiUtils.font14_un);
        resultTextArea.setEditable(false);
        resultTextArea.setText("");
        resultPanel.add(new JScrollPane(resultTextArea));
        add(resultPanel, BorderLayout.CENTER);
    }

}
