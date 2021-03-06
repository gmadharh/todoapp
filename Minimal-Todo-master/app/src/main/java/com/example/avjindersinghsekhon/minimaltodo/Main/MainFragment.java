package com.example.avjindersinghsekhon.minimaltodo.Main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.avjindersinghsekhon.minimaltodo.About.AboutActivity;
import com.example.avjindersinghsekhon.minimaltodo.AddToDo.AddToDoActivity;
import com.example.avjindersinghsekhon.minimaltodo.AddToDo.AddToDoFragment;
import com.example.avjindersinghsekhon.minimaltodo.Analytics.AnalyticsApplication;
import com.example.avjindersinghsekhon.minimaltodo.AppDefault.AppDefaultFragment;
import com.example.avjindersinghsekhon.minimaltodo.R;
import com.example.avjindersinghsekhon.minimaltodo.Reminder.ReminderFragment;
import com.example.avjindersinghsekhon.minimaltodo.Settings.SettingsActivity;
import com.example.avjindersinghsekhon.minimaltodo.Utility.CategoryItem;
import com.example.avjindersinghsekhon.minimaltodo.Utility.ItemTouchHelperClass;
import com.example.avjindersinghsekhon.minimaltodo.Utility.RecyclerViewEmptySupport;
import com.example.avjindersinghsekhon.minimaltodo.Utility.StoreRetrieveData;
import com.example.avjindersinghsekhon.minimaltodo.Utility.TaskItem;
import com.example.avjindersinghsekhon.minimaltodo.Utility.ToDoItem;
import com.example.avjindersinghsekhon.minimaltodo.Utility.TodoNotificationService;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends AppDefaultFragment {

    private RecyclerViewEmptySupport mRecyclerView;
    private ArrayList<TaskItem> items;
    private FloatingActionButton mAddToDoItemFAB;
    private ArrayList<TaskItem> mToDoItemsArrayList;
    private CoordinatorLayout mCoordLayout;
    public static final String TODOITEM = "com.avjindersinghsekhon.com.avjindersinghsekhon.minimaltodo.MainActivity";
    private MainFragment.BasicListAdapter adapter;
    private static final int REQUEST_ID_TODO_ITEM = 100;
    private static final int REQUEST_ID_CAT_ITEM = 101;
    private static final int REQUEST_ID_VIEW_CAT = 102;
    private TaskItem mJustDeletedToDoItem;
    private int mIndexOfDeletedToDoItem;
    public static final String DATE_TIME_FORMAT_12_HOUR = "MMM d, yyyy  h:mm a";
    public static final String DATE_TIME_FORMAT_24_HOUR = "MMM d, yyyy  k:mm";
    public static final String FILENAME = "todoitems.json";
    private StoreRetrieveData storeRetrieveData;
    public ItemTouchHelper itemTouchHelper;
    private CustomRecyclerScrollViewListener customRecyclerScrollViewListener;
    public static final String SHARED_PREF_DATA_SET_CHANGED = "com.avjindersekhon.datasetchanged";
    public static final String CHANGE_OCCURED = "com.avjinder.changeoccured";
    private int mTheme = -1;
    private String theme = "name_of_the_theme";
    public static final String THEME_PREFERENCES = "com.avjindersekhon.themepref";
    public static final String RECREATE_ACTIVITY = "com.avjindersekhon.recreateactivity";
    public static final String THEME_SAVED = "com.avjindersekhon.savedtheme";

    public static final String DARKTHEME = "com.avjindersekon.darktheme";
    public static final String LIGHTTHEME = "com.avjindersekon.lighttheme";

    //New themes:
    public static final String DARKREDTHEME = "com.avjindersekon.darkredtheme";
    public static final String LIGHTREDTHEME = "com.avjindersekon.lightredtheme";

    public static final String DARKYELLOWTHEME = "com.avjindersekon.darkyellowtheme";
    public static final String LIGHTYELLOWTHEME = "com.avjindersekon.lightyellowtheme";

    public static final String DARKGREENTHEME = "com.avjindersekon.darkgreentheme";
    public static final String LIGHTGREENTHEME = "com.avjindersekon.lightgreentheme";

    public static final String DARKBLUETHEME = "com.avjindersekon.darkbluetheme";
    public static final String LIGHTBLUETHEME = "com.avjindersekon.lightbluetheme";

    public static final String DARKPINKTHEME = "com.avjindersekon.darkpinktheme";
    public static final String LIGHTPINKTHEME = "com.avjindersekon.lightpinktheme";

    public static boolean navBack; //when this is set, it lets us know to refresh the page


    private AnalyticsApplication app;
    private String[] testStrings = {"Clean my room",
            "Water the plants",
            "Get car washed",
            "Get my dry cleaning"
    };


//    New variable here
private FloatingActionButton mCategoryFAB;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        System.out.println("onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        app = (AnalyticsApplication) getActivity().getApplication();
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/Aller_Regular.tff").setFontAttrId(R.attr.fontPath).build());

        //We recover the theme we've set and setTheme accordingly
        theme = getActivity().getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getString(THEME_SAVED, LIGHTTHEME);

        if(theme.equals(MainFragment.LIGHTTHEME)) {
            getActivity().setTheme(R.style.CustomStyle_LightTheme);
        } else if (theme.equals(MainFragment.DARKTHEME)) {
            getActivity().setTheme(R.style.CustomStyle_DarkTheme);
        } else if (theme.equals(MainFragment.LIGHTREDTHEME)) {
            getActivity().setTheme(R.style.CustomStyle_RedLightTheme);
        } else if (theme.equals(MainFragment.DARKREDTHEME)) {
            getActivity().setTheme(R.style.CustomStyle_RedDarkTheme);
        } else if (theme.equals(MainFragment.LIGHTYELLOWTHEME)) {
            getActivity().setTheme(R.style.CustomStyle_YellowLightTheme);
        } else if (theme.equals(MainFragment.DARKYELLOWTHEME)) {
            getActivity().setTheme(R.style.CustomStyle_YellowDarkTheme);
        } else if (theme.equals(MainFragment.LIGHTGREENTHEME)) {
            getActivity().setTheme(R.style.CustomStyle_GreenLightTheme);
        } else if (theme.equals(MainFragment.DARKGREENTHEME)) {
            getActivity().setTheme(R.style.CustomStyle_GreenDarkTheme);
        } else if (theme.equals(MainFragment.LIGHTBLUETHEME)) {
            getActivity().setTheme(R.style.CustomStyle_BlueLightTheme);
        } else if (theme.equals(MainFragment.DARKBLUETHEME)) {
            getActivity().setTheme(R.style.CustomStyle_BlueDarkTheme);
        } else if (theme.equals(MainFragment.LIGHTPINKTHEME)) {
            getActivity().setTheme(R.style.CustomStyle_PinkLightTheme);
        } else if (theme.equals(MainFragment.DARKPINKTHEME)) {
            getActivity().setTheme(R.style.CustomStyle_PinkDarkTheme);
        }

        super.onCreate(savedInstanceState);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CHANGE_OCCURED, false);
        editor.apply();

        storeRetrieveData = new StoreRetrieveData(getContext(), FILENAME);
        mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData);
        adapter = new MainFragment.BasicListAdapter(mToDoItemsArrayList);
        setAlarms();


