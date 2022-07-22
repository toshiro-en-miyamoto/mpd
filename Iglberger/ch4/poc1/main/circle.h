#ifndef _CIRCLE_H_
#define _CIRCLE_H_

#include "point.h"
#include "shape.h"
#include <iostream>

class Circle : public Shape
{
public:
    explicit Circle(double radius, std::ostream& out = std::cout)
    : Shape(circle, out), radius_(radius) {}

    double radius() const noexcept { return radius_; }
    Point center() const noexcept { return center_; }

private:
    double radius_;
    Point center_{};
};

# endif
