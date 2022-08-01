# Staying immutable in a mutable language

Some of our operations are reads. [...] The rest of our operations are writes. They modify the data in some way. These will require a discipline, because we don’t want to modify any of the values that may be in use somewhere else.

The discipline we’ll use is called *copy-on-write*. It’s the same discipline used in languages like Haskell and Clojure. [...] Because we’re in JavaScript, mutable data is the default, so we the programmers have to apply the discipline ourselves explicitly in the code.

## The three steps of the copy-on-write discipline

The copy-on-write discipline is just three steps in your code. If you implement these steps, you are doing copy-on-write.

1. Make a copy.
2. Modify the copy (as much as you want!).
3. Return the copy.

## Converting a write to a read with copy-on-write

What does `array.splice()` do?

```javascript
array.splice(index, 1);
```

The call remove `1` item at the `index`.

```
| Copy-on-write version                           | Original
| |
| function remove_item_by_name(cart, name) {      | function remove_item_by_name(cart, name) {
|   var new_cart = cart.slice();                  |
|   var idx = null;                               |   var idx = null;
|   for (var i = 0; i < new_cart.length; i++) {   |   for (var i = 0; i < cart.length; i++) {
|     if (new_cart[i].name == name)               |     if (cart[i].name == name)
|       idx = i;                                  |       idx = i;
|   }                                             |   }
|   if (idx != null)                              |   if (idx != null)
|     new_cart.splice(idx, 1;)                    |     cart.splice(idx, 1;)
|   return new_cart;                              |
| }                                               | }
| |
| function delete_handler(name) {                 | function delete_handler(name) {
|   shopping_cart =                               |
|     remove_item_by_name(shopping_cart, name);   |   remove_item_by_name(shopping_cart, name);
|   ...                                           |   ...
| }                                               | }
```



