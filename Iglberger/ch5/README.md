# Chapter 5. The Strategy and Command Design Patterns

Let us imagine that you and your team are about to implement a new 2D graphics tool. Amongst other, it needs to deal with simple geometric primitives, such as circles, squares, and others, which need to be drawn.

```c++
class shape
{
public:
    virtual ~shape() = default;
    virtual void draw() const = 0;
};

class circle : public shape { ... };
class square : public shape { ... };
```

## The Strategy Design Pattern Explained

> Intent: Define a family of algorithms, encapsulate each one, and make them interchangeable. *Strategy* lets the algorithm vary independently from clients that use it. —GoF

The following code snippet shows a naive implementation of the `draw_strategy` base class:

```c++
class draw_strategy
{
public:
    virtual ~draw_strategy() = default;
    virtual void draw(circle const&) const = 0;
    virtual void draw(square const&) const = 0;
};
```

## Analyzing the Shortcomings of the Naive Solution

Why should `circle`s and `square`s have to recompile if you add a `triangle` class?

By defining a single `draw_strategy` base class you have artificially coupled circles, squares, and triangles together. Due to this coupling, you have made it more difficult to add new types and thus have limited the strength of OOP.

By means of this class template you can lift the `draw_strategy` up into a higher architectural level, reuse code and follow the Don't Repeat Yourself (DRY) principle.

```c++
template<typename T> class draw_strategy
{
public:
    virtual ~draw_strategy() = default;
    virtual void draw(T const&) const = 0;
};
```

In order to properly implement the Strategy design pattern, you have to extract the implementation details of each shape separately.

```c++
class circle : public shape
{
public:
    explicit circle(
        double radius,
        std::unique_ptr<draw_strategy<circle>> drawer
    )
    : radius_{radius}, drawer_{std::move(drawer)} {}

    double radius() const noexcept { return radius_; }
    void draw() const override { drawer_->draw(*this); }

private:
    double radius_;
    std::unique_ptr<draw_strategy<circle>> drawer_;
};
```

For the `circle` class, you have to introduce the `draw_strategy<circle>` base class, for the `square` class the `draw_strategy<square>`.

With this functionality in place, you can easily implement new Strategy classes for drawing circles, squares, and eventually triangles.

```c++
class ostream_circle_strategy : public draw_strategy<circle>
{
public:
    explicit ostream_circle_strategy(std::ostream& out) : out_{out} {}

    void draw(circle const& c) const
    { out_ << "circle(" << c.radius() << ")\n"; }

private:
    std::ostream& out_;
};
```

Then

```c++
TEST(ch5, poc1)
{
    std::ostringstream sout;

    std::vector<std::unique_ptr<shape>> shapes;
    shapes.emplace_back(std::make_unique<circle>(
        2.3, std::make_unique<ostream_circle_strategy>(sout)
    ));
    shapes.emplace_back(std::make_unique<square>(
        1.2, std::make_unique<ostream_square_strategy>(sout)
    ));
    shapes.emplace_back(std::make_unique<circle>(
        4.1, std::make_unique<ostream_circle_strategy>(sout)
    ));

    for (auto const& shape : shapes)
    {
        shape->draw();
    }

    constexpr std::string_view expected{
        "circle(2.3)\n"
        "square(1.2)\n"
        "circle(4.1)\n"
    };
    EXPECT_EQ(sout.str(), expected);
}
```

## Comparison between Visitor and Strategy

In case of the Visitor design pattern we have identified the *general addition of operations* as the variation point. Therefore we have created an abstraction for operations in general, which in turn allowed everyone to add operations. The unfortunate side effect was, that it was no longer easy to add new shape types.

In case of the Strategy design pattern we have identified the *implementation details of a single function* as a variation point. After introducing an abstraction for these implementation details we are still able to easily add new types of shapes, but we are not able to easily add new operations. That is, because adding an operation would still require you to intrusively add a virtual member function.

It may sound promising to combine the two design patterns in order to gain the advantages of both ideas (making it easy to add both types *and* operations). Unfortunately this does not work: whichever of the two design patterns you apply first, it will fix one of the two axes of freedom.

## Analyzing the Shortcomings of the Strategy Design Pattern

Last but not least, the major disadvantage of the Strategy design pattern is that [...], there will have to be multiple Strategy base classes and multiple data members ...

