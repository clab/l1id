#!/usr/bin/python
""" Translationese feature:
This hypothesis checks whether pronouns from
:mod:`translationese.function_words` alone can yield a high classification
accuracy. Each pronoun in the corpus is a feature, whose value is the
normalized frequency of its occurrences in the chunk.
"""

import collections
import math
import sys
import gflags
import feature_extractor

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_pronouns_features", False,
    "Append counts of pronouns")

PRONOUNS = [
 "he",
 "her",
 "hers",
 "herself",
 "him",
 "himself",
 "i",
 "it",
 "itself",
 "me",
 "mine",
 "myself",
 "one",
 "oneself",
 "ours",
 "ourselves",
 "she",
 "theirs",
 "them",
 "themselves",
 "they",
 "us",
 "we",
 "you",
 "yourself",
]

"""Counts all pronouns in a file and returns their probabilities.
"""
class PronounsFeatureExtractor(feature_extractor.FeatureExtractor):
  def ExtractFeaturesFromInstance(self, text, prompt, language, filename):
    counts = collections.defaultdict(int)
    total = 0
    for word in text.split():
      if word in PRONOUNS:
        counts["Pronouns_" + word] += 1
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
  if not FLAGS.append_pronouns_features:
    return None
  return PronounsFeatureExtractor()

