#include "cout_drawer.h"
#include "circle.h"
#include "square.h"
#include <iostream>

void Cout_draw_strategy::operator()(Circle const& c) const
{ std::cout << "circle(" << c.radius() << ")\n"; }
void Cout_draw_strategy::operator()(Square const& s) const
{ std::cout << "square(" << s.side() << ")\n"; }
