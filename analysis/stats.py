#!/bin/env python2.7

import sys, collections
from os import listdir
from os.path import isfile, join

class Stats:
  def __init__(self):
    self.num_essays = 0
    self.num_sentences = 0
    self.num_tokens = 0
    self.all_types = set()

  def AddFileStats(self, f):
    self.num_essays += 1
    for s in f:
      sentence = s.strip().split()
      if len(sentence) > 0:
        self.num_sentences += 1
        self.num_tokens += len(sentence)
        for word in sentence:
          word = word.lower()
          if not word in self.all_types:
            self.all_types.add(word)

  def __repr__(self):
    return "Total essays\t%d\nAvg. sentences per essay\t%.2f\nAvg. tokens per essay\t%.2f\nTotal tokens\t%d\nTotal types\t%d"%(self.num_essays, self.num_sentences/float(self.num_essays), self.num_tokens/float(self.num_essays), self.num_tokens, len(self.all_types))

def PrintStats(essays, metadata, item_in_metadata):
  # item_in_metadata 0-language, 1-prompt, 2-level 
  result = collections.defaultdict(Stats)
  for filename, essay in essays.iteritems():
    all_items = metadata[filename]
    item = all_items[item_in_metadata]
    essay = essays[filename]
    result[item].AddFileStats(essay)

  for item, stats in result.iteritems():
    print item
    print stats
       
def ReadCorpusMetadata(metadata_filename):
  metadata = {}
  for line in metadata_filename:
    filename, prompt, language, level = line.strip().split(",")
    metadata[filename] = (language, prompt, level)
  return metadata

def CollectData(in_dir):
  files = {}
  filenames = [ f for f in listdir(in_dir) if isfile(join(in_dir,f)) ]
  for f in filenames:
    files[f] = open(join(in_dir,f)).read().split('\n')
  return files


def main(args):
  usage = './stats.py in_dir metadata_file'
  if len(args) < len(usage.split()) - 1:
    print 'usage:', usage
    sys.exit(1)

  essays = CollectData(args[1])
  metadata = ReadCorpusMetadata(open(args[2]))
  PrintStats(essays, metadata, 0)
  PrintStats(essays, metadata, 1)
  PrintStats(essays, metadata, 2)


if __name__ == '__main__':
  main(sys.argv)


