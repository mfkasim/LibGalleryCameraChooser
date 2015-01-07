# LibGalleryCameraChooser
Simple library to choose between pick image from camera or gallery



How to use

1.
first of all, you need to create a chooser object

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
	
2.
and you should put this line in the activity onActivityResult method

protected void onActivityResult(int arg0, int arg1, android.content.Intent arg2) {

		super.onActivityResult(arg0, arg1, arg2);

		chooser.onActivityResult(arg0, arg1, arg2);

	};
	
3.
	and to start the chooser, you have to call
			chooser.onStartCameraGalleryRequest();

4. enjoy

