#ifndef _CIRCLE_H_
#define _CIRCLE_H_

#include "shape.h"
#include "shape_visitor.h"

class circle : public shape
{
public:
    explicit circle(double radius) : radius_{radius} {}
    void accept(shape_visitor const& v) override { v.visit(*this); }
    double radius() const noexcept { return radius_; }

private:
    double radius_;
};

#endif
