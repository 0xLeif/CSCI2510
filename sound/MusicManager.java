package sound;

public class MusicManager extends SoundManager {
    public void playMusic(String s) {
        switch (s) {
            case "dubstep":
                super.setupSound("soundclips/bensound-dubstep.wav", s);
                oneShotStream.fire();
                break;
        }
    }
}
