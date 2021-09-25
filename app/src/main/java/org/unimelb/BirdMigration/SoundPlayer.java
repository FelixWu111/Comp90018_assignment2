package org.unimelb.BirdMigration;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class SoundPlayer {

    // private AudioAttributes audioAttributes;
    private static SoundPool soundPool;

    public SoundPlayer() {
    }

    public SoundPlayer(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Sound is deprecated in API level 21.(Lollipop)
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(10)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }
    }

    public int loadSound(Context context, int resId) {
        return soundPool.load(context, resId, 1);
    }

    public void playSound(int soundId) {
        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

}
