# Chapter 7. The Type Erasure Design Pattern

The technique was first discussed in a paper by Kevlin Henney in the July-August 2000 edition of the C++ report. In this paper, Kevlin demonstrates *Type Erasure* by means of a code example that later evolved into what we today know as C++17’s `std::any`.

[`std::any_cast`](https://en.cppreference.com/w/cpp/utility/any/any_cast) performs type-safe access to the contained object.

```c++
TEST(ch7, poc1)
{
    auto a = std::any(12);
    std::cout << std::any_cast<int>(a) << '\n';

    try {
        std::cout << std::any_cast<std::string>(a) << '\n';
    } catch(const std::bad_any_cast& e) {
        std::cout << e.what() << '\n';
    }

    a = std::string("hello");

    auto& ra = std::any_cast<std::string&>(a);
    ra[1] = 'o';
    std::cout << ra << '\n';

    std::cout << std::any_cast<std::string const&>(a) << '\n';

    auto b = std::any_cast<std::string&&>(std::move(a));
    std::cout << std::any_cast<std::string const&>(a) << '\n';
    std::cout << std::any_cast<std::string const&>(b) << '\n';

    std::string_view expected {
        "12\n"
        "bad any_cast\n"
        "hollo\n"
        "hollo\n"
        "\n"
        "hollo\n"
    };
}
```

## The Type Erasure Design Pattern Explained

Type Erasure is nothing but a compound design pattern, meaning that it is a very clever and elegant combination of three other design patterns:

- External Polymorphism for achieving the decoupling effect,
- Bridge for a value semantics based implementation, and
- Prototype for the copy semantics of the resulting values.

> Intent: Provide a value-based, non-intrusive abstraction for an extendable set of unrelated, potentially non-polymorphic types with the same semantic behavior. —Klaus Iglberger

- *Value-based*: the intent of Type Erasure is to create value types, i.e. types that may be copyable, movable, and, most importantly, are easily reasoned about. However, such a value type is not of the same quality as a *regular* value type, [it] works best for unary operations, but has its limits for binary operations.
- *Non-intrusive*: the intent of Type Erasure is to create an external, non-intrusive abstraction based on the example set by the External Polymorphism design pattern.
- *Extendable, unrelated set of types*: Type Erasure [...] enables you to add types easily. These types, though, should not be connected in any way. That includes that they do not have to share common behavior via some base class.
- *Potentially non-polymorphic*: types should [...] not have to provide virtual functionality on their own, but they should be decoupled from their polymorphic behavior.

*Same semantic behavior*: the goal is [...] to provide a semantic abstraction for a set of types that provide the same operations (including same syntax) and adhere to some expected behavior according to the Liskov Substitution Principle.

## An Owning Type Erasure Implementation


