#include "draw_all_shapes.h"
#include "draw.h"

void draw_all_shapes(Shapes shapes, std::ostream& out)
{
    for (auto const& shape : shapes)
    {
        shape->accept(draw{out});
    }
}
