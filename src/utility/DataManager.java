package utility;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.*;

public final class DataManager {

    private static boolean inJAR = false;

    private static File inJarJudge = null;

    public static boolean isInJar() {
        if (inJarJudge == null) {
            inJarJudge = new File(DataManager.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            inJAR = inJarJudge.isFile();
        }
        return inJAR;
    }

    private DataManager() {}

    public static boolean isAbsolute(String path) {
        return new File(path).isAbsolute();
    }

    public static DataInputStream getDataReader(String path) throws FileNotFoundException {
        if (isInJar() && !isAbsolute(path)) {
            DataInputStream stream = new DataInputStream(DataManager.class.getClassLoader().getResourceAsStream(path));
            return stream;
        } else {
            FileInputStream os = new FileInputStream(path);
            DataInputStream stream = new DataInputStream(os);
            return stream;
        }
    }

    public static InputStream getInputStream(String path) throws FileNotFoundException {
        if (isInJar() && !isAbsolute(path)) {
            return DataManager.class.getClassLoader().getResourceAsStream(path);
        } else {
            return new FileInputStream(path);
        }
    }

    public static DataOutputStream getDataWriter(String path) throws FileNotFoundException {
        FileOutputStream os = new FileOutputStream(path);
        DataOutputStream stream = new DataOutputStream(os);
        return stream;
    }

    public static InputStreamReader getFileReader(String path) throws FileNotFoundException {
        if (isInJar() && !isAbsolute(path)) {
            InputStreamReader stream = new InputStreamReader(DataManager.class.getClassLoader().getResourceAsStream(path));
            return stream;
        } else {
            FileInputStream os = new FileInputStream(path);
            InputStreamReader stream = new InputStreamReader(os);
            return stream;
        }
    }

    public static OutputStreamWriter getFileWriter(String path) throws FileNotFoundException {
        FileOutputStream os = new FileOutputStream(path);
        OutputStreamWriter stream = new OutputStreamWriter(os);
        return stream;
    }

    public static String dataReadString(DataInputStream in) throws IOException {
        int len = in.readInt();
        byte[] str = new byte[len];
        in.read(str, 0, len);
        return new String(str, "UTF-8");
    }

    public static void dataWriteString(DataOutputStream out, String str) throws IOException {
        if (str == null) str = new String();
        out.writeInt(str.length());
        out.write(str.getBytes("UTF-8"));
    }

    public static Color dataReadColor(DataInputStream in) throws IOException {
        int alpha = in.read();
        int r = in.read();
        int g = in.read();
        int b = in.read();
        return new Color(r, g, b, alpha);
    }

    public static void dataWriteColor(DataOutputStream out, Color color) throws IOException {
        if (color == null) color = new Color(0, 0, 0, 0);
        out.write(color.getAlpha());
        out.write(color.getRed());
        out.write(color.getGreen());
        out.write(color.getBlue());
    }

    public static Point dataReadPoint(DataInputStream in) throws IOException {
        if (!in.readBoolean())
            return null;
        int x = in.readInt();
        int y = in.readInt();
        return new Point(x, y);
    }

    public static void dataWritePoint(DataOutputStream out, Point point) throws IOException {
        if (point == null) {
            out.writeBoolean(false);
            return;
        }
        out.writeBoolean(true);
        out.writeInt(point.x);
        out.writeInt(point.y);
    }

    public static Dimension dataReadDimension(DataInputStream in) throws IOException {
        if (!in.readBoolean())
            return null;
        int width = in.readInt();
        int height = in.readInt();
        return new Dimension(width, height);
    }

    public static void dataWriteDimension(DataOutputStream out, Dimension size) throws IOException {
        if (size == null) {
            out.writeBoolean(false);
            return;
        }
        out.writeBoolean(true);
        out.writeInt(size.width);
        out.writeInt(size.height);
    }

    public static Rectangle dataReadRectangle(DataInputStream in) throws IOException {
        if (!in.readBoolean())
            return null;
        int x = in.readInt();
        int y = in.readInt();
        int width = in.readInt();
        int height = in.readInt();
        return new Rectangle(x, y, width, height);
    }

    public static void dataWriteRectangle(DataOutputStream out, Rectangle area) throws IOException {
        if (area == null) {
            out.writeBoolean(false);
            return;
        }
        out.writeBoolean(true);
        out.writeInt(area.x);
        out.writeInt(area.y);
        out.writeInt(area.width);
        out.writeInt(area.height);
    }

    private static File previousDirectory = null;

    public static File choosePath(String title, int mode, JComponent parent) {
        JFileChooser chooser = new JFileChooser();
        if (previousDirectory == null) {
            previousDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
        }
        chooser.setCurrentDirectory(previousDirectory);
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(mode);
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            previousDirectory = f.getParentFile();
            return f;
        }
        return null;
    }

    public static File choosePath(String title, int mode) {
        return choosePath(title, mode, null);
    }

    public static File choosePath(String title) {
        return choosePath(title, JFileChooser.FILES_AND_DIRECTORIES);
    }

    public static File[] dir(File directory) {
        if (directory.isDirectory()) {
            return directory.listFiles();
        }
        return null;
    }

    public static File[] dir(String path) {
        return dir(new File(path));
    }
}
