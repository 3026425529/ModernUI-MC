package icyllis.modernui.mc.ui;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public final class ModernFileDialogs {
    private ModernFileDialogs() {}
    public static String openFile(String description, String... extensions) { return show(false, description, null, extensions); }
    public static String saveFile(String description, String name, String... extensions) { return show(true, description, name, extensions); }
    private static String show(boolean save, String description, String name, String... extensions) {
        if (GraphicsEnvironment.isHeadless()) return null;
        AtomicReference<String> result = new AtomicReference<>();
        Runnable task = () -> {
            JFileChooser chooser = new JFileChooser();
            if (extensions != null && extensions.length > 0) chooser.setFileFilter(new FileNameExtensionFilter(description, extensions));
            if (name != null) chooser.setSelectedFile(new File(name));
            int status = save ? chooser.showSaveDialog(null) : chooser.showOpenDialog(null);
            if (status == JFileChooser.APPROVE_OPTION) result.set(chooser.getSelectedFile().getAbsolutePath());
        };
        try { if (javax.swing.SwingUtilities.isEventDispatchThread()) task.run(); else javax.swing.SwingUtilities.invokeAndWait(task); }
        catch (Exception e) { if (e instanceof InterruptedException) Thread.currentThread().interrupt(); return null; }
        return result.get();
    }
}