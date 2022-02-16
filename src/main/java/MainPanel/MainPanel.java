package MainPanel;

import Component.CrtFilePreview.CrtFilePreview;
import com.intellij.openapi.wm.ToolWindow;
import icons.Icons;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: liruipeng1@360.cn
 * Date: 2022/2/16
 * Time: 15:02
 */
public class MainPanel {
    private JPanel panel;
    private JTabbedPane mainPanel;

    public MainPanel(ToolWindow toolWindow) {
        this.mainPanel.addTab("CrtPreview", Icons.Ssl, new CrtFilePreview(toolWindow).getContent());
    }

    public JPanel getContent() {
        return this.panel;
    }
}
