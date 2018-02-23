package bs.tool.commongui.plugins;

import bs.tool.commongui.GuiJPanel;
import bs.tool.commongui.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 颜色工具.
 */
public class ColorTool extends GuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * R表单.
     */
    private JTextField rTextField = new JTextField(3);
    /**
     * G表单.
     */
    private JTextField gTextField = new JTextField(3);
    /**
     * B表单.
     */
    private JTextField bTextField = new JTextField(3);

    /**
     * HTML表单.
     */
    private JTextField htmlTextField = new JTextField(6);

    public ColorTool() {

        // 主面板：边界布局，分North、Center两部分，North用于放置输入及条件控件，Center是放置空面面板
        setLayout(new BorderLayout());

        // 输入及条件Panel
        JPanel colorPanel = new JPanel(new BorderLayout());

        JPanel flowPanel = new JPanel(new FlowLayout());

        addJLabel(flowPanel, "  R:", GuiUtils.font13);
        flowPanel.add(rTextField);
        addJLabel(flowPanel, "G:", GuiUtils.font13);
        flowPanel.add(gTextField);
        addJLabel(flowPanel, "B:", GuiUtils.font13);
        flowPanel.add(bTextField);
        addJLabel(flowPanel, "  HTML: ", GuiUtils.font13);

        addJButton(flowPanel, "<-", "", GuiUtils.font14b_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Color color = getHTMLFillColor();
                if (color != null) {
                    rTextField.setText(Integer.toString(color.getRed()));
                    gTextField.setText(Integer.toString(color.getGreen()));
                    bTextField.setText(Integer.toString(color.getBlue()));
                } else {
                    showMessage("未输入正确的颜色值！", "警告", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        addJButton(flowPanel, "->", "", GuiUtils.font14b_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Color color = getRGBFillColor();
                if (color != null) {
                    htmlTextField.setText("#" + hex(color.getRed()) + hex(color.getGreen()) + hex(color.getBlue()));
                } else {
                    showMessage("未输入正确的颜色值！", "警告", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        flowPanel.add(htmlTextField);
        colorPanel.add(flowPanel, BorderLayout.CENTER);
        colorPanel.add(new JPanel(), BorderLayout.EAST);

        JButton paletteButton = createJButton("显示调色板", "", GuiUtils.font14_cn);
        paletteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Color fillColor = getRGBFillColor();
                fillColor = fillColor == null ? getHTMLFillColor() : fillColor;
                Color chooseColor = fillColor == null ? Color.black : fillColor;
                Color color = JColorChooser.showDialog(ColorTool.this, "调色板", chooseColor);
                if (color != null) {
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();
                    rTextField.setText(Integer.toString(r));
                    gTextField.setText(Integer.toString(g));
                    bTextField.setText(Integer.toString(b));
                    htmlTextField.setText("#" + hex(r) + hex(g) + hex(b));
                }
            }
        });
        flowPanel.add(paletteButton);

        add(colorPanel, BorderLayout.NORTH);

        // 空面面板
        JPanel resultPanel = new JPanel(new BorderLayout());
        // 空白文本域
        JTextArea resultTextArea = createJTextArea(GuiUtils.font14_un);
        resultTextArea.setEditable(false);
        resultTextArea.setText("说明：" + "\n1，R、G、B的范围0-255" + "\n2，HTML颜色码可识别3位或6位，且可带或不带井号#，不区分大小写"
                + "\n3，调色板颜色选择说明，如果RGB有正确的值则选择其值，否则选择HTML颜色码正确的值，若两者都无正确的值，则选择黑色");
        resultPanel.add(new JScrollPane(resultTextArea));
        add(resultPanel, BorderLayout.CENTER);
    }

    /**
     * 将0-255转为双位数字的16进制.
     */
    private String hex(int code) {
        String hex = Integer.toHexString(code);
        return hex.length() == 1 ? "0" + hex : hex;
    }

    /**
     * 获取RGB表单设置的颜色.
     */
    private Color getRGBFillColor() {
        Color color = null;
        String r = rTextField.getText().trim();
        String g = gTextField.getText().trim();
        String b = bTextField.getText().trim();
        try {
            if (!r.equals("") && !g.equals("") && !b.equals("")) {
                color = new Color(Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b));
            }
        } catch (Exception e) {
            GuiUtils.log("", e);
        }
        return color;
    }

    /**
     * 获取HTML颜色表单设置的颜色.
     */
    private Color getHTMLFillColor() {
        Color color = null;
        String r = "";
        String g = "";
        String b = "";
        String html = htmlTextField.getText().trim();
        try {
            if (!html.equals("")) {
                if (html.startsWith("#")) {
                    html = html.substring(1);
                }
                if (html.length() == 3) {
                    r = html.substring(0, 1) + html.substring(0, 1);
                    g = html.substring(1, 2) + html.substring(1, 2);
                    b = html.substring(2, 3) + html.substring(2, 3);
                } else if (html.length() == 6) {
                    r = html.substring(0, 2);
                    g = html.substring(2, 4);
                    b = html.substring(4, 6);
                }
                color = new Color(Integer.parseInt(r, 16), Integer.parseInt(g, 16), Integer.parseInt(b, 16));
            }
        } catch (Exception e) {
            GuiUtils.log("", e);
        }
        return color;
    }

}
