package nz.ac.wgtn.swen225.lc.renderer;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Class for playing sound effects and background music.
 *
 * @author Leory Xue (300607821)
 */
public class Sound {

  private Clip backgroundMusicClip;

  /**
   * Constructor for Sound class.
   */
  public Sound() {
    loadBackgroundMusic();
  }

  /**
   * Load the background music.
   */
  private void loadBackgroundMusic() {
    try {
      URL musicUrl = Sound.class.getResource("/sound/bgm.wav");
      assert musicUrl != null;
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicUrl);
      backgroundMusicClip = AudioSystem.getClip();
      backgroundMusicClip.open(audioInputStream);
      //loop the music
      backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
      e.printStackTrace();
    }
  }

  /**
   * Play the background music.
   */
  public void playBackgroundMusic() {
    if (backgroundMusicClip != null && !backgroundMusicClip.isRunning()) {
      backgroundMusicClip.setFramePosition(0);
      backgroundMusicClip.start();
    }
  }

  /**
   * Stop the background music.
   */
  public void stopBackgroundMusic() {
    if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
      backgroundMusicClip.stop();
    }
  }

  /**
   * Play the damage sound effect.
   */
  public void playDamageSound() {
    playSoundEffect("/sound/damage.wav");
  }

  /**
   * Play the locked sound effect.
   */
  public void playLockedSound() {
    playSoundEffect("/sound/locked.wav");
  }

  /**
   * Play the pickup sound effect.
   */
  public void playPickupSound() {
    playSoundEffect("/sound/treasure.wav");
  }

  /**
   * Play the unlock sound effect.
   */
  public void playUnlockSound() {
    playSoundEffect("/sound/unlock.wav");
  }

  /**
   * Play the death sound effect.
   */
  public void playDeathSound() {
    playSoundEffect("/sound/death.wav");
  }

  /**
   * Play a sound effect.
   *
   * @param soundEffectPath the path to the sound effect.
   */
  private void playSoundEffect(String soundEffectPath) {
    try {
      URL soundEffectUrl = Sound.class.getResource(soundEffectPath);
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundEffectUrl);
      Clip soundEffectClip = AudioSystem.getClip();
      soundEffectClip.open(audioInputStream);
      soundEffectClip.start();
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
      e.printStackTrace();
    }
  }
}
