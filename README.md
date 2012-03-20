## cljs-todo

A simple do list based on Clojurescript One.  I originally wrote this code by 
modifying the existing Clojurescript One, but I wanted to pull it out into a
standalone project to get a better sense for how much of the code is specific
to my application.  The Datomic integration might be a bit of a digression, but it's 
a fun one and doesn't add too much code.

A good portion of the code from the sample app did not need to be changed, so here is a 
quick break down of app specific code:

* **src/clj** - Majority copied directly from the sample app.  **api.clj** and **config.clj** have significant changes.
* **src/cljs-macros** - Changed enlive snippets to reflect different html layout.
* **src/cljs** - This is where most of the changes took place.  history.clj and logging.clj were not changed.
* **src/leiningen** - Direct copy. Provides lein support for git dependencies, etc.

## Usage

Getting started is for the most part, the same as [getting started with Clojurescript One](http://clojurescriptone.com/getting-started.html),
except that you need to install Datomic in your local maven repo.

See http://datomic.com/company/resources/getting-started and http://datomic.com/company/resources/integrating-peer-lib 
for details on how to get Datomic into your local repo.  Basically, you just need to download the peer library, unzip it
and run:

```bash
mvn install:install-file -DgroupId=com.datomic -DartifactId=datomic \
    -Dfile=datomic-DATOMIC-VERSION.jar -DpomFile=pom.xml
```

Make sure your version of the jar file matches the dependency in the project.clj for cljs-todo.

Once Datomic is in your maven repo, you should be able to start up cljs-todo:

```bash
git clone git@github.com:calebphillips/cljs-todo.git
cd cljs-todo
lein bootstrap 
lein repl
cljs-todo.repl=> (go)
```

This will start the server and open a browser window with the Clojurescript One landing page loaded. 
Clicking the **Development** Link will take you to the application running in development mode.

The Clojurescript One wiki has lots of great documentation, including details about the design and production modes.

## License

Copyright (C) 2012 Caleb Phillips

Distributed under the Eclipse Public License, the same as Clojure.
