#include "draw_circle.h"
#include "circle.h"

void draw(Circle const& c)
{
    c.out() << "Circle(" << c.radius() << ")\n";
}
