# Chapter 6. The Bridge, Prototype, Adapter, and External Polymorphism Design Patterns

```c++
class engine
{
public:
    virtual ~engine() = default;
    virtual void start() = 0;
    virtual void stop() = 0;
    // ... more engine-specific functions
};

class electric_car
{
public:
    void drive();
    // ...
private:
    std::unique_ptr<engine> engine_;
    // ...
};
```

## The Bridge Design Pattern Explained

> Intent: Decouple an abstraction from its implementation so that the two can vary independently. —GoF

## The *Pimpl* Idiom

In order to hide all changes to the implementation details of `person` and in order to gain ABI stability, you can use the *Bridge* design pattern. In this particular case however, there is no need to provide an abstraction in form of a base class: there is one, and exactly one, implementation for `person`. Therefore, all we do is to introduce a private, nested class called `impl`:

```c++
// person.h
class person
{
public:
    // ...
private:
    struct impl;
    std::unique_ptr<impl> const pimpl_;
};
```

This pointer data member is the one **p**ointer-to-**impl**ementation for all kinds of `person`s and commonly called the *pimpl*. This opaque pointer represents the Bridge to the encapsulated implementation details and thus, essentially represents the Bridge design pattern as a whole.

```c++
class person
{
public:
    person(
        std::string const& forename,
        std::string const& surname
    );

    ~person();

    person(person const& other);
    person& operator=(person const& other);

    person(person&& other);
    person& operator=(person&& other);

    std::string const& forename() const noexcept;
    std::string const& surname() const noexcept;

private:
    struct impl;
    std::unique_ptr<impl> const pimpl_;
};
```

The sole task of the nested `impl` class is to encapsulate the implementation details of `person`. Thus, the only data member remaining in the `person` class is a `std::unique_ptr` to an `impl` instance.

```c++
#include "person.h"

struct person::impl
{
    std::string forename;
    std::string surname;
    // ... potentially many more data members
};

person::person(
    std::string const& forename,
    std::string const& surname
)
: pimpl_{std::make_unique<impl>(forename, surname)}
{}

person::~person() = default;
```

Since a `std::unique_ptr` cannot be copied, you will have to implement the copy constructor to preserve the copy semantics of the `person` class. The same is true for the copy assignment operator. Note that this operator is implemented under the assumption, that every instance of `person` will always have a valid `pimpl_`.

```c++
person::person(person const& other)
: pimpl_{std::make_unique<impl>(*other.pimpl_)}
{}

person& person::operator=(person const& other)
{
    *pimpl_ = *other.pimpl_;
    return *this;
}
```

This assumption explains the implementation of the move constructor, which also performs a dynamic memory allocation by means of `std::make_unique()`, instead of simply moving the `std::unique_ptr`, and is not declared as `noexcept`.

This assumption also explains, why the `pimpl_` data member is declared as `const`: Once it is initialized, the pointer will not be changed anymore, not even in the move operations, including the move assignment operator.

```c++
person::person(person&& other)
: pimpl_{std::make_unique<impl>(std::move(*other.pimpl_))}
{}

person& person::operator=(person&& other)
{
    *pimpl_ = std::move(*other.pimpl_);
    return *this;
}
```

The last detail worth noting, is the fact that the definition of the `forename()` and `surname()` member functions are located in the source file. Despite the fact that this simple getter function is a great `inline` candidate, the definition has to be moved to the source file. The reason is that in the header file `impl` is an *incomplete type*. That means that within the header file, you are not able to access any members (both data and functions). This is only possible in the source file, or generally speaking, as soon as the compiler knows the definition of `impl`.

```c++
std::string const& person::forename() const noexcept
{ return pimpl_->forename; }

std::string const& person::surname() const noexcept
{ return pimpl_->surname; }
```

> A class that has been declared but not defined [...] is an *incompletely-defined object type*. Incompletely-defined object types and *cv* `void` are *incomplete types*. Objects shall not be defined to have an incomplete type. — 6.8 Types [5], ISO/IEC JTC1 SC22 WG21 N 4860, Programming Languages C++



