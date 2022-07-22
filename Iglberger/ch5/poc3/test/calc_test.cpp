#include <gtest/gtest.h>
#include "ch5/poc3/main/processor.h"
#include "ch5/poc3/main/add.h"
#include "ch5/poc3/main/sub.h"

TEST(ch5, poc3)
{
    calc::processor proc{};
    proc.compute(std::move(std::make_unique<calc::add>(3)));
    proc.compute(std::move(std::make_unique<calc::add>(7)));
    proc.compute(std::move(std::make_unique<calc::sub>(4)));
    proc.compute(std::move(std::make_unique<calc::sub>(2)));

    EXPECT_EQ(proc.result(), 4);

    proc.undo_last();
    EXPECT_EQ(proc.result(), 6);
}
