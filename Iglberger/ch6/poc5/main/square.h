#ifndef _SQUARE_H_
#define _SQUARE_H_

class Square
{
public:
    explicit Square(double side) : side_ {side} {}
    double side() const { return side_; }
private:
    double side_;
};

#endif
