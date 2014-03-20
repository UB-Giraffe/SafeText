package com.example.safetext;

import java.io.IOException;
import java.sql.Time;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.telephony.SmsManager;

public class GeocodingActivity extends Activity {
	public final static String RETURNING = "com.example.safetext.RETURNING";
	private double _lat;
	private double _lng;
	private GoogleMap _mMap;
	private String _phoneNumber;
	private String _messageText;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_texting);

	}

	public void geolocation(View view) {
		try {
			EditText one = (EditText) findViewById(R.id.editText1);
			EditText two = (EditText) findViewById(R.id.editText2);
			_phoneNumber = one.getText().toString();
			_messageText = two.getText().toString();
		} catch (Exception ex) {
			Intent geocoding_intent = new Intent(this, MainActivity.class);
			geocoding_intent.putExtra(RETURNING, 1);
			startActivity(geocoding_intent);
		}
		// Get the message from the intent
		Intent i = getIntent();
		String address = i.getStringExtra(MainActivity.LOCATION);
		
		Geocoder geocoder = new Geocoder(this);
		
		try {
			List<Address> list_addresses = geocoder.getFromLocationName(
					address, 3);
			_lat = list_addresses.iterator().next().getLatitude();
			_lng = list_addresses.iterator().next().getLongitude();
			LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			SafeTextListener listener = new SafeTextListener();
			// set updates for every two minutes or five meters
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 5,
					listener);
		} catch (Exception e) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra(RETURNING, 2);
			startActivity(intent);
		}
		/**
		 * Set up the {@link android.app.ActionBar}.
		 */
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		setContentView(R.layout.main_map);
		_mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		 _mMap.addMarker(new MarkerOptions()
		 .position(new LatLng(_lat, _lng)).title("SendText"));
		 _mMap.setMyLocationEnabled(true);
		 _mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(_lat,
		 _lng), 14.0f));
		 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void sendSMS() {
		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(_phoneNumber, null, _messageText, null,
					null);
			Toast.makeText(getApplicationContext(), "SMS Sent!",
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {

			Intent geocoding_intent = new Intent(this, MainActivity.class);
			geocoding_intent.putExtra(RETURNING, 3);
			startActivity(geocoding_intent);

		}

	}

	public void closeGeocodingActivity(View view) {
		startActivity(new Intent(this, MainActivity.class));
	}

	private class SafeTextListener implements LocationListener {

		private boolean _sent;

		public SafeTextListener() {
			_sent = false;

		}

		@Override
		public void onLocationChanged(Location location) {
			double myLat = location.getLatitude(); // location
													// we're at
			double myLong = location.getLongitude(); // location
														// we're
			if (!_sent) { // at
				if ((myLat > _lat - 0.003618) && (myLat < _lat + 0.003618)) {
					if ((myLong > _lng - 0.003618)
							&& (myLong < _lng + 0.003618)) {
						sendSMS();
						_sent = true;
						setContentView(R.layout.message_sent);

					}
				}
			}

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	}

}
