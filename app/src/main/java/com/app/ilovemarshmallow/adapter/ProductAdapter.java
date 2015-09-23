package com.app.ilovemarshmallow.adapter;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.ilovemarshmallow.R;
import com.app.ilovemarshmallow.app.AppController;
import com.app.ilovemarshmallow.bl.Product;


/**
 * ProductAdapter.java - This class provide a binding from an app-specific data set to views that are displayed within a RecyclerView.
 *                       Here all products will be bind in recycler view which results application got from search item API.
 *
 * @author Saurabh Patel
 *         skpatel@syr.edu
 * @version 1.0
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

	private List<Product> mProductList = Collections.emptyList(); // products list
	private LayoutInflater mInflater;
	private Context mContext; // application context
	private final ImageLoader mImageLoader = AppController.getInstance().getImageLoader();

	// ProductAdapter class constructor
	public ProductAdapter(Context context, List<Product> productList) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mProductList = productList;
	}

	@Override
	public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = mInflater.inflate(R.layout.row_product, parent, false);
		final ProductViewHolder productViewHolder = new ProductViewHolder(view);
		return productViewHolder;
	}

	@Override
	public void onBindViewHolder(ProductViewHolder holder, int position) {

		final Product product = mProductList.get(position);
		holder.thumbNail.setImageUrl(product.getImageUrl(), mImageLoader);
		holder.txtBrandName.setText(product.getBrandName());
		holder.txtPrice.setText(product.getPrice());
		holder.txtProductName.setText(product.getProductName());
		holder.ratingBar.setRating(Float.parseFloat(product.getRating()));
	}

	@Override
	public int getItemCount() {
		return mProductList.size();
	}

	// It is view holder class to initialize the row items for showing product items in list
	public static class ProductViewHolder extends RecyclerView.ViewHolder {

		protected NetworkImageView thumbNail;
		protected TextView txtBrandName;
		protected TextView txtPrice;
		protected TextView txtProductName;
		protected RatingBar ratingBar;

		public ProductViewHolder(View itemView) {
			super(itemView);
			thumbNail = (NetworkImageView) itemView.findViewById(R.id.thumbnail);
			txtBrandName = (TextView) itemView.findViewById(R.id.txt_brand_name);
			txtPrice = (TextView) itemView.findViewById(R.id.txt_price);
			txtProductName = (TextView) itemView.findViewById(R.id.txt_product_name);
			ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
		}
	}
}
