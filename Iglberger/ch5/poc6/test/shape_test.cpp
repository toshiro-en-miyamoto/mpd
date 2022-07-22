#include <gtest/gtest.h>
#include "ch5/poc6/main/circle.h"
#include "ch5/poc6/main/ostream_circle_strategy.h"
#include <sstream>
#include <vector>
#include <memory>
#include <string_view>

TEST(ch5, poc6)
{
    std::stringstream sout;
    std::vector<std::unique_ptr<shape>> shapes{};

    shapes.emplace_back(
        std::make_unique<circle>(2.3, ostream_circle_strategy{sout})
    );

    for (auto const& s : shapes)
    { s->draw(); }

    constexpr std::string_view expected{
        "circle(2.3)\n"
    };
    EXPECT_EQ(sout.str(), expected);
}
