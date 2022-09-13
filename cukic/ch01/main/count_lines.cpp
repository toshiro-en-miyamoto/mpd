#include "count_lines.h"
#include <algorithm>
#include <iterator>

std::vector<int>
count_lines_in_streams_imperative(std::vector<std::istringstream&>& streams)
{
    std::vector<int> results;
    for (auto& stream : streams) {
        int line_count = std::count(
            std::istreambuf_iterator<char>(stream),
            std::istreambuf_iterator<char>(),
            '\n'
        );

        results.push_back(line_count);
    }
    return results;
}
