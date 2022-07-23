#include <gtest/gtest.h>
#include <vector>
#include "ch6/poc3/main/traverse.h"

TEST(ch5, poc3)
{
    const std::vector<int> v {1, 2, 3, 4};
    traverse<>(v);
}
