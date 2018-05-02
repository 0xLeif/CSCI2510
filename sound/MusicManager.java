package sound;

public class MusicManager extends SoundManager {
    public void playMusic(String s) {
        switch (s) {
            case "dubstep":
                super.setupSound("res/soundclips/bensound-dubstep.wav", s);
                loopClip.fire();
                break;
            case "chase":
                super.setupSound("res/music/chase_music.wav", s);
                loopClip.fire();
                break;
            case "level":
                super.setupSound("res/music/level.wav", s);
                loopClip.fire();
                break;
            case "gameover":
                super.setupSound("res/music/gameover.wav", s);
                loopClip.fire();
                break;
            case "start":
                super.setupSound("res/music/start.wav", s);
                loopClip.fire();
                break;
            case "victory":
                super.setupSound("res/music/victory.wav", s);
                loopClip.fire();
                break;
        }
    }

    public void stopMusic() {
        loopClip.done();
    }
}
