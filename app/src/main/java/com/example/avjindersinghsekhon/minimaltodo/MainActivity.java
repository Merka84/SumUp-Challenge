package com.example.avjindersinghsekhon.minimaltodo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sumup.merchant.api.SumUpAPI;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private RecyclerViewEmptySupport mRecyclerView;
    private FloatingActionButton mMainFAB;
    private FloatingActionButton todoFAB;
    private FloatingActionButton paymentFAB;
    private LinearLayout mainFABLayout;
    private LinearLayout todoFABLayout;
    private LinearLayout paymentFABLayout;

    private ArrayList<ToDoItem> mToDoItemsArrayList;
    private CoordinatorLayout mCoordLayout;
    private FrameLayout fabsLayout;
    public static final String TODOITEM = "com.example.avjindersinghsekhon.minimaltodo.MainActivity";
    private BasicListAdapter adapter;
    private static final int REQUEST_ID_TODO_ITEM = 100;
    private ToDoItem mJustDeletedToDoItem;
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
    private AnalyticsApplication app;
    private String[] testStrings = {"Clean my room",
            "Water the plants",
            "Get car washed",
            "Get my dry cleaning"
    };
    private boolean fabExpanded = false;


    public static ArrayList<ToDoItem> getLocallyStoredData(StoreRetrieveData storeRetrieveData){
        ArrayList<ToDoItem> items = null;

        try {
            items  = storeRetrieveData.loadFromFile();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        if(items == null){
            items = new ArrayList<>();
        }
        return items;

    }

    @Override
    protected void onResume() {
        super.onResume();
        app.send(this);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        if(sharedPreferences.getBoolean(ReminderActivity.EXIT, false)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ReminderActivity.EXIT,false);
            editor.apply();
            finish();
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
        if(getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getBoolean(RECREATE_ACTIVITY, false)){
            SharedPreferences.Editor editor = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).edit();
            editor.putBoolean(RECREATE_ACTIVITY, false);
            editor.apply();
            recreate();
        }

    }

    @Override
    protected void onStart() {
        app = (AnalyticsApplication)getApplication();
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        if(sharedPreferences.getBoolean(CHANGE_OCCURED, false)){

            mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData);
            adapter = new BasicListAdapter(mToDoItemsArrayList);
            mRecyclerView.setAdapter(adapter);
            setAlarms();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CHANGE_OCCURED, false);
            editor.apply();
        }
    }

    private void setAlarms(){
        if(mToDoItemsArrayList!=null){
            for(ToDoItem item : mToDoItemsArrayList){
                if(item.hasReminder() && item.getToDoDate()!=null){
                    if(item.getToDoDate().before(new Date())){
                        item.setToDoDate(null);
                        continue;
                    }
                    Intent i = new Intent(this, TodoNotificationService.class);
                    i.putExtra(TodoNotificationService.TODOUUID, item.getIdentifier());
                    i.putExtra(TodoNotificationService.TODOTEXT, item.getToDoText());
                    createAlarm(i, item.getIdentifier().hashCode(), item.getToDoDate().getTime());
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        setupTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        initSharedPrefStorage();
        setupViews();
        addListeners();
        setupData();
        setAlarms();
    }

    private void initSharedPrefStorage() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CHANGE_OCCURED, false);
        editor.apply();
    }

    private void setupViews() {
        mCoordLayout = findViewById(R.id.myCoordinatorLayout);
        fabsLayout = findViewById(R.id.fabs_layout);
        mMainFAB= fabsLayout.findViewById(R.id.fab_main);
        todoFAB = fabsLayout.findViewById(R.id.fab_todo);
        paymentFAB = fabsLayout.findViewById(R.id.fab_payment);
        mainFABLayout = fabsLayout.findViewById(R.id.main_fab_layout);
        todoFABLayout = fabsLayout.findViewById(R.id.todo_fab_layout);
        paymentFABLayout = fabsLayout.findViewById(R.id.payment_fab_layout);

        //mMainFAB = findViewById(R.id.addToDoItemFAB);

        mRecyclerView = findViewById(R.id.toDoRecyclerView);
        if(theme.equals(LIGHTTHEME)){
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
        }
        mRecyclerView.setEmptyView(findViewById(R.id.toDoEmptyView));
        mRecyclerView.setHasFixedSize(true);

        ItemTouchHelper.Callback callback = new ItemTouchHelperClass(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void setupData() {
        storeRetrieveData = new StoreRetrieveData(this, FILENAME);
        mToDoItemsArrayList =  getLocallyStoredData(storeRetrieveData);
        adapter = new BasicListAdapter(mToDoItemsArrayList);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(customRecyclerScrollViewListener);
        mRecyclerView.setAdapter(adapter);
    }

    private void addListeners(){
        mMainFAB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (fabExpanded == true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });

        todoFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.send(this, "Action", "FAB pressed");
                Intent newTodo = new Intent(MainActivity.this, AddToDoActivity.class);
                ToDoItem item = new ToDoItem("", false, null);
                int color = ColorGenerator.MATERIAL.getRandomColor();
                item.setTodoColor(color);
                //noinspection ResourceType
                newTodo.putExtra(TODOITEM, item);
                closeSubMenusFab();
                startActivityForResult(newTodo, REQUEST_ID_TODO_ITEM);
            }
        });

        paymentFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSubMenusFab();
                startActivity(new Intent(MainActivity.this, PaymentActivity.class));
            }
        });

        customRecyclerScrollViewListener = new CustomRecyclerScrollViewListener() {
            @Override
            public void show() {
                fabsLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void hide() {

                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fabsLayout.getLayoutParams();
                int fabMargin = lp.bottomMargin;
                fabsLayout.animate().translationY(fabsLayout.getHeight()+fabMargin).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }
        };
    }

    private void setupTheme() {
        app = (AnalyticsApplication)getApplication();

        //We recover the theme we've set and setTheme accordingly
        theme = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getString(THEME_SAVED, LIGHTTHEME);

        if(theme != null && theme.equals(LIGHTTHEME)){
            mTheme = R.style.CustomStyle_LightTheme;
        }
        else{
            mTheme = R.style.CustomStyle_DarkTheme;
        }
        this.setTheme(mTheme);
    }

    public void addThemeToSharedPreferences(String theme){
        SharedPreferences sharedPreferences = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(THEME_SAVED, theme);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.aboutMeMenuItem:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                return true;
//
            case R.id.preferences:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= RESULT_CANCELED && requestCode == REQUEST_ID_TODO_ITEM){
            ToDoItem item =(ToDoItem) data.getSerializableExtra(TODOITEM);
            if(item.getToDoText().length()<=0){
                return;
            }
            boolean existed = false;

            if(item.hasReminder() && item.getToDoDate()!=null){
                Intent i = new Intent(this, TodoNotificationService.class);
                i.putExtra(TodoNotificationService.TODOTEXT, item.getToDoText());
                i.putExtra(TodoNotificationService.TODOUUID, item.getIdentifier());
                createAlarm(i, item.getIdentifier().hashCode(), item.getToDoDate().getTime());
            }

            for(int i = 0; i<mToDoItemsArrayList.size();i++){
                if(item.getIdentifier().equals(mToDoItemsArrayList.get(i).getIdentifier())){
                    mToDoItemsArrayList.set(i, item);
                    existed = true;
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
            if(!existed) {
                addToDataStore(item);
            }


        }

    }

    private AlarmManager getAlarmManager(){
        return (AlarmManager)getSystemService(ALARM_SERVICE);
    }

    private boolean doesPendingIntentExist(Intent i, int requestCode){
        PendingIntent pi = PendingIntent.getService(this,requestCode, i, PendingIntent.FLAG_NO_CREATE);
        return pi!=null;
    }

    private void createAlarm(Intent i, int requestCode, long timeInMillis){
        AlarmManager am = getAlarmManager();
        PendingIntent pi = PendingIntent.getService(this,requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi);
//        Log.d("OskarSchindler", "createAlarm "+requestCode+" time: "+timeInMillis+" PI "+pi.toString());
    }
    private void deleteAlarm(Intent i, int requestCode){
        if(doesPendingIntentExist(i, requestCode)){
            PendingIntent pi = PendingIntent.getService(this, requestCode,i, PendingIntent.FLAG_NO_CREATE);
            pi.cancel();
            getAlarmManager().cancel(pi);
            Log.d("OskarSchindler", "PI Cancelled " + doesPendingIntentExist(i, requestCode));
        }
    }

    private void addToDataStore(ToDoItem item){
        mToDoItemsArrayList.add(item);
        adapter.notifyItemInserted(mToDoItemsArrayList.size() - 1);
    }


    public void makeUpItems(ArrayList<ToDoItem> items, int len){
        for (String testString : testStrings) {
            ToDoItem item = new ToDoItem(testString, false, new Date());
            //noinspection ResourceType
//            item.setTodoColor(getResources().getString(R.color.red_secondary));
            items.add(item);
        }

    }

    public class BasicListAdapter extends RecyclerView.Adapter<BasicListAdapter.ViewHolder> implements ItemTouchHelperClass.ItemTouchHelperAdapter{
        private ArrayList<ToDoItem> items;

        @Override
        public void onItemMoved(int fromPosition, int toPosition) {
           if(fromPosition<toPosition){
               for(int i=fromPosition; i<toPosition; i++){
                   Collections.swap(items, i, i+1);
               }
           }
            else{
               for(int i=fromPosition; i > toPosition; i--){
                   Collections.swap(items, i, i-1);
               }
           }
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemRemoved(final int position) {
            //Remove this line if not using Google Analytics
            app.send(this, "Action", "Swiped Todo Away");

            mJustDeletedToDoItem =  items.remove(position);
            mIndexOfDeletedToDoItem = position;
            Intent i = new Intent(MainActivity.this,TodoNotificationService.class);
            deleteAlarm(i, mJustDeletedToDoItem.getIdentifier().hashCode());
            notifyItemRemoved(position);

//            String toShow = (mJustDeletedToDoItem.getToDoText().length()>20)?mJustDeletedToDoItem.getToDoText().substring(0, 20)+"...":mJustDeletedToDoItem.getToDoText();
            String toShow = "Todo";
            Snackbar.make(mCoordLayout, "Deleted "+toShow,Snackbar.LENGTH_SHORT)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //Comment the line below if not using Google Analytics
                            app.send(this, "Action", "UNDO Pressed");
                            items.add(mIndexOfDeletedToDoItem, mJustDeletedToDoItem);
                            if(mJustDeletedToDoItem.getToDoDate()!=null && mJustDeletedToDoItem.hasReminder()){
                                Intent i = new Intent(MainActivity.this, TodoNotificationService.class);
                                i.putExtra(TodoNotificationService.TODOTEXT, mJustDeletedToDoItem.getToDoText());
                                i.putExtra(TodoNotificationService.TODOUUID, mJustDeletedToDoItem.getIdentifier());
                                createAlarm(i, mJustDeletedToDoItem.getIdentifier().hashCode(), mJustDeletedToDoItem.getToDoDate().getTime());
                            }
                            notifyItemInserted(mIndexOfDeletedToDoItem);
                        }
                    }).show();
        }

        @Override
        public BasicListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_circle_try, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final BasicListAdapter.ViewHolder holder, final int position) {
            ToDoItem item = items.get(position);
//            if(item.getToDoDate()!=null && item.getToDoDate().before(new Date())){
//                item.setToDoDate(null);
//            }
            SharedPreferences sharedPreferences = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE);
            //Background color for each to-do item. Necessary for night/day mode
            int bgColor;
            //color of title text in our to-do item. White for night mode, dark gray for day mode
            int todoTextColor;
            if(sharedPreferences.getString(THEME_SAVED, LIGHTTHEME).equals(LIGHTTHEME)){
                bgColor = Color.WHITE;
                todoTextColor = getResources().getColor(R.color.secondary_text);
            }
            else{
                bgColor = Color.DKGRAY;
                todoTextColor = Color.WHITE;
            }
            holder.linearLayout.setBackgroundColor(bgColor);

            if(item.hasReminder() && item.getToDoDate()!=null){
                holder.mToDoTextview.setMaxLines(1);
                holder.mTimeTextView.setVisibility(View.VISIBLE);
//                holder.mToDoTextview.setVisibility(View.GONE);
            }
            else{
                holder.mTimeTextView.setVisibility(View.GONE);
                holder.mToDoTextview.setMaxLines(2);
            }
            holder.mToDoTextview.setText(item.getToDoText());
            holder.mToDoTextview.setTextColor(todoTextColor);

            TextDrawable myDrawable = TextDrawable.builder().beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .toUpperCase()
                    .endConfig()
                    .buildRound(item.getToDoText().substring(0,1),item.getTodoColor());

            holder.mColorImageView.setImageDrawable(myDrawable);
            if(item.getToDoDate()!=null){
                String timeToShow;
                if(android.text.format.DateFormat.is24HourFormat(MainActivity.this)){
                    timeToShow = AddToDoActivity.formatDate(MainActivity.DATE_TIME_FORMAT_24_HOUR, item.getToDoDate());
                }
                else{
                    timeToShow = AddToDoActivity.formatDate(MainActivity.DATE_TIME_FORMAT_12_HOUR, item.getToDoDate());
                }
                holder.mTimeTextView.setText(timeToShow);
            }


        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        BasicListAdapter(ArrayList<ToDoItem> items){

            this.items = items;
        }


        @SuppressWarnings("deprecation")
        public class ViewHolder extends RecyclerView.ViewHolder{

            View mView;
            LinearLayout linearLayout;
            TextView mToDoTextview;
//            TextView mColorTextView;
            ImageView mColorImageView;
            TextView mTimeTextView;
//            int color = -1;

            public ViewHolder(View v){
                super(v);
                mView = v;
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToDoItem item = items.get(ViewHolder.this.getAdapterPosition());
                        Intent i = new Intent(MainActivity.this, AddToDoActivity.class);
                        i.putExtra(TODOITEM, item);
                        startActivityForResult(i, REQUEST_ID_TODO_ITEM);
                    }
                });
                mToDoTextview = v.findViewById(R.id.toDoListItemTextview);
                mTimeTextView = v.findViewById(R.id.todoListItemTimeTextView);
                mColorImageView = v.findViewById(R.id.toDoListItemColorImageView);
                linearLayout = v.findViewById(R.id.listItemLinearLayout);
            }


        }
    }


    private void saveDate(){
        try {
            storeRetrieveData.saveToFile(mToDoItemsArrayList);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            storeRetrieveData.saveToFile(mToDoItemsArrayList);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecyclerView.removeOnScrollListener(customRecyclerScrollViewListener);
        SumUpAPI.logout();
    }


    //closes FAB submenus
    private void closeSubMenusFab(){
        todoFABLayout.setVisibility(View.INVISIBLE);
        paymentFABLayout.setVisibility(View.INVISIBLE);
        mMainFAB.setImageResource(R.drawable.ic_add);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        todoFABLayout.setVisibility(View.VISIBLE);
        paymentFABLayout.setVisibility(View.VISIBLE);
        //Change settings icon to 'X' icon
        mMainFAB.setImageResource(R.drawable.ic_close);
        fabExpanded = true;
    }



}


