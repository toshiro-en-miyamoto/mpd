# Functional Iteration

It's a for loop.

```javascript
function emails_for_customers(customers, goods, bests) {
    var emails = [];
    for (var i = 0; i < customers.length; i++) {
        var customer = customers[i];
        var email = email_for_customer(customer, goods, bests);
        emails.push(email);
    }
    return emails;
}
```

Let's convert this for loop to a `for_each()`.

```javascript
function for_each(array, work_with) {
    for(var i = 0; i < array.length; i++) {
        var item = array[i];
        work_with(item);
    }
}

function emails_for_customers(customers, goods, bests) {
    var emails = [];                                                
    for_each(customers, function(customer) {
        var email = email_for_customer(customer, goods, bests);
        emails.push(email);
    });
    return emails;
}
```

## Deriving `map()` from examples

Let’s look at some of the functions [...] that match this same pattern:

```javascript
function emails_for_customers(customers, goods, bests) {
    var emails = [];                                                // before
    for_each(customers, function(customer) {                        // before
        var email = email_for_customer(customer, goods, bests);     // before + body
        emails.push(email);                                         // after
    });                                                             // after
    return emails;                                                  // after
}

function biggest_purchase_per_customer(customers) {
    var purchases = [];                                             // before
    for_each(customers, function(customer) {                        // before
        var purchase = biggest_purchase(customer);                  // before + body
        purchases.push(purchase);                                   // after
    });                                                             // after
    return purchases;                                               // after
}

function customer_cities(customers) {
    var cities = [];                                                // before
    for_each(customers, function(customer) {                        // before
        var city = customer.address.city;                           // before + body
        cities.push(city);                                          // after
    });                                                             // after
    return cities;                                                  // after
}
```

We can do a standard *replace body with callback* refactoring.

```javascript
function for_each(array, work_with) {
    for(var i = 0; i < array.length; i++) {
        var item = array[i];
        work_with(item);
    }
}

function map(array, mapping) {
    var new_array = [];                                             // before
    for_each(new_array, function(element) {                         // before
        var item = mapping(element);                                // before + calling body
        new_array.push(item);                                       // after
    });                                                             // after
    return new_array;                                               // after
}

function emails_for_customers(customers, goods, bests) {
    return map(customers, function(customer) {
        return email_for_customer(customer, goods, bests);          // body
    });
}

function biggest_purchase_per_customer(customers) {
    return map(customers, function(customer) {
        return biggest_purchase(customer);                          // body
    });
}

function customer_cities(customers) {
    return map(customers, function(customer) {
        return customer.address.city;                               // body
    });
}
```

## Functional tool: `map()`

`map()` is one of three “functional tools” that do a ton of work for the functional programmer. The other two are `filter()` and `reduce()`.

You could say that `map()` transforms an array of `X` (some group of values) into an array of `Y` (a different group of values). To do so, you need to pass in a function from `X` to `Y` — that is, a function that takes an `X` and returns a `Y`.

- If you pass `map()` a calculation, the expression calling `map()` will also be a calculation.
- If you pass `map()` an action, it will call the action once for every element in the array. In that case, the expression will be an action.

## Example: Email addresses of all customers

- We have: Array of customers
- We want: Array of their email addresses
- Function: Takes one customer and returns their email address

```javascript
map(customers, function(customer) {
    return customer.email;
});
```

We need an object containing the first name, last name, and address of each customer. Using map(), write the code to generate this array of objects.

```javascript
map(customers, function(customer) {
    return {
        first_name: customer.first_name,
        last_name:  customer.last_name,
        address:    customer.address
    };
});
```

## Deriving `filter()` from examples

`filter()` is a higher-order function that lets you create a new array based on an existing array. But it lets you say what elements you want to keep in the new array, and which elements you skip.

Let’s look at some of the functions [...] that match this same pattern:

```javascript
function select_best_customers(customers) {
    var new_array = [];                                             // before
    for_each(customers, function(customer) {                        // before
        if(customer.purchases.length >= 3)                          // before + body
            new_array.push(customer);                               // after
    });                                                             // after
    return new_array;                                               // after
}

function select_customers_after(customers, date) {
    var new_array = [];                                             // before
    for_each(customers, function(customer) {                        // before
        if(customer.signup_date > date)                             // before + body
            new_array.push(customer);                               // after
    });                                                             // after
    return new_array;                                               // after
}

function single_purchase_customers(customers) {
    var new_array = [];                                             // before
    for_each(customers, function(customer) {                        // before
        if(customer.purchases.length === 1)                         // before + body
            new_array.push(customer);                               // after
    });                                                             // after
    return new_array;                                               // after
}
```

We can do a standard *replace body with callback* refactoring.

