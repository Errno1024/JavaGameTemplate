package data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import utility.GameUtils;
import utility.DataManager;

public class DataUtils {
    public static BufferedImage getAnimationImage(DataAnimation animation, int index) {
        BufferedImage source = GameUtils.getCacheImage(animation.animationPath);
        int x = index % animation.horizontalCount;
        int y = index / animation.horizontalCount;
        int width = source.getWidth() / animation.horizontalCount;
        int height = source.getHeight() / animation.verticalCount;
        BufferedImage image = GameUtils.emptyImage(width, height);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.drawImage(source, 0, 0, width, height, width * x, height * y, width * (x + 1), height * (y + 1), null);
        return image;
    }

    public static DataAnimation readAnimation(DataInputStream in) {
        try {
            DataAnimation animation = new DataAnimation();
            animation.horizontalCount = in.readInt();
            animation.verticalCount = in.readInt();
            animation.animationPath = DataManager.dataReadString(in);
            int framelen = in.readInt();
            animation.frames = new DataAnimationFrame[framelen];
            for (int i = 0; i < framelen; ++i) {
                animation.frames[i] = new DataAnimationFrame();
                animation.frames[i].flashCount = in.readInt();
                animation.frames[i].frameCount = in.readInt();
                animation.frames[i].flashColor = DataManager.dataReadColor(in);
                animation.frames[i].pictureIndex = in.readInt();
                animation.frames[i].mirror = in.readBoolean();
            }
            return animation;
        } catch (IOException | NullPointerException e) {
            return null;
        }
    }

    public static DataAnimation readAnimation(String path) {
        try {
            return readAnimation(DataManager.getDataReader(path));
        } catch (IOException e) {
            return null;
        }
    }

    public static void writeAnimation(DataOutputStream out, DataAnimation animation) {
        try {
            if (animation == null)
                animation = new DataAnimation();
            out.writeInt(animation.horizontalCount);
            out.writeInt(animation.verticalCount);
            DataManager.dataWriteString(out, animation.animationPath);
            if (animation.frames == null)
                animation.frames = new DataAnimationFrame[0];
            int framelen = animation.frames.length;
            out.writeInt(framelen);
            for (int i = 0; i < framelen; ++i) {
                out.writeInt(animation.frames[i].flashCount);
                out.writeInt(animation.frames[i].frameCount);
                DataManager.dataWriteColor(out, animation.frames[i].flashColor);
                out.writeInt(animation.frames[i].pictureIndex);
                out.writeBoolean(animation.frames[i].mirror);
            }
        } catch (IOException e) {
        }
    }

    public static void writeAnimation(String path, DataAnimation animation) {
        try {
            writeAnimation(DataManager.getDataWriter(path), animation);
        } catch (IOException e) {
        }
    }

}
