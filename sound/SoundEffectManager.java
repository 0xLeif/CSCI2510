package sound;

public class SoundEffectManager extends SoundManager {
    public void playSound(String s) {
        switch (s) {
            case "ghost1":
                super.setupSound("soundclips/ghost1.wav", s);
                oneShotClip.fire();
                break;
            case "ghost2":
                super.setupSound("soundclips/ghost2.wav", s);
                oneShotClip.fire();
                break;
            case "jello1":
                super.setupSound("soundclips/jello1.wav", s);
                oneShotStream.fire();
                break;
            case "jello2":
                super.setupSound("soundclips/jello2.wav", s);
                oneShotStream.fire();
                break;
        }
    }
}
