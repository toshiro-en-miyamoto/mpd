load("@rules_cc//cc:defs.bzl", "cc_binary", "cc_library")

cc_library(
    name = "count-lines",
    srcs = ["count-lines.cpp"],
    hdrs = ["count-lines.h"],
    copts = ["-std=c++20"],
    visibility = ["//ch01/test:__pkg__"],
)

cc_library(
    name = "hello-greet",
    srcs = ["hello-greet.cpp"],
    hdrs = ["hello-greet.h"],
    copts = ["-std=c++20"],
    visibility = ["//ch01/test:__pkg__"],
)
