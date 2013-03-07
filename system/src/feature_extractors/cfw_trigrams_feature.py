#!/usr/bin/python

import collections
import sys
import gflags
import feature_extractor
import os 

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_cfw_trigrams_features", False,
    "Append contextual function words trigrams counts")

gflags.DEFINE_string("pos_tagged_dir_cfw_trigrams", "",
    "CFW trigram Part-Of-Speech tagged files root directory")
    
gflags.DEFINE_string("fw_trigrams_file", "",
    "FW trigrams list file")

class ContextualFunctionWordsTrigramFeatureExtractor(feature_extractor.FeatureExtractor):
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
      pos_tags_len=len(pos_tags)
      for index in xrange(pos_tags_len-1):
        if tokens[index] in self.function_words:
          if index < pos_tags_len-2:
            ngram = tokens[index] + "_" + pos_tags[index+1] + "_" + pos_tags[index+2]# <fw,pos tag, pos tag>
            counts[ngram]+=1
          if index > 0 and index < pos_tags_len-1:
            ngram = pos_tags[index-1]+"_"+tokens[index] + "_" + pos_tags[index+1] # <pos tag,fw, pos tag>
            counts[ngram]+=1
          if index > 1:
            ngram = pos_tags[index-2]+"_"+ pos_tags[index-1] + "_"+ tokens[index] # <pos tag, pos tag,fw>
            counts[ngram]+=1
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
  if not FLAGS.append_cfw_trigrams_features:
    return None
  if len(FLAGS.pos_tagged_dir_cfw_trigrams) == 0:
    print "Flag -- fw pos_tagged_dir is required"
    sys.exit(1)
  if len(FLAGS.fw_trigrams_file) == 0:
    print "Flag -- function words list path is required"
    sys.exit(1)
  return ContextualFunctionWordsTrigramFeatureExtractor(FLAGS.pos_tagged_dir_cfw_trigrams,FLAGS.fw_trigrams_file)

