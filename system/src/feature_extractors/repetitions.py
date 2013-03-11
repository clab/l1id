#!/usr/bin/python
""" Translationese feature:
We count the number of content words (words tagged as nouns, verbs, adjectives
or adverbs) that occur more than once in a chunk, and normalize by the number
of tokens in the chunk. Inflections of the verbs be and have are excluded
from the count since these verbs are commonly used as auxiliaries. This
feature's values are magnified by an order of 3.
"""

import collections
import sys
import gflags
import feature_extractor
import os

FLAGS = gflags.FLAGS

gflags.DEFINE_bool("append_repetitions_features", False,
    "Append counts of repetitions")

gflags.DEFINE_string("pos_tagged_dir_repetitions", "",
    "Part-Of-Speech tagged files root directory")



ignored_tokens = set([
    # Inflections of 'be'
    "am", "is", "are", "was", "were", "be", "being", "been", 
    # Inflections of 'have'
    "have", "has", "had", 
])

"""Quantify reptitions
"""

def proper_pos(token, pos):
    if token.lower() in ignored_tokens: return False

    if pos.startswith("NN"): return True # Noun
    if pos.startswith("VB"): return True # Verb
    if pos.startswith("JJ"): return True # Adjective
    if pos.startswith("RB"): return True # Adverb

    return False

class RepetitionsFeatureExtractor(feature_extractor.FeatureExtractor):
  def __init__(self, pos_tagged_dir):
    self.pos_tagged_dir = pos_tagged_dir

  def ExtractFeaturesFromInstance(self, text, language, filename):
    # Find a corresponding tagged file
    pos_filename = os.path.join(self.pos_tagged_dir, os.path.basename(filename))
    counts = collections.defaultdict(int)
    total = 0
    for line in open(pos_filename):
      for pair in line.split():
        word, pos_tag = pair.rsplit("_", 1)
        total += 1
        if proper_pos(word, pos_tag):
          counts[word] += 1

    result = sum(occurrences for token, occurrences in counts.items()
                 if occurrences > 1)
    result *= 3.0
    result /= total
    return { "Repetitions": result }

   
if __name__ == '__main__':
  print "This module is a library, not supposed to be executed directly."
  sys.exit(1)

def REGISTER_FEATURE_EXTRACTOR():
  if not FLAGS.append_repetitions_features:
    return None
  return RepetitionsFeatureExtractor(FLAGS.pos_tagged_dir_repetitions)

