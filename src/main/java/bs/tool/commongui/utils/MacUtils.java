package bs.tool.commongui.utils;

import bs.tool.commongui.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MacUtils {

    public static void showAboutMessage(final Toolkit kit, final JPanel contextPanel, Image icon, final Map<String, String> propsMap) {
        try {
            com.apple.eawt.Application app = com.apple.eawt.Application.getApplication();
            app.setDockIconImage(icon);
            app.setDockIconBadge("");
            app.setAboutHandler(new com.apple.eawt.AboutHandler() {
                @Override
                public void handleAbout(com.apple.eawt.AppEvent.AboutEvent aboutEvent) {
                    GuiUtils.showAboutMessage(kit, contextPanel, propsMap);
                }
            });
        } catch (Error e) {
            e.printStackTrace();
        }
    }

}
