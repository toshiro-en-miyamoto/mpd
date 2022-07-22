#ifndef _SHAPE_VISITOR_H_
#define _SHAPE_VISITOR_H_

class circle;
class square;

class shape_visitor
{
public:
    virtual ~shape_visitor() = default;

    virtual void visit(circle const&) const = 0;
    virtual void visit(square const&) const = 0;
};

#endif
