#ifndef _SQUARE_H_
#define _SQUARE_H_

#include "shape.h"
#include "draw_strategy.h"
#include <memory>
#include <utility>

class square : public shape
{
public:
    explicit square(
        double side,
        std::unique_ptr<draw_strategy<square>> drawer
    )
    : side_{side}, drawer_{std::move(drawer)} {}

    double side() const noexcept { return side_; }
    void draw() const override { drawer_->draw(*this); }

private:
    double side_;
    std::unique_ptr<draw_strategy<square>> drawer_;
};

#endif
