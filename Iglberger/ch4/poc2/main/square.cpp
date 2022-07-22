#include "square.h"

void Square::draw() const
{
    this->out() << "Square(" << this->side() << ")\n";
}
