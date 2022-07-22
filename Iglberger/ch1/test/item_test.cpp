#include <gtest/gtest.h>
#include "ch1/main/book.h"
#include "ch1/main/conference.h"
#include <numeric>

TEST(ch01, item) {
    std::vector<std::unique_ptr<Item>> items{};
    items.emplace_back(std::make_unique<Book>("book", "Spock", 200.00));
    items.emplace_back(std::make_unique<Conference>("conf", 500.00));

    double const total_price =
    std::accumulate(begin(items), end(items), double{},
        [](double accu, auto const& item) {
            return accu + item->price();
        }
    );

    EXPECT_EQ(total_price, 700.00);
}
