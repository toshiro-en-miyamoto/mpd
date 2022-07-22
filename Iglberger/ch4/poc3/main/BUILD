load("@rules_cc//cc:defs.bzl", "cc_library")

cc_library(
    name = "shape",
    srcs = [
        "draw_all_shapes.cpp",
    ],
    hdrs = [
        "shape_visitor.h",
        "draw.h",
        "draw_all_shapes.h",
        "shape.h",
        "circle.h", "square.h",
    ],
    copts = ["-std=c++20"],
    visibility = ["//ch4/poc3/test:__pkg__"],
)
