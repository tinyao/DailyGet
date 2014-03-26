package im.ycz.dailyget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import java.io.IOException;

import im.ycz.dailyget.R;
import im.ycz.dailyget.data.Task;

/**
 * Created by tinyao on 11/17/13.
 */
public class EmptyWindows extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.empty);

        Intent ii = getIntent();
        Task task = (Task) ii.getSerializableExtra("task");

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        // media player
        final MediaPlayer mMediaPlayer = new MediaPlayer();
        try
        {
            mMediaPlayer.setDataSource(this, alert);
        } catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.setLooping(true);
            try
            {
                mMediaPlayer.prepare();
            } catch (IllegalStateException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mMediaPlayer.start();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Time for '" + task.title + "' !")
                .setCancelable(false)
                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mMediaPlayer.stop();
                        dialog.cancel();
                        EmptyWindows.this.finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mMediaPlayer.stop();
                        dialog.cancel();
                        EmptyWindows.this.finish();
                    }
                });
        builder.show();
//        AlertDialog alert1 = builder.create();
//        alert1.show();
    }
}
