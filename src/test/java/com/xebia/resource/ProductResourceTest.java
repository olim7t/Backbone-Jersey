package com.xebia.resource;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.xebia.representation.Basket;
import com.xebia.representation.Link;
import com.xebia.representation.Price;
import com.xebia.representation.Product;
import com.xebia.representation.Rels;
import com.xebia.representation.Stock;
import com.xebia.rule.EmbeddedTestServer;

public class ProductResourceTest {

	@Rule
	public EmbeddedTestServer server = new EmbeddedTestServer();

	@Before
	public void before() {
		for (Product product : Shipment.products()) {
			product = productResource().entity(product).post(Product.class);
			Stock stock = new Stock();
			stock.setQuantity(2);
			stockResource(product.getId()).post(stock);
		}
	}
	
	@After
	public void after() {
		for(Product product : getProducts())
			productResource(product.getId()).delete();
		
		basketResource("xebia").delete();
	}

	@Test
	public void shouldGetProduct() {
		assertNotNull(productResource(2).get(Product.class));
	}

	@Test
	public void shouldListProducts() {
		List<Product> products = getProducts();
		assertEquals(7, products.size());
		for (Product product : products)
			assertEquals(2, stockResource(product.getId()).get(Stock.class).getQuantity(), 0);
	}

	@Test
	public void shouldAccessStock() {
		for (Product product : getProducts())
			assertEquals(2, stockResource(product.getId()).get(Stock.class).getQuantity(), 0);
	}

	@Test
	public void shouldMoveProductToBasket() {
		String username = "xebia";
		int quantity = 2;

		Product product = getProducts(0);
		assertEquals(2, stockResource(product.getId()).get(Stock.class).getQuantity(), 0);

		String uriBook = rels(product.getLinks()).get(Rels.RELS_BOOK);
		resource(uriBook, ImmutableMap.of("quantity", quantity, "username", username)).post();

		assertEquals(0, stockResource(product.getId()).get(Stock.class).getQuantity());
		assertEquals(quantity, basketResource(username, product.getId()).get(Stock.class).getQuantity());
	}

	@Test
	public void shouldGetBasketWithOneItem() {
		String username = "xebia";
		int quantity = 2;

		Product product = getProducts(0);
		assertEquals(2, stockResource(product.getId()).get(Stock.class).getQuantity());

		String uriBook = rels(product.getLinks()).get(Rels.RELS_BOOK);
		resource(uriBook, ImmutableMap.of("quantity", quantity, "username", username)).post();

		Basket basket = basketResource(username).get(Basket.class);
		Product basketProduct = resource(basket.getStock().get(0).getRelated().getHref()).get(Product.class);
		ReflectionAssert.assertReflectionEquals(product, basketProduct);
	}

	@Test
	public void shouldGetBasketWithTwoItems() {
		String username = "xebia";
		int quantity = 2;

		Product product1 = getProducts(0);
		Product product2 = getProducts(1);

		String uriBook1 = rels(product1.getLinks()).get(Rels.RELS_BOOK);
		resource(uriBook1, ImmutableMap.of("quantity", quantity, "username", username)).post();

		String uriBook2 = rels(product2.getLinks()).get(Rels.RELS_BOOK);
		resource(uriBook2, ImmutableMap.of("quantity", quantity, "username", username)).post();

		Basket basket = basketResource(username).get(Basket.class);
		List<Product> basketProducts = Lists.newArrayList();
		for (Stock stock : basket.getStock())
			basketProducts.add(resource(stock.getRelated().getHref()).get(Product.class));

		ReflectionAssert.assertReflectionEquals(Lists.newArrayList(product1, product2), basketProducts,
				ReflectionComparatorMode.LENIENT_ORDER);
	}

	@Test
	public void shouldBasketPriceBeCorrect() {
		String username = "xebia";
		int quantity = 2;

		Product product1 = getProducts(0);
		Product product2 = getProducts(1);

		String uriBook1 = rels(product1.getLinks()).get(Rels.RELS_BOOK);
		resource(uriBook1, ImmutableMap.of("quantity", quantity, "username", username)).post();

		String uriBook2 = rels(product2.getLinks()).get(Rels.RELS_BOOK);
		resource(uriBook2, ImmutableMap.of("quantity", quantity, "username", username)).post();

		Basket basket = basketResource(username).get(Basket.class);
		Map<Rels, String> basketRels = rels(basket.getLinks());

		Integer price = resource(basketRels.get(Rels.RELS_PRICE), ImmutableMap.of("username", username)).get(
				Price.class).getValue();
		assertEquals(quantity * product1.getPrice() + quantity * product2.getPrice(), price.intValue());
	}

