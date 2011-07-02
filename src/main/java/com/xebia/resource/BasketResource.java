package com.xebia.resource;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.xebia.data.Products;
import com.xebia.data.Purchases;
import com.xebia.representation.Basket;
import com.xebia.representation.Link;
import com.xebia.representation.Price;
import com.xebia.representation.Rels;
import com.xebia.representation.Stock;

@Path("/basket/{username}")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class BasketResource {

	static UriBuilder linkProductBuilder = UriBuilder.fromPath("resource/product").path("/{id}");
	static UriBuilder linkPriceBuilder = UriBuilder.fromPath("resource/basket/{username}").path("/price");
	static UriBuilder linkPaymentBuilder = UriBuilder.fromPath("resource/basket/{username}").path("/payment");

	@PathParam("username") String username;
	
	@GET
	@Path("/{product}")
	public Response get(@PathParam("product") long productId) {
		Map<Long, Integer> map = Purchases.get(username);

		Stock stock = createStock(productId, map.get(productId));
		return Response.ok(stock).build();
	}

	private Stock createStock(long productId, Integer quantity) {
		Stock stock = new Stock();
		stock.setQuantity(quantity);
		stock.setId(productId);
		stock.setRelated(productLink(productId));
		return stock;
	}

	@GET
	public Response get() {
		Map<Long, Integer> map = Purchases.get(username);
		
		Basket basket = new Basket();
		for (Long productId : map.keySet()) {
			Stock stock = createStock(productId, map.get(productId));
			basket.getStock().add(stock);
		}
		basket.getLinks().add(paymentLink(username));
		basket.getLinks().add(priceLink(username));
		
		return Response.ok(basket).build();
	}
	
	@GET
	@Path("/price")
	public Response getPrice() {
		Map<Long, Integer> map = Purchases.get(username);

		int value = 0;
		for (Long productId : map.keySet())
			value += map.get(productId) * Products.get(productId).getPrice();

		Price price = new Price();
		price.setValue(value);
		return Response.ok(price).build();
	}
	
	@POST
	@Path("/payment")
	public Response pay() {
		Purchases.empty(username);
		return Response.ok().build();
	}
	
	@DELETE
	public Response delete() {
		Purchases.empty(username);
		return Response.ok().build();
	}
	
	private Link productLink(long productId) {
		URI uri = linkProductBuilder.build(productId);
		return createLink(uri.getPath(), Rels.RELS_RELATED);
	}

	private Link paymentLink(String username) {
		URI uri = linkPaymentBuilder.build(username);
		return createLink(uri.getPath(), Rels.RELS_PAYMENT);
	}

	private Link priceLink(String username) {
		URI uri = linkPriceBuilder.build(username);
		return createLink(uri.getPath(), Rels.RELS_PRICE);
	}
	
	private Link createLink(String path, Rels rel) {
		Link link = new Link();
		link.setHref(path);
		link.setRel(rel);
		return link;
	}
}
