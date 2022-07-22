#ifndef _SQUARE_H_
#define _SQUARE_H_

#include "shape.h"
#include <memory>
#include <utility>

template<typename draw_square_strategy>
class square : public shape
{
public:
    explicit square(double side, draw_square_strategy drawer)
    : side_{side}, drawer_{std::move(drawer)} {}

    double side() const noexcept { return side_; }
    void draw() const override { drawer_.draw(*this); }

private:
    double side_;
    draw_square_strategy drawer_;
};

#endif
