#include <gtest/gtest.h>
#include "ch4/variant/main/print.h"

#include <sstream>
#include <string_view>
#include <variant>

TEST(ch4, variant)
{
    std::ostringstream sout;
    const print p{sout};

    std::variant<int,double,std::string> v{};
    std::visit(p, v);
    v = 42;
    std::visit(p, v);
    v = 3.14;
    std::visit(p, v);
    v = "Variant";
    std::visit(p, v);

    constexpr std::string_view expected{
        "int: 0\n"
        "int: 42\n"
        "double: 3.14\n"
        "string: Variant\n"
    };
    EXPECT_EQ(sout.str(), expected);
}
