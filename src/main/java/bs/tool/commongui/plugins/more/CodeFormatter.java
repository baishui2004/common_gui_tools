package bs.tool.commongui.plugins.more;

import bs.tool.commongui.AbstractGuiJPanel;
import bs.tool.commongui.GuiUtils;
import bs.tool.commongui.utils.CollectionUtils;
import bs.tool.commongui.utils.SimpleMouseListener;
import com.google.gson.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Code格式化.
 */
public class CodeFormatter extends AbstractGuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 文本域.
     */
    private JTextArea textArea = createJTextArea(GuiUtils.font14_un);

    /**
     * Code类型.
     */
    private String[] codeTypes = new String[]{GuiUtils.CODE_TYPE_JSON, GuiUtils.CODE_TYPE_PROPERTIES, GuiUtils.CODE_TYPE_YML,
            GuiUtils.CODE_TYPE_XML, GuiUtils.CODE_TYPE_JAVA, GuiUtils.CODE_TYPE_JS, GuiUtils.CODE_TYPE_PYTHON, GuiUtils.CODE_TYPE_SQL};

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
            @Override
            public void actionPerformed(ActionEvent event) {
                curCodeType = ((JComboBox) event.getSource()).getSelectedItem().toString();
            }
        });
        buttonPanel.add(codeTypePanel);
        // 仅做填充
        buttonPanel.add(new JPanel());

        // 格式化
        addJButton(buttonPanel, " 格式化 ", "", GuiUtils.font14b_cn, new SimpleMouseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                String input = textArea.getText();
                if (curCodeType.equals(GuiUtils.CODE_TYPE_JSON)) {
                    textArea.setText(prettyJson(input, false));
                } else if (curCodeType.equals(GuiUtils.CODE_TYPE_PROPERTIES)) {
                    textArea.setText(prettyProperties(input, false));
                } else {
                    showMessage("当前仅支持JSON, Properties格式化！", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // 反格式化
        addJButton(buttonPanel, " 反格式化 ", "", GuiUtils.font14b_cn, new SimpleMouseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                String input = textArea.getText();
                if (curCodeType.equals(GuiUtils.CODE_TYPE_JSON)) {
                    textArea.setText(unPrettyJson(input));
                } else {
                    showMessage("当前仅支持JSON反格式化！", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // 初始化元素值
        addJButton(buttonPanel, " 初始化元素值 ", "", GuiUtils.font14b_cn, new SimpleMouseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                String input = textArea.getText();
                if (curCodeType.equals(GuiUtils.CODE_TYPE_JSON)) {
                    textArea.setText(prettyJson(input, true));
                } else if (curCodeType.equals(GuiUtils.CODE_TYPE_PROPERTIES)) {
                    textArea.setText(prettyProperties(input, true));
                } else {
                    showMessage("当前仅支持JSON, Properties初始化元素值！", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // 清空
        addJButton(buttonPanel, " 清  空 ", "", GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                textArea.setText("");
            }
        });
    }

    /**
     * 格式化json.
     *
     * @param jsonStr
     * @param reInitItem
     * @return
     */
    public String prettyJson(String jsonStr, boolean reInitItem) {
        try {
            Gson gson = getGsonBuilder()
                    .setPrettyPrinting()
                    .create();
            Object o = gson.fromJson(jsonStr, Object.class);
            if (reInitItem) {
                CollectionUtils.reInitItemValue(o);
            }
            return gson.toJson(o);
        } catch (Exception e) {
            showExceptionMessage(e);
            return jsonStr;
        }
    }

    /**
     * 格式化properties.
     *
     * @param propertiesStr
     * @param reInitItem
     * @return
     */
    public String prettyProperties(String propertiesStr, boolean reInitItem) {
        try {
            StringBuilder rps = new StringBuilder();

            List<String> l = new ArrayList<String>();
            String[] f = propertiesStr.split("\r\n");
            for (String fi : f) {
                String[] s = fi.split("\n");
                for (String si : s) {
                    String[] t = si.split("\r");
                    for (String ti : t) {
                        if (GuiUtils.trim(ti).startsWith("#")) {
                            rps.append(GuiUtils.trim(ti)).append("\n");
                        } else {
                            int idx = ti.indexOf("=");
                            if (idx > 0) {
                                rps.append(GuiUtils.trim(ti.substring(0, idx))).append("=");
                                if (!reInitItem) {
                                    rps.append(GuiUtils.trim(ti.substring(idx + 1)));
                                }
                                rps.append("\n");
                            } else {
                                rps.append(GuiUtils.trim(ti)).append("\n");
                            }
                        }
                    }
                }
            }
            return rps.toString();
        } catch (Exception e) {
            showExceptionMessage(e);
            return propertiesStr;
        }
    }

    /**
     * 反格式化json.
     *
     * @param jsonStr
     * @return
     */
    public String unPrettyJson(String jsonStr) {
        try {
            Gson gson = getGsonBuilder()
                    .create();
            return gson.toJson(gson.fromJson(jsonStr, Object.class));
        } catch (Exception e) {
            showExceptionMessage(e);
            return jsonStr;
        }
    }

    /**
     * How to prevent Gson from expressing integers as floats.
     * https://stackoverflow.com/questions/15507997/how-to-prevent-gson-from-expressing-integers-as-floats
     *
     * @return
     */
    private GsonBuilder getGsonBuilder() {
        return new GsonBuilder().
                registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
                    @Override
                    public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                        if (src == src.longValue()) {
                            return new JsonPrimitive(src.longValue());
                        }
                        return new JsonPrimitive(src);
                    }
                });
    }

}
