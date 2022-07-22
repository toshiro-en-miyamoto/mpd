load("@rules_cc//cc:defs.bzl", "cc_library")

cc_library(
    name = "shape",
    srcs = [
        "processor.cpp",
    ],
    hdrs = [
        "processor.h", "command.h",
        "add.h", "sub.h",
    ],
    copts = ["-std=c++20"],
    visibility = ["//ch5/poc3/test:__pkg__"],
)
