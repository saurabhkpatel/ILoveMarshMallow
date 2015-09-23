package com.app.ilovemarshmallow;

import java.util.List;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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

/**
 * 
 */
public class SearchResultsActivity extends AppCompatActivity {

	private static final String TAG = "ILoveMarshmallow-"
			+ SearchResultsActivity.class.getSimpleName();
	private ProgressDialog pDialog;

	private TextView mTxtMsgResponse;
	private RecyclerView mRecyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		final TextView textView = (TextView) findViewById(R.id.toolbar_title);
		textView.setText("Products");

		mTxtMsgResponse = (TextView) findViewById(R.id.activity_result_txt_response);
		mRecyclerView = (RecyclerView) findViewById(R.id.rv_product);

		handleIntent(getIntent());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);

		final SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

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

	private void handleIntent(Intent intent) {

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			// use the query to search

			Log.d(TAG, "-----------------------Query : " + query);
			Toast.makeText(this, query, Toast.LENGTH_SHORT).show();

			hideProgressDialog();

			pDialog = new ProgressDialog(SearchResultsActivity.this);
			pDialog.setMessage("Loading...");
			pDialog.setCancelable(false);
			makeStringReq(query);

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		hideProgressDialog();
	}

	private void showProgressDialog() {
		if (pDialog != null && !pDialog.isShowing()) {
			pDialog.show();
		}
	}

	private void hideProgressDialog() {
		if (pDialog != null && pDialog.isShowing()) {
			pDialog.hide();
			pDialog.dismiss();
		}
	}

	/**
	 * Making json object request
	 */
	private void makeStringReq(String str) {
		showProgressDialog();

		final StringRequest strReq = new StringRequest(Request.Method.GET,
				Const.URL_STRING_REQ + str, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Zappos Response : " + response);

						final JsonManager jsonManager = new JsonManager();
						final List<Product> productList = jsonManager.getProducts(response);

						mTxtMsgResponse.setText("Product Data:\n\n");

						if (productList != null) {

							final ProductAdapter productAdapter = new ProductAdapter(
									SearchResultsActivity.this, productList);
							mRecyclerView.setAdapter(productAdapter);
							final RecyclerView.LayoutManager manager = new GridLayoutManager(
									getParent(), 2);
							mRecyclerView.setLayoutManager(manager);
							mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(
									SearchResultsActivity.this, mRecyclerView,
									new RecyclerTouchListener.ClickListener() {
								@Override
								public void onClick(View view, int position) {

									Product product = productList.get(position);

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

							mTxtMsgResponse.setVisibility(View.GONE);
						}
						else {
							mTxtMsgResponse.setText("No data found!!!");
							mRecyclerView.setVisibility(View.GONE);
						}

						hideProgressDialog();

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.d(TAG, "Error: " + error.getMessage());
						mTxtMsgResponse.setText(error.getMessage());
						hideProgressDialog();
					}
				});

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, Const.TAG_STRING_REQ);
	}
}
