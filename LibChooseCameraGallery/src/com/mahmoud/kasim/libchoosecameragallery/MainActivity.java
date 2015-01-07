package com.mahmoud.kasim.libchoosecameragallery;

import java.io.File;
import java.io.InputStream;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View view) {

		chooser.onStartCameraGalleryRequest();
	}

	protected void onActivityResult(int arg0, int arg1, android.content.Intent arg2) {

		super.onActivityResult(arg0, arg1, arg2);

		chooser.onActivityResult(arg0, arg1, arg2);

	};

	CameraGalleryChooser chooser = new CameraGalleryChooser(this) {

		@Override
		public void onFileSelectionSucceed(File file) {

			Toast.makeText(getApplicationContext(), file.getAbsolutePath(), Toast.LENGTH_LONG).show();

		}

		@Override
		public void onFileSelectionFailed() {
			Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onStreamSelectedSucceed(InputStream is) {

			
			
		}
	};

}
