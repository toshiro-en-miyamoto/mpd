# Functional tools for nested data

We’ve just worked with higher-order functions that operate on data in arrays. It sounds really useful to be able to operate on values stored in objects as well.

## Deriving `update()`

```javascript
function increment_field(item, field) {         // increment
    var value = item[field];
    var new_value = value + 1;                  // increment
    var new_item = object_set(item, field, new_value);
    return new_item;
}

function decrement_field(item, field) {         // decrement
    var value = item[field];
    var new_value = value - 1;                  // decrement
    var new_item = object_set(item, field, new_value);
    return new_item;
}

function object_set(object, key, value) {
    var copy = Object.assign({}, object);
    copy[key] = value;
    return copy;
}
```

Now all of those operations have been condensed into one higher-order function `update()`. You pass in the difference in behavior, which is the operation to perform on the desired field, as a callback.

```javascript
function update(object, key, modify) {
    var value = object[key];
    var new_value = modify(value);
    var new_object = object_set(object, key, new_value);
    return new_object;
}

function increment_field(item, field) {         // increment
    return update(item, field, function(value) {
        return value + 1;                       // increment
    });
}

function decrement_field(item, field) {         // decrement
    return update(item, field, function(value) {
        return value - 1;                       // decrement
    });
}
```

## Functional tool: `update()`

The functional tools we learned in the last chapters (`map()`, `filter()`, and `reduce()`) operated on arrays, but `update()` operates on objects (treated as hash maps).

## Applying `update()` to nested data

```javascript
var shirt = {
    name: "shirt",
    price: 13,
    options: {
        color: "blue",
        size: 3
    }
};

function increment_size(item) {
    var options = item.options;                                 // get
    var size = options.size;                                    // get
    var new_size = increment(size);                             // modify
    var new_options = object_set(options, 'size', new_size);    // set
    var new_item = object_set(item, 'options', new_options);    // set
    return new_item;
}
```

Replace *get*, *modify*, *set* with `update()`.

```javascript
function increment_size(item) {
    var options = item.options;                                 // get
    var new_options = update(options, 'size', increment);       // update
    var new_item = object_set(item, 'options', new_options);    // set
    return new_item;
}
```

Replace *get*, *modify*, *set* with `update()` again.

```javascript
function increment_size(item) {
    return update(item, 'options', function(options) {
        return update(options, 'size', increment);
    });
}
```

We can nest the updates to work on nested objects. As we nest calls to `update()`, we are working on a deeper level of nested objects.

Actually, it’s the same smell twice: referring to an implicit argument in the function name.

```javascript
function increment_size(item) {                         // increment, size
    return update(item, 'options', function(options) {
        return update(options, 'size', increment);      // increment, size
    });
}
```

We’ve got two implicit arguments, so let’s make them explicit.

```javascript
function increment_size(item) {
    return update_option(item, 'size', increment);
}

function update_option(item, option, modify) {
    return update(item, 'options', function(options) {
        return update(options, option, modify);
    });
}
```

## Deriving `update2()`

We finished expressing two implicit arguments. That refactoring revealed a third implicit argument.

```javascript
function update_option(item, option, modify) {              // _option
    return update(item, 'options', function(options) {      // 'options'
        return update(options, option, modify);
    });
}
```

Let’s apply that refactoring a third time.

```javascript
function update2(item, key1, option, modify) {              // nested twice
    return update(item, key1, function(options) {           // key1
        return update(options, option, modify);
    });
}
```

This refactoring will make the function more general, so we’ll change the names to reflect how general it is as we go:

```javascript
function update2(object, key1, key2, modify) {
    return update(object, key1, function(value1) {
        return update(value1, key2, modify);
    });
}

var shirt = {
    name: "shirt",
    price: 13,
    options: {
        color: "blue",
        size: 3
    }
};

function increment_size(item) {
    return update2(item, 'options', 'size', increment);
}
```

We can call `update2()` because it works on any objects nested in objects (values two levels deep). That’s why it needs two keys.

## Visualizing `update2()` on nested objects

We are incrementing the `size` option.

```javascript
return update2(shirt, 'options', 'size', function(size) {
    return size + 1;
});
```