//        adapter.notifyDataSetChanged();
//        storeRetrieveData = new StoreRetrieveData(this, FILENAME);
//
//        try {
//            mToDoItemsArrayList = storeRetrieveData.loadFromFile();
////            Log.d("OskarSchindler", "Arraylist Length: "+mToDoItemsArrayList.size());
//        } catch (IOException | JSONException e) {
////            Log.d("OskarSchindler", "IOException received");
//            e.printStackTrace();
//        }
//
//        if(mToDoItemsArrayList==null){
//            mToDoItemsArrayList = new ArrayList<>();
//        }
//

//        mToDoItemsArrayList = new ArrayList<>();
//        makeUpItems(mToDoItemsArrayList, testStrings.length);


        mCoordLayout = (CoordinatorLayout) view.findViewById(R.id.myCoordinatorLayout);
        System.out.println("FROM MAIN : " + mCoordLayout);
        mAddToDoItemFAB = (FloatingActionButton) view.findViewById(R.id.addToDoItemFAB);

        mAddToDoItemFAB.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                app.send(this, "Action", "FAB pressed");
                Intent newTodo = new Intent(getContext(), AddToDoActivity.class);
                ToDoItem item = new ToDoItem("","","", false, null,false);
                int color = ColorGenerator.MATERIAL.getRandomColor();
                item.setTodoColor(color);
                //noinspection ResourceType
