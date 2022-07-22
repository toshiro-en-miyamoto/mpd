#ifndef _CIRCLE_H_
#define _CIRCLE_H_

#include "shape.h"
#include <memory>
#include <utility>

template<typename DrawCircleStrategy>
class circle : public shape
{
public:
    explicit circle(double radius, DrawCircleStrategy drawer)
    : radius_{radius}, drawer_{std::move(drawer)} {}

    double radius() const noexcept { return radius_; }
    void draw() const override { drawer_.draw(*this); }

private:
    double radius_;
    DrawCircleStrategy drawer_;
};

#endif
