package com.david.noted;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AchievementActivity extends AppCompatActivity {

    //task status
    int totalCompleted;
    int totalAbandon;
    int totalInProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);
        setTitle("Tasks By Status");

        runTaskStatusDatabase();
        setUpPieChart();
    }

    public void setUpPieChart(){

        double CompletionRatePercentage = (double)(((float)totalCompleted/(float)(totalAbandon + totalCompleted))*100);
        double totalAbandonPercentage = (double)(((float)totalAbandon/(float)(totalAbandon + totalCompleted + totalInProgress))*100);
        double totalInProgressPercentage= (double)(((float)totalInProgress/(float)(totalAbandon + totalCompleted + totalInProgress))*100);
        double totalCompletedPercentage = (double)(((float)totalCompleted/(float)(totalAbandon + totalCompleted + totalInProgress))*100);

        TextView textViewCompletedId = (TextView) findViewById(R.id.textViewCompletedId);
        TextView textViewTotalInProgressId = (TextView) findViewById(R.id.textViewTotalInProgressId);
        TextView textViewTotalTaskAbandonId = (TextView) findViewById(R.id.textViewTotalTaskAbandonId);
        TextView textViewCompletionRateId = (TextView) findViewById(R.id.textViewCompletionRateId);


        DecimalFormat df = new DecimalFormat("#.00");
        String angleFormated = df.format(CompletionRatePercentage);


        textViewCompletedId.setText(Integer.toString(totalCompleted));
        textViewTotalInProgressId.setText(Integer.toString(totalInProgress));
        textViewTotalTaskAbandonId.setText(Integer.toString(totalAbandon));
        textViewCompletionRateId.setText(angleFormated+ "%");




        List<PieEntry> pieEntrys = new ArrayList<>();
        pieEntrys.add(new PieEntry((float)totalCompletedPercentage,"Completed"));
        pieEntrys.add(new PieEntry((float) totalInProgressPercentage,"In progress"));
        pieEntrys.add(new PieEntry((float)totalAbandonPercentage,"Abandon"));


        PieDataSet dataSet = new PieDataSet(pieEntrys, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);


        PieData data = new PieData(dataSet);

        PieChart chart =(PieChart) findViewById(R.id.pieChartId);
        chart.setData(data);
        chart.animateY(1000);
        chart.invalidate();


    }


    //database for recoding task status
    public void runTaskStatusDatabase(){

        try {

            SQLiteDatabase noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
            noteDB.execSQL("CREATE TABLE IF NOT EXISTS taskStatus (id INTEGER, completed INTEGER ,abandon INTEGER)");

            totalInProgress = (int) DatabaseUtils.queryNumEntries(noteDB, "reminders");

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            if(!prefs.getBoolean("firstTime", false)) {
                noteDB.execSQL("INSERT INTO taskStatus (id,completed,abandon) VALUES (1,0,0)");
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstTime", true);
                editor.commit();
            }


            Cursor c = noteDB.rawQuery("SELECT * FROM taskStatus",null);


            int completedIndex = c.getColumnIndex("completed");
            int abandonIndex = c.getColumnIndex("abandon");
            c.moveToFirst();


            while(c != null){

                totalCompleted = c.getInt(completedIndex);
                totalAbandon = c.getInt(abandonIndex);

                c.close();
            }




        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
