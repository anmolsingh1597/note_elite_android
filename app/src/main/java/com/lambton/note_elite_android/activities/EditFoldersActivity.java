package com.lambton.note_elite_android.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.lambton.note_elite_android.R;
import com.lambton.note_elite_android.adapter.EditFolderRecyclerView;

import se.emilsjolander.intentbuilder.IntentBuilder;


@IntentBuilder
public class EditFoldersActivity extends AppCompatActivity{
	private static final String TAG = "EditFoldersActivity";

	@BindView(R.id.toolbar) Toolbar mToolbar;
	@BindView(R.id.recycler_view) RecyclerView mRecyclerView;
	EditFolderRecyclerView adapter;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_of_folders);
		EditFoldersActivityIntentBuilder.inject(getIntent(), this);
		ButterKnife.bind(this);
		setSupportActionBar(mToolbar);
		mToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v){
				onBackPressed();
			}
		});
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(RecyclerView.VERTICAL);
		mRecyclerView.setLayoutManager(llm);
		adapter = new EditFolderRecyclerView();
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
	}
}
