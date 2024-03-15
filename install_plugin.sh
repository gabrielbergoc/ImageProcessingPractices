#!/bin/bash

if [ -z "$1" ]
  then
    echo "No argument supplied"
    exit 1
fi

#rm -rf ~/.imagej/plugins/$1
cp -r ~/Documentos/Processamento\ de\ Imagens/code/praticas/$1 ~/.imagej/plugins/
