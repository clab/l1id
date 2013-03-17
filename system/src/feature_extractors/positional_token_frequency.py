#!/usr/bin/python
""" Translationese feature:
Writers have a relatively limited vocabulary from which to choose words to open
or close a sentence. We hypothesize that the choices subject to interference.
The value of this feature is the normalized frequency of tokens appearing in
the first, second, antepenultimate, penultimate and last positions in a
sentence. We exclude sentences shorter than five tokens.  Punctuation marks are
considered as tokens in this feature, and for this reason the three last
positions of a sentence are considered, while only the first two of them are
interesting for our purposes.

"""

import collections
import math
import sys
import gflags
import feature_extractor

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_positional_token_frequencies", False,
    "Append counts of tokens by their position in a sentence")

POSITION_NAMES = {
                  "first": 0,
                  "second": 1,
                  "antepenultimate":-4,
                  "penultimate":-3,
                  "last":-2  # -1 is the period
                  }


"""Names of the various positions of the sentence, final period excluded."""

class PositionalTokenFrequenciesFeatureExtractor(feature_extractor.FeatureExtractor):
  def ExtractFeaturesFromInstance(self, text, prompt, language, filename):
    counts = collections.defaultdict(int)
    total = 0
    for line in text.split("\n"):
      sentence = line.split()
      if len(sentence) < 6:
        # Sentence has fewer than 5 tokens (and a period)
        continue
      for position_name, position in POSITION_NAMES.items():
        key = "%s_%s" % (position_name, sentence[position])
        counts["PosToken_" + key]+=1
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
  if not FLAGS.append_positional_token_frequencies:
    return None
  return PositionalTokenFrequenciesFeatureExtractor()

