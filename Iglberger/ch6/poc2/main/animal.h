#ifndef _ANIMAL_H_
#define _ANIMAL_H_

#include <memory>
#include <string>

class animal
{
public:
    virtual ~animal() = default;
    virtual std::unique_ptr<animal> clone() const = 0;

    virtual std::string sound() const = 0;
};

#endif
