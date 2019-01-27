package main;

import java.util.HashMap;

public class Test {
	
	private static HashMap<String, Integer> dist = new HashMap<String, Integer>();
	
	public static void main(String[] args) {
		dist.put("123", 1);
		dist.put("123", 3);
		dist.put("123", 2);
		System.out.println(dist.get("123"));
	}
}
