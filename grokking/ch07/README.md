# Staying immutable with untrusted code

## Defensive copying you may be familiar with

A web request comes into your API as JSON. The JSON is a *deep copy* of data from the client that is serialized over the internet. Your service does its work, then sends the response back as a serialized *deep copy*, also in JSON. It’s copying data on the way in and on the way back.

It’s doing defensive copying. One of the benefits of a service-oriented or microservices system is that the services are doing defensive copying when they talk to each other. Services with different coding practices and disciplines can communicate without problems.

When modules implement defensive copying when talking to each other, this is often called a *shared nothing architecture* because the modules don’t share references to any data. You don’t want your copy-on-write code to share references with untrusted code.

## Implementing deep copy in JavaScript is difficult

In JavaScript it is quite hard to get right because there isn’t a good standard library. Implementing a robust one is beyond the scope of this book.

However, just for completeness, here is a simple implementation that may satisfy your curiosity. It should work for all JSON-legal types and functions.

```javascript
function deep_copy(thing) {
    if (thing == null) {
        return null;
    } else if (Array.isArray(this)) {
        var copy = [];
        for (var i = 0; i < thing.length; i++)
            copy.push(deep_copy(thing[i]));
        return copy;
    } else if (typeof thing == "object") {
        var copy = {};
        var keys = Object.keys(thing);
        for (var i = 0; i < keys.length; i++) {
            var key = keys[i];
            copy[key] = deep_copy(thing[key]);
        }
        return copy;
    } else {
        return thing;
    }
}
```

I highly recommend using a robust deep copy implementation from a widely used JavaScript library like `Lodash`. This deep copy function is just for teaching purposes and will not work in production.

## Summary

- Defensive copying is a discipline for implementing immutability. It makes copies as data leaves or enters your code.
- Defensive copying makes deep copies, so it is more expensive than copy-on-write.
- Unlike copy-on-write, defensive copying can protect your data from code that does not implement an immutability discipline.
- We often prefer copy-on-write because it does not require as many copies and we use defensive copying only when we need to interact with untrusted code.
- Deep copies copy an entire nested structure from top to bottom. Shallow copies only copy the bare minimum.
