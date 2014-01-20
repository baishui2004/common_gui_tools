package bs.util.tool.commongui.plugins;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.UnsupportedEncodingException;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.codec.DecoderException;

import bs.util.common.ExCodec;
import bs.util.tool.commongui.GuiJPanel;
import bs.util.tool.commongui.GuiUtils;

/**
 * 加密解密.
 */
public class EncryptAndDecrypt extends GuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 明文文本域.
     */
    private JTextArea encrptyTextArea = createJTextArea(GuiUtils.font14_un);
    /**
     * 密文文本域.
     */
    private JTextArea decrptyTextArea = createJTextArea(GuiUtils.font14_un);

    /**
     * 字符集.
     */
    private String[] charsets = new String[] { GuiUtils.CHARSET_UTF_8, GuiUtils.CHARSET_UTF_16BE,
            GuiUtils.CHARSET_UTF_16LE, GuiUtils.CHARSET_UTF_16, GuiUtils.CHARSET_GBK, GuiUtils.CHARSET_Big5,
            GuiUtils.CHARSET_ISO_8859_1 };

    /**
     * 当前字符集.
     */
    private String curCharset = charsets[0];

    /**
     * 加密算法. 空""用于填充一个空位.
     */
    private String[] cryptos = new String[] { GuiUtils.CRYPTO_ASCII, GuiUtils.CRYPTO_HEX, GuiUtils.CRYPTO_BASE64,
            GuiUtils.CRYPTO_BASE32, GuiUtils.CRYPTO_URL, "", "", "", GuiUtils.CRYPTO_MD5, "",
            GuiUtils.CRYPTO_SHA, GuiUtils.CRYPTO_SHA256, GuiUtils.CRYPTO_SHA384, GuiUtils.CRYPTO_SHA512 };

    /**
     * 当前加密算法.
     */
    private String curCrypto = cryptos[0];

    public EncryptAndDecrypt() {

        // 边界布局
        setLayout(new BorderLayout());
        // Center，加密解密输入输出域，使用2行1列的Grid布局，使其平均显示
        JPanel textAreaPanel = new JPanel(new GridLayout(2, 1));
        add(textAreaPanel, BorderLayout.CENTER);

        JPanel encrptyPanel = new JPanel(new BorderLayout());
        addJLabel(encrptyPanel, " 明文: ", GuiUtils.font14b_cn, BorderLayout.WEST);
        encrptyPanel.add(new JScrollPane(encrptyTextArea), BorderLayout.CENTER);
        textAreaPanel.add(encrptyPanel);

        JPanel decrptyPanel = new JPanel(new BorderLayout());
        addJLabel(decrptyPanel, " 密文: ", GuiUtils.font14b_cn, BorderLayout.WEST);
        decrptyPanel.add(new JScrollPane(decrptyTextArea), BorderLayout.CENTER);
        textAreaPanel.add(decrptyPanel);

        // East，操作区域，使用BorderLayout布局
        JPanel actionPanel = new JPanel(new BorderLayout());
        add(actionPanel, BorderLayout.EAST);
        // 放置下拉框、单选框等
        JPanel actionGridPanel = new JPanel(new GridLayout(10, 1));
        actionPanel.add(actionGridPanel, BorderLayout.NORTH);

        // 字符集下拉框
        JPanel charsetsPanel = new JPanel(new FlowLayout());
        addJLabel(charsetsPanel, "字符集:", GuiUtils.font14b_cn);
        addJComboBox(charsetsPanel, charsets, GuiUtils.font13, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                curCharset = ((JComboBox) event.getSource()).getSelectedItem().toString();
            }
        });
        actionGridPanel.add(charsetsPanel);

        addJLabel(actionGridPanel, " ", GuiUtils.font13); // 仅作填充
        // 算法单选框
        ButtonGroup buttonGroup = new ButtonGroup();
        int cryptosLen = cryptos.length;
        int cryptosLoop = cryptosLen / 2;
        cryptosLoop = cryptosLoop * 2 == cryptosLen ? cryptosLoop : (cryptosLoop + 1);
        for (int i = 0; i < cryptosLoop; i++) {
            // FlowLayout.LEADING，此值指示每一行组件都应该与容器方向的开始边对齐，例如，对于从左到右的方向，则与左边对齐
            JPanel cryptoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
            String crypto = cryptos[i * 2];
            if (crypto.trim().length() != 0) {
                JRadioButton firstRadio = new JRadioButton(GuiUtils.getFillUpString(crypto, 6));
                firstRadio.setFont(GuiUtils.font13_cn);
                firstRadio.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent event) {
                        JRadioButton radio = (JRadioButton) event.getSource();
                        if (radio.isSelected()) {
                            curCrypto = radio.getText().trim();
                        }
                    }
                });
                if (GuiUtils.CRYPTO_MD5.equals(firstRadio.getText().trim())) {
                    firstRadio.setSelected(true);
                }
                buttonGroup.add(firstRadio);
                cryptoPanel.add(firstRadio);
            }
            if (!(cryptosLen % 2 == 1 && i * 2 + 1 == cryptosLen)) {
                crypto = GuiUtils.getFillUpString(cryptos[i * 2 + 1], 6);
                if (crypto.trim().length() != 0) {
                    JRadioButton secondRadio = new JRadioButton(crypto);
                    secondRadio.setFont(GuiUtils.font13_cn);
                    secondRadio.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent event) {
                            JRadioButton radio = (JRadioButton) event.getSource();
                            if (radio.isSelected()) {
                                curCrypto = radio.getText().trim();
                            }
                        }
                    });
                    if (GuiUtils.CRYPTO_MD5.equals(secondRadio.getText().trim())) {
                        secondRadio.setSelected(true);
                    }
                    buttonGroup.add(secondRadio);
                    cryptoPanel.add(secondRadio);
                }
            }
            actionGridPanel.add(cryptoPanel);
        }

        // 仅作填充
        actionPanel.add(new Panel(), BorderLayout.CENTER);

        // 放置加密解密按钮
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
        actionPanel.add(buttonPanel, BorderLayout.SOUTH);
        // 加密按钮
        addJButton(buttonPanel, "加密", "", GuiUtils.font14b_cn, new MouseListener() {
            public void mouseReleased(MouseEvent event) {
                String input = encrptyTextArea.getText();
                try {
                    if (GuiUtils.CRYPTO_ASCII.equals(curCrypto)) {
                        decrptyTextArea.setText(ExCodec.encodeAscii(input, curCharset));
                    } else if (GuiUtils.CRYPTO_HEX.equals(curCrypto)) {
                        decrptyTextArea.setText(ExCodec.encodeHex(input, curCharset));
                    } else if (GuiUtils.CRYPTO_BASE64.equals(curCrypto)) {
                        decrptyTextArea.setText(ExCodec.encodeBase64(input, curCharset));
                    } else if (GuiUtils.CRYPTO_BASE32.equals(curCrypto)) {
                        decrptyTextArea.setText(ExCodec.encodeBase32(input, curCharset));
                    } else if (GuiUtils.CRYPTO_URL.equals(curCrypto)) {
                        decrptyTextArea.setText(ExCodec.encodeURL(input, curCharset));
                    } else if (GuiUtils.CRYPTO_MD5.equals(curCrypto)) {
						String md5Val = ExCodec.encryptMd5(input, curCharset);
						decrptyTextArea.setText("16Bit：" + md5Val.substring(8, 24) + "\n32Bit：" + md5Val);
                    } else if (GuiUtils.CRYPTO_SHA.equals(curCrypto)) {
                        decrptyTextArea.setText(ExCodec.encryptSha(input, curCharset));
                    } else if (GuiUtils.CRYPTO_SHA256.equals(curCrypto)) {
                        decrptyTextArea.setText(ExCodec.encryptSha256(input, curCharset));
                    } else if (GuiUtils.CRYPTO_SHA384.equals(curCrypto)) {
                        decrptyTextArea.setText(ExCodec.encryptSha384(input, curCharset));
                    } else if (GuiUtils.CRYPTO_SHA512.equals(curCrypto)) {
                        decrptyTextArea.setText(ExCodec.encryptSha512(input, curCharset));
                    }
                } catch (UnsupportedEncodingException e) {
                    showExceptionMessage(e);
                }
            }

            public void mousePressed(MouseEvent e) {
                decrptyTextArea.setText("");
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });
        // 解密按钮
        addJButton(buttonPanel, "解密", "", GuiUtils.font14b_cn, new MouseListener() {
            public void mouseReleased(MouseEvent event) {
                String input = decrptyTextArea.getText();
                try {
                    if (GuiUtils.CRYPTO_ASCII.equals(curCrypto)) {
                        encrptyTextArea.setText(ExCodec.decodeAscii(input, curCharset));
                    } else if (GuiUtils.CRYPTO_HEX.equals(curCrypto)) {
                        encrptyTextArea.setText(ExCodec.decodeHex(input, curCharset));
                    } else if (GuiUtils.CRYPTO_BASE64.equals(curCrypto)) {
                        encrptyTextArea.setText(ExCodec.decodeBase64(input, curCharset));
                    } else if (GuiUtils.CRYPTO_BASE32.equals(curCrypto)) {
                        encrptyTextArea.setText(ExCodec.decodeBase32(input, curCharset));
                    } else if (GuiUtils.CRYPTO_URL.equals(curCrypto)) {
                        encrptyTextArea.setText(ExCodec.decodeURL(input, curCharset));
                    } else {
                        encrptyTextArea.setText("不支持此种加密算法的解密！");
                    }
                } catch (UnsupportedEncodingException e) {
                	showExceptionMessage(e);
                } catch (DecoderException e) {
                	showExceptionMessage(e);
                }
            }

            public void mousePressed(MouseEvent e) {
                encrptyTextArea.setText("");
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });
    }
}
