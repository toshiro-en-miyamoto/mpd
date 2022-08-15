# Chaining functional tools

We will learn how to express complex calculations as a series of steps called a *chain*. Each step is an application of a functional tool. By combining multiple functional tools together, we can build very complex computations, yet make sure that each step is simple and easy to read and write.

We need to find the biggest purchase for each of our best customers.

1. Filter for only good customers (three or more purchases).
2. Map those to their biggest purchases.

Use `reduce()` in the callback for `map()` because we're finding biggest purchase for each customer.

```javascript
function biggest_purchases_best_customers(customers) {
    var best_customers = filter(customers, function(customer) {
        return customer.purchases.length >= 3;
    });

    var biggest_purchases = map(best_customers, function(customer) {
        return reduce(
            customer.purchases,
            {total: 0},
            function(biggest, purchase)
            {
                return biggest.total > purchase.total ? biggest : purchase;
            }
        );
    });

    return biggeset_purchases;
}
```

That will work, but it’s a big beast of a function, with multiple nested callbacks. It’s really hard to understand. Functions like these give chaining functional tools a bad reputation.

Then, write `max_key()`, which finds the largest value in an array. Let’s plug it into the code we had in the place of `reduce()`:

```javascript
function biggest_purchases_best_customers(customers) {
    var best_customers = filter(customers, function(customer) {
        return customer.purchases.length >= 3;
    });

    var biggest_purchases = map(best_customers, function(customer) {
        return max_key(
            customer.purchases,
            { total: 0 },
            function(purchase) { return purchase.total; }
        );
    });

    return biggeset_purchases;
}

function max_key(array, init, f) {
    return reduce(
        array,
        init,
        function(biggest, element)
        {
            return f(biggest) > f(elemet) ? biggest : element;
        }
    );
}
```

This code is pretty concise. We’ve extracted another functional tool (`max_key()`) that helps us express our meaning that much better.

- `reduce()` is a low-level function, meaning it is very general. It means nothing more by itself than combining the values on an array.
- `max_key()` is more specific. It means choosing the biggest value from an array.

## Identity functions

Compare `max_key()` to the `max()` function we wrote in the previous chapter:

```javascript
function max(numbers) {
    return reduce(
        numbers,
        Number.MIN_VALUE,
        function(largest, number)
        {
            return largest > number ? largest : number;
        }
    );
}

function max_key(array, init, f) {
    return reduce(
        array,
        init,
        function(biggest, element)
        {
            return f(biggest) > f(elemet) ? biggest : element;
        }
    );
}
```

The difference between them is that the code for biggest purchase has to compare the totals, while the regular `max()` can compare numbers directly.

`max_key()` is more general than `max()`. `max_key()` can find the largest value based on an arbitrary comparison, while `max()` can only compare directly. We can write `max()` in terms of `max_key()`.

```javascript
function max(numbers) {
    return max_key(
        numbers,
        Number.MIN_VALUE,
        function(x) { return x; }           // the identity function
    );
}
```

The *identity function* is a function that returns its argument unchanged. It appears to do nothing, but it is useful for indicating just that: Nothing should be done.

## Clarifying chains, method 1: Name the steps

If we extracted a function for each higher-order function and named it, it would look like this:

```javascript
function biggest_purchases_best_customers(customers) {
    var best_customers= select_best_customers(customers);
    var biggest_purchases = get_biggest_purchases(best_customers);
    return biggest_purchases;
}
 
function select_best_customers(customers) {
    return filter(customers, function(customer) {
        return customer.purchases.length >= 3;
    });
}

function get_biggest_purchases(customers) {
    return map(customers, get_biggest_purchase);
}

function get_biggest_purchase(customer) {
    return max_key(customer.purchases, {total: 0}, function(purchase) {
        return purchase.total;
    });
}
```

## Clarifying chains, method 2: Naming the callbacks

The other way to clarify our chains is to name the callbacks.

