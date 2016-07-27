package com.akipa.listview.utils;


public class ItemListPogo {
	private String companyName;
	private String itemName;
	private String itemImageUrl;
	private String precio;
	private String rating;
	private String idplato;

	public ItemListPogo(String products, String name, String precio, String rating, String imagen, String idplato) {
		this.companyName = products;
		this.itemName = name;
		this.precio = precio;
		this.rating = rating;
		this.idplato = idplato;
		this.itemImageUrl = ReusableClass.asset_url + imagen + ".jpg";
	}


	public String getProductName() {
		return this.companyName;
	}

	public String getItemName() {
		return this.itemName;
	}

	public String getPrecio() {
		return precio;
	}

	public String getRating() {
		return this.rating;
	}

	public String getitemImageUrl() {
		return this.itemImageUrl;
	}

	public String getitemIdplato() {
		return this.idplato;
	}

}
