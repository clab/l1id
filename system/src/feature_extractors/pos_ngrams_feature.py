#!/usr/bin/python

import collections
import math
import sys
import gflags
import feature_extractor
import os 

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_pos_ngrams_features", False,
    "Append Part-Of-Speech n-gram counts")

gflags.DEFINE_integer("max_ngrams_order", 2,
    "Maximal order of n-gram counts")

gflags.DEFINE_string("pos_tagged_dir_ngrams", "",
    "Part-Of-Speech tagged files root directory")

class PosNgramsFeatureExtractor(feature_extractor.FeatureExtractor):
  def __init__(self, pos_tagged_dir, max_ngrams_order):
    self.pos_tagged_dir = pos_tagged_dir
    self.max_ngrams_order = max_ngrams_order

  def ExtractFeaturesFromInstance(self, text, language, filename):
    result = {}
    for order in xrange(1, self.max_ngrams_order+1):
      result.update(self.ExtractFeaturesForOrder(order, filename))
    return result

  def ExtractFeaturesForOrder(self, order, filename):
    # Find a corresponding tagged file
    pos_filename = os.path.join(self.pos_tagged_dir, os.path.basename(filename))
    counts = collections.defaultdict(int)
    total = 0
    for line in open(pos_filename):
      words = line.split()
      pos_tags = [w.split("_")[-1] for w in words]
      for index in xrange(len(pos_tags)-order+1):
        pos_bigram = pos_tags[index:index+order]
        counts["POS_" + "_".join(pos_bigram)] += 1
        total += 1
    total = float(total)
    # Normalize to probabilities
    for feature, count in counts.iteritems():
      counts[feature] = math.log(count + 1)
    return counts

if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_pos_ngrams_features:
    return None
  if len(FLAGS.pos_tagged_dir_ngrams) == 0:
    print "Flag --pos_tagged_dir is required"
    sys.exit(1)
  return PosNgramsFeatureExtractor(FLAGS.pos_tagged_dir_ngrams, FLAGS.max_ngrams_order)

