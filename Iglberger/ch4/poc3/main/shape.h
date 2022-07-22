#ifndef _SHAPE_H_
#define _SHAPE_H_

class shape_visitor;

class shape
{
public:
    virtual ~shape() = default;
    virtual void accept(shape_visitor const&) = 0;
};

#endif
