package com.iser.isdotgame;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;

import static android.content.Context.AUDIO_SERVICE;

public class SoundPlayer {
//    private AudioAttributes audioAttributes;
//    final int SOUND_POOL_MAX = 2;
//
//    private static SoundPool soundPool;
//    private static int hit1Sound;
//    private static int hit2Sound;
//    private static int looseSound;
//    private static int wonSound;
//    private static int equalSound;
//    private static int boxSelectionSound;
//    private static int backgroundSound;
//
//    private float volume;
//    private Activity activity;
//    private AudioManager audioManager;
//    // Stream type.
//    private static final int streamType = AudioManager.STREAM_MUSIC;
//
//    public SoundPlayer(Context context) {
//        activity = ((Activity)context);
//
//        // AudioManager audio settings for adjusting the volume
//        audioManager = (AudioManager)activity.getSystemService(AUDIO_SERVICE);
//
//        // Current volumn Index of particular stream type.
//        float currentVolumeIndex = (float)audioManager.getStreamVolume(streamType);
//
//        // Get the maximum volume index for a particular stream type.
//        float maxVolumeIndex  = (float)audioManager.getStreamMaxVolume(streamType);
//
//        // Volumn (0 --> 1)
//        this.volume = currentVolumeIndex / maxVolumeIndex;
//
//        activity.setVolumeControlStream(streamType);
//        // SoundPool is deprecated in API level 21. (Lollipop)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//            audioAttributes = new AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_GAME)
//                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                    .build();
//
//            soundPool = new SoundPool.Builder()
//                    .setAudioAttributes(audioAttributes)
//                    .setMaxStreams(SOUND_POOL_MAX)
//                    .build();
//
//        } else {
//            //SoundPool (int maxStreams, int streamType, int srcQuality)
//            soundPool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0);
//        }
//
//        hit1Sound = soundPool.load(context, R.raw.hit1, 1);
//        hit2Sound = soundPool.load(context, R.raw.hit2, 1);
//        wonSound = soundPool.load(context, R.raw.win, 1);
//        looseSound = soundPool.load(context, R.raw.loose, 1);
//        equalSound = soundPool.load(context, R.raw.equal, 1);
//        boxSelectionSound = soundPool.load(context, R.raw.boxselection, 1);
//        backgroundSound = soundPool.load(context, R.raw.background, 1);
//    }
//
//    public void playHit1Sound() {
//        // play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
//        soundPool.play(hit1Sound, volume, volume, 1, 0, 1.0f);
//    }
//
//    public void playHit2Sound() {
//        // play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
//        soundPool.play(hit2Sound, volume, volume, 1, 0, 1.0f);
//    }
//
//    public void playWinSound() {
//        // play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
//        soundPool.play(wonSound, volume, volume, 1, 0, 1.0f);
//    }
//
//    public void playLooseSound() {
//        soundPool.play(looseSound, volume, volume, 1, 0, 1.0f);
//    }
//
//    public void playEqualSound() {
//        soundPool.play(equalSound, volume, volume, 1, 0, 1.0f);
//    }
//
//    public void playBoxSelectionSound() {
//        soundPool.play(boxSelectionSound, volume, volume, 1, 0, 1.0f);
//    }
//
//    public void playBackgroundSound() {
//        soundPool.play(backgroundSound, volume / 2, volume / 2, 1, 1, 1.0f);
//    }

//    MediaPlayer player;
//
//    private float volume;
//    private AudioManager audioManager;
//    // Stream type.
//    private static final int streamType = AudioManager.STREAM_MUSIC;
//
//    private Context context;
//    private Activity activity;
//
//    public SoundPlayer(Context context){
//        this.context = context;
//        this.activity = ((Activity)context);
//
//        // AudioManager audio settings for adjusting the volume
//        audioManager = (AudioManager)activity.getSystemService(AUDIO_SERVICE);
//
//        // Current volumn Index of particular stream type.
//        float currentVolumeIndex = (float)audioManager.getStreamVolume(streamType);
//
//        // Get the maximum volume index for a particular stream type.
//        float maxVolumeIndex  = (float)audioManager.getStreamMaxVolume(streamType);
//
//        // Volumn (0 --> 1)
//        this.volume = currentVolumeIndex / maxVolumeIndex;
//    }
//
//    private void play(MediaPlayer player){
//        player.setVolume(volume, volume);
//        player.setLooping(false); //set looping
//        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        int duration = player.getDuration();
//        player.start();
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                // Actions to do after 10 seconds
//                player.stop();
//                player.release();
//            }
//        }, duration);
//    }
//
//    public void playHit1Sound() {
////        player = MediaPlayer.create(context, R.raw.hit1); //select music file
////        play(player);
////    }
////
////    public void playHit2Sound() {
////        player = MediaPlayer.create(context, R.raw.hit2); //select music file
////        play(player);
////    }
////
////    public void playWinSound() {
////        player = MediaPlayer.create(context, R.raw.win); //select music file
////        play(player);
////    }
////
////    public void playLooseSound() {
////        player = MediaPlayer.create(context, R.raw.loose); //select music file
////        play(player);
////    }
////
////    public void playEqualSound() {
////        player = MediaPlayer.create(context, R.raw.equal); //select music file
////        play(player);
////    }
////
////    public void playBoxSelectionSound() {
////        player = MediaPlayer.create(context, R.raw.boxselection); //select music file
////        play(player);
////    }
////
////    public void playBackgroundSound() {
////        player = MediaPlayer.create(context, R.raw.background); //select music file
////        play(player);
////    }


