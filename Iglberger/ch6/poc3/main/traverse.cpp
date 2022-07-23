#include "traverse.h"
#include <iterator>

template<typename Range>
void traverse(Range const& range)
{
    auto first {std::begin(range)};
    auto last {std::end(range)};
    for (; first != last; ++first) {
        auto&& element = *first;
    }
}
