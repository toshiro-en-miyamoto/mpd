# Extracting calculations from actions

The first thing we should look at is what category each function belongs in. This will give us some idea of our code and how we can improve it.

```
| 1 | A | var cart = [];
| 1 | A | var cart_total = 0;
|   |   | 
|   | A | function add_item_to_cart(name, price) {
| 2 |   |   cart.push({name: name, price: price});
|   |   |   calc_total();
|   |   | }
|   |   | 
|   | A | function update_shopping_icons() {
| 3 |   |   var buy_buttons = get_buy_button_dom();
|   |   |   for (var i = 0; i < buy_buttons.length, i++) {
|   |   |     var button = buy_buttons[i];
|   |   |     var item = button.item;
|   |   |     item.price + cart_total >= 20
| 4 |   |     ? button.show_free_shipping_icon()
| 4 |   |     : button.hide_free_shipping_icon();
|   |   |   }
|   |   | }
|   |   | 
|   | A | function update_tax_dom() {
| 5 |   |   set_tax_dom(cart_total * 0.10);
|   |   | }
|   |   | 
|   | A | function calc_cart_total() {
| 6 |   |   for (var i = 0; i < cart.length; i++) {
|   |   |     cart_total += cart[i].price;
|   |   |   }
|   |   |   set_cart_total_dom();
|   |   |   update_shopping_icons();
|   |   |   update_tax_dom();
|   |   | }
```

1. these globals are mutable: actions
2. modifies a global variable: action
3. reads from the DOM: action
4. modifies the DOM: action
5. modifies the DOM: action
6. modifies a global var: action

All of the code is actions. There are no calculations or data.

## Functions have inputs and outputs

```
|     | var total = 0;
|     | 
| 1   | function add_to_total(amount) {
| 2 3 |   console.log("old total:" + total);
| 4   |   total += amount;
| 5   |   return total;
|     | }
```

1. arguments are explicit inputs
2. printing is an implicit output
3. reading a global is an implicit input
4. modifying a global is an implicit output
5. the return value is an explicit output

*Implicit inputs and outputs* make a function an action. If we eliminate all implicit inputs and outputs from an action, it becomes a calculation.

Functional programmers call these implicit inputs and outputs *side effects*.

## Extracting a calculation from an action

Inside of the original function, we see that there is a block of code that does the work of calculating the total. We will extract this code to its own function before modifying it.

```
| Extracted                         | Original
|                                   |
| function calc_cart_total() {      | function calc_cart_total() {
|   calc_total();                   |   cart_total = 0;
|                                   |   for(var i = 0; i < cart.length; i++) {
|                                   |     var item = cart[i];
|                                   |     cart_total += item.price;
|                                   |   }
|   set_cart_total_dom();           |   set_cart_total_dom();
|   update_shipping_icons();        |   update_shipping_icons();
|   update_tax_dom();               |   update_tax_dom();
| }                                 | }
|
| function calc_total() {
|   cart_total = 0;
|   for(var i = 0; i < cart.length; i++) {
|     var item = cart[i];
|     cart_total += item.price;
|   }
| }
```

The operation we did leaves the code working as it was before. Operations that do that are called *refactorings*. We want to cultivate a sense of code that changes without breaking.

The two outputs were writes to the same global variable `cart_total`. We can replace these both with a single return value. Instead of writing to the global variable, we will write to a local variable `total`, which we return. Then we write to the global variable in the original function using the return value.

```
| Eliminating implicit outputs      | Original
|                                   |
| function calc_cart_total() {      | function calc_cart_total() {
|   cart_total = calc_total();      |   cart_total = 0;
|                                   |   for(var i = 0; i < cart.length; i++) {
|                                   |     var item = cart[i];
|                                   |     cart_total += item.price;
|                                   |   }
|   set_cart_total_dom();           |   set_cart_total_dom();
|   update_shipping_icons();        |   update_shipping_icons();
|   update_tax_dom();               |   update_tax_dom();
| }                                 | }
|
| function calc_total() {
|   var total = 0;
|   for(var i = 0; i < cart.length; i++) {
|     var item = cart[i];
|     total += item.price;
|   }
|   return total;
| }
```

We’ve already eliminated the implicit outputs. The last thing to do is convert the implicit input to an argument.

```
| Eliminating implicit inputs       | Original
|                                   |
| function calc_cart_total() {      | function calc_cart_total() {
|   cart_total = calc_total(cart);  |   cart_total = 0;
|                                   |   for(var i = 0; i < cart.length; i++) {
|                                   |     var item = cart[i];
|                                   |     cart_total += item.price;
|                                   |   }
|   set_cart_total_dom();           |   set_cart_total_dom();
|   update_shipping_icons();        |   update_shipping_icons();
|   update_tax_dom();               |   update_tax_dom();
| }                                 | }
|
| function calc_total(ct) {
|   var total = 0;
|   for(var i = 0; i < ct.length; i++) {
|     var item = ct[i];
|     total += item.price;
|   }
|   return total;
| }
```

At this point, `calc_total()` is a calculation. The only inputs and outputs are arguments and return values. And we’ve successfully extracted a calculation.

## Summary

- Functions that are actions will have implicit inputs and outputs.
- Calculations have no implicit inputs or outputs by definition.
- Shared variables (such as globals) are common implicit inputs and outputs.
- Implicit inputs can often be replaced by arguments.
- Implicit outputs can often be replaced by return values.
