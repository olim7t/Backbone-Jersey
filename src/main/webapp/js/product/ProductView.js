define(['text!/template/ProductTemplate.html'], function(tmpl){
	return Backbone.View.extend({
		initialize: function(){
			_.bindAll(this, 'render');
			this.collection.bind('add', this.render);
			this.collection.bind('refresh', this.render);
		},
		el: $("#product-list"),
		render: function() {
			var template = '';
			this.collection.each(function(product) {
				template += _.template(tmpl, { product: product });
			});
			this.el.html(template);
			$('input:submit', this.el).button();
		},
		events: {
			"click input:submit": "book"
		},
		book: function(event) {
			var product_id = $(event.target).data('id');
			var quantity = $('input[type=text][data-id="'+product_id+'"]').val();
			
			var links = this.collection.get(product_id).get('links');
			
			var map = {};
			_.each(links, function(link) {
				map[link.rel] = link.href;
			});

			$.ajax({
					type: 'POST',
					url: map['RELS_BOOK'].replace('{quantity}', quantity).replace('{username}', "xebia"),
					success: function() {$.publish('basket-event')},
					error: function(xhr) { alert(xhr.responseText) }
			});
		}
	});
});