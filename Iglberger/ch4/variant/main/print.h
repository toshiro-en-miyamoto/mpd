#include <iostream>

class print
{
public:
    explicit print(std::ostream& out) : out_{out} {}

    void operator()(int value) const
    { out_ << "int: " << value << '\n'; }

    void operator()(double value) const
    { out_ << "double: " << value << '\n'; }

    void operator()(std::string const& value) const
    { out_ << "string: " << value << '\n'; }

private:
    std::ostream& out_;
};
