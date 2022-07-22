#ifndef _SHAPE_H_
#define _SHAPE_H_

#include <iostream>

class Shape
{
public:
    virtual ~Shape() = default;
    virtual void draw() const = 0;
    std::ostream& out() const noexcept { return out_; }

protected:
    explicit Shape(std::ostream& out = std::cout)
    : out_(out) {}

private:
    std::ostream& out_;
};

#endif
