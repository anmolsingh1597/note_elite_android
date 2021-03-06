package com.lambton.note_elite_android.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import com.lambton.note_elite_android.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.ViewTreeObserver;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.lambton.note_elite_android.database.FoldersDAO;
import com.lambton.note_elite_android.delegate.FolderDelegate;
import com.lambton.note_elite_android.fragment.NoteListFragment;
import com.lambton.note_elite_android.model.Folder;

import java.util.List;

public class NotesCardViewActivity extends AppCompatActivity {
    private static final String TAG = "NotesCardViewActivity";
    private static final int ALL_NOTES_MENU_ID = -1;
    private static final int EDIT_FOLDERS_MENU_ID = -2;
    private static final int SAVE_DATABASE_MENU_ID = -3;
    private static final int IMPORT_DATABASE_MENU_ID = -4;

    public @BindView(R.id.navigation_view) NavigationView mNavigationView;
    public @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    List<Folder> latestFolders;
    FolderDelegate folderDelegate;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_card_view);
        ButterKnife.bind(this);
        mDrawerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override public void onGlobalLayout(){
                mDrawerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                setFragment(null);
            }
        });
        folderDelegate = new FolderDelegate(this);
        if (getIntent().getData() != null) folderDelegate.handleFilePickedWithIntentFilter(getIntent().getData());
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override public boolean onNavigationItemSelected(MenuItem item){
                Log.e(TAG, "onNavigationItemSelected() called with: " + "item id = [" + item.getItemId() + "]");
                int menuId = item.getItemId();
                if (menuId == ALL_NOTES_MENU_ID){
                    setFragment(null);
                }else if (menuId == EDIT_FOLDERS_MENU_ID){
                    startActivity(new EditFoldersActivityIntentBuilder().build(NotesCardViewActivity.this));
                }else if (menuId == SAVE_DATABASE_MENU_ID){
                    folderDelegate.backupDataToFile();
                }else if (menuId == IMPORT_DATABASE_MENU_ID){
                    folderDelegate.startFilePickerIntent();
                }else{
                    setFragment(FoldersDAO.getFolder(menuId));
                }
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                inflateNavigationMenus(menuId);
                return true;
            }
        });
    }

    @Override protected void onStart(){
        super.onStart();
        inflateNavigationMenus(ALL_NOTES_MENU_ID);
    }

    public void inflateNavigationMenus(int checkedItemId){
        Menu menu = mNavigationView.getMenu();
        menu.clear();
        menu
                .add(Menu.NONE, ALL_NOTES_MENU_ID, Menu.NONE, "Notes")
                .setIcon(R.drawable.ic_note_white_24dp)
                .setChecked(checkedItemId == ALL_NOTES_MENU_ID);
        final SubMenu subMenu = menu.addSubMenu("Folders");
        latestFolders = FoldersDAO.getLatestFolders();
        for (Folder folder : latestFolders){
            subMenu
                    .add(Menu.NONE, folder.getId(), Menu.NONE, folder.getName())
                    .setIcon(R.drawable.ic_folder_black_24dp)
                    .setChecked(folder.getId() == checkedItemId);
        }

    }

    @Override public void onBackPressed(){
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }else{
            super.onBackPressed();
        }
    }

    public void setFragment(Folder folder){
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = new NoteListFragment();
        if (folder != null){
            Bundle bundle = new Bundle();
            bundle.putParcelable(NoteListFragment.FOLDER, folder);
            fragment.setArguments(bundle);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FolderDelegate.PICK_RESTORE_FILE_REQUEST_CODE){
            folderDelegate.handleFilePickedWithFilePicker(resultCode, data);
        }
    }
}
