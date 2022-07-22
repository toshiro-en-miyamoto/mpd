#include "draw_square.h"
#include "square.h"

void draw(Square const& s)
{
    s.out() << "Square(" << s.side() << ")\n";
}
