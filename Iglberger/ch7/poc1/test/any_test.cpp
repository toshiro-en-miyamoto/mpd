#include <gtest/gtest.h>
#include <sstream>
#include <iostream>
#include <string_view>

#include <any>
#include <utility>

TEST(ch7, poc1)
{
    std::ostringstream sout;
    auto cout_buf = std::cout.rdbuf();
    std::cout.rdbuf(sout.rdbuf());

    // cppreference: any_cast
    auto a = std::any(12);
    std::cout << std::any_cast<int>(a) << '\n';

    try {
        std::cout << std::any_cast<std::string>(a) << '\n';
    } catch(const std::bad_any_cast& e) {
        std::cout << e.what() << '\n';
    }

    a = std::string("hello");

    auto& ra = std::any_cast<std::string&>(a);
    ra[1] = 'o';
    std::cout << ra << '\n';

    std::cout << std::any_cast<std::string const&>(a) << '\n';

    auto b = std::any_cast<std::string&&>(std::move(a));
    std::cout << std::any_cast<std::string const&>(a) << '\n';
    std::cout << std::any_cast<std::string const&>(b) << '\n';

    std::cout.rdbuf(cout_buf);
    std::string_view expected {
        "12\n"
        "bad any_cast\n"
        "hollo\n"
        "hollo\n"
        "\n"
        "hollo\n"
    };
    EXPECT_EQ(sout.str(), expected);
}
