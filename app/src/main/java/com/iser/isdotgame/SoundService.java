package com.iser.isdotgame;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

    public class SoundService extends Service {
        MediaPlayer player;

        private float volume;
        private AudioManager audioManager;
        // Stream type.
        private static final int streamType = AudioManager.STREAM_MUSIC;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        public void onCreate() {
            // AudioManager audio settings for adjusting the volume
            audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);

            // Current volumn Index of particular stream type.
            float currentVolumeIndex = (float)audioManager.getStreamVolume(streamType);

            // Get the maximum volume index for a particular stream type.
            float maxVolumeIndex  = (float)audioManager.getStreamMaxVolume(streamType);

            // Volumn (0 --> 1)
            this.volume = currentVolumeIndex / maxVolumeIndex;

            player = MediaPlayer.create(this, R.raw.background); //select music file
            player.setVolume(volume / 2, volume / 2);
            player.setLooping(true); //set looping
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }

        public int onStartCommand(Intent intent, int flags, int startId) {
            player.start();
            return Service.START_NOT_STICKY;
        }

        public void onDestroy() {
            player.stop();
            player.release();
            stopSelf();
            super.onDestroy();
        }

    }
