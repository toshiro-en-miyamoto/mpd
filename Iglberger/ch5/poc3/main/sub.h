#ifndef _CALC_COMMAND_SUB_H_
#define _CALC_COMMAND_SUB_H_

#include "command.h"

namespace calc {

class sub : public command
{
public:
    explicit sub(int operand) : operand_{operand} {}
    int execute(int i) const override { return i - operand_; }
    int undo   (int i) const override { return i + operand_; }
private:
    int operand_{};
};

}

#endif
