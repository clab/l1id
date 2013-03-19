#!/usr/bin/env python2.7
'''
Pickles the Google n-grams (unigrams and bigrams) for the PMI feature.
Called by setup_ngrams.sh.

@author: Naama Twitto
'''
from __future__ import print_function
import os, sys
import cPickle as pickle
import codecs
import collections
import re

UNIGRAM_DIR='./1gms'
BIGRAM_DIR='./2gms'

counts = collections.defaultdict(int)

#1-grams
print('Reading',UNIGRAM_DIR+'/vocab',file=sys.stderr)
with codecs.open(UNIGRAM_DIR+'/vocab', "r", "utf-8") as ngramfile1:
	for line in ngramfile1:
		line=line.split('\t')
		if len(line)>1:
		  counts[line[0]]=line[1]

print('Writing vocab.pk', file=sys.stderr)
with open(UNIGRAM_DIR+"/vocab.pk", "w") as f:
    pickle.dump(counts, f, pickle.HIGHEST_PROTOCOL)

#2-grams
folder = os.listdir(BIGRAM_DIR)
for f in folder:
    print('Reading', BIGRAM_DIR+'/'+f, file=sys.stderr)
    counts = collections.defaultdict(int)
    with codecs.open(BIGRAM_DIR+'/'+f, "r", "utf-8") as ngramfile2:
        for line in ngramfile2:
            line=re.split(r'\s', line)
            if len(line)>2:
                counts[(line[0],line[1])]=line[2]
    print('Writing',f+'.pk', file=sys.stderr)
    with open(BIGRAM_DIR+'/'+f+".pk", "w") as dumpfile:
        pickle.dump(counts, dumpfile, pickle.HIGHEST_PROTOCOL)    
