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



