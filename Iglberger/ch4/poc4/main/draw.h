#ifndef _DRAW_H_
#define _DRAW_H_

#include "shape.h"
#include <iostream>

class draw
{
public:
    explicit draw(std::ostream& out) : out_{out} {}

    void operator()(circle const& c) const
    { out_ << "circle(" << c.radius() << ")\n"; }

    void operator()(square const& s) const
    { out_ << "square(" << s.side() << ")\n"; }

private:
    std::ostream& out_;
};

#endif
