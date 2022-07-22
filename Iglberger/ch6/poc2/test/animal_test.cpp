#include <gtest/gtest.h>
#include "ch6/poc2/main/sheep.h"
#include <string_view>

TEST(ch6, dolly)
{
    std::string_view expected{"Dolly:baa\n"};

    std::unique_ptr<animal> dolly = std::make_unique<sheep>("Dolly");
    EXPECT_EQ(dolly->sound(), expected);

    std::unique_ptr<animal> clone = dolly->clone();
    EXPECT_EQ(clone->sound(), expected);
}
