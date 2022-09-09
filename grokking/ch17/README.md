# Coordinating timelines

The ordering and proper timing of actions is difficult. We can make it easier by creating reusable objects that manipulate the timeline. The important aspects of time itself—the ordering of calls and repetition of calls—can be directly manipulated.

## How the code was changed

```javascript
function add_item_to_cart(item) {
    cart = add_item(cart, item);
    update_total_queue(cart);
}

function calc_cart_total(cart, callback) {
    var total = 0;
    cost_ajax(cart, function(cost) {
        total += cost;
    });
    shipping_ajax(cart, function(shipping) {
        total += shipping;
        callback(total);
    });
}

function calc_cart_worker(cart, done) {
    calc_cart_total(cart, function(total) {
        update_total_dom(total);
        done(total);
    });
}

var update_total_queue = dropping_queue(1, calc_cart_worker);
```

That makes the `shipping_ajax()` call happen immediately instead of in the `cost_ajax()` callback. Of course, this would be faster, since the two ajax requests are made at the same time. However, it has obviously caused a bug.

## Waiting for both parallel callbacks

What we have:

```
    Click handler           cost_ajax()     shipping_ajax()
+----------------------+
| Read cart            |
| Wrie cart            |
| Read cart            |
| update_total_queue() |
+----------------------+
+----------------------+
| Initialize total     |
| cost_ajax()          |
| shipping_ajax()      |
+----------------------+
            +---------------------+------------------+----------
                            +-------------+ +--------------------+
                            | Read total  | | Read total         |
                            | Write total | | Write total        |
                            +-------------+ | Read total         |
                                            | update_total_dom() |
                                            +--------------------+
```

What we want:

```
    Click handler           cost_ajax()     shipping_ajax()
+----------------------+
| Read cart            |
| Wrie cart            |
| Read cart            |
| update_total_queue() |
+----------------------+
+----------------------+
| Initialize total     |
| cost_ajax()          |
| shipping_ajax()      |
+----------------------+
            +----------------------+---------------+----------
                            +-------------+ +-------------+
                            | Read total  | | Read total  |
                            | Write total | | Write total |
                            +-------------+ +-------------+
                          ---------+-------+-------+----------
                                +--------------------+
                                | Read total         |
                                | update_total_dom() |
                                +--------------------+
```

## A concurrency primitive for cutting timelines

We want to write a simple, reusable primitive that allows multiple timelines to wait for each other, even if the timelines end in different orders. If we have one, we will be able to ignore that things happen in different orders and simply care when they are all finished. That way, we can prevent the race condition.

> A *race condition* occurs when the behavior depends on which timeline finishes first.

We’ll create a function. Every timeline will call that function when it’s done. Every time the function is called, we increment the number of times it has been called. Then, when the last function calls it, it will call a callback:

```javascript
function cut(num, callback) {
    var num_finished = 0;
    return function() {
        num_finished += 1;
        if (num_finished === num)
            callback();
    };
}
```

A simple example:

```javascript
var done = cut(3, function() {
    console.log("3 timelines are finished");
});

done();
done();
done();     // console => "3 timelines are finished"
```

JavaScript has one thread. A timeline runs to completion before other timelines begin. `cut()` takes advantage of this fact to safely share a mutable variable. Other languages would need to use locks or other coordination mechanisms so timelines can coordinate.

## Using `cut()` in our code

```javascript
function calc_cart_total(cart, callback) {
    var total = 0;
    var done = cut(2, function() {
        callback(total);
    });
    cost_ajax(cart, function(cost) {
        total += cost;
        done();
    });
    shipping_ajax(cart, function(shipping) {
        total += shipping;
        done();
    });
}
```

## Conclusion

In this chapter, we diagnosed a race condition around the timing of web requests. If the requests came back in the same order as they were called, everything was good. But we couldn’t guarantee that, so sometimes it gave us the wrong result. We created a primitive that let the two timelines work together so that they always get the same result. It was an example of coordination between timelines.
