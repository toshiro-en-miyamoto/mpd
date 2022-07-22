#ifndef _OSTREAM_SQUARE_STRATEGY_H_
#define _OSTREAM_SQUARE_STRATEGY_H_

#include "square.h"
#include <ostream>

class ostream_square_strategy
{
public:
    explicit ostream_square_strategy(std::ostream& out) : out_{out} {}
    void draw(square<ostream_square_strategy> const& s) const
    { out_ << "square(" << s.side() << ")\n"; }
private:
    std::ostream& out_;
};

#endif
