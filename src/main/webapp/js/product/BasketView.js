define(['text!/template/BasketTemplate.html',
        'text!/template/BasketItemTemplate.html',
        'product/BasketModel'], 
        function(BasketTemplate,
        		 BasketItemTemplate,
        		 BasketModel){
	return Backbone.View.extend({
		el: $('#cart'),
		initialize: function() {
			_.bindAll(this, 'fetch', 'render', 'checkout');
			
			var view = this;
			$.subscribe('basket-event', view.fetch);
			
			this.el.html(BasketTemplate);
			$('input:submit', this.el).button();
		},
		events: {
			"click input:submit": "checkout"
		},
		fetch: function() {
			var view = this;
			this.model.clear();
			this.model.fetch({ success: view.render });
		},
		price: 0,
		render: function() {
			var stocks = this.model.get('stock');
			var view = this;
			var tmpl = '';
			var total = 0;
			
			_.each(stocks, function(stock) {
				var product = view.collection.get(stock.id);
				var price = stock.quantity * product.get('price');
				total += price;
				tmpl += _.template(BasketItemTemplate, { quantity: stock.quantity, name: product.get('name'), price: price });
			});
			
			this.el.html(_.template(BasketTemplate, { total: total }));
			this.price = total;
			
			$('tbody', this.el).html(tmpl);
			$('input:submit', this.el).button();
		},
		checkout: function() {
			var links = this.model.get('links');
			var map = {};
			_.each(links, function(link) {
				map[link.rel] = link.href;
			});
			
			var total = this.price; 
			$.getJSON(map['RELS_PRICE'], function(response) {
				if(response.value == total)
					$.post(map['RELS_PAYMENT'], function() { $.publish('basket-event') });
			});
		}
	});
});
