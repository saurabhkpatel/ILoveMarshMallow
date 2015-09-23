package com.app.ilovemarshmallow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.app.ilovemarshmallow.app.AppController;
import com.app.ilovemarshmallow.bl.JsonManager;
import com.app.ilovemarshmallow.bl.Product;
import com.app.ilovemarshmallow.utils.Const;
import org.sufficientlysecure.htmltextview.HtmlTextView;

/**
 *
 */
public class DetailActivity extends AppCompatActivity {

	private static final String TAG = "ILoveMarshmallow-" + DetailActivity.class.getSimpleName();
	private ProgressDialog pDialog;

	private String mShareAsin;

	private TextView mTxtTitle;
	private TextView mTxtProductName;
	private NetworkImageView mThumbnail;
	private TextView mTxtDescription;

	private ImageLoader mImageLoader = AppController.getInstance().getImageLoader();

	private static final int ACTION_SHARE = 987;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		// Set Toolbar to Activity
		final Toolbar toolbar = (Toolbar) findViewById(R.id.include_toolbar);
		setSupportActionBar(toolbar);
		assert getSupportActionBar() != null;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("");

		mTxtTitle = (TextView) findViewById(R.id.toolbar_title);
		mTxtProductName = (TextView) findViewById(R.id.txt_product_name);

		final Product product = (Product) getIntent().getParcelableExtra(Product.PAR_KEY);
		if (product == null) {
			final Uri data = getIntent().getData();
			if (data != null) {
				mShareAsin = data.getPathSegments().get(0);
				makeStringReq(mShareAsin);
			}
		}
		else {

			// Set Title to this Activity
			mTxtProductName.setText(product.getBrandName());

			// Set Product name
			mTxtProductName.setText(product.getProductName());

			// Set Product Price
			final TextView txtPrice = (TextView) findViewById(R.id.txt_price);
			txtPrice.setText(product.getPrice());

			makeStringReq(product.getAsin());
			mShareAsin = product.getAsin();
		}

		// Initialize image and description views
		mThumbnail = (NetworkImageView) findViewById(R.id.thumbnail);
		//mTxtDescription = (TextView) findViewById(R.id.txt_description);
		HtmlTextView text = (HtmlTextView) findViewById(R.id.txt_description);

		pDialog = new ProgressDialog(DetailActivity.this);
		pDialog.setMessage("Loading...");
		pDialog.setCancelable(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		menu.findItem(R.id.action_search).setVisible(false);

		// Fetch and store ShareActionProvider
		final MenuItem item = menu.add(ACTION_SHARE, ACTION_SHARE, 0, R.string.action_share);
		item.setIcon(R.drawable.ic_share_white_24dp);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case android.R.id.home:
				finish();
				break;

			case ACTION_SHARE:
				shareIntent(mShareAsin);
				break;

			default:
				break;
		}
		return super.onOptionsItemSelected(item);
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

	public void makeStringReq(String s) {
		showProgressDialog();

		final StringRequest request = new StringRequest(Request.Method.GET,
				Const.URL_STRING_REQ_ASIN + s, new Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Zappos Response : " + response);
						hideProgressDialog();

						JsonManager manager = new JsonManager();
						Product product = manager.getProductDetail(response);

						mTxtTitle.setText(product.getBrandName());
						mTxtProductName.setText(product.getProductName());
						mThumbnail.setImageUrl(product.getDefaultProductUrl(), mImageLoader);
						mTxtDescription.setText(Html.fromHtml(product.getDescription()));

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						hideProgressDialog();

						VolleyLog.d(TAG, "Error: " + error.getMessage());
						mTxtDescription.setText(error.getMessage());
						mThumbnail.setVisibility(View.GONE);
					}
				});

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(request, Const.TAG_STRING_REQ);
	}

	private void shareIntent(String s) {
		final Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.marshmallow_url) + s);
		startActivity(Intent.createChooser(shareIntent, "Share via !!!"));
	}
}
