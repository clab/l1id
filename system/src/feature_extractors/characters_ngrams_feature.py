#!/usr/bin/python

import collections
import math
import sys
import gflags
import feature_extractor
import os 

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_characters_ngrams_features", False,
    "Append Characters n-gram counts")

gflags.DEFINE_integer("characters_max_ngrams_order", 3,
    "Maximal order of n-gram counts")

class CharactersNgramsFeatureExtractor(feature_extractor.FeatureExtractor):
  def __init__(self, max_ngrams_order):
    self.max_ngrams_order = max_ngrams_order

  def ExtractFeaturesFromInstance(self, text, language, filename):
    result = {}
    for order in xrange(1, self.max_ngrams_order+1):
      result.update(self.ExtractFeaturesForOrder(order, text,filename))
    return result

  def ExtractFeaturesForOrder(self, order, text, filename):
    counts = collections.defaultdict(int)
    total = 0
    for index in xrange(len(text)-order+1):
      counts["CHAR_" + "_".join(text[index:index+order])]+= 1
    total = float(len(text.split()))
    # Normalize to probabilities
    for feature, count in counts.iteritems():
      counts[feature] = math.log(count + 1)
    return counts

if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_characters_ngrams_features:
    return None
  return CharactersNgramsFeatureExtractor(FLAGS.characters_max_ngrams_order)

