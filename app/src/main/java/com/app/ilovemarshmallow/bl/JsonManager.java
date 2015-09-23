/**************************************************************************************
 * Copyright: Copyright (c) 2014 Insigno Quipment Technologies (India) Private Limited.  
 * All rights reserved.
 * 
 * Description: This is a JsonManager class to do JSON operations.
 **************************************************************************************/
package com.app.ilovemarshmallow.bl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonManager {

	/**
	 * Tag for use JsonManager output to Logger
	 */
	private static final String TAG = "ILoveMarshmallow-" + JsonManager.class.getSimpleName();

	// JSON Node names for Product
	private static final String RESULTS = "results";
	private static final String BRAND_NAME = "brandName";
	private static final String PRICE = "price";
	private static final String IMAGE_URL = "imageUrl";
	private static final String ASIN = "asin";
	private static final String PRODUCT_NAME = "productName";
	private static final String RATING = "productRating";
	private static final String DESCRPTION = "description";
	private static final String DEFAULT_IMAGE_URL = "defaultImageUrl";

	public List<Product> getProducts(final String response) {

		// 1. build product's ArrayList
		final List<Product> productList = new ArrayList<Product>();
		try {
			// 2. build jsonObject of response
			final JSONObject jsonObject = new JSONObject(response);

			// 3. build jsonArray of voucher
			final JSONArray jsonArray = jsonObject.getJSONArray(RESULTS);

			for (int i = 0; i < jsonArray.length(); i++) {

				// 4. build jsonObject of individual voucher from array list
				final JSONObject object = jsonArray.getJSONObject(i);
				final String brandName = object.optString(BRAND_NAME);
				final String price = object.optString(PRICE);
				final String imageUrl = object.optString(IMAGE_URL);
				final String asin = object.optString(ASIN);
				final String productName = object.optString(PRODUCT_NAME);
				final String rating = object.optString(RATING);

				// 5. get product object
				final Product product = new Product();
				product.setBrandName(brandName);
				product.setPrice(price);
				product.setImageUrl(imageUrl);
				product.setAsin(asin);
				product.setProductName(productName);
				product.setRating(rating);
				productList.add(product);
			}
		}
		catch (final JSONException e) {
			Log.e(TAG, "JSONException in getProducts(): " + e.getMessage(), e);
		}
		// 6. get license voucher list
		return productList;
	}

	public Product getProductDetail(String response) {

		final Product product = new Product();

		try {
			final JSONObject jsonObject = new JSONObject(response);
			product.setProductName(jsonObject.optString(PRODUCT_NAME));
			product.setBrandName(jsonObject.optString(BRAND_NAME));
			product.setDefaultProductUrl(jsonObject.optString(DEFAULT_IMAGE_URL));
			product.setDescription(jsonObject.optString(DESCRPTION));

		}
		catch (JSONException e) {
			Log.e(TAG, "JSONException in getProducts(): " + e.getMessage(), e);
		}

		return product;
	}
}
