#ifndef _CIRCLE_H_
#define _CIRCLE_H_

#include "shape.h"
#include "draw_strategy.h"
#include <memory>
#include <utility>

class circle : public shape
{
public:
    explicit circle(
        double radius,
        std::unique_ptr<draw_strategy<circle>> drawer
    )
    : radius_{radius}, drawer_{std::move(drawer)} {}

    double radius() const noexcept { return radius_; }
    void draw() const override { drawer_->draw(*this); }

private:
    double radius_;
    std::unique_ptr<draw_strategy<circle>> drawer_;
};

#endif
