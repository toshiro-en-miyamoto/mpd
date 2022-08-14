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

