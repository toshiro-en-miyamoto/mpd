# Sharing resources between timelines

```javascript
function queue(worker) {
    var queue_items = [];
    var working = false;

    function run_next() {
        if (working) return;
        if (queue_items.length === 0) return;
        working = true;
        var item = queue_items.shift();

        worker(item.data, function(val) {
            working = false;
            setTimeout(item.callback, 0, val);
            run_next();
        });
    }

    return function(data, callback) {
        queue_items.push({
            data: data,
            callback: callback || function() {}
        });
        setTimeout(run_next, 0);
    };
}

function calc_cart_woker(cart, done) {
    calc_cart_total(cart, function(total) {
        update_total_dom(total);
        done(total);
    });
}

var update_total_queue = queue(calc_cart_worker);
```


