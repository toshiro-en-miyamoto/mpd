# Distinguishing Actions, Calculations, and Data

*Actions*: Depend on how many times or when it is run

- Also called: functions with side-effects, side-effecting functions, impure functions
- Examples: Send an email, read from a database

*Calculations*: Computations from input to output

- Also called: pure functions, mathematical functions
- Examples: Find the maximum number, check if an email address is valid

*Data*: Facts about events

- Examples: The email address a user gave us, the dollar amount read from a bank’s API

> Calculations are *referentially transparent* because a call to a calculation can be replaced by its result. For instance, `+` is a calculation. `2 + 3` always results in `5`, so you could replace the code `2 + 3` with `5` and have an equivalent program. That means you can call `2 + 3` zero, one, or more times and get the same result.

## Actions, calculations, and data apply to any situation

| ACD | Grocery Shopping  | Note
|-----|-------------------|------
| A   | Check fridge      | It matters when I look in the fridge. Tommorow, there may be less milk.
| A   | Drive to store    | Driving twice will use twice as much gas.
| A   | Buy what you need | When I buy broccoli, no one else can after me, so when I buy matters.
| A   | Drive home        | I can't drive home when I'm already at home, so it matters when.

We can’t accept that everything is an action.

- Check fridge: Checking the fridge is an *action* because when we check matters. The information about what food we have is *data*. We will call it a *current inventory*.
- Driving to store: It’s definitely an *action*. However, there are some pieces of *data* in there; for instance, the location of the store and directions to get there.
- Buying stuff is definitely an action, but you could break this down further.
  - *data*: Current inventory
  - *data*: Desired inventory
  - *calculation*: Inventory *minus* (`-`)
  - *data*: Shopping list = desired inventory `-` current inventory
  - *action*: Buy from list

Calculations are often decisions, such as this one, which decides what to buy. Separating actions from calculations is like separating deciding what to buy from buying it.

| Actions        | Calculations      | Data
|----------------|-------------------|---
| Check fridge   |                   | Current inventory
| Drive to store |                   |
|                |                   | Desired inventory
|                | Inventory *minus* | Shopping list
| Buy from list  |  |
| Drive home     |  |

- We can apply the ACD perspective to any situation
- Actions can hide actions, calculations, and data
- Calculations can be composed of smaller calculations and data
- Data can only be composed of more data

Calculations often happen *in our heads*.

- We can ask ourselves, "Do we need to make any decisions? Is there anything we can plan ahead?" *Decisions* and *planning* make good candidates for calculations.



