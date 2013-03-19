#!/bin/bash
# Argument: directory containing Google n-grams dump
set -eux
D=$1

mkdir -p 1gms
cp $D/vocab.gz 1gms/
gunzip 1gms/vocab.gz

mkdir -p 2gms
cp $D/2*.gz 2gms/
for f in 2gms/*.gz; do
  gunzip $f
done

./create_ngrams_dump.py
