#ifndef _SHAPE_H_
#define _SHAPE_H_

#include "circle.h"
#include "square.h"
#include <variant>
#include <vector>

using shape = std::variant<circle, square>;
using shapes = std::vector<shape>;

#endif
