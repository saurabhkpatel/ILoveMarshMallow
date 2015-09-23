package com.app.ilovemarshmallow.bl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Product.java - This class holds information about product. Which includes all necessary parameters and attributes of product object.
 * 				  Here we have used Parcelable because Parcel is a one type of serialization mechanism provided by Android.
 * 				  In order to write an object to a Parcel, that object should implement the interface “Parcelable“.
 * 				  Used parcelable instead of JAVA SERIALIZATION as it gives high performance.
 *
 * @author Saurabh Patel
 *         skpatel@syr.edu
 * @version 1.0
 */
public class Product implements Parcelable {

	public final static String PAR_KEY = "com.app.ilovemarshmallow.par"; 	// Key to pass object in another activity

	private String brandName;
	private String price;
	private String imageUrl;
	private String asin;
	private String productName;
	private String rating;
	private String description;
	private String defaultProductUrl;

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDefaultProductUrl() {
		return defaultProductUrl;
	}

	public void setDefaultProductUrl(String defaultProductUrl) {
		this.defaultProductUrl = defaultProductUrl;
	}

	/**
	 * Define the kind of object that you gonna parcel, You can use hashCode() here
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Actual object serialization happens here, Write object content to parcel, reading should be
	 * done according to this write order
	 * @param dest - parcel
	 * @param flags - Additional flags about how the object should be written
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(brandName);
		dest.writeString(price);
		dest.writeString(imageUrl);
		dest.writeString(asin);
		dest.writeString(productName);
		dest.writeString(rating);
		dest.writeString(description);
		dest.writeString(defaultProductUrl);
	}

	/**
	 * This field is needed for Android to be able to create new objects, individually or as arrays.
	 * If you don’t do that, Android framework will raises an exception Parcelable protocol requires
	 * a Parcelable.Creator object called CREATOR.
	 */
	public static final Parcelable.Creator<Product> CREATOR = new Creator<Product>() {
		@Override
		public Product createFromParcel(Parcel parcel) {

			Product product = new Product();
			product.brandName = parcel.readString();
			product.price = parcel.readString();
			product.imageUrl = parcel.readString();
			product.asin = parcel.readString();
			product.productName = parcel.readString();
			product.rating = parcel.readString();
			product.description = parcel.readString();
			product.defaultProductUrl = parcel.readString();
			return product;
		}

		@Override
		public Product[] newArray(int size) {
			return new Product[size];
		}
	};

}
