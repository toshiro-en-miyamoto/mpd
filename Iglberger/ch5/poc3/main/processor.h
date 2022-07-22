#ifndef _CALC_PROCESSOR_H_
#define _CALC_PROCESSOR_H_

#include "command.h"
#include <stack>
#include <memory>

namespace calc {

class processor
{
public:
    void compute(std::unique_ptr<command>);
    void undo_last();
    int  result() const;
    void clear();
private:
    using command_stack = std::stack<std::unique_ptr<command>>;
    command_stack stack_;
    int current_{};
};

}

#endif
