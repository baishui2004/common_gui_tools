package bs.tool.commongui.plugins.more;

import bs.tool.commongui.AbstractGuiJPanel;
import bs.tool.commongui.GuiUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

import javax.swing.*;
import java.awt.*;

/**
 * Simple Webview.
 */
public class SimpleWebview extends AbstractGuiJPanel {

    public SimpleWebview() {
        setLayout(new BorderLayout());

        NativeInterface.open();
        JWebBrowser webBrowser = new JWebBrowser();
        webBrowser.navigate("file://" + GuiUtils.getActualPath("conf/SimpleWebview/index.html"));

        JPanel webBrowserPanel = new JPanel(new BorderLayout());
        webBrowserPanel.add(webBrowser, BorderLayout.CENTER);

        add(webBrowserPanel, BorderLayout.CENTER);
    }

}
