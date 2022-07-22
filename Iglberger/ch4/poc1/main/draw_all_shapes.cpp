#include "draw_all_shapes.h"
#include "circle.h"
#include "draw_circle.h"
#include "square.h"
#include "draw_square.h"

void draw_all_shapes(
    std::vector<std::unique_ptr<Shape>> const& shapes
) {
    for (auto const& shape : shapes) {
        switch (shape->type()) {
            break; case circle:
                draw(static_cast<Circle const&>(*shape.get()));
            break; case square:
                draw(static_cast<Square const&>(*shape.get()));
        }
    }
}
