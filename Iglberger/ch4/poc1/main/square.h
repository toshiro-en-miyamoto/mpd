#ifndef _SQUARE_H_
#define _SQUARE_H_

#include "point.h"
#include "shape.h"

class Square : public Shape
{
public:
    explicit Square(double side, std::ostream& out = std::cout)
    : Shape(square, out), side_(side) {}

    double side() const noexcept { return side_; }
    Point center() const noexcept { return center_; }

private:
    double side_;
    Point center_{};
};

#endif
