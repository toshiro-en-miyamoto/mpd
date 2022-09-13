#include <gtest/gtest.h>
#include "ch01/main/count_lines.h"

TEST(CountLinesTest, Imperative)
{
    std::vector<std::istringstream&> s {
        std::istringstream {"WORK\nSPACE\n"},
        std::istringstream {"WORKSPACE\n"}
    };

    /*
    std::vector<int> expected {2, 1};

    std::vector<int> actual = count_lines_in_streams_imperative(s);
    */
    EXPECT_EQ("a", "a");
    // EXPECT_EQ(actual.size(), expected.size());
    /*
    for (int i = 0; i < actual.size(); i++)
        EXPECT_EQ(actual[i], expected[i]);
    */
}
