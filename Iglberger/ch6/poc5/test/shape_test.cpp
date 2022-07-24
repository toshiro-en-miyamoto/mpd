#include <gtest/gtest.h>
#include "ch6/poc5/main/circle.h"
#include "ch6/poc5/main/square.h"
#include "ch6/poc5/main/shape.h"
#include "ch6/poc5/main/cout_drawer.h"
#include <vector>
#include <memory>
#include <sstream>
#include <iostream>
#include <string_view>

TEST(ch6, poc5)
{
    using Shapes = std::vector<std::unique_ptr<Shape_concept>>;
    using Circle_model = Shape_model<Circle, Cout_draw_strategy>;
    using Square_model = Shape_model<Square, Cout_draw_strategy>;

    std::ostringstream sout;
    auto cout_buf = std::cout.rdbuf();
    std::cout.rdbuf(sout.rdbuf());

    Shapes shapes{};
    shapes.emplace_back(
        std::make_unique<Circle_model>(
            Circle {2.3}, Cout_draw_strategy{}
        )
    );
    shapes.emplace_back(
        std::make_unique<Square_model>(
            Square {1.2}, Cout_draw_strategy{}
        )
    );
    shapes.emplace_back(
        std::make_unique<Circle_model>(
            Circle {4.1}, Cout_draw_strategy{}
        )
    );

    for (auto const& shape : shapes) {
        shape->draw();
    }

    std::cout.rdbuf(cout_buf);
    std::string_view expected {
        "circle(2.3)\n"
        "square(1.2)\n"
        "circle(4.1)\n"
    };
    EXPECT_EQ(sout.str(), expected);
}