The intent of the Bridge design pattern is to isolate physical dependencies from implementation details;

- Be aware of physical dependencies introduced by data members or includes;
- Prefer using a *pimpl* data member to communicate the use of a Bridge;
- Keep in mind that Bridges can have a negative performance impact;
- Be aware that a *partial Bridge* can have a positive impact on performance when separating frequently used data from infrequently used data;

## The Prototype Design Pattern Explained

> Intent: Specify the kind of objects to create using a prototypical instance, and create new objects by copying this prototype. —GoF

The Prototype design pattern is commonly implemented by means of a virtual `clone()` function in the base class.

```c++
class animal
{
public:
    virtual ~animal() = default;
    virtual std::unique_ptr<animal> clone() const = 0;

    virtual std::string sound() const = 0;
};

class sheep : public animal
{
public:
    explicit sheep(std::string name) : name_{std::move(name)} {}
    std::unique_ptr<animal> clone() const override;

    std::string sound() const override;

private:
    std::string name_;
};
```

The `sheep` class is now required to implement the `clone()` function and to return an exact copy of the `sheep`: Inside its own `clone()` function it makes use of the `std::make_unique()` function and its own copy constructor, which is always assumed to do the right thing, even if the `sheep` class changes in the future.

```c++
std::unique_ptr<animal> sheep::clone() const
{
    return std::make_unique<sheep>(*this);
}

std::string sheep::sound() const
{
    return name_ + ":baa\n";
}
```

With the `clone()` function in place, we are now able to create an exact copy of Dolly.

```c++
TEST(ch6, dolly)
{
    std::string_view expected{"Dolly:baa\n"};

    std::unique_ptr<animal> dolly = std::make_unique<sheep>("Dolly");
    EXPECT_EQ(dolly->sound(), expected);

    std::unique_ptr<animal> clone = dolly->clone();
    EXPECT_EQ(clone->sound(), expected);
}
```

## Analyzing the Shortcomings of the Prototype Design Pattern

- The function name `clone()` can almost be considered a keyword for identifying the Prototype design pattern.
- Because of the specific use case, there is no *modern* solution.
- There also is no value semantics based solution: as soon as we had a value, the most natural and intuitive solution would be to build on the two copy operations (i.e. the copy constructor and the copy assignment operator).

Our `animal` hierarchy would be simpler and more comprehensible if you could replace it with a *value semantics* approach and therefore avoid having to apply the reference semantics based Prototype design pattern. Still, whenever you encounter the need to create an abstract copy, the Prototype design pattern with an according `clone()` function is the right choice. (In comparison to the ability to perform an abstract copy operation, the few downsides are easily acceptable.)

## The Adapter Design Pattern Explained

> Intent: Convert the interface of a class into another interface clients expect. Adapter lets classes work together that couldn’t otherwise because of incompatible interfaces. —GoF

## Examples from the Standard Library

```c++
template<typename T, typename Container = std::deque<T>>
class stack;

template<typename T, typename Container = std::deque<T>>
class queue;

template<typename T
    , typename Container = std::vector<T>
    , typename Compare = std::less<typename Container::value_type>
>
class prioript_queue;
```

## Comparison between Adapter and Strategy

From a structural point of view, the Strategy and Adapter design patterns are very similar.

- the primary focus of an Adapter is to standardize interfaces and to integrate incompatible functionality into an existing set of conventions
- the primary focus of the Strategy is to enable the configuration of behavior from the outside, building on, and providing, an expected interface

## Function Adapters

Another example for the Adapter design pattern is the Standard Library’s free functions `begin()` and `end()`.

## The External Polymorphism Design Pattern Explained

Its intent is to enable the polymorphic treatment of non-polymorphic types (i.e. types without a single virtual function):

> Intent: Allow C++ classes unrelated by inheritance and/or having no virtual methods to be treated polymorphically. These unrelated classes can be treated in a common manner by software that uses them. —C. Cleeland, D. C. Schmidt, T. H. Harrison

