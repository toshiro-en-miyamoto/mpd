cc_test(
    name = "hello-test",
    size = "small",
    srcs = ["hello-test.cpp"],
    copts = ["-std=c++20"],
    deps = [
        "@com_google_googletest//:gtest_main",
        "//ch01/main:hello-greet",
    ],
)
