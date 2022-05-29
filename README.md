# chessGameLoader

This is a wrapper around [chess](https://github.com/mhorod/chess) project, allowing it to work with [GameLoader](https://github.com/janfornal/GameLoader).

## Requirements

- Java 17
- Maven 3 as the build system

## Basic usage

    git clone --recurse-submodules git@github.com:KacperTopolski/chessGameLoader.git
    mvn clean package

Now copy ``chessGameLoader/target/chessGameLoader.jar`` file to ``games`` in ``GameLoader`` directory.
