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

These two classes have not changed since we last encountered them in the discussion of External Polymorphism. But it still pays off to again stress, that these two are completely unrelated, i.e. do not know about each other, and most importantly are non-polymorphic, meaning that they do not inherit from any base class or introduce virtual function on their own.

```c++
lass Circle
{
public:
    explicit Circle(double radius) : radius_ {radius} {}
    double radius() const { return radius_; }
private:
    double radius_;
};
```

We have also seen the `Shape_concept` and `Owning_shape_model` classes before, the latter under the name `Shape_model`. [...] Both classes have been moved to the `detail` namespace. The name of the namespace indicates that these two classes are now becoming implementation details, i.e. they are not intended for direct use anymore.

```c++
namespace detail {

class Shape_concept
{
public:
    virtual ~Shape_concept() = default;
    virtual void draw() const = 0;
    virtual std::unique_ptr<Shape_concept> clone() const = 0;
};

template<typename Shape_t, typename Draw_strategy>
class Owning_shape_model : public Shape_concept
{
public:
    explicit Owning_shape_model(Shape_t shape, Draw_strategy drawer)
    : shape_ {std::move(shape)}, drawer_ {std::move(drawer)}
    {}

    void draw() const override { drawer_(shape_); }

    std::unique_ptr<Shape_concept> clone() const override
    {
        return std::make_unique<Owning_shape_model>(*this);
    }

private:
    Shape_t shape_;
    Draw_strategy drawer_;
};

} // namespace detail
```

Since `Owning_shape_model` inherits from `Shape_concept`, it must implement the two pure virtual functions. The `draw()` function is implemented by applying the given drawing Strategy, while the `clone()` function is implemented to return an exact copy of the corresponding `Owning_shape_model`.

We are just one step away from turning External Polymorphism into Type Erasure, just one step away from switching from *reference semantics* to *value semantics*.

All we need is a value type, a wrapper around the external hierarchy introduced by `Shape_concept` and `Owning_shape_model`, that handles all the details that we do not want to perform manually:

- the instantiation of the `Owning_shape_model` class template,
- managing pointers,
- performing allocations, and
- dealing with lifetime.

This wrapper is given in form of the `Shape` class:

```c++
class Shape
{
public:
    template<typename Shape_t, typename Draw_strategy>
    Shape(Shape_t shape, Draw_strategy drawer)
    {
        using Model = detail::Owning_shape_model<Shape_t, Draw_strategy>;
        pimpl_
        = std::make_unique<Model>(std::move(shape), std::move(drawer));
    }

    Shape(Shape const& other) : pimpl_ {other.pimpl_->clone()} {}
    Shape& operator=(Shape const& other)
    {
        Shape copy {other};
        pimpl_.swap(copy.pimpl_);
        return *this;
    }

    ~Shape() = default;
    Shape(Shape&&) = default;
    Shape& operator=(Shape&&) = default;

private:
    friend void draw(Shape const& shape) { shape.pimpl_->draw(); }
    std::unique_ptr<detail::Shape_concept> pimpl_;
};
```

The copy constructor could be a very difficult function to implement, since we do not know the concrete type of shape stored in the other `Shape`. However, due to providing the `clone()` function in the `Shape_concept` base class, we can ask for an exact copy without the need to know anything about the concrete type.