In our simple example, the polymorphic behavior only consists of the `draw()` function. However, the set of requirements could, of course, be larger (e.g. `rotate()`, `serialize()`, …). This set of virtual functions has been moved into the abstract `Shape_concept` class. Therefore, `Shape_concept` now takes the place of the previous `Shape` base class.

```c++
class Shape_concept
{
public:
    virtual ~Shape_concept() = default;
    virtual void draw() const = 0;
    // potentially more polymorphic operations
};
```

The major difference is, that concrete shapes are not required to know about `Shape_concept` and, in particular, are not expected to inherit from it. Thus, the shapes are completely decoupled from the set of virtual functions.

```c++
class Circle
{
public:
    explicit Circle(double radius) : radius_ {radius} {}
    double radius() const { return radius_; }
private:
    double radius_;
};
```

The only class inheriting from `Shape_concept` is the `Shape_model` class template. This class is instantiated for a specific kind of shape (`Circle`, `Square`, …) and acts as a wrapper for it. However, `Shape_model` does not implement the logic of the virtual functions itself, but delegates the request to the desired implementation.

```c++
template<typename Shape_t>
class Shape_model : public Shape_concept
{
public:
    using Draw_strategy = std::function<void(Shape_t const&)>;

    explicit Shape_model(Shape_t shape, Draw_strategy drawer)
    : shape_ { std::move(shape) }
    , drawer_ { std::move(drawer) }
    {}

    void draw() const override { drawer_(shape_); }

private:
    Shape_t shape_;
    Draw_strategy drawer_;
};
```

Note, that `Shape_model` stores an instance of the according shape (composition, not inheritance). It thus acts as a wrapper that augments the specific shape type with the required polymorphic behavior (in our case the `draw()` function).

Since `Shape_model` implements the `Shape_concept` abstraction, it needs to provide an implementation for the `draw()` function. However, it is not the responsibility of the `Shape_model` to implement the `draw()` details itself. Instead, it should forward a drawing request to the actual implementation. For that purpose, we can again reach for the Strategy design pattern and for the abstracting power of `std::function`.

This is an exemplar for combining runtime and compile time polymorphism: the `Shape_concept` base class provides the abstraction for all possible types, while the deriving `Shape_model` class template provides the code generation for shape-specific code.

With this functionality in place, we are now free to implement any desired drawing behavior.

```c++
class Circle;
class Square;

class Cout_draw_strategy
{
public:
    void operator()(Circle const&) const;
    void operator()(Square const&) const;
};
```

and then

```c++
TEST(ch6, poc4)
{
    using Shapes = std::vector<std::unique_ptr<Shape_concept>>;
    using Circle_model = Shape_model<Circle>;
    using Square_model = Shape_model<Square>;

    Shapes shapes{};
    shapes.emplace_back(
        std::make_unique<Circle_model>(
            Circle {2.3}, Cout_draw_strategy{}
        )
    );
    shapes.emplace_back(
        std::make_unique<Square_model>(
            Square {1.2}, Cout_draw_strategy{}
        )
    );
    shapes.emplace_back(
        std::make_unique<Circle_model>(
            Circle {4.1}, Cout_draw_strategy{}
        )
    );

    for (auto const& shape : shapes) {
        shape->draw();
    }
}
```

## Analyzing the Shortcomings of the External Polymorphism Design Pattern

The External Polymorphism design pattern does not really fulfill the expectations of a clean and simple solution and definitely not the expectations of a value semantics based solution. It does not help to reduce pointers, does not reduce the number of manual allocations, does not lower the number of inheritance hierarchies, and does not help to simplify user code.

Perhaps the `Shape_concept` base class does not really represent an abstraction of a shape. It is reasonable to argue that shapes are more than just drawing. Perhaps we should have named the abstraction `Drawable` and the LSP would have been satisfied.

> The *Liskov Substitution Principle* (LSP) is the third of the SOLID principles and concerned with behavioral subtyping, i.e. with the expected behavior of an abstraction. This is what we commonly call an *IS-A* relationship. This relationship, i.e. the expectations in an abstraction, must be adhered to in a subtype. —Chapter 2, Guideline 6: Adhere to the Expected Behavior of Abstractions

