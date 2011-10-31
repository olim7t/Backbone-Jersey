package com.xebia.data;

import java.util.Map;

import com.google.common.collect.Maps;

public class Stocks {

	static Map<Long, Integer> stocks = Maps.newHashMap();

	public static int quantity(long productId) {
		Products.exists(productId);
	
		Integer quantity = stocks.get(productId);
		return quantity == null ? 0 : quantity;
	}

	public static void put(long productId, int quantity) {
		stocks.put(productId, quantity);
	}

	public static void delete(long productId) {
		stocks.remove(productId);
	}

	public static boolean sell(long productId, int quantity) {
		Integer oldQuantity = stocks.get(productId);
		if (quantity <= oldQuantity) {
			stocks.put(productId, oldQuantity - quantity);
			return true;
		} else {
			return false;
		}
	}
}
