#ifndef _CIRCLE_H_
#define _CIRCLE_H_

#include "point.h"

class circle
{
public:
    explicit circle(double radius) : radius_{radius} {}
    double radius() const noexcept { return radius_; }
    point center() const noexcept { return center_; }

private:
    double radius_;
    point center_{};
};

#endif
