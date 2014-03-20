package com.example.safetext;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	public final static String LOCATION = "com.example.safetext.LOCATION";
	private static final String RETURNING = "com.example.safetext.RETURNING";
	private static final int CONTACT_PICKER_RESULT = 0;

	private String _address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int i = extras.getInt(GeocodingActivity.RETURNING);

			if (i == 1) {
				setContentView(R.layout.phone_num_error_screen);
			}
			if (i == 2) {
				setContentView(R.layout.network_error_screen);
			}
			if (i == 3) {
				setContentView(R.layout.sms_error_screen);
			}
			if (i == 4) {
				setContentView(R.layout.address_error_screen);
			}
		} else {
			setContentView(R.layout.activity_main);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void setLocation(View view) {
		try {
			EditText editText = (EditText) findViewById(R.id.edit_message);
			_address = editText.getText().toString();
		} catch (Exception e) {
			Intent geocoding_intent = new Intent(this, MainActivity.class);
			geocoding_intent.putExtra(RETURNING, 4);
			startActivity(geocoding_intent);
		}
		Intent geocoding_intent = new Intent(this, GeocodingActivity.class);
		geocoding_intent.putExtra(LOCATION, _address);
		startActivity(geocoding_intent);
	}

	public void openContacts(View view) {
	}
}