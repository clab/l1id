#!/usr/bin/python
""" Translationese feature:
We assume that less frequent words are used more often in original texts than
in translated ones.
"""

import collections
import sys
import gflags
import feature_extractor
from word_ranks import WORD_RANKS

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_mean_word_ranks_features", False,
    "Append mean word rank.")
gflags.DEFINE_integer("mean_word_ranks_variant", 1,
    "Words not in the list are ignored altogether(1) or given very high rank(0).")

VERY_HIGH_RANK = 6000
"""Very high rank for a word, guessed for unknown words. The highest rank
for known words is 5000."""

class MeanWordRanksFeatureExtractor(feature_extractor.FeatureExtractor):
  def ExtractFeaturesFromInstance(self, text, language, filename):
    count = 0
    rank_sum = 0
    for word in text.split():
      if not word.isalpha():
        continue
      if word in WORD_RANKS:
        rank_sum += WORD_RANKS[word]
        count += 1
      else:
        if FLAGS.mean_word_ranks_variant == 0:
          rank_sum += VERY_HIGH_RANK
          count += 1
    return { "MeanWordRank": float(rank_sum) / count }
  
if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_mean_word_ranks_features:
    return None
  return MeanWordRanksFeatureExtractor()

