package com.david.noted;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.david.noted.MainActivity.arrayAdapter;

public class NoteEditorActivity extends AppCompatActivity {

    //variable for dialog reminder
    Dialog dialog,dialogTag,dialogImage;
    String dialogReminderType = "";
    String dialogDate = "";
    String dialogTime = "";
    String dialogRepeatBy = "";
    String dialogLocation = "";
    String locationString = "";
    String getReminderDate = "";
    String getReminderTime = "";
    String imageLocation = "";
    String convertedArrayList= "";
    String convertedArrayListImage= "";
    String tagTitle = "None";
    Float getPlaceLatitude = (float)200;
    Float getPlaceLongitude = (float)200;
    Float placeLatitude;
    Float placeLongitude;
    int getIsTrigger;
    Button addListItemButton;
    static CustomAdapter customAdapter;
    ArrayList<String> listItemArray = new ArrayList<>();
    ListView listItem;
    EditText et;
    //make string array for spinner
    String repeats[]={"Does not repeat","Daily","Weekly","Monthly","Yearly"};
    //date and time picker for dialog
    private DatePickerDialog.OnDateSetListener dDateSetListener;
    private TimePickerDialog.OnTimeSetListener tTimeSetListener;

    int noteId;

    SQLiteDatabase noteDB;
    //defines array adapter of string type
    ArrayAdapter repeatAdapter;

    EditText editTextTitle,editTextNotes;
    //Declare for date dialog


    //Declare for tag dialog
    TextView dialogTagCancel,dialogTagSave,currentTagTextView;
    EditText dialogTagEditText;
    Button buttonTagAddNewTag;
    static ArrayList<String> tagsArraylist = new ArrayList<>();
    Set<String> filteredArraylist = new HashSet<>();
    ArrayAdapter arrayAdapterTag;
    ListView listViewTag;

    //Declare for imageView
    ListView listViewImage;
    ArrayList<String> ImageViewArrayList = new ArrayList<>();
    ImageView imageViewId;
    static CustomAdapterImageView customAdapterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        //services to check condition
        addArrayListIntoImageListView();

        listItem = (ListView) findViewById(R.id.listItemListViewId);

        editTextTitle = (EditText) findViewById(R.id.editTextTitleId);
        editTextNotes = (EditText) findViewById(R.id.editTextAddNoteHereId);
        addListItemButton = (Button) findViewById(R.id.addListItemButtonId);


