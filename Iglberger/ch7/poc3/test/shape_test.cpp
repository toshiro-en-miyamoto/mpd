#include <gtest/gtest.h>
#include "ch7/poc3/main/circle.h"
#include "ch7/poc3/main/square.h"
#include "ch7/poc3/main/shape.h"
#include <vector>
#include <memory>
#include <sstream>
#include <iostream>
#include <string_view>

TEST(ch7, poc3)
{
    std::ostringstream sout;
    auto cout_buf = std::cout.rdbuf();
    std::cout.rdbuf(sout.rdbuf());

    auto circle_drawer = [](Circle const& c)
    { std::cout << "circle(" << c.radius() << ")\n"; };

    Shape shape1 {Circle{3.14}, circle_drawer};
    draw(shape1);

    Shape shape2 {shape1};
    draw(shape2);

    std::cout.rdbuf(cout_buf);
    std::string_view expected {
        "circle(3.14)\n"
        "circle(3.14)\n"
    };
    EXPECT_EQ(sout.str(), expected);
}
