#ifndef _SHAPE_H_
#define _SHAPE_H_

#include <functional>
#include <utility>

class Shape_concept
{
public:
    virtual ~Shape_concept() = default;
    virtual void draw() const = 0;
    // potentially more polymorphic operations
};

struct Default_drawer
{
    template<typename T>
    void operator()(T const& obj) const { draw(obj); }
};

template<typename Shape_t
        ,typename Draw_strategy = Default_drawer>
class Shape_model : public Shape_concept
{
public:
    explicit Shape_model(Shape_t shape, Draw_strategy drawer)
    : shape_ { std::move(shape) }
    , drawer_ { std::move(drawer) }
    {}

    void draw() const override { drawer_(shape_); }

private:
    Shape_t shape_;
    Draw_strategy drawer_;
};

#endif