```javascript
function filter(array, predicate) {
    var new_array = [];                                             // before
    for_each(array, function(element) {                             // before
        if(predicate(element))                                      // before + calling body
            new_array.push(element);                                // after
    });                                                             // after
    return new_array;                                               // after
}

function select_best_customers(customers) {
    return filter(customers, function(customer) {
        return customer.purchases.length >= 3;                      // body
    });
}

function select_customers_after(customers, date) {
    return filter(customers, function(customer) {
        return customer.signup_date > date;                         // body
    });
}

function single_purchase_customers(customers) {
    return filter(customers, function(customer) {
        return customer.purchases.length === 1;                     // body
    });
    var new_array = [];                                             // before
}
```

## Functional tool: `filter()`

You could say that `filter()` selects a subset of the elements of an array. If it’s an array of `X`, it’s still an array of `X`, but with potentially fewer elements. To do the selection, you need to pass in a function from `X` to `Boolean` — that is, a function that takes an `X` and returns `true` or `false` (or the equivalent). That function determines if each element stays (in the case of `true`) or goes (in the case of `false`).

Functions that return `true` or `false` are often called *predicates*.

## Example: Customers with zero purchases

- We have: Array of customers
- We want: Array customers who have zero purchases
- Function: Takes one customer and returns `true` if they have zero purchases

```javascript
filter(customers, function(customer) {
    return customer.purchases.length === 0;         // triple equals
});
```

[Briefly](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Equality_comparisons_and_sameness):

- double equals (`==`) will perform a *type conversion* when comparing two things, and will handle `NaN`, `-0`, and `+0` specially to conform to IEEE 754 (so `NaN != NaN`, and `-0 == +0`);
- triple equals (`===`) will do the same comparison as double equals (including the special handling for `NaN`, `-0`, and `+0`) but without type conversion; if the types differ, `false` is returned.

We want to arbitrarily select approximately one third of the customers and send them a different email from the rest. Givens

- `customers` is an array of all customers.
- `customer.id` will give you the user’s ID.
- `%` is the remainder operator; `x % 3 === 0` checks if `x` is divisible by 3.

```javascript
var test_group = filter(customers, function(customer) {
    return customer.id % 3 === 0;
});

var non_test_group = filter(customers, function(customer) {
    return customer.id % 3 !== 0;
});
```

## Deriving `reduce()` from examples

`reduce()` is also a higher-order function, like the other two. But it’s used to accumulate a value as it iterates over the array.

Let’s look at some of the functions [...] that match this same pattern:

```javascript
function count_all_purchases(customers) {
    var total = 0;                                                  // initial value
    for_each(customers, function(customer) {                        // before
        total = total + customer.purchases.length;                  // updating value
    });                                                             // after
    return total;                                                   // after
}

function concatenate_arrays(arrays) {
    var result = [];                                                // initial value
    for_each(arrays, function(array) {                              // before
        result = result.concat(array);                              // updating value
    });                                                             // after
    return result;                                                  // after
}

function customers_per_city(customers) {
    var cities = {};                                                // initial value
    for_each(customers, function(customer) {                        // before
        cities[customer.address.city] += 1;                         // updating value
    });                                                             // after
    return cities;                                                  // after
}

function biggest_purchase(purchases) {
    var biggest = { total: 0 };                                     // initial value
    for_each(purchases, function(purchase) {                        // before
        biggest = biggest.total > purchase.total                    // updating value
                ? biggest : purchase;
    });                                                             // after
    return total;                                                   // after
}
```

In these examples, there are only two things that differ.

- The first is the initialization of the variable.
- The second is the calculation that computes the next value of the variable.

The next value of the variable is based on the previous value of the variable and the current element of the array you’re processing.

```javascript
function reduce(array, init, bi_op) {
    var accum = init;                                               // initial value
    for_each(array, function(element) {                             // before
        accum = bi_op(accum, element);                              // updating value
    });                                                             // after
    return accum;                                                   // after
}

function count_all_purchases(customers) {
    return reduce(customers, 0, function(total, customer) {         // initial value
        return total + customer.purchases.length;                   // updating value
    });
}

function concatenate_arrays(arrays) {
    return reduce(arrays, [], function(result, array) {             // initial value
        return result.concat(array);                                // updating value
    });
}

function customers_per_city(customers) {
    return reduce(customers, {}, function(cities, customer) {       // initial value
        return cities[customer.address.city] += 1;                  // updating value
    });
}

function biggest_purchase(purchases) {
    return reduce(purchases, { total: 0 }, function(biggest, purchase) {
        return biggest.total > purchase.total ? biggest : purchase;
    }).total;
}
```

We’ve extracted a new function, called `reduce()`, that does the common iteration.

## Functional tool: `reduce()`

`reduce()` *accumulates* a value while iterating over an array. Accumulating a value is kind of an abstract idea. It could take many concrete forms. For instance,

- adding things up,
- adding stuff to a hash map or,
- concatenating strings.

