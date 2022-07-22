#include "item.h"
#include <string>

class Conference : public Item
{
public:
    Conference(std::string name, double price);
    std::string const& name() const;
    double price() const override;
private:
    std::string name_;
    double price_;
};