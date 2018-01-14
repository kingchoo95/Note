package com.david.noted;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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


public class CheckConditionService extends Service{
    private  LocationManager locationManager;
    private  LocationListener locationListener;
    public float getNowLatitude;
    public float getNowLongitude;


    @Override
    public void onCreate() {
        super.onCreate();


         locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                getNowLatitude =(float) location.getLatitude();
                getNowLongitude=(float)location.getLongitude();
                Log.i("locationNow",Float.toString(getNowLatitude)+" "+Float.toString(getNowLongitude));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };
         locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2 * 60 * 1000,500, locationListener);


    }

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

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(cal.getTime());


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
            int latitudeIndex = c.getColumnIndex("latitude");
            int longitudeIndex = c.getColumnIndex("longitude");
            int isTriggerIndex = c.getColumnIndex("isTrigger");

            c.moveToFirst();

            while(c != null){

                //Check time Condition match
                //Log.i("DateNow",dateNow[0] + " "+ c.getString(dateIndex));
                String getReminderTypeDb =c.getString(reminderTypeIndex);
                if(c.getInt(isTriggerIndex) == 0) {
                    if(getReminderTypeDb.equals("Time") || getReminderTypeDb.equals("Time and Place")) {
                        if (dateNow[0].equals(c.getString(dateIndex))) {

                            Calendar now = Calendar.getInstance();

                            String repeatType = c.getString(repeatByIndex);
                            String newRepeatTypeDate = c.getString(dateIndex) ;
                            int isTrigger;
                            if(repeatType.equals("Daily")){
                                now.add(Calendar.DAY_OF_MONTH, 1);
                                SimpleDateFormat newRepeatTypeTime = new SimpleDateFormat("yyyy-MM-dd");
                                newRepeatTypeDate = newRepeatTypeTime.format(now.getTime()) ;
                                isTrigger = 0;
                            }else if(repeatType.equals("Weekly")){
                                now.add(Calendar.DAY_OF_MONTH, 7);
                                SimpleDateFormat newRepeatTypeTime = new SimpleDateFormat("yyyy-MM-dd");
                                newRepeatTypeDate = newRepeatTypeTime.format(now.getTime());
                                isTrigger = 0;
                            }else if(repeatType.equals("Monthly")){
                                now.add(Calendar.MONTH, 1);
                                SimpleDateFormat newRepeatTypeTime = new SimpleDateFormat("yyyy-MM-dd");
                                newRepeatTypeDate = newRepeatTypeTime.format(now.getTime()) ;
                                isTrigger = 0;
                            }else if(repeatType.equals("Yearly")){
                                now.add(Calendar.YEAR, 1);
                                SimpleDateFormat newRepeatTypeTime = new SimpleDateFormat("yyyy-MM-dd");
                                newRepeatTypeDate = newRepeatTypeTime.format(now.getTime()) ;
                                isTrigger = 0;
                            }else{
                                isTrigger = 1;
                            }



                            if (timeNow.equals(c.getString(timeIndex))) {
                                //Log.i("alarmtimeout", "alarm ring");
                                selectedId = c.getInt(idIndex) - 1;
                                selectedTitle = c.getString(titleIndex);
                                showAlert();
                                //Log.i("alarmtimeout",Integer.toString(c.getInt(idIndex)));
                                noteDB.execSQL("UPDATE reminders SET date= '"+ newRepeatTypeDate +"',isTrigger= '"+ isTrigger +"' WHERE id = " + Integer.toString(c.getInt(idIndex)) + "");

                                //Log.i("alarmtimeout",Integer.toString(c.getInt(idIndex)) + c.getString(titleIndex) + c.getString(noteIndex) +  c.getString(reminderTypeIndex) + c.getString(dateIndex) +  c.getString(timeIndex) +  c.getString(repeatByIndex) +c.getString(locationIndex)  +c.getString(latitudeIndex)+c.getString(longitudeIndex)+Integer.toString(c.getInt(isTriggerIndex)));
                            }

                        }
                    }
                    if(getReminderTypeDb.equals("Place") || getReminderTypeDb.equals("Time and Place"))
                    {
                        float lat1 = c.getFloat(latitudeIndex);
                        float lon1 = c.getFloat(longitudeIndex);
                        float lat2 = getNowLatitude;
                        float lon2 = getNowLongitude;
                        Log.i("locationcoor", "db :" + Float.toString(c.getFloat(latitudeIndex)) + " " + Float.toString(c.getFloat(longitudeIndex)) + " now : " + Float.toString(getNowLatitude) + " " + Float.toString(getNowLongitude));
                        Location loc1 = new Location("");
                        loc1.setLatitude(lat1);
                        loc1.setLongitude(lon1);

                        Location loc2 = new Location("");
                        loc2.setLatitude(lat2);
                        loc2.setLongitude(lon2);

                        float distanceInMeters = loc1.distanceTo(loc2);
                        Log.i("location", Float.toString(distanceInMeters));
                        //if less than 300 miters trigger alarm
                        if (distanceInMeters <= 300) {
                            Log.i("alarmtimeout", "alarm ring");
                            selectedId = c.getInt(idIndex) - 1;
                            selectedTitle = c.getString(titleIndex);
                            showAlert();
                            //Log.i("alarmtimeout",Integer.toString(c.getInt(idIndex)));
                            noteDB.execSQL("UPDATE reminders SET isTrigger= '1' WHERE id = " + Integer.toString(c.getInt(idIndex)) + "");

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

            //Log.i("5sec","pass");

            }
        }, 0, 10*1000);


    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkTimer();
        return START_STICKY;
    }

    //vibration, wake screen while sleep, play sound
    public void showAlert(){
        Intent showReminderIntent = new Intent(getApplicationContext(), NoteEditorActivity.class);
        Intent snoozeReminderIntent = new Intent(getApplicationContext(), SnoozeReminderActivity.class);
        Intent helperReminderIntent = new Intent(getApplicationContext(), FriendsListActivity.class);
        snoozeReminderIntent.putExtra("noteId",selectedId);
        Intent viewLocationIntent = new Intent(getApplicationContext(), SearchLocationActivity.class);
        showReminderIntent.putExtra("noteId",selectedId);
        viewLocationIntent.putExtra("noteId",selectedId);
        showReminderIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent showIntent = PendingIntent.getActivity(getApplicationContext(),selectedId, showReminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent helperIntent = PendingIntent.getActivity(getApplicationContext(),selectedId, helperReminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent snoozeIntent = PendingIntent.getActivity(getApplicationContext(),selectedId,snoozeReminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent locationIntent = PendingIntent.getActivity(getApplicationContext(),selectedId,viewLocationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(selectedTitle)
                .setContentIntent(showIntent)
                .addAction(android.R.drawable.sym_action_chat,"Snooze", snoozeIntent)
                .addAction(android.R.drawable.ic_menu_mylocation, "View Location", locationIntent)
                .addAction(android.R.drawable.ic_menu_mylocation, "Find Helper", helperIntent)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(selectedId, notification);

        //wake phone when sleep
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
        wl.acquire(10000);
        PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");

        wl_cpu.acquire(10000);
        // Vibrate for 800 milliseconds
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(800);
        //play default phone sound
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notificationSound);

        r.play();


    }

}

