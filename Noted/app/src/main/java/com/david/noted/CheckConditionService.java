package com.david.noted;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Created by david on 18/11/2017.
 */

class CheckConditionService extends Service{


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    int selectedId;
    String selectedTitle;
    SQLiteDatabase noteDB;
    public void checkCondition() {
        Calendar cal = Calendar.getInstance();
        System.out.println("Current time => "+cal.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(cal.getTime());
        // formattedDate have current date/time
        Log.i("formattedDate",formattedDate);
        String[] dateNow = formattedDate.split(" ");

        String timeNow =  dateNow[1].substring(0,5);

        try{


            noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);


            Cursor c = noteDB.rawQuery("SELECT * FROM reminders",null);


            int idIndex = c.getColumnIndex("id");
            int titleIndex = c.getColumnIndex("title");
            int noteIndex = c.getColumnIndex("note");
            int reminderTypeIndex = c.getColumnIndex("reminderType");
            int dateIndex = c.getColumnIndex("date");
            int timeIndex = c.getColumnIndex("time");
            int repeatByIndex = c.getColumnIndex("repeatBy");
            int locationIndex = c.getColumnIndex("location");
            int isTriggerIndex = c.getColumnIndex("isTrigger");

            c.moveToFirst();

            while(c != null){

                //Check Condition
                Log.i("DateNow",dateNow[0] + " "+ c.getString(dateIndex));
                if(c.getInt(isTriggerIndex) == 0) {
                    if (dateNow[0].equals(c.getString(dateIndex))) {

                        if (timeNow.equals(c.getString(timeIndex))) {
                            Log.i("alarmtimeout", "alarm ring");
                            selectedId = c.getInt(idIndex)-1;
                            selectedTitle = c.getString(titleIndex);
                            showAlert();
                            //Log.i("alarmtimeout",Integer.toString(c.getInt(idIndex)));
                            noteDB.execSQL("UPDATE reminders SET isTrigger= '1' WHERE id = "+Integer.toString(c.getInt(idIndex))+"");

                            //Log.i("alarmtimeout",Integer.toString(c.getInt(idIndex)) + c.getString(titleIndex) + c.getString(noteIndex) +  c.getString(reminderTypeIndex) + c.getString(dateIndex) +  c.getString(timeIndex) +  c.getString(repeatByIndex) +c.getString(locationIndex) +Integer.toString(c.getInt(isTriggerIndex)));
                        }

                    }
                }
                c.moveToNext();
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void checkTimer(){

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
              checkCondition();
               // Log.i("2sec","passed");
            }
        }, 0, 5000);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkTimer();

        return START_STICKY;
    }


    public void showAlert(){
        Intent showReminderIntent = new Intent(getApplicationContext(), NoteEditorActivity.class);
        Intent snoozeReminderIntent = new Intent(getApplicationContext(), CheckConditionService.class);
        Intent viewLocationIntent = new Intent(getApplicationContext(), NoteEditorActivity.class);
        showReminderIntent.putExtra("noteId",selectedId);
        showReminderIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent showIntent = PendingIntent.getActivity(getApplicationContext(),selectedId, showReminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent snoozeIntent = PendingIntent.getActivity(getApplicationContext(),0,snoozeReminderIntent, 0);
        PendingIntent locationIntent = PendingIntent.getActivity(getApplicationContext(),0,viewLocationIntent, 0);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(selectedTitle)
                .setContentIntent(showIntent)
                .addAction(android.R.drawable.sym_action_chat,"Snooze", snoozeIntent)
                .addAction(android.R.drawable.ic_menu_mylocation, "View Location", locationIntent)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
        // Vibrate for 800 milliseconds
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(800);
        //play default phone sound
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notificationSound);
        r.play();

    }

    // for snooze button
    public void snoozeReminder(){

        noteDB.execSQL("UPDATE reminders SET time = '10:30' WHERE id = "+Integer.toString(selectedId)+"");
    }
}
