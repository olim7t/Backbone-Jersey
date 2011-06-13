define(['text!/template/ProductCreationTemplate.html', 'product/StockModel'], function(tmpl, StockModel){
	return Backbone.View.extend({
		initialize: function(){
			this.el.html(_.template(tmpl));
			$('input:submit', this.el).button();
		},
		el: $("#product-creation"),
		events: {
			"click input:submit": "create"
		},
		create: function() {
			var name = $('#name');
			var price = $('#price');
			var quantity = $('#quantity');
			
			this.cleanValidationMark(name);
			this.cleanValidationMark(price);
			this.cleanValidationMark(quantity);
			
			var validName = this.validString(name);
			var validPrice = this.validNumber(price);
			var validQuantity = this.validNumber(quantity);
			
			if (validName && validPrice && validQuantity) {
				this.collection.create({ 
					name: name.val(),
					price: price.val()
				}, { success: function(product) {
					var stock = new StockModel();
					stock.setProduct(product.get('id'));
					stock.set({ quantity: quantity.val() });
					stock.save();
					quantity.val('');
				}});
				
				name.val('');
				price.val('');
			}
		},
		validString: function validate(o) {
			var valid = o.val().length > 0;
			this.validationMark(valid, o);
			return valid;
		},
		validNumber: function validate(o) {
			var i = parseInt(o.val());
			var valid = !isNaN(i) && i > 0;
			this.validationMark(valid, o);
			return valid;
		},
		validationMark: function add(valid, o) {
			if(!valid) 
				o.addClass('ui-state-error');
		},
		cleanValidationMark: function remove(o) {
			o.removeClass('ui-state-error');
		}
	});
});