```javascript
function biggest_purchases_best_customers(customers) {
    var best_customers = filter(customers, is_good_customer);
    var biggest_purchases = map(best_customers, get_biggest_purchase);
    return biggest_purchases;
}

function is_good_customer(customer) {
    return customer.purchases.length >= 3;
}

function get_biggest_purchase(customer) {
    return max_key(customer.purchases, {total: 0}, get_purchase_total);
}

function get_purchase_total(purchase) {
    return purchase.total;
}
```

Functional programmers will try both of these methods and compare the results to ultimately decide which code to use.

## Example: Emails of customers who have made one purchase

- We have: Array of customers
- We want: Email address of those customers who have exactly one purchase

```javascript
var first_timers = filter(customers, function(customer) {
    return customer.purchases.length == 1;
});

var first_timer_emails = map(first_timers, function(customer) {
    return customer.email;
});
```

If you want to make it more compact by extracting and naming callback functions, it would look like this:

```javascript
var first_timers = filter(customers, is_first_timer);
var first_timer_emails = map(first_timers, get_customer_email);

function is_first_timer(customer) {
    return customer.purchases.length == 1;
}

function get_customer_email(customer) {
    return customer.email;
}
```

We would like to know which customers have made at least one purchase over $100 AND two or more purchases total. People who meet both criteria are known as big spenders.

```javascript
function big_spenders(customer) {
    var with_big_purchases = filter(customers, has_big_purchase);
    return filter(with_big_purchases, has_two_or_more_purchases);
}

function has_big_purchase(customer) {
    return filter(customer.purchases, is_big_purchase).length > 0;
}

function is_big_purchase(purchase) {
    return purchase.total > 100;
}

function has_two_or_more_purchases(customer) {
    return customer.purchases.length >= 2;
}
```

We want to average an array of numbers at some point.

```javascript
function average(numbers) {
    return reduce(numbers, 0, plus) / numbers.length;
}

function plus(left, right) {
    return left + right;
}
```

We need to compute the average purchase total for each customer.

```javascript
function average_purchase_totals(customers) {
    return map(customers, function(customer) {
        var purchase_totals = map(customer.purchases, function(purchase) {
            return purchase.total;
        });
        return average(purchase_totals);
    });
}
```

Both `filter()` and `map()` create new arrays, and potentially add many items to them, each time they are called.

`map()`, `filter()`, and `reduce()` can be optimized very easily without dropping back down to for loops. The process of optimizing a chain of `map()`, `filter()`, and `reduce()` calls is called *stream fusion*.

If you have two `map()` calls in a row,

```javascript
var names = map(customers, get_full_name);
var name_lengths = map(names, string_length);
```

you can combine them into one step.

```javascript
var name_length = map(customers, function(customer) {
    return string_length(get_full_name(customer));
});
```

These two pieces of code get the same answer, but the second one does it in one `map()` step without a garbage array.

We can do something similar with `filter()`.

```javascript
var good_customers = filter(customers, is_good_customer);
var with_addresses = filter(good_customers, has_address)
```

Two `filter()` steps in a row are like performing a logical AND with two booleans.

```javascript
var with_addresses = filter(customers, function(customer) {
    return is_good_customer(cuatomer) && has_address(customer);
});
```

Finally, `reduce()` can do a lot of the work itself. It can take on a lot more of the processing. For example, if you have a `map()` followed by a `reduce()`,

```javascript
var purchase_totals = map(purchases, get_purchase_total);
var purchases_sum = reduce(purchase_totals, 0, plus);
```

you can do this:

```javascript
var purchases_sum = reduce(purchases, 0, function(total, purchase) {
    return total + get_purchase_total(purchase);
});
```

Again, this is an optimization. It will only make a difference if that is the bottleneck. In most cases, it is much clearer to do things in multiple steps, since each step will be clear and readable.

## Refactoring existing for loops to functional tools

Here is an example code snippet with a nested for loop:

