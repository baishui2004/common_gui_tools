package bs.tool.commongui.plugins.more;

import bs.tool.commongui.GuiImagePanel;
import bs.tool.commongui.GuiJPanel;
import bs.tool.commongui.GuiUtils;
import bs.tool.commongui.utils.FileUtils;
import bs.tool.commongui.utils.QrCodeUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

/**
 * 二维码转换.
 */
public class QrCodeConverter extends GuiJPanel {

    private static final long serialVersionUID = 1L;

    /**
     * 文本域.
     */
    private JTextArea textArea = createJTextArea(GuiUtils.font14_un, System.getProperty("QrCodeConverter.defaultText"));
    private int imageWidth = 420;
    private int imageHeight = 420;
    private String qrCodeImageFile = FileUtils.JAVA_IO_TMPDIR + "/" + "$QrCodeConverter$.jpg";
    private String blankImageFile = GuiUtils.getActualPath("img/cgt_blank.png");
    private String currentQrCodeImageFile = blankImageFile;
    /**
     * 二维码图片域.
     */
    private GuiImagePanel qrCodeImagePanel = new GuiImagePanel(imageWidth, imageHeight);
    /**
     * 图片路径选择.
     */
    private JFileChooser imageChooser = createImageChooser();

    public QrCodeConverter() {

        // 主面板：边界布局，分North、Center、East三部分，North用于放置文本，Center是放置二维码图片，East是按钮
        setLayout(new BorderLayout());

        // 文本
        JPanel textAreaPanel = new JPanel(new BorderLayout());
        add(textAreaPanel, BorderLayout.NORTH);
        addJLabel(textAreaPanel, " 字  符: ", GuiUtils.font14b_cn, BorderLayout.WEST);
        textArea.setRows(4);
        textAreaPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        // 填充
        addJLabel(textAreaPanel, "               ", GuiUtils.font14b_cn, BorderLayout.EAST);

        // Center，二维码图片域
        JPanel imagePanel = new JPanel(new BorderLayout());
        add(imagePanel, BorderLayout.CENTER);
        addJLabel(imagePanel, " 二维码: ", GuiUtils.font14b_cn, BorderLayout.WEST);
        imagePanel.add(new JScrollPane(qrCodeImagePanel), BorderLayout.CENTER);

        // East，操作区域，使用BorderLayout布局
        JPanel actionPanel = new JPanel(new BorderLayout());
        add(actionPanel, BorderLayout.EAST);

        // 填充
        actionPanel.add(new JPanel(), BorderLayout.CENTER);

        // 放置按钮
        JPanel buttonPanel = new JPanel(new GridLayout(9, 1));
        actionPanel.add(buttonPanel, BorderLayout.SOUTH);
        // 生成
        addJButton(buttonPanel, " 生成二维码 ", "", GuiUtils.font14b_cn, new MouseListener() {
            public void mouseReleased(MouseEvent event) {
                String input = textArea.getText();
                if (input.length() != 0) {
                    currentQrCodeImageFile = qrCodeImageFile;
                    genQrCodeImage(input);
                }
            }

            public void mousePressed(MouseEvent e) {
                currentQrCodeImageFile = blankImageFile;
                qrCodeImagePanel.repaint(currentQrCodeImageFile);
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });

        buttonPanel.add(new JPanel()); // 仅做填充
        // 另存图片
        addJButton(buttonPanel, " 另存图片 ", "", GuiUtils.font14_cn,
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        int option = imageChooser.showSaveDialog(null);
                        if (option == JFileChooser.APPROVE_OPTION) {
                            try {
                                // 补后缀名
                                File selectedFile = imageChooser.getSelectedFile();
                                if (selectedFile.getName().indexOf(".") < 0) {
                                    imageChooser.setSelectedFile(new File(selectedFile.getAbsolutePath() + ".png"));
                                }
                                // 复制图片
                                org.apache.commons.io.FileUtils.copyFile(new File(currentQrCodeImageFile), imageChooser.getSelectedFile());
                            } catch (IOException e) {
                                showExceptionMessage(e);
                            }
                        }
                    }
                });

        buttonPanel.add(new JPanel()); // 仅做填充
        // 选择图片
        addJButton(buttonPanel, " 选择图片 ", "", GuiUtils.font14_cn,
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        if (imageChooser.showDialog(getContextPanel(), "确定") != JFileChooser.CANCEL_OPTION) {
                            textArea.setText("");
                            File selectFile = imageChooser.getSelectedFile();
                            if (selectFile != null) {
                                currentQrCodeImageFile = selectFile.getAbsolutePath();
                                decodeQrCodeImage(currentQrCodeImageFile);
                                qrCodeImagePanel.repaint(currentQrCodeImageFile);
                            }
                        }
                    }
                });

        buttonPanel.add(new JPanel()); // 仅做填充
        // 解析
        addJButton(buttonPanel, " 解析二维码 ", "", GuiUtils.font14b_cn, new MouseListener() {
            public void mouseReleased(MouseEvent event) {
                decodeQrCodeImage(currentQrCodeImageFile);
            }

            public void mousePressed(MouseEvent e) {
                textArea.setText("");
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });
    }

    private void genQrCodeImage(String code) {
        try {
            QrCodeUtil.genQrCodeFile(code, currentQrCodeImageFile, QrCodeUtil.FORMAT_PNG, imageWidth, imageHeight);
            qrCodeImagePanel.repaint(currentQrCodeImageFile);
        } catch (Exception e) {
            showExceptionMessage(e, "generate qrCode image error.");
        }
    }

    private void decodeQrCodeImage(String imagePath) {
        if (!blankImageFile.equals(imagePath)) {
            try {
                textArea.setText(QrCodeUtil.decodeQrCodeFile(imagePath).getText());
            } catch (Exception e) {
                showExceptionMessage(e, "decode qrCode image error.");
            }
        }
    }

}
