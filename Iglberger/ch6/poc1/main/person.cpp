#include "person.h"

struct person::impl
{
    std::string forename;
    std::string surname;
    // ... potentially many more data members
};

person::person(
    std::string const& forename,
    std::string const& surname
)
: pimpl_{std::make_unique<impl>(forename, surname)}
{}

person::~person() = default;

person::person(person const& other)
: pimpl_{std::make_unique<impl>(*other.pimpl_)}
{}

person& person::operator=(person const& other)
{
    *pimpl_ = *other.pimpl_;
    return *this;
}

person::person(person&& other)
: pimpl_{std::make_unique<impl>(std::move(*other.pimpl_))}
{}

person& person::operator=(person&& other)
{
    *pimpl_ = std::move(*other.pimpl_);
    return *this;
}

std::string const& person::forename() const
{ return pimpl_->forename; }

std::string const& person::surname() const
{ return pimpl_->surname; }
