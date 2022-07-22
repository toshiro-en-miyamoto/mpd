#ifndef _ITEM_H_
#define _ITEM_H_

class Item
{
public:
    virtual ~Item() = default;
    virtual double price() const = 0;
};

#endif