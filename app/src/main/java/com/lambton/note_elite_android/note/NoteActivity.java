package com.lambton.note_elite_android.note;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.commonsware.cwac.richedit.RichEditText;
import com.greenfrvr.hashtagview.HashtagView;
import com.lambton.note_elite_android.utils.Fileutils;
import com.raizlabs.android.dbflow.sql.language.Operator;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lambton.note_elite_android.R;
import com.lambton.note_elite_android.views.RichEditWidgetView;
import com.lambton.note_elite_android.activities.AddToFoldersActivityIntentBuilder;
import com.lambton.note_elite_android.database.FolderNoteDAO;
import com.lambton.note_elite_android.database.NotesDAO;
import com.lambton.note_elite_android.actions.NoteDeletedEvent;
import com.lambton.note_elite_android.actions.NoteEditedEvent;
import com.lambton.note_elite_android.actions.NoteFoldersUpdatedEvent;
import com.lambton.note_elite_android.model.Folder;
import com.lambton.note_elite_android.model.Note;
import com.lambton.note_elite_android.model.Note_Table;
import com.lambton.note_elite_android.utils.TimeUtils;
import com.lambton.note_elite_android.utils.Utils;
import com.lambton.note_elite_android.utils.ViewUtils;

import se.emilsjolander.intentbuilder.Extra;
import se.emilsjolander.intentbuilder.IntentBuilder;

@IntentBuilder
public class NoteActivity extends AppCompatActivity{
	private static final String TAG = "NoteActivity";

	private MaterialDialog attachmentDialog;
	private boolean isRecording = false;
	private static String fileName = null;
	private MediaRecorder recorder;

	@Extra @Nullable
	Integer noteId;
	Note note;

