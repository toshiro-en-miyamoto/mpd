# Functional Thinking in Action

Actions depend on when they are run, so it is important to make sure they run in the right order.

The hard fact of distributed systems is that uncoordinated timelines run in weird orders.

You will need to focus on the actions (things that depend on time) to make sure they happen in the right order.

- Timelines are uncoordinated by default.
- You cannot rely on the duration of actions.
- Bad timing, however rare, can happen in production.

## Cutting the timeline: Making the robots wait for each other

*Cutting a timeline* is a way to coordinate multiple timelines working in parallel, implemented as a *higher-order operation*. Each of the timelines will do work independently, then wait for each other to finish when they are done. That way, it doesn’t matter which one finishes first.

- Cutting a timeline makes it easy to reason about the pieces in isolation
- Working with timeline diagrams helps you understand how the system works through time


