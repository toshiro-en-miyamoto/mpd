#ifndef _SHEEP_H_
#define _SHEEP_H_

#include "animal.h"

class sheep : public animal
{
public:
    explicit sheep(std::string name) : name_{std::move(name)} {}
    std::unique_ptr<animal> clone() const override;

    std::string sound() const override;

private:
    std::string name_;
};

#endif
