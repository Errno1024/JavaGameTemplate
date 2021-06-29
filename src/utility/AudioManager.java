package utility;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AudioManager {
    private static boolean inJAR = false;
    private static File inJarJudge = null;

    private static boolean isInJar() {
        if (inJarJudge == null) {
            inJarJudge = new File(AudioManager.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            inJAR = inJarJudge.isFile();
        }
        return inJAR;
    }

    private AudioManager() {}

    public static final class SoundPlayer {
        private Clip clip;
        private String path;
        private boolean hasManager = false;

        public SoundPlayer() {
        }

        public SoundPlayer(String path) {
            loadSound(path);
        }

        private SoundPlayerListener listener;

        public boolean isManaged() {
            return hasManager;
        }

        public boolean isAvailable() {
            return clip != null;
        }

        public String getPath() {
            return path;
        }

        public void loadSound(String path) {
            this.path = path;
            if (path == null) return;
            try {
                AudioInputStream in = null;
                if (isInJar() && !DataManager.isAbsolute(path)) {
                    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(path);
                    if (ins == null)
                        return;
                    in = AudioSystem.getAudioInputStream(new BufferedInputStream(ins));
                } else {
                    in = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(path)));
                }
                AudioFormat format = in.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                clip = (Clip) AudioSystem.getLine(info);
                clip.open(in);
                clip.setLoopPoints(0, -1);
                listener = new SoundPlayerListener(this);
                clip.addLineListener(listener);
            }
            catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {}
        }

        public void play(int times, int position) {
            if (clip == null) return;
            clip.setFramePosition(position);
            if (times < 0)
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            else if (times > 0)
                clip.loop(times - 1);
            else stop();
        }

        public void play(int times) {
            play(times, 0);
        }

        public void play() {
            play(1);
        }

        public void loop() {
            play(-1);
        }

        public void stop() {
            if (clip == null) return;
            clip.stop();
        }

        public void start() {
            if (clip == null) return;
            clip.start();
        }

        public long microsecondLength() {
            if (clip == null) return 0;
            return clip.getMicrosecondLength();
        }

        public int length() {
            if (clip == null) return 0;
            return clip.getFrameLength();
        }

        public void setPosition(int pos) {
            if (clip == null) return;
            clip.setFramePosition(pos);
        }

        public void setMicrosecondPosition(long pos) {
            if (clip == null) return;
            clip.setMicrosecondPosition(pos);
        }

        public void setLoopPoints(int init, int dest) {
            if (clip == null) return;
            try {
                clip.setLoopPoints(init, dest);
            } catch (IllegalArgumentException e) {
                clip.setLoopPoints(0, -1);
            }
        }

        @Override
        public SoundPlayer clone() {
            return new SoundPlayer(path);
        }

        private static final class SoundPlayerListener implements LineListener {
            private SoundPlayer player;

            private SoundPlayerListener(SoundPlayer player) {
                this.player = player;
            }

            @Override
            public void update(LineEvent event) {
                if (player != null && player.hasManager) {
                    if (event.getType() == LineEvent.Type.START) {
                        AudioManager.soundPlaying.add(player);
                    }
                    if (event.getType() == LineEvent.Type.STOP) {
                        AudioManager.soundPlaying.remove(player);
                    }
                }
            }
        }
    }

    private static HashMap<String, SoundPlayer> soundplayers = new HashMap<String, SoundPlayer>();
    private static HashSet<SoundPlayer> soundPlaying = new HashSet<SoundPlayer>();

    public static SoundPlayer getCacheSoundPlayer(String path) {
        if (path == null) return null;
        if (soundplayers.containsKey(path))
            return soundplayers.get(path);
        SoundPlayer sp = new SoundPlayer(path);
        if (sp.isAvailable()) {
            soundplayers.put(path, sp);
            sp.hasManager = true;
            return sp;
        } else return null;
    }

    public static SoundPlayer getSoundPlayer(String path) {
        SoundPlayer sp = getCacheSoundPlayer(path);
        if (sp == null) return null;
        return sp.clone();
    }

    public static void playSound(String path, int times) {
        SoundPlayer sp = getCacheSoundPlayer(path);
        if (sp == null) return;
        sp.play(times);
    }

    public static void stopSound(String path) {
        if (soundplayers.containsKey(path))
            soundplayers.get(path).stop();
    }

    public static void stopAll() {
        for (Object sp : soundPlaying.toArray()) {
            ((SoundPlayer) sp).stop();
        }
    }

    public static void clearSound() {
        stopAll();
        soundplayers.clear();
    }
}
