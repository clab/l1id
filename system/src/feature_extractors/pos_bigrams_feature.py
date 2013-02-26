#!/usr/bin/python

import collections
import sys
import gflags
import feature_extractor
import os 

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_pos_bigrams_features", False,
    "Append Part-Of-Speech bi-gram counts")

gflags.DEFINE_string("pos_tagged_dir", "",
    "Part-Of-Speech tagged files root directory")

class PosBigramsFeatureExtractor(feature_extractor.FeatureExtractor):
  def __init__(self, pos_tagged_dir):
    self.pos_tagged_dir = pos_tagged_dir

  def ExtractFeaturesFromInstance(self, text, language, filename):
    # Find a corresponding tagged file
    pos_filename = os.path.join(self.pos_tagged_dir, os.path.basename(filename))
    counts = collections.defaultdict(int)
    total = 0
    for line in open(pos_filename):
      words = line.split()
      pos_tags = [w.split("_")[-1] for w in words]
      for index in xrange(len(pos_tags)-1):
        pos_bigram = pos_tags[index:index+1]
        counts["POS_" + "_".join(pos_bigram)] += 1
        total += 1
    total = float(total)
    # Normalize to probabilities
    for feature, count in counts.iteritems():
      counts[feature] = count/total
    return counts   
    
if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_pos_bigrams_features:
    return None
  if len(FLAGS.pos_tagged_dir) == 0:
    print "Flag --pos_tagged_dir is required"
    sys.exit(1)
  return PosBigramsFeatureExtractor(FLAGS.pos_tagged_dir)

