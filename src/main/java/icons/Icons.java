package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: liruipeng1@360.cn
 * Date: 2022/2/16
 * Time: 15:04
 */
public interface Icons {
    Icon SslError = IconLoader.getIcon("/icon/error.svg", Icons.class);
    Icon SslOK = IconLoader.getIcon("/icon/ok.svg", Icons.class);
    Icon Ssl = IconLoader.getIcon("/icon/ssl.svg", Icons.class);
}
