load("@rules_cc//cc:defs.bzl", "cc_library")

cc_library(
    name = "shape",
    srcs = [
        "draw_all_shapes.cpp",
    ],
    hdrs = [
        "point.h", "shape.h",
        "circle.h", "square.h",
        "draw.h", "draw_all_shapes.h",
    ],
    copts = ["-std=c++20"],
    visibility = ["//ch4/poc4/test:__pkg__"],
)