```javascript
var answer = [];
var window = 5;

for (var i = 0; i < array.length; i++) {
    var sum = 0;
    var count = 0;
    for (var w = 0; w < window; w++) {
        var idx = i + w;
        if (idx < array.length) {
            sum += array[idx];
            count += 1;
        }
    }
    answer.push(sum / count);
}
```

Even without fully understanding what this code does, we can begin to break it down. There are many clues in the code that we can pick up on.

- We are adding one element to the answer array for each element in the original array. That’s a strong indication that we want a `map()`. That’s the outer loop.
- The inner loop is like a `reduce()`. It’s looping over something and combining elements into a single answer.

## Tip 1: Make data

This tip suggests we put that data into an array so that we can use our functional tools on it.

The inner loop is looping through a small range of elements in `array`. `.slice()` makes an array of elements from a subsequence inside the array.

```javascript
var answer = [];
var window = 5;

for (var i = 0; i < array.length; i++) {
    var sum = 0;
    var count = 0;
    var sub_array = array.slice(i, i + window);     // replaced
    for (var w = 0; w < sub_array.length; w++) {    // replaced
        sum += sub_array[w];                        // replaced
        count += 1;
    }
    answer.push(sum / count);
}
```

## Tip 2: Operate on whole array at once

Now that we’ve built that subarray, we are looping over an entire array. That means we can use `map()`, `filter()`, or `reduce()` on it, because those tools operate on whole arrays.

```javascript
var answer = [];
var window = 5;

for (var i = 0; i < array.length; i++) {
    var sub_array = array.slice(i, i + window);
    answer.push(average(sub_array));                // replaced
}

function average(numbers) {
    var total = reduce(numbers, 0, function(accum, number) {
        return accum + number;
    });
    return total / numbers.length;
}
```

The outer loop is adding one element to the answer array for each element in the original array. That’s a strong indication that we want a `map()`.

But this for loop is not using the current element in the array, so we can’t replace it directly. Remember, the callback to `map()` only gets the current element. Instead, this for loop’s index `i` is being used to slice out a `sub_array`. We can’t replace this directly with `map()`.

## Tip 3: Take many small steps

The trouble is that we want to loop over the *indices*, not the values of the array. The indices will let us slice into our original array, creating subarrays or "windows".

```javascript
for (var i = 0; i < array.length; i++) {
    var sub_array = array.slice(i, i + window);
    answer.push(average(sub_array));
}
```

It may be hard (or impossible) to loop over the indices in the one step we already have. So let’s do it in many small steps.

- First, since we need the indices, why don’t we just generate them as an array (tip 1)?
- Then we can operate on the whole array of indices with a functional tool (step 2).

```javascript
var indices = [];
for (var i = 0; i < array.length; i++) indices.push(i);

var window = 5;

var answer = map(indices, function(i) {
    var sub_array = array.slice(i, i + window);
    return average(sub_array);
});

function average(numbers) {
    var total = reduce(numbers, 0, function(accum, number) {
        return accum + number;
    });
    return total / numbers.length;
}
```

We're dowing two things in the callback to `map()`: making a subarray and averaging it. Clearly this could be two separate steps.

```javascript
var indices = [];
for (var i = 0; i < array.length; i++) indices.push(i);

var window = 5;

var windows = map(indices, function(i) {
    return array.slice(i, i + window);
});

var answer = map(windows, average);

function average(numbers) {
    var total = reduce(numbers, 0, function(accum, number) {
        return accum + number;
    });
    return total / numbers.length;
}
```

The last thing to do is to extract the loop that generates indices into a helper function `range()`.

```javascript
var indices = range(0, array.length);
var window = 5;

var windows = map(indices, function(i) {
    return array.slice(i, i + window);
});

var answer = map(windows, average);

function average(numbers) {
    var total = reduce(numbers, 0, function(accum, number) {
        return accum + number;
    });
    return total / numbers.length;
}

function range(start, end) {
    var ret = [];
    for (var i = start; i < end; i++) {
        ret.push(i);
        return ret;
    }
}
```

We’ve replaced all the for loops with a chain of functional tools.

## Comparing functional to imperative code