```c++
class circle : public shape
{
public:
    explicit circle(
        std::unique_ptr<draw_strategy<circle>> drawer,
        std::unique_ptr<serialize_strategy<circle>> serializer
        // potentially more strategy arguments
    )
    : drawer_{std::move(drawer)}
    , serializer_{std::move(serializer)}
    {}

    void draw() const override { drawer_->draw(*this); }
    void serialize const override { serializer_->serialize(*this); }

private:
    std::unique_ptr<draw_strategy<circle>> drawer_;
    std::unique_ptr<serialize_strategy<circle>> serializer_;
    // potentially more strategy data members
};
```

Therefore the Strategy design pattern appears to be strongest in situations where you need to isolate a small number of implementation details. In case you encounter a situation where you need to extract the details of many operations, it might be better to consider other approaches (see for instance Type Erasure in Chapter 7).

## Policy-Based Design

Given the base class:

```c++
class shape
{
public:
    virtual ~shape() = default;
    virtual void draw() const = 0;
};
```

the intent of Strategy can perfectly be implemented in static polymorphism by means of templates.


```c++
template<typename DrawCircleStrategy>
class circle : public shape
{
public:
    explicit circle(double radius, DrawCircleStrategy drawer)
    : radius_{radius}, drawer_{std::move(drawer)} {}

    double radius() const noexcept { return radius_; }
    void draw() const override { drawer_.draw(*this); }

private:
    double radius_;
    DrawCircleStrategy drawer_;
};
```

The template argument allows you to inject some cleanup behavior into the class. This form of Strategy is also called *Policy-Based Design*, based on a design philosophy introduced by Andrei Alexandrescu in 2001 (*Modern C++ Design*). The idea is the same: extract and isolate specific behavior of class templates in order to improve changeability, extensibility, testability, and reusability. Thus Policy-Based Design can be considered the static polymorphism form of the Strategy design pattern.

Instead of passing a `std::unique_ptr` to a base class in the constructor, you could specify the Strategy by means of a template argument.

```c++
class ostream_circle_strategy 
{
public:
    explicit ostream_circle_strategy(std::ostream& out) : out_{out} {}
    void draw(circle<ostream_circle_strategy> const& c) const
    { out_ << "circle(" << c.radius() << ")\n"; }
private:
    std::ostream& out_;
};
```

The biggest advantage would be the performance improvement due to less pointer indirections: instead of calling through a `std::unique_ptr` you could directly call to the concrete implementation. [...] you should keep in mind that class template usually completely reside in header files. You could therefore loose the opportunity to hide implementation details in a source file.

## Guideline 20: Favor Composition Over Inheritance

> Inheritance is the base class of evil. —Sean Parent

Inheritance is not about reusing code in a base class, but instead it is about being reused by other code that uses the base class polymorphically.

All four functions are built on the `shape` abstraction:

```c++
class shape;

void rotate_around_point(shape&);
void marge_shapes(shape&, shape&);
void write_to_file(shape const&);
void send_via_RPC(shape const&);
```

It is the ability to express functionality by means of an abstraction that creates the opportunity to reuse code. And this functionality is expected to create a vast amount of code, in comparison to the little amount of code the base class contains. Real reusability therefore is created *by the polymorphic use of a type*, not by polymorphic types.

> [Programming by difference] fell out of favor in the 1990s when many people in the OO community noticed that inheritance can be rather problematic if it is overused. —Michael Feathers

Inheritance has proven to be hard to use properly and thus is misused unintentionally.It is also overused as many developers have the habit to use it for every kind of problem.

## The Command Design Pattern Explained

> Intent: Encapsulate a request as an object, thereby letting you parameterize clients with different requests, queue or log requests, and support undoable operations. —GoF

In this OO-based form, the Command pattern introduces an abstraction in form of the `command` base class.

```c++
class command
{
public:
    virtual ~command() = default;
    virtual int execute(int i) const = 0;
    virtual int undo   (int i) const = 0;
};
```

This enables anyone to implement a new kind of concrete command such as `add`.

```c++
class add : public command
{
public:
    explicit add(int operand) : operand_{operand} {}
    int execute(int i) const override { return i + operand_; }
    int undo   (int i) const override { return i - operand_; }
private:
    int operand_{};
};
```

Thanks to the `command` hierarchy, the `processor` class itself can be kept rather simple:

```c++
class processor
{
public:
    void compute(std::unique_ptr<command>);
    void undo_last();
    int  result() const;
    void clear();
private:
    using command_stack = std::stack<std::unique_ptr<command>>;
    command_stack stack_;
    int current_{};
};
```

The client code combines all of the pieces:

