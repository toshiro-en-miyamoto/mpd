#include "shape_visitor.h"
#include "circle.h"
#include "square.h"
#include <iostream>

class draw : public shape_visitor
{
public:
    explicit draw(std::ostream& out) : out_{out} {}

    void visit(circle const& c) const override
    {
        out_ << "circle(" << c.radius() << ")\n";
    }

    void visit(square const& s) const override
    {
        out_ << "square(" << s.side() << ")\n";
    }

private:
    std::ostream& out_;
};
