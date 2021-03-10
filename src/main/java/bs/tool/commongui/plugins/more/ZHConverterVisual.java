package bs.tool.commongui.plugins.more;

import bs.tool.commongui.AbstractGuiJPanel;
import bs.tool.commongui.GuiUtils;
import bs.tool.commongui.utils.SimpleMouseListener;
import com.spreada.utils.chinese.ZHConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * 中文简繁体转换.
 */
public class ZHConverterVisual extends AbstractGuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 简体文本域.
     */
    private JTextArea simplifiedTextArea = createJTextArea(GuiUtils.font14_un);
    /**
     * 繁体文本域.
     */
    private JTextArea traditionalTextArea = createJTextArea(GuiUtils.font14_un);

    public ZHConverterVisual() {
        // 边界布局
        setLayout(new BorderLayout());
        // Center，文字及转换结果输入输出域，使用2行1列的Grid布局，使其平均显示
        JPanel textAreaPanel = new JPanel(new GridLayout(2, 1));
        add(textAreaPanel, BorderLayout.CENTER);

        JPanel scriptPanel = new JPanel(new BorderLayout());
        addJLabel(scriptPanel, " 简体文本: ", GuiUtils.font14b_cn, BorderLayout.WEST);
        scriptPanel.add(new JScrollPane(simplifiedTextArea), BorderLayout.CENTER);
        textAreaPanel.add(scriptPanel);

        JPanel resultPanel = new JPanel(new BorderLayout());
        addJLabel(resultPanel, " 繁体文本: ", GuiUtils.font14b_cn, BorderLayout.WEST);
        resultPanel.add(new JScrollPane(traditionalTextArea), BorderLayout.CENTER);
        textAreaPanel.add(resultPanel);

        // East，操作区域，使用BorderLayout布局
        JPanel actionPanel = new JPanel(new BorderLayout());
        add(actionPanel, BorderLayout.EAST);

        // 填充
        actionPanel.add(new JPanel(), BorderLayout.CENTER);

        // 放置按钮
        JPanel buttonPanel = new JPanel(new GridLayout(7, 1));
        actionPanel.add(buttonPanel, BorderLayout.SOUTH);
        // 简->繁
        addJButton(buttonPanel, " 简 -> 繁 ", "", GuiUtils.font14b_cn, new SimpleMouseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                String input = simplifiedTextArea.getText();
                traditionalTextArea.append(ZHConverter.convert(input, ZHConverter.TRADITIONAL));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                traditionalTextArea.setText("");
            }
        });
        // 仅做填充
        buttonPanel.add(new JPanel());
        // 繁->简
        addJButton(buttonPanel, " 繁 -> 简 ", "", GuiUtils.font14b_cn, new SimpleMouseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                String input = traditionalTextArea.getText();
                simplifiedTextArea.append(ZHConverter.convert(input, ZHConverter.SIMPLIFIED));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                simplifiedTextArea.setText("");
            }
        });
        // 仅做填充
        buttonPanel.add(new JPanel());
        // 全部清空
        addJButton(buttonPanel, " 全部清空 ", "", GuiUtils.font14_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                simplifiedTextArea.setText("");
                traditionalTextArea.setText("");
            }
        });
    }

}
