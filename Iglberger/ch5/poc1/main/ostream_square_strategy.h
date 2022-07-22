#ifndef _OSTREAM_SQUARE_STRATEGY_H_
#define _OSTREAM_SQUARE_STRATEGY_H_

#include "draw_strategy.h"
#include "square.h"
#include <ostream>

class ostream_square_strategy : public draw_strategy<square>
{
public:
    explicit ostream_square_strategy(std::ostream& out) : out_{out} {}
    void draw(square const& s) const
    { out_ << "square(" << s.side() << ")\n"; }
private:
    std::ostream& out_;
};

#endif
