#!/usr/bin/python

import collections
import sys
import gflags
import feature_extractor
import os 

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_cfw_bigrams_features", False,
    "Append contextual function words bigrams counts")

gflags.DEFINE_string("pos_tagged_dir_cfw_bigrams", "",
    "CFW bigrams Part-Of-Speech tagged files root directory")
    
gflags.DEFINE_string("fw_bigrams_file", "",
    "Function words list file")

class ContextualFunctionWordsFeatureExtractor(feature_extractor.FeatureExtractor):
  def __init__(self, fw_pos_tagged_dir, function_words_path):
    self.fw_pos_tagged_dir = fw_pos_tagged_dir
    self.function_words = open(function_words_path).readlines()
    self.function_words=[fw.strip() for fw in self.function_words] 

  def ExtractFeaturesFromInstance(self, text, language, filename):
    # Find a corresponding tagged file
    pos_filename = os.path.join(self.fw_pos_tagged_dir, "/".join(filename.split("/")[-1:]))
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
          if index >0:
            bigram = pos_tags[index-1] + " " +tokens[index] #neighbor from left
            counts["FW_" + "_" + bigram]+=1
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
  if not FLAGS.append_cfw_bigrams_features:
    return None
  if len(FLAGS.pos_tagged_dir_cfw_bigrams) == 0:
    print "Flag -- fw pos_tagged_dir is required"
    sys.exit(1)
  if len(FLAGS.fw_bigrams_file) == 0:
    print "Flag -- function words list path is required"
    sys.exit(1)
  return ContextualFunctionWordsFeatureExtractor(FLAGS.pos_tagged_dir_cfw_bigrams,FLAGS.fw_bigrams_file)

