package bs.tool.commongui.plugins.more;

import bs.tool.commongui.GuiJPanel;
import bs.tool.commongui.GuiUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Code格式化.
 */
public class CodeFormatter extends GuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 文本域.
     */
    private JTextArea textArea = createJTextArea(GuiUtils.font14_un);

    /**
     * Code类型.
     */
    private String[] codeTypes = new String[]{GuiUtils.CODE_TYPE_JSON, GuiUtils.CODE_TYPE_XML, GuiUtils.CODE_TYPE_JAVA, GuiUtils.CODE_TYPE_JS, GuiUtils.CODE_TYPE_PYTHON, GuiUtils.CODE_TYPE_SQL};

    /**
     * 当前Code类型.
     */
    private String curCodeType = codeTypes[0];

    public CodeFormatter() {
        // 边界布局
        setLayout(new BorderLayout());
        JPanel textAreaPanel = new JPanel(new GridLayout(1, 1));
        add(textAreaPanel, BorderLayout.CENTER);

        JPanel textPanel = new JPanel(new BorderLayout());
        addJLabel(textPanel, " Code: ", GuiUtils.font14b_cn, BorderLayout.WEST);
        textPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        textAreaPanel.add(textPanel);

        // East，操作区域，使用BorderLayout布局
        JPanel actionPanel = new JPanel(new BorderLayout());
        add(actionPanel, BorderLayout.EAST);

        // 填充
        actionPanel.add(new JPanel(), BorderLayout.CENTER);

        // 放置按钮
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1));
        actionPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel codeTypePanel = new JPanel(new FlowLayout());
        addJLabel(codeTypePanel, "类型:", GuiUtils.font14b_cn);
        // 类型下拉框
        addJComboBox(codeTypePanel, codeTypes, GuiUtils.font13, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                curCodeType = ((JComboBox) event.getSource()).getSelectedItem().toString();
            }
        });
        buttonPanel.add(codeTypePanel);
        buttonPanel.add(new JPanel()); // 仅做填充

        // 格式化
        addJButton(buttonPanel, " 格式化 ", "", GuiUtils.font14b_cn, new MouseListener() {
            public void mouseReleased(MouseEvent event) {
                String input = textArea.getText();
                if (curCodeType.equals(GuiUtils.CODE_TYPE_JSON)) {
                    textArea.setText(prettyJson(input));
                } else {
                    showMessage("当前暂不支持非JSON格式化！", "提示", JOptionPane.INFORMATION_MESSAGE);
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
        });

        // 反格式化
        addJButton(buttonPanel, " 反格式化 ", "", GuiUtils.font14b_cn, new MouseListener() {
            public void mouseReleased(MouseEvent event) {
                String input = textArea.getText();
                if (curCodeType.equals(GuiUtils.CODE_TYPE_JSON)) {
                    textArea.setText(unPrettyJson(input));
                } else {
                    showMessage("当前暂不支持非JSON反格式化！", "提示", JOptionPane.INFORMATION_MESSAGE);
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
        });

        // 清空
        addJButton(buttonPanel, " 清  空 ", "", GuiUtils.font14_cn, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                textArea.setText("");
            }
        });
    }

    /**
     * 格式化json.
     *
     * @param jsonStr
     * @return
     */
    public String prettyJson(String jsonStr) {
        int features = JSON.DEFAULT_GENERATE_FEATURE;
        features = SerializerFeature.config(features, SerializerFeature.PrettyFormat, true);
        features = SerializerFeature.config(features, SerializerFeature.WriteTabAsSpecial, false); // 不起作用
        Object json = JSON.parse(jsonStr);
        return JSON.toJSONString(json, features);
    }

    /**
     * 反格式化json.
     *
     * @param jsonStr
     * @return
     */
    public String unPrettyJson(String jsonStr) {
        Object json = JSON.parse(jsonStr);
        return JSON.toJSONString(json);
    }

}
