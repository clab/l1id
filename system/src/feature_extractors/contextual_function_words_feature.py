#!/usr/bin/python

import collections
import math
import sys
import gflags
import feature_extractor
import os 

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_contextual_function_words_features", False,
    "Append contextual function words counts")

gflags.DEFINE_string("fw_pos_tagged_dir", "",
    "FW Part-Of-Speech tagged files root directory")
    
gflags.DEFINE_string("function_words_list_path", "",
    "Function words list full path")

class ContextualFunctionWordsFeatureExtractor(feature_extractor.FeatureExtractor):
  def __init__(self, fw_pos_tagged_dir, function_words_path):
    self.fw_pos_tagged_dir = fw_pos_tagged_dir
    self.function_words = open(function_words_path).readlines()
    self.function_words = map(lambda s: s.strip(), self.function_words)

  def ExtractFeaturesFromInstance(self, text, language, filename):
    # Find a corresponding tagged file
    pos_filename = os.path.join(self.fw_pos_tagged_dir, os.path.basename(filename))
    pos_tags=open(pos_filename).readlines()
    counts = collections.defaultdict(int)
    total = 0
    for line in open(pos_filename):
      words = line.split()
      pos_tags = [w.split("_")[-1] for w in words]
      tokens = [w.split("_")[0] for w in words]
      for index in xrange(len(pos_tags)-1):
        if tokens[index] in self.function_words:
          bigram = tokens[index] + " " + pos_tags[index+1]#neighbor from right
          counts["FW_" + "_" + bigram]+=1
        if index > 0:
          bigram = pos_tags[index-1] + " " +tokens[index] #neighbor from left
          counts["FW_" + "_" + bigram]+=1
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
  if not FLAGS.append_contextual_function_words_features:
    return None
  if len(FLAGS.fw_pos_tagged_dir) == 0:
    print "Flag -- fw pos_tagged_dir is required"
    sys.exit(1)
  if len(FLAGS.function_words_list_path) == 0:
    print "Flag -- function words list path is required"
    sys.exit(1)
  return ContextualFunctionWordsFeatureExtractor(FLAGS.fw_pos_tagged_dir,FLAGS.function_words_list_path)

