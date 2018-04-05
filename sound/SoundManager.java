package sound;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundManager {
    private OneShotEvent oneShotClip;
    private LoopEvent loopClip;
    private RestartEvent restartClip;
    private OneShotEvent oneShotStream;
    private LoopEvent loopStream;
    private RestartEvent restartStream;
    private byte[] soundBytes;
    private String loaded;

    public SoundManager() {

    }

    public void playSound(String s) {
        switch (s) {
            case "dubstep":
                setupSound("soundclips/bensound-dubstep.wav", s);
                oneShotClip.fire();
                break;
        }
    }

    private void setupSound(String filepath, String sound) {
        InputStream in = ResourceLoader.load(SoundManager.class, filepath, "");
        soundBytes = readBytes(in);
        loadWaveFile(soundBytes);
        loaded = sound;
    }

    public byte[] readBytes(InputStream in) {
        try {
            BufferedInputStream buf = new BufferedInputStream(in);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int read;
            while ((read = buf.read()) != -1) {
                out.write(read);
            }
            in.close();
            return out.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void loadWaveFile(byte[] rawData) {
        shutDownClips();
        oneShotClip = new OneShotEvent(new BlockingClip(rawData));
        oneShotClip.initialize();
        loopClip = new LoopEvent(new BlockingClip(rawData));
        loopClip.initialize();
        restartClip = new RestartEvent(new BlockingClip(rawData));
        restartClip.initialize();
        oneShotStream = new OneShotEvent(new BlockingDataLine(rawData));
        oneShotStream.initialize();
        loopStream = new LoopEvent(new BlockingDataLine(rawData));
        loopStream.initialize();
        restartStream = new RestartEvent(new BlockingDataLine(rawData));
        restartStream.initialize();
    }

    private void shutDownClips() {
        if (oneShotClip != null)
            oneShotClip.shutDown();
        if (loopClip != null)
            loopClip.shutDown();
        if (restartClip != null)
            restartClip.shutDown();
        if (oneShotStream != null)
            oneShotStream.shutDown();
        if (loopStream != null)
            loopStream.shutDown();
        if (restartStream != null)
            restartStream.shutDown();
    }
}
