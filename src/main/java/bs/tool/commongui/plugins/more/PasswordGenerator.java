package bs.tool.commongui.plugins.more;

import bs.tool.commongui.AbstractGuiJPanel;
import bs.tool.commongui.GuiUtils;
import bs.tool.commongui.utils.SimpleMouseListener;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 随机密码生成.
 */
public class PasswordGenerator extends AbstractGuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 密码文本域.
     */
    private JTextArea passwordTextArea = createJTextArea(GuiUtils.font14_un);
    /**
     * 规则校验文本域.
     */
    private JTextArea ruleCheckTextArea = createJTextArea(GuiUtils.font14_un);

    /**
     * 常见密码组合.
     */
    private String[] passwordCombinations = new String[]{GuiUtils.PASSWORD_COMBINATION_8_CHAR_NUM_SIMPLE,
            GuiUtils.PASSWORD_COMBINATION_8_CHAR_NUM_POPULAR};
    private String curPasswordCombination = passwordCombinations[0];

    /**
     * 生成数量输入框.
     */
    private JTextField passwordNumberTextField = new JTextField("10", 5);
    /**
     * 数字位数输入框.
     */
    private JTextField numberCharacterRuleTextField = new JTextField("6", 5);
    /**
     * 字母位数输入框.
     */
    private JTextField englishCharacterRuleTextField = new JTextField("2", 5);
    /**
     * 特殊字符位数输入框.
     */
    private JTextField specialCharacterRuleTextField = new JTextField("0", 5);
    /**
     * 大小字母位数输入框.
     */
    private JTextField upperCaseCharacterRuleTextField = new JTextField(5);
    /**
     * 小写字母位数输入框.
     */
    private JTextField lowerCaseCharacterRuleTextField = new JTextField(5);

    public PasswordGenerator() {

        // 边界布局
        setLayout(new BorderLayout());
        // Center，加密解密输入输出域，使用2行1列的Grid布局，使其平均显示
        JPanel textAreaPanel = new JPanel(new GridLayout(2, 1));
        add(textAreaPanel, BorderLayout.CENTER);

        JPanel encrptyPanel = new JPanel(new BorderLayout());
        addJLabel(encrptyPanel, " 密码: ", GuiUtils.font14b_cn, BorderLayout.WEST);
        encrptyPanel.add(new JScrollPane(passwordTextArea), BorderLayout.CENTER);
        textAreaPanel.add(encrptyPanel);

        JPanel decrptyPanel = new JPanel(new BorderLayout());
        addJLabel(decrptyPanel, " 校验: ", GuiUtils.font14b_cn, BorderLayout.WEST);
        decrptyPanel.add(new JScrollPane(ruleCheckTextArea), BorderLayout.CENTER);
        textAreaPanel.add(decrptyPanel);

        // East，操作区域，使用BorderLayout布局
        JPanel actionPanel = new JPanel(new BorderLayout());
        add(actionPanel, BorderLayout.EAST);
        // 放置下拉框、单选框等
        JPanel actionGridPanel = new JPanel(new GridLayout(10, 1));
        actionPanel.add(actionGridPanel, BorderLayout.NORTH);

        // 密码组合下拉框
        JPanel passwordCombinationsPanel = new JPanel(new FlowLayout());
        addJLabel(passwordCombinationsPanel, "密码组合:", GuiUtils.font14b_cn);
        addJComboBox(passwordCombinationsPanel, passwordCombinations, GuiUtils.font13_cn, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                curPasswordCombination = ((JComboBox) event.getSource()).getSelectedItem().toString();
                if (curPasswordCombination.equals(GuiUtils.PASSWORD_COMBINATION_8_CHAR_NUM_SIMPLE)) {
                    numberCharacterRuleTextField.setText("6");
                    englishCharacterRuleTextField.setText("2");
                    specialCharacterRuleTextField.setText("0");
                    upperCaseCharacterRuleTextField.setText("");
                    lowerCaseCharacterRuleTextField.setText("");
                } else if (curPasswordCombination.equals(GuiUtils.PASSWORD_COMBINATION_8_CHAR_NUM_POPULAR)) {
                    numberCharacterRuleTextField.setText("2");
                    englishCharacterRuleTextField.setText("3");
                    specialCharacterRuleTextField.setText("1");
                    upperCaseCharacterRuleTextField.setText("1");
                    lowerCaseCharacterRuleTextField.setText("1");
                }
            }
        });
        actionGridPanel.add(passwordCombinationsPanel);

        JPanel numPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJLabel(numPanel, "生成数量:", GuiUtils.font14b_cn);
        numPanel.add(passwordNumberTextField);
        actionGridPanel.add(numPanel);

        // 仅作填充
        addJLabel(actionGridPanel, " ", GuiUtils.font13);

        addJLabel(actionGridPanel, " 密码规则:", GuiUtils.font14b_cn);

        // FlowLayout.LEADING，此值指示每一行组件都应该与容器方向的开始边对齐，例如，对于从左到右的方向，则与左边对齐
        JPanel rfPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJLabel(rfPanel, "   数字位数:", GuiUtils.font13_cn);
        rfPanel.add(numberCharacterRuleTextField);
        actionGridPanel.add(rfPanel);

        JPanel rsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJLabel(rsPanel, "   字母位数:", GuiUtils.font13_cn);
        rsPanel.add(englishCharacterRuleTextField);
        actionGridPanel.add(rsPanel);

        JPanel rtPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJLabel(rtPanel, "   特殊字符:", GuiUtils.font13_cn);
        rtPanel.add(specialCharacterRuleTextField);
        actionGridPanel.add(rtPanel);

        JPanel rfoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJLabel(rfoPanel, "   大写字母:", GuiUtils.font13_cn);
        rfoPanel.add(upperCaseCharacterRuleTextField);
        actionGridPanel.add(rfoPanel);

        JPanel rfiPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        addJLabel(rfiPanel, "   小写字母:", GuiUtils.font13_cn);
        rfiPanel.add(lowerCaseCharacterRuleTextField);
        actionGridPanel.add(rfiPanel);

        // 仅作填充
        actionPanel.add(new Panel(), BorderLayout.CENTER);

        // 放置生成校验按钮
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
        actionPanel.add(buttonPanel, BorderLayout.SOUTH);
        // 生成按钮
        addJButton(buttonPanel, "生成", "", GuiUtils.font14b_cn, new SimpleMouseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                try {
                    // https://github.com/vt-middleware/passay/blob/master/src/test/java/org/passay/PasswordGeneratorTest.java
                    List<CharacterRule> rules = new ArrayList<CharacterRule>();
                    int pt = 0;
                    pt += addRuleIfNotNull(numberCharacterRuleTextField.getText(), EnglishCharacterData.Digit, rules);
                    pt += addRuleIfNotNull(englishCharacterRuleTextField.getText(), EnglishCharacterData.Alphabetical, rules);
                    pt += addRuleIfNotNull(specialCharacterRuleTextField.getText(), EnglishCharacterData.Special, rules);
                    pt += addRuleIfNotNull(upperCaseCharacterRuleTextField.getText(), EnglishCharacterData.UpperCase, rules);
                    pt += addRuleIfNotNull(lowerCaseCharacterRuleTextField.getText(), EnglishCharacterData.LowerCase, rules);

                    org.passay.PasswordGenerator generator = new org.passay.PasswordGenerator();
                    int number = Integer.parseInt(passwordNumberTextField.getText().trim());
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < number; i++) {
                        sb.append("\n").append(generator.generatePassword(pt, rules));
                    }
                    passwordTextArea.setText(sb.length() > 0 ? sb.substring(1) : "");
                } catch (Exception e) {
                    showExceptionMessage(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                ruleCheckTextArea.setText("");
            }
        });
        // 校验按钮
        addJButton(buttonPanel, "校验", "", GuiUtils.font14b_cn, new SimpleMouseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                String input = passwordTextArea.getText();
                try {
                    showMessage("暂未实现！", "提示", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    showExceptionMessage(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                passwordTextArea.setText("");
            }
        });
    }

    private int addRuleIfNotNull(String value, EnglishCharacterData cData, List<CharacterRule> rules) {
        int n = 0;
        String v = GuiUtils.trim(value);
        if (v.length() != 0) {
            n = Integer.parseInt(v);
            if (n > 0) {
                rules.add(new CharacterRule(cData, n));
            } else {
                n = 0;
            }
        }
        return n;
    }
}
