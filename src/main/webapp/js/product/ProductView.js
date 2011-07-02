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
			var links = this.collection.get(event.target.id).get('links');
			var quantity = $('input:text#'+event.target.id).attr('value');
			
			var map = {};
			_.each(links, function(link) {
				map[link.rel] = link.href;
			});

			this.template(true);
			var tmpl = _.template(map['RELS_BOOK'], { quantity: quantity, username: "xebia" });
			$.ajax({
					type: 'POST',
					url: tmpl,
					success: function() {$.publish('basket-event')},
					error: function(xhr) { alert(xhr.responseText) }
			});
			this.template(false);
		},
		template: function(custom) {
			var template;
			if(custom)
				template = /\{(.+?)\}/g;
			else
				template = /<%=([\s\S]+?)%>/g;
			_.templateSettings = { interpolate:  template};
		}
	});
});