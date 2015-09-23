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

/**
 * JsonManager.java - This class is responsible to parse json response and return product details.
 *
 * @author Saurabh Patel
 *         skpatel@syr.edu
 * @version 1.0
 */
public class JsonManager {

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

	/**
	 * Get products information from json string response.
	 *
	 * @return List of products, ArrayList<Product>
	 */
	public ArrayList<Product> getProducts(final String response) {
		// 1. build product's ArrayList
		final ArrayList<Product> productList = new ArrayList<Product>();
		try {
			// 2. build jsonObject of response
			final JSONObject jsonObject = new JSONObject(response);
			// 3. build jsonArray
			final JSONArray jsonArray = jsonObject.getJSONArray(RESULTS);
			for (int i = 0; i < jsonArray.length(); i++) {
				// 4. parse one by one attribute from json.
				final JSONObject object = jsonArray.getJSONObject(i);
				final String brandName = object.optString(BRAND_NAME);
				final String price = object.optString(PRICE);
				final String imageUrl = object.optString(IMAGE_URL);
				final String asin = object.optString(ASIN);
				final String productName = object.optString(PRODUCT_NAME);
				final String rating = object.optString(RATING);
				// 5. build product object from data
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
		return productList;
	}

	/**
	 * Get product details from json string response.
	 *
	 * @return products detail object, Product
	 */
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
			Log.e(TAG, "JSONException in getProductDetail(): " + e.getMessage(), e);
		}
		return product;
	}
}