//                String color = getResources().getString(R.color.primary_ligher);
                newTodo.putExtra(TODOITEM, item);
//                View decorView = getWindow().getDecorView();
//                View navView= decorView.findViewById(android.R.id.navigationBarBackground);
//                View statusView = decorView.findViewById(android.R.id.statusBarBackground);
//                Pair<View, String> navBar ;
//                if(navView!=null){
//                    navBar = Pair.create(navView, navView.getTransitionName());
//                }
//                else{
//                    navBar = null;
//                }
//                Pair<View, String> statusBar= Pair.create(statusView, statusView.getTransitionName());
//                ActivityOptions options;
//                if(navBar!=null){
//                    options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, navBar, statusBar);
//                }
//                else{
//                    options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, statusBar);
//                }

//                startActivity(new Intent(MainActivity.this, TestLayout.class), options.toBundle());
//                startActivityForResult(newTodo, REQUEST_ID_TODO_ITEM, options.toBundle());

                startActivityForResult(newTodo, REQUEST_ID_TODO_ITEM);
            }
        });


//        Start new category button here

        mCategoryFAB = (FloatingActionButton) view.findViewById(R.id.addCategoryFAB);

        /*
            Listener for the Floating Button to create a Category
         */
        mCategoryFAB.setOnClickListener(new View.OnClickListener() {

//            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                //app.send(this, "Action", "FAB pressed");

                // create new Intent
                // Used for calling the CustomDialogActivity class
                // Also sends in the Category object to the activity
                Intent i = new Intent(getContext(),CustomDialogActivity.class);

                // Create new Category Object
                CategoryItem cItem = new CategoryItem();

                // Puts the Category object with the intent
                // This is by a key-value pair
                // The String "category" holds the object 'cItem'
                i.putExtra("category",cItem);

                // Start the activity
                startActivityForResult(i,REQUEST_ID_CAT_ITEM);

            }
        });


//        End new category button here





