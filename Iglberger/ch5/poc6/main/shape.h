#ifndef _SHAPE_H_
#define _SHAPE_H_

class shape
{
public:
    virtual ~shape() = default;
    virtual void draw() const = 0;
};

#endif