    private Context context;

    public SoundPlayer(Context context) {
        this.context = context;
    }

    public void stop() {
        HXMusic.stop();
    }

    public void playHit1Sound() {
        HXSound.sound()
                .load(R.raw.hit1) // Sets the resource of the sound effect. [REQUIRED]
                .looped(false)                // Sets the sound effect to be looped. [OPTIONAL]
                .play(context);
    }

    public void playHit2Sound() {
        HXSound.sound()
                .load(R.raw.hit2) // Sets the resource of the sound effect. [REQUIRED]
                .looped(false)                // Sets the sound effect to be looped. [OPTIONAL]
                .play(context);
    }

    public void playWinSound() {
        HXSound.sound()
                .load(R.raw.win) // Sets the resource of the sound effect. [REQUIRED]
                .looped(false)                // Sets the sound effect to be looped. [OPTIONAL]
                .play(context);
    }

    public void playLooseSound() {
        HXSound.sound()
                .load(R.raw.loose) // Sets the resource of the sound effect. [REQUIRED]
                .looped(false)                // Sets the sound effect to be looped. [OPTIONAL]
                .play(context);
    }

    public void playEqualSound() {
        HXSound.sound()
                .load(R.raw.equal) // Sets the resource of the sound effect. [REQUIRED]
                .looped(false)                // Sets the sound effect to be looped. [OPTIONAL]
                .play(context);
    }

    public void playBoxSelectionSound() {
        HXSound.sound()
                .load(R.raw.box) // Sets the resource of the sound effect. [REQUIRED]
                .looped(false)                // Sets the sound effect to be looped. [OPTIONAL]
                .play(context);
    }

    public void playInBackground(){
        HXMusic.music()
                .load(R.raw.background)    // Sets the resource of the song. [REQUIRED]
                .title("Background sound")    // Sets the title of the song. [OPTIONAL]
                .artist("Anonymous")     // Sets the artist of the song. [OPTIONAL]
//                .date("January 1, 1998")     // Sets the date of the song. [OPTIONAL]
//                .at(5)                       // Sets the position for where the song should start. [OPTIONAL]
                .gapless(true)               // Enables gapless playback for this song. [OPTIONAL]
                .looped(true)                // Sets the song to be looped. [OPTIONAL]
                .play(context);
    }
}
