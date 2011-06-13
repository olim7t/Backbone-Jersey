package com.xebia.resource;

import java.util.Arrays;
import java.util.List;

import com.xebia.representation.Product;

public class Shipment {

	public static List<Product> products() {
		return products;
	}

	static List<Product> products = Arrays.asList(product("Pastorale américaine"), product("Les raisins de la colère"),
			product("L'étranger"), product("Terre des hommes"), product("La bouche pleine de terre"),
			product("Substance mort"), product("Blonde"));
	
	static long productCount;

	private static Product product(String name) {
		Product product = new Product();
		product.setName(name);
		product.setPrice(1 + (int)(10 * Math.random()));
		return product;
	}
}
