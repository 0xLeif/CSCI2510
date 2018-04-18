package sound;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundManager {
    protected OneShotEvent oneShotClip;
    protected LoopEvent loopClip;
    protected RestartEvent restartClip;
    protected OneShotEvent oneShotStream;
    protected LoopEvent loopStream;
    protected RestartEvent restartStream;
    protected byte[] soundBytes;
    protected String loaded;

    public SoundManager() {

    }

    protected void setupSound(String filepath, String sound) {
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
