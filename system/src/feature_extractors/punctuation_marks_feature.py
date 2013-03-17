#!/usr/bin/python

import collections
import math
import sys
import gflags
import feature_extractor

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_punctuation_features", False,
    "Append counts of punctuation marks")

PUNCTUATION_DICT={
    ",":"comma", "'":"apostrophe", "/":"slash", '"':"qout", "?":"question",
    "!":"excl", ".":"period", ":":"colon", ";":"semicolon", "-":"dash",    
    "(":"l_bracket", ")":"r_bracket", "[":"l_sbracket", "]":"r_sbracket"}

"""Counts all punctuation marks in a file and returns their probabilities.
   FIXME: It counts punctuation marks inside words such as "don't" or "as-is".
"""
class PunctuationFeatureExtractor(feature_extractor.FeatureExtractor):
  def ExtractFeaturesFromInstance(self, text, prompt, language, filename):
    counts = collections.defaultdict(int)
    total = 0
    for char in text:
      if char in PUNCTUATION_DICT:
        counts["Punc_" + PUNCTUATION_DICT[char]] += 1
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
  if not FLAGS.append_punctuation_features:
    return None
  return PunctuationFeatureExtractor()
  
