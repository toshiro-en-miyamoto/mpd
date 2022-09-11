#include <gtest/gtest.h>
#include "ch01/main/count-lines.h"
#include <sstream>

TEST(CountLinesTest, Imperative)
{
    const std::string
    str1 {"W\nO\nR\nK\nSPACE\n"},
    str2 {".gitignore\n"};

    std::vector<std::istream> s {
        std::istringstream(str1),
        std::istringstream(str2)
    };

    std::vector<int> expected {5, 1};

    std::vector<int> actual = count_lines_in_streams_imperative(s);

    EXPECT_EQ(actual, expected);
}
