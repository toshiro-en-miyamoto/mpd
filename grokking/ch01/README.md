# Welcome to Glokking Simplicity

## The three categories of code in FP

*Actions*: Anything that depends on when it is run, or how many times it is run, or both, is an action. If I send an urgent email today, it’s much different from sending it next week. And of course, sending the same email 10 times is different from sending it 0 times or 1 time.

- Tools for safely changing state over time
- Ways to guarantee ordering
- Tools to ensure actions happen exactly once

*Calculations*: are computations from input to output. They always give the same output when you give them the same input. You can call them anytime, anywhere, and it won’t affect anything outside of them. That makes them really easy to test and safe to use without worrying about how many times or when they are called.

- Static analysis to aid correctness
- Mathematical tools that work well for software
- Testing strategies

*Data*: is recorded facts about events. We distinguish data because it is not as complex as executable code. It has well-understood properties. Data is interesting because it is meaningful without being run. It can be interpreted in multiple ways. Take a restaurant receipt as an example: It can be used by the restaurant manager to determine which food items are popular. And it can be used by the customer to track their dining-out budget.

- Ways to organize data for efficient access
- Disciplines to keep records long term
- Principles for capturing what is important using data

## How does distinguishing actions, calculations, and data help us?

Data and calculations do not depend on how many times they are run or accessed. By moving more of our code into data and calculations, we sweep that code clean of the problems inherent in distributed systems.



