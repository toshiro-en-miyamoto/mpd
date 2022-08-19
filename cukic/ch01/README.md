# Introduction to functional programming

## 1.2 Pure functions

Suppose you’re making a text editor, and you’re storing the text the user has written in a variable. [...] What happens if the user changes part of the text while the program is saving it?

This example involves two clearly separate user actions: saving the typed text and typing the text. These should be able to be performed independently of one another. Having multiple actions that might be executed at the same time and that share a mutable state *creates a dependency* between them, opening you to issues like the ones just described.

[Mutable states] create dependencies between different parts of the function, making it difficult to factor out parts of the function into a separate function.

### 1.2.1 Avoiding mutable state

[Consider] an algorithm that counts newlines in a collection of files. The function that counts newlines should always return the same array of integers when invoked over the same list of files (provided the files weren’t changed by an external entity). This means the function could be implemented as a pure function.


