#include "circle.h"

void Circle::draw() const
{
    this->out() << "Circle(" << this->radius() << ")\n";
}
