#include <gtest/gtest.h>
#include "ch4/poc4/main/circle.h"
#include "ch4/poc4/main/square.h"
#include "ch4/poc4/main/shape.h"
#include "ch4/poc4/main/draw_all_shapes.h"
#include <sstream>
#include <string_view>

TEST(ch4, poc4)
{
    std::ostringstream sout;

    shapes shapes_;
    shapes_.emplace_back(circle{2.3});
    shapes_.emplace_back(square{1.2});
    shapes_.emplace_back(circle{4.1});
    draw_all_shapes(shapes_, sout);

    constexpr std::string_view expected{
        "circle(2.3)\n"
        "square(1.2)\n"
        "circle(4.1)\n"
    };
    EXPECT_EQ(sout.str(), expected);
}
