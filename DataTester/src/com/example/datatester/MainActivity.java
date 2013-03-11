package com.example.datatester;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	private EditText textArea;
	private final int COLOR_REQUEST = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textArea = (EditText)findViewById(R.id.ascii_text);
		Button setBtn = (Button)findViewById(R.id.set_colors_btn);
		setBtn.setOnClickListener((OnClickListener) this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() ==R.id.set_colors_btn){
			Intent colorIntent = new Intent(this, ColorChooser.class);
			this.startActivityForResult(colorIntent, COLOR_REQUEST);
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == COLOR_REQUEST) {
			if(resultCode == RESULT_OK) {
				
			}
		}
	}

}
