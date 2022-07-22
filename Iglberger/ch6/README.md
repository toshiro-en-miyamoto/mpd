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

