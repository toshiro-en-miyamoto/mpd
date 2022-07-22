#include "conference.h"

Conference::Conference(std::string name, double price)
    : name_(std::move(name))
    , price_(price)
{}
std::string const& Conference::name() const { return name_; }
double Conference::price() const { return price_; }
