#!/usr/bin/python
""" Translationese feature:
We assume that English original texts tend to use the passive form more
excessively than translated texts, due to the fact that the passive voice is
more frequent in English than in some other languages. If an active voice is
used in the source language, translators may prefer not to convert it to the
passive. Passives are defined as the verb be followed by the POS tag VBN
(past participle). We calculate the ratio of passive verbs to all verbs, and
magnified it by an order of 6.

"""

import collections
import sys, os
import gflags
import feature_extractor

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_ratio_to_passive_verbs", False,
    "Quantify ratio of passive forms to all verbs.")

gflags.DEFINE_string("pos_tagged_dir_passive_verbs", "",
    "Part-Of-Speech tagged files root directory")

class RatioToPassiveVerbsFeatureExtractor(feature_extractor.FeatureExtractor):
  def __init__(self, pos_tagged_dir):
    self.pos_tagged_dir = pos_tagged_dir

  def ExtractFeaturesFromInstance(self, text, prompt, language, filename):
    verbs_count = 0.0
    passive_verbs_count = 0.0
    pos_filename = os.path.join(self.pos_tagged_dir, os.path.basename(filename))
    for line in open(pos_filename):
      pairs = [ tuple(pair.rsplit("_", 1)) for pair in line.strip().split()]
      prev_word_is_be = False
      for index, (word, pos_tag) in enumerate(pairs):
        if pos_tag.startswith("VB"):
          verbs_count += 1
        if prev_word_is_be and pos_tag == 'VBN':
          passive_verbs_count += 1
        prev_word_is_be = (word == "be")

    if passive_verbs_count > 0:
      return { "RatioToPassiveVerbs_" : verbs_count/passive_verbs_count }
    else:
      return {}

if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_ratio_to_passive_verbs:
    return None
  return RatioToPassiveVerbsFeatureExtractor(FLAGS.pos_tagged_dir_passive_verbs)

