package net.jmesnil.numbers.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import net.jmesnil.numbers.bean.*;

import net.jmesnil.numbers.bean.CartData;

@ApplicationScoped
public class ServiceCartHashMapDAO {
	
	private Map<String, CartData> cartDataMap = new HashMap<String, CartData>();

	public Collection<CartData> cartDataList() {
		System.out.println("Inside ServiceDAO-cartDataList method");
		//dataMap.values();
		return cartDataMap.values();
	}

	public CartData listCart(String id) {
		System.out.println("Inside ServiceDAO-listCart-ID method");
		CartData cartData = cartDataMap.get(id);

		return cartData;
	}

	public CartData save(CartData p) {
		System.out.println("Inside ServiceDAO-save method");
		// ToDO
		cartDataMap.put(p.getId(), p);

		System.out.println("Exit ServiceDAO-save method");

		return p;
	}

	public CartData update(CartData p) {
		System.out.println("Inside ServiceDAO-update method");
		// ToDO

		cartDataMap.put(p.getId(), p);

		System.out.println("Exit ServiceDAO-update method");

		return p;
	}
	
	public String removeCart(String id) {
		System.out.println("Inside ServiceDAO-removeCart method");
		//dataMap.values();
		cartDataMap.remove(id);
		
		return "Removed" + id;
	}

	public long getProductCount() {
		// TODO Auto-generated method stub
		System.out.println("Inside ServiceDAO-getProductCount method");
		
		return cartDataMap.size();
	}

}
