# Chapter 1. Software Design and Design Principles

## The Open-Closed Principle

The *Open-Closed Principle* (OCP) is the second of the *SOLID* principles. It advises us to design software such that it is easy to make the necessary extensions:

> Software artifacts (classes, modules, functions, etc.) should be open for extension, but closed for modification. —Bertrand Meyer

The OCP tells us, that we should be able to extend our software (open for extension). However, the extension should be easy and in the best case be possible by just adding new code. In other words we should not have to modify existing code (closed for modification).

# Chapter 4. The Visitor Design Pattern

> Intent: Represent an operation to be performed on the elements of an object structure. *Visitor* lets you define a new operation without changing the classes of the elements on which it operates. —GoF

## A Procedural Solution (`poc1`)

The source of the problem is the direct dependency of all shape classes and functions on the enumeration. Any change to the enumeration results in a rippling effect and that requires the dependent files to be recompiled. Obviously, this directly violates the OCP.

## An Object-Oriented Solution (`poc2`)

With an object-oriented approach we are now able to add new types very easily. All we have to do is to write a new derived class. We do not have to modify or recompile any existing code (with the exception of the main() function). That perfectly fulfills the OCP.

However, did you notice that we are not able to easily add operations anymore? [...] We are now dealing with **a closed set of operations**, which means that we violate the OCP in relation to addition operations.

In order to add a virtual function, the base class needs to be modified and all derived classes (i.e. circles, squares, etc.) need to implement the new function, even though the function might never be called. In summary, the object-oriented solution

- fulfills the OCP with respect to adding types,
- but violates it in relation to operations.

## Be Aware of the Design Choice in Dynamic Polymorphism

The strength of procedural programming (poc1) is the easy addition of operations, but adding types is a real pain.

The strength of object-oriented programming (poc2) is the easy addition of new types, but its weakness is that the addition of operations becomes much more difficult.

| Paradigm | Operations | Types
|----------|------------|-------
| Procedural      | a closed set | an open set
| Object-oriented | an open set  | a closed set

It depends on your project:

- In case you expect new types will be added frequently, rather than operations, you should strive for an OOP solution, which treats operations as a closed set and types as an open set.
- In case you expect operations will be added, you should strive for a procedural solution, which treats types as a closed set and operations as an open set.

## Analyzing the Shortcomings of the Visitor Design Pattern

In summary, the Visitor design pattern is the OOP solution to allow for the easy extension of operations instead of types. That is achieved by introducing an abstraction in the form of the `ShapeVisitor` base class, which enables you to add operations on another set of types.

While this is a unique strength of Visitor, it unfortunately comes with several deficiencies:

- implementation inflexibilities in both inheritance hierarchies due to a strong coupling to the requirements of the base classes,
- a rather bad performance, and
- the intrinsic complexity of Visitor make it a rather unpopular design pattern.

## Introduction to `std::variant`

```c++
struct print
{
    void operator()(int value) const { ... }            // (1)
    void operator()(double value) const { ... }         // (2)
    void operator()(std::string value) const { ... }    // (3)
}
void visiting()
{
    const print p{};
    std::variant<int,double,std::string> v{};
    std::visit(p, v);       // calls (1) with int 0
    v = 42;
    std::visit(p, v);       // calls (1) with int 42
    v = 3.14;
    std::visit(p, v);       // calls (2) with double 3.14
    v = "Variant";
    std::visit(p, v);       // calls (3) with string "Variant"
}
```

The `std::visit` allows you to pass a custom visitor to perform any operation on the stored value of a closed set of types. The `print` visitor must provide `operator()` for every possible alternatives.

- There is only a very loose coupling based on the convention that for every alternative there needs to be an `operator()`.
- We do not have a `Visitor` base class anymore.
- We also do not have any base class for the alternatives: we are free to use fundamental types such as `int` and `double`, as well as arbitrary class types such as `std::string`.

Anyone can easily add new operations. No existing code needs to be modified. With this, we can argue that this is a procedural solution, just much more elegant than the initial `enum`-based approach.

## Refactoring the Drawing of Shapes with `std::variant`

Both `ciecle` and `square` are significantly simplified:
- no more `shape` base class,
- no more need to implement any virtual functions the `accept()`, and
- no need to prepare these classes for any upcoming operations.

Instead of a base class that abstracts from the actual type of `shape`, `std::variant` now acquires this task.

```c++
using shape = std::variant<circle, square>;
using shapes = std::vector<shape>;
```

The only reason we used (smart) pointers `std::unique_ptr` was to enable us to store different kinds of shapes in the same `vector`. But now that `std::variant` enables us to do the same, we can simply store variant objects inside a single vector.

Again, we are following the expectation to implement one `operator()` for every alternative: one for `circle`, and one for `square`.

```c++
struct draw
{
    void operator()(circle const& c) const { ... }
    void operator()(square const& s) const { ... }
};
```

Instead of creating `circle`s and `square`s by means of `std::make_unique()`, we simply create `circle`s and `square`s directly and add them to the `vector`. This works thanks to the non-explicit constructor of variant, which allows implicit conversion of any of the alternatives.

```c++
TEST(ch4, poc4)
{
    shapes shapes_;
    shapes_.emplace_back(circle{2.3});
    shapes_.emplace_back(square{1.2});
    shapes_.emplace_back(circle{4.1});
    draw_all_shapes(shapes_, sout);
}
```

The end result of this *value-based* solution is stunningly fascinating: No base classes anywhere. No virtual functions. No pointers. No manual memory allocations.

Everyone is able to add new operations, without the need to modify existing code. Therefore we still fulfill the OCP in respect to adding operations.

## Analyzing the Shortcomings of the `std::variant` Solution

The `std::variant` solution is focused on providing an *open set of operations*. The downside is, that this means you will have to deal with a *closed set of types*.

- you would have to update the variant itself, which might trigger a recompilation of all code using the variant type
- you would have to update all operations and add potentially missing `operator()` for the new alternative(s)