To do so, we need to follow a path through nested objects. Starting with the item (`shirt`), we step into the object at the `options` key, then finally the value at the `size` key. Collectively, the list of keys is called the *path*.

The sequence of keys for locating a value in nested objects is called a path. The path has one key for each level of nesting.

But the item object is inside of the `cart` object.

```javascript
var cart = {
    shirt: {
        name: "shirt",
        price: 13,
        options: {
            color: "blue",
            size: 3
        }
    }
};
```

## Deriving `nestedUpdate()`

```javascript
function update3(object, key1, key2, key3, modify) {
    return update(object, key1, function(value1) {
        return update2(value1, key2, key3, modify);
    });
}

function update4(object, key1, key2, key3, key4, modify) {
    return update(object, key1, function(value1) {
        return update3(value1, key2, key3, key4, modify);
    });
}

function update5(object, key1, key2, key3, key4, ke5, modify) {
    return update(object, key1, function(value1) {
        return update4(value1, key2, key3, key4, key5, modify);
    });
}
```

Before we get asked to derive `update6()` through `update21()`, let’s start working on `nestedUpdate()`, which works with any number of nesting levels.

The pattern is simple: We define `updateX()` as an `updateX-1()` nested inside an `update()`. The `update()` uses the first key, then passes the rest of the keys, in order, and modify to `updateX-1()`.

```javascript
function update2(object, key1, key2, modify) {
    return update(object, key1, function(value1) {
        return update1(value1, key2, modify);
    });
}

function update1(object, key1, modify) {
    return update(object, key1, function(value1) {
        return update0(value1, modify);
    });
}
```

Intuitively, `update0()` means we are nested zero objects deep. There are zero gets and zero sets, just the modify. In other words, we have the value we are looking for, so we should just apply the `modify()` function:

```javascript
function update0(value, modify) {
    return modify(value);
}
```

The `0` in the `update0()` indicates zero keys.

Our code smell has shown up again! We’ve got an *implicit argument in function name*. The numbers in the function name always match the number of arguments.

Let’s look at `update3()` as an example. How do we make the 3 an explicit argument?

```javascript
function update3(object, key1, key2, key3, modify) {    // 3, key1-3
    return update(object, key1, function(value1) {
        return update2(value1, key2, key3, modify);     // 3-1
    });
}
```

We could easily just add an argument called `depth`. The `depth` corresponds to the number of keys:

```javascript
function updateX(object, depth, key1, key2, key3, modify) {
    return update(object, key1, function(value1) {
        return updateX(value1, depth-1, key2, key3, modify);
    });
}
```

We need to maintain the order and the number of keys. That indicates a clear data structure: arrays. What if we pass the keys in as an array? The depth parameter will be the length.

```javascript
function updateX(object, keys, modify) {
    var key1 = keys[0];
    var rest_of_keys = drop_first(keys);
    return update(object, key1, function(value1) {
        return updateX(value1, rest_of_keys, modify);
    });
}
```

This will be able to replace all of the `updateX()` functions except `update0()`, which had a different pattern.

```javascript
function update0(value, modify) {
    return modify(value);
}
```

Well, we have to make a special case for zero. We don’t typically call this `updateX()`. A better name would be `nested_update()`.

```javascript
function nested_update(object, keys, modify) {
    if(keys.length === 0)
        return modify(object);

    var key1 = keys[0];
    var rest_of_keys = drop_first(keys);
    return update(object, key1, function(value1) {
        return nested_update(value1, rest_of_keys, modify);
    });
}
```

A function can call any function it wants to, including itself. If it does call itself, it’s called *recursive*. *Recursion* is the general idea of functions calling themselves.

Recursion takes advantage of the function call stack that keeps track of the argument values and return locations of function calls. In order to do this with a for loop, we would need to manage our own stack.

## The superpower of recursion

We can write `increment_size_by_name()` using `nested_update()`.

```javascript
var cart = {
    shirt: {                        // cart.shirt
        name: "shirt",
        price: 13,
        options: {                  // cart.shirt.options
            color: "blue",
            size: 3                 // cart.shirt.options.size
        }
    }
};

function increment_size_by_name(cart, name) {
    return nested_update(cart, [name, 'options', 'size'], increment);
}

increment_size_by_name(cart, 'shirt');
```
