load("@rules_cc//cc:defs.bzl", "cc_library")

cc_library(
    name = "shape",
    srcs = [
        "draw_circle.cpp",
        "draw_square.cpp",
        "draw_all_shapes.cpp",
    ],
    hdrs = [
        "point.h", "shape.h",
        "circle.h", "draw_circle.h",
        "square.h", "draw_square.h",
        "draw_all_shapes.h",
    ],
    copts = ["-std=c++20"],
    visibility = ["//ch4/poc1/test:__pkg__"],
)
