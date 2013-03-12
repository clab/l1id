#!/usr/bin/python
""" Translationese feature:
The normalized frequencies of the `N` most frequent words in the corpus.
Punctuation marks are excluded.
"""

import collections
import sys
import gflags
import feature_extractor
import word_ranks

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_most_frequent_words_features", False,
    "Append mean word rank.")
gflags.DEFINE_integer("most_frequent_words_num", 10,
    "Number of most frequent words.")

class MostFrequentWordsFeatureExtractor(feature_extractor.FeatureExtractor):
  def ExtractFeaturesFromInstance(self, text, language, filename):
    counts = {}
    total = 0.0

    for word in word_ranks.TOP_WORDS[:FLAGS.most_frequent_words_num]:
        counts[word] = 0

    for word in text.split():
      total += 1
      if word in counts:
        counts[word] += 1
    return {"MostFreq_"+word: count/total for (word, count) in counts.items()}

if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_most_frequent_words_features:
    return None
  return MostFrequentWordsFeatureExtractor()

