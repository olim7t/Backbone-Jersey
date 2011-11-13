Backbone+Jersey
===============

Integration between a Java server-side using Jersey and a JavaScript client-side using Backbone.
Launch `com.xebia.server.EmbeddedServer.main` (or run `mvn compile exec:java` from the root directory) and open a browser at `http://localhost:8080`

Resources
---------

### Product

/product -> `GET`, `POST`

/product/{id} -> `GET`, `DELETE`

/product/{id}/stock -> `GET`, `POST`

/product/{id}/stock/{quantity}/{username} -> `POST`

### Basket

/basket/{username} -> `GET`, `DELETE`

/basket/{username}/{productid} -> `GET`

/basket/{username}/price -> `GET`

/basket/{username}/payment -> `POST`

Representations
---------------

product:`{id:long, name:string, price:int, links:link[]}`

stock:`{quantity:int, id:long, related:link}`

basket:`{stock:stock, links:link[]}`

price:`{value:int}`

link:`{href:anyURI, rel:rels[rels/book,rels/price,rels/payment,rels/related], type:string}`
