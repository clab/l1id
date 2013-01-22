#!/bin/bash

IN_DIR=$1
OUT_DIR=$2

echo "Running POS tagger"
pushd ${POS_TAGGER_DIR}
for f in `find ${IN_DIR} -type f` ; do
  OUT_FILE=${OUT_DIR}${f:${#IN_DIR}}
  mkdir -p `dirname ${OUT_FILE}`
  if [ ! -s "${OUT_FILE}" ] ; then
    echo "Running POS tagger on $f"
    java -mx300m -classpath stanford-postagger-3.1.4.jar edu.stanford.nlp.tagger.maxent.MaxentTagger -model models/english-left3words-distsim.tagger  -textFile $f > ${OUT_FILE}
  fi
done
popd
