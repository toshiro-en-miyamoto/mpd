#ifndef _OSTREAM_CIRCLE_STRATEGY_H_
#define _OSTREAM_CIRCLE_STRATEGY_H_

#include "draw_strategy.h"
#include "circle.h"
#include <ostream>

class ostream_circle_strategy : public draw_strategy<circle>
{
public:
    explicit ostream_circle_strategy(std::ostream& out) : out_{out} {}
    void draw(circle const& c) const
    { out_ << "circle(" << c.radius() << ")\n"; }
private:
    std::ostream& out_;
};

#endif
