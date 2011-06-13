define(['text!/template/ProductTemplate.html'], function(Template){
	return Backbone.View.extend({
		initialize: function(){
			_.bindAll(this, 'render');
			this.collection.bind('add', this.render);
			this.collection.bind('refresh', this.render);
		},
		el: $("#product-list"),
		render: function() {
			_.templateSettings = { interpolate : /\{\{(.+?)\}\}/g };
			
			var tmpl = '';
			this.collection.each(function(product) {
				tmpl += _.template(Template, { product: product });
			});
			this.el.html(tmpl);
			$('input:submit', this.el).button();
		},
		events: {
			"click input:submit": "book"
		},
		book: function(event) {
			_.templateSettings = { interpolate : /\{(.+?)\}/g };
			var links = this.collection.get(event.target.id).get('links');
			
			var map = new Object();
			_.each(links, function(link) {
				map[link.rel] = link.href;
			});
			var quantity = $('input:text#'+event.target.id)[0].value;
			var tmpl = _.template(map['RELS_BOOK'], { quantity: quantity, username: "xebia" });
			$.ajax({
					type: 'POST',
					url: tmpl,
					success: function() {$.publish('basket-event')},
					error: function(xhr) { alert(xhr.responseText) }
			});
		}
	});
});