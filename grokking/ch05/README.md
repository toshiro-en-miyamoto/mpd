# Improving the Design of Actions

## Improving the design by pulling `add_item()` apart

```
|     | function add_item(cart, nm, pr) {
| 1   |   var new_cart = cart.slice();
| 2 3 |   new_cart.push({name: nm, price: pr});
| 4   |   return new_cart;
|     | }
```

1. make a copy of an array
2. build an item object
3. add the item to the copy
4. return the copy

This function knows both the structure of the cart and the structure of the item. We can pull out the item into its own function.

```
| Pulled apart                      | Original
|                                   |
| function make_item(nm, pr) {      |
|   return({name: nm, price: pr});  |
| }                                 |
|                                   |
| function add_item(cart, item) {   | function add_item(cart, nm, pr) {
|   var new_cart = cart.slice();    |   var new_cart = cart.slice();
|   new_cart.push(item);            |   new_cart.push({name: nm, price: pr});
|   return new_cart;                |   return new_cart;
| }                                 | }
|                                   |
| add_item(                         | add_item(
|     shopping_cart,                |     shopping_cart,
|     make_item("shoes", 3.45));    |     "shoes", 3.45);
```

By pulling this function out, [...] the cart and item structures can evolve independently.

## Extracting a copy-on-write pattern

```
| Generic name                         | Specific name
|                                      |
| function add_elem_last(arr, elem) {  | function add_item(cart, item) {
|   var new_array = arr.slice();       |   var new_cart = cart.slice();
|   new_array.push(elem);              |   new_cart.push(item);
|   return new_array;                  |   return new_cart;
| }                                    | }
|                                      |
| function add_item(cart, item) {      |
|   return add_elem_last(cart, item);  |
| }                                    |
```

## Smaller functions and more calculations

```
| A | var shopping_cart = [];
|   |
| A | function add_item_to_cart(name, price) {
|   |   var item = make_cart_item(name, price);
|   |   shopping_cart = add_item(shopping_cart, item);
|   |   var total = calc_total(shopping_cart);
|   |   set_cart_total_dom(total);
|   |   update_shopping_icons(shopping_cart);
|   |   update_tax_dom(total);
|   | }
|   |
| A | function update_shipping_icons(cart) {
|   |   var buttons = get_buy_buttons_dom();
|   |   for (var i = 0; i < buttons.length; i++) {
|   |     var button = buttons[i];
|   |     var item = button.item;
|   |     var new_cart = add_item(cart, item);
|   |     gets_free_shipping(new_cart)
|   |     ? button.show_free_shipping_icon()
|   |     : button.hide_free_shipping_icon();
|   |   }
|   | }
|   |
| A | function update_tax_dom(total) {
|   |   set_tax_dom(calc_tax(total));
|   | }
|   |
| C | function add_element_last(array, elem) {
|   |   var new_array = array.slice();
|   |   new_array.push(elem);
|   |   return new_array;
|   | }
|   |
| C | function add_item(cart, item) {
|   |   return add_element_ast(cart, item);
|   | }
|   |
| C | function make_cart_item(name, price) {
|   |   return(name: name, price: price);
|   | }
|   |
| C | function calc_total(cart) {
|   |   var total = 0;
|   |   for (var i = 0; i < cart.length; i++) {
|   |     var item = cart[i];
|   |     total += item.price;
|   |   }
|   |   return total;
|   | }
|   |
| C | function gets_free_shipping(cart) {
|   |   return calc_total(cart) >= 20;
|   | }
|   |
| C | function calc_tax(amount) {
|   |   return amount * 0.10;
|   | }
```

We see that our actions now don’t need to know the structure of our data.
