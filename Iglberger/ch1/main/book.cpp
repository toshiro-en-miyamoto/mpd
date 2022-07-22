#include "book.h"

Book::Book(std::string title, std::string author, double price)
    : title_(std::move(title))
    , author_(std::move(author))
    , price_(price)
{}
std::string const& Book::title() const { return title_; }
std::string const& Book::author() const { return author_; }
double Book::price() const { return price_; }
