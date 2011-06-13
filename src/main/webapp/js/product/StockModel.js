define([], function(){
	return Backbone.Model.extend({
		url: this.url,
 		setProduct: function(id) {
            this.url = '/resource/product/'+id+'/stock';
 	    }
	});
});
