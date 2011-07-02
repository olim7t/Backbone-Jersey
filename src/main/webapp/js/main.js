require({ baseUrl:'/js' },
    ["order!//ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js",
     "order!//ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/jquery-ui.min.js",
     "order!//ajax.cdnjs.com/ajax/libs/underscore.js/1.1.6/underscore-min.js",
     "order!//ajax.cdnjs.com/ajax/libs/backbone.js/0.3.3/backbone-min.js",
     "order!/lib/jquery.pubsub.min.js"
    ], 	
    function () {
        require(["product/ProductView",
                 "product/ProductCreationView",
                 "product/BasketView",
                 "product/ProductCollection",
                 "product/BasketModel"
                ], 
                function (ProductView,
        		 	  ProductCreationView,
        		      BasketView,
        		 	  ProductCollection,
        		 	  BasketModel) {
        	var products = new ProductCollection();
			new ProductView({ collection: products });
			new ProductCreationView({ collection: products });
			
			var basket = new BasketModel();
			var basketView = new BasketView({ model: basket, collection: products });

			products.fetch();
			basket.fetch({ success: function() {$.publish('basket-event')}});
        });
    }
);