The shortest, most painless and most convenient way to implement the copy assignment operator is to build on the [Copy-and-Swap Idiom](https://en.wikibooks.org/wiki/More_C%2B%2B_Idioms/Copy-and-swap).

In addition, the `Shape` class provides a so-called [hidden friend](https://www.justsoftwaresolutions.co.uk/cplusplus/hidden-friends.html) called `draw()`.

```c++
TEST(ch7, poc3)
{
    auto circle_drawer = [](Circle const& c)
    { std::cout << "circle(" << c.radius() << ")\n"; };

    Shape shape1 {Circle{3.14}, circle_drawer};
    draw(shape1);

    Shape shape2 {shape1};
    draw(shape2);

    std::string_view expected {
        "circle(3.14)\n"
        "circle(3.14)\n"
    };
}
```

By means of the `draw()` function we are able to draw this `Shape`. Directly afterwards we create a copy of the shape. A real copy, i.e. a *deep copy*, not just the copy of a pointer. [...] You can rely on the copy operations of the value type (in this case the copy constructor) and you do not have to `clone()` manually.

- you no longer have to deal with pointers, there are no manual allocations, and you do not have to deal with inheritance hierarchies anymore.
- you are still able to easily add new types and the concrete shape types are still oblivious about the drawing behavior.

They are only connected to the desired functionality via the Shape constructor.

## Analyzing the Shortcomings of the Type Erasure Design Pattern

As soon as you are starting to go beyond the basic implementation and start to consider performance, exception safety, etc., the implementation details indeed become quite tricky very quickly.

Although we are now dealing with values that can be copied and moved, it is not straight forward to use Type Erasure for binary operations. For instance, it is not easily possible to equality-compare these values, as you would expect from regular values:

```c++
    if (shape1 == shape2)  // does not compile
```

The reason is, that after all `Shape` is only an abstraction from a concrete shape type and only stores a pointer-to-base.

## The Setup Costs of an Owning Type Erasure Wrapper

```c++
int compute(int i, int j, std::function<int(int, int)> op)
{
    return op(i, j);
}
int main()
{
    int const i = 17;
    int const j = 10;
    int const sum = compute(i, j, [offset=15](int x, int y){
        return x + y + offset;
    });
}
```

In this example, the given lambda is converted into a `std::function` instance. This conversion will involve a copy operation and might involve a memory allocation. It entirely depends on the size of the given callable and on the implementation of `std::function`.

For that reason, `std::function` is a different kind of abstraction than for instance `std::string_view` and `std::span`. `std::string_view` and `std::span` are *non-owning abstractions*, that are cheap to copy because they only consist of a pointer to the first element and a size. Because these two types perform a shallow copy, they are perfectly suited as function parameters.

`std::function`, on the other hand, is an *owning abstraction*, that performs a deep copy. Therefore it is not the perfect type to be used as function parameter. Unfortunately, the same is true for our `Shape` implementation.

The underlying problem is the *owning semantics* of the `Shape` class: on the basis of its *value semantics* background, our current `Shape` implementation will always create a copy of the given shape and will always own the copy.

## A Simple Non-Owning Type Erasure Implementation

You might want to reach for a *non-owning* implementation of Type Erasure, despite the disadvantage that this pulls you back into the realm of reference semantics.

```c++
class Shape_const_ref
{
public:
    template<typename Shape_t, typename Draw_strategy>
    Shape_const_ref(Shape_t& shape, Draw_strategy& drawer)
    : shape_ {std::addressof(shape)}
    , drawer_ {std::addressof(drawer)}
    , draw_ {[](void const* shape_bytes, void const* drawer_bytes){
        auto const* shape = static_cast<Shape_t const*>(shape_bytes);
        auto const* drawer = static_cast<Draw_strategy const*>(drawer_bytes);
        (*drawer)(*shape);
    }} {}

private:
    friend void draw(Shape_const_ref const& shape)
    { shape.draw_(shape.shape_, shape.drawer_); }

    void const* shape_ {nullptr};
    void const* drawer_ {nullptr};

    using Draw_operation = void(void const*, void const*);
    Draw_operation* draw_ {nullptr};
};
```

As the name suggests, the `Shape_const_ref` class represents a *reference* to a `const` shape type. Instead of storing a copy of the given shape, it only holds a pointer to it in form of a `void*`.

In addition, it holds a `void*` to the associated `Draw_strategy`, and as the third data member, a function pointer to the manually implemented virtual `draw()` function.

It is efficient, short, simple, and it can even be shorter and simpler if you do not need to store any kind of associated data or Strategy object.

```c++
TEST(ch7, poc4)
{
    Circle circle {3.14};

    auto circle_drawer = [](Circle const& c)
    { std::cout << "circle(" << c.radius() << ")\n"; };

    draw(Shape_const_ref {circle, circle_drawer});

    std::string_view expected {
        "circle(3.14)\n"
    };
}
```

This call triggers the desired effect, notably without any memory allocation or expensive copy operation, but only by wrapping polymorphic behavior around a set of pointers to the given shape and drawing Strategy.

[You can use `std::vector` of `Shape_const_ref` as long as circles are lvalue instances.]

```c++
TEST(ch7, poc4b)
{
    auto circle_drawer = [](Circle const& c)
    { std::cout << "circle(" << c.radius() << ")\n"; };

    std::vector<Shape_const_ref> shapes {};
    Circle c1 {3.14};
    Circle c2 {4.15};
    shapes.emplace_back(Shape_const_ref {c1, circle_drawer});
    shapes.emplace_back(Shape_const_ref {c2, circle_drawer});

    for (auto const& shape : shapes)
        draw(shape);

    std::string_view expected {
        "circle(3.14)\n"
        "circle(4.15)\n"
    };
}
```

[The following code results in undefined behavior.]

```c++
TEST(ch7, poc4a)
{
    auto circle_drawer = [](Circle const& c)
    { std::cout << "circle(" << c.radius() << ")\n"; };

    std::vector<Shape_const_ref> shapes {};
    shapes.emplace_back(Shape_const_ref {Circle {3.14}, circle_drawer});
    shapes.emplace_back(Shape_const_ref {Circle {4.15}, circle_drawer});

    for (auto const& shape : shapes)
        draw(shape);

    // undefined behavior
}
```
