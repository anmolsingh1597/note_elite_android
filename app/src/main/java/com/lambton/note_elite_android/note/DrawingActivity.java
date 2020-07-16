package com.lambton.note_elite_android.note;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.github.gcacace.signaturepad.views.SignaturePad;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.lambton.note_elite_android.App;
import com.lambton.note_elite_android.R;
import com.lambton.note_elite_android.database.NotesDAO;
import com.lambton.note_elite_android.tasks.SaveDrawingTask;
import com.lambton.note_elite_android.model.Note;
import com.lambton.note_elite_android.utils.Utils;
import com.lambton.note_elite_android.utils.ViewUtils;
import se.emilsjolander.intentbuilder.Extra;
import se.emilsjolander.intentbuilder.IntentBuilder;
@IntentBuilder
public class DrawingActivity extends AppCompatActivity {
	private static final String TAG = "DrawingActivity";
	private static final int CAMERA_REQUEST = 1888;
	public static final int FILES_REQUEST = 1999;
	private static final int MY_CAMERA_PERMISSION_CODE = 100;
	public static final int MY_FILES_PERMISSION_CODE = 200;
	@Extra
	Integer noteId;
	Note note;
	private boolean hasDrawnSomething = false;
	@BindView(R.id.drawing_pad)
	SignaturePad drawingPad;
	@BindView(R.id.toolbar)
	Toolbar mToolbar;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawing);
		DrawingActivityIntentBuilder.inject(getIntent(), this);
		ButterKnife.bind(this);
		setSupportActionBar(mToolbar);
		mToolbar.setNavigationIcon(ViewUtils.tintDrawable(R.drawable.ic_arrow_back_white_24dp, R.color.md_blue_grey_400));
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		note = NotesDAO.getNote(noteId);
		Log.e(TAG, "onCreate: noteId= " + noteId + ", note= " + note);
		drawingPad.setOnSignedListener(new SignaturePad.OnSignedListener() {
			@Override
			public void onStartSigning() {
			}

			@Override
			public void onSigned() {
				hasDrawnSomething = true;
			}

			@Override
			public void onClear() {
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (hasDrawnSomething)
			App.JOB_MANAGER.addJobInBackground(new SaveDrawingTask(drawingPad, note.getId()));
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			drawingPad.setSignatureBitmap(Utils.getImage(note.getDrawing().getBlob()));
		} catch (NullPointerException e) {
			Log.i(TAG, "Empty Drawing onStart: ", e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.drawing_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.btn_camera_menu) {
			openCamera();
		} else if (item.getItemId() == R.id.btn_file_menu) {
			pickImage();
		}
		return super.onOptionsItemSelected(item);
	}

	private void pickImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), FILES_REQUEST);
	}

	private void openCamera() {
		if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
		} else {
			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(cameraIntent, CAMERA_REQUEST);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == MY_CAMERA_PERMISSION_CODE) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_REQUEST);
			} else {
				Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
			Bitmap photo = (Bitmap) data.getExtras().get("data");
			drawingPad.setSignatureBitmap(photo);
		}
		if (requestCode == FILES_REQUEST && resultCode == Activity.RESULT_OK) {
			Uri uri = data.getData();
//			uriToBitmap(uri);
		}
	}
}