//        mRecyclerView = (RecyclerView)findViewById(R.id.toDoRecyclerView);
        mRecyclerView = (RecyclerViewEmptySupport) view.findViewById(R.id.toDoRecyclerView);
        if (theme.equals(LIGHTTHEME)) {
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
        } else if (theme.equals(LIGHTREDTHEME)) {
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
        } else if (theme.equals(LIGHTYELLOWTHEME)) {
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
        } else if (theme.equals(LIGHTGREENTHEME)) {
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
        } else if (theme.equals(LIGHTBLUETHEME)) {
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
        } else if (theme.equals(LIGHTPINKTHEME)) {
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
        }
        mRecyclerView.setEmptyView(view.findViewById(R.id.toDoEmptyView));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        customRecyclerScrollViewListener = new CustomRecyclerScrollViewListener() {
            @Override
            public void show() {

                mAddToDoItemFAB.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
//                mAddToDoItemFAB.animate().translationY(0).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }

            @Override
            public void hide() {

                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mAddToDoItemFAB.getLayoutParams();
                int fabMargin = lp.bottomMargin;
                mAddToDoItemFAB.animate().translationY(mAddToDoItemFAB.getHeight() + fabMargin).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }
        };
        mRecyclerView.addOnScrollListener(customRecyclerScrollViewListener);


        ItemTouchHelper.Callback callback = new ItemTouchHelperClass(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        mRecyclerView.setAdapter(adapter);
//        setUpTransitions();


    }


    public static ArrayList<TaskItem> getLocallyStoredData(StoreRetrieveData storeRetrieveData) {
        ArrayList<TaskItem> items = null;

        try {
            items = storeRetrieveData.loadFromFile();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        if (items == null) {
            items = new ArrayList<>();
        }
        return items;

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        System.out.println("onResume");
        app.send(this);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(ReminderFragment.EXIT, false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ReminderFragment.EXIT, false);
            editor.apply();
            getActivity().finish();
        }
        /*
        We need to do this, as this activity's onCreate won't be called when coming back from SettingsActivity,
        thus our changes to dark/light mode won't take place, as the setContentView() is not called again.
        So, inside our SettingsFragment, whenever the checkbox's value is changed, in our shared preferences,
        we mark our recreate_activity key as true.

        Note: the recreate_key's value is changed to false before calling recreate(), or we woudl have ended up in an infinite loop,
        as onResume() will be called on recreation, which will again call recreate() and so on....
        and get an ANR

         */
        if (getActivity().getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getBoolean(RECREATE_ACTIVITY, false)) {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).edit();
            editor.putBoolean(RECREATE_ACTIVITY, false);
            editor.apply();
            getActivity().recreate();
        }

        //navBack lets us know if the back button was pressed in category view
        if(navBack) {
            //recreate the activity to refresh the page
            getActivity().recreate();
            //make sure this doesnt loop again
            navBack = false;
        }


    }

    @Override
    public void onStart() {

        System.out.println("onStart");
        for (int i = 0;i < mToDoItemsArrayList.size();i++)
        {
            if (mToDoItemsArrayList.get(i) instanceof ToDoItem)
            {
                System.out.println("NAME: " + ((ToDoItem) mToDoItemsArrayList.get(i)).getToDoText() + " Category: " +  ((ToDoItem) mToDoItemsArrayList.get(i)).getCategoryBelongs());
            }

        }
        app = (AnalyticsApplication) getActivity().getApplication();
        super.onStart();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(CHANGE_OCCURED, false)) {

            mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData);
            adapter = new MainFragment.BasicListAdapter(mToDoItemsArrayList);
            mRecyclerView.setAdapter(adapter);
            setAlarms();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CHANGE_OCCURED, false);
//            editor.commit();
            editor.apply();


        }
    }

    private void setAlarms() {
        if (mToDoItemsArrayList != null) {
            for (TaskItem item : mToDoItemsArrayList) {
                if (item instanceof ToDoItem)
                {
                    if (((ToDoItem) item).hasReminder() && ((ToDoItem) item).getToDoDate() != null) {
                        if (((ToDoItem) item).getToDoDate().before(new Date())) {
                            ((ToDoItem) item).setToDoDate(null);
                            continue;
                        }
                        Intent i = new Intent(getContext(), TodoNotificationService.class);
                        i.putExtra(TodoNotificationService.TODOUUID, item.getIdentifier());
                        i.putExtra(TodoNotificationService.TODOTEXT, ((ToDoItem) item).getToDoText());
                        createAlarm(i, item.getIdentifier().hashCode(), ((ToDoItem) item).getToDoDate().getTime());
                    }

                }

            }
        }
    }


    public void addThemeToSharedPreferences(String theme) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(THEME_SAVED, theme);
        editor.apply();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutMeMenuItem:
                Intent i = new Intent(getContext(), AboutActivity.class);
                startActivity(i);
                return true;
