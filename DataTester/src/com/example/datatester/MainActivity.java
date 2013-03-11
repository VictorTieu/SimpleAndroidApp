package com.example.datatester;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import android.graphics.Color;
import android.content.SharedPreferences;
import android.os.Environment;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.widget.EditText;


public class MainActivity extends Activity implements OnClickListener {

 //request reference for settings
 private final int COLOR_REQUEST=1;
 //picture loader activity return
 private final int LOAD_REQUEST=2;
 //ASCII text area
 private EditText textArea;
 //Shared Preferences for color settings
 private SharedPreferences asciiPrefs;
 //the database helper
 private DataBase imgData;
 //keep track of the current pictures
 private int currentPic=-1;

 @Override
 public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);

  //get the text area
  textArea = (EditText)findViewById(R.id.ascii_text);

  //get the database helper
  imgData = DataBase.getInstance(this);

  //setup click listeners for buttons
  Button setBtn = (Button)findViewById(R.id.set_colors_btn);
  setBtn.setOnClickListener(this);
  Button saveImgBtn = (Button)findViewById(R.id.export_btn);
  saveImgBtn.setOnClickListener(this);
  Button loadBtn = (Button)findViewById(R.id.load_btn);
  loadBtn.setOnClickListener(this);

  //check shared preferences for saved colors
  asciiPrefs = getSharedPreferences("AsciiPicPreferences", 0);
  String chosenColors = asciiPrefs.getString("colors", "");
  //only set if user has already chosen
  if(chosenColors.length()>0){
   //split into tokens: e.g. "#ffffff #000000"
   String[] prefColors = chosenColors.split(" ");
   updateColors(prefColors[0], prefColors[1]);
  }

 }

 public void onClick(View v) {

  //user has clicked settings button
  if(v.getId()==R.id.set_colors_btn) {
   //start activity to display list
   Intent colorIntent = new Intent(this, ColorChooser.class);
   this.startActivityForResult(colorIntent, COLOR_REQUEST);//request code for onActivityResult
  }
  //user has clicked export button
  else if(v.getId()==R.id.export_btn) {
   //helper method
   saveImg();
  }
  //user has clicked load button
  else if(v.getId()==R.id.load_btn) {
   //start activity to display list
   Intent loadIntent = new Intent(this, LoadImage.class);
   this.startActivityForResult(loadIntent, LOAD_REQUEST);//request code for onActivityResult
  }
 }

 @Override
 public boolean onCreateOptionsMenu(Menu menu) {
  getMenuInflater().inflate(R.menu.activity_main, menu);
  return true;
 }

 protected void onActivityResult(int requestCode, int resultCode, Intent data) {

  //data returned from color chooser
  if (requestCode == COLOR_REQUEST) {
   if(resultCode == RESULT_OK){
    //get the two colors chosen for text and background
    String chosenTextColor = data.getStringExtra("textColor");
    String chosenBackColor = data.getStringExtra("backColor");
    //helper method to update display
    updateColors(chosenTextColor, chosenBackColor);
    //write user choice to shared preferences
    SharedPreferences.Editor prefsEd = asciiPrefs.edit();
    prefsEd.putString("colors", ""+chosenTextColor+" "+chosenBackColor);
    prefsEd.commit();
   }
  }
  //data returned from load image activity
  else if(requestCode == LOAD_REQUEST) {
   if(resultCode == RESULT_OK){
    //get id of chosen image
    String pickedID = data.getStringExtra("pickedImg");
    //get the id as an integer and save in class
    currentPic=Integer.parseInt(pickedID);
    //get the database
    SQLiteDatabase savedPicsDB = imgData.getWritableDatabase();
    //query for this picture
    Cursor chosenCursor = savedPicsDB.query("pics", 
      new String[]{DataBase.ASCII_COL}, 
      DataBase.ID_COL+"=?", 
      new String[]{""+currentPic}, 
      null, null, null);
    chosenCursor.moveToFirst();
    //get the ascii text saved
    String savedChars = chosenCursor.getString(0);
    //display in text area ready for editing
    textArea.setText(savedChars);
    chosenCursor.close();
    savedPicsDB.close();
    imgData.close();
   }
  }
 }

 /*
  * Update the color display
  */
 private void updateColors(String tColor, String bColor){

  textArea.setTextColor(Color.parseColor(tColor));
  textArea.setBackgroundColor(Color.parseColor(bColor));
 }

 /*
  * Save the image as a PNG to SD card
  */
 private void saveImg(){

  //check storage availability
  String state = Environment.getExternalStorageState();
  if (Environment.MEDIA_MOUNTED.equals(state)) {

   //media is available for writing
   File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

   //cache the view
   textArea.setDrawingCacheEnabled(true);
   //build the cache of the view
   textArea.buildDrawingCache(true);
   //get cache as a bitmap
   Bitmap bitmap = textArea.getDrawingCache();

   //use the date as a file reference
   Date theDate = new Date();
   String fileName = "asciipic"+theDate.getTime()+".png";
   //define the file location and name
   File picFile = new File(picDir+"/"+fileName);
   //try in case of io errors
   try {
    //create file
    picFile.createNewFile();
    //start an output stream
    FileOutputStream picOut = new FileOutputStream(picFile);
    //output the image data
    boolean worked = bitmap.compress(CompressFormat.PNG, 100, picOut);
    //confirm to user
    if(worked){
     Toast.makeText(getApplicationContext(), "Image saved to your device Pictures " +
       "directory!", Toast.LENGTH_SHORT).show();
    }
    else {
     Toast.makeText(getApplicationContext(), "Whoops! File not saved.",
       Toast.LENGTH_SHORT).show();
    }
    //close the file
    picOut.close();

   } 
   catch (Exception e) { e.printStackTrace(); }
   //destroy cache
   textArea.destroyDrawingCache();
  }
  //user does not have external storage available
  else {
   Toast.makeText(this.getApplicationContext(), "Sorry - you don't have an external" +
     " storage directory available!", Toast.LENGTH_SHORT).show();
  }
 }
}
