#ifndef _CIRCLE_H_
#define _CIRCLE_H_

#include "shape.h"
#include <functional>
#include <utility>

class circle : public shape
{
public:
    using draw_strategy = std::function<void(circle const&)>;

    explicit circle(double radius, draw_strategy drawer)
    : radius_{radius}
    , drawer_{std::move(drawer)}
    {}

    void draw() const override { drawer_(*this); }
    double radius() const noexcept { return radius_; }

private:
    double radius_;
    draw_strategy drawer_;
};

#endif
