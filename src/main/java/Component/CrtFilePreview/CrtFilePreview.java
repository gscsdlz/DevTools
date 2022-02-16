package Component.CrtFilePreview;

import Component.BasicComponent.BasicInterface;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileTypeDescriptor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import icons.Icons;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: liruipeng1@360.cn
 * Date: 2022/2/14
 * Time: 16:33
 */
public class CrtFilePreview implements BasicInterface {
    private JPanel panel;
    private JButton openFile;
    private JTextField fileText;
    private JLabel notBefore;
    private JLabel notAfter;
    private JLabel version;
    private JLabel sn;
    private JLabel issuer;
    private JLabel subject;
    private JLabel sigAlg;
    private JList<String> subjectAlternative;

    public CrtFilePreview(ToolWindow toolWindow) {
        openFile.addActionListener(this::openFileDialog);
    }

    public JPanel getContent() {
        return this.panel;
    }

    private void openFileDialog(ActionEvent e) {
        FileTypeDescriptor descriptor = new FileTypeDescriptor("", "crt");
        FileChooserDialog fileChooserDialog = FileChooserFactory.getInstance().createFileChooser(descriptor, null, panel);
        VirtualFile[] files = fileChooserDialog.choose(null, LocalFileSystem.getInstance().refreshAndFindFileByPath("/"));
        if (files.length == 0) {
            return;
        }
        VirtualFile file = files[0];
        fileText.setText(file.getPath());
        try {
            this.parseFile(file);
        } catch (Exception ex) {
            Messages.showErrorDialog(ex.getMessage(), "Parse Crt File Exception");
        }
    }

    private void parseFile(VirtualFile crtFile) throws CertificateException, FileNotFoundException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) cf.generateCertificate(new FileInputStream(crtFile.getPath()));
        this.notBefore.setText(certificate.getNotBefore().toString());
        this.notAfter.setText(certificate.getNotAfter().toString());
        if (certificate.getNotAfter().compareTo(new Date()) > 0) {
            this.notAfter.setIcon(Icons.SslOK);
        } else {
            this.notAfter.setIcon(Icons.SslError);
        }

        this.issuer.setText("<html><body>" +
                this.mapToLine(this.parsePrincipal(certificate.getIssuerX500Principal())) +
                "</body></html>");

        this.version.setText(String.valueOf(certificate.getVersion()));

        var bytes = certificate.getSerialNumber().toByteArray();
        Vector<String> list = new Vector<>();
        for (byte b : bytes) {
            list.add(Integer.toHexString(Byte.toUnsignedInt(b)));
        }
        this.sn.setText(String.join(":", list));

        this.subject.setText("<html><body>" +
                this.mapToLine(this.parsePrincipal(certificate.getSubjectX500Principal())) +
                "</body></html>");

        this.sigAlg.setText(certificate.getSigAlgName());

        var sa = certificate.getSubjectAlternativeNames();
        Vector<String> dnsList = new Vector<>();
        for (var saList : sa) {
            if (saList.get(0).equals(2)) {
                dnsList.add(saList.get(1).toString());
            }
        }
        this.subjectAlternative.setListData(dnsList);
    }

    private Map<String, String> parsePrincipal(Principal principal) {
        String name = principal.getName();
        String tmpName = name.replace("\\,", "\0");
        String[] arr = tmpName.split(",");
        Map<String, String> res = new TreeMap<>();
        for (String a : arr) {
            String[] tmp = a.split("=");
            res.put(tmp[0], tmp[1].replace("\0", ","));
        }
        return res;
    }

    private String mapToLine(Map<String, String> m) {
        StringBuilder builder = new StringBuilder();
        Set<String> keys = m.keySet();
        int size = 0;
        for (String key : keys) {
            builder.append(key);
            builder.append("=");
            builder.append(m.get(key));
            if (size++ < m.size()) {
                builder.append("<br/>");
            }
        }
        return builder.toString();
    }
}
