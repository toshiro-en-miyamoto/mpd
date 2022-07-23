#ifndef _COUT_DRAWER_H_
#define _COUT_DRAWER_H_

class Circle;
class Square;

class Cout_draw_strategy
{
public:
    void operator()(Circle const&) const;
    void operator()(Square const&) const;
};

#endif
