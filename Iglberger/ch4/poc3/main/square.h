#ifndef _SQAURE_H_
#define _SQUARE_H_

#include "shape.h"
#include "shape_visitor.h"

class square : public shape
{
public:
    explicit square(double side) : side_{side} {}
    void accept(shape_visitor const& v) override {v.visit(*this); }
    double side() const noexcept { return side_; }

private:
    double side_;
};

#endif
