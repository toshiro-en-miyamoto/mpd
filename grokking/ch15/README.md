# Isolating timelines

Here is the relevant code from the add-to-cart buttons:

```javascript
function add_item_to_cart(name, price, quantity) {
    cart = add_item(cart, name, price, quantity);
    calc_cart_total();
}

function calc_cart_total() {
    total = 0;
    cost_ajax(cart, function(cost) {
        total += cost;
        shipping_ajax(cart, function(shipping) {
            total += shipping;
            update_total_dom(total);
        });
    });
}
```

Here's the timeline diagram - two clow clicks, where the right answer `14` gets written to the DOM:

```
+----------------+
| Read cart      |                          cart is empty
| Write cart     |                          cart has one shoe
| Write total=0  |                          total = 0
| Read cart      |                          cart has one shoe
| cost_ajax()    |
+----------------+
        +-------------------+
+----------------+          |
| Read total     |          |               total = 0
| Write total    |          |               total = 6
| Read cart      |          |               cart has one shoe
| shipping_ajx() |          |
+----------------+          |
+----------------+          |
| Read total     |          |               total = 6
| Write total    |          |               total = 8
| Read total     |          |               total = 8
| update DOM     |          |
+----------------+          |
                    +----------------+
                    | Read cart      |      cart has one shoe
                    | Write cart     |      cart has two shoes
                    | Write total=0  |      total = 0
                    | Read cart      |      cart has two shoes
                    | cost_ajax()    |
                    +----------------+
                    +----------------+
                    | Read total     |      total = 0
                    | Write total    |      total = 12
                    | Read cart      |      cart has two shoes
                    | shipping_ajx() |
                    +----------------+
                    +----------------+
                    | Read total     |      total = 12
                    | Write total    |      total = 14
                    | Read total     |      total = 14
                    | update DOM     |
                    +----------------+
```

## Two fast clicks can get the wrong result

```
+----------------+
| Read cart      |                          cart is empty
| Write cart     |                          cart has one shoe
| Write total=0  |                          total = 0
| Read cart      |                          cart has one shoe
| cost_ajax()    |
+----------------+
        +-------------------+
+----------------+          |
| Read total     |          |               total = 0
| Write total    |          |               total = 6
| Read cart      |          |               cart has one shoe
| shipping_ajx() |          |
+----------------+          |
                    +----------------+
                    | Read cart      |      cart has one shoe
                    | Write cart     |      cart has two shoes
                    | Write total=0  |      total = 0
                    | Read cart      |      cart has two shoes
                    | cost_ajax()    |
                    +----------------+
                    +----------------+
                    | Read total     |      total = 0
                    | Write total    |      total = 12
                    | Read cart      |      cart has two shoes
                    | shipping_ajx() |
                    +----------------+
                    +----------------+
                    | Read total     |      total = 12
                    | Write total    |      total = 14
                    | Read total     |      total = 14
                    | update DOM     |
                    +----------------+
+----------------+
| Read total     |                          total = 14  >> the bug
| Write total    |                          total = 16
| Read total     |                          total = 16
| update DOM     |
+----------------+
```

These two relatively short timelines can generate 10 possible orderings. Which ones are correct? Which ones are incorrect? We could do the work and trace through them, but most timelines are much longer. They can generate hundreds, thousands, or millions of possible orderings. Looking at each one of them is just not possible. We need a better way to guarantee that our code will work.

## Converting a global variable to a local one

There is no reason to use a global variable for the total. The easiest improvement is to use a local variable instead.

```javascript
function add_item_to_cart(name, price, quantity) {
    cart = add_item(cart, name, price, quantity);
    calc_cart_total();
}

function calc_cart_total() {
    var total = 0;                                      // now local
    cost_ajax(cart, function(cost) {
        total += cost;
        shipping_ajax(cart, function(shipping) {
            total += shipping;
            update_total_dom(total);
        });
    });
}
```

Now reads and writes to `total` doesn't have an effect outside of the function, so they are not actions. Only actions need to appear in timelines.

