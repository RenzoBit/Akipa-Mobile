package com.akipa.listview.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.akipa.akipadelivery.R;
import com.akipa.lazyloading.utils.ImageLoader;
import com.akipa.navigation.PlateDetailsFragment;
import com.akipa.navigation.PlateListsFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


public class ListViewAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater inflater;
	private List<ItemListPogo> itemListPogo = null;
	private ArrayList<ItemListPogo> arraylist;
	public ImageLoader imageLoader; 

	public ListViewAdapter(Context context, List<ItemListPogo> itemLists) 
	{
		mContext = context;
		this.itemListPogo = itemLists;
		inflater = LayoutInflater.from(mContext);
		this.arraylist = new ArrayList<ItemListPogo>();
		this.arraylist.addAll(itemLists);
		imageLoader      = new ImageLoader(mContext.getApplicationContext());
	}

	public class ViewHolder {

		TextView product_name;
		TextView name;
		TextView price;
		RatingBar ratingBar;
		ImageView plateImage;
		TextView plateId;
	}

	@Override
	public int getCount() {
		return itemListPogo.size();
	}

	@Override
	public ItemListPogo getItem(int position) {
		return itemListPogo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup parent) 
	{
		final ViewHolder holder;
		if (view == null) 
		{
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.list_item, null);

			holder.product_name  = (TextView) view.findViewById(R.id.product_name);
			holder.name      	 = (TextView) view.findViewById(R.id.name);
			holder.price     	 = (TextView) view.findViewById(R.id.price);
			holder.ratingBar     = (RatingBar) view.findViewById(R.id.ratingBar);
			holder.plateImage    = (ImageView) view.findViewById(R.id.imageViewPlateImage);
			holder.plateId       = (TextView) view.findViewById(R.id.plateId);

			view.setTag(holder);
		} 
		else 
		{
			holder = (ViewHolder) view.getTag();
		}

		// Set the results into TextViews
		holder.name.setText(itemListPogo.get(position).getItemName());
		holder.product_name.setText(itemListPogo.get(position).getProductName());
		holder.price.setText("S/. " + itemListPogo.get(position).getPrecio());
		holder.ratingBar.setRating(Float.parseFloat(itemListPogo.get(position).getRating()));
		holder.plateId.setText(itemListPogo.get(position).getitemIdplato());

		imageLoader.DisplayImage(itemListPogo.get(position).getitemImageUrl(), (ImageView) holder.plateImage);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{
				Log.v("TAG", "Clicked: " + itemListPogo.get(position).getitemIdplato());
				
				Intent i = new Intent("start.fragment.action");
				i.putExtra("plateId", itemListPogo.get(position).getitemIdplato());
				mContext.sendBroadcast(i);
			}
		});

		return view;
	}

	// Filter Class
	public void filter(String charText) 
	{
		charText = charText.toLowerCase(Locale.getDefault());
		itemListPogo.clear();

		if (charText.length() == 0) 
		{
			itemListPogo.addAll(arraylist);
		} 
		else
		{
			for (ItemListPogo wp : arraylist) 
			{
				if (wp.getProductName().toLowerCase(Locale.getDefault())
						.contains(charText)) 
				{
					itemListPogo.add(wp);
				}

				else if (wp.getItemName().toLowerCase(Locale.getDefault())
						.contains(charText)) 
				{
					itemListPogo.add(wp);
				}

				else if (wp.getPrecio().toLowerCase(Locale.getDefault())
						.contains(charText)) 
				{
					itemListPogo.add(wp);
				}

				else if (wp.getRating().toLowerCase(Locale.getDefault())
						.contains(charText)) 
				{
					itemListPogo.add(wp);
				}
			}
		}
		notifyDataSetChanged();
	}
}