```c++
TEST(ch5, poc3)
{
    calc::processor proc{};
    proc.compute(std::move(std::make_unique<calc::add>(3)));
    proc.compute(std::move(std::make_unique<calc::add>(7)));
    proc.compute(std::move(std::make_unique<calc::sub>(4)));
    proc.compute(std::move(std::make_unique<calc::sub>(2)));

    EXPECT_EQ(proc.result(), 4);

    proc.undo_last();
    EXPECT_EQ(proc.result(), 6);
}
```

## The Command Design Pattern vs. the Strategy Design Pattern

From a structural point of view, the Strategy and Command design patterns are identical: whether you are using dynamic or static polymorphism, from an implementation point of view there is no difference between Strategy and Command. The difference lies entirely in the intent of the two design patterns.

- the Strategy design pattern specified how something should be done,
- the Command design pattern specifies what should be done.

You can only control *how* to select elements in the `std::partition()` algorithm.

```c++
template< typename ForwardIt, typename UnaryPredicate >
constexpr ForwardIt
   partition( ForwardIt first, ForwardIt last, UnaryPredicate p );  
```

The `std::for_each()` algorithm gives you control over *what* operation is applied to each element in the given range.

```c++
template<typename InputIt, typename UnaryFunction>
constexpr UnaryFunction
   for_each(InputIt first, InputIt last, UnaryFunction f);
```

There is no clear separation between these two patterns and that there is a gray area in-between these two.

## The Shortcomings of the GoF Style: Reference Semantics

The design patterns collected by the Gang of Four (GoF) [...] are using at least one inheritance hierarchy and thus are firmly rooted in the realm of OO programming.

The GoF style falls into what we today call *reference semantics* (or sometimes also *pointer semantics*).

To demonstrate what the term reference semantics means and why it usually comes with a rather negative connotation, let us take a look at the following code example using the C++20 `std::span` class template:

```c++
#include <span>
#include <ostream>

void print(std::span<int> s, std::ostream& out)
{
    out << '(';
    for (int i : s) {
        out << ' ' << i;
    }
    out << " )";
}
```

The `print()` function demonstrates the purpose of `std::span`. The `std::span` class template represents an abstraction for an array. The `print()` function can be called with any kind of array (built-in arrays, `std::array`, `std::vector`, …) without coupling to any specific type of array.

In the demonstrated example of a `std::span` with a dynamic extent (i.e. no second template argument representing the size of the array), a typical implementation of `std::span` contains two data members: a pointer to the first element of the array, and the size of the array.

```c++
TEST(ch5, poc4_expected)
{
    std::vector<int> v{1, 2, 3, 4};
    std::span<int> const s{v};

    std::ostringstream sout;
    s[2] = 99;
    print(s, sout);
    constexpr std::string_view expected{
        "( 1 2 99 4 )"
    };   
    EXPECT_EQ(sout.str(), expected);
}
```

The `std::span s` is qualified as `const`. The attempt to change `s`, however, works fine. There will be no compilation error, despite the fact that `s` is declared const.

The reason for this is, that `s` is not a copy of `v` and does not represent a value. Instead it represents a reference to `v`. It essentially acts as a pointer to the first element of `v`. Thus the const qualifier semantically has the same effect as declaring a pointer `const`:

```c++
std::span<int> const s{ v };  // s acts as pointer to the first element of v
int* const ptr{ v.data() };   // Equivalent semantical meaning
```

While the pointer `ptr` cannot be changed and will refer to the first element of `v` throughout its lifetime, the referenced integer can be easily modified. In order to prevent an assignment to the integer, you would need to add an additional `const` qualifier for the `int`:

```c++
std::span<int const> const s{ v };  // s represents a const pointer to a const int
int const* const ptr{ v.data() };   // Equivalent semantical meaning
```

Since the semantics of a pointer and a std::span are equivalent, std::span obviously falls into the category of *reference semantics*.

Because we can [...] we now assign a new set of numbers to the vector `v`:

```c++
TEST(ch5, poc4_not_expected)
{
    std::vector<int> v{1, 2, 3, 4};
    std::span<int> const s{v};

    std::ostringstream sout;
    v = {5, 6, 7, 8, 9};
    s[2] = 99;
    print(s, sout);
    constexpr std::string_view expected{
        "( 5 6 99 8 9 )"
    };   
    EXPECT_NE(sout.str(), expected);
}
```

