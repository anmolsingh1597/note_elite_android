package com.lambton.note_elite_android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.lambton.note_elite_android.R;
import com.lambton.note_elite_android.activities.NotesCardViewActivity;
import com.lambton.note_elite_android.note.NoteActivityIntentBuilder;
import com.lambton.note_elite_android.adapter.HomeAdapter;
import com.lambton.note_elite_android.model.Folder;


public class NoteListFragment extends Fragment{
	public static final String FOLDER = "FOLDER";

	@BindView(R.id.toolbar) Toolbar mToolbar;
	@BindView(R.id.recycler_view) RecyclerView mRecyclerView;
	@BindView(R.id.new_note) FloatingActionButton mNewNoteFAB;
	@BindView(R.id.zero_notes_view) View zeroNotesView;
	HomeAdapter homeAdapter;
	Folder folder;

	@Nullable @Override public View onCreateView(
			LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState
	){
		View view = inflater.inflate(R.layout.fragment_note_list, container, false);
		ButterKnife.bind(this, view);
		folder = getArguments() == null ? null : (Folder) getArguments().getParcelable(NoteListFragment.FOLDER);
		return view;
	}

	@Override public void onActivityCreated(@Nullable Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		if (folder != null) mToolbar.setTitle(folder.getName());
		mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v){
				((NotesCardViewActivity) getActivity()).mDrawerLayout.openDrawer(Gravity.LEFT);
			}
		});

		//Search
		//------------------------------------------------------//
		mToolbar.inflateMenu(R.menu.note_card_view_menu);
		MenuItem searchItem = mToolbar.getMenu().findItem(R.id.btnSearch);
		SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				homeAdapter.getFilter().filter(newText);
				return false;
			}
		});

		//Sort
		//------------------------------------------------------//
		MenuItem sortItem = mToolbar.getMenu().findItem(R.id.btnSort);
		sortItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Toast.makeText(((NotesCardViewActivity) getActivity()), "Sort", Toast.LENGTH_SHORT).show();
//				homeAdapter.sortByLetters();
				return false;
			}
		});
		//------------------------------------------------------//
		StaggeredGridLayoutManager slm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
		slm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
		mRecyclerView.setLayoutManager(slm);
		homeAdapter = new HomeAdapter(zeroNotesView, folder);
		mRecyclerView.setAdapter(homeAdapter);
		homeAdapter.loadFromDatabase();
	}

	@OnClick(R.id.new_note) void clickNewNoteButton(){
		Intent intent = new NoteActivityIntentBuilder().build(getContext());
		this.startActivity(intent);
	}

	@Override public void onStart(){
		super.onStart();
		homeAdapter.registerEventBus();
	}

	@Override public void onStop(){
		super.onStop();
		homeAdapter.unregisterEventBus();
	}

/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.note_card_view_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.btnSearch);
		SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
                homeAdapter.getFilter().filter(newText);
				return false;
			}
		});
		return true;
	}*/
}
