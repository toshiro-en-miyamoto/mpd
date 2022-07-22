#ifndef _SQUARE_H_
#define _SQUARE_H_

#include "point.h"

class square
{
public:
    explicit square(double side) : side_{side} {}
    double side() const noexcept { return side_; }
    point center() const noexcept { return center_; }

private:
    double side_;
    point center_{};
};

#endif