	@BindView(R.id.toolbar) Toolbar mToolbar;
	@BindView(R.id.title) EditText title;
	@BindView(R.id.body) RichEditText body;
	@BindView(R.id.folders_tag_view) HashtagView foldersTagView;
	@BindView(R.id.drawing_image) ImageView drawingImage;
	@BindView(R.id.create_time_text) TextView creationTimeTextView;
	@BindView(R.id.rich_edit_widget)
	RichEditWidgetView richEditWidgetView;
	private boolean shouldFireDeleteEvent = false;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);
		NoteActivityIntentBuilder.inject(getIntent(), this);
		ButterKnife.bind(this);
		setSupportActionBar(mToolbar);
		mToolbar.setNavigationIcon(ViewUtils.tintDrawable(R.drawable.ic_arrow_back_white_24dp, R.color.md_blue_grey_400));
		mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v){
				onBackPressed();
			}
		});

		if (noteId == null){
			note = new Note();
			Date now = new Date();
			note.setCreatedAt(now);
			note.save();
			noteId = note.getId();
		}

		richEditWidgetView.setRichEditView(body);

		bind();

		foldersTagView.addOnTagClickListener(new HashtagView.TagsClickListener(){
			@Override public void onItemClicked(Object item){
				Toast.makeText(NoteActivity.this, "Folder Clicked", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void bind(){
		note = NotesDAO.getNote(noteId);
		if (note.getTitle() != null){
			title.setText(note.getTitle());
		}
		if (note.getBody() != null){
			body.setText(note.getSpannedBody());
		}
		foldersTagView.setData(FolderNoteDAO.getFolders(note.getId()), new HashtagView.DataTransform<Folder>(){
			@Override public CharSequence prepare(Folder item){
				return item.getName();
			}
		});
		if (note.getDrawingTrimmed() == null)
			drawingImage.setVisibility(View.GONE);
		else{
			drawingImage.setVisibility(View.VISIBLE);
			drawingImage.setImageBitmap(Utils.getImage(note.getDrawingTrimmed().getBlob()));
		}
		creationTimeTextView.setText("Created " + TimeUtils.getHumanReadableTimeDiff(note.getCreatedAt()));
	}

	@Override protected void onStop(){
		super.onStop();
		EventBus.getDefault().unregister(this);
	}

	@Override protected void onStart(){
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.note_menu, menu);
		ViewUtils.tintMenu(menu, R.id.delete_note, R.color.md_blue_grey_400);
		ViewUtils.tintMenu(menu, R.id.attachments, R.color.md_blue_grey_400);
		return super.onCreateOptionsMenu(menu);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item){
		if (item.getItemId() == R.id.delete_note){
			SQLite.delete().from(Note.class).where(Note_Table.id.is(note.getId())).execute();
			shouldFireDeleteEvent = true;
			onBackPressed();
		}

		if (item.getItemId() == R.id.attachments){

			LayoutInflater inflater = this.getLayoutInflater();
			final View layout = inflater.inflate(R.layout.attachment_dialog, null);
			attachmentDialog = new MaterialDialog.Builder(this)
					.autoDismiss(false)
					.customView(layout, false)
					.build();
			attachmentDialog.show();


			// Camera
			android.widget.TextView cameraSelection = layout.findViewById(R.id.camera);
			cameraSelection.setOnClickListener(new AttachmentOnClickListener());

			// Audio recording
			android.widget.TextView recordingSelection = layout.findViewById(R.id.recordings);
			recordingSelection.setOnClickListener(new AttachmentOnClickListener());

			// Video recording
			android.widget.TextView videoSelection = layout.findViewById(R.id.video);
			videoSelection.setOnClickListener(new AttachmentOnClickListener());

			// Files
			android.widget.TextView filesSelection = layout.findViewById(R.id.files);
			filesSelection.setOnClickListener(new AttachmentOnClickListener());

			// Location
			android.widget.TextView locationSelection = layout.findViewById(R.id.location);
			locationSelection.setOnClickListener(new AttachmentOnClickListener());


		}
		return super.onOptionsItemSelected(item);
	}

	@OnClick({ R.id.edit_drawing_button, R.id.drawing_image }) void clickEditDrawingButton(){
		Intent intent = new DrawingActivityIntentBuilder(note.getId()).build(this);
		startActivity(intent);
	}

	@OnClick(R.id.edit_folders_button) void clickEditFoldersButton(){
		Intent intent = new AddToFoldersActivityIntentBuilder(note.getId()).build(this);
		startActivity(intent);
	}

	@Subscribe(threadMode = ThreadMode.MAIN) public void onNoteEditedEvent(NoteEditedEvent noteEditedEvent){
		Log.e(TAG, "onNoteEditedEvent() called with: " + "noteEditedEvent = [" + noteEditedEvent + "]");
		if (note.getId() == noteEditedEvent.getNote().getId()){
			bind();
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onNoteFoldersUpdatedEvent(NoteFoldersUpdatedEvent noteFoldersUpdatedEvent){
		if (note.getId() == noteFoldersUpdatedEvent.getNoteId()){
			bind();
		}
	}

	private class AttachmentOnClickListener implements View.OnClickListener {

		@Override
		public void onClick (View v) {

			switch (v.getId()) {
				// Photo from camera
				case R.id.camera:
					takePhoto();
					break;
				case R.id.recordings:
					Intent intent = new Intent(NoteActivity.this,RecordingActivity.class);
					startActivity(intent);
					break;
				case R.id.video:
					takeVideo();
					break;
				case R.id.files:
					showFileChooser();
					break;
				case R.id.location:
//					displayLocationDialog();
					break;
				default:
					break;
			}
			if (!isRecording) {
				attachmentDialog.dismiss();
			}
		}
	}

	private void takePhoto () {

//		Toast.makeText(this, "Camera click", Toast.LENGTH_SHORT).show();

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, 1);

	}

	private void takeVideo(){
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		startActivityForResult(intent, 2);
	}

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select a File to Upload"),
					3);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(this, "Please install a File Manager.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		switch (requestCode) {
			case 3:
				if (resultCode == RESULT_OK) {
					// Get the Uri of the selected file
					Uri uri = data.getData();
					Log.d(TAG, "File Uri: " + uri.toString());
					// Get the path
					String path = null;
					try {
						path = Fileutils.getPath(this, uri);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
					Log.d(TAG, "File Path: " + path);
					// Get the file instance
					// File file = new File(path);
					// Initiate the upload
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override public void onBackPressed(){
		super.onBackPressed();
		assert note != null;
		if (shouldFireDeleteEvent){
			EventBus.getDefault().postSticky(new NoteDeletedEvent(note));
		}else{
			String processedTitle = title.getText().toString().trim();
			String processedBody = body.getText().toString().trim();
			if (TextUtils.isEmpty(processedTitle) && TextUtils.isEmpty(processedBody) && note.getDrawingTrimmed() == null){
				SQLite.delete().from(Note.class).where(Note_Table.id.is(note.getId())).execute();
				return;
			}
			note.setSpannedBody(body.getText());
			note.setTitle(processedTitle);
			note.save();
			EventBus.getDefault().postSticky(new NoteEditedEvent(note.getId()));
		}
	}
}