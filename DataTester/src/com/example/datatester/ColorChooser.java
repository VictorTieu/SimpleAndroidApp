package com.example.datatester;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class ColorChooser extends Activity {

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.color_choice);
	}
	
	public void setColors(View view){
		String tagInfo = (String)view.getTag();
		String[] tagColors = tagInfo.split(" ");
		Intent backIntent = new Intent();
		backIntent.putExtra("textColor", tagColors[0]);
		backIntent.putExtra("backColor", tagColors[1]);
		setResult(RESULT_OK, backIntent);
		finish();
	}
}
