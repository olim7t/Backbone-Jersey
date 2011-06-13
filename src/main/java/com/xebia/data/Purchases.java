package com.xebia.data;

import java.util.Map;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;

public class Purchases {

	static Map<String, Multiset<Long>> purchases = Maps.newHashMap();
	
	public static Map<Long, Integer> get(String name) {
		Map<Long, Integer> map = Maps.newHashMap();
		Multiset<Long> set = purchases.get(name);
		if (set == null)
			return Maps.newHashMap();
		
		for (Long product : set)
			map.put(product, set.count(product));
		return map;
	}
	
	public static void put(String name, long productId, int quantity) {
		Multiset<Long> set = purchases.get(name);
		if (set == null)
			set = HashMultiset.create();
		for (int i = 0; i < quantity; i++)
			set.add(productId);
		
		purchases.put(name, set);
	}
	
	public static void empty(String name) {
		purchases.put(name, null);
	}
}