```
+----------------+
| Read cart      |                          cart is empty
| Write cart     |                          cart has one shoe
| Read cart      |                          cart has one shoe
| cost_ajax()    |
+----------------+
        +-------------------+
+----------------+          |
| Read cart      |          |               cart has one shoe
| shipping_ajx() |          |
+----------------+          |
                    +----------------+
                    | Read cart      |      cart has one shoe
                    | Write cart     |      cart has two shoes
                    | Read cart      |      cart has two shoes
                    | cost_ajax()    |
                    +----------------+
                    +----------------+
                    | Read cart      |      cart has two shoes
                    | shipping_ajx() |
                    +----------------+
                    +----------------+
                    | update DOM     |
                    +----------------+
+----------------+
| update DOM     |
+----------------+
```

## Converting a global variable to an argument

This timeline uses the `cart` global variable as an implicit input. We can eliminate this implicit input and make the timelines share less in one go!


```javascript
function add_item_to_cart(name, price, quantity) {
    cart = add_item(cart, name, price, quantity);       // still global
    calc_cart_total(cart);                              // passing the global
}

function calc_cart_total(cart) {                        // now an argument
    var total = 0;                                      // now local
    cost_ajax(cart, function(cost) {
        total += cost;
        shipping_ajax(cart, function(shipping) {
            total += shipping;
            update_total_dom(total);
        });
    });
}
```

We still have the one step that uses the global variable cart, but remember, the second timeline is constrained to run after the first step (hence the dotted line), so these first steps that use the cart will always run in order. They can’t interfere with each other.

```
+----------------+
| Read cart      |                          cart is empty
| Write cart     |                          cart has one shoe
| Read cart      |                          cart has one shoe
| cost_ajax()    |
+----------------+
        +-------------------+
+----------------+          |
| shipping_ajx() |          |
+----------------+          |
                    +----------------+
                    | Read cart      |      cart has one shoe
                    | Write cart     |      cart has two shoes
                    | Read cart      |      cart has two shoes
                    | cost_ajax()    |
                    +----------------+
                    +----------------+
                    | shipping_ajx() |
                    +----------------+
                    +----------------+
                    | update DOM     |
                    +----------------+
+----------------+
| update DOM     |
+----------------+
```

## Making our code more reusable

Accounting wants to be able to use `calc_cart_total()` without modifying a DOM. They want the total that is calculated as a number they can use in other calculations, not as an update to the DOM.

But we can’t return the total as a return value from `calc_cart_total()`. It’s not available until the two asynchronous calls (`cost_ajax()` and `shipping_ajax()`) are completed.

```javascript
function add_item_to_cart(name, price, quantity) {
    cart = add_item(cart, name, price, quantity);
    calc_cart_total(cart);
}

function calc_cart_total(cart) {
    var total = 0;
    cost_ajax(cart, function(cost) {
        total += cost;
        shipping_ajax(cart, function(shipping) {
            total += shipping;
            update_total_dom(total);
        });
    });
}
```

Since we can’t return the value we want, we’ll have to pass it to a callback function. At the moment, after we finish calculating the total, we pass total to `update_total_dom()`.

```javascript
function add_item_to_cart(name, price, quantity) {
    cart = add_item(cart, name, price, quantity);
    calc_cart_total(cart, update_total_dom);        // passing a callback
}

function calc_cart_total(cart, callback) {
    var total = 0;
    cost_ajax(cart, function(cost) {
        total += cost;
        shipping_ajax(cart, function(shipping) {
            total += shipping;
            callback(total);                        // calling the callback
        });
    });
}
```

Now we have a way of getting the total once it is completely calculated. We can do what we want with it, including writing it to the DOM or using it for accounting purposes.

We can’t return values from asynchronous calls. Asynchronous calls return immediately, but the value won’t be generated until later, when the callback is called. You can’t get a value out in the normal way, as you would with synchronous functions.

The way to get a value out in asynchronous calls is with a callback. You pass a callback as an argument, and you call that callback with the value you need. This is standard JavaScript asynchronous programming.

