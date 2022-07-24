#include <gtest/gtest.h>
#include <sstream>
#include <iostream>
#include <string_view>

#include <memory>

struct Entity
{
    ~Entity() { std::cout << "deleting" << '\n'; }
};

TEST(ch7, poc2)
{
    std::ostringstream sout;
    auto cout_buf = std::cout.rdbuf();
    std::cout.rdbuf(sout.rdbuf());

    {
        std::shared_ptr<Entity> e {
            new Entity{},
            [](Entity* pe) { delete pe; }
        };
    }
    {
        auto sh = std::make_shared<Entity>();
    }

    std::cout.rdbuf(cout_buf);
    std::string_view expected {
        "deleting\n"
        "deleting\n"
    };
    EXPECT_EQ(sout.str(), expected);
}
