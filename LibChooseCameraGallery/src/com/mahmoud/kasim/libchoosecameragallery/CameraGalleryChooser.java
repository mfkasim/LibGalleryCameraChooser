package com.mahmoud.kasim.libchoosecameragallery;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public abstract class CameraGalleryChooser implements ICameraGalleryChooser {

	public static final int MODE_BOTH = 0x03;
	public static final int MODE_GALLERY = 0x02;
	public static final int MODE_CAMERA = 0x01;

	private int mRequestGallery = 10;
	private int mRequestCamera = 11;

	private int mMode = MODE_BOTH;

	Fragment mFragment;

	private Uri mImageUri;

	private FragmentActivity mActivity;

	public CameraGalleryChooser(FragmentActivity activity) {

		if (activity == null)
			throw new IllegalArgumentException();

		mActivity = activity;
	}

	public CameraGalleryChooser(FragmentActivity activity, int requestGalleryCode, int requestCameraCode) {
		if (activity == null)
			throw new IllegalArgumentException();

		mRequestCamera = requestCameraCode;
		mRequestGallery = requestGalleryCode;
		mActivity = activity;
	}

	public void setMode(int mode) {
		mMode = mode;

	}

	@Override
	public void onStartCameraGalleryRequest() {

		if (mMode == MODE_CAMERA) {
			openCamera();

		} else if (mMode == MODE_GALLERY) {
			openGallery();

		} else {
			AlertDialog.Builder cameraOptions = new AlertDialog.Builder(mActivity);
			cameraOptions.setMessage(mActivity.getString(R.string.choose_source));
			cameraOptions.setPositiveButton(mActivity.getString(R.string.camera),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {

							openCamera();

						}
					});
			cameraOptions.setNegativeButton(mActivity.getString(R.string.gallery),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							openGallery();
						}
					});
			cameraOptions.show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == mRequestCamera && resultCode == Activity.RESULT_OK) {

			mActivity.getContentResolver().notifyChange(mImageUri, null);
			File file = new File(mImageUri.getPath());
			onFileSelectionSucceed(file);
		}

		else if (requestCode == mRequestGallery && resultCode == Activity.RESULT_OK) {

			mImageUri = data.getData();
			// String path = getRealPathFromURI(mActivity, mImageUri);
			// File file = new File(path);
			try {
				File file;
				InputStream is = mActivity.getContentResolver().openInputStream(mImageUri);

				File dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

				if (dest.exists())
					dest.mkdirs();

				file = File.createTempFile("pic", ".jpg", dest);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				BufferedInputStream bis = new BufferedInputStream(is);

				FileOutputStream fos = new FileOutputStream(file);
				BufferedOutputStream bos = new BufferedOutputStream(fos);

				byte[] buffer = new byte[1024];
				int noOfBytes = -1;
				while ((noOfBytes = bis.read(buffer)) > -1) {
					bos.write(buffer, 0, noOfBytes);
				}
				bos.flush();

				bis.close();
				bos.close();

				baos.close();
				is.close();
				onFileSelectionSucceed(file);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			onFileSelectionFailed();
		}

	}

	@Override
	public void onStartCameraGalleryRequest(final int layoutId, final int cameraViewId, final int galleryViewId) {

		if (mMode == MODE_CAMERA) {
			openCamera();

		} else if (mMode == MODE_GALLERY) {
			openGallery();

		} else {

			DialogFragment dialog = new DialogFragment() {

				View btnCamera;
				View btnGallery;

				@Override
				public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
						@Nullable Bundle savedInstanceState) {

					View rootView = inflater.inflate(layoutId, container, false);

					btnCamera = rootView.findViewById(cameraViewId);
					btnGallery = rootView.findViewById(galleryViewId);

					if (btnCamera != null) {

						btnCamera.setOnClickListener(onClickListener);

					}
					if (btnGallery != null) {

						btnGallery.setOnClickListener(onClickListener);
					}

					return rootView;

				}

				OnClickListener onClickListener = new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						if (arg0 == btnCamera) {

							openCamera();
						} else if (arg0 == btnGallery) {

							openGallery();
						}

					}
				};

			};

			dialog.show(mActivity.getSupportFragmentManager(), "open_camera_gallery");
		}
	}

	private void openGallery() {

		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		if (mFragment == null) {
			mActivity.startActivityForResult(i, mRequestGallery);
		} else {
			mFragment.startActivityForResult(i, mRequestGallery);
		}
	}

	private void openCamera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
		mImageUri = Uri.fromFile(photo);

		if (mFragment == null)
			mActivity.startActivityForResult(intent, mRequestCamera);
		else
			mFragment.startActivityForResult(intent, mRequestCamera);
	}

	public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public void onStartCameraGalleryRequest(Fragment targetFragment) {
		mFragment = targetFragment;
		onStartCameraGalleryRequest();
	}
}

interface ICameraGalleryChooser {
	public void onStartCameraGalleryRequest();

	public void onStartCameraGalleryRequest(Fragment targetFragment);

	public void onStartCameraGalleryRequest(int layoutId, int cameraViewId, int galleryViewId);

	public void onActivityResult(int requestCode, int resultCode, Intent data);

	public void onFileSelectionSucceed(File file);

	public void onStreamSelectedSucceed(InputStream is);

	public void onFileSelectionFailed();
}
