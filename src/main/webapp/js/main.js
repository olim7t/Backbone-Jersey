require(
    {
        baseUrl:'/js'
    },
    [
        "order!http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js",
        "order!http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/jquery-ui.min.js",
        "order!http://ajax.cdnjs.com/ajax/libs/underscore.js/1.1.6/underscore-min.js",
        "order!http://ajax.cdnjs.com/ajax/libs/backbone.js/0.3.3/backbone-min.js",
        "order!/lib/jquery.pubsub.min.js"
    ], 
    function () {
        require(["app"], function (app) {
            app.start();
        });
    }
);