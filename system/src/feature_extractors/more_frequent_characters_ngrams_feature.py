#!/usr/bin/python

import collections
import math
import sys
import gflags
import feature_extractor
import os
import codecs

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_more_frequent_characters_ngrams_features", False,
    "Append more frequent characters n-gram counts")

gflags.DEFINE_string("more_frequent_characters_ngrams_input_dir", "",
    "input dir of texts")

gflags.DEFINE_integer("more_frequent_characters_max_ngrams_order", 3,
    "Maximal order of n-gram counts")

gflags.DEFINE_integer("frequency_limit", 5, "frequency limit")


class MoreFrequentCharactersNgramsFeatureExtractor(feature_extractor.FeatureExtractor):
  def __init__(self,input_dir,max_ngrams_order,frequency_limit):
    self.max_order = max_ngrams_order
    self.frequency_limit = frequency_limit
    self.mf_ngrams = self.ExtractMoreFrequentCharactersNgramsSet(input_dir)
    
    

  def ExtractFeaturesFromInstance(self, text, prompt, language, filename):
    result = {}
    for order in xrange(1, self.max_order+1):
      result.update(self.ExtractFeaturesForOrder(order,text,prompt))
    return result

  def ExtractFeaturesForOrder(self,order,text,prompt):
    counts = collections.defaultdict(int)
    #total = 0
    for index in xrange(len(text)-order+1):
      if text[index:index+order] in self.mf_ngrams:
        counts["MFChar_"+"_".join(text[index:index+order])]+= 1
    #total = float(len(text.split()))
    # Normalize to probabilities
    for feature, count in counts.iteritems():
      counts[feature] = math.log(count + 1)
    return counts
  
  def ExtractMoreFrequentCharactersNgramsSet(self,input_dir):
    counts = collections.defaultdict(int)
    folder = os.listdir(input_dir)
    for filename in folder:
      text = codecs.open(os.path.join(input_dir, filename),"r","utf-8").read()
      for index in xrange(len(text)-self.max_order+1):
        for order in xrange(1,self.max_order+1):
          counts[text[index:index+order]]+= 1
    mf_ngrams =collections.defaultdict(int)
  
    for feature, count in counts.iteritems():
      if count >self.frequency_limit:
        mf_ngrams[feature]=count
    return mf_ngrams
  

if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_more_frequent_characters_ngrams_features:
    return None
  if len(FLAGS.more_frequent_characters_ngrams_input_dir) == 0:
    print "Flag --more_frequent_characters_ngrams_input_dir is required"
    sys.exit(1)
  return MoreFrequentCharactersNgramsFeatureExtractor(FLAGS.more_frequent_characters_ngrams_input_dir,FLAGS.more_frequent_characters_max_ngrams_order,FLAGS.frequency_limit)