Yes, of course, *undefined behavior*! When assigning new values to the `std::vector v`, we have not just changed the values, but also the size of the vector. Instead of four values it now needs to store five elements. For that reason, the vector has (possibly) performed a reallocation and thus changed the address of its first element. Unfortunately the `std::span s` did not get the note and still firmly holds on to the address of the previous first element.

Hence when we try to write to `v` by means of `s` we do not write into the current array of `v`, but to an *already discarded piece of memory* that used to be the internal array of `v`. *Classic undefined behavior* and a classic problem of *reference semantics*.

I am not trying to suggest that `std::span`, and also `std::string_view`, are bad. [...]. When I use them, I use them consciously, fully aware that any non-owning reference type requires careful attention to the lifetime of the value it references. For instance, while I consider both as very useful tools for function arguments, I tend to not use them as data members.

## The Modern C++ Philosophy: Value Semantics

*Value semantics* is nothing new in C++.

```c++
TEST(ch5, vector)
{
    std::vector<int> v1{1, 2, 3, 4, 5};

    auto v2{v1};
    EXPECT_TRUE(v1 == v2);
    EXPECT_FALSE(v1.data() == v2.data());

    v2[2] = 99;
    EXPECT_FALSE(v1 == v2);

    auto const v3{v1};
    // v3[2] == 99;     compilation error
}
```

Changing one element in `v2` has the effect that the two vectors are not equal anymore. Yes, both vectors have their own arrays. They do not share their content, i.e. they do not try to *optimize* the copy operation.

We create another copy called `v3`, which we declare as `const`. If we now try to change a value of `v3` we will get a compilation error. This shows, that a `const vector` does not just prevent adding and removing elements, but that *all elements are also considered to be* `const`.

From a semantic perspective, this means that a `std::vector` [...] is considered to be a value. Yes, a value, like for instance an `int`. If we copy a value, we do not just copy a part of the value, but the entire value. If we make a value `const`, it is not just partially `const` but completely `const`. That is the rationale of *value semantics*.

Changing a value does not have an impact on some other value. The change happens locally, not somewhere else. [...] Also, values do not make us think about ownership. A value is in charge of its own content.

In real code, we can often rely on *copy elision*, *move semantics*, and well… pass-by-reference.

