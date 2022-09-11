load("@rules_cc//cc:defs.bzl", "cc_binary", "cc_library")

cc_library(
    name = "hello-greet",
    srcs = ["hello-greet.cpp"],
    hdrs = ["hello-greet.h"],
    copts = ["-std=c++20"],
    visibility = ["//ch01/test:__pkg__"],
)
