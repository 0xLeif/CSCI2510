package sound;

public class MusicManager extends SoundManager {
    public void playMusic(String s) {
        switch (s) {
            case "dubstep":
                super.setupSound("res/soundclips/bensound-dubstep.wav", s);
                oneShotStream.fire();
                break;
        }
    }
}
