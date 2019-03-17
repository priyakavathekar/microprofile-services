package net.jmesnil.numbers.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import net.jmesnil.numbers.bean.Product;

@ApplicationScoped
public class ServiceHashMapDAO {

	private Map<String, Product> dataMap = new HashMap<String, Product>();

	public Collection<Product> listProducts() {
		System.out.println("Inside ServiceDAO-list method");
		//dataMap.values();
		return dataMap.values();
	}

	public Product listProducts(String id) {
		System.out.println("Inside ServiceDAO-list-ID method");
		Product product = dataMap.get(id);

		return product;
	}

	public Product save(Product p) {
		System.out.println("Inside ServiceDAO-save method");
		// ToDO
		dataMap.put(p.getId(), p);

		System.out.println("Exit ServiceDAO-save method");

		return p;
	}

	public Product update(Product p) {
		System.out.println("Inside ServiceDAO-update method");
		// ToDO

		dataMap.put(p.getId(), p);

		System.out.println("Exit ServiceDAO-update method");

		return p;
	}
	
	public String removeProduct(String id) {
		System.out.println("Inside ServiceDAO-list method");
		//dataMap.values();
		dataMap.remove(id);
		
		return "Removed" + id;
	}

	public long getProductCount() {
		// TODO Auto-generated method stub
		System.out.println("Inside ServiceDAO-getProductCount method");
		
		return dataMap.size();
	}

}
