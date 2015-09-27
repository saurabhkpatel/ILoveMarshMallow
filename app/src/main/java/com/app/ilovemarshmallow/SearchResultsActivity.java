package com.app.ilovemarshmallow;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.app.ilovemarshmallow.adapter.ProductAdapter;
import com.app.ilovemarshmallow.app.AppController;
import com.app.ilovemarshmallow.bl.JsonManager;
import com.app.ilovemarshmallow.bl.Product;
import com.app.ilovemarshmallow.utils.Const;
import com.app.ilovemarshmallow.utils.Utils;

import java.util.ArrayList;

/**
 * SearchResultsActivity.java - This activity class shows results after search query applied. User will enter text in search bar
 * to search any products.
 *
 * @author Saurabh Patel
 *         skpatel@syr.edu
 * @version 1.0
 */
public class SearchResultsActivity extends AppCompatActivity {

    private static final String TAG = "ILoveMarshmallow-"
            + SearchResultsActivity.class.getSimpleName();
    private ProgressDialog pDialog;

    private RecyclerView mRecyclerView;
    private ProductAdapter mProductAdapter;


    private ArrayList<Product> mProductList = null;
    private int mCurrentPage = 1;
    private String mSearchQuery = "";
    private int mSpanCount = 2;

    private static final String PRODUCT_LIST = "productlist";
    private static final String PAGE_NUMBER = "page_number";
    private static final String SEARCH_QUERY = "search_query";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        setupToolbar();

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_product);


        // Usually you restore your state in onCreate(). It is possible to restore it in onRestoreInstanceState() as well,
        // but not very common. (onRestoreInstanceState() is called after onStart(), whereas onCreate() is called before onStart().
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mSpanCount = 3;
        } else {
            mSpanCount = 2;
        }
        if (savedInstanceState != null) {
            getSavedData(savedInstanceState);
            setupRecyclerView();
            setUpProductAdapter();
        } else {
            handleIntent(getIntent());
        }
    }

    /**
     * setup tool bar.
     */
    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.include_toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getString(R.string.products));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    /**
     * get saved data from bundle, when activity orientation change.
     */
    private void getSavedData(Bundle savedInstanceState) {
        mSearchQuery = savedInstanceState.getString(SEARCH_QUERY);
        mProductList = savedInstanceState.getParcelableArrayList(PRODUCT_LIST);
        mCurrentPage = savedInstanceState.getInt(PAGE_NUMBER);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Usually you restore your state in onCreate(). It is possible to restore it in onRestoreInstanceState() as well,
        // but not very common. (onRestoreInstanceState() is called after onStart(), whereas onCreate() is called before onStart().
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        // Save product list into the bundle
        savedState.putParcelableArrayList(PRODUCT_LIST, mProductList);
        savedState.putInt(PAGE_NUMBER, mCurrentPage);
        savedState.putString(SEARCH_QUERY, mSearchQuery);
        super.onSaveInstanceState(savedState);
    }

    /**
     * Handle intent when this activity call from parent activity.
     */
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mSearchQuery = intent.getStringExtra(SearchManager.QUERY);
            mCurrentPage = 1;
            // use the query to search
            Log.d(TAG, "Search Query : " + mSearchQuery);
            hideProgressDialog();

            // Check internet connection before we make request to API.
            if (Utils.isConnectingToInternet(SearchResultsActivity.this)) {
                pDialog = new ProgressDialog(SearchResultsActivity.this);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                makeStringReq(mSearchQuery, mCurrentPage, true);
            } else {
                Toast.makeText(SearchResultsActivity.this, getString(R.string.please_check_internet), Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideProgressDialog();
    }

    /**
     * show progress bar when network operations starts in background.
     */
    private void showProgressDialog() {
        if (pDialog != null && !pDialog.isShowing()) {
            pDialog.show();
        }
    }

    /**
     * Hide Progress bar when network operation done.
     */
    private void hideProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.hide();
            pDialog.dismiss();
        }
    }

    /**
     * setup product adapter to bind data with recyclerview
     */
    private void setUpProductAdapter() {
        if(mProductList != null)
        {
            mProductAdapter = new ProductAdapter(
                    SearchResultsActivity.this, mProductList);
            mRecyclerView.setAdapter(mProductAdapter);
        }
    }

    /**
     * setup recyclerview with attributes and click listener.
     */
    private void setupRecyclerView() {
        if (mRecyclerView != null) {
            Log.d(TAG, "span count " + mSpanCount);
            GridLayoutManager mGridLayoutManager = new GridLayoutManager(
                    getParent(), mSpanCount);
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(
                    SearchResultsActivity.this, mRecyclerView,
                    new RecyclerTouchListener.ClickListener() {
                        @Override
                        public void onClick(View view, int position) {

                            Product product = mProductList.get(position);

                            Bundle bundle = new Bundle();
                            bundle.putParcelable(Product.PAR_KEY, product);

                            Intent intent = new Intent(SearchResultsActivity.this,
                                    DetailActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }

                        @Override
                        public void onLongClick(View view, int position) {

                        }
                    }));
        }
    }


    /**
     * Making API request to get search data.
     */
    private void makeStringReq(String str, int page, final boolean firstTime) {
        showProgressDialog();

        final StringRequest strReq = new StringRequest(Request.Method.GET,
                Const.URL_STRING_REQ + str + Const.URL_STRING_REQ_PAGEPARAMETER + String.valueOf(page), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response);

                final JsonManager jsonManager = new JsonManager();
                mProductList = jsonManager.getProducts(response);


                if (mProductList != null) {
                    if (firstTime) {
                        setupRecyclerView();
                        setUpProductAdapter();
                    } else {
                        // get new products list and add in existing list
                        mProductList.addAll(jsonManager.getProducts(response));
                        mProductAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.search_data_notfound), Toast.LENGTH_SHORT).show();
                    mRecyclerView.setVisibility(View.GONE);
                }
                hideProgressDialog();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (mCurrentPage == 1) {
                    Toast.makeText(getApplicationContext(), "No search results found", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "There are no more items to load", Toast.LENGTH_SHORT).show();
                }

                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, Const.TAG_STRING_REQ);
    }
}
