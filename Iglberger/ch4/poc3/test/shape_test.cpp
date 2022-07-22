#include <gtest/gtest.h>
#include "ch4/poc3/main/shape.h"
#include "ch4/poc3/main/circle.h"
#include "ch4/poc3/main/square.h"
#include "ch4/poc3/main/draw_all_shapes.h"

#include <vector>
#include <memory>
#include <sstream>
#include <string_view>

TEST(ch4, poc3)
{
    std::ostringstream sout;

    std::vector<std::unique_ptr<shape>> shapes;
    shapes.emplace_back(std::make_unique<circle>(2.3));
    shapes.emplace_back(std::make_unique<square>(1.2));
    shapes.emplace_back(std::make_unique<circle>(4.1));
    draw_all_shapes(shapes, sout);

    constexpr std::string_view expected{
        "circle(2.3)\n"
        "square(1.2)\n"
        "circle(4.1)\n"
    };
    EXPECT_EQ(sout.str(), expected);
}
