#include <span>
#include <ostream>

void print(std::span<int> s, std::ostream& out)
{
    out << '(';
    for (int i : s) {
        out << ' ' << i;
    }
    out << " )";
}
