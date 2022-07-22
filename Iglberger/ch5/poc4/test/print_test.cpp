#include <gtest/gtest.h>
#include <vector>
#include <sstream>
#include <string_view>
#include "ch5/poc4/main/print.h"

TEST(ch5, poc4_expected)
{
    std::vector<int> v{1, 2, 3, 4};
    std::span<int> const s{v};

    std::ostringstream sout;
    s[2] = 99;
    print(s, sout);
    constexpr std::string_view expected{
        "( 1 2 99 4 )"
    };   
    EXPECT_EQ(sout.str(), expected);
}

TEST(ch5, poc4_not_expected)
{
    std::vector<int> v{1, 2, 3, 4};
    std::span<int> const s{v};

    std::ostringstream sout;
    v = {5, 6, 7, 8, 9};
    s[2] = 99;
    print(s, sout);
    constexpr std::string_view expected{
        "( 5 6 99 8 9 )"
    };   
    EXPECT_NE(sout.str(), expected);
}

TEST(ch5, vector)
{
    std::vector<int> v1{1, 2, 3, 4, 5};

    auto v2{v1};
    EXPECT_EQ(v1, v2);
    EXPECT_NE(v1.data(), v2.data());

    v2[2] = 99;
    EXPECT_NE(v1, v2);

    auto const v3{v1};
    // v3[2] == 99;     compilation error
}
