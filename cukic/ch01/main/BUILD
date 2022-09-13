load("@rules_cc//cc:defs.bzl", "cc_binary", "cc_library")

cc_library(
    name = "count-lines",
    srcs = ["count_lines.cpp"],
    hdrs = ["count_lines.h"],
    copts = ["-std=c++20"],
    visibility = ["//ch01/test:__pkg__"],
)
