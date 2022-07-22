#ifndef _CALC_COMMAND_H_
#define _CALC_COMMAND_H_

namespace calc {

class command
{
public:
    virtual ~command() = default;
    virtual int execute(int i) const = 0;
    virtual int undo   (int i) const = 0;
};

}

#endif
