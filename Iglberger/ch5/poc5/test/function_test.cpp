#include <gtest/gtest.h>
#include "ch5/poc5/main/foo.h"
#include <functional>
#include <sstream>
#include <string_view>

TEST(ch5, default_function_throw)
{
    std::function<void(int)> f{};
    EXPECT_THROW(f(1), std::bad_function_call);
}

TEST(ch5, function_lambda)
{
    std::stringstream sout;
    std::function<void(int)> f = [&](int i){
        sout << "lambda: " << i;
    };
    f(1);

    std::string_view expected{
        "lambda: 1"
    };
    EXPECT_EQ(sout.str(), expected);
}

TEST(ch5, function_assignment_external)
{
    std::stringstream sout;
    std::function<void(int, std::ostream&)> f{foo};
    f(2, sout);

    std::string_view expected{
        "foo: 2"
    };
    EXPECT_EQ(sout.str(), expected);
}

TEST(ch5, function_assignment_lambda)
{
    std::stringstream sout;
    std::function<void(int)> f = [&](int i){
        sout << "lambda: " << i;
    };

    auto g = f;

    f = [&](int i){
        sout << "another lambda: " << i;
    };

    g(3);

    std::string_view expected{
        "lambda: 3"
    };
    EXPECT_EQ(sout.str(), expected);
}
