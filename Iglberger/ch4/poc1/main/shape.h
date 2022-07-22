#ifndef _SHAPE_H_
#define _SHAPE_H_

#include <iostream>

enum ShapeType
{
    circle,
    square
};

class Shape
{
public:
    virtual ~Shape() = default;
    ShapeType type() const noexcept { return type_; }
    std::ostream& out() const noexcept { return out_; }

protected:
    explicit Shape(ShapeType type, std::ostream& out = std::cout)
    : type_(type), out_(out) {}

private:
    ShapeType type_;
    std::ostream& out_;
};

#endif
