#include "draw_all_shapes.h"
#include "circle.h"
#include "square.h"

void draw_all_shapes(
    std::vector<std::unique_ptr<Shape>> const& shapes
) {
    for (auto const& shape : shapes) {
        shape->draw();
    }
}
