#ifndef _SHAPE_H_
#define _SHAPE_H_

#include <memory>
#include <utility>

namespace detail {

class Shape_concept
{
public:
    virtual ~Shape_concept() = default;
    virtual void draw() const = 0;
    virtual std::unique_ptr<Shape_concept> clone() const = 0;
};

template<typename Shape_t
        ,typename Draw_strategy>
class Owning_shape_model : public Shape_concept
{
public:
    explicit Owning_shape_model(Shape_t shape, Draw_strategy drawer)
    : shape_ {std::move(shape)}, drawer_ {std::move(drawer)}
    {}

    void draw() const override { drawer_(shape_); }

    std::unique_ptr<Shape_concept> clone() const override
    {
        return std::make_unique<Owning_shape_model>(*this);
    }

private:
    Shape_t shape_;
    Draw_strategy drawer_;
};

} // namespace detail

class Shape
{
public:
    template<typename Shape_t, typename Draw_strategy>
    Shape(Shape_t shape, Draw_strategy drawer)
    {
        using Model = detail::Owning_shape_model<Shape_t, Draw_strategy>;
        pimpl_
        = std::make_unique<Model>(std::move(shape), std::move(drawer));
    }

    Shape(Shape const& other) : pimpl_ {other.pimpl_->clone()} {}
    Shape& operator=(Shape const& other)
    {
        Shape copy {other};
        pimpl_.swap(copy.pimpl_);
        return *this;
    }

    ~Shape() = default;
    Shape(Shape&&) = default;
    Shape& operator=(Shape&&) = default;

private:
    friend void draw(Shape const& shape) { shape.pimpl_->draw(); }
    std::unique_ptr<detail::Shape_concept> pimpl_;
};

#endif
