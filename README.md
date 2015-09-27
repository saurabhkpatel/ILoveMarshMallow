# ILoveMarshMallow
This is android application challenge.


**Application Description**

* Accepts a search query from the user and makes a request to the Zappos using API.
* Parse Json response and get the results of the search and displays them RecyclerView.
* Opens a product's detailed page when a user selects any item from displayed results.
* After open product detail page, it can be shared via share intent to a friend who has your app running on their      phone such that they are able to view the same product.


**API**

1)  The following URL/endpoint/web service can be used to get back search results.
https://zappos.amazon.com/mobileapi/v1/search?term= <?>

Example:
https://zappos.amazon.com/mobileapi/v1/search?term=adidas

2)  Product Information endpoint - 
Use the asin that is returned from the search results to load product information
https://zappos.amazon.com/mobileapi/v1/product/asin/?

Example:
https://zappos.amazon.com/mobileapi/v1/product/asin/B00LLS8QV0
