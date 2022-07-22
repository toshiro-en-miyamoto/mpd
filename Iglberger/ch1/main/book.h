#include "item.h"
#include <string>

class Book : public Item
{
public:
    Book(std::string title, std::string author, double price);
    std::string const& title() const;
    std::string const& author() const;
    double price() const override;

private:
    std::string title_;
    std::string author_;
    double price_;
};
