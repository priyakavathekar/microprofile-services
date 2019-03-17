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

import net.jmesnil.numbers.bean.CartData;
import net.jmesnil.numbers.dao.ServiceCartHashMapDAO;



@Path("/cart")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartController  {
	
	
	@Inject
	private ServiceCartHashMapDAO serviceCartDAO;
	@Context
	private CartData cartData;
	
	
	
	/*@Inject
	private ProductServiceEndPoint prodInterface ;*/
	
	@GET
	public Collection<CartData> getCart(){
		
		System.out.println("in get cart latest" );
		//Collection<Product> prodList =prodInterface.listProducts();
		//System.out.println("in get cart:: Product" + prodList );
		Collection<CartData> cartData = serviceCartDAO.cartDataList();
		return cartData;
	}
	
	@POST
	@Path("/add")
	public CartData addToCart(CartData d) {
		
		
		
		CartData cartData = serviceCartDAO.save(d);
		return cartData;
	}
	
	@PUT
	@Path("/update")
	public CartData updateDate(CartData d) {
		 CartData cartData = serviceCartDAO.update(d);
		 return cartData;
	}
	@DELETE
	@Path("/remove/{id}")
	public String removeData(@PathParam("id") String id){
		
		String status =serviceCartDAO.removeCart(id);
		return status;
	}
		
	
}
