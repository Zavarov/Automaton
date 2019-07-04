# fa-language

This project implements finite automata and is split into three modules.

#### fa-implementation
The core of the project. Here there are implementation for both deterministic and nondeterministic automata, together with builders for a simplified construction.

Additionally, it also supports the basic operations of transforming an NFA into a DFA, reversing a DFA and minimizing a DFA.

#### fa-grammar

This module focuses on the generation of finite automata from text files.
For the parsing process, [MontiCore](https://github.com/MontiCore/monticore) is used and the resulting syntax tree is then combined with the automaton builder to create the respective instances.

#### fa-regex

Analogous to the previous module, this one focuses on the generation of finite automata from regular expressions.
Just like before, [MontiCore](https://github.com/MontiCore/monticore) is used to parse a simplified version of an regular expression grammar, which is then transformed into an NFA using [Thompson's construction algorithm](https://en.wikipedia.org/wiki/Thompson%27s_construction).

### Installing

In order to install this project, simply execute the maven command:

```
mvn clean install
```

## Built With

* [MontiCore](https://github.com/MontiCore/monticore) - The language workbench for the automata grammar
* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Zavarov**

## License

This project is licensed under the GPLv3 License - see the [LICENSE](LICENSE) file for details
