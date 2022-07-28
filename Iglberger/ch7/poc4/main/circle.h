#ifndef _CIRCLE_H_
#define _CIRCLE_H_

class Circle
{
public:
    explicit Circle(double radius) : radius_ {radius} {}
    double radius() const { return radius_; }
private:
    double radius_;
};

#endif