	@Test
	public void shouldPayBasket() {
		String username = "xebia";
		int quantity = 2;

		Product product1 = getProducts(0);
		Product product2 = getProducts(1);

		String uriBook1 = rels(product1.getLinks()).get(Rels.RELS_BOOK);
		resource(uriBook1, ImmutableMap.of("quantity", quantity, "username", username)).post();

		String uriBook2 = rels(product2.getLinks()).get(Rels.RELS_BOOK);
		resource(uriBook2, ImmutableMap.of("quantity", quantity, "username", username)).post();

		Basket basket = basketResource(username).get(Basket.class);
		Map<Rels, String> basketRels = rels(basket.getLinks());

		assertEquals(Status.OK.getStatusCode(), resource(basketRels.get(Rels.RELS_PAYMENT)).post(ClientResponse.class)
				.getStatus());
		assertEquals(0, basketResource(username).get(Basket.class).getStock().size());
	}

	@Test
	public void shouldBanBookingIfStockIsLow() {
		String username = "xebia";
		int quantity = 3;

		Product product = getProducts(0);
		assertEquals(2, stockResource(product.getId()).get(Stock.class).getQuantity());

		String uriBook = rels(product.getLinks()).get(Rels.RELS_BOOK);
		assertEquals(
				Status.PRECONDITION_FAILED.getStatusCode(), //
				resource(uriBook, ImmutableMap.of("quantity", quantity, "username", username)).post(
						ClientResponse.class).getStatus());
	}

	@Test
	public void shouldBanBookingIfStockIsBookedByAnotherOne() {
		ImmutableMap<String, ?> params1 = ImmutableMap.of("quantity", 1, "username", "xebia");
		ImmutableMap<String, ?> params2 = ImmutableMap.of("quantity", 1, "username", "zenika");

		Product product = getProducts(0);
		ClientResponse response = stockResource(product.getId()).get(ClientResponse.class);
		EntityTag eTag = response.getEntityTag();
		assertEquals(2, response.getEntity(Stock.class).getQuantity());

		String uriBook = rels(product.getLinks()).get(Rels.RELS_BOOK);
		assertEquals(Status.OK.getStatusCode(), //
				resource(uriBook, params1).header("If-Match", eTag).post(ClientResponse.class).getStatus());
		assertEquals(Status.PRECONDITION_FAILED.getStatusCode(), //
				resource(uriBook, params2).header("If-Match", eTag).post(ClientResponse.class).getStatus());
	}

	// FIXME some test may fails because of etag: fix that

	/* Helpers */

	private WebResource resource(String path) {
		return createClient().resource(server.uri()).path(path);
	}

	private WebResource productResource() {
		URI uri = UriBuilder.fromPath("resource/product").build();
		return createClient().resource(server.uri()).path(uri.getPath());
	}

	private WebResource productResource(long productId) {
		URI uri = UriBuilder.fromPath("resource/product/{id}").build(productId);
		return createClient().resource(server.uri()).path(uri.getPath());
	}

	private WebResource stockResource(long productId) {
		URI uri = UriBuilder.fromPath("resource/product/{id}/stock").build(productId);
		return createClient().resource(server.uri()).path(uri.getPath());
	}

	private WebResource basketResource(String username) {
		URI uri = UriBuilder.fromPath("resource/basket/{username}").build(username);
		return createClient().resource(server.uri()).path(uri.getPath());
	}

	private WebResource basketResource(String username, long productId) {
		URI uri = UriBuilder.fromPath("resource/basket/{user}/{id}").build(username, productId);
		return createClient().resource(server.uri()).path(uri.getPath());
	}

	private WebResource resource(String href, Map<String, ?> params) {
		URI uri = UriBuilder.fromPath(href).buildFromMap(params);
		return resource(uri.getPath());
	}

	public Product getProducts(int index) {
		return getProducts().get(index);
	}

	public List<Product> getProducts() {
		WebResource resource = createClient().resource(server.uri());
		ClientResponse clientResponse = resource.path("resource/product").type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		return clientResponse.getEntity(new GenericType<List<Product>>() {});
	}

	private Map<Rels, String> rels(List<Link> links) {
		Map<Rels, String> rels = Maps.newHashMap();
		for (Link link : links)
			rels.put(link.getRel(), link.getHref());
		return rels;
	}
	
	private Client createClient() {
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		return Client.create(clientConfig);
	}
}
