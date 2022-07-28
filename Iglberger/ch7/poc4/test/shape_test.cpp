#include <gtest/gtest.h>
#include "ch7/poc4/main/circle.h"
#include "ch7/poc4/main/shape.h"
#include <sstream>
#include <iostream>
#include <string_view>

TEST(ch7, poc4)
{
    std::ostringstream sout;
    auto cout_buf = std::cout.rdbuf();
    std::cout.rdbuf(sout.rdbuf());

    Circle circle {3.14};
    auto circle_drawer = [](Circle const& c)
    { std::cout << "circle(" << c.radius() << ")\n"; };

    draw(Shape_const_ref {circle, circle_drawer});

    std::cout.rdbuf(cout_buf);
    std::string_view expected {
        "circle(3.14)\n"
    };
    EXPECT_EQ(sout.str(), expected);
}

#include <vector>

TEST(ch7, poc4a)
{
    std::ostringstream sout;
    auto cout_buf = std::cout.rdbuf();
    std::cout.rdbuf(sout.rdbuf());

    auto circle_drawer = [](Circle const& c)
    { std::cout << "circle(" << c.radius() << ")\n"; };

    std::vector<Shape_const_ref> shapes {};
    shapes.emplace_back(Shape_const_ref {Circle {3.14}, circle_drawer});
    shapes.emplace_back(Shape_const_ref {Circle {4.15}, circle_drawer});

    for (auto const& shape : shapes)
        draw(shape);

    std::cout.rdbuf(cout_buf);
    std::string_view expected {
        "circle(1.81543e-312)\n"
        "circle(1.81543e-312)\n"
    };

    // undefined behavior
    // EXPECT_EQ(sout.str(), expected);
}

TEST(ch7, poc4b)
{
    std::ostringstream sout;
    auto cout_buf = std::cout.rdbuf();
    std::cout.rdbuf(sout.rdbuf());

    auto circle_drawer = [](Circle const& c)
    { std::cout << "circle(" << c.radius() << ")\n"; };

    std::vector<Shape_const_ref> shapes {};
    Circle c1 {3.14};
    Circle c2 {4.15};
    shapes.emplace_back(Shape_const_ref {c1, circle_drawer});
    shapes.emplace_back(Shape_const_ref {c2, circle_drawer});

    for (auto const& shape : shapes)
        draw(shape);

    std::cout.rdbuf(cout_buf);
    std::string_view expected {
        "circle(3.14)\n"
        "circle(4.15)\n"
    };
    EXPECT_EQ(sout.str(), expected);
}
