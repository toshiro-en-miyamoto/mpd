#include <gtest/gtest.h>
#include "ch2/main/widget.h"

TEST(ch02, widget) {
    widget::Widget<std::string> w1{ "Hello" };
    widget::Widget<std::string> w2{ "World" };
    widget::swap(w1, w2);
    EXPECT_EQ(w1.value, "World");
    EXPECT_EQ(w2.value, "Hello");
}
