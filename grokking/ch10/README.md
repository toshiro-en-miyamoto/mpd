# First-class functions: Part 1

## Code smell: Implicit argument in function name

There are two characteristics to the *implicit argument in function name smell*:

1. Very similar function implementations
2. Name of function indicates the difference in implementation

The function name difference is an implicit argument.

## Refactoring: Express implicit argument

We can apply the refactoring called *express implicit argument* whenever we have an implicit argument as part of the name of the function. The basic idea is to turn the implicit argument into an explicit argument.

Previously, the field name (of the shipping cart, such as `price` and `quantity`) was never exposed to the API clients except implicitly as part of the function names.

```javascript
function setPriceByName   (cart, name, price) { ... }
function setQuantityByName(cart, name, quant) { ... }
function setShippingByName(cart, name, ship ) { ... }
function setTaxByName     (cart, name, tax  ) { ... }

cart = setPriceByName   (cart, "shoe", 13);
cart = setQuantityByName(cart, "shoe", 3);
cart = setShippingByName(cart, "shoe", 0);
cart = setTaxByName     (cart, "shoe", 2.34);
```

Now, it is a value (a string in this case) that can be passed as an argument but also stored in a variable or in an array. That’s what we mean by *first-class*.

```javascript
function setFieldByName(cart, name, field, value) { ... }

cart = setFieldByName(cart, "shoe", 'price',    13);
cart = setFieldByName(cart, "shoe", 'quantity', 3);
cart = setFieldByName(cart, "shoe", 'shipping', 0);
cart = setFieldByName(cart, "shoe", 'tax',      2.34);
```

A *first-class value* can be used just like all of the other values in your language.

## Recognize what is and what isn’t first-class

Think about what you can do with a number in JavaScript.

- You can pass it to a function.
- You can return it from a function.
- You can store it in a variable.
- You can make it an item in an array or a value in an object.
- And you can do the same with strings, booleans, arrays and objects.

In JavaScript, like many languages, you can also do all of those things with functions (which we’ll see shortly). These values are *first-class* because you can do all of those things with them.

But the JavaScript language has a lot of things that are not first-class values. For instance, there is no way to refer to the `+` operator as a value you can assign to a variable. Nor can you pass `*` to a function. The arithmetic operators are *not first-class* in JavaScript.

And there’s more! What is the value of an `if` keyword? Or the `for` keyword? They don’t have values in JavaScript. That’s what we mean when we say they’re *not first-class*.

## For loop example: Eating and cleaning up

Let’s look at two typical for loops that iterate through arrays. The first one has us prepare and eat our food. The second one has us clean all the dirty dishes.

```javascript
for(var i = 0; i < foods.length; i++) {
    var food = foods[i];
    cook(food);
    eat(food);
}

for(var i = 0; i < dishes.length; i++) {
    var dish = dishes[i];
    wash(dish);
    dry(dish);
    putAway(dish);
}
```

The `forEach()` takes an array and a function as arguments. Because it takes a function as an argument, it is a `higher-order function`.

> Higher-order functions take other functions as arguments or return functions as their return values.

```javascript
function cookAndEat(food) {
    cook(food);
    eat(food);
}

function clean(dish) {
    wash(dish);
    dry(dish);
    putAway(dish);
}

function forEach(array, f) {
    for(var i = 0; i < array.length; i++) {
        var item = array[i];
        f(item);
    }
}

forEach(foods, cookAndEat);
forEach(dishes, clean);
```

## Refactoring: Replace body with callback

The trick is to identify the pattern of before-body-after.

```javascript
try {                               // before
    save_user_data(user);           // body
} catch (error) {                   // after
    log_to_snap_errors(error);      // after
}                                   // after

try {                               // before
    fetach_product(product_id);     // body
} catch (error) {                   // after
    log_to_snap_errors(error);      // after
}                                   // after
```

The before and after sections don’t change with each instance. Both of the pieces of code have the exact same code in the before and after sections. However, they do have a part between the before and after sections that is different (the body section). We need to be able to vary that while reusing the before and after sections.

By replacing body with callback:

```javascript
function with_logging(f) {
    try {
        f();
    } catch (error) {
        log_to_snap_errors(error);
    }
}

with_logging(function() {
    save_user_data(user);
});


with_logging(function() {
    fetch_product(product_id);
});
```
