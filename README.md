# chessGameLoader

This is a wrapper around [chess](https://github.com/mhorod/chess) project, allowing it to work with [GameLoader](https://github.com/janfornal/GameLoader).

## Requirements

- Java 17
- Maven 3 as the build system

## Basic usage

You can either download ``.jar`` file from releases or compile it yourself.

    git clone --recurse-submodules https://github.com/KacperTopolski/chessGameLoader.git
    cd chessGameLoader
    mvn clean package

Now copy ``chessGameLoader/target/chessGameLoader.jar`` file to ``games`` directory.

## Warning

Git submodules point to specific revision, therefore provided version of ``GameLoader`` is probably not the most recent one. It should only be used to compile this game and should not be used directly.

This is just a side project, made as a proof of concept. It is not properly tested.
