#include <string>
#include <memory>

class person
{
public:
    person(
        std::string const& forename,
        std::string const& surname
    );

    ~person();

    person(person const& other);
    person& operator=(person const& other);

    person(person&& other);
    person& operator=(person&& other);

    std::string const& forename() const;
    std::string const& surname() const;

private:
    struct impl;
    std::unique_ptr<impl> const pimpl_;
};