//            case R.id.switch_themes:
//                if(mTheme == R.style.CustomStyle_DarkTheme){
//                    addThemeToSharedPreferences(LIGHTTHEME);
//                }
//                else{
//                    addThemeToSharedPreferences(DARKTHEME);
//                }
//
////                if(mTheme == R.style.CustomStyle_DarkTheme){
////                    mTheme = R.style.CustomStyle_LightTheme;
////                }
////                else{
////                    mTheme = R.style.CustomStyle_DarkTheme;
////                }
//                this.recreate();
//                return true;
            case R.id.preferences:
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
        This method is called after the Activity for creating a ToDoItem and Category is finished
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_CANCELED && requestCode == REQUEST_ID_TODO_ITEM) {

            // Retrieve new TaskItem from the Intent
            TaskItem item = (TaskItem) data.getSerializableExtra(TODOITEM);

            // If the TaskItem is a ToDoItem
            if (item instanceof ToDoItem)
            {
                if (((ToDoItem) item).getToDoText().length() <= 0) {
                    return;
                }

                boolean existed = false;

                if (((ToDoItem) item).hasReminder() && ((ToDoItem) item).getToDoDate() != null) {
                    Intent i = new Intent(getContext(), TodoNotificationService.class);
                    i.putExtra(TodoNotificationService.TODOTEXT, ((ToDoItem) item).getToDoText());
                    i.putExtra(TodoNotificationService.TODOUUID, item.getIdentifier());
                    createAlarm(i, item.getIdentifier().hashCode(), ((ToDoItem) item).getToDoDate().getTime());
//                Log.d("OskarSchindler", "Alarm Created: "+item.getToDoText()+" at "+item.getToDoDate());
                }

                for (int i = 0; i < mToDoItemsArrayList.size(); i++) {
                    if (item.getIdentifier().equals(mToDoItemsArrayList.get(i).getIdentifier())) {
                        mToDoItemsArrayList.set(i, item);
                        existed = true;
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
                if (!existed) {
                    addToDataStore(item);
                }

            }

        }

        // else it's a category
        else if (requestCode == REQUEST_ID_CAT_ITEM){

            TaskItem item = (TaskItem) data.getSerializableExtra("category");

            if (((CategoryItem) item).getTitle().length() <= 0)
            {
                return;
            }
            boolean existed = false;

            for (int i = 0; i < mToDoItemsArrayList.size(); i++) {
                if (item.getIdentifier().equals(mToDoItemsArrayList.get(i).getIdentifier())) {
                    mToDoItemsArrayList.set(i, item);
                    existed = true;
                    adapter.notifyDataSetChanged();
                    break;
                }
            }

            if (!existed){
                addToDataStore(item);
            }

        }

        // Else the user clicked a category
        // Get the updated arraylist back from the Category to see if there were any changes to the tasks
        else if(requestCode == REQUEST_ID_VIEW_CAT){

            // set the main arraylist to the updated one
            mToDoItemsArrayList = (ArrayList<TaskItem>) data.getSerializableExtra("newArray");

            // update the one in the recycler view as well
            items = mToDoItemsArrayList;

            // notify there was a change so we can refresh
            adapter.notifyDataSetChanged();


        }
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
    }

    private boolean doesPendingIntentExist(Intent i, int requestCode) {
        PendingIntent pi = PendingIntent.getService(getContext(), requestCode, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    public void createAlarm(Intent i, int requestCode, long timeInMillis) {
        AlarmManager am = getAlarmManager();
        PendingIntent pi = PendingIntent.getService(getContext(), requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi);
//        Log.d("OskarSchindler", "createAlarm "+requestCode+" time: "+timeInMillis+" PI "+pi.toString());
    }

    public void deleteAlarm(Intent i, int requestCode) {
        if (doesPendingIntentExist(i, requestCode)) {
            PendingIntent pi = PendingIntent.getService(getContext(), requestCode, i, PendingIntent.FLAG_NO_CREATE);
            pi.cancel();
            getAlarmManager().cancel(pi);
            Log.d("OskarSchindler", "PI Cancelled " + doesPendingIntentExist(i, requestCode));
        }
    }

    private void addToDataStore(TaskItem item) {
        mToDoItemsArrayList.add(item);
        adapter.notifyItemInserted(mToDoItemsArrayList.size() - 1);

    }


    public void makeUpItems(ArrayList<ToDoItem> items, int len) {
        for (String testString : testStrings) {
            ToDoItem item = new ToDoItem(testString,testString,testString, false, new Date(),false);
            //noinspection ResourceType
//            item.setTodoColor(getResources().getString(R.color.red_secondary));
            items.add(item);
        }

    }

    public class BasicListAdapter extends RecyclerView.Adapter<BasicListAdapter.ViewHolder> implements ItemTouchHelperClass.ItemTouchHelperAdapter {


        @Override
        public void onItemMoved(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(items, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(items, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemRemoved(final int position) {
            //Remove this line if not using Google Analytics
            app.send(this, "Action", "Swiped Todo Away");

            mJustDeletedToDoItem = items.remove(position);
            mIndexOfDeletedToDoItem = position;
            Intent i = new Intent(getContext(), TodoNotificationService.class);
            deleteAlarm(i, mJustDeletedToDoItem.getIdentifier().hashCode());
            notifyItemRemoved(position);

//            String toShow = (mJustDeletedToDoItem.getToDoText().length()>20)?mJustDeletedToDoItem.getToDoText().substring(0, 20)+"...":mJustDeletedToDoItem.getToDoText();
            String toShow = "Todo";
            Snackbar.make(mCoordLayout, "Deleted " + toShow, Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //Comment the line below if not using Google Analytics
                            app.send(this, "Action", "UNDO Pressed");

                            if (((ToDoItem) mJustDeletedToDoItem).getToDoDate() != null && ((ToDoItem) mJustDeletedToDoItem).hasReminder()) {
                                Intent i = new Intent(getContext(), TodoNotificationService.class);
                                i.putExtra(TodoNotificationService.TODOTEXT, ((ToDoItem) mJustDeletedToDoItem).getToDoText());
                                i.putExtra(TodoNotificationService.TODOUUID, mJustDeletedToDoItem.getIdentifier());
                                createAlarm(i, mJustDeletedToDoItem.getIdentifier().hashCode(), ((ToDoItem) mJustDeletedToDoItem).getToDoDate().getTime());
                            }
                            //Refreshes the list
                            items.add(mIndexOfDeletedToDoItem, mJustDeletedToDoItem);
                            notifyItemInserted(mIndexOfDeletedToDoItem);
                        }
                    }).show();

            //User Story #20, Task #75 - Ask user for confirmation to delete the whole category and its tasks
            //Check if its a category
            if (mJustDeletedToDoItem instanceof CategoryItem) {

                //If it is a category then create a pop up
                final String catName = ((CategoryItem) mJustDeletedToDoItem).getTitle();
                System.out.println(catName);

                for (TaskItem it : items) {
                    if(it instanceof ToDoItem){
                       if (((ToDoItem) it).getCategoryBelongs().equals(catName)) {

                        //workaround instead of deleting from the items list
                        ((ToDoItem) it).setCategoryBelongs("Null");
                    }
                    }


                }


                Snackbar.make(mCoordLayout, "Are you sure you want to delete \"" + catName + "\" and all of its tasks? ", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            // make undo work
                            public void onClick(View v) {
                                items.add(mIndexOfDeletedToDoItem, mJustDeletedToDoItem);
                                notifyItemInserted(mIndexOfDeletedToDoItem);
                                for (TaskItem it : items) {
                                    if(it instanceof ToDoItem){
                                        if (((ToDoItem) it).getCategoryBelongs().equals("Null")) {
                                            ((ToDoItem) it).setCategoryBelongs(catName);
                                        }
                                    }


                                }
                            }


                        }).show();
            }


        }

        @Override
        public BasicListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_circle_try, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final BasicListAdapter.ViewHolder holder, final int position) {
            final TaskItem item = items.get(position);

            System.out.println("onBindViewHolder");

//            if(item.getToDoDate()!=null && item.getToDoDate().before(new Date())){
//                item.setToDoDate(null);
//            }
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE);
            //Background color for each to-do item. Necessary for night/day mode
            int bgColor;
            //color of title text in our to-do item. White for night mode, dark gray for day mode
            int todoTextColor;
            if (sharedPreferences.getString(THEME_SAVED, LIGHTTHEME).equals(LIGHTTHEME)) {
                bgColor = Color.WHITE;
                todoTextColor = getResources().getColor(R.color.secondary_text);
            } else if (sharedPreferences.getString(THEME_SAVED, LIGHTTHEME).equals(LIGHTREDTHEME)) {
                bgColor = Color.WHITE;
                todoTextColor = getResources().getColor(R.color.secondary_text);
            } else if (sharedPreferences.getString(THEME_SAVED, LIGHTTHEME).equals(LIGHTYELLOWTHEME)) {
                bgColor = Color.WHITE;
                todoTextColor = getResources().getColor(R.color.secondary_text);
            } else if (sharedPreferences.getString(THEME_SAVED, LIGHTTHEME).equals(LIGHTGREENTHEME)) {
                bgColor = Color.WHITE;
                todoTextColor = getResources().getColor(R.color.secondary_text);
            } else if (sharedPreferences.getString(THEME_SAVED, LIGHTTHEME).equals(LIGHTBLUETHEME)) {
                bgColor = Color.WHITE;
                todoTextColor = getResources().getColor(R.color.secondary_text);
            } else if (sharedPreferences.getString(THEME_SAVED, LIGHTTHEME).equals(LIGHTPINKTHEME)) {
                bgColor = Color.WHITE;
                todoTextColor = getResources().getColor(R.color.secondary_text);
            } else {
                bgColor = getResources().getColor(R.color.darkModeTasks);
                todoTextColor = Color.WHITE;
            }
            holder.linearLayout.setBackgroundColor(bgColor);

            /**
             * If the todoitem belongs to the category "None" (which is just no category, the main page), then display it
             */
            if(item instanceof ToDoItem && ((ToDoItem) item).getCategoryBelongs().equalsIgnoreCase("None"))
            {
                System.out.println("onBindViewHolder: " + ((ToDoItem) item).getToDoText());

                holder.linearLayout.setVisibility(View.VISIBLE);

                if (((ToDoItem) item).hasReminder() && ((ToDoItem) item).getToDoDate() != null) {
                    holder.mToDoTextview.setMaxLines(1);
                    holder.mTimeTextView.setVisibility(View.VISIBLE);
//                holder.mToDoTextview.setVisibility(View.GONE);
                } else {
                    holder.mTimeTextView.setVisibility(View.GONE);
                    holder.mToDoTextview.setMaxLines(2);
                }

                if(((ToDoItem) item).isPriority()) {
                    holder.mToDoTextview.setText(((ToDoItem) item).getToDoText() + new String(Character.toChars(0x2757)));


                }
                else {
                    holder.mToDoTextview.setText(((ToDoItem) item).getToDoText());
                }
                holder.mToDoTextview.setTextColor(todoTextColor);
//              holder.mColorTextView.setBackgroundColor(Color.parseColor(item.getTodoColor()));

//              TextDrawable myDrawable = TextDrawable.builder().buildRoundRect(item.getToDoText().substring(0,1),Color.RED, 10);
                //We check if holder.color is set or not
//              if(item.getTodoColor() == null){
//                  ColorGenerator generator = ColorGenerator.MATERIAL;
//                  int color = generator.getRandomColor();
//                  item.setTodoColor(color+"");
//              }
//              Log.d("OskarSchindler", "Color: "+item.getTodoColor());
                TextDrawable myDrawable = TextDrawable.builder().beginConfig()
                        .textColor(Color.WHITE)
                        .useFont(Typeface.DEFAULT)
                        .toUpperCase()
                        .endConfig()
                        .buildRound(((ToDoItem) item).getToDoText().substring(0, 1), ((ToDoItem) item).getTodoColor());

//              TextDrawable myDrawable = TextDrawable.builder().buildRound(item.getToDoText().substring(0,1),holder.color);
                holder.mColorImageView.setImageDrawable(myDrawable);
                if (((ToDoItem) item).getToDoDate() != null) {
                    String timeToShow;
                    if (android.text.format.DateFormat.is24HourFormat(getContext())) {
                        timeToShow = AddToDoFragment.formatDate(MainFragment.DATE_TIME_FORMAT_24_HOUR, ((ToDoItem) item).getToDoDate());
                    } else {
                        timeToShow = AddToDoFragment.formatDate(MainFragment.DATE_TIME_FORMAT_12_HOUR, ((ToDoItem) item).getToDoDate());
                    }
                    holder.mTimeTextView.setText(timeToShow);
                }


            }
            else if (item instanceof CategoryItem){
                //set category title once CategoryItem is created
                holder.mToDoTextview.setText(((CategoryItem) item).getTitle());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    //When category is click show test
                    public void onClick(View view) {
                        //Toast.makeText(getContext(),"test",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getContext(),CategoryView.class);
                        i.putExtra("tasks",mToDoItemsArrayList);
                        i.putExtra("categoryClicked", item);

                        startActivityForResult(i,REQUEST_ID_VIEW_CAT);
                    }
                });
                
            }

            /**
             * Else don't display the task, if it doesn't belong to the main page
             */
            else{
                holder.linearLayout.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            }
        }



        @Override
        public int getItemCount() {
            return items.size();
        }

        BasicListAdapter(ArrayList<TaskItem> itemss) {

            items = itemss;
        }


        @SuppressWarnings("deprecation")
        public class ViewHolder extends RecyclerView.ViewHolder {
            //create image of folder icon

            View mView;
            LinearLayout linearLayout;
            TextView mToDoTextview;
            //            TextView mColorTextView;
            ImageView mColorImageView;
            TextView mTimeTextView;
//            int color = -1;

            //Brings up already created task form
            public ViewHolder(View v) {
                super(v);
                mView = v;
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TaskItem item = items.get(ViewHolder.this.getAdapterPosition());

                        if(item instanceof ToDoItem){
                            Intent i = new Intent(getContext(), AddToDoActivity.class);
                            i.putExtra(TODOITEM, item);
                            startActivityForResult(i, REQUEST_ID_TODO_ITEM);
                        }
                        /*
                        else if(item instanceof CategoryItem){
                            Intent i = new Intent(getContext(),CustomDialogActivity.class);
                            i.putExtra("category",item);
                            startActivityForResult(i,REQUEST_ID_CAT_ITEM);
                        }
                         */
                    }
                });
                mToDoTextview = (TextView) v.findViewById(R.id.toDoListItemTextview);
                mTimeTextView = (TextView) v.findViewById(R.id.todoListItemTimeTextView);
//                mColorTextView = (TextView)v.findViewById(R.id.toDoColorTextView);
                mColorImageView = (ImageView) v.findViewById(R.id.toDoListItemColorImageView);

                linearLayout = (LinearLayout) v.findViewById(R.id.listItemLinearLayout);


            }


        }
    }

    //Used when using custom fonts
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

    private void saveDate() {
        try {
            storeRetrieveData.saveToFile(mToDoItemsArrayList);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause");
        try {
            storeRetrieveData.saveToFile(mToDoItemsArrayList);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        System.out.println("onDestroy");
        super.onDestroy();
        mRecyclerView.removeOnScrollListener(customRecyclerScrollViewListener);
    }


    //    public void setUpTransitions(){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            Transition enterT = new Slide(Gravity.RIGHT);
//            enterT.setDuration(500);
//
//            Transition exitT = new Slide(Gravity.LEFT);
//            exitT.setDuration(300);
//
//            Fade fade = new Fade();
//            fade.setDuration(500);
//
//            getWindow().setExitTransition(fade);
//            getWindow().setReenterTransition(fade);
//
//        }
//    }
    @Override
    protected int layoutRes() {
        return R.layout.fragment_main;
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }
}
