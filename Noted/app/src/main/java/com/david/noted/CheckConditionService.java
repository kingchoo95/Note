package com.david.noted;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by david on 18/11/2017.
 */

class CheckConditionService extends Service{


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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

            SQLiteDatabase noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);



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
                            showAlert();
                            Log.i("alarmtimeout",Integer.toString(c.getInt(idIndex)));
                            noteDB.execSQL("UPDATE reminders SET isTrigger= '1' WHERE id = "+Integer.toString(c.getInt(idIndex))+"");

                            Log.i("alarmtimeout",Integer.toString(c.getInt(idIndex)) + c.getString(titleIndex) + c.getString(noteIndex) +  c.getString(reminderTypeIndex) + c.getString(dateIndex) +  c.getString(timeIndex) +  c.getString(repeatByIndex) +c.getString(locationIndex) +Integer.toString(c.getInt(isTriggerIndex)));
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
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivities(getApplicationContext(),1, new Intent[]{intent}, 0);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("mdv time to d")
                .setContentText("ya agree")
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.sym_action_chat,"Chat", pendingIntent)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);


    }
}
