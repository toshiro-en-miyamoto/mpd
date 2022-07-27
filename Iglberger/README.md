# C++ Software Design by Klaus Iglberger

There are a couple of recurring pieces of advice throughout this book:

- Minimize dependencies.
- Separate concerns.
- Prefer composition to inheritance.
- Prefer non-intrusive solutions.
- Prefer value semantics over reference semantics.

Chapter 4.

- GoF: Visitor
- C++: `std::variant`, `std::visit()`

Chapter 5.

- GoF: Strategy, Command
- C++: `std::span`, `std::optional`, `std::function`
- general: reference and value semantics, copy and move constructors

Chapter 6.

- GoF: Bridge (*Pimpl*), Prototype (`clone()`)
- C++: Container *adaptors* in `std::stack` and `std::dueue`
- general: the External Ploymorphism design pattern

Chapter 7.

- C++: `alignas` specifier, `std::construct_at()`, `std::destroy_at()`, `std::forward()`
