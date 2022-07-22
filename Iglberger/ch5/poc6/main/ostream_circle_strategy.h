#ifndef _OSTREAM_CIRCLE_STRATEGY_H_
#define _OSTREAM_CIRCLE_STRATEGY_H_

#include "circle.h"
#include <ostream>

class ostream_circle_strategy
{
public:
    explicit ostream_circle_strategy(std::ostream& out)
    : out_{out} {}

    void operator()(circle const& c)
    { out_ << "circle(" << c.radius() << ")\n"; }

private:
    std::ostream& out_;
};

#endif
