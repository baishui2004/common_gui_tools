package bs.tool.commongui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DateFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public abstract class AbstractGuiJPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public AbstractGuiJPanel() {
    }

    /**
     * 获取当前面板.
     */
    public JPanel getContextPanel() {
        return this;
    }

    /**
     * 给面板增加指定标题及字体的Label.
     */
    public void addJLabel(JPanel panel, String name, Font font) {
        JLabel label = new JLabel(name);
        label.setFont(font);
        panel.add(label);
    }

    /**
     * 给面板增加指定标题及字体的Label，并指定布局位置.
     */
    public void addJLabel(JPanel panel, String name, Font font, String layout) {
        JLabel label = new JLabel(name);
        label.setFont(font);
        panel.add(label, layout);
    }

    /**
     * 给面板增加指定字体的JTextField.
     */
    public void addJTextField(JPanel panel, JTextField textField, Font font) {
        textField.setFont(font);
        panel.add(textField);
    }

    /**
     * 给面板增加指定字体的JTextField，并指定布局位置.
     */
    public void addJTextField(JPanel panel, JTextField textField, Font font, String layout) {
        textField.setFont(font);
        panel.add(textField, layout);
    }

    /**
     * 创建指定字体、自动换行的JTextArea.
     */
    public JTextArea createJTextArea(Font font) {
        return createJTextArea(font, "");
    }

    /**
     * 创建指定字体、自动换行的JTextArea.
     */
    public JTextArea createJTextArea(Font font, String defaultText) {
        JTextArea textArea = new JTextArea(defaultText);
        textArea.setFont(font);
        textArea.setLineWrap(true);
        return textArea;
    }

    /**
     * 浏览按钮对于JFileChooser表单选择文件/文件夹的事件.
     */
    public ActionListener buttonBrowseListener(final JFileChooser fileChooser, final JTextField pathTextField) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                fileChooserBrowse(fileChooser, pathTextField);
            }
        };
    }

    /**
     * JFileChooser表单选择文件/文件夹.
     */
    public void fileChooserBrowse(final JFileChooser fileChooser, final JTextField pathTextField) {
        if (fileChooser.showDialog(getContextPanel(), "确定") != JFileChooser.CANCEL_OPTION) {
            File selectFile = fileChooser.getSelectedFile();
            if (selectFile != null) {
                pathTextField.setText(selectFile.getAbsolutePath());
            }
        }
    }

    /**
     * 图片选择.
     */
    public JFileChooser createImageChooser() {
        return createFileChooser("png, jpg, jpeg, gif, bmp", "png", "jpg", "jpeg", "gif", "bmp");
    }

    /**
     * 文件选择.
     *
     * @param description 描述
     * @param extensions  文件后缀
     * @return
     */
    public JFileChooser createFileChooser(String description, String... extensions) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // 不显示所有文件
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(description, extensions));
        return fileChooser;
    }

    /**
     * 给面板增加指定标题、名称、字体及ActionListener的JButton.
     */
    public void addJButton(JPanel panel, String title, String name, Font font, ActionListener listener) {
        JButton button = createJButton(title, name, font);
        button.addActionListener(listener);
        panel.add(button);
    }

    /**
     * 给面板增加指定标题、名称、字体及MouseListener的JButton.
     */
    public void addJButton(JPanel panel, String title, String name, Font font, MouseListener listener) {
        JButton button = createJButton(title, name, font);
        button.addMouseListener(listener);
        panel.add(button);
    }

    /**
     * 创建指定标题、名称、字体的JButton.
     */
    public JButton createJButton(String title, String name, Font font) {
        JButton button = new JButton(title);
        button.setFont(font);
        button.setName(name);
        return button;
    }

    /**
     * 给面板增加指定标题、是否selected、字体及ActionListener的JCheckbox.
     */
    public void addJCheckBox(JPanel panel, String title, boolean isSelected, Font font, ActionListener listener) {
        panel.add(createJCheckBox(title, isSelected, font, listener));
    }

    /**
     * 给面板增加指定标题、是否selected、字体及ActionListener的JCheckbox，并指定布局位置.
     */
    public void addJCheckBox(JPanel panel, String title, boolean isSelected, Font font, ActionListener listener,
                             String layout) {
        panel.add(createJCheckBox(title, isSelected, font, listener), layout);
    }

    /**
     * 获取指定标题、是否selected、字体及ActionListener的JCheckbox.
     */
    public JCheckBox createJCheckBox(String title, boolean isSelected, Font font, ActionListener listener) {
        JCheckBox checkBox = new JCheckBox(title);
        checkBox.setFont(font);
        checkBox.setSelected(isSelected);
        checkBox.addActionListener(listener);
        return checkBox;
    }

    /**
     * 给面板增加指定下拉Items、字体及ActionListener的JComboBox.
     */
    public void addJComboBox(JPanel panel, String[] items, Font font, ActionListener listener) {
        panel.add(createJComboBox(items, font, listener));
    }

    /**
     * 给面板增加指定下拉Items、字体及ActionListener的JComboBox，并选择特定位置的Item.
     */
    public void addJComboBox(JPanel panel, String[] items, Integer selectIndex, Font font, ActionListener listener) {
        panel.add(createJComboBox(items, selectIndex, font, listener));
    }

    /**
     * 给面板增加指定下拉Items、字体及ActionListener的JComboBox，并指定布局位置.
     */
    public void addJComboBox(JPanel panel, String[] items, Font font, ActionListener listener, String layout) {
        panel.add(createJComboBox(items, font, listener), layout);
    }

    /**
     * 给面板增加指定下拉Items、字体及ActionListener的JComboBox，并指定布局位置，并选择特定位置的Item.
     */
    public void addJComboBox(JPanel panel, String[] items, Integer selectIndex, Font font, ActionListener listener,
                             String layout) {
        panel.add(createJComboBox(items, selectIndex, font, listener), layout);
    }

    /**
     * 创建指定下拉Items、字体及ActionListener的JComboBox.
     */
    public JComboBox createJComboBox(String[] items, Font font, ActionListener listener) {
        return createJComboBox(items, 0, font, listener);
    }

    /**
     * 创建指定下拉Items、字体及ActionListener的JComboBox，并选择特定位置的Item.
     */
    public JComboBox createJComboBox(String[] items, Integer selectIndex, Font font, ActionListener listener) {
        JComboBox comboBox = new JComboBox();
        comboBox.setFont(font);
        for (int i = 0; i < items.length; i++) {
            comboBox.addItem(items[i]);
        }
        comboBox.setSelectedIndex(selectIndex);
        comboBox.addActionListener(listener);
        return comboBox;
    }

    /**
     * 消息提示.
     */
    public void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    /**
     * 消息提示.
     */
    public void showTextAreaMessage(String message, String title, int type, Font font, Color background) {
        JTextArea textArea = new JTextArea(message);
        if (font == null) {
            textArea.setFont(GuiUtils.font14_un);
        } else {
            textArea.setFont(font);
        }
        textArea.setEditable(false);
        if (background == null) {
            textArea.setBackground(new Color(214, 217, 223));
        } else {
            textArea.setBackground(background);
        }
        JOptionPane.showMessageDialog(this, textArea, title, type);
    }

    /**
     * 确认提示.
     */
    public int showConfirmMessage(String message, String title, int type) {
        return JOptionPane.showConfirmDialog(this, message, title, type);
    }

    /**
     * 格式化数字-三位有效数字.
     */
    public DecimalFormat formatDouble3 = new DecimalFormat("0.000");
    /**
     * 格式化数字-六位有效数字.
     */
    public DecimalFormat formatDouble6 = new DecimalFormat("0.000000");

    /**
     * 日期格式化Formatter yyyyMMdd.
     */
    public final static String FORMATTER_YYYYMMDD = "yyyyMMdd";
    /**
     * 日期格式化Formatter yyyy-MM-dd HH:mm:ss.
     */
    public final static String FORMATTER_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间输入表单，默认格式yyyy-MM-dd HH:mm:ss.
     */
    public JFormattedTextField createDateTextField() {
        return createDateTextField(new SimpleDateFormat(FORMATTER_YYYYMMDDHHMMSS), (GuiUtils.IS_MAC ? 11 : 19), GuiUtils.font14_cn, "");
    }

    /**
     * 获取格式化时间字符串的Long型表示.
     */
    public Long getLongFormatTime(String fomartStr, SimpleDateFormat dateFormat) {
        Long time = null;
        try {
            if (fomartStr != null && fomartStr.length() != 0) {
                time = dateFormat.parse(fomartStr).getTime();
            }
        } catch (ParseException e) {
            GuiUtils.log(e);
        }
        return time;
    }

    /**
     * 时间输入表单. 时间格式为"yyyyMMdd"或"yyyy-MM-dd"或"yyyy.MM.dd"或"yyyy,MM,dd"或"yyyy-MM-dd HH:mm:ss".
     */
    public JFormattedTextField createDateTextField(Format format, int columns, Font font, String defaultValue) {
        JFormattedTextField field = new JFormattedTextField(format);
        field.setColumns(columns);
        field.setFont(font);
        field.setText(defaultValue);
        /**
         * <pre>
         * JFormattedTextField.REVERT            恢复显示以匹配 getValue，这可能丢失当前的编辑内容。
         * JFormattedTextField.COMMIT            提交当前值。如果 AbstractFormatter 不认为所编辑的值是合法值，则抛出 ParseException，然后不更改该值并保留已编辑的值。
         * JFormattedTextField.COMMIT_OR_REVERT  与COMMIT 类似，但是如果该值不是合法的，则其行为类似于 REVERT。
         * JFormattedTextField.PERSIST           不执行任何操作，不获取新的 AbstractFormatter 也不更新该值。
         * 默认值为 JFormattedTextField.COMMIT_OR_REVERT。
         * </pre>
         */
        field.setFocusLostBehavior(JFormattedTextField.COMMIT);
        field.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent event) {
                JFormattedTextField field = (JFormattedTextField) event.getSource();
                String value = field.getText().trim();
                if (value.length() == 8 || value.length() == 10) {
                    String mayDate = value.replace("-", "").replace(".", "").replace(",", "");
                    if (mayDate.length() == 8) {
                        try {
                            new SimpleDateFormat(FORMATTER_YYYYMMDD).parse(mayDate);
                            field.setText(mayDate.substring(0, 4) + "-" + mayDate.substring(4, 6) + "-"
                                    + mayDate.substring(6, 8) + " 00:00:00");
                        } catch (ParseException e) {
                            GuiUtils.log(e);
                        }
                    }
                }
                if (value.length() != 0 && !field.isEditValid()) {
                    SimpleDateFormat dateFormat = (SimpleDateFormat) (((DateFormatter) field.getFormatter())
                            .getFormat());
                    showMessage(
                            "时间格式必须为\"yyyyMMdd\"或\"yyyy-MM-dd\"或\"yyyy.MM.dd\"或\"yyyy,MM,dd\"或\""
                                    + dateFormat.toPattern() + "\"！", "警告", JOptionPane.WARNING_MESSAGE);
                    field.setText("");
                }
            }

            @Override
            public void focusGained(FocusEvent event) {
            }
        });
        return field;
    }

    /**
     * 数字输入表单.
     */
    public JFormattedTextField createNumberTextField() {
        return createNumberTextField(new NumberFormatter(), (GuiUtils.IS_MAC ? 7 : 13), GuiUtils.font14_cn, "");
    }

    /**
     * 数字输入表单.
     */
    public JFormattedTextField createNumberTextField(NumberFormatter format, int columns, Font font, String defaultValue) {
        JFormattedTextField field = new JFormattedTextField(format);
        field.setColumns(columns);
        field.setFont(font);
        field.setText(defaultValue);
        /**
         * <pre>
         * JFormattedTextField.REVERT            恢复显示以匹配 getValue，这可能丢失当前的编辑内容。
         * JFormattedTextField.COMMIT            提交当前值。如果 AbstractFormatter 不认为所编辑的值是合法值，则抛出 ParseException，然后不更改该值并保留已编辑的值。
         * JFormattedTextField.COMMIT_OR_REVERT  与COMMIT 类似，但是如果该值不是合法的，则其行为类似于 REVERT。
         * JFormattedTextField.PERSIST           不执行任何操作，不获取新的 AbstractFormatter 也不更新该值。
         * 默认值为 JFormattedTextField.COMMIT_OR_REVERT。
         * </pre>
         */
        field.setFocusLostBehavior(JFormattedTextField.COMMIT);
        field.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent event) {
                JFormattedTextField field = (JFormattedTextField) event.getSource();
                String value = field.getText().trim();
                if (value.length() != 0 && !field.isEditValid()) {
                    /*
                     * NumberFormat numberFormat = (NumberFormat) (((NumberFormatter)
                     * field.getFormatter()).getFormat());
                     */
                    showMessage("必须填写数字！", "警告", JOptionPane.WARNING_MESSAGE);
                    field.setText("");
                }
            }

            @Override
            public void focusGained(FocusEvent event) {
            }
        });
        return field;
    }

    /**
     * 文件大小单位.
     */
    public String[] fileSizeUnit = new String[]{GuiUtils.FileSize_G, GuiUtils.FileSize_M, GuiUtils.FileSize_KB,
            GuiUtils.FileSize_Byte};

    /**
     * 文件大小单位下拉框.
     */
    public JComboBox createFileSizeUnitBox(Font font) {
        JComboBox fileSizeUnitBox = new JComboBox();
        fileSizeUnitBox.setFont(font);
        for (int i = 0; i < fileSizeUnit.length; i++) {
            fileSizeUnitBox.addItem(fileSizeUnit[i]);
        }
        fileSizeUnitBox.setSelectedIndex(1);
        return fileSizeUnitBox;
    }

    /**
     * 异常输出.
     *
     * @param e 异常
     */
    public void showExceptionMessage(Exception e) {
        showExceptionMessage(e, e.getClass().getName() + ": " + e.getMessage());
    }

    /**
     * 异常输出.
     *
     * @param e       异常
     * @param message 提示信息
     */
    public void showExceptionMessage(Exception e, String message) {
        GuiUtils.log(e);
        showMessage(message, "异常", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 加载配置异常输出.
     *
     * @param filePath 配置文件路径
     * @param e        异常
     */
    public void logLoadPropertiesException(String filePath, Exception e) {
        GuiUtils.log("加载配置\"" + filePath + "\"出错！", e);
    }

}
