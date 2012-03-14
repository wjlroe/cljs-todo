## cljs-todo

A simple do list based on ClojurescriptOne.  I originally wrote this code by 
modifying the existing ClojurescriptOne, but I wanted to pull it out into a
standalone project to get a better sense for how much of the code is specific
to my application.

Quick break down of app specific code:

* **src/clj** - Majority copied directly from the sample app.  **api.clj** and **config.clj** have significant changes.
* **src/cljs-macros** - Changed enlive templates to reflect different html layout.
* **src/cljs** - This is where most of the changes took place.  history.clj and logging.clj were not changed.
* **src/leiningen** - Direct copy

## Usage

Getting started should work just like [getting started with ClojurescriptOne](http://clojurescriptone.com/getting-started.html)

```bash
git clone git@github.com:calebphillips/cljs-todo.git
cd cljs-todo
lein bootstrap 
lein repl
```

## License

Copyright (C) 2012 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
