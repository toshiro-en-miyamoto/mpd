#include <gtest/gtest.h>
#include "ch4/poc2/main/draw_all_shapes.h"
#include "ch4/poc2/main/circle.h"
#include "ch4/poc2/main/square.h"
#include <vector>
#include <memory>
#include <sstream>
#include <string_view>

TEST(ch4, poc2)
{
    std::ostringstream sout;

    std::vector<std::unique_ptr<Shape>> shapes;
    shapes.emplace_back(std::make_unique<Circle>(2.3, sout));
    shapes.emplace_back(std::make_unique<Square>(1.2, sout));
    shapes.emplace_back(std::make_unique<Circle>(4.1, sout));
    draw_all_shapes(shapes);

    constexpr std::string_view expected{
        "Circle(2.3)\n"
        "Square(1.2)\n"
        "Circle(4.1)\n"
    };
    EXPECT_EQ(sout.str(), expected);
}
