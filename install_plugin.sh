#!/bin/bash

if [ -z "$1" ]
  then
    echo "No argument supplied"
    exit 1
fi

# LAB
# cp -r ~/Documentos/Processamento\ de\ Imagens/code/praticas/$1 ~/.imagej/plugins/

# HOME
cp -r "$PWD/$1" /c/ImageJ/plugins/
