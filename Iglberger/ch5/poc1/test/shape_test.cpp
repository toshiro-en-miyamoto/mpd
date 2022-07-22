#include <gtest/gtest.h>
#include <sstream>
#include <vector>
#include <memory>
#include "ch5/poc1/main/circle.h"
#include "ch5/poc1/main/square.h"
#include "ch5/poc1/main/ostream_circle_strategy.h"
#include "ch5/poc1/main/ostream_square_strategy.h"

TEST(ch5, poc1)
{
    std::ostringstream sout;

    std::vector<std::unique_ptr<shape>> shapes;
    shapes.emplace_back(std::make_unique<circle>(
        2.3, std::make_unique<ostream_circle_strategy>(sout)
    ));
    shapes.emplace_back(std::make_unique<square>(
        1.2, std::make_unique<ostream_square_strategy>(sout)
    ));
    shapes.emplace_back(std::make_unique<circle>(
        4.1, std::make_unique<ostream_circle_strategy>(sout)
    ));

    for (auto const& shape : shapes)
    {
        shape->draw();
    }

    constexpr std::string_view expected{
        "circle(2.3)\n"
        "square(1.2)\n"
        "circle(4.1)\n"
    };
    EXPECT_EQ(sout.str(), expected);
}
