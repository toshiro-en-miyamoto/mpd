load("@rules_cc//cc:defs.bzl", "cc_library")

cc_library(
    name = "animal",
    hdrs = [
        "animal.h",
        "sheep.h",
    ],
    srcs = [
        "sheep.cpp",
    ],
    copts = ["-std=c++20"],
    visibility = ["//ch6/poc2/test:__pkg__"],
)
