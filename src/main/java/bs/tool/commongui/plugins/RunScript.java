package bs.tool.commongui.plugins;

import bs.tool.commongui.GuiJPanel;
import bs.tool.commongui.GuiUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 执行Script脚本.
 */
public class RunScript extends GuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 脚本文本域.
     */
    private JTextArea scriptTextArea = createJTextArea(GuiUtils.font14_un);
    /**
     * 结果文本域.
     */
    private JTextArea resultTextArea = createJTextArea(GuiUtils.font14_un);

    /**
     * 脚本类型.
     */
    private String[] scriptTypes = getScriptTypes();
    /**
     * 当前脚本类型.
     */
    private String curScriptType = scriptTypes[0];

    public RunScript() {
        // 边界布局
        setLayout(new BorderLayout());
        // Center，脚本及结果输入输出域，使用2行1列的Grid布局，使其平均显示
        JPanel textAreaPanel = new JPanel(new GridLayout(2, 1));
        add(textAreaPanel, BorderLayout.CENTER);

        JPanel scriptPanel = new JPanel(new BorderLayout());
        addJLabel(scriptPanel, " 执行脚本: ", GuiUtils.font14b_cn, BorderLayout.WEST);
        scriptPanel.add(new JScrollPane(scriptTextArea), BorderLayout.CENTER);
        textAreaPanel.add(scriptPanel);

        JPanel resultPanel = new JPanel(new BorderLayout());
        addJLabel(resultPanel, " 执行结果: ", GuiUtils.font14b_cn, BorderLayout.WEST);
        resultPanel.add(new JScrollPane(resultTextArea), BorderLayout.CENTER);
        textAreaPanel.add(resultPanel);

        // East，操作区域，使用BorderLayout布局
        JPanel actionPanel = new JPanel(new BorderLayout());
        add(actionPanel, BorderLayout.EAST);
        // 放置下拉框等
        JPanel actionGridPanel = new JPanel(new GridLayout(5, 1));
        actionPanel.add(actionGridPanel, BorderLayout.NORTH);

        // 脚本类型下拉框
        JPanel scriptTypesPanel = new JPanel(new FlowLayout());
        addJLabel(scriptTypesPanel, "脚本类型:", GuiUtils.font14b_cn);
        addJComboBox(scriptTypesPanel, scriptTypes, GuiUtils.font13, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                curScriptType = ((JComboBox) event.getSource()).getSelectedItem().toString();
            }
        });
        actionGridPanel.add(scriptTypesPanel);

        // 放置执行按钮
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        actionPanel.add(buttonPanel, BorderLayout.SOUTH);
        // 执行按钮
        addJButton(buttonPanel, "执行", "", GuiUtils.font14b_cn, new MouseListener() {
            public void mouseReleased(MouseEvent event) {
                String input = scriptTextArea.getText();

                ScriptEngineManager sem = new ScriptEngineManager();
                ScriptEngine se = sem.getEngineByName(curScriptType);
                try {
                    resultTextArea.setText(String.valueOf(se.eval(input)));
                } catch (ScriptException e) {
                    showExceptionMessage(e);
                }
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
    }

    /**
     * 支持的脚本类型.
     */
    private String[] getScriptTypes() {
        ScriptEngineManager sem = new ScriptEngineManager();
        List<ScriptEngineFactory> factorys = sem.getEngineFactories();
        Set<String> languages = new LinkedHashSet<String>();
        for (ScriptEngineFactory factory : factorys) {
            languages.add(factory.getLanguageName());
        }
        String[] types = new String[languages.size()];
        int i = 0;
        for (String name : languages) {
            types[i] = name;
            i++;
        }
        return types;
    }

}
