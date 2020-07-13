package com.lambton.note_elite_android.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.lambton.note_elite_android.R;
import com.lambton.note_elite_android.actions.NoteFoldersUpdatedEvent;
import com.lambton.note_elite_android.adapter.AddtoFolderAdapter;

import se.emilsjolander.intentbuilder.Extra;
import se.emilsjolander.intentbuilder.IntentBuilder;

@IntentBuilder
public class AddToFoldersActivity extends AppCompatActivity{
	private static final String TAG = "AddToFoldersActivity";

	@BindView(R.id.toolbar) Toolbar mToolbar;
	@BindView(R.id.recycler_view) RecyclerView mRecyclerView;
	AddtoFolderAdapter adapter;
	@Extra Integer noteId;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_of_folders);
		AddToFoldersActivityIntentBuilder.inject(getIntent(), this);
		ButterKnife.bind(this);
		setSupportActionBar(mToolbar);
		mToolbar.setTitle("Add to folders");
		mToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v){
				onBackPressed();
			}
		});
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(RecyclerView.VERTICAL);
		mRecyclerView.setLayoutManager(llm);
		adapter = new AddtoFolderAdapter(noteId);
		mRecyclerView.setAdapter(adapter);
		adapter.loadFromDatabase();
	}

	@Override protected void onStart(){
		super.onStart();
		adapter.registerEventBus();
	}

	@Override protected void onStop(){
		super.onStop();
		adapter.unregisterEventBus();
		EventBus.getDefault().post(new NoteFoldersUpdatedEvent(noteId));
	}
}
