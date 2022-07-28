#include <memory>

class Shape_const_ref
{
public:
    template<typename Shape_t, typename Draw_strategy>
    Shape_const_ref(Shape_t const& shape, Draw_strategy& drawer)
    : shape_ {std::addressof(shape)}
    , drawer_ {std::addressof(drawer)}
    , draw_ {[](void const* shape_bytes, void const* drawer_bytes){
        auto const* shape = static_cast<Shape_t const*>(shape_bytes);
        auto const* drawer = static_cast<Draw_strategy const*>(drawer_bytes);
        (*drawer)(*shape);
    }} {}

private:
    friend void draw(Shape_const_ref const& shape)
    { shape.draw_(shape.shape_, shape.drawer_); }

    void const* shape_ {nullptr};
    void const* drawer_ {nullptr};

    using Draw_operation = void(void const*, void const*);
    Draw_operation* draw_ {nullptr};
};
