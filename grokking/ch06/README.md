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
|                                                 |
| function remove_item_by_name(cart, name) {      | function remove_item_by_name(cart, name) {
|   var new_cart = cart.slice();                  |
|   var idx = null;                               |   var idx = null;
|   for (var i = 0; i < new_cart.length; i++) {   |   for (var i = 0; i < cart.length; i++) {
|     if (new_cart[i].name == name)               |     if (cart[i].name == name)
|       idx = i;                                  |       idx = i;
|   }                                             |   }
|   if (idx != null)                              |   if (idx != null)
|     new_cart.splice(idx, 1);                    |     cart.splice(idx, 1);
|   return new_cart;                              |
| }                                               | }
|                                                 |
| function delete_handler(name) {                 | function delete_handler(name) {
|   shopping_cart =                               |
|     remove_item_by_name(shopping_cart, name);   |   remove_item_by_name(shopping_cart, name);
|   ...                                           |   ...
| }                                               | }
```

## These copy-on-write operations are generalizable

```
| Copy-on-write using splice()                 | Previous copy-on-write
|                                              |
| function remove_item(array, idx, count) {    | function remove_item(array, idx, count) {
|   var copy = array.slice();                  |
|   copy.splice(idx, count);                   |   array.splice(idx, count);
|   return copy;                               |
| }                                            | }
|                                              |
| function remove_item_by_name(cart, name) {   | function remove_item_by_name(cart, name) {
|                                              |   var new_cart = cart.slice();
|   var idx = null;                            |   var idx = null;
|   for (var i = 0; i < cart.length; i++) {    |   for (var i = 0; i < new_cart.length; i++) {
|     if (cart[i].name == name)                |     if (new_cart[i].name == name)
|       idx = i;                               |       idx = i;
|   }                                          |   }
|   if (idx != null)                           |   if (idx != null)
|     return remove_items(cart, idx, 1);       |     new_cart.splice(idx, 1);
|   return cart;                               |   return new_cart;
| }                                            | }
```

## JavaScript arrays at a glance

*Arrays* in JavaScript represent ordered collections of values. They are heterogeneous, meaning they can have values of different types in them at the same time. [...] You can extend and shrink them, unlike arrays in Java or C.

Suppose you have arrays:

```javascript
var array = [1, 2, 3, 4];
var arr = [1, 2, 3, 4, 5, 6];
```

then

| What you want             | Operation             | Eval              | `array`
|---------------------------|-----------------------|-------------------|----
| lookup by index           | `array[2]`            | `2`               | `[1, 2, 3, 4]`
| set an element            | `array[2] = "abc"`    | `"abc"`           | `[1, "abc", 3, 4]`
| length                    | `array.length`        | `4`               | `[1, 2, 3, 4]`
| add to the end            | `array.push(10)`      | `5`               | `[1, 2, 3, 4, 10]`
| remove from the end       | `array.pop()`         | `4`               | `[1, 2, 3]`
| add to the front          | `array.unshift(10)`   | `5`               | `[10, 1, 2, 3, 4]`
| remove from the front     | `array.shift()`       | `1`               | `[2, 3, 4]`
| copy an array             | `array.slice()`       | `[1, 2, 3, 4]`    | `[1, 2, 3, 4]`
| remove items              | `arr.splice(2, 3)`    | `[3, 4, 5]`       | `[1, 2, 6]`

## What to do if an operation is a read and a write

Sometimes a function plays two roles at the same time: It modifies a value and it returns a value. The `.shift()` method is a good example.

In copy-on-write, we are essentially converting a write to a read, which means we need to return a value. But `.shift()` is already a read, so it already has a return value.

How can you convert this to a copy-on-write?

1. Split the function into read and write.
2. Return two values from the function.

## Splitting a function that does a read and write

Splitting the read from the write is the preferred approach because it gives us all of the pieces we need. We can use them separately or together. Before, we were forced to use them together. Now we have the choice.

```
| Copy-on-write                     | Splitting
|                                   |
| function first_elem(array) {      | function first_elem(array) {
|   return array[0];                |   return array[0];
| }                                 | }
|                                   |
| function drop_first(array) {      | function drop_first(array) {
|   var copy = array.slice();       |
|   copy.shift();                   |   array.shift();
|   return copy;                    |
| }                                 | }
```

## Returning two values from one function

In this case, we need to convert the `shift()` function we’ve written to make a copy, modify the copy, and return both the first element and the modified copy.

```
| Copy-on-write                     | Wrapping
|                                   |
| function shift(array) {           | function shift(array) {
|   var copy = array.slice();       |
|   var first = copy.shift();       |
|   return {                        |
|     first: first,                 |   return array.shift();
|     array: copy                   |
|   };                              |
| }                                 | }
```

Another option is to use the approach we took on the previous page and combine the two return values into an object:

```
| Copy-on-write combined            | Wrapping
|                                   |
| function shift(array) {           | function shift(array) {
|   return {                        |
|     first: first_elem(array),     |   return array.shift();
|     array: drop_first(array)      |
|   };                              |
| }                                 | }
```

We just wrote copy-on-write versions of the `.shift()` method on arrays. Arrays also have a `.pop()` method, which removes the last item in the array and returns it.

```
| Copy-on-write                     | Splitting
|                                   |
| function last_elem(array) {       | function last_elem(array) {
|   return array[array.length - 1]; |   return array[array.length - 1];
| }                                 | }
|                                   |
| function drop_last(array) {       | function drop_last(array) {
|   var copy = array.slice();       |
|   copy.pop();                     |   array.pop();
|   return copy;                    |
| }                                 | }
```

```
| Copy-on-write                     | Wrapping
|                                   |
| function pop(array) {             | function pop(array) {
|   var copy = array.slice();       |
|   var last = copy.pop();          |
|   return {                        |
|     last: last,                   |   return array.pop();
|     array: copy                   |
|   };                              |
| }                                 | }
```

```
| Copy-on-write combined            | Wrapping
|                                   |
| function pop(array) {             | function pop(array) {
|   return {                        |
|     last: last_elem(array),       |   return array.pop();
|     array: drop_last(array)       |
|   };                              |
| }                                 | }
```

Write a copy-on-write version of `.push()`, the array method. Remember, `.push()` adds one element to the end of an array.

```
| Copy-on-write                     | Splitting
|                                   |
| function add_last(array, elem) {  | function add_last(array, elem) {
|   var copy = array.slice();       |
|   copy.push(elem);                |   return array.push(elem);
|   return copy;                    |
| }                                 | }
```

```
| Copy-on-write                     | Wrapping
|                                   |
| function push(array, elem) {      | function push(array, elem) {
|   var copy = array.slice();       |
|   copy.push(elem);                |
|   return {                        |
|     length: copy.length,          |   return array.push(elem);
|     array: copy                   |
|   };                              |
| }                                 | }
```

```
| Copy-on-write combined                | Wrapping
|                                       |
| function push(array, elem) {          | function push(array, elem) {
|   var copy = add_last(array, elem);   |
|   return {                            |
|     length: copy.length,              |   return array.push(elem);
|     array: copy                       |
|   };                                  |
| }                                     | }
```

Write a function `array_set(array, idx, value)` that is a copy-on-write version of the array assignment operator. Example: `a[15] = 2;`, which is evaluated as `2`.

```
| Copy-on-write                             | Splitting
|                                           |
| function array_set(array, idx, value) {   | function array_set(array, idx, value) {
|   var copy = array.slice();               |
|   copy[idx] == value;                     |   return array[idx] == value;
|   return copy;                            |
| }                                         | }
```

```
| Copy-on-write combined                    | Wrapping
|                                           |
| function array_set(array, idx, value) {   | function array_set(array, idx, value) {
|   var copy = array.slice();               |
|   copy[idx] == value;                     |   array[idx] == value;
|   return {                                |
|     value: copy[idx],                     |   return array[idx];
|     array: copy                           |
|   };                                      |
| }                                         | }
```

## Reads to immutable data structures are calculations

> Reads to mutable data are actions

If we read from a mutable value, we could get a different answer each time we read it, so reading mutable data is an action.

> Writes make a given piece of data mutable

Writes modify data, so they are what make the data mutable.

> If there are no writes to a piece of data, it is immutable

If we get rid of all of the writes by converting them to reads, the data won’t ever change after it is created. That means it’s immutable.

> Reads to immutable data structures are calculations

Once we do make the data immutable, all of those reads become calculations.

> Converting writes to reads makes more code calculations

The more data structures we treat as immutable, the more code we have in calculations and the less we have in actions.

## Copy-on-write operations on objects

We need a way to set the price on a shopping cart item, which is represented with an object.

If we copy all of the keys and values into an empty object, we’ve effectively made a copy. This method is called `Object.assign()`.

```
| Copy-on-write                             | Original
|                                           |
| function set_price(item, new_price) {     | function set_price(item, new_price) {
|   var copy = Object.assign({}, item);     |
|   copy.price = new_price;                 |   item.price = new_price
|   return copy;                            |
| }                                         | }
```

## JavaScript objects at a glance

JavaScript’s Objects are very much like hash maps or associative arrays that you find in other languages. Objects in JavaScript are collections of key/value pairs, where the keys are unique. The keys are always strings, but the values can be any type. Here are the operations we will use in our examples:

Suppose you have an object:

```javascript
var o = {a: 1, b: 2};
```

then

| What you want             | Operation                 | Eval              | `o`
|---------------------------|---------------------------|-------------------|----
| lookup by `[key]`         | `o["a"]`                  | `1`               | `{a: 1, b: 2}`
| lookup by `.key`          | `o.a`                     | `1`               | `{a: 1, b: 2}`
| set value for `[key]`     | `o["a"] = 7`              | `7`               | `{a: 7, b: 2}`
| set value for `.key`      | `o.c = 10`                | `10`              | `{a: 7, b: 2, c: 10}`
| remove a key/value pair   | `delete o["a"]`           | `true`            | `{b: 2}` 
| copy an object            | `Object.assign({}, o)`    | `{a: 1, b: 2}`    | `{a: 1, b: 2}`
| list the keys             | `Object.keys(o)`          | `["a", "b"]`      | `{a: 1, b: 2}`


Write a function `object_set()` that is a copy-on-write version of the object assignment operator. Example: `o["price"] = 37;`

```
| Copy-on-write                                 | Wrapping
|                                               |
| function object_set(object, key, value) {     | function object_set(object, key, value) {
|   var copy = Object.assign({}, object);       |
|   copy[key] = value;                          |   return object[key] = value;
|   return copy;                                |
| }                                             | }
```

Write a copy-on-write version of the `delete` operator, which removes a key from an object.
Example: `delete o["a"]`.

```
| Copy-on-write combined                        | Wrapping
|                                               |
| function object_del(object, key) {            | function object_del(object, key) {
|   var copy = Object.assign({}, object);       |
|   var eval = delete copy[key];                |   delete object[key];
|   return {                                    |
|     eval: eval,                               |
|     result: copy                              |   return object;
|   };                                          |
| }                                             | }
```

## What gets copied?

```
|   | shopping_cart = set_price_by_name(shopping_cart, "t-shirt", 13);
|   | 
|   | function set_price_by_name(cart, name, price) {
| 1 |   var copy = cart.slice();
|   |   for (var i = 0; i < copy.length; i++) {
|   |     if (copy[i].name == name)
| 2 |       copy[i] = set_price(copy[i].price);
|   |   }
|   |   return copy;
|   | }
|   | 
| 3 | function set_price(item, new_price) {
|   |   var copy = Object.assign({}, item);
|   |   copy.price = new_price;
|   |   return copy;
|   | }
```

1. copy the array `shopping_cart`
2. we call `set_price()` only once, when the loop find the t-shirt
3. copy the object

Let’s do a quick vocabulary review of some words we’ve already seen:

- *Nested data*: Data structures inside data structures; we can talk about the inner data structure and the top-level data structure
- *Shallow copy*: Copying only the top-level data structure in nested data
- *Structural sharing*: Two nested data structures referencing the same inner data structure

## Visualizing shallow copies and structural sharing

We started out with a shopping cart (one array) with three items (three objects). That’s four pieces of data total.

```
| Addr | Object
|      |
|  r1  | [ o1, o2, o3 ]
|  o1  | {name: "shoes", price: 10}
|  o2  | {name: "socks", price: 3}
|  o3  | {name: "t-shirt", price 7}
```

We then made a shallow copy of the shopping cart: `| 1 | var copy = cart.slice();`.

```
| Addr | Object
|      |
|  r1  | [ o1, o2, o3 ]
|  o1  | {name: "shoes", price: 10}
|  o2  | {name: "socks", price: 3}
|  o3  | {name: "t-shirt", price 7}
|  r2  | [ o1, o2, o3 ]
```

The loop eventually found the t-shirt and called `set_price()` on it.

```
| 3 | function set_price(item, new_price) {
|   |   var copy = Object.assign({}, item);
|   |   copy.price = new_price;
|   |   return copy;
|   | }
```

That function created a shallow copy of the t-shirt Object and changed the price to `13`.

```
| Addr | Object
|      |
|  r1  | [ o1, o2, o3 ]
|  o1  | {name: "shoes", price: 10}
|  o2  | {name: "socks", price: 3}
|  o3  | {name: "t-shirt", price 7}
|  r2  | [ o1, o2, o3 ]
|  o4  | {name: "t-shirt", price 13}
```

`set_price()` returned that copy, and `set_price_by_name()` assigned it in the array in place of the original t-shirt.

```
|   | function set_price_by_name(cart, name, price) {
| 1 |   var copy = cart.slice();
|   |   for (var i = 0; i < copy.length; i++) {
|   |     if (copy[i].name == name)
| 2 |       copy[i] = set_price(copy[i].price);
|   |   }
|   |   return copy;
|   | }
```

Although we had four pieces of data at the start (one array `r1` and three objects: `o1`, `o2`, and `o3`), only two of them (one array `r1 -> r2` and one object `o3 -> o4`) had to be copied. The other objects (`o2` and `o3`) weren’t modified so we didn’t copy them.

```
| Addr | Object
|      |
|  r1  | [ o1, o2, o3 ]
|  o1  | {name: "shoes", price: 10}
|  o2  | {name: "socks", price: 3}
|  o3  | {name: "t-shirt", price 7}
|  r2  | [ o1, o2, o4 ]
|  o4  | {name: "t-shirt", price 13}
```

The original array and the copy are both pointing to everything that hasn’t changed. That’s the *structural sharing* that we’ve talked about before.

## Conclusion

In this chapter we learned the ins and outs of the copy-on-write discipline. Although it’s the same discipline you find in languages like Clojure and Haskell, in JavaScript you have to do all the work yourself. That’s why it’s convenient to wrap it up with some utility functions that handle everything for you. If you stick with those wrapper functions, you’ll be fine. Sticking with it is why it’s called a *discipline*.
