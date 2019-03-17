package net.jmesnil.numbers;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Metered;

import net.jmesnil.numbers.bean.Product;
import net.jmesnil.numbers.dao.ServiceHashMapDAO;

@Path("/products")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class ProductController {
	
	@Inject
	private ServiceHashMapDAO serviceDao ;
	
	@Context
	private Product product;
	@Inject
	@ConfigProperty(name="productServicePort" ,defaultValue="9000" )
	private String productServicePort;
	
	@Inject
	private Config config;
	
	@GET
	public Collection<Product> listProducts() {
		
		Collection<Product> productList = serviceDao.listProducts();
		System.out.println("productServicePort" + productServicePort);
		return productList;
	}
	
	@GET
	@Path("/config")
	public Config showConfig() {
		
		return config;
		
		
	}
	
		
	@GET
	@Path("/{id}")
	public Product listProduct(@PathParam("id") String id) {
		
		Product product = serviceDao.listProducts(id);
		return product;
	}
	
	@POST
	@Metered(name = "addMethod" ,description = "how many times add is getting called" )
	@Path("/add")
	public Product addProducts(Product p) {
		
		Product product = serviceDao.save(p);
		return product;
	}
	
	@PUT
	@Path("/update")
	public Product updateProduct(Product p) {
		
		Product product = serviceDao.update(p);
		return product;
	}
	
	@DELETE
	@Path("/remove/{id}")
	public String removeProduct(@PathParam("id") String id) {
		String status = serviceDao.removeProduct(id);
		return status;
	}
	@GET
	@Gauge(name ="listProduct", unit = MetricUnits.NONE  )
	@Path("/productCount")
	public long  getProductCounter() {
		
		long count= serviceDao.getProductCount();
		
		return count;
	}
	
	

}