You get to decide what accumulation means by the function you pass in.

## Example: Concatenating strings

- We have: Array of strings
- We want: One string that is the original strings concatenated
- Function: Takes an accumulated string and the current string from the array to concatenate

```javascript
reduce(strings, "", functions(accum, string) {
    return accum + string;
});
```

[How do you] determine the initial value. It depends on the operation and the context. But it’s the same answer as these questions:

- Where does the calculation start? For instance, summing starts at zero, so that’s the initial value for addition. But multiplying starts at 1, so that’s the initial value for multiplication.
- What value should you return if the array is empty? In the case of an empty list of strings, concatenating them should be an empty string.

Add up all numbers in the array:

```javascript
reduce(numbers, 0, function(sum, number) {
    return sum + number;
});
```

Multiply all numbers in the array:

```javascript
reduce(numbers, 1, function(product, number) {
    return product * number;
});
```

Return the smallest number in the array:

```javascript
reduce(numbers, Number.MAX_VALUE, function(smallest, number) {
    return smallest < number ? smallest : number;
});
```

Return the largest number in the array:

```javascript
reduce(numbers, Number.MIN_VALUE, function(largest, number) {
    return largest > number ? largest : number;
});
```

Answer the following questions, which all have to do with one extreme or another:

1. What does `map()` return when you pass it an empty array?
    - Answer: `map()` return another empty array.
    - In `for_each()`, the function passed to it will never called. Therefore, the empty array initialized in `map()` will not change, and `map()` returns the enpmty array.
2. What does `filter()` return when you pass it an empty array?
    - Answer: `filter()` return another empty array.
    - In `for_each()`, the function passed to it will never called. Therefore, the empty array initialized in `filter()` will not change, and `filter()` returns the enpmty array.
3. What does `reduce()` return when you pass it an empty array?
    - Answer: `reduce()` returns a copy of the initial value passed to it.
    - In `for_each()`, the function passed to it will never called. Therefore, the copy of the initial value defined in `reduce()` will not change, and `reduce()` return the copy.
4. What does `map()` return if the function you pass to it just returns its argument?
    - Answer: `map()` returns a copy of the array you passed to it.
    - `new_array.push(item)` in `map()` pushes the item retrieved from the original array.
5. What does `filter()` return if the function you pass to it always returns `true`?
    - Answer: `filter()` returns a copy of the array you passed to it.
    - `predicate(element)` in `filter()` always evaluates `true`, therefore `new_array.push(element)` runs for all elements, which are the elements of the array you passed to `filter()`.
6. What does `filter()` return if the function you pass to it always returns `false`?
    - Answer: `filter()` returns an empty array.
    - `predicate(element)` in `filter()` always evaluates `false`, therefore `new_array.push(element)` never runs and the `new_array` is left empty.

You can write `map()` in terms of `reduce()`.

```javascript
function reduce(array, init, bi_op) {
    var accum = init;
    for_each(array, function(element) {
        accum = bi_op(accum, element);
    });
    return accum;
}

function map(array, mapping) {
    var new_array = [];
    for_each(new_array, function(element) {
        var item = mapping(element);
        new_array.push(item);
    });
    return new_array;
}

function map_by_reduce(array, mapping) {
    return reduce(array, [], function(accum, element) {
        accum.push(mapping(element));
        return accum;
    });
}
```

You can write `filter()` in terms of `reduce()`.

```javascript
function reduce(array, init, bi_op) {
    var accum = init;
    for_each(array, function(element) {
        accum = bi_op(accum, element);
    });
    return accum;
}

function filter(array, predicate) {
    var new_array = [];
    for_each(array, function(element) {
        if(predicate(element))
            new_array.push(element);
    });
    return new_array;
}

function filter_by_reduce(array, predicate) {
    return reduce(array, [], function(accum, element) {
        if (predicate(element)) accum.push(elememt);
        return accum;
    });
}
```

The preceeding implementations of `map_by_reduce()` and `filter_by_reduce()`. Both code mutates the returned array at each step (by calling `push()`). The mutating one is much more efficient. They are all still calculations, though, because it only mutates a local value, then doesn’t mutate it after returning it.

We said earlier that the function you pass to `reduce()` should be a calculation. In the mutating operations, we have violated this rule. However, we also can clearly see that in these functions, the mutation only happens in a local context. We are sure `map_by_reduce()` and `filter_by_reduce()` are still calculations.

These examples show that the rules (in this case, the function you pass to `reduce()` should be a calculation) are more like guidelines. They should be followed by default. Use caution and judgment when you violate them.

## Summary

- The three most common functional tools are `map()`, `filter()`, and `reduce()`. Nearly every functional programmer uses them often.
- `map()`, `filter()`, and `reduce()` are essentially specialized for loops over arrays. They can replace those for loops and add *clarity* because they are special-purpose.
