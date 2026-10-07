package com.example.jukeboxservicej;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class JukeBoxService extends Service {
    // The actual media player to play the music
    private MediaPlayer player = null;

    // Constants
    public static final String ACTION_PLAY = "play";
    public static final String ACTION_STOP = "stop";

    /*
     * This method is called each time a request comes in from the app
     * via an intent. It processes the request by playing or stopping
     * the song as appropriate.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        JukeBoxServiceThread t = new JukeBoxServiceThread(this, intent);
        t.start();

        return START_STICKY; // START_STICKY means service will keep running
    }

    /*
     * This method specifies how our service will deal with binding.
     * We don't choose to support binding, which is indicated by returning null.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Inner class representing the background thread
    private class JukeBoxServiceThread extends Thread {
        private final Service service;
        private final Intent intent;

        public JukeBoxServiceThread(Service service, Intent intent) {
            this.service = service;
            this.intent = intent;
        }

        @Override
        public void run() {
            String action = intent.getAction();
            if (ACTION_PLAY.equals(action)) {
                // Convert the song title into a resource ID
                // e.g. "ehonda" -> R.raw.ehonda
                String title = intent.getStringExtra("title");
                int id = getResources().getIdentifier(title, "raw", getPackageName());

                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    // Ignore exception as per original Kotlin design
                }

                // Stop any previous playing song
                if (player != null) {
                    player.stop();
                    player.release();
                }

                // Start the new song
                player = MediaPlayer.create(service, id);
                if (player != null) {
                    player.setLooping(true);
                    player.start();
                }

            } else if (ACTION_STOP.equals(action)) {
                // Stop any currently playing song
                if (player != null) {
                    player.stop();
                    player.release();
                    player = null; // Good practice to nullify after release
                }
            }
        }
    }
}