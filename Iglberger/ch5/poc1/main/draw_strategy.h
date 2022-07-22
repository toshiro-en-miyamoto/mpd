#ifndef _DRAW_STRATEGY_H_
#define _DRAW_STRATEGY_H_

template<typename T> class draw_strategy
{
public:
    virtual ~draw_strategy() = default;
    virtual void draw(T const&) const = 0;
};

#endif
