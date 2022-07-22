#include <vector>
#include <memory>
#include <iostream>

class shape;
using Shapes = std::vector<std::unique_ptr<shape>> const&;
void draw_all_shapes(Shapes shapes, std::ostream& out = std::cout);
