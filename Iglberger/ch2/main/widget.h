#ifndef _WIGET_H_
#define _WIGET_H_

#include <utility>

namespace widget {

template<typename T>
struct Widget
{
    T value;
};

template<typename T>
void swap(Widget<T>& lhs, Widget<T>& rhs)
{
//    using std::swap;
    std::swap(lhs.value, rhs.value);
}

}

#endif