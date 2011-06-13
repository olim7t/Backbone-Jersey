define([ "product/ProductView",
         "product/ProductCreationView",
         "product/BasketView",
         "product/ProductCollection",
         "product/BasketModel"
         ], function (ProductView,
        		 	  ProductCreationView,
        		      BasketView,
        		 	  ProductCollection,
        		 	  BasketModel
        		 	  ) {
	return {
		start: function() {
			var products = new ProductCollection();
			new ProductView({ collection: products });
			new ProductCreationView({ collection: products });
			
			var basket = new BasketModel();
			var basketView = new BasketView({ model: basket, collection: products });

			products.fetch();
			basket.fetch({ success: function() {$.publish('basket-event')}});
			return {};
		}
	}
});
