# First-class functions: Part 2

## One code smell and two refactorings

Code smell: *Implicit argument in function name*

1. There are very similar function implementations.
2. The name of the function indicates the difference in implementation.

Refactoring: *Express implicit argument*

1. Identify the implicit argument in the name of the function.
2. Add explicit argument.
3. Use new argument in body in place of hard0coded value.
4. Update the calling code.

Refactoring: *Replace body with callback*

1. Identify the before, body, after sections.
2. Extract the whole thing into a function.
3. Extract the body into a function passed as an argument to that function.

## Refactoring copy-on-write for arrays

In chapter 6, we developed several copy-on-write routines for arrays. They all followed the basic pattern of make a copy, modify the copy, return the copy.

We can see that they have very similar definitions. The copy, modify, return corresponds naturally to before, body, after.

```javascript
function array_set(array, idx, value) {
    var copy = array.slice();       // before
    copy[idx] = value;              // body
    return copy;                    // after
}

function push(array, elem) {
    var copy = array.slice();
    copy.push(elem);
    return copy;
}

function drop_last(array) {
    var array_copy = array.slice();
    array_copy.pop();
    return array_copy;
}

function drop_first(array) {
    var array_copy = array.slice();
    array_copy.shift();
    return array_copy;
}
```

Our next step is to extract these three sections into a function.

```javascript
function with_array_copy(array) {
    var copy = array.slice();       // before
    copy[idx] = value;              // idx and value are out of scope
    return copy;                    // after
}

function array_set(array, idx, value) {
    return with_array_copy(array);
}
```

Our next step is to extract out the body into a callback.

```javascript
function with_array_copy(array, modify) {
    var copy = array.slice();       // before
    modify(copy);                   // calling the callback
    return copy;                    // after
}

function array_set(array, idx, value) {
    return with_array_copy(array, function(copy) {
        copy[idx] = value;          // body
    });
}

function push(array, elem) {
    return with_array_copy(array, function(copy) {
        copy.push(elem)             // body
    });
}

function drop_last(array) {
    return with_array_copy(array, function(copy) {
        copy.pop();                 // body
    });
}

function drop_first(array) {
    return with_array_copy(array, function(copy) {
        copy.shift();               // body
    });
}
```

Benefits we archived:

1. Standardized the copy-on-write dicipline for arrays
2. Applied the dicipline to new operations
3. Optimiazed sequences of modifications

A series of copy-on-write operations will create a new copy for each operation. That can be slow and hog memory. The following code makes four copies of the array:

```javascript
var a1 = drop_first(array);
var a2 = push(a1, 10);
var a3 = push(a2, 11);
var a4 = array_set(a3, 0, 42);
```

`with_array_copy()` gives us a way to optimize them by making one single copy.

```javascript
var v4 = with_array_copy(array, function(copy) {
    copy.shift();
    copy.push(10);
    copy.push(11);
    copy[0] = 42;
});
```

Here is the code for a couple of copy-on-write implementations for objects:

```javascript
function object_set(object, key, value) {
    var copy = Object.assign({}, object);
    copy[key] = value;
    return copy;
}

function object_delete(object, key) {
    var copy = Object.assign({}, object);
    delete copy[key];
    return copy;
}
```

With `with_object_copy()`, you can re-implement these two object copy-on-write functions.

```javascript
function with_object_copy(object, modify) {
    var copy = Object.assign({}, object);
    modify(copy);
    return copy;
}

function object_set(object, key, value) {
    return with_object_copy(object, function(copy) {
        copy[key] = value;
    });
}

function object_delete(object, key) {
    return with_object_copy(object, function(copy) {
        delete copy[key];
    });
}
```

The try/catch has two parts that vary, the body of the try and the body of the catch. We only let the body of the try vary.

```javascript
try {
    sendEmail();
} catch(error) {
    logToSnapErrors(error);
}
```

With `with_logging()`, you can re-implement the code. 

```javascript
function with_logging(f, error_handler) {
    try {
        f();
    } catch (error) {
        error_handler(error);
    }
}

with_logging(
    function() {
        sendMail()
    },
    function(error) {
        logToSnapErrors(error)
    }
);
```

## Returning functions from functions

```
save_user_data(user);                |  fetch_product(product_id)
```

We need to wrap code in a try/catch.

```
try {                                |  try {
    save_user_data(user);            |      fetch_product(product_id)
} catch (error) {                    |  } catch (error) {
    log_to_snap_errors(error);       |      log_to_snap_errors(error);
}                                    |  }
```

With `with_logging()`,

```javascript
function with_logging(f) {
    try {
        f();
    } catch (error) {
        log_to_snap_errors(error);
    }
}
```

you can wrap code in a try/catch.

```
with_logging(function() {            |  with_logging(function() {
    save_user_data(user)             |      fetch_product(product_id)
});                                  |  });
```

Now we have a standard system. But there are still two problems:

1. We could still forget to log in some places.
2. We still have to manually write this code everywhere.

We can write that, but we want something that will write it for us automatically.

```javascript
function wrap_logging(f) {
    return function(arg) {
        try {
            f(arg);
        } catch (error) {
            log_to_snap_errors(error);
        }
    };
}

var save_user_data_logging = wrap_logging(save_user_data);
var fetch_product_logging  = wrap_logging(fetch_product);

save_user_data_logging(user);
fetch_product_logging(product_id);
```

Let’s look at the before

```
try {                                |  try {
    save_user_data(user);            |      fetch_product(product_id)
} catch (error) {                    |  } catch (error) {
    log_to_snap_errors(error);       |      log_to_snap_errors(error);
}                                    |  }
```

and the after:

```
save_user_data_logging(user);        |  fetch_product_logging(product_id);
```

Returning functions from functions (as `wrap_logging()` does) lets us make *function factories*. They automate the creation of functions and codify a standard.

### Exercise 1

Write a function that transforms the function you pass it into a function that catches and ignores all errors. If there is an error, just return null. Make it work on functions of at least three arguments.

```javascript
function wrap_ignore_errors(code_that_might_throw) {
    return function(arg1, arg2, arg3) {
        try {
            return code_that_might_throw(arg1, arg2, arg3);
        } catch (e) {
            return null;
        }
    }
}
```

### Exercise 2

Write a function called `make_adder()` that makes a function to add a number to another number. For instance,

```javascript
var increment = make_adder(1);
assert.equal(increment(10), 11);

var plus10 = make_adder(10);
assert.equal(plus10(12), 22)

function make_adder(left) {
    return function(right) {
        return left + right;
    };
}
```

## Summary

- Higher-order functions can codify patterns and disciplines that otherwise we would have to maintain manually.
- We can make functions by returning them from higher-order functions.
- Higher-order functions come with a set of tradeoffs. They can remove a lot of duplication, but sometimes they cost readability.
