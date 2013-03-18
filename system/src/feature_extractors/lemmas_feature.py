#!/usr/bin/python

import collections
import sys
import gflags
import feature_extractor
import os
import math

try:
  from nltk.stem.porter import PorterStemmer
  #from nltk.stem.wordnet import WordNetLemmatizer
except ImportError as ex:
  def PorterStemmer(): # lazily complain about the import (only if lemma features are used)
    raise ex

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_lemmas_features", False,
    "Append lemmas features")

gflags.DEFINE_string("pos_tagged_dir_lemmas", "",
    "Part-Of-Speech tagged files root directory")

gflags.DEFINE_integer("most_frequent_lemmas_num", 300,
    "Number of most frequent lemmas+pos combinations.")



class LemmasFeatureExtractor(feature_extractor.FeatureExtractor):
  def __init__(self, pos_tagged_dir,most_frequent_lemmas_num):
    self.pos_tagged_dir = pos_tagged_dir
    self.most_frequent_lemmas_num=most_frequent_lemmas_num
    self.mf_lemmas=self.ExtractMostFrequentLemmasPOSSet()

  def ExtractFeaturesFromInstance(self, text, prompt, language, filename):
    # Find a corresponding tagged file
    pos_filename = os.path.join(self.pos_tagged_dir, os.path.basename(filename))
    counts = collections.defaultdict(int)
    for line in open(pos_filename):
      words = line.split()
      pos_tags = [w.split("_")[-1] for w in words]
      tokens = [w.split("_")[0] for w in words]
      for index in xrange(len(pos_tags)):
        pos_tag=pos_tags[index]
        lemma=PorterStemmer().stem_word(tokens[index])
        combination=lemma+"_"+pos_tag
        if combination in self.mf_lemmas:
          counts["LemmaPOS" + "_"+combination] += 1
    for feature, count in counts.iteritems():
      counts[feature] = math.log(count + 1)
    return counts   
  
  def ExtractMostFrequentLemmasPOSSet(self):
    counts = collections.defaultdict(int)
    folder = os.listdir(self.pos_tagged_dir)
    for filename in folder:
      pos_filename = os.path.join(self.pos_tagged_dir, os.path.basename(filename))
      for line in open(pos_filename):
        words = line.split()
        pos_tags = [w.split("_")[-1] for w in words]
        tokens = [w.split("_")[0] for w in words]
        for index in xrange(len(pos_tags)):
          pos_tag=pos_tags[index]
          lemma=PorterStemmer().stem_word(tokens[index])
          counts[lemma+"_"+pos_tag] += 1
    sorted_counts=sorted(counts, key=counts.get,reverse=True)
    mf_lemmas =sorted_counts[:self.most_frequent_lemmas_num]
    return mf_lemmas
  
if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_lemmas_features:
    return None
  if len(FLAGS.pos_tagged_dir_lemmas) == 0:
    print "Flag --pos_tagged_dir is required"
    sys.exit(1)
  return LemmasFeatureExtractor(FLAGS.pos_tagged_dir_lemmas,FLAGS.most_frequent_lemmas_num)

