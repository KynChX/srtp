package com.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class newActivity extends Activity {

	TextView textView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shedule);
		
		Bundle bundle=getIntent().getExtras();
		String shedule=bundle.getString("shedule");
		textView = (TextView) findViewById(R.id.textView2);
		textView.setText(shedule);
	}
	
}
