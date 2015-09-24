package com.app.ilovemarshmallow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
 * DetailActivity.java - This is Activity class, which represents data of any product in detail view.
 * 				 		 If user selects any item from Search results activity then this activity call with asin number.
 * 				 		 Using this asin number application will fetch data from server using API.
 *
 * @author Saurabh Patel
 *         skpatel@syr.edu
 * @version 1.0
 */
public class DetailActivity extends AppCompatActivity {

	private static final String TAG = "ILoveMarshmallow-" + DetailActivity.class.getSimpleName();

	private String mShareAsin;

	// widgets declarations.
	private TextView mTxtTitle;
	private TextView mTxtProductName;
	private NetworkImageView mThumbnail;
	private HtmlTextView mTxtDescription;

	private ImageLoader mImageLoader = AppController.getInstance().getImageLoader();

	// used to store data when configuration changes.
	private static final String PRODUCT_IMAGE = "image";
	private static final String PRODUCT_NAME = "product";
	private Product mProduct = null;

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
		// Initialize image and description views
		mThumbnail = (NetworkImageView) findViewById(R.id.thumbnail);
		mTxtDescription = (HtmlTextView) findViewById(R.id.txt_description);

		mProduct = (Product) getIntent().getParcelableExtra(Product.PAR_KEY);

		if (getIntent().getAction() == Intent.ACTION_VIEW) {
			// share intent condition.
			if (mProduct == null) {
				final Uri data = getIntent().getData();
				if (data != null) {
					mShareAsin = data.getPathSegments().get(0);
					makeStringReq(mShareAsin);
				}
			}
		}
		else if(savedInstanceState != null)
		{
			// configuration change condition
			getSavedProductData(savedInstanceState);
			updateProductData();
		}
		else {
			// start new activity condition.
			// Set Title to this Activity
			mTxtTitle.setText(mProduct.getBrandName());
			// Set Product name
			mTxtProductName.setText(mProduct.getProductName());
			// Set Product Price
			final TextView txtPrice = (TextView) findViewById(R.id.txt_price);
			txtPrice.setText(mProduct.getPrice());
			mShareAsin = mProduct.getAsin();
			makeStringReq(mShareAsin);

		}
	}


	@Override
	protected void onSaveInstanceState(Bundle savedBundle) {
		// saves key info when activity destroyed due to config change
		Bitmap bitmap = ((BitmapDrawable)mThumbnail.getDrawable()).getBitmap();
		savedBundle.putParcelable(PRODUCT_IMAGE, bitmap);
		savedBundle.putParcelable(PRODUCT_NAME, mProduct);
		super.onSaveInstanceState(savedBundle);
	}

	/**
	 * Get product data when configuration change or android device rotate.
	 */
	private void getSavedProductData(Bundle savedInstanceState) {
		// get saved product data using key.
		mProduct = savedInstanceState.getParcelable(PRODUCT_NAME);
	}

	/**
	 * Update product data to textview fields and image view.
	 */
	private void updateProductData() {
		// Set Title to this Activity
		mTxtTitle.setText(mProduct.getBrandName());
		mTxtProductName.setText(mProduct.getProductName());
		mTxtDescription.setHtmlFromString(mProduct.getDescription(), new HtmlTextView.LocalImageGetter());
		final TextView txtPrice = (TextView) findViewById(R.id.txt_price);
		mThumbnail.setImageUrl(mProduct.getDefaultProductUrl(), mImageLoader);
		txtPrice.setText(mProduct.getPrice());
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
	}


	/**
	 * make API request using Volley library.
	 */
	public void makeStringReq(String s) {

		final StringRequest request = new StringRequest(Request.Method.GET,
				Const.URL_STRING_REQ_ASIN + s, new Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Response : " + response);

						JsonManager manager = new JsonManager();
						Product product = manager.getProductDetail(response);

						mTxtTitle.setText(product.getBrandName());
						mTxtProductName.setText(product.getProductName());
						mThumbnail.setImageUrl(product.getDefaultProductUrl(), mImageLoader);
						mProduct.setDescription(product.getDescription());
						mProduct.setDefaultProductUrl(product.getDefaultProductUrl());
						mTxtDescription.setHtmlFromString(product.getDescription(), new HtmlTextView.LocalImageGetter());

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.d(TAG, "Error: " + error.getMessage());
						mTxtDescription.setText(error.getMessage());
						mThumbnail.setVisibility(View.GONE);
					}
				});

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(request, Const.TAG_STRING_REQ);
	}

	/**
	 * Share URL with friends using share intent.
	 */
	private void shareIntent(String s) {
		final Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.marshmallow_url) + s);
		startActivity(Intent.createChooser(shareIntent, "Share via "));
	}
}
