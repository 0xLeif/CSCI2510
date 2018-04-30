package sound;

public class SoundEffectManager extends SoundManager {
    public void playSound(String s) {
        switch (s) {
            case "ghost1":
                super.setupSound("res/soundclips/ghost1.wav", s);
                oneShotClip.fire();
                break;
            case "ghost2":
                super.setupSound("res/soundclips/ghost2.wav", s);
                oneShotClip.fire();
        }
    }
}
