#include "draw_all_shapes.h"
#include "draw.h"

void draw_all_shapes(shapes const& shapes_, std::ostream& out)
{
    auto draw_ = draw{out};
    for (auto const& shape_ : shapes_)
    {
        std::visit(draw_, shape_);
    }
}
