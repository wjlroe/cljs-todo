## cljs-todo

A simple do list based on ClojurescriptOne.  I originally wrote this code by 
modifying the existing ClojurescriptOne, but I wanted to pull it out into a
standalone project to get a better sense for how much of the code is specific
to my application.

A good portion of the code from the sample app did not need to be changed, so here is a 
quick break down of app specific code:

* **src/clj** - Majority copied directly from the sample app.  **api.clj** and **config.clj** have significant changes.
* **src/cljs-macros** - Changed enlive snippets to reflect different html layout.
* **src/cljs** - This is where most of the changes took place.  history.clj and logging.clj were not changed.
* **src/leiningen** - Direct copy. Provides lein support for git dependencies, etc.

## Usage

Getting started is almost just like [getting started with ClojurescriptOne](http://clojurescriptone.com/getting-started.html),
except that we need to install Datomic in our local maven repo.

```bash
git clone git@github.com:calebphillips/cljs-todo.git
cd cljs-todo
mvn install:install-file -DgroupId=com.datomic -DartifactId=datomic -Dfile=datomic-DATOMIC-VERSION.jar -DpomFile=pom.xml
lein bootstrap 
lein repl
```

## License

Copyright (C) 2012 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