> [Copy elision](https://en.cppreference.com/w/cpp/language/copy_elision): In a return statement, when the operand is a *prvalue* of the same class type (ignoring cv-qualification) as the function return type, the compilers are required to omit the copy and move construction of class objects, even if the copy/move constructor and the destructor have observable side-effects.

```c++
T f() {
    return T();
}

f();    // only one call to default constructor of T
```

## Value Semantics — A Second Example

This function parses the given string [...] and converts it to an `int`.

```c++
int to_int(std::string_view);
```

The most interesting question for us now, is how the function should deal with errors, or in other words, what the function should do if the string cannot be converted to an `int`.

Although exceptions may be C++’s native tool to signal error cases, [...], this may appear as overkill to you.

A third possibility is change the signature by a little bit:

```c++
bool to_int(std::string_view s, int&);
```

Now the function takes a reference to a *mutable* `int` as the second parameter and returns a `bool`. [...] the most natural way to return a result is via the return value, but now the result is produced by means of an output value. This, for instance, prevents us from assigning the result to a const value.

The fourth approach is to return by pointer:

```c++
std::unique_ptr<int> to_int(std::string_view);
```

Semantically, this approach is pretty attractive [...] at the cost of a dynamic memory allocation, the need to deal with lifetime management [...]

The solution comes in the form of `std::optional`:

```c++
std::optional<int> to_int(std::string_view);

TEST(ch5, optional)
{
    if (auto i = to_int("42")) {
        // the returned std::optional contains an integer value
    } else {
        // the returned std::optional does not contain a value
    }
}
```

Semantically, this is the equivalent to the pointer approach, but we do not pay the cost of dynamic memory allocation and we do not have to deal with lifetime management. This solution is semantically clear, understandable, and efficient.

Almost all GoF patterns are based on inheritance hierarchies and therefore reference semantics. How should we deal with this?

## Introduction to `std::function`

`std::function` represents an abstraction for a callable (e.g. a function pointer, function object, or lambda).

The template parameter specifies the required function type. In our example this is `void(int)`.

```c++
TEST(ch5, default_function_throw)
{
    std::function<void(int)> f{};
    EXPECT_THROW(f(1), std::bad_function_call);
}

TEST(ch5, function_lambda)
{
    std::stringstream sout;
    std::function<void(int)> f = [&](int i){
        sout << "lambda: " << i;
    };
    f(1);

    std::string_view expected{
        "lambda: 1"
    };
    EXPECT_EQ(sout.str(), expected);
}
```

This time, we assign a pointer to the function `foo()`. Again, this callable fulfills the requirements of the `std::function` instance: it takes an `int` and `std::ostream&`, and returns nothing.

```c++
void foo(int i, std::ostream& out)
{
    out << "foo: " << i;
}

TEST(ch5, function_assignment_external)
{
    std::stringstream sout;
    std::function<void(int, std::ostream&)> f{foo};
    f(2, sout);

    std::string_view expected{
        "foo: 2"
    };
    EXPECT_EQ(sout.str(), expected);
}
```

If you call `g` with the integer `3`, the output demonstrates that `std::function` is firmly based on *value semantics*:

```c++
TEST(ch5, function_assignment_lambda)
{
    std::stringstream sout;
    std::function<void(int)> f = [&](int i){
        sout << "lambda: " << i;
    };

    auto g = f;

    f = [&](int i){
        sout << "another lambda: " << i;
    };

    g(3);

    std::string_view expected{
        "lambda: 3"
    };
    EXPECT_EQ(sout.str(), expected);
}
```

During the initialization of `g`, the instance `f` was copied. And it was copied as a value should be copied: it does not perform a *shallow copy*, which would result in `g` being affected when `f` is subsequently changed, but it performs a complete copy (i.e. deep copy), which includes a copy of the lambda.

Thus changing `f` does not affect `g`. That is the benefit of *value semantics*: the code is easy and intuitive and you do not have to be afraid that you are accidentally breaking something anywhere else.

## Refactoring the Drawing of Shapes

```c++
class shape
{
public:
    virtual ~shape() = default;
    virtual void draw() const = 0;
};
```

In the constructor of `circle`, we now take an instance of type `std::function` as a replacement for the pointer to a Strategy base class.

```c++
class circle : public shape
{
public:
    using draw_strategy = std::function<void(circle const&)>;

    explicit circle(double radius, draw_strategy drawer)
    : radius_{radius}
    , drawer_{std::move(drawer)}
    {}

    void draw() const override { drawer_(*this); }
    double radius() const noexcept { return radius_; }

private:
    double radius_;
    draw_strategy drawer_;
};
```

Once we have refactored the `circle` class, we can implement different drawing strategies in any form we like (i.e. in form of a function, a function object, or a lambda).

```c++
class ostream_circle_strategy
{
public:
    explicit ostream_circle_strategy(std::ostream& out)
    : out_{out} {}

    void operator()(circle const& c)
    { out_ << "circle(" << c.radius() << ")\n"; }

private:
    std::ostream& out_;
};
```

and finally draw them:

```c++
TEST(ch5, poc6)
{
    std::stringstream sout;
    std::vector<std::unique_ptr<shape>> shapes{};

    shapes.emplace_back(
        std::make_unique<circle>(2.3, ostream_circle_strategy{sout})
    );

    for (auto const& s : shapes)
    { s->draw(); }

    constexpr std::string_view expected{
        "circle(2.3)\n"
    };
    EXPECT_EQ(sout.str(), expected);
}
```

> Why are you taking the `std::function` instance by *value* [instead of] *reference*-to-`const`?

The approach to provide two constructors (one for *lvalues*, one for *rvalues*) does work and is efficient, but I would not necessarily call it elegant.

`std::function` provides both a *copy constructor* and a *move constructor*, and so we know that it can be moved efficiently. When we pass a `std::function` by value, either the copy constructor or the move constructor will be called.

- In case we are passed an *lvalue*, the copy constructor is called, copying the *lvalue*. Then we would move that copy into the data member. In total we would perform one copy and one move to initialize the `drawer_` data member.
- In case we are passed an *rvalue*, the move constructor is called, moving the *rvalue*. The resulting argument strategy is then moved into the data member `drawer_`. In total we would perform two move operations to initialize the `drawer_` data member.

Therefore this form represents a great compromise: it is elegant and there is hardly any difference in efficiency.

## Analyzing the Shortcomings of the `std::function` Solution

There is also a design-related issue. `std::function` is only capable of replacing a single virtual function. In case you need to abstract multiple virtual functions, which for instance occurs

- if you want to configure multiple aspects by means of the Strategy design pattern, or 
-in case you need an `undo()` function in the Command design pattern,

you would have to use multiple `std::function` instances. This would

- not just increase the size of a class due to the multiple data members,
- but would also incur an interface burden

due to the question of how to elegantly handle the passing of multiple `std::function` instances.

