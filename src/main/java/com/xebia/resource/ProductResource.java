package com.xebia.resource;

import java.net.URI;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import com.google.common.base.Objects;
import com.xebia.data.Products;
import com.xebia.data.Purchases;
import com.xebia.data.Stocks;
import com.xebia.representation.Link;
import com.xebia.representation.Product;
import com.xebia.representation.Rels;
import com.xebia.representation.Stock;

@Path("/product")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ProductResource {

	@GET
	public Response get() {
		List<Product> products = Products.get();
		for (Product product : products)
			addBookLink(product);

		GenericEntity<List<Product>> entity = new GenericEntity<List<Product>>(products) {};
		return Response.ok(entity).build();
	}

	@GET
	@Path("/{id}")
	public Response get(@PathParam("id") long id) {
		Product product = Products.get(id);
		addBookLink(product);
		return Response.ok(product).build();
	}

	static UriBuilder uriBuilder = //
	UriBuilder.fromPath("resource/product").path("{id}/stock/{quantity}/{username}");

	private void addBookLink(Product product) {
		Link link = new Link();
		URI uri = uriBuilder.build(product.getId(), "{quantity}", "{username}");
		link.setHref(uri.getPath());
		link.setRel(Rels.RELS_BOOK);
		product.getLinks().add(link);
	}

	@POST
	public Response post(Product product) {
		Products.put(product);
		addBookLink(product);
		return Response.ok(product).build();
	}

	@DELETE
	@Path("/{id}")
	public void delete(@PathParam("id") long id) {
		Products.delete(id);
		Stocks.delete(id);
	}

	@GET
	@Path("/{id}/stock")
	public Response stock(@PathParam("id") long id) {
		Stock stock = new Stock();
		stock.setQuantity(Stocks.quantity(id));
		return Response.ok(stock).tag(eTag(id, stock.getQuantity())).build();
	}

	private EntityTag eTag(long id, int quantity) {
		return new EntityTag(String.valueOf(Objects.hashCode(id, quantity)));
	}

	@POST
	@Path("/{id}/stock")
	public Response addToStock(@PathParam("id") long id, Stock stock) {
		Status status;
		Integer instock = Stocks.quantity(id);
		if (instock != null) {
			Stocks.put(id, instock + stock.getQuantity());
			status = Status.ACCEPTED;
		} else {
			status = Status.NOT_FOUND;
		}
		return Response.status(status).build();
	}

	@POST
	@Path("/{id}/stock/{quantity}/{username}")
	public Response post(@PathParam("id") long id, @PathParam("quantity") int quantity,
			@PathParam("username") String username, @Context Request request) {

		String message;
		
		int productQuantity = Stocks.quantity(id);
		if (request.evaluatePreconditions(eTag(id, productQuantity)) == null) {
			if (Stocks.sell(id, quantity)) {	
				Purchases.put(username, id, quantity);
				return Response.ok().build();
			} else message = Products.get(id).getName()+" is out of stock";
		} else message = "eTag mismatch";
		return Response.status(Status.PRECONDITION_FAILED).entity(message).build();
	}
}