        Intent intent = new Intent(this, CheckConditionService.class);
        startService(intent);
        bottomNavigationBar();
        noteDB = openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
        repeatAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, repeats);



        intent = getIntent();
        dialog = new Dialog(NoteEditorActivity.this);
        dialog.setContentView(R.layout.add_reminder_dialog);

        dialogTag = new Dialog(NoteEditorActivity.this);
        dialogTag.setContentView(R.layout.add_tag_dialog);

        dialogImage = new Dialog(NoteEditorActivity.this);
        dialogImage.setContentView(R.layout.image_dialog);
        searchAndAddAllTags();
        listViewTag = (ListView) dialogTag.findViewById(R.id.tagListViewId);
        arrayAdapterTag = new ArrayAdapter(this,android.R.layout.simple_list_item_1,filteredArraylist.toArray());
        listViewTag.setAdapter(arrayAdapterTag);
        noteId = intent.getIntExtra("noteId",noteId);

        if(noteId != -1 ){

            Cursor c = noteDB.rawQuery("SELECT * FROM reminders WHERE id = " + Integer.toString(noteId+1)+ "", null);
            Log.i("noteId",Integer.toString(noteId));
            int idIndex = c.getColumnIndex("id");
            int titleIndex = c.getColumnIndex("title");
            int noteIndex = c.getColumnIndex("note");
            int checkListIndex = c.getColumnIndex("checkList");
            int imageIndex = c.getColumnIndex("image");
            int tagIndex = c.getColumnIndex("tag");
            int reminderTypeIndex = c.getColumnIndex("reminderType");
            int dateIndex = c.getColumnIndex("date");
            int timeIndex = c.getColumnIndex("time");
            int repeatByIndex = c.getColumnIndex("repeatBy");
            int locationIndex = c.getColumnIndex("location");
            int latitudeIndex = c.getColumnIndex("latitude");
            int longitudeIndex = c.getColumnIndex("longitude");
            int isTriggerIndex = c.getColumnIndex("isTrigger");

            c.moveToFirst();

           //Log.i("Resultdbcontidion",Integer.toString(c.getInt(idIndex)) + c.getString(titleIndex) + c.getString(noteIndex) + c.getString(reminderTypeIndex) + c.getString(dateIndex) + c.getString(repeatByIndex) + c.getString(locationIndex) + c.getString(latitudeIndex)+" " + c.getString(longitudeIndex));

            editTextTitle.setText(c.getString(titleIndex));
            editTextNotes.setText(c.getString(noteIndex));
            imageLocation = c.getString(imageIndex);

            try {
                convertJsonObjectToArrayListImage();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            convertedArrayList = c.getString(checkListIndex);
            dialogReminderType = c.getString(reminderTypeIndex);
            dialogDate = c.getString(dateIndex);
            dialogTime = c.getString(timeIndex);
            dialogRepeatBy = c.getString(repeatByIndex);
            dialogLocation =  c.getString(locationIndex);
            getIsTrigger =  c.getInt(isTriggerIndex);
            placeLatitude = c.getFloat(latitudeIndex);
            placeLongitude = c.getFloat(longitudeIndex);
            tagTitle = c.getString(tagIndex);
            try {
                convertJsonObjectToArrayList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            hideImageView();
            arrayAdapter.notifyDataSetChanged();
        }

        listItem.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                checkItemArrayList();
                listItemArray.remove(position);
                NoteEditorActivity.customAdapter.notifyDataSetChanged();
                return false;
            }
        });

        listViewImage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                    new AlertDialog.Builder(NoteEditorActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Are you sure?")
                            .setMessage("Do you want to remove photo?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ImageViewArrayList.remove(position);
                                    NoteEditorActivity.customAdapterImageView.notifyDataSetChanged();
                                    if(!ImageViewArrayList.isEmpty()){
                                        showImageView();
                                    }else{
                                        hideImageView();
                                    }
                                }

                            }).setNegativeButton("No",null).show();



                return false;
            }
        });
        listViewImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //tell which row the user tap

                String imageTitle = ImageViewArrayList.get(position).toString();
                ImageView showImage = (ImageView) dialogImage.findViewById(R.id.imageViewDialogId);
                showImage.setImageURI(Uri.fromFile(new File(imageTitle)));
                dialogImage.show();

            }
        });
        dismissImageDialog();
    }
    // menu bar for add reminder start
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.addreminder_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.addReminderId ){

        final Spinner repeatSp = (Spinner)dialog.findViewById(R.id.spinnerRepeatId);
        //find textView id for dialog
        TextView saveTextBtn = (TextView) dialog.findViewById(R.id.textViewSaveId);
        TextView removeTextBtn = (TextView) dialog.findViewById(R.id.textViewRemoveId);
        TextView cancelTextBtn = (TextView) dialog.findViewById(R.id.textViewCancelId);
        final TextView textViewDateId = (TextView) dialog.findViewById(R.id.textViewDateId);
        final TextView textViewTimeId = (TextView) dialog.findViewById(R.id.textViewTimeId);
        //find id for radio button
        final RadioGroup radioGroupReminderType = (RadioGroup) dialog.findViewById(R.id.radioReminderType);
        RadioButton placeRadioButton = (RadioButton) dialog.findViewById(R.id.radioPlaceId);
        RadioButton timeRadioButton = (RadioButton) dialog.findViewById(R.id.radioTimeId);
        RadioButton timenplaceRadioButton = (RadioButton) dialog.findViewById(R.id.radioTimeAndPlaceId);

        //set adapter to spinner
        repeatSp.setAdapter(repeatAdapter);
            if(dialogReminderType.equals("Place")) {
                placeRadioButton.setChecked(true);
            }
            if(dialogReminderType.equals("Time")){
                timeRadioButton.setChecked(true);
            }
            if(dialogReminderType.equals("Time and Place")){
                timenplaceRadioButton.setChecked(true);
            }

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.locationReminderSectionId);

        if(dialogLocation==""){
            autocompleteFragment.setHint("Add a location");
        }else{
            autocompleteFragment.setText(dialogLocation);
        }


        //auto complete
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                locationString = place.getName().toString();
                getPlaceLatitude = (float) place.getLatLng().latitude;
                getPlaceLongitude = (float) place.getLatLng().longitude;

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("tag", "An error occurred: " + status);
            }
        });


        if((!dialogDate.equals("") && !dialogTime.equals(""))){

            textViewDateId.setText(dialogDate);
            textViewTimeId.setText(dialogTime);

        }else{

            textViewDateId.setText("Add Date");
            textViewTimeId.setText("Add Time");

        }

        textViewDateId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dateDialog = new DatePickerDialog(NoteEditorActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, dDateSetListener,year, month,day);
                //to fix white background
                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dateDialog.show();
            }
        });

        dDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String newMonthFormat;
                String newDayOfMonthFormat;
                if(month+1 >=1 && month+1 <= 9){
                    newMonthFormat = "0"+Integer.toString(month+1);
                }else {
                    newMonthFormat = Integer.toString(month+1);
                }
                if(dayOfMonth >=1 && dayOfMonth <= 9){
                    newDayOfMonthFormat = "0"+Integer.toString(dayOfMonth);
                }else {
                    newDayOfMonthFormat = Integer.toString(dayOfMonth);
                }


                getReminderDate = Integer.toString(year)+"-"+newMonthFormat+"-"+newDayOfMonthFormat;
                textViewDateId.setText(getReminderDate);
            }
        };

        textViewTimeId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int min = cal.get(Calendar.MINUTE);

                TimePickerDialog timeDialog = new TimePickerDialog(NoteEditorActivity.this, tTimeSetListener, hour, min, true);
                timeDialog.show();
            }
        });

        tTimeSetListener = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(android.widget.TimePicker view,
                                  int hourOfDay, int minute) {
                String newMinuteFormat;
                String newHourFormat;
                if(hourOfDay>=0 &&hourOfDay<=9 ){
                    newHourFormat ="0"+Integer.toString(hourOfDay);
                }else{
                    newHourFormat =Integer.toString(hourOfDay);
                }

                if (minute>=0 &&minute<=9){
                    newMinuteFormat = "0"+Integer.toString(minute);
                }else{
                    newMinuteFormat= Integer.toString(minute);
                }
                getReminderTime = newHourFormat+":"+ newMinuteFormat;
                textViewTimeId.setText(getReminderTime);
            }
        };
        removeTextBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogLocation ="";
                dialogRepeatBy = "";
                dialogDate ="";
                dialogTime = "";
                getReminderDate="";
                getReminderTime="";
                locationString="";
                dialogReminderType ="";
                placeLatitude =(float)200;
                placeLongitude=(float)200;
                textViewDateId.setText("Add Date");
                textViewTimeId.setText("Add Time");

                dialog.dismiss();
            }
        });
        cancelTextBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = radioGroupReminderType.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                RadioButton radioButtonSelected = (RadioButton) dialog.findViewById(selectedId);
                String reminderType = radioButtonSelected.getText().toString();

                //get and assign spinner value
                dialogRepeatBy = repeatSp.getSelectedItem().toString();

                getIsTrigger = 0;
                //get and assign radio button value

                dialogReminderType = reminderType;
                if(getPlaceLatitude == null||getPlaceLongitude==null){
                    placeLatitude =(float)200;
                    placeLongitude=(float)200;
                }else{
                    placeLatitude =getPlaceLatitude;
                    placeLongitude=getPlaceLongitude;
                }
                if(reminderType.equals("Time")) {
                    checkDateAndTimeIsNull();
                }
                if(reminderType.equals("Place")){
                    checkPlaceIsNull();
                }
                if(reminderType.equals("Time and Place")){
                    checkPlaceNTimeIsNull();
                }

            }
        });

        dialog.show();
        return true;

    }else if(item.getItemId() == R.id.addPhotoId){
            takeImage();
        return true;


    }else if(item.getItemId() == R.id.addAudioId){
            voiceInput();
        return true;
    }else if(item.getItemId() == R.id.addTagId){


            addTag();
            return true;
        }else{
        return false;
    }

}
    public void checkDateAndTimeIsNull(){


        if((getReminderDate=="" || getReminderTime=="")){

            Toast.makeText(getApplicationContext(),"Please update time and date to save",Toast.LENGTH_SHORT).show();
        }else{
            dialogDate = getReminderDate;
            dialogTime = getReminderTime;
            dialogLocation = locationString;
            dialog.dismiss();

        }
    }

    public void checkPlaceIsNull(){
        if(dialogRepeatBy !="Does not repeat"){
            Toast.makeText(getApplicationContext(),"Reminder will not repeat for place reminder!",Toast.LENGTH_SHORT).show();
        }

        if((locationString=="")){

            Toast.makeText(getApplicationContext(),"Please update a location",Toast.LENGTH_SHORT).show();
        }else{
            dialogLocation = locationString;

            dialog.dismiss();

        }
    }
    public void checkPlaceNTimeIsNull(){
        if(dialogRepeatBy !="Does not repeat"){
            Toast.makeText(getApplicationContext(),"Reminder will not repeat for place reminder!",Toast.LENGTH_SHORT).show();
        }

        if((locationString==""||getReminderDate=="" || getReminderTime=="")){

            Toast.makeText(getApplicationContext(),"Please update time, date and location",Toast.LENGTH_SHORT).show();
        }else{
            dialogLocation = locationString;
            dialogDate = getReminderDate;
            dialogTime = getReminderTime;
            dialog.dismiss();

        }
    }
    // menu bar for add reminder end
    public void preventNullValue(){
        if(dialogLocation==null){
            dialogLocation="";
        }

        if(placeLatitude==null){
            placeLatitude =(float)200;
        }
        if(placeLongitude==null){

            placeLongitude=(float)200;
        }
        if(dialogReminderType==null){
            dialogReminderType="";
        }
        if(dialogRepeatBy==null){
            dialogRepeatBy="";
        }
        if(convertedArrayList==null){
            convertedArrayList="";
        }
        if(convertedArrayListImage==null){
            convertedArrayListImage="";
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        //prevent null value if user did not click add reminder button
        if(et!=null){
            checkItemArrayList();
        }
        convertArrayListToJasonObject();
        convertArrayListImageToJasonObject();
        preventNullValue();
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {

            if(noteId ==-1){
                SQLiteDatabase noteDB = openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
                int numRows = (int) DatabaseUtils.queryNumEntries(noteDB, "reminders");
                Log.i("noteId","-1");
                noteDB.execSQL("INSERT INTO reminders (id, title, note,checklist , image, reminderType, tag, date, time, repeatBy, location, latitude, longitude, isTrigger) VALUES ( " + Integer.toString(numRows+1) + ",'" + editTextTitle.getText().toString() + "' ,'" + editTextNotes.getText().toString() + "','"+ convertedArrayList +"' ,'"+ convertedArrayListImage +"' ,'"+ dialogReminderType +"','"+ tagTitle +"' ,'"+ dialogDate +"', '"+  dialogTime +"','"+ dialogRepeatBy +"', '"+ dialogLocation +"','"+ placeLatitude +"' ,'"+ placeLongitude +"','0')");

                MainActivity.titles.add(editTextTitle.getText().toString());
                arrayAdapter.notifyDataSetChanged();
                onBackPressed();
                return true;
            }else{

                noteDB.execSQL("UPDATE reminders SET title= '"+ editTextTitle.getText().toString() +"',note = '"+ editTextNotes.getText().toString() +"',checklist ='"+convertedArrayList+"' ,image = '"+ convertedArrayListImage +"',reminderType = '" + dialogReminderType + "', tag = '"+ tagTitle +"' ,date = '" + dialogDate + "',time = '"+ dialogTime +"', repeatBy = '" + dialogRepeatBy + "', location = '"+ dialogLocation +"',latitude = '"+ placeLatitude+"', longitude = '"+placeLongitude+"',isTrigger = '"+getIsTrigger +"'  WHERE id = "+ Integer.toString(noteId+1)+"");
                if(SearchReminderActivity.arrayAdapter != null) {

                    SearchReminderActivity.titlesFilter.set(noteId, editTextTitle.getText().toString());
                    SearchReminderActivity.arrayAdapter.notifyDataSetChanged();
                }

                MainActivity.titles.set(noteId,editTextTitle.getText().toString());
                arrayAdapter.notifyDataSetChanged();


            }

        }

        return super.onKeyDown(keyCode, event);
    }

    public void bottomNavigationBar(){

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_editor);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.action_normalText:
                        hideItemList();
                        enableAudioButton();

                        if(!ImageViewArrayList.isEmpty()){
                        showImageView();

                        }else{
                        hideImageView();

                        }
                        break;
                    case R.id.action_tickBoxes:
                        hideTextOnly();
                        runCheckBox();
                        diableHeaderButton();
                        hideImageView();
                        break;

                }
                return true;
            }
        });

    }

    public void takeImage() {

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)  != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{


            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == 3&& resultCode == RESULT_OK ){

            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String text = results.get(0);
            editTextNotes.setText(text);

        }
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                //imageView.setImageBitmap(bitmap);


                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if(cursor.moveToFirst()){
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageLocation = cursor.getString(columnIndex);
                    ImageViewArrayList.add(imageLocation);
                    if(!imageLocation.equals("")){
                        showImageView();
                    }else{
                        hideImageView();
                    }

                }
                cursor.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takeImage();
            }
        }
    }

    public void showImageView(){
        listViewImage.setVisibility(View.VISIBLE);
        listViewImage.getLayoutParams().height = 700;
        editTextNotes.getLayoutParams().height = 1500;

    }
    public void runCheckBox(){
        customAdapter = new CustomAdapter();
        listItem.setAdapter(customAdapter);
    }

    public void hideImageView(){
        listViewImage.setVisibility(View.GONE);
        editTextNotes.getLayoutParams().height = 2200;
        listItem.getLayoutParams().height = 1100;
    }

    public void hideItemList(){
        editTextNotes.setVisibility(View.VISIBLE);
        addListItemButton.setVisibility(View.GONE);
        listItem.setVisibility(View.GONE);
        editTextNotes.getLayoutParams().height = 2200;
    }

    //when user click from text only to checkbox
    public void hideTextOnly(){
        editTextNotes.setVisibility(View.GONE);
        addListItemButton.setVisibility(View.VISIBLE);
        listItem.setVisibility(View.VISIBLE);
        listItem.getLayoutParams().height = 1100;
    }

    public void voiceInput(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(i, 3);
    }

    //create custom listview
    class CustomAdapter extends BaseAdapter implements Filterable {

        @Override
        public int getCount() {
            return listItemArray.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.custom_checklist,null);
            EditText listItemListView = (EditText) convertView.findViewById(R.id.editTextCheckBoxId);
            listItemListView.setText(listItemArray.get(position));
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return null;
        }
    }

    public void addNewList(View view){
        checkItemArrayList();
        listItemArray.add("");
        NoteEditorActivity.customAdapter.notifyDataSetChanged();
    }

    public void checkItemArrayList(){
        View v;

        listItemArray.clear();

        for(int x = 0 ; x < listItem.getCount() ; x++){
            v = listItem.getChildAt(x);

            et = (EditText)v.findViewById(R.id.editTextCheckBoxId);
            listItemArray.add(et.getText().toString());
        }
    }

    public void convertArrayListToJasonObject(){


        JSONObject json = new JSONObject();
        try {
            Log.i("listItemArray",listItemArray.toString());
            json.put("uniqueArrays", new JSONArray(listItemArray));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        convertedArrayList = json.toString();
    }

    public void convertJsonObjectToArrayList() throws JSONException {

        if(!convertedArrayList.equals("")){


            runCheckBox();
            JSONObject json = null;

            try {
                json = new JSONObject(convertedArrayList);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray items =  json.optJSONArray("uniqueArrays");

            for (int i = 0; i < items.length(); i++) {
                listItemArray.add(items.getString(i));
            }


            listItem.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();


        }else{
            hideItemList();
        }
    }

    public void diableHeaderButton(){

        View takePhotoButton =  findViewById(R.id.addPhotoId);
        View addAudioButton =  findViewById(R.id.addAudioId);
        takePhotoButton.setEnabled(false);
        addAudioButton.setEnabled(false);
        Toast.makeText(getApplicationContext(),"Pick photo and audio recording are disabled!",Toast.LENGTH_SHORT).show();

    }
    public void enableAudioButton(){
        View takePhotoButton =  findViewById(R.id.addPhotoId);
        View addAudioButton =  findViewById(R.id.addAudioId);
        takePhotoButton.setEnabled(true);
        addAudioButton.setEnabled(true);
        Toast.makeText(getApplicationContext(),"Pick photo and audio recording are enabled!",Toast.LENGTH_SHORT).show();
    }


    public void addTag(){

        dialogTagSave = (TextView) dialogTag.findViewById(R.id.textViewTagSaveId);
        dialogTagCancel = (TextView) dialogTag.findViewById(R.id.textViewTagCancelId);
        dialogTagEditText = (EditText) dialogTag.findViewById(R.id.editTextTagId);
        currentTagTextView = (TextView) dialogTag.findViewById(R.id.currentTagTextViewId);
        buttonTagAddNewTag = (Button) dialogTag.findViewById(R.id.buttonAddNewTagId);verifyTag();
        dialogTag.show();
        dialogTagCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogTag.dismiss();

            }
        });

        dialogTagSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogTag.dismiss();

            }
        });

        buttonTagAddNewTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewTag();
            }
        });

        listViewTag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //tell which row the user tap

                tagTitle = listViewTag.getItemAtPosition(position).toString();
                currentTagTextView.setText(tagTitle);
            }
        });
    }

    public void saveNewTag(){

        String tempTag = dialogTagEditText.getText().toString();

        if(!tempTag.equals("")){
            tagTitle = tempTag.substring(0, 1).toUpperCase() + tempTag.substring(1).toLowerCase();
        }else{
            tagTitle= tempTag;
        }

        verifyTag();


    }

    public void verifyTag(){
        if(tagTitle.equals("")){
            currentTagTextView.setText("None");
            tagTitle="None";
        }else{
            currentTagTextView.setText(tagTitle);
        }
    }

    public void searchAndAddAllTags(){

        tagsArraylist.clear();
        try{

            SQLiteDatabase noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);

            Cursor c = noteDB.rawQuery("SELECT * FROM reminders",null);
            int tagIndex = c.getColumnIndex("tag");
            c.moveToFirst();

            while(c != null){
                tagsArraylist.add(c.getString(tagIndex));
                c.moveToNext();
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        //prevent duplicated tag
        filteredArraylist.clear();
        filteredArraylist.addAll(tagsArraylist);
    }


    //create custom listview
    class CustomAdapterImageView extends BaseAdapter implements Filterable{

        @Override
        public int getCount() {
            return ImageViewArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.custom_image_listview,null);
            imageViewId = (ImageView) convertView.findViewById(R.id.imageViewId);

            imageViewId.setImageURI(Uri.fromFile(new File(ImageViewArrayList.get(position))));


            return convertView;
        }

        @Override
        public Filter getFilter() {

            return null;
        }


    }

    //add array list into listView
    public void addArrayListIntoImageListView(){
        customAdapterImageView = new CustomAdapterImageView();
        listViewImage = (ListView) findViewById(R.id.listViewImageId);

        if(ImageViewArrayList != null){
            listViewImage.setAdapter(customAdapterImageView);
        }
    }

    public void convertArrayListImageToJasonObject(){


        JSONObject json = new JSONObject();
        try {

            json.put("uniqueImageArrays", new JSONArray(ImageViewArrayList));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        convertedArrayListImage = json.toString();
    }

    public void convertJsonObjectToArrayListImage() throws JSONException {

            JSONObject json = null;

            try {
                json = new JSONObject(imageLocation);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray items =  json.optJSONArray("uniqueImageArrays");

            for (int i = 0; i < items.length(); i++) {
                ImageViewArrayList.add(items.getString(i));
            }

            listViewImage.setAdapter(customAdapterImageView);

            customAdapterImageView.notifyDataSetChanged();

            Log.i("ImageViewArrayList",ImageViewArrayList.toString());
        if(!ImageViewArrayList.isEmpty()){
            showImageView();
            Log.i("gotimage","yes!");
        }else{
            hideImageView();
            Log.i("gotimage","no!");
        }

    }
    public void dismissImageDialog(){
        ImageButton imageButtonDismiss = (ImageButton) dialogImage.findViewById(R.id.imageButtonDismissId);

        imageButtonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogImage.dismiss();

            }
        });

    }

}