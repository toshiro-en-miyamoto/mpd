#include "processor.h"

namespace calc {

void processor::compute(std::unique_ptr<command> c)
{
    current_ = c->execute(current_);
    stack_.push(std::move(c));
}

void processor::undo_last()
{
    if(stack_.empty()) return;
    auto c = std::move(stack_.top());
    stack_.pop();
    current_ = c->undo(current_);
}

int processor::result() const { return current_;}

void processor::clear()
{
    current_ = 0;
    command_stack{}.swap(stack_);
}

}
