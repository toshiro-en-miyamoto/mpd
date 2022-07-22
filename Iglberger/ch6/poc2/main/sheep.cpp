#include "sheep.h"

std::unique_ptr<animal> sheep::clone() const
{
    return std::make_unique<sheep>(*this);
}

std::string sheep::sound() const
{
    return name_ + ":baa\n";
}
