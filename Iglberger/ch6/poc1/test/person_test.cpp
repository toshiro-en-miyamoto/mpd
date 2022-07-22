#include <gtest/gtest.h>
#include "ch6/poc1/main/person.h"
#include <string>

TEST(ch6, poc1)
{
    const std::string forename{"John"};
    const std::string surname ("Doe");
    person p{forename, surname};
    EXPECT_EQ(p.forename(), forename);
    EXPECT_EQ(p.surname(),  surname);
}
