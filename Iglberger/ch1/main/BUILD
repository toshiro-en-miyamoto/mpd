load("@rules_cc//cc:defs.bzl", "cc_library")

cc_library(
    name = "item",
    srcs = ["book.cpp", "conference.cpp"],
    hdrs = ["item.h", "book.h", "conference.h"],
    copts = ["-std=c++20"],
    visibility = ["//ch1/test:__pkg__"],
)
