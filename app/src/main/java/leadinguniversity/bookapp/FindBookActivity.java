package leadinguniversity.bookapp;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FindBookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<FindBook>> {

    private static final int BOOK_LOADER_ID = 1;
    private FindBookAdapter mAdapter;
    private String GOOGLE_API_REQUEST_URL ;
    private LoaderManager loaderManager;

    Dialog myDialog;
    EditText editText;
    ImageButton imageButton;
    TextView mEmptyStateTextView, startingTextView;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_buybook);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Book Information");

        editText = findViewById(R.id.et_search);
        imageButton = findViewById(R.id.img_search);

        startingTextView = findViewById(R.id.emoji_view);
        startingTextView.setText(getString(R.string.emoji));

        mProgressBar = findViewById(R.id.loading_indicator);
        mEmptyStateTextView  = findViewById(R.id.empty_view);

        ListView bookListView = findViewById(R.id.list);
        bookListView.setEmptyView(mEmptyStateTextView);

        mAdapter = new FindBookAdapter(this, new ArrayList<FindBook>());
        bookListView.setAdapter(mAdapter);

        myDialog = new Dialog(this);
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FindBook currentFindBook = mAdapter.getItem(position);
                showPopup(view, currentFindBook);
            }
        });


        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected){
            // Get a reference to the LoaderManager, in order to interact with loaders.
            loaderManager = getLoaderManager();

        }else{
            startingTextView.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText.setFocusableInTouchMode(true);
                return false;
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    imageButton.callOnClick();
                    return true;
                }
                return false;
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setFocusable(false);
                String search = editText.getText().toString();
                search = search.replace(' ','+');
                GOOGLE_API_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q="+search+"&maxResults=40";

                loaderManager.restartLoader(BOOK_LOADER_ID, null, FindBookActivity.this);
            }
        });

    }

    @Override
    public Loader<List<FindBook>> onCreateLoader(int i, Bundle bundle) {
        startingTextView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        // Create a new loader for the given URL
        return new BookLoader(this, GOOGLE_API_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<FindBook>> loader, List<FindBook> findBooks) {
        mProgressBar.setVisibility(View.GONE);

        // Set empty state text to display "No books found."
        mEmptyStateTextView .setText(R.string.no_books);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (findBooks != null && !findBooks.isEmpty()) {
            mAdapter.addAll(findBooks);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<FindBook>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    public void showPopup(View v, final FindBook currentFindBook){
        myDialog.setContentView(R.layout.layout_dialog);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView title = myDialog.findViewById(R.id.dialog_title);
        title.setText(currentFindBook.getmTitle());

        ImageView img = myDialog.findViewById(R.id.dialog_img);
        Picasso.get().load(currentFindBook.getmImageUrl()).fit().into(img);

        TextView author = myDialog.findViewById(R.id.dialog_author);
        author.setText("by "+ currentFindBook.getmAuthor());

        RatingBar rating = myDialog.findViewById(R.id.dialog_rating);
        rating.setRating((float) currentFindBook.getmRating());

        TextView ratingReviews = myDialog.findViewById(R.id.dialog_rating_reviews);
        ratingReviews.setText("("+ currentFindBook.getmRatingCount()+")");

        TextView category = myDialog.findViewById(R.id.dialog_catogry);
        category.setText(currentFindBook.getmCategories());

        TextView pageCount = myDialog.findViewById(R.id.dialog_pageCount);
        pageCount.setText(currentFindBook.getmPageCount()+" pages");

        String[] arr = currentFindBook.getmPublishedDate().split("-");
        TextView date = myDialog.findViewById(R.id.dialog_publishDate);
        date.setText("Published Date: "+arr[0]);

        TextView discreption = myDialog.findViewById(R.id.dialog_disc);
        discreption.setText(currentFindBook.getmDescription());
        discreption.setMovementMethod(new ScrollingMovementMethod());

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(30);
        shape.setColor(getResources().getColor(R.color.gold));
        Button buy = myDialog.findViewById(R.id.btn_buy);
        buy.setBackground(shape);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri BookUri = Uri.parse(currentFindBook.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, BookUri);
                startActivity(websiteIntent);
            }
        });

        GradientDrawable shape2 = new GradientDrawable();
        shape2.setCornerRadius(30);
        shape2.setColor(Color.BLACK);
        Button cancel = myDialog.findViewById(R.id.btn_cancle);
        cancel.setBackground(shape2);

        myDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
        }
        return super.onOptionsItemSelected(item);
    }

    public void canclePopup(View view) {
        myDialog.dismiss();
    }
}
